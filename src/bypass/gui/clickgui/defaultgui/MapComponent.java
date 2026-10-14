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
   public MapComponent(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void l(int i) {
      if (i == 0) {
         this.u(!this.s_boolean());
      }
   }

   @Override
   public void render(int i, int j) {
      int k = this.i();
      if (this.s_boolean()) {
         RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y() + k, new Color(18, 18, 18).getRGB());
      }

      GuiMainMenu.m
         .drawString(
            this.T().getName() + "...",
            this.M() + this.x() / 2 - GuiMainMenu.m.getStringWidth(this.r().getName() + "...", true) / 2 + 3.0F,
            this.E_float(),
            Color.WHITE.getRGB()
         );
      if (this.s_boolean()) {
         int l = 0;

         for (Element element : this.M_ArrayList()) {
            if (element instanceof SettingContainer) {
               ((SettingContainer)element).r(this.M() + 3.0F, this.E_float() + l + 10.0F);
               element.render(i, j);
               l += ((SettingContainer)element).Y() + (((SettingContainer)element).s_boolean() ? 3 : 0);
            }

            if (element instanceof SettingComponent) {
               ((SettingComponent)element).r(this.M() + 3.0F, this.E_float() + l + 10.0F);
               element.render(i, j);
               l += 10;
            }
         }
      }
   }

   public int i() {
      int j = 0;
      if (this.s_boolean()) {
         for (Element element : this.M_ArrayList()) {
            if (element instanceof BooleanComponent
               || element instanceof ColorPicker
               || element instanceof StringComponent
               || element instanceof NumberSlider
               || element instanceof RangeSlider
               || element instanceof EnumComponent) {
               j += ((Component)element).Y();
            }
         }
      }

      return j + (this.s_boolean() ? 3 : 0);
   }
}
