package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonSerializableSet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {
   private static final Logger logger = LogManager.getLogger();
   private String translator = "en_US";
   public NetHandlerPlayServer playerNetServerHandler;
   public final MinecraftServer mcServer;
   public final ItemInWorldManager theItemInWorldManager;
   public double managedPosX;
   public double managedPosZ;
   public final List<ChunkCoordIntPair> loadedChunks = Lists.newLinkedList();
   private final List<Integer> destroyedItemsNetCache = Lists.newLinkedList();
   private final StatisticsFile statsFile;
   private float combinedHealth = Float.MIN_VALUE;
   private float lastHealth = -1.0E8F;
   private int lastFoodLevel = -99999999;
   private boolean wasHungry = true;
   private int lastExperience = -99999999;
   private int respawnInvulnerabilityTicks = 60;
   private EntityPlayer.EnumChatVisibility chatVisibility;
   private boolean chatColours = true;
   private long playerLastActiveTime = System.currentTimeMillis();
   private Entity spectatingEntity = null;
   private int currentWindowId;
   public boolean isChangingQuantityOnly;
   public int ping;
   public boolean playerConqueredTheEnd;

   public EntityPlayerMP(MinecraftServer server, WorldServer worldIn, GameProfile profile, ItemInWorldManager interactionManager) {
      super(worldIn, profile);
      interactionManager.thisPlayerMP = this;
      this.theItemInWorldManager = interactionManager;
      BlockPos blockPos = worldIn.getSpawnPoint();
      if (!worldIn.provider.getHasNoSky() && worldIn.getWorldInfo().getGameType() != WorldSettings.GameType.ADVENTURE) {
         int i = Math.max(5, server.getSpawnProtectionSize() - 6);
         int j = MathHelper.floor_double(worldIn.getWorldBorder().getClosestDistance(blockPos.getX(), blockPos.getZ()));
         if (j < i) {
            i = j;
         }

         if (j <= 1) {
            i = 1;
         }

         blockPos = worldIn.getTopSolidOrLiquidBlock(blockPos.add(this.rand.nextInt(i * 2) - i, 0, this.rand.nextInt(i * 2) - i));
      }

      this.mcServer = server;
      this.statsFile = server.getConfigurationManager().getPlayerStatsFile(this);
      this.stepHeight = 0.0F;
      this.moveToBlockPosAndAngles(blockPos, 0.0F, 0.0F);

      while (!worldIn.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && this.posY < 255.0) {
         this.setPosition(this.posX, this.posY + 1.0, this.posZ);
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("playerGameType", 99)) {
         if (MinecraftServer.getServer().getForceGamemode()) {
            this.theItemInWorldManager.setGameType(MinecraftServer.getServer().getGameType());
         } else {
            this.theItemInWorldManager.setGameType(WorldSettings.GameType.getByID(tagCompund.getInteger("playerGameType")));
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("playerGameType", this.theItemInWorldManager.getGameType().getID());
   }

   @Override
   public void addExperienceLevel(int levels) {
      super.addExperienceLevel(levels);
      this.lastExperience = -1;
   }

   @Override
   public void removeExperienceLevel(int levels) {
      super.removeExperienceLevel(levels);
      this.lastExperience = -1;
   }

   public void addSelfToInternalCraftingInventory() {
      this.openContainer.onCraftGuiOpened(this);
   }

   @Override
   public void sendEnterCombat() {
      super.sendEnterCombat();
      this.playerNetServerHandler.sendPacket(new S42PacketCombatEvent(this.getCombatTracker(), S42PacketCombatEvent.Event.ENTER_COMBAT));
   }

   @Override
   public void sendEndCombat() {
      super.sendEndCombat();
      this.playerNetServerHandler.sendPacket(new S42PacketCombatEvent(this.getCombatTracker(), S42PacketCombatEvent.Event.END_COMBAT));
   }

   @Override
   public void onUpdate() {
      this.theItemInWorldManager.updateBlockRemoving();
      this.respawnInvulnerabilityTicks--;
      if (this.hurtResistantTime > 0) {
         this.hurtResistantTime--;
      }

      this.openContainer.detectAndSendChanges();
      if (!this.worldObj.isRemote && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.inventoryContainer;
      }

      while (!this.destroyedItemsNetCache.isEmpty()) {
         int i = Math.min(this.destroyedItemsNetCache.size(), Integer.MAX_VALUE);
         int[] ints = new int[i];
         Iterator iterator2 = this.destroyedItemsNetCache.iterator();
         int j = 0;

         while (iterator2.hasNext() && j < i) {
            ints[j++] = (Integer)iterator2.next();
            iterator2.remove();
         }

         this.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(ints));
      }

      if (!this.loadedChunks.isEmpty()) {
         ArrayList arrayList = Lists.newArrayList();
         Iterator iterator3 = this.loadedChunks.iterator();
         ArrayList arrayList2 = Lists.newArrayList();

         while (iterator3.hasNext() && arrayList.size() < 10) {
            ChunkCoordIntPair chunkCoordIntPair = (ChunkCoordIntPair)iterator3.next();
            if (chunkCoordIntPair != null) {
               if (this.worldObj.isBlockLoaded(new BlockPos(chunkCoordIntPair.chunkXPos << 4, 0, chunkCoordIntPair.chunkZPos << 4))) {
                  Chunk chunk = this.worldObj.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
                  if (chunk.isPopulated()) {
                     arrayList.add(chunk);
                     arrayList2.addAll(
                        ((WorldServer)this.worldObj)
                           .getTileEntitiesIn(chunkCoordIntPair.chunkXPos * 16, 0, chunkCoordIntPair.chunkZPos * 16, chunkCoordIntPair.chunkXPos * 16 + 16, 256, chunkCoordIntPair.chunkZPos * 16 + 16)
                     );
                     iterator3.remove();
                  }
               }
            } else {
               iterator3.remove();
            }
         }

         if (!arrayList.isEmpty()) {
            if (arrayList.size() == 1) {
               this.playerNetServerHandler.sendPacket(new S21PacketChunkData((Chunk)arrayList.get(0), true, 65535));
            } else {
               this.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(arrayList));
            }

            for (Object object : arrayList2) {
               TileEntity tileEntity = (TileEntity)object;
               this.sendTileEntityUpdate(tileEntity);
            }

            for (Object object2 : arrayList) {
               Chunk chunk2 = (Chunk)object2;
               this.getServerForPlayer().getEntityTracker().func_85172_a(this, chunk2);
            }
         }
      }

      Entity entity = this.getSpectatingEntity();
      if (entity != this) {
         if (!entity.isEntityAlive()) {
            this.setSpectatingEntity(this);
         } else {
            this.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this);
            if (this.isSneaking()) {
               this.setSpectatingEntity(this);
            }
         }
      }
   }

   public void onUpdateEntity() {
      try {
         super.onUpdate();

         for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
            ItemStack itemStack = this.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem().isMap()) {
               Packet packet = ((ItemMapBase)itemStack.getItem()).createMapDataPacket(itemStack, this.worldObj, this);
               if (packet != null) {
                  this.playerNetServerHandler.sendPacket(packet);
               }
            }
         }

         if (this.getHealth() != this.lastHealth
            || this.lastFoodLevel != this.foodStats.getFoodLevel()
            || this.foodStats.getSaturationLevel() == 0.0F != this.wasHungry) {
            this.playerNetServerHandler
               .sendPacket(new S06PacketUpdateHealth(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
            this.lastHealth = this.getHealth();
            this.lastFoodLevel = this.foodStats.getFoodLevel();
            this.wasHungry = this.foodStats.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.combinedHealth) {
            this.combinedHealth = this.getHealth() + this.getAbsorptionAmount();

            for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.health)) {
               this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective).func_96651_a(Arrays.asList(this));
            }
         }

         if (this.experienceTotal != this.lastExperience) {
            this.lastExperience = this.experienceTotal;
            this.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(this.experience, this.experienceTotal, this.experienceLevel));
         }

         if (this.ticksExisted % 20 * 5 == 0 && !this.getStatFile().hasAchievementUnlocked(AchievementList.exploreAllBiomes)) {
            this.updateBiomesExplored();
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Ticking player");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Player being ticked");
         this.addEntityCrashInfo(crashReportCategory);
         throw new ReportedException(crashReport);
      }
   }

   public void updateBiomesExplored() {
      BiomeGenBase biomeGenBase = this.worldObj.getBiomeGenForCoords(new BlockPos(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ)));
      String string = biomeGenBase.biomeName;
      JsonSerializableSet jsonSerializableSet = this.getStatFile().func_150870_b(AchievementList.exploreAllBiomes);
      if (jsonSerializableSet == null) {
         jsonSerializableSet = this.getStatFile().func_150872_a(AchievementList.exploreAllBiomes, new JsonSerializableSet());
      }

      jsonSerializableSet.add(string);
      if (this.getStatFile().canUnlockAchievement(AchievementList.exploreAllBiomes) && jsonSerializableSet.size() >= BiomeGenBase.explorationBiomesList.size()) {
         HashSet hashSet = Sets.newHashSet(BiomeGenBase.explorationBiomesList);

         for (String string2 : jsonSerializableSet) {
            Iterator iterator2 = hashSet.iterator();

            while (iterator2.hasNext()) {
               BiomeGenBase biomeGenBase2 = (BiomeGenBase)iterator2.next();
               if (biomeGenBase2.biomeName.equals(string2)) {
                  iterator2.remove();
               }
            }

            if (hashSet.isEmpty()) {
               break;
            }
         }

         if (hashSet.isEmpty()) {
            this.triggerAchievement(AchievementList.exploreAllBiomes);
         }
      }
   }

   @Override
   public void onDeath(DamageSource cause) {
      if (this.worldObj.getGameRules().getBoolean("showDeathMessages")) {
         Team team = this.getTeam();
         if (team == null || team.getDeathMessageVisibility() == Team.EnumVisible.ALWAYS) {
            this.mcServer.getConfigurationManager().sendChatMsg(this.getCombatTracker().getDeathMessage());
         } else if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OTHER_TEAMS) {
            this.mcServer.getConfigurationManager().sendMessageToAllTeamMembers(this, this.getCombatTracker().getDeathMessage());
         } else if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OWN_TEAM) {
            this.mcServer.getConfigurationManager().sendMessageToTeamOrEvryPlayer(this, this.getCombatTracker().getDeathMessage());
         }
      }

      if (!this.worldObj.getGameRules().getBoolean("keepInventory")) {
         this.inventory.dropAllItems();
      }

      for (ScoreObjective scoreObjective : this.worldObj.getScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.deathCount)) {
         Score score = this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective);
         score.func_96648_a();
      }

      EntityLivingBase entityLivingBase = this.getAttackingEntity();
      if (entityLivingBase != null) {
         EntityList.EntityEggInfo entityEggInfo = EntityList.entityEggs.get(EntityList.getEntityID(entityLivingBase));
         if (entityEggInfo != null) {
            this.triggerAchievement(entityEggInfo.field_151513_e);
         }

         entityLivingBase.addToPlayerScore(this, this.scoreValue);
      }

      this.triggerAchievement(StatList.deathsStat);
      this.func_175145_a(StatList.timeSinceDeathStat);
      this.getCombatTracker().reset();
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      boolean bl = this.mcServer.isDedicatedServer() && this.canPlayersAttack() && "fall".equals(source.damageType);
      if (!bl && this.respawnInvulnerabilityTicks > 0 && source != DamageSource.outOfWorld) {
         return false;
      }

      if (source instanceof EntityDamageSource) {
         Entity entity = source.getEntity();
         if (entity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entity)) {
            return false;
         }

         if (entity instanceof EntityArrow) {
            EntityArrow entityArrow = (EntityArrow)entity;
            if (entityArrow.shootingEntity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entityArrow.shootingEntity)) {
               return false;
            }
         }
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public boolean canAttackPlayer(EntityPlayer other) {
      return !this.canPlayersAttack() ? false : super.canAttackPlayer(other);
   }

   private boolean canPlayersAttack() {
      return this.mcServer.isPVPEnabled();
   }

   @Override
   public void travelToDimension(int dimensionId) {
      if (this.dimension == 1 && dimensionId == 1) {
         this.triggerAchievement(AchievementList.theEnd2);
         this.worldObj.removeEntity(this);
         this.playerConqueredTheEnd = true;
         this.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(4, 0.0F));
      } else {
         if (this.dimension == 0 && dimensionId == 1) {
            this.triggerAchievement(AchievementList.theEnd);
            BlockPos blockPos = this.mcServer.worldServerForDimension(dimensionId).getSpawnCoordinate();
            if (blockPos != null) {
               this.playerNetServerHandler.setPlayerLocation(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.0F, 0.0F);
            }

            dimensionId = 1;
         } else {
            this.triggerAchievement(AchievementList.portal);
         }

         this.mcServer.getConfigurationManager().transferPlayerToDimension(this, dimensionId);
         this.lastExperience = -1;
         this.lastHealth = -1.0F;
         this.lastFoodLevel = -1;
      }
   }

   @Override
   public boolean isSpectatedByPlayer(EntityPlayerMP player) {
      return player.isSpectator() ? this.getSpectatingEntity() == this : (this.isSpectator() ? false : super.isSpectatedByPlayer(player));
   }

   private void sendTileEntityUpdate(TileEntity tileEntity) {
      if (tileEntity != null) {
         Packet packet = tileEntity.getDescriptionPacket();
         if (packet != null) {
            this.playerNetServerHandler.sendPacket(packet);
         }
      }
   }

   @Override
   public void onItemPickup(Entity entity, int i) {
      super.onItemPickup(entity, i);
      this.openContainer.detectAndSendChanges();
   }

   @Override
   public EntityPlayer.EnumStatus trySleep(BlockPos bedLocation) {
      EntityPlayer.EnumStatus enumStatus = super.trySleep(bedLocation);
      if (enumStatus == EntityPlayer.EnumStatus.OK) {
         S0APacketUseBed s0APacketUseBed = new S0APacketUseBed(this, bedLocation);
         this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntity(this, s0APacketUseBed);
         this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.playerNetServerHandler.sendPacket(s0APacketUseBed);
      }

      return enumStatus;
   }

   @Override
   public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
      if (this.isPlayerSleeping()) {
         this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(this, 2));
      }

      super.wakeUpPlayer(immediately, updateWorldFlag, setSpawn);
      if (this.playerNetServerHandler != null) {
         this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }
   }

   @Override
   public void mountEntity(Entity entityIn) {
      Entity entity = this.ridingEntity;
      super.mountEntity(entityIn);
      if (entityIn != entity) {
         this.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this, this.ridingEntity));
         this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }
   }

   @Override
   public void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
   }

   public void handleFalling(double d, boolean bl) {
      int i = MathHelper.floor_double(this.posX);
      int j = MathHelper.floor_double(this.posY - 0.2F);
      int k = MathHelper.floor_double(this.posZ);
      BlockPos blockPos = new BlockPos(i, j, k);
      Block block = this.worldObj.getBlockState(blockPos).getBlock();
      if (block.getMaterial() == Material.air) {
         Block block2 = this.worldObj.getBlockState(blockPos.down()).getBlock();
         if (block2 instanceof BlockFence || block2 instanceof BlockWall || block2 instanceof BlockFenceGate) {
            blockPos = blockPos.down();
            block = this.worldObj.getBlockState(blockPos).getBlock();
         }
      }

      super.updateFallState(d, bl, block, blockPos);
   }

   @Override
   public void openEditSign(TileEntitySign signTile) {
      signTile.setPlayer(this);
      this.playerNetServerHandler.sendPacket(new S36PacketSignEditorOpen(signTile.getPos()));
   }

   private void getNextWindowId() {
      this.currentWindowId = this.currentWindowId % 100 + 1;
   }

   @Override
   public void displayGui(IInteractionObject guiOwner) {
      this.getNextWindowId();
      this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, guiOwner.getGuiID(), guiOwner.getDisplayName()));
      this.openContainer = guiOwner.createContainer(this.inventory, this);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
   }

   @Override
   public void displayGUIChest(IInventory chestInventory) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      if (chestInventory instanceof ILockableContainer) {
         ILockableContainer iLockableContainer = (ILockableContainer)chestInventory;
         if (iLockableContainer.isLocked() && !this.canOpen(iLockableContainer.getLockCode()) && !this.isSpectator()) {
            this.playerNetServerHandler
               .sendPacket(new S02PacketChat(new ChatComponentTranslation("container.isLocked", chestInventory.getDisplayName()), (byte)2));
            this.playerNetServerHandler.sendPacket(new S29PacketSoundEffect("random.door_close", this.posX, this.posY, this.posZ, 1.0F, 1.0F));
            return;
         }
      }

      this.getNextWindowId();
      if (chestInventory instanceof IInteractionObject) {
         this.playerNetServerHandler
            .sendPacket(
               new S2DPacketOpenWindow(
                  this.currentWindowId, ((IInteractionObject)chestInventory).getGuiID(), chestInventory.getDisplayName(), chestInventory.getSizeInventory()
               )
            );
         this.openContainer = ((IInteractionObject)chestInventory).createContainer(this.inventory, this);
      } else {
         this.playerNetServerHandler
            .sendPacket(
               new S2DPacketOpenWindow(this.currentWindowId, "minecraft:container", chestInventory.getDisplayName(), chestInventory.getSizeInventory())
            );
         this.openContainer = new ContainerChest(this.inventory, chestInventory, this);
      }

      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
   }

   @Override
   public void displayVillagerTradeGui(IMerchant villager) {
      this.getNextWindowId();
      this.openContainer = new ContainerMerchant(this.inventory, villager, this.worldObj);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
      InventoryMerchant inventoryMerchant = ((ContainerMerchant)this.openContainer).getMerchantInventory();
      IChatComponent iChatComponent = villager.getDisplayName();
      this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, "minecraft:villager", iChatComponent, inventoryMerchant.getSizeInventory()));
      MerchantRecipeList merchantRecipeList = villager.getRecipes(this);
      if (merchantRecipeList != null) {
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeInt(this.currentWindowId);
         merchantRecipeList.writeToBuf(packetBuffer);
         this.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|TrList", packetBuffer));
      }
   }

   @Override
   public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      this.getNextWindowId();
      this.playerNetServerHandler
         .sendPacket(
            new S2DPacketOpenWindow(
               this.currentWindowId, "EntityHorse", horseInventory.getDisplayName(), horseInventory.getSizeInventory(), horse.getEntityId()
            )
         );
      this.openContainer = new ContainerHorseInventory(this.inventory, horseInventory, horse, this);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
   }

   @Override
   public void displayGUIBook(ItemStack bookStack) {
      Item item = bookStack.getItem();
      if (item == Items.written_book) {
         this.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|BOpen", new PacketBuffer(Unpooled.buffer())));
      }
   }

   @Override
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      if (!(containerToSend.getSlot(slotInd) instanceof SlotCrafting) && !this.isChangingQuantityOnly) {
         this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(containerToSend.windowId, slotInd, stack));
      }
   }

   public void sendContainerToPlayer(Container container) {
      this.updateCraftingInventory(container, container.getInventory());
   }

   @Override
   public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList) {
      this.playerNetServerHandler.sendPacket(new S30PacketWindowItems(containerToSend.windowId, itemsList));
      this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
   }

   @Override
   public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue) {
      this.playerNetServerHandler.sendPacket(new S31PacketWindowProperty(containerIn.windowId, varToUpdate, newValue));
   }

   @Override
   public void sendAllWindowProperties(Container container, IInventory iInventory) {
      for (int i = 0; i < iInventory.getFieldCount(); i++) {
         this.playerNetServerHandler.sendPacket(new S31PacketWindowProperty(container.windowId, i, iInventory.getField(i)));
      }
   }

   @Override
   public void closeScreen() {
      this.playerNetServerHandler.sendPacket(new S2EPacketCloseWindow(this.openContainer.windowId));
      this.closeContainer();
   }

   public void updateHeldItem() {
      if (!this.isChangingQuantityOnly) {
         this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
      }
   }

   public void closeContainer() {
      this.openContainer.onContainerClosed(this);
      this.openContainer = this.inventoryContainer;
   }

   public void setEntityActionState(float f, float g, boolean bl, boolean sneaking) {
      if (this.ridingEntity != null) {
         if (f >= -1.0F && f <= 1.0F) {
            this.moveStrafing = f;
         }

         if (g >= -1.0F && g <= 1.0F) {
            this.moveForward = g;
         }

         this.isJumping = bl;
         this.setSneaking(sneaking);
      }
   }

   @Override
   public void addStat(StatBase stat, int amount) {
      if (stat != null) {
         this.statsFile.increaseStat(this, stat, amount);

         for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(stat.getCriteria())) {
            this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective).increseScore(amount);
         }

         if (this.statsFile.func_150879_e()) {
            this.statsFile.func_150876_a(this);
         }
      }
   }

   @Override
   public void func_175145_a(StatBase statBase) {
      if (statBase != null) {
         this.statsFile.unlockAchievement(this, statBase, 0);

         for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(statBase.getCriteria())) {
            this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective).setScorePoints(0);
         }

         if (this.statsFile.func_150879_e()) {
            this.statsFile.func_150876_a(this);
         }
      }
   }

   public void mountEntityAndWakeUp() {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.mountEntity(this);
      }

      if (this.sleeping) {
         this.wakeUpPlayer(true, false, false);
      }
   }

   public void setPlayerHealthUpdated() {
      this.lastHealth = -1.0E8F;
   }

   @Override
   public void addChatComponentMessage(IChatComponent chatComponent) {
      this.playerNetServerHandler.sendPacket(new S02PacketChat(chatComponent));
   }

   @Override
   public void onItemUseFinish() {
      this.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(this, (byte)9));
      super.onItemUseFinish();
   }

   @Override
   public void setItemInUse(ItemStack stack, int duration) {
      super.setItemInUse(stack, duration);
      if (stack != null && stack.getItem() != null && stack.getItem().getItemUseAction(stack) == EnumAction.EAT) {
         this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(this, 3));
      }
   }

   @Override
   public void clonePlayer(EntityPlayer oldPlayer, boolean respawnFromEnd) {
      super.clonePlayer(oldPlayer, respawnFromEnd);
      this.lastExperience = -1;
      this.lastHealth = -1.0F;
      this.lastFoodLevel = -1;
      this.destroyedItemsNetCache.addAll(((EntityPlayerMP)oldPlayer).destroyedItemsNetCache);
   }

   @Override
   public void onNewPotionEffect(PotionEffect id) {
      super.onNewPotionEffect(id);
      this.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.getEntityId(), id));
   }

   @Override
   public void onChangedPotionEffect(PotionEffect id, boolean bl) {
      super.onChangedPotionEffect(id, bl);
      this.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.getEntityId(), id));
   }

   @Override
   public void onFinishedPotionEffect(PotionEffect effect) {
      super.onFinishedPotionEffect(effect);
      this.playerNetServerHandler.sendPacket(new S1EPacketRemoveEntityEffect(this.getEntityId(), effect));
   }

   @Override
   public void setPositionAndUpdate(double x, double y, double z) {
      this.playerNetServerHandler.setPlayerLocation(x, y, z, this.rotationYaw, this.rotationPitch);
   }

   @Override
   public void onCriticalHit(Entity entityHit) {
      this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(entityHit, 4));
   }

   @Override
   public void onEnchantmentCritical(Entity entityHit) {
      this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(entityHit, 5));
   }

   @Override
   public void sendPlayerAbilities() {
      if (this.playerNetServerHandler != null) {
         this.playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(this.capabilities));
         this.updatePotionMetadata();
      }
   }

   public WorldServer getServerForPlayer() {
      return (WorldServer)this.worldObj;
   }

   @Override
   public void setGameType(WorldSettings.GameType gameType) {
      this.theItemInWorldManager.setGameType(gameType);
      this.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(3, gameType.getID()));
      if (gameType == WorldSettings.GameType.SPECTATOR) {
         this.mountEntity((Entity)null);
      } else {
         this.setSpectatingEntity(this);
      }

      this.sendPlayerAbilities();
      this.markPotionsDirty();
   }

   @Override
   public boolean isSpectator() {
      return this.theItemInWorldManager.getGameType() == WorldSettings.GameType.SPECTATOR;
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      this.playerNetServerHandler.sendPacket(new S02PacketChat(component));
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      if ("seed".equals(commandName) && !this.mcServer.isDedicatedServer()) {
         return true;
      }

      if (!"tell".equals(commandName) && !"help".equals(commandName) && !"me".equals(commandName) && !"trigger".equals(commandName)) {
         if (this.mcServer.getConfigurationManager().canSendCommands(this.getGameProfile())) {
            UserListOpsEntry userListOpsEntry = this.mcServer.getConfigurationManager().getOppedPlayers().getEntry(this.getGameProfile());
            return userListOpsEntry != null ? userListOpsEntry.getPermissionLevel() >= permLevel : this.mcServer.getOpPermissionLevel() >= permLevel;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public String getPlayerIP() {
      String string = this.playerNetServerHandler.netManager.getRemoteAddress().toString();
      string = string.substring(string.indexOf("/") + 1);
      return string.substring(0, string.indexOf(":"));
   }

   public void handleClientSettings(C15PacketClientSettings packetIn) {
      this.translator = packetIn.getLang();
      this.chatVisibility = packetIn.getChatVisibility();
      this.chatColours = packetIn.isColorsEnabled();
      this.getDataWatcher().updateObject(10, (byte)packetIn.getModelPartFlags());
   }

   public EntityPlayer.EnumChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public void loadResourcePack(String url, String hash) {
      this.playerNetServerHandler.sendPacket(new S48PacketResourcePackSend(url, hash));
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX, this.posY + 0.5, this.posZ);
   }

   public void markPlayerActive() {
      this.playerLastActiveTime = MinecraftServer.getCurrentTimeMillis();
   }

   public StatisticsFile getStatFile() {
      return this.statsFile;
   }

   public void removeEntity(Entity entity) {
      if (entity instanceof EntityPlayer) {
         this.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(entity.getEntityId()));
      } else {
         this.destroyedItemsNetCache.add(entity.getEntityId());
      }
   }

   @Override
   public void updatePotionMetadata() {
      if (this.isSpectator()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(true);
      } else {
         super.updatePotionMetadata();
      }

      this.getServerForPlayer().getEntityTracker().func_180245_a(this);
   }

   public Entity getSpectatingEntity() {
      return this.spectatingEntity == null ? this : this.spectatingEntity;
   }

   public void setSpectatingEntity(Entity entityToSpectate) {
      Entity entity = this.getSpectatingEntity();
      this.spectatingEntity = entityToSpectate == null ? this : entityToSpectate;
      if (entity != this.spectatingEntity) {
         this.playerNetServerHandler.sendPacket(new S43PacketCamera(this.spectatingEntity));
         this.setPositionAndUpdate(this.spectatingEntity.posX, this.spectatingEntity.posY, this.spectatingEntity.posZ);
      }
   }

   @Override
   public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
      if (this.theItemInWorldManager.getGameType() == WorldSettings.GameType.SPECTATOR) {
         this.setSpectatingEntity(targetEntity);
      } else {
         super.attackTargetEntityWithCurrentItem(targetEntity);
      }
   }

   public long getLastActiveTime() {
      return this.playerLastActiveTime;
   }

   public IChatComponent getTabListDisplayName() {
      return null;
   }
}
