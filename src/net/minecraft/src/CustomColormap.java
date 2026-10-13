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
   private static final int V = 2;
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
   private static final long b = 33731680684698L;

   public CustomColormap(Properties var1, String var2, int var3, int var4, String var5) {
      ConnectedParser var8 = new ConnectedParser("Colormap");
      this.name = var8.parseName(var2);
      this.basePath = var8.parseBasePath(var2);
      this.format = this.parseFormat(var1.getProperty("format", var5));
      this.matchBlocks = var8.t(var1.getProperty("blocks"));
      this.source = parseTexture(var1.getProperty("source"), var2, this.basePath);
      this.color = ConnectedParser.w(var1.getProperty("color"), -1);
      this.yVariance = var8.O(var1.getProperty("yVariance"), 0);
      this.yOffset = var8.O(var1.getProperty("yOffset"), 0);
      this.width = var3;
      this.height = var4;
   }

   private int parseFormat(String var1) {
      if (var1 == null) {
         return 0;
      }

      if (var1.equals("vanilla")) {
         return 0;
      }

      if (var1.equals("grid")) {
         return 1;
      }

      if (var1.equals("fixed")) {
         return 2;
      }

      warn("Unknown format: " + var1);
      return -1;
   }

   public boolean isValid(String var1) {
      if (this.format != 0 && this.format != 1) {
         if (this.format != 2) {
            return false;
         }

         if (this.color < 0) {
            this.color = 16777215;
         }
      } else {
         if (this.source == null) {
            warn("Source not defined: " + var1);
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

   public boolean isValidMatchBlocks(String var1) {
      if (this.matchBlocks == null) {
         this.matchBlocks = this.detectMatchBlocks();
         if (this.matchBlocks == null) {
            warn("Match blocks not defined: " + var1);
            return false;
         }
      }

      return true;
   }

   private MatchBlock[] detectMatchBlocks() {
      Block var4 = Block.getBlockFromName(this.name);
      if (var4 != null) {
         return new MatchBlock[]{new MatchBlock(Block.getIdFromBlock(var4))};
      }

      Pattern var5 = Pattern.compile("^block([0-9]+).*$");
      Matcher var6 = var5.matcher(this.name);
      if (var6.matches()) {
         String var7 = var6.group(1);
         int var8 = Config.parseInt(var7, -1);
         if (var8 >= 0) {
            return new MatchBlock[]{new MatchBlock(var8)};
         }
      }

      ConnectedParser var9 = new ConnectedParser("Colormap");
      MatchBlock[] var10 = var9.g(this.name);
      return var10 != null ? var10 : null;
   }

   private void readColors() {
      try {
         this.colors = null;
         if (this.source == null) {
            return;
         }

         String var4 = this.source + ".png";
         ResourceLocation var5 = new ResourceLocation(var4);
         InputStream var6 = Config.getResourceStream(var5);
         if (var6 == null) {
            return;
         }

         BufferedImage var7 = TextureUtil.readBufferedImage(var6);
         if (var7 == null) {
            return;
         }

         int var8 = var7.getWidth();
         int var9 = var7.getHeight();
         boolean var10 = this.width < 0 || this.width == var8;
         boolean var11 = this.height < 0 || this.height == var9;
         if (!var10 || !var11) {
            dbg("Non-standard palette size: " + var8 + "x" + var9 + ", should be: " + this.width + "x" + this.height + ", path: " + var4);
         }

         this.width = var8;
         this.height = var9;
         if (this.width <= 0 || this.height <= 0) {
            warn("Invalid palette size: " + var8 + "x" + var9 + ", path: " + var4);
            return;
         }

         this.colors = new int[var8 * var9];
         var7.getRGB(0, 0, var8, var9, this.colors, 0, var8);
      } catch (IOException var12) {
         var12.printStackTrace();
      }
   }

   private static void dbg(String var0) {
      Config.dbg("CustomColors: " + var0);
   }

   private static void warn(String var0) {
      Config.warn("CustomColors: " + var0);
   }

   private static String parseTexture(String var0, String var1, String var2) {
      if (var0 != null) {
         String var9 = ".png";
         if (var0.endsWith(var9)) {
            var0 = var0.substring(0, var0.length() - var9.length());
         }

         return fixTextureName(var0, var2);
      } else {
         String var6 = var1;
         int var7 = var1.lastIndexOf(47);
         if (var7 >= 0) {
            var6 = var1.substring(var7 + 1);
         }

         int var8 = var6.lastIndexOf(46);
         if (var8 >= 0) {
            var6 = var6.substring(0, var8);
         }

         return fixTextureName(var6, var2);
      }
   }

   private static String fixTextureName(String var0, String var1) {
      var0 = TextureUtils.fixResourcePath(var0, var1);
      if (!var0.startsWith(var1) && !var0.startsWith("textures/") && !var0.startsWith("mcpatcher/")) {
         var0 = var1 + "/" + var0;
      }

      if (var0.endsWith(".png")) {
         var0 = var0.substring(0, var0.length() - 4);
      }

      String var5 = "textures/blocks/";
      if (var0.startsWith(var5)) {
         var0 = var0.substring(var5.length());
      }

      if (var0.startsWith("/")) {
         var0 = var0.substring(1);
      }

      return var0;
   }

   public boolean matchesBlock(BlockStateBase var1) {
      return Matches.block(var1, this.matchBlocks);
   }

   public int getColorRandom() {
      if (this.format == 2) {
         return this.color;
      }

      int var4 = CustomColors.random.nextInt(this.colors.length);
      return this.colors[var4];
   }

   public int getColor(int var1) {
      var1 = Config.limit(var1, 0, this.colors.length - 1);
      return this.colors[var1] & 16777215;
   }

   public int getColor(int var1, int var2) {
      var1 = Config.limit(var1, 0, this.width - 1);
      var2 = Config.limit(var2, 0, this.height - 1);
      return this.colors[var2 * this.width + var1] & 16777215;
   }

   public float[][] getColorsRgb() {
      if (this.colorsRgb == null) {
         this.colorsRgb = toRgb(this.colors);
      }

      return this.colorsRgb;
   }

   @Override
   public int getColor(IBlockAccess var1, BlockPos var2) {
      BiomeGenBase var3 = CustomColors.getColorBiome(var1, var2);
      return this.getColor(var3, var2);
   }

   @Override
   public boolean isColorConstant() {
      return this.format == 2;
   }

   public int getColor(BiomeGenBase var1, BlockPos var2) {
      return this.format == 0 ? this.R(var1, var2) : (this.format == 1 ? this.getColorGrid(var1, var2) : this.color);
   }

   public int getColorSmooth(IBlockAccess var1, double var2, double var4, double var6, int var8) {
      if (this.format == 2) {
         return this.color;
      }

      int var12 = MathHelper.floor_double(var2);
      int var13 = MathHelper.floor_double(var4);
      int var14 = MathHelper.floor_double(var6);
      int var15 = 0;
      int var16 = 0;
      int var17 = 0;
      int var18 = 0;
      BlockPosM var19 = new BlockPosM(0, 0, 0);

      for (int var20 = var12 - var8; var20 <= var12 + var8; var20++) {
         for (int var21 = var14 - var8; var21 <= var14 + var8; var21++) {
            var19.setXyz(var20, var13, var21);
            int var22 = this.getColor(var1, var19);
            var15 += var22 >> 16 & 0xFF;
            var16 += var22 >> 8 & 0xFF;
            var17 += var22 & 0xFF;
            var18++;
         }
      }

      int var23 = var15 / var18;
      int var24 = var16 / var18;
      int var25 = var17 / var18;
      return var23 << 16 | var24 << 8 | var25;
   }

   private int R(BiomeGenBase var1, BlockPos var2) {
      double var3 = MathHelper.clamp_float(var1.getFloatTemperature(var2), 0.0F, 1.0F);
      double var5 = MathHelper.clamp_float(var1.getFloatRainfall(), 0.0F, 1.0F);
      var5 *= var3;
      int var7 = (int)((1.0 - var3) * (this.width - 1));
      int var8 = (int)((1.0 - var5) * (this.height - 1));
      return this.getColor(var7, var8);
   }

   private int getColorGrid(BiomeGenBase var1, BlockPos var2) {
      int var6 = var1.biomeID;
      int var7 = var2.getY() - this.yOffset;
      if (this.yVariance > 0) {
         int var8 = var2.getX() << 16 + var2.getZ();
         int var9 = Config.intHash(var8);
         int var10 = this.yVariance * 2 + 1;
         int var11 = (var9 & 0xFF) % var10 - this.yVariance;
         var7 += var11;
      }

      return this.getColor(var6, var7);
   }

   public int getLength() {
      return this.format == 2 ? 1 : this.colors.length;
   }

   public int i() {
      return this.width;
   }

   public int W() {
      return this.height;
   }

   private static float[][] toRgb(int[] var0) {
      float[][] var4 = new float[var0.length][3];

      for (int var5 = 0; var5 < var0.length; var5++) {
         int var6 = var0[var5];
         float var7 = (var6 >> 16 & 0xFF) / 255.0F;
         float var8 = (var6 >> 8 & 0xFF) / 255.0F;
         float var9 = (var6 & 0xFF) / 255.0F;
         float[] var10 = var4[var5];
         var10[0] = var7;
         var10[1] = var8;
         var10[2] = var9;
      }

      return var4;
   }

   public void addMatchBlock(MatchBlock var1) {
      if (this.matchBlocks == null) {
         this.matchBlocks = new MatchBlock[0];
      }

      this.matchBlocks = (MatchBlock[])Config.addObjectToArray(this.matchBlocks, var1);
   }

   public void addMatchBlock(int var1, int var2) {
      MatchBlock var6 = this.getMatchBlock(var1);
      if (var6 != null) {
         if (var2 >= 0) {
            var6.addMetadata(var2);
         }
      } else {
         this.addMatchBlock(new MatchBlock(var1, var2));
      }
   }

   private MatchBlock getMatchBlock(int var1) {
      if (this.matchBlocks == null) {
         return null;
      }

      for (int var5 = 0; var5 < this.matchBlocks.length; var5++) {
         MatchBlock var6 = this.matchBlocks[var5];
         if (var6.getBlockId() == var1) {
            return var6;
         }
      }

      return null;
   }

   public int[] getMatchBlockIds() {
      if (this.matchBlocks == null) {
         return null;
      }

      HashSet var4 = new HashSet();

      for (int var5 = 0; var5 < this.matchBlocks.length; var5++) {
         MatchBlock var6 = this.matchBlocks[var5];
         if (var6.getBlockId() >= 0) {
            var4.add(var6.getBlockId());
         }
      }

      Integer[] var8 = (java.lang.Integer[])(var4.toArray(new Integer[var4.size()]));
      int[] var9 = new int[var8.length];

      for (int var7 = 0; var7 < var8.length; var7++) {
         var9[var7] = var8[var7];
      }

      return var9;
   }

   @Override
   public String toString() {
      return "" + this.basePath + "/" + this.name + ", blocks: " + Config.arrayToString(this.matchBlocks) + ", source: " + this.source;
   }

   static {
      long var20 = b ^ 103864604932848L;
      long var10000 = var20 ^ 5223541030354725770L;
   }
}
