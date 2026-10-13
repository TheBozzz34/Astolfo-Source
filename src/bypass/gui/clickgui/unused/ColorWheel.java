package bypass.gui.clickgui.unused;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.render.RenderUtil;
import bypass.setting.Setting;
import java.awt.Color;
import org.lwjgl.opengl.GL11;

public class ColorWheel extends SettingComponent {
   public int L = Color.red.getRGB();
   private static final long c = 27443863746824L;

   public ColorWheel(Container var1, Setting var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void l(int var1) {
   }

   public void R(int var1, int var2) {
      float var6 = this.M();
      float var7 = this.M() + 10.0F;
      float var8 = this.E_float();
      float var9 = this.E_float() + 17.0F;
      float var10 = (var6 + var7) / 2.0F;
      float var11 = (var8 + var9) / 2.0F;
      float[] var13 = Color.RGBtoHSB(this.L >> 16 & 0xFF, this.L >> 8 & 0xFF, this.L & 0xFF, new float[3]);
      double var14 = var1 - var10;
      double var16 = var2 - var11;
      double var18 = Math.atan2(var16, var14);
      if (var18 < 0.0) {
         var18 += (float) (Math.PI * 2);
      } else if (var18 > (float) (Math.PI * 2)) {
         var18 -= (float) (Math.PI * 2);
      }

      this.L = Color.HSBtoRGB((float)(var18 / (float) (Math.PI * 2)), var13[1], var13[2]);
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = c ^ 17566477772666L;
      RenderUtil.drawRect(
         this.M() + 1.5F, this.E_float() - 1.0F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 4.0F, new Color(1, 1, 1, 120).getRGB()
      );
      client.getCustomFontRenderer().drawStringWithShadow("me gusta color", this.M() + 20.0F, this.E_float(), Color.WHITE.getRGB());
      float var7 = this.M() + 10.0F;
      float var8 = this.E_float() + 6.0F;
      float var9 = 5.0F;
      int var10 = this.L;
      float[] var11 = Color.RGBtoHSB(var10 >> 16 & 0xFF, var10 >> 8 & 0xFF, var10 & 0xFF, new float[3]);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glBlendFunc(770, 771);
      GL11.glLineWidth(7.0F);
      GL11.glBegin(2);

      for (double var12 = 0.0; var12 < (float) (Math.PI * 2); var12 += 0.02F) {
         RenderUtil.setColor(Color.HSBtoRGB((float)(var12 / (float) (Math.PI * 2)), var11[1], var11[2]));
         GL11.glVertex2d(var7 + Math.cos(var12) * var9, var8 + Math.sin(var12) * var9);
      }

      GL11.glEnd();
      double var14 = var11[0] * (float) (Math.PI * 2);
      GL11.glBegin(1);
      RenderUtil.setColor(new Color(25, 25, 25, 255).getRGB());
      GL11.glVertex2d(var7 + Math.cos(var14) * (var9 - 4.0F), var8 + Math.sin(var14) * (var9 - 4.0F));
      GL11.glVertex2d(var7 + Math.cos(var14) * (var9 + 4.0F), var8 + Math.sin(var14) * (var9 + 4.0F));
      GL11.glEnd();
      GL11.glLineWidth(8.0F);
      GL11.glBegin(1);
      RenderUtil.setColor(new Color(200, 200, 200, 255).getRGB());
      GL11.glVertex2d(var7 + Math.cos(var14) * (var9 - 3.5F), var8 + Math.sin(var14) * (var9 - 3.5F));
      long var10000 = 7592729224049090461L ^ var3;
      GL11.glVertex2d(var7 + Math.cos(var14) * (var9 + 3.5F), var8 + Math.sin(var14) * (var9 + 3.5F));
      GL11.glEnd();
      GL11.glLineWidth(1.0F);
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
   }

   public static void t(float var0, float var1, float var2, float var3, float var4, int var5, int var6) {
      RenderUtil.drawRect(var0 + var4, var1 + var4, var2 - var4, var3 - var4, var6);
      RenderUtil.drawRect(var0, var1 + var4, var0 + var4, var3 - var4, var5);
      RenderUtil.drawRect(var2 - var4, var1 + var4, var2, var3 - var4, var5);
      RenderUtil.drawRect(var0, var1, var2, var1 + var4, var5);
      RenderUtil.drawRect(var0, var3 - var4, var2, var3, var5);
   }

   public static void z(float var0, float var1, float var2, float var3, float var4, int var5) {
      RenderUtil.drawRect(var0, var1 + var4, var0 + var4, var3 - var4, var5);
      RenderUtil.drawRect(var2 - var4, var1 + var4, var2, var3 - var4, var5);
      RenderUtil.drawRect(var0, var1, var2, var1 + var4, var5);
      RenderUtil.drawRect(var0, var3 - var4, var2, var3, var5);
   }
}
