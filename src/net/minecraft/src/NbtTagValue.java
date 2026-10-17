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
   private String name = null;
   private int type = 0;
   private String value = null;
   private static final int TYPE_IPATTERN = 2;
   private static final int TYPE_REGEX = 3;
   private static final int TYPE_IREGEX = 4;

   public NbtTagValue(String tag, String value) {
      String[] strings = Config.tokenize(tag, ".");
      this.parents = Arrays.copyOfRange(strings, 0, strings.length - 1);
      this.name = strings[strings.length - 1];
      if (value.startsWith("pattern:")) {
         this.type = 1;
         value = value.substring("pattern:".length());
      } else if (value.startsWith("ipattern:")) {
         this.type = 2;
         value = value.substring("ipattern:".length()).toLowerCase();
      } else if (value.startsWith("regex:")) {
         this.type = 3;
         value = value.substring("regex:".length());
      } else if (value.startsWith("iregex:")) {
         this.type = 4;
         value = value.substring("iregex:".length()).toLowerCase();
      } else {
         this.type = 0;
      }

      value = StringEscapeUtils.unescapeJava(value);
      this.value = value;
   }

   public boolean matches(NBTTagCompound nbt) {
      if (nbt == null) {
         return false;
      }

      NBTBase nbtBase = nbt;

      for (int i = 0; i < this.parents.length; i++) {
         String string = this.parents[i];
         nbtBase = getChildTag(nbtBase, string);
         if (nbtBase == null) {
            return false;
         }
      }

      if (this.name.equals("*")) {
         return this.matchesAnyChild(nbtBase);
      }

      nbtBase = getChildTag(nbtBase, this.name);
      return nbtBase == null ? false : this.matchesBase(nbtBase);
   }

   private boolean matchesAnyChild(NBTBase tagBase) {
      if (tagBase instanceof NBTTagCompound) {
         NBTTagCompound nbtTagCompound = (NBTTagCompound)tagBase;

         for (String string : nbtTagCompound.getKeySet()) {
            NBTBase nbtBase = nbtTagCompound.getTag(string);
            if (this.matchesBase(nbtBase)) {
               return true;
            }
         }
      }

      if (tagBase instanceof NBTTagList) {
         NBTTagList nbtTagList = (NBTTagList)tagBase;
         int i = nbtTagList.tagCount();

         for (int j = 0; j < i; j++) {
            NBTBase nbtBase2 = nbtTagList.get(j);
            if (this.matchesBase(nbtBase2)) {
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

   private boolean matchesBase(NBTBase nbtBase) {
      if (nbtBase == null) {
         return false;
      }

      String string = getValue(nbtBase);
      if (string == null) {
         return false;
      }

      switch (this.type) {
         case 0:
            return string.equals(this.value);
         case 1:
            return this.matchesPattern(string, this.value);
         case 2:
            return this.matchesPattern(string.toLowerCase(), this.value);
         case 3:
            return this.matchesRegex(string, this.value);
         case 4:
            return this.matchesRegex(string.toLowerCase(), this.value);
         default:
            throw new IllegalArgumentException("Unknown NbtTagValue type: " + this.type);
      }
   }

   private boolean matchesPattern(String str, String pattern) {
      return StrUtils.equalsMask(str, pattern, '*', '?');
   }

   private boolean matchesRegex(String str, String regex) {
      return str.matches(regex);
   }

   private static String getValue(NBTBase nbtBase) {
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

      stringBuffer.append(this.name);
      stringBuffer.append(" = ");
      stringBuffer.append(this.value);
      return stringBuffer.toString();
   }
}
