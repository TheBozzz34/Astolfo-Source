package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class ConnectedProperties {
   public String name = null;
   public String basePath = null;
   public MatchBlock[] matchBlocks = null;
   public int[] metadatas = null;
   public String[] matchTiles = null;
   public int method = 0;
   public String[] tiles = null;
   public int connect = 0;
   public int faces = 63;
   public BiomeGenBase[] biomes = null;
   public int N = 0;
   public int l = 1024;
   public int renderPass = 0;
   public boolean w = false;
   public int width = 0;
   public int height = 0;
   public int[] weights = null;
   public int symmetry = 1;
   public int[] sumWeights = null;
   public int sumAllWeights = 1;
   public TextureAtlasSprite[] matchTileIcons = null;
   public TextureAtlasSprite[] tileIcons = null;
   public static final int METHOD_NONE = 0;
   public static final int METHOD_CTM = 1;
   public static final int METHOD_HORIZONTAL = 2;
   public static final int METHOD_TOP = 3;
   public static final int METHOD_RANDOM = 4;
   public static final int METHOD_REPEAT = 5;
   public static final int METHOD_VERTICAL = 6;
   public static final int METHOD_FIXED = 7;
   public static final int METHOD_HORIZONTAL_VERTICAL = 8;
   public static final int METHOD_VERTICAL_HORIZONTAL = 9;
   public static final int CONNECT_NONE = 0;
   public static final int CONNECT_BLOCK = 1;
   public static final int CONNECT_TILE = 2;
   public static final int CONNECT_MATERIAL = 3;
   public static final int CONNECT_UNKNOWN = 128;
   public static final int FACE_BOTTOM = 1;
   public static final int FACE_TOP = 2;
   public static final int FACE_NORTH = 4;
   public static final int FACE_SOUTH = 8;
   public static final int FACE_WEST = 16;
   public static final int FACE_EAST = 32;
   public static final int FACE_SIDES = 60;
   public static final int FACE_ALL = 63;
   public static final int FACE_UNKNOWN = 128;
   public static final int SYMMETRY_NONE = 1;
   public static final int SYMMETRY_OPPOSITE = 2;
   public static final int SYMMETRY_ALL = 6;
   public static final int SYMMETRY_UNKNOWN = 128;
   private static final long T = 102351302801070L;

   public ConnectedProperties(Properties var1, String var2) {
      ConnectedParser var5 = new ConnectedParser("ConnectedTextures");
      this.name = var5.parseName(var2);
      this.basePath = var5.parseBasePath(var2);
      this.matchBlocks = var5.t(var1.getProperty("matchBlocks"));
      this.metadatas = var5.parseIntList(var1.getProperty("metadata"));
      this.matchTiles = this.parseMatchTiles(var1.getProperty("matchTiles"));
      this.method = parseMethod(var1.getProperty("method"));
      this.tiles = this.parseTileNames(var1.getProperty("tiles"));
      this.connect = parseConnect(var1.getProperty("connect"));
      this.faces = parseFaces(var1.getProperty("faces"));
      this.biomes = var5.y(var1.getProperty("biomes"));
      this.N = var5.O(var1.getProperty("minHeight"), -1);
      this.l = var5.O(var1.getProperty("maxHeight"), 1024);
      this.renderPass = var5.Y(var1.getProperty("renderPass"));
      this.w = ConnectedParser.parseBoolean(var1.getProperty("innerSeams"));
      this.width = var5.Y(var1.getProperty("width"));
      this.height = var5.Y(var1.getProperty("height"));
      this.weights = var5.parseIntList(var1.getProperty("weights"));
      this.symmetry = parseSymmetry(var1.getProperty("symmetry"));
   }

   private String[] parseMatchTiles(String var1) {
      if (var1 == null) {
         return null;
      }

      String[] var5 = Config.tokenize(var1, " ");

      for (int var6 = 0; var6 < var5.length; var6++) {
         String var7 = var5[var6];
         if (var7.endsWith(".png")) {
            var7 = var7.substring(0, var7.length() - 4);
         }

         var7 = TextureUtils.fixResourcePath(var7, this.basePath);
         var5[var6] = var7;
      }

      return var5;
   }

   private String[] parseTileNames(String var1) {
      if (var1 == null) {
         return null;
      }

      ArrayList var5 = new ArrayList();
      String[] var6 = Config.tokenize(var1, " ,");

      for (int var7 = 0; var7 < var6.length; var7++) {
         String var8 = var6[var7];
         if (var8.contains("-")) {
            String[] var9 = Config.tokenize(var8, "-");
            if (var9.length == 2) {
               int var10 = Config.parseInt(var9[0], -1);
               int var11 = Config.parseInt(var9[1], -1);
               if (var10 >= 0 && var11 >= 0) {
                  if (var10 > var11) {
                     Config.warn("Invalid interval: " + var8 + ", when parsing: " + var1);
                     continue;
                  }

                  for (int var12 = var10; var12 <= var11; var12++) {
                     var5.add(String.valueOf(var12));
                  }
                  continue;
               }
            }
         }

         var5.add(var8);
      }

      String[] var13 = (java.lang.String[])(var5.toArray(new String[var5.size()]));

      for (int var14 = 0; var14 < var13.length; var14++) {
         String var15 = var13[var14];
         var15 = TextureUtils.fixResourcePath(var15, this.basePath);
         if (!var15.startsWith(this.basePath) && !var15.startsWith("textures/") && !var15.startsWith("mcpatcher/")) {
            var15 = this.basePath + "/" + var15;
         }

         if (var15.endsWith(".png")) {
            var15 = var15.substring(0, var15.length() - 4);
         }

         String var17 = "textures/blocks/";
         if (var15.startsWith(var17)) {
            var15 = var15.substring(var17.length());
         }

         if (var15.startsWith("/")) {
            var15 = var15.substring(1);
         }

         var13[var14] = var15;
      }

      return var13;
   }

   private static int parseSymmetry(String var0) {
      if (var0 == null) {
         return 1;
      }

      if (var0.equals("opposite")) {
         return 2;
      }

      if (var0.equals("all")) {
         return 6;
      }

      Config.warn("Unknown symmetry: " + var0);
      return 1;
   }

   private static int parseFaces(String var0) {
      if (var0 == null) {
         return 63;
      }

      String[] var4 = Config.tokenize(var0, " ,");
      int var5 = 0;

      for (int var6 = 0; var6 < var4.length; var6++) {
         String var7 = var4[var6];
         int var8 = parseFace(var7);
         var5 |= var8;
      }

      return var5;
   }

   private static int parseFace(String var0) {
      var0 = var0.toLowerCase();
      if (var0.equals("bottom") || var0.equals("down")) {
         return 1;
      }

      if (var0.equals("top") || var0.equals("up")) {
         return 2;
      }

      if (var0.equals("north")) {
         return 4;
      }

      if (var0.equals("south")) {
         return 8;
      }

      if (var0.equals("east")) {
         return 32;
      }

      if (var0.equals("west")) {
         return 16;
      }

      if (var0.equals("sides")) {
         return 60;
      }

      if (var0.equals("all")) {
         return 63;
      }

      Config.warn("Unknown face: " + var0);
      return 128;
   }

   private static int parseConnect(String var0) {
      if (var0 == null) {
         return 0;
      }

      if (var0.equals("block")) {
         return 1;
      }

      if (var0.equals("tile")) {
         return 2;
      }

      if (var0.equals("material")) {
         return 3;
      }

      Config.warn("Unknown connect: " + var0);
      return 128;
   }

   public static IProperty getProperty(String var0, Collection var1) {
      for (Object var6 : var1) {
         if (var0.equals(((IProperty)var6).getName())) {
            return (IProperty)var6;
         }
      }

      return null;
   }

   private static int parseMethod(String var0) {
      if (var0 == null) {
         return 1;
      } else if (var0.equals("ctm") || var0.equals("glass")) {
         return 1;
      } else if (var0.equals("horizontal") || var0.equals("bookshelf")) {
         return 2;
      } else if (var0.equals("vertical")) {
         return 6;
      } else if (var0.equals("top")) {
         return 3;
      } else if (var0.equals("random")) {
         return 4;
      } else if (var0.equals("repeat")) {
         return 5;
      } else if (var0.equals("fixed")) {
         return 7;
      } else if (var0.equals("horizontal+vertical") || var0.equals("h+v")) {
         return 8;
      } else if (!var0.equals("vertical+horizontal") && !var0.equals("v+h")) {
         Config.warn("Unknown method: " + var0);
         return 0;
      } else {
         return 9;
      }
   }

   public boolean isValid(String var1) {
      if (this.name == null || this.name.length() <= 0) {
         Config.warn("No name found: " + var1);
         return false;
      }

      if (this.basePath == null) {
         Config.warn("No base path found: " + var1);
         return false;
      }

      if (this.matchBlocks == null) {
         this.matchBlocks = this.detectMatchBlocks();
      }

      if (this.matchTiles == null && this.matchBlocks == null) {
         this.matchTiles = this.detectMatchTiles();
      }

      if (this.matchBlocks == null && this.matchTiles == null) {
         Config.warn("No matchBlocks or matchTiles specified: " + var1);
         return false;
      }

      if (this.method == 0) {
         Config.warn("No method: " + var1);
         return false;
      }

      if (this.tiles != null && this.tiles.length > 0) {
         if (this.connect == 0) {
            this.connect = this.detectConnect();
         }

         if (this.connect == 128) {
            Config.warn("Invalid connect in: " + var1);
            return false;
         }

         if (this.renderPass > 0) {
            Config.warn("Render pass not supported: " + this.renderPass);
            return false;
         }

         if ((this.faces & 128) != 0) {
            Config.warn("Invalid faces in: " + var1);
            return false;
         }

         if ((this.symmetry & 128) != 0) {
            Config.warn("Invalid symmetry in: " + var1);
            return false;
         }

         switch (this.method) {
            case 1:
               return this.isValidCtm(var1);
            case 2:
               return this.isValidHorizontal(var1);
            case 3:
               return this.isValidTop(var1);
            case 4:
               return this.isValidRandom(var1);
            case 5:
               return this.isValidRepeat(var1);
            case 6:
               return this.isValidVertical(var1);
            case 7:
               return this.isValidFixed(var1);
            case 8:
               return this.isValidHorizontalVertical(var1);
            case 9:
               return this.isValidVerticalHorizontal(var1);
            default:
               Config.warn("Unknown method: " + var1);
               return false;
         }
      } else {
         Config.warn("No tiles specified: " + var1);
         return false;
      }
   }

   private int detectConnect() {
      return this.matchBlocks != null ? 1 : (this.matchTiles != null ? 2 : 128);
   }

   private MatchBlock[] detectMatchBlocks() {
      int[] var4 = this.detectMatchBlockIds();
      if (var4 == null) {
         return null;
      }

      MatchBlock[] var5 = new MatchBlock[var4.length];

      for (int var6 = 0; var6 < var5.length; var6++) {
         var5[var6] = new MatchBlock(var4[var6]);
      }

      return var5;
   }

   private int[] detectMatchBlockIds() {
      if (!this.name.startsWith("block")) {
         return null;
      }

      int var4 = "block".length();

      int var5;
      for (var5 = var4; var5 < this.name.length(); var5++) {
         char var6 = this.name.charAt(var5);
         if (var6 < '0' || var6 > '9') {
            break;
         }
      }

      if (var5 == var4) {
         return null;
      }

      String var8 = this.name.substring(var4, var5);
      int var7 = Config.parseInt(var8, -1);
      return var7 < 0 ? null : new int[]{var7};
   }

   private String[] detectMatchTiles() {
      TextureAtlasSprite var3 = getIcon(this.name);
      return var3 == null ? null : new String[]{this.name};
   }

   private static TextureAtlasSprite getIcon(String var0) {
      TextureMap var4 = Minecraft.getMinecraft().getTextureMapBlocks();
      TextureAtlasSprite var5 = var4.getSpriteSafe(var0);
      return var5 != null ? var5 : var4.getSpriteSafe("blocks/" + var0);
   }

   private boolean isValidCtm(String var1) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("0-11 16-27 32-43 48-58");
      }

      if (this.tiles.length < 47) {
         Config.warn("Invalid tiles, must be at least 47: " + var1);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidHorizontal(String var1) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("12-15");
      }

      if (this.tiles.length != 4) {
         Config.warn("Invalid tiles, must be exactly 4: " + var1);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidVertical(String var1) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for vertical: " + var1);
         return false;
      } else if (this.tiles.length != 4) {
         Config.warn("Invalid tiles, must be exactly 4: " + var1);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidHorizontalVertical(String var1) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for horizontal+vertical: " + var1);
         return false;
      } else if (this.tiles.length != 7) {
         Config.warn("Invalid tiles, must be exactly 7: " + var1);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidVerticalHorizontal(String var1) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for vertical+horizontal: " + var1);
         return false;
      } else if (this.tiles.length != 7) {
         Config.warn("Invalid tiles, must be exactly 7: " + var1);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidRandom(String var1) {
      if (this.tiles != null && this.tiles.length > 0) {
         if (this.weights != null) {
            if (this.weights.length > this.tiles.length) {
               Config.warn("More weights defined than tiles, trimming weights: " + var1);
               int[] var5 = new int[this.tiles.length];
               System.arraycopy(this.weights, 0, var5, 0, var5.length);
               this.weights = var5;
            }

            if (this.weights.length < this.tiles.length) {
               Config.warn("Less weights defined than tiles, expanding weights: " + var1);
               int[] var8 = new int[this.tiles.length];
               System.arraycopy(this.weights, 0, var8, 0, this.weights.length);
               int var6 = MathUtils.o(this.weights);

               for (int var7 = this.weights.length; var7 < var8.length; var7++) {
                  var8[var7] = var6;
               }

               this.weights = var8;
            }

            this.sumWeights = new int[this.weights.length];
            int var9 = 0;

            for (int var10 = 0; var10 < this.weights.length; var10++) {
               var9 += this.weights[var10];
               this.sumWeights[var10] = var9;
            }

            this.sumAllWeights = var9;
            if (this.sumAllWeights <= 0) {
               Config.warn("Invalid sum of all weights: " + var9);
               this.sumAllWeights = 1;
            }
         }

         return true;
      } else {
         Config.warn("Tiles not defined: " + var1);
         return false;
      }
   }

   private boolean isValidRepeat(String var1) {
      if (this.tiles == null) {
         Config.warn("Tiles not defined: " + var1);
         return false;
      }

      if (this.width <= 0 || this.width > 16) {
         Config.warn("Invalid width: " + var1);
         return false;
      }

      if (this.height > 0 && this.height <= 16) {
         if (this.tiles.length != this.width * this.height) {
            Config.warn("Number of tiles does not equal width x height: " + var1);
            return false;
         } else {
            return true;
         }
      } else {
         Config.warn("Invalid height: " + var1);
         return false;
      }
   }

   private boolean isValidFixed(String var1) {
      if (this.tiles == null) {
         Config.warn("Tiles not defined: " + var1);
         return false;
      } else if (this.tiles.length != 1) {
         Config.warn("Number of tiles should be 1 for method: fixed.");
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidTop(String var1) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("66");
      }

      if (this.tiles.length != 1) {
         Config.warn("Invalid tiles, must be exactly 1: " + var1);
         return false;
      } else {
         return true;
      }
   }

   public void updateIcons(TextureMap var1) {
      if (this.matchTiles != null) {
         this.matchTileIcons = registerIcons(this.matchTiles, var1);
      }

      if (this.tiles != null) {
         this.tileIcons = registerIcons(this.tiles, var1);
      }
   }

   private static TextureAtlasSprite[] registerIcons(String[] var0, TextureMap var1) {
      if (var0 == null) {
         return null;
      }

      ArrayList var5 = new ArrayList();

      for (int var6 = 0; var6 < var0.length; var6++) {
         String var7 = var0[var6];
         ResourceLocation var8 = new ResourceLocation(var7);
         String var9 = var8.getResourceDomain();
         String var10 = var8.getResourcePath();
         if (!var10.contains("/")) {
            var10 = "textures/blocks/" + var10;
         }

         String var11 = var10 + ".png";
         ResourceLocation var12 = new ResourceLocation(var9, var11);
         boolean var13 = Config.hasResource(var12);
         if (!var13) {
            Config.warn("File not found: " + var11);
         }

         String var14 = "textures/";
         String var15 = var10;
         if (var10.startsWith(var14)) {
            var15 = var10.substring(var14.length());
         }

         ResourceLocation var16 = new ResourceLocation(var9, var15);
         TextureAtlasSprite var17 = var1.registerSprite(var16);
         var5.add(var17);
      }

      return (net.minecraft.client.renderer.texture.TextureAtlasSprite[])(var5.toArray(new TextureAtlasSprite[var5.size()]));
   }

   public boolean matchesBlockId(int var1) {
      return Matches.blockId(var1, this.matchBlocks);
   }

   public boolean matchesBlock(int var1, int var2) {
      return !Matches.block(var1, var2, this.matchBlocks) ? false : Matches.metadata(var2, this.metadatas);
   }

   public boolean matchesIcon(TextureAtlasSprite var1) {
      return Matches.sprite(var1, this.matchTileIcons);
   }

   @Override
   public String toString() {
      return "CTM name: "
         + this.name
         + ", basePath: "
         + this.basePath
         + ", matchBlocks: "
         + Config.arrayToString(this.matchBlocks)
         + ", matchTiles: "
         + Config.arrayToString(this.matchTiles);
   }

   public boolean matchesBiome(BiomeGenBase var1) {
      return Matches.biome(var1, this.biomes);
   }

   public int getMetadataMax() {
      int var4 = -1;
      var4 = this.getMax(this.metadatas, var4);
      if (this.matchBlocks != null) {
         for (int var5 = 0; var5 < this.matchBlocks.length; var5++) {
            MatchBlock var6 = this.matchBlocks[var5];
            var4 = this.getMax(var6.getMetadatas(), var4);
         }
      }

      return var4;
   }

   private int getMax(int[] var1, int var2) {
      if (var1 == null) {
         return var2;
      }

      for (int var6 = 0; var6 < var1.length; var6++) {
         int var7 = var1[var6];
         if (var7 > var2) {
            var2 = var7;
         }
      }

      return var2;
   }

   static {
      long var11 = T ^ 44114622247622L;
      long var10000 = var11 ^ 6403952073836237468L;
      var10000 = var11 ^ 8155859250628623345L;
      var10000 = var11 ^ 2887310990623235014L;
      var10000 = var11 ^ 6913467948314459574L;
      var10000 = var11 ^ 8430265563071566890L;
      var10000 = var11 ^ 5869525808785808358L;
      var10000 = var11 ^ 1428528093700250010L;
      var10000 = var11 ^ 2887310990623235014L;
      var10000 = var11 ^ 2887310990623235014L;
      var10000 = var11 ^ 592418725322477608L;
      var10000 = var11 ^ 592418725322477608L;
      var10000 = var11 ^ 2834066858742771059L;
      var10000 = var11 ^ 576502711948925390L;
      var10000 = var11 ^ 979275560414158383L;
      var10000 = var11 ^ 1859320588349717954L;
      var10000 = var11 ^ 4085068039272170867L;
      var10000 = var11 ^ 592418725322477608L;
      var10000 = var11 ^ 576502711948925390L;
      var10000 = var11 ^ 6913467948314459574L;
      var10000 = var11 ^ 1859320588349717954L;
      var10000 = var11 ^ 2887310990623235014L;
      var10000 = var11 ^ 5869525808785808358L;
   }
}
