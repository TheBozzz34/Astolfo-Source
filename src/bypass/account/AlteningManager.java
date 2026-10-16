package bypass.account;

import bypass.gui.screen.AlteningScreen;
import bypass.manager.JsonFileManager;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.Base64;

public class AlteningManager extends JsonFileManager<AlteningAccount, JsonObject> {
   public AlteningManager(File file) {
      super(file, "altening.json");
   }

   public void loadEntry(String string, JsonObject jsonObject) {
      AlteningScreen.apiKey = new String(Base64.getDecoder().decode(jsonObject.get("token").getAsString().getBytes()));
      if (jsonObject.has("addAccount")) {
         AlteningScreen.addToAltList = jsonObject.get("addAccount").getAsBoolean();
      }
   }

   @Override
   public void save() {
      this.items.add(new AlteningAccount(AlteningScreen.apiKey, AlteningScreen.addToAltList));
      super.save();
   }

   @Override
   public void loadDefaults() {
   }
}
