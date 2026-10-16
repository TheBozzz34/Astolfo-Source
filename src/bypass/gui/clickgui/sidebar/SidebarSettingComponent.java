package bypass.gui.clickgui.sidebar;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Container;
import bypass.setting.Setting;

public abstract class SidebarSettingComponent extends Container {
   private Setting setting;
   public int fadeAlpha;
   public Animation fadeAnimation = new TimedAnimation().setDuration(200).setEasing(Easing.LINEAR);
   private boolean forceVisible;

   public SidebarSettingComponent(ModuleButton moduleButton, Setting setting, float f, float g, int i, int j) {
      super(setting.getName(), f, g, i, j);
      this.setParent(moduleButton);
      this.setting = setting;
   }

   public ModuleButton getModuleButton() {
      return (ModuleButton)this.getParent();
   }

   public Setting getSetting() {
      return this.setting;
   }

   @Override
   public void onMouseScroll(int i, int j, int k) {
      ModuleButton moduleButton = this.getModuleButton();
      int l = moduleButton.settingsScrollOffset + k;
      int m = moduleButton.getCategoryTab().getSidebar().getHeight() - moduleButton.getSettingsListHeight();
      if (l > 25) {
         moduleButton.settingsScrollOffset = 25;
      } else if (l >= m) {
         moduleButton.settingsScrollOffset += k;
         super.onMouseScroll(i, j, k);
      }
   }

   public boolean isVisible() {
      if (this.forceVisible) {
         return true;
      }

      if (this.getModuleButton() == null) {
         return true;
      }

      CategoryTab categoryTab = this.getModuleButton().getCategoryTab();
      int i = this.getModuleButton().settingsScrollOffset;
      int j = categoryTab.getSidebar().getHeight() - this.getModuleButton().getSettingOffset(this);
      return j >= i - 10 && categoryTab.getParent().getYPos() + 20.0F < this.getYPos();
   }

   public void setForceVisible(boolean bl) {
      this.forceVisible = bl;
   }
}
