package bypass.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class AimRotation {
   private float G;
   private float c;
   private boolean f;
   private static final long a = 36825389874458L;

   public AimRotation(float f, float g) {
      this.G = f;
      this.c = g;
   }

   public void U(EntityPlayer entityPlayer) {
      if (!Float.isNaN(this.G) && !Float.isNaN(this.c)) {
         if (this.f) {
            this.n(Minecraft.getMinecraft().gameSettings.Zm);
         }

         float g = MathHelper.wrapAngleTo180_float(this.G - entityPlayer.rotationYaw);
         this.G = entityPlayer.rotationYaw + g;
         entityPlayer.rotationYaw = this.G;
         entityPlayer.rotationPitch = this.c;
      }
   }

   public void a(boolean bl) {
      this.f = bl;
   }

   public void n(float f) {
      float g = f * 0.6F + 0.2F;
      float h = g * g * g * 1.2F;
      this.G = this.G - this.G % h;
      this.c = this.c - this.c % h;
   }

   public float z() {
      return this.c;
   }

   public float r() {
      return this.G;
   }
}
