package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
   private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
   private static final Vec3 LIGHT0_POS = new Vec3(0.2F, 1.0, -0.7F).normalize();
   private static final Vec3 LIGHT1_POS = new Vec3(-0.2F, 1.0, 0.7F).normalize();

   public static void disableStandardItemLighting() {
      GlStateManager.disableLighting();
      GlStateManager.disableLight(0);
      GlStateManager.disableLight(1);
      GlStateManager.disableColorMaterial();
   }

   public static void enableStandardItemLighting() {
      GlStateManager.enableLighting();
      GlStateManager.enableLight(0);
      GlStateManager.enableLight(1);
      GlStateManager.enableColorMaterial();
      GlStateManager.colorMaterial(1032, 5634);
      float f = 0.4F;
      float g = 0.6F;
      float h = 0.0F;
      GL11.glLight(16384, 4611, setColorBuffer(LIGHT0_POS.xCoord, LIGHT0_POS.yCoord, LIGHT0_POS.zCoord, 0.0));
      GL11.glLight(16384, 4609, setColorBuffer(g, g, g, 1.0F));
      GL11.glLight(16384, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GL11.glLight(16384, 4610, setColorBuffer(h, h, h, 1.0F));
      GL11.glLight(16385, 4611, setColorBuffer(LIGHT1_POS.xCoord, LIGHT1_POS.yCoord, LIGHT1_POS.zCoord, 0.0));
      GL11.glLight(16385, 4609, setColorBuffer(g, g, g, 1.0F));
      GL11.glLight(16385, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GL11.glLight(16385, 4610, setColorBuffer(h, h, h, 1.0F));
      GlStateManager.shadeModel(7424);
      GL11.glLightModel(2899, setColorBuffer(f, f, f, 1.0F));
   }

   private static FloatBuffer setColorBuffer(double d, double d2, double d3, double d4) {
      return setColorBuffer((float)d, (float)d2, (float)d3, (float)d4);
   }

   private static FloatBuffer setColorBuffer(float f, float g, float h, float f2) {
      ((Buffer)colorBuffer).clear();
      colorBuffer.put(f).put(g).put(h).put(f2);
      ((Buffer)colorBuffer).flip();
      return colorBuffer;
   }

   public static void enableGUIStandardItemLighting() {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
      enableStandardItemLighting();
      GlStateManager.popMatrix();
   }
}
