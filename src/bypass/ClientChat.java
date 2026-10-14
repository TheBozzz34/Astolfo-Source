package bypass;

import bypass.command.UnusedTeleportCommand;
import bypass.module.visuals.HUD;
import java.util.regex.Pattern;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.EnumChatFormatting;

public enum ClientChat {
   CHAT;

   private static final Pattern M = Pattern.compile(
      "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)", 42
   );
   private final EnumChatFormatting F = EnumChatFormatting.AQUA;
   private final ChatComponentStyle Y = this.D(false, false).i("§8> §f").N().u();
   private static final ClientChat[] C = new ClientChat[]{CHAT};

   public void I(String string) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.f(string);
      } else {
         this.y(string).N().b();
      }
   }

   public void R(String string) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.f(string);
      } else {
         this.y(string).M(EnumChatFormatting.GREEN).N().b();
      }
   }

   public void P(String string) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.f(string);
      } else {
         this.y(string).M(EnumChatFormatting.YELLOW).N().b();
      }
   }

   public void f(String string) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.f(string);
      } else {
         this.y(string).M(EnumChatFormatting.RED).N().b();
      }
   }

   public String h(String string) {
      return Character.toUpperCase(string.charAt(0)) + string.substring(1);
   }

   public ChatMessageBuilder U() {
      ChatMessageBuilder chatMessageBuilder = new ChatMessageBuilder(this);
      ChatMessageBuilder.Y(chatMessageBuilder).appendSibling(this.Y);
      return chatMessageBuilder;
   }

   public ChatMessageBuilder D(boolean bl, boolean bl2) {
      ChatMessageBuilder chatMessageBuilder = bl ? this.U() : new ChatMessageBuilder(this);
      ChatMessageBuilder.m(chatMessageBuilder, bl2);
      return chatMessageBuilder;
   }

   public ChatMessageBuilder y(String string) {
      return this.U().i(string);
   }

   public static Pattern N() {
      return M;
   }

   public static EnumChatFormatting K(ClientChat clientChat) {
      return clientChat.F;
   }
}
