package net.minecraft.src;

import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ClearWater {
   private static final long a = 14276141465017L;

   public static void updateWaterOpacity(GameSettings var0, World var1) {
      if (var0 != null) {
         byte var5 = 3;
         if (var0.ofClearWater) {
            var5 = 1;
         }

         BlockLeavesBase.setLightOpacity(Blocks.water, var5);
         BlockLeavesBase.setLightOpacity(Blocks.flowing_water, var5);
      }

      if (var1 != null) {
         IChunkProvider var28 = var1.getChunkProvider();
         if (var28 != null) {
            Entity var6 = Config.getMinecraft().getRenderViewEntity();
            if (var6 != null) {
               int var7 = (int)var6.posX / 16;
               int var8 = (int)var6.posZ / 16;
               int var9 = var7 - 512;
               int var10 = var7 + 512;
               int var11 = var8 - 512;
               int var12 = var8 + 512;
               int var13 = 0;

               for (int var14 = var9; var14 < var10; var14++) {
                  for (int var15 = var11; var15 < var12; var15++) {
                     if (var28.chunkExists(var14, var15)) {
                        Chunk var16 = var28.provideChunk(var14, var15);
                        if (var16 != null && !(var16 instanceof EmptyChunk)) {
                           int var17 = var14 << 4;
                           int var18 = var15 << 4;
                           int var19 = var17 + 16;
                           int var20 = var18 + 16;
                           BlockPosM var21 = new BlockPosM(0, 0, 0);
                           BlockPosM var22 = new BlockPosM(0, 0, 0);

                           for (int var23 = var17; var23 < var19; var23++) {
                              for (int var24 = var18; var24 < var20; var24++) {
                                 var21.setXyz(var23, 0, var24);
                                 BlockPos var25 = var1.getPrecipitationHeight(var21);

                                 for (int var26 = 0; var26 < var25.getY(); var26++) {
                                    var22.setXyz(var23, var26, var24);
                                    IBlockState var27 = var1.getBlockState(var22);
                                    if (var27.getBlock().getMaterial() == Material.water) {
                                       var1.markBlocksDirtyVertical(var23, var24, var22.getY(), var25.getY());
                                       var13++;
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (var13 > 0) {
                  String var29 = "server";
                  if (Config.isMinecraftThread()) {
                     var29 = "client";
                  }

                  Config.dbg("ClearWater (" + var29 + ") relighted " + var13 + " chunks");
               }
            }
         }
      }
   }
}
