package bypass.gui.tabgui;

import bypass.gui.TabGui;
import java.awt.Dimension;
import java.util.List;

public interface TabGuiPanel {
   List<TabGuiItem> getItems();

   default void addItem(TabGuiItem tabGuiItem) {
      this.getItems().add(tabGuiItem);
   }

   Dimension getSize();

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
         this.getSelectedItem().handleKeyPress(i);
         if (i == 200) {
            this.selectPrevious();
         } else if (i == 208) {
            this.selectNext();
         } else if (i == 203 && this.getTabGui().getValue().indexOf(this) != 0 && this.getState() != PanelState.CLOSING) {
            this.setRevealWidth((float)this.getSize().getWidth());
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
      this.getSize().setSize(0, 0);
      this.getItems()
         .forEach(
            tabGuiItem -> this.getSize()
               .setSize(
                  this.getSize().getWidth() < this.getTabGui().getRendererSet().get((Class<? extends Sized>)tabGuiItem.getClass()).computeSizeUnchecked(tabGuiItem)[0]
                     ? this.getTabGui().getRendererSet().get((Class<? extends Sized>)tabGuiItem.getClass()).computeSizeUnchecked(tabGuiItem)[0]
                     : this.getSize().getWidth(),
                  this.getSize().getHeight() + this.getTabGui().getRendererSet().get((Class<? extends Sized>)tabGuiItem.getClass()).computeSizeUnchecked(tabGuiItem)[1]
               )
         );
   }

   TabGui getTabGui();

   float getRevealWidth();

   void setRevealWidth(float f);

   float getOffsetX();

   PanelState getState();

   void setState(PanelState panelState);
}
