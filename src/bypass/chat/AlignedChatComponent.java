package bypass.chat;

import bypass.ClientAccess;
import bypass.ClientChat;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;

public class AlignedChatComponent extends ChatComponentStyle implements ClientAccess {
   public final TextAlignment alignment;
   public int padWidth;
   public final ClientChat clientChat;

   public AlignedChatComponent(ClientChat clientChat, TextAlignment textAlignment) {
      this.clientChat = clientChat;
      this.padWidth = 0;
      this.alignment = textAlignment;
   }

   @Override
   public String getUnformattedTextForChat() {
      return this.padWidth <= 0 ? "" : StringUtils.repeat(' ', this.padWidth / mc.fontRendererObj.getStringWidth(" "));
   }

   @Override
   public IChatComponent createCopy() {
      return this;
   }
}
