package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.auth.packet.IrcMessage;
import bypass.auth.packet.IrcMessageType;
import bypass.auth.packet.PacketId;
import bypass.chat.ChatManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class IrcMessageHandler extends PacketHandler<IrcMessage> {
   public IrcMessageHandler() {
      super(PacketId.IRC_MESSAGE);
   }

   public void handle(IrcMessage ircMessage, PacketOutput packetOutput) {
      if (ircMessage.type == IrcMessageType.B) {
         if (!ChatManager.g()) {
            return;
         }

         String string = "@" + mc.mouseHelper.Y().getAccount().getUsername();
         String string2 = ircMessage.message;
         if (ircMessage.message.contains(string)) {
            string2 = string2.replace(string, EnumChatFormatting.BOLD + "" + EnumChatFormatting.YELLOW + string + EnumChatFormatting.RESET);
            if (ChatManager.X()) {
               Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), 1.0F));
            }
         }

         if (ircMessage.sender.isEmpty()) {
            chat.D(false, false).S().i("[A]").M().M(EnumChatFormatting.GOLD).i(" ").i(string2).N().b();
         } else {
            chat.D(false, false).S().i("[A] ").M().M(EnumChatFormatting.GOLD).i(ircMessage.sender).M(EnumChatFormatting.GRAY).i(": ").i(string2).N().b();
         }
      }
   }
}
