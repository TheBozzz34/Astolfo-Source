package bypass.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public final class GlHelper {
   public static List<Integer> u = new ArrayList<>();
   public static List<Integer> K = new ArrayList<>();
   private static int P;
   private static int m;
   private static final Random b = new Random();
   private static final long a = 111294764714019L;

   private GlHelper() {
   }

   public static void F(int[] var0) {
      m(var0[0], var0[1], var0[0] + var0[2], var0[1] + var0[3]);
   }

   public static void m(float var0, float var1, float var2, float var3) {
      int var8 = q();
      GL11.glScissor((int)(var0 * var8), (int)(Minecraft.getMinecraft().displayHeight - var3 * var8), (int)((var2 - var0) * var8), (int)((var3 - var1) * var8));
   }

   public static int T() {
      int var0 = GL11.glGenTextures();
      u.add(var0);
      return var0;
   }

   public static int i(int var0, File var1, int var2, int var3) throws IOException {
      X(var0, ImageIO.read(var1), var2, var3);
      return var0;
   }

   public static int X(int var0, BufferedImage var1, int var2, int var3) {
      int[] var6 = new int[var1.getWidth() * var1.getHeight()];
      var1.getRGB(0, 0, var1.getWidth(), var1.getHeight(), var6, 0, var1.getWidth());
      ByteBuffer var7 = BufferUtils.createByteBuffer(var1.getWidth() * var1.getHeight() * 4);

      for (int var8 = 0; var8 < var1.getHeight(); var8++) {
         for (int var9 = 0; var9 < var1.getWidth(); var9++) {
            int var10 = var6[var8 * var1.getWidth() + var9];
            var7.put((byte)(var10 >> 16 & 0xFF));
            var7.put((byte)(var10 >> 8 & 0xFF));
            var7.put((byte)(var10 & 0xFF));
            var7.put((byte)(var10 >> 24 & 0xFF));
         }
      }

      ((Buffer)var7).flip();
      q(var0, var1.getWidth(), var1.getHeight(), var7, var2, var3);
      return var0;
   }

   public static int q(int var0, int var1, int var2, ByteBuffer var3, int var4, int var5) {
      GL11.glBindTexture(3553, var0);
      GL11.glTexParameteri(3553, 10241, var4);
      GL11.glTexParameteri(3553, 10240, var4);
      GL11.glTexParameteri(3553, 10242, var5);
      GL11.glTexParameteri(3553, 10243, var5);
      GL11.glPixelStorei(3317, 1);
      GL11.glTexImage2D(3553, 0, 32856, var1, var2, 0, 6408, 5121, var3);
      GL11.glBindTexture(3553, 0);
      return var0;
   }

   public static int D() {
      int var2 = GL15.glGenBuffers();
      K.add(var2);
      GL15.glBindBuffer(34962, var2);
      return var2;
   }

   public static void g() {
      GL15.glBindBuffer(34962, 0);
      GL11.glBindTexture(3553, 0);

      for (int var4 : u) {
         GL11.glDeleteTextures(var4);
      }

      for (int var6 : K) {
         GL15.glDeleteBuffers(var6);
      }
   }

   public static int k() {
      return Mouse.getX() * G() / Minecraft.getMinecraft().displayWidth;
   }

   public static int Z() {
      return k() - P;
   }

   public static int d() {
      return v() - Mouse.getY() * v() / Minecraft.getMinecraft().displayHeight - 1;
   }

   public static int B() {
      return d() - m;
   }

   public static int G() {
      return Minecraft.getMinecraft().displayWidth / q();
   }

   public static int v() {
      return Minecraft.getMinecraft().displayHeight / q();
   }

   public static int q() {
      int var3 = 1;
      boolean var4 = Minecraft.getMinecraft().isUnicode();
      int var5 = Minecraft.getMinecraft().gameSettings.guiScale;
      if (var5 == 0) {
         var5 = 1000;
      }

      while (var3 < var5 && Minecraft.getMinecraft().displayWidth / (var3 + 1) >= 320 && Minecraft.getMinecraft().displayHeight / (var3 + 1) >= 240) {
         var3++;
      }

      if (var4 && var3 % 2 != 0 && var3 != 1) {
         var3--;
      }

      return var3;
   }

   public static void R(float var0, float var1, float var2, float var3) {
      GlStateManager.color(var0, var1, var2, var3);
   }

   public static void W(Color var0) {
      GlStateManager.color(var0.getRed() / 255.0F, var0.getGreen() / 255.0F, var0.getBlue() / 255.0F, var0.getAlpha() / 255.0F);
   }

   public static void l(Color var0, float var1) {
      GlStateManager.color(var0.getRed() / 255.0F, var0.getGreen() / 255.0F, var0.getBlue() / 255.0F, var1);
   }

   public static void R(int var0) {
      GlStateManager.color((var0 >> 16 & 0xFF) / 255.0F, (var0 >> 8 & 0xFF) / 255.0F, (var0 & 0xFF) / 255.0F, (var0 >> 24 & 0xFF) / 255.0F);
   }

   public static void N(int var0, float var1) {
      GlStateManager.color((var0 >> 16 & 0xFF) / 255.0F, (var0 >> 8 & 0xFF) / 255.0F, (var0 & 0xFF) / 255.0F, var1);
   }

   public static Color s(int var0, float var1) {
      float var2 = b.nextFloat();
      float var3 = ((float)b.nextInt(var0) + var0) / var0 + var0;
      return Color.getHSBColor(var2, var3, var1);
   }

   public static Color Z(float var0, float var1, float var2) {
      return Color.getHSBColor(var0, var1, var2);
   }

   public static Color o() {
      return s(1000, 0.6F);
   }

   public static void I() {
      P = k();
      m = d();
   }
}
