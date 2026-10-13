package bypass.gui.tabgui.render;

import bypass.gui.tabgui.TabGuiItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class ItemRenderer implements ElementRenderer<TabGuiItem> {
   private static final long a = 44208449283911L;

   public void x(TabGuiItem var1, ScaledResolution var2, Minecraft var3) {
      float var7 = (var1.s().isSelected(var1) ? 8 : 2) - var1.k;
      var1.k += var7 / 9.5F;
   }

   public void a(TabGuiItem var1, ScaledResolution var2, Minecraft var3) {
      var1.s()
         .getTabGui()
         .p()
         .O()
         .drawStringWithShadow(
            var1.L(),
            var1.s().getTabGui().J() + var1.s().F() + var1.k,
            var1.s().getTabGui().Q() + var1.s().getItems().indexOf(var1) * var1.s().getTabGui().p().O().getStringHeight(var1.L()),
            16777215
         );
   }

   public float[] H(TabGuiItem var1) {
      return new float[]{var1.s().getTabGui().p().O().getStringWidth(var1.L()) + 15, var1.s().getTabGui().p().O().getStringHeight(var1.L())};
   }
}
