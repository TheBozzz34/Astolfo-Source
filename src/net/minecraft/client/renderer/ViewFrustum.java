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

   public ViewFrustum(World var1, int var2, RenderGlobal var3, IRenderChunkFactory var4) {
      this.renderGlobal = var3;
      this.world = var1;
      this.setCountChunksXYZ(var2);
      this.createRenderChunks(var4);
   }

   public void createRenderChunks(IRenderChunkFactory var1) {
      int var5 = this.countChunksX * this.countChunksY * this.countChunksZ;
      this.renderChunks = new RenderChunk[var5];
      int var6 = 0;

      for (int var7 = 0; var7 < this.countChunksX; var7++) {
         for (int var8 = 0; var8 < this.countChunksY; var8++) {
            for (int var9 = 0; var9 < this.countChunksZ; var9++) {
               int var10 = (var9 * this.countChunksY + var8) * this.countChunksX + var7;
               BlockPos var11 = new BlockPos(var7 * 16, var8 * 16, var9 * 16);
               this.renderChunks[var10] = var1.makeRenderChunk(this.world, this.renderGlobal, var11, var6++);
            }
         }
      }
   }

   public void deleteGlResources() {
      for (RenderChunk var7 : this.renderChunks) {
         var7.deleteGlResources();
      }
   }

   public void setCountChunksXYZ(int var1) {
      int var4 = var1 * 2 + 1;
      this.countChunksX = var4;
      this.countChunksY = 16;
      this.countChunksZ = var4;
   }

   public void updateChunkPositions(double var1, double var3) {
      int var8 = MathHelper.floor_double(var1) - 8;
      int var9 = MathHelper.floor_double(var3) - 8;
      int var10 = this.countChunksX * 16;

      for (int var11 = 0; var11 < this.countChunksX; var11++) {
         int var12 = this.func_178157_a(var8, var10, var11);

         for (int var13 = 0; var13 < this.countChunksZ; var13++) {
            int var14 = this.func_178157_a(var9, var10, var13);

            for (int var15 = 0; var15 < this.countChunksY; var15++) {
               int var16 = var15 * 16;
               RenderChunk var17 = this.renderChunks[(var13 * this.countChunksY + var15) * this.countChunksX + var11];
               BlockPos var18 = var17.getPosition();
               if (var18.getX() != var12 || var18.getY() != var16 || var18.getZ() != var14) {
                  BlockPos var19 = new BlockPos(var12, var16, var14);
                  if (!var19.equals(var17.getPosition())) {
                     var17.setPosition(var19);
                  }
               }
            }
         }
      }
   }

   private int func_178157_a(int var1, int var2, int var3) {
      int var7 = var3 * 16;
      int var8 = var7 - var1 + var2 / 2;
      if (var8 < 0) {
         var8 -= var2 - 1;
      }

      return var7 - var8 / var2 * var2;
   }

   public void markBlocksForUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
      int var10 = MathHelper.bucketInt(var1, 16);
      int var11 = MathHelper.bucketInt(var2, 16);
      int var12 = MathHelper.bucketInt(var3, 16);
      int var13 = MathHelper.bucketInt(var4, 16);
      int var14 = MathHelper.bucketInt(var5, 16);
      int var15 = MathHelper.bucketInt(var6, 16);

      for (int var16 = var10; var16 <= var13; var16++) {
         int var17 = var16 % this.countChunksX;
         if (var17 < 0) {
            var17 += this.countChunksX;
         }

         for (int var18 = var11; var18 <= var14; var18++) {
            int var19 = var18 % this.countChunksY;
            if (var19 < 0) {
               var19 += this.countChunksY;
            }

            for (int var20 = var12; var20 <= var15; var20++) {
               int var21 = var20 % this.countChunksZ;
               if (var21 < 0) {
                  var21 += this.countChunksZ;
               }

               int var22 = (var21 * this.countChunksY + var19) * this.countChunksX + var17;
               RenderChunk var23 = this.renderChunks[var22];
               var23.setNeedsUpdate(true);
            }
         }
      }
   }

   public RenderChunk getRenderChunk(BlockPos var1) {
      int var5 = var1.getX() >> 4;
      int var6 = var1.getY() >> 4;
      int var7 = var1.getZ() >> 4;
      if (var6 >= 0 && var6 < this.countChunksY) {
         var5 %= this.countChunksX;
         if (var5 < 0) {
            var5 += this.countChunksX;
         }

         var7 %= this.countChunksZ;
         if (var7 < 0) {
            var7 += this.countChunksZ;
         }

         int var8 = (var7 * this.countChunksY + var6) * this.countChunksX + var5;
         return this.renderChunks[var8];
      } else {
         return null;
      }
   }
}
