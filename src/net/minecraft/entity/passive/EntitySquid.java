package net.minecraft.entity.passive;

import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySquid extends EntityWaterMob {
   public float squidPitch;
   public float prevSquidPitch;
   public float squidYaw;
   public float prevSquidYaw;
   public float squidRotation;
   public float prevSquidRotation;
   public float tentacleAngle;
   public float lastTentacleAngle;
   private float randomMotionSpeed;
   private float rotationVelocity;
   private float field_70871_bB;
   private float randomMotionVecX;
   private float randomMotionVecY;
   private float randomMotionVecZ;
   private static final long ab = 90053086944477L;

   public EntitySquid(World var1) {
      super(var1);
      this.setSize(0.95F, 0.95F);
      this.rand.setSeed(1 + this.getEntityId());
      this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
      this.tasks.addTask(0, new EntitySquid.AIMoveRandom(this));
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
   }

   @Override
   public float getEyeHeight() {
      return this.height * 0.5F;
   }

   @Override
   public String getLivingSound() {
      return null;
   }

   @Override
   public String getHurtSound() {
      return null;
   }

   @Override
   public String getDeathSound() {
      return null;
   }

   @Override
   public float getSoundVolume() {
      return 0.4F;
   }

   @Override
   public Item getDropItem() {
      return null;
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
      int var6 = this.rand.nextInt(3 + var2) + 1;

      for (int var7 = 0; var7 < var6; var7++) {
         this.entityDropItem(new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()), 0.0F);
      }
   }

   @Override
   public boolean isInWater() {
      return this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox().expand(0.0, -0.6F, 0.0), Material.water, this);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.prevSquidPitch = this.squidPitch;
      this.prevSquidYaw = this.squidYaw;
      this.prevSquidRotation = this.squidRotation;
      this.lastTentacleAngle = this.tentacleAngle;
      this.squidRotation = this.squidRotation + this.rotationVelocity;
      if (this.squidRotation > Math.PI * 2) {
         if (this.worldObj.isRemote) {
            this.squidRotation = (float) (Math.PI * 2);
         } else {
            this.squidRotation = (float)(this.squidRotation - (Math.PI * 2));
            if (this.rand.nextInt(10) == 0) {
               this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }

            this.worldObj.setEntityState(this, (byte)19);
         }
      }

      if (this.inWater) {
         if (this.squidRotation < (float) Math.PI) {
            float var4 = this.squidRotation / (float) Math.PI;
            this.tentacleAngle = MathHelper.sin(var4 * var4 * (float) Math.PI) * (float) Math.PI * 0.25F;
            if (var4 > 0.75) {
               this.randomMotionSpeed = 1.0F;
               this.field_70871_bB = 1.0F;
            } else {
               this.field_70871_bB *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.randomMotionSpeed *= 0.9F;
            this.field_70871_bB *= 0.99F;
         }

         if (!this.worldObj.isRemote) {
            this.motionX = this.randomMotionVecX * this.randomMotionSpeed;
            this.motionY = this.randomMotionVecY * this.randomMotionSpeed;
            this.motionZ = this.randomMotionVecZ * this.randomMotionSpeed;
         }

         float var5 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.renderYawOffset = this.renderYawOffset
            + (-((float)MathHelper.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI - this.renderYawOffset) * 0.1F;
         this.rotationYaw = this.renderYawOffset;
         this.squidYaw = (float)(this.squidYaw + Math.PI * this.field_70871_bB * 1.5);
         this.squidPitch = this.squidPitch + (-((float)MathHelper.atan2(var5, this.motionY)) * 180.0F / (float) Math.PI - this.squidPitch) * 0.1F;
      } else {
         this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * (float) Math.PI * 0.25F;
         if (!this.worldObj.isRemote) {
            this.motionX = 0.0;
            this.motionY -= 0.08;
            this.motionY *= 0.98F;
            this.motionZ = 0.0;
         }

         this.squidPitch = (float)(this.squidPitch + (-90.0F - this.squidPitch) * 0.02);
      }
   }

   @Override
   public void moveEntityWithHeading(float var1, float var2) {
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.posY > 45.0 && this.posY < this.worldObj.getSeaLevel() && super.getCanSpawnHere();
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 19) {
         this.squidRotation = 0.0F;
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   public void func_175568_b(float var1, float var2, float var3) {
      this.randomMotionVecX = var1;
      this.randomMotionVecY = var2;
      this.randomMotionVecZ = var3;
   }

   public boolean func_175567_n() {
      return this.randomMotionVecX != 0.0F || this.randomMotionVecY != 0.0F || this.randomMotionVecZ != 0.0F;
   }

   public static class AIMoveRandom extends EntityAIBase {
      private EntitySquid squid;
      private static final long a = 85272192811135L;

      public AIMoveRandom(EntitySquid var1) {
         this.squid = var1;
      }

      @Override
      public boolean shouldExecute() {
         return true;
      }

      @Override
      public void updateTask() {
         int var4 = this.squid.s();
         if (var4 > 100) {
            this.squid.func_175568_b(0.0F, 0.0F, 0.0F);
         } else if (this.squid.getRNG().nextInt(50) == 0 || !this.squid.inWater || !this.squid.func_175567_n()) {
            float var5 = this.squid.getRNG().nextFloat() * (float) Math.PI * 2.0F;
            float var6 = MathHelper.cos(var5) * 0.2F;
            float var7 = -0.1F + this.squid.getRNG().nextFloat() * 0.2F;
            float var8 = MathHelper.sin(var5) * 0.2F;
            this.squid.func_175568_b(var6, var7, var8);
         }
      }
   }
}
