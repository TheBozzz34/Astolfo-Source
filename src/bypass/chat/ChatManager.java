package bypass.chat;

import bypass.manager.JsonFileManager;
import com.google.gson.JsonObject;
import java.io.File;

public class ChatManager extends JsonFileManager<ChatSettings, JsonObject> {
   private static boolean chatEnabled = true;
   private static boolean pingsEnabled = true;

   public ChatManager(File file) {
      super(file, "chat.json");
   }

   public void loadEntry(String string, JsonObject jsonObject) {
      if (jsonObject.has("token")) {
         chatEnabled = jsonObject.get("token").getAsBoolean();
      }

      if (jsonObject.has("pings")) {
         pingsEnabled = jsonObject.get("pings").getAsBoolean();
      }
   }

   @Override
   public void loadDefaults() {
   }

   @Override
   public void save() {
      this.items.add(new ChatSettings(chatEnabled, pingsEnabled));
      super.save();
   }

   public static void setChatEnabled(boolean bl) {
      chatEnabled = bl;
   }

   public static void setPingsEnabled(boolean bl) {
      pingsEnabled = bl;
   }

   public static boolean isPingsEnabled() {
      return pingsEnabled;
   }

   public static boolean isChatEnabled() {
      return chatEnabled;
   }
}
