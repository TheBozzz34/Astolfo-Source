package bypass.manager;

import bypass.ClientAccess;
import bypass.util.FileUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

public abstract class JsonFileManager<T extends JsonSerializable, U extends JsonElement> extends Manager<T> implements Loadable, ClientAccess {
   public final File R;
   private static final long b = 51472734298755L;

   public JsonFileManager(File var1, String var2) {
      this.R = new File(var1, var2);
      if (!var1.exists()) {
         var1.mkdir();
      }
   }

   public abstract void t(String var1, U var2);

   public abstract void I();

   @Override
   public void load() {
      if (!this.R.exists()) {
         try {
            this.R.createNewFile();
            this.I();
            return;
         } catch (IOException var11) {
            var11.printStackTrace();
         }
      }

      JsonElement var4 = FileUtil.readJson(this.R);
      if (!(var4 instanceof JsonObject)) {
         this.I();
      } else {
         JsonObject var6 = (JsonObject)var4;

         for (Entry var8 : var6.entrySet()) {
            try {
               this.t((String)var8.getKey(), (U)var8.getValue());
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         }

         this.G();
      }
   }

   public void G() {
   }

   @Override
   public void a() {
      JsonObject var5 = new JsonObject();

      for (JsonSerializable var7 : this.getList()) {
         this.e(var5, (T)var7);
      }

      FileUtil.writeJson(this.R, var5);
   }

   public void e(JsonObject var1, T var2) {
      var1.add(var2.c(), var2.V());
   }
}
