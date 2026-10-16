package bypass.gui.tabgui;

import bypass.gui.TabGui;
import java.awt.Dimension;

public abstract class TabGuiItem implements Sized, TabGuiEntry {
   public float slideOffset;
   private final TabGui.Panel panel;
   private final Dimension size = new Dimension(0, 0);

   public TabGuiItem(TabGui.Panel panel) {
      this.panel = panel;
   }

   @Override
   public Dimension getSize() {
      return this.size;
   }

   @Override
   public TabGui.Panel getPanel() {
      return this.panel;
   }

   @Override
   public void handleKeyPress(int i) {
      if (i == 28) {
         this.onPress();
      } else if (i == 205) {
         this.onExpand();
      }
   }
}
