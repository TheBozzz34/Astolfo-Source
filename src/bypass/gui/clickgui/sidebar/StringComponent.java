package bypass.gui.clickgui.sidebar;

import bypass.render.RenderUtil;
import bypass.setting.Setting;
import bypass.setting.StringSetting;
import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Mouse;

public class StringComponent extends SidebarSettingComponent {
   private boolean focused;

   public StringComponent(ModuleButton moduleButton, Setting setting, float f, float g, int i, int j) {
      super(moduleButton, setting, f, g, i, j);
   }

   @Override
   public void onClick(int i) {
      if (i == 0 && this.isVisible()) {
         this.focused = true;
      }
   }

   @Override
   public void render(int i, int j) {
      if (Mouse.isButtonDown(0) && !this.isHovered(i, j)) {
         this.focused = false;
      }

      RenderUtil.drawRect(
         this.getX(),
         this.getYPos() + this.getHeight() + 1.0F,
         this.getX() + this.getWidth(),
         this.getYPos() + this.getHeight() + 2.0F,
         this.focused ? client.getSidebarGui().getPulseColor() : Color.WHITE.getRGB()
      );
      client.getCustomFontRenderer().drawStringWithShadow(this.getStringSetting().getValue() + "...", this.getX(), this.getYPos() + 1.0F, -1);
   }

   @Override
   public void onKeyTyped(char c, int i) {
      if (this.focused) {
         StringSetting stringSetting = this.getStringSetting();
         if (GuiScreen.isKeyComboCtrlV(i)) {
            stringSetting.setValue(stringSetting.getValue() + GuiScreen.getClipboardString());
         } else {
            switch (i) {
               case 14:
                  if (!StringUtils.isNullOrEmpty(stringSetting.getValue())) {
                     stringSetting.setValue(stringSetting.getValue().substring(0, stringSetting.getValue().length() - 1));
                  }
                  break;
               default:
                  if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                     stringSetting.setValue(stringSetting.getValue() + c);
                  }
            }

            super.onKeyTyped(c, i);
         }
      }
   }

   public boolean isFocused() {
      return this.focused;
   }

   public StringSetting getStringSetting() {
      return (StringSetting)this.getSetting();
   }
}
