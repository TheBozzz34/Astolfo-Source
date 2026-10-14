package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ViewFrustum {
   public final RenderGlobal renderGlobal;
   public final World world;
   public int countChunksY;
   public int countChunksX;
   public int countChunksZ;
   public RenderChunk[] renderChunks;
   private static final long a = 76452716329873L;

   public ViewFrustum(World worldIn, int renderDistanceChunks, RenderGlobal renderGlobal, IRenderChunkFactory renderChunkFactory) {
      this.renderGlobal = renderGlobal;
      this.world = worldIn;
      this.setCountChunksXYZ(renderDistanceChunks);
      this.createRenderChunks(renderChunkFactory);
   }

   public void createRenderChunks(IRenderChunkFactory renderChunkFactory) {
      int i = this.countChunksX * this.countChunksY * this.countChunksZ;
      this.renderChunks = new RenderChunk[i];
      int j = 0;

      for (int k = 0; k < this.countChunksX; k++) {
         for (int l = 0; l < this.countChunksY; l++) {
            for (int m = 0; m < this.countChunksZ; m++) {
               int n = (m * this.countChunksY + l) * this.countChunksX + k;
               BlockPos blockPos = new BlockPos(k * 16, l * 16, m * 16);
               this.renderChunks[n] = renderChunkFactory.makeRenderChunk(this.world, this.renderGlobal, blockPos, j++);
            }
         }
      }
   }

   public void deleteGlResources() {
      for (RenderChunk renderChunk : this.renderChunks) {
         renderChunk.deleteGlResources();
      }
   }

   public void setCountChunksXYZ(int renderDistanceChunks) {
      int i = renderDistanceChunks * 2 + 1;
      this.countChunksX = i;
      this.countChunksY = 16;
      this.countChunksZ = i;
   }

   public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
      int i = MathHelper.floor_double(viewEntityX) - 8;
      int j = MathHelper.floor_double(viewEntityZ) - 8;
      int k = this.countChunksX * 16;

      for (int l = 0; l < this.countChunksX; l++) {
         int m = this.func_178157_a(i, k, l);

         for (int n = 0; n < this.countChunksZ; n++) {
            int i2 = this.func_178157_a(j, k, n);

            for (int i3 = 0; i3 < this.countChunksY; i3++) {
               int i4 = i3 * 16;
               RenderChunk renderChunk = this.renderChunks[(n * this.countChunksY + i3) * this.countChunksX + l];
               BlockPos blockPos = renderChunk.getPosition();
               if (blockPos.getX() != m || blockPos.getY() != i4 || blockPos.getZ() != i2) {
                  BlockPos blockPos2 = new BlockPos(m, i4, i2);
                  if (!blockPos2.equals(renderChunk.getPosition())) {
                     renderChunk.setPosition(blockPos2);
                  }
               }
            }
         }
      }
   }

   private int func_178157_a(int i, int j, int k) {
      int l = k * 16;
      int m = l - i + j / 2;
      if (m < 0) {
         m -= j - 1;
      }

      return l - m / j * j;
   }

   public void markBlocksForUpdate(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
      int i = MathHelper.bucketInt(fromX, 16);
      int j = MathHelper.bucketInt(fromY, 16);
      int k = MathHelper.bucketInt(fromZ, 16);
      int l = MathHelper.bucketInt(toX, 16);
      int m = MathHelper.bucketInt(toY, 16);
      int n = MathHelper.bucketInt(toZ, 16);

      for (int i2 = i; i2 <= l; i2++) {
         int i3 = i2 % this.countChunksX;
         if (i3 < 0) {
            i3 += this.countChunksX;
         }

         for (int i4 = j; i4 <= m; i4++) {
            int i5 = i4 % this.countChunksY;
            if (i5 < 0) {
               i5 += this.countChunksY;
            }

            for (int i6 = k; i6 <= n; i6++) {
               int i7 = i6 % this.countChunksZ;
               if (i7 < 0) {
                  i7 += this.countChunksZ;
               }

               int i8 = (i7 * this.countChunksY + i5) * this.countChunksX + i3;
               RenderChunk renderChunk = this.renderChunks[i8];
               renderChunk.setNeedsUpdate(true);
            }
         }
      }
   }

   public RenderChunk getRenderChunk(BlockPos pos) {
      int i = pos.getX() >> 4;
      int j = pos.getY() >> 4;
      int k = pos.getZ() >> 4;
      if (j >= 0 && j < this.countChunksY) {
         i %= this.countChunksX;
         if (i < 0) {
            i += this.countChunksX;
         }

         k %= this.countChunksZ;
         if (k < 0) {
            k += this.countChunksZ;
         }

         int l = (k * this.countChunksY + j) * this.countChunksX + i;
         return this.renderChunks[l];
      } else {
         return null;
      }
   }
}
