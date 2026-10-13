package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.clickgui.SettingContainer;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import bypass.setting.Setting;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;

public class MapComponent extends SettingContainer {
   private static final long d = 97963436442909L;

   public MapComponent(Container var1, Setting var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void l(int var1) {
      if (var1 == 0) {
         this.u(!this.s_boolean());
      }
   }

   @Override
   public void render(int var1, int var2) {
      int var6 = this.i();
      if (this.s_boolean()) {
         RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y() + var6, new Color(18, 18, 18).getRGB());
      }

      GuiMainMenu.m
         .drawString(
            this.T().getName() + "...",
            this.M() + this.x() / 2 - GuiMainMenu.m.getStringWidth(this.r().getName() + "...", true) / 2 + 3.0F,
            this.E_float(),
            Color.WHITE.getRGB()
         );
      if (this.s_boolean()) {
         int var7 = 0;

         for (Element var9 : this.M_ArrayList()) {
            if (var9 instanceof SettingContainer) {
               ((SettingContainer)var9).r(this.M() + 3.0F, this.E_float() + var7 + 10.0F);
               var9.render(var1, var2);
               var7 += ((SettingContainer)var9).Y() + (((SettingContainer)var9).s_boolean() ? 3 : 0);
            }

            if (var9 instanceof SettingComponent) {
               ((SettingComponent)var9).r(this.M() + 3.0F, this.E_float() + var7 + 10.0F);
               var9.render(var1, var2);
               var7 += 10;
            }
         }
      }
   }

   public int i() {
      int var4 = 0;
      if (this.s_boolean()) {
         for (Element var6 : this.M_ArrayList()) {
            if (var6 instanceof BooleanComponent
               || var6 instanceof ColorPicker
               || var6 instanceof StringComponent
               || var6 instanceof NumberSlider
               || var6 instanceof RangeSlider
               || var6 instanceof EnumComponent) {
               var4 += ((Component)var6).Y();
            }
         }
      }

      return var4 + (this.s_boolean() ? 3 : 0);
   }
}
