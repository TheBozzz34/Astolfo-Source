package shadersmod.client;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class HFNoiseTexture {
   public int texID = GL11.glGenTextures();
   public int textureUnit = 15;

   public HFNoiseTexture(int width, int height) {
      byte[] bytes = this.genHFNoiseImage(width, height);
      ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bytes.length);
      byteBuffer.put(bytes);
      ((Buffer)byteBuffer).flip();
      GlStateManager.bindTexture(this.texID);
      GL11.glTexImage2D(3553, 0, 6407, width, height, 0, 6407, 5121, byteBuffer);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10241, 9729);
      GlStateManager.bindTexture(0);
   }

   public int getTexID() {
      return this.texID;
   }

   public void deleteTexture() {
      GlStateManager.deleteTexture(this.texID);
      this.texID = 0;
   }

   private int y(int i) {
      i ^= i << 13;
      i ^= i >> 17;
      return i ^ i << 5;
   }

   private byte random(int x, int y, int z) {
      int i = (this.y(x) + this.y(y * 19)) * this.y(z * 23) - z;
      return (byte)(this.y(i) % 128);
   }

   private byte[] genHFNoiseImage(int width, int height) {
      byte[] bytes = new byte[width * height * 3];
      int i = 0;

      for (int j = 0; j < height; j++) {
         for (int k = 0; k < width; k++) {
            for (int l = 1; l < 4; l++) {
               bytes[i++] = this.random(k, j, l);
            }
         }
      }

      return bytes;
   }
}
