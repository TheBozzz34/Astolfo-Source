package bypass.gui.tabgui;

import bypass.Astolfo;
import bypass.gui.TabGui;
import bypass.module.Category;
import bypass.module.Module;

public class CategoryItem extends TabGuiItem {
   private final Category b;
   private static final long c = 66562054744264L;

   public CategoryItem(TabGui.Panel var1, Category var2) {
      super(var1);
      this.b = var2;
   }

   public Category C() {
      return this.b;
   }

   @Override
   public String L() {
      return this.b.getDisplayName();
   }

   @Override
   public void onPress() {
      long var1 = c ^ 137224528231714L;
      long var3 = var1 ^ 131050176768089L;
      TabGui.Panel var7 = new TabGui.Panel(this.s().getTabGui());
      Astolfo.INSTANCE.getModuleManager().getList().forEach(var2 -> {
         if (var2 instanceof Module && var2.getCategory() == this.b) {
            var7.addItem(new ModuleItem(var7, (Module)var2));
         }
      });
      if (var7.getItems().size() != 0) {
         this.s().getTabGui().s(var7);
      }
   }

   @Override
   public void onExpand() {
      this.onPress();
   }
}
