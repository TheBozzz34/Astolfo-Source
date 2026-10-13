package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale {
   private static final Splitter splitter = Splitter.on('=').limit(2);
   private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   public Map<String, String> properties = Maps.newHashMap();
   private boolean unicode;
   private static final long a = 7752813143793L;

   public synchronized void loadLocaleDataFiles(IResourceManager var1, List<String> var2) {
      this.properties.clear();

      for (String var7 : var2) {
         String var8 = String.format("lang/%s.lang", var7);

         for (String var10 : var1.getResourceDomains()) {
            try {
               this.loadLocaleData(var1.getAllResources(new ResourceLocation(var10, var8)));
            } catch (IOException var12) {
            }
         }
      }

      this.checkUnicode();
   }

   public boolean isUnicode() {
      return this.unicode;
   }

   private void checkUnicode() {
      this.unicode = false;
      int var4 = 0;
      int var5 = 0;

      for (String var7 : this.properties.values()) {
         int var8 = var7.length();
         var5 += var8;

         for (int var9 = 0; var9 < var8; var9++) {
            if (var7.charAt(var9) >= 256) {
               var4++;
            }
         }
      }

      float var10 = (float)var4 / var5;
      this.unicode = var10 > 0.1;
   }

   private void loadLocaleData(List<IResource> var1) throws IOException {
      for (IResource var6 : var1) {
         InputStream var7 = var6.getInputStream();

         try {
            this.loadLocaleData(var7);
         } finally {
            IOUtils.closeQuietly(var7);
         }
      }
   }

   private void loadLocaleData(InputStream var1) throws IOException {
      for (String var6 : IOUtils.readLines(var1, Charsets.UTF_8)) {
         if (!var6.isEmpty() && var6.charAt(0) != '#') {
            String[] var7 = Iterables.toArray(splitter.split(var6), String.class);
            if (var7 != null && var7.length == 2) {
               String var8 = var7[0];
               String var9 = pattern.matcher(var7[1]).replaceAll("%$1s");
               this.properties.put(var8, var9);
            }
         }
      }
   }

   private String translateKeyPrivate(String var1) {
      String var4 = this.properties.get(var1);
      return var4 == null ? var1 : var4;
   }

   public String formatMessage(String var1, Object[] var2) {
      String var5 = this.translateKeyPrivate(var1);

      try {
         return String.format(var5, var2);
      } catch (IllegalFormatException var7) {
         return "Format error: " + var5;
      }
   }
}
