package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public class CustomColormap implements CustomColors.IColorizer {
   public String name = null;
   public String basePath = null;
   private int format = -1;
   private MatchBlock[] matchBlocks = null;
   private String source = null;
   private int color = -1;
   private int yVariance = 0;
   private int yOffset = 0;
   private int width = 0;
   private int height = 0;
   private int[] colors = null;
   private float[][] colorsRgb = (float[][])null;
   private static final int FORMAT_FIXED = 2;
   public static final String FORMAT_VANILLA_STRING = "vanilla";
   public static final String FORMAT_GRID_STRING = "grid";
   public static final String FORMAT_FIXED_STRING = "fixed";
   public static final String[] FORMAT_STRINGS = new String[]{"vanilla", "grid", "fixed"};
   public static final String KEY_FORMAT = "format";
   public static final String KEY_BLOCKS = "blocks";
   public static final String KEY_SOURCE = "source";
   public static final String KEY_COLOR = "color";
   public static final String KEY_Y_VARIANCE = "yVariance";
   public static final String KEY_Y_OFFSET = "yOffset";

   public CustomColormap(Properties props, String path, int width, int height, String formatDefault) {
      ConnectedParser connectedParser = new ConnectedParser("Colormap");
      this.name = connectedParser.parseName(path);
      this.basePath = connectedParser.parseBasePath(path);
      this.format = this.parseFormat(props.getProperty("format", formatDefault));
      this.matchBlocks = connectedParser.parseMatchBlocks(props.getProperty("blocks"));
      this.source = parseTexture(props.getProperty("source"), path, this.basePath);
      this.color = ConnectedParser.parseColor(props.getProperty("color"), -1);
      this.yVariance = connectedParser.parseIntOrDefault(props.getProperty("yVariance"), 0);
      this.yOffset = connectedParser.parseIntOrDefault(props.getProperty("yOffset"), 0);
      this.width = width;
      this.height = height;
   }

   private int parseFormat(String str) {
      if (str == null) {
         return 0;
      }

      if (str.equals("vanilla")) {
         return 0;
      }

      if (str.equals("grid")) {
         return 1;
      }

      if (str.equals("fixed")) {
         return 2;
      }

      warn("Unknown format: " + str);
      return -1;
   }

   public boolean isValid(String path) {
      if (this.format != 0 && this.format != 1) {
         if (this.format != 2) {
            return false;
         }

         if (this.color < 0) {
            this.color = 16777215;
         }
      } else {
         if (this.source == null) {
            warn("Source not defined: " + path);
            return false;
         }

         this.readColors();
         if (this.colors == null) {
            return false;
         }

         if (this.color < 0) {
            if (this.format == 0) {
               this.color = this.getColor(127, 127);
            }

            if (this.format == 1) {
               this.color = this.getColorGrid(BiomeGenBase.plains, new BlockPos(0, 64, 0));
            }
         }
      }

      return true;
   }

   public boolean isValidMatchBlocks(String path) {
      if (this.matchBlocks == null) {
         this.matchBlocks = this.detectMatchBlocks();
         if (this.matchBlocks == null) {
            warn("Match blocks not defined: " + path);
            return false;
         }
      }

      return true;
   }

   private MatchBlock[] detectMatchBlocks() {
      Block block2 = Block.getBlockFromName(this.name);
      if (block2 != null) {
         return new MatchBlock[]{new MatchBlock(Block.getIdFromBlock(block2))};
      }

      Pattern pattern = Pattern.compile("^block([0-9]+).*$");
      Matcher matcher2 = pattern.matcher(this.name);
      if (matcher2.matches()) {
         String string = matcher2.group(1);
         int i = Config.parseInt(string, -1);
         if (i >= 0) {
            return new MatchBlock[]{new MatchBlock(i)};
         }
      }

      ConnectedParser connectedParser = new ConnectedParser("Colormap");
      MatchBlock[] matchBlocks = connectedParser.parseMatchBlock(this.name);
      return matchBlocks != null ? matchBlocks : null;
   }

   private void readColors() {
      try {
         this.colors = null;
         if (this.source == null) {
            return;
         }

         String string = this.source + ".png";
         ResourceLocation resourceLocation = new ResourceLocation(string);
         InputStream inputStream = Config.getResourceStream(resourceLocation);
         if (inputStream == null) {
            return;
         }

         BufferedImage bufferedImage = TextureUtil.readBufferedImage(inputStream);
         if (bufferedImage == null) {
            return;
         }

         int i = bufferedImage.getWidth();
         int j = bufferedImage.getHeight();
         boolean bl = this.width < 0 || this.width == i;
         boolean bl2 = this.height < 0 || this.height == j;
         if (!bl || !bl2) {
            dbg("Non-standard palette size: " + i + "x" + j + ", should be: " + this.width + "x" + this.height + ", path: " + string);
         }

         this.width = i;
         this.height = j;
         if (this.width <= 0 || this.height <= 0) {
            warn("Invalid palette size: " + i + "x" + j + ", path: " + string);
            return;
         }

         this.colors = new int[i * j];
         bufferedImage.getRGB(0, 0, i, j, this.colors, 0, i);
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   private static void dbg(String str) {
      Config.dbg("CustomColors: " + str);
   }

   private static void warn(String str) {
      Config.warn("CustomColors: " + str);
   }

   private static String parseTexture(String texStr, String path, String basePath) {
      if (texStr != null) {
         String string = ".png";
         if (texStr.endsWith(string)) {
            texStr = texStr.substring(0, texStr.length() - string.length());
         }

         return fixTextureName(texStr, basePath);
      } else {
         String string2 = path;
         int i = path.lastIndexOf(47);
         if (i >= 0) {
            string2 = path.substring(i + 1);
         }

         int j = string2.lastIndexOf(46);
         if (j >= 0) {
            string2 = string2.substring(0, j);
         }

         return fixTextureName(string2, basePath);
      }
   }

   private static String fixTextureName(String iconName, String basePath) {
      iconName = TextureUtils.fixResourcePath(iconName, basePath);
      if (!iconName.startsWith(basePath) && !iconName.startsWith("textures/") && !iconName.startsWith("mcpatcher/")) {
         iconName = basePath + "/" + iconName;
      }

      if (iconName.endsWith(".png")) {
         iconName = iconName.substring(0, iconName.length() - 4);
      }

      String string = "textures/blocks/";
      if (iconName.startsWith(string)) {
         iconName = iconName.substring(string.length());
      }

      if (iconName.startsWith("/")) {
         iconName = iconName.substring(1);
      }

      return iconName;
   }

   public boolean matchesBlock(BlockStateBase blockState) {
      return Matches.block(blockState, this.matchBlocks);
   }

   public int getColorRandom() {
      if (this.format == 2) {
         return this.color;
      }

      int i = CustomColors.random.nextInt(this.colors.length);
      return this.colors[i];
   }

   public int getColor(int index) {
      index = Config.limit(index, 0, this.colors.length - 1);
      return this.colors[index] & 16777215;
   }

   public int getColor(int cx, int cy) {
      cx = Config.limit(cx, 0, this.width - 1);
      cy = Config.limit(cy, 0, this.height - 1);
      return this.colors[cy * this.width + cx] & 16777215;
   }

   public float[][] getColorsRgb() {
      if (this.colorsRgb == null) {
         this.colorsRgb = toRgb(this.colors);
      }

      return this.colorsRgb;
   }

   @Override
   public int getColor(IBlockAccess blockAccess, BlockPos blockPos) {
      BiomeGenBase biome = CustomColors.getColorBiome(blockAccess, blockPos);
      return this.getColor(biome, blockPos);
   }

   @Override
   public boolean isColorConstant() {
      return this.format == 2;
   }

   public int getColor(BiomeGenBase biome, BlockPos blockPos) {
      return this.format == 0 ? this.getColorVanilla(biome, blockPos) : (this.format == 1 ? this.getColorGrid(biome, blockPos) : this.color);
   }

   public int getColorSmooth(IBlockAccess blockAccess, double x, double y, double z, int radius) {
      if (this.format == 2) {
         return this.color;
      }

      int i = MathHelper.floor_double(x);
      int j = MathHelper.floor_double(y);
      int k = MathHelper.floor_double(z);
      int l = 0;
      int m = 0;
      int n = 0;
      int i2 = 0;
      BlockPosM blockPosM = new BlockPosM(0, 0, 0);

      for (int i3 = i - radius; i3 <= i + radius; i3++) {
         for (int i4 = k - radius; i4 <= k + radius; i4++) {
            blockPosM.setXyz(i3, j, i4);
            int i5 = this.getColor(blockAccess, blockPosM);
            l += i5 >> 16 & 0xFF;
            m += i5 >> 8 & 0xFF;
            n += i5 & 0xFF;
            i2++;
         }
      }

      int i6 = l / i2;
      int i7 = m / i2;
      int i8 = n / i2;
      return i6 << 16 | i7 << 8 | i8;
   }

   private int getColorVanilla(BiomeGenBase biome, BlockPos blockPos) {
      double temperature = MathHelper.clamp_float(biome.getFloatTemperature(blockPos), 0.0F, 1.0F);
      double rainfall = MathHelper.clamp_float(biome.getFloatRainfall(), 0.0F, 1.0F);
      rainfall *= temperature;
      int cx = (int)((1.0 - temperature) * (this.width - 1));
      int cy = (int)((1.0 - rainfall) * (this.height - 1));
      return this.getColor(cx, cy);
   }

   private int getColorGrid(BiomeGenBase biome, BlockPos blockPos) {
      int i = biome.biomeID;
      int j = blockPos.getY() - this.yOffset;
      if (this.yVariance > 0) {
         int k = blockPos.getX() << 16 + blockPos.getZ();
         int l = Config.intHash(k);
         int m = this.yVariance * 2 + 1;
         int n = (l & 0xFF) % m - this.yVariance;
         j += n;
      }

      return this.getColor(i, j);
   }

   public int getLength() {
      return this.format == 2 ? 1 : this.colors.length;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   private static float[][] toRgb(int[] cols) {
      float[][] floatss = new float[cols.length][3];

      for (int i = 0; i < cols.length; i++) {
         int j = cols[i];
         float f = (j >> 16 & 0xFF) / 255.0F;
         float g = (j >> 8 & 0xFF) / 255.0F;
         float h = (j & 0xFF) / 255.0F;
         float[] floats = floatss[i];
         floats[0] = f;
         floats[1] = g;
         floats[2] = h;
      }

      return floatss;
   }

   public void addMatchBlock(MatchBlock mb) {
      if (this.matchBlocks == null) {
         this.matchBlocks = new MatchBlock[0];
      }

      this.matchBlocks = (MatchBlock[])Config.addObjectToArray(this.matchBlocks, mb);
   }

   public void addMatchBlock(int blockId, int metadata) {
      MatchBlock matchBlock = this.getMatchBlock(blockId);
      if (matchBlock != null) {
         if (metadata >= 0) {
            matchBlock.addMetadata(metadata);
         }
      } else {
         this.addMatchBlock(new MatchBlock(blockId, metadata));
      }
   }

   private MatchBlock getMatchBlock(int blockId) {
      if (this.matchBlocks == null) {
         return null;
      }

      for (int i = 0; i < this.matchBlocks.length; i++) {
         MatchBlock matchBlock = this.matchBlocks[i];
         if (matchBlock.getBlockId() == blockId) {
            return matchBlock;
         }
      }

      return null;
   }

   public int[] getMatchBlockIds() {
      if (this.matchBlocks == null) {
         return null;
      }

      HashSet hashSet = new HashSet();

      for (int i = 0; i < this.matchBlocks.length; i++) {
         MatchBlock matchBlock = this.matchBlocks[i];
         if (matchBlock.getBlockId() >= 0) {
            hashSet.add(matchBlock.getBlockId());
         }
      }

      Integer[] integers = (java.lang.Integer[])(hashSet.toArray(new Integer[hashSet.size()]));
      int[] ints = new int[integers.length];

      for (int j = 0; j < integers.length; j++) {
         ints[j] = integers[j];
      }

      return ints;
   }

   @Override
   public String toString() {
      return "" + this.basePath + "/" + this.name + ", blocks: " + Config.arrayToString(this.matchBlocks) + ", source: " + this.source;
   }
}
