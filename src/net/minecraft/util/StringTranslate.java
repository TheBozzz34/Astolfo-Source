package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class StringTranslate {
   private static final Pattern numericVariablePattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   private static final Splitter equalSignSplitter = Splitter.on('=').limit(2);
   private static StringTranslate instance = new StringTranslate();
   private final Map<String, String> languageList = Maps.newHashMap();
   private long lastUpdateTimeInMilliseconds;
   private static final long a = 22209934659500L;

   public StringTranslate() {
      try {
         InputStream inputStream = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");

         for (String string : IOUtils.readLines(inputStream, Charsets.UTF_8)) {
            if (!string.isEmpty() && string.charAt(0) != '#') {
               String[] strings = Iterables.toArray(equalSignSplitter.split(string), String.class);
               if (strings != null && strings.length == 2) {
                  String string2 = strings[0];
                  String string3 = numericVariablePattern.matcher(strings[1]).replaceAll("%$1s");
                  this.languageList.put(string2, string3);
               }
            }
         }

         this.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
      } catch (IOException ioException) {
      }
   }

   public static StringTranslate getInstance() {
      return instance;
   }

   public static synchronized void replaceWith(Map<String, String> map) {
      instance.languageList.clear();
      instance.languageList.putAll(map);
      instance.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
   }

   public synchronized String translateKey(String key) {
      return this.tryTranslateKey(key);
   }

   public synchronized String translateKeyFormat(String key, Object... formatIn) {
      String string = this.tryTranslateKey(key);

      try {
         return String.format(string, formatIn);
      } catch (IllegalFormatException illegalFormatException) {
         return "Format error: " + string;
      }
   }

   private String tryTranslateKey(String key) {
      String string = this.languageList.get(key);
      return string == null ? key : string;
   }

   public synchronized boolean isKeyTranslated(String key) {
      return this.languageList.containsKey(key);
   }

   public long getLastUpdateTimeInMilliseconds() {
      return this.lastUpdateTimeInMilliseconds;
   }
}
