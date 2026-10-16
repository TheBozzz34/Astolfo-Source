package bypass.gui.clickgui.sidebar;

import bypass.Astolfo;
import bypass.render.RenderUtil;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;
import org.lwjgl.input.Mouse;

public class NumberSlider extends SidebarSettingComponent {
   private boolean sliderDragging;

   public NumberSlider(ModuleButton moduleButton, Setting setting, float f, float g, int i, int j) {
      super(moduleButton, setting, f, g, i, j);
   }

   @Override
   public void onClick(int i) {
      if (i == 0 && this.isVisible()) {
         this.sliderDragging = true;
      }
   }

   @Override
   public void render(int i, int j) {
      float g = this.getX() + this.getWidth() / 1.7F;
      int k = this.updateAndGetFillWidth(i, (int)g, 60);
      String string = String.format("%.1f", Double.valueOf(this.getNumberSetting().asString()));
      RenderUtil.drawRect(g - 0.5F, this.getYPos() + 0.5F, g + 60.0F, this.getYPos() + 4.5F, new Color(34, 36, 43, this.fadeAlpha).getRGB());
      RenderUtil.drawRect(
         g,
         this.getYPos() + 1.0F,
         g + k,
         this.getYPos() + 5.0F,
         this.sliderDragging ? Astolfo.INSTANCE.getSidebarGui().getPulseColor() : new Color(255, 255, 255, this.fadeAlpha).getRGB()
      );
      if (this.fadeAlpha > 40) {
         GuiMainMenu.c.drawString(string, g + k, this.getYPos() + 4.0F, new Color(255, 255, 255, this.fadeAlpha).getRGB());
      }

      if (this.fadeAlpha > 40) {
         client.getCustomFontRenderer()
            .drawString(this.getNumberSetting().getName(), this.getX(), this.getYPos(), new Color(255, 255, 255, this.fadeAlpha).getRGB());
      }
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
            (((Integer)numberSetting5.getValue()).intValue() + 0.1F - ((Integer)numberSetting5.getMin()).intValue() + 0.1F)
               / (((Integer)numberSetting5.getMax()).intValue() + 0.1F - ((Integer)numberSetting5.getMin()).intValue() + 0.1F)
               * k
         );
      } else if (numberSetting.valueClass == Double.class) {
         NumberSetting numberSetting6 = numberSetting;
         return (int)(((Double)numberSetting6.getValue() - (Double)numberSetting6.getMin()) / ((Double)numberSetting6.getMax() - (Double)numberSetting6.getMin()) * k);
      } else {
         NumberSetting numberSetting7 = numberSetting;
         return (int)(((Float)numberSetting7.getValue() - (Float)numberSetting7.getMin()) / ((Float)numberSetting7.getMax() - (Float)numberSetting7.getMin()) * k);
      }
   }

   public NumberSetting getNumberSetting() {
      return (NumberSetting)this.getSetting();
   }
}
