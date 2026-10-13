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
   private static final long a = 4752896723902L;

   public static void resourcesReloaded() {
      Map var3 = I18n.getLocaleProperties();
      ArrayList var4 = new ArrayList();
      String var5 = "optifine/lang/";
      String var6 = "en_US";
      String var7 = ".lang";
      var4.add(var5 + var6 + var7);
      if (!Config.getGameSettings().language.equals(var6)) {
         var4.add(var5 + Config.getGameSettings().language + var7);
      }

      String[] var8 = (java.lang.String[])(var4.toArray(new String[var4.size()]));
      loadResources(Config.getDefaultResourcePack(), var8, var3);
      IResourcePack[] var9 = Config.getResourcePacks();

      for (int var10 = 0; var10 < var9.length; var10++) {
         IResourcePack var11 = var9[var10];
         loadResources(var11, var8, var3);
      }
   }

   private static void loadResources(IResourcePack var0, String[] var1, Map var2) {
      try {
         for (int var6 = 0; var6 < var1.length; var6++) {
            String var7 = var1[var6];
            ResourceLocation var8 = new ResourceLocation(var7);
            if (var0.resourceExists(var8)) {
               InputStream var9 = var0.getInputStream(var8);
               if (var9 != null) {
                  loadLocaleData(var9, var2);
               }
            }
         }
      } catch (IOException var10) {
         var10.printStackTrace();
      }
   }

   public static void loadLocaleData(InputStream var0, Map var1) throws IOException {
      for (String var6 : IOUtils.readLines(var0, Charsets.UTF_8)) {
         if (!var6.isEmpty() && var6.charAt(0) != '#') {
            String[] var7 = Iterables.toArray(splitter.split(var6), String.class);
            if (var7 != null && var7.length == 2) {
               String var8 = var7[0];
               String var9 = pattern.matcher(var7[1]).replaceAll("%$1s");
               var1.put(var8, var9);
            }
         }
      }
   }

   public static String get(String var0) {
      return I18n.format(var0);
   }

   public static String get(String var0, String var1) {
      String var5 = I18n.format(var0);
      return var5 != null && !var5.equals(var0) ? var5 : var1;
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
