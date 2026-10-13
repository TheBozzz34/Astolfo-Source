package bypass;

import net.minecraft.util.ChatComponentText;

public class ChatMessage implements ClientAccess {
   private final ChatComponentText T;
   public final ClientChat E;
   private static final long b = 136952962367006L;

   public ChatMessage(ClientChat var1, ChatComponentText var2) {
      this.E = var1;
      this.T = var2;
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
