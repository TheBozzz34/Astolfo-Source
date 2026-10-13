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
   private static final long b = 60629317350356L;

   public ServerConfigurationManager(MinecraftServer var1) {
      this.mcServer = var1;
      this.bannedPlayers.setLanServer(false);
      this.bannedIPs.setLanServer(false);
      this.maxPlayers = 8;
   }

   public void initializeConnectionToPlayer(NetworkManager var1, EntityPlayerMP var2) {
      long var3 = b ^ 21680138897192L;
      GameProfile var7 = var2.getGameProfile();
      PlayerProfileCache var8 = this.mcServer.getPlayerProfileCache();
      GameProfile var9 = var8.getProfileByUUID(var7.getId());
      String var10 = var9 == null ? var7.getName() : var9.getName();
      var8.addEntry(var7);
      long var10000 = 6380641756622188446L ^ var3;
      NBTTagCompound var11 = this.readPlayerDataFromFile(var2);
      var2.setWorld(this.mcServer.worldServerForDimension(var2.dimension));
      var2.theItemInWorldManager.setWorld((WorldServer)var2.worldObj);
      String var12 = "local";
      if (var1.getRemoteAddress() != null) {
         var12 = var1.getRemoteAddress().toString();
      }

      logger.info(
         var2.getName() + "[" + var12 + "] logged in with entity id " + var2.getEntityId() + " at (" + var2.posX + ", " + var2.posY + ", " + var2.posZ + ")"
      );
      WorldServer var13 = this.mcServer.worldServerForDimension(var2.dimension);
      WorldInfo var14 = var13.getWorldInfo();
      BlockPos var15 = var13.getSpawnPoint();
      this.setPlayerGameTypeBasedOnOther(var2, (EntityPlayerMP)null, var13);
      NetHandlerPlayServer var16 = new NetHandlerPlayServer(this.mcServer, var1, var2);
      var16.sendPacket(
         new S01PacketJoinGame(
            var2.getEntityId(),
            var2.theItemInWorldManager.getGameType(),
            var14.isHardcoreModeEnabled(),
            var13.provider.getDimensionId(),
            var13.getDifficulty(),
            this.getMaxPlayers(),
            var14.getTerrainType(),
            var13.getGameRules().getBoolean("reducedDebugInfo")
         )
      );
      var16.sendPacket(new S3FPacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).writeString(this.getServerInstance().getServerModName())));
      var16.sendPacket(new S41PacketServerDifficulty(var14.getDifficulty(), var14.isDifficultyLocked()));
      var16.sendPacket(new S05PacketSpawnPosition(var15));
      var16.sendPacket(new S39PacketPlayerAbilities(var2.capabilities));
      var16.sendPacket(new S09PacketHeldItemChange(var2.inventory.currentItem));
      var2.getStatFile().func_150877_d();
      var2.getStatFile().sendAchievements(var2);
      this.sendScoreboard((ServerScoreboard)var13.getScoreboard(), var2);
      this.mcServer.refreshStatusNextTick();
      ChatComponentTranslation var17;
      if (!var2.getName().equalsIgnoreCase(var10)) {
         var17 = new ChatComponentTranslation("multiplayer.player.joined.renamed", var2.getDisplayName(), var10);
      } else {
         var17 = new ChatComponentTranslation("multiplayer.player.joined", var2.getDisplayName());
      }

      var17.getChatStyle().setColor(EnumChatFormatting.YELLOW);
      this.sendChatMsg(var17);
      this.playerLoggedIn(var2);
      var16.setPlayerLocation(var2.posX, var2.posY, var2.posZ, var2.rotationYaw, var2.rotationPitch);
      this.updateTimeAndWeatherForPlayer(var2, var13);
      if (this.mcServer.getResourcePackUrl().length() > 0) {
         var2.loadResourcePack(this.mcServer.getResourcePackUrl(), this.mcServer.getResourcePackHash());
      }

      for (PotionEffect var19 : var2.getActivePotionEffects()) {
         var16.sendPacket(new S1DPacketEntityEffect(var2.getEntityId(), var19));
      }

      var2.addSelfToInternalCraftingInventory();
      if (var11 != null && var11.hasKey("Riding", 10)) {
         Entity var20 = EntityList.createEntityFromNBT(var11.getCompoundTag("Riding"), var13);
         if (var20 != null) {
            var20.forceSpawn = true;
            var13.spawnEntityInWorld(var20);
            var2.mountEntity(var20);
            var20.forceSpawn = false;
         }
      }
   }

   public void sendScoreboard(ServerScoreboard var1, EntityPlayerMP var2) {
      HashSet var6 = Sets.newHashSet();

      for (ScorePlayerTeam var8 : var1.getTeams()) {
         var2.playerNetServerHandler.sendPacket(new S3EPacketTeams(var8, 0));
      }

      for (int var11 = 0; var11 < 19; var11++) {
         ScoreObjective var12 = var1.getObjectiveInDisplaySlot(var11);
         if (var12 != null && !var6.contains(var12)) {
            for (Packet var10 : var1.func_96550_d(var12)) {
               var2.playerNetServerHandler.sendPacket(var10);
            }

            var6.add(var12);
         }
      }
   }

   public void setPlayerManager(WorldServer[] var1) {
      this.playerNBTManagerObj = var1[0].getSaveHandler().getPlayerNBTManager();
      var1[0].getWorldBorder().addListener(new IBorderListener() {
         @Override
         public void onSizeChanged(WorldBorder var1, double var2) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(var1, S44PacketWorldBorder.Action.SET_SIZE));
         }

         @Override
         public void onTransitionStarted(WorldBorder var1, double var2, double var4, long var6) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(var1, S44PacketWorldBorder.Action.LERP_SIZE));
         }

         @Override
         public void onCenterChanged(WorldBorder var1, double var2, double var4) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(var1, S44PacketWorldBorder.Action.SET_CENTER));
         }

         @Override
         public void onWarningTimeChanged(WorldBorder var1, int var2) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(var1, S44PacketWorldBorder.Action.SET_WARNING_TIME));
         }

         @Override
         public void onWarningDistanceChanged(WorldBorder var1, int var2) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(var1, S44PacketWorldBorder.Action.SET_WARNING_BLOCKS));
         }

         @Override
         public void onDamageAmountChanged(WorldBorder var1, double var2) {
         }

         @Override
         public void onDamageBufferChanged(WorldBorder var1, double var2) {
         }
      });
   }

   public void preparePlayer(EntityPlayerMP var1, WorldServer var2) {
      WorldServer var6 = var1.getServerForPlayer();
      if (var2 != null) {
         var2.getPlayerManager().removePlayer(var1);
      }

      var6.getPlayerManager().addPlayer(var1);
      var6.theChunkProviderServer.loadChunk((int)var1.posX >> 4, (int)var1.posZ >> 4);
   }

   public int getEntityViewDistance() {
      return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
   }

   public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP var1) {
      NBTTagCompound var5 = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
      NBTTagCompound var6;
      if (var1.getName().equals(this.mcServer.getServerOwner()) && var5 != null) {
         var1.readFromNBT(var5);
         var6 = var5;
         logger.debug("loading single player");
      } else {
         var6 = this.playerNBTManagerObj.readPlayerData(var1);
      }

      return var6;
   }

   public void writePlayerData(EntityPlayerMP var1) {
      this.playerNBTManagerObj.writePlayerData(var1);
      StatisticsFile var5 = this.playerStatFiles.get(var1.getUniqueID());
      if (var5 != null) {
         var5.saveStatFile();
      }
   }

   public void playerLoggedIn(EntityPlayerMP var1) {
      this.playerEntityList.add(var1);
      this.uuidToPlayerMap.put(var1.getUniqueID(), var1);
      this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, var1));
      WorldServer var5 = this.mcServer.worldServerForDimension(var1.dimension);
      var5.spawnEntityInWorld(var1);
      this.preparePlayer(var1, (WorldServer)null);

      for (int var6 = 0; var6 < this.playerEntityList.size(); var6++) {
         EntityPlayerMP var7 = this.playerEntityList.get(var6);
         var1.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, var7));
      }
   }

   public void serverUpdateMountedMovingPlayer(EntityPlayerMP var1) {
      var1.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(var1);
   }

   public void playerLoggedOut(EntityPlayerMP var1) {
      var1.triggerAchievement(StatList.leaveGameStat);
      this.writePlayerData(var1);
      WorldServer var5 = var1.getServerForPlayer();
      if (var1.ridingEntity != null) {
         var5.removePlayerEntityDangerously(var1.ridingEntity);
         logger.debug("removing player mount");
      }

      var5.removeEntity(var1);
      var5.getPlayerManager().removePlayer(var1);
      this.playerEntityList.remove(var1);
      UUID var6 = var1.getUniqueID();
      EntityPlayerMP var7 = this.uuidToPlayerMap.get(var6);
      if (var7 == var1) {
         this.uuidToPlayerMap.remove(var6);
         this.playerStatFiles.remove(var6);
      }

      this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.REMOVE_PLAYER, var1));
   }

   public String allowUserToConnect(SocketAddress var1, GameProfile var2) {
      if (this.bannedPlayers.isBanned(var2)) {
         UserListBansEntry var8 = this.bannedPlayers.getEntry(var2);
         String var9 = "You are banned from this server!\nReason: " + var8.getBanReason();
         if (var8.getBanEndDate() != null) {
            var9 = var9 + "\nYour ban will be removed on " + dateFormat.format(var8.getBanEndDate());
         }

         return var9;
      } else {
         if (!this.canJoin(var2)) {
            return "You are not white-listed on this server!";
         }

         if (this.bannedIPs.isBanned(var1)) {
            IPBanEntry var6 = this.bannedIPs.getBanEntry(var1);
            String var7 = "Your IP address is banned from this server!\nReason: " + var6.getBanReason();
            if (var6.getBanEndDate() != null) {
               var7 = var7 + "\nYour ban will be removed on " + dateFormat.format(var6.getBanEndDate());
            }

            return var7;
         } else {
            return this.playerEntityList.size() >= this.maxPlayers && !this.bypassesPlayerLimit(var2) ? "The server is full!" : null;
         }
      }
   }

   public EntityPlayerMP createPlayerForUser(GameProfile var1) {
      UUID var5 = EntityPlayer.getUUID(var1);
      ArrayList var6 = Lists.newArrayList();

      for (int var7 = 0; var7 < this.playerEntityList.size(); var7++) {
         EntityPlayerMP var8 = this.playerEntityList.get(var7);
         if (var8.getUniqueID().equals(var5)) {
            var6.add(var8);
         }
      }

      EntityPlayerMP var10 = this.uuidToPlayerMap.get(var1.getId());
      if (var10 != null && !var6.contains(var10)) {
         var6.add(var10);
      }

      for (Object var9$raw : var6) {
         EntityPlayerMP var9 = (EntityPlayerMP)var9$raw;
         var9.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
      }

      ItemInWorldManager var12;
      if (this.mcServer.isDemo()) {
         var12 = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
      } else {
         var12 = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
      }

      return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), var1, var12);
   }

   public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP var1, int var2, boolean var3) {
      var1.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(var1);
      var1.getServerForPlayer().getEntityTracker().untrackEntity(var1);
      var1.getServerForPlayer().getPlayerManager().removePlayer(var1);
      this.playerEntityList.remove(var1);
      this.mcServer.worldServerForDimension(var1.dimension).removePlayerEntityDangerously(var1);
      BlockPos var7 = var1.getBedLocation();
      boolean var8 = var1.S();
      var1.dimension = var2;
      ItemInWorldManager var9;
      if (this.mcServer.isDemo()) {
         var9 = new DemoWorldManager(this.mcServer.worldServerForDimension(var1.dimension));
      } else {
         var9 = new ItemInWorldManager(this.mcServer.worldServerForDimension(var1.dimension));
      }

      EntityPlayerMP var10 = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(var1.dimension), var1.getGameProfile(), var9);
      var10.playerNetServerHandler = var1.playerNetServerHandler;
      var10.clonePlayer(var1, var3);
      var10.setEntityId(var1.getEntityId());
      var10.T_void(var1);
      WorldServer var11 = this.mcServer.worldServerForDimension(var1.dimension);
      this.setPlayerGameTypeBasedOnOther(var10, var1, var11);
      if (var7 != null) {
         BlockPos var12 = EntityPlayer.getBedSpawnLocation(this.mcServer.worldServerForDimension(var1.dimension), var7, var8);
         if (var12 != null) {
            var10.setLocationAndAngles(var12.getX() + 0.5F, var12.getY() + 0.1F, var12.getZ() + 0.5F, 0.0F, 0.0F);
            var10.setSpawnPoint(var7, var8);
         } else {
            var10.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
         }
      }

      var11.theChunkProviderServer.loadChunk((int)var10.posX >> 4, (int)var10.posZ >> 4);

      while (!var11.getCollidingBoundingBoxes(var10, var10.getEntityBoundingBox()).isEmpty() && var10.posY < 256.0) {
         var10.setPosition(var10.posX, var10.posY + 1.0, var10.posZ);
      }

      var10.playerNetServerHandler
         .sendPacket(
            new S07PacketRespawn(
               var10.dimension, var10.worldObj.getDifficulty(), var10.worldObj.getWorldInfo().getTerrainType(), var10.theItemInWorldManager.getGameType()
            )
         );
      BlockPos var13 = var11.getSpawnPoint();
      var10.playerNetServerHandler.setPlayerLocation(var10.posX, var10.posY, var10.posZ, var10.rotationYaw, var10.rotationPitch);
      var10.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(var13));
      var10.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(var10.experience, var10.experienceTotal, var10.experienceLevel));
      this.updateTimeAndWeatherForPlayer(var10, var11);
      var11.getPlayerManager().addPlayer(var10);
      var11.spawnEntityInWorld(var10);
      this.playerEntityList.add(var10);
      this.uuidToPlayerMap.put(var10.getUniqueID(), var10);
      var10.addSelfToInternalCraftingInventory();
      var10.setHealth(var10.getHealth());
      return var10;
   }

   public void transferPlayerToDimension(EntityPlayerMP var1, int var2) {
      int var6 = var1.dimension;
      WorldServer var7 = this.mcServer.worldServerForDimension(var1.dimension);
      var1.dimension = var2;
      WorldServer var8 = this.mcServer.worldServerForDimension(var1.dimension);
      var1.playerNetServerHandler
         .sendPacket(
            new S07PacketRespawn(
               var1.dimension, var1.worldObj.getDifficulty(), var1.worldObj.getWorldInfo().getTerrainType(), var1.theItemInWorldManager.getGameType()
            )
         );
      var7.removePlayerEntityDangerously(var1);
      var1.isDead = false;
      this.transferEntityToWorld(var1, var6, var7, var8);
      this.preparePlayer(var1, var7);
      var1.playerNetServerHandler.setPlayerLocation(var1.posX, var1.posY, var1.posZ, var1.rotationYaw, var1.rotationPitch);
      var1.theItemInWorldManager.setWorld(var8);
      this.updateTimeAndWeatherForPlayer(var1, var8);
      this.syncPlayerInventory(var1);

      for (PotionEffect var10 : var1.getActivePotionEffects()) {
         var1.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(var1.getEntityId(), var10));
      }
   }

   public void transferEntityToWorld(Entity var1, int var2, WorldServer var3, WorldServer var4) {
      double var8 = var1.posX;
      double var10 = var1.posZ;
      double var12 = 8.0;
      float var14 = var1.rotationYaw;
      var3.theProfiler.startSection("moving");
      if (var1.dimension == -1) {
         var8 = MathHelper.clamp_double(var8 / var12, var4.getWorldBorder().minX() + 16.0, var4.getWorldBorder().maxX() - 16.0);
         var10 = MathHelper.clamp_double(var10 / var12, var4.getWorldBorder().minZ() + 16.0, var4.getWorldBorder().maxZ() - 16.0);
         var1.setLocationAndAngles(var8, var1.posY, var10, var1.rotationYaw, var1.rotationPitch);
         if (var1.isEntityAlive()) {
            var3.updateEntityWithOptionalForce(var1, false);
         }
      } else if (var1.dimension == 0) {
         var8 = MathHelper.clamp_double(var8 * var12, var4.getWorldBorder().minX() + 16.0, var4.getWorldBorder().maxX() - 16.0);
         var10 = MathHelper.clamp_double(var10 * var12, var4.getWorldBorder().minZ() + 16.0, var4.getWorldBorder().maxZ() - 16.0);
         var1.setLocationAndAngles(var8, var1.posY, var10, var1.rotationYaw, var1.rotationPitch);
         if (var1.isEntityAlive()) {
            var3.updateEntityWithOptionalForce(var1, false);
         }
      } else {
         BlockPos var15;
         if (var2 == 1) {
            var15 = var4.getSpawnPoint();
         } else {
            var15 = var4.getSpawnCoordinate();
         }

         var8 = var15.getX();
         var1.posY = var15.getY();
         var10 = var15.getZ();
         var1.setLocationAndAngles(var8, var1.posY, var10, 90.0F, 0.0F);
         if (var1.isEntityAlive()) {
            var3.updateEntityWithOptionalForce(var1, false);
         }
      }

      var3.theProfiler.endSection();
      if (var2 != 1) {
         var3.theProfiler.startSection("placing");
         var8 = MathHelper.clamp_int((int)var8, -29999872, 29999872);
         var10 = MathHelper.clamp_int((int)var10, -29999872, 29999872);
         if (var1.isEntityAlive()) {
            var1.setLocationAndAngles(var8, var1.posY, var10, var1.rotationYaw, var1.rotationPitch);
            var4.getDefaultTeleporter().placeInPortal(var1, var14);
            var4.spawnEntityInWorld(var1);
            var4.updateEntityWithOptionalForce(var1, false);
         }

         var3.theProfiler.endSection();
      }

      var1.setWorld(var4);
   }

   public void onTick() {
      if (++this.playerPingIndex > 600) {
         this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_LATENCY, this.playerEntityList));
         this.playerPingIndex = 0;
      }
   }

   public void sendPacketToAllPlayers(Packet var1) {
      for (int var5 = 0; var5 < this.playerEntityList.size(); var5++) {
         this.playerEntityList.get(var5).playerNetServerHandler.sendPacket(var1);
      }
   }

   public void sendPacketToAllPlayersInDimension(Packet var1, int var2) {
      for (int var6 = 0; var6 < this.playerEntityList.size(); var6++) {
         EntityPlayerMP var7 = this.playerEntityList.get(var6);
         if (var7.dimension == var2) {
            var7.playerNetServerHandler.sendPacket(var1);
         }
      }
   }

   public void sendMessageToAllTeamMembers(EntityPlayer var1, IChatComponent var2) {
      Team var6 = var1.getTeam();
      if (var6 != null) {
         for (String var8 : var6.getMembershipCollection()) {
            EntityPlayerMP var9 = this.getPlayerByUsername(var8);
            if (var9 != null && var9 != var1) {
               var9.addChatMessage(var2);
            }
         }
      }
   }

   public void sendMessageToTeamOrEvryPlayer(EntityPlayer var1, IChatComponent var2) {
      Team var6 = var1.getTeam();
      if (var6 == null) {
         this.sendChatMsg(var2);
      } else {
         for (int var7 = 0; var7 < this.playerEntityList.size(); var7++) {
            EntityPlayerMP var8 = this.playerEntityList.get(var7);
            if (var8.getTeam() != var6) {
               var8.addChatMessage(var2);
            }
         }
      }
   }

   public String func_181058_b(boolean var1) {
      String var5 = "";
      ArrayList var6 = Lists.newArrayList(this.playerEntityList);

      for (int var7 = 0; var7 < var6.size(); var7++) {
         if (var7 > 0) {
            var5 = var5 + ", ";
         }

         var5 = var5 + ((EntityPlayerMP)var6.get(var7)).getName();
         if (var1) {
            var5 = var5 + " (" + ((EntityPlayerMP)var6.get(var7)).getUniqueID().toString() + ")";
         }
      }

      return var5;
   }

   public String[] getAllUsernames() {
      String[] var4 = new String[this.playerEntityList.size()];

      for (int var5 = 0; var5 < this.playerEntityList.size(); var5++) {
         var4[var5] = this.playerEntityList.get(var5).getName();
      }

      return var4;
   }

   public GameProfile[] getAllProfiles() {
      GameProfile[] var4 = new GameProfile[this.playerEntityList.size()];

      for (int var5 = 0; var5 < this.playerEntityList.size(); var5++) {
         var4[var5] = this.playerEntityList.get(var5).getGameProfile();
      }

      return var4;
   }

   public UserListBans getBannedPlayers() {
      return this.bannedPlayers;
   }

   public BanList getBannedIPs() {
      return this.bannedIPs;
   }

   public void J(GameProfile var1) {
      this.ops.addEntry(new UserListOpsEntry(var1, this.mcServer.getOpPermissionLevel(), this.ops.bypassesPlayerLimit(var1)));
   }

   public void C(GameProfile var1) {
      this.ops.removeEntry(var1);
   }

   public boolean canJoin(GameProfile var1) {
      return !this.whiteListEnforced || this.ops.hasEntry(var1) || this.whiteListedPlayers.hasEntry(var1);
   }

   public boolean canSendCommands(GameProfile var1) {
      return this.ops.hasEntry(var1)
         || this.mcServer.isSinglePlayer()
            && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed()
            && this.mcServer.getServerOwner().equalsIgnoreCase(var1.getName())
         || this.commandsAllowedForAll;
   }

   public EntityPlayerMP getPlayerByUsername(String var1) {
      for (EntityPlayerMP var6 : this.playerEntityList) {
         if (var6.getName().equalsIgnoreCase(var1)) {
            return var6;
         }
      }

      return null;
   }

   public void sendToAllNear(double var1, double var3, double var5, double var7, int var9, Packet var10) {
      this.sendToAllNearExcept((EntityPlayer)null, var1, var3, var5, var7, var9, var10);
   }

   public void sendToAllNearExcept(EntityPlayer var1, double var2, double var4, double var6, double var8, int var10, Packet var11) {
      for (int var15 = 0; var15 < this.playerEntityList.size(); var15++) {
         EntityPlayerMP var16 = this.playerEntityList.get(var15);
         if (var16 != var1 && var16.dimension == var10) {
            double var17 = var2 - var16.posX;
            double var19 = var4 - var16.posY;
            double var21 = var6 - var16.posZ;
            if (var17 * var17 + var19 * var19 + var21 * var21 < var8 * var8) {
               var16.playerNetServerHandler.sendPacket(var11);
            }
         }
      }
   }

   public void saveAllPlayerData() {
      for (int var4 = 0; var4 < this.playerEntityList.size(); var4++) {
         this.writePlayerData(this.playerEntityList.get(var4));
      }
   }

   public void addWhitelistedPlayer(GameProfile var1) {
      this.whiteListedPlayers.addEntry(new UserListWhitelistEntry(var1));
   }

   public void removePlayerFromWhitelist(GameProfile var1) {
      this.whiteListedPlayers.removeEntry(var1);
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

   public void updateTimeAndWeatherForPlayer(EntityPlayerMP var1, WorldServer var2) {
      WorldBorder var6 = this.mcServer.worldServers[0].getWorldBorder();
      var1.playerNetServerHandler.sendPacket(new S44PacketWorldBorder(var6, S44PacketWorldBorder.Action.INITIALIZE));
      var1.playerNetServerHandler
         .sendPacket(new S03PacketTimeUpdate(var2.getTotalWorldTime(), var2.getWorldTime(), var2.getGameRules().getBoolean("doDaylightCycle")));
      if (var2.isRaining()) {
         var1.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(1, 0.0F));
         var1.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(7, var2.getRainStrength(1.0F)));
         var1.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(8, var2.getThunderStrength(1.0F)));
      }
   }

   public void syncPlayerInventory(EntityPlayerMP var1) {
      var1.sendContainerToPlayer(var1.inventoryContainer);
      var1.setPlayerHealthUpdated();
      var1.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(var1.inventory.currentItem));
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

   public void setWhiteListEnabled(boolean var1) {
      this.whiteListEnforced = var1;
   }

   public List<EntityPlayerMP> getPlayersMatchingAddress(String var1) {
      ArrayList var5 = Lists.newArrayList();

      for (EntityPlayerMP var7 : this.playerEntityList) {
         if (var7.getPlayerIP().equals(var1)) {
            var5.add(var7);
         }
      }

      return var5;
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

   public void setGameType(WorldSettings.GameType var1) {
      this.gameType = var1;
   }

   private void setPlayerGameTypeBasedOnOther(EntityPlayerMP var1, EntityPlayerMP var2, World var3) {
      if (var2 != null) {
         var1.theItemInWorldManager.setGameType(var2.theItemInWorldManager.getGameType());
      } else if (this.gameType != null) {
         var1.theItemInWorldManager.setGameType(this.gameType);
      }

      var1.theItemInWorldManager.initializeGameType(var3.getWorldInfo().getGameType());
   }

   public void setCommandsAllowedForAll(boolean var1) {
      this.commandsAllowedForAll = var1;
   }

   public void removeAllPlayers() {
      for (int var4 = 0; var4 < this.playerEntityList.size(); var4++) {
         this.playerEntityList.get(var4).playerNetServerHandler.kickPlayerFromServer("Server closed");
      }
   }

   public void sendChatMsgImpl(IChatComponent var1, boolean var2) {
      this.mcServer.addChatMessage(var1);
      byte var6 = (byte)(var2 ? 1 : 0);
      this.sendPacketToAllPlayers(new S02PacketChat(var1, var6));
   }

   public void sendChatMsg(IChatComponent var1) {
      this.sendChatMsgImpl(var1, true);
   }

   public StatisticsFile getPlayerStatsFile(EntityPlayer var1) {
      UUID var5 = var1.getUniqueID();
      StatisticsFile var6 = var5 == null ? null : this.playerStatFiles.get(var5);
      if (var6 == null) {
         File var7 = new File(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "stats");
         File var8 = new File(var7, var5.toString() + ".json");
         if (!var8.exists()) {
            File var9 = new File(var7, var1.getName() + ".json");
            if (var9.exists() && var9.isFile()) {
               var9.renameTo(var8);
            }
         }

         var6 = new StatisticsFile(this.mcServer, var8);
         var6.readStatFile();
         this.playerStatFiles.put(var5, var6);
      }

      return var6;
   }

   public void setViewDistance(int var1) {
      this.viewDistance = var1;
      if (this.mcServer.worldServers != null) {
         for (WorldServer var8 : this.mcServer.worldServers) {
            if (var8 != null) {
               var8.getPlayerManager().setPlayerViewRadius(var1);
            }
         }
      }
   }

   public List<EntityPlayerMP> getPlayerList() {
      return this.playerEntityList;
   }

   public EntityPlayerMP getPlayerByUUID(UUID var1) {
      return this.uuidToPlayerMap.get(var1);
   }

   public boolean bypassesPlayerLimit(GameProfile var1) {
      return false;
   }
}
