package bypass.gui.clickgui.transparent;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.render.RenderUtil;
import bypass.setting.Setting;
import bypass.setting.StringSetting;
import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Mouse;

public class StringComponent extends SettingComponent {
   private boolean s;
   private static final long c = 74464720209322L;
   private static final long e = 4874718530123071608L;

   public StringComponent(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void render(int i, int j) {
      if (Mouse.isButtonDown(0) && !this.G(i, j)) {
         this.s = false;
      }

      RenderUtil.drawRect(
         this.M() + 1.5F, this.E_float() - 1.0F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 4.0F, new Color(1, 1, 1, (int)e).getRGB()
      );
      if (this.s) {
         RenderUtil.c(1.0F, this.M() + 1.0F, this.x() - 1, this.E_float(), this.Y() + 3, client.getColorSetting().getValue().getRGB());
      }

      RenderUtil.drawRect(
         this.M() + 3.0F,
         this.E_float() + this.Y() + 1.0F,
         this.M() + this.x() - 3.0F,
         this.E_float() + this.Y() + 2.0F,
         client.getColorSetting().getValue().getRGB()
      );
      client.getCustomFontRenderer().drawStringWithShadow(this.z().getValue() + "...", this.M() + 4.0F, this.E_float(), -1);
   }

   @Override
   public void l(int i) {
      if (i == 0) {
         this.s = true;
      }
   }

   @Override
   public void P(char c2, int i) {
      if (this.s) {
         if (GuiScreen.isKeyComboCtrlV(i)) {
            this.z().setValue(this.z().getValue() + GuiScreen.getClipboardString());
         } else {
            switch (i) {
               case 14:
                  if (!StringUtils.isNullOrEmpty(this.z().getValue())) {
                     this.z().setValue(this.z().getValue().substring(0, this.z().getValue().length() - 1));
                  }
                  break;
               default:
                  if (ChatAllowedCharacters.isAllowedCharacter(c2)) {
                     this.z().setValue(this.z().getValue() + c2);
                  }
            }

            super.P(c2, i);
         }
      }
   }

   public StringSetting z() {
      return (StringSetting)this.t();
   }

   public boolean k() {
      return this.s;
   }
}
