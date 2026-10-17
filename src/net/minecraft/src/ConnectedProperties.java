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
   public int minHeight = 0;
   public int maxHeight = 1024;
   public int renderPass = 0;
   public boolean innerSeams = false;
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

   public ConnectedProperties(Properties props, String path) {
      ConnectedParser connectedParser = new ConnectedParser("ConnectedTextures");
      this.name = connectedParser.parseName(path);
      this.basePath = connectedParser.parseBasePath(path);
      this.matchBlocks = connectedParser.parseMatchBlocks(props.getProperty("matchBlocks"));
      this.metadatas = connectedParser.parseIntList(props.getProperty("metadata"));
      this.matchTiles = this.parseMatchTiles(props.getProperty("matchTiles"));
      this.method = parseMethod(props.getProperty("method"));
      this.tiles = this.parseTileNames(props.getProperty("tiles"));
      this.connect = parseConnect(props.getProperty("connect"));
      this.faces = parseFaces(props.getProperty("faces"));
      this.biomes = connectedParser.y(props.getProperty("biomes"));
      this.minHeight = connectedParser.parseIntOrDefault(props.getProperty("minHeight"), -1);
      this.maxHeight = connectedParser.parseIntOrDefault(props.getProperty("maxHeight"), 1024);
      this.renderPass = connectedParser.parseInt(props.getProperty("renderPass"));
      this.innerSeams = ConnectedParser.parseBoolean(props.getProperty("innerSeams"));
      this.width = connectedParser.parseInt(props.getProperty("width"));
      this.height = connectedParser.parseInt(props.getProperty("height"));
      this.weights = connectedParser.parseIntList(props.getProperty("weights"));
      this.symmetry = parseSymmetry(props.getProperty("symmetry"));
   }

   private String[] parseMatchTiles(String str) {
      if (str == null) {
         return null;
      }

      String[] strings = Config.tokenize(str, " ");

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         if (string.endsWith(".png")) {
            string = string.substring(0, string.length() - 4);
         }

         string = TextureUtils.fixResourcePath(string, this.basePath);
         strings[i] = string;
      }

      return strings;
   }

   private String[] parseTileNames(String str) {
      if (str == null) {
         return null;
      }

      ArrayList arrayList = new ArrayList();
      String[] strings = Config.tokenize(str, " ,");

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         if (string.contains("-")) {
            String[] strings2 = Config.tokenize(string, "-");
            if (strings2.length == 2) {
               int j = Config.parseInt(strings2[0], -1);
               int k = Config.parseInt(strings2[1], -1);
               if (j >= 0 && k >= 0) {
                  if (j > k) {
                     Config.warn("Invalid interval: " + string + ", when parsing: " + str);
                     continue;
                  }

                  for (int l = j; l <= k; l++) {
                     arrayList.add(String.valueOf(l));
                  }
                  continue;
               }
            }
         }

         arrayList.add(string);
      }

      String[] strings3 = (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));

      for (int m = 0; m < strings3.length; m++) {
         String string2 = strings3[m];
         string2 = TextureUtils.fixResourcePath(string2, this.basePath);
         if (!string2.startsWith(this.basePath) && !string2.startsWith("textures/") && !string2.startsWith("mcpatcher/")) {
            string2 = this.basePath + "/" + string2;
         }

         if (string2.endsWith(".png")) {
            string2 = string2.substring(0, string2.length() - 4);
         }

         String string3 = "textures/blocks/";
         if (string2.startsWith(string3)) {
            string2 = string2.substring(string3.length());
         }

         if (string2.startsWith("/")) {
            string2 = string2.substring(1);
         }

         strings3[m] = string2;
      }

      return strings3;
   }

   private static int parseSymmetry(String str) {
      if (str == null) {
         return 1;
      }

      if (str.equals("opposite")) {
         return 2;
      }

      if (str.equals("all")) {
         return 6;
      }

      Config.warn("Unknown symmetry: " + str);
      return 1;
   }

   private static int parseFaces(String str) {
      if (str == null) {
         return 63;
      }

      String[] strings = Config.tokenize(str, " ,");
      int i = 0;

      for (int j = 0; j < strings.length; j++) {
         String string = strings[j];
         int k = parseFace(string);
         i |= k;
      }

      return i;
   }

   private static int parseFace(String str) {
      str = str.toLowerCase();
      if (str.equals("bottom") || str.equals("down")) {
         return 1;
      }

      if (str.equals("top") || str.equals("up")) {
         return 2;
      }

      if (str.equals("north")) {
         return 4;
      }

      if (str.equals("south")) {
         return 8;
      }

      if (str.equals("east")) {
         return 32;
      }

      if (str.equals("west")) {
         return 16;
      }

      if (str.equals("sides")) {
         return 60;
      }

      if (str.equals("all")) {
         return 63;
      }

      Config.warn("Unknown face: " + str);
      return 128;
   }

   private static int parseConnect(String str) {
      if (str == null) {
         return 0;
      }

      if (str.equals("block")) {
         return 1;
      }

      if (str.equals("tile")) {
         return 2;
      }

      if (str.equals("material")) {
         return 3;
      }

      Config.warn("Unknown connect: " + str);
      return 128;
   }

   public static IProperty getProperty(String key, Collection properties) {
      for (Object object : properties) {
         if (key.equals(((IProperty)object).getName())) {
            return (IProperty)object;
         }
      }

      return null;
   }

   private static int parseMethod(String str) {
      if (str == null) {
         return 1;
      } else if (str.equals("ctm") || str.equals("glass")) {
         return 1;
      } else if (str.equals("horizontal") || str.equals("bookshelf")) {
         return 2;
      } else if (str.equals("vertical")) {
         return 6;
      } else if (str.equals("top")) {
         return 3;
      } else if (str.equals("random")) {
         return 4;
      } else if (str.equals("repeat")) {
         return 5;
      } else if (str.equals("fixed")) {
         return 7;
      } else if (str.equals("horizontal+vertical") || str.equals("h+v")) {
         return 8;
      } else if (!str.equals("vertical+horizontal") && !str.equals("v+h")) {
         Config.warn("Unknown method: " + str);
         return 0;
      } else {
         return 9;
      }
   }

   public boolean isValid(String path) {
      if (this.name == null || this.name.length() <= 0) {
         Config.warn("No name found: " + path);
         return false;
      }

      if (this.basePath == null) {
         Config.warn("No base path found: " + path);
         return false;
      }

      if (this.matchBlocks == null) {
         this.matchBlocks = this.detectMatchBlocks();
      }

      if (this.matchTiles == null && this.matchBlocks == null) {
         this.matchTiles = this.detectMatchTiles();
      }

      if (this.matchBlocks == null && this.matchTiles == null) {
         Config.warn("No matchBlocks or matchTiles specified: " + path);
         return false;
      }

      if (this.method == 0) {
         Config.warn("No method: " + path);
         return false;
      }

      if (this.tiles != null && this.tiles.length > 0) {
         if (this.connect == 0) {
            this.connect = this.detectConnect();
         }

         if (this.connect == 128) {
            Config.warn("Invalid connect in: " + path);
            return false;
         }

         if (this.renderPass > 0) {
            Config.warn("Render pass not supported: " + this.renderPass);
            return false;
         }

         if ((this.faces & 128) != 0) {
            Config.warn("Invalid faces in: " + path);
            return false;
         }

         if ((this.symmetry & 128) != 0) {
            Config.warn("Invalid symmetry in: " + path);
            return false;
         }

         switch (this.method) {
            case 1:
               return this.isValidCtm(path);
            case 2:
               return this.isValidHorizontal(path);
            case 3:
               return this.isValidTop(path);
            case 4:
               return this.isValidRandom(path);
            case 5:
               return this.isValidRepeat(path);
            case 6:
               return this.isValidVertical(path);
            case 7:
               return this.isValidFixed(path);
            case 8:
               return this.isValidHorizontalVertical(path);
            case 9:
               return this.isValidVerticalHorizontal(path);
            default:
               Config.warn("Unknown method: " + path);
               return false;
         }
      } else {
         Config.warn("No tiles specified: " + path);
         return false;
      }
   }

   private int detectConnect() {
      return this.matchBlocks != null ? 1 : (this.matchTiles != null ? 2 : 128);
   }

   private MatchBlock[] detectMatchBlocks() {
      int[] ints = this.detectMatchBlockIds();
      if (ints == null) {
         return null;
      }

      MatchBlock[] matchBlocks = new MatchBlock[ints.length];

      for (int i = 0; i < matchBlocks.length; i++) {
         matchBlocks[i] = new MatchBlock(ints[i]);
      }

      return matchBlocks;
   }

   private int[] detectMatchBlockIds() {
      if (!this.name.startsWith("block")) {
         return null;
      }

      int i = "block".length();

      int j;
      for (j = i; j < this.name.length(); j++) {
         char c = this.name.charAt(j);
         if (c < '0' || c > '9') {
            break;
         }
      }

      if (j == i) {
         return null;
      }

      String string = this.name.substring(i, j);
      int k = Config.parseInt(string, -1);
      return k < 0 ? null : new int[]{k};
   }

   private String[] detectMatchTiles() {
      TextureAtlasSprite textureAtlasSprite = getIcon(this.name);
      return textureAtlasSprite == null ? null : new String[]{this.name};
   }

   private static TextureAtlasSprite getIcon(String iconName) {
      TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
      TextureAtlasSprite textureAtlasSprite = textureMap.getSpriteSafe(iconName);
      return textureAtlasSprite != null ? textureAtlasSprite : textureMap.getSpriteSafe("blocks/" + iconName);
   }

   private boolean isValidCtm(String path) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("0-11 16-27 32-43 48-58");
      }

      if (this.tiles.length < 47) {
         Config.warn("Invalid tiles, must be at least 47: " + path);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidHorizontal(String path) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("12-15");
      }

      if (this.tiles.length != 4) {
         Config.warn("Invalid tiles, must be exactly 4: " + path);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidVertical(String path) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for vertical: " + path);
         return false;
      } else if (this.tiles.length != 4) {
         Config.warn("Invalid tiles, must be exactly 4: " + path);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidHorizontalVertical(String path) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for horizontal+vertical: " + path);
         return false;
      } else if (this.tiles.length != 7) {
         Config.warn("Invalid tiles, must be exactly 7: " + path);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidVerticalHorizontal(String path) {
      if (this.tiles == null) {
         Config.warn("No tiles defined for vertical+horizontal: " + path);
         return false;
      } else if (this.tiles.length != 7) {
         Config.warn("Invalid tiles, must be exactly 7: " + path);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidRandom(String path) {
      if (this.tiles != null && this.tiles.length > 0) {
         if (this.weights != null) {
            if (this.weights.length > this.tiles.length) {
               Config.warn("More weights defined than tiles, trimming weights: " + path);
               int[] ints = new int[this.tiles.length];
               System.arraycopy(this.weights, 0, ints, 0, ints.length);
               this.weights = ints;
            }

            if (this.weights.length < this.tiles.length) {
               Config.warn("Less weights defined than tiles, expanding weights: " + path);
               int[] ints2 = new int[this.tiles.length];
               System.arraycopy(this.weights, 0, ints2, 0, this.weights.length);
               int i = MathUtils.getAverage(this.weights);

               for (int j = this.weights.length; j < ints2.length; j++) {
                  ints2[j] = i;
               }

               this.weights = ints2;
            }

            this.sumWeights = new int[this.weights.length];
            int k = 0;

            for (int l = 0; l < this.weights.length; l++) {
               k += this.weights[l];
               this.sumWeights[l] = k;
            }

            this.sumAllWeights = k;
            if (this.sumAllWeights <= 0) {
               Config.warn("Invalid sum of all weights: " + k);
               this.sumAllWeights = 1;
            }
         }

         return true;
      } else {
         Config.warn("Tiles not defined: " + path);
         return false;
      }
   }

   private boolean isValidRepeat(String path) {
      if (this.tiles == null) {
         Config.warn("Tiles not defined: " + path);
         return false;
      }

      if (this.width <= 0 || this.width > 16) {
         Config.warn("Invalid width: " + path);
         return false;
      }

      if (this.height > 0 && this.height <= 16) {
         if (this.tiles.length != this.width * this.height) {
            Config.warn("Number of tiles does not equal width x height: " + path);
            return false;
         } else {
            return true;
         }
      } else {
         Config.warn("Invalid height: " + path);
         return false;
      }
   }

   private boolean isValidFixed(String path) {
      if (this.tiles == null) {
         Config.warn("Tiles not defined: " + path);
         return false;
      } else if (this.tiles.length != 1) {
         Config.warn("Number of tiles should be 1 for method: fixed.");
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidTop(String path) {
      if (this.tiles == null) {
         this.tiles = this.parseTileNames("66");
      }

      if (this.tiles.length != 1) {
         Config.warn("Invalid tiles, must be exactly 1: " + path);
         return false;
      } else {
         return true;
      }
   }

   public void updateIcons(TextureMap textureMap) {
      if (this.matchTiles != null) {
         this.matchTileIcons = registerIcons(this.matchTiles, textureMap);
      }

      if (this.tiles != null) {
         this.tileIcons = registerIcons(this.tiles, textureMap);
      }
   }

   private static TextureAtlasSprite[] registerIcons(String[] tileNames, TextureMap textureMap) {
      if (tileNames == null) {
         return null;
      }

      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < tileNames.length; i++) {
         String string = tileNames[i];
         ResourceLocation resourceLocation = new ResourceLocation(string);
         String string2 = resourceLocation.getResourceDomain();
         String string3 = resourceLocation.getResourcePath();
         if (!string3.contains("/")) {
            string3 = "textures/blocks/" + string3;
         }

         String string4 = string3 + ".png";
         ResourceLocation resourceLocation2 = new ResourceLocation(string2, string4);
         boolean bl = Config.hasResource(resourceLocation2);
         if (!bl) {
            Config.warn("File not found: " + string4);
         }

         String string5 = "textures/";
         String string6 = string3;
         if (string3.startsWith(string5)) {
            string6 = string3.substring(string5.length());
         }

         ResourceLocation resourceLocation3 = new ResourceLocation(string2, string6);
         TextureAtlasSprite textureAtlasSprite = textureMap.registerSprite(resourceLocation3);
         arrayList.add(textureAtlasSprite);
      }

      return (net.minecraft.client.renderer.texture.TextureAtlasSprite[])(arrayList.toArray(new TextureAtlasSprite[arrayList.size()]));
   }

   public boolean matchesBlockId(int blockId) {
      return Matches.blockId(blockId, this.matchBlocks);
   }

   public boolean matchesBlock(int blockId, int metadata) {
      return !Matches.block(blockId, metadata, this.matchBlocks) ? false : Matches.metadata(metadata, this.metadatas);
   }

   public boolean matchesIcon(TextureAtlasSprite icon) {
      return Matches.sprite(icon, this.matchTileIcons);
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

   public boolean matchesBiome(BiomeGenBase biome) {
      return Matches.biome(biome, this.biomes);
   }

   public int getMetadataMax() {
      int i = -1;
      i = this.getMax(this.metadatas, i);
      if (this.matchBlocks != null) {
         for (int j = 0; j < this.matchBlocks.length; j++) {
            MatchBlock matchBlock = this.matchBlocks[j];
            i = this.getMax(matchBlock.getMetadatas(), i);
         }
      }

      return i;
   }

   private int getMax(int[] mds, int maxIn) {
      if (mds == null) {
         return maxIn;
      }

      for (int i = 0; i < mds.length; i++) {
         int j = mds[i];
         if (j > maxIn) {
            maxIn = j;
         }
      }

      return maxIn;
   }
}
