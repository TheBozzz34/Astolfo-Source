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
   private static final long b = 64771618837165L;

   public IrcMessageHandler() {
      super(PacketId.IRC_MESSAGE);
   }

   public void handle(IrcMessage var1, PacketOutput var2) {
      if (var1.type == IrcMessageType.B) {
         if (!ChatManager.g()) {
            return;
         }

         String var6 = "@" + mc.mouseHelper.Y().getAccount().getUsername();
         String var7 = var1.message;
         if (var1.message.contains(var6)) {
            var7 = var7.replace(var6, EnumChatFormatting.BOLD + "" + EnumChatFormatting.YELLOW + var6 + EnumChatFormatting.RESET);
            if (ChatManager.X()) {
               Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), 1.0F));
            }
         }

         if (var1.sender.isEmpty()) {
            chat.D(false, false).S().i("[A]").M().M(EnumChatFormatting.GOLD).i(" ").i(var7).N().b();
         } else {
            chat.D(false, false).S().i("[A] ").M().M(EnumChatFormatting.GOLD).i(var1.sender).M(EnumChatFormatting.GRAY).i(": ").i(var7).N().b();
         }
      }
   }
}
