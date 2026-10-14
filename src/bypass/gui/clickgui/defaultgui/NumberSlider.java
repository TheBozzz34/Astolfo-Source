package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.module.Category;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.client.gui.GuiMainMenu;
import org.lwjgl.input.Mouse;

public class NumberSlider extends SettingComponent {
   private final Category w;
   private boolean Z;
   private static final DecimalFormat o = new DecimalFormat("#.##");

   public NumberSlider(Category category, Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
      this.w = category;
   }

   @Override
   public void l(int i) {
      if (i == 0 && this.I().s_boolean()) {
         this.Z = true;
      }
   }

   @Override
   public void render(int i, int j) {
      int k = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.w.getColor() : client.getColorSetting().getValue().getRGB();
      int l = this.a(i, (int)this.M(), this.x());
      String string = o.format(this.o().getValue());
      if (this.o().getSuffix().equals("stacks")) {
         if (this.o().asInt() == 1) {
            string = string + " stack";
         } else {
            string = string + " " + this.o().getSuffix();
         }
      } else {
         string = string + this.o().getSuffix();
      }

      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + l, this.E_float() + this.Y(), k);
      RenderUtil.drawRect(this.M() + Math.max(l - 3, 0), this.E_float(), this.M() + l, this.E_float() + this.Y(), new Color(k).darker().getRGB());
      GuiMainMenu.m.drawString(this.o().getName(), this.M() + 3.0F, this.E_float(), -1);
      GuiMainMenu.m.drawString(string, this.M() + this.x() - GuiMainMenu.m.getStringWidth(string, true), this.E_float() + 1.0F, 13421772);
   }

   public int a(int i, int j, int k) {
      int l = Math.max(Math.min(i - j, k), 0);
      NumberSetting numberSetting = this.o();
      if (this.Z) {
         this.Z = Mouse.isButtonDown(0);
         if (numberSetting.valueClass == Integer.class) {
            NumberSetting numberSetting2 = numberSetting;
            numberSetting2.setValue(
               (int)(
                  ((Integer)numberSetting2.getMin()).intValue()
                     + 0.1F
                     + l * ((((Integer)numberSetting2.getMax()).intValue() + 0.1F - ((Integer)numberSetting2.getMin()).intValue() + 0.1F) / k)
               )
            );
         } else if (numberSetting.valueClass == Double.class) {
            NumberSetting numberSetting3 = numberSetting;
            numberSetting3.setValue((Double)numberSetting3.getMin() + l * (((Double)numberSetting3.getMax() - (Double)numberSetting3.getMin()) / k));
         } else {
            NumberSetting numberSetting4 = numberSetting;
            numberSetting4.setValue((Float)numberSetting4.getMin() + l * (((Float)numberSetting4.getMax() - (Float)numberSetting4.getMin()) / k));
         }
      }

      if (numberSetting.valueClass == Integer.class) {
         NumberSetting numberSetting5 = numberSetting;
         return (int)(
            (numberSetting5.asInt() + 0.1F - ((Integer)numberSetting5.getMin()).intValue() + 0.1F)
               / (((Integer)numberSetting5.getMax()).intValue() + 0.1F - ((Integer)numberSetting5.getMin()).intValue() + 0.1F)
               * k
         );
      } else if (numberSetting.valueClass == Double.class) {
         NumberSetting numberSetting6 = numberSetting;
         return (int)((numberSetting6.asDouble() - (Double)numberSetting6.getMin()) / ((Double)numberSetting6.getMax() - (Double)numberSetting6.getMin()) * k);
      } else {
         NumberSetting numberSetting7 = numberSetting;
         return (int)((numberSetting7.asFloat() - (Float)numberSetting7.getMin()) / ((Float)numberSetting7.getMax() - (Float)numberSetting7.getMin()) * k);
      }
   }

   public NumberSetting o() {
      return (NumberSetting)this.t();
   }
}
