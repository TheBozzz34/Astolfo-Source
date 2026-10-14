package net.minecraft.nbt;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonToNBT {
   private static final Logger logger = LogManager.getLogger();
   private static final Pattern field_179273_b = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

   public static NBTTagCompound getTagFromJson(String jsonString) throws NBTException {
      jsonString = jsonString.trim();
      if (!jsonString.startsWith("{")) {
         throw new NBTException("Invalid tag encountered, expected '{' as first char.");
      } else if (func_150310_b(jsonString) != 1) {
         throw new NBTException("Encountered multiple top tags, only one expected");
      } else {
         return (NBTTagCompound)func_150316_a("tag", jsonString).parse();
      }
   }

   public static int func_150310_b(String string) throws NBTException {
      int i = 0;
      boolean bl = false;
      Stack stack = new Stack();

      for (int j = 0; j < string.length(); j++) {
         char c = string.charAt(j);
         if (c == '"') {
            if (func_179271_b(string, j)) {
               if (!bl) {
                  throw new NBTException("Illegal use of \\\": " + string);
               }
            } else {
               bl = !bl;
            }
         } else if (!bl) {
            if (c != '{' && c != '[') {
               if (c == '}' && (stack.isEmpty() || (Character)stack.pop() != '{')) {
                  throw new NBTException("Unbalanced curly brackets {}: " + string);
               }

               if (c == ']' && (stack.isEmpty() || (Character)stack.pop() != '[')) {
                  throw new NBTException("Unbalanced square brackets []: " + string);
               }
            } else {
               if (stack.isEmpty()) {
                  i++;
               }

               stack.push(c);
            }
         }
      }

      if (bl) {
         throw new NBTException("Unbalanced quotation: " + string);
      }

      if (!stack.isEmpty()) {
         throw new NBTException("Unbalanced brackets: " + string);
      }

      if (i == 0 && !string.isEmpty()) {
         i = 1;
      }

      return i;
   }

   public static JsonToNBT.Any func_179272_a(String... strings) throws NBTException {
      return func_150316_a(strings[0], strings[1]);
   }

   public static JsonToNBT.Any func_150316_a(String string, String string2) throws NBTException {
      string2 = string2.trim();
      if (string2.startsWith("{")) {
         string2 = string2.substring(1, string2.length() - 1);
         JsonToNBT.Compound compound = new JsonToNBT.Compound(string);

         while (string2.length() > 0) {
            String string3 = func_150314_a(string2, true);
            if (string3.length() > 0) {
               boolean bl = false;
               compound.field_150491_b.add(func_179270_a(string3, bl));
            }

            if (string2.length() < string3.length() + 1) {
               break;
            }

            char c = string2.charAt(string3.length());
            if (c != ',' && c != '{' && c != '}' && c != '[' && c != ']') {
               throw new NBTException("Unexpected token '" + c + "' at: " + string2.substring(string3.length()));
            }

            string2 = string2.substring(string3.length() + 1);
         }

         return compound;
      } else if (string2.startsWith("[") && !field_179273_b.matcher(string2).matches()) {
         string2 = string2.substring(1, string2.length() - 1);
         JsonToNBT.List list = new JsonToNBT.List(string);

         while (string2.length() > 0) {
            String string4 = func_150314_a(string2, false);
            if (string4.length() > 0) {
               boolean bl2 = true;
               list.field_150492_b.add(func_179270_a(string4, bl2));
            }

            if (string2.length() < string4.length() + 1) {
               break;
            }

            char c2 = string2.charAt(string4.length());
            if (c2 != ',' && c2 != '{' && c2 != '}' && c2 != '[' && c2 != ']') {
               throw new NBTException("Unexpected token '" + c2 + "' at: " + string2.substring(string4.length()));
            }

            string2 = string2.substring(string4.length() + 1);
         }

         return list;
      } else {
         return new JsonToNBT.Primitive(string, string2);
      }
   }

   private static JsonToNBT.Any func_179270_a(String string, boolean bl) throws NBTException {
      String string2 = func_150313_b(string, bl);
      String string3 = func_150311_c(string, bl);
      return func_179272_a(string2, string3);
   }

   private static String func_150314_a(String string, boolean bl) throws NBTException {
      int i = func_150312_a(string, ':');
      int j = func_150312_a(string, ',');
      if (bl) {
         if (i == -1) {
            throw new NBTException("Unable to locate name/value separator for string: " + string);
         }

         if (j != -1 && j < i) {
            throw new NBTException("Name error at: " + string);
         }
      } else if (i == -1 || i > j) {
         i = -1;
      }

      return func_179269_a(string, i);
   }

   private static String func_179269_a(String string, int i) throws NBTException {
      Stack stack = new Stack();
      int j = i + 1;
      boolean bl = false;
      boolean bl2 = false;
      boolean bl3 = false;
      int k = 0;

      while (j < string.length()) {
         char c = string.charAt(j);
         if (c == '"') {
            if (func_179271_b(string, j)) {
               if (!bl) {
                  throw new NBTException("Illegal use of \\\": " + string);
               }
            } else {
               bl = !bl;
               if (bl && !bl3) {
                  bl2 = true;
               }

               if (!bl) {
                  k = j;
               }
            }
         } else if (!bl) {
            if (c != '{' && c != '[') {
               if (c == '}' && (stack.isEmpty() || (Character)stack.pop() != '{')) {
                  throw new NBTException("Unbalanced curly brackets {}: " + string);
               }

               if (c == ']' && (stack.isEmpty() || (Character)stack.pop() != '[')) {
                  throw new NBTException("Unbalanced square brackets []: " + string);
               }

               if (c == ',' && stack.isEmpty()) {
                  return string.substring(0, j);
               }
            } else {
               stack.push(c);
            }
         }

         if (!Character.isWhitespace(c)) {
            if (!bl && bl2 && k != j) {
               return string.substring(0, k + 1);
            }

            bl3 = true;
         }

         j++;
      }

      return string.substring(0, j);
   }

   private static String func_150313_b(String string, boolean bl) throws NBTException {
      if (bl) {
         string = string.trim();
         if (string.startsWith("{") || string.startsWith("[")) {
            return "";
         }
      }

      int i = func_150312_a(string, ':');
      if (i != -1) {
         return string.substring(0, i).trim();
      } else if (bl) {
         return "";
      } else {
         throw new NBTException("Unable to locate name/value separator for string: " + string);
      }
   }

   private static String func_150311_c(String string, boolean bl) throws NBTException {
      if (bl) {
         string = string.trim();
         if (string.startsWith("{") || string.startsWith("[")) {
            return string;
         }
      }

      int i = func_150312_a(string, ':');
      if (i != -1) {
         return string.substring(i + 1).trim();
      } else if (bl) {
         return string;
      } else {
         throw new NBTException("Unable to locate name/value separator for string: " + string);
      }
   }

   private static int func_150312_a(String string, char c) {
      int i = 0;
      boolean bl = true;

      while (i < string.length()) {
         char c2 = string.charAt(i);
         if (c2 == '"') {
            if (!func_179271_b(string, i)) {
               bl = !bl;
            }
         } else if (bl) {
            if (c2 == c) {
               return i;
            }

            if (c2 == '{' || c2 == '[') {
               return -1;
            }
         }

         i++;
      }

      return -1;
   }

   private static boolean func_179271_b(String string, int i) {
      return i > 0 && string.charAt(i - 1) == '\\' && !func_179271_b(string, i - 1);
   }

   public abstract static class Any {
      public String json;

      public abstract NBTBase parse() throws NBTException;
   }

   public static class Compound extends JsonToNBT.Any {
      public java.util.List<JsonToNBT.Any> field_150491_b = Lists.newArrayList();

      public Compound(String string) {
         this.json = string;
      }

      @Override
      public NBTBase parse() throws NBTException {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();

         for (JsonToNBT.Any any : this.field_150491_b) {
            nbtTagCompound.setTag(any.json, any.parse());
         }

         return nbtTagCompound;
      }
   }

   public static class List extends JsonToNBT.Any {
      public java.util.List<JsonToNBT.Any> field_150492_b = Lists.newArrayList();

      public List(String json) {
         this.json = json;
      }

      @Override
      public NBTBase parse() throws NBTException {
         NBTTagList nbtTagList = new NBTTagList();

         for (JsonToNBT.Any any : this.field_150492_b) {
            nbtTagList.appendTag(any.parse());
         }

         return nbtTagList;
      }
   }

   public static class Primitive extends JsonToNBT.Any {
      private static final Pattern DOUBLE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
      private static final Pattern FLOAT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
      private static final Pattern BYTE = Pattern.compile("[-+]?[0-9]+[b|B]");
      private static final Pattern LONG = Pattern.compile("[-+]?[0-9]+[l|L]");
      private static final Pattern SHORT = Pattern.compile("[-+]?[0-9]+[s|S]");
      private static final Pattern INTEGER = Pattern.compile("[-+]?[0-9]+");
      private static final Pattern DOUBLE_UNTYPED = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
      private static final Splitter SPLITTER = Splitter.on(',').omitEmptyStrings();
      public String jsonValue;

      public Primitive(String string, String string2) {
         this.json = string;
         this.jsonValue = string2;
      }

      @Override
      public NBTBase parse() throws NBTException {
         try {
            if (DOUBLE.matcher(this.jsonValue).matches()) {
               return new NBTTagDouble(Double.parseDouble(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
            }

            if (FLOAT.matcher(this.jsonValue).matches()) {
               return new NBTTagFloat(Float.parseFloat(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
            }

            if (BYTE.matcher(this.jsonValue).matches()) {
               return new NBTTagByte(Byte.parseByte(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
            }

            if (LONG.matcher(this.jsonValue).matches()) {
               return new NBTTagLong(Long.parseLong(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
            }

            if (SHORT.matcher(this.jsonValue).matches()) {
               return new NBTTagShort(Short.parseShort(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
            }

            if (INTEGER.matcher(this.jsonValue).matches()) {
               return new NBTTagInt(Integer.parseInt(this.jsonValue));
            }

            if (DOUBLE_UNTYPED.matcher(this.jsonValue).matches()) {
               return new NBTTagDouble(Double.parseDouble(this.jsonValue));
            }

            if (this.jsonValue.equalsIgnoreCase("true") || this.jsonValue.equalsIgnoreCase("false")) {
               return new NBTTagByte((byte)(Boolean.parseBoolean(this.jsonValue) ? 1 : 0));
            }
         } catch (NumberFormatException numberFormatException) {
            this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
            return new NBTTagString(this.jsonValue);
         }

         if (this.jsonValue.startsWith("[") && this.jsonValue.endsWith("]")) {
            String string = this.jsonValue.substring(1, this.jsonValue.length() - 1);
            String[] strings = Iterables.toArray(SPLITTER.split(string), String.class);

            try {
               int[] ints = new int[strings.length];

               for (int i = 0; i < strings.length; i++) {
                  ints[i] = Integer.parseInt(strings[i].trim());
               }

               return new NBTTagIntArray(ints);
            } catch (NumberFormatException numberFormatException2) {
               return new NBTTagString(this.jsonValue);
            }
         } else {
            if (this.jsonValue.startsWith("\"") && this.jsonValue.endsWith("\"")) {
               this.jsonValue = this.jsonValue.substring(1, this.jsonValue.length() - 1);
            }

            this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
            StringBuilder stringBuilder = new StringBuilder();

            for (int j = 0; j < this.jsonValue.length(); j++) {
               if (j < this.jsonValue.length() - 1 && this.jsonValue.charAt(j) == '\\' && this.jsonValue.charAt(j + 1) == '\\') {
                  stringBuilder.append('\\');
                  j++;
               } else {
                  stringBuilder.append(this.jsonValue.charAt(j));
               }
            }

            return new NBTTagString(stringBuilder.toString());
         }
      }
   }
}
