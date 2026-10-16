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
   private boolean focused;

   public StringComponent(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void render(int i, int j) {
      if (Mouse.isButtonDown(0) && !this.isHovered(i, j)) {
         this.focused = false;
      }

      RenderUtil.drawRect(
         this.getX() + 1.5F,
         this.getYPos() - 1.0F,
         this.getX() + this.getWidth() + 0.5F,
         this.getYPos() + this.getHeight() + 4.0F,
         new Color(1, 1, 1, 120).getRGB()
      );
      if (this.focused) {
         RenderUtil.drawLineOutline(
            1.0F, this.getX() + 1.0F, this.getWidth() - 1, this.getYPos(), this.getHeight() + 3, client.getColorSetting().getValue().getRGB()
         );
      }

      RenderUtil.drawRect(
         this.getX() + 3.0F,
         this.getYPos() + this.getHeight() + 1.0F,
         this.getX() + this.getWidth() - 3.0F,
         this.getYPos() + this.getHeight() + 2.0F,
         client.getColorSetting().getValue().getRGB()
      );
      client.getCustomFontRenderer().drawStringWithShadow(this.getStringSetting().getValue() + "...", this.getX() + 4.0F, this.getYPos(), -1);
   }

   @Override
   public void onClick(int i) {
      if (i == 0) {
         this.focused = true;
      }
   }

   @Override
   public void onKeyTyped(char c, int i) {
      if (this.focused) {
         if (GuiScreen.isKeyComboCtrlV(i)) {
            this.getStringSetting().setValue(this.getStringSetting().getValue() + GuiScreen.getClipboardString());
         } else {
            switch (i) {
               case 14:
                  if (!StringUtils.isNullOrEmpty(this.getStringSetting().getValue())) {
                     this.getStringSetting().setValue(this.getStringSetting().getValue().substring(0, this.getStringSetting().getValue().length() - 1));
                  }
                  break;
               default:
                  if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                     this.getStringSetting().setValue(this.getStringSetting().getValue() + c);
                  }
            }

            super.onKeyTyped(c, i);
         }
      }
   }

   public StringSetting getStringSetting() {
      return (StringSetting)this.getSetting();
   }

   public boolean isFocused() {
      return this.focused;
   }
}
