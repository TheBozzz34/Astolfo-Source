package bypass.gui.tabgui;

import bypass.gui.TabGui;
import java.awt.Dimension;
import java.util.List;

public interface TabGuiPanel {
   long a = 101296837649396L;

   List<TabGuiItem> getItems();

   default void addItem(TabGuiItem tabGuiItem) {
      this.getItems().add(tabGuiItem);
   }

   Dimension x();

   int getSelectedIndex();

   void setSelectedIndex(int i);

   default TabGuiItem getSelectedItem() {
      return this.getItems().get(this.getSelectedIndex());
   }

   default boolean isSelected(TabGuiItem tabGuiItem) {
      return this.getItems().indexOf(tabGuiItem) == this.getSelectedIndex();
   }

   default void onKeyPressed(int i) {
      if (this.getTabGui().Q_r6q() == this) {
         this.getSelectedItem().z(i);
         if (i == 200) {
            this.selectPrevious();
         } else if (i == 208) {
            this.selectNext();
         } else if (i == 203 && this.getTabGui().L().indexOf(this) != 0 && this.getState() != PanelState.CLOSING) {
            this.q((float)this.x().getWidth());
            this.setState(PanelState.CLOSING);
         }
      }
   }

   default void selectPrevious() {
      int i = this.getSelectedIndex() - 1;
      if (i >= 0) {
         this.setSelectedIndex(i);
      }
   }

   default void selectNext() {
      int i = this.getSelectedIndex() + 1;
      if (i < this.getItems().size()) {
         this.setSelectedIndex(i);
      }
   }

   default void refreshSize() {
      this.x().setSize(0, 0);
      this.getItems()
         .forEach(
            tabGuiItem -> this.x()
               .setSize(
                  this.x().getWidth() < this.getTabGui().p().get((Class<? extends Sized>)tabGuiItem.getClass()).p(tabGuiItem)[0]
                     ? this.getTabGui().p().get((Class<? extends Sized>)tabGuiItem.getClass()).p(tabGuiItem)[0]
                     : this.x().getWidth(),
                  this.x().getHeight() + this.getTabGui().p().get((Class<? extends Sized>)tabGuiItem.getClass()).p(tabGuiItem)[1]
               )
         );
   }

   TabGui getTabGui();

   float j();

   void q(float f);

   float F();

   PanelState getState();

   void setState(PanelState panelState);
}
