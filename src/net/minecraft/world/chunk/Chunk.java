package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderDebug;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
   private static final Logger logger = LogManager.getLogger();
   private final ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
   private final byte[] blockBiomeArray = new byte[256];
   private final int[] precipitationHeightMap = new int[256];
   private final boolean[] updateSkylightColumns = new boolean[256];
   private boolean isChunkLoaded;
   private final World worldObj;
   private final int[] heightMap;
   public final int xPosition;
   public final int zPosition;
   private boolean isGapLightingUpdated;
   private final Map<BlockPos, TileEntity> chunkTileEntityMap = Maps.newHashMap();
   private final ClassInheritanceMultiMap<Entity>[] entityLists;
   private boolean isTerrainPopulated;
   private boolean isLightPopulated;
   private boolean field_150815_m;
   private boolean isModified;
   private boolean hasEntities;
   private long lastSaveTime;
   private int heightMapMinimum;
   private long inhabitedTime;
   private int queuedLightChecks = 4096;
   private ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue = Queues.newConcurrentLinkedQueue();

   public Chunk(World worldIn, int x, int z) {
      this.entityLists = new ClassInheritanceMultiMap[16];
      this.worldObj = worldIn;
      this.xPosition = x;
      this.zPosition = z;
      this.heightMap = new int[256];

      for (int i = 0; i < this.entityLists.length; i++) {
         this.entityLists[i] = new ClassInheritanceMultiMap<>(Entity.class);
      }

      Arrays.fill(this.precipitationHeightMap, -999);
      Arrays.fill(this.blockBiomeArray, (byte)-1);
   }

   public Chunk(World worldIn, ChunkPrimer primer, int x, int z) {
      this(worldIn, x, z);
      short s = 256;
      boolean bl = !worldIn.provider.getHasNoSky();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < s; k++) {
               int l = i * s * 16 | j * s | k;
               IBlockState iBlockState = primer.getBlockState(l);
               if (iBlockState.getBlock().getMaterial() != Material.air) {
                  int m = k >> 4;
                  if (this.storageArrays[m] == null) {
                     this.storageArrays[m] = new ExtendedBlockStorage(m << 4, bl);
                  }

                  this.storageArrays[m].set(i, k & 15, j, iBlockState);
               }
            }
         }
      }
   }

   public boolean isAtLocation(int x, int z) {
      return x == this.xPosition && z == this.zPosition;
   }

   public int getHeight(BlockPos pos) {
      return this.getHeightValue(pos.getX() & 15, pos.getZ() & 15);
   }

   public int getHeightValue(int x, int z) {
      return this.heightMap[z << 4 | x];
   }

   public int getTopFilledSegment() {
      for (int i = this.storageArrays.length - 1; i >= 0; i--) {
         if (this.storageArrays[i] != null) {
            return this.storageArrays[i].getYLocation();
         }
      }

      return 0;
   }

   public ExtendedBlockStorage[] getBlockStorageArray() {
      return this.storageArrays;
   }

   public void generateHeightMap() {
      int i = this.getTopFilledSegment();
      this.heightMapMinimum = Integer.MAX_VALUE;

      for (int j = 0; j < 16; j++) {
         for (int k = 0; k < 16; k++) {
            this.precipitationHeightMap[j + (k << 4)] = -999;

            for (int l = i + 16; l > 0; l--) {
               Block block = this.getBlock0(j, l - 1, k);
               if (block.getLightOpacity() != 0) {
                  this.heightMap[k << 4 | j] = l;
                  if (l < this.heightMapMinimum) {
                     this.heightMapMinimum = l;
                  }
                  break;
               }
            }
         }
      }

      this.isModified = true;
   }

   public void generateSkylightMap() {
      int i = this.getTopFilledSegment();
      this.heightMapMinimum = Integer.MAX_VALUE;

      for (int j = 0; j < 16; j++) {
         for (int k = 0; k < 16; k++) {
            this.precipitationHeightMap[j + (k << 4)] = -999;

            for (int l = i + 16; l > 0; l--) {
               if (this.getBlockLightOpacity(j, l - 1, k) != 0) {
                  this.heightMap[k << 4 | j] = l;
                  if (l < this.heightMapMinimum) {
                     this.heightMapMinimum = l;
                  }
                  break;
               }
            }

            if (!this.worldObj.provider.getHasNoSky()) {
               int m = 15;
               int n = i + 16 - 1;

               while (true) {
                  int i2 = this.getBlockLightOpacity(j, n, k);
                  if (i2 == 0 && m != 15) {
                     i2 = 1;
                  }

                  m -= i2;
                  if (m > 0) {
                     ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n >> 4];
                     if (extendedBlockStorage != null) {
                        extendedBlockStorage.setExtSkylightValue(j, n & 15, k, m);
                        this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + j, n, (this.zPosition << 4) + k));
                     }
                  }

                  if (--n <= 0 || m <= 0) {
                     break;
                  }
               }
            }
         }
      }

      this.isModified = true;
   }

   private void propagateSkylightOcclusion(int x, int z) {
      this.updateSkylightColumns[x + z * 16] = true;
      this.isGapLightingUpdated = true;
   }

   private void recheckGaps(boolean bl) {
      this.worldObj.theProfiler.startSection("recheckGaps");
      if (this.worldObj.isAreaLoaded(new BlockPos(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8), 16)) {
         for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               if (this.updateSkylightColumns[i + j * 16]) {
                  this.updateSkylightColumns[i + j * 16] = false;
                  int k = this.getHeightValue(i, j);
                  int l = this.xPosition * 16 + i;
                  int m = this.zPosition * 16 + j;
                  int n = Integer.MAX_VALUE;

                  for (Object object : EnumFacing.Plane.HORIZONTAL) {
                     n = Math.min(
                        n, this.worldObj.getChunksLowestHorizon(l + ((EnumFacing)object).getFrontOffsetX(), m + ((EnumFacing)object).getFrontOffsetZ())
                     );
                  }

                  this.checkSkylightNeighborHeight(l, m, n);

                  for (Object object2 : EnumFacing.Plane.HORIZONTAL) {
                     this.checkSkylightNeighborHeight(l + ((EnumFacing)object2).getFrontOffsetX(), m + ((EnumFacing)object2).getFrontOffsetZ(), k);
                  }

                  if (bl) {
                     this.worldObj.theProfiler.endSection();
                     return;
                  }
               }
            }
         }

         this.isGapLightingUpdated = false;
      }

      this.worldObj.theProfiler.endSection();
   }

   private void checkSkylightNeighborHeight(int x, int z, int maxValue) {
      int i = this.worldObj.getHeight(new BlockPos(x, 0, z)).getY();
      if (i > maxValue) {
         this.updateSkylightNeighborHeight(x, z, maxValue, i + 1);
      } else if (i < maxValue) {
         this.updateSkylightNeighborHeight(x, z, i, maxValue + 1);
      }
   }

   private void updateSkylightNeighborHeight(int x, int z, int startY, int endY) {
      if (endY > startY && this.worldObj.isAreaLoaded(new BlockPos(x, 0, z), 16)) {
         for (int i = startY; i < endY; i++) {
            this.worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, i, z));
         }

         this.isModified = true;
      }
   }

   private void relightBlock(int x, int y, int z) {
      int i = this.heightMap[z << 4 | x] & 0xFF;
      int j = i;
      if (y > i) {
         j = y;
      }

      while (j > 0 && this.getBlockLightOpacity(x, j - 1, z) == 0) {
         j--;
      }

      if (j != i) {
         this.worldObj.markBlocksDirtyVertical(x + this.xPosition * 16, z + this.zPosition * 16, j, i);
         this.heightMap[z << 4 | x] = j;
         int k = this.xPosition * 16 + x;
         int l = this.zPosition * 16 + z;
         if (!this.worldObj.provider.getHasNoSky()) {
            if (j < i) {
               for (int m = j; m < i; m++) {
                  ExtendedBlockStorage extendedBlockStorage = this.storageArrays[m >> 4];
                  if (extendedBlockStorage != null) {
                     extendedBlockStorage.setExtSkylightValue(x, m & 15, z, 15);
                     this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, m, (this.zPosition << 4) + z));
                  }
               }
            } else {
               for (int n = i; n < j; n++) {
                  ExtendedBlockStorage extendedBlockStorage2 = this.storageArrays[n >> 4];
                  if (extendedBlockStorage2 != null) {
                     extendedBlockStorage2.setExtSkylightValue(x, n & 15, z, 0);
                     this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, n, (this.zPosition << 4) + z));
                  }
               }
            }

            int i2 = 15;

            while (j > 0 && i2 > 0) {
               int i3 = this.getBlockLightOpacity(x, --j, z);
               if (i3 == 0) {
                  i3 = 1;
               }

               i2 -= i3;
               if (i2 < 0) {
                  i2 = 0;
               }

               ExtendedBlockStorage extendedBlockStorage3 = this.storageArrays[j >> 4];
               if (extendedBlockStorage3 != null) {
                  extendedBlockStorage3.setExtSkylightValue(x, j & 15, z, i2);
               }
            }
         }

         int i4 = this.heightMap[z << 4 | x];
         int i5 = i;
         int i6 = i4;
         if (i4 < i) {
            i5 = i4;
            i6 = i;
         }

         if (i4 < this.heightMapMinimum) {
            this.heightMapMinimum = i4;
         }

         if (!this.worldObj.provider.getHasNoSky()) {
            for (Object object : EnumFacing.Plane.HORIZONTAL) {
               this.updateSkylightNeighborHeight(k + ((EnumFacing)object).getFrontOffsetX(), l + ((EnumFacing)object).getFrontOffsetZ(), i5, i6);
            }

            this.updateSkylightNeighborHeight(k, l, i5, i6);
         }

         this.isModified = true;
      }
   }

   public int getBlockLightOpacity(BlockPos pos) {
      return this.getBlock(pos).getLightOpacity();
   }

   private int getBlockLightOpacity(int x, int y, int z) {
      return this.getBlock0(x, y, z).getLightOpacity();
   }

   private Block getBlock0(int x, int y, int z) {
      Block block2 = Blocks.air;
      if (y >= 0 && y >> 4 < this.storageArrays.length) {
         ExtendedBlockStorage extendedBlockStorage = this.storageArrays[y >> 4];
         if (extendedBlockStorage != null) {
            try {
               block2 = extendedBlockStorage.getBlockByExtId(x, y & 15, z);
            } catch (Throwable throwable) {
               CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Getting block");
               throw new ReportedException(crashReport);
            }
         }
      }

      return block2;
   }

   public Block getBlock(final int x, final int y, final int z) {
      try {
         return this.getBlock0(x & 15, y, z & 15);
      } catch (ReportedException reportedException) {
         CrashReportCategory crashReportCategory = reportedException.getCrashReport().makeCategory("Block being got");
         crashReportCategory.addCrashSectionCallable("Location", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(new BlockPos(Chunk.this.xPosition * 16 + x, y, Chunk.this.zPosition * 16 + z));
            }
         });
         throw reportedException;
      }
   }

   public Block getBlock(final BlockPos pos) {
      try {
         return this.getBlock0(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
      } catch (ReportedException reportedException) {
         CrashReportCategory crashReportCategory = reportedException.getCrashReport().makeCategory("Block being got");
         crashReportCategory.addCrashSectionCallable("Location", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(pos);
            }
         });
         throw reportedException;
      }
   }

   public IBlockState getBlockState(final BlockPos pos) {
      if (this.worldObj.getWorldType() == WorldType.DEBUG_WORLD) {
         IBlockState iBlockState = null;
         if (pos.getY() == 60) {
            iBlockState = Blocks.barrier.getDefaultState();
         }

         if (pos.getY() == 70) {
            iBlockState = ChunkProviderDebug.func_177461_b(pos.getX(), pos.getZ());
         }

         return iBlockState == null ? Blocks.air.getDefaultState() : iBlockState;
      } else {
         try {
            if (pos.getY() >= 0 && pos.getY() >> 4 < this.storageArrays.length) {
               ExtendedBlockStorage extendedBlockStorage = this.storageArrays[pos.getY() >> 4];
               if (extendedBlockStorage != null) {
                  int i = pos.getX() & 15;
                  int j = pos.getY() & 15;
                  int k = pos.getZ() & 15;
                  return extendedBlockStorage.get(i, j, k);
               }
            }

            return Blocks.air.getDefaultState();
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Getting block state");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Block being got");
            crashReportCategory.addCrashSectionCallable("Location", new Callable<String>() {
               public String call() throws Exception {
                  return CrashReportCategory.getCoordinateInfo(pos);
               }
            });
            throw new ReportedException(crashReport);
         }
      }
   }

   private int getBlockMetadata(int x, int y, int z) {
      if (y >> 4 >= this.storageArrays.length) {
         return 0;
      }

      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[y >> 4];
      return extendedBlockStorage != null ? extendedBlockStorage.getExtBlockMetadata(x, y & 15, z) : 0;
   }

   public int getBlockMetadata(BlockPos pos) {
      return this.getBlockMetadata(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
   }

   public IBlockState setBlockState(BlockPos pos, IBlockState state) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      int l = k << 4 | i;
      if (j >= this.precipitationHeightMap[l] - 1) {
         this.precipitationHeightMap[l] = -999;
      }

      int m = this.heightMap[l];
      IBlockState iBlockState = this.getBlockState(pos);
      if (iBlockState == state) {
         return null;
      }

      Block block = state.getBlock();
      Block block2 = iBlockState.getBlock();
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[j >> 4];
      boolean bl = false;
      if (extendedBlockStorage == null) {
         if (block == Blocks.air) {
            return null;
         }

         extendedBlockStorage = this.storageArrays[j >> 4] = new ExtendedBlockStorage(j >> 4 << 4, !this.worldObj.provider.getHasNoSky());
         bl = j >= m;
      }

      extendedBlockStorage.set(i, j & 15, k, state);
      if (block2 != block) {
         if (!this.worldObj.isRemote) {
            block2.breakBlock(this.worldObj, pos, iBlockState);
         } else if (block2 instanceof ITileEntityProvider) {
            this.worldObj.removeTileEntity(pos);
         }
      }

      if (extendedBlockStorage.getBlockByExtId(i, j & 15, k) != block) {
         return null;
      }

      if (bl) {
         this.generateSkylightMap();
      } else {
         int n = block.getLightOpacity();
         int i2 = block2.getLightOpacity();
         if (n > 0) {
            if (j >= m) {
               this.relightBlock(i, j + 1, k);
            }
         } else if (j == m - 1) {
            this.relightBlock(i, j, k);
         }

         if (n != i2 && (n < i2 || this.getLightFor(EnumSkyBlock.SKY, pos) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, pos) > 0)) {
            this.propagateSkylightOcclusion(i, k);
         }
      }

      if (block2 instanceof ITileEntityProvider) {
         TileEntity tileEntity = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
         if (tileEntity != null) {
            tileEntity.updateContainingBlockInfo();
         }
      }

      if (!this.worldObj.isRemote && block2 != block) {
         block.onBlockAdded(this.worldObj, pos, state);
      }

      if (block instanceof ITileEntityProvider) {
         TileEntity tileEntity2 = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
         if (tileEntity2 == null) {
            tileEntity2 = ((ITileEntityProvider)block).createNewTileEntity(this.worldObj, block.getMetaFromState(state));
            this.worldObj.setTileEntity(pos, tileEntity2);
         }

         if (tileEntity2 != null) {
            tileEntity2.updateContainingBlockInfo();
         }
      }

      this.isModified = true;
      return iBlockState;
   }

   public int getLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[j >> 4];
      return extendedBlockStorage == null
         ? (this.canSeeSky(pos) ? enumSkyBlock.defaultLightValue : 0)
         : (
            enumSkyBlock == EnumSkyBlock.SKY
               ? (this.worldObj.provider.getHasNoSky() ? 0 : extendedBlockStorage.getExtSkylightValue(i, j & 15, k))
               : (enumSkyBlock == EnumSkyBlock.BLOCK ? extendedBlockStorage.getExtBlocklightValue(i, j & 15, k) : enumSkyBlock.defaultLightValue)
         );
   }

   public void setLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos, int value) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[j >> 4];
      if (extendedBlockStorage == null) {
         extendedBlockStorage = this.storageArrays[j >> 4] = new ExtendedBlockStorage(j >> 4 << 4, !this.worldObj.provider.getHasNoSky());
         this.generateSkylightMap();
      }

      this.isModified = true;
      if (enumSkyBlock == EnumSkyBlock.SKY) {
         if (!this.worldObj.provider.getHasNoSky()) {
            extendedBlockStorage.setExtSkylightValue(i, j & 15, k, value);
         }
      } else if (enumSkyBlock == EnumSkyBlock.BLOCK) {
         extendedBlockStorage.setExtBlocklightValue(i, j & 15, k, value);
      }
   }

   public int getLightSubtracted(BlockPos pos, int amount) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[j >> 4];
      if (extendedBlockStorage != null) {
         int l = this.worldObj.provider.getHasNoSky() ? 0 : extendedBlockStorage.getExtSkylightValue(i, j & 15, k);
         l -= amount;
         int m = extendedBlockStorage.getExtBlocklightValue(i, j & 15, k);
         if (m > l) {
            l = m;
         }

         return l;
      } else {
         return !this.worldObj.provider.getHasNoSky() && amount < EnumSkyBlock.SKY.defaultLightValue ? EnumSkyBlock.SKY.defaultLightValue - amount : 0;
      }
   }

   public void addEntity(Entity entityIn) {
      this.hasEntities = true;
      int i = MathHelper.floor_double(entityIn.posX / 16.0);
      int j = MathHelper.floor_double(entityIn.posZ / 16.0);
      if (i != this.xPosition || j != this.zPosition) {
         logger.warn(
            "Wrong location! (" + i + ", " + j + ") should be (" + this.xPosition + ", " + this.zPosition + "), " + entityIn, new Object[]{entityIn}
         );
         entityIn.setDead();
      }

      int k = MathHelper.floor_double(entityIn.posY / 16.0);
      if (k < 0) {
         k = 0;
      }

      if (k >= this.entityLists.length) {
         k = this.entityLists.length - 1;
      }

      entityIn.addedToChunk = true;
      entityIn.chunkCoordX = this.xPosition;
      entityIn.chunkCoordY = k;
      entityIn.chunkCoordZ = this.zPosition;
      this.entityLists[k].add(entityIn);
   }

   public void removeEntity(Entity entityIn) {
      this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
   }

   public void removeEntityAtIndex(Entity entityIn, int i) {
      if (i < 0) {
         i = 0;
      }

      if (i >= this.entityLists.length) {
         i = this.entityLists.length - 1;
      }

      this.entityLists[i].remove(entityIn);
   }

   public boolean canSeeSky(BlockPos pos) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      return j >= this.heightMap[k << 4 | i];
   }

   private TileEntity createNewTileEntity(BlockPos pos) {
      Block block = this.getBlock(pos);
      return !block.hasTileEntity() ? null : ((ITileEntityProvider)block).createNewTileEntity(this.worldObj, this.getBlockMetadata(pos));
   }

   public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType enumCreateEntityType) {
      TileEntity tileEntity = this.chunkTileEntityMap.get(pos);
      if (tileEntity == null) {
         if (enumCreateEntityType == Chunk.EnumCreateEntityType.IMMEDIATE) {
            tileEntity = this.createNewTileEntity(pos);
            this.worldObj.setTileEntity(pos, tileEntity);
         } else if (enumCreateEntityType == Chunk.EnumCreateEntityType.QUEUED) {
            this.tileEntityPosQueue.add(pos);
         }
      } else if (tileEntity.isInvalid()) {
         this.chunkTileEntityMap.remove(pos);
         return null;
      }

      return tileEntity;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
      this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
      if (this.isChunkLoaded) {
         this.worldObj.addTileEntity(tileEntityIn);
      }
   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
      tileEntityIn.setWorldObj(this.worldObj);
      tileEntityIn.setPos(pos);
      if (this.getBlock(pos) instanceof ITileEntityProvider) {
         if (this.chunkTileEntityMap.containsKey(pos)) {
            this.chunkTileEntityMap.get(pos).invalidate();
         }

         tileEntityIn.validate();
         this.chunkTileEntityMap.put(pos, tileEntityIn);
      }
   }

   public void removeTileEntity(BlockPos pos) {
      if (this.isChunkLoaded) {
         TileEntity tileEntity = this.chunkTileEntityMap.remove(pos);
         if (tileEntity != null) {
            tileEntity.invalidate();
         }
      }
   }

   public void onChunkLoad() {
      this.isChunkLoaded = true;
      this.worldObj.addTileEntities(this.chunkTileEntityMap.values());

      for (int i = 0; i < this.entityLists.length; i++) {
         for (Entity entity : this.entityLists[i]) {
            entity.onChunkLoad();
         }

         this.worldObj.loadEntities(this.entityLists[i]);
      }
   }

   public void onChunkUnload() {
      this.isChunkLoaded = false;

      for (TileEntity tileEntity : this.chunkTileEntityMap.values()) {
         this.worldObj.markTileEntityForRemoval(tileEntity);
      }

      for (int i = 0; i < this.entityLists.length; i++) {
         this.worldObj.unloadEntities(this.entityLists[i]);
      }
   }

   public void setChunkModified() {
      this.isModified = true;
   }

   public void getEntitiesWithinAABBForEntity(Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> predicate) {
      int i = MathHelper.floor_double((aabb.minY - 2.0) / 16.0);
      int j = MathHelper.floor_double((aabb.maxY + 2.0) / 16.0);
      i = MathHelper.clamp_int(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp_int(j, 0, this.entityLists.length - 1);

      for (int k = i; k <= j; k++) {
         if (!this.entityLists[k].isEmpty()) {
            for (Entity entity : this.entityLists[k]) {
               if (entity.getEntityBoundingBox().intersectsWith(aabb) && entity != entityIn) {
                  if (predicate == null || predicate.apply(entity)) {
                     listToFill.add(entity);
                  }

                  Entity[] entitys = entity.getParts();
                  if (entitys != null) {
                     for (int l = 0; l < entitys.length; l++) {
                        entity = entitys[l];
                        if (entity != entityIn && entity.getEntityBoundingBox().intersectsWith(aabb) && (predicate == null || predicate.apply(entity))) {
                           listToFill.add(entity);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public <T extends Entity> void getEntitiesOfTypeWithinAAAB(
      Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> predicate
   ) {
      int i = MathHelper.floor_double((aabb.minY - 2.0) / 16.0);
      int j = MathHelper.floor_double((aabb.maxY + 2.0) / 16.0);
      i = MathHelper.clamp_int(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp_int(j, 0, this.entityLists.length - 1);

      for (int k = i; k <= j; k++) {
         for (Entity entity : this.entityLists[k].getByClass(entityClass)) {
            if (entity.getEntityBoundingBox().intersectsWith(aabb) && (predicate == null || predicate.apply((T)(entity)))) {
               listToFill.add((T)(entity));
            }
         }
      }
   }

   public boolean needsSaving(boolean bl) {
      if (bl) {
         if (this.hasEntities && this.worldObj.getTotalWorldTime() != this.lastSaveTime || this.isModified) {
            return true;
         }
      } else if (this.hasEntities && this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L) {
         return true;
      }

      return this.isModified;
   }

   public Random getRandomWithSeed(long seed) {
      return new Random(
         this.worldObj.getSeed()
               + this.xPosition * this.xPosition * 4987142
               + this.xPosition * 5947611
               + this.zPosition * this.zPosition * 4392871L
               + this.zPosition * 389711
            ^ seed
      );
   }

   public boolean isEmpty() {
      return false;
   }

   public void populateChunk(IChunkProvider iChunkProvider, IChunkProvider iChunkProvider2, int x, int z) {
      boolean bl = iChunkProvider.chunkExists(x, z - 1);
      boolean bl2 = iChunkProvider.chunkExists(x + 1, z);
      boolean bl3 = iChunkProvider.chunkExists(x, z + 1);
      boolean bl4 = iChunkProvider.chunkExists(x - 1, z);
      boolean bl5 = iChunkProvider.chunkExists(x - 1, z - 1);
      boolean bl6 = iChunkProvider.chunkExists(x + 1, z + 1);
      boolean bl7 = iChunkProvider.chunkExists(x - 1, z + 1);
      boolean bl8 = iChunkProvider.chunkExists(x + 1, z - 1);
      if (bl2 && bl3 && bl6) {
         if (!this.isTerrainPopulated) {
            iChunkProvider.populate(iChunkProvider2, x, z);
         } else {
            iChunkProvider.populateChunk(iChunkProvider2, this, x, z);
         }
      }

      if (bl4 && bl3 && bl7) {
         Chunk chunk = iChunkProvider.provideChunk(x - 1, z);
         if (!chunk.isTerrainPopulated) {
            iChunkProvider.populate(iChunkProvider2, x - 1, z);
         } else {
            iChunkProvider.populateChunk(iChunkProvider2, chunk, x - 1, z);
         }
      }

      if (bl && bl2 && bl8) {
         Chunk chunk2 = iChunkProvider.provideChunk(x, z - 1);
         if (!chunk2.isTerrainPopulated) {
            iChunkProvider.populate(iChunkProvider2, x, z - 1);
         } else {
            iChunkProvider.populateChunk(iChunkProvider2, chunk2, x, z - 1);
         }
      }

      if (bl5 && bl && bl4) {
         Chunk chunk3 = iChunkProvider.provideChunk(x - 1, z - 1);
         if (!chunk3.isTerrainPopulated) {
            iChunkProvider.populate(iChunkProvider2, x - 1, z - 1);
         } else {
            iChunkProvider.populateChunk(iChunkProvider2, chunk3, x - 1, z - 1);
         }
      }
   }

   public BlockPos getPrecipitationHeight(BlockPos pos) {
      int i = pos.getX() & 15;
      int j = pos.getZ() & 15;
      int k = i | j << 4;
      BlockPos blockPos = new BlockPos(pos.getX(), this.precipitationHeightMap[k], pos.getZ());
      if (blockPos.getY() == -999) {
         int l = this.getTopFilledSegment() + 15;
         blockPos = new BlockPos(pos.getX(), l, pos.getZ());
         int m = -1;

         while (blockPos.getY() > 0 && m == -1) {
            Block block = this.getBlock(blockPos);
            Material material = block.getMaterial();
            if (!material.blocksMovement() && !material.isLiquid()) {
               blockPos = blockPos.down();
            } else {
               m = blockPos.getY() + 1;
            }
         }

         this.precipitationHeightMap[k] = m;
      }

      return new BlockPos(pos.getX(), this.precipitationHeightMap[k], pos.getZ());
   }

   public void func_150804_b(boolean bl) {
      if (this.isGapLightingUpdated && !this.worldObj.provider.getHasNoSky() && !bl) {
         this.recheckGaps(this.worldObj.isRemote);
      }

      this.field_150815_m = true;
      if (!this.isLightPopulated && this.isTerrainPopulated) {
         this.func_150809_p();
      }

      while (!this.tileEntityPosQueue.isEmpty()) {
         BlockPos blockPos = this.tileEntityPosQueue.poll();
         if (this.getTileEntity(blockPos, Chunk.EnumCreateEntityType.CHECK) == null && this.getBlock(blockPos).hasTileEntity()) {
            TileEntity tileEntity = this.createNewTileEntity(blockPos);
            this.worldObj.setTileEntity(blockPos, tileEntity);
            this.worldObj.markBlockRangeForRenderUpdate(blockPos, blockPos);
         }
      }
   }

   public boolean isPopulated() {
      return this.field_150815_m && this.isTerrainPopulated && this.isLightPopulated;
   }

   public ChunkCoordIntPair getChunkCoordIntPair() {
      return new ChunkCoordIntPair(this.xPosition, this.zPosition);
   }

   public boolean getAreLevelsEmpty(int startY, int endY) {
      if (startY < 0) {
         startY = 0;
      }

      if (endY >= 256) {
         endY = 255;
      }

      for (int i = startY; i <= endY; i += 16) {
         ExtendedBlockStorage extendedBlockStorage = this.storageArrays[i >> 4];
         if (extendedBlockStorage != null && !extendedBlockStorage.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void setStorageArrays(ExtendedBlockStorage[] newStorageArrays) {
      if (this.storageArrays.length != newStorageArrays.length) {
         logger.warn("Could not set level chunk sections, array length is " + newStorageArrays.length + " instead of " + this.storageArrays.length);
      } else {
         for (int i = 0; i < this.storageArrays.length; i++) {
            this.storageArrays[i] = newStorageArrays[i];
         }
      }
   }

   public void fillChunk(byte[] bytes, int i, boolean bl) {
      int j = 0;
      boolean bl2 = !this.worldObj.provider.getHasNoSky();

      for (int k = 0; k < this.storageArrays.length; k++) {
         if ((i & 1 << k) != 0) {
            if (this.storageArrays[k] == null) {
               this.storageArrays[k] = new ExtendedBlockStorage(k << 4, bl2);
            }

            char[] chars = this.storageArrays[k].getData();

            for (int l = 0; l < chars.length; l++) {
               chars[l] = (char)((bytes[j + 1] & 255) << 8 | bytes[j] & 0xFF);
               j += 2;
            }
         } else if (bl && this.storageArrays[k] != null) {
            this.storageArrays[k] = null;
         }
      }

      for (int m = 0; m < this.storageArrays.length; m++) {
         if ((i & 1 << m) != 0 && this.storageArrays[m] != null) {
            NibbleArray nibbleArray = this.storageArrays[m].getBlocklightArray();
            System.arraycopy(bytes, j, nibbleArray.getData(), 0, nibbleArray.getData().length);
            j += nibbleArray.getData().length;
         }
      }

      if (bl2) {
         for (int n = 0; n < this.storageArrays.length; n++) {
            if ((i & 1 << n) != 0 && this.storageArrays[n] != null) {
               NibbleArray nibbleArray2 = this.storageArrays[n].getSkylightArray();
               System.arraycopy(bytes, j, nibbleArray2.getData(), 0, nibbleArray2.getData().length);
               j += nibbleArray2.getData().length;
            }
         }
      }

      if (bl) {
         System.arraycopy(bytes, j, this.blockBiomeArray, 0, this.blockBiomeArray.length);
         int i2 = j + this.blockBiomeArray.length;
      }

      for (int i3 = 0; i3 < this.storageArrays.length; i3++) {
         if (this.storageArrays[i3] != null && (i & 1 << i3) != 0) {
            this.storageArrays[i3].removeInvalidBlocks();
         }
      }

      this.isLightPopulated = true;
      this.isTerrainPopulated = true;
      this.generateHeightMap();

      for (TileEntity tileEntity : this.chunkTileEntityMap.values()) {
         tileEntity.updateContainingBlockInfo();
      }
   }

   public BiomeGenBase getBiome(BlockPos pos, WorldChunkManager chunkManager) {
      int i = pos.getX() & 15;
      int j = pos.getZ() & 15;
      int k = this.blockBiomeArray[j << 4 | i] & 255;
      if (k == 255) {
         BiomeGenBase biomeGenBase = chunkManager.getBiomeGenerator(pos, BiomeGenBase.plains);
         k = biomeGenBase.biomeID;
         this.blockBiomeArray[j << 4 | i] = (byte)(k & 0xFF);
      }

      BiomeGenBase biomeGenBase2 = BiomeGenBase.getBiome(k);
      return biomeGenBase2 == null ? BiomeGenBase.plains : biomeGenBase2;
   }

   public byte[] getBiomeArray() {
      return this.blockBiomeArray;
   }

   public void setBiomeArray(byte[] biomeArray) {
      if (this.blockBiomeArray.length != biomeArray.length) {
         logger.warn("Could not set level chunk biomes, array length is " + biomeArray.length + " instead of " + this.blockBiomeArray.length);
      } else {
         for (int i = 0; i < this.blockBiomeArray.length; i++) {
            this.blockBiomeArray[i] = biomeArray[i];
         }
      }
   }

   public void resetRelightChecks() {
      this.queuedLightChecks = 0;
   }

   public void enqueueRelightChecks() {
      BlockPos blockPos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);

      for (int i = 0; i < 8; i++) {
         if (this.queuedLightChecks >= 4096) {
            return;
         }

         int j = this.queuedLightChecks % 16;
         int k = this.queuedLightChecks / 16 % 16;
         int l = this.queuedLightChecks / 256;
         this.queuedLightChecks++;

         for (int m = 0; m < 16; m++) {
            BlockPos blockPos2 = blockPos.add(k, (j << 4) + m, l);
            boolean bl = m == 0 || m == 15 || k == 0 || k == 15 || l == 0 || l == 15;
            if (this.storageArrays[j] == null && bl
               || this.storageArrays[j] != null && this.storageArrays[j].getBlockByExtId(k, m, l).getMaterial() == Material.air) {
               for (EnumFacing enumFacing : EnumFacing.values()) {
                  BlockPos blockPos3 = blockPos2.offset(enumFacing);
                  if (this.worldObj.getBlockState(blockPos3).getBlock().getLightValue() > 0) {
                     this.worldObj.checkLight(blockPos3);
                  }
               }

               this.worldObj.checkLight(blockPos2);
            }
         }
      }
   }

   public void func_150809_p() {
      this.isTerrainPopulated = true;
      this.isLightPopulated = true;
      BlockPos blockPos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);
      if (!this.worldObj.provider.getHasNoSky()) {
         if (this.worldObj.isAreaLoaded(blockPos.add(-1, 0, -1), blockPos.add(16, this.worldObj.getSeaLevel(), 16))) {
            label44:
            for (int i = 0; i < 16; i++) {
               for (int j = 0; j < 16; j++) {
                  if (!this.func_150811_f(i, j)) {
                     this.isLightPopulated = false;
                     break label44;
                  }
               }
            }

            if (this.isLightPopulated) {
               for (Object object : EnumFacing.Plane.HORIZONTAL) {
                  int k = ((EnumFacing)object).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 16 : 1;
                  this.worldObj.getChunkFromBlockCoords(blockPos.offset((EnumFacing)object, k)).func_180700_a(((EnumFacing)object).getOpposite());
               }

               this.func_177441_y();
            }
         } else {
            this.isLightPopulated = false;
         }
      }
   }

   private void func_177441_y() {
      for (int i = 0; i < this.updateSkylightColumns.length; i++) {
         this.updateSkylightColumns[i] = true;
      }

      this.recheckGaps(false);
   }

   private void func_180700_a(EnumFacing facing) {
      if (this.isTerrainPopulated) {
         if (facing == EnumFacing.EAST) {
            for (int i = 0; i < 16; i++) {
               this.func_150811_f(15, i);
            }
         } else if (facing == EnumFacing.WEST) {
            for (int j = 0; j < 16; j++) {
               this.func_150811_f(0, j);
            }
         } else if (facing == EnumFacing.SOUTH) {
            for (int k = 0; k < 16; k++) {
               this.func_150811_f(k, 15);
            }
         } else if (facing == EnumFacing.NORTH) {
            for (int l = 0; l < 16; l++) {
               this.func_150811_f(l, 0);
            }
         }
      }
   }

   private boolean func_150811_f(int x, int z) {
      int i = this.getTopFilledSegment();
      boolean bl = false;
      boolean bl2 = false;
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos((this.xPosition << 4) + x, 0, (this.zPosition << 4) + z);

      for (int j = i + 16 - 1; j > this.worldObj.getSeaLevel() || j > 0 && !bl2; j--) {
         mutableBlockPos.set(mutableBlockPos.getX(), j, mutableBlockPos.getZ());
         int k = this.getBlockLightOpacity(mutableBlockPos);
         if (k == 255 && mutableBlockPos.getY() < this.worldObj.getSeaLevel()) {
            bl2 = true;
         }

         if (!bl && k > 0) {
            bl = true;
         } else if (bl && k == 0 && !this.worldObj.checkLight(mutableBlockPos)) {
            return false;
         }
      }

      for (int l = mutableBlockPos.getY(); l > 0; l--) {
         mutableBlockPos.set(mutableBlockPos.getX(), l, mutableBlockPos.getZ());
         if (this.getBlock(mutableBlockPos).getLightValue() > 0) {
            this.worldObj.checkLight(mutableBlockPos);
         }
      }

      return true;
   }

   public boolean isLoaded() {
      return this.isChunkLoaded;
   }

   public void setChunkLoaded(boolean loaded) {
      this.isChunkLoaded = loaded;
   }

   public World getWorld() {
      return this.worldObj;
   }

   public int[] getHeightMap() {
      return this.heightMap;
   }

   public void setHeightMap(int[] newHeightMap) {
      if (this.heightMap.length != newHeightMap.length) {
         logger.warn("Could not set level chunk heightmap, array length is " + newHeightMap.length + " instead of " + this.heightMap.length);
      } else {
         for (int i = 0; i < this.heightMap.length; i++) {
            this.heightMap[i] = newHeightMap[i];
         }
      }
   }

   public Map<BlockPos, TileEntity> getTileEntityMap() {
      return this.chunkTileEntityMap;
   }

   public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
      return this.entityLists;
   }

   public boolean isTerrainPopulated() {
      return this.isTerrainPopulated;
   }

   public void setTerrainPopulated(boolean terrainPopulated) {
      this.isTerrainPopulated = terrainPopulated;
   }

   public boolean isLightPopulated() {
      return this.isLightPopulated;
   }

   public void setLightPopulated(boolean lightPopulated) {
      this.isLightPopulated = lightPopulated;
   }

   public void setModified(boolean modified) {
      this.isModified = modified;
   }

   public void setHasEntities(boolean hasEntitiesIn) {
      this.hasEntities = hasEntitiesIn;
   }

   public void setLastSaveTime(long saveTime) {
      this.lastSaveTime = saveTime;
   }

   public int getLowestHeight() {
      return this.heightMapMinimum;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long newInhabitedTime) {
      this.inhabitedTime = newInhabitedTime;
   }

   public enum EnumCreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }
}
