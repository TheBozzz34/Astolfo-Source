package net.minecraft.client.renderer;

import java.util.ArrayDeque;
import java.util.Arrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.src.DynamicLights;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class RegionRenderCache extends ChunkCache {
   private static final IBlockState DEFAULT_STATE = Blocks.air.getDefaultState();
   private final BlockPos position;
   private int[] combinedLights;
   private IBlockState[] blockStates;
   private static ArrayDeque<int[]> cacheLights = new ArrayDeque<>();
   private static ArrayDeque<IBlockState[]> cacheStates = new ArrayDeque<>();
   private static int maxCacheSize = Config.limit(Runtime.getRuntime().availableProcessors(), 1, 32);
   private static final long b = 52916503166768L;

   public RegionRenderCache(World var1, BlockPos var2, BlockPos var3, int var4) {
      super(var1, var2, var3, var4);
      this.position = var2.subtract(new Vec3i(var4, var4, var4));
      this.combinedLights = allocateLights(8000);
      Arrays.fill(this.combinedLights, -1);
      this.blockStates = allocateStates(8000);
   }

   @Override
   public TileEntity getTileEntity(BlockPos var1) {
      int var2 = (var1.getX() >> 4) - this.chunkX;
      int var3 = (var1.getZ() >> 4) - this.chunkZ;
      return this.chunkArray[var2][var3].getTileEntity(var1, Chunk.EnumCreateEntityType.QUEUED);
   }

   @Override
   public int getCombinedLight(BlockPos var1, int var2) {
      int var6 = this.getPositionIndex(var1);
      int var7 = this.combinedLights[var6];
      if (var7 == -1) {
         var7 = super.getCombinedLight(var1, var2);
         if (Config.isDynamicLights() && !this.getBlockState(var1).getBlock().isOpaqueCube()) {
            var7 = DynamicLights.getCombinedLight(var1, var7);
         }

         this.combinedLights[var6] = var7;
      }

      return var7;
   }

   @Override
   public IBlockState getBlockState(BlockPos var1) {
      int var5 = this.getPositionIndex(var1);
      IBlockState var6 = this.blockStates[var5];
      if (var6 == null) {
         var6 = this.getBlockStateRaw(var1);
         this.blockStates[var5] = var6;
      }

      return var6;
   }

   private IBlockState getBlockStateRaw(BlockPos var1) {
      if (var1.getY() >= 0 && var1.getY() < 256) {
         int var5 = (var1.getX() >> 4) - this.chunkX;
         int var6 = (var1.getZ() >> 4) - this.chunkZ;
         return this.chunkArray[var5][var6].getBlockState(var1);
      } else {
         return DEFAULT_STATE;
      }
   }

   private int getPositionIndex(BlockPos var1) {
      int var4 = var1.getX() - this.position.getX();
      int var5 = var1.getY() - this.position.getY();
      int var6 = var1.getZ() - this.position.getZ();
      return var4 * 400 + var6 * 20 + var5;
   }

   public void freeBuffers() {
      freeLights(this.combinedLights);
      freeStates(this.blockStates);
   }

   private static int[] allocateLights(int var0) {
      synchronized (cacheLights) {
         int[] var5 = cacheLights.pollLast();
         if (var5 == null || var5.length < var0) {
            var5 = new int[var0];
         }

         return var5;
      }
   }

   public static void freeLights(int[] var0) {
      synchronized (cacheLights) {
         if (cacheLights.size() < maxCacheSize) {
            cacheLights.add(var0);
         }
      }
   }

   private static IBlockState[] allocateStates(int var0) {
      synchronized (cacheStates) {
         IBlockState[] var5 = cacheStates.pollLast();
         if (var5 != null && var5.length >= var0) {
            Arrays.fill(var5, null);
         } else {
            var5 = new IBlockState[var0];
         }

         return var5;
      }
   }

   public static void freeStates(IBlockState[] var0) {
      synchronized (cacheStates) {
         if (cacheStates.size() < maxCacheSize) {
            cacheStates.add(var0);
         }
      }
   }
}
