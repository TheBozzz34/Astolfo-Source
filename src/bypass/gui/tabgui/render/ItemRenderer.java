package bypass.gui.tabgui.render;

import bypass.gui.tabgui.TabGuiItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class ItemRenderer implements ElementRenderer<TabGuiItem> {
   public void update(TabGuiItem tabGuiItem, ScaledResolution scaledResolution, Minecraft minecraft) {
      float f = (tabGuiItem.getPanel().isSelected(tabGuiItem) ? 8 : 2) - tabGuiItem.slideOffset;
      tabGuiItem.slideOffset += f / 9.5F;
   }

   public void render(TabGuiItem tabGuiItem, ScaledResolution scaledResolution, Minecraft minecraft) {
      tabGuiItem.getPanel()
         .getTabGui()
         .getRendererSet()
         .getFontRenderer()
         .drawStringWithShadow(
            tabGuiItem.getDisplayText(),
            tabGuiItem.getPanel().getTabGui().getX() + tabGuiItem.getPanel().getOffsetX() + tabGuiItem.slideOffset,
            tabGuiItem.getPanel().getTabGui().getY()
               + tabGuiItem.getPanel().getItems().indexOf(tabGuiItem)
                  * tabGuiItem.getPanel().getTabGui().getRendererSet().getFontRenderer().getStringHeight(tabGuiItem.getDisplayText()),
            16777215
         );
   }

   public float[] computeSize(TabGuiItem tabGuiItem) {
      return new float[]{
         tabGuiItem.getPanel().getTabGui().getRendererSet().getFontRenderer().getStringWidth(tabGuiItem.getDisplayText()) + 15,
         tabGuiItem.getPanel().getTabGui().getRendererSet().getFontRenderer().getStringHeight(tabGuiItem.getDisplayText())
      };
   }
}
