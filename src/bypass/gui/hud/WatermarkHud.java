package bypass.gui.hud;

import bypass.auth.DisconnectHandler;
import bypass.module.visuals.HUD;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class WatermarkHud extends HudElement {
   private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

   public WatermarkHud() {
      super("Watermark", 4.0F, 4.0F, 12, 9);
      this.setVisible(true);
   }

   @Override
   public void render(int i, int j) {
      String string = HUD.getWatermarkText();
      string = string.replace("&", "§");
      string = string.replace("%fps%", String.valueOf(Minecraft.getDebugFPS()));
      string = string.replace("%time%", this.timeFormat.format(new Date()));
      if (mc.thePlayer.getPlayerInfo() != null && mc.thePlayer.getPlayerInfo().getResponseTime() > 1) {
         string = string.replace("%ping%", mc.thePlayer.getPlayerInfo().getResponseTime() + "ms");
      } else {
         string = string.replace("%ping%", "");
      }

      if (mc.getCurrentServerData() != null) {
         string = string.replace("%ip%", mc.getCurrentServerData().serverIP);
      }

      string = string.replace("%user%", mc.mouseHelper.getAuthSession().getAccount().getUsername());
      if (DisconnectHandler.disconnected) {
         string = string + EnumChatFormatting.GRAY + " - DISCONNECTED";
      }

      this.setWidth(client.getCustomFontRenderer().getStringWidth(string));
      client.getCustomFontRenderer().drawStringWithShadow("§l" + string.charAt(0), this.getX(), this.getYPos() - 2.0F, client.getColor().getRGB());
      client.getCustomFontRenderer()
         .drawStringWithShadow(
            string.substring(1), this.getX() + client.getCustomFontRenderer().getStringWidth(string.substring(0, 1)), this.getYPos() - 2.0F, Color.WHITE.getRGB()
         );
   }

   @Override
   public void onClick(int i) {
   }
}
