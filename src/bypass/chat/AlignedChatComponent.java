package bypass.chat;

import bypass.ClientAccess;
import bypass.ClientChat;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;

public class AlignedChatComponent extends ChatComponentStyle implements ClientAccess {
   public final TextAlignment f;
   public int x;
   public final ClientChat O;
   private static final long b = 80347999039298L;
   private static final long c = 5391170609252139040L;

   public AlignedChatComponent(ClientChat clientChat, TextAlignment textAlignment) {
      this.O = clientChat;
      this.x = 0;
      this.f = textAlignment;
   }

   @Override
   public String getUnformattedTextForChat() {
      return this.x <= 0 ? "" : StringUtils.repeat((char)((int)c), this.x / mc.fontRendererObj.getStringWidth(" "));
   }

   @Override
   public IChatComponent createCopy() {
      return this;
   }
}
