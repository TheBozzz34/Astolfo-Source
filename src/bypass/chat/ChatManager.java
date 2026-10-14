package bypass.chat;

import bypass.manager.JsonFileManager;
import com.google.gson.JsonObject;
import java.io.File;

public class ChatManager extends JsonFileManager<ChatSettings, JsonObject> {
   private static boolean v = true;
   private static boolean p = true;
   private static final long c = 113002365647811L;

   public ChatManager(File file) {
      super(file, "chat.json");
   }

   public void t(String string, JsonObject jsonObject) {
      if (jsonObject.has("token")) {
         v = jsonObject.get("token").getAsBoolean();
      }

      if (jsonObject.has("pings")) {
         p = jsonObject.get("pings").getAsBoolean();
      }
   }

   @Override
   public void I() {
   }

   @Override
   public void a() {
      this.C.add(new ChatSettings(v, p));
      super.a();
   }

   public static void D(boolean bl) {
      v = bl;
   }

   public static void x(boolean bl) {
      p = bl;
   }

   public static boolean X() {
      return p;
   }

   public static boolean g() {
      return v;
   }
}
