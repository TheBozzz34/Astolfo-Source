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
   private static final long a = 103264722357314L;

   public void I(String var1) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.f(var1);
      } else {
         this.y(var1).N().b();
      }
   }

   public void R(String var1) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.f(var1);
      } else {
         this.y(var1).M(EnumChatFormatting.GREEN).N().b();
      }
   }

   public void P(String var1) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.f(var1);
      } else {
         this.y(var1).M(EnumChatFormatting.YELLOW).N().b();
      }
   }

   public void f(String var1) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.f(var1);
      } else {
         this.y(var1).M(EnumChatFormatting.RED).N().b();
      }
   }

   public String h(String var1) {
      return Character.toUpperCase(var1.charAt(0)) + var1.substring(1);
   }

   public ChatMessageBuilder U() {
      ChatMessageBuilder var1 = new ChatMessageBuilder(this);
      ChatMessageBuilder.Y(var1).appendSibling(this.Y);
      return var1;
   }

   public ChatMessageBuilder D(boolean var1, boolean var2) {
      ChatMessageBuilder var5 = var1 ? this.U() : new ChatMessageBuilder(this);
      ChatMessageBuilder.m(var5, var2);
      return var5;
   }

   public ChatMessageBuilder y(String var1) {
      return this.U().i(var1);
   }

   public static Pattern N() {
      return M;
   }

   public static EnumChatFormatting K(ClientChat var0) {
      return var0.F;
   }
}
