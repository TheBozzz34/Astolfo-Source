package bypass.manager;

import bypass.ClientAccess;
import bypass.util.FileUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

public abstract class JsonFileManager<T extends JsonSerializable, U extends JsonElement> extends Manager<T> implements Loadable, ClientAccess {
   public final File file;

   public JsonFileManager(File file, String string) {
      this.file = new File(file, string);
      if (!file.exists()) {
         file.mkdir();
      }
   }

   public abstract void loadEntry(String string, U jsonElement);

   public abstract void loadDefaults();

   @Override
   public void load() {
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
            this.loadDefaults();
            return;
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      }

      JsonElement jsonElement = FileUtil.readJson(this.file);
      if (!(jsonElement instanceof JsonObject)) {
         this.loadDefaults();
      } else {
         JsonObject jsonObject = (JsonObject)jsonElement;

         for (Entry entry : jsonObject.entrySet()) {
            try {
               this.loadEntry((String)entry.getKey(), (U)entry.getValue());
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }

         this.onLoaded();
      }
   }

   public void onLoaded() {
   }

   @Override
   public void save() {
      JsonObject jsonObject = new JsonObject();

      for (JsonSerializable jsonSerializable : this.getList()) {
         this.writeEntry(jsonObject, (T)jsonSerializable);
      }

      FileUtil.writeJson(this.file, jsonObject);
   }

   public void writeEntry(JsonObject jsonObject, T jsonSerializable) {
      jsonObject.add(jsonSerializable.getKey(), jsonSerializable.toJson());
   }
}
