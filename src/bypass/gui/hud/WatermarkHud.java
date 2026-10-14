package bypass.gui.hud;

import bypass.auth.DisconnectHandler;
import bypass.module.visuals.HUD;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class WatermarkHud extends HudElement {
   private final SimpleDateFormat B = new SimpleDateFormat("hh:mm a");
   private static final long d = 59441810956495L;

   public WatermarkHud() {
      super("Watermark", 4.0F, 4.0F, 12, 9);
      this.f(true);
   }

   @Override
   public void render(int i, int j) {
      long m = d ^ 36284522561213L;
      long n = m ^ 39308001293406L;
      String string = HUD.getWatermarkText();
      string = string.replace("&", "§");
      string = string.replace("%fps%", String.valueOf(Minecraft.B()));
      string = string.replace("%time%", this.B.format(new Date()));
      if (mc.thePlayer.getPlayerInfo() != null && mc.thePlayer.getPlayerInfo().getResponseTime() > 1) {
         string = string.replace("%ping%", mc.thePlayer.getPlayerInfo().getResponseTime() + "ms");
      } else {
         string = string.replace("%ping%", "");
      }

      if (mc.getCurrentServerData() != null) {
         string = string.replace("%ip%", mc.getCurrentServerData().serverIP);
      }

      string = string.replace("%user%", mc.mouseHelper.Y().getAccount().getUsername());
      if (DisconnectHandler.disconnected) {
         string = string + EnumChatFormatting.GRAY + " - DISCONNECTED";
      }

      this.t(client.getCustomFontRenderer().getStringWidth(string));
      client.getCustomFontRenderer().drawStringWithShadow("§l" + string.charAt(0), this.M(), this.E_float() - 2.0F, client.getColor().getRGB());
      client.getCustomFontRenderer()
         .drawStringWithShadow(
            string.substring(1), this.M() + client.getCustomFontRenderer().getStringWidth(string.substring(0, 1)), this.E_float() - 2.0F, Color.WHITE.getRGB()
         );
   }

   @Override
   public void l(int i) {
   }
}
