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

   public void t(String var1, JsonObject var2) {
      if (var1.equals("unsafe-functions")) {
         N.j(var2.get("state").getAsBoolean());
      }

      L.put(var1, var2.get("state").getAsBoolean());
   }

   @Override
   public void I() {
   }

   private void E() {
      try (Stream var4 = Files.walk(Paths.get(B.getAbsolutePath()))) {
         var4.forEach(var1 -> {
            String var5 = ((java.nio.file.Path)var1).getFileName().toString();
            String var6 = var1.toString();
            if (var5.endsWith(".lua")) {
               Script var7 = new Script(var6, var5);
               if (!var7.K()) {
                  this.C.add(var7);
                  if (L.get(var7.C) != null && L.get(var7.C) && var7.o()) {
                     var7.A();
                  }
               } else {
                  chat.f("Error loading " + var7.C + " " + var7.Q);
               }
            }
         });
      } catch (IOException var17) {
         var17.printStackTrace();
      }
   }

   private void W_void() {
   }

   @Override
   public void G() {
      this.W_void();
      this.E();
   }

   public boolean v(String var1) {
      for (Script var6 : this.C) {
         if (var6.C.equals(var1)) {
            var6.r();
            return true;
         }
      }

      return false;
   }

   public Script T(String var1) {
      for (Script var6 : this.C) {
         if (var6.C.equals(var1)) {
            return var6;
         }
      }

      return null;
   }

   private void N() {
      long var1 = c ^ 76255384151247L;
      long var3 = var1 ^ 40109808716404L;
      List var6 = client.getModuleManager().getList();
      ArrayList var7 = new ArrayList();
      int var8 = 0;

      for (int var9 = var6.size(); var8 < var9; var8++) {
         ModuleInfo var10 = (ModuleInfo)var6.get(var8);
         if (var10 instanceof ScriptModule) {
            client.getEventBus().unsubscribe(var10);
            var7.add(var10);
         }
      }

      for (Object var12$raw : var7) {
         ModuleInfo var12 = (ModuleInfo)var12$raw;
         client.getModuleManager().remove(var12);
      }
   }

   public void X() {
      for (Script var5 : this.C) {
         if (var5 != null) {
            var5.A();
         }
      }
   }

   public void t() {
      for (Script var5 : this.C) {
         var5.x();
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
