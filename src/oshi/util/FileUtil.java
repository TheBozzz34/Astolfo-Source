package oshi.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class FileUtil {
   private static final long a = 38113426515061L;

   private FileUtil() {
   }

   public static List<String> readFile(String var0) {
      return readFile(var0, true);
   }

   public static List<String> readFile(String var0, boolean var1) {
      if (new File(var0).canRead()) {
         try {
            return Files.readAllLines(Paths.get(var0), StandardCharsets.UTF_8);
         } catch (IOException var5) {
         }
      }

      return new ArrayList<>();
   }

   public static long getLongFromFile(String var0) {
      List var4 = readFile(var0, false);
      return !var4.isEmpty() ? ParseUtil.parseLongOrDefault((String)var4.get(0), 0L) : 0L;
   }

   public static long getUnsignedLongFromFile(String var0) {
      List var4 = readFile(var0, false);
      return !var4.isEmpty() ? ParseUtil.parseUnsignedLongOrDefault((String)var4.get(0), 0L) : 0L;
   }

   public static int getIntFromFile(String var0) {
      try {
         List var4 = readFile(var0, false);
         if (!var4.isEmpty()) {
            return Integer.parseInt((String)var4.get(0));
         }
      } catch (NumberFormatException var5) {
      }

      return 0;
   }

   public static String getStringFromFile(String var0) {
      List var3 = readFile(var0, false);
      return !var3.isEmpty() ? (String)var3.get(0) : "";
   }

   public static Map<String, String> getKeyValueMapFromFile(String var0, String var1) {
      HashMap var5 = new HashMap();

      for (String var8 : readFile(var0, false)) {
         String[] var9 = var8.split(var1);
         if (var9.length == 2) {
            var5.put(var9[0], var9[1].trim());
         }
      }

      return var5;
   }

   public static Properties readPropertiesFromFilename(String var0) {
      Properties var3 = new Properties();

      try {
         ClassLoader var4 = Thread.currentThread().getContextClassLoader();
         if (var4 == null) {
            var4 = ClassLoader.getSystemClassLoader();
            if (var4 == null) {
               throw new IOException();
            }
         }

         ArrayList var5 = Collections.list(var4.getResources(var0));
         if (!var5.isEmpty()) {
            if (var5.size() > 1) {
            }

            try (InputStream var6 = ((URL)var5.get(0)).openStream()) {
               if (var6 != null) {
                  var3.load(var6);
               }
            }
         }
      } catch (IOException var19) {
      }

      return var3;
   }
}
