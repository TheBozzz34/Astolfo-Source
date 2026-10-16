package bypass.gui.clickgui.unused;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.render.RenderUtil;
import bypass.setting.Setting;
import java.awt.Color;
import org.lwjgl.opengl.GL11;

public class ColorWheel extends SettingComponent {
   public int selectedColor = Color.red.getRGB();

   public ColorWheel(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void onClick(int i) {
   }

   public void updateHueFromMouse(int i, int j) {
      float f = this.getX();
      float g = this.getX() + 10.0F;
      float h = this.getYPos();
      float f2 = this.getYPos() + 17.0F;
      float f3 = (f + g) / 2.0F;
      float f4 = (h + f2) / 2.0F;
      float[] floats = Color.RGBtoHSB(this.selectedColor >> 16 & 0xFF, this.selectedColor >> 8 & 0xFF, this.selectedColor & 0xFF, new float[3]);
      double d = i - f3;
      double e = j - f4;
      double d2 = Math.atan2(e, d);
      if (d2 < 0.0) {
         d2 += (float) (Math.PI * 2);
      } else if (d2 > (float) (Math.PI * 2)) {
         d2 -= (float) (Math.PI * 2);
      }

      this.selectedColor = Color.HSBtoRGB((float)(d2 / (float) (Math.PI * 2)), floats[1], floats[2]);
   }

   @Override
   public void render(int i, int j) {
      RenderUtil.drawRect(
         this.getX() + 1.5F,
         this.getYPos() - 1.0F,
         this.getX() + this.getWidth() + 0.5F,
         this.getYPos() + this.getHeight() + 4.0F,
         new Color(1, 1, 1, 120).getRGB()
      );
      client.getCustomFontRenderer().drawStringWithShadow("me gusta color", this.getX() + 20.0F, this.getYPos(), Color.WHITE.getRGB());
      float f = this.getX() + 10.0F;
      float g = this.getYPos() + 6.0F;
      float h = 5.0F;
      int k = this.selectedColor;
      float[] floats = Color.RGBtoHSB(k >> 16 & 0xFF, k >> 8 & 0xFF, k & 0xFF, new float[3]);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glBlendFunc(770, 771);
      GL11.glLineWidth(7.0F);
      GL11.glBegin(2);

      for (double d = 0.0; d < (float) (Math.PI * 2); d += 0.02F) {
         RenderUtil.setColor(Color.HSBtoRGB((float)(d / (float) (Math.PI * 2)), floats[1], floats[2]));
         GL11.glVertex2d(f + Math.cos(d) * h, g + Math.sin(d) * h);
      }

      GL11.glEnd();
      double e = floats[0] * (float) (Math.PI * 2);
      GL11.glBegin(1);
      RenderUtil.setColor(new Color(25, 25, 25, 255).getRGB());
      GL11.glVertex2d(f + Math.cos(e) * (h - 4.0F), g + Math.sin(e) * (h - 4.0F));
      GL11.glVertex2d(f + Math.cos(e) * (h + 4.0F), g + Math.sin(e) * (h + 4.0F));
      GL11.glEnd();
      GL11.glLineWidth(8.0F);
      GL11.glBegin(1);
      RenderUtil.setColor(new Color(200, 200, 200, 255).getRGB());
      GL11.glVertex2d(f + Math.cos(e) * (h - 3.5F), g + Math.sin(e) * (h - 3.5F));
      GL11.glVertex2d(f + Math.cos(e) * (h + 3.5F), g + Math.sin(e) * (h + 3.5F));
      GL11.glEnd();
      GL11.glLineWidth(1.0F);
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
   }

   public static void drawBorderedRect(float f, float g, float h, float f2, float f3, int i, int j) {
      RenderUtil.drawRect(f + f3, g + f3, h - f3, f2 - f3, j);
      RenderUtil.drawRect(f, g + f3, f + f3, f2 - f3, i);
      RenderUtil.drawRect(h - f3, g + f3, h, f2 - f3, i);
      RenderUtil.drawRect(f, g, h, g + f3, i);
      RenderUtil.drawRect(f, f2 - f3, h, f2, i);
   }

   public static void drawRectOutline(float f, float g, float h, float f2, float f3, int i) {
      RenderUtil.drawRect(f, g + f3, f + f3, f2 - f3, i);
      RenderUtil.drawRect(h - f3, g + f3, h, f2 - f3, i);
      RenderUtil.drawRect(f, g, h, g + f3, i);
      RenderUtil.drawRect(f, f2 - f3, h, f2, i);
   }
}
