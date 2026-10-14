package net.minecraft.src;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Lang {
   private static final Splitter splitter = Splitter.on('=').limit(2);
   private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

   public static void resourcesReloaded() {
      Map map = I18n.getLocaleProperties();
      ArrayList arrayList = new ArrayList();
      String string = "optifine/lang/";
      String string2 = "en_US";
      String string3 = ".lang";
      arrayList.add(string + string2 + string3);
      if (!Config.getGameSettings().language.equals(string2)) {
         arrayList.add(string + Config.getGameSettings().language + string3);
      }

      String[] strings = (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
      loadResources(Config.getDefaultResourcePack(), strings, map);
      IResourcePack[] iResourcePacks = Config.getResourcePacks();

      for (int i = 0; i < iResourcePacks.length; i++) {
         IResourcePack iResourcePack = iResourcePacks[i];
         loadResources(iResourcePack, strings, map);
      }
   }

   private static void loadResources(IResourcePack rp, String[] files, Map localeProperties) {
      try {
         for (int i = 0; i < files.length; i++) {
            String string = files[i];
            ResourceLocation resourceLocation = new ResourceLocation(string);
            if (rp.resourceExists(resourceLocation)) {
               InputStream inputStream = rp.getInputStream(resourceLocation);
               if (inputStream != null) {
                  loadLocaleData(inputStream, localeProperties);
               }
            }
         }
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   public static void loadLocaleData(InputStream is, Map localeProperties) throws IOException {
      for (String string : IOUtils.readLines(is, Charsets.UTF_8)) {
         if (!string.isEmpty() && string.charAt(0) != '#') {
            String[] strings = Iterables.toArray(splitter.split(string), String.class);
            if (strings != null && strings.length == 2) {
               String string2 = strings[0];
               String string3 = pattern.matcher(strings[1]).replaceAll("%$1s");
               localeProperties.put(string2, string3);
            }
         }
      }
   }

   public static String get(String key) {
      return I18n.format(key);
   }

   public static String get(String key, String def) {
      String string = I18n.format(key);
      return string != null && !string.equals(key) ? string : def;
   }

   public static String getOn() {
      return I18n.format("options.on");
   }

   public static String getOff() {
      return I18n.format("options.off");
   }

   public static String getFast() {
      return I18n.format("options.graphics.fast");
   }

   public static String getFancy() {
      return I18n.format("options.graphics.fancy");
   }

   public static String getDefault() {
      return I18n.format("generator.default");
   }
}
