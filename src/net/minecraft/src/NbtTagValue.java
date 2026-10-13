package net.minecraft.src;

import java.util.Arrays;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.StringEscapeUtils;

public class NbtTagValue {
   private String[] parents = null;
   private String b = null;
   private int q = 0;
   private String m = null;
   private static final int U = 2;
   private static final int R = 3;
   private static final int B = 4;
   private static final long a = 25088025412516L;

   public NbtTagValue(String var1, String var2) {
      String[] var6 = Config.tokenize(var1, ".");
      this.parents = Arrays.copyOfRange(var6, 0, var6.length - 1);
      this.b = var6[var6.length - 1];
      if (var2.startsWith("pattern:")) {
         this.q = 1;
         var2 = var2.substring("pattern:".length());
      } else if (var2.startsWith("ipattern:")) {
         this.q = 2;
         var2 = var2.substring("ipattern:".length()).toLowerCase();
      } else if (var2.startsWith("regex:")) {
         this.q = 3;
         var2 = var2.substring("regex:".length());
      } else if (var2.startsWith("iregex:")) {
         this.q = 4;
         var2 = var2.substring("iregex:".length()).toLowerCase();
      } else {
         this.q = 0;
      }

      var2 = StringEscapeUtils.unescapeJava(var2);
      this.m = var2;
   }

   public boolean S(NBTTagCompound var1) {
      if (var1 == null) {
         return false;
      }

      NBTBase var5 = var1;

      for (int var6 = 0; var6 < this.parents.length; var6++) {
         String var7 = this.parents[var6];
         var5 = getChildTag(var5, var7);
         if (var5 == null) {
            return false;
         }
      }

      if (this.b.equals("*")) {
         return this.T(var5);
      }

      var5 = getChildTag(var5, this.b);
      return var5 == null ? false : this.g(var5);
   }

   private boolean T(NBTBase var1) {
      if (var1 instanceof NBTTagCompound) {
         NBTTagCompound var5 = (NBTTagCompound)var1;

         for (String var7 : var5.getKeySet()) {
            NBTBase var8 = var5.getTag(var7);
            if (this.g(var8)) {
               return true;
            }
         }
      }

      if (var1 instanceof NBTTagList) {
         NBTTagList var9 = (NBTTagList)var1;
         int var10 = var9.tagCount();

         for (int var11 = 0; var11 < var10; var11++) {
            NBTBase var12 = var9.get(var11);
            if (this.g(var12)) {
               return true;
            }
         }
      }

      return false;
   }

   private static NBTBase getChildTag(NBTBase var0, String var1) {
      if (var0 instanceof NBTTagCompound) {
         NBTTagCompound var7 = (NBTTagCompound)var0;
         return var7.getTag(var1);
      } else if (var0 instanceof NBTTagList) {
         NBTTagList var5 = (NBTTagList)var0;
         int var6 = Config.parseInt(var1, -1);
         return var6 < 0 ? null : var5.get(var6);
      } else {
         return null;
      }
   }

   private boolean g(NBTBase var1) {
      if (var1 == null) {
         return false;
      }

      String var5 = r(var1);
      if (var5 == null) {
         return false;
      }

      switch (this.q) {
         case 0:
            return var5.equals(this.m);
         case 1:
            return this.matchesPattern(var5, this.m);
         case 2:
            return this.matchesPattern(var5.toLowerCase(), this.m);
         case 3:
            return this.matchesRegex(var5, this.m);
         case 4:
            return this.matchesRegex(var5.toLowerCase(), this.m);
         default:
            throw new IllegalArgumentException("Unknown NbtTagValue type: " + this.q);
      }
   }

   private boolean matchesPattern(String var1, String var2) {
      return StrUtils.equalsMask(var1, var2, '*', '?');
   }

   private boolean matchesRegex(String var1, String var2) {
      return var1.matches(var2);
   }

   private static String r(NBTBase var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof NBTTagString) {
         NBTTagString var10 = (NBTTagString)var0;
         return var10.getString();
      } else if (var0 instanceof NBTTagInt) {
         NBTTagInt var9 = (NBTTagInt)var0;
         return Integer.toString(var9.getInt());
      } else if (var0 instanceof NBTTagByte) {
         NBTTagByte var8 = (NBTTagByte)var0;
         return Byte.toString(var8.getByte());
      } else if (var0 instanceof NBTTagShort) {
         NBTTagShort var7 = (NBTTagShort)var0;
         return Short.toString(var7.getShort());
      } else if (var0 instanceof NBTTagLong) {
         NBTTagLong var6 = (NBTTagLong)var0;
         return Long.toString(var6.getLong());
      } else if (var0 instanceof NBTTagFloat) {
         NBTTagFloat var5 = (NBTTagFloat)var0;
         return Float.toString(var5.getFloat());
      } else if (var0 instanceof NBTTagDouble) {
         NBTTagDouble var4 = (NBTTagDouble)var0;
         return Double.toString(var4.getDouble());
      } else {
         return var0.toString();
      }
   }

   @Override
   public String toString() {
      StringBuffer var4 = new StringBuffer();

      for (int var5 = 0; var5 < this.parents.length; var5++) {
         String var6 = this.parents[var5];
         if (var5 > 0) {
            var4.append(".");
         }

         var4.append(var6);
      }

      if (var4.length() > 0) {
         var4.append(".");
      }

      var4.append(this.b);
      var4.append(" = ");
      var4.append(this.m);
      return var4.toString();
   }

   static {
      long var11 = a ^ 5295936175246L;
      long var10000 = var11 ^ 4374650486567341701L;
      var10000 = var11 ^ 5865959718055512320L;
      var10000 = var11 ^ 7628397100219079880L;
   }
}
