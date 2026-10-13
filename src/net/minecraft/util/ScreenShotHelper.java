package net.minecraft.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ScreenShotHelper {
   private static final Logger logger = LogManager.getLogger();
   private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
   private static IntBuffer pixelBuffer;
   private static int[] pixelValues;
   private static final long a = 137457736366518L;

   public static IChatComponent saveScreenshot(File var0, int var1, int var2, Framebuffer var3) {
      return saveScreenshot(var0, (String)null, var1, var2, var3);
   }

   public static IChatComponent saveScreenshot(File var0, String var1, int var2, int var3, Framebuffer var4) {
      try {
         File var8 = new File(var0, "screenshots");
         var8.mkdir();
         if (OpenGlHelper.isFramebufferEnabled()) {
            var2 = var4.framebufferTextureWidth;
            var3 = var4.framebufferTextureHeight;
         }

         int var9 = var2 * var3;
         if (pixelBuffer == null || pixelBuffer.capacity() < var9) {
            pixelBuffer = BufferUtils.createIntBuffer(var9);
            pixelValues = new int[var9];
         }

         GL11.glPixelStorei(3333, 1);
         GL11.glPixelStorei(3317, 1);
         ((Buffer)pixelBuffer).clear();
         if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(var4.framebufferTexture);
            GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
         } else {
            GL11.glReadPixels(0, 0, var2, var3, 32993, 33639, pixelBuffer);
         }

         pixelBuffer.get(pixelValues);
         TextureUtil.processPixelValues(pixelValues, var2, var3);
         BufferedImage var10 = null;
         if (OpenGlHelper.isFramebufferEnabled()) {
            var10 = new BufferedImage(var4.framebufferWidth, var4.framebufferHeight, 1);
            int var11 = var4.framebufferTextureHeight - var4.framebufferHeight;

            for (int var12 = var11; var12 < var4.framebufferTextureHeight; var12++) {
               for (int var13 = 0; var13 < var4.framebufferWidth; var13++) {
                  var10.setRGB(var13, var12 - var11, pixelValues[var12 * var4.framebufferTextureWidth + var13]);
               }
            }
         } else {
            var10 = new BufferedImage(var2, var3, 1);
            var10.setRGB(0, 0, var2, var3, pixelValues, 0, var2);
         }

         File var16;
         if (var1 == null) {
            var16 = getTimestampedPNGFileForDirectory(var8);
         } else {
            var16 = new File(var8, var1);
         }

         ImageIO.write(var10, "png", var16);
         ChatComponentText var17 = new ChatComponentText(var16.getName());
         var17.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var16.getAbsolutePath()));
         var17.getChatStyle().setUnderlined(true);
         return new ChatComponentTranslation("screenshot.success", var17);
      } catch (Exception var14) {
         logger.warn("Couldn't save screenshot", var14);
         return new ChatComponentTranslation("screenshot.failure", var14.getMessage());
      }
   }

   private static File getTimestampedPNGFileForDirectory(File var0) {
      String var4 = dateFormat.format(new Date()).toString();
      int var5 = 1;

      while (true) {
         File var6 = new File(var0, var4 + (var5 == 1 ? "" : "_" + var5) + ".png");
         if (!var6.exists()) {
            return var6;
         }

         var5++;
      }
   }
}
