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
   private static IBakedModel modelEmpty = new SimpleBakedModel(
      new ArrayList<>(), new ArrayList<>(), false, false, (TextureAtlasSprite)null, (ItemCameraTransforms)null
   );
   private static IBakedModel modelCubeMycelium = null;
   private static IBakedModel modelCubeSnow = null;
   private static IBakedModel modelCubeGrass = null;

   public static void update() {
      modelCubeGrass = BlockModelUtils.makeModelCube("minecraft:blocks/grass_top", 0);
      modelCubeSnow = BlockModelUtils.makeModelCube("minecraft:blocks/snow", -1);
      modelCubeMycelium = BlockModelUtils.makeModelCube("minecraft:blocks/mycelium_top", -1);
   }

   public static List getFaceQuads(IBlockAccess iBlockAccess, Block block, BlockPos blockPos, EnumFacing enumFacing, List list) {
      if (enumFacing == EnumFacing.UP || enumFacing == EnumFacing.DOWN) {
         return list;
      }

      if (block instanceof BlockMycelium) {
         return Config.isBetterGrassFancy()
            ? (getBlockAt(blockPos.down(), enumFacing, iBlockAccess) == Blocks.mycelium ? modelCubeMycelium.getFaceQuads(enumFacing) : list)
            : modelCubeMycelium.getFaceQuads(enumFacing);
      }

      if (block instanceof BlockGrass) {
         Block block2 = iBlockAccess.getBlockState(blockPos.up()).getBlock();
         boolean bl = block2 == Blocks.snow || block2 == Blocks.snow_layer;
         if (!Config.isBetterGrassFancy()) {
            if (bl) {
               return modelCubeSnow.getFaceQuads(enumFacing);
            }

            return modelCubeGrass.getFaceQuads(enumFacing);
         }

         if (bl) {
            if (getBlockAt(blockPos, enumFacing, iBlockAccess) == Blocks.snow_layer) {
               return modelCubeSnow.getFaceQuads(enumFacing);
            }
         } else if (getBlockAt(blockPos.down(), enumFacing, iBlockAccess) == Blocks.grass) {
            return modelCubeGrass.getFaceQuads(enumFacing);
         }
      }

      return list;
   }

   private static Block getBlockAt(BlockPos blockPos, EnumFacing facing, IBlockAccess blockAccess) {
      BlockPos pos = blockPos.offset(facing);
      return blockAccess.getBlockState(pos).getBlock();
   }
}
