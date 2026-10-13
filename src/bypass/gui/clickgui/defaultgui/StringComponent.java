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
   private static final long c = 119843223678281L;

   public StringComponent(Category var1, Container var2, Setting<?> var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.h = var1;
   }

   @Override
   public void l(int var1) {
      if (var1 == 0) {
         this.w = !this.w;
      }
   }

   @Override
   public void render(int var1, int var2) {
      if (Mouse.isButtonDown(0) && !this.G(var1, var2)) {
         this.w = false;
      }

      int var6 = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.h.getColor() : client.getColorSetting().getValue().getRGB();
      if (this.w) {
         RenderUtil.c(1.0F, this.M() + 1.0F, this.x() - 1, this.E_float(), this.Y() + 3, var6);
      }

      RenderUtil.drawRect(this.M() + 3.0F, this.E_float() + this.Y() + 1.0F, this.M() + this.x() - 3.0F, this.E_float() + this.Y() + 2.0F, var6);
      GuiMainMenu.m.drawString(this.r().getValue() + "...", this.M() + 4.0F, this.E_float(), -1);
   }

   @Override
   public void P(char var1, int var2) {
      if (this.w) {
         if (GuiScreen.isKeyComboCtrlV(var2)) {
            this.r().setValue(this.r().getValue() + GuiScreen.getClipboardString());
         } else {
            switch (var2) {
               case 14:
                  if (!StringUtils.isNullOrEmpty(this.r().getValue())) {
                     this.r().setValue(this.r().getValue().substring(0, this.r().getValue().length() - 1));
                  }
                  break;
               default:
                  if (ChatAllowedCharacters.isAllowedCharacter(var1)) {
                     this.r().setValue(this.r().getValue() + var1);
                  }
            }

            super.P(var1, var2);
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
