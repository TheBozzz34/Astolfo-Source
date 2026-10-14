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

   public static IChatComponent saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer) {
      return saveScreenshot(gameDirectory, (String)null, width, height, buffer);
   }

   public static IChatComponent saveScreenshot(File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer) {
      try {
         File file = new File(gameDirectory, "screenshots");
         file.mkdir();
         if (OpenGlHelper.isFramebufferEnabled()) {
            width = buffer.framebufferTextureWidth;
            height = buffer.framebufferTextureHeight;
         }

         int i = width * height;
         if (pixelBuffer == null || pixelBuffer.capacity() < i) {
            pixelBuffer = BufferUtils.createIntBuffer(i);
            pixelValues = new int[i];
         }

         GL11.glPixelStorei(3333, 1);
         GL11.glPixelStorei(3317, 1);
         ((Buffer)pixelBuffer).clear();
         if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(buffer.framebufferTexture);
            GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
         } else {
            GL11.glReadPixels(0, 0, width, height, 32993, 33639, pixelBuffer);
         }

         pixelBuffer.get(pixelValues);
         TextureUtil.processPixelValues(pixelValues, width, height);
         BufferedImage bufferedImage = null;
         if (OpenGlHelper.isFramebufferEnabled()) {
            bufferedImage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
            int j = buffer.framebufferTextureHeight - buffer.framebufferHeight;

            for (int k = j; k < buffer.framebufferTextureHeight; k++) {
               for (int l = 0; l < buffer.framebufferWidth; l++) {
                  bufferedImage.setRGB(l, k - j, pixelValues[k * buffer.framebufferTextureWidth + l]);
               }
            }
         } else {
            bufferedImage = new BufferedImage(width, height, 1);
            bufferedImage.setRGB(0, 0, width, height, pixelValues, 0, width);
         }

         File file2;
         if (screenshotName == null) {
            file2 = getTimestampedPNGFileForDirectory(file);
         } else {
            file2 = new File(file, screenshotName);
         }

         ImageIO.write(bufferedImage, "png", file2);
         ChatComponentText chatComponentText = new ChatComponentText(file2.getName());
         chatComponentText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
         chatComponentText.getChatStyle().setUnderlined(true);
         return new ChatComponentTranslation("screenshot.success", chatComponentText);
      } catch (Exception exception) {
         logger.warn("Couldn't save screenshot", exception);
         return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
      }
   }

   private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
      String string = dateFormat.format(new Date()).toString();
      int i = 1;

      while (true) {
         File file = new File(gameDirectory, string + (i == 1 ? "" : "_" + i) + ".png");
         if (!file.exists()) {
            return file;
         }

         i++;
      }
   }
}
