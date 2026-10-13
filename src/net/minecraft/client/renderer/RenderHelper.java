package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
   private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
   private static final Vec3 LIGHT0_POS = new Vec3(0.2F, 1.0, -0.7F).normalize();
   private static final Vec3 LIGHT1_POS = new Vec3(-0.2F, 1.0, 0.7F).normalize();
   private static final long a = 127962110091667L;

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
      float var2 = 0.4F;
      float var3 = 0.6F;
      float var4 = 0.0F;
      GL11.glLight(16384, 4611, setColorBuffer(LIGHT0_POS.xCoord, LIGHT0_POS.yCoord, LIGHT0_POS.zCoord, 0.0));
      GL11.glLight(16384, 4609, setColorBuffer(var3, var3, var3, 1.0F));
      GL11.glLight(16384, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GL11.glLight(16384, 4610, setColorBuffer(var4, var4, var4, 1.0F));
      GL11.glLight(16385, 4611, setColorBuffer(LIGHT1_POS.xCoord, LIGHT1_POS.yCoord, LIGHT1_POS.zCoord, 0.0));
      GL11.glLight(16385, 4609, setColorBuffer(var3, var3, var3, 1.0F));
      GL11.glLight(16385, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GL11.glLight(16385, 4610, setColorBuffer(var4, var4, var4, 1.0F));
      GlStateManager.shadeModel(7424);
      GL11.glLightModel(2899, setColorBuffer(var2, var2, var2, 1.0F));
   }

   private static FloatBuffer setColorBuffer(double var0, double var2, double var4, double var6) {
      return setColorBuffer((float)var0, (float)var2, (float)var4, (float)var6);
   }

   private static FloatBuffer setColorBuffer(float var0, float var1, float var2, float var3) {
      ((Buffer)colorBuffer).clear();
      colorBuffer.put(var0).put(var1).put(var2).put(var3);
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
