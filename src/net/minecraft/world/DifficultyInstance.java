package net.minecraft.world;

import net.minecraft.util.MathHelper;

public class DifficultyInstance {
   private final EnumDifficulty worldDifficulty;
   private final float additionalDifficulty;
   private static final long a = 42500662477259L;

   public DifficultyInstance(EnumDifficulty var1, long var2, long var4, float var6) {
      this.worldDifficulty = var1;
      this.additionalDifficulty = this.calculateAdditionalDifficulty(var1, var2, var4, var6);
   }

   public float getAdditionalDifficulty() {
      return this.additionalDifficulty;
   }

   public float getClampedAdditionalDifficulty() {
      return this.additionalDifficulty < 2.0F ? 0.0F : (this.additionalDifficulty > 4.0F ? 1.0F : (this.additionalDifficulty - 2.0F) / 2.0F);
   }

   private float calculateAdditionalDifficulty(EnumDifficulty var1, long var2, long var4, float var6) {
      if (var1 == EnumDifficulty.PEACEFUL) {
         return 0.0F;
      }

      boolean var10 = var1 == EnumDifficulty.HARD;
      float var11 = 0.75F;
      float var12 = MathHelper.clamp_float(((float)var2 + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
      var11 += var12;
      float var13 = 0.0F;
      var13 += MathHelper.clamp_float((float)var4 / 3600000.0F, 0.0F, 1.0F) * (var10 ? 1.0F : 0.75F);
      var13 += MathHelper.clamp_float(var6 * 0.25F, 0.0F, var12);
      if (var1 == EnumDifficulty.EASY) {
         var13 *= 0.5F;
      }

      var11 += var13;
      return var1.getDifficultyId() * var11;
   }
}
