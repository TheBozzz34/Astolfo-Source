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
   private boolean T;
   private static final long d = 21925982432868L;

   public StringComponent(ModuleButton moduleButton, Setting setting, float g, float h, int i, int j) {
      super(moduleButton, setting, g, h, i, j);
   }

   @Override
   public void l(int i) {
      if (i == 0 && this.y()) {
         this.T = true;
      }
   }

   @Override
   public void render(int i, int j) {
      long l = d ^ 5492398223382L;
      long m = l ^ 8972929756414L;
      if (Mouse.isButtonDown(0) && !this.G(i, j)) {
         this.T = false;
      }

      RenderUtil.drawRect(
         this.M(),
         this.E_float() + this.Y() + 1.0F,
         this.M() + this.x(),
         this.E_float() + this.Y() + 2.0F,
         this.T ? client.getSidebarGui().U() : Color.WHITE.getRGB()
      );
      client.getCustomFontRenderer().drawStringWithShadow(this.c().getValue() + "...", this.M(), this.E_float() + 1.0F, -1);
   }

   @Override
   public void P(char c, int i) {
      if (this.T) {
         StringSetting stringSetting = this.c();
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

            super.P(c, i);
         }
      }
   }

   public boolean q() {
      return this.T;
   }

   public StringSetting c() {
      return (StringSetting)this.H();
   }
}
