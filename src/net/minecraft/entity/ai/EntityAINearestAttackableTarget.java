package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class EntityAINearestAttackableTarget<T extends EntityLivingBase> extends EntityAITarget {
   public final Class<T> targetClass;
   private final int targetChance;
   public final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
   public Predicate<? super T> targetEntitySelector;
   public EntityLivingBase targetEntity;

   public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
      this(creature, classTarget, checkSight, false);
   }

   public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
      this(creature, classTarget, 10, checkSight, onlyNearby, (Predicate<? super T>)null);
   }

   public EntityAINearestAttackableTarget(
      EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, final Predicate<? super T> targetSelector
   ) {
      super(creature, checkSight, onlyNearby);
      this.targetClass = classTarget;
      this.targetChance = chance;
      this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(creature);
      this.setMutexBits(1);
      this.targetEntitySelector = new Predicate<T>() {
         public boolean apply(T entityLivingBase) {
            if (targetSelector != null && !targetSelector.apply(entityLivingBase)) {
               return false;
            }

            if (entityLivingBase instanceof EntityPlayer) {
               double d = EntityAINearestAttackableTarget.this.getTargetDistance();
               if (entityLivingBase.isSneaking()) {
                  d *= 0.8F;
               }

               if (entityLivingBase.isInvisible()) {
                  float f = ((EntityPlayer)entityLivingBase).getArmorVisibility();
                  if (f < 0.1F) {
                     f = 0.1F;
                  }

                  d *= 0.7F * f;
               }

               if (entityLivingBase.getDistanceToEntity(EntityAINearestAttackableTarget.this.taskOwner) > d) {
                  return false;
               }
            }

            return EntityAINearestAttackableTarget.this.isSuitableTarget(entityLivingBase, false);
         }
      };
   }

   @Override
   public boolean shouldExecute() {
      if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      }

      double d = this.getTargetDistance();
      List list = this.taskOwner
         .worldObj
         .getEntitiesWithinAABB(
            this.targetClass,
            this.taskOwner.getEntityBoundingBox().expand(d, 4.0, d),
            Predicates.and(this.targetEntitySelector, EntitySelectors.NOT_SPECTATING)
         );
      Collections.sort(list, this.theNearestAttackableTargetSorter);
      if (list.isEmpty()) {
         return false;
      }

      this.targetEntity = (EntityLivingBase)list.get(0);
      return true;
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.targetEntity);
      super.startExecuting();
   }

   public static class Sorter implements Comparator<Entity> {
      private final Entity theEntity;

      public Sorter(Entity theEntityIn) {
         this.theEntity = theEntityIn;
      }

      public int compare(Entity entity, Entity entity2) {
         double d = this.theEntity.getDistanceSqToEntity(entity);
         double e = this.theEntity.getDistanceSqToEntity(entity2);
         return d < e ? -1 : (d > e ? 1 : 0);
      }
   }
}
