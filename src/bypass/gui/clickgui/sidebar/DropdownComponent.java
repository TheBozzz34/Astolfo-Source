package bypass.gui.clickgui.sidebar;

import bypass.setting.Setting;

public abstract class DropdownComponent extends SidebarSettingComponent {
   private static final long e = 108119764741532L;

   public DropdownComponent(ModuleButton moduleButton, Setting setting, float g, float h, int i, int j) {
      super(moduleButton, setting, g, h, i, j);
   }

   @Override
   public void l(int i) {
      long m = e ^ 2523268707162L;
      long n = m ^ 139878712749130L;
      if (i == 0 && this.y()) {
         client.getSidebarGui().G(client.getSidebarGui().A() != null ? null : this);
      }
   }

   public void M(int i, int j) {
   }
}
