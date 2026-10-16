package bypass;

import net.minecraft.util.ChatComponentText;

public class ChatMessage implements ClientAccess {
   private final ChatComponentText component;
   public final ClientChat clientChat;

   public ChatMessage(ClientChat clientChat, ChatComponentText chatComponentText) {
      this.clientChat = clientChat;
      this.component = chatComponentText;
   }

   public void send() {
      if (mc.thePlayer != null) {
         mc.thePlayer.addChatMessage(this.component);
      }
   }

   public ChatComponentText getComponent() {
      return this.component;
   }
}
