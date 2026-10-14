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
   private FileUtil() {
   }

   public static List<String> readFile(String filename) {
      return readFile(filename, true);
   }

   public static List<String> readFile(String filename, boolean reportError) {
      if (new File(filename).canRead()) {
         try {
            return Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
         } catch (IOException ioException) {
         }
      }

      return new ArrayList<>();
   }

   public static long getLongFromFile(String filename) {
      List list = readFile(filename, false);
      return !list.isEmpty() ? ParseUtil.parseLongOrDefault((String)list.get(0), 0L) : 0L;
   }

   public static long getUnsignedLongFromFile(String filename) {
      List list = readFile(filename, false);
      return !list.isEmpty() ? ParseUtil.parseUnsignedLongOrDefault((String)list.get(0), 0L) : 0L;
   }

   public static int getIntFromFile(String filename) {
      try {
         List list = readFile(filename, false);
         if (!list.isEmpty()) {
            return Integer.parseInt((String)list.get(0));
         }
      } catch (NumberFormatException numberFormatException) {
      }

      return 0;
   }

   public static String getStringFromFile(String filename) {
      List list = readFile(filename, false);
      return !list.isEmpty() ? (String)list.get(0) : "";
   }

   public static Map<String, String> getKeyValueMapFromFile(String filename, String separator) {
      HashMap hashMap = new HashMap();

      for (String string : readFile(filename, false)) {
         String[] strings = string.split(separator);
         if (strings.length == 2) {
            hashMap.put(strings[0], strings[1].trim());
         }
      }

      return hashMap;
   }

   public static Properties readPropertiesFromFilename(String propsFilename) {
      Properties properties = new Properties();

      try {
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
            if (classLoader == null) {
               throw new IOException();
            }
         }

         ArrayList arrayList = Collections.list(classLoader.getResources(propsFilename));
         if (!arrayList.isEmpty()) {
            if (arrayList.size() > 1) {
            }

            try (InputStream inputStream = ((URL)arrayList.get(0)).openStream()) {
               if (inputStream != null) {
                  properties.load(inputStream);
               }
            }
         }
      } catch (IOException ioException) {
      }

      return properties;
   }
}
