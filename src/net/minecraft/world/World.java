package net.minecraft.world;

import bypass.module.ModuleManager;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

public abstract class World implements IBlockAccess {
   private int seaLevel = 63;
   public boolean scheduledUpdatesAreImmediate;
   public final List<Entity> loadedEntityList = Lists.newArrayList();
   public final List<Entity> unloadedEntityList = Lists.newArrayList();
   public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
   public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
   private final List<TileEntity> addedTileEntityList = Lists.newArrayList();
   private final List<TileEntity> tileEntitiesToBeRemoved = Lists.newArrayList();
   public final List<EntityPlayer> playerEntities = Lists.newArrayList();
   public final List<Entity> weatherEffects = Lists.newArrayList();
   public final IntHashMap<Entity> entitiesById = new IntHashMap<>();
   private long cloudColour = 16777215L;
   private int skylightSubtracted;
   public int updateLCG = new Random().nextInt();
   public final int DIST_HASH_MAGIC = 1013904223;
   public float prevRainingStrength;
   public float rainingStrength;
   public float prevThunderingStrength;
   public float thunderingStrength;
   private int lastLightningBolt;
   public final Random rand = new Random();
   public final WorldProvider provider;
   public List<IWorldAccess> worldAccesses = Lists.newArrayList();
   public IChunkProvider chunkProvider;
   public final ISaveHandler saveHandler;
   public WorldInfo worldInfo;
   public boolean findingSpawnPoint;
   public MapStorage mapStorage;
   public VillageCollection villageCollectionObj;
   public final Profiler theProfiler;
   private final Calendar theCalendar = Calendar.getInstance();
   public Scoreboard worldScoreboard = new Scoreboard();
   public final boolean isRemote;
   public Set<ChunkCoordIntPair> activeChunkSet = Sets.newHashSet();
   private int ambientTickCountdown = this.rand.nextInt(12000);
   public boolean spawnHostileMobs = true;
   public boolean spawnPeacefulMobs = true;
   private boolean processingLoadedTiles;
   private final WorldBorder worldBorder;
   public int[] lightUpdateBlockList = new int[32768];
   private static final long ab = 123060729954914L;

   public World(ISaveHandler var1, WorldInfo var2, WorldProvider var3, Profiler var4, boolean var5) {
      this.saveHandler = var1;
      this.theProfiler = var4;
      this.worldInfo = var2;
      this.provider = var3;
      this.isRemote = var5;
      this.worldBorder = var3.getWorldBorder();
   }

   public World init() {
      return this;
   }

   @Override
   public BiomeGenBase getBiomeGenForCoords(final BlockPos var1) {
      if (this.isBlockLoaded(var1)) {
         Chunk var5 = this.getChunkFromBlockCoords(var1);

         try {
            return var5.getBiome(var1, this.provider.getWorldChunkManager());
         } catch (Throwable var9) {
            CrashReport var7 = CrashReport.makeCrashReport(var9, "Getting biome");
            CrashReportCategory var8 = var7.makeCategory("Coordinates of biome request");
            var8.addCrashSectionCallable("Location", new Callable<String>() {
               public String call() throws Exception {
                  return CrashReportCategory.getCoordinateInfo(var1);
               }
            });
            throw new ReportedException(var7);
         }
      } else {
         return this.provider.getWorldChunkManager().getBiomeGenerator(var1, BiomeGenBase.plains);
      }
   }

   public WorldChunkManager getWorldChunkManager() {
      return this.provider.getWorldChunkManager();
   }

   public abstract IChunkProvider createChunkProvider();

   public void initialize(WorldSettings var1) {
      this.worldInfo.setServerInitialized(true);
   }

   public void setInitialSpawnLocation() {
      this.setSpawnPoint(new BlockPos(8, 64, 8));
   }

   public Block getGroundAboveSeaLevel(BlockPos var1) {
      BlockPos var5 = new BlockPos(var1.getX(), this.getSeaLevel(), var1.getZ());

      while (!this.isAirBlock(var5.up())) {
         var5 = var5.up();
      }

      return this.getBlockState(var5).getBlock();
   }

   private boolean isValid(BlockPos var1) {
      return var1.getX() >= -30000000 && var1.getZ() >= -30000000 && var1.getX() < 30000000 && var1.getZ() < 30000000 && var1.getY() >= 0 && var1.getY() < 256;
   }

   @Override
   public boolean isAirBlock(BlockPos var1) {
      return this.getBlockState(var1).getBlock().getMaterial() == Material.air;
   }

   public boolean isBlockLoaded(BlockPos var1) {
      return this.isBlockLoaded(var1, true);
   }

   public boolean isBlockLoaded(BlockPos var1, boolean var2) {
      return !this.isValid(var1) ? false : this.isChunkLoaded(var1.getX() >> 4, var1.getZ() >> 4, var2);
   }

   public boolean isAreaLoaded(BlockPos var1, int var2) {
      return this.isAreaLoaded(var1, var2, true);
   }

   public boolean isAreaLoaded(BlockPos var1, int var2, boolean var3) {
      return this.isAreaLoaded(var1.getX() - var2, var1.getY() - var2, var1.getZ() - var2, var1.getX() + var2, var1.getY() + var2, var1.getZ() + var2, var3);
   }

   public boolean isAreaLoaded(BlockPos var1, BlockPos var2) {
      return this.isAreaLoaded(var1, var2, true);
   }

   public boolean isAreaLoaded(BlockPos var1, BlockPos var2, boolean var3) {
      return this.isAreaLoaded(var1.getX(), var1.getY(), var1.getZ(), var2.getX(), var2.getY(), var2.getZ(), var3);
   }

   public boolean isAreaLoaded(StructureBoundingBox var1) {
      return this.isAreaLoaded(var1, true);
   }

   public boolean isAreaLoaded(StructureBoundingBox var1, boolean var2) {
      return this.isAreaLoaded(var1.minX, var1.minY, var1.minZ, var1.maxX, var1.maxY, var1.maxZ, var2);
   }

