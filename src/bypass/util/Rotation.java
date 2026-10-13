package bypass.util;

import net.minecraft.util.MathHelper;

public class Rotation {
   private float yaw;
   private float pitch;

   public Rotation(float var1, float var2) {
      this.yaw = var1;
      this.pitch = var2;
   }

   public Rotation() {
      this(0.0F, 0.0F);
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setYaw(float var1) {
      this.yaw = var1;
   }

   public void setPitch(float var1) {
      this.pitch = var1;
   }

   public Rotation wrap() {
      this.setYaw(MathHelper.wrapAngleTo180_float(this.yaw));
      this.setPitch(MathHelper.wrapAngleTo180_float(this.pitch));
      return this;
   }
}
