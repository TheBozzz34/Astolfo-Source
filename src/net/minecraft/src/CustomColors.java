package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CustomColors {
   private static String paletteFormatDefault = "vanilla";
   private static CustomColormap waterColors = null;
   private static CustomColormap foliagePineColors = null;
   private static CustomColormap foliageBirchColors = null;
   private static CustomColormap swampFoliageColors = null;
   private static CustomColormap swampGrassColors = null;
   private static CustomColormap[] colorsBlockColormaps = null;
   private static CustomColormap[][] blockColormaps = (CustomColormap[][])null;
   private static CustomColormap G = null;
   private static CustomColorFader J = new CustomColorFader();
   private static CustomColormap h = null;
   private static CustomColorFader N = new CustomColorFader();
   private static CustomColormap L = null;
   private static CustomColorFader P = new CustomColorFader();
   private static CustomColormap[] F = null;
   private static int lightmapMinDimensionId = 0;
   private static float[][] K = new float[16][3];
   private static float[][] R = new float[16][3];
   private static CustomColormap redstoneColors = null;
   private static CustomColormap xpOrbColors = null;
   private static int xpOrbTime = -1;
   private static CustomColormap durabilityColors = null;
   private static CustomColormap stemColors = null;
   private static CustomColormap stemMelonColors = null;
   private static CustomColormap stemPumpkinColors = null;
   private static CustomColormap v = null;
   private static boolean useDefaultGrassFoliageColors = true;
   private static int particleWaterColor = -1;
   private static int particlePortalColor = -1;
   private static int lilyPadColor = -1;
   private static int expBarTextColor = -1;
   private static int bossTextColor = -1;
   private static int signTextColor = -1;
   private static Vec3 fogColorNether = null;
   private static Vec3 fogColorEnd = null;
   private static Vec3 skyColorEnd = null;
   private static int[] spawnEggPrimaryColors = null;
   private static int[] spawnEggSecondaryColors = null;
   private static float[][] wolfCollarColors = (float[][])null;
   private static float[][] sheepColors = (float[][])null;
   private static int[] textColors = null;
   private static int[] mapColorsOriginal = null;
   private static int[] potionColors = null;
   private static final IBlockState BLOCK_STATE_DIRT = Blocks.dirt.getDefaultState();
   private static final IBlockState BLOCK_STATE_WATER = Blocks.water.getDefaultState();
   public static Random random = new Random();
   private static final CustomColors.IColorizer COLORIZER_GRASS = new CustomColors.IColorizer() {
      private static final long a = 74837109615766L;

      @Override
      public int getColor(IBlockAccess var1, BlockPos var2) {
         BiomeGenBase var6 = CustomColors.getColorBiome(var1, var2);
         return CustomColors.U() != null && var6 == BiomeGenBase.swampland ? CustomColors.U().getColor(var6, var2) : var6.getGrassColorAtPos(var2);
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };
   private static final CustomColors.IColorizer COLORIZER_FOLIAGE = new CustomColors.IColorizer() {
      private static final long a = 80085882544834L;

      @Override
      public int getColor(IBlockAccess var1, BlockPos var2) {
         BiomeGenBase var6 = CustomColors.getColorBiome(var1, var2);
         return CustomColors.y() != null && var6 == BiomeGenBase.swampland ? CustomColors.y().getColor(var6, var2) : var6.getFoliageColorAtPos(var2);
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };
   private static final CustomColors.IColorizer COLORIZER_FOLIAGE_PINE = new CustomColors.IColorizer() {
      private static final long a = 76363573944486L;

      @Override
      public int getColor(IBlockAccess var1, BlockPos var2) {
         return CustomColors.I() != null ? CustomColors.I().getColor(var1, var2) : ColorizerFoliage.getFoliageColorPine();
      }

      @Override
      public boolean isColorConstant() {
         return CustomColors.I() == null;
      }
   };
   private static final CustomColors.IColorizer COLORIZER_FOLIAGE_BIRCH = new CustomColors.IColorizer() {
      private static final long a = 116302175848277L;

      @Override
      public int getColor(IBlockAccess var1, BlockPos var2) {
         return CustomColors.s() != null ? CustomColors.s().getColor(var1, var2) : ColorizerFoliage.getFoliageColorBirch();
      }

      @Override
      public boolean isColorConstant() {
         return CustomColors.s() == null;
      }
   };
   private static final CustomColors.IColorizer COLORIZER_WATER = new CustomColors.IColorizer() {
      private static final long a = 78334371687161L;

      @Override
      public int getColor(IBlockAccess var1, BlockPos var2) {
         BiomeGenBase var6 = CustomColors.getColorBiome(var1, var2);
         return CustomColors.k() != null
            ? CustomColors.k().getColor(var6, var2)
            : (
               Reflector.ForgeBiome_getWaterColorMultiplier.exists()
                  ? Reflector.callInt(var6, Reflector.ForgeBiome_getWaterColorMultiplier)
                  : var6.waterColorMultiplier
            );
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };
   private static final long O = 18250294499803L;

   public static void update() {
      long var0 = O ^ 30816986478181L;
      paletteFormatDefault = "vanilla";
      waterColors = null;
      foliageBirchColors = null;
      foliagePineColors = null;
      swampGrassColors = null;
      swampFoliageColors = null;
      G = null;
      h = null;
      L = null;
      redstoneColors = null;
      xpOrbColors = null;
      xpOrbTime = -1;
      durabilityColors = null;
      stemColors = null;
      v = null;
      F = null;
      particleWaterColor = -1;
      particlePortalColor = -1;
      lilyPadColor = -1;
      expBarTextColor = -1;
      bossTextColor = -1;
      signTextColor = -1;
      fogColorNether = null;
      fogColorEnd = null;
      skyColorEnd = null;
      colorsBlockColormaps = null;
      blockColormaps = (CustomColormap[][])null;
      useDefaultGrassFoliageColors = true;
      spawnEggPrimaryColors = null;
      spawnEggSecondaryColors = null;
      wolfCollarColors = (float[][])null;
      sheepColors = (float[][])null;
      textColors = null;
      setMapColors(mapColorsOriginal);
      potionColors = null;
      PotionHelper.clearPotionColorCache();
      paletteFormatDefault = getValidProperty("mcpatcher/color.properties", "palette.format", CustomColormap.FORMAT_STRINGS, "vanilla");
      String var3 = "mcpatcher/colormap/";
      String[] var4 = new String[]{"water.png", "watercolorX.png"};
      waterColors = getCustomColors(var3, var4, 256, 256);
      updateUseDefaultGrassFoliageColors();
      if (Config.isCustomColors()) {
         String[] var5 = new String[]{"pine.png", "pinecolor.png"};
         foliagePineColors = getCustomColors(var3, var5, 256, 256);
         String[] var6 = new String[]{"birch.png", "birchcolor.png"};
         foliageBirchColors = getCustomColors(var3, var6, 256, 256);
         String[] var7 = new String[]{"swampgrass.png", "swampgrasscolor.png"};
         swampGrassColors = getCustomColors(var3, var7, 256, 256);
         String[] var8 = new String[]{"swampfoliage.png", "swampfoliagecolor.png"};
         swampFoliageColors = getCustomColors(var3, var8, 256, 256);
         String[] var9 = new String[]{"sky0.png", "skycolor0.png"};
         G = getCustomColors(var3, var9, 256, 256);
         String[] var10 = new String[]{"fog0.png", "fogcolor0.png"};
         h = getCustomColors(var3, var10, 256, 256);
         String[] var11 = new String[]{"underwater.png", "underwatercolor.png"};
         L = getCustomColors(var3, var11, 256, 256);
         String[] var12 = new String[]{"redstone.png", "redstonecolor.png"};
         redstoneColors = getCustomColors(var3, var12, 16, 1);
         xpOrbColors = getCustomColors(var3 + "xporb.png", -1, -1);
         durabilityColors = getCustomColors(var3 + "durability.png", -1, -1);
         String[] var13 = new String[]{"stem.png", "stemcolor.png"};
         stemColors = getCustomColors(var3, var13, 8, 1);
         stemPumpkinColors = getCustomColors(var3 + "pumpkinstem.png", 8, 1);
         stemMelonColors = getCustomColors(var3 + "melonstem.png", 8, 1);
         String[] var14 = new String[]{"myceliumparticle.png", "myceliumparticlecolor.png"};
         v = getCustomColors(var3, var14, -1, -1);
         long var10000 = 5115117361574682099L ^ var0;
         Pair var15 = parseLightMapPacks();
         F = (CustomColormap[])var15.getLeft();
         lightmapMinDimensionId = (Integer)var15.getRight();
         readColorProperties("mcpatcher/color.properties");
         blockColormaps = readBlockColormaps(new String[]{var3 + "custom/", var3 + "blocks/"}, colorsBlockColormaps, 256, 256);
         updateUseDefaultGrassFoliageColors();
      }
   }

   private static String getValidProperty(String var0, String var1, String[] var2, String var3) {
      try {
         ResourceLocation var7 = new ResourceLocation(var0);
         InputStream var8 = Config.getResourceStream(var7);
         if (var8 == null) {
            return var3;
         } else {
            Properties var9 = new Properties();
            var9.load(var8);
            var8.close();
            String var10 = var9.getProperty(var1);
            if (var10 == null) {
               return var3;
            } else {
               List var11 = Arrays.asList(var2);
               if (!var11.contains(var10)) {
                  warn("Invalid value: " + var1 + "=" + var10);
                  warn("Expected values: " + Config.arrayToString(var2));
                  return var3;
               } else {
                  dbg("" + var1 + "=" + var10);
                  return var10;
               }
            }
         }
      } catch (FileNotFoundException var12) {
         return var3;
      } catch (IOException var13) {
         var13.printStackTrace();
         return var3;
      }
   }

   private static Pair<CustomColormap[], Integer> parseLightMapPacks() {
      String var3 = "mcpatcher/lightmap/world";
      String var4 = ".png";
      String[] var5 = ResUtils.collectFiles(var3, var4);
      HashMap var6 = new HashMap();

      for (int var7 = 0; var7 < var5.length; var7++) {
         String var8 = var5[var7];
         String var9 = StrUtils.removePrefixSuffix(var8, var3, var4);
         int var10 = Config.parseInt(var9, Integer.MIN_VALUE);
         if (var10 == Integer.MIN_VALUE) {
            warn("Invalid dimension ID: " + var9 + ", path: " + var8);
         } else {
            var6.put(var10, var8);
         }
      }

      Set var18 = var6.keySet();
      Integer[] var19 = (java.lang.Integer[])(var18.toArray(new Integer[var18.size()]));
      Arrays.sort(var19);
      if (var19.length <= 0) {
         return new ImmutablePair<>(null, 0);
      }

      int var20 = var19[0];
      int var21 = var19[var19.length - 1];
      int var11 = var21 - var20 + 1;
      CustomColormap[] var12 = new CustomColormap[var11];

      for (int var13 = 0; var13 < var19.length; var13++) {
         Integer var14 = var19[var13];
         String var15 = (String)var6.get(var14);
         CustomColormap var16 = getCustomColors(var15, -1, -1);
         if (var16 != null) {
            if (var16.i() < 16) {
               warn("Invalid lightmap width: " + var16.i() + ", path: " + var15);
            } else {
               int var17 = var14 - var20;
               var12[var17] = var16;
            }
         }
      }

      return new ImmutablePair<>(var12, var20);
   }

   private static void readColorProperties(String var0) {
      try {
         ResourceLocation var4 = new ResourceLocation(var0);
         InputStream var5 = Config.getResourceStream(var4);
         if (var5 == null) {
            return;
         }

         dbg("Loading " + var0);
         Properties var6 = new Properties();
         var6.load(var5);
         var5.close();
         particleWaterColor = readColor(var6, new String[]{"particle.water", "drop.water"});
         particlePortalColor = readColor(var6, "particle.portal");
         lilyPadColor = readColor(var6, "lilypad");
         expBarTextColor = readColor(var6, "text.xpbar");
         bossTextColor = readColor(var6, "text.boss");
         signTextColor = readColor(var6, "text.sign");
         fogColorNether = readColorVec3(var6, "fog.nether");
         fogColorEnd = readColorVec3(var6, "fog.end");
         skyColorEnd = readColorVec3(var6, "sky.end");
         colorsBlockColormaps = readCustomColormaps(var6, var0);
         spawnEggPrimaryColors = readSpawnEggColors(var6, var0, "egg.shell.", "Spawn egg shell");
         spawnEggSecondaryColors = readSpawnEggColors(var6, var0, "egg.spots.", "Spawn egg spot");
         wolfCollarColors = readDyeColors(var6, var0, "collar.", "Wolf collar");
         sheepColors = readDyeColors(var6, var0, "sheep.", "Sheep");
         textColors = readTextColors(var6, var0, "text.code.", "Text");
         int[] var7 = readMapColors(var6, var0, "map.", "Map");
         if (var7 != null) {
            if (mapColorsOriginal == null) {
               mapColorsOriginal = getMapColors();
            }

            setMapColors(var7);
         }

         potionColors = readPotionColors(var6, var0, "potion.", "Potion");
         xpOrbTime = Config.parseInt(var6.getProperty("xporb.time"), -1);
      } catch (FileNotFoundException var8) {
         return;
      } catch (IOException var9) {
         var9.printStackTrace();
      }
   }

   private static CustomColormap[] readCustomColormaps(Properties var0, String var1) {
      ArrayList var5 = new ArrayList();
      String var6 = "palette.block.";
      HashMap var7 = new HashMap();

      for (Object var9 : var0.keySet()) {
         String var10 = var0.getProperty((String)var9);
         if (((String)var9).startsWith(var6)) {
            var7.put(var9, var10);
         }
      }

      String[] var19 = (java.lang.String[])(var7.keySet().toArray(new String[var7.size()]));

      for (int var20 = 0; var20 < var19.length; var20++) {
         String var21 = var19[var20];
         String var11 = var0.getProperty(var21);
         dbg("Block palette: " + var21 + " = " + var11);
         String var12 = var21.substring(var6.length());
         String var13 = TextureUtils.getBasePath(var1);
         var12 = TextureUtils.fixResourcePath(var12, var13);
         CustomColormap var14 = getCustomColors(var12, 256, 256);
         if (var14 == null) {
            warn("Colormap not found: " + var12);
         } else {
            ConnectedParser var15 = new ConnectedParser("CustomColors");
            MatchBlock[] var16 = var15.t(var11);
            if (var16 != null && var16.length > 0) {
               for (int var17 = 0; var17 < var16.length; var17++) {
                  MatchBlock var18 = var16[var17];
                  var14.addMatchBlock(var18);
               }

               var5.add(var14);
            } else {
               warn("Invalid match blocks: " + var11);
            }
         }
      }

      return var5.size() <= 0 ? null : (net.minecraft.src.CustomColormap[])(var5.toArray(new CustomColormap[var5.size()]));
   }

   private static CustomColormap[][] readBlockColormaps(String[] var0, CustomColormap[] var1, int var2, int var3) {
      String[] var7 = ResUtils.collectFiles(var0, new String[]{".properties"});
      Arrays.sort(var7);
      ArrayList var8 = new ArrayList();

      for (int var9 = 0; var9 < var7.length; var9++) {
         String var10 = var7[var9];
         dbg("Block colormap: " + var10);

         try {
            ResourceLocation var11 = new ResourceLocation("minecraft", var10);
            InputStream var12 = Config.getResourceStream(var11);
            if (var12 == null) {
               warn("File not found: " + var10);
            } else {
               Properties var13 = new Properties();
               var13.load(var12);
               CustomColormap var14 = new CustomColormap(var13, var10, var2, var3, paletteFormatDefault);
               if (var14.isValid(var10) && var14.isValidMatchBlocks(var10)) {
                  addToBlockList(var14, var8);
               }
            }
         } catch (FileNotFoundException var15) {
            warn("File not found: " + var10);
         } catch (Exception var16) {
            var16.printStackTrace();
         }
      }

      if (var1 != null) {
         for (int var17 = 0; var17 < var1.length; var17++) {
            CustomColormap var18 = var1[var17];
            addToBlockList(var18, var8);
         }
      }

      return var8.size() <= 0 ? (CustomColormap[][])null : blockListToArray(var8);
   }

   private static void addToBlockList(CustomColormap var0, List var1) {
      int[] var5 = var0.getMatchBlockIds();
      if (var5 != null && var5.length > 0) {
         for (int var6 = 0; var6 < var5.length; var6++) {
            int var7 = var5[var6];
            if (var7 < 0) {
               warn("Invalid block ID: " + var7);
            } else {
               addToList(var0, var1, var7);
            }
         }
      } else {
         warn("No match blocks: " + Config.arrayToString(var5));
      }
   }

   private static void addToList(CustomColormap var0, List var1, int var2) {
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

   private static CustomColormap[][] blockListToArray(List var0) {
      CustomColormap[][] var4 = new CustomColormap[var0.size()][];

      for (int var5 = 0; var5 < var0.size(); var5++) {
         List var6 = (List)var0.get(var5);
         if (var6 != null) {
            CustomColormap[] var7 = (net.minecraft.src.CustomColormap[])(var6.toArray(new CustomColormap[var6.size()]));
            var4[var5] = var7;
         }
      }

      return var4;
   }

   private static int readColor(Properties var0, String[] var1) {
      for (int var5 = 0; var5 < var1.length; var5++) {
         String var6 = var1[var5];
         int var7 = readColor(var0, var6);
         if (var7 >= 0) {
            return var7;
         }
      }

      return -1;
   }

   private static int readColor(Properties var0, String var1) {
      String var5 = var0.getProperty(var1);
      if (var5 == null) {
         return -1;
      } else {
         var5 = var5.trim();
         int var6 = parseColor(var5);
         if (var6 < 0) {
            warn("Invalid color: " + var1 + " = " + var5);
            return var6;
         } else {
            dbg(var1 + " = " + var5);
            return var6;
         }
      }
   }

   private static int parseColor(String var0) {
      if (var0 == null) {
         return -1;
      }

      var0 = var0.trim();

      try {
         return Integer.parseInt(var0, 16) & 16777215;
      } catch (NumberFormatException var5) {
         return -1;
      }
   }

   private static Vec3 readColorVec3(Properties var0, String var1) {
      int var5 = readColor(var0, var1);
      if (var5 < 0) {
         return null;
      }

      int var6 = var5 >> 16 & 0xFF;
      int var7 = var5 >> 8 & 0xFF;
      int var8 = var5 & 0xFF;
      float var9 = var6 / 255.0F;
      float var10 = var7 / 255.0F;
      float var11 = var8 / 255.0F;
      return new Vec3(var9, var10, var11);
   }

   private static CustomColormap getCustomColors(String var0, String[] var1, int var2, int var3) {
      for (int var7 = 0; var7 < var1.length; var7++) {
         String var8 = var1[var7];
         var8 = var0 + var8;
         CustomColormap var9 = getCustomColors(var8, var2, var3);
         if (var9 != null) {
            return var9;
         }
      }

      return null;
   }

   public static CustomColormap getCustomColors(String var0, int var1, int var2) {
      try {
         ResourceLocation var6 = new ResourceLocation(var0);
         if (!Config.hasResource(var6)) {
            return null;
         }

         dbg("Colormap " + var0);
         Properties var7 = new Properties();
         String var8 = StrUtils.replaceSuffix(var0, ".png", ".properties");
         ResourceLocation var9 = new ResourceLocation(var8);
         if (Config.hasResource(var9)) {
            InputStream var10 = Config.getResourceStream(var9);
            var7.load(var10);
            var10.close();
            dbg("Colormap properties: " + var8);
         } else {
            var7.put("format", paletteFormatDefault);
            var7.put("source", var0);
            var8 = var0;
         }

         CustomColormap var12 = new CustomColormap(var7, var8, var1, var2, paletteFormatDefault);
         return !var12.isValid(var8) ? null : var12;
      } catch (Exception var11) {
         var11.printStackTrace();
         return null;
      }
   }

   public static void updateUseDefaultGrassFoliageColors() {
      useDefaultGrassFoliageColors = foliageBirchColors == null
         && foliagePineColors == null
         && swampGrassColors == null
         && swampFoliageColors == null
         && Config.isSwampColors()
         && Config.isSmoothBiomes();
   }

   public static int getColorMultiplier(BakedQuad var0, Block var1, IBlockAccess var2, BlockPos var3, RenderEnv var4) {
      if (blockColormaps != null) {
         IBlockState var8 = var4.getBlockState();
         if (!var0.hasTintIndex()) {
            if (var1 == Blocks.grass) {
               var8 = BLOCK_STATE_DIRT;
            }

            if (var1 == Blocks.redstone_wire) {
               return -1;
            }
         }

         if (var1 == Blocks.double_plant && var4.getMetadata() >= 8) {
            var3 = var3.down();
            var8 = var2.getBlockState(var3);
         }

         CustomColormap var9 = getBlockColormap(var8);
         if (var9 != null) {
            if (Config.isSmoothBiomes() && !var9.isColorConstant()) {
               return getSmoothColorMultiplier(var2, var3, var9, var4.getColorizerBlockPosM());
            }

            return var9.getColor(var2, var3);
         }
      }

      if (!var0.hasTintIndex()) {
         return -1;
      }

      if (var1 == Blocks.waterlily) {
         return getLilypadColorMultiplier(var2, var3);
      }

      if (var1 == Blocks.redstone_wire) {
         return getRedstoneColor(var4.getBlockState());
      }

      if (var1 instanceof BlockStem) {
         return getStemColorMultiplier(var1, var2, var3, var4);
      }

      if (useDefaultGrassFoliageColors) {
         return -1;
      }

      int var10 = var4.getMetadata();
      CustomColors.IColorizer var11;
      if (var1 == Blocks.grass || var1 == Blocks.tallgrass || var1 == Blocks.double_plant) {
         var11 = COLORIZER_GRASS;
      } else if (var1 == Blocks.double_plant) {
         var11 = COLORIZER_GRASS;
         if (var10 >= 8) {
            var3 = var3.down();
         }
      } else if (var1 == Blocks.leaves) {
         switch (var10 & 3) {
            case 0:
               var11 = COLORIZER_FOLIAGE;
               break;
            case 1:
               var11 = COLORIZER_FOLIAGE_PINE;
               break;
            case 2:
               var11 = COLORIZER_FOLIAGE_BIRCH;
               break;
            default:
               var11 = COLORIZER_FOLIAGE;
         }
      } else if (var1 == Blocks.leaves2) {
         var11 = COLORIZER_FOLIAGE;
      } else {
         if (var1 != Blocks.vine) {
            return -1;
         }

         var11 = COLORIZER_FOLIAGE;
      }

      return Config.isSmoothBiomes() && !var11.isColorConstant()
         ? getSmoothColorMultiplier(var2, var3, var11, var4.getColorizerBlockPosM())
         : var11.getColor(var2, var3);
   }

   public static BiomeGenBase getColorBiome(IBlockAccess var0, BlockPos var1) {
      BiomeGenBase var5 = var0.getBiomeGenForCoords(var1);
      if (var5 == BiomeGenBase.swampland && !Config.isSwampColors()) {
         var5 = BiomeGenBase.plains;
      }

      return var5;
   }

   private static CustomColormap getBlockColormap(IBlockState var0) {
      if (blockColormaps == null) {
         return null;
      }

      if (!(var0 instanceof BlockStateBase)) {
         return null;
      }

      BlockStateBase var4 = (BlockStateBase)var0;
      int var5 = var4.getBlockId();
      if (var5 >= 0 && var5 < blockColormaps.length) {
         CustomColormap[] var6 = blockColormaps[var5];
         if (var6 == null) {
            return null;
         }

         for (int var7 = 0; var7 < var6.length; var7++) {
            CustomColormap var8 = var6[var7];
            if (var8.matchesBlock(var4)) {
               return var8;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static int getSmoothColorMultiplier(IBlockAccess var0, BlockPos var1, CustomColors.IColorizer var2, BlockPosM var3) {
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      int var10 = var1.getX();
      int var11 = var1.getY();
      int var12 = var1.getZ();
      BlockPosM var13 = var3;

      for (int var14 = var10 - 1; var14 <= var10 + 1; var14++) {
         for (int var15 = var12 - 1; var15 <= var12 + 1; var15++) {
            var13.setXyz(var14, var11, var15);
            int var16 = var2.getColor(var0, var13);
            var7 += var16 >> 16 & 0xFF;
            var8 += var16 >> 8 & 0xFF;
            var9 += var16 & 0xFF;
         }
      }

      int var17 = var7 / 9;
      int var18 = var8 / 9;
      int var19 = var9 / 9;
      return var17 << 16 | var18 << 8 | var19;
   }

   public static int getFluidColor(IBlockAccess var0, IBlockState var1, BlockPos var2, RenderEnv var3) {
      Block var7 = var1.getBlock();
      CustomColors.IColorizer var8 = getBlockColormap(var1);
      if (var8 == null && var7.getMaterial() == Material.water) {
         var8 = COLORIZER_WATER;
      }

      return var8 == null
         ? var7.colorMultiplier(var0, var2)
         : (
            Config.isSmoothBiomes() && !var8.isColorConstant()
               ? getSmoothColorMultiplier(var0, var2, var8, var3.getColorizerBlockPosM())
               : var8.getColor(var0, var2)
         );
   }

   public static void updatePortalFX(EntityFX var0) {
      if (particlePortalColor >= 0) {
         int var4 = particlePortalColor;
         int var5 = var4 >> 16 & 0xFF;
         int var6 = var4 >> 8 & 0xFF;
         int var7 = var4 & 0xFF;
         float var8 = var5 / 255.0F;
         float var9 = var6 / 255.0F;
         float var10 = var7 / 255.0F;
         var0.setRBGColorF(var8, var9, var10);
      }
   }

   public static void updateMyceliumFX(EntityFX var0) {
      if (v != null) {
         int var4 = v.getColorRandom();
         int var5 = var4 >> 16 & 0xFF;
         int var6 = var4 >> 8 & 0xFF;
         int var7 = var4 & 0xFF;
         float var8 = var5 / 255.0F;
         float var9 = var6 / 255.0F;
         float var10 = var7 / 255.0F;
         var0.setRBGColorF(var8, var9, var10);
      }
   }

   private static int getRedstoneColor(IBlockState var0) {
      if (redstoneColors == null) {
         return -1;
      }

      int var3 = getRedstoneLevel(var0, 15);
      return redstoneColors.getColor(var3);
   }

   public static void updateReddustFX(EntityFX var0, IBlockAccess var1, double var2, double var4, double var6) {
      if (redstoneColors != null) {
         IBlockState var10 = var1.getBlockState(new BlockPos(var2, var4, var6));
         int var11 = getRedstoneLevel(var10, 15);
         int var12 = redstoneColors.getColor(var11);
         int var13 = var12 >> 16 & 0xFF;
         int var14 = var12 >> 8 & 0xFF;
         int var15 = var12 & 0xFF;
         float var16 = var13 / 255.0F;
         float var17 = var14 / 255.0F;
         float var18 = var15 / 255.0F;
         var0.setRBGColorF(var16, var17, var18);
      }
   }

   private static int getRedstoneLevel(IBlockState var0, int var1) {
      Block var5 = var0.getBlock();
      if (!(var5 instanceof BlockRedstoneWire)) {
         return var1;
      }

      Comparable var6 = var0.getValue(BlockRedstoneWire.POWER);
      if (!(var6 instanceof Integer)) {
         return var1;
      }

      Integer var7 = (Integer)var6;
      return var7;
   }

   public static float getXpOrbTimer(float var0) {
      if (xpOrbTime <= 0) {
         return var0;
      }

      float var3 = 628.0F / xpOrbTime;
      return var0 * var3;
   }

   public static int getXpOrbColor(float var0) {
      if (xpOrbColors == null) {
         return -1;
      }

      int var3 = (int)Math.round((MathHelper.sin(var0) + 1.0F) * (xpOrbColors.getLength() - 1) / 2.0);
      return xpOrbColors.getColor(var3);
   }

   public static int getDurabilityColor(int var0) {
      if (durabilityColors == null) {
         return -1;
      }

      int var3 = var0 * durabilityColors.getLength() / 255;
      return durabilityColors.getColor(var3);
   }

   public static void updateWaterFX(EntityFX var0, IBlockAccess var1, double var2, double var4, double var6) {
      if (waterColors != null || blockColormaps != null) {
         BlockPos var11 = new BlockPos(var2, var4, var6);
         RenderEnv var12 = RenderEnv.getInstance(var1, BLOCK_STATE_WATER, var11);
         int var13 = getFluidColor(var1, BLOCK_STATE_WATER, var11, var12);
         int var14 = var13 >> 16 & 0xFF;
         int var15 = var13 >> 8 & 0xFF;
         int var16 = var13 & 0xFF;
         float var17 = var14 / 255.0F;
         float var18 = var15 / 255.0F;
         float var19 = var16 / 255.0F;
         if (particleWaterColor >= 0) {
            int var20 = particleWaterColor >> 16 & 0xFF;
            int var21 = particleWaterColor >> 8 & 0xFF;
            int var22 = particleWaterColor & 0xFF;
            var17 *= var20 / 255.0F;
            var18 *= var21 / 255.0F;
            var19 *= var22 / 255.0F;
         }

         var0.setRBGColorF(var17, var18, var19);
      }
   }

   private static int getLilypadColorMultiplier(IBlockAccess var0, BlockPos var1) {
      return lilyPadColor < 0 ? Blocks.waterlily.colorMultiplier(var0, var1) : lilyPadColor;
   }

   private static Vec3 getFogColorNether(Vec3 var0) {
      return fogColorNether == null ? var0 : fogColorNether;
   }

   private static Vec3 getFogColorEnd(Vec3 var0) {
      return fogColorEnd == null ? var0 : fogColorEnd;
   }

   private static Vec3 getSkyColorEnd(Vec3 var0) {
      return skyColorEnd == null ? var0 : skyColorEnd;
   }

   public static Vec3 getSkyColor(Vec3 var0, IBlockAccess var1, double var2, double var4, double var6) {
      if (G == null) {
         return var0;
      }

      int var11 = G.getColorSmooth(var1, var2, var4, var6, 3);
      int var12 = var11 >> 16 & 0xFF;
      int var13 = var11 >> 8 & 0xFF;
      int var14 = var11 & 0xFF;
      float var15 = var12 / 255.0F;
      float var16 = var13 / 255.0F;
      float var17 = var14 / 255.0F;
      float var18 = (float)var0.xCoord / 0.5F;
      float var19 = (float)var0.yCoord / 0.66275F;
      float var20 = (float)var0.zCoord;
      var15 *= var18;
      var16 *= var19;
      var17 *= var20;
      return J.getColor(var15, var16, var17);
   }

   private static Vec3 getFogColor(Vec3 var0, IBlockAccess var1, double var2, double var4, double var6) {
      if (h == null) {
         return var0;
      }

      int var11 = h.getColorSmooth(var1, var2, var4, var6, 3);
      int var12 = var11 >> 16 & 0xFF;
      int var13 = var11 >> 8 & 0xFF;
      int var14 = var11 & 0xFF;
      float var15 = var12 / 255.0F;
      float var16 = var13 / 255.0F;
      float var17 = var14 / 255.0F;
      float var18 = (float)var0.xCoord / 0.753F;
      float var19 = (float)var0.yCoord / 0.8471F;
      float var20 = (float)var0.zCoord;
      var15 *= var18;
      var16 *= var19;
      var17 *= var20;
      return N.getColor(var15, var16, var17);
   }

   public static Vec3 S(IBlockAccess var0, double var1, double var3, double var5) {
      if (L == null) {
         return null;
      }

      int var10 = L.getColorSmooth(var0, var1, var3, var5, 3);
      int var11 = var10 >> 16 & 0xFF;
      int var12 = var10 >> 8 & 0xFF;
      int var13 = var10 & 0xFF;
      float var14 = var11 / 255.0F;
      float var15 = var12 / 255.0F;
      float var16 = var13 / 255.0F;
      return P.getColor(var14, var15, var16);
   }

   private static int getStemColorMultiplier(Block var0, IBlockAccess var1, BlockPos var2, RenderEnv var3) {
      CustomColormap var7 = stemColors;
      if (var0 == Blocks.pumpkin_stem && stemPumpkinColors != null) {
         var7 = stemPumpkinColors;
      }

      if (var0 == Blocks.melon_stem && stemMelonColors != null) {
         var7 = stemMelonColors;
      }

      if (var7 == null) {
         return -1;
      }

      int var8 = var3.getMetadata();
      return var7.getColor(var8);
   }

   public static boolean l(World var0, float var1, int[] var2, boolean var3) {
      if (var0 == null) {
         return false;
      }

      if (F == null) {
         return false;
      }

      int var7 = var0.provider.getDimensionId();
      int var8 = var7 - lightmapMinDimensionId;
      if (var8 >= 0 && var8 < F.length) {
         CustomColormap var9 = F[var8];
         if (var9 == null) {
            return false;
         }

         int var10 = var9.W();
         if (var3 && var10 < 64) {
            return false;
         }

         int var11 = var9.i();
         if (var11 < 16) {
            warn("Invalid lightmap width: " + var11 + " for dimension: " + var7);
            F[var8] = null;
            return false;
         }

         int var12 = 0;
         if (var3) {
            var12 = var11 * 16 * 2;
         }

         float var13 = 1.1666666F * (var0.getSunBrightness(1.0F) - 0.2F);
         if (var0.x() > 0) {
            var13 = 1.0F;
         }

         var13 = Config.limitTo1(var13);
         float var14 = var13 * (var11 - 1);
         float var15 = Config.limitTo1(var1 + 0.5F) * (var11 - 1);
         float var16 = Config.limitTo1(Config.getGameSettings().Zu);
         boolean var17 = var16 > 1.0E-4F;
         float[][] var18 = var9.getColorsRgb();
         g(var18, var14, var12, var11, K);
         g(var18, var15, var12 + 16 * var11, var11, R);
         float[] var19 = new float[3];

         for (int var20 = 0; var20 < 16; var20++) {
            for (int var21 = 0; var21 < 16; var21++) {
               for (int var22 = 0; var22 < 3; var22++) {
                  float var23 = Config.limitTo1(K[var20][var22] + R[var21][var22]);
                  if (var17) {
                     float var24 = 1.0F - var23;
                     var24 = 1.0F - var24 * var24 * var24 * var24;
                     var23 = var16 * var24 + (1.0F - var16) * var23;
                  }

                  var19[var22] = var23;
               }

               int var26 = (int)(var19[0] * 255.0F);
               int var27 = (int)(var19[1] * 255.0F);
               int var29 = (int)(var19[2] * 255.0F);
               var2[var20 * 16 + var21] = 0xFF000000 | var26 << 16 | var27 << 8 | var29;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static void g(float[][] var0, float var1, int var2, int var3, float[][] var4) {
      int var8 = (int)Math.floor(var1);
      int var9 = (int)Math.ceil(var1);
      if (var8 == var9) {
         for (int var10 = 0; var10 < 16; var10++) {
            float[] var11 = var0[var2 + var10 * var3 + var8];
            float[] var12 = var4[var10];

            for (int var13 = 0; var13 < 3; var13++) {
               var12[var13] = var11[var13];
            }
         }
      } else {
         float var17 = 1.0F - (var1 - var8);
         float var18 = 1.0F - (var9 - var1);

         for (int var19 = 0; var19 < 16; var19++) {
            float[] var20 = var0[var2 + var19 * var3 + var8];
            float[] var14 = var0[var2 + var19 * var3 + var9];
            float[] var15 = var4[var19];

            for (int var16 = 0; var16 < 3; var16++) {
               var15[var16] = var20[var16] * var17 + var14[var16] * var18;
            }
         }
      }
   }

   public static Vec3 getWorldFogColor(Vec3 var0, WorldClient var1, Entity var2, float var3) {
      int var7 = var1.provider.getDimensionId();
      switch (var7) {
         case -1:
            var0 = getFogColorNether(var0);
            break;
         case 0:
            Minecraft var8 = Minecraft.getMinecraft();
            var0 = getFogColor(var0, var8.theWorld, var2.posX, var2.posY + 1.0, var2.posZ);
            break;
         case 1:
            var0 = getFogColorEnd(var0);
      }

      return var0;
   }

   public static Vec3 getWorldSkyColor(Vec3 var0, World var1, Entity var2, float var3) {
      int var7 = var1.provider.getDimensionId();
      switch (var7) {
         case 0:
            Minecraft var8 = Minecraft.getMinecraft();
            var0 = getSkyColor(var0, var8.theWorld, var2.posX, var2.posY + 1.0, var2.posZ);
            break;
         case 1:
            var0 = getSkyColorEnd(var0);
      }

      return var0;
   }

   private static int[] readSpawnEggColors(Properties var0, String var1, String var2, String var3) {
      ArrayList var7 = new ArrayList();
      Set var8 = var0.keySet();
      int var9 = 0;

      for (Object var11 : var8) {
         String var12 = var0.getProperty((String)var11);
         if (((String)var11).startsWith(var2)) {
            String var13 = StrUtils.removePrefix((String)var11, var2);
            int var14 = getEntityId(var13);
            int var15 = parseColor(var12);
            if (var14 >= 0 && var15 >= 0) {
               while (var7.size() <= var14) {
                  var7.add(-1);
               }

               var7.set(var14, var15);
               var9++;
            } else {
               warn("Invalid spawn egg color: " + var11 + " = " + var12);
            }
         }
      }

      if (var9 <= 0) {
         return null;
      }

      dbg(var3 + " colors: " + var9);
      int[] var16 = new int[var7.size()];

      for (int var17 = 0; var17 < var16.length; var17++) {
         var16[var17] = (Integer)var7.get(var17);
      }

      return var16;
   }

   private static int getSpawnEggColor(ItemMonsterPlacer var0, ItemStack var1, int var2, int var3) {
      int var7 = var1.getMetadata();
      int[] var8 = var2 == 0 ? spawnEggPrimaryColors : spawnEggSecondaryColors;
      if (var8 == null) {
         return var3;
      } else if (var7 >= 0 && var7 < var8.length) {
         int var9 = var8[var7];
         return var9 < 0 ? var3 : var9;
      } else {
         return var3;
      }
   }

   public static int getColorFromItemStack(ItemStack var0, int var1, int var2) {
      if (var0 == null) {
         return var2;
      }

      Item var6 = var0.getItem();
      return var6 == null ? var2 : (var6 instanceof ItemMonsterPlacer ? getSpawnEggColor((ItemMonsterPlacer)var6, var0, var1, var2) : var2);
   }

   private static float[][] readDyeColors(Properties var0, String var1, String var2, String var3) {
      EnumDyeColor[] var7 = EnumDyeColor.values();
      HashMap var8 = new HashMap();

      for (int var9 = 0; var9 < var7.length; var9++) {
         EnumDyeColor var10 = var7[var9];
         var8.put(var10.getName(), var10);
      }

      float[][] var18 = new float[var7.length][];
      int var19 = 0;

      for (Object var12 : var0.keySet()) {
         String var13 = var0.getProperty((String)var12);
         if (((String)var12).startsWith(var2)) {
            String var14 = StrUtils.removePrefix((String)var12, var2);
            if (var14.equals("lightBlue")) {
               var14 = "light_blue";
            }

            EnumDyeColor var15 = (EnumDyeColor)var8.get(var14);
            int var16 = parseColor(var13);
            if (var15 != null && var16 >= 0) {
               float[] var17 = new float[]{(var16 >> 16 & 0xFF) / 255.0F, (var16 >> 8 & 0xFF) / 255.0F, (var16 & 0xFF) / 255.0F};
               var18[var15.ordinal()] = var17;
               var19++;
            } else {
               warn("Invalid color: " + var12 + " = " + var13);
            }
         }
      }

      if (var19 <= 0) {
         return (float[][])null;
      }

      dbg(var3 + " colors: " + var19);
      return var18;
   }

   private static float[] getDyeColors(EnumDyeColor var0, float[][] var1, float[] var2) {
      if (var1 == null) {
         return var2;
      }

      if (var0 == null) {
         return var2;
      }

      float[] var6 = var1[var0.ordinal()];
      return var6 == null ? var2 : var6;
   }

   public static float[] getWolfCollarColors(EnumDyeColor var0, float[] var1) {
      return getDyeColors(var0, wolfCollarColors, var1);
   }

   public static float[] getSheepColors(EnumDyeColor var0, float[] var1) {
      return getDyeColors(var0, sheepColors, var1);
   }

   private static int[] readTextColors(Properties var0, String var1, String var2, String var3) {
      int[] var7 = new int[32];
      Arrays.fill(var7, -1);
      int var8 = 0;

      for (Object var10 : var0.keySet()) {
         String var11 = var0.getProperty((String)var10);
         if (((String)var10).startsWith(var2)) {
            String var12 = StrUtils.removePrefix((String)var10, var2);
            int var13 = Config.parseInt(var12, -1);
            int var14 = parseColor(var11);
            if (var13 >= 0 && var13 < var7.length && var14 >= 0) {
               var7[var13] = var14;
               var8++;
            } else {
               warn("Invalid color: " + var10 + " = " + var11);
            }
         }
      }

      if (var8 <= 0) {
         return null;
      }

      dbg(var3 + " colors: " + var8);
      return var7;
   }

   public static int getTextColor(int var0, int var1) {
      if (textColors == null) {
         return var1;
      } else if (var0 >= 0 && var0 < textColors.length) {
         int var5 = textColors[var0];
         return var5 < 0 ? var1 : var5;
      } else {
         return var1;
      }
   }

   private static int[] readMapColors(Properties var0, String var1, String var2, String var3) {
      int[] var7 = new int[MapColor.mapColorArray.length];
      Arrays.fill(var7, -1);
      int var8 = 0;

      for (Object var10 : var0.keySet()) {
         String var11 = var0.getProperty((String)var10);
         if (((String)var10).startsWith(var2)) {
            String var12 = StrUtils.removePrefix((String)var10, var2);
            int var13 = getMapColorIndex(var12);
            int var14 = parseColor(var11);
            if (var13 >= 0 && var13 < var7.length && var14 >= 0) {
               var7[var13] = var14;
               var8++;
            } else {
               warn("Invalid color: " + var10 + " = " + var11);
            }
         }
      }

      if (var8 <= 0) {
         return null;
      }

      dbg(var3 + " colors: " + var8);
      return var7;
   }

   private static int[] readPotionColors(Properties var0, String var1, String var2, String var3) {
      int[] var7 = new int[Potion.potionTypes.length];
      Arrays.fill(var7, -1);
      int var8 = 0;

      for (Object var10 : var0.keySet()) {
         String var11 = var0.getProperty((String)var10);
         if (((String)var10).startsWith(var2)) {
            int var12 = getPotionId((String)var10);
            int var13 = parseColor(var11);
            if (var12 >= 0 && var12 < var7.length && var13 >= 0) {
               var7[var12] = var13;
               var8++;
            } else {
               warn("Invalid color: " + var10 + " = " + var11);
            }
         }
      }

      if (var8 <= 0) {
         return null;
      }

      dbg(var3 + " colors: " + var8);
      return var7;
   }

   private static int getPotionId(String var0) {
      if (var0.equals("potion.water")) {
         return 0;
      }

      Potion[] var4 = Potion.potionTypes;

      for (int var5 = 0; var5 < var4.length; var5++) {
         Potion var6 = var4[var5];
         if (var6 != null && var6.getName().equals(var0)) {
            return var6.getId();
         }
      }

      return -1;
   }

   public static int getPotionColor(int var0, int var1) {
      if (potionColors == null) {
         return var1;
      } else if (var0 >= 0 && var0 < potionColors.length) {
         int var5 = potionColors[var0];
         return var5 < 0 ? var1 : var5;
      } else {
         return var1;
      }
   }

   private static int getMapColorIndex(String var0) {
      return var0 == null
         ? -1
         : (
            var0.equals("air")
               ? MapColor.airColor.colorIndex
               : (
                  var0.equals("grass")
                     ? MapColor.grassColor.colorIndex
                     : (
                        var0.equals("sand")
                           ? MapColor.sandColor.colorIndex
                           : (
                              var0.equals("cloth")
                                 ? MapColor.clothColor.colorIndex
                                 : (
                                    var0.equals("tnt")
                                       ? MapColor.tntColor.colorIndex
                                       : (
                                          var0.equals("ice")
                                             ? MapColor.iceColor.colorIndex
                                             : (
                                                var0.equals("iron")
                                                   ? MapColor.ironColor.colorIndex
                                                   : (
                                                      var0.equals("foliage")
                                                         ? MapColor.foliageColor.colorIndex
                                                         : (
                                                            var0.equals("clay")
                                                               ? MapColor.clayColor.colorIndex
                                                               : (
                                                                  var0.equals("dirt")
                                                                     ? MapColor.dirtColor.colorIndex
                                                                     : (
                                                                        var0.equals("stone")
                                                                           ? MapColor.stoneColor.colorIndex
                                                                           : (
                                                                              var0.equals("water")
                                                                                 ? MapColor.waterColor.colorIndex
                                                                                 : (
                                                                                    var0.equals("wood")
                                                                                       ? MapColor.woodColor.colorIndex
                                                                                       : (
                                                                                          var0.equals("quartz")
                                                                                             ? MapColor.quartzColor.colorIndex
                                                                                             : (
                                                                                                var0.equals("gold")
                                                                                                   ? MapColor.goldColor.colorIndex
                                                                                                   : (
                                                                                                      var0.equals("diamond")
                                                                                                         ? MapColor.diamondColor.colorIndex
                                                                                                         : (
                                                                                                            var0.equals("lapis")
                                                                                                               ? MapColor.lapisColor.colorIndex
                                                                                                               : (
                                                                                                                  var0.equals("emerald")
                                                                                                                     ? MapColor.emeraldColor.colorIndex
                                                                                                                     : (
                                                                                                                        var0.equals("podzol")
                                                                                                                           ? MapColor.obsidianColor.colorIndex
                                                                                                                           : (
                                                                                                                              var0.equals("netherrack")
                                                                                                                                 ? MapColor.netherrackColor.colorIndex
                                                                                                                                 : (
                                                                                                                                    var0.equals("snow")
                                                                                                                                          || var0.equals(
                                                                                                                                             "white"
                                                                                                                                          )
                                                                                                                                       ? MapColor.snowColor.colorIndex
                                                                                                                                       : (
                                                                                                                                          var0.equals("adobe")
                                                                                                                                                || var0.equals(
                                                                                                                                                   "orange"
                                                                                                                                                )
                                                                                                                                             ? MapColor.adobeColor
                                                                                                                                                .colorIndex
                                                                                                                                             : (
                                                                                                                                                var0.equals(
                                                                                                                                                      "magenta"
                                                                                                                                                   )
                                                                                                                                                   ? MapColor.magentaColor
                                                                                                                                                      .colorIndex
                                                                                                                                                   : (
                                                                                                                                                      !var0.equals(
                                                                                                                                                               "light_blue"
                                                                                                                                                            )
                                                                                                                                                            && !var0.equals(
                                                                                                                                                               "lightBlue"
                                                                                                                                                            )
                                                                                                                                                         ? (
                                                                                                                                                            var0.equals(
                                                                                                                                                                  "yellow"
                                                                                                                                                               )
                                                                                                                                                               ? MapColor.yellowColor
                                                                                                                                                                  .colorIndex
                                                                                                                                                               : (
                                                                                                                                                                  var0.equals(
                                                                                                                                                                        "lime"
                                                                                                                                                                     )
                                                                                                                                                                     ? MapColor.limeColor
                                                                                                                                                                        .colorIndex
                                                                                                                                                                     : (
                                                                                                                                                                        var0.equals(
                                                                                                                                                                              "pink"
                                                                                                                                                                           )
                                                                                                                                                                           ? MapColor.pinkColor
                                                                                                                                                                              .colorIndex
                                                                                                                                                                           : (
                                                                                                                                                                              var0.equals(
                                                                                                                                                                                    "gray"
                                                                                                                                                                                 )
                                                                                                                                                                                 ? MapColor.grayColor
                                                                                                                                                                                    .colorIndex
                                                                                                                                                                                 : (
                                                                                                                                                                                    var0.equals(
                                                                                                                                                                                          "silver"
                                                                                                                                                                                       )
                                                                                                                                                                                       ? MapColor.silverColor
                                                                                                                                                                                          .colorIndex
                                                                                                                                                                                       : (
                                                                                                                                                                                          var0.equals(
                                                                                                                                                                                                "cyan"
                                                                                                                                                                                             )
                                                                                                                                                                                             ? MapColor.cyanColor
                                                                                                                                                                                                .colorIndex
                                                                                                                                                                                             : (
                                                                                                                                                                                                var0.equals(
                                                                                                                                                                                                      "purple"
                                                                                                                                                                                                   )
                                                                                                                                                                                                   ? MapColor.purpleColor
                                                                                                                                                                                                      .colorIndex
                                                                                                                                                                                                   : (
                                                                                                                                                                                                      var0.equals(
                                                                                                                                                                                                            "blue"
                                                                                                                                                                                                         )
                                                                                                                                                                                                         ? MapColor.blueColor
                                                                                                                                                                                                            .colorIndex
                                                                                                                                                                                                         : (
                                                                                                                                                                                                            var0.equals(
                                                                                                                                                                                                                  "brown"
                                                                                                                                                                                                               )
                                                                                                                                                                                                               ? MapColor.brownColor
                                                                                                                                                                                                                  .colorIndex
                                                                                                                                                                                                               : (
                                                                                                                                                                                                                  var0.equals(
                                                                                                                                                                                                                        "green"
                                                                                                                                                                                                                     )
                                                                                                                                                                                                                     ? MapColor.greenColor
                                                                                                                                                                                                                        .colorIndex
                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                        var0.equals(
                                                                                                                                                                                                                              "red"
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                           ? MapColor.redColor
                                                                                                                                                                                                                              .colorIndex
                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                              var0.equals(
                                                                                                                                                                                                                                    "black"
                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                 ? MapColor.blackColor
                                                                                                                                                                                                                                    .colorIndex
                                                                                                                                                                                                                                 : -1
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                     )
                                                                                                                                                                                                               )
                                                                                                                                                                                                         )
                                                                                                                                                                                                   )
                                                                                                                                                                                             )
                                                                                                                                                                                       )
                                                                                                                                                                                 )
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                               )
                                                                                                                                                         )
                                                                                                                                                         : MapColor.lightBlueColor
                                                                                                                                                            .colorIndex
                                                                                                                                                   )
                                                                                                                                             )
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                           )
                                                                                                                     )
                                                                                                               )
                                                                                                         )
                                                                                                   )
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   private static int[] getMapColors() {
      MapColor[] var3 = MapColor.mapColorArray;
      int[] var4 = new int[var3.length];
      Arrays.fill(var4, -1);

      for (int var5 = 0; var5 < var3.length && var5 < var4.length; var5++) {
         MapColor var6 = var3[var5];
         if (var6 != null) {
            var4[var5] = var6.colorValue;
         }
      }

      return var4;
   }

   private static void setMapColors(int[] var0) {
      if (var0 != null) {
         MapColor[] var4 = MapColor.mapColorArray;
         boolean var5 = false;

         for (int var6 = 0; var6 < var4.length && var6 < var0.length; var6++) {
            MapColor var7 = var4[var6];
            if (var7 != null) {
               int var8 = var0[var6];
               if (var8 >= 0 && var7.colorValue != var8) {
                  var7.colorValue = var8;
                  var5 = true;
               }
            }
         }

         if (var5) {
            Minecraft.getMinecraft().getTextureManager().reloadBannerTextures();
         }
      }
   }

   private static int getEntityId(String var0) {
      if (var0 == null) {
         return -1;
      }

      int var4 = EntityList.getIDFromString(var0);
      if (var4 < 0) {
         return -1;
      }

      String var5 = EntityList.getStringFromID(var4);
      return !Config.equals(var0, var5) ? -1 : var4;
   }

   private static void dbg(String var0) {
      Config.dbg("CustomColors: " + var0);
   }

   private static void warn(String var0) {
      Config.warn("CustomColors: " + var0);
   }

   public static int k(int var0) {
      return expBarTextColor < 0 ? var0 : expBarTextColor;
   }

   public static int P(int var0) {
      return bossTextColor < 0 ? var0 : bossTextColor;
   }

   public static int E(int var0) {
      return signTextColor < 0 ? var0 : signTextColor;
   }

   public static CustomColormap U() {
      return swampGrassColors;
   }

   public static CustomColormap y() {
      return swampFoliageColors;
   }

   public static CustomColormap I() {
      return foliagePineColors;
   }

   public static CustomColormap s() {
      return foliageBirchColors;
   }

   public static CustomColormap k() {
      return waterColors;
   }

   static {
      long var20 = O ^ 2283370206762L;
      long var10000 = 6319544610923073784L ^ var20;
   }

   public interface IColorizer {
      int getColor(IBlockAccess var1, BlockPos var2);

      boolean isColorConstant();
   }
}
