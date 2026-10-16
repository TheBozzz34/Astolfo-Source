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
   private final Category category;
   private boolean sliderDragging;
   private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

   public NumberSlider(Category category, Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
      this.category = category;
   }

   @Override
   public void onClick(int i) {
      if (i == 0 && this.getParentContainer().isExpanded()) {
         this.sliderDragging = true;
      }
   }

   @Override
   public void render(int i, int j) {
      int k = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.category.getColor() : client.getColorSetting().getValue().getRGB();
      int l = this.updateAndGetFillWidth(i, (int)this.getX(), this.getWidth());
      String string = decimalFormat.format(this.getNumberSetting().getValue());
      if (this.getNumberSetting().getSuffix().equals("stacks")) {
         if (this.getNumberSetting().asInt() == 1) {
            string = string + " stack";
         } else {
            string = string + " " + this.getNumberSetting().getSuffix();
         }
      } else {
         string = string + this.getNumberSetting().getSuffix();
      }

      RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + l, this.getYPos() + this.getHeight(), k);
      RenderUtil.drawRect(
         this.getX() + Math.max(l - 3, 0), this.getYPos(), this.getX() + l, this.getYPos() + this.getHeight(), new Color(k).darker().getRGB()
      );
      GuiMainMenu.m.drawString(this.getNumberSetting().getName(), this.getX() + 3.0F, this.getYPos(), -1);
      GuiMainMenu.m.drawString(string, this.getX() + this.getWidth() - GuiMainMenu.m.getStringWidth(string, true), this.getYPos() + 1.0F, 13421772);
   }

   public int updateAndGetFillWidth(int i, int j, int k) {
      int l = Math.max(Math.min(i - j, k), 0);
      NumberSetting numberSetting = this.getNumberSetting();
      if (this.sliderDragging) {
         this.sliderDragging = Mouse.isButtonDown(0);
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

   public NumberSetting getNumberSetting() {
      return (NumberSetting)this.getSetting();
   }
}
