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

   public void x(TabGui.Panel panel, ScaledResolution scaledResolution, Minecraft minecraft) {
      float f = panel.getSelectedIndex() * panel.getTabGui().p().O().getStringHeight("D");
      float g = f - panel.h;
      panel.h += g / 6.5F;
      if (panel.getState() == PanelState.CLOSING) {
         panel.q(panel.j() - panel.F() / 18.0F);
         if ((int)panel.j() <= 0) {
            panel.getTabGui().L().remove(panel);
         }
      } else if (panel.getState() == PanelState.OPENING) {
         float f2 = panel.F() + (float)panel.x().getWidth();
         panel.q(panel.j() + f2 / 18.0F);
         if ((int)panel.j() == (int)panel.F() + panel.x().getWidth()) {
            panel.setState(PanelState.NORMAL);
         }
      }
   }

   public void a(TabGui.Panel panel, ScaledResolution scaledResolution, Minecraft minecraft) {
      RenderUtil.drawRect(
         panel.getTabGui().J() + panel.F(),
         panel.getTabGui().Q(),
         panel.getTabGui().J() + panel.F() + (float)panel.x().getWidth(),
         panel.getTabGui().Q() + (float)panel.x().getHeight(),
         new Color(0, 0, 0, (int)b).getRGB()
      );
      RenderUtil.drawRect(
         panel.getTabGui().J() + panel.F(),
         panel.getTabGui().Q() + panel.h,
         panel.getTabGui().J() + panel.F() + (float)panel.x().getWidth(),
         panel.getTabGui().Q() + panel.h + panel.getTabGui().p().O().getStringHeight("D"),
         Astolfo.INSTANCE.getColorSetting().getValue().getRGB()
      );
   }
}
