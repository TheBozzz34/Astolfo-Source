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
   private static final int k = 2;
   private static final int B = 3;
   private static final int S = 4;
   private static final int O = 5;
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
   private static final long a = 65484782951679L;

   public static synchronized BakedQuad getConnectedTexture(IBlockAccess var0, IBlockState var1, BlockPos var2, BakedQuad var3, RenderEnv var4) {
      TextureAtlasSprite var8 = var3.getSprite();
      if (var8 == null) {
         return var3;
      }

      Block var9 = var1.getBlock();
      EnumFacing var10 = var3.getFace();
      if (var9 instanceof BlockPane && var8.getIconName().startsWith("minecraft:blocks/glass_pane_top")) {
         IBlockState var11 = var0.getBlockState(var2.offset(var3.getFace()));
         if (var11 == var1) {
            return getQuad(emptySprite, var9, var1, var3);
         }
      }

      TextureAtlasSprite var12 = getConnectedTextureMultiPass(var0, var1, var2, var10, var8, var4);
      return var12 == var8 ? var3 : getQuad(var12, var9, var1, var3);
   }

   private static BakedQuad getQuad(TextureAtlasSprite var0, Block var1, IBlockState var2, BakedQuad var3) {
      if (spriteQuadMaps == null) {
         return var3;
      }

      int var7 = var0.getIndexInMap();
      if (var7 >= 0 && var7 < spriteQuadMaps.length) {
         Map var8 = spriteQuadMaps[var7];
         if (var8 == null) {
            var8 = new IdentityHashMap(1);
            spriteQuadMaps[var7] = var8;
         }

         BakedQuad var9 = (BakedQuad)var8.get(var3);
         if (var9 == null) {
            var9 = makeSpriteQuad(var3, var0);
            var8.put(var3, var9);
         }

         return var9;
      } else {
         return var3;
      }
   }

   private static BakedQuad makeSpriteQuad(BakedQuad var0, TextureAtlasSprite var1) {
      int[] var5 = (int[])var0.getVertexData().clone();
      TextureAtlasSprite var6 = var0.getSprite();

      for (int var7 = 0; var7 < 4; var7++) {
         fixVertex(var5, var7, var6, var1);
      }

      return new BakedQuad(var5, var0.getTintIndex(), var0.getFace(), var1);
   }

   private static void fixVertex(int[] var0, int var1, TextureAtlasSprite var2, TextureAtlasSprite var3) {
      int var4 = var0.length / 4;
      int var5 = var4 * var1;
      float var6 = Float.intBitsToFloat(var0[var5 + 4]);
      float var7 = Float.intBitsToFloat(var0[var5 + 4 + 1]);
      double var8 = var2.getSpriteU16(var6);
      double var10 = var2.getSpriteV16(var7);
      var0[var5 + 4] = Float.floatToRawIntBits(var3.getInterpolatedU(var8));
      var0[var5 + 4 + 1] = Float.floatToRawIntBits(var3.getInterpolatedV(var10));
   }

   private static TextureAtlasSprite getConnectedTextureMultiPass(
      IBlockAccess var0, IBlockState var1, BlockPos var2, EnumFacing var3, TextureAtlasSprite var4, RenderEnv var5
   ) {
      TextureAtlasSprite var9 = getConnectedTextureSingle(var0, var1, var2, var3, var4, true, var5);
      if (!multipass) {
         return var9;
      }

      if (var9 == var4) {
         return var9;
      }

      TextureAtlasSprite var10 = var9;

      for (int var11 = 0; var11 < 3; var11++) {
         TextureAtlasSprite var12 = getConnectedTextureSingle(var0, var1, var2, var3, var10, false, var5);
         if (var12 == var10) {
            break;
         }

         var10 = var12;
      }

      return var10;
   }

   public static TextureAtlasSprite getConnectedTextureSingle(
      IBlockAccess var0, IBlockState var1, BlockPos var2, EnumFacing var3, TextureAtlasSprite var4, boolean var5, RenderEnv var6
   ) {
      Block var10 = var1.getBlock();
      if (!(var1 instanceof BlockStateBase)) {
         return var4;
      }

      BlockStateBase var11 = (BlockStateBase)var1;
      if (tileProperties != null) {
         int var12 = var4.getIndexInMap();
         if (var12 >= 0 && var12 < tileProperties.length) {
            ConnectedProperties[] var13 = tileProperties[var12];
            if (var13 != null) {
               int var14 = getSide(var3);

               for (int var15 = 0; var15 < var13.length; var15++) {
                  ConnectedProperties var16 = var13[var15];
                  if (var16 != null && var16.matchesBlockId(var11.getBlockId())) {
                     TextureAtlasSprite var17 = getConnectedTexture(var16, var0, var11, var2, var14, var4, var6);
                     if (var17 != null) {
                        return var17;
                     }
                  }
               }
            }
         }
      }

      if (blockProperties != null && var5) {
         int var18 = var6.getBlockId();
         if (var18 >= 0 && var18 < blockProperties.length) {
            ConnectedProperties[] var19 = blockProperties[var18];
            if (var19 != null) {
               int var20 = getSide(var3);

               for (int var21 = 0; var21 < var19.length; var21++) {
                  ConnectedProperties var22 = var19[var21];
                  if (var22 != null && var22.matchesIcon(var4)) {
                     TextureAtlasSprite var23 = getConnectedTexture(var22, var0, var11, var2, var20, var4, var6);
                     if (var23 != null) {
                        return var23;
                     }
                  }
               }
            }
         }
      }

      return var4;
   }

   public static int getSide(EnumFacing var0) {
      if (var0 == null) {
         return -1;
      }

      switch (var0) {
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

   private static EnumFacing getFacing(int var0) {
      switch (var0) {
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
      ConnectedProperties var0, IBlockAccess var1, BlockStateBase var2, BlockPos var3, int var4, TextureAtlasSprite var5, RenderEnv var6
   ) {
      int var10 = 0;
      int var11 = var2.getMetadata();
      int var12 = var11;
      Block var13 = var2.getBlock();
      if (var13 instanceof BlockRotatedPillar) {
         var10 = getWoodAxis(var4, var11);
         if (var0.getMetadataMax() <= 3) {
            var12 = var11 & 3;
         }
      }

      if (var13 instanceof BlockQuartz) {
         var10 = r(var4, var11);
         if (var0.getMetadataMax() <= 2 && var12 > 2) {
            var12 = 2;
         }
      }

      if (!var0.matchesBlock(var2.getBlockId(), var12)) {
         return null;
      }

      if (var4 >= 0 && var0.faces != 63) {
         int var14 = var4;
         if (var10 != 0) {
            var14 = P(var4, var10);
         }

         if ((1 << var14 & var0.faces) == 0) {
            return null;
         }
      }

      int var16 = var3.getY();
      if (var16 >= var0.N && var16 <= var0.l) {
         if (var0.biomes != null) {
            BiomeGenBase var15 = var1.getBiomeGenForCoords(var3);
            if (!var0.matchesBiome(var15)) {
               return null;
            }
         }

         switch (var0.method) {
            case 1:
               return getConnectedTextureCtm(var0, var1, var2, var3, var10, var4, var5, var11, var6);
            case 2:
               return N(var0, var1, var2, var3, var10, var4, var5, var11);
            case 3:
               return p(var0, var1, var2, var3, var10, var4, var5, var11);
            case 4:
               return H(var0, var3, var4);
            case 5:
               return l(var0, var3, var4);
            case 6:
               return K(var0, var1, var2, var3, var10, var4, var5, var11);
            case 7:
               return getConnectedTextureFixed(var0);
            case 8:
               return b(var0, var1, var2, var3, var10, var4, var5, var11);
            case 9:
               return O(var0, var1, var2, var3, var10, var4, var5, var11);
            default:
               return null;
         }
      } else {
         return null;
      }
   }

   private static int P(int var0, int var1) {
      switch (var1) {
         case 0:
            return var0;
         case 1:
            switch (var0) {
               case 0:
                  return 2;
               case 1:
                  return 3;
               case 2:
                  return 1;
               case 3:
                  return 0;
               default:
                  return var0;
            }
         case 2:
            switch (var0) {
               case 0:
                  return 4;
               case 1:
                  return 5;
               case 2:
               case 3:
               default:
                  return var0;
               case 4:
                  return 1;
               case 5:
                  return 0;
            }
         default:
            return var0;
      }
   }

   private static int getWoodAxis(int var0, int var1) {
      int var5 = (var1 & 12) >> 2;
      switch (var5) {
         case 1:
            return 2;
         case 2:
            return 1;
         default:
            return 0;
      }
   }

   private static int r(int var0, int var1) {
      switch (var1) {
         case 3:
            return 2;
         case 4:
            return 1;
         default:
            return 0;
      }
   }

   private static TextureAtlasSprite H(ConnectedProperties var0, BlockPos var1, int var2) {
      if (var0.tileIcons.length == 1) {
         return var0.tileIcons[0];
      }

      int var6 = var2 / var0.symmetry * var0.symmetry;
      int var7 = Config.getRandom(var1, var6) & 2147483647;
      int var8 = 0;
      if (var0.weights == null) {
         var8 = var7 % var0.tileIcons.length;
      } else {
         int var9 = var7 % var0.sumAllWeights;
         int[] var10 = var0.sumWeights;

         for (int var11 = 0; var11 < var10.length; var11++) {
            if (var9 < var10[var11]) {
               var8 = var11;
               break;
            }
         }
      }

      return var0.tileIcons[var8];
   }

   private static TextureAtlasSprite getConnectedTextureFixed(ConnectedProperties var0) {
      return var0.tileIcons[0];
   }

   private static TextureAtlasSprite l(ConnectedProperties var0, BlockPos var1, int var2) {
      if (var0.tileIcons.length == 1) {
         return var0.tileIcons[0];
      }

      int var6 = var1.getX();
      int var7 = var1.getY();
      int var8 = var1.getZ();
      int var9 = 0;
      int var10 = 0;
      switch (var2) {
         case 0:
            var9 = var6;
            var10 = var8;
            break;
         case 1:
            var9 = var6;
            var10 = var8;
            break;
         case 2:
            var9 = -var6 - 1;
            var10 = -var7;
            break;
         case 3:
            var9 = var6;
            var10 = -var7;
            break;
         case 4:
            var9 = var8;
            var10 = -var7;
            break;
         case 5:
            var9 = -var8 - 1;
            var10 = -var7;
      }

      var9 %= var0.width;
      var10 %= var0.height;
      if (var9 < 0) {
         var9 += var0.width;
      }

      if (var10 < 0) {
         var10 += var0.height;
      }

      int var11 = var10 * var0.width + var9;
      return var0.tileIcons[var11];
   }

   private static TextureAtlasSprite getConnectedTextureCtm(
      ConnectedProperties var0, IBlockAccess var1, IBlockState var2, BlockPos var3, int var4, int var5, TextureAtlasSprite var6, int var7, RenderEnv var8
   ) {
      boolean[] var12 = var8.getBorderFlags();
      switch (var5) {
         case 0:
            var12[0] = isNeighbour(var0, var1, var2, var3.west(), var5, var6, var7);
            var12[1] = isNeighbour(var0, var1, var2, var3.east(), var5, var6, var7);
            var12[2] = isNeighbour(var0, var1, var2, var3.north(), var5, var6, var7);
            var12[3] = isNeighbour(var0, var1, var2, var3.south(), var5, var6, var7);
            break;
         case 1:
            var12[0] = isNeighbour(var0, var1, var2, var3.west(), var5, var6, var7);
            var12[1] = isNeighbour(var0, var1, var2, var3.east(), var5, var6, var7);
            var12[2] = isNeighbour(var0, var1, var2, var3.south(), var5, var6, var7);
            var12[3] = isNeighbour(var0, var1, var2, var3.north(), var5, var6, var7);
            break;
         case 2:
            var12[0] = isNeighbour(var0, var1, var2, var3.east(), var5, var6, var7);
            var12[1] = isNeighbour(var0, var1, var2, var3.west(), var5, var6, var7);
            var12[2] = isNeighbour(var0, var1, var2, var3.down(), var5, var6, var7);
            var12[3] = isNeighbour(var0, var1, var2, var3.up(), var5, var6, var7);
            break;
         case 3:
            var12[0] = isNeighbour(var0, var1, var2, var3.west(), var5, var6, var7);
            var12[1] = isNeighbour(var0, var1, var2, var3.east(), var5, var6, var7);
            var12[2] = isNeighbour(var0, var1, var2, var3.down(), var5, var6, var7);
            var12[3] = isNeighbour(var0, var1, var2, var3.up(), var5, var6, var7);
            break;
         case 4:
            var12[0] = isNeighbour(var0, var1, var2, var3.north(), var5, var6, var7);
            var12[1] = isNeighbour(var0, var1, var2, var3.south(), var5, var6, var7);
            var12[2] = isNeighbour(var0, var1, var2, var3.down(), var5, var6, var7);
            var12[3] = isNeighbour(var0, var1, var2, var3.up(), var5, var6, var7);
            break;
         case 5:
            var12[0] = isNeighbour(var0, var1, var2, var3.south(), var5, var6, var7);
            var12[1] = isNeighbour(var0, var1, var2, var3.north(), var5, var6, var7);
            var12[2] = isNeighbour(var0, var1, var2, var3.down(), var5, var6, var7);
            var12[3] = isNeighbour(var0, var1, var2, var3.up(), var5, var6, var7);
      }

      byte var13 = 0;
      if (var12[0] & !var12[1] & !var12[2] & !var12[3]) {
         var13 = 3;
      } else if (!var12[0] & var12[1] & !var12[2] & !var12[3]) {
         var13 = 1;
      } else if (!var12[0] & !var12[1] & var12[2] & !var12[3]) {
         var13 = 12;
      } else if (!var12[0] & !var12[1] & !var12[2] & var12[3]) {
         var13 = 36;
      } else if (var12[0] & var12[1] & !var12[2] & !var12[3]) {
         var13 = 2;
      } else if (!var12[0] & !var12[1] & var12[2] & var12[3]) {
         var13 = 24;
      } else if (var12[0] & !var12[1] & var12[2] & !var12[3]) {
         var13 = 15;
      } else if (var12[0] & !var12[1] & !var12[2] & var12[3]) {
         var13 = 39;
      } else if (!var12[0] & var12[1] & var12[2] & !var12[3]) {
         var13 = 13;
      } else if (!var12[0] & var12[1] & !var12[2] & var12[3]) {
         var13 = 37;
      } else if (!var12[0] & var12[1] & var12[2] & var12[3]) {
         var13 = 25;
      } else if (var12[0] & !var12[1] & var12[2] & var12[3]) {
         var13 = 27;
      } else if (var12[0] & var12[1] & !var12[2] & var12[3]) {
         var13 = 38;
      } else if (var12[0] & var12[1] & var12[2] & !var12[3]) {
         var13 = 14;
      } else if (var12[0] & var12[1] & var12[2] & var12[3]) {
         var13 = 26;
      }

      if (var13 == 0) {
         return var0.tileIcons[var13];
      }

      if (!Config.isConnectedTexturesFancy()) {
         return var0.tileIcons[var13];
      }

      switch (var5) {
         case 0:
            var12[0] = !isNeighbour(var0, var1, var2, var3.east().north(), var5, var6, var7);
            var12[1] = !isNeighbour(var0, var1, var2, var3.west().north(), var5, var6, var7);
            var12[2] = !isNeighbour(var0, var1, var2, var3.east().south(), var5, var6, var7);
            var12[3] = !isNeighbour(var0, var1, var2, var3.west().south(), var5, var6, var7);
            break;
         case 1:
            var12[0] = !isNeighbour(var0, var1, var2, var3.east().south(), var5, var6, var7);
            var12[1] = !isNeighbour(var0, var1, var2, var3.west().south(), var5, var6, var7);
            var12[2] = !isNeighbour(var0, var1, var2, var3.east().north(), var5, var6, var7);
            var12[3] = !isNeighbour(var0, var1, var2, var3.west().north(), var5, var6, var7);
            break;
         case 2:
            var12[0] = !isNeighbour(var0, var1, var2, var3.west().down(), var5, var6, var7);
            var12[1] = !isNeighbour(var0, var1, var2, var3.east().down(), var5, var6, var7);
            var12[2] = !isNeighbour(var0, var1, var2, var3.west().up(), var5, var6, var7);
            var12[3] = !isNeighbour(var0, var1, var2, var3.east().up(), var5, var6, var7);
            break;
         case 3:
            var12[0] = !isNeighbour(var0, var1, var2, var3.east().down(), var5, var6, var7);
            var12[1] = !isNeighbour(var0, var1, var2, var3.west().down(), var5, var6, var7);
            var12[2] = !isNeighbour(var0, var1, var2, var3.east().up(), var5, var6, var7);
            var12[3] = !isNeighbour(var0, var1, var2, var3.west().up(), var5, var6, var7);
            break;
         case 4:
            var12[0] = !isNeighbour(var0, var1, var2, var3.down().south(), var5, var6, var7);
            var12[1] = !isNeighbour(var0, var1, var2, var3.down().north(), var5, var6, var7);
            var12[2] = !isNeighbour(var0, var1, var2, var3.up().south(), var5, var6, var7);
            var12[3] = !isNeighbour(var0, var1, var2, var3.up().north(), var5, var6, var7);
            break;
         case 5:
            var12[0] = !isNeighbour(var0, var1, var2, var3.down().north(), var5, var6, var7);
            var12[1] = !isNeighbour(var0, var1, var2, var3.down().south(), var5, var6, var7);
            var12[2] = !isNeighbour(var0, var1, var2, var3.up().north(), var5, var6, var7);
            var12[3] = !isNeighbour(var0, var1, var2, var3.up().south(), var5, var6, var7);
      }

      if (var13 == 13 && var12[0]) {
         var13 = 4;
      } else if (var13 == 15 && var12[1]) {
         var13 = 5;
      } else if (var13 == 37 && var12[2]) {
         var13 = 16;
      } else if (var13 == 39 && var12[3]) {
         var13 = 17;
      } else if (var13 == 14 && var12[0] && var12[1]) {
         var13 = 7;
      } else if (var13 == 25 && var12[0] && var12[2]) {
         var13 = 6;
      } else if (var13 == 27 && var12[3] && var12[1]) {
         var13 = 19;
      } else if (var13 == 38 && var12[3] && var12[2]) {
         var13 = 18;
      } else if (var13 == 14 && !var12[0] && var12[1]) {
         var13 = 31;
      } else if (var13 == 25 && var12[0] && !var12[2]) {
         var13 = 30;
      } else if (var13 == 27 && !var12[3] && var12[1]) {
         var13 = 41;
      } else if (var13 == 38 && var12[3] && !var12[2]) {
         var13 = 40;
      } else if (var13 == 14 && var12[0] && !var12[1]) {
         var13 = 29;
      } else if (var13 == 25 && !var12[0] && var12[2]) {
         var13 = 28;
      } else if (var13 == 27 && var12[3] && !var12[1]) {
         var13 = 43;
      } else if (var13 == 38 && !var12[3] && var12[2]) {
         var13 = 42;
      } else if (var13 == 26 && var12[0] && var12[1] && var12[2] && var12[3]) {
         var13 = 46;
      } else if (var13 == 26 && !var12[0] && var12[1] && var12[2] && var12[3]) {
         var13 = 9;
      } else if (var13 == 26 && var12[0] && !var12[1] && var12[2] && var12[3]) {
         var13 = 21;
      } else if (var13 == 26 && var12[0] && var12[1] && !var12[2] && var12[3]) {
         var13 = 8;
      } else if (var13 == 26 && var12[0] && var12[1] && var12[2] && !var12[3]) {
         var13 = 20;
      } else if (var13 == 26 && var12[0] && var12[1] && !var12[2] && !var12[3]) {
         var13 = 11;
      } else if (var13 == 26 && !var12[0] && !var12[1] && var12[2] && var12[3]) {
         var13 = 22;
      } else if (var13 == 26 && !var12[0] && var12[1] && !var12[2] && var12[3]) {
         var13 = 23;
      } else if (var13 == 26 && var12[0] && !var12[1] && var12[2] && !var12[3]) {
         var13 = 10;
      } else if (var13 == 26 && var12[0] && !var12[1] && !var12[2] && var12[3]) {
         var13 = 34;
      } else if (var13 == 26 && !var12[0] && var12[1] && var12[2] && !var12[3]) {
         var13 = 35;
      } else if (var13 == 26 && var12[0] && !var12[1] && !var12[2] && !var12[3]) {
         var13 = 32;
      } else if (var13 == 26 && !var12[0] && var12[1] && !var12[2] && !var12[3]) {
         var13 = 33;
      } else if (var13 == 26 && !var12[0] && !var12[1] && var12[2] && !var12[3]) {
         var13 = 44;
      } else if (var13 == 26 && !var12[0] && !var12[1] && !var12[2] && var12[3]) {
         var13 = 45;
      }

      return var0.tileIcons[var13];
   }

   private static boolean isNeighbour(ConnectedProperties var0, IBlockAccess var1, IBlockState var2, BlockPos var3, int var4, TextureAtlasSprite var5, int var6) {
      IBlockState var10 = var1.getBlockState(var3);
      if (var2 == var10) {
         return true;
      }

      if (var0.connect == 2) {
         if (var10 == null) {
            return false;
         }

         if (var10 == AIR_DEFAULT_STATE) {
            return false;
         }

         TextureAtlasSprite var11 = getNeighbourIcon(var1, var3, var10, var4);
         return var11 == var5;
      } else {
         return var0.connect == 3
            ? (var10 == null ? false : (var10 == AIR_DEFAULT_STATE ? false : var10.getBlock().getMaterial() == var2.getBlock().getMaterial()))
            : false;
      }
   }

   private static TextureAtlasSprite getNeighbourIcon(IBlockAccess var0, BlockPos var1, IBlockState var2, int var3) {
      var2 = var2.getBlock().getActualState(var2, var0, var1);
      IBakedModel var7 = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(var2);
      if (var7 == null) {
         return null;
      }

      EnumFacing var8 = getFacing(var3);
      List var9 = var7.getFaceQuads(var8);
      if (var9.size() > 0) {
         BakedQuad var14 = (BakedQuad)var9.get(0);
         return var14.getSprite();
      }

      List var10 = var7.getGeneralQuads();

      for (int var11 = 0; var11 < var10.size(); var11++) {
         BakedQuad var12 = (BakedQuad)var10.get(var11);
         if (var12.getFace() == var8) {
            return var12.getSprite();
         }
      }

      return null;
   }

   private static TextureAtlasSprite N(
      ConnectedProperties var0, IBlockAccess var1, IBlockState var2, BlockPos var3, int var4, int var5, TextureAtlasSprite var6, int var7
   ) {
      boolean var11;
      boolean var12;
      var11 = false;
      var12 = false;
      label43:
      switch (var4) {
         case 0:
            switch (var5) {
               case 0:
               case 1:
                  return null;
               case 2:
                  var11 = isNeighbour(var0, var1, var2, var3.east(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.west(), var5, var6, var7);
                  break label43;
               case 3:
                  var11 = isNeighbour(var0, var1, var2, var3.west(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.east(), var5, var6, var7);
                  break label43;
               case 4:
                  var11 = isNeighbour(var0, var1, var2, var3.north(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.south(), var5, var6, var7);
                  break label43;
               case 5:
                  var11 = isNeighbour(var0, var1, var2, var3.south(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.north(), var5, var6, var7);
               default:
                  break label43;
            }
         case 1:
            switch (var5) {
               case 0:
                  var11 = isNeighbour(var0, var1, var2, var3.west(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.east(), var5, var6, var7);
                  break label43;
               case 1:
                  var11 = isNeighbour(var0, var1, var2, var3.west(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.east(), var5, var6, var7);
                  break label43;
               case 2:
               case 3:
                  return null;
               case 4:
                  var11 = isNeighbour(var0, var1, var2, var3.down(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.up(), var5, var6, var7);
                  break label43;
               case 5:
                  var11 = isNeighbour(var0, var1, var2, var3.up(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.down(), var5, var6, var7);
               default:
                  break label43;
            }
         case 2:
            switch (var5) {
               case 0:
                  var11 = isNeighbour(var0, var1, var2, var3.north(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.south(), var5, var6, var7);
                  break;
               case 1:
                  var11 = isNeighbour(var0, var1, var2, var3.north(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.south(), var5, var6, var7);
                  break;
               case 2:
                  var11 = isNeighbour(var0, var1, var2, var3.down(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.up(), var5, var6, var7);
                  break;
               case 3:
                  var11 = isNeighbour(var0, var1, var2, var3.up(), var5, var6, var7);
                  var12 = isNeighbour(var0, var1, var2, var3.down(), var5, var6, var7);
                  break;
               case 4:
               case 5:
                  return null;
            }
      }

      byte var13 = 3;
      if (var11) {
         if (var12) {
            var13 = 1;
         } else {
            var13 = 2;
         }
      } else if (var12) {
         var13 = 0;
      } else {
         var13 = 3;
      }

      return var0.tileIcons[var13];
   }

   private static TextureAtlasSprite K(
      ConnectedProperties var0, IBlockAccess var1, IBlockState var2, BlockPos var3, int var4, int var5, TextureAtlasSprite var6, int var7
   ) {
      boolean var11 = false;
      boolean var12 = false;
      switch (var4) {
         case 0:
            if (var5 == 1 || var5 == 0) {
               return null;
            }

            var11 = isNeighbour(var0, var1, var2, var3.down(), var5, var6, var7);
            var12 = isNeighbour(var0, var1, var2, var3.up(), var5, var6, var7);
            break;
         case 1:
            if (var5 == 3 || var5 == 2) {
               return null;
            }

            var11 = isNeighbour(var0, var1, var2, var3.south(), var5, var6, var7);
            var12 = isNeighbour(var0, var1, var2, var3.north(), var5, var6, var7);
            break;
         case 2:
            if (var5 == 5 || var5 == 4) {
               return null;
            }

            var11 = isNeighbour(var0, var1, var2, var3.west(), var5, var6, var7);
            var12 = isNeighbour(var0, var1, var2, var3.east(), var5, var6, var7);
      }

      byte var13 = 3;
      if (var11) {
         if (var12) {
            var13 = 1;
         } else {
            var13 = 2;
         }
      } else if (var12) {
         var13 = 0;
      } else {
         var13 = 3;
      }

      return var0.tileIcons[var13];
   }

   private static TextureAtlasSprite b(
      ConnectedProperties var0, IBlockAccess var1, IBlockState var2, BlockPos var3, int var4, int var5, TextureAtlasSprite var6, int var7
   ) {
      TextureAtlasSprite[] var11 = var0.tileIcons;
      TextureAtlasSprite var12 = N(var0, var1, var2, var3, var4, var5, var6, var7);
      if (var12 != null && var12 != var6 && var12 != var11[3]) {
         return var12;
      }

      TextureAtlasSprite var13 = K(var0, var1, var2, var3, var4, var5, var6, var7);
      return var13 == var11[0] ? var11[4] : (var13 == var11[1] ? var11[5] : (var13 == var11[2] ? var11[6] : var13));
   }

   private static TextureAtlasSprite O(
      ConnectedProperties var0, IBlockAccess var1, IBlockState var2, BlockPos var3, int var4, int var5, TextureAtlasSprite var6, int var7
   ) {
      TextureAtlasSprite[] var11 = var0.tileIcons;
      TextureAtlasSprite var12 = K(var0, var1, var2, var3, var4, var5, var6, var7);
      if (var12 != null && var12 != var6 && var12 != var11[3]) {
         return var12;
      }

      TextureAtlasSprite var13 = N(var0, var1, var2, var3, var4, var5, var6, var7);
      return var13 == var11[0] ? var11[4] : (var13 == var11[1] ? var11[5] : (var13 == var11[2] ? var11[6] : var13));
   }

   private static TextureAtlasSprite p(
      ConnectedProperties var0, IBlockAccess var1, IBlockState var2, BlockPos var3, int var4, int var5, TextureAtlasSprite var6, int var7
   ) {
      boolean var11 = false;
      switch (var4) {
         case 0:
            if (var5 == 1 || var5 == 0) {
               return null;
            }

            var11 = isNeighbour(var0, var1, var2, var3.up(), var5, var6, var7);
            break;
         case 1:
            if (var5 == 3 || var5 == 2) {
               return null;
            }

            var11 = isNeighbour(var0, var1, var2, var3.south(), var5, var6, var7);
            break;
         case 2:
            if (var5 == 5 || var5 == 4) {
               return null;
            }

            var11 = isNeighbour(var0, var1, var2, var3.east(), var5, var6, var7);
      }

      return var11 ? var0.tileIcons[0] : null;
   }

   public static void updateIcons(TextureMap var0) {
      blockProperties = (ConnectedProperties[][])null;
      tileProperties = (ConnectedProperties[][])null;
      spriteQuadMaps = null;
      if (Config.isConnectedTextures()) {
         IResourcePack[] var4 = Config.getResourcePacks();

         for (int var5 = var4.length - 1; var5 >= 0; var5--) {
            IResourcePack var6 = var4[var5];
            updateIcons(var0, var6);
         }

         updateIcons(var0, Config.getDefaultResourcePack());
         ResourceLocation var7 = new ResourceLocation("mcpatcher/ctm/default/empty");
         emptySprite = var0.registerSprite(var7);
         spriteQuadMaps = new Map[var0.getCountRegisteredSprites() + 1];
         if (blockProperties.length <= 0) {
            blockProperties = (ConnectedProperties[][])null;
         }

         if (tileProperties.length <= 0) {
            tileProperties = (ConnectedProperties[][])null;
         }
      }
   }

   public static void updateIcons(TextureMap var0, IResourcePack var1) {
      String[] var5 = ResUtils.collectFiles(var1, "mcpatcher/ctm/", ".properties", getDefaultCtmPaths());
      Arrays.sort(var5);
      List var6 = makePropertyList(tileProperties);
      List var7 = makePropertyList(blockProperties);

      for (int var8 = 0; var8 < var5.length; var8++) {
         String var9 = var5[var8];
         Config.dbg("ConnectedTextures: " + var9);

         try {
            ResourceLocation var10 = new ResourceLocation(var9);
            InputStream var11 = var1.getInputStream(var10);
            if (var11 == null) {
               Config.warn("ConnectedTextures file not found: " + var9);
            } else {
               Properties var12 = new Properties();
               var12.load(var11);
               ConnectedProperties var13 = new ConnectedProperties(var12, var9);
               if (var13.isValid(var9)) {
                  var13.updateIcons(var0);
                  addToTileList(var13, var6);
                  addToBlockList(var13, var7);
               }
            }
         } catch (FileNotFoundException var14) {
            Config.warn("ConnectedTextures file not found: " + var9);
         } catch (Exception var15) {
            var15.printStackTrace();
         }
      }

      blockProperties = propertyListToArray(var7);
      tileProperties = propertyListToArray(var6);
      multipass = detectMultipass();
      Config.dbg("Multipass connected textures: " + multipass);
   }

   private static List makePropertyList(ConnectedProperties[][] var0) {
      ArrayList var4 = new ArrayList();
      if (var0 != null) {
         for (int var5 = 0; var5 < var0.length; var5++) {
            ConnectedProperties[] var6 = var0[var5];
            ArrayList var7 = null;
            if (var6 != null) {
               var7 = new ArrayList<>(Arrays.asList(var6));
            }

            var4.add(var7);
         }
      }

      return var4;
   }

   private static boolean detectMultipass() {
      ArrayList var3 = new ArrayList();

      for (int var4 = 0; var4 < tileProperties.length; var4++) {
         ConnectedProperties[] var5 = tileProperties[var4];
         if (var5 != null) {
            var3.addAll(Arrays.asList(var5));
         }
      }

      for (int var9 = 0; var9 < blockProperties.length; var9++) {
         ConnectedProperties[] var11 = blockProperties[var9];
         if (var11 != null) {
            var3.addAll(Arrays.asList(var11));
         }
      }

      ConnectedProperties[] var10 = (net.minecraft.src.ConnectedProperties[])(var3.toArray(new ConnectedProperties[var3.size()]));
      HashSet var12 = new HashSet();
      HashSet var6 = new HashSet();

      for (int var7 = 0; var7 < var10.length; var7++) {
         ConnectedProperties var8 = var10[var7];
         if (var8.matchTileIcons != null) {
            var12.addAll(Arrays.asList(var8.matchTileIcons));
         }

         if (var8.tileIcons != null) {
            var6.addAll(Arrays.asList(var8.tileIcons));
         }
      }

      var12.retainAll(var6);
      return !var12.isEmpty();
   }

   private static ConnectedProperties[][] propertyListToArray(List var0) {
      ConnectedProperties[][] var4 = new ConnectedProperties[var0.size()][];

      for (int var5 = 0; var5 < var0.size(); var5++) {
         List var6 = (List)var0.get(var5);
         if (var6 != null) {
            ConnectedProperties[] var7 = (net.minecraft.src.ConnectedProperties[])(var6.toArray(new ConnectedProperties[var6.size()]));
            var4[var5] = var7;
         }
      }

      return var4;
   }

   private static void addToTileList(ConnectedProperties var0, List var1) {
      if (var0.matchTileIcons != null) {
         for (int var5 = 0; var5 < var0.matchTileIcons.length; var5++) {
            TextureAtlasSprite var6 = var0.matchTileIcons[var5];
            if (!(var6 instanceof TextureAtlasSprite)) {
               Config.warn("TextureAtlasSprite is not TextureAtlasSprite: " + var6 + ", name: " + var6.getIconName());
            } else {
               int var7 = var6.getIndexInMap();
               if (var7 < 0) {
                  Config.warn("Invalid tile ID: " + var7 + ", icon: " + var6.getIconName());
               } else {
                  addToList(var0, var1, var7);
               }
            }
         }
      }
   }

   private static void addToBlockList(ConnectedProperties var0, List var1) {
      if (var0.matchBlocks != null) {
         for (int var5 = 0; var5 < var0.matchBlocks.length; var5++) {
            int var6 = var0.matchBlocks[var5].getBlockId();
            if (var6 < 0) {
               Config.warn("Invalid block ID: " + var6);
            } else {
               addToList(var0, var1, var6);
            }
         }
      }
   }

   private static void addToList(ConnectedProperties var0, List var1, int var2) {
      while (var2 >= var1.size()) {
         var1.add(null);
      }

      List var6 = (List)var1.get(var2);
      if (var6 == null) {
         var6 = new ArrayList();
         var1.set(var2, var6);
      }

      var6.add(var0);
   }

   private static String[] getDefaultCtmPaths() {
      ArrayList var3 = new ArrayList();
      String var4 = "mcpatcher/ctm/default/";
      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png"))) {
         var3.add(var4 + "glass.properties");
         var3.add(var4 + "glasspane.properties");
      }

      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png"))) {
         var3.add(var4 + "bookshelf.properties");
      }

      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png"))) {
         var3.add(var4 + "sandstone.properties");
      }

      String[] var5 = new String[]{
         "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"
      };

      for (int var6 = 0; var6 < var5.length; var6++) {
         String var7 = var5[var6];
         if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + var7 + ".png"))) {
            var3.add(var4 + var6 + "_glass_" + var7 + "/glass_" + var7 + ".properties");
            var3.add(var4 + var6 + "_glass_" + var7 + "/glass_pane_" + var7 + ".properties");
         }
      }

      return (java.lang.String[])(var3.toArray(new String[var3.size()]));
   }

   public static int getPaneTextureIndex(boolean var0, boolean var1, boolean var2, boolean var3) {
      return var1 && var0
         ? (var2 ? (var3 ? 34 : 50) : (var3 ? 18 : 2))
         : (
            var1 && !var0
               ? (var2 ? (var3 ? 35 : 51) : (var3 ? 19 : 3))
               : (!var1 && var0 ? (var2 ? (var3 ? 33 : 49) : (var3 ? 17 : 1)) : (var2 ? (var3 ? 32 : 48) : (var3 ? 16 : 0)))
         );
   }

   public static int getReversePaneTextureIndex(int var0) {
      int var4 = var0 % 16;
      return var4 == 1 ? var0 + 2 : (var4 == 3 ? var0 - 2 : var0);
   }

   public static TextureAtlasSprite getCtmTexture(ConnectedProperties var0, int var1, TextureAtlasSprite var2) {
      if (var0.method != 1) {
         return var2;
      } else if (var1 >= 0 && var1 < ctmIndexes.length) {
         int var6 = ctmIndexes[var1];
         TextureAtlasSprite[] var7 = var0.tileIcons;
         return var6 >= 0 && var6 < var7.length ? var7[var6] : var2;
      } else {
         return var2;
      }
   }

   static {
      long var20 = a ^ 40006994803512L;
      long var10000 = var20 ^ 617817944395236024L;
      var10000 = var20 ^ 470373425828205768L;
      var10000 = var20 ^ 175991781878921116L;
      var10000 = var20 ^ 175991781878921116L;
      var10000 = var20 ^ 5078607984497868203L;
      var10000 = 8622501404010109995L ^ var20;
   }
}
