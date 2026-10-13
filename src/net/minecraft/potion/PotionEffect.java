package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEffect {
   private static final Logger LOGGER = LogManager.getLogger();
   private int potionID;
   private int duration;
   private int amplifier;
   private boolean isSplashPotion;
   private boolean isAmbient;
   private boolean isPotionDurationMax;
   private boolean showParticles;
   private static final long a = 124145540083956L;

   public PotionEffect(int var1, int var2) {
      this(var1, var2, 0);
   }

   public PotionEffect(Potion var1) {
      this(var1.id, 0, 0);
   }

   public PotionEffect(int var1, int var2, int var3) {
      this(var1, var2, var3, false, true);
   }

   public PotionEffect(int var1, int var2, int var3, boolean var4, boolean var5) {
      this.potionID = var1;
      this.duration = var2;
      this.amplifier = var3;
      this.isAmbient = var4;
      this.showParticles = var5;
   }

   public PotionEffect(PotionEffect var1) {
      this.potionID = var1.potionID;
      this.duration = var1.duration;
      this.amplifier = var1.amplifier;
      this.isAmbient = var1.isAmbient;
      this.showParticles = var1.showParticles;
   }

   public void combine(PotionEffect var1) {
      if (this.potionID != var1.potionID) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      if (var1.amplifier > this.amplifier) {
         this.amplifier = var1.amplifier;
         this.duration = var1.duration;
      } else if (var1.amplifier == this.amplifier && this.duration < var1.duration) {
         this.duration = var1.duration;
      } else if (!var1.isAmbient && this.isAmbient) {
         this.isAmbient = var1.isAmbient;
      }

      this.showParticles = var1.showParticles;
   }

   public int getPotionID() {
      return this.potionID;
   }

   public int getDuration() {
      return this.duration;
   }

   public void w(int var1) {
      this.duration = var1;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public void setSplashPotion(boolean var1) {
      this.isSplashPotion = var1;
   }

   public boolean getIsAmbient() {
      return this.isAmbient;
   }

   public boolean getIsShowParticles() {
      return this.showParticles;
   }

   public boolean onUpdate(EntityLivingBase var1) {
      if (this.duration > 0) {
         if (Potion.potionTypes[this.potionID].isReady(this.duration, this.amplifier)) {
            this.performEffect(var1);
         }

         this.deincrementDuration();
      }

      return this.duration > 0;
   }

   private int deincrementDuration() {
      return --this.duration;
   }

   public void performEffect(EntityLivingBase var1) {
      if (this.duration > 0) {
         Potion.potionTypes[this.potionID].performEffect(var1, this.amplifier);
      }
   }

   public String getEffectName() {
      return Potion.potionTypes[this.potionID].getName();
   }

   @Override
   public int hashCode() {
      return this.potionID;
   }

   @Override
   public String toString() {
      String var4 = "";
      if (this.getAmplifier() > 0) {
         var4 = this.getEffectName() + " x " + (this.getAmplifier() + 1) + ", Duration: " + this.getDuration();
      } else {
         var4 = this.getEffectName() + ", Duration: " + this.getDuration();
      }

      if (this.isSplashPotion) {
         var4 = var4 + ", Splash: true";
      }

      if (!this.showParticles) {
         var4 = var4 + ", Particles: false";
      }

      return Potion.potionTypes[this.potionID].isUsable() ? "(" + var4 + ")" : var4;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PotionEffect)) {
         return false;
      }

      PotionEffect var5 = (PotionEffect)var1;
      return this.potionID == var5.potionID
         && this.amplifier == var5.amplifier
         && this.duration == var5.duration
         && this.isSplashPotion == var5.isSplashPotion
         && this.isAmbient == var5.isAmbient;
   }

   public NBTTagCompound writeCustomPotionEffectToNBT(NBTTagCompound var1) {
      var1.setByte("Id", (byte)this.getPotionID());
      var1.setByte("Amplifier", (byte)this.getAmplifier());
      var1.setInteger("Duration", this.getDuration());
      var1.setBoolean("Ambient", this.getIsAmbient());
      var1.setBoolean("ShowParticles", this.getIsShowParticles());
      return var1;
   }

   public static PotionEffect readCustomPotionEffectFromNBT(NBTTagCompound var0) {
      byte var4 = var0.getByte("Id");
      if (var4 >= 0 && var4 < Potion.potionTypes.length && Potion.potionTypes[var4] != null) {
         byte var5 = var0.getByte("Amplifier");
         int var6 = var0.getInteger("Duration");
         boolean var7 = var0.getBoolean("Ambient");
         boolean var8 = true;
         if (var0.hasKey("ShowParticles", 1)) {
            var8 = var0.getBoolean("ShowParticles");
         }

         return new PotionEffect(var4, var6, var5, var7, var8);
      } else {
         return null;
      }
   }

   public void setPotionDurationMax(boolean var1) {
      this.isPotionDurationMax = var1;
   }

   public boolean getIsPotionDurationMax() {
      return this.isPotionDurationMax;
   }
}