   private boolean isAreaLoaded(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      if (var5 >= 0 && var2 < 256) {
         var1 >>= 4;
         var3 >>= 4;
         var4 >>= 4;
         var6 >>= 4;

         for (int var11 = var1; var11 <= var4; var11++) {
            for (int var12 = var3; var12 <= var6; var12++) {
               if (!this.isChunkLoaded(var11, var12, var7)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isChunkLoaded(int var1, int var2, boolean var3) {
      return this.chunkProvider.chunkExists(var1, var2) && (var3 || !this.chunkProvider.provideChunk(var1, var2).isEmpty());
   }

   public Chunk getChunkFromBlockCoords(BlockPos var1) {
      return this.getChunkFromChunkCoords(var1.getX() >> 4, var1.getZ() >> 4);
   }

   public Chunk getChunkFromChunkCoords(int var1, int var2) {
      return this.chunkProvider.provideChunk(var1, var2);
   }

   public boolean setBlockState(BlockPos var1, IBlockState var2, int var3) {
      if (!this.isValid(var1)) {
         return false;
      }

      if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         return false;
      }

      Chunk var7 = this.getChunkFromBlockCoords(var1);
      Block var8 = var2.getBlock();
      IBlockState var9 = var7.setBlockState(var1, var2);
      if (var9 == null) {
         return false;
      }

      Block var10 = var9.getBlock();
      if (var8.getLightOpacity() != var10.getLightOpacity() || var8.getLightValue() != var10.getLightValue()) {
         this.theProfiler.startSection("checkLight");
         this.checkLight(var1);
         this.theProfiler.endSection();
      }

      if ((var3 & 2) != 0 && (!this.isRemote || (var3 & 4) == 0) && var7.isPopulated()) {
         this.markBlockForUpdate(var1);
      }

      if (!this.isRemote && (var3 & 1) != 0) {
         this.notifyNeighborsRespectDebug(var1, var9.getBlock());
         if (var8.hasComparatorInputOverride()) {
            this.updateComparatorOutputLevel(var1, var8);
         }
      }

      return true;
   }

   public boolean setBlockToAir(BlockPos var1) {
      return this.setBlockState(var1, Blocks.air.getDefaultState(), 3);
   }

   public boolean destroyBlock(BlockPos var1, boolean var2) {
      IBlockState var6 = this.getBlockState(var1);
      Block var7 = var6.getBlock();
      if (var7.getMaterial() == Material.air) {
         return false;
      }

      this.playAuxSFX(2001, var1, Block.getStateId(var6));
      if (var2) {
         var7.dropBlockAsItem(this, var1, var6, 0);
      }

      return this.setBlockState(var1, Blocks.air.getDefaultState(), 3);
   }

   public boolean setBlockState(BlockPos var1, IBlockState var2) {
      return this.setBlockState(var1, var2, 3);
   }

   public void markBlockForUpdate(BlockPos var1) {
      for (int var5 = 0; var5 < this.worldAccesses.size(); var5++) {
         this.worldAccesses.get(var5).markBlockForUpdate(var1);
      }
   }

   public void notifyNeighborsRespectDebug(BlockPos var1, Block var2) {
      if (this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
         this.notifyNeighborsOfStateChange(var1, var2);
      }
   }

   public void markBlocksDirtyVertical(int var1, int var2, int var3, int var4) {
      if (var3 > var4) {
         int var8 = var4;
         var4 = var3;
         var3 = var8;
      }

      if (!this.provider.getHasNoSky()) {
         for (int var9 = var3; var9 <= var4; var9++) {
            this.checkLightFor(EnumSkyBlock.SKY, new BlockPos(var1, var9, var2));
         }
      }

      this.markBlockRangeForRenderUpdate(var1, var3, var2, var1, var4, var2);
   }

   public void markBlockRangeForRenderUpdate(BlockPos var1, BlockPos var2) {
      this.markBlockRangeForRenderUpdate(var1.getX(), var1.getY(), var1.getZ(), var2.getX(), var2.getY(), var2.getZ());
   }

   public void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
      for (int var10 = 0; var10 < this.worldAccesses.size(); var10++) {
         this.worldAccesses.get(var10).markBlockRangeForRenderUpdate(var1, var2, var3, var4, var5, var6);
      }
   }

   public void notifyNeighborsOfStateChange(BlockPos var1, Block var2) {
      this.notifyBlockOfStateChange(var1.west(), var2);
      this.notifyBlockOfStateChange(var1.east(), var2);
      this.notifyBlockOfStateChange(var1.down(), var2);
      this.notifyBlockOfStateChange(var1.up(), var2);
      this.notifyBlockOfStateChange(var1.north(), var2);
      this.notifyBlockOfStateChange(var1.south(), var2);
   }

   public void notifyNeighborsOfStateExcept(BlockPos var1, Block var2, EnumFacing var3) {
      if (var3 != EnumFacing.WEST) {
         this.notifyBlockOfStateChange(var1.west(), var2);
      }

      if (var3 != EnumFacing.EAST) {
         this.notifyBlockOfStateChange(var1.east(), var2);
      }

      if (var3 != EnumFacing.DOWN) {
         this.notifyBlockOfStateChange(var1.down(), var2);
      }

      if (var3 != EnumFacing.UP) {
         this.notifyBlockOfStateChange(var1.up(), var2);
      }

      if (var3 != EnumFacing.NORTH) {
         this.notifyBlockOfStateChange(var1.north(), var2);
      }

      if (var3 != EnumFacing.SOUTH) {
         this.notifyBlockOfStateChange(var1.south(), var2);
      }
   }

   public void notifyBlockOfStateChange(BlockPos var1, final Block var2) {
      if (!this.isRemote) {
         IBlockState var6 = this.getBlockState(var1);

         try {
            var6.getBlock().onNeighborBlockChange(this, var1, var6, var2);
         } catch (Throwable var10) {
            CrashReport var8 = CrashReport.makeCrashReport(var10, "Exception while updating neighbours");
            CrashReportCategory var9 = var8.makeCategory("Block being updated");
            var9.addCrashSectionCallable("Source block type", new Callable<String>() {
               private static final long a = 49531960915489L;

               public String call() throws Exception {
                  try {
                     return String.format("ID #%d (%s // %s)", Block.getIdFromBlock(var2), var2.getUnlocalizedName(), var2.getClass().getCanonicalName());
                  } catch (Throwable var4) {
                     return "ID #" + Block.getIdFromBlock(var2);
                  }
               }
            });
            CrashReportCategory.addBlockInfo(var9, var1, var6);
            throw new ReportedException(var8);
         }
      }
   }

   public boolean isBlockTickPending(BlockPos var1, Block var2) {
      return false;
   }

   public boolean canSeeSky(BlockPos var1) {
      return this.getChunkFromBlockCoords(var1).canSeeSky(var1);
   }

   public boolean canBlockSeeSky(BlockPos var1) {
      if (var1.getY() >= this.getSeaLevel()) {
         return this.canSeeSky(var1);
      }

      BlockPos var5 = new BlockPos(var1.getX(), this.getSeaLevel(), var1.getZ());
      if (!this.canSeeSky(var5)) {
         return false;
      }

      for (BlockPos var7 = var5.down(); var7.getY() > var1.getY(); var7 = var7.down()) {
         Block var6 = this.getBlockState(var7).getBlock();
         if (var6.getLightOpacity() > 0 && !var6.getMaterial().isLiquid()) {
            return false;
         }
      }

      return true;
   }

   public int getLight(BlockPos var1) {
      if (var1.getY() < 0) {
         return 0;
      }

      if (var1.getY() >= 256) {
         var1 = new BlockPos(var1.getX(), 255, var1.getZ());
      }

      return this.getChunkFromBlockCoords(var1).getLightSubtracted(var1, 0);
   }

   public int getLightFromNeighbors(BlockPos var1) {
      return this.getLight(var1, true);
   }

   public int getLight(BlockPos var1, boolean var2) {
      if (var1.getX() < -30000000 || var1.getZ() < -30000000 || var1.getX() >= 30000000 || var1.getZ() >= 30000000) {
         return 15;
      }

      if (var2 && this.getBlockState(var1).getBlock().getUseNeighborBrightness()) {
         int var11 = this.getLight(var1.up(), false);
         int var7 = this.getLight(var1.east(), false);
         int var8 = this.getLight(var1.west(), false);
         int var9 = this.getLight(var1.south(), false);
         int var10 = this.getLight(var1.north(), false);
         if (var7 > var11) {
            var11 = var7;
         }

         if (var8 > var11) {
            var11 = var8;
         }

         if (var9 > var11) {
            var11 = var9;
         }

         if (var10 > var11) {
            var11 = var10;
         }

         return var11;
      } else {
         if (var1.getY() < 0) {
            return 0;
         }

         if (var1.getY() >= 256) {
            var1 = new BlockPos(var1.getX(), 255, var1.getZ());
         }

         Chunk var6 = this.getChunkFromBlockCoords(var1);
         return var6.getLightSubtracted(var1, this.skylightSubtracted);
      }
   }

   public BlockPos getHeight(BlockPos var1) {
      int var5;
      if (var1.getX() >= -30000000 && var1.getZ() >= -30000000 && var1.getX() < 30000000 && var1.getZ() < 30000000) {
         if (this.isChunkLoaded(var1.getX() >> 4, var1.getZ() >> 4, true)) {
            var5 = this.getChunkFromChunkCoords(var1.getX() >> 4, var1.getZ() >> 4).getHeightValue(var1.getX() & 15, var1.getZ() & 15);
         } else {
            var5 = 0;
         }
      } else {
         var5 = this.getSeaLevel() + 1;
      }

      return new BlockPos(var1.getX(), var5, var1.getZ());
   }

   public int getChunksLowestHorizon(int var1, int var2) {
      if (var1 >= -30000000 && var2 >= -30000000 && var1 < 30000000 && var2 < 30000000) {
         if (!this.isChunkLoaded(var1 >> 4, var2 >> 4, true)) {
            return 0;
         }

         Chunk var6 = this.getChunkFromChunkCoords(var1 >> 4, var2 >> 4);
         return var6.getLowestHeight();
      } else {
         return this.getSeaLevel() + 1;
      }
   }

   public int getLightFromNeighborsFor(EnumSkyBlock var1, BlockPos var2) {
      if (this.provider.getHasNoSky() && var1 == EnumSkyBlock.SKY) {
         return 0;
      }

      if (var2.getY() < 0) {
         var2 = new BlockPos(var2.getX(), 0, var2.getZ());
      }

      if (!this.isValid(var2)) {
         return var1.defaultLightValue;
      }

      if (!this.isBlockLoaded(var2)) {
         return var1.defaultLightValue;
      }

      if (this.getBlockState(var2).getBlock().getUseNeighborBrightness()) {
         int var11 = this.getLightFor(var1, var2.up());
         int var7 = this.getLightFor(var1, var2.east());
         int var8 = this.getLightFor(var1, var2.west());
         int var9 = this.getLightFor(var1, var2.south());
         int var10 = this.getLightFor(var1, var2.north());
         if (var7 > var11) {
            var11 = var7;
         }

         if (var8 > var11) {
            var11 = var8;
         }

         if (var9 > var11) {
            var11 = var9;
         }

         if (var10 > var11) {
            var11 = var10;
         }

         return var11;
      } else {
         Chunk var6 = this.getChunkFromBlockCoords(var2);
         return var6.getLightFor(var1, var2);
      }
   }

   public int getLightFor(EnumSkyBlock var1, BlockPos var2) {
      if (var2.getY() < 0) {
         var2 = new BlockPos(var2.getX(), 0, var2.getZ());
      }

      if (!this.isValid(var2)) {
         return var1.defaultLightValue;
      }

      if (!this.isBlockLoaded(var2)) {
         return var1.defaultLightValue;
      }

      Chunk var6 = this.getChunkFromBlockCoords(var2);
      return var6.getLightFor(var1, var2);
   }

   public void setLightFor(EnumSkyBlock var1, BlockPos var2, int var3) {
      if (this.isValid(var2) && this.isBlockLoaded(var2)) {
         Chunk var7 = this.getChunkFromBlockCoords(var2);
         var7.setLightFor(var1, var2, var3);
         this.notifyLightSet(var2);
      }
   }

   public void notifyLightSet(BlockPos var1) {
      for (int var5 = 0; var5 < this.worldAccesses.size(); var5++) {
         this.worldAccesses.get(var5).notifyLightSet(var1);
      }
   }

   @Override
   public int getCombinedLight(BlockPos var1, int var2) {
      int var6 = this.getLightFromNeighborsFor(EnumSkyBlock.SKY, var1);
      int var7 = this.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, var1);
      if (var7 < var2) {
         var7 = var2;
      }

      return var6 << 20 | var7 << 4;
   }

   public float getLightBrightness(BlockPos var1) {
      return this.provider.getLightBrightnessTable()[this.getLightFromNeighbors(var1)];
   }

   @Override
   public IBlockState getBlockState(BlockPos var1) {
      if (!this.isValid(var1)) {
         return Blocks.air.getDefaultState();
      }

      Chunk var5 = this.getChunkFromBlockCoords(var1);
      return var5.getBlockState(var1);
   }

   public boolean isDaytime() {
      return this.skylightSubtracted < 4;
   }

   public MovingObjectPosition rayTraceBlocks(Vec3 var1, Vec3 var2) {
      return this.rayTraceBlocks(var1, var2, false, false, false);
   }

   public MovingObjectPosition rayTraceBlocks(Vec3 var1, Vec3 var2, boolean var3) {
      return this.rayTraceBlocks(var1, var2, var3, false, false);
   }

   public MovingObjectPosition rayTraceBlocks(Vec3 var1, Vec3 var2, boolean var3, boolean var4, boolean var5) {
      if (Double.isNaN(var1.xCoord) || Double.isNaN(var1.yCoord) || Double.isNaN(var1.zCoord)) {
         return null;
      }

      if (!Double.isNaN(var2.xCoord) && !Double.isNaN(var2.yCoord) && !Double.isNaN(var2.zCoord)) {
         int var9 = MathHelper.floor_double(var2.xCoord);
         int var10 = MathHelper.floor_double(var2.yCoord);
         int var11 = MathHelper.floor_double(var2.zCoord);
         int var12 = MathHelper.floor_double(var1.xCoord);
         int var13 = MathHelper.floor_double(var1.yCoord);
         int var14 = MathHelper.floor_double(var1.zCoord);
         BlockPos var15 = new BlockPos(var12, var13, var14);
         IBlockState var16 = this.getBlockState(var15);
         Block var17 = var16.getBlock();
         if ((!var4 || var17.getCollisionBoundingBox(this, var15, var16) != null) && var17.canCollideCheck(var16, var3)) {
            MovingObjectPosition var18 = var17.collisionRayTrace(this, var15, var1, var2);
            if (var18 != null && !ModuleManager.Q.isEnabled()) {
               return var18;
            }
         }

         MovingObjectPosition var46 = null;
         int var19 = 200;

         while (var19-- >= 0) {
            if (Double.isNaN(var1.xCoord) || Double.isNaN(var1.yCoord) || Double.isNaN(var1.zCoord)) {
               return null;
            }

            if (var12 == var9 && var13 == var10 && var14 == var11) {
               return var5 ? var46 : null;
            }

            boolean var20 = true;
            boolean var21 = true;
            boolean var22 = true;
            double var23 = 999.0;
            double var25 = 999.0;
            double var27 = 999.0;
            if (var9 > var12) {
               var23 = var12 + 1.0;
            } else if (var9 < var12) {
               var23 = var12 + 0.0;
            } else {
               var20 = false;
            }

            if (var10 > var13) {
               var25 = var13 + 1.0;
            } else if (var10 < var13) {
               var25 = var13 + 0.0;
            } else {
               var21 = false;
            }

            if (var11 > var14) {
               var27 = var14 + 1.0;
            } else if (var11 < var14) {
               var27 = var14 + 0.0;
            } else {
               var22 = false;
            }

            double var29 = 999.0;
            double var31 = 999.0;
            double var33 = 999.0;
            double var35 = var2.xCoord - var1.xCoord;
            double var37 = var2.yCoord - var1.yCoord;
            double var39 = var2.zCoord - var1.zCoord;
            if (var20) {
               var29 = (var23 - var1.xCoord) / var35;
            }

            if (var21) {
               var31 = (var25 - var1.yCoord) / var37;
            }

            if (var22) {
               var33 = (var27 - var1.zCoord) / var39;
            }

            if (var29 == -0.0) {
               var29 = -1.0E-4;
            }

            if (var31 == -0.0) {
               var31 = -1.0E-4;
            }

            if (var33 == -0.0) {
               var33 = -1.0E-4;
            }

            EnumFacing var41;
            if (var29 < var31 && var29 < var33) {
               var41 = var9 > var12 ? EnumFacing.WEST : EnumFacing.EAST;
               var1 = new Vec3(var23, var1.yCoord + var37 * var29, var1.zCoord + var39 * var29);
            } else if (var31 < var33) {
               var41 = var10 > var13 ? EnumFacing.DOWN : EnumFacing.UP;
               var1 = new Vec3(var1.xCoord + var35 * var31, var25, var1.zCoord + var39 * var31);
            } else {
               var41 = var11 > var14 ? EnumFacing.NORTH : EnumFacing.SOUTH;
               var1 = new Vec3(var1.xCoord + var35 * var33, var1.yCoord + var37 * var33, var27);
            }

            var12 = MathHelper.floor_double(var1.xCoord) - (var41 == EnumFacing.EAST ? 1 : 0);
            var13 = MathHelper.floor_double(var1.yCoord) - (var41 == EnumFacing.UP ? 1 : 0);
            var14 = MathHelper.floor_double(var1.zCoord) - (var41 == EnumFacing.SOUTH ? 1 : 0);
            var15 = new BlockPos(var12, var13, var14);
            IBlockState var42 = this.getBlockState(var15);
            Block var43 = var42.getBlock();
            if (!var4 || var43.getCollisionBoundingBox(this, var15, var42) != null) {
               if (var43.canCollideCheck(var42, var3)) {
                  MovingObjectPosition var44 = var43.collisionRayTrace(this, var15, var1, var2);
                  if (var44 != null) {
                     return var44;
                  }
               } else {
                  var46 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, var1, var41, var15);
               }
            }
         }

         return var5 ? var46 : null;
      } else {
         return null;
      }
   }

   public void playSoundAtEntity(Entity var1, String var2, float var3, float var4) {
      for (int var8 = 0; var8 < this.worldAccesses.size(); var8++) {
         this.worldAccesses.get(var8).playSound(var2, var1.posX, var1.posY, var1.posZ, var3, var4);
      }
   }

   public void playSoundToNearExcept(EntityPlayer var1, String var2, float var3, float var4) {
      for (int var8 = 0; var8 < this.worldAccesses.size(); var8++) {
         this.worldAccesses.get(var8).playSoundToNearExcept(var1, var2, var1.posX, var1.posY, var1.posZ, var3, var4);
      }
   }

   public void playSoundEffect(double var1, double var3, double var5, String var7, float var8, float var9) {
      for (int var13 = 0; var13 < this.worldAccesses.size(); var13++) {
         this.worldAccesses.get(var13).playSound(var7, var1, var3, var5, var8, var9);
      }
   }

   public void playSound(double var1, double var3, double var5, String var7, float var8, float var9, boolean var10) {
   }

   public void playRecord(BlockPos var1, String var2) {
      for (int var6 = 0; var6 < this.worldAccesses.size(); var6++) {
         this.worldAccesses.get(var6).playRecord(var2, var1);
      }
   }

   public void spawnParticle(EnumParticleTypes var1, double var2, double var4, double var6, double var8, double var10, double var12, int... var14) {
      this.spawnParticle(var1.getParticleID(), var1.getShouldIgnoreRange(), var2, var4, var6, var8, var10, var12, var14);
   }

   public void spawnParticle(EnumParticleTypes var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
      this.spawnParticle(var1.getParticleID(), var1.getShouldIgnoreRange() | var2, var3, var5, var7, var9, var11, var13, var15);
   }

   private void spawnParticle(int var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
      for (int var19 = 0; var19 < this.worldAccesses.size(); var19++) {
         this.worldAccesses.get(var19).spawnParticle(var1, var2, var3, var5, var7, var9, var11, var13, var15);
      }
   }

   public boolean addWeatherEffect(Entity var1) {
      this.weatherEffects.add(var1);
      return true;
   }

   public boolean spawnEntityInWorld(Entity var1) {
      int var5 = MathHelper.floor_double(var1.posX / 16.0);
      int var6 = MathHelper.floor_double(var1.posZ / 16.0);
      boolean var7 = var1.forceSpawn;
      if (var1 instanceof EntityPlayer) {
         var7 = true;
      }

      if (!var7 && !this.isChunkLoaded(var5, var6, true)) {
         return false;
      }

      if (var1 instanceof EntityPlayer) {
         EntityPlayer var8 = (EntityPlayer)var1;
         this.playerEntities.add(var8);
         this.updateAllPlayersSleepingFlag();
      }

      this.getChunkFromChunkCoords(var5, var6).addEntity(var1);
      this.loadedEntityList.add(var1);
      this.onEntityAdded(var1);
      return true;
   }

   public void onEntityAdded(Entity var1) {
      for (int var5 = 0; var5 < this.worldAccesses.size(); var5++) {
         this.worldAccesses.get(var5).onEntityAdded(var1);
      }
   }

   public void onEntityRemoved(Entity var1) {
      for (int var5 = 0; var5 < this.worldAccesses.size(); var5++) {
         this.worldAccesses.get(var5).onEntityRemoved(var1);
      }
   }

   public void removeEntity(Entity var1) {
      if (var1.riddenByEntity != null) {
         var1.riddenByEntity.mountEntity((Entity)null);
      }

      if (var1.ridingEntity != null) {
         var1.mountEntity((Entity)null);
      }

      var1.setDead();
      if (var1 instanceof EntityPlayer) {
         this.playerEntities.remove(var1);
         this.updateAllPlayersSleepingFlag();
         this.onEntityRemoved(var1);
      }
   }

   public void removePlayerEntityDangerously(Entity var1) {
      var1.setDead();
      if (var1 instanceof EntityPlayer) {
         this.playerEntities.remove(var1);
         this.updateAllPlayersSleepingFlag();
      }

      int var5 = var1.chunkCoordX;
      int var6 = var1.chunkCoordZ;
      if (var1.addedToChunk && this.isChunkLoaded(var5, var6, true)) {
         this.getChunkFromChunkCoords(var5, var6).removeEntity(var1);
      }

      this.loadedEntityList.remove(var1);
      this.onEntityRemoved(var1);
   }

   public void addWorldAccess(IWorldAccess var1) {
      this.worldAccesses.add(var1);
   }

   public void removeWorldAccess(IWorldAccess var1) {
      this.worldAccesses.remove(var1);
   }

   public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity var1, AxisAlignedBB var2) {
      ArrayList var6 = Lists.newArrayList();
      int var7 = MathHelper.floor_double(var2.minX);
      int var8 = MathHelper.floor_double(var2.maxX + 1.0);
      int var9 = MathHelper.floor_double(var2.minY);
      int var10 = MathHelper.floor_double(var2.maxY + 1.0);
      int var11 = MathHelper.floor_double(var2.minZ);
      int var12 = MathHelper.floor_double(var2.maxZ + 1.0);
      WorldBorder var13 = this.getWorldBorder();
      boolean var14 = var1.isOutsideBorder();
      boolean var15 = this.isInsideBorder(var13, var1);
      IBlockState var16 = Blocks.stone.getDefaultState();
      BlockPos.MutableBlockPos var17 = new BlockPos.MutableBlockPos();

      for (int var18 = var7; var18 < var8; var18++) {
         for (int var19 = var11; var19 < var12; var19++) {
            if (this.isBlockLoaded(var17.set(var18, 64, var19))) {
               for (int var20 = var9 - 1; var20 < var10; var20++) {
                  var17.set(var18, var20, var19);
                  if (var14 && var15) {
                     var1.setOutsideBorder(false);
                  } else if (!var14 && !var15) {
                     var1.setOutsideBorder(true);
                  }

                  IBlockState var21 = var16;
                  if (var13.contains(var17) || !var15) {
                     var21 = this.getBlockState(var17);
                  }

                  var21.getBlock().addCollisionBoxesToList(this, var17, var21, var2, var6, var1);
               }
            }
         }
      }

      double var23 = 0.25;
      List var24 = this.getEntitiesWithinAABBExcludingEntity(var1, var2.expand(var23, var23, var23));

      for (int var25 = 0; var25 < var24.size(); var25++) {
         if (var1.riddenByEntity != var24 && var1.ridingEntity != var24) {
            AxisAlignedBB var22 = ((Entity)var24.get(var25)).getCollisionBoundingBox();
            if (var22 != null && var22.intersectsWith(var2)) {
               var6.add(var22);
            }

            var22 = var1.getCollisionBox((Entity)var24.get(var25));
            if (var22 != null && var22.intersectsWith(var2)) {
               var6.add(var22);
            }
         }
      }

      return var6;
   }

