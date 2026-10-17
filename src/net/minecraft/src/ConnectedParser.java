package net.minecraft.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;

public class ConnectedParser {
   private String context = null;
   private static final MatchBlock[] NO_MATCH_BLOCKS = new MatchBlock[0];

   public ConnectedParser(String context) {
      this.context = context;
   }

   public String parseName(String path) {
      String string = path;
      int i = path.lastIndexOf(47);
      if (i >= 0) {
         string = path.substring(i + 1);
      }

      int j = string.lastIndexOf(46);
      if (j >= 0) {
         string = string.substring(0, j);
      }

      return string;
   }

   public String parseBasePath(String path) {
      int i = path.lastIndexOf(47);
      return i < 0 ? "" : path.substring(0, i);
   }

   public MatchBlock[] parseMatchBlocks(String propMatchBlocks) {
      if (propMatchBlocks == null) {
         return null;
      }

      ArrayList arrayList = new ArrayList();
      String[] strings = Config.tokenize(propMatchBlocks, " ");

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         MatchBlock[] matchBlocks = this.parseMatchBlock(string);
         if (matchBlocks == null) {
            return NO_MATCH_BLOCKS;
         }

         arrayList.addAll(Arrays.asList(matchBlocks));
      }

      return (net.minecraft.src.MatchBlock[])(arrayList.toArray(new MatchBlock[arrayList.size()]));
   }

   public MatchBlock[] parseMatchBlock(String blockStr) {
      if (blockStr == null) {
         return null;
      }

      blockStr = blockStr.trim();
      if (blockStr.length() <= 0) {
         return null;
      }

      String[] strings = Config.tokenize(blockStr, ":");
      String string = "minecraft";
      byte b = 0;
      if (strings.length > 1 && this.isFullBlockName(strings)) {
         string = strings[0];
         b = 1;
      } else {
         string = "minecraft";
         b = 0;
      }

      String string2 = strings[b];
      String[] strings2 = Arrays.copyOfRange(strings, b + 1, strings.length);
      Block[] blocks = this.parseBlockPart(string, string2);
      if (blocks == null) {
         return null;
      }

      MatchBlock[] matchBlocks = new MatchBlock[blocks.length];

      for (int i = 0; i < blocks.length; i++) {
         Block block = blocks[i];
         int j = Block.getIdFromBlock(block);
         int[] ints = null;
         if (strings2.length > 0) {
            ints = this.parseBlockMetadatas(block, strings2);
            if (ints == null) {
               return null;
            }
         }

         MatchBlock matchBlock = new MatchBlock(j, ints);
         matchBlocks[i] = matchBlock;
      }

      return matchBlocks;
   }

   public boolean isFullBlockName(String[] parts) {
      if (parts.length < 2) {
         return false;
      }

      String string = parts[1];
      return string.length() < 1 ? false : (this.startsWithDigit(string) ? false : !string.contains("="));
   }

   public boolean startsWithDigit(String str) {
      if (str == null) {
         return false;
      }

      if (str.length() < 1) {
         return false;
      }

      char c = str.charAt(0);
      return Character.isDigit(c);
   }

   public Block[] parseBlockPart(String domain, String blockPart) {
      if (this.startsWithDigit(blockPart)) {
         int[] ints = this.parseIntList(blockPart);
         if (ints == null) {
            return null;
         }

         Block[] blocks = new Block[ints.length];

         for (int i = 0; i < ints.length; i++) {
            int j = ints[i];
            Block block = Block.getBlockById(j);
            if (block == null) {
               this.warn("Block not found for id: " + j);
               return null;
            }

            blocks[i] = block;
         }

         return blocks;
      } else {
         String string = domain + ":" + blockPart;
         Block block2 = Block.getBlockFromName(string);
         if (block2 == null) {
            this.warn("Block not found for name: " + string);
            return null;
         } else {
            return new Block[]{block2};
         }
      }
   }

   public int[] parseBlockMetadatas(Block block, String[] params) {
      if (params.length <= 0) {
         return null;
      }

      String string = params[0];
      if (this.startsWithDigit(string)) {
         return this.parseIntList(string);
      }

      IBlockState iBlockState = block.getDefaultState();
      Collection collection = iBlockState.getPropertyNames();
      HashMap hashMap = new HashMap();

      for (int i = 0; i < params.length; i++) {
         String string2 = params[i];
         if (string2.length() > 0) {
            String[] strings = Config.tokenize(string2, "=");
            if (strings.length != 2) {
               this.warn("Invalid block property: " + string2);
               return null;
            }

            String string3 = strings[0];
            String string4 = strings[1];
            IProperty iProperty = ConnectedProperties.getProperty(string3, collection);
            if (iProperty == null) {
               this.warn("Property not found: " + string3 + ", block: " + block);
               return null;
            }

            List list = (List)hashMap.get(string3);
            if (list == null) {
               list = new ArrayList();
               hashMap.put(iProperty, list);
            }

            String[] strings2 = Config.tokenize(string4, ",");

            for (int j = 0; j < strings2.length; j++) {
               String string5 = strings2[j];
               Comparable comparable = parsePropertyValue(iProperty, string5);
               if (comparable == null) {
                  this.warn("Property value not found: " + string5 + ", property: " + string3 + ", block: " + block);
                  return null;
               }

               list.add(comparable);
            }
         }
      }

      if (hashMap.isEmpty()) {
         return null;
      }

      ArrayList arrayList = new ArrayList();

      for (int k = 0; k < 16; k++) {
         int l = k;

         try {
            IBlockState iBlockState2 = this.getStateFromMeta(block, l);
            if (this.matchState(iBlockState2, hashMap)) {
               arrayList.add(l);
            }
         } catch (IllegalArgumentException illegalArgumentException) {
         }
      }

      if (arrayList.size() == 16) {
         return null;
      }

      int[] ints = new int[arrayList.size()];

      for (int m = 0; m < ints.length; m++) {
         ints[m] = (Integer)arrayList.get(m);
      }

      return ints;
   }

   private IBlockState getStateFromMeta(Block block, int md) {
      try {
         IBlockState iBlockState = block.getStateFromMeta(md);
         if (block == Blocks.double_plant && md > 7) {
            IBlockState iBlockState2 = block.getStateFromMeta(md & 7);
            iBlockState = iBlockState.withProperty(BlockDoublePlant.VARIANT, iBlockState2.getValue(BlockDoublePlant.VARIANT));
         }

         return iBlockState;
      } catch (IllegalArgumentException illegalArgumentException) {
         return block.getDefaultState();
      }
   }

   public static Comparable parsePropertyValue(IProperty prop, String valStr) {
      Class class2 = prop.getValueClass();
      Comparable comparable = parseValue(valStr, class2);
      if (comparable == null) {
         Collection collection = prop.getAllowedValues();
         comparable = getPropertyValue(valStr, collection);
      }

      return comparable;
   }

   public static Comparable getPropertyValue(String value, Collection propertyValues) {
      for (Object object : propertyValues) {
         if (String.valueOf(object).equals(value)) {
            return (Comparable)object;
         }
      }

      return null;
   }

   public static Comparable parseValue(String str, Class cls) {
      return (Comparable)(
         cls == String.class
            ? str
            : (
               cls == Boolean.class
                  ? Boolean.valueOf(str)
                  : cls == Float.class
                     ? Float.valueOf(str).floatValue()
                     : (
                        cls == Double.class
                           ? Double.valueOf(str)
                           : (cls == Integer.class ? Integer.valueOf(str).intValue() : cls == Long.class ? Long.valueOf(str) : null)
                     )
            )
      );
   }

   public boolean matchState(IBlockState bs, Map<IProperty, List<Comparable>> mapPropValues) {
      for (IProperty iProperty : mapPropValues.keySet()) {
         List list = (List)mapPropValues.get(iProperty);
         Comparable comparable = bs.getValue(iProperty);
         if (comparable == null) {
            return false;
         }

         if (!list.contains(comparable)) {
            return false;
         }
      }

      return true;
   }

   public BiomeGenBase[] y(String string) {
      if (string == null) {
         return null;
      }

      String[] strings = Config.tokenize(string, " ");
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < strings.length; i++) {
         String string2 = strings[i];
         BiomeGenBase biomeGenBase = this.findBiome(string2);
         if (biomeGenBase == null) {
            this.warn("Biome not found: " + string2);
         } else {
            arrayList.add(biomeGenBase);
         }
      }

      return (net.minecraft.world.biome.BiomeGenBase[])(arrayList.toArray(new BiomeGenBase[arrayList.size()]));
   }

   public BiomeGenBase findBiome(String biomeName) {
      biomeName = biomeName.toLowerCase();
      if (biomeName.equals("nether")) {
         return BiomeGenBase.hell;
      }

      BiomeGenBase[] biomeGenBases = BiomeGenBase.getBiomeGenArray();

      for (int i = 0; i < biomeGenBases.length; i++) {
         BiomeGenBase biomeGenBase = biomeGenBases[i];
         if (biomeGenBase != null) {
            String string = biomeGenBase.biomeName.replace(" ", "").toLowerCase();
            if (string.equals(biomeName)) {
               return biomeGenBase;
            }
         }
      }

      return null;
   }

   public int parseInt(String str) {
      if (str == null) {
         return -1;
      }

      int i = Config.parseInt(str, -1);
      if (i < 0) {
         this.warn("Invalid number: " + str);
      }

      return i;
   }

   public int parseIntOrDefault(String string, int i) {
      if (string == null) {
         return i;
      } else {
         int j = Config.parseInt(string, -1);
         if (j < 0) {
            this.warn("Invalid number: " + string);
            return i;
         } else {
            return j;
         }
      }
   }

   public int[] parseIntList(String str) {
      if (str == null) {
         return null;
      }

      ArrayList arrayList = new ArrayList();
      String[] strings = Config.tokenize(str, " ,");

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         if (string.contains("-")) {
            String[] strings2 = Config.tokenize(string, "-");
            if (strings2.length != 2) {
               this.warn("Invalid interval: " + string + ", when parsing: " + str);
            } else {
               int j = Config.parseInt(strings2[0], -1);
               int k = Config.parseInt(strings2[1], -1);
               if (j >= 0 && k >= 0 && j <= k) {
                  for (int l = j; l <= k; l++) {
                     arrayList.add(l);
                  }
               } else {
                  this.warn("Invalid interval: " + string + ", when parsing: " + str);
               }
            }
         } else {
            int m = Config.parseInt(string, -1);
            if (m < 0) {
               this.warn("Invalid number: " + string + ", when parsing: " + str);
            } else {
               arrayList.add(m);
            }
         }
      }

      int[] ints = new int[arrayList.size()];

      for (int n = 0; n < ints.length; n++) {
         ints[n] = (Integer)arrayList.get(n);
      }

      return ints;
   }

   public boolean[] parseFaces(String str, boolean[] defVal) {
      if (str == null) {
         return defVal;
      }

      EnumSet enumSet = EnumSet.allOf(EnumFacing.class);
      String[] strings = Config.tokenize(str, " ,");

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         if (string.equals("sides")) {
            enumSet.add(EnumFacing.NORTH);
            enumSet.add(EnumFacing.SOUTH);
            enumSet.add(EnumFacing.WEST);
            enumSet.add(EnumFacing.EAST);
         } else if (string.equals("all")) {
            enumSet.addAll(Arrays.asList(EnumFacing.VALUES));
         } else {
            EnumFacing enumFacing = this.parseFace(string);
            if (enumFacing != null) {
               enumSet.add(enumFacing);
            }
         }
      }

      boolean[] booleans = new boolean[EnumFacing.VALUES.length];

      for (int j = 0; j < booleans.length; j++) {
         booleans[j] = enumSet.contains(EnumFacing.VALUES[j]);
      }

      return booleans;
   }

   public EnumFacing parseFace(String str) {
      str = str.toLowerCase();
      if (str.equals("bottom") || str.equals("down")) {
         return EnumFacing.DOWN;
      }

      if (str.equals("top") || str.equals("up")) {
         return EnumFacing.UP;
      }

      if (str.equals("north")) {
         return EnumFacing.NORTH;
      }

      if (str.equals("south")) {
         return EnumFacing.SOUTH;
      }

      if (str.equals("east")) {
         return EnumFacing.EAST;
      }

      if (str.equals("west")) {
         return EnumFacing.WEST;
      }

      Config.warn("Unknown face: " + str);
      return null;
   }

   public void w_void(String string) {
      Config.dbg("" + this.context + ": " + string);
   }

   public void warn(String str) {
      Config.warn("" + this.context + ": " + str);
   }

   public RangeListInt parseRangeListInt(String str) {
      if (str == null) {
         return null;
      }

      RangeListInt rangeListInt = new RangeListInt();
      String[] strings = Config.tokenize(str, " ,");

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         RangeInt rangeInt = this.parseRangeInt(string);
         if (rangeInt == null) {
            return null;
         }

         rangeListInt.addRange(rangeInt);
      }

      return rangeListInt;
   }

   private RangeInt parseRangeInt(String str) {
      if (str == null) {
         return null;
      }

      if (str.indexOf(45) >= 0) {
         String[] strings = Config.tokenize(str, "-");
         if (strings.length != 2) {
            this.warn("Invalid range: " + str);
            return null;
         }

         int i = Config.parseInt(strings[0], -1);
         int j = Config.parseInt(strings[1], -1);
         if (i >= 0 && j >= 0) {
            return new RangeInt(i, j);
         }

         this.warn("Invalid range: " + str);
         return null;
      } else {
         int k = Config.parseInt(str, -1);
         if (k < 0) {
            this.warn("Invalid integer: " + str);
            return null;
         } else {
            return new RangeInt(k, k);
         }
      }
   }

   public static boolean parseBoolean(String string) {
      try {
         return string == null ? false : string.toLowerCase().equals("true");
      } catch (Exception exception) {
         return false;
      }
   }

   public static int parseColor(String str, int defVal) {
      if (str == null) {
         return defVal;
      }

      str = str.trim();

      try {
         return Integer.parseInt(str, 16) & 16777215;
      } catch (NumberFormatException numberFormatException) {
         return defVal;
      }
   }
}
