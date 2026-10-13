package bypass.util;

import java.util.Random;
import net.minecraft.util.Vector3d;

public class Rotator {
   private boolean randomize;
   private float smoothing;
   private Random random;
   private static final double s = 180.0 / Math.PI;
   private static final long a = 17189128633061L;

   public Rotator(boolean var1, float var2) {
      this.randomize = var1;
      this.smoothing = var2;
      this.random = new Random();
   }

   public Rotation rotationTo(Vector3d var1, Vector3d var2) {
      Rotation var7 = new Rotation();
      Vector3d var8 = var1;
      var8.x = var8.x + ((this.randomize ? this.randomBetween(-0.5F, 0.5F) : 0.0F) - var2.x);
      var8.y = var8.y + ((this.randomize ? this.randomBetween(-0.25F, 0.5F) : 0.0F) - var2.y);
      var8.z = var8.z + ((this.randomize ? this.randomBetween(-0.5F, 0.5F) : 0.0F) - var2.z);
      double var9 = Math.hypot(var8.x, var8.z);
      var7.setYaw((float)(Math.atan2(var8.z, var8.x) * (180.0 / Math.PI)) - 90.0F);
      var7.setPitch(-((float)(Math.atan2(var8.y, var9) * (180.0 / Math.PI))));
      return var7.wrap();
   }

   public Rotation smooth(Rotation var1, Rotation var2) {
      Rotation var3 = new Rotation(var2.getYaw() - var1.getYaw(), var2.getPitch() - var1.getPitch()).wrap();
      var3.setYaw(var2.getYaw() - var3.getYaw() / 100.0F * this.smoothing);
      var3.setPitch(var2.getPitch() - var3.getPitch() / 100.0F * this.smoothing);
      return var3.wrap();
   }

   public Vector3d predict(Vector3d var1, Vector3d var2, boolean var3, double var4, double var6) {
      var6 -= var6 % 2.0;
      var6 /= 2.0;
      double var11 = var6 * var2.x * (var3 ? var4 + 1.0 : var4);
      double var13 = var6 * var2.z * (var3 ? var4 + 1.0 : var4);
      return new Vector3d(var1.x + var11, var1.y, var1.z + var13);
   }

   public Rotation bezier(Rotation var1, Rotation var2, float var3) {
      float var7 = this.randomBetween(0.02F, 0.4F);
      float var8 = this.randomBetween(0.8F, 1.3F);
      Rotation var9 = this.lerp(var1, var2, var7);
      Rotation var10 = this.lerp(var1, var2, var8);
      Rotation var11 = this.lerp(var1, var9, var3);
      Rotation var12 = this.lerp(var9, var10, var3);
      Rotation var13 = this.lerp(var10, var2, var3);
      Rotation var14 = this.lerp(var11, var12, var3);
      Rotation var15 = this.lerp(var12, var13, var3);
      Rotation var16 = this.lerp(var14, var15, var3);
      return var16.wrap();
   }

   private Rotation lerp(Rotation var1, Rotation var2, float var3) {
      Rotation var4 = new Rotation();
      var4.setYaw(var1.getYaw() + (var2.getYaw() - var1.getYaw()) * var3);
      var4.setPitch(var1.getPitch() + (var2.getPitch() - var1.getPitch()) * var3);
      return var4;
   }

   public float randomBetween(float var1, float var2) {
      return var1 + this.random.nextFloat() * (var2 - var1);
   }

   public void setRandomize(boolean var1) {
      this.randomize = var1;
   }

   public void setSmoothing(float var1) {
      this.smoothing = var1;
   }
}
