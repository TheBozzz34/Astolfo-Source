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

   public static List Q(IBlockAccess iBlockAccess, Block block, BlockPos blockPos, EnumFacing enumFacing, List list) {
      if (enumFacing == EnumFacing.UP || enumFacing == EnumFacing.DOWN) {
         return list;
      }

      if (block instanceof BlockMycelium) {
         return Config.e() ? (e(blockPos.down(), enumFacing, iBlockAccess) == Blocks.mycelium ? C.getFaceQuads(enumFacing) : list) : C.getFaceQuads(enumFacing);
      }

      if (block instanceof BlockGrass) {
         Block block2 = iBlockAccess.getBlockState(blockPos.up()).getBlock();
         boolean bl = block2 == Blocks.snow || block2 == Blocks.snow_layer;
         if (!Config.e()) {
            if (bl) {
               return g.getFaceQuads(enumFacing);
            }

            return O.getFaceQuads(enumFacing);
         }

         if (bl) {
            if (e(blockPos, enumFacing, iBlockAccess) == Blocks.snow_layer) {
               return g.getFaceQuads(enumFacing);
            }
         } else if (e(blockPos.down(), enumFacing, iBlockAccess) == Blocks.grass) {
            return O.getFaceQuads(enumFacing);
         }
      }

      return list;
   }

   private static Block e(BlockPos blockPos, EnumFacing enumFacing, IBlockAccess iBlockAccess) {
      BlockPos blockPos2 = blockPos.offset(enumFacing);
      return iBlockAccess.getBlockState(blockPos2).getBlock();
   }
}
