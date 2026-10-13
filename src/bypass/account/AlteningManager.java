package bypass.account;

import bypass.gui.screen.AlteningScreen;
import bypass.manager.JsonFileManager;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.Base64;

public class AlteningManager extends JsonFileManager<AlteningAccount, JsonObject> {
   private static final long c = 39403370639183L;

   public AlteningManager(File var1) {
      super(var1, "altening.json");
   }

   public void t(String var1, JsonObject var2) {
      AlteningScreen.b = new String(Base64.getDecoder().decode(var2.get("token").getAsString().getBytes()));
      if (var2.has("addAccount")) {
         AlteningScreen.f = var2.get("addAccount").getAsBoolean();
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
