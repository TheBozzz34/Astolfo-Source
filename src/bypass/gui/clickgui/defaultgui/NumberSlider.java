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

   public NumberSlider(Category var1, Container var2, Setting var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.w = var1;
   }

   @Override
   public void l(int var1) {
      if (var1 == 0 && this.I().s_boolean()) {
         this.Z = true;
      }
   }

   @Override
   public void render(int var1, int var2) {
      int var6 = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.w.getColor() : client.getColorSetting().getValue().getRGB();
      int var7 = this.a(var1, (int)this.M(), this.x());
      String var8 = o.format(this.o().getValue());
      if (this.o().getSuffix().equals("stacks")) {
         if (this.o().asInt() == 1) {
            var8 = var8 + " stack";
         } else {
            var8 = var8 + " " + this.o().getSuffix();
         }
      } else {
         var8 = var8 + this.o().getSuffix();
      }

      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + var7, this.E_float() + this.Y(), var6);
      RenderUtil.drawRect(this.M() + Math.max(var7 - 3, 0), this.E_float(), this.M() + var7, this.E_float() + this.Y(), new Color(var6).darker().getRGB());
      GuiMainMenu.m.drawString(this.o().getName(), this.M() + 3.0F, this.E_float(), -1);
      GuiMainMenu.m.drawString(var8, this.M() + this.x() - GuiMainMenu.m.getStringWidth(var8, true), this.E_float() + 1.0F, (int)j);
   }

   public int a(int var1, int var2, int var3) {
      int var7 = Math.max(Math.min(var1 - var2, var3), 0);
      NumberSetting var8 = this.o();
      if (this.Z) {
         this.Z = Mouse.isButtonDown(0);
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
            (var14.asInt() + 0.1F - ((Integer)var14.getMin()).intValue() + 0.1F)
               / (((Integer)var14.getMax()).intValue() + 0.1F - ((Integer)var14.getMin()).intValue() + 0.1F)
               * var3
         );
      } else if (var8.valueClass == Double.class) {
         NumberSetting var13 = var8;
         return (int)((var13.asDouble() - (Double)var13.getMin()) / ((Double)var13.getMax() - (Double)var13.getMin()) * var3);
      } else {
         NumberSetting var12 = var8;
         return (int)((var12.asFloat() - (Float)var12.getMin()) / ((Float)var12.getMax() - (Float)var12.getMin()) * var3);
      }
   }

   public NumberSetting o() {
      return (NumberSetting)this.t();
   }
}
