package bypass.gui.tabgui;

import bypass.gui.TabGui;
import java.awt.Dimension;

public abstract class TabGuiItem implements Sized, TabGuiEntry {
   public float k;
   private final TabGui.Panel T;
   private final Dimension h = new Dimension(0, 0);
   private static final long a = 107816900586236L;

   public TabGuiItem(TabGui.Panel var1) {
      this.T = var1;
   }

   @Override
   public Dimension x() {
      return this.h;
   }

   @Override
   public TabGui.Panel s() {
      return this.T;
   }

   @Override
   public void z(int var1) {
      if (var1 == 28) {
         this.onPress();
      } else if (var1 == 205) {
         this.onExpand();
      }
   }
}
