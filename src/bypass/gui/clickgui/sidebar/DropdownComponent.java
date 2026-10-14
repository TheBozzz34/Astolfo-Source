package bypass.gui.clickgui.sidebar;

import bypass.setting.Setting;

public abstract class DropdownComponent extends SidebarSettingComponent {
   public DropdownComponent(ModuleButton moduleButton, Setting setting, float f, float g, int i, int j) {
      super(moduleButton, setting, f, g, i, j);
   }

   @Override
   public void l(int i) {
      if (i == 0 && this.y()) {
         client.getSidebarGui().G(client.getSidebarGui().A() != null ? null : this);
      }
   }

   public void M(int i, int j) {
   }
}