   public boolean isInsideBorder(WorldBorder var1, Entity var2) {
      double var6 = var1.minX();
      double var8 = var1.minZ();
      double var10 = var1.maxX();
      double var12 = var1.maxZ();
      if (var2.isOutsideBorder()) {
         var6++;
         var8++;
         var10--;
         var12--;
      } else {
         var6--;
         var8--;
         var10++;
         var12++;
      }

      return var2.posX > var6 && var2.posX < var10 && var2.posZ > var8 && var2.posZ < var12;
   }

   public List<AxisAlignedBB> getCollisionBoxes(AxisAlignedBB var1) {
      ArrayList var5 = Lists.newArrayList();
      int var6 = MathHelper.floor_double(var1.minX);
      int var7 = MathHelper.floor_double(var1.maxX + 1.0);
      int var8 = MathHelper.floor_double(var1.minY);
      int var9 = MathHelper.floor_double(var1.maxY + 1.0);
      int var10 = MathHelper.floor_double(var1.minZ);
      int var11 = MathHelper.floor_double(var1.maxZ + 1.0);
      BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

      for (int var13 = var6; var13 < var7; var13++) {
         for (int var14 = var10; var14 < var11; var14++) {
            if (this.isBlockLoaded(var12.set(var13, 64, var14))) {
               for (int var15 = var8 - 1; var15 < var9; var15++) {
                  var12.set(var13, var15, var14);
                  IBlockState var16;
                  if (var13 >= -30000000 && var13 < 30000000 && var14 >= -30000000 && var14 < 30000000) {
                     var16 = this.getBlockState(var12);
                  } else {
                     var16 = Blocks.bedrock.getDefaultState();
                  }

                  var16.getBlock().addCollisionBoxesToList(this, var12, var16, var1, var5, (Entity)null);
               }
            }
         }
      }

      return var5;
   }

