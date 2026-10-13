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
   private static final long u = 120746963449654L;

   public WorldClient(NetHandlerPlayClient var1, WorldSettings var2, int var3, EnumDifficulty var4, Profiler var5) {
      super(new SaveHandlerMP(), new WorldInfo(var2, "MpServer"), WorldProvider.getProviderForDimension(var3), var5, true);
      this.sendQueue = var1;
      this.getWorldInfo().setDifficulty(var4);
      this.provider.registerWorld(this);
      this.setSpawnPoint(new BlockPos(8, 64, 8));
      this.chunkProvider = this.createChunkProvider();
      this.mapStorage = new SaveDataMemoryStorage();
      this.a();
      this.H();
      Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, this);
      if (this.mc.playerController != null && this.mc.playerController.getClass() == PlayerControllerMP.class) {
         this.mc.playerController = new PlayerControllerOF(this.mc, var1);
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

      for (int var4 = 0; var4 < 10 && !this.entitySpawnQueue.isEmpty(); var4++) {
         Entity var5 = this.entitySpawnQueue.iterator().next();
         this.entitySpawnQueue.remove(var5);
         if (!this.loadedEntityList.contains(var5)) {
            this.spawnEntityInWorld(var5);
         }
      }

      this.theProfiler.endStartSection("chunkCache");
      this.clientChunkProvider.unloadQueuedChunks();
      this.theProfiler.endStartSection("blocks");
      this.updateBlocks();
      this.theProfiler.endSection();
   }

   public void invalidateBlockReceiveRegion(int var1, int var2, int var3, int var4, int var5, int var6) {
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

      int var4 = 0;

      for (ChunkCoordIntPair var6 : this.activeChunkSet) {
         if (!this.previousActiveChunkSet.contains(var6)) {
            int var7 = var6.chunkXPos * 16;
            int var8 = var6.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk var9 = this.getChunkFromChunkCoords(var6.chunkXPos, var6.chunkZPos);
            this.playMoodSoundAndCheckLight(var7, var8, var9);
            this.theProfiler.endSection();
            this.previousActiveChunkSet.add(var6);
            if (++var4 >= 10) {
               return;
            }
         }
      }
   }

   public void doPreChunk(int var1, int var2, boolean var3) {
      if (var3) {
         this.clientChunkProvider.loadChunk(var1, var2);
      } else {
         this.clientChunkProvider.unloadChunk(var1, var2);
      }

      if (!var3) {
         this.markBlockRangeForRenderUpdate(var1 * 16, 0, var2 * 16, var1 * 16 + 15, 256, var2 * 16 + 15);
      }
   }

   @Override
   public boolean spawnEntityInWorld(Entity var1) {
      boolean var5 = super.spawnEntityInWorld(var1);
      this.entityList.add(var1);
      if (!var5) {
         this.entitySpawnQueue.add(var1);
      } else if (var1 instanceof EntityMinecart) {
         this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart)var1));
      }

      return var5;
   }

   @Override
   public void removeEntity(Entity var1) {
      super.removeEntity(var1);
      this.entityList.remove(var1);
   }

   @Override
   public void onEntityAdded(Entity var1) {
      super.onEntityAdded(var1);
      if (this.entitySpawnQueue.contains(var1)) {
         this.entitySpawnQueue.remove(var1);
      }
   }

   @Override
   public void onEntityRemoved(Entity var1) {
      super.onEntityRemoved(var1);
      if (this.entityList.contains(var1)) {
         if (var1.isEntityAlive()) {
            this.entitySpawnQueue.add(var1);
         } else {
            this.entityList.remove(var1);
         }
      }
   }

   public void addEntityToWorld(int var1, Entity var2) {
      Entity var6 = this.getEntityByID(var1);
      if (var6 != null) {
         this.removeEntity(var6);
      }

      this.entityList.add(var2);
      var2.setEntityId(var1);
      if (!this.spawnEntityInWorld(var2)) {
         this.entitySpawnQueue.add(var2);
      }

      this.entitiesById.addKey(var1, var2);
   }

   @Override
   public Entity getEntityByID(int var1) {
      return var1 == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : super.getEntityByID(var1);
   }

   public Entity removeEntityFromWorld(int var1) {
      Entity var5 = this.entitiesById.removeObject(var1);
      if (var5 != null) {
         this.entityList.remove(var5);
         this.removeEntity(var5);
      }

      return var5;
   }

   public boolean invalidateRegionAndSetBlock(BlockPos var1, IBlockState var2) {
      int var3 = var1.getX();
      int var4 = var1.getY();
      int var5 = var1.getZ();
      this.invalidateBlockReceiveRegion(var3, var4, var5, var3, var4, var5);
      return super.setBlockState(var1, var2, 3);
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

   public void doVoidFogParticles(int var1, int var2, int var3) {
      byte var7 = 16;
      Random var8 = new Random();
      ItemStack var9 = this.mc.thePlayer.getHeldItem();
      boolean var10 = this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.CREATIVE
         && var9 != null
         && Block.getBlockFromItem(var9.getItem()) == Blocks.barrier;
      BlockPosM var11 = this.randomTickPosM;

      for (int var12 = 0; var12 < 1000; var12++) {
         int var13 = var1 + this.rand.nextInt(var7) - this.rand.nextInt(var7);
         int var14 = var2 + this.rand.nextInt(var7) - this.rand.nextInt(var7);
         int var15 = var3 + this.rand.nextInt(var7) - this.rand.nextInt(var7);
         var11.setXyz(var13, var14, var15);
         IBlockState var16 = this.getBlockState(var11);
         var16.getBlock().randomDisplayTick(this, var11, var16, var8);
         if (var10 && var16.getBlock() == Blocks.barrier) {
            this.spawnParticle(EnumParticleTypes.BARRIER, var13 + 0.5F, var14 + 0.5F, var15 + 0.5F, 0.0, 0.0, 0.0);
         }
      }
   }

   public void removeAllEntities() {
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for (int var4 = 0; var4 < this.unloadedEntityList.size(); var4++) {
         Entity var5 = this.unloadedEntityList.get(var4);
         int var6 = var5.chunkCoordX;
         int var7 = var5.chunkCoordZ;
         if (var5.addedToChunk && this.isChunkLoaded(var6, var7, true)) {
            this.getChunkFromChunkCoords(var6, var7).removeEntity(var5);
         }
      }

      for (int var8 = 0; var8 < this.unloadedEntityList.size(); var8++) {
         this.onEntityRemoved(this.unloadedEntityList.get(var8));
      }

      this.unloadedEntityList.clear();

      for (int var9 = 0; var9 < this.loadedEntityList.size(); var9++) {
         Entity var10 = this.loadedEntityList.get(var9);
         if (var10.ridingEntity != null) {
            if (!var10.ridingEntity.isDead && var10.ridingEntity.riddenByEntity == var10) {
               continue;
            }

            var10.ridingEntity.riddenByEntity = null;
            var10.ridingEntity = null;
         }

         if (var10.isDead) {
            int var11 = var10.chunkCoordX;
            int var12 = var10.chunkCoordZ;
            if (var10.addedToChunk && this.isChunkLoaded(var11, var12, true)) {
               this.getChunkFromChunkCoords(var11, var12).removeEntity(var10);
            }

            this.loadedEntityList.remove(var9--);
            this.onEntityRemoved(var10);
         }
      }
   }

   @Override
   public CrashReportCategory addWorldInfoToCrashReport(CrashReport var1) {
      CrashReportCategory var4 = super.addWorldInfoToCrashReport(var1);
      var4.addCrashSectionCallable("Forced entities", new Callable<String>() {
         private static final long a = 13337848206670L;

         public String call() {
            return WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList.toString();
         }
      });
      var4.addCrashSectionCallable("Retry entities", new Callable<String>() {
         private static final long a = 16242806181988L;

         public String call() {
            return WorldClient.this.entitySpawnQueue.size() + " total; " + WorldClient.this.entitySpawnQueue.toString();
         }
      });
      var4.addCrashSectionCallable("Server brand", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.mc.thePlayer.getClientBrand();
         }
      });
      var4.addCrashSectionCallable("Server type", new Callable<String>() {
         private static final long a = 96903777562323L;

         public String call() throws Exception {
            return WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
         }
      });
      return var4;
   }

   public void playSoundAtPos(BlockPos var1, String var2, float var3, float var4, boolean var5) {
      this.playSound(var1.getX() + 0.5, var1.getY() + 0.5, var1.getZ() + 0.5, var2, var3, var4, var5);
   }

   @Override
   public void playSound(double var1, double var3, double var5, String var7, float var8, float var9, boolean var10) {
      double var14 = this.mc.getRenderViewEntity().getDistanceSq(var1, var3, var5);
      PositionedSoundRecord var16 = new PositionedSoundRecord(new ResourceLocation(var7), var8, var9, (float)var1, (float)var3, (float)var5);
      if (var10 && var14 > 100.0) {
         double var17 = Math.sqrt(var14) / 40.0;
         this.mc.getSoundHandler().playDelayedSound(var16, (int)(var17 * 20.0));
      } else {
         this.mc.getSoundHandler().playSound(var16);
      }
   }

   @Override
   public void makeFireworks(double var1, double var3, double var5, double var7, double var9, double var11, NBTTagCompound var13) {
      this.mc.effectRenderer.addEffect(new EntityFirework$StarterFX(this, var1, var3, var5, var7, var9, var11, this.mc.effectRenderer, var13));
   }

   public void setWorldScoreboard(Scoreboard var1) {
      this.worldScoreboard = var1;
   }

   @Override
   public void setWorldTime(long var1) {
      if (var1 < 0L) {
         var1 = -var1;
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
      } else {
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
      }

      super.setWorldTime(var1);
   }

   @Override
   public int getCombinedLight(BlockPos var1, int var2) {
      int var6 = super.getCombinedLight(var1, var2);
      if (Config.isDynamicLights()) {
         var6 = DynamicLights.getCombinedLight(var1, var6);
      }

      return var6;
   }

   @Override
   public boolean setBlockState(BlockPos var1, IBlockState var2, int var3) {
      this.playerUpdate = this.isPlayerActing();
      boolean var4 = super.setBlockState(var1, var2, var3);
      this.playerUpdate = false;
      return var4;
   }

   private boolean isPlayerActing() {
      if (this.mc.playerController instanceof PlayerControllerOF) {
         PlayerControllerOF var4 = (PlayerControllerOF)this.mc.playerController;
         return var4.isActing();
      } else {
         return false;
      }
   }

   public boolean o() {
      return this.playerUpdate;
   }
}
