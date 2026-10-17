package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public class ConnectedTextures {
   private static Map[] spriteQuadMaps = null;
   private static ConnectedProperties[][] blockProperties = (ConnectedProperties[][])null;
   private static ConnectedProperties[][] tileProperties = (ConnectedProperties[][])null;
   private static boolean multipass = false;
   private static final int Z_NEG_NORTH = 2;
   private static final int Z_POS_SOUTH = 3;
   private static final int X_NEG_WEST = 4;
   private static final int X_POS_EAST = 5;
   private static final int y = 2;
   private static final String[] propSuffixes = new String[]{
      "", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
   };
   private static final int[] ctmIndexes = new int[]{
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      0,
      0,
      0,
      0,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      0,
      0,
      0,
      0,
      24,
      25,
      26,
      27,
      28,
      29,
      30,
      31,
      32,
      33,
      34,
      35,
      0,
      0,
      0,
      0,
      36,
      37,
      38,
      39,
      40,
      41,
      42,
      43,
      44,
      45,
      46,
      0,
      0,
      0,
      0,
      0
   };
   public static final IBlockState AIR_DEFAULT_STATE = Blocks.air.getDefaultState();
   private static TextureAtlasSprite emptySprite = null;

   public static synchronized BakedQuad getConnectedTexture(
      IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad quad, RenderEnv renderEnv
   ) {
      TextureAtlasSprite textureAtlasSprite = quad.getSprite();
      if (textureAtlasSprite == null) {
         return quad;
      }

      Block block = blockState.getBlock();
      EnumFacing enumFacing = quad.getFace();
      if (block instanceof BlockPane && textureAtlasSprite.getIconName().startsWith("minecraft:blocks/glass_pane_top")) {
         IBlockState iBlockState = blockAccess.getBlockState(blockPos.offset(quad.getFace()));
         if (iBlockState == blockState) {
            return getQuad(emptySprite, block, blockState, quad);
         }
      }

      TextureAtlasSprite textureAtlasSprite2 = getConnectedTextureMultiPass(blockAccess, blockState, blockPos, enumFacing, textureAtlasSprite, renderEnv);
      return textureAtlasSprite2 == textureAtlasSprite ? quad : getQuad(textureAtlasSprite2, block, blockState, quad);
   }

   private static BakedQuad getQuad(TextureAtlasSprite sprite, Block block, IBlockState blockState, BakedQuad quadIn) {
      if (spriteQuadMaps == null) {
         return quadIn;
      }

      int i = sprite.getIndexInMap();
      if (i >= 0 && i < spriteQuadMaps.length) {
         Map map = spriteQuadMaps[i];
         if (map == null) {
            map = new IdentityHashMap(1);
            spriteQuadMaps[i] = map;
         }

         BakedQuad bakedQuad = (BakedQuad)map.get(quadIn);
         if (bakedQuad == null) {
            bakedQuad = makeSpriteQuad(quadIn, sprite);
            map.put(quadIn, bakedQuad);
         }

         return bakedQuad;
      } else {
         return quadIn;
      }
   }

   private static BakedQuad makeSpriteQuad(BakedQuad quad, TextureAtlasSprite sprite) {
      int[] ints = (int[])quad.getVertexData().clone();
      TextureAtlasSprite textureAtlasSprite = quad.getSprite();

      for (int i = 0; i < 4; i++) {
         fixVertex(ints, i, textureAtlasSprite, sprite);
      }

      return new BakedQuad(ints, quad.getTintIndex(), quad.getFace(), sprite);
   }

   private static void fixVertex(int[] data, int vertex, TextureAtlasSprite spriteFrom, TextureAtlasSprite spriteTo) {
      int mul = data.length / 4;
      int pos = mul * vertex;
      float u = Float.intBitsToFloat(data[pos + 4]);
      float v = Float.intBitsToFloat(data[pos + 4 + 1]);
      double su16 = spriteFrom.getSpriteU16(u);
      double sv16 = spriteFrom.getSpriteV16(v);
      data[pos + 4] = Float.floatToRawIntBits(spriteTo.getInterpolatedU(su16));
      data[pos + 4 + 1] = Float.floatToRawIntBits(spriteTo.getInterpolatedV(sv16));
   }

   private static TextureAtlasSprite getConnectedTextureMultiPass(
      IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing side, TextureAtlasSprite icon, RenderEnv renderEnv
   ) {
      TextureAtlasSprite textureAtlasSprite = getConnectedTextureSingle(blockAccess, blockState, blockPos, side, icon, true, renderEnv);
      if (!multipass) {
         return textureAtlasSprite;
      }

      if (textureAtlasSprite == icon) {
         return textureAtlasSprite;
      }

      TextureAtlasSprite textureAtlasSprite2 = textureAtlasSprite;

      for (int i = 0; i < 3; i++) {
         TextureAtlasSprite textureAtlasSprite3 = getConnectedTextureSingle(blockAccess, blockState, blockPos, side, textureAtlasSprite2, false, renderEnv);
         if (textureAtlasSprite3 == textureAtlasSprite2) {
            break;
         }

         textureAtlasSprite2 = textureAtlasSprite3;
      }

      return textureAtlasSprite2;
   }

   public static TextureAtlasSprite getConnectedTextureSingle(
      IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, TextureAtlasSprite icon, boolean checkBlocks, RenderEnv renderEnv
   ) {
      Block block = blockState.getBlock();
      if (!(blockState instanceof BlockStateBase)) {
         return icon;
      }

      BlockStateBase blockStateBase = (BlockStateBase)blockState;
      if (tileProperties != null) {
         int i = icon.getIndexInMap();
         if (i >= 0 && i < tileProperties.length) {
            ConnectedProperties[] connectedPropertiess = tileProperties[i];
            if (connectedPropertiess != null) {
               int j = getSide(facing);

               for (int k = 0; k < connectedPropertiess.length; k++) {
                  ConnectedProperties connectedProperties = connectedPropertiess[k];
                  if (connectedProperties != null && connectedProperties.matchesBlockId(blockStateBase.getBlockId())) {
                     TextureAtlasSprite textureAtlasSprite = getConnectedTexture(connectedProperties, blockAccess, blockStateBase, blockPos, j, icon, renderEnv);
                     if (textureAtlasSprite != null) {
                        return textureAtlasSprite;
                     }
                  }
               }
            }
         }
      }

      if (blockProperties != null && checkBlocks) {
         int l = renderEnv.getBlockId();
         if (l >= 0 && l < blockProperties.length) {
            ConnectedProperties[] connectedPropertiess2 = blockProperties[l];
            if (connectedPropertiess2 != null) {
               int m = getSide(facing);

               for (int n = 0; n < connectedPropertiess2.length; n++) {
                  ConnectedProperties connectedProperties2 = connectedPropertiess2[n];
                  if (connectedProperties2 != null && connectedProperties2.matchesIcon(icon)) {
                     TextureAtlasSprite textureAtlasSprite2 = getConnectedTexture(connectedProperties2, blockAccess, blockStateBase, blockPos, m, icon, renderEnv);
                     if (textureAtlasSprite2 != null) {
                        return textureAtlasSprite2;
                     }
                  }
               }
            }
         }
      }

      return icon;
   }

   public static int getSide(EnumFacing facing) {
      if (facing == null) {
         return -1;
      }

      switch (facing) {
         case DOWN:
            return 0;
         case UP:
            return 1;
         case EAST:
            return 5;
         case WEST:
            return 4;
         case NORTH:
            return 2;
         case SOUTH:
            return 3;
         default:
            return -1;
      }
   }

   private static EnumFacing getFacing(int side) {
      switch (side) {
         case 0:
            return EnumFacing.DOWN;
         case 1:
            return EnumFacing.UP;
         case 2:
            return EnumFacing.NORTH;
         case 3:
            return EnumFacing.SOUTH;
         case 4:
            return EnumFacing.WEST;
         case 5:
            return EnumFacing.EAST;
         default:
            return EnumFacing.UP;
      }
   }

   private static TextureAtlasSprite getConnectedTexture(
      ConnectedProperties cp, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, RenderEnv renderEnv
   ) {
      int i = 0;
      int j = blockState.getMetadata();
      int k = j;
      Block block = blockState.getBlock();
      if (block instanceof BlockRotatedPillar) {
         i = getWoodAxis(side, j);
         if (cp.getMetadataMax() <= 3) {
            k = j & 3;
         }
      }

      if (block instanceof BlockQuartz) {
         i = getQuartzAxis(side, j);
         if (cp.getMetadataMax() <= 2 && k > 2) {
            k = 2;
         }
      }

      if (!cp.matchesBlock(blockState.getBlockId(), k)) {
         return null;
      }

      if (side >= 0 && cp.faces != 63) {
         int l = side;
         if (i != 0) {
            l = fixSideByAxis(side, i);
         }

         if ((1 << l & cp.faces) == 0) {
            return null;
         }
      }

      int m = blockPos.getY();
      if (m >= cp.minHeight && m <= cp.maxHeight) {
         if (cp.biomes != null) {
            BiomeGenBase biomeGenBase = blockAccess.getBiomeGenForCoords(blockPos);
            if (!cp.matchesBiome(biomeGenBase)) {
               return null;
            }
         }

         switch (cp.method) {
            case 1:
               return getConnectedTextureCtm(cp, blockAccess, blockState, blockPos, i, side, icon, j, renderEnv);
            case 2:
               return getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, i, side, icon, j);
            case 3:
               return getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, i, side, icon, j);
            case 4:
               return getConnectedTextureRandom(cp, blockPos, side);
            case 5:
               return getConnectedTextureRepeat(cp, blockPos, side);
            case 6:
               return getConnectedTextureVerticalBoth(cp, blockAccess, blockState, blockPos, i, side, icon, j);
            case 7:
               return getConnectedTextureFixed(cp);
            case 8:
               return getConnectedTextureHorizontalVertical(cp, blockAccess, blockState, blockPos, i, side, icon, j);
            case 9:
               return getConnectedTextureVerticalHorizontal(cp, blockAccess, blockState, blockPos, i, side, icon, j);
            default:
               return null;
         }
      } else {
         return null;
      }
   }

   private static int fixSideByAxis(int side, int vertAxis) {
      switch (vertAxis) {
         case 0:
            return side;
         case 1:
            switch (side) {
               case 0:
                  return 2;
               case 1:
                  return 3;
               case 2:
                  return 1;
               case 3:
                  return 0;
               default:
                  return side;
            }
         case 2:
            switch (side) {
               case 0:
                  return 4;
               case 1:
                  return 5;
               case 2:
               case 3:
               default:
                  return side;
               case 4:
                  return 1;
               case 5:
                  return 0;
            }
         default:
            return side;
      }
   }

   private static int getWoodAxis(int side, int metadata) {
      int i = (metadata & 12) >> 2;
      switch (i) {
         case 1:
            return 2;
         case 2:
            return 1;
         default:
            return 0;
      }
   }

   private static int getQuartzAxis(int side, int metadata) {
      switch (metadata) {
         case 3:
            return 2;
         case 4:
            return 1;
         default:
            return 0;
      }
   }

   private static TextureAtlasSprite getConnectedTextureRandom(ConnectedProperties connectedProperties, BlockPos blockPos, int i) {
      if (connectedProperties.tileIcons.length == 1) {
         return connectedProperties.tileIcons[0];
      }

      int j = i / connectedProperties.symmetry * connectedProperties.symmetry;
      int k = Config.getRandom(blockPos, j) & 2147483647;
      int l = 0;
      if (connectedProperties.weights == null) {
         l = k % connectedProperties.tileIcons.length;
      } else {
         int m = k % connectedProperties.sumAllWeights;
         int[] ints = connectedProperties.sumWeights;

         for (int n = 0; n < ints.length; n++) {
            if (m < ints[n]) {
               l = n;
               break;
            }
         }
      }

      return connectedProperties.tileIcons[l];
   }

   private static TextureAtlasSprite getConnectedTextureFixed(ConnectedProperties cp) {
      return cp.tileIcons[0];
   }

   private static TextureAtlasSprite getConnectedTextureRepeat(ConnectedProperties cp, BlockPos blockPos, int side) {
      if (cp.tileIcons.length == 1) {
         return cp.tileIcons[0];
      }

      int i = blockPos.getX();
      int j = blockPos.getY();
      int k = blockPos.getZ();
      int l = 0;
      int m = 0;
      switch (side) {
         case 0:
            l = i;
            m = k;
            break;
         case 1:
            l = i;
            m = k;
            break;
         case 2:
            l = -i - 1;
            m = -j;
            break;
         case 3:
            l = i;
            m = -j;
            break;
         case 4:
            l = k;
            m = -j;
            break;
         case 5:
            l = -k - 1;
            m = -j;
      }

      l %= cp.width;
      m %= cp.height;
      if (l < 0) {
         l += cp.width;
      }

      if (m < 0) {
         m += cp.height;
      }

      int n = m * cp.width + l;
      return cp.tileIcons[n];
   }

   private static TextureAtlasSprite getConnectedTextureCtm(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata,
      RenderEnv renderEnv
   ) {
      boolean[] booleans = renderEnv.getBorderFlags();
      switch (side) {
         case 0:
            booleans[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
            booleans[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            booleans[2] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
            booleans[3] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            break;
         case 1:
            booleans[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
            booleans[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            booleans[2] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            booleans[3] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
            break;
         case 2:
            booleans[0] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            booleans[1] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
            booleans[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
            booleans[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            break;
         case 3:
            booleans[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
            booleans[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            booleans[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
            booleans[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            break;
         case 4:
            booleans[0] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
            booleans[1] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            booleans[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
            booleans[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            break;
         case 5:
            booleans[0] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            booleans[1] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
            booleans[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
            booleans[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
      }

      byte b = 0;
      if (booleans[0] & !booleans[1] & !booleans[2] & !booleans[3]) {
         b = 3;
      } else if (!booleans[0] & booleans[1] & !booleans[2] & !booleans[3]) {
         b = 1;
      } else if (!booleans[0] & !booleans[1] & booleans[2] & !booleans[3]) {
         b = 12;
      } else if (!booleans[0] & !booleans[1] & !booleans[2] & booleans[3]) {
         b = 36;
      } else if (booleans[0] & booleans[1] & !booleans[2] & !booleans[3]) {
         b = 2;
      } else if (!booleans[0] & !booleans[1] & booleans[2] & booleans[3]) {
         b = 24;
      } else if (booleans[0] & !booleans[1] & booleans[2] & !booleans[3]) {
         b = 15;
      } else if (booleans[0] & !booleans[1] & !booleans[2] & booleans[3]) {
         b = 39;
      } else if (!booleans[0] & booleans[1] & booleans[2] & !booleans[3]) {
         b = 13;
      } else if (!booleans[0] & booleans[1] & !booleans[2] & booleans[3]) {
         b = 37;
      } else if (!booleans[0] & booleans[1] & booleans[2] & booleans[3]) {
         b = 25;
      } else if (booleans[0] & !booleans[1] & booleans[2] & booleans[3]) {
         b = 27;
      } else if (booleans[0] & booleans[1] & !booleans[2] & booleans[3]) {
         b = 38;
      } else if (booleans[0] & booleans[1] & booleans[2] & !booleans[3]) {
         b = 14;
      } else if (booleans[0] & booleans[1] & booleans[2] & booleans[3]) {
         b = 26;
      }

      if (b == 0) {
         return cp.tileIcons[b];
      }

      if (!Config.isConnectedTexturesFancy()) {
         return cp.tileIcons[b];
      }

      switch (side) {
         case 0:
            booleans[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().north(), side, icon, metadata);
            booleans[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().north(), side, icon, metadata);
            booleans[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().south(), side, icon, metadata);
            booleans[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().south(), side, icon, metadata);
            break;
         case 1:
            booleans[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().south(), side, icon, metadata);
            booleans[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().south(), side, icon, metadata);
            booleans[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().north(), side, icon, metadata);
            booleans[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().north(), side, icon, metadata);
            break;
         case 2:
            booleans[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().down(), side, icon, metadata);
            booleans[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().down(), side, icon, metadata);
            booleans[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().up(), side, icon, metadata);
            booleans[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().up(), side, icon, metadata);
            break;
         case 3:
            booleans[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().down(), side, icon, metadata);
            booleans[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().down(), side, icon, metadata);
            booleans[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().up(), side, icon, metadata);
            booleans[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().up(), side, icon, metadata);
            break;
         case 4:
            booleans[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().south(), side, icon, metadata);
            booleans[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().north(), side, icon, metadata);
            booleans[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().south(), side, icon, metadata);
            booleans[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().north(), side, icon, metadata);
            break;
         case 5:
            booleans[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().north(), side, icon, metadata);
            booleans[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().south(), side, icon, metadata);
            booleans[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().north(), side, icon, metadata);
            booleans[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().south(), side, icon, metadata);
      }

      if (b == 13 && booleans[0]) {
         b = 4;
      } else if (b == 15 && booleans[1]) {
         b = 5;
      } else if (b == 37 && booleans[2]) {
         b = 16;
      } else if (b == 39 && booleans[3]) {
         b = 17;
      } else if (b == 14 && booleans[0] && booleans[1]) {
         b = 7;
      } else if (b == 25 && booleans[0] && booleans[2]) {
         b = 6;
      } else if (b == 27 && booleans[3] && booleans[1]) {
         b = 19;
      } else if (b == 38 && booleans[3] && booleans[2]) {
         b = 18;
      } else if (b == 14 && !booleans[0] && booleans[1]) {
         b = 31;
      } else if (b == 25 && booleans[0] && !booleans[2]) {
         b = 30;
      } else if (b == 27 && !booleans[3] && booleans[1]) {
         b = 41;
      } else if (b == 38 && booleans[3] && !booleans[2]) {
         b = 40;
      } else if (b == 14 && booleans[0] && !booleans[1]) {
         b = 29;
      } else if (b == 25 && !booleans[0] && booleans[2]) {
         b = 28;
      } else if (b == 27 && booleans[3] && !booleans[1]) {
         b = 43;
      } else if (b == 38 && !booleans[3] && booleans[2]) {
         b = 42;
      } else if (b == 26 && booleans[0] && booleans[1] && booleans[2] && booleans[3]) {
         b = 46;
      } else if (b == 26 && !booleans[0] && booleans[1] && booleans[2] && booleans[3]) {
         b = 9;
      } else if (b == 26 && booleans[0] && !booleans[1] && booleans[2] && booleans[3]) {
         b = 21;
      } else if (b == 26 && booleans[0] && booleans[1] && !booleans[2] && booleans[3]) {
         b = 8;
      } else if (b == 26 && booleans[0] && booleans[1] && booleans[2] && !booleans[3]) {
         b = 20;
      } else if (b == 26 && booleans[0] && booleans[1] && !booleans[2] && !booleans[3]) {
         b = 11;
      } else if (b == 26 && !booleans[0] && !booleans[1] && booleans[2] && booleans[3]) {
         b = 22;
      } else if (b == 26 && !booleans[0] && booleans[1] && !booleans[2] && booleans[3]) {
         b = 23;
      } else if (b == 26 && booleans[0] && !booleans[1] && booleans[2] && !booleans[3]) {
         b = 10;
      } else if (b == 26 && booleans[0] && !booleans[1] && !booleans[2] && booleans[3]) {
         b = 34;
      } else if (b == 26 && !booleans[0] && booleans[1] && booleans[2] && !booleans[3]) {
         b = 35;
      } else if (b == 26 && booleans[0] && !booleans[1] && !booleans[2] && !booleans[3]) {
         b = 32;
      } else if (b == 26 && !booleans[0] && booleans[1] && !booleans[2] && !booleans[3]) {
         b = 33;
      } else if (b == 26 && !booleans[0] && !booleans[1] && booleans[2] && !booleans[3]) {
         b = 44;
      } else if (b == 26 && !booleans[0] && !booleans[1] && !booleans[2] && booleans[3]) {
         b = 45;
      }

      return cp.tileIcons[b];
   }

   private static boolean isNeighbour(
      ConnectedProperties cp, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata
   ) {
      IBlockState iBlockState = iblockaccess.getBlockState(blockPos);
      if (blockState == iBlockState) {
         return true;
      }

      if (cp.connect == 2) {
         if (iBlockState == null) {
            return false;
         }

         if (iBlockState == AIR_DEFAULT_STATE) {
            return false;
         }

         TextureAtlasSprite textureAtlasSprite = getNeighbourIcon(iblockaccess, blockPos, iBlockState, side);
         return textureAtlasSprite == icon;
      } else {
         return cp.connect == 3
            ? (iBlockState == null ? false : (iBlockState == AIR_DEFAULT_STATE ? false : iBlockState.getBlock().getMaterial() == blockState.getBlock().getMaterial()))
            : false;
      }
   }

   private static TextureAtlasSprite getNeighbourIcon(IBlockAccess iblockaccess, BlockPos blockPos, IBlockState neighbourState, int side) {
      neighbourState = neighbourState.getBlock().getActualState(neighbourState, iblockaccess, blockPos);
      IBakedModel iBakedModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(neighbourState);
      if (iBakedModel == null) {
         return null;
      }

      EnumFacing enumFacing = getFacing(side);
      List list = iBakedModel.getFaceQuads(enumFacing);
      if (list.size() > 0) {
         BakedQuad bakedQuad = (BakedQuad)list.get(0);
         return bakedQuad.getSprite();
      }

      List list2 = iBakedModel.getGeneralQuads();

      for (int i = 0; i < list2.size(); i++) {
         BakedQuad bakedQuad2 = (BakedQuad)list2.get(i);
         if (bakedQuad2.getFace() == enumFacing) {
            return bakedQuad2.getSprite();
         }
      }

      return null;
   }

   private static TextureAtlasSprite getConnectedTextureHorizontal(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata
   ) {
      boolean bl;
      boolean bl2;
      bl = false;
      bl2 = false;
      label43:
      switch (vertAxis) {
         case 0:
            switch (side) {
               case 0:
               case 1:
                  return null;
               case 2:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  break label43;
               case 3:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  break label43;
               case 4:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                  break label43;
               case 5:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
               default:
                  break label43;
            }
         case 1:
            switch (side) {
               case 0:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  break label43;
               case 1:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  break label43;
               case 2:
               case 3:
                  return null;
               case 4:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                  break label43;
               case 5:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
               default:
                  break label43;
            }
         case 2:
            switch (side) {
               case 0:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                  break;
               case 1:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                  break;
               case 2:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                  break;
               case 3:
                  bl = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                  bl2 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                  break;
               case 4:
               case 5:
                  return null;
            }
      }

      byte b = 3;
      if (bl) {
         if (bl2) {
            b = 1;
         } else {
            b = 2;
         }
      } else if (bl2) {
         b = 0;
      } else {
         b = 3;
      }

      return cp.tileIcons[b];
   }

   private static TextureAtlasSprite getConnectedTextureVerticalBoth(
      ConnectedProperties connectedProperties,
      IBlockAccess iBlockAccess,
      IBlockState iBlockState,
      BlockPos blockPos,
      int i,
      int j,
      TextureAtlasSprite textureAtlasSprite,
      int k
   ) {
      boolean bl = false;
      boolean bl2 = false;
      switch (i) {
         case 0:
            if (j == 1 || j == 0) {
               return null;
            }

            bl = isNeighbour(connectedProperties, iBlockAccess, iBlockState, blockPos.down(), j, textureAtlasSprite, k);
            bl2 = isNeighbour(connectedProperties, iBlockAccess, iBlockState, blockPos.up(), j, textureAtlasSprite, k);
            break;
         case 1:
            if (j == 3 || j == 2) {
               return null;
            }

            bl = isNeighbour(connectedProperties, iBlockAccess, iBlockState, blockPos.south(), j, textureAtlasSprite, k);
            bl2 = isNeighbour(connectedProperties, iBlockAccess, iBlockState, blockPos.north(), j, textureAtlasSprite, k);
            break;
         case 2:
            if (j == 5 || j == 4) {
               return null;
            }

            bl = isNeighbour(connectedProperties, iBlockAccess, iBlockState, blockPos.west(), j, textureAtlasSprite, k);
            bl2 = isNeighbour(connectedProperties, iBlockAccess, iBlockState, blockPos.east(), j, textureAtlasSprite, k);
      }

      byte b = 3;
      if (bl) {
         if (bl2) {
            b = 1;
         } else {
            b = 2;
         }
      } else if (bl2) {
         b = 0;
      } else {
         b = 3;
      }

      return connectedProperties.tileIcons[b];
   }

   private static TextureAtlasSprite getConnectedTextureHorizontalVertical(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata
   ) {
      TextureAtlasSprite[] textureAtlasSprites = cp.tileIcons;
      TextureAtlasSprite textureAtlasSprite = getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      if (textureAtlasSprite != null && textureAtlasSprite != icon && textureAtlasSprite != textureAtlasSprites[3]) {
         return textureAtlasSprite;
      }

      TextureAtlasSprite textureAtlasSprite2 = getConnectedTextureVerticalBoth(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      return textureAtlasSprite2 == textureAtlasSprites[0] ? textureAtlasSprites[4] : (textureAtlasSprite2 == textureAtlasSprites[1] ? textureAtlasSprites[5] : (textureAtlasSprite2 == textureAtlasSprites[2] ? textureAtlasSprites[6] : textureAtlasSprite2));
   }

   private static TextureAtlasSprite getConnectedTextureVerticalHorizontal(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata
   ) {
      TextureAtlasSprite[] textureAtlasSprites = cp.tileIcons;
      TextureAtlasSprite textureAtlasSprite = getConnectedTextureVerticalBoth(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      if (textureAtlasSprite != null && textureAtlasSprite != icon && textureAtlasSprite != textureAtlasSprites[3]) {
         return textureAtlasSprite;
      }

      TextureAtlasSprite textureAtlasSprite2 = getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      return textureAtlasSprite2 == textureAtlasSprites[0] ? textureAtlasSprites[4] : (textureAtlasSprite2 == textureAtlasSprites[1] ? textureAtlasSprites[5] : (textureAtlasSprite2 == textureAtlasSprites[2] ? textureAtlasSprites[6] : textureAtlasSprite2));
   }

   private static TextureAtlasSprite getConnectedTextureVertical(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata
   ) {
      boolean bl = false;
      switch (vertAxis) {
         case 0:
            if (side == 1 || side == 0) {
               return null;
            }

            bl = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            break;
         case 1:
            if (side == 3 || side == 2) {
               return null;
            }

            bl = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            break;
         case 2:
            if (side == 5 || side == 4) {
               return null;
            }

            bl = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
      }

      return bl ? cp.tileIcons[0] : null;
   }

   public static void updateIcons(TextureMap textureMap) {
      blockProperties = (ConnectedProperties[][])null;
      tileProperties = (ConnectedProperties[][])null;
      spriteQuadMaps = null;
      if (Config.isConnectedTextures()) {
         IResourcePack[] iResourcePacks = Config.getResourcePacks();

         for (int i = iResourcePacks.length - 1; i >= 0; i--) {
            IResourcePack iResourcePack = iResourcePacks[i];
            updateIcons(textureMap, iResourcePack);
         }

         updateIcons(textureMap, Config.getDefaultResourcePack());
         ResourceLocation resourceLocation = new ResourceLocation("mcpatcher/ctm/default/empty");
         emptySprite = textureMap.registerSprite(resourceLocation);
         spriteQuadMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
         if (blockProperties.length <= 0) {
            blockProperties = (ConnectedProperties[][])null;
         }

         if (tileProperties.length <= 0) {
            tileProperties = (ConnectedProperties[][])null;
         }
      }
   }

   public static void updateIcons(TextureMap textureMap, IResourcePack rp) {
      String[] strings = ResUtils.collectFiles(rp, "mcpatcher/ctm/", ".properties", getDefaultCtmPaths());
      Arrays.sort(strings);
      List list = makePropertyList(tileProperties);
      List list2 = makePropertyList(blockProperties);

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         Config.dbg("ConnectedTextures: " + string);

         try {
            ResourceLocation resourceLocation = new ResourceLocation(string);
            InputStream inputStream = rp.getInputStream(resourceLocation);
            if (inputStream == null) {
               Config.warn("ConnectedTextures file not found: " + string);
            } else {
               Properties properties2 = new Properties();
               properties2.load(inputStream);
               ConnectedProperties connectedProperties = new ConnectedProperties(properties2, string);
               if (connectedProperties.isValid(string)) {
                  connectedProperties.updateIcons(textureMap);
                  addToTileList(connectedProperties, list);
                  addToBlockList(connectedProperties, list2);
               }
            }
         } catch (FileNotFoundException fileNotFoundException) {
            Config.warn("ConnectedTextures file not found: " + string);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

      blockProperties = propertyListToArray(list2);
      tileProperties = propertyListToArray(list);
      multipass = detectMultipass();
      Config.dbg("Multipass connected textures: " + multipass);
   }

   private static List makePropertyList(ConnectedProperties[][] propsArr) {
      ArrayList arrayList = new ArrayList();
      if (propsArr != null) {
         for (int i = 0; i < propsArr.length; i++) {
            ConnectedProperties[] connectedPropertiess = propsArr[i];
            ArrayList arrayList2 = null;
            if (connectedPropertiess != null) {
               arrayList2 = new ArrayList<>(Arrays.asList(connectedPropertiess));
            }

            arrayList.add(arrayList2);
         }
      }

      return arrayList;
   }

   private static boolean detectMultipass() {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < tileProperties.length; i++) {
         ConnectedProperties[] connectedPropertiess = tileProperties[i];
         if (connectedPropertiess != null) {
            arrayList.addAll(Arrays.asList(connectedPropertiess));
         }
      }

      for (int j = 0; j < blockProperties.length; j++) {
         ConnectedProperties[] connectedPropertiess2 = blockProperties[j];
         if (connectedPropertiess2 != null) {
            arrayList.addAll(Arrays.asList(connectedPropertiess2));
         }
      }

      ConnectedProperties[] connectedPropertiess3 = (net.minecraft.src.ConnectedProperties[])(arrayList.toArray(new ConnectedProperties[arrayList.size()]));
      HashSet hashSet = new HashSet();
      HashSet hashSet2 = new HashSet();

      for (int k = 0; k < connectedPropertiess3.length; k++) {
         ConnectedProperties connectedProperties = connectedPropertiess3[k];
         if (connectedProperties.matchTileIcons != null) {
            hashSet.addAll(Arrays.asList(connectedProperties.matchTileIcons));
         }

         if (connectedProperties.tileIcons != null) {
            hashSet2.addAll(Arrays.asList(connectedProperties.tileIcons));
         }
      }

      hashSet.retainAll(hashSet2);
      return !hashSet.isEmpty();
   }

   private static ConnectedProperties[][] propertyListToArray(List list) {
      ConnectedProperties[][] connectedPropertiesss = new ConnectedProperties[list.size()][];

      for (int i = 0; i < list.size(); i++) {
         List list2 = (List)list.get(i);
         if (list2 != null) {
            ConnectedProperties[] connectedPropertiess = (net.minecraft.src.ConnectedProperties[])(list2.toArray(new ConnectedProperties[list2.size()]));
            connectedPropertiesss[i] = connectedPropertiess;
         }
      }

      return connectedPropertiesss;
   }

   private static void addToTileList(ConnectedProperties cp, List tileList) {
      if (cp.matchTileIcons != null) {
         for (int i = 0; i < cp.matchTileIcons.length; i++) {
            TextureAtlasSprite textureAtlasSprite = cp.matchTileIcons[i];
            if (!(textureAtlasSprite instanceof TextureAtlasSprite)) {
               Config.warn("TextureAtlasSprite is not TextureAtlasSprite: " + textureAtlasSprite + ", name: " + textureAtlasSprite.getIconName());
            } else {
               int j = textureAtlasSprite.getIndexInMap();
               if (j < 0) {
                  Config.warn("Invalid tile ID: " + j + ", icon: " + textureAtlasSprite.getIconName());
               } else {
                  addToList(cp, tileList, j);
               }
            }
         }
      }
   }

   private static void addToBlockList(ConnectedProperties cp, List blockList) {
      if (cp.matchBlocks != null) {
         for (int i = 0; i < cp.matchBlocks.length; i++) {
            int j = cp.matchBlocks[i].getBlockId();
            if (j < 0) {
               Config.warn("Invalid block ID: " + j);
            } else {
               addToList(cp, blockList, j);
            }
         }
      }
   }

   private static void addToList(ConnectedProperties cp, List list, int id) {
      while (id >= list.size()) {
         list.add(null);
      }

      List list2 = (List)list.get(id);
      if (list2 == null) {
         list2 = new ArrayList();
         list.set(id, list2);
      }

      list2.add(cp);
   }

   private static String[] getDefaultCtmPaths() {
      ArrayList arrayList = new ArrayList();
      String string = "mcpatcher/ctm/default/";
      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png"))) {
         arrayList.add(string + "glass.properties");
         arrayList.add(string + "glasspane.properties");
      }

      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png"))) {
         arrayList.add(string + "bookshelf.properties");
      }

      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png"))) {
         arrayList.add(string + "sandstone.properties");
      }

      String[] strings = new String[]{
         "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"
      };

      for (int i = 0; i < strings.length; i++) {
         String string2 = strings[i];
         if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + string2 + ".png"))) {
            arrayList.add(string + i + "_glass_" + string2 + "/glass_" + string2 + ".properties");
            arrayList.add(string + i + "_glass_" + string2 + "/glass_pane_" + string2 + ".properties");
         }
      }

      return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
   }

   public static int getPaneTextureIndex(boolean linkP, boolean linkN, boolean linkYp, boolean linkYn) {
      return linkN && linkP
         ? (linkYp ? (linkYn ? 34 : 50) : (linkYn ? 18 : 2))
         : (
            linkN && !linkP
               ? (linkYp ? (linkYn ? 35 : 51) : (linkYn ? 19 : 3))
               : (!linkN && linkP ? (linkYp ? (linkYn ? 33 : 49) : (linkYn ? 17 : 1)) : (linkYp ? (linkYn ? 32 : 48) : (linkYn ? 16 : 0)))
         );
   }

   public static int getReversePaneTextureIndex(int texNum) {
      int i = texNum % 16;
      return i == 1 ? texNum + 2 : (i == 3 ? texNum - 2 : texNum);
   }

   public static TextureAtlasSprite getCtmTexture(ConnectedProperties cp, int ctmIndex, TextureAtlasSprite icon) {
      if (cp.method != 1) {
         return icon;
      } else if (ctmIndex >= 0 && ctmIndex < ctmIndexes.length) {
         int i = ctmIndexes[ctmIndex];
         TextureAtlasSprite[] textureAtlasSprites = cp.tileIcons;
         return i >= 0 && i < textureAtlasSprites.length ? textureAtlasSprites[i] : icon;
      } else {
         return icon;
      }
   }
}
