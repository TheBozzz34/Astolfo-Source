package bypass.gui.hud;

import bypass.gui.component.Component;
import bypass.module.exploits.Denicker;
import bypass.module.visuals.PlayerList;
import bypass.render.RenderUtil;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class PlayerListHud extends Component {
   private final Map<String, String> playerTags = new LinkedHashMap<>();
   private int backgroundColor = new Color(17, 17, 17, 197).getRGB();

   public PlayerListHud(float f, float g, int i, int j) {
      super("Player list", f, g, i, j);
   }

   @Override
   public void onClick(int i) {
   }

   @Override
   public void render(int i, int j) {
      float f = this.getX();
      float g = this.getYPos();
      float h = this.getWidth();
      float f2 = this.getHeight();
      RenderUtil.drawRect(f, g, f + h, g + f2, new Color(1, 1, 1, 180).getRGB());
      RenderUtil.drawRect(f, g, f + h, g + 15.0F, new Color(1, 1, 1, 120).getRGB());
      mc.fontRendererObj.drawStringWithShadow(this.getName(), f + 3.0F, g + 3.0F, -1);
      byte b = 15;
      int k = 0;

      for (Entry entry : this.playerTags.entrySet()) {
         boolean bl = false;
         if (!((String)entry.getValue()).contains("kill")
            && !((String)entry.getKey()).equals(PlayerList.RED_TEAM)
            && !((String)entry.getKey()).equals(PlayerList.BLUE_TEAM)
            && !((String)entry.getKey()).equals(PlayerList.GREEN_TEAM)
            && !((String)entry.getKey()).equals(PlayerList.YELLOW_TEAM)) {
            EntityPlayer entityPlayer = mc.theWorld.getPlayerEntityByName((String)entry.getKey());
            if (entityPlayer == null) {
               continue;
            }
         } else {
            bl = true;
            k++;
         }

         if (k <= 10 || !bl) {
            if (bl) {
               mc.fontRendererObj
                  .drawStringWithShadow(
                     (
                           k == 1 && !((String)entry.getKey()).equals(PlayerList.RED_TEAM)
                              ? EnumChatFormatting.YELLOW + "✦" + EnumChatFormatting.RESET + " "
                              : ""
                        )
                        + (
                           client.getRelationManager().isEnemyName((String)entry.getKey())
                              ? EnumChatFormatting.RED
                              : (Denicker.nicks.get(entry.getKey()) != null ? EnumChatFormatting.YELLOW : "")
                        )
                        + (String)entry.getKey(),
                     f + 3.0F,
                     g + 3.0F + b,
                     -1
                  );
               mc.fontRendererObj
                  .drawStringWithShadow(
                     (String)entry.getValue(), f + h - mc.fontRendererObj.getStringWidth((String)entry.getValue()) - 3.0F, g + 3.0F + b, -1
                  );
            } else {
               String string = "";
               if (((String)entry.getValue()).equals("nicked")) {
                  string = EnumChatFormatting.YELLOW.toString();
               }

               if (((String)entry.getValue()).equals("enemy")) {
                  string = EnumChatFormatting.RED.toString();
               }

               mc.fontRendererObj.drawStringWithShadow(string + (String)entry.getKey(), f + 3.0F, g + 3.0F + b, -1);
            }

            b += 15;
         }
      }

      this.setHeight(b);
   }

   public Map<String, String> getPlayerTags() {
      return this.playerTags;
   }
}
