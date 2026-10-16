package bypass.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class AimRotation {
   private float yaw;
   private float pitch;
   private boolean gcdFixEnabled;

   public AimRotation(float f, float g) {
      this.yaw = f;
      this.pitch = g;
   }

   public void applyTo(EntityPlayer entityPlayer) {
      if (!Float.isNaN(this.yaw) && !Float.isNaN(this.pitch)) {
         if (this.gcdFixEnabled) {
            this.applyGcdFix(Minecraft.getMinecraft().gameSettings.Zm);
         }

         float f = MathHelper.wrapAngleTo180_float(this.yaw - entityPlayer.rotationYaw);
         this.yaw = entityPlayer.rotationYaw + f;
         entityPlayer.rotationYaw = this.yaw;
         entityPlayer.rotationPitch = this.pitch;
      }
   }

   public void setGcdFixEnabled(boolean bl) {
      this.gcdFixEnabled = bl;
   }

   public void applyGcdFix(float f) {
      float g = f * 0.6F + 0.2F;
      float h = g * g * g * 1.2F;
      this.yaw = this.yaw - this.yaw % h;
      this.pitch = this.pitch - this.pitch % h;
   }

   public float getPitch() {
      return this.pitch;
   }

   public float getYaw() {
      return this.yaw;
   }
}
