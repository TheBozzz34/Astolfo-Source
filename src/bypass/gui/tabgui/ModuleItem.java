package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.NumberSetting;

public class ModuleItem extends TabGuiItem {
   private final Module U;
   private static final long b = 49190119730991L;

   public ModuleItem(TabGui.Panel var1, Module var2) {
      super(var1);
      this.U = var2;
   }

   public Module c() {
      return this.U;
   }

   @Override
   public String L() {
      return (this.U.isEnabled() ? "§f" : "§7") + this.U.getName();
   }

   @Override
   public void onPress() {
      this.U.toggle();
   }

   @Override
   public void onExpand() {
      TabGui.Panel var4 = new TabGui.Panel(this.s().getTabGui());
      this.U.getSettings().forEach(var1 -> {
         if (var1 instanceof BooleanSetting) {
            BooleanSetting var5 = (BooleanSetting)var1;
            if (!var1.getName().equals("Hide")) {
               var4.addItem(new BooleanSettingItem(var4, var5));
            }
         } else if (var1 instanceof NumberSetting) {
            NumberSetting var6 = (NumberSetting)var1;
            var4.addItem(new NumberSettingItem(var4, var6));
         } else if (var1 instanceof MapSetting) {
            MapSetting var7 = (MapSetting)var1;
            var4.addItem(new MapSettingItem(var4, var7));
         } else if (var1 instanceof EnumSetting) {
            var4.addItem(new EnumSettingItem(var4, (EnumSetting)var1));
         }
      });
      if (var4.getItems().size() != 0) {
         var4.getTabGui().s(var4);
      }
   }
}
