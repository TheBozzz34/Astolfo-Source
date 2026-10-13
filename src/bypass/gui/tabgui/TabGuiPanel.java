package bypass.gui.tabgui;

import bypass.gui.TabGui;
import java.awt.Dimension;
import java.util.List;

public interface TabGuiPanel {
   long a = 101296837649396L;

   List<TabGuiItem> getItems();

   default void addItem(TabGuiItem var1) {
      this.getItems().add(var1);
   }

   Dimension x();

   int getSelectedIndex();

   void setSelectedIndex(int var1);

   default TabGuiItem getSelectedItem() {
      return this.getItems().get(this.getSelectedIndex());
   }

   default boolean isSelected(TabGuiItem var1) {
      return this.getItems().indexOf(var1) == this.getSelectedIndex();
   }

   default void onKeyPressed(int var1) {
      if (this.getTabGui().Q_r6q() == this) {
         this.getSelectedItem().z(var1);
         if (var1 == 200) {
            this.selectPrevious();
         } else if (var1 == 208) {
            this.selectNext();
         } else if (var1 == 203 && this.getTabGui().L().indexOf(this) != 0 && this.getState() != PanelState.CLOSING) {
            this.q((float)this.x().getWidth());
            this.setState(PanelState.CLOSING);
         }
      }
   }

   default void selectPrevious() {
      int var3 = this.getSelectedIndex() - 1;
      if (var3 >= 0) {
         this.setSelectedIndex(var3);
      }
   }

   default void selectNext() {
      int var3 = this.getSelectedIndex() + 1;
      if (var3 < this.getItems().size()) {
         this.setSelectedIndex(var3);
      }
   }

   default void refreshSize() {
      this.x().setSize(0, 0);
      this.getItems()
         .forEach(
            var1 -> this.x()
               .setSize(
                  this.x().getWidth() < this.getTabGui().p().get((Class<? extends Sized>)var1.getClass()).p(var1)[0]
                     ? this.getTabGui().p().get((Class<? extends Sized>)var1.getClass()).p(var1)[0]
                     : this.x().getWidth(),
                  this.x().getHeight() + this.getTabGui().p().get((Class<? extends Sized>)var1.getClass()).p(var1)[1]
               )
         );
   }

   TabGui getTabGui();

   float j();

   void q(float var1);

   float F();

   PanelState getState();

   void setState(PanelState var1);
}
