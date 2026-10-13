package bypass.gui.tabgui;

import bypass.gui.TabGui;

public interface TabGuiEntry {
   String L();

   default void onPress() {
   }

   default void onExpand() {
   }

   void z(int var1);

   TabGui.Panel s();
}
