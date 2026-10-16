package bypass.gui.tabgui;

import bypass.gui.TabGui;

public interface TabGuiEntry {
   String getDisplayText();

   default void onPress() {
   }

   default void onExpand() {
   }

   void handleKeyPress(int i);

   TabGui.Panel getPanel();
}
