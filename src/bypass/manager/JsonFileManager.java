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

   public JsonFileManager(File file, String string) {
      this.R = new File(file, string);
      if (!file.exists()) {
         file.mkdir();
      }
   }

   public abstract void t(String string, U jsonElement);

   public abstract void I();

   @Override
   public void load() {
      if (!this.R.exists()) {
         try {
            this.R.createNewFile();
            this.I();
            return;
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      }

      JsonElement jsonElement = FileUtil.readJson(this.R);
      if (!(jsonElement instanceof JsonObject)) {
         this.I();
      } else {
         JsonObject jsonObject = (JsonObject)jsonElement;

         for (Entry entry : jsonObject.entrySet()) {
            try {
               this.t((String)entry.getKey(), (U)entry.getValue());
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }

         this.G();
      }
   }

   public void G() {
   }

   @Override
   public void a() {
      JsonObject jsonObject = new JsonObject();

      for (JsonSerializable jsonSerializable : this.getList()) {
         this.e(jsonObject, (T)jsonSerializable);
      }

      FileUtil.writeJson(this.R, jsonObject);
   }

   public void e(JsonObject jsonObject, T jsonSerializable) {
      jsonObject.add(jsonSerializable.c(), jsonSerializable.V());
   }
}
