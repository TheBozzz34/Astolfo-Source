package bypass.gui.tabgui.render;

import bypass.Astolfo;
import bypass.gui.TabGui;
import bypass.gui.tabgui.PanelState;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class PanelRenderer implements ElementRenderer<TabGui.Panel> {
   public void update(TabGui.Panel panel, ScaledResolution scaledResolution, Minecraft minecraft) {
      float f = panel.getSelectedIndex() * panel.getTabGui().getRendererSet().getFontRenderer().getStringHeight("D");
      float g = f - panel.selectorY;
      panel.selectorY += g / 6.5F;
      if (panel.getState() == PanelState.CLOSING) {
         panel.setRevealWidth(panel.getRevealWidth() - panel.getOffsetX() / 18.0F);
         if ((int)panel.getRevealWidth() <= 0) {
            panel.getTabGui().getValue().remove(panel);
         }
      } else if (panel.getState() == PanelState.OPENING) {
         float h = panel.getOffsetX() + (float)panel.getSize().getWidth();
         panel.setRevealWidth(panel.getRevealWidth() + h / 18.0F);
         if ((int)panel.getRevealWidth() == (int)panel.getOffsetX() + panel.getSize().getWidth()) {
            panel.setState(PanelState.NORMAL);
         }
      }
   }

   public void render(TabGui.Panel panel, ScaledResolution scaledResolution, Minecraft minecraft) {
      RenderUtil.drawRect(
         panel.getTabGui().getX() + panel.getOffsetX(),
         panel.getTabGui().getY(),
         panel.getTabGui().getX() + panel.getOffsetX() + (float)panel.getSize().getWidth(),
         panel.getTabGui().getY() + (float)panel.getSize().getHeight(),
         new Color(0, 0, 0, 160).getRGB()
      );
      RenderUtil.drawRect(
         panel.getTabGui().getX() + panel.getOffsetX(),
         panel.getTabGui().getY() + panel.selectorY,
         panel.getTabGui().getX() + panel.getOffsetX() + (float)panel.getSize().getWidth(),
         panel.getTabGui().getY() + panel.selectorY + panel.getTabGui().getRendererSet().getFontRenderer().getStringHeight("D"),
         Astolfo.INSTANCE.getColorSetting().getValue().getRGB()
      );
   }
}
