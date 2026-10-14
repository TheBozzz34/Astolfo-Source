package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.module.Category;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import bypass.setting.Setting;
import bypass.setting.StringSetting;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Mouse;

public class StringComponent extends SettingComponent {
   private final Category h;
   private boolean w;

   public StringComponent(Category category, Container container, Setting<?> setting, int i, int j) {
      super(container, setting, i, j);
      this.h = category;
   }

   @Override
   public void l(int i) {
      if (i == 0) {
         this.w = !this.w;
      }
   }

   @Override
   public void render(int i, int j) {
      if (Mouse.isButtonDown(0) && !this.G(i, j)) {
         this.w = false;
      }

      int k = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.h.getColor() : client.getColorSetting().getValue().getRGB();
      if (this.w) {
         RenderUtil.c(1.0F, this.M() + 1.0F, this.x() - 1, this.E_float(), this.Y() + 3, k);
      }

      RenderUtil.drawRect(this.M() + 3.0F, this.E_float() + this.Y() + 1.0F, this.M() + this.x() - 3.0F, this.E_float() + this.Y() + 2.0F, k);
      GuiMainMenu.m.drawString(this.r().getValue() + "...", this.M() + 4.0F, this.E_float(), -1);
   }

   @Override
   public void P(char c, int i) {
      if (this.w) {
         if (GuiScreen.isKeyComboCtrlV(i)) {
            this.r().setValue(this.r().getValue() + GuiScreen.getClipboardString());
         } else {
            switch (i) {
               case 14:
                  if (!StringUtils.isNullOrEmpty(this.r().getValue())) {
                     this.r().setValue(this.r().getValue().substring(0, this.r().getValue().length() - 1));
                  }
                  break;
               default:
                  if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                     this.r().setValue(this.r().getValue() + c);
                  }
            }

            super.P(c, i);
         }
      }
   }

   public StringSetting r() {
      return (StringSetting)this.t();
   }

   public boolean E() {
      return this.w;
   }
}
