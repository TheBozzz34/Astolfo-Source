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
   private static final long c = 55920566408041L;

   public PlayerListHud(float var1, float var2, int var3, int var4) {
      super("Player list", var1, var2, var3, var4);
   }

   @Override
   public void l(int var1) {
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = c ^ 41591446834459L;
      long var10001 = var3 ^ 112428014582575L;
      int var5 = (int)((var3 ^ 112428014582575L) >>> 56);
      int var6 = (int)((var3 ^ 112428014582575L) << 8 >>> 32);
      int var7 = (int)(var10001 << 40 >>> 40);
      float var9 = this.M();
      float var10 = this.E_float();
      float var11 = this.x();
      float var12 = this.Y();
      RenderUtil.drawRect(var9, var10, var9 + var11, var10 + var12, new Color(1, 1, 1, 180).getRGB());
      RenderUtil.drawRect(var9, var10, var9 + var11, var10 + 15.0F, new Color(1, 1, 1, 120).getRGB());
      mc.fontRendererObj.drawStringWithShadow(this.J(), var9 + 3.0F, var10 + 3.0F, -1);
      byte var13 = 15;
      int var14 = 0;

      for (Entry var16 : this.u.entrySet()) {
         boolean var17 = false;
         if (!((String)var16.getValue()).contains("kill")
            && !((String)var16.getKey()).equals(PlayerList.j)
            && !((String)var16.getKey()).equals(PlayerList.O)
            && !((String)var16.getKey()).equals(PlayerList.X)
            && !((String)var16.getKey()).equals(PlayerList.T)) {
            EntityPlayer var18 = mc.theWorld.getPlayerEntityByName((String)var16.getKey());
            if (var18 == null) {
               continue;
            }
         } else {
            var17 = true;
            var14++;
         }

         if (var14 <= 10 || !var17) {
            if (var17) {
               mc.fontRendererObj
                  .drawStringWithShadow(
                     (var14 == 1 && !((String)var16.getKey()).equals(PlayerList.j) ? EnumChatFormatting.YELLOW + "✦" + EnumChatFormatting.RESET + " " : "")
                        + (
                           client.getRelationManager().U((String)var16.getKey())
                              ? EnumChatFormatting.RED
                              : (Denicker.nicks.get(var16.getKey()) != null ? EnumChatFormatting.YELLOW : "")
                        )
                        + (String)var16.getKey(),
                     var9 + 3.0F,
                     var10 + 3.0F + var13,
                     -1
                  );
               mc.fontRendererObj
                  .drawStringWithShadow(
                     (String)var16.getValue(), var9 + var11 - mc.fontRendererObj.getStringWidth((String)var16.getValue()) - 3.0F, var10 + 3.0F + var13, -1
                  );
            } else {
               String var19 = "";
               if (((String)var16.getValue()).equals("nicked")) {
                  var19 = EnumChatFormatting.YELLOW.toString();
               }

               if (((String)var16.getValue()).equals("enemy")) {
                  var19 = EnumChatFormatting.RED.toString();
               }

               mc.fontRendererObj.drawStringWithShadow(var19 + (String)var16.getKey(), var9 + 3.0F, var10 + 3.0F + var13, -1);
            }

            var13 += 15;
         }
      }

      this.B(var13);
   }

   public Map<String, String> j() {
      return this.u;
   }
}
