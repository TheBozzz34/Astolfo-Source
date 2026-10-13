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

   public RangeSlider(Category var1, Container var2, Setting var3, Setting var4, int var5, int var6) {
      super(var2, var3, var5, var6);
      this.V = var1;
      this.l = var4;
   }

   @Override
   public void l(int var1) {
      if (var1 == 0 && this.I().s_boolean()) {
         int var5 = this.s(this.H, (int)this.M(), this.x(), this.t());
         int var6 = this.s(this.H, (int)this.M(), this.x(), this.l);
         if (this.u((int)(this.M() + var5 - 5.0F), (int)this.E_float(), 7, this.Y(), this.H, this.c)) {
            this.T = true;
         } else if (this.u((int)(this.M() + var6 - 5.0F), (int)this.E_float(), 7, this.Y(), this.H, this.c)) {
            this.B = true;
         }
      }
   }

   @Override
   public void render(int var1, int var2) {
      int var6 = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.V.getColor() : client.getColorSetting().getValue().getRGB();
      NumberSetting var7 = (NumberSetting)this.l;
      int var8 = this.s(var1, (int)this.M(), this.x(), this.t());
      int var9 = this.s(var1, (int)this.M(), this.x(), this.l);
      if (var7.valueClass == Integer.class) {
         NumberSetting var10 = var7;
         NumberSetting var11 = this.c();
         if (var10.asInt() < var11.asInt()) {
            var10.setValue(var11.asInt());
         }

         if (var11.asInt() > var10.asInt()) {
            var11.setValue(var10.asInt());
         }
      } else if (var7.valueClass == Double.class) {
         NumberSetting var14 = var7;
         NumberSetting var18 = this.c();
         boolean var12 = false;
         boolean var13 = false;
         if (var14.asDouble() < var18.asDouble()) {
            var12 = true;
         }

         if (var18.asDouble() > var14.asDouble()) {
            var13 = true;
         }

         if (var12) {
            var14.setValue(var18.asDouble());
         }

         if (var13) {
            var18.setValue(var14.asDouble());
         }
      } else {
         NumberSetting var15 = var7;
         NumberSetting var19 = this.c();
         if (var15.asFloat() < var19.asFloat()) {
            var15.setValue((Number)var19.getValue());
         }

         if (var15.asFloat() > var19.asFloat()) {
            var19.setValue(var15.asFloat());
         }
      }

      String var16 = G.format(this.c().getValue()) + "-" + G.format(this.l.getValue());
      var16 = var16 + this.c().getSuffix();
      RenderUtil.drawRect(this.M() + var8 - 3.0F, this.E_float(), this.M() + var8, this.E_float() + this.Y(), new Color(var6).darker().getRGB());
      RenderUtil.drawRect(this.M() + var8, this.E_float(), this.M() + var9, this.E_float() + this.Y(), var6);
      if (var9 > var8) {
         RenderUtil.drawRect(this.M() + var9 - 3.0F, this.E_float(), this.M() + var9, this.E_float() + this.Y(), new Color(var6).darker().getRGB());
      }

      GuiMainMenu.m.drawString(this.c().getName().replace("Min-", ""), this.M() + 3.0F, this.E_float(), -1);
      GuiMainMenu.m.drawString(var16, this.M() + this.x() - GuiMainMenu.m.getStringWidth(var16, true), this.E_float() + 1.0F, 13421772);
      this.H = var1;
      this.c = var2;
   }

   public int s(int var1, int var2, int var3, Setting var4) {
      int var8 = Math.max(Math.min(var1 - var2, var3), 0);
      NumberSetting var9 = (NumberSetting)var4;
      if (this.T && var4 == this.c()) {
         this.T = Mouse.isButtonDown(0);
         if (var9.valueClass == Integer.class) {
            NumberSetting var10 = var9;
            var10.setValue(
               (int)(
                  ((Integer)var10.getMin()).intValue()
                     + 0.1F
                     + var8 * ((((Integer)var10.getMax()).intValue() + 0.1F - ((Integer)var10.getMin()).intValue() + 0.1F) / var3)
               )
            );
         } else if (var9.valueClass == Double.class) {
            NumberSetting var11 = var9;
            var11.setValue((Double)var11.getMin() + var8 * (((Double)var11.getMax() - (Double)var11.getMin()) / var3));
         } else {
            NumberSetting var12 = var9;
            var12.setValue((Float)var12.getMin() + var8 * (((Float)var12.getMax() - (Float)var12.getMin()) / var3));
         }
      }

      if (this.B && var4 == this.l) {
         this.B = Mouse.isButtonDown(0);
         if (var9.valueClass == Integer.class) {
            NumberSetting var13 = var9;
            var13.setValue(
               (int)(
                  ((Integer)var13.getMin()).intValue()
                     + 0.1F
                     + var8 * ((((Integer)var13.getMax()).intValue() + 0.1F - ((Integer)var13.getMin()).intValue() + 0.1F) / var3)
               )
            );
         } else if (var9.valueClass == Double.class) {
            NumberSetting var14 = var9;
            var14.setValue((Double)var14.getMin() + var8 * (((Double)var14.getMax() - (Double)var14.getMin()) / var3));
         } else {
            NumberSetting var15 = var9;
            var15.setValue((Float)var15.getMin() + var8 * (((Float)var15.getMax() - (Float)var15.getMin()) / var3));
         }
      }

      if (var9.valueClass == Integer.class) {
         NumberSetting var18 = var9;
         return (int)(
            (var18.asInt() + 0.1F - ((Integer)var18.getMin()).intValue() + 0.1F)
               / (((Integer)var18.getMax()).intValue() + 0.1F - ((Integer)var18.getMin()).intValue() + 0.1F)
               * var3
         );
      } else if (var9.valueClass == Double.class) {
         NumberSetting var17 = var9;
         return (int)((var17.asDouble() - (Double)var17.getMin()) / ((Double)var17.getMax() - (Double)var17.getMin()) * var3);
      } else {
         NumberSetting var16 = var9;
         return (int)((var16.asFloat() - (Float)var16.getMin()) / ((Float)var16.getMax() - (Float)var16.getMin()) * var3);
      }
   }

   public NumberSetting c() {
      return (NumberSetting)this.t();
   }
}
