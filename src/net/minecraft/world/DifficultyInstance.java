package net.minecraft.world;

import net.minecraft.util.MathHelper;

public class DifficultyInstance {
   private final EnumDifficulty worldDifficulty;
   private final float additionalDifficulty;
   private static final long a = 42500662477259L;

   public DifficultyInstance(EnumDifficulty worldDifficulty, long worldTime, long chunkInhabitedTime, float moonPhaseFactor) {
      this.worldDifficulty = worldDifficulty;
      this.additionalDifficulty = this.calculateAdditionalDifficulty(worldDifficulty, worldTime, chunkInhabitedTime, moonPhaseFactor);
   }

   public float getAdditionalDifficulty() {
      return this.additionalDifficulty;
   }

   public float getClampedAdditionalDifficulty() {
      return this.additionalDifficulty < 2.0F ? 0.0F : (this.additionalDifficulty > 4.0F ? 1.0F : (this.additionalDifficulty - 2.0F) / 2.0F);
   }

   private float calculateAdditionalDifficulty(EnumDifficulty difficulty, long worldTime, long chunkInhabitedTime, float moonPhaseFactor) {
      if (difficulty == EnumDifficulty.PEACEFUL) {
         return 0.0F;
      }

      boolean bl = difficulty == EnumDifficulty.HARD;
      float f = 0.75F;
      float g = MathHelper.clamp_float(((float)worldTime + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
      f += g;
      float h = 0.0F;
      h += MathHelper.clamp_float((float)chunkInhabitedTime / 3600000.0F, 0.0F, 1.0F) * (bl ? 1.0F : 0.75F);
      h += MathHelper.clamp_float(moonPhaseFactor * 0.25F, 0.0F, g);
      if (difficulty == EnumDifficulty.EASY) {
         h *= 0.5F;
      }

      f += h;
      return difficulty.getDifficultyId() * f;
   }
}