   public int calculateSkylightSubtracted(float var1) {
      float var2 = this.getCelestialAngle(var1);
      float var3 = 1.0F - (MathHelper.cos(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
      var3 = MathHelper.clamp_float(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)(var3 * (1.0 - this.getRainStrength(var1) * 5.0F / 16.0));
      var3 = (float)(var3 * (1.0 - this.getThunderStrength(var1) * 5.0F / 16.0));
      var3 = 1.0F - var3;
      return (int)(var3 * 11.0F);
   }

   public float getSunBrightness(float var1) {
      float var2 = this.getCelestialAngle(var1);
      float var3 = 1.0F - (MathHelper.cos(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.2F);
      var3 = MathHelper.clamp_float(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)(var3 * (1.0 - this.getRainStrength(var1) * 5.0F / 16.0));
      var3 = (float)(var3 * (1.0 - this.getThunderStrength(var1) * 5.0F / 16.0));
      return var3 * 0.8F + 0.2F;
   }

   public Vec3 getSkyColor(Entity var1, float var2) {
      float var7 = this.getCelestialAngle(var2);
      float var8 = MathHelper.cos(var7 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      var8 = MathHelper.clamp_float(var8, 0.0F, 1.0F);
      int var9 = MathHelper.floor_double(var1.posX);
      int var10 = MathHelper.floor_double(var1.posY);
      int var11 = MathHelper.floor_double(var1.posZ);
      BlockPos var12 = new BlockPos(var9, var10, var11);
      BiomeGenBase var13 = this.getBiomeGenForCoords(var12);
      float var14 = var13.getFloatTemperature(var12);
      int var15 = var13.getSkyColorByTemp(var14);
      float var16 = (var15 >> 16 & 0xFF) / 255.0F;
      float var17 = (var15 >> 8 & 0xFF) / 255.0F;
      float var18 = (var15 & 0xFF) / 255.0F;
      var16 *= var8;
      var17 *= var8;
      var18 *= var8;
      float var19 = this.getRainStrength(var2);
      if (var19 > 0.0F) {
         float var20 = (var16 * 0.3F + var17 * 0.59F + var18 * 0.11F) * 0.6F;
         float var21 = 1.0F - var19 * 0.75F;
         var16 = var16 * var21 + var20 * (1.0F - var21);
         var17 = var17 * var21 + var20 * (1.0F - var21);
         var18 = var18 * var21 + var20 * (1.0F - var21);
      }

      float var27 = this.getThunderStrength(var2);
      if (var27 > 0.0F) {
         float var28 = (var16 * 0.3F + var17 * 0.59F + var18 * 0.11F) * 0.2F;
         float var22 = 1.0F - var27 * 0.75F;
         var16 = var16 * var22 + var28 * (1.0F - var22);
         var17 = var17 * var22 + var28 * (1.0F - var22);
         var18 = var18 * var22 + var28 * (1.0F - var22);
      }

      if (this.lastLightningBolt > 0) {
         float var29 = this.lastLightningBolt - var2;
         if (var29 > 1.0F) {
            var29 = 1.0F;
         }

         var29 *= 0.45F;
         var16 = var16 * (1.0F - var29) + 0.8F * var29;
         var17 = var17 * (1.0F - var29) + 0.8F * var29;
         var18 = var18 * (1.0F - var29) + 1.0F * var29;
      }

      return new Vec3(var16, var17, var18);
   }

   public float getCelestialAngle(float var1) {
      return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), var1);
   }

   public int f() {
      return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
   }

   public float getCurrentMoonPhaseFactor() {
      return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
   }

   public float getCelestialAngleRadians(float var1) {
      float var2 = this.getCelestialAngle(var1);
      return var2 * (float) Math.PI * 2.0F;
   }

   public Vec3 getCloudColour(float var1) {
      float var5 = this.getCelestialAngle(var1);
      float var6 = MathHelper.cos(var5 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      var6 = MathHelper.clamp_float(var6, 0.0F, 1.0F);
      float var7 = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
      float var8 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
      float var9 = (float)(this.cloudColour & 255L) / 255.0F;
      float var10 = this.getRainStrength(var1);
      if (var10 > 0.0F) {
         float var11 = (var7 * 0.3F + var8 * 0.59F + var9 * 0.11F) * 0.6F;
         float var12 = 1.0F - var10 * 0.95F;
         var7 = var7 * var12 + var11 * (1.0F - var12);
         var8 = var8 * var12 + var11 * (1.0F - var12);
         var9 = var9 * var12 + var11 * (1.0F - var12);
      }

      var7 *= var6 * 0.9F + 0.1F;
      var8 *= var6 * 0.9F + 0.1F;
      var9 *= var6 * 0.85F + 0.15F;
      float var18 = this.getThunderStrength(var1);
      if (var18 > 0.0F) {
         float var19 = (var7 * 0.3F + var8 * 0.59F + var9 * 0.11F) * 0.2F;
         float var13 = 1.0F - var18 * 0.95F;
         var7 = var7 * var13 + var19 * (1.0F - var13);
         var8 = var8 * var13 + var19 * (1.0F - var13);
         var9 = var9 * var13 + var19 * (1.0F - var13);
      }

      return new Vec3(var7, var8, var9);
   }

   public Vec3 getFogColor(float var1) {
      float var2 = this.getCelestialAngle(var1);
      return this.provider.getFogColor(var2, var1);
   }

   public BlockPos getPrecipitationHeight(BlockPos var1) {
      return this.getChunkFromBlockCoords(var1).getPrecipitationHeight(var1);
   }

   public BlockPos getTopSolidOrLiquidBlock(BlockPos var1) {
      Chunk var5 = this.getChunkFromBlockCoords(var1);
      BlockPos var6 = new BlockPos(var1.getX(), var5.getTopFilledSegment() + 16, var1.getZ());

      while (var6.getY() >= 0) {
         BlockPos var7 = var6.down();
         Material var8 = var5.getBlock(var7).getMaterial();
         if (var8.blocksMovement() && var8 != Material.leaves) {
            break;
         }

         var6 = var7;
      }

      return var6;
   }

   public float getStarBrightness(float var1) {
      float var2 = this.getCelestialAngle(var1);
      float var3 = 1.0F - (MathHelper.cos(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
      var3 = MathHelper.clamp_float(var3, 0.0F, 1.0F);
      return var3 * var3 * 0.5F;
   }

   public void scheduleUpdate(BlockPos var1, Block var2, int var3) {
   }

   public void updateBlockTick(BlockPos var1, Block var2, int var3, int var4) {
   }

   public void scheduleBlockUpdate(BlockPos var1, Block var2, int var3, int var4) {
   }

   public void updateEntities() {
      this.theProfiler.startSection("entities");
      this.theProfiler.startSection("global");

      for (int var4 = 0; var4 < this.weatherEffects.size(); var4++) {
         Entity var5 = this.weatherEffects.get(var4);

         try {
            var5.ticksExisted++;
            var5.onUpdate();
         } catch (Throwable var12) {
            CrashReport var7 = CrashReport.makeCrashReport(var12, "Ticking entity");
            CrashReportCategory var8 = var7.makeCategory("Entity being ticked");
            if (var5 == null) {
               var8.addCrashSection("Entity", "~~NULL~~");
            } else {
               var5.addEntityCrashInfo(var8);
            }

            throw new ReportedException(var7);
         }

         if (var5.isDead) {
            this.weatherEffects.remove(var4--);
         }
      }

      this.theProfiler.endStartSection("remove");
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for (int var13 = 0; var13 < this.unloadedEntityList.size(); var13++) {
         Entity var17 = this.unloadedEntityList.get(var13);
         int var6 = var17.chunkCoordX;
         int var24 = var17.chunkCoordZ;
         if (var17.addedToChunk && this.isChunkLoaded(var6, var24, true)) {
            this.getChunkFromChunkCoords(var6, var24).removeEntity(var17);
         }
      }

      for (int var14 = 0; var14 < this.unloadedEntityList.size(); var14++) {
         this.onEntityRemoved(this.unloadedEntityList.get(var14));
      }

      this.unloadedEntityList.clear();
      this.theProfiler.endStartSection("regular");

      for (int var15 = 0; var15 < this.loadedEntityList.size(); var15++) {
         Entity var18 = this.loadedEntityList.get(var15);
         if (var18.ridingEntity != null) {
            if (!var18.ridingEntity.isDead && var18.ridingEntity.riddenByEntity == var18) {
               continue;
            }

            var18.ridingEntity.riddenByEntity = null;
            var18.ridingEntity = null;
         }

         this.theProfiler.startSection("tick");
         if (!var18.isDead) {
            try {
               this.updateEntity(var18);
            } catch (Throwable var11) {
               CrashReport var25 = CrashReport.makeCrashReport(var11, "Ticking entity");
               CrashReportCategory var27 = var25.makeCategory("Entity being ticked");
               var18.addEntityCrashInfo(var27);
               throw new ReportedException(var25);
            }
         }

         this.theProfiler.endSection();
         this.theProfiler.startSection("remove");
         if (var18.isDead) {
            int var21 = var18.chunkCoordX;
            int var26 = var18.chunkCoordZ;
            if (var18.addedToChunk && this.isChunkLoaded(var21, var26, true)) {
               this.getChunkFromChunkCoords(var21, var26).removeEntity(var18);
            }

            this.loadedEntityList.remove(var15--);
            this.onEntityRemoved(var18);
         }

         this.theProfiler.endSection();
      }

      this.theProfiler.endStartSection("blockEntities");
      this.processingLoadedTiles = true;
      Iterator var16 = this.tickableTileEntities.iterator();

      while (var16.hasNext()) {
         TileEntity var19 = (TileEntity)var16.next();
         if (!var19.isInvalid() && var19.hasWorldObj()) {
            BlockPos var22 = var19.getPos();
            if (this.isBlockLoaded(var22) && this.worldBorder.contains(var22)) {
               try {
                  ((ITickable)var19).update();
               } catch (Throwable var10) {
                  CrashReport var28 = CrashReport.makeCrashReport(var10, "Ticking block entity");
                  CrashReportCategory var9 = var28.makeCategory("Block entity being ticked");
                  var19.addInfoToCrashReport(var9);
                  throw new ReportedException(var28);
               }
            }
         }

         if (var19.isInvalid()) {
            var16.remove();
            this.loadedTileEntityList.remove(var19);
            if (this.isBlockLoaded(var19.getPos())) {
               this.getChunkFromBlockCoords(var19.getPos()).removeTileEntity(var19.getPos());
            }
         }
      }

      this.processingLoadedTiles = false;
      if (!this.tileEntitiesToBeRemoved.isEmpty()) {
         this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
         this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
         this.tileEntitiesToBeRemoved.clear();
      }

      this.theProfiler.endStartSection("pendingBlockEntities");
      if (!this.addedTileEntityList.isEmpty()) {
         for (int var20 = 0; var20 < this.addedTileEntityList.size(); var20++) {
            TileEntity var23 = this.addedTileEntityList.get(var20);
            if (!var23.isInvalid()) {
               if (!this.loadedTileEntityList.contains(var23)) {
                  this.addTileEntity(var23);
               }

               if (this.isBlockLoaded(var23.getPos())) {
                  this.getChunkFromBlockCoords(var23.getPos()).addTileEntity(var23.getPos(), var23);
               }

               this.markBlockForUpdate(var23.getPos());
            }
         }

         this.addedTileEntityList.clear();
      }

      this.theProfiler.endSection();
      this.theProfiler.endSection();
   }

   public boolean addTileEntity(TileEntity var1) {
      boolean var5 = this.loadedTileEntityList.add(var1);
      if (var5 && var1 instanceof ITickable) {
         this.tickableTileEntities.add(var1);
      }

      return var5;
   }

   public void addTileEntities(Collection<TileEntity> var1) {
      if (this.processingLoadedTiles) {
         this.addedTileEntityList.addAll(var1);
      } else {
         for (TileEntity var6 : var1) {
            this.loadedTileEntityList.add(var6);
            if (var6 instanceof ITickable) {
               this.tickableTileEntities.add(var6);
            }
         }
      }
   }

   public void updateEntity(Entity var1) {
      this.updateEntityWithOptionalForce(var1, true);
   }

   public void updateEntityWithOptionalForce(Entity var1, boolean var2) {
      int var6 = MathHelper.floor_double(var1.posX);
      int var7 = MathHelper.floor_double(var1.posZ);
      byte var8 = 32;
      if (!var2 || this.isAreaLoaded(var6 - var8, 0, var7 - var8, var6 + var8, 0, var7 + var8, true)) {
         var1.lastTickPosX = var1.posX;
         var1.lastTickPosY = var1.posY;
         var1.lastTickPosZ = var1.posZ;
         var1.prevRotationYaw = var1.rotationYaw;
         var1.prevRotationPitch = var1.rotationPitch;
         if (var2 && var1.addedToChunk) {
            var1.ticksExisted++;
            if (var1.ridingEntity != null) {
               var1.updateRidden();
            } else {
               var1.onUpdate();
            }
         }

         this.theProfiler.startSection("chunkCheck");
         if (Double.isNaN(var1.posX) || Double.isInfinite(var1.posX)) {
            var1.posX = var1.lastTickPosX;
         }

         if (Double.isNaN(var1.posY) || Double.isInfinite(var1.posY)) {
            var1.posY = var1.lastTickPosY;
         }

         if (Double.isNaN(var1.posZ) || Double.isInfinite(var1.posZ)) {
            var1.posZ = var1.lastTickPosZ;
         }

         if (Double.isNaN(var1.rotationPitch) || Double.isInfinite(var1.rotationPitch)) {
            var1.rotationPitch = var1.prevRotationPitch;
         }

         if (Double.isNaN(var1.rotationYaw) || Double.isInfinite(var1.rotationYaw)) {
            var1.rotationYaw = var1.prevRotationYaw;
         }

         int var9 = MathHelper.floor_double(var1.posX / 16.0);
         int var10 = MathHelper.floor_double(var1.posY / 16.0);
         int var11 = MathHelper.floor_double(var1.posZ / 16.0);
         if (!var1.addedToChunk || var1.chunkCoordX != var9 || var1.chunkCoordY != var10 || var1.chunkCoordZ != var11) {
            if (var1.addedToChunk && this.isChunkLoaded(var1.chunkCoordX, var1.chunkCoordZ, true)) {
               this.getChunkFromChunkCoords(var1.chunkCoordX, var1.chunkCoordZ).removeEntityAtIndex(var1, var1.chunkCoordY);
            }

            if (this.isChunkLoaded(var9, var11, true)) {
               var1.addedToChunk = true;
               this.getChunkFromChunkCoords(var9, var11).addEntity(var1);
            } else {
               var1.addedToChunk = false;
            }
         }

         this.theProfiler.endSection();
         if (var2 && var1.addedToChunk && var1.riddenByEntity != null) {
            if (!var1.riddenByEntity.isDead && var1.riddenByEntity.ridingEntity == var1) {
               this.updateEntity(var1.riddenByEntity);
            } else {
               var1.riddenByEntity.ridingEntity = null;
               var1.riddenByEntity = null;
            }
         }
      }
   }

   public boolean checkNoEntityCollision(AxisAlignedBB var1) {
      return this.checkNoEntityCollision(var1, (Entity)null);
   }

   public boolean checkNoEntityCollision(AxisAlignedBB var1, Entity var2) {
      List var6 = this.getEntitiesWithinAABBExcludingEntity((Entity)null, var1);

      for (int var7 = 0; var7 < var6.size(); var7++) {
         Entity var8 = (Entity)var6.get(var7);
         if (!var8.isDead && var8.preventEntitySpawning && var8 != var2 && (var2 == null || var2.ridingEntity != var8 && var2.riddenByEntity != var8)) {
            return false;
         }
      }

      return true;
   }

   public boolean X(AxisAlignedBB var1) {
      int var5 = MathHelper.floor_double(var1.minX);
      int var6 = MathHelper.floor_double(var1.maxX);
      int var7 = MathHelper.floor_double(var1.minY);
      int var8 = MathHelper.floor_double(var1.maxY);
      int var9 = MathHelper.floor_double(var1.minZ);
      int var10 = MathHelper.floor_double(var1.maxZ);
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

      for (int var12 = var5; var12 <= var6; var12++) {
         for (int var13 = var7; var13 <= var8; var13++) {
            for (int var14 = var9; var14 <= var10; var14++) {
               Block var15 = this.getBlockState(var11.set(var12, var13, var14)).getBlock();
               if (var15.getMaterial() != Material.air) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isAnyLiquid(AxisAlignedBB var1) {
      int var5 = MathHelper.floor_double(var1.minX);
      int var6 = MathHelper.floor_double(var1.maxX);
      int var7 = MathHelper.floor_double(var1.minY);
      int var8 = MathHelper.floor_double(var1.maxY);
      int var9 = MathHelper.floor_double(var1.minZ);
      int var10 = MathHelper.floor_double(var1.maxZ);
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

      for (int var12 = var5; var12 <= var6; var12++) {
         for (int var13 = var7; var13 <= var8; var13++) {
            for (int var14 = var9; var14 <= var10; var14++) {
               Block var15 = this.getBlockState(var11.set(var12, var13, var14)).getBlock();
               if (var15.getMaterial().isLiquid()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean a(AxisAlignedBB var1) {
      int var5 = MathHelper.floor_double(var1.minX);
      int var6 = MathHelper.floor_double(var1.maxX + 1.0);
      int var7 = MathHelper.floor_double(var1.minY);
      int var8 = MathHelper.floor_double(var1.maxY + 1.0);
      int var9 = MathHelper.floor_double(var1.minZ);
      int var10 = MathHelper.floor_double(var1.maxZ + 1.0);
      if (this.isAreaLoaded(var5, var7, var9, var6, var8, var10, true)) {
         BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

         for (int var12 = var5; var12 < var6; var12++) {
            for (int var13 = var7; var13 < var8; var13++) {
               for (int var14 = var9; var14 < var10; var14++) {
                  Block var15 = this.getBlockState(var11.set(var12, var13, var14)).getBlock();
                  if (var15 == Blocks.fire || var15 == Blocks.flowing_lava || var15 == Blocks.lava) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean handleMaterialAcceleration(AxisAlignedBB var1, Material var2, Entity var3) {
      int var7 = MathHelper.floor_double(var1.minX);
      int var8 = MathHelper.floor_double(var1.maxX + 1.0);
      int var9 = MathHelper.floor_double(var1.minY);
      int var10 = MathHelper.floor_double(var1.maxY + 1.0);
      int var11 = MathHelper.floor_double(var1.minZ);
      int var12 = MathHelper.floor_double(var1.maxZ + 1.0);
      if (!this.isAreaLoaded(var7, var9, var11, var8, var10, var12, true)) {
         return false;
      }

      boolean var13 = false;
      Vec3 var14 = new Vec3(0.0, 0.0, 0.0);
      BlockPos.MutableBlockPos var15 = new BlockPos.MutableBlockPos();

      for (int var16 = var7; var16 < var8; var16++) {
         for (int var17 = var9; var17 < var10; var17++) {
            for (int var18 = var11; var18 < var12; var18++) {
               var15.set(var16, var17, var18);
               IBlockState var19 = this.getBlockState(var15);
               Block var20 = var19.getBlock();
               if (var20.getMaterial() == var2) {
                  double var21 = var17 + 1 - BlockLiquid.getLiquidHeightPercent(var19.getValue(BlockLiquid.LEVEL));
                  if (var10 >= var21) {
                     var13 = true;
                     var14 = var20.modifyAcceleration(this, var15, var3, var14);
                  }
               }
            }
         }
      }

      if (var14.lengthVector() > 0.0 && var3.isPushedByWater()) {
         var14 = var14.normalize();
         double var24 = 0.014;
         var3.motionX = var3.motionX + var14.xCoord * var24;
         var3.motionY = var3.motionY + var14.yCoord * var24;
         var3.motionZ = var3.motionZ + var14.zCoord * var24;
      }

      return var13;
   }

   public boolean isMaterialInBB(AxisAlignedBB var1, Material var2) {
      int var6 = MathHelper.floor_double(var1.minX);
      int var7 = MathHelper.floor_double(var1.maxX + 1.0);
      int var8 = MathHelper.floor_double(var1.minY);
      int var9 = MathHelper.floor_double(var1.maxY + 1.0);
      int var10 = MathHelper.floor_double(var1.minZ);
      int var11 = MathHelper.floor_double(var1.maxZ + 1.0);
      BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

      for (int var13 = var6; var13 < var7; var13++) {
         for (int var14 = var8; var14 < var9; var14++) {
            for (int var15 = var10; var15 < var11; var15++) {
               if (this.getBlockState(var12.set(var13, var14, var15)).getBlock().getMaterial() == var2) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isAABBInMaterial(AxisAlignedBB var1, Material var2) {
      int var6 = MathHelper.floor_double(var1.minX);
      int var7 = MathHelper.floor_double(var1.maxX + 1.0);
      int var8 = MathHelper.floor_double(var1.minY);
      int var9 = MathHelper.floor_double(var1.maxY + 1.0);
      int var10 = MathHelper.floor_double(var1.minZ);
      int var11 = MathHelper.floor_double(var1.maxZ + 1.0);
      BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

      for (int var13 = var6; var13 < var7; var13++) {
         for (int var14 = var8; var14 < var9; var14++) {
            for (int var15 = var10; var15 < var11; var15++) {
               IBlockState var16 = this.getBlockState(var12.set(var13, var14, var15));
               Block var17 = var16.getBlock();
               if (var17.getMaterial() == var2) {
                  int var18 = var16.getValue(BlockLiquid.LEVEL);
                  double var19 = var14 + 1;
                  if (var18 < 8) {
                     var19 = var14 + 1 - var18 / 8.0;
                  }

                  if (var19 >= var1.minY) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public Explosion createExplosion(Entity var1, double var2, double var4, double var6, float var8, boolean var9) {
      return this.newExplosion(var1, var2, var4, var6, var8, false, var9);
   }

   public Explosion newExplosion(Entity var1, double var2, double var4, double var6, float var8, boolean var9, boolean var10) {
      Explosion var11 = new Explosion(this, var1, var2, var4, var6, var8, var9, var10);
      var11.doExplosionA();
      var11.doExplosionB(true);
      return var11;
   }

   public float getBlockDensity(Vec3 var1, AxisAlignedBB var2) {
      double var6 = 1.0 / ((var2.maxX - var2.minX) * 2.0 + 1.0);
      double var8 = 1.0 / ((var2.maxY - var2.minY) * 2.0 + 1.0);
      double var10 = 1.0 / ((var2.maxZ - var2.minZ) * 2.0 + 1.0);
      double var12 = (1.0 - Math.floor(1.0 / var6) * var6) / 2.0;
      double var14 = (1.0 - Math.floor(1.0 / var10) * var10) / 2.0;
      if (var6 >= 0.0 && var8 >= 0.0 && var10 >= 0.0) {
         int var16 = 0;
         int var17 = 0;

         for (float var18 = 0.0F; var18 <= 1.0F; var18 = (float)(var18 + var6)) {
            for (float var19 = 0.0F; var19 <= 1.0F; var19 = (float)(var19 + var8)) {
               for (float var20 = 0.0F; var20 <= 1.0F; var20 = (float)(var20 + var10)) {
                  double var21 = var2.minX + (var2.maxX - var2.minX) * var18;
                  double var23 = var2.minY + (var2.maxY - var2.minY) * var19;
                  double var25 = var2.minZ + (var2.maxZ - var2.minZ) * var20;
                  if (this.rayTraceBlocks(new Vec3(var21 + var12, var23, var25 + var14), var1) == null) {
                     var16++;
                  }

                  var17++;
               }
            }
         }

         return (float)var16 / var17;
      } else {
         return 0.0F;
      }
   }

   public boolean extinguishFire(EntityPlayer var1, BlockPos var2, EnumFacing var3) {
      var2 = var2.offset(var3);
      if (this.getBlockState(var2).getBlock() == Blocks.fire) {
         this.playAuxSFXAtEntity(var1, 1004, var2, 0);
         this.setBlockToAir(var2);
         return true;
      } else {
         return false;
      }
   }

   public String getDebugLoadedEntities() {
      return "All: " + this.loadedEntityList.size();
   }

   public String getProviderName() {
      return this.chunkProvider.makeString();
   }

   @Override
   public TileEntity getTileEntity(BlockPos var1) {
      if (!this.isValid(var1)) {
         return null;
      }

      TileEntity var5 = null;
      if (this.processingLoadedTiles) {
         for (int var6 = 0; var6 < this.addedTileEntityList.size(); var6++) {
            TileEntity var7 = this.addedTileEntityList.get(var6);
            if (!var7.isInvalid() && var7.getPos().equals(var1)) {
               var5 = var7;
               break;
            }
         }
      }

      if (var5 == null) {
         var5 = this.getChunkFromBlockCoords(var1).getTileEntity(var1, Chunk.EnumCreateEntityType.IMMEDIATE);
      }

      if (var5 == null) {
         for (int var8 = 0; var8 < this.addedTileEntityList.size(); var8++) {
            TileEntity var9 = this.addedTileEntityList.get(var8);
            if (!var9.isInvalid() && var9.getPos().equals(var1)) {
               var5 = var9;
               break;
            }
         }
      }

      return var5;
   }

   public void setTileEntity(BlockPos var1, TileEntity var2) {
      if (var2 != null && !var2.isInvalid()) {
         if (this.processingLoadedTiles) {
            var2.setPos(var1);
            Iterator var6 = this.addedTileEntityList.iterator();

            while (var6.hasNext()) {
               TileEntity var7 = (TileEntity)var6.next();
               if (var7.getPos().equals(var1)) {
                  var7.invalidate();
                  var6.remove();
               }
            }

            this.addedTileEntityList.add(var2);
         } else {
            this.addTileEntity(var2);
            this.getChunkFromBlockCoords(var1).addTileEntity(var1, var2);
         }
      }
   }

   public void removeTileEntity(BlockPos var1) {
      TileEntity var5 = this.getTileEntity(var1);
      if (var5 != null && this.processingLoadedTiles) {
         var5.invalidate();
         this.addedTileEntityList.remove(var5);
      } else {
         if (var5 != null) {
            this.addedTileEntityList.remove(var5);
            this.loadedTileEntityList.remove(var5);
            this.tickableTileEntities.remove(var5);
         }

         this.getChunkFromBlockCoords(var1).removeTileEntity(var1);
      }
   }

   public void markTileEntityForRemoval(TileEntity var1) {
      this.tileEntitiesToBeRemoved.add(var1);
   }

   public boolean isBlockFullCube(BlockPos var1) {
      IBlockState var5 = this.getBlockState(var1);
      AxisAlignedBB var6 = var5.getBlock().getCollisionBoundingBox(this, var1, var5);
      return var6 != null && var6.getAverageEdgeLength() >= 1.0;
   }

   public static boolean doesBlockHaveSolidTopSurface(IBlockAccess var0, BlockPos var1) {
      IBlockState var5 = var0.getBlockState(var1);
      Block var6 = var5.getBlock();
      return var6.getMaterial().isOpaque() && var6.isFullCube()
         ? true
         : (
            var6 instanceof BlockStairs
               ? var5.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP
               : (
                  var6 instanceof BlockSlab
                     ? var5.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP
                     : (var6 instanceof BlockHopper ? true : (var6 instanceof BlockSnow ? var5.getValue(BlockSnow.LAYERS) == 7 : false))
               )
         );
   }

   public boolean isBlockNormalCube(BlockPos var1, boolean var2) {
      if (!this.isValid(var1)) {
         return var2;
      }

      Chunk var6 = this.chunkProvider.provideChunk(var1);
      if (var6.isEmpty()) {
         return var2;
      }

      Block var7 = this.getBlockState(var1).getBlock();
      return var7.getMaterial().isOpaque() && var7.isFullCube();
   }

   public void a() {
      int var3 = this.calculateSkylightSubtracted(1.0F);
      if (var3 != this.skylightSubtracted) {
         this.skylightSubtracted = var3;
      }
   }

   public void setAllowedSpawnTypes(boolean var1, boolean var2) {
      this.spawnHostileMobs = var1;
      this.spawnPeacefulMobs = var2;
   }

   public void tick() {
      this.updateWeather();
   }

   public void H() {
      if (this.worldInfo.isRaining()) {
         this.rainingStrength = 1.0F;
         if (this.worldInfo.isThundering()) {
            this.thunderingStrength = 1.0F;
         }
      }
   }

   public void updateWeather() {
      if (!this.provider.getHasNoSky() && !this.isRemote) {
         int var4 = this.worldInfo.getCleanWeatherTime();
         if (var4 > 0) {
            this.worldInfo.setCleanWeatherTime(--var4);
            this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
            this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
         }

         int var5 = this.worldInfo.getThunderTime();
         if (var5 <= 0) {
            if (this.worldInfo.isThundering()) {
               this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
            } else {
               this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
            }
         } else {
            this.worldInfo.setThunderTime(--var5);
            if (var5 <= 0) {
               this.worldInfo.setThundering(!this.worldInfo.isThundering());
            }
         }

         this.prevThunderingStrength = this.thunderingStrength;
         if (this.worldInfo.isThundering()) {
            this.thunderingStrength = (float)(this.thunderingStrength + 0.01);
         } else {
            this.thunderingStrength = (float)(this.thunderingStrength - 0.01);
         }

         this.thunderingStrength = MathHelper.clamp_float(this.thunderingStrength, 0.0F, 1.0F);
         int var6 = this.worldInfo.getRainTime();
         if (var6 <= 0) {
            if (this.worldInfo.isRaining()) {
               this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
            } else {
               this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
            }
         } else {
            this.worldInfo.setRainTime(--var6);
            if (var6 <= 0) {
               this.worldInfo.setRaining(!this.worldInfo.isRaining());
            }
         }

         this.prevRainingStrength = this.rainingStrength;
         if (this.worldInfo.isRaining()) {
            this.rainingStrength = (float)(this.rainingStrength + 0.01);
         } else {
            this.rainingStrength = (float)(this.rainingStrength - 0.01);
         }

         this.rainingStrength = MathHelper.clamp_float(this.rainingStrength, 0.0F, 1.0F);
      }
   }

   public void setActivePlayerChunksAndCheckLight() {
      this.activeChunkSet.clear();
      this.theProfiler.startSection("buildList");

      for (int var4 = 0; var4 < this.playerEntities.size(); var4++) {
         EntityPlayer var5 = this.playerEntities.get(var4);
         int var6 = MathHelper.floor_double(var5.posX / 16.0);
         int var7 = MathHelper.floor_double(var5.posZ / 16.0);
         int var8 = this.getRenderDistanceChunks();

         for (int var9 = -var8; var9 <= var8; var9++) {
            for (int var10 = -var8; var10 <= var8; var10++) {
               this.activeChunkSet.add(new ChunkCoordIntPair(var9 + var6, var10 + var7));
            }
         }
      }

      this.theProfiler.endSection();
      if (this.ambientTickCountdown > 0) {
         this.ambientTickCountdown--;
      }

      this.theProfiler.startSection("playerCheckLight");
      if (!this.playerEntities.isEmpty()) {
         int var11 = this.rand.nextInt(this.playerEntities.size());
         EntityPlayer var12 = this.playerEntities.get(var11);
         int var13 = MathHelper.floor_double(var12.posX) + this.rand.nextInt(11) - 5;
         int var14 = MathHelper.floor_double(var12.posY) + this.rand.nextInt(11) - 5;
         int var15 = MathHelper.floor_double(var12.posZ) + this.rand.nextInt(11) - 5;
         this.checkLight(new BlockPos(var13, var14, var15));
      }

      this.theProfiler.endSection();
   }

   public abstract int getRenderDistanceChunks();

   public void playMoodSoundAndCheckLight(int var1, int var2, Chunk var3) {
      this.theProfiler.endStartSection("moodSound");
      if (this.ambientTickCountdown == 0 && !this.isRemote) {
         this.updateLCG = this.updateLCG * 3 + 1013904223;
         int var7 = this.updateLCG >> 2;
         int var8 = var7 & 15;
         int var9 = var7 >> 8 & 15;
         int var10 = var7 >> 16 & 0xFF;
         BlockPos var11 = new BlockPos(var8, var10, var9);
         Block var12 = var3.getBlock(var11);
         var8 += var1;
         var9 += var2;
         if (var12.getMaterial() == Material.air && this.getLight(var11) <= this.rand.nextInt(8) && this.getLightFor(EnumSkyBlock.SKY, var11) <= 0) {
            EntityPlayer var13 = this.getClosestPlayer(var8 + 0.5, var10 + 0.5, var9 + 0.5, 8.0);
            if (var13 != null && var13.getDistanceSq(var8 + 0.5, var10 + 0.5, var9 + 0.5) > 4.0) {
               this.playSoundEffect(var8 + 0.5, var10 + 0.5, var9 + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
               this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
            }
         }
      }

      this.theProfiler.endStartSection("checkLight");
      var3.enqueueRelightChecks();
   }

   public void updateBlocks() {
      this.setActivePlayerChunksAndCheckLight();
   }

   public void forceBlockUpdateTick(Block var1, BlockPos var2, Random var3) {
      this.scheduledUpdatesAreImmediate = true;
      var1.updateTick(this, var2, this.getBlockState(var2), var3);
      this.scheduledUpdatesAreImmediate = false;
   }

   public boolean canBlockFreezeWater(BlockPos var1) {
      return this.canBlockFreeze(var1, false);
   }

   public boolean canBlockFreezeNoWater(BlockPos var1) {
      return this.canBlockFreeze(var1, true);
   }

   public boolean canBlockFreeze(BlockPos var1, boolean var2) {
      BiomeGenBase var6 = this.getBiomeGenForCoords(var1);
      float var7 = var6.getFloatTemperature(var1);
      if (var7 > 0.15F) {
         return false;
      }

      if (var1.getY() >= 0 && var1.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, var1) < 10) {
         IBlockState var8 = this.getBlockState(var1);
         Block var9 = var8.getBlock();
         if ((var9 == Blocks.water || var9 == Blocks.flowing_water) && var8.getValue(BlockLiquid.LEVEL) == 0) {
            if (!var2) {
               return true;
            }

            boolean var10 = this.isWater(var1.west()) && this.isWater(var1.east()) && this.isWater(var1.north()) && this.isWater(var1.south());
            if (!var10) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isWater(BlockPos var1) {
      return this.getBlockState(var1).getBlock().getMaterial() == Material.water;
   }

   public boolean canSnowAt(BlockPos var1, boolean var2) {
      BiomeGenBase var6 = this.getBiomeGenForCoords(var1);
      float var7 = var6.getFloatTemperature(var1);
      if (var7 > 0.15F) {
         return false;
      }

      if (!var2) {
         return true;
      }

      if (var1.getY() >= 0 && var1.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, var1) < 10) {
         Block var8 = this.getBlockState(var1).getBlock();
         if (var8.getMaterial() == Material.air && Blocks.snow_layer.canPlaceBlockAt(this, var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean checkLight(BlockPos var1) {
      boolean var5 = false;
      if (!this.provider.getHasNoSky()) {
         var5 |= this.checkLightFor(EnumSkyBlock.SKY, var1);
      }

      return var5 | this.checkLightFor(EnumSkyBlock.BLOCK, var1);
   }

   private int getRawLight(BlockPos var1, EnumSkyBlock var2) {
      if (var2 == EnumSkyBlock.SKY && this.canSeeSky(var1)) {
         return 15;
      }

      Block var6 = this.getBlockState(var1).getBlock();
      int var7 = var2 == EnumSkyBlock.SKY ? 0 : var6.getLightValue();
      int var8 = var6.getLightOpacity();
      if (var8 >= 15 && var6.getLightValue() > 0) {
         var8 = 1;
      }

      if (var8 < 1) {
         var8 = 1;
      }

      if (var8 >= 15) {
         return 0;
      }

      if (var7 >= 14) {
         return var7;
      }

      for (EnumFacing var12 : EnumFacing.values()) {
         BlockPos var13 = var1.offset(var12);
         int var14 = this.getLightFor(var2, var13) - var8;
         if (var14 > var7) {
            var7 = var14;
         }

         if (var7 >= 14) {
            return var7;
         }
      }

      return var7;
   }

   public boolean checkLightFor(EnumSkyBlock var1, BlockPos var2) {
      if (!this.isAreaLoaded(var2, 17, false)) {
         return false;
      }

      int var6 = 0;
      int var7 = 0;
      this.theProfiler.startSection("getBrightness");
      int var8 = this.getLightFor(var1, var2);
      int var9 = this.getRawLight(var2, var1);
      int var10 = var2.getX();
      int var11 = var2.getY();
      int var12 = var2.getZ();
      if (var9 > var8) {
         this.lightUpdateBlockList[var7++] = 133152;
      } else if (var9 < var8) {
         this.lightUpdateBlockList[var7++] = 133152 | var8 << 18;

         while (var6 < var7) {
            int var13 = this.lightUpdateBlockList[var6++];
            int var14 = (var13 & 63) - 32 + var10;
            int var15 = (var13 >> 6 & 63) - 32 + var11;
            int var16 = (var13 >> 12 & 63) - 32 + var12;
            int var17 = var13 >> 18 & 15;
            BlockPos var18 = new BlockPos(var14, var15, var16);
            int var19 = this.getLightFor(var1, var18);
            if (var19 == var17) {
               this.setLightFor(var1, var18, 0);
               if (var17 > 0) {
                  int var20 = MathHelper.abs_int(var14 - var10);
                  int var21 = MathHelper.abs_int(var15 - var11);
                  int var22 = MathHelper.abs_int(var16 - var12);
                  if (var20 + var21 + var22 < 17) {
                     BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();

                     for (EnumFacing var27 : EnumFacing.values()) {
                        int var28 = var14 + var27.getFrontOffsetX();
                        int var29 = var15 + var27.getFrontOffsetY();
                        int var30 = var16 + var27.getFrontOffsetZ();
                        var23.set(var28, var29, var30);
                        int var31 = Math.max(1, this.getBlockState(var23).getBlock().getLightOpacity());
                        var19 = this.getLightFor(var1, var23);
                        if (var19 == var17 - var31 && var7 < this.lightUpdateBlockList.length) {
                           this.lightUpdateBlockList[var7++] = var28 - var10 + 32 | var29 - var11 + 32 << 6 | var30 - var12 + 32 << 12 | var17 - var31 << 18;
                        }
                     }
                  }
               }
            }
         }

         var6 = 0;
      }

      this.theProfiler.endSection();
      this.theProfiler.startSection("checkedPosition < toCheckCount");

      while (var6 < var7) {
         int var32 = this.lightUpdateBlockList[var6++];
         int var33 = (var32 & 63) - 32 + var10;
         int var34 = (var32 >> 6 & 63) - 32 + var11;
         int var35 = (var32 >> 12 & 63) - 32 + var12;
         BlockPos var36 = new BlockPos(var33, var34, var35);
         int var37 = this.getLightFor(var1, var36);
         int var39 = this.getRawLight(var36, var1);
         if (var39 != var37) {
            this.setLightFor(var1, var36, var39);
            if (var39 > var37) {
               int var40 = Math.abs(var33 - var10);
               int var41 = Math.abs(var34 - var11);
               int var42 = Math.abs(var35 - var12);
               boolean var43 = var7 < this.lightUpdateBlockList.length - 6;
               if (var40 + var41 + var42 < 17 && var43) {
                  if (this.getLightFor(var1, var36.west()) < var39) {
                     this.lightUpdateBlockList[var7++] = var33 - 1 - var10 + 32 + (var34 - var11 + 32 << 6) + (var35 - var12 + 32 << 12);
                  }

                  if (this.getLightFor(var1, var36.east()) < var39) {
                     this.lightUpdateBlockList[var7++] = var33 + 1 - var10 + 32 + (var34 - var11 + 32 << 6) + (var35 - var12 + 32 << 12);
                  }

                  if (this.getLightFor(var1, var36.down()) < var39) {
                     this.lightUpdateBlockList[var7++] = var33 - var10 + 32 + (var34 - 1 - var11 + 32 << 6) + (var35 - var12 + 32 << 12);
                  }

                  if (this.getLightFor(var1, var36.up()) < var39) {
                     this.lightUpdateBlockList[var7++] = var33 - var10 + 32 + (var34 + 1 - var11 + 32 << 6) + (var35 - var12 + 32 << 12);
                  }

                  if (this.getLightFor(var1, var36.north()) < var39) {
                     this.lightUpdateBlockList[var7++] = var33 - var10 + 32 + (var34 - var11 + 32 << 6) + (var35 - 1 - var12 + 32 << 12);
                  }

                  if (this.getLightFor(var1, var36.south()) < var39) {
                     this.lightUpdateBlockList[var7++] = var33 - var10 + 32 + (var34 - var11 + 32 << 6) + (var35 + 1 - var12 + 32 << 12);
                  }
               }
            }
         }
      }

      this.theProfiler.endSection();
      return true;
   }

   public boolean tickUpdates(boolean var1) {
      return false;
   }

   public List<NextTickListEntry> getPendingBlockUpdates(Chunk var1, boolean var2) {
      return null;
   }

   public List<NextTickListEntry> func_175712_a(StructureBoundingBox var1, boolean var2) {
      return null;
   }

   public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity var1, AxisAlignedBB var2) {
      return this.getEntitiesInAABBexcluding(var1, var2, EntitySelectors.NOT_SPECTATING);
   }

   public List<Entity> getEntitiesInAABBexcluding(Entity var1, AxisAlignedBB var2, Predicate<? super Entity> var3) {
      ArrayList var7 = Lists.newArrayList();
      int var8 = MathHelper.floor_double((var2.minX - 2.0) / 16.0);
      int var9 = MathHelper.floor_double((var2.maxX + 2.0) / 16.0);
      int var10 = MathHelper.floor_double((var2.minZ - 2.0) / 16.0);
      int var11 = MathHelper.floor_double((var2.maxZ + 2.0) / 16.0);

      for (int var12 = var8; var12 <= var9; var12++) {
         for (int var13 = var10; var13 <= var11; var13++) {
            if (this.isChunkLoaded(var12, var13, true)) {
               this.getChunkFromChunkCoords(var12, var13).getEntitiesWithinAABBForEntity(var1, var2, var7, var3);
            }
         }
      }

      return var7;
   }

   public <T extends Entity> List<T> getEntities(Class<? extends T> var1, Predicate<? super T> var2) {
      ArrayList var6 = Lists.newArrayList();

      for (Entity var8 : this.loadedEntityList) {
         if (var1.isAssignableFrom(var8.getClass()) && var2.apply((T)(var8))) {
            var6.add(var8);
         }
      }

      return var6;
   }

   public <T extends Entity> List<T> getPlayers(Class<? extends T> var1, Predicate<? super T> var2) {
      ArrayList var6 = Lists.newArrayList();

      for (Entity var8 : this.playerEntities) {
         if (var1.isAssignableFrom(var8.getClass()) && var2.apply((T)(var8))) {
            var6.add(var8);
         }
      }

      return var6;
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> var1, AxisAlignedBB var2) {
      return this.getEntitiesWithinAABB(var1, var2, EntitySelectors.NOT_SPECTATING);
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> var1, AxisAlignedBB var2, Predicate<? super T> var3) {
      int var7 = MathHelper.floor_double((var2.minX - 2.0) / 16.0);
      int var8 = MathHelper.floor_double((var2.maxX + 2.0) / 16.0);
      int var9 = MathHelper.floor_double((var2.minZ - 2.0) / 16.0);
      int var10 = MathHelper.floor_double((var2.maxZ + 2.0) / 16.0);
      ArrayList var11 = Lists.newArrayList();

      for (int var12 = var7; var12 <= var8; var12++) {
         for (int var13 = var9; var13 <= var10; var13++) {
            if (this.isChunkLoaded(var12, var13, true)) {
               this.getChunkFromChunkCoords(var12, var13).getEntitiesOfTypeWithinAAAB(var1, var2, var11, var3);
            }
         }
      }

      return var11;
   }

   public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> var1, AxisAlignedBB var2, T var3) {
      List var7 = this.getEntitiesWithinAABB(var1, var2);
      Entity var8 = null;
      double var9 = Double.MAX_VALUE;

      for (int var11 = 0; var11 < var7.size(); var11++) {
         Entity var12 = (Entity)var7.get(var11);
         if (var12 != var3 && EntitySelectors.NOT_SPECTATING.apply(var12)) {
            double var13 = var3.getDistanceSqToEntity(var12);
            if (var13 <= var9) {
               var8 = var12;
               var9 = var13;
            }
         }
      }

      return (T)var8;
   }

   public Entity getEntityByID(int var1) {
      return this.entitiesById.lookup(var1);
   }

   public List<Entity> getLoadedEntityList() {
      return this.loadedEntityList;
   }

   public void markChunkDirty(BlockPos var1, TileEntity var2) {
      if (this.isBlockLoaded(var1)) {
         this.getChunkFromBlockCoords(var1).setChunkModified();
      }
   }

   public int countEntities(Class<?> var1) {
      int var5 = 0;

      for (Entity var7 : this.loadedEntityList) {
         if ((!(var7 instanceof EntityLiving) || !((EntityLiving)var7).isNoDespawnRequired()) && var1.isAssignableFrom(var7.getClass())) {
            var5++;
         }
      }

      return var5;
   }

   public void loadEntities(Collection<Entity> var1) {
      this.loadedEntityList.addAll(var1);

      for (Entity var6 : var1) {
         this.onEntityAdded(var6);
      }
   }

   public void unloadEntities(Collection<Entity> var1) {
      this.unloadedEntityList.addAll(var1);
   }

   public boolean canBlockBePlaced(Block var1, BlockPos var2, boolean var3, EnumFacing var4, Entity var5, ItemStack var6) {
      Block var10 = this.getBlockState(var2).getBlock();
      AxisAlignedBB var11 = var3 ? null : var1.getCollisionBoundingBox(this, var2, var1.getDefaultState());
      return var11 != null && !this.checkNoEntityCollision(var11, var5)
         ? false
         : (
            var10.getMaterial() == Material.circuits && var1 == Blocks.anvil
               ? true
               : var10.getMaterial().isReplaceable() && var1.canReplace(this, var2, var4, var6)
         );
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public void setSeaLevel(int var1) {
      this.seaLevel = var1;
   }

   @Override
   public int getStrongPower(BlockPos var1, EnumFacing var2) {
      IBlockState var3 = this.getBlockState(var1);
      return var3.getBlock().getStrongPower(this, var1, var3, var2);
   }

   @Override
   public WorldType getWorldType() {
      return this.worldInfo.getTerrainType();
   }

   public int getStrongPower(BlockPos var1) {
      int var5 = 0;
      var5 = Math.max(var5, this.getStrongPower(var1.down(), EnumFacing.DOWN));
      if (var5 >= 15) {
         return var5;
      }

      var5 = Math.max(var5, this.getStrongPower(var1.up(), EnumFacing.UP));
      if (var5 >= 15) {
         return var5;
      }

      var5 = Math.max(var5, this.getStrongPower(var1.north(), EnumFacing.NORTH));
      if (var5 >= 15) {
         return var5;
      }

      var5 = Math.max(var5, this.getStrongPower(var1.south(), EnumFacing.SOUTH));
      if (var5 >= 15) {
         return var5;
      }

      var5 = Math.max(var5, this.getStrongPower(var1.west(), EnumFacing.WEST));
      if (var5 >= 15) {
         return var5;
      }

      var5 = Math.max(var5, this.getStrongPower(var1.east(), EnumFacing.EAST));
      return var5 >= 15 ? var5 : var5;
   }

   public boolean isSidePowered(BlockPos var1, EnumFacing var2) {
      return this.getRedstonePower(var1, var2) > 0;
   }

   public int getRedstonePower(BlockPos var1, EnumFacing var2) {
      IBlockState var6 = this.getBlockState(var1);
      Block var7 = var6.getBlock();
      return var7.isNormalCube() ? this.getStrongPower(var1) : var7.getWeakPower(this, var1, var6, var2);
   }

   public boolean isBlockPowered(BlockPos var1) {
      return this.getRedstonePower(var1.down(), EnumFacing.DOWN) > 0
         ? true
         : (
            this.getRedstonePower(var1.up(), EnumFacing.UP) > 0
               ? true
               : (
                  this.getRedstonePower(var1.north(), EnumFacing.NORTH) > 0
                     ? true
                     : (
                        this.getRedstonePower(var1.south(), EnumFacing.SOUTH) > 0
                           ? true
                           : (this.getRedstonePower(var1.west(), EnumFacing.WEST) > 0 ? true : this.getRedstonePower(var1.east(), EnumFacing.EAST) > 0)
                     )
               )
         );
   }

   public int isBlockIndirectlyGettingPowered(BlockPos var1) {
      int var5 = 0;

      for (EnumFacing var9 : EnumFacing.values()) {
         int var10 = this.getRedstonePower(var1.offset(var9), var9);
         if (var10 >= 15) {
            return 15;
         }

         if (var10 > var5) {
            var5 = var10;
         }
      }

      return var5;
   }

   public EntityPlayer getClosestPlayerToEntity(Entity var1, double var2) {
      return this.getClosestPlayer(var1.posX, var1.posY, var1.posZ, var2);
   }

   public EntityPlayer getClosestPlayer(double var1, double var3, double var5, double var7) {
      double var12 = -1.0;
      EntityPlayer var14 = null;

      for (int var15 = 0; var15 < this.playerEntities.size(); var15++) {
         EntityPlayer var16 = this.playerEntities.get(var15);
         if (EntitySelectors.NOT_SPECTATING.apply(var16)) {
            double var17 = var16.getDistanceSq(var1, var3, var5);
            if ((var7 < 0.0 || var17 < var7 * var7) && (var12 == -1.0 || var17 < var12)) {
               var12 = var17;
               var14 = var16;
            }
         }
      }

      return var14;
   }

   public boolean isAnyPlayerWithinRangeAt(double var1, double var3, double var5, double var7) {
      for (int var12 = 0; var12 < this.playerEntities.size(); var12++) {
         EntityPlayer var13 = this.playerEntities.get(var12);
         if (EntitySelectors.NOT_SPECTATING.apply(var13)) {
            double var14 = var13.getDistanceSq(var1, var3, var5);
            if (var7 < 0.0 || var14 < var7 * var7) {
               return true;
            }
         }
      }

      return false;
   }

   public EntityPlayer getPlayerEntityByName(String var1) {
      for (int var5 = 0; var5 < this.playerEntities.size(); var5++) {
         EntityPlayer var6 = this.playerEntities.get(var5);
         if (var1.equals(var6.getName())) {
            return var6;
         }
      }

      return null;
   }

   public EntityPlayer getPlayerEntityByUUID(UUID var1) {
      for (int var5 = 0; var5 < this.playerEntities.size(); var5++) {
         EntityPlayer var6 = this.playerEntities.get(var5);
         if (var1.equals(var6.getUniqueID())) {
            return var6;
         }
      }

      return null;
   }

   public void sendQuittingDisconnectingPacket() {
   }

   public void checkSessionLock() throws MinecraftException {
      this.saveHandler.checkSessionLock();
   }

   public void m(long var1) {
      this.worldInfo.setWorldTotalTime(var1);
   }

   public long getSeed() {
      return this.worldInfo.getSeed();
   }

   public long getTotalWorldTime() {
      return this.worldInfo.getWorldTotalTime();
   }

   public long getWorldTime() {
      return this.worldInfo.getWorldTime();
   }

   public void setWorldTime(long var1) {
      this.worldInfo.setWorldTime(var1);
   }

   public BlockPos getSpawnPoint() {
      BlockPos var4 = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
      if (!this.getWorldBorder().contains(var4)) {
         var4 = this.getHeight(new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
      }

      return var4;
   }

   public void setSpawnPoint(BlockPos var1) {
      this.worldInfo.setSpawn(var1);
   }

   public void joinEntityInSurroundings(Entity var1) {
      int var5 = MathHelper.floor_double(var1.posX / 16.0);
      int var6 = MathHelper.floor_double(var1.posZ / 16.0);
      byte var7 = 2;

      for (int var8 = var5 - var7; var8 <= var5 + var7; var8++) {
         for (int var9 = var6 - var7; var9 <= var6 + var7; var9++) {
            this.getChunkFromChunkCoords(var8, var9);
         }
      }

      if (!this.loadedEntityList.contains(var1)) {
         this.loadedEntityList.add(var1);
      }
   }

   public boolean isBlockModifiable(EntityPlayer var1, BlockPos var2) {
      return true;
   }

   public void setEntityState(Entity var1, byte var2) {
   }

   public IChunkProvider getChunkProvider() {
      return this.chunkProvider;
   }

   public void addBlockEvent(BlockPos var1, Block var2, int var3, int var4) {
      var2.onBlockEventReceived(this, var1, this.getBlockState(var1), var3, var4);
   }

   public ISaveHandler getSaveHandler() {
      return this.saveHandler;
   }

   public WorldInfo getWorldInfo() {
      return this.worldInfo;
   }

   public GameRules getGameRules() {
      return this.worldInfo.getGameRulesInstance();
   }

   public void updateAllPlayersSleepingFlag() {
   }

   public float getThunderStrength(float var1) {
      return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * var1) * this.getRainStrength(var1);
   }

   public void t(float var1) {
      this.prevThunderingStrength = var1;
      this.thunderingStrength = var1;
   }

   public float getRainStrength(float var1) {
      return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * var1;
   }

   public void V(float var1) {
      this.prevRainingStrength = var1;
      this.rainingStrength = var1;
   }

   public boolean isThundering() {
      return this.getThunderStrength(1.0F) > 0.9;
   }

   public boolean isRaining() {
      return this.getRainStrength(1.0F) > 0.2;
   }

   public boolean isRainingAt(BlockPos var1) {
      if (!this.isRaining()) {
         return false;
      }

      if (!this.canSeeSky(var1)) {
         return false;
      }

      if (this.getPrecipitationHeight(var1).getY() > var1.getY()) {
         return false;
      }

      BiomeGenBase var5 = this.getBiomeGenForCoords(var1);
      return var5.getEnableSnow() ? false : (this.canSnowAt(var1, false) ? false : var5.canRain());
   }

   public boolean isBlockinHighHumidity(BlockPos var1) {
      BiomeGenBase var2 = this.getBiomeGenForCoords(var1);
      return var2.isHighHumidity();
   }

   public MapStorage getMapStorage() {
      return this.mapStorage;
   }

   public void setItemData(String var1, WorldSavedData var2) {
      this.mapStorage.setData(var1, var2);
   }

   public WorldSavedData loadItemData(Class<? extends WorldSavedData> var1, String var2) {
      return this.mapStorage.loadData(var1, var2);
   }

   public int getUniqueDataId(String var1) {
      return this.mapStorage.getUniqueDataId(var1);
   }

   public void playBroadcastSound(int var1, BlockPos var2, int var3) {
      for (int var7 = 0; var7 < this.worldAccesses.size(); var7++) {
         this.worldAccesses.get(var7).broadcastSound(var1, var2, var3);
      }
   }

   public void playAuxSFX(int var1, BlockPos var2, int var3) {
      this.playAuxSFXAtEntity((EntityPlayer)null, var1, var2, var3);
   }

   public void playAuxSFXAtEntity(EntityPlayer var1, int var2, BlockPos var3, int var4) {
      try {
         for (int var8 = 0; var8 < this.worldAccesses.size(); var8++) {
            this.worldAccesses.get(var8).playAuxSFX(var1, var2, var3, var4);
         }
      } catch (Throwable var11) {
         CrashReport var9 = CrashReport.makeCrashReport(var11, "Playing level event");
         CrashReportCategory var10 = var9.makeCategory("Level event being played");
         var10.addCrashSection("Block coordinates", CrashReportCategory.getCoordinateInfo(var3));
         var10.addCrashSection("Event source", var1);
         var10.addCrashSection("Event type", var2);
         var10.addCrashSection("Event data", var4);
         throw new ReportedException(var9);
      }
   }

   public int getHeight() {
      return 256;
   }

   public int getActualHeight() {
      return this.provider.getHasNoSky() ? 128 : 256;
   }

   public Random setRandomSeed(int var1, int var2, int var3) {
      long var4 = var1 * 341873128712L + var2 * 132897987541L + this.getWorldInfo().getSeed() + var3;
      this.rand.setSeed(var4);
      return this.rand;
   }

   public BlockPos getStrongholdPos(String var1, BlockPos var2) {
      return this.getChunkProvider().getStrongholdGen(this, var1, var2);
   }

   @Override
   public boolean extendedLevelsInChunkCache() {
      return false;
   }

   public double getHorizon() {
      return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0 : 63.0;
   }

   public CrashReportCategory addWorldInfoToCrashReport(CrashReport var1) {
      CrashReportCategory var5 = var1.makeCategoryDepth("Affected level", 1);
      var5.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
      var5.addCrashSectionCallable("All players", new Callable<String>() {
         private static final long a = 71452777359573L;

         public String call() {
            return World.this.playerEntities.size() + " total; " + World.this.playerEntities.toString();
         }
      });
      var5.addCrashSectionCallable("Chunk stats", new Callable<String>() {
         public String call() {
            return World.this.chunkProvider.makeString();
         }
      });

      try {
         this.worldInfo.addToCrashReport(var5);
      } catch (Throwable var7) {
         var5.addCrashSectionThrowable("Level Data Unobtainable", var7);
      }

      return var5;
   }

   public void sendBlockBreakProgress(int var1, BlockPos var2, int var3) {
      for (int var7 = 0; var7 < this.worldAccesses.size(); var7++) {
         IWorldAccess var8 = this.worldAccesses.get(var7);
         var8.sendBlockBreakProgress(var1, var2, var3);
      }
   }

   public Calendar getCurrentDate() {
      if (this.getTotalWorldTime() % 600L == 0L) {
         this.theCalendar.setTimeInMillis(MinecraftServer.getCurrentTimeMillis());
      }

      return this.theCalendar;
   }

   public void makeFireworks(double var1, double var3, double var5, double var7, double var9, double var11, NBTTagCompound var13) {
   }

   public Scoreboard getScoreboard() {
      return this.worldScoreboard;
   }

   public void updateComparatorOutputLevel(BlockPos var1, Block var2) {
      for (Object var7 : EnumFacing.Plane.HORIZONTAL) {
         BlockPos var8 = var1.offset((EnumFacing)var7);
         if (this.isBlockLoaded(var8)) {
            IBlockState var9 = this.getBlockState(var8);
            if (Blocks.unpowered_comparator.N(var9.getBlock())) {
               var9.getBlock().onNeighborBlockChange(this, var8, var9, var2);
            } else if (var9.getBlock().isNormalCube()) {
               var8 = var8.offset((EnumFacing)var7);
               var9 = this.getBlockState(var8);
               if (Blocks.unpowered_comparator.N(var9.getBlock())) {
                  var9.getBlock().onNeighborBlockChange(this, var8, var9, var2);
               }
            }
         }
      }
   }

   public DifficultyInstance getDifficultyForLocation(BlockPos var1) {
      long var5 = 0L;
      float var7 = 0.0F;
      if (this.isBlockLoaded(var1)) {
         var7 = this.getCurrentMoonPhaseFactor();
         var5 = this.getChunkFromBlockCoords(var1).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getWorldTime(), var5, var7);
   }

   public EnumDifficulty getDifficulty() {
      return this.getWorldInfo().getDifficulty();
   }

   public int getSkylightSubtracted() {
      return this.skylightSubtracted;
   }

   public void setSkylightSubtracted(int var1) {
      this.skylightSubtracted = var1;
   }

   public int x() {
      return this.lastLightningBolt;
   }

   public void setLastLightningBolt(int var1) {
      this.lastLightningBolt = var1;
   }

   public boolean v() {
      return this.findingSpawnPoint;
   }

   public VillageCollection getVillageCollection() {
      return this.villageCollectionObj;
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public boolean isSpawnChunk(int var1, int var2) {
      BlockPos var6 = this.getSpawnPoint();
      int var7 = var1 * 16 + 8 - var6.getX();
      int var8 = var2 * 16 + 8 - var6.getZ();
      short var9 = 128;
      return var7 >= -var9 && var7 <= var9 && var8 >= -var9 && var8 <= var9;
   }
}
