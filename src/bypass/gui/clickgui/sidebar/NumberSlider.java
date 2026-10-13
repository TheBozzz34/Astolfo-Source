package bypass.gui.clickgui.sidebar;

import bypass.Astolfo;
import bypass.render.RenderUtil;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;
import org.lwjgl.input.Mouse;

public class NumberSlider extends SidebarSettingComponent {
   private boolean d;
   private static final long e = 111044635657820L;

   public NumberSlider(ModuleButton var1, Setting var2, float var3, float var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void l(int var1) {
      if (var1 == 0 && this.y()) {
         this.d = true;
      }
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = e ^ 127480501704750L;
      long var5 = var3 ^ 8972929756414L;
      float var8 = this.M() + this.x() / 1.7F;
      int var9 = this.t(var1, (int)var8, 60);
      String var10 = String.format("%.1f", Double.valueOf(this.b().asString()));
      RenderUtil.drawRect(var8 - 0.5F, this.E_float() + 0.5F, var8 + 60.0F, this.E_float() + 4.5F, new Color(34, 36, 43, this.X).getRGB());
      RenderUtil.drawRect(
         var8,
         this.E_float() + 1.0F,
         var8 + var9,
         this.E_float() + 5.0F,
         this.d ? Astolfo.INSTANCE.getSidebarGui().U() : new Color(255, 255, 255, this.X).getRGB()
      );
      if (this.X > 40) {
         GuiMainMenu.c.drawString(var10, var8 + var9, this.E_float() + 4.0F, new Color(255, 255, 255, this.X).getRGB());
      }

      if (this.X > 40) {
         client.getCustomFontRenderer().drawString(this.b().getName(), this.M(), this.E_float(), new Color(255, 255, 255, this.X).getRGB());
      }
   }

   public int t(int var1, int var2, int var3) {
      int var7 = Math.max(Math.min(var1 - var2, var3), 0);
      NumberSetting var8 = this.b();
      if (this.d) {
         this.d = Mouse.isButtonDown(0);
         if (var8.valueClass == Integer.class) {
            NumberSetting var9 = var8;
            var9.setValue(
               (int)(
                  ((Integer)var9.getMin()).intValue()
                     + 0.1F
                     + var7 * ((((Integer)var9.getMax()).intValue() + 0.1F - ((Integer)var9.getMin()).intValue() + 0.1F) / var3)
               )
            );
         } else if (var8.valueClass == Double.class) {
            NumberSetting var10 = var8;
            var10.setValue((Double)var10.getMin() + var7 * (((Double)var10.getMax() - (Double)var10.getMin()) / var3));
         } else {
            NumberSetting var11 = var8;
            var11.setValue((Float)var11.getMin() + var7 * (((Float)var11.getMax() - (Float)var11.getMin()) / var3));
         }
      }

      if (var8.valueClass == Integer.class) {
         NumberSetting var14 = var8;
         return (int)(
            (((Integer)var14.getValue()).intValue() + 0.1F - ((Integer)var14.getMin()).intValue() + 0.1F)
               / (((Integer)var14.getMax()).intValue() + 0.1F - ((Integer)var14.getMin()).intValue() + 0.1F)
               * var3
         );
      } else if (var8.valueClass == Double.class) {
         NumberSetting var13 = var8;
         return (int)(((Double)var13.getValue() - (Double)var13.getMin()) / ((Double)var13.getMax() - (Double)var13.getMin()) * var3);
      } else {
         NumberSetting var12 = var8;
         return (int)(((Float)var12.getValue() - (Float)var12.getMin()) / ((Float)var12.getMax() - (Float)var12.getMin()) * var3);
      }
   }

   public NumberSetting b() {
      return (NumberSetting)this.H();
   }
}
