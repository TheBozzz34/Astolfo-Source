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
   private static final long c = 94672730686451L;
   private static final long j = 826872970764668108L;

   public NumberSlider(Category category, Container container, Setting setting, int i, int k) {
      super(container, setting, i, k);
      this.w = category;
   }

   @Override
   public void l(int i) {
      if (i == 0 && this.I().s_boolean()) {
         this.Z = true;
      }
   }

   @Override
   public void render(int i, int k) {
      int l = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.w.getColor() : client.getColorSetting().getValue().getRGB();
      int n = this.a(i, (int)this.M(), this.x());
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

      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + n, this.E_float() + this.Y(), l);
      RenderUtil.drawRect(this.M() + Math.max(n - 3, 0), this.E_float(), this.M() + n, this.E_float() + this.Y(), new Color(l).darker().getRGB());
      GuiMainMenu.m.drawString(this.o().getName(), this.M() + 3.0F, this.E_float(), -1);
      GuiMainMenu.m.drawString(string, this.M() + this.x() - GuiMainMenu.m.getStringWidth(string, true), this.E_float() + 1.0F, (int)j);
   }

   public int a(int i, int k, int l) {
      int j = Math.max(Math.min(i - k, l), 0);
      NumberSetting numberSetting = this.o();
      if (this.Z) {
         this.Z = Mouse.isButtonDown(0);
         if (numberSetting.valueClass == Integer.class) {
            NumberSetting numberSetting2 = numberSetting;
            numberSetting2.setValue(
               (int)(
                  ((Integer)numberSetting2.getMin()).intValue()
                     + 0.1F
                     + j * ((((Integer)numberSetting2.getMax()).intValue() + 0.1F - ((Integer)numberSetting2.getMin()).intValue() + 0.1F) / l)
               )
            );
         } else if (numberSetting.valueClass == Double.class) {
            NumberSetting numberSetting3 = numberSetting;
            numberSetting3.setValue((Double)numberSetting3.getMin() + j * (((Double)numberSetting3.getMax() - (Double)numberSetting3.getMin()) / l));
         } else {
            NumberSetting numberSetting4 = numberSetting;
            numberSetting4.setValue((Float)numberSetting4.getMin() + j * (((Float)numberSetting4.getMax() - (Float)numberSetting4.getMin()) / l));
         }
      }

      if (numberSetting.valueClass == Integer.class) {
         NumberSetting numberSetting5 = numberSetting;
         return (int)(
            (numberSetting5.asInt() + 0.1F - ((Integer)numberSetting5.getMin()).intValue() + 0.1F)
               / (((Integer)numberSetting5.getMax()).intValue() + 0.1F - ((Integer)numberSetting5.getMin()).intValue() + 0.1F)
               * l
         );
      } else if (numberSetting.valueClass == Double.class) {
         NumberSetting numberSetting6 = numberSetting;
         return (int)((numberSetting6.asDouble() - (Double)numberSetting6.getMin()) / ((Double)numberSetting6.getMax() - (Double)numberSetting6.getMin()) * l);
      } else {
         NumberSetting numberSetting7 = numberSetting;
         return (int)((numberSetting7.asFloat() - (Float)numberSetting7.getMin()) / ((Float)numberSetting7.getMax() - (Float)numberSetting7.getMin()) * l);
      }
   }

   public NumberSetting o() {
      return (NumberSetting)this.t();
   }
}
