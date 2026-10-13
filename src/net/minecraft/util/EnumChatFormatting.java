package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public enum EnumChatFormatting {
   BLACK("BLACK", '0', 0),
   DARK_BLUE("DARK_BLUE", '1', 1),
   DARK_GREEN("DARK_GREEN", '2', 2),
   DARK_AQUA("DARK_AQUA", '3', 3),
   DARK_RED("DARK_RED", '4', 4),
   DARK_PURPLE("DARK_PURPLE", '5', 5),
   GOLD("GOLD", '6', 6),
   GRAY("GRAY", '7', 7),
   DARK_GRAY("DARK_GRAY", '8', 8),
   BLUE("BLUE", '9', 9),
   GREEN("GREEN", 'a', 10),
   AQUA("AQUA", 'b', 11),
   RED("RED", 'c', 12),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
   YELLOW("YELLOW", 'e', 14),
   WHITE("WHITE", 'f', 15),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1);

   private static final Map<String, EnumChatFormatting> nameMapping = Maps.newHashMap();
   private static final Pattern formattingCodePattern = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-OR]");
   private final String name;
   private final char formattingCode;
   private final boolean fancyStyling;
   private final String controlString;
   private final int colorIndex;
   private static final long a = 68245928068756L;

   private static String func_175745_c(String var0) {
      return var0.toLowerCase().replaceAll("[^a-z]", "");
   }

   EnumChatFormatting(String var3, char var4, int var5) {
      this(var3, var4, false, var5);
   }

   EnumChatFormatting(String var3, char var4, boolean var5) {
      this(var3, var4, var5, -1);
   }

   EnumChatFormatting(String var3, char var4, boolean var5, int var6) {
      this.name = var3;
      this.formattingCode = var4;
      this.fancyStyling = var5;
      this.colorIndex = var6;
      this.controlString = "§" + var4;
   }

   public int getColorIndex() {
      return this.colorIndex;
   }

   public boolean isFancyStyling() {
      return this.fancyStyling;
   }

   public boolean isColor() {
      return !this.fancyStyling && this != RESET;
   }

   public String getFriendlyName() {
      return this.name().toLowerCase();
   }

   @Override
   public String toString() {
      return this.controlString;
   }

   public static String getTextWithoutFormattingCodes(String var0) {
      return var0 == null ? null : formattingCodePattern.matcher(var0).replaceAll("");
   }

   public static EnumChatFormatting getValueByName(String var0) {
      return var0 == null ? null : nameMapping.get(func_175745_c(var0));
   }

   public static EnumChatFormatting func_175744_a(int var0) {
      if (var0 < 0) {
         return RESET;
      }

      for (EnumChatFormatting var7 : values()) {
         if (var7.getColorIndex() == var0) {
            return var7;
         }
      }

      return null;
   }

   public static Collection<String> getValidValues(boolean var0, boolean var1) {
      ArrayList var5 = Lists.newArrayList();

      for (EnumChatFormatting var9 : values()) {
         if ((!var9.isColor() || var0) && (!var9.isFancyStyling() || var1)) {
            var5.add(var9.getFriendlyName());
         }
      }

      return var5;
   }

   static {
      for (EnumChatFormatting var25 : values()) {
         nameMapping.put(func_175745_c(var25.name), var25);
      }
   }
}
