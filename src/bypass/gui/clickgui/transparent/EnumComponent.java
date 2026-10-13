package bypass.gui.clickgui.transparent;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.render.RenderUtil;
import bypass.setting.EnumSetting;
import bypass.setting.Setting;
import java.awt.Color;
import net.minecraft.util.EnumChatFormatting;

public class EnumComponent extends SettingComponent {
   private static final long c = 22446301099627L;
   private static final long d = -38810986638999432L;

   public EnumComponent(Container var1, Setting var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void render(int var1, int var2) {
      RenderUtil.drawRect(
         this.M() + 1.5F, this.E_float() - 1.0F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 4.0F, new Color(1, 1, 1, (int)d).getRGB()
      );
      client.getCustomFontRenderer()
         .drawStringWithShadow(
            EnumChatFormatting.GRAY + this.j().getName().replace("-", " ") + ":" + EnumChatFormatting.WHITE + " " + this.j().getValue().name(),
            this.M() + 4.0F,
            this.E_float(),
            Color.WHITE.getRGB()
         );
   }

   @Override
   public void l(int var1) {
      if (this.I().s_boolean()) {
         Enum[] var5 = (Enum[])this.j().getValue().getClass().getEnumConstants();
         int var6 = this.w();
         var6 += var1 == 0 ? 1 : -1;
         if (var6 >= var5.length || var6 < 0) {
            var6 = 0;
         }

         this.j().setValue(var5[var6]);
      }
   }

   private EnumSetting j() {
      return (EnumSetting)this.t();
   }

   private int w() {
      Enum[] var4 = (Enum[])this.j().getValue().getClass().getEnumConstants();

      for (int var5 = 0; var5 < var4.length; var5++) {
         if (var4[var5] == this.j().getValue()) {
            return var5;
         }
      }

      return -1;
   }
}
