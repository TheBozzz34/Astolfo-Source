package bypass.gui.tabgui.render;

import bypass.Astolfo;
import bypass.gui.TabGui;
import bypass.gui.tabgui.PanelState;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class PanelRenderer implements ElementRenderer<TabGui.Panel> {
   private static final long a = 120718652994391L;
   private static final long b = -1538887585315684192L;

   public void x(TabGui.Panel var1, ScaledResolution var2, Minecraft var3) {
      float var7 = var1.getSelectedIndex() * var1.getTabGui().p().O().getStringHeight("D");
      float var8 = var7 - var1.h;
      var1.h += var8 / 6.5F;
      if (var1.getState() == PanelState.CLOSING) {
         var1.q(var1.j() - var1.F() / 18.0F);
         if ((int)var1.j() <= 0) {
            var1.getTabGui().L().remove(var1);
         }
      } else if (var1.getState() == PanelState.OPENING) {
         float var9 = var1.F() + (float)var1.x().getWidth();
         var1.q(var1.j() + var9 / 18.0F);
         if ((int)var1.j() == (int)var1.F() + var1.x().getWidth()) {
            var1.setState(PanelState.NORMAL);
         }
      }
   }

   public void a(TabGui.Panel var1, ScaledResolution var2, Minecraft var3) {
      RenderUtil.drawRect(
         var1.getTabGui().J() + var1.F(),
         var1.getTabGui().Q(),
         var1.getTabGui().J() + var1.F() + (float)var1.x().getWidth(),
         var1.getTabGui().Q() + (float)var1.x().getHeight(),
         new Color(0, 0, 0, (int)b).getRGB()
      );
      RenderUtil.drawRect(
         var1.getTabGui().J() + var1.F(),
         var1.getTabGui().Q() + var1.h,
         var1.getTabGui().J() + var1.F() + (float)var1.x().getWidth(),
         var1.getTabGui().Q() + var1.h + var1.getTabGui().p().O().getStringHeight("D"),
         Astolfo.INSTANCE.getColorSetting().getValue().getRGB()
      );
   }
}
