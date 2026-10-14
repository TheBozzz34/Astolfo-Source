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

   public NbtTagValue(String tag, String value) {
      String[] strings = Config.tokenize(tag, ".");
      this.parents = Arrays.copyOfRange(strings, 0, strings.length - 1);
      this.b = strings[strings.length - 1];
      if (value.startsWith("pattern:")) {
         this.q = 1;
         value = value.substring("pattern:".length());
      } else if (value.startsWith("ipattern:")) {
         this.q = 2;
         value = value.substring("ipattern:".length()).toLowerCase();
      } else if (value.startsWith("regex:")) {
         this.q = 3;
         value = value.substring("regex:".length());
      } else if (value.startsWith("iregex:")) {
         this.q = 4;
         value = value.substring("iregex:".length()).toLowerCase();
      } else {
         this.q = 0;
      }

      value = StringEscapeUtils.unescapeJava(value);
      this.m = value;
   }

   public boolean S(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound == null) {
         return false;
      }

      NBTBase nbtBase = nbtTagCompound;

      for (int i = 0; i < this.parents.length; i++) {
         String string = this.parents[i];
         nbtBase = getChildTag(nbtBase, string);
         if (nbtBase == null) {
            return false;
         }
      }

      if (this.b.equals("*")) {
         return this.T(nbtBase);
      }

      nbtBase = getChildTag(nbtBase, this.b);
      return nbtBase == null ? false : this.g(nbtBase);
   }

   private boolean T(NBTBase nbtBase) {
      if (nbtBase instanceof NBTTagCompound) {
         NBTTagCompound nbtTagCompound = (NBTTagCompound)nbtBase;

         for (String string : nbtTagCompound.getKeySet()) {
            NBTBase nbtBase2 = nbtTagCompound.getTag(string);
            if (this.g(nbtBase2)) {
               return true;
            }
         }
      }

      if (nbtBase instanceof NBTTagList) {
         NBTTagList nbtTagList = (NBTTagList)nbtBase;
         int i = nbtTagList.tagCount();

         for (int j = 0; j < i; j++) {
            NBTBase nbtBase3 = nbtTagList.get(j);
            if (this.g(nbtBase3)) {
               return true;
            }
         }
      }

      return false;
   }

   private static NBTBase getChildTag(NBTBase tagBase, String tag) {
      if (tagBase instanceof NBTTagCompound) {
         NBTTagCompound nbtTagCompound = (NBTTagCompound)tagBase;
         return nbtTagCompound.getTag(tag);
      } else if (tagBase instanceof NBTTagList) {
         NBTTagList nbtTagList = (NBTTagList)tagBase;
         int i = Config.parseInt(tag, -1);
         return i < 0 ? null : nbtTagList.get(i);
      } else {
         return null;
      }
   }

   private boolean g(NBTBase nbtBase) {
      if (nbtBase == null) {
         return false;
      }

      String string = r(nbtBase);
      if (string == null) {
         return false;
      }

      switch (this.q) {
         case 0:
            return string.equals(this.m);
         case 1:
            return this.matchesPattern(string, this.m);
         case 2:
            return this.matchesPattern(string.toLowerCase(), this.m);
         case 3:
            return this.matchesRegex(string, this.m);
         case 4:
            return this.matchesRegex(string.toLowerCase(), this.m);
         default:
            throw new IllegalArgumentException("Unknown NbtTagValue type: " + this.q);
      }
   }

   private boolean matchesPattern(String str, String pattern) {
      return StrUtils.equalsMask(str, pattern, '*', '?');
   }

   private boolean matchesRegex(String str, String regex) {
      return str.matches(regex);
   }

   private static String r(NBTBase nbtBase) {
      if (nbtBase == null) {
         return null;
      } else if (nbtBase instanceof NBTTagString) {
         NBTTagString nbtTagString = (NBTTagString)nbtBase;
         return nbtTagString.getString();
      } else if (nbtBase instanceof NBTTagInt) {
         NBTTagInt nbtTagInt = (NBTTagInt)nbtBase;
         return Integer.toString(nbtTagInt.getInt());
      } else if (nbtBase instanceof NBTTagByte) {
         NBTTagByte nbtTagByte = (NBTTagByte)nbtBase;
         return Byte.toString(nbtTagByte.getByte());
      } else if (nbtBase instanceof NBTTagShort) {
         NBTTagShort nbtTagShort = (NBTTagShort)nbtBase;
         return Short.toString(nbtTagShort.getShort());
      } else if (nbtBase instanceof NBTTagLong) {
         NBTTagLong nbtTagLong = (NBTTagLong)nbtBase;
         return Long.toString(nbtTagLong.getLong());
      } else if (nbtBase instanceof NBTTagFloat) {
         NBTTagFloat nbtTagFloat = (NBTTagFloat)nbtBase;
         return Float.toString(nbtTagFloat.getFloat());
      } else if (nbtBase instanceof NBTTagDouble) {
         NBTTagDouble nbtTagDouble = (NBTTagDouble)nbtBase;
         return Double.toString(nbtTagDouble.getDouble());
      } else {
         return nbtBase.toString();
      }
   }

   @Override
   public String toString() {
      StringBuffer stringBuffer = new StringBuffer();

      for (int i = 0; i < this.parents.length; i++) {
         String string = this.parents[i];
         if (i > 0) {
            stringBuffer.append(".");
         }

         stringBuffer.append(string);
      }

      if (stringBuffer.length() > 0) {
         stringBuffer.append(".");
      }

      stringBuffer.append(this.b);
      stringBuffer.append(" = ");
      stringBuffer.append(this.m);
      return stringBuffer.toString();
   }

   static {
      long l = a ^ 5295936175246L;
      long m = l ^ 4374650486567341701L;
      m = l ^ 5865959718055512320L;
      m = l ^ 7628397100219079880L;
   }
}
