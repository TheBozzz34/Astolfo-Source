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
   private static final MatchBlock[] C = new MatchBlock[0];
   private static final long a = 11636544326233L;

   public ConnectedParser(String var1) {
      this.context = var1;
   }

   public String parseName(String var1) {
      String var5 = var1;
      int var6 = var1.lastIndexOf(47);
      if (var6 >= 0) {
         var5 = var1.substring(var6 + 1);
      }

      int var7 = var5.lastIndexOf(46);
      if (var7 >= 0) {
         var5 = var5.substring(0, var7);
      }

      return var5;
   }

   public String parseBasePath(String var1) {
      int var4 = var1.lastIndexOf(47);
      return var4 < 0 ? "" : var1.substring(0, var4);
   }

   public MatchBlock[] t(String var1) {
      if (var1 == null) {
         return null;
      }

      ArrayList var5 = new ArrayList();
      String[] var6 = Config.tokenize(var1, " ");

      for (int var7 = 0; var7 < var6.length; var7++) {
         String var8 = var6[var7];
         MatchBlock[] var9 = this.g(var8);
         if (var9 == null) {
            return C;
         }

         var5.addAll(Arrays.asList(var9));
      }

      return (net.minecraft.src.MatchBlock[])(var5.toArray(new MatchBlock[var5.size()]));
   }

   public MatchBlock[] g(String var1) {
      if (var1 == null) {
         return null;
      }

      var1 = var1.trim();
      if (var1.length() <= 0) {
         return null;
      }

      String[] var5 = Config.tokenize(var1, ":");
      String var6 = "minecraft";
      byte var7 = 0;
      if (var5.length > 1 && this.isFullBlockName(var5)) {
         var6 = var5[0];
         var7 = 1;
      } else {
         var6 = "minecraft";
         var7 = 0;
      }

      String var8 = var5[var7];
      String[] var9 = Arrays.copyOfRange(var5, var7 + 1, var5.length);
      Block[] var10 = this.parseBlockPart(var6, var8);
      if (var10 == null) {
         return null;
      }

      MatchBlock[] var11 = new MatchBlock[var10.length];

      for (int var12 = 0; var12 < var10.length; var12++) {
         Block var13 = var10[var12];
         int var14 = Block.getIdFromBlock(var13);
         int[] var15 = null;
         if (var9.length > 0) {
            var15 = this.parseBlockMetadatas(var13, var9);
            if (var15 == null) {
               return null;
            }
         }

         MatchBlock var16 = new MatchBlock(var14, var15);
         var11[var12] = var16;
      }

      return var11;
   }

   public boolean isFullBlockName(String[] var1) {
      if (var1.length < 2) {
         return false;
      }

      String var5 = var1[1];
      return var5.length() < 1 ? false : (this.startsWithDigit(var5) ? false : !var5.contains("="));
   }

   public boolean startsWithDigit(String var1) {
      if (var1 == null) {
         return false;
      }

      if (var1.length() < 1) {
         return false;
      }

      char var5 = var1.charAt(0);
      return Character.isDigit(var5);
   }

   public Block[] parseBlockPart(String var1, String var2) {
      if (this.startsWithDigit(var2)) {
         int[] var11 = this.parseIntList(var2);
         if (var11 == null) {
            return null;
         }

         Block[] var12 = new Block[var11.length];

         for (int var8 = 0; var8 < var11.length; var8++) {
            int var9 = var11[var8];
            Block var10 = Block.getBlockById(var9);
            if (var10 == null) {
               this.c("Block not found for id: " + var9);
               return null;
            }

            var12[var8] = var10;
         }

         return var12;
      } else {
         String var6 = var1 + ":" + var2;
         Block var7 = Block.getBlockFromName(var6);
         if (var7 == null) {
            this.c("Block not found for name: " + var6);
            return null;
         } else {
            return new Block[]{var7};
         }
      }
   }

   public int[] parseBlockMetadatas(Block var1, String[] var2) {
      if (var2.length <= 0) {
         return null;
      }

      String var6 = var2[0];
      if (this.startsWithDigit(var6)) {
         return this.parseIntList(var6);
      }

      IBlockState var7 = var1.getDefaultState();
      Collection var8 = var7.getPropertyNames();
      HashMap var9 = new HashMap();

      for (int var10 = 0; var10 < var2.length; var10++) {
         String var11 = var2[var10];
         if (var11.length() > 0) {
            String[] var12 = Config.tokenize(var11, "=");
            if (var12.length != 2) {
               this.c("Invalid block property: " + var11);
               return null;
            }

            String var13 = var12[0];
            String var14 = var12[1];
            IProperty var15 = ConnectedProperties.getProperty(var13, var8);
            if (var15 == null) {
               this.c("Property not found: " + var13 + ", block: " + var1);
               return null;
            }

            List var16 = (List)var9.get(var13);
            if (var16 == null) {
               var16 = new ArrayList();
               var9.put(var15, var16);
            }

            String[] var17 = Config.tokenize(var14, ",");

            for (int var18 = 0; var18 < var17.length; var18++) {
               String var19 = var17[var18];
               Comparable var20 = parsePropertyValue(var15, var19);
               if (var20 == null) {
                  this.c("Property value not found: " + var19 + ", property: " + var13 + ", block: " + var1);
                  return null;
               }

               var16.add(var20);
            }
         }
      }

      if (var9.isEmpty()) {
         return null;
      }

      ArrayList var22 = new ArrayList();

      for (int var23 = 0; var23 < 16; var23++) {
         int var25 = var23;

         try {
            IBlockState var27 = this.getStateFromMeta(var1, var25);
            if (this.matchState(var27, var9)) {
               var22.add(var25);
            }
         } catch (IllegalArgumentException var21) {
         }
      }

      if (var22.size() == 16) {
         return null;
      }

      int[] var24 = new int[var22.size()];

      for (int var26 = 0; var26 < var24.length; var26++) {
         var24[var26] = (Integer)var22.get(var26);
      }

      return var24;
   }

   private IBlockState getStateFromMeta(Block var1, int var2) {
      try {
         IBlockState var5 = var1.getStateFromMeta(var2);
         if (var1 == Blocks.double_plant && var2 > 7) {
            IBlockState var6 = var1.getStateFromMeta(var2 & 7);
            var5 = var5.withProperty(BlockDoublePlant.VARIANT, var6.getValue(BlockDoublePlant.VARIANT));
         }

         return var5;
      } catch (IllegalArgumentException var7) {
         return var1.getDefaultState();
      }
   }

   public static Comparable parsePropertyValue(IProperty var0, String var1) {
      Class var5 = var0.getValueClass();
      Comparable var6 = parseValue(var1, var5);
      if (var6 == null) {
         Collection var7 = var0.getAllowedValues();
         var6 = getPropertyValue(var1, var7);
      }

      return var6;
   }

   public static Comparable getPropertyValue(String var0, Collection var1) {
      for (Object var6 : var1) {
         if (String.valueOf(var6).equals(var0)) {
            return (Comparable)var6;
         }
      }

      return null;
   }

   public static Comparable parseValue(String var0, Class var1) {
      return (Comparable)(
         var1 == String.class
            ? var0
            : (
               var1 == Boolean.class
                  ? Boolean.valueOf(var0)
                  : var1 == Float.class
                     ? Float.valueOf(var0).floatValue()
                     : (
                        var1 == Double.class
                           ? Double.valueOf(var0)
                           : (var1 == Integer.class ? Integer.valueOf(var0).intValue() : var1 == Long.class ? Long.valueOf(var0) : null)
                     )
            )
      );
   }

   public boolean matchState(IBlockState var1, Map<IProperty, List<Comparable>> var2) {
      for (IProperty var7 : var2.keySet()) {
         List var8 = (List)var2.get(var7);
         Comparable var9 = var1.getValue(var7);
         if (var9 == null) {
            return false;
         }

         if (!var8.contains(var9)) {
            return false;
         }
      }

      return true;
   }

   public BiomeGenBase[] y(String var1) {
      if (var1 == null) {
         return null;
      }

      String[] var5 = Config.tokenize(var1, " ");
      ArrayList var6 = new ArrayList();

      for (int var7 = 0; var7 < var5.length; var7++) {
         String var8 = var5[var7];
         BiomeGenBase var9 = this.I(var8);
         if (var9 == null) {
            this.c("Biome not found: " + var8);
         } else {
            var6.add(var9);
         }
      }

      return (net.minecraft.world.biome.BiomeGenBase[])(var6.toArray(new BiomeGenBase[var6.size()]));
   }

   public BiomeGenBase I(String var1) {
      var1 = var1.toLowerCase();
      if (var1.equals("nether")) {
         return BiomeGenBase.hell;
      }

      BiomeGenBase[] var5 = BiomeGenBase.getBiomeGenArray();

      for (int var6 = 0; var6 < var5.length; var6++) {
         BiomeGenBase var7 = var5[var6];
         if (var7 != null) {
            String var8 = var7.biomeName.replace(" ", "").toLowerCase();
            if (var8.equals(var1)) {
               return var7;
            }
         }
      }

      return null;
   }

   public int Y(String var1) {
      if (var1 == null) {
         return -1;
      }

      int var5 = Config.parseInt(var1, -1);
      if (var5 < 0) {
         this.c("Invalid number: " + var1);
      }

      return var5;
   }

   public int O(String var1, int var2) {
      if (var1 == null) {
         return var2;
      } else {
         int var6 = Config.parseInt(var1, -1);
         if (var6 < 0) {
            this.c("Invalid number: " + var1);
            return var2;
         } else {
            return var6;
         }
      }
   }

   public int[] parseIntList(String var1) {
      if (var1 == null) {
         return null;
      }

      ArrayList var5 = new ArrayList();
      String[] var6 = Config.tokenize(var1, " ,");

      for (int var7 = 0; var7 < var6.length; var7++) {
         String var8 = var6[var7];
         if (var8.contains("-")) {
            String[] var15 = Config.tokenize(var8, "-");
            if (var15.length != 2) {
               this.c("Invalid interval: " + var8 + ", when parsing: " + var1);
            } else {
               int var10 = Config.parseInt(var15[0], -1);
               int var11 = Config.parseInt(var15[1], -1);
               if (var10 >= 0 && var11 >= 0 && var10 <= var11) {
                  for (int var12 = var10; var12 <= var11; var12++) {
                     var5.add(var12);
                  }
               } else {
                  this.c("Invalid interval: " + var8 + ", when parsing: " + var1);
               }
            }
         } else {
            int var9 = Config.parseInt(var8, -1);
            if (var9 < 0) {
               this.c("Invalid number: " + var8 + ", when parsing: " + var1);
            } else {
               var5.add(var9);
            }
         }
      }

      int[] var13 = new int[var5.size()];

      for (int var14 = 0; var14 < var13.length; var14++) {
         var13[var14] = (Integer)var5.get(var14);
      }

      return var13;
   }

   public boolean[] parseFaces(String var1, boolean[] var2) {
      if (var1 == null) {
         return var2;
      }

      EnumSet var6 = EnumSet.allOf(EnumFacing.class);
      String[] var7 = Config.tokenize(var1, " ,");

      for (int var8 = 0; var8 < var7.length; var8++) {
         String var9 = var7[var8];
         if (var9.equals("sides")) {
            var6.add(EnumFacing.NORTH);
            var6.add(EnumFacing.SOUTH);
            var6.add(EnumFacing.WEST);
            var6.add(EnumFacing.EAST);
         } else if (var9.equals("all")) {
            var6.addAll(Arrays.asList(EnumFacing.VALUES));
         } else {
            EnumFacing var10 = this.p(var9);
            if (var10 != null) {
               var6.add(var10);
            }
         }
      }

      boolean[] var11 = new boolean[EnumFacing.VALUES.length];

      for (int var12 = 0; var12 < var11.length; var12++) {
         var11[var12] = var6.contains(EnumFacing.VALUES[var12]);
      }

      return var11;
   }

   public EnumFacing p(String var1) {
      var1 = var1.toLowerCase();
      if (var1.equals("bottom") || var1.equals("down")) {
         return EnumFacing.DOWN;
      }

      if (var1.equals("top") || var1.equals("up")) {
         return EnumFacing.UP;
      }

      if (var1.equals("north")) {
         return EnumFacing.NORTH;
      }

      if (var1.equals("south")) {
         return EnumFacing.SOUTH;
      }

      if (var1.equals("east")) {
         return EnumFacing.EAST;
      }

      if (var1.equals("west")) {
         return EnumFacing.WEST;
      }

      Config.warn("Unknown face: " + var1);
      return null;
   }

   public void w_void(String var1) {
      Config.dbg("" + this.context + ": " + var1);
   }

   public void c(String var1) {
      Config.warn("" + this.context + ": " + var1);
   }

   public RangeListInt F(String var1) {
      if (var1 == null) {
         return null;
      }

      RangeListInt var5 = new RangeListInt();
      String[] var6 = Config.tokenize(var1, " ,");

      for (int var7 = 0; var7 < var6.length; var7++) {
         String var8 = var6[var7];
         RangeInt var9 = this.w(var8);
         if (var9 == null) {
            return null;
         }

         var5.addRange(var9);
      }

      return var5;
   }

   private RangeInt w(String var1) {
      if (var1 == null) {
         return null;
      }

      if (var1.indexOf(45) >= 0) {
         String[] var8 = Config.tokenize(var1, "-");
         if (var8.length != 2) {
            this.c("Invalid range: " + var1);
            return null;
         }

         int var6 = Config.parseInt(var8[0], -1);
         int var7 = Config.parseInt(var8[1], -1);
         if (var6 >= 0 && var7 >= 0) {
            return new RangeInt(var6, var7);
         }

         this.c("Invalid range: " + var1);
         return null;
      } else {
         int var5 = Config.parseInt(var1, -1);
         if (var5 < 0) {
            this.c("Invalid integer: " + var1);
            return null;
         } else {
            return new RangeInt(var5, var5);
         }
      }
   }

   public static boolean parseBoolean(String var0) {
      try {
         return var0 == null ? false : var0.toLowerCase().equals("true");
      } catch (Exception var5) {
         return false;
      }
   }

   public static int w(String var0, int var1) {
      if (var0 == null) {
         return var1;
      }

      var0 = var0.trim();

      try {
         return Integer.parseInt(var0, 16) & 16777215;
      } catch (NumberFormatException var6) {
         return var1;
      }
   }
}
