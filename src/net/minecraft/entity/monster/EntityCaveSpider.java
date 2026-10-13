package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityCaveSpider extends EntitySpider {
   private static final long ab = 130610965861365L;

   public EntityCaveSpider(World var1) {
      super(var1);
      this.setSize(0.7F, 0.5F);
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(12.0);
   }

   @Override
   public boolean attackEntityAsMob(Entity var1) {
      if (super.attackEntityAsMob(var1)) {
         if (var1 instanceof EntityLivingBase) {
            byte var5 = 0;
            if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL) {
               var5 = 7;
            } else if (this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
               var5 = 15;
            }

            if (var5 > 0) {
               ((EntityLivingBase)var1).addPotionEffect(new PotionEffect(Potion.poison.id, var5 * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      return var2;
   }

   @Override
   public float getEyeHeight() {
      return 0.45F;
   }
}
