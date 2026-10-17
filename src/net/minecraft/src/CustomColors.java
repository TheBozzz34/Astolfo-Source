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
   private static CustomColormap skyColors = null;
   private static CustomColorFader skyColorFader = new CustomColorFader();
   private static CustomColormap fogColors = null;
   private static CustomColorFader fogColorFader = new CustomColorFader();
   private static CustomColormap underwaterColors = null;
   private static CustomColorFader underwaterColorFader = new CustomColorFader();
   private static CustomColormap[] lightMapsColorsRgb = null;
   private static int lightmapMinDimensionId = 0;
   private static float[][] sunRgbs = new float[16][3];
   private static float[][] torchRgbs = new float[16][3];
   private static CustomColormap redstoneColors = null;
   private static CustomColormap xpOrbColors = null;
   private static int xpOrbTime = -1;
   private static CustomColormap durabilityColors = null;
   private static CustomColormap stemColors = null;
   private static CustomColormap stemMelonColors = null;
   private static CustomColormap stemPumpkinColors = null;
   private static CustomColormap myceliumParticleColors = null;
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
      @Override
      public int getColor(IBlockAccess blockAccess, BlockPos blockPos) {
         BiomeGenBase biomeGenBase = CustomColors.getColorBiome(blockAccess, blockPos);
         return CustomColors.getSwampGrassColors() != null && biomeGenBase == BiomeGenBase.swampland
            ? CustomColors.getSwampGrassColors().getColor(biomeGenBase, blockPos)
            : biomeGenBase.getGrassColorAtPos(blockPos);
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };
   private static final CustomColors.IColorizer COLORIZER_FOLIAGE = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockAccess blockAccess, BlockPos blockPos) {
         BiomeGenBase biomeGenBase = CustomColors.getColorBiome(blockAccess, blockPos);
         return CustomColors.y() != null && biomeGenBase == BiomeGenBase.swampland ? CustomColors.y().getColor(biomeGenBase, blockPos) : biomeGenBase.getFoliageColorAtPos(blockPos);
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };
   private static final CustomColors.IColorizer COLORIZER_FOLIAGE_PINE = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockAccess blockAccess, BlockPos blockPos) {
         return CustomColors.getFoliagePineColors() != null
            ? CustomColors.getFoliagePineColors().getColor(blockAccess, blockPos)
            : ColorizerFoliage.getFoliageColorPine();
      }

      @Override
      public boolean isColorConstant() {
         return CustomColors.getFoliagePineColors() == null;
      }
   };
   private static final CustomColors.IColorizer COLORIZER_FOLIAGE_BIRCH = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockAccess blockAccess, BlockPos blockPos) {
         return CustomColors.getFoliageBirchColors() != null
            ? CustomColors.getFoliageBirchColors().getColor(blockAccess, blockPos)
            : ColorizerFoliage.getFoliageColorBirch();
      }

      @Override
      public boolean isColorConstant() {
         return CustomColors.getFoliageBirchColors() == null;
      }
   };
   private static final CustomColors.IColorizer COLORIZER_WATER = new CustomColors.IColorizer() {
      @Override
      public int getColor(IBlockAccess blockAccess, BlockPos blockPos) {
         BiomeGenBase biomeGenBase = CustomColors.getColorBiome(blockAccess, blockPos);
         return CustomColors.getWaterColors() != null
            ? CustomColors.getWaterColors().getColor(biomeGenBase, blockPos)
            : (
               Reflector.ForgeBiome_getWaterColorMultiplier.exists()
                  ? Reflector.callInt(biomeGenBase, Reflector.ForgeBiome_getWaterColorMultiplier)
                  : biomeGenBase.waterColorMultiplier
            );
      }

      @Override
      public boolean isColorConstant() {
         return false;
      }
   };

   public static void update() {
      paletteFormatDefault = "vanilla";
      waterColors = null;
      foliageBirchColors = null;
      foliagePineColors = null;
      swampGrassColors = null;
      swampFoliageColors = null;
      skyColors = null;
      fogColors = null;
      underwaterColors = null;
      redstoneColors = null;
      xpOrbColors = null;
      xpOrbTime = -1;
      durabilityColors = null;
      stemColors = null;
      myceliumParticleColors = null;
      lightMapsColorsRgb = null;
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
      String string = "mcpatcher/colormap/";
      String[] strings = new String[]{"water.png", "watercolorX.png"};
      waterColors = getCustomColors(string, strings, 256, 256);
      updateUseDefaultGrassFoliageColors();
      if (Config.isCustomColors()) {
         String[] strings2 = new String[]{"pine.png", "pinecolor.png"};
         foliagePineColors = getCustomColors(string, strings2, 256, 256);
         String[] strings3 = new String[]{"birch.png", "birchcolor.png"};
         foliageBirchColors = getCustomColors(string, strings3, 256, 256);
         String[] strings4 = new String[]{"swampgrass.png", "swampgrasscolor.png"};
         swampGrassColors = getCustomColors(string, strings4, 256, 256);
         String[] strings5 = new String[]{"swampfoliage.png", "swampfoliagecolor.png"};
         swampFoliageColors = getCustomColors(string, strings5, 256, 256);
         String[] strings6 = new String[]{"sky0.png", "skycolor0.png"};
         skyColors = getCustomColors(string, strings6, 256, 256);
         String[] strings7 = new String[]{"fog0.png", "fogcolor0.png"};
         fogColors = getCustomColors(string, strings7, 256, 256);
         String[] strings8 = new String[]{"underwater.png", "underwatercolor.png"};
         underwaterColors = getCustomColors(string, strings8, 256, 256);
         String[] strings9 = new String[]{"redstone.png", "redstonecolor.png"};
         redstoneColors = getCustomColors(string, strings9, 16, 1);
         xpOrbColors = getCustomColors(string + "xporb.png", -1, -1);
         durabilityColors = getCustomColors(string + "durability.png", -1, -1);
         String[] strings10 = new String[]{"stem.png", "stemcolor.png"};
         stemColors = getCustomColors(string, strings10, 8, 1);
         stemPumpkinColors = getCustomColors(string + "pumpkinstem.png", 8, 1);
         stemMelonColors = getCustomColors(string + "melonstem.png", 8, 1);
         String[] strings11 = new String[]{"myceliumparticle.png", "myceliumparticlecolor.png"};
         myceliumParticleColors = getCustomColors(string, strings11, -1, -1);
         Pair pair = parseLightMapPacks();
         lightMapsColorsRgb = (CustomColormap[])pair.getLeft();
         lightmapMinDimensionId = (Integer)pair.getRight();
         readColorProperties("mcpatcher/color.properties");
         blockColormaps = readBlockColormaps(new String[]{string + "custom/", string + "blocks/"}, colorsBlockColormaps, 256, 256);
         updateUseDefaultGrassFoliageColors();
      }
   }

   private static String getValidProperty(String fileName, String key, String[] validValues, String valDef) {
      try {
         ResourceLocation resourceLocation = new ResourceLocation(fileName);
         InputStream inputStream = Config.getResourceStream(resourceLocation);
         if (inputStream == null) {
            return valDef;
         } else {
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            String string = properties.getProperty(key);
            if (string == null) {
               return valDef;
            } else {
               List list = Arrays.asList(validValues);
               if (!list.contains(string)) {
                  warn("Invalid value: " + key + "=" + string);
                  warn("Expected values: " + Config.arrayToString(validValues));
                  return valDef;
               } else {
                  dbg("" + key + "=" + string);
                  return string;
               }
            }
         }
      } catch (FileNotFoundException fileNotFoundException) {
         return valDef;
      } catch (IOException ioException) {
         ioException.printStackTrace();
         return valDef;
      }
   }

   private static Pair<CustomColormap[], Integer> parseLightMapPacks() {
      String string = "mcpatcher/lightmap/world";
      String string2 = ".png";
      String[] strings = ResUtils.collectFiles(string, string2);
      HashMap hashMap = new HashMap();

      for (int i = 0; i < strings.length; i++) {
         String string3 = strings[i];
         String string4 = StrUtils.removePrefixSuffix(string3, string, string2);
         int j = Config.parseInt(string4, Integer.MIN_VALUE);
         if (j == Integer.MIN_VALUE) {
            warn("Invalid dimension ID: " + string4 + ", path: " + string3);
         } else {
            hashMap.put(j, string3);
         }
      }

      Set set = hashMap.keySet();
      Integer[] integers = (java.lang.Integer[])(set.toArray(new Integer[set.size()]));
      Arrays.sort(integers);
      if (integers.length <= 0) {
         return new ImmutablePair<>(null, 0);
      }

      int k = integers[0];
      int l = integers[integers.length - 1];
      int m = l - k + 1;
      CustomColormap[] customColormaps = new CustomColormap[m];

      for (int n = 0; n < integers.length; n++) {
         Integer integer = integers[n];
         String string5 = (String)hashMap.get(integer);
         CustomColormap customColormap = getCustomColors(string5, -1, -1);
         if (customColormap != null) {
            if (customColormap.getWidth() < 16) {
               warn("Invalid lightmap width: " + customColormap.getWidth() + ", path: " + string5);
            } else {
               int i2 = integer - k;
               customColormaps[i2] = customColormap;
            }
         }
      }

      return new ImmutablePair<>(customColormaps, k);
   }

   private static void readColorProperties(String fileName) {
      try {
         ResourceLocation resourceLocation = new ResourceLocation(fileName);
         InputStream inputStream = Config.getResourceStream(resourceLocation);
         if (inputStream == null) {
            return;
         }

         dbg("Loading " + fileName);
         Properties properties = new Properties();
         properties.load(inputStream);
         inputStream.close();
         particleWaterColor = readColor(properties, new String[]{"particle.water", "drop.water"});
         particlePortalColor = readColor(properties, "particle.portal");
         lilyPadColor = readColor(properties, "lilypad");
         expBarTextColor = readColor(properties, "text.xpbar");
         bossTextColor = readColor(properties, "text.boss");
         signTextColor = readColor(properties, "text.sign");
         fogColorNether = readColorVec3(properties, "fog.nether");
         fogColorEnd = readColorVec3(properties, "fog.end");
         skyColorEnd = readColorVec3(properties, "sky.end");
         colorsBlockColormaps = readCustomColormaps(properties, fileName);
         spawnEggPrimaryColors = readSpawnEggColors(properties, fileName, "egg.shell.", "Spawn egg shell");
         spawnEggSecondaryColors = readSpawnEggColors(properties, fileName, "egg.spots.", "Spawn egg spot");
         wolfCollarColors = readDyeColors(properties, fileName, "collar.", "Wolf collar");
         sheepColors = readDyeColors(properties, fileName, "sheep.", "Sheep");
         textColors = readTextColors(properties, fileName, "text.code.", "Text");
         int[] ints = readMapColors(properties, fileName, "map.", "Map");
         if (ints != null) {
            if (mapColorsOriginal == null) {
               mapColorsOriginal = getMapColors();
            }

            setMapColors(ints);
         }

         potionColors = readPotionColors(properties, fileName, "potion.", "Potion");
         xpOrbTime = Config.parseInt(properties.getProperty("xporb.time"), -1);
      } catch (FileNotFoundException fileNotFoundException) {
         return;
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   private static CustomColormap[] readCustomColormaps(Properties props, String fileName) {
      ArrayList arrayList = new ArrayList();
      String string = "palette.block.";
      HashMap hashMap = new HashMap();

      for (Object object : props.keySet()) {
         String string2 = props.getProperty((String)object);
         if (((String)object).startsWith(string)) {
            hashMap.put(object, string2);
         }
      }

      String[] strings = (java.lang.String[])(hashMap.keySet().toArray(new String[hashMap.size()]));

      for (int i = 0; i < strings.length; i++) {
         String string3 = strings[i];
         String string4 = props.getProperty(string3);
         dbg("Block palette: " + string3 + " = " + string4);
         String string5 = string3.substring(string.length());
         String string6 = TextureUtils.getBasePath(fileName);
         string5 = TextureUtils.fixResourcePath(string5, string6);
         CustomColormap customColormap = getCustomColors(string5, 256, 256);
         if (customColormap == null) {
            warn("Colormap not found: " + string5);
         } else {
            ConnectedParser connectedParser = new ConnectedParser("CustomColors");
            MatchBlock[] matchBlocks = connectedParser.parseMatchBlocks(string4);
            if (matchBlocks != null && matchBlocks.length > 0) {
               for (int j = 0; j < matchBlocks.length; j++) {
                  MatchBlock matchBlock = matchBlocks[j];
                  customColormap.addMatchBlock(matchBlock);
               }

               arrayList.add(customColormap);
            } else {
               warn("Invalid match blocks: " + string4);
            }
         }
      }

      return arrayList.size() <= 0 ? null : (net.minecraft.src.CustomColormap[])(arrayList.toArray(new CustomColormap[arrayList.size()]));
   }

   private static CustomColormap[][] readBlockColormaps(String[] basePaths, CustomColormap[] basePalettes, int width, int height) {
      String[] strings = ResUtils.collectFiles(basePaths, new String[]{".properties"});
      Arrays.sort(strings);
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         dbg("Block colormap: " + string);

         try {
            ResourceLocation resourceLocation = new ResourceLocation("minecraft", string);
            InputStream inputStream = Config.getResourceStream(resourceLocation);
            if (inputStream == null) {
               warn("File not found: " + string);
            } else {
               Properties properties2 = new Properties();
               properties2.load(inputStream);
               CustomColormap customColormap = new CustomColormap(properties2, string, width, height, paletteFormatDefault);
               if (customColormap.isValid(string) && customColormap.isValidMatchBlocks(string)) {
                  addToBlockList(customColormap, arrayList);
               }
            }
         } catch (FileNotFoundException fileNotFoundException) {
            warn("File not found: " + string);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

      if (basePalettes != null) {
         for (int j = 0; j < basePalettes.length; j++) {
            CustomColormap customColormap2 = basePalettes[j];
            addToBlockList(customColormap2, arrayList);
         }
      }

      return arrayList.size() <= 0 ? (CustomColormap[][])null : blockListToArray(arrayList);
   }

   private static void addToBlockList(CustomColormap cm, List blockList) {
      int[] ints = cm.getMatchBlockIds();
      if (ints != null && ints.length > 0) {
         for (int i = 0; i < ints.length; i++) {
            int j = ints[i];
            if (j < 0) {
               warn("Invalid block ID: " + j);
            } else {
               addToList(cm, blockList, j);
            }
         }
      } else {
         warn("No match blocks: " + Config.arrayToString(ints));
      }
   }

   private static void addToList(CustomColormap cm, List list, int id) {
      while (id >= list.size()) {
         list.add(null);
      }

      List list2 = (List)list.get(id);
      if (list2 == null) {
         list2 = new ArrayList();
         list.set(id, list2);
      }

      list2.add(cm);
   }

   private static CustomColormap[][] blockListToArray(List list) {
      CustomColormap[][] customColormapss = new CustomColormap[list.size()][];

      for (int i = 0; i < list.size(); i++) {
         List list2 = (List)list.get(i);
         if (list2 != null) {
            CustomColormap[] customColormaps = (net.minecraft.src.CustomColormap[])(list2.toArray(new CustomColormap[list2.size()]));
            customColormapss[i] = customColormaps;
         }
      }

      return customColormapss;
   }

   private static int readColor(Properties props, String[] names) {
      for (int i = 0; i < names.length; i++) {
         String string = names[i];
         int j = readColor(props, string);
         if (j >= 0) {
            return j;
         }
      }

      return -1;
   }

   private static int readColor(Properties props, String name) {
      String string = props.getProperty(name);
      if (string == null) {
         return -1;
      } else {
         string = string.trim();
         int i = parseColor(string);
         if (i < 0) {
            warn("Invalid color: " + name + " = " + string);
            return i;
         } else {
            dbg(name + " = " + string);
            return i;
         }
      }
   }

   private static int parseColor(String str) {
      if (str == null) {
         return -1;
      }

      str = str.trim();

      try {
         return Integer.parseInt(str, 16) & 16777215;
      } catch (NumberFormatException numberFormatException) {
         return -1;
      }
   }

   private static Vec3 readColorVec3(Properties props, String name) {
      int i = readColor(props, name);
      if (i < 0) {
         return null;
      }

      int j = i >> 16 & 0xFF;
      int k = i >> 8 & 0xFF;
      int l = i & 0xFF;
      float f = j / 255.0F;
      float g = k / 255.0F;
      float h = l / 255.0F;
      return new Vec3(f, g, h);
   }

   private static CustomColormap getCustomColors(String basePath, String[] paths, int width, int height) {
      for (int i = 0; i < paths.length; i++) {
         String string = paths[i];
         string = basePath + string;
         CustomColormap customColormap = getCustomColors(string, width, height);
         if (customColormap != null) {
            return customColormap;
         }
      }

      return null;
   }

   public static CustomColormap getCustomColors(String pathImage, int width, int height) {
      try {
         ResourceLocation resourceLocation = new ResourceLocation(pathImage);
         if (!Config.hasResource(resourceLocation)) {
            return null;
         }

         dbg("Colormap " + pathImage);
         Properties properties2 = new Properties();
         String string = StrUtils.replaceSuffix(pathImage, ".png", ".properties");
         ResourceLocation resourceLocation2 = new ResourceLocation(string);
         if (Config.hasResource(resourceLocation2)) {
            InputStream inputStream = Config.getResourceStream(resourceLocation2);
            properties2.load(inputStream);
            inputStream.close();
            dbg("Colormap properties: " + string);
         } else {
            properties2.put("format", paletteFormatDefault);
            properties2.put("source", pathImage);
            string = pathImage;
         }

         CustomColormap customColormap = new CustomColormap(properties2, string, width, height, paletteFormatDefault);
         return !customColormap.isValid(string) ? null : customColormap;
      } catch (Exception exception) {
         exception.printStackTrace();
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

   public static int getColorMultiplier(BakedQuad quad, Block block, IBlockAccess blockAccess, BlockPos blockPos, RenderEnv renderEnv) {
      if (blockColormaps != null) {
         IBlockState iBlockState = renderEnv.getBlockState();
         if (!quad.hasTintIndex()) {
            if (block == Blocks.grass) {
               iBlockState = BLOCK_STATE_DIRT;
            }

            if (block == Blocks.redstone_wire) {
               return -1;
            }
         }

         if (block == Blocks.double_plant && renderEnv.getMetadata() >= 8) {
            blockPos = blockPos.down();
            iBlockState = blockAccess.getBlockState(blockPos);
         }

         CustomColormap customColormap = getBlockColormap(iBlockState);
         if (customColormap != null) {
            if (Config.isSmoothBiomes() && !customColormap.isColorConstant()) {
               return getSmoothColorMultiplier(blockAccess, blockPos, customColormap, renderEnv.getColorizerBlockPosM());
            }

            return customColormap.getColor(blockAccess, blockPos);
         }
      }

      if (!quad.hasTintIndex()) {
         return -1;
      }

      if (block == Blocks.waterlily) {
         return getLilypadColorMultiplier(blockAccess, blockPos);
      }

      if (block == Blocks.redstone_wire) {
         return getRedstoneColor(renderEnv.getBlockState());
      }

      if (block instanceof BlockStem) {
         return getStemColorMultiplier(block, blockAccess, blockPos, renderEnv);
      }

      if (useDefaultGrassFoliageColors) {
         return -1;
      }

      int i = renderEnv.getMetadata();
      CustomColors.IColorizer iColorizer;
      if (block == Blocks.grass || block == Blocks.tallgrass || block == Blocks.double_plant) {
         iColorizer = COLORIZER_GRASS;
      } else if (block == Blocks.double_plant) {
         iColorizer = COLORIZER_GRASS;
         if (i >= 8) {
            blockPos = blockPos.down();
         }
      } else if (block == Blocks.leaves) {
         switch (i & 3) {
            case 0:
               iColorizer = COLORIZER_FOLIAGE;
               break;
            case 1:
               iColorizer = COLORIZER_FOLIAGE_PINE;
               break;
            case 2:
               iColorizer = COLORIZER_FOLIAGE_BIRCH;
               break;
            default:
               iColorizer = COLORIZER_FOLIAGE;
         }
      } else if (block == Blocks.leaves2) {
         iColorizer = COLORIZER_FOLIAGE;
      } else {
         if (block != Blocks.vine) {
            return -1;
         }

         iColorizer = COLORIZER_FOLIAGE;
      }

      return Config.isSmoothBiomes() && !iColorizer.isColorConstant()
         ? getSmoothColorMultiplier(blockAccess, blockPos, iColorizer, renderEnv.getColorizerBlockPosM())
         : iColorizer.getColor(blockAccess, blockPos);
   }

   public static BiomeGenBase getColorBiome(IBlockAccess blockAccess, BlockPos blockPos) {
      BiomeGenBase biomeGenBase = blockAccess.getBiomeGenForCoords(blockPos);
      if (biomeGenBase == BiomeGenBase.swampland && !Config.isSwampColors()) {
         biomeGenBase = BiomeGenBase.plains;
      }

      return biomeGenBase;
   }

   private static CustomColormap getBlockColormap(IBlockState blockState) {
      if (blockColormaps == null) {
         return null;
      }

      if (!(blockState instanceof BlockStateBase)) {
         return null;
      }

      BlockStateBase blockStateBase = (BlockStateBase)blockState;
      int i = blockStateBase.getBlockId();
      if (i >= 0 && i < blockColormaps.length) {
         CustomColormap[] customColormaps = blockColormaps[i];
         if (customColormaps == null) {
            return null;
         }

         for (int j = 0; j < customColormaps.length; j++) {
            CustomColormap customColormap = customColormaps[j];
            if (customColormap.matchesBlock(blockStateBase)) {
               return customColormap;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static int getSmoothColorMultiplier(IBlockAccess blockAccess, BlockPos blockPos, CustomColors.IColorizer colorizer, BlockPosM blockPosM) {
      int i = 0;
      int j = 0;
      int k = 0;
      int l = blockPos.getX();
      int m = blockPos.getY();
      int n = blockPos.getZ();
      BlockPosM blockPosM2 = blockPosM;

      for (int i2 = l - 1; i2 <= l + 1; i2++) {
         for (int i3 = n - 1; i3 <= n + 1; i3++) {
            blockPosM2.setXyz(i2, m, i3);
            int i4 = colorizer.getColor(blockAccess, blockPosM2);
            i += i4 >> 16 & 0xFF;
            j += i4 >> 8 & 0xFF;
            k += i4 & 0xFF;
         }
      }

      int i5 = i / 9;
      int i6 = j / 9;
      int i7 = k / 9;
      return i5 << 16 | i6 << 8 | i7;
   }

   public static int getFluidColor(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, RenderEnv renderEnv) {
      Block block = blockState.getBlock();
      CustomColors.IColorizer iColorizer = getBlockColormap(blockState);
      if (iColorizer == null && block.getMaterial() == Material.water) {
         iColorizer = COLORIZER_WATER;
      }

      return iColorizer == null
         ? block.colorMultiplier(blockAccess, blockPos)
         : (
            Config.isSmoothBiomes() && !iColorizer.isColorConstant()
               ? getSmoothColorMultiplier(blockAccess, blockPos, iColorizer, renderEnv.getColorizerBlockPosM())
               : iColorizer.getColor(blockAccess, blockPos)
         );
   }

   public static void updatePortalFX(EntityFX fx) {
      if (particlePortalColor >= 0) {
         int i = particlePortalColor;
         int j = i >> 16 & 0xFF;
         int k = i >> 8 & 0xFF;
         int l = i & 0xFF;
         float f = j / 255.0F;
         float g = k / 255.0F;
         float h = l / 255.0F;
         fx.setRBGColorF(f, g, h);
      }
   }

   public static void updateMyceliumFX(EntityFX fx) {
      if (myceliumParticleColors != null) {
         int i = myceliumParticleColors.getColorRandom();
         int j = i >> 16 & 0xFF;
         int k = i >> 8 & 0xFF;
         int l = i & 0xFF;
         float f = j / 255.0F;
         float g = k / 255.0F;
         float h = l / 255.0F;
         fx.setRBGColorF(f, g, h);
      }
   }

   private static int getRedstoneColor(IBlockState blockState) {
      if (redstoneColors == null) {
         return -1;
      }

      int i = getRedstoneLevel(blockState, 15);
      return redstoneColors.getColor(i);
   }

   public static void updateReddustFX(EntityFX fx, IBlockAccess blockAccess, double x, double y, double z) {
      if (redstoneColors != null) {
         IBlockState iBlockState = blockAccess.getBlockState(new BlockPos(x, y, z));
         int i = getRedstoneLevel(iBlockState, 15);
         int j = redstoneColors.getColor(i);
         int k = j >> 16 & 0xFF;
         int l = j >> 8 & 0xFF;
         int m = j & 0xFF;
         float f = k / 255.0F;
         float g = l / 255.0F;
         float h = m / 255.0F;
         fx.setRBGColorF(f, g, h);
      }
   }

   private static int getRedstoneLevel(IBlockState state, int def) {
      Block block = state.getBlock();
      if (!(block instanceof BlockRedstoneWire)) {
         return def;
      }

      Comparable comparable = state.getValue(BlockRedstoneWire.POWER);
      if (!(comparable instanceof Integer)) {
         return def;
      }

      Integer integer = (Integer)comparable;
      return integer;
   }

   public static float getXpOrbTimer(float timer) {
      if (xpOrbTime <= 0) {
         return timer;
      }

      float f = 628.0F / xpOrbTime;
      return timer * f;
   }

   public static int getXpOrbColor(float timer) {
      if (xpOrbColors == null) {
         return -1;
      }

      int i = (int)Math.round((MathHelper.sin(timer) + 1.0F) * (xpOrbColors.getLength() - 1) / 2.0);
      return xpOrbColors.getColor(i);
   }

   public static int getDurabilityColor(int dur255) {
      if (durabilityColors == null) {
         return -1;
      }

      int i = dur255 * durabilityColors.getLength() / 255;
      return durabilityColors.getColor(i);
   }

   public static void updateWaterFX(EntityFX fx, IBlockAccess blockAccess, double x, double y, double z) {
      if (waterColors != null || blockColormaps != null) {
         BlockPos blockPos = new BlockPos(x, y, z);
         RenderEnv renderEnv = RenderEnv.getInstance(blockAccess, BLOCK_STATE_WATER, blockPos);
         int i = getFluidColor(blockAccess, BLOCK_STATE_WATER, blockPos, renderEnv);
         int j = i >> 16 & 0xFF;
         int k = i >> 8 & 0xFF;
         int l = i & 0xFF;
         float f = j / 255.0F;
         float g = k / 255.0F;
         float h = l / 255.0F;
         if (particleWaterColor >= 0) {
            int m = particleWaterColor >> 16 & 0xFF;
            int n = particleWaterColor >> 8 & 0xFF;
            int i2 = particleWaterColor & 0xFF;
            f *= m / 255.0F;
            g *= n / 255.0F;
            h *= i2 / 255.0F;
         }

         fx.setRBGColorF(f, g, h);
      }
   }

   private static int getLilypadColorMultiplier(IBlockAccess blockAccess, BlockPos blockPos) {
      return lilyPadColor < 0 ? Blocks.waterlily.colorMultiplier(blockAccess, blockPos) : lilyPadColor;
   }

   private static Vec3 getFogColorNether(Vec3 col) {
      return fogColorNether == null ? col : fogColorNether;
   }

   private static Vec3 getFogColorEnd(Vec3 col) {
      return fogColorEnd == null ? col : fogColorEnd;
   }

   private static Vec3 getSkyColorEnd(Vec3 col) {
      return skyColorEnd == null ? col : skyColorEnd;
   }

   public static Vec3 getSkyColor(Vec3 skyColor3d, IBlockAccess blockAccess, double x, double y, double z) {
      if (skyColors == null) {
         return skyColor3d;
      }

      int i = skyColors.getColorSmooth(blockAccess, x, y, z, 3);
      int j = i >> 16 & 0xFF;
      int k = i >> 8 & 0xFF;
      int l = i & 0xFF;
      float f = j / 255.0F;
      float g = k / 255.0F;
      float h = l / 255.0F;
      float f2 = (float)skyColor3d.xCoord / 0.5F;
      float f3 = (float)skyColor3d.yCoord / 0.66275F;
      float f4 = (float)skyColor3d.zCoord;
      f *= f2;
      g *= f3;
      h *= f4;
      return skyColorFader.getColor(f, g, h);
   }

   private static Vec3 getFogColor(Vec3 fogColor3d, IBlockAccess blockAccess, double x, double y, double z) {
      if (fogColors == null) {
         return fogColor3d;
      }

      int i = fogColors.getColorSmooth(blockAccess, x, y, z, 3);
      int j = i >> 16 & 0xFF;
      int k = i >> 8 & 0xFF;
      int l = i & 0xFF;
      float f = j / 255.0F;
      float g = k / 255.0F;
      float h = l / 255.0F;
      float f2 = (float)fogColor3d.xCoord / 0.753F;
      float f3 = (float)fogColor3d.yCoord / 0.8471F;
      float f4 = (float)fogColor3d.zCoord;
      f *= f2;
      g *= f3;
      h *= f4;
      return fogColorFader.getColor(f, g, h);
   }

   public static Vec3 getUnderwaterColor(IBlockAccess blockAccess, double x, double y, double z) {
      if (underwaterColors == null) {
         return null;
      }

      int i = underwaterColors.getColorSmooth(blockAccess, x, y, z, 3);
      int j = i >> 16 & 0xFF;
      int k = i >> 8 & 0xFF;
      int l = i & 0xFF;
      float f = j / 255.0F;
      float g = k / 255.0F;
      float h = l / 255.0F;
      return underwaterColorFader.getColor(f, g, h);
   }

   private static int getStemColorMultiplier(Block blockStem, IBlockAccess blockAccess, BlockPos blockPos, RenderEnv renderEnv) {
      CustomColormap customColormap = stemColors;
      if (blockStem == Blocks.pumpkin_stem && stemPumpkinColors != null) {
         customColormap = stemPumpkinColors;
      }

      if (blockStem == Blocks.melon_stem && stemMelonColors != null) {
         customColormap = stemMelonColors;
      }

      if (customColormap == null) {
         return -1;
      }

      int i = renderEnv.getMetadata();
      return customColormap.getColor(i);
   }

   public static boolean updateLightmap(World world, float f, int[] ints, boolean bl) {
      if (world == null) {
         return false;
      }

      if (lightMapsColorsRgb == null) {
         return false;
      }

      int i = world.provider.getDimensionId();
      int j = i - lightmapMinDimensionId;
      if (j >= 0 && j < lightMapsColorsRgb.length) {
         CustomColormap customColormap = lightMapsColorsRgb[j];
         if (customColormap == null) {
            return false;
         }

         int k = customColormap.getHeight();
         if (bl && k < 64) {
            return false;
         }

         int l = customColormap.getWidth();
         if (l < 16) {
            warn("Invalid lightmap width: " + l + " for dimension: " + i);
            lightMapsColorsRgb[j] = null;
            return false;
         }

         int m = 0;
         if (bl) {
            m = l * 16 * 2;
         }

         float g = 1.1666666F * (world.getSunBrightness(1.0F) - 0.2F);
         if (world.getLastLightningBolt() > 0) {
            g = 1.0F;
         }

         g = Config.limitTo1(g);
         float h = g * (l - 1);
         float f2 = Config.limitTo1(f + 0.5F) * (l - 1);
         float f3 = Config.limitTo1(Config.getGameSettings().gammaSetting);
         boolean bl2 = f3 > 1.0E-4F;
         float[][] floatss = customColormap.getColorsRgb();
         getLightMapColumn(floatss, h, m, l, sunRgbs);
         getLightMapColumn(floatss, f2, m + 16 * l, l, torchRgbs);
         float[] floats = new float[3];

         for (int n = 0; n < 16; n++) {
            for (int i2 = 0; i2 < 16; i2++) {
               for (int i3 = 0; i3 < 3; i3++) {
                  float f4 = Config.limitTo1(sunRgbs[n][i3] + torchRgbs[i2][i3]);
                  if (bl2) {
                     float f5 = 1.0F - f4;
                     f5 = 1.0F - f5 * f5 * f5 * f5;
                     f4 = f3 * f5 + (1.0F - f3) * f4;
                  }

                  floats[i3] = f4;
               }

               int i4 = (int)(floats[0] * 255.0F);
               int i5 = (int)(floats[1] * 255.0F);
               int i6 = (int)(floats[2] * 255.0F);
               ints[n * 16 + i2] = 0xFF000000 | i4 << 16 | i5 << 8 | i6;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static void getLightMapColumn(float[][] floats, float f, int i, int j, float[][] floats2) {
      int k = (int)Math.floor(f);
      int l = (int)Math.ceil(f);
      if (k == l) {
         for (int m = 0; m < 16; m++) {
            float[] floats3 = floats[i + m * j + k];
            float[] floats4 = floats2[m];

            for (int n = 0; n < 3; n++) {
               floats4[n] = floats3[n];
            }
         }
      } else {
         float g = 1.0F - (f - k);
         float h = 1.0F - (l - f);

         for (int i2 = 0; i2 < 16; i2++) {
            float[] floats5 = floats[i + i2 * j + k];
            float[] floats6 = floats[i + i2 * j + l];
            float[] floats7 = floats2[i2];

            for (int i3 = 0; i3 < 3; i3++) {
               floats7[i3] = floats5[i3] * g + floats6[i3] * h;
            }
         }
      }
   }

   public static Vec3 getWorldFogColor(Vec3 fogVec, WorldClient world, Entity renderViewEntity, float partialTicks) {
      int i = world.provider.getDimensionId();
      switch (i) {
         case -1:
            fogVec = getFogColorNether(fogVec);
            break;
         case 0:
            Minecraft minecraft = Minecraft.getMinecraft();
            fogVec = getFogColor(fogVec, minecraft.theWorld, renderViewEntity.posX, renderViewEntity.posY + 1.0, renderViewEntity.posZ);
            break;
         case 1:
            fogVec = getFogColorEnd(fogVec);
      }

      return fogVec;
   }

   public static Vec3 getWorldSkyColor(Vec3 skyVec, World world, Entity renderViewEntity, float partialTicks) {
      int i = world.provider.getDimensionId();
      switch (i) {
         case 0:
            Minecraft minecraft = Minecraft.getMinecraft();
            skyVec = getSkyColor(skyVec, minecraft.theWorld, renderViewEntity.posX, renderViewEntity.posY + 1.0, renderViewEntity.posZ);
            break;
         case 1:
            skyVec = getSkyColorEnd(skyVec);
      }

      return skyVec;
   }

   private static int[] readSpawnEggColors(Properties props, String fileName, String prefix, String logName) {
      ArrayList arrayList = new ArrayList();
      Set set2 = props.keySet();
      int i = 0;

      for (Object object : set2) {
         String string = props.getProperty((String)object);
         if (((String)object).startsWith(prefix)) {
            String string2 = StrUtils.removePrefix((String)object, prefix);
            int j = getEntityId(string2);
            int k = parseColor(string);
            if (j >= 0 && k >= 0) {
               while (arrayList.size() <= j) {
                  arrayList.add(-1);
               }

               arrayList.set(j, k);
               i++;
            } else {
               warn("Invalid spawn egg color: " + object + " = " + string);
            }
         }
      }

      if (i <= 0) {
         return null;
      }

      dbg(logName + " colors: " + i);
      int[] ints = new int[arrayList.size()];

      for (int l = 0; l < ints.length; l++) {
         ints[l] = (Integer)arrayList.get(l);
      }

      return ints;
   }

   private static int getSpawnEggColor(ItemMonsterPlacer item, ItemStack itemStack, int layer, int color) {
      int i = itemStack.getMetadata();
      int[] ints = layer == 0 ? spawnEggPrimaryColors : spawnEggSecondaryColors;
      if (ints == null) {
         return color;
      } else if (i >= 0 && i < ints.length) {
         int j = ints[i];
         return j < 0 ? color : j;
      } else {
         return color;
      }
   }

   public static int getColorFromItemStack(ItemStack itemStack, int layer, int color) {
      if (itemStack == null) {
         return color;
      }

      Item item = itemStack.getItem();
      return item == null ? color : (item instanceof ItemMonsterPlacer ? getSpawnEggColor((ItemMonsterPlacer)item, itemStack, layer, color) : color);
   }

   private static float[][] readDyeColors(Properties props, String fileName, String prefix, String logName) {
      EnumDyeColor[] enumDyeColors = EnumDyeColor.values();
      HashMap hashMap = new HashMap();

      for (int i = 0; i < enumDyeColors.length; i++) {
         EnumDyeColor enumDyeColor = enumDyeColors[i];
         hashMap.put(enumDyeColor.getName(), enumDyeColor);
      }

      float[][] floatss = new float[enumDyeColors.length][];
      int j = 0;

      for (Object object : props.keySet()) {
         String string = props.getProperty((String)object);
         if (((String)object).startsWith(prefix)) {
            String string2 = StrUtils.removePrefix((String)object, prefix);
            if (string2.equals("lightBlue")) {
               string2 = "light_blue";
            }

            EnumDyeColor enumDyeColor2 = (EnumDyeColor)hashMap.get(string2);
            int k = parseColor(string);
            if (enumDyeColor2 != null && k >= 0) {
               float[] floats = new float[]{(k >> 16 & 0xFF) / 255.0F, (k >> 8 & 0xFF) / 255.0F, (k & 0xFF) / 255.0F};
               floatss[enumDyeColor2.ordinal()] = floats;
               j++;
            } else {
               warn("Invalid color: " + object + " = " + string);
            }
         }
      }

      if (j <= 0) {
         return (float[][])null;
      }

      dbg(logName + " colors: " + j);
      return floatss;
   }

   private static float[] getDyeColors(EnumDyeColor dye, float[][] dyeColors, float[] colors) {
      if (dyeColors == null) {
         return colors;
      }

      if (dye == null) {
         return colors;
      }

      float[] floats = dyeColors[dye.ordinal()];
      return floats == null ? colors : floats;
   }

   public static float[] getWolfCollarColors(EnumDyeColor dye, float[] colors) {
      return getDyeColors(dye, wolfCollarColors, colors);
   }

   public static float[] getSheepColors(EnumDyeColor dye, float[] colors) {
      return getDyeColors(dye, sheepColors, colors);
   }

   private static int[] readTextColors(Properties props, String fileName, String prefix, String logName) {
      int[] ints = new int[32];
      Arrays.fill(ints, -1);
      int i = 0;

      for (Object object : props.keySet()) {
         String string = props.getProperty((String)object);
         if (((String)object).startsWith(prefix)) {
            String string2 = StrUtils.removePrefix((String)object, prefix);
            int j = Config.parseInt(string2, -1);
            int k = parseColor(string);
            if (j >= 0 && j < ints.length && k >= 0) {
               ints[j] = k;
               i++;
            } else {
               warn("Invalid color: " + object + " = " + string);
            }
         }
      }

      if (i <= 0) {
         return null;
      }

      dbg(logName + " colors: " + i);
      return ints;
   }

   public static int getTextColor(int index, int color) {
      if (textColors == null) {
         return color;
      } else if (index >= 0 && index < textColors.length) {
         int i = textColors[index];
         return i < 0 ? color : i;
      } else {
         return color;
      }
   }

   private static int[] readMapColors(Properties props, String fileName, String prefix, String logName) {
      int[] ints = new int[MapColor.mapColorArray.length];
      Arrays.fill(ints, -1);
      int i = 0;

      for (Object object : props.keySet()) {
         String string = props.getProperty((String)object);
         if (((String)object).startsWith(prefix)) {
            String string2 = StrUtils.removePrefix((String)object, prefix);
            int j = getMapColorIndex(string2);
            int k = parseColor(string);
            if (j >= 0 && j < ints.length && k >= 0) {
               ints[j] = k;
               i++;
            } else {
               warn("Invalid color: " + object + " = " + string);
            }
         }
      }

      if (i <= 0) {
         return null;
      }

      dbg(logName + " colors: " + i);
      return ints;
   }

   private static int[] readPotionColors(Properties props, String fileName, String prefix, String logName) {
      int[] ints = new int[Potion.potionTypes.length];
      Arrays.fill(ints, -1);
      int i = 0;

      for (Object object : props.keySet()) {
         String string = props.getProperty((String)object);
         if (((String)object).startsWith(prefix)) {
            int j = getPotionId((String)object);
            int k = parseColor(string);
            if (j >= 0 && j < ints.length && k >= 0) {
               ints[j] = k;
               i++;
            } else {
               warn("Invalid color: " + object + " = " + string);
            }
         }
      }

      if (i <= 0) {
         return null;
      }

      dbg(logName + " colors: " + i);
      return ints;
   }

   private static int getPotionId(String name) {
      if (name.equals("potion.water")) {
         return 0;
      }

      Potion[] potions = Potion.potionTypes;

      for (int i = 0; i < potions.length; i++) {
         Potion potion2 = potions[i];
         if (potion2 != null && potion2.getName().equals(name)) {
            return potion2.getId();
         }
      }

      return -1;
   }

   public static int getPotionColor(int potionId, int color) {
      if (potionColors == null) {
         return color;
      } else if (potionId >= 0 && potionId < potionColors.length) {
         int i = potionColors[potionId];
         return i < 0 ? color : i;
      } else {
         return color;
      }
   }

   private static int getMapColorIndex(String name) {
      return name == null
         ? -1
         : (
            name.equals("air")
               ? MapColor.airColor.colorIndex
               : (
                  name.equals("grass")
                     ? MapColor.grassColor.colorIndex
                     : (
                        name.equals("sand")
                           ? MapColor.sandColor.colorIndex
                           : (
                              name.equals("cloth")
                                 ? MapColor.clothColor.colorIndex
                                 : (
                                    name.equals("tnt")
                                       ? MapColor.tntColor.colorIndex
                                       : (
                                          name.equals("ice")
                                             ? MapColor.iceColor.colorIndex
                                             : (
                                                name.equals("iron")
                                                   ? MapColor.ironColor.colorIndex
                                                   : (
                                                      name.equals("foliage")
                                                         ? MapColor.foliageColor.colorIndex
                                                         : (
                                                            name.equals("clay")
                                                               ? MapColor.clayColor.colorIndex
                                                               : (
                                                                  name.equals("dirt")
                                                                     ? MapColor.dirtColor.colorIndex
                                                                     : (
                                                                        name.equals("stone")
                                                                           ? MapColor.stoneColor.colorIndex
                                                                           : (
                                                                              name.equals("water")
                                                                                 ? MapColor.waterColor.colorIndex
                                                                                 : (
                                                                                    name.equals("wood")
                                                                                       ? MapColor.woodColor.colorIndex
                                                                                       : (
                                                                                          name.equals("quartz")
                                                                                             ? MapColor.quartzColor.colorIndex
                                                                                             : (
                                                                                                name.equals("gold")
                                                                                                   ? MapColor.goldColor.colorIndex
                                                                                                   : (
                                                                                                      name.equals("diamond")
                                                                                                         ? MapColor.diamondColor.colorIndex
                                                                                                         : (
                                                                                                            name.equals("lapis")
                                                                                                               ? MapColor.lapisColor.colorIndex
                                                                                                               : (
                                                                                                                  name.equals("emerald")
                                                                                                                     ? MapColor.emeraldColor.colorIndex
                                                                                                                     : (
                                                                                                                        name.equals("podzol")
                                                                                                                           ? MapColor.obsidianColor.colorIndex
                                                                                                                           : (
                                                                                                                              name.equals("netherrack")
                                                                                                                                 ? MapColor.netherrackColor.colorIndex
                                                                                                                                 : (
                                                                                                                                    name.equals("snow")
                                                                                                                                          || name.equals(
                                                                                                                                             "white"
                                                                                                                                          )
                                                                                                                                       ? MapColor.snowColor.colorIndex
                                                                                                                                       : (
                                                                                                                                          name.equals("adobe")
                                                                                                                                                || name.equals(
                                                                                                                                                   "orange"
                                                                                                                                                )
                                                                                                                                             ? MapColor.adobeColor
                                                                                                                                                .colorIndex
                                                                                                                                             : (
                                                                                                                                                name.equals(
                                                                                                                                                      "magenta"
                                                                                                                                                   )
                                                                                                                                                   ? MapColor.magentaColor
                                                                                                                                                      .colorIndex
                                                                                                                                                   : (
                                                                                                                                                      !name.equals(
                                                                                                                                                               "light_blue"
                                                                                                                                                            )
                                                                                                                                                            && !name.equals(
                                                                                                                                                               "lightBlue"
                                                                                                                                                            )
                                                                                                                                                         ? (
                                                                                                                                                            name.equals(
                                                                                                                                                                  "yellow"
                                                                                                                                                               )
                                                                                                                                                               ? MapColor.yellowColor
                                                                                                                                                                  .colorIndex
                                                                                                                                                               : (
                                                                                                                                                                  name.equals(
                                                                                                                                                                        "lime"
                                                                                                                                                                     )
                                                                                                                                                                     ? MapColor.limeColor
                                                                                                                                                                        .colorIndex
                                                                                                                                                                     : (
                                                                                                                                                                        name.equals(
                                                                                                                                                                              "pink"
                                                                                                                                                                           )
                                                                                                                                                                           ? MapColor.pinkColor
                                                                                                                                                                              .colorIndex
                                                                                                                                                                           : (
                                                                                                                                                                              name.equals(
                                                                                                                                                                                    "gray"
                                                                                                                                                                                 )
                                                                                                                                                                                 ? MapColor.grayColor
                                                                                                                                                                                    .colorIndex
                                                                                                                                                                                 : (
                                                                                                                                                                                    name.equals(
                                                                                                                                                                                          "silver"
                                                                                                                                                                                       )
                                                                                                                                                                                       ? MapColor.silverColor
                                                                                                                                                                                          .colorIndex
                                                                                                                                                                                       : (
                                                                                                                                                                                          name.equals(
                                                                                                                                                                                                "cyan"
                                                                                                                                                                                             )
                                                                                                                                                                                             ? MapColor.cyanColor
                                                                                                                                                                                                .colorIndex
                                                                                                                                                                                             : (
                                                                                                                                                                                                name.equals(
                                                                                                                                                                                                      "purple"
                                                                                                                                                                                                   )
                                                                                                                                                                                                   ? MapColor.purpleColor
                                                                                                                                                                                                      .colorIndex
                                                                                                                                                                                                   : (
                                                                                                                                                                                                      name.equals(
                                                                                                                                                                                                            "blue"
                                                                                                                                                                                                         )
                                                                                                                                                                                                         ? MapColor.blueColor
                                                                                                                                                                                                            .colorIndex
                                                                                                                                                                                                         : (
                                                                                                                                                                                                            name.equals(
                                                                                                                                                                                                                  "brown"
                                                                                                                                                                                                               )
                                                                                                                                                                                                               ? MapColor.brownColor
                                                                                                                                                                                                                  .colorIndex
                                                                                                                                                                                                               : (
                                                                                                                                                                                                                  name.equals(
                                                                                                                                                                                                                        "green"
                                                                                                                                                                                                                     )
                                                                                                                                                                                                                     ? MapColor.greenColor
                                                                                                                                                                                                                        .colorIndex
                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                        name.equals(
                                                                                                                                                                                                                              "red"
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                           ? MapColor.redColor
                                                                                                                                                                                                                              .colorIndex
                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                              name.equals(
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
      MapColor[] mapColors = MapColor.mapColorArray;
      int[] ints = new int[mapColors.length];
      Arrays.fill(ints, -1);

      for (int i = 0; i < mapColors.length && i < ints.length; i++) {
         MapColor mapColor = mapColors[i];
         if (mapColor != null) {
            ints[i] = mapColor.colorValue;
         }
      }

      return ints;
   }

   private static void setMapColors(int[] colors) {
      if (colors != null) {
         MapColor[] mapColors = MapColor.mapColorArray;
         boolean bl = false;

         for (int i = 0; i < mapColors.length && i < colors.length; i++) {
            MapColor mapColor = mapColors[i];
            if (mapColor != null) {
               int j = colors[i];
               if (j >= 0 && mapColor.colorValue != j) {
                  mapColor.colorValue = j;
                  bl = true;
               }
            }
         }

         if (bl) {
            Minecraft.getMinecraft().getTextureManager().reloadBannerTextures();
         }
      }
   }

   private static int getEntityId(String name) {
      if (name == null) {
         return -1;
      }

      int i = EntityList.getIDFromString(name);
      if (i < 0) {
         return -1;
      }

      String string = EntityList.getStringFromID(i);
      return !Config.equals(name, string) ? -1 : i;
   }

   private static void dbg(String str) {
      Config.dbg("CustomColors: " + str);
   }

   private static void warn(String str) {
      Config.warn("CustomColors: " + str);
   }

   public static int getExpBarTextColor(int color) {
      return expBarTextColor < 0 ? color : expBarTextColor;
   }

   public static int getBossTextColor(int color) {
      return bossTextColor < 0 ? color : bossTextColor;
   }

   public static int getSignTextColor(int color) {
      return signTextColor < 0 ? color : signTextColor;
   }

   public static CustomColormap getSwampGrassColors() {
      return swampGrassColors;
   }

   public static CustomColormap y() {
      return swampFoliageColors;
   }

   public static CustomColormap getFoliagePineColors() {
      return foliagePineColors;
   }

   public static CustomColormap getFoliageBirchColors() {
      return foliageBirchColors;
   }

   public static CustomColormap getWaterColors() {
      return waterColors;
   }

   public interface IColorizer {
      int getColor(IBlockAccess iBlockAccess, BlockPos blockPos);

      boolean isColorConstant();
   }
}
