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

   public StringComponent(ModuleButton var1, Setting var2, float var3, float var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void l(int var1) {
      if (var1 == 0 && this.y()) {
         this.T = true;
      }
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = d ^ 5492398223382L;
      long var5 = var3 ^ 8972929756414L;
      if (Mouse.isButtonDown(0) && !this.G(var1, var2)) {
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
   public void P(char var1, int var2) {
      if (this.T) {
         StringSetting var6 = this.c();
         if (GuiScreen.isKeyComboCtrlV(var2)) {
            var6.setValue(var6.getValue() + GuiScreen.getClipboardString());
         } else {
            switch (var2) {
               case 14:
                  if (!StringUtils.isNullOrEmpty(var6.getValue())) {
                     var6.setValue(var6.getValue().substring(0, var6.getValue().length() - 1));
                  }
                  break;
               default:
                  if (ChatAllowedCharacters.isAllowedCharacter(var1)) {
                     var6.setValue(var6.getValue() + var1);
                  }
            }

            super.P(var1, var2);
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
