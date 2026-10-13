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
   public void render(int var1, int var2) {
      long var3 = d ^ 36284522561213L;
      long var5 = var3 ^ 39308001293406L;
      String var9 = HUD.getWatermarkText();
      var9 = var9.replace("&", "§");
      var9 = var9.replace("%fps%", String.valueOf(Minecraft.B()));
      var9 = var9.replace("%time%", this.B.format(new Date()));
      if (mc.thePlayer.getPlayerInfo() != null && mc.thePlayer.getPlayerInfo().getResponseTime() > 1) {
         var9 = var9.replace("%ping%", mc.thePlayer.getPlayerInfo().getResponseTime() + "ms");
      } else {
         var9 = var9.replace("%ping%", "");
      }

      if (mc.getCurrentServerData() != null) {
         var9 = var9.replace("%ip%", mc.getCurrentServerData().serverIP);
      }

      var9 = var9.replace("%user%", mc.mouseHelper.Y().getAccount().getUsername());
      if (DisconnectHandler.disconnected) {
         var9 = var9 + EnumChatFormatting.GRAY + " - DISCONNECTED";
      }

      this.t(client.getCustomFontRenderer().getStringWidth(var9));
      client.getCustomFontRenderer().drawStringWithShadow("§l" + var9.charAt(0), this.M(), this.E_float() - 2.0F, client.getColor().getRGB());
      client.getCustomFontRenderer()
         .drawStringWithShadow(
            var9.substring(1), this.M() + client.getCustomFontRenderer().getStringWidth(var9.substring(0, 1)), this.E_float() - 2.0F, Color.WHITE.getRGB()
         );
   }

   @Override
   public void l(int var1) {
   }
}
