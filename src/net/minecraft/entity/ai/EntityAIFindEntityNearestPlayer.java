package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAIFindEntityNearestPlayer extends EntityAIBase {
   private static final Logger LOGGER = LogManager.getLogger();
   private EntityLiving entityLiving;
   private final Predicate<Entity> predicate;
   private final EntityAINearestAttackableTarget.Sorter sorter;
   private EntityLivingBase entityTarget;

   public EntityAIFindEntityNearestPlayer(EntityLiving entityLivingIn) {
      this.entityLiving = entityLivingIn;
      if (entityLivingIn instanceof EntityCreature) {
         LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
      }

      this.predicate = new Predicate<Entity>() {
         public boolean apply(Entity entity) {
            if (!(entity instanceof EntityPlayer)) {
               return false;
            }

            if (((EntityPlayer)entity).capabilities.disableDamage) {
               return false;
            }

            double d = EntityAIFindEntityNearestPlayer.this.maxTargetRange();
            if (entity.isSneaking()) {
               d *= 0.8F;
            }

            if (entity.isInvisible()) {
               float f = ((EntityPlayer)entity).getArmorVisibility();
               if (f < 0.1F) {
                  f = 0.1F;
               }

               d *= 0.7F * f;
            }

            return entity.getDistanceToEntity(EntityAIFindEntityNearestPlayer.this.entityLiving) > d
               ? false
               : EntityAITarget.isSuitableTarget(EntityAIFindEntityNearestPlayer.this.entityLiving, (EntityLivingBase)entity, false, true);
         }
      };
      this.sorter = new EntityAINearestAttackableTarget.Sorter(entityLivingIn);
   }

   @Override
   public boolean shouldExecute() {
      double d = this.maxTargetRange();
      List list = this.entityLiving
         .worldObj
         .getEntitiesWithinAABB(EntityPlayer.class, this.entityLiving.getEntityBoundingBox().expand(d, 4.0, d), this.predicate);
      Collections.sort(list, this.sorter);
      if (list.isEmpty()) {
         return false;
      }

      this.entityTarget = (EntityLivingBase)list.get(0);
      return true;
   }

   @Override
   public boolean continueExecuting() {
      EntityLivingBase entityLivingBase = this.entityLiving.getAttackTarget();
      if (entityLivingBase == null) {
         return false;
      }

      if (!entityLivingBase.isEntityAlive()) {
         return false;
      }

      if (entityLivingBase instanceof EntityPlayer && ((EntityPlayer)entityLivingBase).capabilities.disableDamage) {
         return false;
      }

      Team team = this.entityLiving.getTeam();
      Team team2 = entityLivingBase.getTeam();
      if (team != null && team2 == team) {
         return false;
      }

      double d = this.maxTargetRange();
      return this.entityLiving.getDistanceSqToEntity(entityLivingBase) > d * d
         ? false
         : !(entityLivingBase instanceof EntityPlayerMP) || !((EntityPlayerMP)entityLivingBase).theItemInWorldManager.isCreative();
   }

   @Override
   public void startExecuting() {
      this.entityLiving.setAttackTarget(this.entityTarget);
      super.startExecuting();
   }

   @Override
   public void resetTask() {
      this.entityLiving.setAttackTarget((EntityLivingBase)null);
      super.startExecuting();
   }

   public double maxTargetRange() {
      IAttributeInstance iAttributeInstance = this.entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
      return iAttributeInstance == null ? 16.0 : iAttributeInstance.getAttributeValue();
   }
}
