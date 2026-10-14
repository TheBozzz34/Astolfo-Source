package net.minecraft.src;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StrUtils {
   public static boolean equalsMask(String str, String mask, char wildChar, char wildCharSingle) {
      if (mask != null && str != null) {
         if (mask.indexOf(wildChar) < 0) {
            return mask.indexOf(wildCharSingle) < 0 ? mask.equals(str) : equalsMaskSingle(str, mask, wildCharSingle);
         }

         ArrayList arrayList = new ArrayList();
         String string = "" + wildChar;
         if (mask.startsWith(string)) {
            arrayList.add("");
         }

         StringTokenizer stringTokenizer = new StringTokenizer(mask, string);

         while (stringTokenizer.hasMoreElements()) {
            arrayList.add(stringTokenizer.nextToken());
         }

         if (mask.endsWith(string)) {
            arrayList.add("");
         }

         String string2 = (String)arrayList.get(0);
         if (!startsWithMaskSingle(str, string2, wildCharSingle)) {
            return false;
         }

         String string3 = (String)arrayList.get(arrayList.size() - 1);
         if (!endsWithMaskSingle(str, string3, wildCharSingle)) {
            return false;
         }

         int i = 0;

         for (int j = 0; j < arrayList.size(); j++) {
            String string4 = (String)arrayList.get(j);
            if (string4.length() > 0) {
               int k = indexOfMaskSingle(str, string4, i, wildCharSingle);
               if (k < 0) {
                  return false;
               }

               i = k + string4.length();
            }
         }

         return true;
      } else {
         return mask == str;
      }
   }

   private static boolean equalsMaskSingle(String str, String mask, char wildCharSingle) {
      if (str != null && mask != null) {
         if (str.length() != mask.length()) {
            return false;
         }

         for (int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            if (c != wildCharSingle && str.charAt(i) != c) {
               return false;
            }
         }

         return true;
      } else {
         return str == mask;
      }
   }

   private static int indexOfMaskSingle(String str, String mask, int startPos, char wildCharSingle) {
      if (str != null && mask != null) {
         if (startPos >= 0 && startPos <= str.length()) {
            if (str.length() < startPos + mask.length()) {
               return -1;
            }

            for (int i = startPos; i + mask.length() <= str.length(); i++) {
               String string = str.substring(i, i + mask.length());
               if (equalsMaskSingle(string, mask, wildCharSingle)) {
                  return i;
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

   private static boolean endsWithMaskSingle(String str, String mask, char wildCharSingle) {
      if (str != null && mask != null) {
         if (str.length() < mask.length()) {
            return false;
         }

         String string = str.substring(str.length() - mask.length(), str.length());
         return equalsMaskSingle(string, mask, wildCharSingle);
      } else {
         return str == mask;
      }
   }

   private static boolean startsWithMaskSingle(String str, String mask, char wildCharSingle) {
      if (str != null && mask != null) {
         if (str.length() < mask.length()) {
            return false;
         }

         String string = str.substring(0, mask.length());
         return equalsMaskSingle(string, mask, wildCharSingle);
      } else {
         return str == mask;
      }
   }

   public static boolean equalsMask(String str, String[] masks, char wildChar) {
      for (int i = 0; i < masks.length; i++) {
         String string = masks[i];
         if (equalsMask(str, string, wildChar)) {
            return true;
         }
      }

      return false;
   }

   public static boolean equalsMask(String str, String mask, char wildChar) {
      if (mask != null && str != null) {
         if (mask.indexOf(wildChar) < 0) {
            return mask.equals(str);
         }

         ArrayList arrayList = new ArrayList();
         String string = "" + wildChar;
         if (mask.startsWith(string)) {
            arrayList.add("");
         }

         StringTokenizer stringTokenizer = new StringTokenizer(mask, string);

         while (stringTokenizer.hasMoreElements()) {
            arrayList.add(stringTokenizer.nextToken());
         }

         if (mask.endsWith(string)) {
            arrayList.add("");
         }

         String string2 = (String)arrayList.get(0);
         if (!str.startsWith(string2)) {
            return false;
         }

         String string3 = (String)arrayList.get(arrayList.size() - 1);
         if (!str.endsWith(string3)) {
            return false;
         }

         int i = 0;

         for (int j = 0; j < arrayList.size(); j++) {
            String string4 = (String)arrayList.get(j);
            if (string4.length() > 0) {
               int k = str.indexOf(string4, i);
               if (k < 0) {
                  return false;
               }

               i = k + string4.length();
            }
         }

         return true;
      } else {
         return mask == str;
      }
   }

   public static String[] split(String str, String separators) {
      if (str != null && str.length() > 0) {
         if (separators == null) {
            return new String[]{str};
         }

         ArrayList arrayList = new ArrayList();
         int i = 0;

         for (int j = 0; j < str.length(); j++) {
            char c = str.charAt(j);
            if (equals(c, separators)) {
               arrayList.add(str.substring(i, j));
               i = j + 1;
            }
         }

         arrayList.add(str.substring(i, str.length()));
         return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
      } else {
         return new String[0];
      }
   }

   private static boolean equals(char ch, String matches) {
      for (int i = 0; i < matches.length(); i++) {
         if (matches.charAt(i) == ch) {
            return true;
         }
      }

      return false;
   }

   public static boolean equalsTrim(String a, String b) {
      if (a != null) {
         a = a.trim();
      }

      if (b != null) {
         b = b.trim();
      }

      return equals(a, b);
   }

   public static boolean isEmpty(String string) {
      return string == null ? true : string.trim().length() <= 0;
   }

   public static String stringInc(String str) {
      int i = parseInt(str, -1);
      if (i == -1) {
         return "";
      }

      String string = "" + ++i;
      return string.length() > str.length() ? "" : fillLeft("" + i, str.length(), '0');
   }

   public static int parseInt(String s, int defVal) {
      if (s == null) {
         return defVal;
      }

      try {
         return Integer.parseInt(s);
      } catch (NumberFormatException numberFormatException) {
         return defVal;
      }
   }

   public static boolean isFilled(String string) {
      return !isEmpty(string);
   }

   public static String addIfNotContains(String target, String source) {
      for (int i = 0; i < source.length(); i++) {
         if (target.indexOf(source.charAt(i)) < 0) {
            target = target + source.charAt(i);
         }
      }

      return target;
   }

   public static String fillLeft(String s, int lenIn, char fillChar) {
      if (s == null) {
         s = "";
      }

      if (s.length() >= lenIn) {
         return s;
      }

      StringBuffer stringBuffer = new StringBuffer(s);

      while (stringBuffer.length() < lenIn) {
         stringBuffer.insert(0, fillChar);
      }

      return stringBuffer.toString();
   }

   public static String fillRight(String s, int lenIn, char fillChar) {
      if (s == null) {
         s = "";
      }

      if (s.length() >= lenIn) {
         return s;
      }

      StringBuffer stringBuffer = new StringBuffer(s);

      while (stringBuffer.length() < lenIn) {
         stringBuffer.append(fillChar);
      }

      return stringBuffer.toString();
   }

   public static boolean equals(Object a, Object b) {
      return a == b ? true : (a != null && a.equals(b) ? true : b != null && b.equals(a));
   }

   public static boolean startsWith(String str, String[] prefixes) {
      if (str == null) {
         return false;
      }

      if (prefixes == null) {
         return false;
      }

      for (int i = 0; i < prefixes.length; i++) {
         String string = prefixes[i];
         if (str.startsWith(string)) {
            return true;
         }
      }

      return false;
   }

   public static boolean endsWith(String str, String[] suffixes) {
      if (str == null) {
         return false;
      }

      if (suffixes == null) {
         return false;
      }

      for (int i = 0; i < suffixes.length; i++) {
         String string = suffixes[i];
         if (str.endsWith(string)) {
            return true;
         }
      }

      return false;
   }

   public static String removePrefix(String str, String prefix) {
      if (str != null && prefix != null) {
         if (str.startsWith(prefix)) {
            str = str.substring(prefix.length());
         }

         return str;
      } else {
         return str;
      }
   }

   public static String removeSuffix(String str, String suffix) {
      if (str != null && suffix != null) {
         if (str.endsWith(suffix)) {
            str = str.substring(0, str.length() - suffix.length());
         }

         return str;
      } else {
         return str;
      }
   }

   public static String replaceSuffix(String str, String suffix, String suffixNew) {
      if (str != null && suffix != null) {
         if (suffixNew == null) {
            suffixNew = "";
         }

         if (str.endsWith(suffix)) {
            str = str.substring(0, str.length() - suffix.length());
         }

         return str + suffixNew;
      } else {
         return str;
      }
   }

   public static int findPrefix(String[] strs, String prefix) {
      if (strs != null && prefix != null) {
         for (int i = 0; i < strs.length; i++) {
            String string = strs[i];
            if (string.startsWith(prefix)) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int findSuffix(String[] strs, String suffix) {
      if (strs != null && suffix != null) {
         for (int i = 0; i < strs.length; i++) {
            String string = strs[i];
            if (string.endsWith(suffix)) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static String[] remove(String[] strs, int startIn, int end) {
      if (strs == null) {
         return strs;
      }

      if (end > 0 && startIn < strs.length) {
         if (startIn >= end) {
            return strs;
         }

         ArrayList arrayList = new ArrayList(strs.length);

         for (int i = 0; i < strs.length; i++) {
            String string = strs[i];
            if (i < startIn || i >= end) {
               arrayList.add(string);
            }
         }

         return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
      } else {
         return strs;
      }
   }

   public static String removeSuffix(String str, String[] suffixes) {
      if (str != null && suffixes != null) {
         int i = str.length();

         for (int j = 0; j < suffixes.length; j++) {
            String string = suffixes[j];
            str = removeSuffix(str, string);
            if (str.length() != i) {
               break;
            }
         }

         return str;
      } else {
         return str;
      }
   }

   public static String removePrefix(String str, String[] prefixes) {
      if (str != null && prefixes != null) {
         int i = str.length();

         for (int j = 0; j < prefixes.length; j++) {
            String string = prefixes[j];
            str = removePrefix(str, string);
            if (str.length() != i) {
               break;
            }
         }

         return str;
      } else {
         return str;
      }
   }

   public static String removePrefixSuffix(String str, String[] prefixes, String[] suffixes) {
      str = removePrefix(str, prefixes);
      return removeSuffix(str, suffixes);
   }

   public static String removePrefixSuffix(String str, String prefix, String suffix) {
      return removePrefixSuffix(str, new String[]{prefix}, new String[]{suffix});
   }

   public static String getSegment(String str, String startIn, String end) {
      if (str != null && startIn != null && end != null) {
         int i = str.indexOf(startIn);
         if (i < 0) {
            return null;
         }

         int j = str.indexOf(end, i);
         return j < 0 ? null : str.substring(i, j + end.length());
      } else {
         return null;
      }
   }
}
