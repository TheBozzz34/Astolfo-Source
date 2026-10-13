package bypass.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class AimRotation {
   private float G;
   private float c;
   private boolean f;
   private static final long a = 36825389874458L;

   public AimRotation(float var1, float var2) {
      this.G = var1;
      this.c = var2;
   }

   public void U(EntityPlayer var1) {
      if (!Float.isNaN(this.G) && !Float.isNaN(this.c)) {
         if (this.f) {
            this.n(Minecraft.getMinecraft().gameSettings.Zm);
         }

         float var5 = MathHelper.wrapAngleTo180_float(this.G - var1.rotationYaw);
         this.G = var1.rotationYaw + var5;
         var1.rotationYaw = this.G;
         var1.rotationPitch = this.c;
      }
   }

   public void a(boolean var1) {
      this.f = var1;
   }

   public void n(float var1) {
      float var2 = var1 * 0.6F + 0.2F;
      float var3 = var2 * var2 * var2 * 1.2F;
      this.G = this.G - this.G % var3;
      this.c = this.c - this.c % var3;
   }

   public float z() {
      return this.c;
   }

   public float r() {
      return this.G;
   }
}
