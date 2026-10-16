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

   public World(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
      this.saveHandler = saveHandlerIn;
      this.theProfiler = profilerIn;
      this.worldInfo = info;
      this.provider = providerIn;
      this.isRemote = client;
      this.worldBorder = providerIn.getWorldBorder();
   }

   public World init() {
      return this;
   }

   @Override
   public BiomeGenBase getBiomeGenForCoords(final BlockPos pos) {
      if (this.isBlockLoaded(pos)) {
         Chunk chunk = this.getChunkFromBlockCoords(pos);

         try {
            return chunk.getBiome(pos, this.provider.getWorldChunkManager());
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Getting biome");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Coordinates of biome request");
            crashReportCategory.addCrashSectionCallable("Location", new Callable<String>() {
               public String call() throws Exception {
                  return CrashReportCategory.getCoordinateInfo(pos);
               }
            });
            throw new ReportedException(crashReport);
         }
      } else {
         return this.provider.getWorldChunkManager().getBiomeGenerator(pos, BiomeGenBase.plains);
      }
   }

   public WorldChunkManager getWorldChunkManager() {
      return this.provider.getWorldChunkManager();
   }

   public abstract IChunkProvider createChunkProvider();

   public void initialize(WorldSettings settings) {
      this.worldInfo.setServerInitialized(true);
   }

   public void setInitialSpawnLocation() {
      this.setSpawnPoint(new BlockPos(8, 64, 8));
   }

   public Block getGroundAboveSeaLevel(BlockPos pos) {
      BlockPos blockPos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());

      while (!this.isAirBlock(blockPos.up())) {
         blockPos = blockPos.up();
      }

      return this.getBlockState(blockPos).getBlock();
   }

   private boolean isValid(BlockPos pos) {
      return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000 && pos.getY() >= 0 && pos.getY() < 256;
   }

   @Override
   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
   }

   public boolean isBlockLoaded(BlockPos pos) {
      return this.isBlockLoaded(pos, true);
   }

   public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
      return !this.isValid(pos) ? false : this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, allowEmpty);
   }

   public boolean isAreaLoaded(BlockPos center, int radius) {
      return this.isAreaLoaded(center, radius, true);
   }

   public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty) {
      return this.isAreaLoaded(
         center.getX() - radius,
         center.getY() - radius,
         center.getZ() - radius,
         center.getX() + radius,
         center.getY() + radius,
         center.getZ() + radius,
         allowEmpty
      );
   }

   public boolean isAreaLoaded(BlockPos from, BlockPos to) {
      return this.isAreaLoaded(from, to, true);
   }

   public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty) {
      return this.isAreaLoaded(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), allowEmpty);
   }

   public boolean isAreaLoaded(StructureBoundingBox box) {
      return this.isAreaLoaded(box, true);
   }

   public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty) {
      return this.isAreaLoaded(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, allowEmpty);
   }

   private boolean isAreaLoaded(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, boolean allowEmpty) {
      if (yEnd >= 0 && yStart < 256) {
         xStart >>= 4;
         zStart >>= 4;
         xEnd >>= 4;
         zEnd >>= 4;

         for (int i = xStart; i <= xEnd; i++) {
            for (int j = zStart; j <= zEnd; j++) {
               if (!this.isChunkLoaded(i, j, allowEmpty)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
      return this.chunkProvider.chunkExists(x, z) && (allowEmpty || !this.chunkProvider.provideChunk(x, z).isEmpty());
   }

   public Chunk getChunkFromBlockCoords(BlockPos pos) {
      return this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
   }

   public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
      return this.chunkProvider.provideChunk(chunkX, chunkZ);
   }

   public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
      if (!this.isValid(pos)) {
         return false;
      }

      if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         return false;
      }

      Chunk chunk = this.getChunkFromBlockCoords(pos);
      Block block = newState.getBlock();
      IBlockState iBlockState = chunk.setBlockState(pos, newState);
      if (iBlockState == null) {
         return false;
      }

      Block block2 = iBlockState.getBlock();
      if (block.getLightOpacity() != block2.getLightOpacity() || block.getLightValue() != block2.getLightValue()) {
         this.theProfiler.startSection("checkLight");
         this.checkLight(pos);
         this.theProfiler.endSection();
      }

      if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && chunk.isPopulated()) {
         this.markBlockForUpdate(pos);
      }

      if (!this.isRemote && (flags & 1) != 0) {
         this.notifyNeighborsRespectDebug(pos, iBlockState.getBlock());
         if (block.hasComparatorInputOverride()) {
            this.updateComparatorOutputLevel(pos, block);
         }
      }

      return true;
   }

   public boolean setBlockToAir(BlockPos pos) {
      return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
   }

   public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
      IBlockState iBlockState = this.getBlockState(pos);
      Block block = iBlockState.getBlock();
      if (block.getMaterial() == Material.air) {
         return false;
      }

      this.playAuxSFX(2001, pos, Block.getStateId(iBlockState));
      if (dropBlock) {
         block.dropBlockAsItem(this, pos, iBlockState, 0);
      }

      return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
   }

   public boolean setBlockState(BlockPos pos, IBlockState state) {
      return this.setBlockState(pos, state, 3);
   }

   public void markBlockForUpdate(BlockPos pos) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).markBlockForUpdate(pos);
      }
   }

   public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType) {
      if (this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
         this.notifyNeighborsOfStateChange(pos, blockType);
      }
   }

   public void markBlocksDirtyVertical(int x1, int z1, int x2, int z2) {
      if (x2 > z2) {
         int i = z2;
         z2 = x2;
         x2 = i;
      }

      if (!this.provider.getHasNoSky()) {
         for (int j = x2; j <= z2; j++) {
            this.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x1, j, z1));
         }
      }

      this.markBlockRangeForRenderUpdate(x1, x2, z1, x1, z2, z1);
   }

   public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) {
      this.markBlockRangeForRenderUpdate(rangeMin.getX(), rangeMin.getY(), rangeMin.getZ(), rangeMax.getX(), rangeMax.getY(), rangeMax.getZ());
   }

   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
      }
   }

   public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType) {
      this.notifyBlockOfStateChange(pos.west(), blockType);
      this.notifyBlockOfStateChange(pos.east(), blockType);
      this.notifyBlockOfStateChange(pos.down(), blockType);
      this.notifyBlockOfStateChange(pos.up(), blockType);
      this.notifyBlockOfStateChange(pos.north(), blockType);
      this.notifyBlockOfStateChange(pos.south(), blockType);
   }

   public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
      if (skipSide != EnumFacing.WEST) {
         this.notifyBlockOfStateChange(pos.west(), blockType);
      }

      if (skipSide != EnumFacing.EAST) {
         this.notifyBlockOfStateChange(pos.east(), blockType);
      }

      if (skipSide != EnumFacing.DOWN) {
         this.notifyBlockOfStateChange(pos.down(), blockType);
      }

      if (skipSide != EnumFacing.UP) {
         this.notifyBlockOfStateChange(pos.up(), blockType);
      }

      if (skipSide != EnumFacing.NORTH) {
         this.notifyBlockOfStateChange(pos.north(), blockType);
      }

      if (skipSide != EnumFacing.SOUTH) {
         this.notifyBlockOfStateChange(pos.south(), blockType);
      }
   }

   public void notifyBlockOfStateChange(BlockPos pos, final Block blockIn) {
      if (!this.isRemote) {
         IBlockState iBlockState = this.getBlockState(pos);

         try {
            iBlockState.getBlock().onNeighborBlockChange(this, pos, iBlockState, blockIn);
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Block being updated");
            crashReportCategory.addCrashSectionCallable(
               "Source block type",
               new Callable<String>() {
                  public String call() throws Exception {
                     try {
                        return String.format(
                           "ID #%d (%s // %s)", Block.getIdFromBlock(blockIn), blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName()
                        );
                     } catch (Throwable throwable2) {
                        return "ID #" + Block.getIdFromBlock(blockIn);
                     }
                  }
               }
            );
            CrashReportCategory.addBlockInfo(crashReportCategory, pos, iBlockState);
            throw new ReportedException(crashReport);
         }
      }
   }

   public boolean isBlockTickPending(BlockPos pos, Block blockType) {
      return false;
   }

   public boolean canSeeSky(BlockPos pos) {
      return this.getChunkFromBlockCoords(pos).canSeeSky(pos);
   }

   public boolean canBlockSeeSky(BlockPos pos) {
      if (pos.getY() >= this.getSeaLevel()) {
         return this.canSeeSky(pos);
      }

      BlockPos blockPos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
      if (!this.canSeeSky(blockPos)) {
         return false;
      }

      for (BlockPos blockPos2 = blockPos.down(); blockPos2.getY() > pos.getY(); blockPos2 = blockPos2.down()) {
         Block block = this.getBlockState(blockPos2).getBlock();
         if (block.getLightOpacity() > 0 && !block.getMaterial().isLiquid()) {
            return false;
         }
      }

      return true;
   }

   public int getLight(BlockPos pos) {
      if (pos.getY() < 0) {
         return 0;
      }

      if (pos.getY() >= 256) {
         pos = new BlockPos(pos.getX(), 255, pos.getZ());
      }

      return this.getChunkFromBlockCoords(pos).getLightSubtracted(pos, 0);
   }

   public int getLightFromNeighbors(BlockPos pos) {
      return this.getLight(pos, true);
   }

   public int getLight(BlockPos pos, boolean checkNeighbors) {
      if (pos.getX() < -30000000 || pos.getZ() < -30000000 || pos.getX() >= 30000000 || pos.getZ() >= 30000000) {
         return 15;
      }

      if (checkNeighbors && this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
         int i = this.getLight(pos.up(), false);
         int j = this.getLight(pos.east(), false);
         int k = this.getLight(pos.west(), false);
         int l = this.getLight(pos.south(), false);
         int m = this.getLight(pos.north(), false);
         if (j > i) {
            i = j;
         }

         if (k > i) {
            i = k;
         }

         if (l > i) {
            i = l;
         }

         if (m > i) {
            i = m;
         }

         return i;
      } else {
         if (pos.getY() < 0) {
            return 0;
         }

         if (pos.getY() >= 256) {
            pos = new BlockPos(pos.getX(), 255, pos.getZ());
         }

         Chunk chunk = this.getChunkFromBlockCoords(pos);
         return chunk.getLightSubtracted(pos, this.skylightSubtracted);
      }
   }

   public BlockPos getHeight(BlockPos pos) {
      int i;
      if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
         if (this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, true)) {
            i = this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4).getHeightValue(pos.getX() & 15, pos.getZ() & 15);
         } else {
            i = 0;
         }
      } else {
         i = this.getSeaLevel() + 1;
      }

      return new BlockPos(pos.getX(), i, pos.getZ());
   }

   public int getChunksLowestHorizon(int x, int z) {
      if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
         if (!this.isChunkLoaded(x >> 4, z >> 4, true)) {
            return 0;
         }

         Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
         return chunk.getLowestHeight();
      } else {
         return this.getSeaLevel() + 1;
      }
   }

   public int getLightFromNeighborsFor(EnumSkyBlock typeIn, BlockPos pos) {
      if (this.provider.getHasNoSky() && typeIn == EnumSkyBlock.SKY) {
         return 0;
      }

      if (pos.getY() < 0) {
         pos = new BlockPos(pos.getX(), 0, pos.getZ());
      }

      if (!this.isValid(pos)) {
         return typeIn.defaultLightValue;
      }

      if (!this.isBlockLoaded(pos)) {
         return typeIn.defaultLightValue;
      }

      if (this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
         int i = this.getLightFor(typeIn, pos.up());
         int j = this.getLightFor(typeIn, pos.east());
         int k = this.getLightFor(typeIn, pos.west());
         int l = this.getLightFor(typeIn, pos.south());
         int m = this.getLightFor(typeIn, pos.north());
         if (j > i) {
            i = j;
         }

         if (k > i) {
            i = k;
         }

         if (l > i) {
            i = l;
         }

         if (m > i) {
            i = m;
         }

         return i;
      } else {
         Chunk chunk = this.getChunkFromBlockCoords(pos);
         return chunk.getLightFor(typeIn, pos);
      }
   }

   public int getLightFor(EnumSkyBlock typeIn, BlockPos pos) {
      if (pos.getY() < 0) {
         pos = new BlockPos(pos.getX(), 0, pos.getZ());
      }

      if (!this.isValid(pos)) {
         return typeIn.defaultLightValue;
      }

      if (!this.isBlockLoaded(pos)) {
         return typeIn.defaultLightValue;
      }

      Chunk chunk = this.getChunkFromBlockCoords(pos);
      return chunk.getLightFor(typeIn, pos);
   }

   public void setLightFor(EnumSkyBlock typeIn, BlockPos pos, int lightValue) {
      if (this.isValid(pos) && this.isBlockLoaded(pos)) {
         Chunk chunk = this.getChunkFromBlockCoords(pos);
         chunk.setLightFor(typeIn, pos, lightValue);
         this.notifyLightSet(pos);
      }
   }

   public void notifyLightSet(BlockPos pos) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).notifyLightSet(pos);
      }
   }

   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int i = this.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
      int j = this.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);
      if (j < lightValue) {
         j = lightValue;
      }

      return i << 20 | j << 4;
   }

   public float getLightBrightness(BlockPos pos) {
      return this.provider.getLightBrightnessTable()[this.getLightFromNeighbors(pos)];
   }

   @Override
   public IBlockState getBlockState(BlockPos pos) {
      if (!this.isValid(pos)) {
         return Blocks.air.getDefaultState();
      }

      Chunk chunk = this.getChunkFromBlockCoords(pos);
      return chunk.getBlockState(pos);
   }

   public boolean isDaytime() {
      return this.skylightSubtracted < 4;
   }

   public MovingObjectPosition rayTraceBlocks(Vec3 vec3, Vec3 vec32) {
      return this.rayTraceBlocks(vec3, vec32, false, false, false);
   }

   public MovingObjectPosition rayTraceBlocks(Vec3 startIn, Vec3 end, boolean stopOnLiquid) {
      return this.rayTraceBlocks(startIn, end, stopOnLiquid, false, false);
   }

   public MovingObjectPosition rayTraceBlocks(
      Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock
   ) {
      if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
         return null;
      }

      if (!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord)) {
         int i = MathHelper.floor_double(vec32.xCoord);
         int j = MathHelper.floor_double(vec32.yCoord);
         int k = MathHelper.floor_double(vec32.zCoord);
         int l = MathHelper.floor_double(vec31.xCoord);
         int m = MathHelper.floor_double(vec31.yCoord);
         int n = MathHelper.floor_double(vec31.zCoord);
         BlockPos blockPos = new BlockPos(l, m, n);
         IBlockState iBlockState = this.getBlockState(blockPos);
         Block block = iBlockState.getBlock();
         if ((!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(this, blockPos, iBlockState) != null) && block.canCollideCheck(iBlockState, stopOnLiquid)) {
            MovingObjectPosition movingObjectPosition = block.collisionRayTrace(this, blockPos, vec31, vec32);
            if (movingObjectPosition != null && !ModuleManager.noMouseIntersect.isEnabled()) {
               return movingObjectPosition;
            }
         }

         MovingObjectPosition movingObjectPosition2 = null;
         int i2 = 200;

         while (i2-- >= 0) {
            if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
               return null;
            }

            if (l == i && m == j && n == k) {
               return returnLastUncollidableBlock ? movingObjectPosition2 : null;
            }

            boolean bl = true;
            boolean bl2 = true;
            boolean bl3 = true;
            double d = 999.0;
            double e = 999.0;
            double d2 = 999.0;
            if (i > l) {
               d = l + 1.0;
            } else if (i < l) {
               d = l + 0.0;
            } else {
               bl = false;
            }

            if (j > m) {
               e = m + 1.0;
            } else if (j < m) {
               e = m + 0.0;
            } else {
               bl2 = false;
            }

            if (k > n) {
               d2 = n + 1.0;
            } else if (k < n) {
               d2 = n + 0.0;
            } else {
               bl3 = false;
            }

            double d3 = 999.0;
            double d4 = 999.0;
            double d5 = 999.0;
            double d6 = vec32.xCoord - vec31.xCoord;
            double d7 = vec32.yCoord - vec31.yCoord;
            double d8 = vec32.zCoord - vec31.zCoord;
            if (bl) {
               d3 = (d - vec31.xCoord) / d6;
            }

            if (bl2) {
               d4 = (e - vec31.yCoord) / d7;
            }

            if (bl3) {
               d5 = (d2 - vec31.zCoord) / d8;
            }

            if (d3 == -0.0) {
               d3 = -1.0E-4;
            }

            if (d4 == -0.0) {
               d4 = -1.0E-4;
            }

            if (d5 == -0.0) {
               d5 = -1.0E-4;
            }

            EnumFacing enumFacing;
            if (d3 < d4 && d3 < d5) {
               enumFacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
               vec31 = new Vec3(d, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
            } else if (d4 < d5) {
               enumFacing = j > m ? EnumFacing.DOWN : EnumFacing.UP;
               vec31 = new Vec3(vec31.xCoord + d6 * d4, e, vec31.zCoord + d8 * d4);
            } else {
               enumFacing = k > n ? EnumFacing.NORTH : EnumFacing.SOUTH;
               vec31 = new Vec3(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
            }

            l = MathHelper.floor_double(vec31.xCoord) - (enumFacing == EnumFacing.EAST ? 1 : 0);
            m = MathHelper.floor_double(vec31.yCoord) - (enumFacing == EnumFacing.UP ? 1 : 0);
            n = MathHelper.floor_double(vec31.zCoord) - (enumFacing == EnumFacing.SOUTH ? 1 : 0);
            blockPos = new BlockPos(l, m, n);
            IBlockState iBlockState2 = this.getBlockState(blockPos);
            Block block2 = iBlockState2.getBlock();
            if (!ignoreBlockWithoutBoundingBox || block2.getCollisionBoundingBox(this, blockPos, iBlockState2) != null) {
               if (block2.canCollideCheck(iBlockState2, stopOnLiquid)) {
                  MovingObjectPosition movingObjectPosition3 = block2.collisionRayTrace(this, blockPos, vec31, vec32);
                  if (movingObjectPosition3 != null) {
                     return movingObjectPosition3;
                  }
               } else {
                  movingObjectPosition2 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec31, enumFacing, blockPos);
               }
            }
         }

         return returnLastUncollidableBlock ? movingObjectPosition2 : null;
      } else {
         return null;
      }
   }

   public void playSoundAtEntity(Entity entityIn, String name, float volume, float pitch) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).playSound(name, entityIn.posX, entityIn.posY, entityIn.posZ, volume, pitch);
      }
   }

   public void playSoundToNearExcept(EntityPlayer player, String name, float volume, float pitch) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).playSoundToNearExcept(player, name, player.posX, player.posY, player.posZ, volume, pitch);
      }
   }

   public void playSoundEffect(double x, double y, double z, String soundName, float volume, float pitch) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).playSound(soundName, x, y, z, volume, pitch);
      }
   }

   public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay) {
   }

   public void playRecord(BlockPos pos, String name) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).playRecord(name, pos);
      }
   }

   public void spawnParticle(
      EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... ints
   ) {
      this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, ints);
   }

   public void spawnParticle(
      EnumParticleTypes particleType, boolean bl, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... ints
   ) {
      this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() | bl, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, ints);
   }

   private void spawnParticle(
      int particleID, boolean bl, double xCood, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... ints
   ) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).spawnParticle(particleID, bl, xCood, yCoord, zCoord, xOffset, yOffset, zOffset, ints);
      }
   }

   public boolean addWeatherEffect(Entity entityIn) {
      this.weatherEffects.add(entityIn);
      return true;
   }

   public boolean spawnEntityInWorld(Entity entityIn) {
      int i = MathHelper.floor_double(entityIn.posX / 16.0);
      int j = MathHelper.floor_double(entityIn.posZ / 16.0);
      boolean bl = entityIn.forceSpawn;
      if (entityIn instanceof EntityPlayer) {
         bl = true;
      }

      if (!bl && !this.isChunkLoaded(i, j, true)) {
         return false;
      }

      if (entityIn instanceof EntityPlayer) {
         EntityPlayer entityPlayer = (EntityPlayer)entityIn;
         this.playerEntities.add(entityPlayer);
         this.updateAllPlayersSleepingFlag();
      }

      this.getChunkFromChunkCoords(i, j).addEntity(entityIn);
      this.loadedEntityList.add(entityIn);
      this.onEntityAdded(entityIn);
      return true;
   }

   public void onEntityAdded(Entity entityIn) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).onEntityAdded(entityIn);
      }
   }

   public void onEntityRemoved(Entity entityIn) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         this.worldAccesses.get(i).onEntityRemoved(entityIn);
      }
   }

   public void removeEntity(Entity entityIn) {
      if (entityIn.riddenByEntity != null) {
         entityIn.riddenByEntity.mountEntity((Entity)null);
      }

      if (entityIn.ridingEntity != null) {
         entityIn.mountEntity((Entity)null);
      }

      entityIn.setDead();
      if (entityIn instanceof EntityPlayer) {
         this.playerEntities.remove(entityIn);
         this.updateAllPlayersSleepingFlag();
         this.onEntityRemoved(entityIn);
      }
   }

   public void removePlayerEntityDangerously(Entity entityIn) {
      entityIn.setDead();
      if (entityIn instanceof EntityPlayer) {
         this.playerEntities.remove(entityIn);
         this.updateAllPlayersSleepingFlag();
      }

      int i = entityIn.chunkCoordX;
      int j = entityIn.chunkCoordZ;
      if (entityIn.addedToChunk && this.isChunkLoaded(i, j, true)) {
         this.getChunkFromChunkCoords(i, j).removeEntity(entityIn);
      }

      this.loadedEntityList.remove(entityIn);
      this.onEntityRemoved(entityIn);
   }

   public void addWorldAccess(IWorldAccess worldAccess) {
      this.worldAccesses.add(worldAccess);
   }

   public void removeWorldAccess(IWorldAccess worldAccess) {
      this.worldAccesses.remove(worldAccess);
   }

   public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity entityIn, AxisAlignedBB bb) {
      ArrayList arrayList = Lists.newArrayList();
      int i = MathHelper.floor_double(bb.minX);
      int j = MathHelper.floor_double(bb.maxX + 1.0);
      int k = MathHelper.floor_double(bb.minY);
      int l = MathHelper.floor_double(bb.maxY + 1.0);
      int m = MathHelper.floor_double(bb.minZ);
      int n = MathHelper.floor_double(bb.maxZ + 1.0);
      WorldBorder worldBorder = this.getWorldBorder();
      boolean bl = entityIn.isOutsideBorder();
      boolean bl2 = this.isInsideBorder(worldBorder, entityIn);
      IBlockState iBlockState = Blocks.stone.getDefaultState();
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int i2 = i; i2 < j; i2++) {
         for (int i3 = m; i3 < n; i3++) {
            if (this.isBlockLoaded(mutableBlockPos.set(i2, 64, i3))) {
               for (int i4 = k - 1; i4 < l; i4++) {
                  mutableBlockPos.set(i2, i4, i3);
                  if (bl && bl2) {
                     entityIn.setOutsideBorder(false);
                  } else if (!bl && !bl2) {
                     entityIn.setOutsideBorder(true);
                  }

                  IBlockState iBlockState2 = iBlockState;
                  if (worldBorder.contains(mutableBlockPos) || !bl2) {
                     iBlockState2 = this.getBlockState(mutableBlockPos);
                  }

                  iBlockState2.getBlock().addCollisionBoxesToList(this, mutableBlockPos, iBlockState2, bb, arrayList, entityIn);
               }
            }
         }
      }

      double d = 0.25;
      List list = this.getEntitiesWithinAABBExcludingEntity(entityIn, bb.expand(d, d, d));

      for (int i5 = 0; i5 < list.size(); i5++) {
         if (entityIn.riddenByEntity != list && entityIn.ridingEntity != list) {
            AxisAlignedBB axisAlignedBB = ((Entity)list.get(i5)).getCollisionBoundingBox();
            if (axisAlignedBB != null && axisAlignedBB.intersectsWith(bb)) {
               arrayList.add(axisAlignedBB);
            }

            axisAlignedBB = entityIn.getCollisionBox((Entity)list.get(i5));
            if (axisAlignedBB != null && axisAlignedBB.intersectsWith(bb)) {
               arrayList.add(axisAlignedBB);
            }
         }
      }

      return arrayList;
   }

   public boolean isInsideBorder(WorldBorder worldBorderIn, Entity entityIn) {
      double d = worldBorderIn.minX();
      double e = worldBorderIn.minZ();
      double d2 = worldBorderIn.maxX();
      double d3 = worldBorderIn.maxZ();
      if (entityIn.isOutsideBorder()) {
         d++;
         e++;
         d2--;
         d3--;
      } else {
         d--;
         e--;
         d2++;
         d3++;
      }

      return entityIn.posX > d && entityIn.posX < d2 && entityIn.posZ > e && entityIn.posZ < d3;
   }

   public List<AxisAlignedBB> getCollisionBoxes(AxisAlignedBB bb) {
      ArrayList arrayList = Lists.newArrayList();
      int i = MathHelper.floor_double(bb.minX);
      int j = MathHelper.floor_double(bb.maxX + 1.0);
      int k = MathHelper.floor_double(bb.minY);
      int l = MathHelper.floor_double(bb.maxY + 1.0);
      int m = MathHelper.floor_double(bb.minZ);
      int n = MathHelper.floor_double(bb.maxZ + 1.0);
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int i2 = i; i2 < j; i2++) {
         for (int i3 = m; i3 < n; i3++) {
            if (this.isBlockLoaded(mutableBlockPos.set(i2, 64, i3))) {
               for (int i4 = k - 1; i4 < l; i4++) {
                  mutableBlockPos.set(i2, i4, i3);
                  IBlockState iBlockState;
                  if (i2 >= -30000000 && i2 < 30000000 && i3 >= -30000000 && i3 < 30000000) {
                     iBlockState = this.getBlockState(mutableBlockPos);
                  } else {
                     iBlockState = Blocks.bedrock.getDefaultState();
                  }

                  iBlockState.getBlock().addCollisionBoxesToList(this, mutableBlockPos, iBlockState, bb, arrayList, (Entity)null);
               }
            }
         }
      }

      return arrayList;
   }

   public int calculateSkylightSubtracted(float f) {
      float g = this.getCelestialAngle(f);
      float h = 1.0F - (MathHelper.cos(g * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
      h = MathHelper.clamp_float(h, 0.0F, 1.0F);
      h = 1.0F - h;
      h = (float)(h * (1.0 - this.getRainStrength(f) * 5.0F / 16.0));
      h = (float)(h * (1.0 - this.getThunderStrength(f) * 5.0F / 16.0));
      h = 1.0F - h;
      return (int)(h * 11.0F);
   }

   public float getSunBrightness(float f) {
      float g = this.getCelestialAngle(f);
      float h = 1.0F - (MathHelper.cos(g * (float) Math.PI * 2.0F) * 2.0F + 0.2F);
      h = MathHelper.clamp_float(h, 0.0F, 1.0F);
      h = 1.0F - h;
      h = (float)(h * (1.0 - this.getRainStrength(f) * 5.0F / 16.0));
      h = (float)(h * (1.0 - this.getThunderStrength(f) * 5.0F / 16.0));
      return h * 0.8F + 0.2F;
   }

   public Vec3 getSkyColor(Entity entityIn, float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      float g = MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      g = MathHelper.clamp_float(g, 0.0F, 1.0F);
      int i = MathHelper.floor_double(entityIn.posX);
      int j = MathHelper.floor_double(entityIn.posY);
      int k = MathHelper.floor_double(entityIn.posZ);
      BlockPos blockPos = new BlockPos(i, j, k);
      BiomeGenBase biomeGenBase = this.getBiomeGenForCoords(blockPos);
      float h = biomeGenBase.getFloatTemperature(blockPos);
      int l = biomeGenBase.getSkyColorByTemp(h);
      float f2 = (l >> 16 & 0xFF) / 255.0F;
      float f3 = (l >> 8 & 0xFF) / 255.0F;
      float f4 = (l & 0xFF) / 255.0F;
      f2 *= g;
      f3 *= g;
      f4 *= g;
      float f5 = this.getRainStrength(partialTicks);
      if (f5 > 0.0F) {
         float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         float f7 = 1.0F - f5 * 0.75F;
         f2 = f2 * f7 + f6 * (1.0F - f7);
         f3 = f3 * f7 + f6 * (1.0F - f7);
         f4 = f4 * f7 + f6 * (1.0F - f7);
      }

      float f8 = this.getThunderStrength(partialTicks);
      if (f8 > 0.0F) {
         float f9 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f10 = 1.0F - f8 * 0.75F;
         f2 = f2 * f10 + f9 * (1.0F - f10);
         f3 = f3 * f10 + f9 * (1.0F - f10);
         f4 = f4 * f10 + f9 * (1.0F - f10);
      }

      if (this.lastLightningBolt > 0) {
         float f11 = this.lastLightningBolt - partialTicks;
         if (f11 > 1.0F) {
            f11 = 1.0F;
         }

         f11 *= 0.45F;
         f2 = f2 * (1.0F - f11) + 0.8F * f11;
         f3 = f3 * (1.0F - f11) + 0.8F * f11;
         f4 = f4 * (1.0F - f11) + 1.0F * f11;
      }

      return new Vec3(f2, f3, f4);
   }

   public float getCelestialAngle(float partialTicks) {
      return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), partialTicks);
   }

   public int f() {
      return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
   }

   public float getCurrentMoonPhaseFactor() {
      return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
   }

   public float getCelestialAngleRadians(float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      return f * (float) Math.PI * 2.0F;
   }

   public Vec3 getCloudColour(float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      float g = MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      g = MathHelper.clamp_float(g, 0.0F, 1.0F);
      float h = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
      float f2 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
      float f3 = (float)(this.cloudColour & 255L) / 255.0F;
      float f4 = this.getRainStrength(partialTicks);
      if (f4 > 0.0F) {
         float f5 = (h * 0.3F + f2 * 0.59F + f3 * 0.11F) * 0.6F;
         float f6 = 1.0F - f4 * 0.95F;
         h = h * f6 + f5 * (1.0F - f6);
         f2 = f2 * f6 + f5 * (1.0F - f6);
         f3 = f3 * f6 + f5 * (1.0F - f6);
      }

      h *= g * 0.9F + 0.1F;
      f2 *= g * 0.9F + 0.1F;
      f3 *= g * 0.85F + 0.15F;
      float f7 = this.getThunderStrength(partialTicks);
      if (f7 > 0.0F) {
         float f8 = (h * 0.3F + f2 * 0.59F + f3 * 0.11F) * 0.2F;
         float f9 = 1.0F - f7 * 0.95F;
         h = h * f9 + f8 * (1.0F - f9);
         f2 = f2 * f9 + f8 * (1.0F - f9);
         f3 = f3 * f9 + f8 * (1.0F - f9);
      }

      return new Vec3(h, f2, f3);
   }

   public Vec3 getFogColor(float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      return this.provider.getFogColor(f, partialTicks);
   }

   public BlockPos getPrecipitationHeight(BlockPos pos) {
      return this.getChunkFromBlockCoords(pos).getPrecipitationHeight(pos);
   }

   public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
      Chunk chunk = this.getChunkFromBlockCoords(pos);
      BlockPos blockPos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ());

      while (blockPos.getY() >= 0) {
         BlockPos blockPos2 = blockPos.down();
         Material material = chunk.getBlock(blockPos2).getMaterial();
         if (material.blocksMovement() && material != Material.leaves) {
            break;
         }

         blockPos = blockPos2;
      }

      return blockPos;
   }

   public float getStarBrightness(float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      float g = 1.0F - (MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
      g = MathHelper.clamp_float(g, 0.0F, 1.0F);
      return g * g * 0.5F;
   }

   public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
   }

   public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority) {
   }

   public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority) {
   }

   public void updateEntities() {
      this.theProfiler.startSection("entities");
      this.theProfiler.startSection("global");

      for (int i = 0; i < this.weatherEffects.size(); i++) {
         Entity entity2 = this.weatherEffects.get(i);

         try {
            entity2.ticksExisted++;
            entity2.onUpdate();
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Ticking entity");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being ticked");
            if (entity2 == null) {
               crashReportCategory.addCrashSection("Entity", "~~NULL~~");
            } else {
               entity2.addEntityCrashInfo(crashReportCategory);
            }

            throw new ReportedException(crashReport);
         }

         if (entity2.isDead) {
            this.weatherEffects.remove(i--);
         }
      }

      this.theProfiler.endStartSection("remove");
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for (int j = 0; j < this.unloadedEntityList.size(); j++) {
         Entity entity3 = this.unloadedEntityList.get(j);
         int k = entity3.chunkCoordX;
         int l = entity3.chunkCoordZ;
         if (entity3.addedToChunk && this.isChunkLoaded(k, l, true)) {
            this.getChunkFromChunkCoords(k, l).removeEntity(entity3);
         }
      }

      for (int m = 0; m < this.unloadedEntityList.size(); m++) {
         this.onEntityRemoved(this.unloadedEntityList.get(m));
      }

      this.unloadedEntityList.clear();
      this.theProfiler.endStartSection("regular");

      for (int n = 0; n < this.loadedEntityList.size(); n++) {
         Entity entity4 = this.loadedEntityList.get(n);
         if (entity4.ridingEntity != null) {
            if (!entity4.ridingEntity.isDead && entity4.ridingEntity.riddenByEntity == entity4) {
               continue;
            }

            entity4.ridingEntity.riddenByEntity = null;
            entity4.ridingEntity = null;
         }

         this.theProfiler.startSection("tick");
         if (!entity4.isDead) {
            try {
               this.updateEntity(entity4);
            } catch (Throwable throwable2) {
               CrashReport crashReport2 = CrashReport.makeCrashReport(throwable2, "Ticking entity");
               CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("Entity being ticked");
               entity4.addEntityCrashInfo(crashReportCategory2);
               throw new ReportedException(crashReport2);
            }
         }

         this.theProfiler.endSection();
         this.theProfiler.startSection("remove");
         if (entity4.isDead) {
            int i2 = entity4.chunkCoordX;
            int i3 = entity4.chunkCoordZ;
            if (entity4.addedToChunk && this.isChunkLoaded(i2, i3, true)) {
               this.getChunkFromChunkCoords(i2, i3).removeEntity(entity4);
            }

            this.loadedEntityList.remove(n--);
            this.onEntityRemoved(entity4);
         }

         this.theProfiler.endSection();
      }

      this.theProfiler.endStartSection("blockEntities");
      this.processingLoadedTiles = true;
      Iterator iterator2 = this.tickableTileEntities.iterator();

      while (iterator2.hasNext()) {
         TileEntity tileEntity = (TileEntity)iterator2.next();
         if (!tileEntity.isInvalid() && tileEntity.hasWorldObj()) {
            BlockPos blockPos = tileEntity.getPos();
            if (this.isBlockLoaded(blockPos) && this.worldBorder.contains(blockPos)) {
               try {
                  ((ITickable)tileEntity).update();
               } catch (Throwable throwable3) {
                  CrashReport crashReport3 = CrashReport.makeCrashReport(throwable3, "Ticking block entity");
                  CrashReportCategory crashReportCategory3 = crashReport3.makeCategory("Block entity being ticked");
                  tileEntity.addInfoToCrashReport(crashReportCategory3);
                  throw new ReportedException(crashReport3);
               }
            }
         }

         if (tileEntity.isInvalid()) {
            iterator2.remove();
            this.loadedTileEntityList.remove(tileEntity);
            if (this.isBlockLoaded(tileEntity.getPos())) {
               this.getChunkFromBlockCoords(tileEntity.getPos()).removeTileEntity(tileEntity.getPos());
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
         for (int i4 = 0; i4 < this.addedTileEntityList.size(); i4++) {
            TileEntity tileEntity2 = this.addedTileEntityList.get(i4);
            if (!tileEntity2.isInvalid()) {
               if (!this.loadedTileEntityList.contains(tileEntity2)) {
                  this.addTileEntity(tileEntity2);
               }

               if (this.isBlockLoaded(tileEntity2.getPos())) {
                  this.getChunkFromBlockCoords(tileEntity2.getPos()).addTileEntity(tileEntity2.getPos(), tileEntity2);
               }

               this.markBlockForUpdate(tileEntity2.getPos());
            }
         }

         this.addedTileEntityList.clear();
      }

      this.theProfiler.endSection();
      this.theProfiler.endSection();
   }

   public boolean addTileEntity(TileEntity tile) {
      boolean bl = this.loadedTileEntityList.add(tile);
      if (bl && tile instanceof ITickable) {
         this.tickableTileEntities.add(tile);
      }

      return bl;
   }

   public void addTileEntities(Collection<TileEntity> tileEntityCollection) {
      if (this.processingLoadedTiles) {
         this.addedTileEntityList.addAll(tileEntityCollection);
      } else {
         for (TileEntity tileEntity : tileEntityCollection) {
            this.loadedTileEntityList.add(tileEntity);
            if (tileEntity instanceof ITickable) {
               this.tickableTileEntities.add(tileEntity);
            }
         }
      }
   }

   public void updateEntity(Entity ent) {
      this.updateEntityWithOptionalForce(ent, true);
   }

   public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
      int i = MathHelper.floor_double(entityIn.posX);
      int j = MathHelper.floor_double(entityIn.posZ);
      byte b = 32;
      if (!forceUpdate || this.isAreaLoaded(i - b, 0, j - b, i + b, 0, j + b, true)) {
         entityIn.lastTickPosX = entityIn.posX;
         entityIn.lastTickPosY = entityIn.posY;
         entityIn.lastTickPosZ = entityIn.posZ;
         entityIn.prevRotationYaw = entityIn.rotationYaw;
         entityIn.prevRotationPitch = entityIn.rotationPitch;
         if (forceUpdate && entityIn.addedToChunk) {
            entityIn.ticksExisted++;
            if (entityIn.ridingEntity != null) {
               entityIn.updateRidden();
            } else {
               entityIn.onUpdate();
            }
         }

         this.theProfiler.startSection("chunkCheck");
         if (Double.isNaN(entityIn.posX) || Double.isInfinite(entityIn.posX)) {
            entityIn.posX = entityIn.lastTickPosX;
         }

         if (Double.isNaN(entityIn.posY) || Double.isInfinite(entityIn.posY)) {
            entityIn.posY = entityIn.lastTickPosY;
         }

         if (Double.isNaN(entityIn.posZ) || Double.isInfinite(entityIn.posZ)) {
            entityIn.posZ = entityIn.lastTickPosZ;
         }

         if (Double.isNaN(entityIn.rotationPitch) || Double.isInfinite(entityIn.rotationPitch)) {
            entityIn.rotationPitch = entityIn.prevRotationPitch;
         }

         if (Double.isNaN(entityIn.rotationYaw) || Double.isInfinite(entityIn.rotationYaw)) {
            entityIn.rotationYaw = entityIn.prevRotationYaw;
         }

         int k = MathHelper.floor_double(entityIn.posX / 16.0);
         int l = MathHelper.floor_double(entityIn.posY / 16.0);
         int m = MathHelper.floor_double(entityIn.posZ / 16.0);
         if (!entityIn.addedToChunk || entityIn.chunkCoordX != k || entityIn.chunkCoordY != l || entityIn.chunkCoordZ != m) {
            if (entityIn.addedToChunk && this.isChunkLoaded(entityIn.chunkCoordX, entityIn.chunkCoordZ, true)) {
               this.getChunkFromChunkCoords(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
            }

            if (this.isChunkLoaded(k, m, true)) {
               entityIn.addedToChunk = true;
               this.getChunkFromChunkCoords(k, m).addEntity(entityIn);
            } else {
               entityIn.addedToChunk = false;
            }
         }

         this.theProfiler.endSection();
         if (forceUpdate && entityIn.addedToChunk && entityIn.riddenByEntity != null) {
            if (!entityIn.riddenByEntity.isDead && entityIn.riddenByEntity.ridingEntity == entityIn) {
               this.updateEntity(entityIn.riddenByEntity);
            } else {
               entityIn.riddenByEntity.ridingEntity = null;
               entityIn.riddenByEntity = null;
            }
         }
      }
   }

   public boolean checkNoEntityCollision(AxisAlignedBB bb) {
      return this.checkNoEntityCollision(bb, (Entity)null);
   }

   public boolean checkNoEntityCollision(AxisAlignedBB bb, Entity entityIn) {
      List list = this.getEntitiesWithinAABBExcludingEntity((Entity)null, bb);

      for (int i = 0; i < list.size(); i++) {
         Entity entity = (Entity)list.get(i);
         if (!entity.isDead
            && entity.preventEntitySpawning
            && entity != entityIn
            && (entityIn == null || entityIn.ridingEntity != entity && entityIn.riddenByEntity != entity)) {
            return false;
         }
      }

      return true;
   }

   public boolean X(AxisAlignedBB axisAlignedBB) {
      int i = MathHelper.floor_double(axisAlignedBB.minX);
      int j = MathHelper.floor_double(axisAlignedBB.maxX);
      int k = MathHelper.floor_double(axisAlignedBB.minY);
      int l = MathHelper.floor_double(axisAlignedBB.maxY);
      int m = MathHelper.floor_double(axisAlignedBB.minZ);
      int n = MathHelper.floor_double(axisAlignedBB.maxZ);
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int i2 = i; i2 <= j; i2++) {
         for (int i3 = k; i3 <= l; i3++) {
            for (int i4 = m; i4 <= n; i4++) {
               Block block = this.getBlockState(mutableBlockPos.set(i2, i3, i4)).getBlock();
               if (block.getMaterial() != Material.air) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isAnyLiquid(AxisAlignedBB bb) {
      int i = MathHelper.floor_double(bb.minX);
      int j = MathHelper.floor_double(bb.maxX);
      int k = MathHelper.floor_double(bb.minY);
      int l = MathHelper.floor_double(bb.maxY);
      int m = MathHelper.floor_double(bb.minZ);
      int n = MathHelper.floor_double(bb.maxZ);
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int i2 = i; i2 <= j; i2++) {
         for (int i3 = k; i3 <= l; i3++) {
            for (int i4 = m; i4 <= n; i4++) {
               Block block = this.getBlockState(mutableBlockPos.set(i2, i3, i4)).getBlock();
               if (block.getMaterial().isLiquid()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean a(AxisAlignedBB axisAlignedBB) {
      int i = MathHelper.floor_double(axisAlignedBB.minX);
      int j = MathHelper.floor_double(axisAlignedBB.maxX + 1.0);
      int k = MathHelper.floor_double(axisAlignedBB.minY);
      int l = MathHelper.floor_double(axisAlignedBB.maxY + 1.0);
      int m = MathHelper.floor_double(axisAlignedBB.minZ);
      int n = MathHelper.floor_double(axisAlignedBB.maxZ + 1.0);
      if (this.isAreaLoaded(i, k, m, j, l, n, true)) {
         BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

         for (int i2 = i; i2 < j; i2++) {
            for (int i3 = k; i3 < l; i3++) {
               for (int i4 = m; i4 < n; i4++) {
                  Block block = this.getBlockState(mutableBlockPos.set(i2, i3, i4)).getBlock();
                  if (block == Blocks.fire || block == Blocks.flowing_lava || block == Blocks.lava) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean handleMaterialAcceleration(AxisAlignedBB bb, Material materialIn, Entity entityIn) {
      int i = MathHelper.floor_double(bb.minX);
      int j = MathHelper.floor_double(bb.maxX + 1.0);
      int k = MathHelper.floor_double(bb.minY);
      int l = MathHelper.floor_double(bb.maxY + 1.0);
      int m = MathHelper.floor_double(bb.minZ);
      int n = MathHelper.floor_double(bb.maxZ + 1.0);
      if (!this.isAreaLoaded(i, k, m, j, l, n, true)) {
         return false;
      }

      boolean bl = false;
      Vec3 vec3 = new Vec3(0.0, 0.0, 0.0);
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int i2 = i; i2 < j; i2++) {
         for (int i3 = k; i3 < l; i3++) {
            for (int i4 = m; i4 < n; i4++) {
               mutableBlockPos.set(i2, i3, i4);
               IBlockState iBlockState = this.getBlockState(mutableBlockPos);
               Block block = iBlockState.getBlock();
               if (block.getMaterial() == materialIn) {
                  double d = i3 + 1 - BlockLiquid.getLiquidHeightPercent(iBlockState.getValue(BlockLiquid.LEVEL));
                  if (l >= d) {
                     bl = true;
                     vec3 = block.modifyAcceleration(this, mutableBlockPos, entityIn, vec3);
                  }
               }
            }
         }
      }

      if (vec3.lengthVector() > 0.0 && entityIn.isPushedByWater()) {
         vec3 = vec3.normalize();
         double e = 0.014;
         entityIn.motionX = entityIn.motionX + vec3.xCoord * e;
         entityIn.motionY = entityIn.motionY + vec3.yCoord * e;
         entityIn.motionZ = entityIn.motionZ + vec3.zCoord * e;
      }

      return bl;
   }

   public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn) {
      int i = MathHelper.floor_double(bb.minX);
      int j = MathHelper.floor_double(bb.maxX + 1.0);
      int k = MathHelper.floor_double(bb.minY);
      int l = MathHelper.floor_double(bb.maxY + 1.0);
      int m = MathHelper.floor_double(bb.minZ);
      int n = MathHelper.floor_double(bb.maxZ + 1.0);
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int i2 = i; i2 < j; i2++) {
         for (int i3 = k; i3 < l; i3++) {
            for (int i4 = m; i4 < n; i4++) {
               if (this.getBlockState(mutableBlockPos.set(i2, i3, i4)).getBlock().getMaterial() == materialIn) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isAABBInMaterial(AxisAlignedBB bb, Material materialIn) {
      int i = MathHelper.floor_double(bb.minX);
      int j = MathHelper.floor_double(bb.maxX + 1.0);
      int k = MathHelper.floor_double(bb.minY);
      int l = MathHelper.floor_double(bb.maxY + 1.0);
      int m = MathHelper.floor_double(bb.minZ);
      int n = MathHelper.floor_double(bb.maxZ + 1.0);
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int i2 = i; i2 < j; i2++) {
         for (int i3 = k; i3 < l; i3++) {
            for (int i4 = m; i4 < n; i4++) {
               IBlockState iBlockState = this.getBlockState(mutableBlockPos.set(i2, i3, i4));
               Block block = iBlockState.getBlock();
               if (block.getMaterial() == materialIn) {
                  int i5 = iBlockState.getValue(BlockLiquid.LEVEL);
                  double d = i3 + 1;
                  if (i5 < 8) {
                     d = i3 + 1 - i5 / 8.0;
                  }

                  if (d >= bb.minY) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public Explosion createExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isSmoking) {
      return this.newExplosion(entityIn, x, y, z, strength, false, isSmoking);
   }

   public Explosion newExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
      Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, isFlaming, isSmoking);
      explosion.doExplosionA();
      explosion.doExplosionB(true);
      return explosion;
   }

   public float getBlockDensity(Vec3 vec, AxisAlignedBB bb) {
      double d = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
      double e = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
      double d2 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
      double d3 = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
      double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
      if (d >= 0.0 && e >= 0.0 && d2 >= 0.0) {
         int i = 0;
         int j = 0;

         for (float f = 0.0F; f <= 1.0F; f = (float)(f + d)) {
            for (float g = 0.0F; g <= 1.0F; g = (float)(g + e)) {
               for (float h = 0.0F; h <= 1.0F; h = (float)(h + d2)) {
                  double d5 = bb.minX + (bb.maxX - bb.minX) * f;
                  double d6 = bb.minY + (bb.maxY - bb.minY) * g;
                  double d7 = bb.minZ + (bb.maxZ - bb.minZ) * h;
                  if (this.rayTraceBlocks(new Vec3(d5 + d3, d6, d7 + d4), vec) == null) {
                     i++;
                  }

                  j++;
               }
            }
         }

         return (float)i / j;
      } else {
         return 0.0F;
      }
   }

   public boolean extinguishFire(EntityPlayer player, BlockPos pos, EnumFacing side) {
      pos = pos.offset(side);
      if (this.getBlockState(pos).getBlock() == Blocks.fire) {
         this.playAuxSFXAtEntity(player, 1004, pos, 0);
         this.setBlockToAir(pos);
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
   public TileEntity getTileEntity(BlockPos pos) {
      if (!this.isValid(pos)) {
         return null;
      }

      TileEntity tileEntity = null;
      if (this.processingLoadedTiles) {
         for (int i = 0; i < this.addedTileEntityList.size(); i++) {
            TileEntity tileEntity2 = this.addedTileEntityList.get(i);
            if (!tileEntity2.isInvalid() && tileEntity2.getPos().equals(pos)) {
               tileEntity = tileEntity2;
               break;
            }
         }
      }

      if (tileEntity == null) {
         tileEntity = this.getChunkFromBlockCoords(pos).getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
      }

      if (tileEntity == null) {
         for (int j = 0; j < this.addedTileEntityList.size(); j++) {
            TileEntity tileEntity3 = this.addedTileEntityList.get(j);
            if (!tileEntity3.isInvalid() && tileEntity3.getPos().equals(pos)) {
               tileEntity = tileEntity3;
               break;
            }
         }
      }

      return tileEntity;
   }

   public void setTileEntity(BlockPos pos, TileEntity tileEntityIn) {
      if (tileEntityIn != null && !tileEntityIn.isInvalid()) {
         if (this.processingLoadedTiles) {
            tileEntityIn.setPos(pos);
            Iterator iterator2 = this.addedTileEntityList.iterator();

            while (iterator2.hasNext()) {
               TileEntity tileEntity = (TileEntity)iterator2.next();
               if (tileEntity.getPos().equals(pos)) {
                  tileEntity.invalidate();
                  iterator2.remove();
               }
            }

            this.addedTileEntityList.add(tileEntityIn);
         } else {
            this.addTileEntity(tileEntityIn);
            this.getChunkFromBlockCoords(pos).addTileEntity(pos, tileEntityIn);
         }
      }
   }

   public void removeTileEntity(BlockPos pos) {
      TileEntity tileEntity = this.getTileEntity(pos);
      if (tileEntity != null && this.processingLoadedTiles) {
         tileEntity.invalidate();
         this.addedTileEntityList.remove(tileEntity);
      } else {
         if (tileEntity != null) {
            this.addedTileEntityList.remove(tileEntity);
            this.loadedTileEntityList.remove(tileEntity);
            this.tickableTileEntities.remove(tileEntity);
         }

         this.getChunkFromBlockCoords(pos).removeTileEntity(pos);
      }
   }

   public void markTileEntityForRemoval(TileEntity tileEntityIn) {
      this.tileEntitiesToBeRemoved.add(tileEntityIn);
   }

   public boolean isBlockFullCube(BlockPos pos) {
      IBlockState iBlockState = this.getBlockState(pos);
      AxisAlignedBB axisAlignedBB = iBlockState.getBlock().getCollisionBoundingBox(this, pos, iBlockState);
      return axisAlignedBB != null && axisAlignedBB.getAverageEdgeLength() >= 1.0;
   }

   public static boolean doesBlockHaveSolidTopSurface(IBlockAccess blockAccess, BlockPos pos) {
      IBlockState iBlockState = blockAccess.getBlockState(pos);
      Block block = iBlockState.getBlock();
      return block.getMaterial().isOpaque() && block.isFullCube()
         ? true
         : (
            block instanceof BlockStairs
               ? iBlockState.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP
               : (
                  block instanceof BlockSlab
                     ? iBlockState.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP
                     : (block instanceof BlockHopper ? true : (block instanceof BlockSnow ? iBlockState.getValue(BlockSnow.LAYERS) == 7 : false))
               )
         );
   }

   public boolean isBlockNormalCube(BlockPos pos, boolean _default) {
      if (!this.isValid(pos)) {
         return _default;
      }

      Chunk chunk = this.chunkProvider.provideChunk(pos);
      if (chunk.isEmpty()) {
         return _default;
      }

      Block block = this.getBlockState(pos).getBlock();
      return block.getMaterial().isOpaque() && block.isFullCube();
   }

   public void a() {
      int i = this.calculateSkylightSubtracted(1.0F);
      if (i != this.skylightSubtracted) {
         this.skylightSubtracted = i;
      }
   }

   public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
      this.spawnHostileMobs = hostile;
      this.spawnPeacefulMobs = peaceful;
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
         int i = this.worldInfo.getCleanWeatherTime();
         if (i > 0) {
            this.worldInfo.setCleanWeatherTime(--i);
            this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
            this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
         }

         int j = this.worldInfo.getThunderTime();
         if (j <= 0) {
            if (this.worldInfo.isThundering()) {
               this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
            } else {
               this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
            }
         } else {
            this.worldInfo.setThunderTime(--j);
            if (j <= 0) {
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
         int k = this.worldInfo.getRainTime();
         if (k <= 0) {
            if (this.worldInfo.isRaining()) {
               this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
            } else {
               this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
            }
         } else {
            this.worldInfo.setRainTime(--k);
            if (k <= 0) {
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

      for (int i = 0; i < this.playerEntities.size(); i++) {
         EntityPlayer entityPlayer = this.playerEntities.get(i);
         int j = MathHelper.floor_double(entityPlayer.posX / 16.0);
         int k = MathHelper.floor_double(entityPlayer.posZ / 16.0);
         int l = this.getRenderDistanceChunks();

         for (int m = -l; m <= l; m++) {
            for (int n = -l; n <= l; n++) {
               this.activeChunkSet.add(new ChunkCoordIntPair(m + j, n + k));
            }
         }
      }

      this.theProfiler.endSection();
      if (this.ambientTickCountdown > 0) {
         this.ambientTickCountdown--;
      }

      this.theProfiler.startSection("playerCheckLight");
      if (!this.playerEntities.isEmpty()) {
         int i2 = this.rand.nextInt(this.playerEntities.size());
         EntityPlayer entityPlayer2 = this.playerEntities.get(i2);
         int i3 = MathHelper.floor_double(entityPlayer2.posX) + this.rand.nextInt(11) - 5;
         int i4 = MathHelper.floor_double(entityPlayer2.posY) + this.rand.nextInt(11) - 5;
         int i5 = MathHelper.floor_double(entityPlayer2.posZ) + this.rand.nextInt(11) - 5;
         this.checkLight(new BlockPos(i3, i4, i5));
      }

      this.theProfiler.endSection();
   }

   public abstract int getRenderDistanceChunks();

   public void playMoodSoundAndCheckLight(int i, int j, Chunk chunkIn) {
      this.theProfiler.endStartSection("moodSound");
      if (this.ambientTickCountdown == 0 && !this.isRemote) {
         this.updateLCG = this.updateLCG * 3 + 1013904223;
         int k = this.updateLCG >> 2;
         int l = k & 15;
         int m = k >> 8 & 15;
         int n = k >> 16 & 0xFF;
         BlockPos blockPos = new BlockPos(l, n, m);
         Block block = chunkIn.getBlock(blockPos);
         l += i;
         m += j;
         if (block.getMaterial() == Material.air && this.getLight(blockPos) <= this.rand.nextInt(8) && this.getLightFor(EnumSkyBlock.SKY, blockPos) <= 0) {
            EntityPlayer entityPlayer = this.getClosestPlayer(l + 0.5, n + 0.5, m + 0.5, 8.0);
            if (entityPlayer != null && entityPlayer.getDistanceSq(l + 0.5, n + 0.5, m + 0.5) > 4.0) {
               this.playSoundEffect(l + 0.5, n + 0.5, m + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
               this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
            }
         }
      }

      this.theProfiler.endStartSection("checkLight");
      chunkIn.enqueueRelightChecks();
   }

   public void updateBlocks() {
      this.setActivePlayerChunksAndCheckLight();
   }

   public void forceBlockUpdateTick(Block blockType, BlockPos pos, Random randomIn) {
      this.scheduledUpdatesAreImmediate = true;
      blockType.updateTick(this, pos, this.getBlockState(pos), randomIn);
      this.scheduledUpdatesAreImmediate = false;
   }

   public boolean canBlockFreezeWater(BlockPos pos) {
      return this.canBlockFreeze(pos, false);
   }

   public boolean canBlockFreezeNoWater(BlockPos pos) {
      return this.canBlockFreeze(pos, true);
   }

   public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj) {
      BiomeGenBase biomeGenBase = this.getBiomeGenForCoords(pos);
      float f = biomeGenBase.getFloatTemperature(pos);
      if (f > 0.15F) {
         return false;
      }

      if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
         IBlockState iBlockState = this.getBlockState(pos);
         Block block = iBlockState.getBlock();
         if ((block == Blocks.water || block == Blocks.flowing_water) && iBlockState.getValue(BlockLiquid.LEVEL) == 0) {
            if (!noWaterAdj) {
               return true;
            }

            boolean bl = this.isWater(pos.west()) && this.isWater(pos.east()) && this.isWater(pos.north()) && this.isWater(pos.south());
            if (!bl) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isWater(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getMaterial() == Material.water;
   }

   public boolean canSnowAt(BlockPos pos, boolean checkLight) {
      BiomeGenBase biomeGenBase = this.getBiomeGenForCoords(pos);
      float f = biomeGenBase.getFloatTemperature(pos);
      if (f > 0.15F) {
         return false;
      }

      if (!checkLight) {
         return true;
      }

      if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
         Block block = this.getBlockState(pos).getBlock();
         if (block.getMaterial() == Material.air && Blocks.snow_layer.canPlaceBlockAt(this, pos)) {
            return true;
         }
      }

      return false;
   }

   public boolean checkLight(BlockPos pos) {
      boolean bl = false;
      if (!this.provider.getHasNoSky()) {
         bl |= this.checkLightFor(EnumSkyBlock.SKY, pos);
      }

      return bl | this.checkLightFor(EnumSkyBlock.BLOCK, pos);
   }

   private int getRawLight(BlockPos pos, EnumSkyBlock lightType) {
      if (lightType == EnumSkyBlock.SKY && this.canSeeSky(pos)) {
         return 15;
      }

      Block block = this.getBlockState(pos).getBlock();
      int i = lightType == EnumSkyBlock.SKY ? 0 : block.getLightValue();
      int j = block.getLightOpacity();
      if (j >= 15 && block.getLightValue() > 0) {
         j = 1;
      }

      if (j < 1) {
         j = 1;
      }

      if (j >= 15) {
         return 0;
      }

      if (i >= 14) {
         return i;
      }

      for (EnumFacing enumFacing : EnumFacing.values()) {
         BlockPos blockPos = pos.offset(enumFacing);
         int k = this.getLightFor(lightType, blockPos) - j;
         if (k > i) {
            i = k;
         }

         if (i >= 14) {
            return i;
         }
      }

      return i;
   }

   public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
      if (!this.isAreaLoaded(pos, 17, false)) {
         return false;
      }

      int i = 0;
      int j = 0;
      this.theProfiler.startSection("getBrightness");
      int k = this.getLightFor(lightType, pos);
      int l = this.getRawLight(pos, lightType);
      int m = pos.getX();
      int n = pos.getY();
      int i2 = pos.getZ();
      if (l > k) {
         this.lightUpdateBlockList[j++] = 133152;
      } else if (l < k) {
         this.lightUpdateBlockList[j++] = 133152 | k << 18;

         while (i < j) {
            int i3 = this.lightUpdateBlockList[i++];
            int i4 = (i3 & 63) - 32 + m;
            int i5 = (i3 >> 6 & 63) - 32 + n;
            int i6 = (i3 >> 12 & 63) - 32 + i2;
            int i7 = i3 >> 18 & 15;
            BlockPos blockPos = new BlockPos(i4, i5, i6);
            int i8 = this.getLightFor(lightType, blockPos);
            if (i8 == i7) {
               this.setLightFor(lightType, blockPos, 0);
               if (i7 > 0) {
                  int i9 = MathHelper.abs_int(i4 - m);
                  int i10 = MathHelper.abs_int(i5 - n);
                  int i11 = MathHelper.abs_int(i6 - i2);
                  if (i9 + i10 + i11 < 17) {
                     BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

                     for (EnumFacing enumFacing : EnumFacing.values()) {
                        int i12 = i4 + enumFacing.getFrontOffsetX();
                        int i13 = i5 + enumFacing.getFrontOffsetY();
                        int i14 = i6 + enumFacing.getFrontOffsetZ();
                        mutableBlockPos.set(i12, i13, i14);
                        int i15 = Math.max(1, this.getBlockState(mutableBlockPos).getBlock().getLightOpacity());
                        i8 = this.getLightFor(lightType, mutableBlockPos);
                        if (i8 == i7 - i15 && j < this.lightUpdateBlockList.length) {
                           this.lightUpdateBlockList[j++] = i12 - m + 32 | i13 - n + 32 << 6 | i14 - i2 + 32 << 12 | i7 - i15 << 18;
                        }
                     }
                  }
               }
            }
         }

         i = 0;
      }

      this.theProfiler.endSection();
      this.theProfiler.startSection("checkedPosition < toCheckCount");

      while (i < j) {
         int i16 = this.lightUpdateBlockList[i++];
         int i17 = (i16 & 63) - 32 + m;
         int i18 = (i16 >> 6 & 63) - 32 + n;
         int i19 = (i16 >> 12 & 63) - 32 + i2;
         BlockPos blockPos2 = new BlockPos(i17, i18, i19);
         int i20 = this.getLightFor(lightType, blockPos2);
         int i21 = this.getRawLight(blockPos2, lightType);
         if (i21 != i20) {
            this.setLightFor(lightType, blockPos2, i21);
            if (i21 > i20) {
               int i22 = Math.abs(i17 - m);
               int i23 = Math.abs(i18 - n);
               int i24 = Math.abs(i19 - i2);
               boolean bl = j < this.lightUpdateBlockList.length - 6;
               if (i22 + i23 + i24 < 17 && bl) {
                  if (this.getLightFor(lightType, blockPos2.west()) < i21) {
                     this.lightUpdateBlockList[j++] = i17 - 1 - m + 32 + (i18 - n + 32 << 6) + (i19 - i2 + 32 << 12);
                  }

                  if (this.getLightFor(lightType, blockPos2.east()) < i21) {
                     this.lightUpdateBlockList[j++] = i17 + 1 - m + 32 + (i18 - n + 32 << 6) + (i19 - i2 + 32 << 12);
                  }

                  if (this.getLightFor(lightType, blockPos2.down()) < i21) {
                     this.lightUpdateBlockList[j++] = i17 - m + 32 + (i18 - 1 - n + 32 << 6) + (i19 - i2 + 32 << 12);
                  }

                  if (this.getLightFor(lightType, blockPos2.up()) < i21) {
                     this.lightUpdateBlockList[j++] = i17 - m + 32 + (i18 + 1 - n + 32 << 6) + (i19 - i2 + 32 << 12);
                  }

                  if (this.getLightFor(lightType, blockPos2.north()) < i21) {
                     this.lightUpdateBlockList[j++] = i17 - m + 32 + (i18 - n + 32 << 6) + (i19 - 1 - i2 + 32 << 12);
                  }

                  if (this.getLightFor(lightType, blockPos2.south()) < i21) {
                     this.lightUpdateBlockList[j++] = i17 - m + 32 + (i18 - n + 32 << 6) + (i19 + 1 - i2 + 32 << 12);
                  }
               }
            }
         }
      }

      this.theProfiler.endSection();
      return true;
   }

   public boolean tickUpdates(boolean bl) {
      return false;
   }

   public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean bl) {
      return null;
   }

   public List<NextTickListEntry> func_175712_a(StructureBoundingBox structureBB, boolean bl) {
      return null;
   }

   public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entityIn, AxisAlignedBB bb) {
      return this.getEntitiesInAABBexcluding(entityIn, bb, EntitySelectors.NOT_SPECTATING);
   }

   public List<Entity> getEntitiesInAABBexcluding(Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
      ArrayList arrayList = Lists.newArrayList();
      int i = MathHelper.floor_double((boundingBox.minX - 2.0) / 16.0);
      int j = MathHelper.floor_double((boundingBox.maxX + 2.0) / 16.0);
      int k = MathHelper.floor_double((boundingBox.minZ - 2.0) / 16.0);
      int l = MathHelper.floor_double((boundingBox.maxZ + 2.0) / 16.0);

      for (int m = i; m <= j; m++) {
         for (int n = k; n <= l; n++) {
            if (this.isChunkLoaded(m, n, true)) {
               this.getChunkFromChunkCoords(m, n).getEntitiesWithinAABBForEntity(entityIn, boundingBox, arrayList, predicate);
            }
         }
      }

      return arrayList;
   }

   public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, Predicate<? super T> filter) {
      ArrayList arrayList = Lists.newArrayList();

      for (Entity entity : this.loadedEntityList) {
         if (entityType.isAssignableFrom(entity.getClass()) && filter.apply((T)(entity))) {
            arrayList.add(entity);
         }
      }

      return arrayList;
   }

   public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, Predicate<? super T> filter) {
      ArrayList arrayList = Lists.newArrayList();

      for (Entity entity : this.playerEntities) {
         if (playerType.isAssignableFrom(entity.getClass()) && filter.apply((T)(entity))) {
            arrayList.add(entity);
         }
      }

      return arrayList;
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> classEntity, AxisAlignedBB bb) {
      return this.getEntitiesWithinAABB(classEntity, bb, EntitySelectors.NOT_SPECTATING);
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, Predicate<? super T> filter) {
      int i = MathHelper.floor_double((aabb.minX - 2.0) / 16.0);
      int j = MathHelper.floor_double((aabb.maxX + 2.0) / 16.0);
      int k = MathHelper.floor_double((aabb.minZ - 2.0) / 16.0);
      int l = MathHelper.floor_double((aabb.maxZ + 2.0) / 16.0);
      ArrayList arrayList = Lists.newArrayList();

      for (int m = i; m <= j; m++) {
         for (int n = k; n <= l; n++) {
            if (this.isChunkLoaded(m, n, true)) {
               this.getChunkFromChunkCoords(m, n).getEntitiesOfTypeWithinAAAB(clazz, aabb, arrayList, filter);
            }
         }
      }

      return arrayList;
   }

   public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo) {
      List list = this.getEntitiesWithinAABB(entityType, aabb);
      Entity entity = null;
      double d = Double.MAX_VALUE;

      for (int i = 0; i < list.size(); i++) {
         Entity entity2 = (Entity)list.get(i);
         if (entity2 != closestTo && EntitySelectors.NOT_SPECTATING.apply(entity2)) {
            double e = closestTo.getDistanceSqToEntity(entity2);
            if (e <= d) {
               entity = entity2;
               d = e;
            }
         }
      }

      return (T)entity;
   }

   public Entity getEntityByID(int id) {
      return this.entitiesById.lookup(id);
   }

   public List<Entity> getLoadedEntityList() {
      return this.loadedEntityList;
   }

   public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
      if (this.isBlockLoaded(pos)) {
         this.getChunkFromBlockCoords(pos).setChunkModified();
      }
   }

   public int countEntities(Class<?> entityType) {
      int i = 0;

      for (Entity entity : this.loadedEntityList) {
         if ((!(entity instanceof EntityLiving) || !((EntityLiving)entity).isNoDespawnRequired()) && entityType.isAssignableFrom(entity.getClass())) {
            i++;
         }
      }

      return i;
   }

   public void loadEntities(Collection<Entity> entityCollection) {
      this.loadedEntityList.addAll(entityCollection);

      for (Entity entity : entityCollection) {
         this.onEntityAdded(entity);
      }
   }

   public void unloadEntities(Collection<Entity> entityCollection) {
      this.unloadedEntityList.addAll(entityCollection);
   }

   public boolean canBlockBePlaced(Block blockIn, BlockPos pos, boolean bl, EnumFacing side, Entity entityIn, ItemStack itemStackIn) {
      Block block = this.getBlockState(pos).getBlock();
      AxisAlignedBB axisAlignedBB = bl ? null : blockIn.getCollisionBoundingBox(this, pos, blockIn.getDefaultState());
      return axisAlignedBB != null && !this.checkNoEntityCollision(axisAlignedBB, entityIn)
         ? false
         : (
            block.getMaterial() == Material.circuits && blockIn == Blocks.anvil
               ? true
               : block.getMaterial().isReplaceable() && blockIn.canReplace(this, pos, side, itemStackIn)
         );
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public void setSeaLevel(int i) {
      this.seaLevel = i;
   }

   @Override
   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      IBlockState iBlockState = this.getBlockState(pos);
      return iBlockState.getBlock().getStrongPower(this, pos, iBlockState, direction);
   }

   @Override
   public WorldType getWorldType() {
      return this.worldInfo.getTerrainType();
   }

   public int getStrongPower(BlockPos pos) {
      int i = 0;
      i = Math.max(i, this.getStrongPower(pos.down(), EnumFacing.DOWN));
      if (i >= 15) {
         return i;
      }

      i = Math.max(i, this.getStrongPower(pos.up(), EnumFacing.UP));
      if (i >= 15) {
         return i;
      }

      i = Math.max(i, this.getStrongPower(pos.north(), EnumFacing.NORTH));
      if (i >= 15) {
         return i;
      }

      i = Math.max(i, this.getStrongPower(pos.south(), EnumFacing.SOUTH));
      if (i >= 15) {
         return i;
      }

      i = Math.max(i, this.getStrongPower(pos.west(), EnumFacing.WEST));
      if (i >= 15) {
         return i;
      }

      i = Math.max(i, this.getStrongPower(pos.east(), EnumFacing.EAST));
      return i >= 15 ? i : i;
   }

   public boolean isSidePowered(BlockPos pos, EnumFacing side) {
      return this.getRedstonePower(pos, side) > 0;
   }

   public int getRedstonePower(BlockPos pos, EnumFacing facing) {
      IBlockState iBlockState = this.getBlockState(pos);
      Block block = iBlockState.getBlock();
      return block.isNormalCube() ? this.getStrongPower(pos) : block.getWeakPower(this, pos, iBlockState, facing);
   }

   public boolean isBlockPowered(BlockPos pos) {
      return this.getRedstonePower(pos.down(), EnumFacing.DOWN) > 0
         ? true
         : (
            this.getRedstonePower(pos.up(), EnumFacing.UP) > 0
               ? true
               : (
                  this.getRedstonePower(pos.north(), EnumFacing.NORTH) > 0
                     ? true
                     : (
                        this.getRedstonePower(pos.south(), EnumFacing.SOUTH) > 0
                           ? true
                           : (this.getRedstonePower(pos.west(), EnumFacing.WEST) > 0 ? true : this.getRedstonePower(pos.east(), EnumFacing.EAST) > 0)
                     )
               )
         );
   }

   public int isBlockIndirectlyGettingPowered(BlockPos pos) {
      int i = 0;

      for (EnumFacing enumFacing : EnumFacing.values()) {
         int j = this.getRedstonePower(pos.offset(enumFacing), enumFacing);
         if (j >= 15) {
            return 15;
         }

         if (j > i) {
            i = j;
         }
      }

      return i;
   }

   public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance) {
      return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance);
   }

   public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
      double d = -1.0;
      EntityPlayer entityPlayer = null;

      for (int i = 0; i < this.playerEntities.size(); i++) {
         EntityPlayer entityPlayer2 = this.playerEntities.get(i);
         if (EntitySelectors.NOT_SPECTATING.apply(entityPlayer2)) {
            double e = entityPlayer2.getDistanceSq(x, y, z);
            if ((distance < 0.0 || e < distance * distance) && (d == -1.0 || e < d)) {
               d = e;
               entityPlayer = entityPlayer2;
            }
         }
      }

      return entityPlayer;
   }

   public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range) {
      for (int i = 0; i < this.playerEntities.size(); i++) {
         EntityPlayer entityPlayer = this.playerEntities.get(i);
         if (EntitySelectors.NOT_SPECTATING.apply(entityPlayer)) {
            double d = entityPlayer.getDistanceSq(x, y, z);
            if (range < 0.0 || d < range * range) {
               return true;
            }
         }
      }

      return false;
   }

   public EntityPlayer getPlayerEntityByName(String name) {
      for (int i = 0; i < this.playerEntities.size(); i++) {
         EntityPlayer entityPlayer = this.playerEntities.get(i);
         if (name.equals(entityPlayer.getName())) {
            return entityPlayer;
         }
      }

      return null;
   }

   public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
      for (int i = 0; i < this.playerEntities.size(); i++) {
         EntityPlayer entityPlayer = this.playerEntities.get(i);
         if (uuid.equals(entityPlayer.getUniqueID())) {
            return entityPlayer;
         }
      }

      return null;
   }

   public void sendQuittingDisconnectingPacket() {
   }

   public void checkSessionLock() throws MinecraftException {
      this.saveHandler.checkSessionLock();
   }

   public void m(long l) {
      this.worldInfo.setWorldTotalTime(l);
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

   public void setWorldTime(long time) {
      this.worldInfo.setWorldTime(time);
   }

   public BlockPos getSpawnPoint() {
      BlockPos blockPos = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
      if (!this.getWorldBorder().contains(blockPos)) {
         blockPos = this.getHeight(new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
      }

      return blockPos;
   }

   public void setSpawnPoint(BlockPos pos) {
      this.worldInfo.setSpawn(pos);
   }

   public void joinEntityInSurroundings(Entity entityIn) {
      int i = MathHelper.floor_double(entityIn.posX / 16.0);
      int j = MathHelper.floor_double(entityIn.posZ / 16.0);
      byte b = 2;

      for (int k = i - b; k <= i + b; k++) {
         for (int l = j - b; l <= j + b; l++) {
            this.getChunkFromChunkCoords(k, l);
         }
      }

      if (!this.loadedEntityList.contains(entityIn)) {
         this.loadedEntityList.add(entityIn);
      }
   }

   public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
      return true;
   }

   public void setEntityState(Entity entityIn, byte state) {
   }

   public IChunkProvider getChunkProvider() {
      return this.chunkProvider;
   }

   public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
      blockIn.onBlockEventReceived(this, pos, this.getBlockState(pos), eventID, eventParam);
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

   public float getThunderStrength(float delta) {
      return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * delta) * this.getRainStrength(delta);
   }

   public void t(float f) {
      this.prevThunderingStrength = f;
      this.thunderingStrength = f;
   }

   public float getRainStrength(float delta) {
      return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * delta;
   }

   public void V(float f) {
      this.prevRainingStrength = f;
      this.rainingStrength = f;
   }

   public boolean isThundering() {
      return this.getThunderStrength(1.0F) > 0.9;
   }

   public boolean isRaining() {
      return this.getRainStrength(1.0F) > 0.2;
   }

   public boolean isRainingAt(BlockPos strikePosition) {
      if (!this.isRaining()) {
         return false;
      }

      if (!this.canSeeSky(strikePosition)) {
         return false;
      }

      if (this.getPrecipitationHeight(strikePosition).getY() > strikePosition.getY()) {
         return false;
      }

      BiomeGenBase biomeGenBase = this.getBiomeGenForCoords(strikePosition);
      return biomeGenBase.getEnableSnow() ? false : (this.canSnowAt(strikePosition, false) ? false : biomeGenBase.canRain());
   }

   public boolean isBlockinHighHumidity(BlockPos pos) {
      BiomeGenBase biomeGenBase = this.getBiomeGenForCoords(pos);
      return biomeGenBase.isHighHumidity();
   }

   public MapStorage getMapStorage() {
      return this.mapStorage;
   }

   public void setItemData(String dataID, WorldSavedData worldSavedDataIn) {
      this.mapStorage.setData(dataID, worldSavedDataIn);
   }

   public WorldSavedData loadItemData(Class<? extends WorldSavedData> clazz, String dataID) {
      return this.mapStorage.loadData(clazz, dataID);
   }

   public int getUniqueDataId(String key) {
      return this.mapStorage.getUniqueDataId(key);
   }

   public void playBroadcastSound(int i, BlockPos pos, int j) {
      for (int k = 0; k < this.worldAccesses.size(); k++) {
         this.worldAccesses.get(k).broadcastSound(i, pos, j);
      }
   }

   public void playAuxSFX(int i, BlockPos pos, int j) {
      this.playAuxSFXAtEntity((EntityPlayer)null, i, pos, j);
   }

   public void playAuxSFXAtEntity(EntityPlayer player, int sfxType, BlockPos pos, int i) {
      try {
         for (int j = 0; j < this.worldAccesses.size(); j++) {
            this.worldAccesses.get(j).playAuxSFX(player, sfxType, pos, i);
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Playing level event");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Level event being played");
         crashReportCategory.addCrashSection("Block coordinates", CrashReportCategory.getCoordinateInfo(pos));
         crashReportCategory.addCrashSection("Event source", player);
         crashReportCategory.addCrashSection("Event type", sfxType);
         crashReportCategory.addCrashSection("Event data", i);
         throw new ReportedException(crashReport);
      }
   }

   public int getHeight() {
      return 256;
   }

   public int getActualHeight() {
      return this.provider.getHasNoSky() ? 128 : 256;
   }

   public Random setRandomSeed(int i, int j, int k) {
      long l = i * 341873128712L + j * 132897987541L + this.getWorldInfo().getSeed() + k;
      this.rand.setSeed(l);
      return this.rand;
   }

   public BlockPos getStrongholdPos(String name, BlockPos pos) {
      return this.getChunkProvider().getStrongholdGen(this, name, pos);
   }

   @Override
   public boolean extendedLevelsInChunkCache() {
      return false;
   }

   public double getHorizon() {
      return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0 : 63.0;
   }

   public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
      CrashReportCategory crashReportCategory = report.makeCategoryDepth("Affected level", 1);
      crashReportCategory.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
      crashReportCategory.addCrashSectionCallable("All players", new Callable<String>() {
         public String call() {
            return World.this.playerEntities.size() + " total; " + World.this.playerEntities.toString();
         }
      });
      crashReportCategory.addCrashSectionCallable("Chunk stats", new Callable<String>() {
         public String call() {
            return World.this.chunkProvider.makeString();
         }
      });

      try {
         this.worldInfo.addToCrashReport(crashReportCategory);
      } catch (Throwable throwable) {
         crashReportCategory.addCrashSectionThrowable("Level Data Unobtainable", throwable);
      }

      return crashReportCategory;
   }

   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      for (int i = 0; i < this.worldAccesses.size(); i++) {
         IWorldAccess iWorldAccess = this.worldAccesses.get(i);
         iWorldAccess.sendBlockBreakProgress(breakerId, pos, progress);
      }
   }

   public Calendar getCurrentDate() {
      if (this.getTotalWorldTime() % 600L == 0L) {
         this.theCalendar.setTimeInMillis(MinecraftServer.getCurrentTimeMillis());
      }

      return this.theCalendar;
   }

   public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound compund) {
   }

   public Scoreboard getScoreboard() {
      return this.worldScoreboard;
   }

   public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
      for (Object object : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockPos = pos.offset((EnumFacing)object);
         if (this.isBlockLoaded(blockPos)) {
            IBlockState iBlockState = this.getBlockState(blockPos);
            if (Blocks.unpowered_comparator.N(iBlockState.getBlock())) {
               iBlockState.getBlock().onNeighborBlockChange(this, blockPos, iBlockState, blockIn);
            } else if (iBlockState.getBlock().isNormalCube()) {
               blockPos = blockPos.offset((EnumFacing)object);
               iBlockState = this.getBlockState(blockPos);
               if (Blocks.unpowered_comparator.N(iBlockState.getBlock())) {
                  iBlockState.getBlock().onNeighborBlockChange(this, blockPos, iBlockState, blockIn);
               }
            }
         }
      }
   }

   public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
      long l = 0L;
      float f = 0.0F;
      if (this.isBlockLoaded(pos)) {
         f = this.getCurrentMoonPhaseFactor();
         l = this.getChunkFromBlockCoords(pos).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getWorldTime(), l, f);
   }

   public EnumDifficulty getDifficulty() {
      return this.getWorldInfo().getDifficulty();
   }

   public int getSkylightSubtracted() {
      return this.skylightSubtracted;
   }

   public void setSkylightSubtracted(int newSkylightSubtracted) {
      this.skylightSubtracted = newSkylightSubtracted;
   }

   public int x() {
      return this.lastLightningBolt;
   }

   public void setLastLightningBolt(int lastLightningBoltIn) {
      this.lastLightningBolt = lastLightningBoltIn;
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

   public boolean isSpawnChunk(int x, int z) {
      BlockPos blockPos = this.getSpawnPoint();
      int i = x * 16 + 8 - blockPos.getX();
      int j = z * 16 + 8 - blockPos.getZ();
      short s = 128;
      return i >= -s && i <= s && j >= -s && j <= s;
   }
}
