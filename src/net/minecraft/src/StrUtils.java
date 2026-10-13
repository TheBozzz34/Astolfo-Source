package net.minecraft.src;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StrUtils {
   private static final long a = 100558836188296L;
   private static final long b = 7411752035378266160L;

   public static boolean equalsMask(String var0, String var1, char var2, char var3) {
      if (var1 != null && var0 != null) {
         if (var1.indexOf(var2) < 0) {
            return var1.indexOf(var3) < 0 ? var1.equals(var0) : equalsMaskSingle(var0, var1, var3);
         }

         ArrayList var7 = new ArrayList();
         String var8 = "" + var2;
         if (var1.startsWith(var8)) {
            var7.add("");
         }

         StringTokenizer var9 = new StringTokenizer(var1, var8);

         while (var9.hasMoreElements()) {
            var7.add(var9.nextToken());
         }

         if (var1.endsWith(var8)) {
            var7.add("");
         }

         String var10 = (String)var7.get(0);
         if (!startsWithMaskSingle(var0, var10, var3)) {
            return false;
         }

         String var11 = (String)var7.get(var7.size() - 1);
         if (!endsWithMaskSingle(var0, var11, var3)) {
            return false;
         }

         int var12 = 0;

         for (int var13 = 0; var13 < var7.size(); var13++) {
            String var14 = (String)var7.get(var13);
            if (var14.length() > 0) {
               int var15 = indexOfMaskSingle(var0, var14, var12, var3);
               if (var15 < 0) {
                  return false;
               }

               var12 = var15 + var14.length();
            }
         }

         return true;
      } else {
         return var1 == var0;
      }
   }

   private static boolean equalsMaskSingle(String var0, String var1, char var2) {
      if (var0 != null && var1 != null) {
         if (var0.length() != var1.length()) {
            return false;
         }

         for (int var6 = 0; var6 < var1.length(); var6++) {
            char var7 = var1.charAt(var6);
            if (var7 != var2 && var0.charAt(var6) != var7) {
               return false;
            }
         }

         return true;
      } else {
         return var0 == var1;
      }
   }

   private static int indexOfMaskSingle(String var0, String var1, int var2, char var3) {
      if (var0 != null && var1 != null) {
         if (var2 >= 0 && var2 <= var0.length()) {
            if (var0.length() < var2 + var1.length()) {
               return -1;
            }

            for (int var7 = var2; var7 + var1.length() <= var0.length(); var7++) {
               String var8 = var0.substring(var7, var7 + var1.length());
               if (equalsMaskSingle(var8, var1, var3)) {
                  return var7;
               }
            }

            return -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private static boolean endsWithMaskSingle(String var0, String var1, char var2) {
      if (var0 != null && var1 != null) {
         if (var0.length() < var1.length()) {
            return false;
         }

         String var6 = var0.substring(var0.length() - var1.length(), var0.length());
         return equalsMaskSingle(var6, var1, var2);
      } else {
         return var0 == var1;
      }
   }

   private static boolean startsWithMaskSingle(String var0, String var1, char var2) {
      if (var0 != null && var1 != null) {
         if (var0.length() < var1.length()) {
            return false;
         }

         String var6 = var0.substring(0, var1.length());
         return equalsMaskSingle(var6, var1, var2);
      } else {
         return var0 == var1;
      }
   }

   public static boolean equalsMask(String var0, String[] var1, char var2) {
      for (int var6 = 0; var6 < var1.length; var6++) {
         String var7 = var1[var6];
         if (equalsMask(var0, var7, var2)) {
            return true;
         }
      }

      return false;
   }

   public static boolean equalsMask(String var0, String var1, char var2) {
      if (var1 != null && var0 != null) {
         if (var1.indexOf(var2) < 0) {
            return var1.equals(var0);
         }

         ArrayList var6 = new ArrayList();
         String var7 = "" + var2;
         if (var1.startsWith(var7)) {
            var6.add("");
         }

         StringTokenizer var8 = new StringTokenizer(var1, var7);

         while (var8.hasMoreElements()) {
            var6.add(var8.nextToken());
         }

         if (var1.endsWith(var7)) {
            var6.add("");
         }

         String var9 = (String)var6.get(0);
         if (!var0.startsWith(var9)) {
            return false;
         }

         String var10 = (String)var6.get(var6.size() - 1);
         if (!var0.endsWith(var10)) {
            return false;
         }

         int var11 = 0;

         for (int var12 = 0; var12 < var6.size(); var12++) {
            String var13 = (String)var6.get(var12);
            if (var13.length() > 0) {
               int var14 = var0.indexOf(var13, var11);
               if (var14 < 0) {
                  return false;
               }

               var11 = var14 + var13.length();
            }
         }

         return true;
      } else {
         return var1 == var0;
      }
   }

   public static String[] split(String var0, String var1) {
      if (var0 != null && var0.length() > 0) {
         if (var1 == null) {
            return new String[]{var0};
         }

         ArrayList var5 = new ArrayList();
         int var6 = 0;

         for (int var7 = 0; var7 < var0.length(); var7++) {
            char var8 = var0.charAt(var7);
            if (equals(var8, var1)) {
               var5.add(var0.substring(var6, var7));
               var6 = var7 + 1;
            }
         }

         var5.add(var0.substring(var6, var0.length()));
         return (java.lang.String[])(var5.toArray(new String[var5.size()]));
      } else {
         return new String[0];
      }
   }

   private static boolean equals(char var0, String var1) {
      for (int var5 = 0; var5 < var1.length(); var5++) {
         if (var1.charAt(var5) == var0) {
            return true;
         }
      }

      return false;
   }

   public static boolean equalsTrim(String var0, String var1) {
      if (var0 != null) {
         var0 = var0.trim();
      }

      if (var1 != null) {
         var1 = var1.trim();
      }

      return equals(var0, var1);
   }

   public static boolean isEmpty(String var0) {
      return var0 == null ? true : var0.trim().length() <= 0;
   }

   public static String stringInc(String var0) {
      int var4 = parseInt(var0, -1);
      if (var4 == -1) {
         return "";
      }

      String var5 = "" + ++var4;
      return var5.length() > var0.length() ? "" : fillLeft("" + var4, var0.length(), (char)((int)b));
   }

   public static int parseInt(String var0, int var1) {
      if (var0 == null) {
         return var1;
      }

      try {
         return Integer.parseInt(var0);
      } catch (NumberFormatException var6) {
         return var1;
      }
   }

   public static boolean isFilled(String var0) {
      return !isEmpty(var0);
   }

   public static String addIfNotContains(String var0, String var1) {
      for (int var5 = 0; var5 < var1.length(); var5++) {
         if (var0.indexOf(var1.charAt(var5)) < 0) {
            var0 = var0 + var1.charAt(var5);
         }
      }

      return var0;
   }

   public static String fillLeft(String var0, int var1, char var2) {
      if (var0 == null) {
         var0 = "";
      }

      if (var0.length() >= var1) {
         return var0;
      }

      StringBuffer var6 = new StringBuffer(var0);

      while (var6.length() < var1) {
         var6.insert(0, var2);
      }

      return var6.toString();
   }

   public static String fillRight(String var0, int var1, char var2) {
      if (var0 == null) {
         var0 = "";
      }

      if (var0.length() >= var1) {
         return var0;
      }

      StringBuffer var6 = new StringBuffer(var0);

      while (var6.length() < var1) {
         var6.append(var2);
      }

      return var6.toString();
   }

   public static boolean equals(Object var0, Object var1) {
      return var0 == var1 ? true : (var0 != null && var0.equals(var1) ? true : var1 != null && var1.equals(var0));
   }

   public static boolean startsWith(String var0, String[] var1) {
      if (var0 == null) {
         return false;
      }

      if (var1 == null) {
         return false;
      }

      for (int var5 = 0; var5 < var1.length; var5++) {
         String var6 = var1[var5];
         if (var0.startsWith(var6)) {
            return true;
         }
      }

      return false;
   }

   public static boolean endsWith(String var0, String[] var1) {
      if (var0 == null) {
         return false;
      }

      if (var1 == null) {
         return false;
      }

      for (int var5 = 0; var5 < var1.length; var5++) {
         String var6 = var1[var5];
         if (var0.endsWith(var6)) {
            return true;
         }
      }

      return false;
   }

   public static String removePrefix(String var0, String var1) {
      if (var0 != null && var1 != null) {
         if (var0.startsWith(var1)) {
            var0 = var0.substring(var1.length());
         }

         return var0;
      } else {
         return var0;
      }
   }

   public static String removeSuffix(String var0, String var1) {
      if (var0 != null && var1 != null) {
         if (var0.endsWith(var1)) {
            var0 = var0.substring(0, var0.length() - var1.length());
         }

         return var0;
      } else {
         return var0;
      }
   }

   public static String replaceSuffix(String var0, String var1, String var2) {
      if (var0 != null && var1 != null) {
         if (var2 == null) {
            var2 = "";
         }

         if (var0.endsWith(var1)) {
            var0 = var0.substring(0, var0.length() - var1.length());
         }

         return var0 + var2;
      } else {
         return var0;
      }
   }

   public static int findPrefix(String[] var0, String var1) {
      if (var0 != null && var1 != null) {
         for (int var5 = 0; var5 < var0.length; var5++) {
            String var6 = var0[var5];
            if (var6.startsWith(var1)) {
               return var5;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int findSuffix(String[] var0, String var1) {
      if (var0 != null && var1 != null) {
         for (int var5 = 0; var5 < var0.length; var5++) {
            String var6 = var0[var5];
            if (var6.endsWith(var1)) {
               return var5;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static String[] remove(String[] var0, int var1, int var2) {
      if (var0 == null) {
         return var0;
      }

      if (var2 > 0 && var1 < var0.length) {
         if (var1 >= var2) {
            return var0;
         }

         ArrayList var6 = new ArrayList(var0.length);

         for (int var7 = 0; var7 < var0.length; var7++) {
            String var8 = var0[var7];
            if (var7 < var1 || var7 >= var2) {
               var6.add(var8);
            }
         }

         return (java.lang.String[])(var6.toArray(new String[var6.size()]));
      } else {
         return var0;
      }
   }

   public static String removeSuffix(String var0, String[] var1) {
      if (var0 != null && var1 != null) {
         int var5 = var0.length();

         for (int var6 = 0; var6 < var1.length; var6++) {
            String var7 = var1[var6];
            var0 = removeSuffix(var0, var7);
            if (var0.length() != var5) {
               break;
            }
         }

         return var0;
      } else {
         return var0;
      }
   }

   public static String removePrefix(String var0, String[] var1) {
      if (var0 != null && var1 != null) {
         int var5 = var0.length();

         for (int var6 = 0; var6 < var1.length; var6++) {
            String var7 = var1[var6];
            var0 = removePrefix(var0, var7);
            if (var0.length() != var5) {
               break;
            }
         }

         return var0;
      } else {
         return var0;
      }
   }

   public static String removePrefixSuffix(String var0, String[] var1, String[] var2) {
      var0 = removePrefix(var0, var1);
      return removeSuffix(var0, var2);
   }

   public static String removePrefixSuffix(String var0, String var1, String var2) {
      return removePrefixSuffix(var0, new String[]{var1}, new String[]{var2});
   }

   public static String getSegment(String var0, String var1, String var2) {
      if (var0 != null && var1 != null && var2 != null) {
         int var6 = var0.indexOf(var1);
         if (var6 < 0) {
            return null;
         }

         int var7 = var0.indexOf(var2, var6);
         return var7 < 0 ? null : var0.substring(var6, var7 + var2.length());
      } else {
         return null;
      }
   }
}
