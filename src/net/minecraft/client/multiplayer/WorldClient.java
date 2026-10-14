package net.minecraft.client.multiplayer;

import bypass.server.ServerManager;
import bypass.server.ServerType;
import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFirework$StarterFX;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.BlockPosM;
import net.minecraft.src.Config;
import net.minecraft.src.DynamicLights;
import net.minecraft.src.PlayerControllerOF;
import net.minecraft.src.Reflector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

public class WorldClient extends World {
   private NetHandlerPlayClient sendQueue;
   private ChunkProviderClient clientChunkProvider;
   private final Set<Entity> entityList = Sets.newHashSet();
   private final Set<Entity> entitySpawnQueue = Sets.newHashSet();
   private final Minecraft mc = Minecraft.getMinecraft();
   private final Set<ChunkCoordIntPair> previousActiveChunkSet = Sets.newHashSet();
   private BlockPosM randomTickPosM = new BlockPosM(0, 0, 0, 3);
   private boolean playerUpdate = false;

   public WorldClient(NetHandlerPlayClient netHandler, WorldSettings settings, int dimension, EnumDifficulty difficulty, Profiler profilerIn) {
      super(new SaveHandlerMP(), new WorldInfo(settings, "MpServer"), WorldProvider.getProviderForDimension(dimension), profilerIn, true);
      this.sendQueue = netHandler;
      this.getWorldInfo().setDifficulty(difficulty);
      this.provider.registerWorld(this);
      this.setSpawnPoint(new BlockPos(8, 64, 8));
      this.chunkProvider = this.createChunkProvider();
      this.mapStorage = new SaveDataMemoryStorage();
      this.a();
      this.H();
      Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, this);
      if (this.mc.playerController != null && this.mc.playerController.getClass() == PlayerControllerMP.class) {
         this.mc.playerController = new PlayerControllerOF(this.mc, netHandler);
      }
   }

   @Override
   public void tick() {
      super.tick();
      this.m(this.getTotalWorldTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.setWorldTime(this.getWorldTime() + 1L);
      }

      this.theProfiler.startSection("reEntryProcessing");

      for (int i = 0; i < 10 && !this.entitySpawnQueue.isEmpty(); i++) {
         Entity entity = this.entitySpawnQueue.iterator().next();
         this.entitySpawnQueue.remove(entity);
         if (!this.loadedEntityList.contains(entity)) {
            this.spawnEntityInWorld(entity);
         }
      }

      this.theProfiler.endStartSection("chunkCache");
      this.clientChunkProvider.unloadQueuedChunks();
      this.theProfiler.endStartSection("blocks");
      this.updateBlocks();
      this.theProfiler.endSection();
   }

   public void invalidateBlockReceiveRegion(int x1, int y1, int z1, int x2, int y2, int z2) {
   }

   @Override
   public IChunkProvider createChunkProvider() {
      this.clientChunkProvider = new ChunkProviderClient(this);
      return this.clientChunkProvider;
   }

   @Override
   public void updateBlocks() {
      super.updateBlocks();
      this.previousActiveChunkSet.retainAll(this.activeChunkSet);
      if (this.previousActiveChunkSet.size() == this.activeChunkSet.size()) {
         this.previousActiveChunkSet.clear();
      }

      int i = 0;

      for (ChunkCoordIntPair chunkCoordIntPair : this.activeChunkSet) {
         if (!this.previousActiveChunkSet.contains(chunkCoordIntPair)) {
            int j = chunkCoordIntPair.chunkXPos * 16;
            int k = chunkCoordIntPair.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunk = this.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
            this.playMoodSoundAndCheckLight(j, k, chunk);
            this.theProfiler.endSection();
            this.previousActiveChunkSet.add(chunkCoordIntPair);
            if (++i >= 10) {
               return;
            }
         }
      }
   }

   public void doPreChunk(int chuncX, int chuncZ, boolean loadChunk) {
      if (loadChunk) {
         this.clientChunkProvider.loadChunk(chuncX, chuncZ);
      } else {
         this.clientChunkProvider.unloadChunk(chuncX, chuncZ);
      }

      if (!loadChunk) {
         this.markBlockRangeForRenderUpdate(chuncX * 16, 0, chuncZ * 16, chuncX * 16 + 15, 256, chuncZ * 16 + 15);
      }
   }

   @Override
   public boolean spawnEntityInWorld(Entity entityIn) {
      boolean bl = super.spawnEntityInWorld(entityIn);
      this.entityList.add(entityIn);
      if (!bl) {
         this.entitySpawnQueue.add(entityIn);
      } else if (entityIn instanceof EntityMinecart) {
         this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart)entityIn));
      }

      return bl;
   }

   @Override
   public void removeEntity(Entity entityIn) {
      super.removeEntity(entityIn);
      this.entityList.remove(entityIn);
   }

   @Override
   public void onEntityAdded(Entity entityIn) {
      super.onEntityAdded(entityIn);
      if (this.entitySpawnQueue.contains(entityIn)) {
         this.entitySpawnQueue.remove(entityIn);
      }
   }

   @Override
   public void onEntityRemoved(Entity entityIn) {
      super.onEntityRemoved(entityIn);
      if (this.entityList.contains(entityIn)) {
         if (entityIn.isEntityAlive()) {
            this.entitySpawnQueue.add(entityIn);
         } else {
            this.entityList.remove(entityIn);
         }
      }
   }

   public void addEntityToWorld(int entityID, Entity entityToSpawn) {
      Entity entity = this.getEntityByID(entityID);
      if (entity != null) {
         this.removeEntity(entity);
      }

      this.entityList.add(entityToSpawn);
      entityToSpawn.setEntityId(entityID);
      if (!this.spawnEntityInWorld(entityToSpawn)) {
         this.entitySpawnQueue.add(entityToSpawn);
      }

      this.entitiesById.addKey(entityID, entityToSpawn);
   }

   @Override
   public Entity getEntityByID(int id) {
      return id == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : super.getEntityByID(id);
   }

   public Entity removeEntityFromWorld(int entityID) {
      Entity entity = this.entitiesById.removeObject(entityID);
      if (entity != null) {
         this.entityList.remove(entity);
         this.removeEntity(entity);
      }

      return entity;
   }

   public boolean invalidateRegionAndSetBlock(BlockPos pos, IBlockState state) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      this.invalidateBlockReceiveRegion(i, j, k, i, j, k);
      return super.setBlockState(pos, state, 3);
   }

   @Override
   public void sendQuittingDisconnectingPacket() {
      this.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("Quitting"));
      ServerManager.c(ServerType.UNKNOWN);
   }

   @Override
   public void updateWeather() {
   }

   @Override
   public int getRenderDistanceChunks() {
      return this.mc.gameSettings.renderDistanceChunks;
   }

   public void doVoidFogParticles(int posX, int posY, int posZ) {
      byte b = 16;
      Random random = new Random();
      ItemStack itemStack = this.mc.thePlayer.getHeldItem();
      boolean bl = this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.CREATIVE
         && itemStack != null
         && Block.getBlockFromItem(itemStack.getItem()) == Blocks.barrier;
      BlockPosM blockPosM = this.randomTickPosM;

      for (int i = 0; i < 1000; i++) {
         int j = posX + this.rand.nextInt(b) - this.rand.nextInt(b);
         int k = posY + this.rand.nextInt(b) - this.rand.nextInt(b);
         int l = posZ + this.rand.nextInt(b) - this.rand.nextInt(b);
         blockPosM.setXyz(j, k, l);
         IBlockState iBlockState = this.getBlockState(blockPosM);
         iBlockState.getBlock().randomDisplayTick(this, blockPosM, iBlockState, random);
         if (bl && iBlockState.getBlock() == Blocks.barrier) {
            this.spawnParticle(EnumParticleTypes.BARRIER, j + 0.5F, k + 0.5F, l + 0.5F, 0.0, 0.0, 0.0);
         }
      }
   }

   public void removeAllEntities() {
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for (int i = 0; i < this.unloadedEntityList.size(); i++) {
         Entity entity = this.unloadedEntityList.get(i);
         int j = entity.chunkCoordX;
         int k = entity.chunkCoordZ;
         if (entity.addedToChunk && this.isChunkLoaded(j, k, true)) {
            this.getChunkFromChunkCoords(j, k).removeEntity(entity);
         }
      }

      for (int l = 0; l < this.unloadedEntityList.size(); l++) {
         this.onEntityRemoved(this.unloadedEntityList.get(l));
      }

      this.unloadedEntityList.clear();

      for (int m = 0; m < this.loadedEntityList.size(); m++) {
         Entity entity2 = this.loadedEntityList.get(m);
         if (entity2.ridingEntity != null) {
            if (!entity2.ridingEntity.isDead && entity2.ridingEntity.riddenByEntity == entity2) {
               continue;
            }

            entity2.ridingEntity.riddenByEntity = null;
            entity2.ridingEntity = null;
         }

         if (entity2.isDead) {
            int n = entity2.chunkCoordX;
            int i2 = entity2.chunkCoordZ;
            if (entity2.addedToChunk && this.isChunkLoaded(n, i2, true)) {
               this.getChunkFromChunkCoords(n, i2).removeEntity(entity2);
            }

            this.loadedEntityList.remove(m--);
            this.onEntityRemoved(entity2);
         }
      }
   }

   @Override
   public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
      CrashReportCategory crashReportCategory = super.addWorldInfoToCrashReport(report);
      crashReportCategory.addCrashSectionCallable("Forced entities", new Callable<String>() {
         public String call() {
            return WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList.toString();
         }
      });
      crashReportCategory.addCrashSectionCallable("Retry entities", new Callable<String>() {
         public String call() {
            return WorldClient.this.entitySpawnQueue.size() + " total; " + WorldClient.this.entitySpawnQueue.toString();
         }
      });
      crashReportCategory.addCrashSectionCallable("Server brand", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.mc.thePlayer.getClientBrand();
         }
      });
      crashReportCategory.addCrashSectionCallable("Server type", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
         }
      });
      return crashReportCategory;
   }

   public void playSoundAtPos(BlockPos pos, String soundName, float volume, float pitch, boolean distanceDelay) {
      this.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundName, volume, pitch, distanceDelay);
   }

   @Override
   public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay) {
      double d = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
      PositionedSoundRecord positionedSoundRecord = new PositionedSoundRecord(new ResourceLocation(soundName), volume, pitch, (float)x, (float)y, (float)z);
      if (distanceDelay && d > 100.0) {
         double e = Math.sqrt(d) / 40.0;
         this.mc.getSoundHandler().playDelayedSound(positionedSoundRecord, (int)(e * 20.0));
      } else {
         this.mc.getSoundHandler().playSound(positionedSoundRecord);
      }
   }

   @Override
   public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound compund) {
      this.mc.effectRenderer.addEffect(new EntityFirework$StarterFX(this, x, y, z, motionX, motionY, motionZ, this.mc.effectRenderer, compund));
   }

   public void setWorldScoreboard(Scoreboard scoreboardIn) {
      this.worldScoreboard = scoreboardIn;
   }

   @Override
   public void setWorldTime(long time) {
      if (time < 0L) {
         time = -time;
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
      } else {
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
      }

      super.setWorldTime(time);
   }

   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int i = super.getCombinedLight(pos, lightValue);
      if (Config.isDynamicLights()) {
         i = DynamicLights.getCombinedLight(pos, i);
      }

      return i;
   }

   @Override
   public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
      this.playerUpdate = this.isPlayerActing();
      boolean bl = super.setBlockState(pos, newState, flags);
      this.playerUpdate = false;
      return bl;
   }

   private boolean isPlayerActing() {
      if (this.mc.playerController instanceof PlayerControllerOF) {
         PlayerControllerOF playerControllerOF = (PlayerControllerOF)this.mc.playerController;
         return playerControllerOF.isActing();
      } else {
         return false;
      }
   }

   public boolean o() {
      return this.playerUpdate;
   }
}
