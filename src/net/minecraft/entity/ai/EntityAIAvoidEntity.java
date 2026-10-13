package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Vec3;

public class EntityAIAvoidEntity<T extends Entity> extends EntityAIBase {
   private final Predicate<Entity> canBeSeenSelector = new Predicate<Entity>() {
      private static final long a = 22587339452174L;

      public boolean apply(Entity var1) {
         return var1.isEntityAlive() && EntityAIAvoidEntity.this.theEntity.getEntitySenses().canSee(var1);
      }
   };
   public EntityCreature theEntity;
   private double farSpeed;
   private double nearSpeed;
   public T closestLivingEntity;
   private float avoidDistance;
   private PathEntity entityPathEntity;
   private PathNavigate entityPathNavigate;
   private Class<T> classToAvoid;
   private Predicate<? super T> avoidTargetSelector;
   private static final long c = 43870698849270L;

   public EntityAIAvoidEntity(EntityCreature var1, Class<T> var2, float var3, double var4, double var6) {
      this(var1, var2, Predicates.alwaysTrue(), var3, var4, var6);
   }

   public EntityAIAvoidEntity(EntityCreature var1, Class<T> var2, Predicate<? super T> var3, float var4, double var5, double var7) {
      this.theEntity = var1;
      this.classToAvoid = var2;
      this.avoidTargetSelector = var3;
      this.avoidDistance = var4;
      this.farSpeed = var5;
      this.nearSpeed = var7;
      this.entityPathNavigate = var1.getNavigator();
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      List var4 = this.theEntity
         .worldObj
         .getEntitiesWithinAABB(
            this.classToAvoid,
            this.theEntity.getEntityBoundingBox().expand(this.avoidDistance, 3.0, this.avoidDistance),
            Predicates.and(EntitySelectors.NOT_SPECTATING, this.canBeSeenSelector, this.avoidTargetSelector)
         );
      if (var4.isEmpty()) {
         return false;
      }

      this.closestLivingEntity = (T)var4.get(0);
      Vec3 var5 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(
         this.theEntity, 16, 7, new Vec3(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ)
      );
      if (var5 == null) {
         return false;
      }

      if (this.closestLivingEntity.getDistanceSq(var5.xCoord, var5.yCoord, var5.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(this.theEntity)) {
         return false;
      }

      this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(var5.xCoord, var5.yCoord, var5.zCoord);
      return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(var5);
   }

   @Override
   public boolean continueExecuting() {
      return !this.entityPathNavigate.noPath();
   }

   @Override
   public void startExecuting() {
      this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
   }

   @Override
   public void resetTask() {
      this.closestLivingEntity = null;
   }

   @Override
   public void updateTask() {
      if (this.theEntity.getDistanceSqToEntity(this.closestLivingEntity) < 49.0) {
         this.theEntity.getNavigator().setSpeed(this.nearSpeed);
      } else {
         this.theEntity.getNavigator().setSpeed(this.farSpeed);
      }
   }
}
