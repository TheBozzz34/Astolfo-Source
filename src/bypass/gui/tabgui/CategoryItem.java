package bypass.gui.tabgui;

import bypass.Astolfo;
import bypass.gui.TabGui;
import bypass.module.Category;
import bypass.module.Module;

public class CategoryItem extends TabGuiItem {
   private final Category category;

   public CategoryItem(TabGui.Panel panel, Category category) {
      super(panel);
      this.category = category;
   }

   public Category getCategory() {
      return this.category;
   }

   @Override
   public String getDisplayText() {
      return this.category.getDisplayName();
   }

   @Override
   public void onPress() {
      TabGui.Panel panel = new TabGui.Panel(this.getPanel().getTabGui());
      Astolfo.INSTANCE.getModuleManager().getList().forEach(moduleInfo -> {
         if (moduleInfo instanceof Module && moduleInfo.getCategory() == this.category) {
            panel.addItem(new ModuleItem(panel, (Module)moduleInfo));
         }
      });
      if (panel.getItems().size() != 0) {
         this.getPanel().getTabGui().addElement(panel);
      }
   }

   @Override
   public void onExpand() {
      this.onPress();
   }
}
