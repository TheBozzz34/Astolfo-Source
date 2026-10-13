package net.minecraft.entity.monster;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.world.World;

public abstract class EntityGolem extends EntityCreature implements IAnimals {
   private static final long ab = 13418293646981L;
   private static final long vb = 2195331580837232760L;

   public EntityGolem(World var1) {
      super(var1);
   }

   @Override
   public void fall(float var1, float var2) {
   }

   @Override
   public String getLivingSound() {
      return "none";
   }

   @Override
   public String getHurtSound() {
      return "none";
   }

   @Override
   public String getDeathSound() {
      return "none";
   }

   @Override
   public int getTalkInterval() {
      return (int)vb;
   }

   @Override
   public boolean canDespawn() {
      return false;
   }
}
