package shadersmod.client;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class HFNoiseTexture {
   public int h = GL11.glGenTextures();
   public int X = 15;
   private static final long a = 134986854817065L;

   public HFNoiseTexture(int width, int height) {
      byte[] bytes = this.W(width, height);
      ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bytes.length);
      byteBuffer.put(bytes);
      ((Buffer)byteBuffer).flip();
      GlStateManager.bindTexture(this.h);
      GL11.glTexImage2D(3553, 0, 6407, width, height, 0, 6407, 5121, byteBuffer);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10241, 9729);
      GlStateManager.bindTexture(0);
   }

   public int y() {
      return this.h;
   }

   public void C() {
      GlStateManager.deleteTexture(this.h);
      this.h = 0;
   }

   private int y(int i) {
      i ^= i << 13;
      i ^= i >> 17;
      return i ^ i << 5;
   }

   private byte C(int i, int j, int k) {
      int l = (this.y(i) + this.y(j * 19)) * this.y(k * 23) - k;
      return (byte)(this.y(l) % 128);
   }

   private byte[] W(int i, int j) {
      byte[] bytes = new byte[i * j * 3];
      int k = 0;

      for (int l = 0; l < j; l++) {
         for (int m = 0; m < i; m++) {
            for (int n = 1; n < 4; n++) {
               bytes[k++] = this.C(m, l, n);
            }
         }
      }

      return bytes;
   }
}
