package bypass;

import net.minecraft.util.ChatComponentText;

public class ChatMessage implements ClientAccess {
   private final ChatComponentText T;
   public final ClientChat E;
   private static final long b = 136952962367006L;

   public ChatMessage(ClientChat clientChat, ChatComponentText chatComponentText) {
      this.E = clientChat;
      this.T = chatComponentText;
   }

   public void b() {
      if (mc.thePlayer != null) {
         mc.thePlayer.addChatMessage(this.T);
      }
   }

   public ChatComponentText u() {
      return this.T;
   }
}
