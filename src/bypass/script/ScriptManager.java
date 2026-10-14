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
   public static final File B = new File(client.getDirectory().getAbsolutePath(), "scripts");
   public static final Map<String, Boolean> L = new HashMap<>();
   private final ArrayList<String> Z = new ArrayList<>();
   public static final Script N = new Script("troleado", "unsafe-functions");
   private static final long c = 94934315717384L;

   public ScriptManager() {
      super(B, "scripts.json");
      this.C.add(N);
   }

   public void t(String string, JsonObject jsonObject) {
      if (string.equals("unsafe-functions")) {
         N.j(jsonObject.get("state").getAsBoolean());
      }

      L.put(string, jsonObject.get("state").getAsBoolean());
   }

   @Override
   public void I() {
   }

   private void E() {
      try (Stream stream = Files.walk(Paths.get(B.getAbsolutePath()))) {
         stream.forEach(object -> {
            String string = ((java.nio.file.Path)object).getFileName().toString();
            String string2 = object.toString();
            if (string.endsWith(".lua")) {
               Script script = new Script(string2, string);
               if (!script.K()) {
                  this.C.add(script);
                  if (L.get(script.C) != null && L.get(script.C) && script.o()) {
                     script.A();
                  }
               } else {
                  chat.f("Error loading " + script.C + " " + script.Q);
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
   public void G() {
      this.W_void();
      this.E();
   }

   public boolean v(String string) {
      for (Script script : this.C) {
         if (script.C.equals(string)) {
            script.r();
            return true;
         }
      }

      return false;
   }

   public Script T(String string) {
      for (Script script : this.C) {
         if (script.C.equals(string)) {
            return script;
         }
      }

      return null;
   }

   private void N() {
      long l = c ^ 76255384151247L;
      long m = l ^ 40109808716404L;
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

   public void X() {
      for (Script script : this.C) {
         if (script != null) {
            script.A();
         }
      }
   }

   public void t() {
      for (Script script : this.C) {
         script.x();
      }
   }

   public void M() {
      this.N();
      LuaApi.f.clear();
      LuaApi.Z.clear();
      this.C.clear();
      this.C.add(N);
      this.E();
      System.gc();
      chat.I("Reloaded " + this.C.size() + " scripts");
   }

   public ArrayList<String> W() {
      return this.Z;
   }
}
