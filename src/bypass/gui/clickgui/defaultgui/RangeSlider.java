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

public class RangeSlider extends SettingComponent {
   private final Category V;
   private Setting l;
   private boolean T;
   private boolean B;
   private int H;
   private int c;
   private static final DecimalFormat G = new DecimalFormat("#.##");
   private static final long d = 64464004619804L;

   public RangeSlider(Category category, Container container, Setting setting, Setting setting2, int i, int j) {
      super(container, setting, i, j);
      this.V = category;
      this.l = setting2;
   }

   @Override
   public void l(int i) {
      if (i == 0 && this.I().s_boolean()) {
         int j = this.s(this.H, (int)this.M(), this.x(), this.t());
         int k = this.s(this.H, (int)this.M(), this.x(), this.l);
         if (this.u((int)(this.M() + j - 5.0F), (int)this.E_float(), 7, this.Y(), this.H, this.c)) {
            this.T = true;
         } else if (this.u((int)(this.M() + k - 5.0F), (int)this.E_float(), 7, this.Y(), this.H, this.c)) {
            this.B = true;
         }
      }
   }

   @Override
   public void render(int i, int j) {
      int k = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.V.getColor() : client.getColorSetting().getValue().getRGB();
      NumberSetting numberSetting = (NumberSetting)this.l;
      int n = this.s(i, (int)this.M(), this.x(), this.t());
      int i2 = this.s(i, (int)this.M(), this.x(), this.l);
      if (numberSetting.valueClass == Integer.class) {
         NumberSetting numberSetting2 = numberSetting;
         NumberSetting numberSetting3 = this.c();
         if (numberSetting2.asInt() < numberSetting3.asInt()) {
            numberSetting2.setValue(numberSetting3.asInt());
         }

         if (numberSetting3.asInt() > numberSetting2.asInt()) {
            numberSetting3.setValue(numberSetting2.asInt());
         }
      } else if (numberSetting.valueClass == Double.class) {
         NumberSetting numberSetting4 = numberSetting;
         NumberSetting numberSetting5 = this.c();
         boolean bl = false;
         boolean bl2 = false;
         if (numberSetting4.asDouble() < numberSetting5.asDouble()) {
            bl = true;
         }

         if (numberSetting5.asDouble() > numberSetting4.asDouble()) {
            bl2 = true;
         }

         if (bl) {
            numberSetting4.setValue(numberSetting5.asDouble());
         }

         if (bl2) {
            numberSetting5.setValue(numberSetting4.asDouble());
         }
      } else {
         NumberSetting numberSetting6 = numberSetting;
         NumberSetting numberSetting7 = this.c();
         if (numberSetting6.asFloat() < numberSetting7.asFloat()) {
            numberSetting6.setValue((Number)numberSetting7.getValue());
         }

         if (numberSetting6.asFloat() > numberSetting7.asFloat()) {
            numberSetting7.setValue(numberSetting6.asFloat());
         }
      }

      String string = G.format(this.c().getValue()) + "-" + G.format(this.l.getValue());
      string = string + this.c().getSuffix();
      RenderUtil.drawRect(this.M() + n - 3.0F, this.E_float(), this.M() + n, this.E_float() + this.Y(), new Color(k).darker().getRGB());
      RenderUtil.drawRect(this.M() + n, this.E_float(), this.M() + i2, this.E_float() + this.Y(), k);
      if (i2 > n) {
         RenderUtil.drawRect(this.M() + i2 - 3.0F, this.E_float(), this.M() + i2, this.E_float() + this.Y(), new Color(k).darker().getRGB());
      }

      GuiMainMenu.m.drawString(this.c().getName().replace("Min-", ""), this.M() + 3.0F, this.E_float(), -1);
      GuiMainMenu.m.drawString(string, this.M() + this.x() - GuiMainMenu.m.getStringWidth(string, true), this.E_float() + 1.0F, 13421772);
      this.H = i;
      this.c = j;
   }

   public int s(int i, int j, int k, Setting setting) {
      int m = Math.max(Math.min(i - j, k), 0);
      NumberSetting numberSetting = (NumberSetting)setting;
      if (this.T && setting == this.c()) {
         this.T = Mouse.isButtonDown(0);
         if (numberSetting.valueClass == Integer.class) {
            NumberSetting numberSetting2 = numberSetting;
            numberSetting2.setValue(
               (int)(
                  ((Integer)numberSetting2.getMin()).intValue()
                     + 0.1F
                     + m * ((((Integer)numberSetting2.getMax()).intValue() + 0.1F - ((Integer)numberSetting2.getMin()).intValue() + 0.1F) / k)
               )
            );
         } else if (numberSetting.valueClass == Double.class) {
            NumberSetting numberSetting3 = numberSetting;
            numberSetting3.setValue((Double)numberSetting3.getMin() + m * (((Double)numberSetting3.getMax() - (Double)numberSetting3.getMin()) / k));
         } else {
            NumberSetting numberSetting4 = numberSetting;
            numberSetting4.setValue((Float)numberSetting4.getMin() + m * (((Float)numberSetting4.getMax() - (Float)numberSetting4.getMin()) / k));
         }
      }

      if (this.B && setting == this.l) {
         this.B = Mouse.isButtonDown(0);
         if (numberSetting.valueClass == Integer.class) {
            NumberSetting numberSetting5 = numberSetting;
            numberSetting5.setValue(
               (int)(
                  ((Integer)numberSetting5.getMin()).intValue()
                     + 0.1F
                     + m * ((((Integer)numberSetting5.getMax()).intValue() + 0.1F - ((Integer)numberSetting5.getMin()).intValue() + 0.1F) / k)
               )
            );
         } else if (numberSetting.valueClass == Double.class) {
            NumberSetting numberSetting6 = numberSetting;
            numberSetting6.setValue((Double)numberSetting6.getMin() + m * (((Double)numberSetting6.getMax() - (Double)numberSetting6.getMin()) / k));
         } else {
            NumberSetting numberSetting7 = numberSetting;
            numberSetting7.setValue((Float)numberSetting7.getMin() + m * (((Float)numberSetting7.getMax() - (Float)numberSetting7.getMin()) / k));
         }
      }

      if (numberSetting.valueClass == Integer.class) {
         NumberSetting numberSetting8 = numberSetting;
         return (int)(
            (numberSetting8.asInt() + 0.1F - ((Integer)numberSetting8.getMin()).intValue() + 0.1F)
               / (((Integer)numberSetting8.getMax()).intValue() + 0.1F - ((Integer)numberSetting8.getMin()).intValue() + 0.1F)
               * k
         );
      } else if (numberSetting.valueClass == Double.class) {
         NumberSetting numberSetting9 = numberSetting;
         return (int)((numberSetting9.asDouble() - (Double)numberSetting9.getMin()) / ((Double)numberSetting9.getMax() - (Double)numberSetting9.getMin()) * k);
      } else {
         NumberSetting numberSetting10 = numberSetting;
         return (int)((numberSetting10.asFloat() - (Float)numberSetting10.getMin()) / ((Float)numberSetting10.getMax() - (Float)numberSetting10.getMin()) * k);
      }
   }

   public NumberSetting c() {
      return (NumberSetting)this.t();
   }
}
