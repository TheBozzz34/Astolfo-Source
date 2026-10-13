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

   public HFNoiseTexture(int var1, int var2) {
      byte[] var5 = this.W(var1, var2);
      ByteBuffer var6 = BufferUtils.createByteBuffer(var5.length);
      var6.put(var5);
      ((Buffer)var6).flip();
      GlStateManager.bindTexture(this.h);
      GL11.glTexImage2D(3553, 0, 6407, var1, var2, 0, 6407, 5121, var6);
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

   private int y(int var1) {
      var1 ^= var1 << 13;
      var1 ^= var1 >> 17;
      return var1 ^ var1 << 5;
   }

   private byte C(int var1, int var2, int var3) {
      int var6 = (this.y(var1) + this.y(var2 * 19)) * this.y(var3 * 23) - var3;
      return (byte)(this.y(var6) % 128);
   }

   private byte[] W(int var1, int var2) {
      byte[] var6 = new byte[var1 * var2 * 3];
      int var7 = 0;

      for (int var8 = 0; var8 < var2; var8++) {
         for (int var9 = 0; var9 < var1; var9++) {
            for (int var10 = 1; var10 < 4; var10++) {
               var6[var7++] = this.C(var9, var8, var10);
            }
         }
      }

      return var6;
   }
}
