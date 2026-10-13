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
   private static final long a = 66396940123355L;
   private static final long j = 5243105258149773322L;

   public EntityAINearestAttackableTarget(EntityCreature var1, Class<T> var2, boolean var3) {
      this(var1, var2, var3, false);
   }

   public EntityAINearestAttackableTarget(EntityCreature var1, Class<T> var2, boolean var3, boolean var4) {
      this(var1, var2, (int)j, var3, var4, (Predicate<? super T>)null);
   }

   public EntityAINearestAttackableTarget(EntityCreature var1, Class<T> var2, int var3, boolean var4, boolean var5, final Predicate<? super T> var6) {
      super(var1, var4, var5);
      this.targetClass = var2;
      this.targetChance = var3;
      this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(var1);
      this.setMutexBits(1);
      this.targetEntitySelector = new Predicate<T>() {
         private static final long a = 8051006728681L;

         public boolean apply(T var1) {
            if (var6 != null && !var6.apply(var1)) {
               return false;
            }

            if (var1 instanceof EntityPlayer) {
               double var5x = EntityAINearestAttackableTarget.this.getTargetDistance();
               if (var1.isSneaking()) {
                  var5x *= 0.8F;
               }

               if (var1.isInvisible()) {
                  float var7 = ((EntityPlayer)var1).getArmorVisibility();
                  if (var7 < 0.1F) {
                     var7 = 0.1F;
                  }

                  var5x *= 0.7F * var7;
               }

               if (var1.getDistanceToEntity(EntityAINearestAttackableTarget.this.taskOwner) > var5x) {
                  return false;
               }
            }

            return EntityAINearestAttackableTarget.this.isSuitableTarget(var1, false);
         }
      };
   }

   @Override
   public boolean shouldExecute() {
      if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      }

      double var4 = this.getTargetDistance();
      List var6 = this.taskOwner
         .worldObj
         .getEntitiesWithinAABB(
            this.targetClass,
            this.taskOwner.getEntityBoundingBox().expand(var4, 4.0, var4),
            Predicates.and(this.targetEntitySelector, EntitySelectors.NOT_SPECTATING)
         );
      Collections.sort(var6, this.theNearestAttackableTargetSorter);
      if (var6.isEmpty()) {
         return false;
      }

      this.targetEntity = (EntityLivingBase)var6.get(0);
      return true;
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.targetEntity);
      super.startExecuting();
   }

   public static class Sorter implements Comparator<Entity> {
      private final Entity theEntity;
      private static final long a = 76493256523678L;

      public Sorter(Entity var1) {
         this.theEntity = var1;
      }

      public int compare(Entity var1, Entity var2) {
         double var6 = this.theEntity.getDistanceSqToEntity(var1);
         double var8 = this.theEntity.getDistanceSqToEntity(var2);
         return var6 < var8 ? -1 : (var6 > var8 ? 1 : 0);
      }
   }
}
