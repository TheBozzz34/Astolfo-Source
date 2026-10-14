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

   public static void F(int[] ints) {
      m(ints[0], ints[1], ints[0] + ints[2], ints[1] + ints[3]);
   }

   public static void m(float f, float g, float h, float f2) {
      int i = q();
      GL11.glScissor((int)(f * i), (int)(Minecraft.getMinecraft().displayHeight - f2 * i), (int)((h - f) * i), (int)((f2 - g) * i));
   }

   public static int T() {
      int i = GL11.glGenTextures();
      u.add(i);
      return i;
   }

   public static int i(int i, File file, int j, int k) throws IOException {
      X(i, ImageIO.read(file), j, k);
      return i;
   }

   public static int X(int i, BufferedImage bufferedImage, int j, int k) {
      int[] ints = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
      bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), ints, 0, bufferedImage.getWidth());
      ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bufferedImage.getWidth() * bufferedImage.getHeight() * 4);

      for (int l = 0; l < bufferedImage.getHeight(); l++) {
         for (int m = 0; m < bufferedImage.getWidth(); m++) {
            int n = ints[l * bufferedImage.getWidth() + m];
            byteBuffer.put((byte)(n >> 16 & 0xFF));
            byteBuffer.put((byte)(n >> 8 & 0xFF));
            byteBuffer.put((byte)(n & 0xFF));
            byteBuffer.put((byte)(n >> 24 & 0xFF));
         }
      }

      ((Buffer)byteBuffer).flip();
      q(i, bufferedImage.getWidth(), bufferedImage.getHeight(), byteBuffer, j, k);
      return i;
   }

   public static int q(int i, int j, int k, ByteBuffer byteBuffer, int l, int n) {
      GL11.glBindTexture(3553, i);
      GL11.glTexParameteri(3553, 10241, l);
      GL11.glTexParameteri(3553, 10240, l);
      GL11.glTexParameteri(3553, 10242, n);
      GL11.glTexParameteri(3553, 10243, n);
      GL11.glPixelStorei(3317, 1);
      GL11.glTexImage2D(3553, 0, 32856, j, k, 0, 6408, 5121, byteBuffer);
      GL11.glBindTexture(3553, 0);
      return i;
   }

   public static int D() {
      int i = GL15.glGenBuffers();
      K.add(i);
      GL15.glBindBuffer(34962, i);
      return i;
   }

   public static void g() {
      GL15.glBindBuffer(34962, 0);
      GL11.glBindTexture(3553, 0);

      for (int i : u) {
         GL11.glDeleteTextures(i);
      }

      for (int j : K) {
         GL15.glDeleteBuffers(j);
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
      int i = 1;
      boolean bl = Minecraft.getMinecraft().isUnicode();
      int j = Minecraft.getMinecraft().gameSettings.guiScale;
      if (j == 0) {
         j = 1000;
      }

      while (i < j && Minecraft.getMinecraft().displayWidth / (i + 1) >= 320 && Minecraft.getMinecraft().displayHeight / (i + 1) >= 240) {
         i++;
      }

      if (bl && i % 2 != 0 && i != 1) {
         i--;
      }

      return i;
   }

   public static void R(float f, float g, float h, float f2) {
      GlStateManager.color(f, g, h, f2);
   }

   public static void W(Color color) {
      GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
   }

   public static void l(Color color, float f) {
      GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f);
   }

   public static void R(int i) {
      GlStateManager.color((i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, (i >> 24 & 0xFF) / 255.0F);
   }

   public static void N(int i, float f) {
      GlStateManager.color((i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, f);
   }

   public static Color s(int i, float f) {
      float g = b.nextFloat();
      float h = ((float)b.nextInt(i) + i) / i + i;
      return Color.getHSBColor(g, h, f);
   }

   public static Color Z(float f, float g, float h) {
      return Color.getHSBColor(f, g, h);
   }

   public static Color o() {
      return s(1000, 0.6F);
   }

   public static void I() {
      P = k();
      m = d();
   }
}
