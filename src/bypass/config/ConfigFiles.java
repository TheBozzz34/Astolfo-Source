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
   public static final Gson GSON = new Gson();
   public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

   private static JsonObject toJson(Setting<?> setting, String string) {
      JsonObject jsonObject = new JsonObject();
      JsonArray jsonArray = new JsonArray();
      jsonObject.addProperty("option_id", string);
      if (setting instanceof MapSetting) {
         MapSetting mapSetting = (MapSetting)setting;
         Map map = mapSetting.getSettings();
         if (map != null && !map.isEmpty()) {
            JsonArray jsonArray2 = new JsonArray();

            for (Object object : map.keySet()) {
               String string2 = (String)object;
               Setting setting2 = (Setting)map.get(string2);
               JsonObject jsonObject2 = toJson(setting2, string2);
               jsonArray2.add(jsonObject2);
            }

            jsonObject.add("option_map", jsonArray2);
         }
      } else if (setting instanceof ListSetting) {
         ListSetting listSetting = (ListSetting)setting;
         List list = listSetting.getSettings();
         if (list != null && !list.isEmpty()) {
            JsonArray jsonArray3 = new JsonArray();

            for (int i = 0; i < list.size(); i++) {
               Setting setting3 = (Setting)list.get(i);
               JsonObject jsonObject3 = toJson(setting3, String.valueOf(i));
               jsonArray3.add(jsonObject3);
            }

            jsonObject.add("option_list", jsonArray3);
         }
      } else {
         jsonObject.addProperty("option_value", setting.asString());
      }

      Map map2 = SettingFinder.getSubOptions(setting);
      if (map2 != null) {
         for (Object object2 : map2.keySet()) {
            String string3 = (String)object2;
            Setting setting4 = (Setting)map2.get(string3);
            JsonObject jsonObject4 = toJson(setting4, string3);
            jsonObject4.addProperty("sub_option_of", setting.getName());
            jsonArray.add(jsonObject4);
         }

         jsonObject.add("sub_options", jsonArray);
      }

      return jsonObject;
   }

   public static List<JsonObject> serializeModules() {
      ArrayList arrayList = new ArrayList();
      JsonObject jsonObject = new JsonObject();

      for (ModuleInfo moduleInfo : Astolfo.INSTANCE.getModuleManager().getList()) {
         if (moduleInfo instanceof Module) {
            JsonArray jsonArray = new JsonArray();
            Module module = (Module)moduleInfo;

            for (Setting setting : module.getSettings()) {
               jsonArray.add(toJson(setting, setting.getName()));
            }

            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("id", moduleInfo.getName());
            jsonObject2.addProperty("key", module.getKey());
            jsonObject2.addProperty("enabled", module.isEnabled());
            jsonObject2.add("options", jsonArray);
            jsonObject.add(moduleInfo.getName(), jsonObject2);
         }
      }

      arrayList.add(jsonObject);
      return arrayList;
   }

   public static void save(List<JsonObject> list, String string) {
      File file = new File(Astolfo.INSTANCE.getDirectory().getAbsolutePath(), string + ".json");
      FileWriter fileWriter = null;

      try {
         fileWriter = new FileWriter(file);
         GSON.toJson(list, fileWriter);
         fileWriter.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   public static void load(String string) {
      try {
         File file = new File(Astolfo.INSTANCE.getDirectory().getAbsolutePath(), string + ".json");
         if (!file.exists()) {
            return;
         }

         JsonReader jsonReader = null;

         try {
            jsonReader = new JsonReader(new FileReader(file.getAbsolutePath()));
         } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
         }

         Type type = new JsonObjectListType().getType();
         List list = GSON.fromJson(jsonReader, type);

         for (ModuleInfo moduleInfo : Astolfo.INSTANCE.getModuleManager().getList()) {
            if (moduleInfo instanceof Module) {
               Module module = (Module)moduleInfo;

               for (Object object : list) {
                  JsonObject jsonObject = (JsonObject)object;
                  if (jsonObject.has(moduleInfo.getName())) {
                     JsonObject jsonObject2 = jsonObject.getAsJsonObject(module.getName());
                     if (jsonObject2.has("id") && jsonObject2.get("id").getAsString().equals(moduleInfo.getName())) {
                        if (jsonObject2.has("key")) {
                           module.setKey(jsonObject2.get("key").getAsInt());
                        }

                        if (jsonObject2.has("enabled")) {
                           module.setEnabled(jsonObject2.get("enabled").getAsBoolean());
                        }
                     }

                     JsonArray jsonArray = jsonObject2.getAsJsonArray("options");

                     for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonObject3 = jsonArray.get(i).getAsJsonObject();
                        if (jsonObject3.has("option_id")) {
                           String string2 = jsonObject3.get("option_id").getAsString();

                           for (Setting setting : module.getSettings()) {
                              if (setting.getName().equals(string2)) {
                                 if (setting instanceof MapSetting) {
                                    MapSetting mapSetting = (MapSetting)setting;
                                    if (jsonObject3.has("option_map")) {
                                       JsonArray jsonArray2 = jsonObject3.getAsJsonArray("option_map");

                                       for (int j = 0; j < jsonArray2.size(); j++) {
                                          JsonObject jsonObject4 = jsonArray2.get(j).getAsJsonObject();
                                          if (jsonObject4.has("option_id")) {
                                             Setting setting2 = mapSetting.getSetting(jsonObject4.get("option_id").getAsString());
                                             if (jsonObject4.has("option_value") && setting2 != null) {
                                                setting2.parse(jsonObject4.get("option_value").getAsString());
                                             }
                                          }
                                       }
                                    }
                                 } else {
                                    setting.parse(jsonObject3.get("option_value").getAsString());
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
         toasts.warning("Failed to load config");
      }
   }

   public static String getGameJarPath() throws URISyntaxException {
      return new File(Minecraft.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
   }
}
