package bypass.config;

import bypass.Astolfo;
import bypass.ClientAccess;
import bypass.module.Module;
import bypass.module.ModuleInfo;
import bypass.setting.ListSetting;
import bypass.setting.MapSetting;
import bypass.setting.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;

public final class ConfigFiles implements ClientAccess {
   public static final Gson V = new Gson();
   public static final Gson G = new GsonBuilder().setPrettyPrinting().create();
   private static final long b = 49891827257663L;

   private static JsonObject toJson(Setting<?> var0, String var1) {
      JsonObject var5 = new JsonObject();
      JsonArray var6 = new JsonArray();
      var5.addProperty("option_id", var1);
      if (var0 instanceof MapSetting) {
         MapSetting var7 = (MapSetting)var0;
         Map var8 = var7.getSettings();
         if (var8 != null && !var8.isEmpty()) {
            JsonArray var9 = new JsonArray();

            for (Object var11$raw : var8.keySet()) {
               String var11 = (String)var11$raw;
               Setting var12 = (Setting)var8.get(var11);
               JsonObject var13 = toJson(var12, var11);
               var9.add(var13);
            }

            var5.add("option_map", var9);
         }
      } else if (var0 instanceof ListSetting) {
         ListSetting var14 = (ListSetting)var0;
         List var16 = var14.getSettings();
         if (var16 != null && !var16.isEmpty()) {
            JsonArray var18 = new JsonArray();

            for (int var20 = 0; var20 < var16.size(); var20++) {
               Setting var22 = (Setting)var16.get(var20);
               JsonObject var24 = toJson(var22, String.valueOf(var20));
               var18.add(var24);
            }

            var5.add("option_list", var18);
         }
      } else {
         var5.addProperty("option_value", var0.asString());
      }

      Map var15 = SettingFinder.getSubOptions(var0);
      if (var15 != null) {
         for (Object var19$raw : var15.keySet()) {
            String var19 = (String)var19$raw;
            Setting var21 = (Setting)var15.get(var19);
            JsonObject var23 = toJson(var21, var19);
            var23.addProperty("sub_option_of", var0.getName());
            var6.add(var23);
         }

         var5.add("sub_options", var6);
      }

      return var5;
   }

   public static List<JsonObject> serializeModules() {
      long var0 = b ^ 34046360829469L;
      long var2 = var0 ^ 5362573070993L;
      ArrayList var6 = new ArrayList();
      JsonObject var7 = new JsonObject();

      for (ModuleInfo var9 : Astolfo.INSTANCE.getModuleManager().getList()) {
         if (var9 instanceof Module) {
            JsonArray var10 = new JsonArray();
            Module var11 = (Module)var9;

            for (Setting var13 : var11.getSettings()) {
               var10.add(toJson(var13, var13.getName()));
            }

            JsonObject var14 = new JsonObject();
            var14.addProperty("id", var9.getName());
            var14.addProperty("key", var11.getKey());
            var14.addProperty("enabled", var11.isEnabled());
            var14.add("options", var10);
            var7.add(var9.getName(), var14);
         }
      }

      var6.add(var7);
      return var6;
   }

   public static void save(List<JsonObject> var0, String var1) {
      File var4 = new File(Astolfo.INSTANCE.getDirectory().getAbsolutePath(), var1 + ".json");
      FileWriter var5 = null;

      try {
         var5 = new FileWriter(var4);
         V.toJson(var0, var5);
         var5.close();
      } catch (IOException var7) {
         var7.printStackTrace();
      }
   }

   public static void load(String var0) {
      long var1 = b ^ 29066464234499L;
      long var3 = var1 ^ 515584549007L;

      try {
         File var6 = new File(Astolfo.INSTANCE.getDirectory().getAbsolutePath(), var0 + ".json");
         if (!var6.exists()) {
            return;
         }

         JsonReader var7 = null;

         try {
            var7 = new JsonReader(new FileReader(var6.getAbsolutePath()));
         } catch (FileNotFoundException var27) {
            var27.printStackTrace();
         }

         Type var8 = new JsonObjectListType().getType();
         List var9 = V.fromJson(var7, var8);

         for (ModuleInfo var11 : Astolfo.INSTANCE.getModuleManager().getList()) {
            if (var11 instanceof Module) {
               Module var12 = (Module)var11;

               for (Object var14$raw : var9) {
                  JsonObject var14 = (JsonObject)var14$raw;
                  if (var14.has(var11.getName())) {
                     JsonObject var15 = var14.getAsJsonObject(var12.getName());
                     if (var15.has("id") && var15.get("id").getAsString().equals(var11.getName())) {
                        if (var15.has("key")) {
                           var12.setKey(var15.get("key").getAsInt());
                        }

                        if (var15.has("enabled")) {
                           var12.setEnabled(var15.get("enabled").getAsBoolean());
                        }
                     }

                     JsonArray var16 = var15.getAsJsonArray("options");

                     for (int var17 = 0; var17 < var16.size(); var17++) {
                        JsonObject var18 = var16.get(var17).getAsJsonObject();
                        if (var18.has("option_id")) {
                           String var19 = var18.get("option_id").getAsString();

                           for (Setting var21 : var12.getSettings()) {
                              if (var21.getName().equals(var19)) {
                                 if (var21 instanceof MapSetting) {
                                    MapSetting var22 = (MapSetting)var21;
                                    if (var18.has("option_map")) {
                                       JsonArray var23 = var18.getAsJsonArray("option_map");

                                       for (int var24 = 0; var24 < var23.size(); var24++) {
                                          JsonObject var25 = var23.get(var24).getAsJsonObject();
                                          if (var25.has("option_id")) {
                                             Setting var26 = var22.getSetting(var25.get("option_id").getAsString());
                                             if (var25.has("option_value") && var26 != null) {
                                                var26.parse(var25.get("option_value").getAsString());
                                             }
                                          }
                                       }
                                    }
                                 } else {
                                    var21.parse(var18.get("option_value").getAsString());
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var28) {
         var28.printStackTrace();
         toasts.m("Failed to load config");
      }
   }

   public static String getGameJarPath() throws URISyntaxException {
      return new File(Minecraft.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
   }
}
