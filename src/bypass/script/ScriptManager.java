package bypass.script;

import bypass.ClientAccess;
import bypass.lua.LuaApi;
import bypass.manager.JsonFileManager;
import bypass.module.ModuleInfo;
import bypass.module.scripts.ScriptModule;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ScriptManager extends JsonFileManager<Script, JsonObject> implements ClientAccess {
   public static final File SCRIPTS_DIR = new File(client.getDirectory().getAbsolutePath(), "scripts");
   public static final Map<String, Boolean> savedStates = new HashMap<>();
   private final ArrayList<String> scriptNames = new ArrayList<>();
   public static final Script unsafeFunctions = new Script("troleado", "unsafe-functions");

   public ScriptManager() {
      super(SCRIPTS_DIR, "scripts.json");
      this.items.add(unsafeFunctions);
   }

   public void loadEntry(String string, JsonObject jsonObject) {
      if (string.equals("unsafe-functions")) {
         unsafeFunctions.setEnabled(jsonObject.get("state").getAsBoolean());
      }

      savedStates.put(string, jsonObject.get("state").getAsBoolean());
   }

   @Override
   public void loadDefaults() {
   }

   private void loadScriptFiles() {
      try (Stream stream = Files.walk(Paths.get(SCRIPTS_DIR.getAbsolutePath()))) {
         stream.forEach(object -> {
            String string = ((java.nio.file.Path)object).getFileName().toString();
            String string2 = object.toString();
            if (string.endsWith(".lua")) {
               Script script = new Script(string2, string);
               if (!script.hasLoadError()) {
                  this.items.add(script);
                  if (savedStates.get(script.name) != null && savedStates.get(script.name) && script.loadAndRun()) {
                     script.enable();
                  }
               } else {
                  chat.printError("Error loading " + script.name + " " + script.errorMessage);
               }
            }
         });
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   private void W_void() {
   }

   @Override
   public void onLoaded() {
      this.W_void();
      this.loadScriptFiles();
   }

   public boolean rerunScript(String string) {
      for (Script script : this.items) {
         if (script.name.equals(string)) {
            script.runFile();
            return true;
         }
      }

      return false;
   }

   public Script getScript(String string) {
      for (Script script : this.items) {
         if (script.name.equals(string)) {
            return script;
         }
      }

      return null;
   }

   private void unregisterScriptModules() {
      List list = client.getModuleManager().getList();
      ArrayList arrayList = new ArrayList();
      int i = 0;

      for (int j = list.size(); i < j; i++) {
         ModuleInfo moduleInfo = (ModuleInfo)list.get(i);
         if (moduleInfo instanceof ScriptModule) {
            client.getEventBus().unsubscribe(moduleInfo);
            arrayList.add(moduleInfo);
         }
      }

      for (Object object : arrayList) {
         ModuleInfo moduleInfo2 = (ModuleInfo)object;
         client.getModuleManager().remove(moduleInfo2);
      }
   }

   public void enableAll() {
      for (Script script : this.items) {
         if (script != null) {
            script.enable();
         }
      }
   }

   public void disableAll() {
      for (Script script : this.items) {
         script.disable();
      }
   }

   public void reload() {
      this.unregisterScriptModules();
      LuaApi.settingsByName.clear();
      LuaApi.entitiesById.clear();
      this.items.clear();
      this.items.add(unsafeFunctions);
      this.loadScriptFiles();
      System.gc();
      chat.print("Reloaded " + this.items.size() + " scripts");
   }

   public ArrayList<String> getScriptNames() {
      return this.scriptNames;
   }
}
