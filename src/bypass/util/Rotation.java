package bypass.util;

import net.minecraft.util.MathHelper;

public class Rotation {
   private float yaw;
   private float pitch;

   public Rotation(float f, float g) {
      this.yaw = f;
      this.pitch = g;
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

   public void setYaw(float f) {
      this.yaw = f;
   }

   public void setPitch(float f) {
      this.pitch = f;
   }

   public Rotation wrap() {
      this.setYaw(MathHelper.wrapAngleTo180_float(this.yaw));
      this.setPitch(MathHelper.wrapAngleTo180_float(this.pitch));
      return this;
   }
}
