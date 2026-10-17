package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerConfigurationManager {
   public static final File FILE_PLAYERBANS = new File("banned-players.json");
   public static final File FILE_IPBANS = new File("banned-ips.json");
   public static final File FILE_OPS = new File("ops.json");
   public static final File FILE_WHITELIST = new File("whitelist.json");
   private static final Logger logger = LogManager.getLogger();
   private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer mcServer;
   private final List<EntityPlayerMP> playerEntityList = Lists.newArrayList();
   private final Map<UUID, EntityPlayerMP> uuidToPlayerMap = Maps.newHashMap();
   private final UserListBans bannedPlayers = new UserListBans(FILE_PLAYERBANS);
   private final BanList bannedIPs = new BanList(FILE_IPBANS);
   private final UserListOps ops = new UserListOps(FILE_OPS);
   private final UserListWhitelist whiteListedPlayers = new UserListWhitelist(FILE_WHITELIST);
   private final Map<UUID, StatisticsFile> playerStatFiles = Maps.newHashMap();
   private IPlayerFileData playerNBTManagerObj;
   private boolean whiteListEnforced;
   public int maxPlayers;
   private int viewDistance;
   private WorldSettings.GameType gameType;
   private boolean commandsAllowedForAll;
   private int playerPingIndex;

   public ServerConfigurationManager(MinecraftServer server) {
      this.mcServer = server;
      this.bannedPlayers.setLanServer(false);
      this.bannedIPs.setLanServer(false);
      this.maxPlayers = 8;
   }

   public void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP playerIn) {
      GameProfile gameProfile = playerIn.getGameProfile();
      PlayerProfileCache playerProfileCache = this.mcServer.getPlayerProfileCache();
      GameProfile gameProfile2 = playerProfileCache.getProfileByUUID(gameProfile.getId());
      String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
      playerProfileCache.addEntry(gameProfile);
      NBTTagCompound nbtTagCompound = this.readPlayerDataFromFile(playerIn);
      playerIn.setWorld(this.mcServer.worldServerForDimension(playerIn.dimension));
      playerIn.theItemInWorldManager.setWorld((WorldServer)playerIn.worldObj);
      String string2 = "local";
      if (netManager.getRemoteAddress() != null) {
         string2 = netManager.getRemoteAddress().toString();
      }

      logger.info(
         playerIn.getName()
            + "["
            + string2
            + "] logged in with entity id "
            + playerIn.getEntityId()
            + " at ("
            + playerIn.posX
            + ", "
            + playerIn.posY
            + ", "
            + playerIn.posZ
            + ")"
      );
      WorldServer worldServer = this.mcServer.worldServerForDimension(playerIn.dimension);
      WorldInfo worldInfo = worldServer.getWorldInfo();
      BlockPos blockPos = worldServer.getSpawnPoint();
      this.setPlayerGameTypeBasedOnOther(playerIn, (EntityPlayerMP)null, worldServer);
      NetHandlerPlayServer netHandlerPlayServer = new NetHandlerPlayServer(this.mcServer, netManager, playerIn);
      netHandlerPlayServer.sendPacket(
         new S01PacketJoinGame(
            playerIn.getEntityId(),
            playerIn.theItemInWorldManager.getGameType(),
            worldInfo.isHardcoreModeEnabled(),
            worldServer.provider.getDimensionId(),
            worldServer.getDifficulty(),
            this.getMaxPlayers(),
            worldInfo.getTerrainType(),
            worldServer.getGameRules().getBoolean("reducedDebugInfo")
         )
      );
      netHandlerPlayServer.sendPacket(new S3FPacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).writeString(this.getServerInstance().getServerModName())));
      netHandlerPlayServer.sendPacket(new S41PacketServerDifficulty(worldInfo.getDifficulty(), worldInfo.isDifficultyLocked()));
      netHandlerPlayServer.sendPacket(new S05PacketSpawnPosition(blockPos));
      netHandlerPlayServer.sendPacket(new S39PacketPlayerAbilities(playerIn.capabilities));
      netHandlerPlayServer.sendPacket(new S09PacketHeldItemChange(playerIn.inventory.currentItem));
      playerIn.getStatFile().func_150877_d();
      playerIn.getStatFile().sendAchievements(playerIn);
      this.sendScoreboard((ServerScoreboard)worldServer.getScoreboard(), playerIn);
      this.mcServer.refreshStatusNextTick();
      ChatComponentTranslation chatComponentTranslation;
      if (!playerIn.getName().equalsIgnoreCase(string)) {
         chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.joined.renamed", playerIn.getDisplayName(), string);
      } else {
         chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.joined", playerIn.getDisplayName());
      }

      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
      this.sendChatMsg(chatComponentTranslation);
      this.playerLoggedIn(playerIn);
      netHandlerPlayServer.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
      this.updateTimeAndWeatherForPlayer(playerIn, worldServer);
      if (this.mcServer.getResourcePackUrl().length() > 0) {
         playerIn.loadResourcePack(this.mcServer.getResourcePackUrl(), this.mcServer.getResourcePackHash());
      }

      for (PotionEffect potionEffect : playerIn.getActivePotionEffects()) {
         netHandlerPlayServer.sendPacket(new S1DPacketEntityEffect(playerIn.getEntityId(), potionEffect));
      }

      playerIn.addSelfToInternalCraftingInventory();
      if (nbtTagCompound != null && nbtTagCompound.hasKey("Riding", 10)) {
         Entity entity2 = EntityList.createEntityFromNBT(nbtTagCompound.getCompoundTag("Riding"), worldServer);
         if (entity2 != null) {
            entity2.forceSpawn = true;
            worldServer.spawnEntityInWorld(entity2);
            playerIn.mountEntity(entity2);
            entity2.forceSpawn = false;
         }
      }
   }

   public void sendScoreboard(ServerScoreboard scoreboardIn, EntityPlayerMP playerIn) {
      HashSet hashSet = Sets.newHashSet();

      for (ScorePlayerTeam scorePlayerTeam : scoreboardIn.getTeams()) {
         playerIn.playerNetServerHandler.sendPacket(new S3EPacketTeams(scorePlayerTeam, 0));
      }

      for (int i = 0; i < 19; i++) {
         ScoreObjective scoreObjective = scoreboardIn.getObjectiveInDisplaySlot(i);
         if (scoreObjective != null && !hashSet.contains(scoreObjective)) {
            for (Packet packet : scoreboardIn.func_96550_d(scoreObjective)) {
               playerIn.playerNetServerHandler.sendPacket(packet);
            }

            hashSet.add(scoreObjective);
         }
      }
   }

   public void setPlayerManager(WorldServer[] worldServers) {
      this.playerNBTManagerObj = worldServers[0].getSaveHandler().getPlayerNBTManager();
      worldServers[0].getWorldBorder().addListener(new IBorderListener() {
         @Override
         public void onSizeChanged(WorldBorder border, double newSize) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_SIZE));
         }

         @Override
         public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.LERP_SIZE));
         }

         @Override
         public void onCenterChanged(WorldBorder border, double x, double z) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_CENTER));
         }

         @Override
         public void onWarningTimeChanged(WorldBorder border, int newTime) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_WARNING_TIME));
         }

         @Override
         public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_WARNING_BLOCKS));
         }

         @Override
         public void onDamageAmountChanged(WorldBorder border, double newAmount) {
         }

         @Override
         public void onDamageBufferChanged(WorldBorder border, double newSize) {
         }
      });
   }

   public void preparePlayer(EntityPlayerMP playerIn, WorldServer worldIn) {
      WorldServer worldServer = playerIn.getServerForPlayer();
      if (worldIn != null) {
         worldIn.getPlayerManager().removePlayer(playerIn);
      }

      worldServer.getPlayerManager().addPlayer(playerIn);
      worldServer.theChunkProviderServer.loadChunk((int)playerIn.posX >> 4, (int)playerIn.posZ >> 4);
   }

   public int getEntityViewDistance() {
      return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
   }

   public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP playerIn) {
      NBTTagCompound nbtTagCompound = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
      NBTTagCompound nbtTagCompound2;
      if (playerIn.getName().equals(this.mcServer.getServerOwner()) && nbtTagCompound != null) {
         playerIn.readFromNBT(nbtTagCompound);
         nbtTagCompound2 = nbtTagCompound;
         logger.debug("loading single player");
      } else {
         nbtTagCompound2 = this.playerNBTManagerObj.readPlayerData(playerIn);
      }

      return nbtTagCompound2;
   }

   public void writePlayerData(EntityPlayerMP playerIn) {
      this.playerNBTManagerObj.writePlayerData(playerIn);
      StatisticsFile statisticsFile = this.playerStatFiles.get(playerIn.getUniqueID());
      if (statisticsFile != null) {
         statisticsFile.saveStatFile();
      }
   }

   public void playerLoggedIn(EntityPlayerMP playerIn) {
      this.playerEntityList.add(playerIn);
      this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
      this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, playerIn));
      WorldServer worldServer = this.mcServer.worldServerForDimension(playerIn.dimension);
      worldServer.spawnEntityInWorld(playerIn);
      this.preparePlayer(playerIn, (WorldServer)null);

      for (int i = 0; i < this.playerEntityList.size(); i++) {
         EntityPlayerMP entityPlayerMP = this.playerEntityList.get(i);
         playerIn.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, entityPlayerMP));
      }
   }

   public void serverUpdateMountedMovingPlayer(EntityPlayerMP playerIn) {
      playerIn.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(playerIn);
   }

   public void playerLoggedOut(EntityPlayerMP playerIn) {
      playerIn.triggerAchievement(StatList.leaveGameStat);
      this.writePlayerData(playerIn);
      WorldServer worldServer = playerIn.getServerForPlayer();
      if (playerIn.ridingEntity != null) {
         worldServer.removePlayerEntityDangerously(playerIn.ridingEntity);
         logger.debug("removing player mount");
      }

      worldServer.removeEntity(playerIn);
      worldServer.getPlayerManager().removePlayer(playerIn);
      this.playerEntityList.remove(playerIn);
      UUID uuid = playerIn.getUniqueID();
      EntityPlayerMP entityPlayerMP = this.uuidToPlayerMap.get(uuid);
      if (entityPlayerMP == playerIn) {
         this.uuidToPlayerMap.remove(uuid);
         this.playerStatFiles.remove(uuid);
      }

      this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.REMOVE_PLAYER, playerIn));
   }

   public String allowUserToConnect(SocketAddress address, GameProfile profile) {
      if (this.bannedPlayers.isBanned(profile)) {
         UserListBansEntry userListBansEntry = this.bannedPlayers.getEntry(profile);
         String string = "You are banned from this server!\nReason: " + userListBansEntry.getBanReason();
         if (userListBansEntry.getBanEndDate() != null) {
            string = string + "\nYour ban will be removed on " + dateFormat.format(userListBansEntry.getBanEndDate());
         }

         return string;
      } else {
         if (!this.canJoin(profile)) {
            return "You are not white-listed on this server!";
         }

         if (this.bannedIPs.isBanned(address)) {
            IPBanEntry ipBanEntry = this.bannedIPs.getBanEntry(address);
            String string2 = "Your IP address is banned from this server!\nReason: " + ipBanEntry.getBanReason();
            if (ipBanEntry.getBanEndDate() != null) {
               string2 = string2 + "\nYour ban will be removed on " + dateFormat.format(ipBanEntry.getBanEndDate());
            }

            return string2;
         } else {
            return this.playerEntityList.size() >= this.maxPlayers && !this.bypassesPlayerLimit(profile) ? "The server is full!" : null;
         }
      }
   }

   public EntityPlayerMP createPlayerForUser(GameProfile profile) {
      UUID uuid = EntityPlayer.getUUID(profile);
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < this.playerEntityList.size(); i++) {
         EntityPlayerMP entityPlayerMP = this.playerEntityList.get(i);
         if (entityPlayerMP.getUniqueID().equals(uuid)) {
            arrayList.add(entityPlayerMP);
         }
      }

      EntityPlayerMP entityPlayerMP2 = this.uuidToPlayerMap.get(profile.getId());
      if (entityPlayerMP2 != null && !arrayList.contains(entityPlayerMP2)) {
         arrayList.add(entityPlayerMP2);
      }

      for (Object object : arrayList) {
         EntityPlayerMP entityPlayerMP3 = (EntityPlayerMP)object;
         entityPlayerMP3.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
      }

      ItemInWorldManager itemInWorldManager;
      if (this.mcServer.isDemo()) {
         itemInWorldManager = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
      } else {
         itemInWorldManager = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
      }

      return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), profile, itemInWorldManager);
   }

   public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd) {
      playerIn.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(playerIn);
      playerIn.getServerForPlayer().getEntityTracker().untrackEntity(playerIn);
      playerIn.getServerForPlayer().getPlayerManager().removePlayer(playerIn);
      this.playerEntityList.remove(playerIn);
      this.mcServer.worldServerForDimension(playerIn.dimension).removePlayerEntityDangerously(playerIn);
      BlockPos blockPos = playerIn.getBedLocation();
      boolean bl = playerIn.isSpawnForced();
      playerIn.dimension = dimension;
      ItemInWorldManager itemInWorldManager;
      if (this.mcServer.isDemo()) {
         itemInWorldManager = new DemoWorldManager(this.mcServer.worldServerForDimension(playerIn.dimension));
      } else {
         itemInWorldManager = new ItemInWorldManager(this.mcServer.worldServerForDimension(playerIn.dimension));
      }

      EntityPlayerMP entityPlayerMP = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(playerIn.dimension), playerIn.getGameProfile(), itemInWorldManager);
      entityPlayerMP.playerNetServerHandler = playerIn.playerNetServerHandler;
      entityPlayerMP.clonePlayer(playerIn, conqueredEnd);
      entityPlayerMP.setEntityId(playerIn.getEntityId());
      entityPlayerMP.T_void(playerIn);
      WorldServer worldServer = this.mcServer.worldServerForDimension(playerIn.dimension);
      this.setPlayerGameTypeBasedOnOther(entityPlayerMP, playerIn, worldServer);
      if (blockPos != null) {
         BlockPos blockPos2 = EntityPlayer.getBedSpawnLocation(this.mcServer.worldServerForDimension(playerIn.dimension), blockPos, bl);
         if (blockPos2 != null) {
            entityPlayerMP.setLocationAndAngles(blockPos2.getX() + 0.5F, blockPos2.getY() + 0.1F, blockPos2.getZ() + 0.5F, 0.0F, 0.0F);
            entityPlayerMP.setSpawnPoint(blockPos, bl);
         } else {
            entityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
         }
      }

      worldServer.theChunkProviderServer.loadChunk((int)entityPlayerMP.posX >> 4, (int)entityPlayerMP.posZ >> 4);

      while (!worldServer.getCollidingBoundingBoxes(entityPlayerMP, entityPlayerMP.getEntityBoundingBox()).isEmpty() && entityPlayerMP.posY < 256.0) {
         entityPlayerMP.setPosition(entityPlayerMP.posX, entityPlayerMP.posY + 1.0, entityPlayerMP.posZ);
      }

      entityPlayerMP.playerNetServerHandler
         .sendPacket(
            new S07PacketRespawn(
               entityPlayerMP.dimension, entityPlayerMP.worldObj.getDifficulty(), entityPlayerMP.worldObj.getWorldInfo().getTerrainType(), entityPlayerMP.theItemInWorldManager.getGameType()
            )
         );
      BlockPos blockPos3 = worldServer.getSpawnPoint();
      entityPlayerMP.playerNetServerHandler.setPlayerLocation(entityPlayerMP.posX, entityPlayerMP.posY, entityPlayerMP.posZ, entityPlayerMP.rotationYaw, entityPlayerMP.rotationPitch);
      entityPlayerMP.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(blockPos3));
      entityPlayerMP.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(entityPlayerMP.experience, entityPlayerMP.experienceTotal, entityPlayerMP.experienceLevel));
      this.updateTimeAndWeatherForPlayer(entityPlayerMP, worldServer);
      worldServer.getPlayerManager().addPlayer(entityPlayerMP);
      worldServer.spawnEntityInWorld(entityPlayerMP);
      this.playerEntityList.add(entityPlayerMP);
      this.uuidToPlayerMap.put(entityPlayerMP.getUniqueID(), entityPlayerMP);
      entityPlayerMP.addSelfToInternalCraftingInventory();
      entityPlayerMP.setHealth(entityPlayerMP.getHealth());
      return entityPlayerMP;
   }

   public void transferPlayerToDimension(EntityPlayerMP playerIn, int dimension) {
      int i = playerIn.dimension;
      WorldServer worldServer = this.mcServer.worldServerForDimension(playerIn.dimension);
      playerIn.dimension = dimension;
      WorldServer worldServer2 = this.mcServer.worldServerForDimension(playerIn.dimension);
      playerIn.playerNetServerHandler
         .sendPacket(
            new S07PacketRespawn(
               playerIn.dimension,
               playerIn.worldObj.getDifficulty(),
               playerIn.worldObj.getWorldInfo().getTerrainType(),
               playerIn.theItemInWorldManager.getGameType()
            )
         );
      worldServer.removePlayerEntityDangerously(playerIn);
      playerIn.isDead = false;
      this.transferEntityToWorld(playerIn, i, worldServer, worldServer2);
      this.preparePlayer(playerIn, worldServer);
      playerIn.playerNetServerHandler.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
      playerIn.theItemInWorldManager.setWorld(worldServer2);
      this.updateTimeAndWeatherForPlayer(playerIn, worldServer2);
      this.syncPlayerInventory(playerIn);

      for (PotionEffect potionEffect : playerIn.getActivePotionEffects()) {
         playerIn.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(playerIn.getEntityId(), potionEffect));
      }
   }

   public void transferEntityToWorld(Entity entityIn, int i, WorldServer oldWorldIn, WorldServer toWorldIn) {
      double d = entityIn.posX;
      double e = entityIn.posZ;
      double d2 = 8.0;
      float f = entityIn.rotationYaw;
      oldWorldIn.theProfiler.startSection("moving");
      if (entityIn.dimension == -1) {
         d = MathHelper.clamp_double(d / d2, toWorldIn.getWorldBorder().minX() + 16.0, toWorldIn.getWorldBorder().maxX() - 16.0);
         e = MathHelper.clamp_double(e / d2, toWorldIn.getWorldBorder().minZ() + 16.0, toWorldIn.getWorldBorder().maxZ() - 16.0);
         entityIn.setLocationAndAngles(d, entityIn.posY, e, entityIn.rotationYaw, entityIn.rotationPitch);
         if (entityIn.isEntityAlive()) {
            oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }
      } else if (entityIn.dimension == 0) {
         d = MathHelper.clamp_double(d * d2, toWorldIn.getWorldBorder().minX() + 16.0, toWorldIn.getWorldBorder().maxX() - 16.0);
         e = MathHelper.clamp_double(e * d2, toWorldIn.getWorldBorder().minZ() + 16.0, toWorldIn.getWorldBorder().maxZ() - 16.0);
         entityIn.setLocationAndAngles(d, entityIn.posY, e, entityIn.rotationYaw, entityIn.rotationPitch);
         if (entityIn.isEntityAlive()) {
            oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }
      } else {
         BlockPos blockPos;
         if (i == 1) {
            blockPos = toWorldIn.getSpawnPoint();
         } else {
            blockPos = toWorldIn.getSpawnCoordinate();
         }

         d = blockPos.getX();
         entityIn.posY = blockPos.getY();
         e = blockPos.getZ();
         entityIn.setLocationAndAngles(d, entityIn.posY, e, 90.0F, 0.0F);
         if (entityIn.isEntityAlive()) {
            oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }
      }

      oldWorldIn.theProfiler.endSection();
      if (i != 1) {
         oldWorldIn.theProfiler.startSection("placing");
         d = MathHelper.clamp_int((int)d, -29999872, 29999872);
         e = MathHelper.clamp_int((int)e, -29999872, 29999872);
         if (entityIn.isEntityAlive()) {
            entityIn.setLocationAndAngles(d, entityIn.posY, e, entityIn.rotationYaw, entityIn.rotationPitch);
            toWorldIn.getDefaultTeleporter().placeInPortal(entityIn, f);
            toWorldIn.spawnEntityInWorld(entityIn);
            toWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }

         oldWorldIn.theProfiler.endSection();
      }

      entityIn.setWorld(toWorldIn);
   }

   public void onTick() {
      if (++this.playerPingIndex > 600) {
         this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_LATENCY, this.playerEntityList));
         this.playerPingIndex = 0;
      }
   }

   public void sendPacketToAllPlayers(Packet packetIn) {
      for (int i = 0; i < this.playerEntityList.size(); i++) {
         this.playerEntityList.get(i).playerNetServerHandler.sendPacket(packetIn);
      }
   }

   public void sendPacketToAllPlayersInDimension(Packet packetIn, int dimension) {
      for (int i = 0; i < this.playerEntityList.size(); i++) {
         EntityPlayerMP entityPlayerMP = this.playerEntityList.get(i);
         if (entityPlayerMP.dimension == dimension) {
            entityPlayerMP.playerNetServerHandler.sendPacket(packetIn);
         }
      }
   }

   public void sendMessageToAllTeamMembers(EntityPlayer player, IChatComponent message) {
      Team team = player.getTeam();
      if (team != null) {
         for (String string : team.getMembershipCollection()) {
            EntityPlayerMP entityPlayerMP = this.getPlayerByUsername(string);
            if (entityPlayerMP != null && entityPlayerMP != player) {
               entityPlayerMP.addChatMessage(message);
            }
         }
      }
   }

   public void sendMessageToTeamOrEvryPlayer(EntityPlayer player, IChatComponent message) {
      Team team = player.getTeam();
      if (team == null) {
         this.sendChatMsg(message);
      } else {
         for (int i = 0; i < this.playerEntityList.size(); i++) {
            EntityPlayerMP entityPlayerMP = this.playerEntityList.get(i);
            if (entityPlayerMP.getTeam() != team) {
               entityPlayerMP.addChatMessage(message);
            }
         }
      }
   }

   public String func_181058_b(boolean bl) {
      String string = "";
      ArrayList arrayList = Lists.newArrayList(this.playerEntityList);

      for (int i = 0; i < arrayList.size(); i++) {
         if (i > 0) {
            string = string + ", ";
         }

         string = string + ((EntityPlayerMP)arrayList.get(i)).getName();
         if (bl) {
            string = string + " (" + ((EntityPlayerMP)arrayList.get(i)).getUniqueID().toString() + ")";
         }
      }

      return string;
   }

   public String[] getAllUsernames() {
      String[] strings = new String[this.playerEntityList.size()];

      for (int i = 0; i < this.playerEntityList.size(); i++) {
         strings[i] = this.playerEntityList.get(i).getName();
      }

      return strings;
   }

   public GameProfile[] getAllProfiles() {
      GameProfile[] gameProfiles = new GameProfile[this.playerEntityList.size()];

      for (int i = 0; i < this.playerEntityList.size(); i++) {
         gameProfiles[i] = this.playerEntityList.get(i).getGameProfile();
      }

      return gameProfiles;
   }

   public UserListBans getBannedPlayers() {
      return this.bannedPlayers;
   }

   public BanList getBannedIPs() {
      return this.bannedIPs;
   }

   public void addOp(GameProfile profile) {
      this.ops.addEntry(new UserListOpsEntry(profile, this.mcServer.getOpPermissionLevel(), this.ops.bypassesPlayerLimit(profile)));
   }

   public void removeOp(GameProfile profile) {
      this.ops.removeEntry(profile);
   }

   public boolean canJoin(GameProfile profile) {
      return !this.whiteListEnforced || this.ops.hasEntry(profile) || this.whiteListedPlayers.hasEntry(profile);
   }

   public boolean canSendCommands(GameProfile profile) {
      return this.ops.hasEntry(profile)
         || this.mcServer.isSinglePlayer()
            && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed()
            && this.mcServer.getServerOwner().equalsIgnoreCase(profile.getName())
         || this.commandsAllowedForAll;
   }

   public EntityPlayerMP getPlayerByUsername(String username) {
      for (EntityPlayerMP entityPlayerMP : this.playerEntityList) {
         if (entityPlayerMP.getName().equalsIgnoreCase(username)) {
            return entityPlayerMP;
         }
      }

      return null;
   }

   public void sendToAllNear(double x, double y, double z, double radius, int dimension, Packet packetIn) {
      this.sendToAllNearExcept((EntityPlayer)null, x, y, z, radius, dimension, packetIn);
   }

   public void sendToAllNearExcept(EntityPlayer entityPlayer, double x, double y, double z, double radius, int dimension, Packet packet) {
      for (int i = 0; i < this.playerEntityList.size(); i++) {
         EntityPlayerMP entityPlayerMP = this.playerEntityList.get(i);
         if (entityPlayerMP != entityPlayer && entityPlayerMP.dimension == dimension) {
            double d = x - entityPlayerMP.posX;
            double e = y - entityPlayerMP.posY;
            double d2 = z - entityPlayerMP.posZ;
            if (d * d + e * e + d2 * d2 < radius * radius) {
               entityPlayerMP.playerNetServerHandler.sendPacket(packet);
            }
         }
      }
   }

   public void saveAllPlayerData() {
      for (int i = 0; i < this.playerEntityList.size(); i++) {
         this.writePlayerData(this.playerEntityList.get(i));
      }
   }

   public void addWhitelistedPlayer(GameProfile profile) {
      this.whiteListedPlayers.addEntry(new UserListWhitelistEntry(profile));
   }

   public void removePlayerFromWhitelist(GameProfile profile) {
      this.whiteListedPlayers.removeEntry(profile);
   }

   public UserListWhitelist getWhitelistedPlayers() {
      return this.whiteListedPlayers;
   }

   public String[] getWhitelistedPlayerNames() {
      return this.whiteListedPlayers.getKeys();
   }

   public UserListOps getOppedPlayers() {
      return this.ops;
   }

   public String[] getOppedPlayerNames() {
      return this.ops.getKeys();
   }

   public void loadWhiteList() {
   }

   public void updateTimeAndWeatherForPlayer(EntityPlayerMP playerIn, WorldServer worldIn) {
      WorldBorder worldBorder = this.mcServer.worldServers[0].getWorldBorder();
      playerIn.playerNetServerHandler.sendPacket(new S44PacketWorldBorder(worldBorder, S44PacketWorldBorder.Action.INITIALIZE));
      playerIn.playerNetServerHandler
         .sendPacket(new S03PacketTimeUpdate(worldIn.getTotalWorldTime(), worldIn.getWorldTime(), worldIn.getGameRules().getBoolean("doDaylightCycle")));
      if (worldIn.isRaining()) {
         playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(1, 0.0F));
         playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(7, worldIn.getRainStrength(1.0F)));
         playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(8, worldIn.getThunderStrength(1.0F)));
      }
   }

   public void syncPlayerInventory(EntityPlayerMP playerIn) {
      playerIn.sendContainerToPlayer(playerIn.inventoryContainer);
      playerIn.setPlayerHealthUpdated();
      playerIn.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(playerIn.inventory.currentItem));
   }

   public int getCurrentPlayerCount() {
      return this.playerEntityList.size();
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public String[] getAvailablePlayerDat() {
      return this.mcServer.worldServers[0].getSaveHandler().getPlayerNBTManager().getAvailablePlayerDat();
   }

   public void setWhiteListEnabled(boolean whitelistEnabled) {
      this.whiteListEnforced = whitelistEnabled;
   }

   public List<EntityPlayerMP> getPlayersMatchingAddress(String address) {
      ArrayList arrayList = Lists.newArrayList();

      for (EntityPlayerMP entityPlayerMP : this.playerEntityList) {
         if (entityPlayerMP.getPlayerIP().equals(address)) {
            arrayList.add(entityPlayerMP);
         }
      }

      return arrayList;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public MinecraftServer getServerInstance() {
      return this.mcServer;
   }

   public NBTTagCompound getHostPlayerData() {
      return null;
   }

   public void setGameType(WorldSettings.GameType gameType) {
      this.gameType = gameType;
   }

   private void setPlayerGameTypeBasedOnOther(EntityPlayerMP entityPlayerMP, EntityPlayerMP entityPlayerMP2, World worldIn) {
      if (entityPlayerMP2 != null) {
         entityPlayerMP.theItemInWorldManager.setGameType(entityPlayerMP2.theItemInWorldManager.getGameType());
      } else if (this.gameType != null) {
         entityPlayerMP.theItemInWorldManager.setGameType(this.gameType);
      }

      entityPlayerMP.theItemInWorldManager.initializeGameType(worldIn.getWorldInfo().getGameType());
   }

   public void setCommandsAllowedForAll(boolean bl) {
      this.commandsAllowedForAll = bl;
   }

   public void removeAllPlayers() {
      for (int i = 0; i < this.playerEntityList.size(); i++) {
         this.playerEntityList.get(i).playerNetServerHandler.kickPlayerFromServer("Server closed");
      }
   }

   public void sendChatMsgImpl(IChatComponent component, boolean isChat) {
      this.mcServer.addChatMessage(component);
      byte b = (byte)(isChat ? 1 : 0);
      this.sendPacketToAllPlayers(new S02PacketChat(component, b));
   }

   public void sendChatMsg(IChatComponent component) {
      this.sendChatMsgImpl(component, true);
   }

   public StatisticsFile getPlayerStatsFile(EntityPlayer playerIn) {
      UUID uuid = playerIn.getUniqueID();
      StatisticsFile statisticsFile = uuid == null ? null : this.playerStatFiles.get(uuid);
      if (statisticsFile == null) {
         File file = new File(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "stats");
         File file2 = new File(file, uuid.toString() + ".json");
         if (!file2.exists()) {
            File file3 = new File(file, playerIn.getName() + ".json");
            if (file3.exists() && file3.isFile()) {
               file3.renameTo(file2);
            }
         }

         statisticsFile = new StatisticsFile(this.mcServer, file2);
         statisticsFile.readStatFile();
         this.playerStatFiles.put(uuid, statisticsFile);
      }

      return statisticsFile;
   }

   public void setViewDistance(int distance) {
      this.viewDistance = distance;
      if (this.mcServer.worldServers != null) {
         for (WorldServer worldServer : this.mcServer.worldServers) {
            if (worldServer != null) {
               worldServer.getPlayerManager().setPlayerViewRadius(distance);
            }
         }
      }
   }

   public List<EntityPlayerMP> getPlayerList() {
      return this.playerEntityList;
   }

   public EntityPlayerMP getPlayerByUUID(UUID playerUUID) {
      return this.uuidToPlayerMap.get(playerUUID);
   }

   public boolean bypassesPlayerLimit(GameProfile gameProfile) {
      return false;
   }
}
