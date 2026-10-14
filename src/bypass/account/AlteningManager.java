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

   public void t(String string, JsonObject jsonObject) {
      AlteningScreen.b = new String(Base64.getDecoder().decode(jsonObject.get("token").getAsString().getBytes()));
      if (jsonObject.has("addAccount")) {
         AlteningScreen.f = jsonObject.get("addAccount").getAsBoolean();
      }
   }

   @Override
   public void a() {
      this.C.add(new AlteningAccount(AlteningScreen.b, AlteningScreen.f));
      super.a();
   }

   @Override
   public void I() {
   }
}
