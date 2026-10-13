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
   private static final long a = 111095944434681L;

   public EntityAIFindEntityNearestPlayer(EntityLiving var1) {
      this.entityLiving = var1;
      if (var1 instanceof EntityCreature) {
         LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
      }

      this.predicate = new Predicate<Entity>() {
         private static final long a = 135507398792546L;

         public boolean apply(Entity var1) {
            if (!(var1 instanceof EntityPlayer)) {
               return false;
            }

            if (((EntityPlayer)var1).capabilities.disableDamage) {
               return false;
            }

            double var5 = EntityAIFindEntityNearestPlayer.this.maxTargetRange();
            if (var1.isSneaking()) {
               var5 *= 0.8F;
            }

            if (var1.isInvisible()) {
               float var7 = ((EntityPlayer)var1).getArmorVisibility();
               if (var7 < 0.1F) {
                  var7 = 0.1F;
               }

               var5 *= 0.7F * var7;
            }

            return var1.getDistanceToEntity(EntityAIFindEntityNearestPlayer.this.entityLiving) > var5
               ? false
               : EntityAITarget.isSuitableTarget(EntityAIFindEntityNearestPlayer.this.entityLiving, (EntityLivingBase)var1, false, true);
         }
      };
      this.sorter = new EntityAINearestAttackableTarget.Sorter(var1);
   }

   @Override
   public boolean shouldExecute() {
      double var4 = this.maxTargetRange();
      List var6 = this.entityLiving
         .worldObj
         .getEntitiesWithinAABB(EntityPlayer.class, this.entityLiving.getEntityBoundingBox().expand(var4, 4.0, var4), this.predicate);
      Collections.sort(var6, this.sorter);
      if (var6.isEmpty()) {
         return false;
      }

      this.entityTarget = (EntityLivingBase)var6.get(0);
      return true;
   }

   @Override
   public boolean continueExecuting() {
      EntityLivingBase var4 = this.entityLiving.getAttackTarget();
      if (var4 == null) {
         return false;
      }

      if (!var4.isEntityAlive()) {
         return false;
      }

      if (var4 instanceof EntityPlayer && ((EntityPlayer)var4).capabilities.disableDamage) {
         return false;
      }

      Team var5 = this.entityLiving.getTeam();
      Team var6 = var4.getTeam();
      if (var5 != null && var6 == var5) {
         return false;
      }

      double var7 = this.maxTargetRange();
      return this.entityLiving.getDistanceSqToEntity(var4) > var7 * var7
         ? false
         : !(var4 instanceof EntityPlayerMP) || !((EntityPlayerMP)var4).theItemInWorldManager.isCreative();
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
      IAttributeInstance var4 = this.entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
      return var4 == null ? 16.0 : var4.getAttributeValue();
   }
}
