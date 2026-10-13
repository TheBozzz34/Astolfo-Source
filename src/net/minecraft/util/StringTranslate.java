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
         InputStream var3 = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");

         for (String var5 : IOUtils.readLines(var3, Charsets.UTF_8)) {
            if (!var5.isEmpty() && var5.charAt(0) != '#') {
               String[] var6 = Iterables.toArray(equalSignSplitter.split(var5), String.class);
               if (var6 != null && var6.length == 2) {
                  String var7 = var6[0];
                  String var8 = numericVariablePattern.matcher(var6[1]).replaceAll("%$1s");
                  this.languageList.put(var7, var8);
               }
            }
         }

         this.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
      } catch (IOException var9) {
      }
   }

   public static StringTranslate getInstance() {
      return instance;
   }

   public static synchronized void replaceWith(Map<String, String> var0) {
      instance.languageList.clear();
      instance.languageList.putAll(var0);
      instance.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
   }

   public synchronized String translateKey(String var1) {
      return this.tryTranslateKey(var1);
   }

   public synchronized String translateKeyFormat(String var1, Object... var2) {
      String var5 = this.tryTranslateKey(var1);

      try {
         return String.format(var5, var2);
      } catch (IllegalFormatException var7) {
         return "Format error: " + var5;
      }
   }

   private String tryTranslateKey(String var1) {
      String var5 = this.languageList.get(var1);
      return var5 == null ? var1 : var5;
   }

   public synchronized boolean isKeyTranslated(String var1) {
      return this.languageList.containsKey(var1);
   }

   public long getLastUpdateTimeInMilliseconds() {
      return this.lastUpdateTimeInMilliseconds;
   }
}
