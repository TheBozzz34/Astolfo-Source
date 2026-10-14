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
   public static void updateWaterOpacity(GameSettings settings, World world) {
      if (settings != null) {
         byte b = 3;
         if (settings.ofClearWater) {
            b = 1;
         }

         BlockLeavesBase.setLightOpacity(Blocks.water, b);
         BlockLeavesBase.setLightOpacity(Blocks.flowing_water, b);
      }

      if (world != null) {
         IChunkProvider iChunkProvider = world.getChunkProvider();
         if (iChunkProvider != null) {
            Entity entity = Config.getMinecraft().getRenderViewEntity();
            if (entity != null) {
               int i = (int)entity.posX / 16;
               int j = (int)entity.posZ / 16;
               int k = i - 512;
               int l = i + 512;
               int m = j - 512;
               int n = j + 512;
               int i2 = 0;

               for (int i3 = k; i3 < l; i3++) {
                  for (int i4 = m; i4 < n; i4++) {
                     if (iChunkProvider.chunkExists(i3, i4)) {
                        Chunk chunk = iChunkProvider.provideChunk(i3, i4);
                        if (chunk != null && !(chunk instanceof EmptyChunk)) {
                           int i5 = i3 << 4;
                           int i6 = i4 << 4;
                           int i7 = i5 + 16;
                           int i8 = i6 + 16;
                           BlockPosM blockPosM = new BlockPosM(0, 0, 0);
                           BlockPosM blockPosM2 = new BlockPosM(0, 0, 0);

                           for (int i9 = i5; i9 < i7; i9++) {
                              for (int i10 = i6; i10 < i8; i10++) {
                                 blockPosM.setXyz(i9, 0, i10);
                                 BlockPos blockPos = world.getPrecipitationHeight(blockPosM);

                                 for (int i11 = 0; i11 < blockPos.getY(); i11++) {
                                    blockPosM2.setXyz(i9, i11, i10);
                                    IBlockState iBlockState = world.getBlockState(blockPosM2);
                                    if (iBlockState.getBlock().getMaterial() == Material.water) {
                                       world.markBlocksDirtyVertical(i9, i10, blockPosM2.getY(), blockPos.getY());
                                       i2++;
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (i2 > 0) {
                  String string = "server";
                  if (Config.isMinecraftThread()) {
                     string = "client";
                  }

                  Config.dbg("ClearWater (" + string + ") relighted " + i2 + " chunks");
               }
            }
         }
      }
   }
}
