package bypass.gui.clickgui.sidebar;

import bypass.setting.Setting;

public abstract class DropdownComponent extends SidebarSettingComponent {
   public DropdownComponent(ModuleButton moduleButton, Setting setting, float f, float g, int i, int j) {
      super(moduleButton, setting, f, g, i, j);
   }

   @Override
   public void onClick(int i) {
      if (i == 0 && this.isVisible()) {
         client.getSidebarGui().setOpenDropdown(client.getSidebarGui().getOpenDropdown() != null ? null : this);
      }
   }

   public void renderDropdown(int i, int j) {
   }
}
