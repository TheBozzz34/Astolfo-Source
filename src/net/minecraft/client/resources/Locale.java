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

   public synchronized void loadLocaleDataFiles(IResourceManager resourceManager, List<String> languageList) {
      this.properties.clear();

      for (String string : languageList) {
         String string2 = String.format("lang/%s.lang", string);

         for (String string3 : resourceManager.getResourceDomains()) {
            try {
               this.loadLocaleData(resourceManager.getAllResources(new ResourceLocation(string3, string2)));
            } catch (IOException ioException) {
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
      int i = 0;
      int j = 0;

      for (String string : this.properties.values()) {
         int k = string.length();
         j += k;

         for (int l = 0; l < k; l++) {
            if (string.charAt(l) >= 256) {
               i++;
            }
         }
      }

      float f = (float)i / j;
      this.unicode = f > 0.1;
   }

   private void loadLocaleData(List<IResource> resourcesList) throws IOException {
      for (IResource iResource : resourcesList) {
         InputStream inputStream = iResource.getInputStream();

         try {
            this.loadLocaleData(inputStream);
         } finally {
            IOUtils.closeQuietly(inputStream);
         }
      }
   }

   private void loadLocaleData(InputStream inputStreamIn) throws IOException {
      for (String string : IOUtils.readLines(inputStreamIn, Charsets.UTF_8)) {
         if (!string.isEmpty() && string.charAt(0) != '#') {
            String[] strings = Iterables.toArray(splitter.split(string), String.class);
            if (strings != null && strings.length == 2) {
               String string2 = strings[0];
               String string3 = pattern.matcher(strings[1]).replaceAll("%$1s");
               this.properties.put(string2, string3);
            }
         }
      }
   }

   private String translateKeyPrivate(String translateKey) {
      String string = this.properties.get(translateKey);
      return string == null ? translateKey : string;
   }

   public String formatMessage(String translateKey, Object[] parameters) {
      String string = this.translateKeyPrivate(translateKey);

      try {
         return String.format(string, parameters);
      } catch (IllegalFormatException illegalFormatException) {
         return "Format error: " + string;
      }
   }
}
