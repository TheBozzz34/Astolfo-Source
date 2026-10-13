package bypass.chat;

import bypass.manager.JsonFileManager;
import com.google.gson.JsonObject;
import java.io.File;

public class ChatManager extends JsonFileManager<ChatSettings, JsonObject> {
   private static boolean v = true;
   private static boolean p = true;
   private static final long c = 113002365647811L;

   public ChatManager(File var1) {
      super(var1, "chat.json");
   }

   public void t(String var1, JsonObject var2) {
      if (var2.has("token")) {
         v = var2.get("token").getAsBoolean();
      }

      if (var2.has("pings")) {
         p = var2.get("pings").getAsBoolean();
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

   public static void D(boolean var0) {
      v = var0;
   }

   public static void x(boolean var0) {
      p = var0;
   }

   public static boolean X() {
      return p;
   }

   public static boolean g() {
      return v;
   }
}
