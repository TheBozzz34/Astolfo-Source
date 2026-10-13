package bypass.gui.clickgui.sidebar;

import bypass.setting.Setting;

public abstract class DropdownComponent extends SidebarSettingComponent {
   private static final long e = 108119764741532L;

   public DropdownComponent(ModuleButton var1, Setting var2, float var3, float var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void l(int var1) {
      long var2 = e ^ 2523268707162L;
      long var4 = var2 ^ 139878712749130L;
      if (var1 == 0 && this.y()) {
         client.getSidebarGui().G(client.getSidebarGui().A() != null ? null : this);
      }
   }

   public void M(int var1, int var2) {
   }
}
