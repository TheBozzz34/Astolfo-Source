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
   private static final long c = 116391355854384L;

   public Chunk(World var1, int var2, int var3) {
      this.entityLists = new ClassInheritanceMultiMap[16];
      this.worldObj = var1;
      this.xPosition = var2;
      this.zPosition = var3;
      this.heightMap = new int[256];

      for (int var7 = 0; var7 < this.entityLists.length; var7++) {
         this.entityLists[var7] = new ClassInheritanceMultiMap<>(Entity.class);
      }

      Arrays.fill(this.precipitationHeightMap, -999);
      Arrays.fill(this.blockBiomeArray, (byte)-1);
   }

   public Chunk(World var1, ChunkPrimer var2, int var3, int var4) {
      this(var1, var3, var4);
      short var8 = 256;
      boolean var9 = !var1.provider.getHasNoSky();

      for (int var10 = 0; var10 < 16; var10++) {
         for (int var11 = 0; var11 < 16; var11++) {
            for (int var12 = 0; var12 < var8; var12++) {
               int var13 = var10 * var8 * 16 | var11 * var8 | var12;
               IBlockState var14 = var2.getBlockState(var13);
               if (var14.getBlock().getMaterial() != Material.air) {
                  int var15 = var12 >> 4;
                  if (this.storageArrays[var15] == null) {
                     this.storageArrays[var15] = new ExtendedBlockStorage(var15 << 4, var9);
                  }

                  this.storageArrays[var15].set(var10, var12 & 15, var11, var14);
               }
            }
         }
      }
   }

   public boolean isAtLocation(int var1, int var2) {
      return var1 == this.xPosition && var2 == this.zPosition;
   }

   public int getHeight(BlockPos var1) {
      return this.getHeightValue(var1.getX() & 15, var1.getZ() & 15);
   }

   public int getHeightValue(int var1, int var2) {
      return this.heightMap[var2 << 4 | var1];
   }

   public int getTopFilledSegment() {
      for (int var4 = this.storageArrays.length - 1; var4 >= 0; var4--) {
         if (this.storageArrays[var4] != null) {
            return this.storageArrays[var4].getYLocation();
         }
      }

      return 0;
   }

   public ExtendedBlockStorage[] getBlockStorageArray() {
      return this.storageArrays;
   }

   public void generateHeightMap() {
      int var4 = this.getTopFilledSegment();
      this.heightMapMinimum = Integer.MAX_VALUE;

      for (int var5 = 0; var5 < 16; var5++) {
         for (int var6 = 0; var6 < 16; var6++) {
            this.precipitationHeightMap[var5 + (var6 << 4)] = -999;

            for (int var7 = var4 + 16; var7 > 0; var7--) {
               Block var8 = this.getBlock0(var5, var7 - 1, var6);
               if (var8.getLightOpacity() != 0) {
                  this.heightMap[var6 << 4 | var5] = var7;
                  if (var7 < this.heightMapMinimum) {
                     this.heightMapMinimum = var7;
                  }
                  break;
               }
            }
         }
      }

      this.isModified = true;
   }

   public void generateSkylightMap() {
      int var4 = this.getTopFilledSegment();
      this.heightMapMinimum = Integer.MAX_VALUE;

      for (int var5 = 0; var5 < 16; var5++) {
         for (int var6 = 0; var6 < 16; var6++) {
            this.precipitationHeightMap[var5 + (var6 << 4)] = -999;

            for (int var7 = var4 + 16; var7 > 0; var7--) {
               if (this.getBlockLightOpacity(var5, var7 - 1, var6) != 0) {
                  this.heightMap[var6 << 4 | var5] = var7;
                  if (var7 < this.heightMapMinimum) {
                     this.heightMapMinimum = var7;
                  }
                  break;
               }
            }

            if (!this.worldObj.provider.getHasNoSky()) {
               int var11 = 15;
               int var8 = var4 + 16 - 1;

               while (true) {
                  int var9 = this.getBlockLightOpacity(var5, var8, var6);
                  if (var9 == 0 && var11 != 15) {
                     var9 = 1;
                  }

                  var11 -= var9;
                  if (var11 > 0) {
                     ExtendedBlockStorage var10 = this.storageArrays[var8 >> 4];
                     if (var10 != null) {
                        var10.setExtSkylightValue(var5, var8 & 15, var6, var11);
                        this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + var5, var8, (this.zPosition << 4) + var6));
                     }
                  }

                  if (--var8 <= 0 || var11 <= 0) {
                     break;
                  }
               }
            }
         }
      }

      this.isModified = true;
   }

   private void propagateSkylightOcclusion(int var1, int var2) {
      this.updateSkylightColumns[var1 + var2 * 16] = true;
      this.isGapLightingUpdated = true;
   }

   private void recheckGaps(boolean var1) {
      this.worldObj.theProfiler.startSection("recheckGaps");
      if (this.worldObj.isAreaLoaded(new BlockPos(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8), 16)) {
         for (int var5 = 0; var5 < 16; var5++) {
            for (int var6 = 0; var6 < 16; var6++) {
               if (this.updateSkylightColumns[var5 + var6 * 16]) {
                  this.updateSkylightColumns[var5 + var6 * 16] = false;
                  int var7 = this.getHeightValue(var5, var6);
                  int var8 = this.xPosition * 16 + var5;
                  int var9 = this.zPosition * 16 + var6;
                  int var10 = Integer.MAX_VALUE;

                  for (Object var12 : EnumFacing.Plane.HORIZONTAL) {
                     var10 = Math.min(
                        var10, this.worldObj.getChunksLowestHorizon(var8 + ((EnumFacing)var12).getFrontOffsetX(), var9 + ((EnumFacing)var12).getFrontOffsetZ())
                     );
                  }

                  this.checkSkylightNeighborHeight(var8, var9, var10);

                  for (Object var14 : EnumFacing.Plane.HORIZONTAL) {
                     this.checkSkylightNeighborHeight(var8 + ((EnumFacing)var14).getFrontOffsetX(), var9 + ((EnumFacing)var14).getFrontOffsetZ(), var7);
                  }

                  if (var1) {
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

   private void checkSkylightNeighborHeight(int var1, int var2, int var3) {
      int var7 = this.worldObj.getHeight(new BlockPos(var1, 0, var2)).getY();
      if (var7 > var3) {
         this.updateSkylightNeighborHeight(var1, var2, var3, var7 + 1);
      } else if (var7 < var3) {
         this.updateSkylightNeighborHeight(var1, var2, var7, var3 + 1);
      }
   }

   private void updateSkylightNeighborHeight(int var1, int var2, int var3, int var4) {
      if (var4 > var3 && this.worldObj.isAreaLoaded(new BlockPos(var1, 0, var2), 16)) {
         for (int var8 = var3; var8 < var4; var8++) {
            this.worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(var1, var8, var2));
         }

         this.isModified = true;
      }
   }

   private void relightBlock(int var1, int var2, int var3) {
      int var7 = this.heightMap[var3 << 4 | var1] & 0xFF;
      int var8 = var7;
      if (var2 > var7) {
         var8 = var2;
      }

      while (var8 > 0 && this.getBlockLightOpacity(var1, var8 - 1, var3) == 0) {
         var8--;
      }

      if (var8 != var7) {
         this.worldObj.markBlocksDirtyVertical(var1 + this.xPosition * 16, var3 + this.zPosition * 16, var8, var7);
         this.heightMap[var3 << 4 | var1] = var8;
         int var9 = this.xPosition * 16 + var1;
         int var10 = this.zPosition * 16 + var3;
         if (!this.worldObj.provider.getHasNoSky()) {
            if (var8 < var7) {
               for (int var11 = var8; var11 < var7; var11++) {
                  ExtendedBlockStorage var12 = this.storageArrays[var11 >> 4];
                  if (var12 != null) {
                     var12.setExtSkylightValue(var1, var11 & 15, var3, 15);
                     this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + var1, var11, (this.zPosition << 4) + var3));
                  }
               }
            } else {
               for (int var16 = var7; var16 < var8; var16++) {
                  ExtendedBlockStorage var19 = this.storageArrays[var16 >> 4];
                  if (var19 != null) {
                     var19.setExtSkylightValue(var1, var16 & 15, var3, 0);
                     this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + var1, var16, (this.zPosition << 4) + var3));
                  }
               }
            }

            int var17 = 15;

            while (var8 > 0 && var17 > 0) {
               int var20 = this.getBlockLightOpacity(var1, --var8, var3);
               if (var20 == 0) {
                  var20 = 1;
               }

               var17 -= var20;
               if (var17 < 0) {
                  var17 = 0;
               }

               ExtendedBlockStorage var13 = this.storageArrays[var8 >> 4];
               if (var13 != null) {
                  var13.setExtSkylightValue(var1, var8 & 15, var3, var17);
               }
            }
         }

         int var18 = this.heightMap[var3 << 4 | var1];
         int var21 = var7;
         int var22 = var18;
         if (var18 < var7) {
            var21 = var18;
            var22 = var7;
         }

         if (var18 < this.heightMapMinimum) {
            this.heightMapMinimum = var18;
         }

         if (!this.worldObj.provider.getHasNoSky()) {
            for (Object var15 : EnumFacing.Plane.HORIZONTAL) {
               this.updateSkylightNeighborHeight(var9 + ((EnumFacing)var15).getFrontOffsetX(), var10 + ((EnumFacing)var15).getFrontOffsetZ(), var21, var22);
            }

            this.updateSkylightNeighborHeight(var9, var10, var21, var22);
         }

         this.isModified = true;
      }
   }

   public int getBlockLightOpacity(BlockPos var1) {
      return this.getBlock(var1).getLightOpacity();
   }

   private int getBlockLightOpacity(int var1, int var2, int var3) {
      return this.getBlock0(var1, var2, var3).getLightOpacity();
   }

   private Block getBlock0(int var1, int var2, int var3) {
      Block var7 = Blocks.air;
      if (var2 >= 0 && var2 >> 4 < this.storageArrays.length) {
         ExtendedBlockStorage var8 = this.storageArrays[var2 >> 4];
         if (var8 != null) {
            try {
               var7 = var8.getBlockByExtId(var1, var2 & 15, var3);
            } catch (Throwable var11) {
               CrashReport var10 = CrashReport.makeCrashReport(var11, "Getting block");
               throw new ReportedException(var10);
            }
         }
      }

      return var7;
   }

   public Block getBlock(final int var1, final int var2, final int var3) {
      try {
         return this.getBlock0(var1 & 15, var2, var3 & 15);
      } catch (ReportedException var8) {
         CrashReportCategory var7 = var8.getCrashReport().makeCategory("Block being got");
         var7.addCrashSectionCallable("Location", new Callable<String>() {
            private static final long a = 11573433213160L;

            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(new BlockPos(Chunk.this.xPosition * 16 + var1, var2, Chunk.this.zPosition * 16 + var3));
            }
         });
         throw var8;
      }
   }

   public Block getBlock(final BlockPos var1) {
      try {
         return this.getBlock0(var1.getX() & 15, var1.getY(), var1.getZ() & 15);
      } catch (ReportedException var6) {
         CrashReportCategory var5 = var6.getCrashReport().makeCategory("Block being got");
         var5.addCrashSectionCallable("Location", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(var1);
            }
         });
         throw var6;
      }
   }

   public IBlockState getBlockState(final BlockPos var1) {
      if (this.worldObj.getWorldType() == WorldType.DEBUG_WORLD) {
         IBlockState var10 = null;
         if (var1.getY() == 60) {
            var10 = Blocks.barrier.getDefaultState();
         }

         if (var1.getY() == 70) {
            var10 = ChunkProviderDebug.func_177461_b(var1.getX(), var1.getZ());
         }

         return var10 == null ? Blocks.air.getDefaultState() : var10;
      } else {
         try {
            if (var1.getY() >= 0 && var1.getY() >> 4 < this.storageArrays.length) {
               ExtendedBlockStorage var5 = this.storageArrays[var1.getY() >> 4];
               if (var5 != null) {
                  int var11 = var1.getX() & 15;
                  int var12 = var1.getY() & 15;
                  int var8 = var1.getZ() & 15;
                  return var5.get(var11, var12, var8);
               }
            }

            return Blocks.air.getDefaultState();
         } catch (Throwable var9) {
            CrashReport var6 = CrashReport.makeCrashReport(var9, "Getting block state");
            CrashReportCategory var7 = var6.makeCategory("Block being got");
            var7.addCrashSectionCallable("Location", new Callable<String>() {
               public String call() throws Exception {
                  return CrashReportCategory.getCoordinateInfo(var1);
               }
            });
            throw new ReportedException(var6);
         }
      }
   }

   private int getBlockMetadata(int var1, int var2, int var3) {
      if (var2 >> 4 >= this.storageArrays.length) {
         return 0;
      }

      ExtendedBlockStorage var7 = this.storageArrays[var2 >> 4];
      return var7 != null ? var7.getExtBlockMetadata(var1, var2 & 15, var3) : 0;
   }

   public int getBlockMetadata(BlockPos var1) {
      return this.getBlockMetadata(var1.getX() & 15, var1.getY(), var1.getZ() & 15);
   }

   public IBlockState setBlockState(BlockPos var1, IBlockState var2) {
      int var6 = var1.getX() & 15;
      int var7 = var1.getY();
      int var8 = var1.getZ() & 15;
      int var9 = var8 << 4 | var6;
      if (var7 >= this.precipitationHeightMap[var9] - 1) {
         this.precipitationHeightMap[var9] = -999;
      }

      int var10 = this.heightMap[var9];
      IBlockState var11 = this.getBlockState(var1);
      if (var11 == var2) {
         return null;
      }

      Block var12 = var2.getBlock();
      Block var13 = var11.getBlock();
      ExtendedBlockStorage var14 = this.storageArrays[var7 >> 4];
      boolean var15 = false;
      if (var14 == null) {
         if (var12 == Blocks.air) {
            return null;
         }

         var14 = this.storageArrays[var7 >> 4] = new ExtendedBlockStorage(var7 >> 4 << 4, !this.worldObj.provider.getHasNoSky());
         var15 = var7 >= var10;
      }

      var14.set(var6, var7 & 15, var8, var2);
      if (var13 != var12) {
         if (!this.worldObj.isRemote) {
            var13.breakBlock(this.worldObj, var1, var11);
         } else if (var13 instanceof ITileEntityProvider) {
            this.worldObj.removeTileEntity(var1);
         }
      }

      if (var14.getBlockByExtId(var6, var7 & 15, var8) != var12) {
         return null;
      }

      if (var15) {
         this.generateSkylightMap();
      } else {
         int var16 = var12.getLightOpacity();
         int var17 = var13.getLightOpacity();
         if (var16 > 0) {
            if (var7 >= var10) {
               this.relightBlock(var6, var7 + 1, var8);
            }
         } else if (var7 == var10 - 1) {
            this.relightBlock(var6, var7, var8);
         }

         if (var16 != var17 && (var16 < var17 || this.getLightFor(EnumSkyBlock.SKY, var1) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, var1) > 0)) {
            this.propagateSkylightOcclusion(var6, var8);
         }
      }

      if (var13 instanceof ITileEntityProvider) {
         TileEntity var18 = this.getTileEntity(var1, Chunk.EnumCreateEntityType.CHECK);
         if (var18 != null) {
            var18.updateContainingBlockInfo();
         }
      }

      if (!this.worldObj.isRemote && var13 != var12) {
         var12.onBlockAdded(this.worldObj, var1, var2);
      }

      if (var12 instanceof ITileEntityProvider) {
         TileEntity var19 = this.getTileEntity(var1, Chunk.EnumCreateEntityType.CHECK);
         if (var19 == null) {
            var19 = ((ITileEntityProvider)var12).createNewTileEntity(this.worldObj, var12.getMetaFromState(var2));
            this.worldObj.setTileEntity(var1, var19);
         }

         if (var19 != null) {
            var19.updateContainingBlockInfo();
         }
      }

      this.isModified = true;
      return var11;
   }

   public int getLightFor(EnumSkyBlock var1, BlockPos var2) {
      int var6 = var2.getX() & 15;
      int var7 = var2.getY();
      int var8 = var2.getZ() & 15;
      ExtendedBlockStorage var9 = this.storageArrays[var7 >> 4];
      return var9 == null
         ? (this.canSeeSky(var2) ? var1.defaultLightValue : 0)
         : (
            var1 == EnumSkyBlock.SKY
               ? (this.worldObj.provider.getHasNoSky() ? 0 : var9.getExtSkylightValue(var6, var7 & 15, var8))
               : (var1 == EnumSkyBlock.BLOCK ? var9.getExtBlocklightValue(var6, var7 & 15, var8) : var1.defaultLightValue)
         );
   }

   public void setLightFor(EnumSkyBlock var1, BlockPos var2, int var3) {
      int var7 = var2.getX() & 15;
      int var8 = var2.getY();
      int var9 = var2.getZ() & 15;
      ExtendedBlockStorage var10 = this.storageArrays[var8 >> 4];
      if (var10 == null) {
         var10 = this.storageArrays[var8 >> 4] = new ExtendedBlockStorage(var8 >> 4 << 4, !this.worldObj.provider.getHasNoSky());
         this.generateSkylightMap();
      }

      this.isModified = true;
      if (var1 == EnumSkyBlock.SKY) {
         if (!this.worldObj.provider.getHasNoSky()) {
            var10.setExtSkylightValue(var7, var8 & 15, var9, var3);
         }
      } else if (var1 == EnumSkyBlock.BLOCK) {
         var10.setExtBlocklightValue(var7, var8 & 15, var9, var3);
      }
   }

   public int getLightSubtracted(BlockPos var1, int var2) {
      int var6 = var1.getX() & 15;
      int var7 = var1.getY();
      int var8 = var1.getZ() & 15;
      ExtendedBlockStorage var9 = this.storageArrays[var7 >> 4];
      if (var9 != null) {
         int var10 = this.worldObj.provider.getHasNoSky() ? 0 : var9.getExtSkylightValue(var6, var7 & 15, var8);
         var10 -= var2;
         int var11 = var9.getExtBlocklightValue(var6, var7 & 15, var8);
         if (var11 > var10) {
            var10 = var11;
         }

         return var10;
      } else {
         return !this.worldObj.provider.getHasNoSky() && var2 < EnumSkyBlock.SKY.defaultLightValue ? EnumSkyBlock.SKY.defaultLightValue - var2 : 0;
      }
   }

   public void addEntity(Entity var1) {
      this.hasEntities = true;
      int var5 = MathHelper.floor_double(var1.posX / 16.0);
      int var6 = MathHelper.floor_double(var1.posZ / 16.0);
      if (var5 != this.xPosition || var6 != this.zPosition) {
         logger.warn("Wrong location! (" + var5 + ", " + var6 + ") should be (" + this.xPosition + ", " + this.zPosition + "), " + var1, new Object[]{var1});
         var1.setDead();
      }

      int var7 = MathHelper.floor_double(var1.posY / 16.0);
      if (var7 < 0) {
         var7 = 0;
      }

      if (var7 >= this.entityLists.length) {
         var7 = this.entityLists.length - 1;
      }

      var1.addedToChunk = true;
      var1.chunkCoordX = this.xPosition;
      var1.chunkCoordY = var7;
      var1.chunkCoordZ = this.zPosition;
      this.entityLists[var7].add(var1);
   }

   public void removeEntity(Entity var1) {
      this.removeEntityAtIndex(var1, var1.chunkCoordY);
   }

   public void removeEntityAtIndex(Entity var1, int var2) {
      if (var2 < 0) {
         var2 = 0;
      }

      if (var2 >= this.entityLists.length) {
         var2 = this.entityLists.length - 1;
      }

      this.entityLists[var2].remove(var1);
   }

   public boolean canSeeSky(BlockPos var1) {
      int var5 = var1.getX() & 15;
      int var6 = var1.getY();
      int var7 = var1.getZ() & 15;
      return var6 >= this.heightMap[var7 << 4 | var5];
   }

   private TileEntity createNewTileEntity(BlockPos var1) {
      Block var5 = this.getBlock(var1);
      return !var5.hasTileEntity() ? null : ((ITileEntityProvider)var5).createNewTileEntity(this.worldObj, this.getBlockMetadata(var1));
   }

   public TileEntity getTileEntity(BlockPos var1, Chunk.EnumCreateEntityType var2) {
      TileEntity var6 = this.chunkTileEntityMap.get(var1);
      if (var6 == null) {
         if (var2 == Chunk.EnumCreateEntityType.IMMEDIATE) {
            var6 = this.createNewTileEntity(var1);
            this.worldObj.setTileEntity(var1, var6);
         } else if (var2 == Chunk.EnumCreateEntityType.QUEUED) {
            this.tileEntityPosQueue.add(var1);
         }
      } else if (var6.isInvalid()) {
         this.chunkTileEntityMap.remove(var1);
         return null;
      }

      return var6;
   }

   public void addTileEntity(TileEntity var1) {
      this.addTileEntity(var1.getPos(), var1);
      if (this.isChunkLoaded) {
         this.worldObj.addTileEntity(var1);
      }
   }

   public void addTileEntity(BlockPos var1, TileEntity var2) {
      var2.setWorldObj(this.worldObj);
      var2.setPos(var1);
      if (this.getBlock(var1) instanceof ITileEntityProvider) {
         if (this.chunkTileEntityMap.containsKey(var1)) {
            this.chunkTileEntityMap.get(var1).invalidate();
         }

         var2.validate();
         this.chunkTileEntityMap.put(var1, var2);
      }
   }

   public void removeTileEntity(BlockPos var1) {
      if (this.isChunkLoaded) {
         TileEntity var5 = this.chunkTileEntityMap.remove(var1);
         if (var5 != null) {
            var5.invalidate();
         }
      }
   }

   public void onChunkLoad() {
      this.isChunkLoaded = true;
      this.worldObj.addTileEntities(this.chunkTileEntityMap.values());

      for (int var4 = 0; var4 < this.entityLists.length; var4++) {
         for (Entity var6 : this.entityLists[var4]) {
            var6.onChunkLoad();
         }

         this.worldObj.loadEntities(this.entityLists[var4]);
      }
   }

   public void onChunkUnload() {
      this.isChunkLoaded = false;

      for (TileEntity var5 : this.chunkTileEntityMap.values()) {
         this.worldObj.markTileEntityForRemoval(var5);
      }

      for (int var6 = 0; var6 < this.entityLists.length; var6++) {
         this.worldObj.unloadEntities(this.entityLists[var6]);
      }
   }

   public void setChunkModified() {
      this.isModified = true;
   }

   public void getEntitiesWithinAABBForEntity(Entity var1, AxisAlignedBB var2, List<Entity> var3, Predicate<? super Entity> var4) {
      int var8 = MathHelper.floor_double((var2.minY - 2.0) / 16.0);
      int var9 = MathHelper.floor_double((var2.maxY + 2.0) / 16.0);
      var8 = MathHelper.clamp_int(var8, 0, this.entityLists.length - 1);
      var9 = MathHelper.clamp_int(var9, 0, this.entityLists.length - 1);

      for (int var10 = var8; var10 <= var9; var10++) {
         if (!this.entityLists[var10].isEmpty()) {
            for (Entity var12 : this.entityLists[var10]) {
               if (var12.getEntityBoundingBox().intersectsWith(var2) && var12 != var1) {
                  if (var4 == null || var4.apply(var12)) {
                     var3.add(var12);
                  }

                  Entity[] var13 = var12.getParts();
                  if (var13 != null) {
                     for (int var14 = 0; var14 < var13.length; var14++) {
                        var12 = var13[var14];
                        if (var12 != var1 && var12.getEntityBoundingBox().intersectsWith(var2) && (var4 == null || var4.apply(var12))) {
                           var3.add(var12);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class<? extends T> var1, AxisAlignedBB var2, List<T> var3, Predicate<? super T> var4) {
      int var8 = MathHelper.floor_double((var2.minY - 2.0) / 16.0);
      int var9 = MathHelper.floor_double((var2.maxY + 2.0) / 16.0);
      var8 = MathHelper.clamp_int(var8, 0, this.entityLists.length - 1);
      var9 = MathHelper.clamp_int(var9, 0, this.entityLists.length - 1);

      for (int var10 = var8; var10 <= var9; var10++) {
         for (Entity var12 : this.entityLists[var10].getByClass(var1)) {
            if (var12.getEntityBoundingBox().intersectsWith(var2) && (var4 == null || var4.apply((T)(var12)))) {
               var3.add((T)(var12));
            }
         }
      }
   }

   public boolean needsSaving(boolean var1) {
      if (var1) {
         if (this.hasEntities && this.worldObj.getTotalWorldTime() != this.lastSaveTime || this.isModified) {
            return true;
         }
      } else if (this.hasEntities && this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L) {
         return true;
      }

      return this.isModified;
   }

   public Random getRandomWithSeed(long var1) {
      return new Random(
         this.worldObj.getSeed()
               + this.xPosition * this.xPosition * 4987142
               + this.xPosition * 5947611
               + this.zPosition * this.zPosition * 4392871L
               + this.zPosition * 389711
            ^ var1
      );
   }

   public boolean isEmpty() {
      return false;
   }

   public void populateChunk(IChunkProvider var1, IChunkProvider var2, int var3, int var4) {
      boolean var9 = var1.chunkExists(var3, var4 - 1);
      boolean var10 = var1.chunkExists(var3 + 1, var4);
      boolean var11 = var1.chunkExists(var3, var4 + 1);
      boolean var12 = var1.chunkExists(var3 - 1, var4);
      boolean var13 = var1.chunkExists(var3 - 1, var4 - 1);
      boolean var14 = var1.chunkExists(var3 + 1, var4 + 1);
      boolean var15 = var1.chunkExists(var3 - 1, var4 + 1);
      boolean var16 = var1.chunkExists(var3 + 1, var4 - 1);
      if (var10 && var11 && var14) {
         if (!this.isTerrainPopulated) {
            var1.populate(var2, var3, var4);
         } else {
            var1.populateChunk(var2, this, var3, var4);
         }
      }

      if (var12 && var11 && var15) {
         Chunk var17 = var1.provideChunk(var3 - 1, var4);
         if (!var17.isTerrainPopulated) {
            var1.populate(var2, var3 - 1, var4);
         } else {
            var1.populateChunk(var2, var17, var3 - 1, var4);
         }
      }

      if (var9 && var10 && var16) {
         Chunk var18 = var1.provideChunk(var3, var4 - 1);
         if (!var18.isTerrainPopulated) {
            var1.populate(var2, var3, var4 - 1);
         } else {
            var1.populateChunk(var2, var18, var3, var4 - 1);
         }
      }

      if (var13 && var9 && var12) {
         Chunk var19 = var1.provideChunk(var3 - 1, var4 - 1);
         if (!var19.isTerrainPopulated) {
            var1.populate(var2, var3 - 1, var4 - 1);
         } else {
            var1.populateChunk(var2, var19, var3 - 1, var4 - 1);
         }
      }
   }

   public BlockPos getPrecipitationHeight(BlockPos var1) {
      int var5 = var1.getX() & 15;
      int var6 = var1.getZ() & 15;
      int var7 = var5 | var6 << 4;
      BlockPos var8 = new BlockPos(var1.getX(), this.precipitationHeightMap[var7], var1.getZ());
      if (var8.getY() == -999) {
         int var9 = this.getTopFilledSegment() + 15;
         var8 = new BlockPos(var1.getX(), var9, var1.getZ());
         int var10 = -1;

         while (var8.getY() > 0 && var10 == -1) {
            Block var11 = this.getBlock(var8);
            Material var12 = var11.getMaterial();
            if (!var12.blocksMovement() && !var12.isLiquid()) {
               var8 = var8.down();
            } else {
               var10 = var8.getY() + 1;
            }
         }

         this.precipitationHeightMap[var7] = var10;
      }

      return new BlockPos(var1.getX(), this.precipitationHeightMap[var7], var1.getZ());
   }

   public void func_150804_b(boolean var1) {
      if (this.isGapLightingUpdated && !this.worldObj.provider.getHasNoSky() && !var1) {
         this.recheckGaps(this.worldObj.isRemote);
      }

      this.field_150815_m = true;
      if (!this.isLightPopulated && this.isTerrainPopulated) {
         this.func_150809_p();
      }

      while (!this.tileEntityPosQueue.isEmpty()) {
         BlockPos var5 = this.tileEntityPosQueue.poll();
         if (this.getTileEntity(var5, Chunk.EnumCreateEntityType.CHECK) == null && this.getBlock(var5).hasTileEntity()) {
            TileEntity var6 = this.createNewTileEntity(var5);
            this.worldObj.setTileEntity(var5, var6);
            this.worldObj.markBlockRangeForRenderUpdate(var5, var5);
         }
      }
   }

   public boolean isPopulated() {
      return this.field_150815_m && this.isTerrainPopulated && this.isLightPopulated;
   }

   public ChunkCoordIntPair getChunkCoordIntPair() {
      return new ChunkCoordIntPair(this.xPosition, this.zPosition);
   }

   public boolean getAreLevelsEmpty(int var1, int var2) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var2 >= 256) {
         var2 = 255;
      }

      for (int var6 = var1; var6 <= var2; var6 += 16) {
         ExtendedBlockStorage var7 = this.storageArrays[var6 >> 4];
         if (var7 != null && !var7.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void setStorageArrays(ExtendedBlockStorage[] var1) {
      if (this.storageArrays.length != var1.length) {
         logger.warn("Could not set level chunk sections, array length is " + var1.length + " instead of " + this.storageArrays.length);
      } else {
         for (int var5 = 0; var5 < this.storageArrays.length; var5++) {
            this.storageArrays[var5] = var1[var5];
         }
      }
   }

   public void fillChunk(byte[] var1, int var2, boolean var3) {
      int var7 = 0;
      boolean var8 = !this.worldObj.provider.getHasNoSky();

      for (int var9 = 0; var9 < this.storageArrays.length; var9++) {
         if ((var2 & 1 << var9) != 0) {
            if (this.storageArrays[var9] == null) {
               this.storageArrays[var9] = new ExtendedBlockStorage(var9 << 4, var8);
            }

            char[] var10 = this.storageArrays[var9].getData();

            for (int var11 = 0; var11 < var10.length; var11++) {
               var10[var11] = (char)((var1[var7 + 1] & 255) << 8 | var1[var7] & 0xFF);
               var7 += 2;
            }
         } else if (var3 && this.storageArrays[var9] != null) {
            this.storageArrays[var9] = null;
         }
      }

      for (int var12 = 0; var12 < this.storageArrays.length; var12++) {
         if ((var2 & 1 << var12) != 0 && this.storageArrays[var12] != null) {
            NibbleArray var17 = this.storageArrays[var12].getBlocklightArray();
            System.arraycopy(var1, var7, var17.getData(), 0, var17.getData().length);
            var7 += var17.getData().length;
         }
      }

      if (var8) {
         for (int var13 = 0; var13 < this.storageArrays.length; var13++) {
            if ((var2 & 1 << var13) != 0 && this.storageArrays[var13] != null) {
               NibbleArray var18 = this.storageArrays[var13].getSkylightArray();
               System.arraycopy(var1, var7, var18.getData(), 0, var18.getData().length);
               var7 += var18.getData().length;
            }
         }
      }

      if (var3) {
         System.arraycopy(var1, var7, this.blockBiomeArray, 0, this.blockBiomeArray.length);
         int var14 = var7 + this.blockBiomeArray.length;
      }

      for (int var15 = 0; var15 < this.storageArrays.length; var15++) {
         if (this.storageArrays[var15] != null && (var2 & 1 << var15) != 0) {
            this.storageArrays[var15].removeInvalidBlocks();
         }
      }

      this.isLightPopulated = true;
      this.isTerrainPopulated = true;
      this.generateHeightMap();

      for (TileEntity var19 : this.chunkTileEntityMap.values()) {
         var19.updateContainingBlockInfo();
      }
   }

   public BiomeGenBase getBiome(BlockPos var1, WorldChunkManager var2) {
      int var6 = var1.getX() & 15;
      int var7 = var1.getZ() & 15;
      int var8 = this.blockBiomeArray[var7 << 4 | var6] & 255;
      if (var8 == 255) {
         BiomeGenBase var9 = var2.getBiomeGenerator(var1, BiomeGenBase.plains);
         var8 = var9.biomeID;
         this.blockBiomeArray[var7 << 4 | var6] = (byte)(var8 & 0xFF);
      }

      BiomeGenBase var10 = BiomeGenBase.getBiome(var8);
      return var10 == null ? BiomeGenBase.plains : var10;
   }

   public byte[] getBiomeArray() {
      return this.blockBiomeArray;
   }

   public void setBiomeArray(byte[] var1) {
      if (this.blockBiomeArray.length != var1.length) {
         logger.warn("Could not set level chunk biomes, array length is " + var1.length + " instead of " + this.blockBiomeArray.length);
      } else {
         for (int var5 = 0; var5 < this.blockBiomeArray.length; var5++) {
            this.blockBiomeArray[var5] = var1[var5];
         }
      }
   }

   public void resetRelightChecks() {
      this.queuedLightChecks = 0;
   }

   public void enqueueRelightChecks() {
      BlockPos var4 = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);

      for (int var5 = 0; var5 < 8; var5++) {
         if (this.queuedLightChecks >= 4096) {
            return;
         }

         int var6 = this.queuedLightChecks % 16;
         int var7 = this.queuedLightChecks / 16 % 16;
         int var8 = this.queuedLightChecks / 256;
         this.queuedLightChecks++;

         for (int var9 = 0; var9 < 16; var9++) {
            BlockPos var10 = var4.add(var7, (var6 << 4) + var9, var8);
            boolean var11 = var9 == 0 || var9 == 15 || var7 == 0 || var7 == 15 || var8 == 0 || var8 == 15;
            if (this.storageArrays[var6] == null && var11
               || this.storageArrays[var6] != null && this.storageArrays[var6].getBlockByExtId(var7, var9, var8).getMaterial() == Material.air) {
               for (EnumFacing var15 : EnumFacing.values()) {
                  BlockPos var16 = var10.offset(var15);
                  if (this.worldObj.getBlockState(var16).getBlock().getLightValue() > 0) {
                     this.worldObj.checkLight(var16);
                  }
               }

               this.worldObj.checkLight(var10);
            }
         }
      }
   }

   public void func_150809_p() {
      this.isTerrainPopulated = true;
      this.isLightPopulated = true;
      BlockPos var4 = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);
      if (!this.worldObj.provider.getHasNoSky()) {
         if (this.worldObj.isAreaLoaded(var4.add(-1, 0, -1), var4.add(16, this.worldObj.getSeaLevel(), 16))) {
            label44:
            for (int var5 = 0; var5 < 16; var5++) {
               for (int var6 = 0; var6 < 16; var6++) {
                  if (!this.func_150811_f(var5, var6)) {
                     this.isLightPopulated = false;
                     break label44;
                  }
               }
            }

            if (this.isLightPopulated) {
               for (Object var9 : EnumFacing.Plane.HORIZONTAL) {
                  int var7 = ((EnumFacing)var9).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 16 : 1;
                  this.worldObj.getChunkFromBlockCoords(var4.offset((EnumFacing)var9, var7)).func_180700_a(((EnumFacing)var9).getOpposite());
               }

               this.func_177441_y();
            }
         } else {
            this.isLightPopulated = false;
         }
      }
   }

   private void func_177441_y() {
      for (int var4 = 0; var4 < this.updateSkylightColumns.length; var4++) {
         this.updateSkylightColumns[var4] = true;
      }

      this.recheckGaps(false);
   }

   private void func_180700_a(EnumFacing var1) {
      if (this.isTerrainPopulated) {
         if (var1 == EnumFacing.EAST) {
            for (int var5 = 0; var5 < 16; var5++) {
               this.func_150811_f(15, var5);
            }
         } else if (var1 == EnumFacing.WEST) {
            for (int var6 = 0; var6 < 16; var6++) {
               this.func_150811_f(0, var6);
            }
         } else if (var1 == EnumFacing.SOUTH) {
            for (int var7 = 0; var7 < 16; var7++) {
               this.func_150811_f(var7, 15);
            }
         } else if (var1 == EnumFacing.NORTH) {
            for (int var8 = 0; var8 < 16; var8++) {
               this.func_150811_f(var8, 0);
            }
         }
      }
   }

   private boolean func_150811_f(int var1, int var2) {
      int var6 = this.getTopFilledSegment();
      boolean var7 = false;
      boolean var8 = false;
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos((this.xPosition << 4) + var1, 0, (this.zPosition << 4) + var2);

      for (int var10 = var6 + 16 - 1; var10 > this.worldObj.getSeaLevel() || var10 > 0 && !var8; var10--) {
         var9.set(var9.getX(), var10, var9.getZ());
         int var11 = this.getBlockLightOpacity(var9);
         if (var11 == 255 && var9.getY() < this.worldObj.getSeaLevel()) {
            var8 = true;
         }

         if (!var7 && var11 > 0) {
            var7 = true;
         } else if (var7 && var11 == 0 && !this.worldObj.checkLight(var9)) {
            return false;
         }
      }

      for (int var12 = var9.getY(); var12 > 0; var12--) {
         var9.set(var9.getX(), var12, var9.getZ());
         if (this.getBlock(var9).getLightValue() > 0) {
            this.worldObj.checkLight(var9);
         }
      }

      return true;
   }

   public boolean isLoaded() {
      return this.isChunkLoaded;
   }

   public void setChunkLoaded(boolean var1) {
      this.isChunkLoaded = var1;
   }

   public World getWorld() {
      return this.worldObj;
   }

   public int[] getHeightMap() {
      return this.heightMap;
   }

   public void setHeightMap(int[] var1) {
      if (this.heightMap.length != var1.length) {
         logger.warn("Could not set level chunk heightmap, array length is " + var1.length + " instead of " + this.heightMap.length);
      } else {
         for (int var5 = 0; var5 < this.heightMap.length; var5++) {
            this.heightMap[var5] = var1[var5];
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

   public void setTerrainPopulated(boolean var1) {
      this.isTerrainPopulated = var1;
   }

   public boolean isLightPopulated() {
      return this.isLightPopulated;
   }

   public void setLightPopulated(boolean var1) {
      this.isLightPopulated = var1;
   }

   public void setModified(boolean var1) {
      this.isModified = var1;
   }

   public void setHasEntities(boolean var1) {
      this.hasEntities = var1;
   }

   public void setLastSaveTime(long var1) {
      this.lastSaveTime = var1;
   }

   public int getLowestHeight() {
      return this.heightMapMinimum;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long var1) {
      this.inhabitedTime = var1;
   }

   public enum EnumCreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }
}
