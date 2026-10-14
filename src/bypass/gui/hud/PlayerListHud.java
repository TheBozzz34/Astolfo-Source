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
   private final Map<String, String> u = new LinkedHashMap<>();
   private int d = new Color(17, 17, 17, 197).getRGB();

   public PlayerListHud(float f, float g, int i, int j) {
      super("Player list", f, g, i, j);
   }

   @Override
   public void l(int i) {
   }

   @Override
   public void render(int i, int j) {
      float f = this.M();
      float g = this.E_float();
      float h = this.x();
      float f2 = this.Y();
      RenderUtil.drawRect(f, g, f + h, g + f2, new Color(1, 1, 1, 180).getRGB());
      RenderUtil.drawRect(f, g, f + h, g + 15.0F, new Color(1, 1, 1, 120).getRGB());
      mc.fontRendererObj.drawStringWithShadow(this.J(), f + 3.0F, g + 3.0F, -1);
      byte b = 15;
      int k = 0;

      for (Entry entry : this.u.entrySet()) {
         boolean bl = false;
         if (!((String)entry.getValue()).contains("kill")
            && !((String)entry.getKey()).equals(PlayerList.j)
            && !((String)entry.getKey()).equals(PlayerList.O)
            && !((String)entry.getKey()).equals(PlayerList.X)
            && !((String)entry.getKey()).equals(PlayerList.T)) {
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
                     (k == 1 && !((String)entry.getKey()).equals(PlayerList.j) ? EnumChatFormatting.YELLOW + "✦" + EnumChatFormatting.RESET + " " : "")
                        + (
                           client.getRelationManager().U((String)entry.getKey())
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

      this.B(b);
   }

   public Map<String, String> j() {
      return this.u;
   }
}
