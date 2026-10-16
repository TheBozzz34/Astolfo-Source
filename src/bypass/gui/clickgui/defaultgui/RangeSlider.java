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
   private final Category category;
   private Setting maxSetting;
   private boolean draggingMin;
   private boolean draggingMax;
   private int lastMouseX;
   private int lastMouseY;
   private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

   public RangeSlider(Category category, Container container, Setting setting, Setting setting2, int i, int j) {
      super(container, setting, i, j);
      this.category = category;
      this.maxSetting = setting2;
   }

   @Override
   public void onClick(int i) {
      if (i == 0 && this.getParentContainer().isExpanded()) {
         int j = this.updateAndGetHandlePos(this.lastMouseX, (int)this.getX(), this.getWidth(), this.getSetting());
         int k = this.updateAndGetHandlePos(this.lastMouseX, (int)this.getX(), this.getWidth(), this.maxSetting);
         if (this.isMouseInRect((int)(this.getX() + j - 5.0F), (int)this.getYPos(), 7, this.getHeight(), this.lastMouseX, this.lastMouseY)) {
            this.draggingMin = true;
         } else if (this.isMouseInRect((int)(this.getX() + k - 5.0F), (int)this.getYPos(), 7, this.getHeight(), this.lastMouseX, this.lastMouseY)) {
            this.draggingMax = true;
         }
      }
   }

   @Override
   public void render(int i, int j) {
      int k = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.category.getColor() : client.getColorSetting().getValue().getRGB();
      NumberSetting numberSetting = (NumberSetting)this.maxSetting;
      int l = this.updateAndGetHandlePos(i, (int)this.getX(), this.getWidth(), this.getSetting());
      int n = this.updateAndGetHandlePos(i, (int)this.getX(), this.getWidth(), this.maxSetting);
      if (numberSetting.valueClass == Integer.class) {
         NumberSetting numberSetting2 = numberSetting;
         NumberSetting numberSetting3 = this.getMinSetting();
         if (numberSetting2.asInt() < numberSetting3.asInt()) {
            numberSetting2.setValue(numberSetting3.asInt());
         }

         if (numberSetting3.asInt() > numberSetting2.asInt()) {
            numberSetting3.setValue(numberSetting2.asInt());
         }
      } else if (numberSetting.valueClass == Double.class) {
         NumberSetting numberSetting4 = numberSetting;
         NumberSetting numberSetting5 = this.getMinSetting();
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
         NumberSetting numberSetting7 = this.getMinSetting();
         if (numberSetting6.asFloat() < numberSetting7.asFloat()) {
            numberSetting6.setValue((Number)numberSetting7.getValue());
         }

         if (numberSetting6.asFloat() > numberSetting7.asFloat()) {
            numberSetting7.setValue(numberSetting6.asFloat());
         }
      }

      String string = decimalFormat.format(this.getMinSetting().getValue()) + "-" + decimalFormat.format(this.maxSetting.getValue());
      string = string + this.getMinSetting().getSuffix();
      RenderUtil.drawRect(this.getX() + l - 3.0F, this.getYPos(), this.getX() + l, this.getYPos() + this.getHeight(), new Color(k).darker().getRGB());
      RenderUtil.drawRect(this.getX() + l, this.getYPos(), this.getX() + n, this.getYPos() + this.getHeight(), k);
      if (n > l) {
         RenderUtil.drawRect(
            this.getX() + n - 3.0F, this.getYPos(), this.getX() + n, this.getYPos() + this.getHeight(), new Color(k).darker().getRGB()
         );
      }

      GuiMainMenu.m.drawString(this.getMinSetting().getName().replace("Min-", ""), this.getX() + 3.0F, this.getYPos(), -1);
      GuiMainMenu.m.drawString(string, this.getX() + this.getWidth() - GuiMainMenu.m.getStringWidth(string, true), this.getYPos() + 1.0F, 13421772);
      this.lastMouseX = i;
      this.lastMouseY = j;
   }

   public int updateAndGetHandlePos(int i, int j, int k, Setting setting) {
      int l = Math.max(Math.min(i - j, k), 0);
      NumberSetting numberSetting = (NumberSetting)setting;
      if (this.draggingMin && setting == this.getMinSetting()) {
         this.draggingMin = Mouse.isButtonDown(0);
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

      if (this.draggingMax && setting == this.maxSetting) {
         this.draggingMax = Mouse.isButtonDown(0);
         if (numberSetting.valueClass == Integer.class) {
            NumberSetting numberSetting5 = numberSetting;
            numberSetting5.setValue(
               (int)(
                  ((Integer)numberSetting5.getMin()).intValue()
                     + 0.1F
                     + l * ((((Integer)numberSetting5.getMax()).intValue() + 0.1F - ((Integer)numberSetting5.getMin()).intValue() + 0.1F) / k)
               )
            );
         } else if (numberSetting.valueClass == Double.class) {
            NumberSetting numberSetting6 = numberSetting;
            numberSetting6.setValue((Double)numberSetting6.getMin() + l * (((Double)numberSetting6.getMax() - (Double)numberSetting6.getMin()) / k));
         } else {
            NumberSetting numberSetting7 = numberSetting;
            numberSetting7.setValue((Float)numberSetting7.getMin() + l * (((Float)numberSetting7.getMax() - (Float)numberSetting7.getMin()) / k));
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

   public NumberSetting getMinSetting() {
      return (NumberSetting)this.getSetting();
   }
}
