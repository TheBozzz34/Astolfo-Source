package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMycelium;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BetterGrass {
   private static IBakedModel L = new SimpleBakedModel(new ArrayList<>(), new ArrayList<>(), false, false, (TextureAtlasSprite)null, (ItemCameraTransforms)null);
   private static IBakedModel C = null;
   private static IBakedModel g = null;
   private static IBakedModel O = null;
   private static final long a = 139731282429173L;

   public static void G() {
      O = BlockModelUtils.G("minecraft:blocks/grass_top", 0);
      g = BlockModelUtils.G("minecraft:blocks/snow", -1);
      C = BlockModelUtils.G("minecraft:blocks/mycelium_top", -1);
   }

   public static List Q(IBlockAccess var0, Block var1, BlockPos var2, EnumFacing var3, List var4) {
      if (var3 == EnumFacing.UP || var3 == EnumFacing.DOWN) {
         return var4;
      }

      if (var1 instanceof BlockMycelium) {
         return Config.e() ? (e(var2.down(), var3, var0) == Blocks.mycelium ? C.getFaceQuads(var3) : var4) : C.getFaceQuads(var3);
      }

      if (var1 instanceof BlockGrass) {
         Block var8 = var0.getBlockState(var2.up()).getBlock();
         boolean var9 = var8 == Blocks.snow || var8 == Blocks.snow_layer;
         if (!Config.e()) {
            if (var9) {
               return g.getFaceQuads(var3);
            }

            return O.getFaceQuads(var3);
         }

         if (var9) {
            if (e(var2, var3, var0) == Blocks.snow_layer) {
               return g.getFaceQuads(var3);
            }
         } else if (e(var2.down(), var3, var0) == Blocks.grass) {
            return O.getFaceQuads(var3);
         }
      }

      return var4;
   }

   private static Block e(BlockPos var0, EnumFacing var1, IBlockAccess var2) {
      BlockPos var3 = var0.offset(var1);
      return var2.getBlockState(var3).getBlock();
   }
}
