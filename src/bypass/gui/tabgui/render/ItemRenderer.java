package bypass.gui.tabgui.render;

import bypass.gui.tabgui.TabGuiItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class ItemRenderer implements ElementRenderer<TabGuiItem> {
   private static final long a = 44208449283911L;

   public void x(TabGuiItem tabGuiItem, ScaledResolution scaledResolution, Minecraft minecraft) {
      float f = (tabGuiItem.s().isSelected(tabGuiItem) ? 8 : 2) - tabGuiItem.k;
      tabGuiItem.k += f / 9.5F;
   }

   public void a(TabGuiItem tabGuiItem, ScaledResolution scaledResolution, Minecraft minecraft) {
      tabGuiItem.s()
         .getTabGui()
         .p()
         .O()
         .drawStringWithShadow(
            tabGuiItem.L(),
            tabGuiItem.s().getTabGui().J() + tabGuiItem.s().F() + tabGuiItem.k,
            tabGuiItem.s().getTabGui().Q() + tabGuiItem.s().getItems().indexOf(tabGuiItem) * tabGuiItem.s().getTabGui().p().O().getStringHeight(tabGuiItem.L()),
            16777215
         );
   }

   public float[] H(TabGuiItem tabGuiItem) {
      return new float[]{
         tabGuiItem.s().getTabGui().p().O().getStringWidth(tabGuiItem.L()) + 15, tabGuiItem.s().getTabGui().p().O().getStringHeight(tabGuiItem.L())
      };
   }
}
