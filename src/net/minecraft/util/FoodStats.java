package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;

public class FoodStats {
   private int foodLevel = 20;
   private float foodSaturationLevel = 5.0F;
   private float foodExhaustionLevel;
   private int foodTimer;
   private int prevFoodLevel = 20;

   public void addStats(int foodLevelIn, float foodSaturationModifier) {
      this.foodLevel = Math.min(foodLevelIn + this.foodLevel, 20);
      this.foodSaturationLevel = Math.min(this.foodSaturationLevel + foodLevelIn * foodSaturationModifier * 2.0F, this.foodLevel);
   }

   public void addStats(ItemFood foodItem, ItemStack itemStack) {
      this.addStats(foodItem.getHealAmount(itemStack), foodItem.getSaturationModifier(itemStack));
   }

   public void onUpdate(EntityPlayer player) {
      EnumDifficulty enumDifficulty = player.worldObj.getDifficulty();
      this.prevFoodLevel = this.foodLevel;
      if (this.foodExhaustionLevel > 4.0F) {
         this.foodExhaustionLevel -= 4.0F;
         if (this.foodSaturationLevel > 0.0F) {
            this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
         } else if (enumDifficulty != EnumDifficulty.PEACEFUL) {
            this.foodLevel = Math.max(this.foodLevel - 1, 0);
         }
      }

      if (player.worldObj.getGameRules().getBoolean("naturalRegeneration") && this.foodLevel >= 18 && player.shouldHeal()) {
         this.foodTimer++;
         if (this.foodTimer >= 80) {
            player.heal(1.0F);
            this.addExhaustion(3.0F);
            this.foodTimer = 0;
         }
      } else if (this.foodLevel <= 0) {
         this.foodTimer++;
         if (this.foodTimer >= 80) {
            if (player.getHealth() > 10.0F || enumDifficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && enumDifficulty == EnumDifficulty.NORMAL) {
               player.attackEntityFrom(DamageSource.starve, 1.0F);
            }

            this.foodTimer = 0;
         }
      } else {
         this.foodTimer = 0;
      }
   }

   public void readNBT(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound.hasKey("foodLevel", 99)) {
         this.foodLevel = nbtTagCompound.getInteger("foodLevel");
         this.foodTimer = nbtTagCompound.getInteger("foodTickTimer");
         this.foodSaturationLevel = nbtTagCompound.getFloat("foodSaturationLevel");
         this.foodExhaustionLevel = nbtTagCompound.getFloat("foodExhaustionLevel");
      }
   }

   public void writeNBT(NBTTagCompound nbtTagCompound) {
      nbtTagCompound.setInteger("foodLevel", this.foodLevel);
      nbtTagCompound.setInteger("foodTickTimer", this.foodTimer);
      nbtTagCompound.setFloat("foodSaturationLevel", this.foodSaturationLevel);
      nbtTagCompound.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
   }

   public int getFoodLevel() {
      return this.foodLevel;
   }

   public int getPrevFoodLevel() {
      return this.prevFoodLevel;
   }

   public boolean needFood() {
      return this.foodLevel < 20;
   }

   public void addExhaustion(float f) {
      this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + f, 40.0F);
   }

   public float getSaturationLevel() {
      return this.foodSaturationLevel;
   }

   public void setFoodLevel(int foodLevelIn) {
      this.foodLevel = foodLevelIn;
   }

   public void setFoodSaturationLevel(float foodSaturationLevelIn) {
      this.foodSaturationLevel = foodSaturationLevelIn;
   }
}
