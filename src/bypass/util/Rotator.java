package bypass.util;

import java.util.Random;
import net.minecraft.util.Vector3d;

public class Rotator {
   private boolean randomize;
   private float smoothing;
   private Random random;
   private static final double s = 180.0 / Math.PI;
   private static final long a = 17189128633061L;

   public Rotator(boolean bl, float f) {
      this.randomize = bl;
      this.smoothing = f;
      this.random = new Random();
   }

   public Rotation rotationTo(Vector3d vector3d, Vector3d vector3d2) {
      Rotation rotation = new Rotation();
      Vector3d vector3d3 = vector3d;
      vector3d3.x = vector3d3.x + ((this.randomize ? this.randomBetween(-0.5F, 0.5F) : 0.0F) - vector3d2.x);
      vector3d3.y = vector3d3.y + ((this.randomize ? this.randomBetween(-0.25F, 0.5F) : 0.0F) - vector3d2.y);
      vector3d3.z = vector3d3.z + ((this.randomize ? this.randomBetween(-0.5F, 0.5F) : 0.0F) - vector3d2.z);
      double d = Math.hypot(vector3d3.x, vector3d3.z);
      rotation.setYaw((float)(Math.atan2(vector3d3.z, vector3d3.x) * (180.0 / Math.PI)) - 90.0F);
      rotation.setPitch(-((float)(Math.atan2(vector3d3.y, d) * (180.0 / Math.PI))));
      return rotation.wrap();
   }

   public Rotation smooth(Rotation rotation, Rotation rotation2) {
      Rotation rotation3 = new Rotation(rotation2.getYaw() - rotation.getYaw(), rotation2.getPitch() - rotation.getPitch()).wrap();
      rotation3.setYaw(rotation2.getYaw() - rotation3.getYaw() / 100.0F * this.smoothing);
      rotation3.setPitch(rotation2.getPitch() - rotation3.getPitch() / 100.0F * this.smoothing);
      return rotation3.wrap();
   }

   public Vector3d predict(Vector3d vector3d, Vector3d vector3d2, boolean bl, double d, double d2) {
      d2 -= d2 % 2.0;
      d2 /= 2.0;
      double e = d2 * vector3d2.x * (bl ? d + 1.0 : d);
      double d3 = d2 * vector3d2.z * (bl ? d + 1.0 : d);
      return new Vector3d(vector3d.x + e, vector3d.y, vector3d.z + d3);
   }

   public Rotation bezier(Rotation rotation, Rotation rotation2, float f) {
      float g = this.randomBetween(0.02F, 0.4F);
      float h = this.randomBetween(0.8F, 1.3F);
      Rotation rotation3 = this.lerp(rotation, rotation2, g);
      Rotation rotation4 = this.lerp(rotation, rotation2, h);
      Rotation rotation5 = this.lerp(rotation, rotation3, f);
      Rotation rotation6 = this.lerp(rotation3, rotation4, f);
      Rotation rotation7 = this.lerp(rotation4, rotation2, f);
      Rotation rotation8 = this.lerp(rotation5, rotation6, f);
      Rotation rotation9 = this.lerp(rotation6, rotation7, f);
      Rotation rotation10 = this.lerp(rotation8, rotation9, f);
      return rotation10.wrap();
   }

   private Rotation lerp(Rotation rotation, Rotation rotation2, float f) {
      Rotation rotation3 = new Rotation();
      rotation3.setYaw(rotation.getYaw() + (rotation2.getYaw() - rotation.getYaw()) * f);
      rotation3.setPitch(rotation.getPitch() + (rotation2.getPitch() - rotation.getPitch()) * f);
      return rotation3;
   }

   public float randomBetween(float f, float g) {
      return f + this.random.nextFloat() * (g - f);
   }

   public void setRandomize(boolean bl) {
      this.randomize = bl;
   }

   public void setSmoothing(float f) {
      this.smoothing = f;
   }
}
