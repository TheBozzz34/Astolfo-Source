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
   private static final long a = 134605833724289L;

   public static NBTTagCompound getTagFromJson(String var0) throws NBTException {
      var0 = var0.trim();
      if (!var0.startsWith("{")) {
         throw new NBTException("Invalid tag encountered, expected '{' as first char.");
      } else if (func_150310_b(var0) != 1) {
         throw new NBTException("Encountered multiple top tags, only one expected");
      } else {
         return (NBTTagCompound)func_150316_a("tag", var0).parse();
      }
   }

   public static int func_150310_b(String var0) throws NBTException {
      int var4 = 0;
      boolean var5 = false;
      Stack var6 = new Stack();

      for (int var7 = 0; var7 < var0.length(); var7++) {
         char var8 = var0.charAt(var7);
         if (var8 == '"') {
            if (func_179271_b(var0, var7)) {
               if (!var5) {
                  throw new NBTException("Illegal use of \\\": " + var0);
               }
            } else {
               var5 = !var5;
            }
         } else if (!var5) {
            if (var8 != '{' && var8 != '[') {
               if (var8 == '}' && (var6.isEmpty() || (Character)var6.pop() != '{')) {
                  throw new NBTException("Unbalanced curly brackets {}: " + var0);
               }

               if (var8 == ']' && (var6.isEmpty() || (Character)var6.pop() != '[')) {
                  throw new NBTException("Unbalanced square brackets []: " + var0);
               }
            } else {
               if (var6.isEmpty()) {
                  var4++;
               }

               var6.push(var8);
            }
         }
      }

      if (var5) {
         throw new NBTException("Unbalanced quotation: " + var0);
      }

      if (!var6.isEmpty()) {
         throw new NBTException("Unbalanced brackets: " + var0);
      }

      if (var4 == 0 && !var0.isEmpty()) {
         var4 = 1;
      }

      return var4;
   }

   public static JsonToNBT.Any func_179272_a(String... var0) throws NBTException {
      return func_150316_a(var0[0], var0[1]);
   }

   public static JsonToNBT.Any func_150316_a(String var0, String var1) throws NBTException {
      var1 = var1.trim();
      if (var1.startsWith("{")) {
         var1 = var1.substring(1, var1.length() - 1);
         JsonToNBT.Compound var11 = new JsonToNBT.Compound(var0);

         while (var1.length() > 0) {
            String var12 = func_150314_a(var1, true);
            if (var12.length() > 0) {
               boolean var14 = false;
               var11.field_150491_b.add(func_179270_a(var12, var14));
            }

            if (var1.length() < var12.length() + 1) {
               break;
            }

            char var15 = var1.charAt(var12.length());
            if (var15 != ',' && var15 != '{' && var15 != '}' && var15 != '[' && var15 != ']') {
               throw new NBTException("Unexpected token '" + var15 + "' at: " + var1.substring(var12.length()));
            }

            var1 = var1.substring(var12.length() + 1);
         }

         return var11;
      } else if (var1.startsWith("[") && !field_179273_b.matcher(var1).matches()) {
         var1 = var1.substring(1, var1.length() - 1);
         JsonToNBT.List var5 = new JsonToNBT.List(var0);

         while (var1.length() > 0) {
            String var6 = func_150314_a(var1, false);
            if (var6.length() > 0) {
               boolean var7 = true;
               var5.field_150492_b.add(func_179270_a(var6, var7));
            }

            if (var1.length() < var6.length() + 1) {
               break;
            }

            char var13 = var1.charAt(var6.length());
            if (var13 != ',' && var13 != '{' && var13 != '}' && var13 != '[' && var13 != ']') {
               throw new NBTException("Unexpected token '" + var13 + "' at: " + var1.substring(var6.length()));
            }

            var1 = var1.substring(var6.length() + 1);
         }

         return var5;
      } else {
         return new JsonToNBT.Primitive(var0, var1);
      }
   }

   private static JsonToNBT.Any func_179270_a(String var0, boolean var1) throws NBTException {
      String var2 = func_150313_b(var0, var1);
      String var3 = func_150311_c(var0, var1);
      return func_179272_a(var2, var3);
   }

   private static String func_150314_a(String var0, boolean var1) throws NBTException {
      int var5 = func_150312_a(var0, ':');
      int var6 = func_150312_a(var0, ',');
      if (var1) {
         if (var5 == -1) {
            throw new NBTException("Unable to locate name/value separator for string: " + var0);
         }

         if (var6 != -1 && var6 < var5) {
            throw new NBTException("Name error at: " + var0);
         }
      } else if (var5 == -1 || var5 > var6) {
         var5 = -1;
      }

      return func_179269_a(var0, var5);
   }

   private static String func_179269_a(String var0, int var1) throws NBTException {
      Stack var5 = new Stack();
      int var6 = var1 + 1;
      boolean var7 = false;
      boolean var8 = false;
      boolean var9 = false;
      int var10 = 0;

      while (var6 < var0.length()) {
         char var11 = var0.charAt(var6);
         if (var11 == '"') {
            if (func_179271_b(var0, var6)) {
               if (!var7) {
                  throw new NBTException("Illegal use of \\\": " + var0);
               }
            } else {
               var7 = !var7;
               if (var7 && !var9) {
                  var8 = true;
               }

               if (!var7) {
                  var10 = var6;
               }
            }
         } else if (!var7) {
            if (var11 != '{' && var11 != '[') {
               if (var11 == '}' && (var5.isEmpty() || (Character)var5.pop() != '{')) {
                  throw new NBTException("Unbalanced curly brackets {}: " + var0);
               }

               if (var11 == ']' && (var5.isEmpty() || (Character)var5.pop() != '[')) {
                  throw new NBTException("Unbalanced square brackets []: " + var0);
               }

               if (var11 == ',' && var5.isEmpty()) {
                  return var0.substring(0, var6);
               }
            } else {
               var5.push(var11);
            }
         }

         if (!Character.isWhitespace(var11)) {
            if (!var7 && var8 && var10 != var6) {
               return var0.substring(0, var10 + 1);
            }

            var9 = true;
         }

         var6++;
      }

      return var0.substring(0, var6);
   }

   private static String func_150313_b(String var0, boolean var1) throws NBTException {
      if (var1) {
         var0 = var0.trim();
         if (var0.startsWith("{") || var0.startsWith("[")) {
            return "";
         }
      }

      int var5 = func_150312_a(var0, ':');
      if (var5 != -1) {
         return var0.substring(0, var5).trim();
      } else if (var1) {
         return "";
      } else {
         throw new NBTException("Unable to locate name/value separator for string: " + var0);
      }
   }

   private static String func_150311_c(String var0, boolean var1) throws NBTException {
      if (var1) {
         var0 = var0.trim();
         if (var0.startsWith("{") || var0.startsWith("[")) {
            return var0;
         }
      }

      int var5 = func_150312_a(var0, ':');
      if (var5 != -1) {
         return var0.substring(var5 + 1).trim();
      } else if (var1) {
         return var0;
      } else {
         throw new NBTException("Unable to locate name/value separator for string: " + var0);
      }
   }

   private static int func_150312_a(String var0, char var1) {
      int var5 = 0;
      boolean var6 = true;

      while (var5 < var0.length()) {
         char var7 = var0.charAt(var5);
         if (var7 == '"') {
            if (!func_179271_b(var0, var5)) {
               var6 = !var6;
            }
         } else if (var6) {
            if (var7 == var1) {
               return var5;
            }

            if (var7 == '{' || var7 == '[') {
               return -1;
            }
         }

         var5++;
      }

      return -1;
   }

   private static boolean func_179271_b(String var0, int var1) {
      return var1 > 0 && var0.charAt(var1 - 1) == '\\' && !func_179271_b(var0, var1 - 1);
   }

   public abstract static class Any {
      public String json;

      public abstract NBTBase parse() throws NBTException;
   }

   public static class Compound extends JsonToNBT.Any {
      public java.util.List<JsonToNBT.Any> field_150491_b = Lists.newArrayList();
      private static final long a = 95430018355985L;

      public Compound(String var1) {
         this.json = var1;
      }

      @Override
      public NBTBase parse() throws NBTException {
         NBTTagCompound var4 = new NBTTagCompound();

         for (JsonToNBT.Any var6 : this.field_150491_b) {
            var4.setTag(var6.json, var6.parse());
         }

         return var4;
      }
   }

   public static class List extends JsonToNBT.Any {
      public java.util.List<JsonToNBT.Any> field_150492_b = Lists.newArrayList();
      private static final long a = 64032946623615L;

      public List(String var1) {
         this.json = var1;
      }

      @Override
      public NBTBase parse() throws NBTException {
         NBTTagList var4 = new NBTTagList();

         for (JsonToNBT.Any var6 : this.field_150492_b) {
            var4.appendTag(var6.parse());
         }

         return var4;
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
      private static final long a = 100673840525467L;

      public Primitive(String var1, String var2) {
         this.json = var1;
         this.jsonValue = var2;
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
         } catch (NumberFormatException var9) {
            this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
            return new NBTTagString(this.jsonValue);
         }

         if (this.jsonValue.startsWith("[") && this.jsonValue.endsWith("]")) {
            String var10 = this.jsonValue.substring(1, this.jsonValue.length() - 1);
            String[] var11 = Iterables.toArray(SPLITTER.split(var10), String.class);

            try {
               int[] var6 = new int[var11.length];

               for (int var7 = 0; var7 < var11.length; var7++) {
                  var6[var7] = Integer.parseInt(var11[var7].trim());
               }

               return new NBTTagIntArray(var6);
            } catch (NumberFormatException var8) {
               return new NBTTagString(this.jsonValue);
            }
         } else {
            if (this.jsonValue.startsWith("\"") && this.jsonValue.endsWith("\"")) {
               this.jsonValue = this.jsonValue.substring(1, this.jsonValue.length() - 1);
            }

            this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
            StringBuilder var4 = new StringBuilder();

            for (int var5 = 0; var5 < this.jsonValue.length(); var5++) {
               if (var5 < this.jsonValue.length() - 1 && this.jsonValue.charAt(var5) == '\\' && this.jsonValue.charAt(var5 + 1) == '\\') {
                  var4.append('\\');
                  var5++;
               } else {
                  var4.append(this.jsonValue.charAt(var5));
               }
            }

            return new NBTTagString(var4.toString());
         }
      }

      static {
         long var20 = a ^ 10889409421275L;
         long var10000 = 8068976305530735131L ^ var20;
      }
   }
}
