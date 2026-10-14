package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowOwner extends EntityAIBase {
   private EntityTameable thePet;
   private EntityLivingBase theOwner;
   public World theWorld;
   private double followSpeed;
   private PathNavigate petPathfinder;
   private int field_75343_h;
   public float maxDist;
   public float minDist;
   private boolean field_75344_i;

   public EntityAIFollowOwner(EntityTameable thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
      this.thePet = thePetIn;
      this.theWorld = thePetIn.worldObj;
      this.followSpeed = followSpeedIn;
      this.petPathfinder = thePetIn.getNavigator();
      this.minDist = minDistIn;
      this.maxDist = maxDistIn;
      this.setMutexBits(3);
      if (!(thePetIn.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase entityLivingBase = this.thePet.getOwner();
      if (entityLivingBase == null) {
         return false;
      }

      if (entityLivingBase instanceof EntityPlayer && ((EntityPlayer)entityLivingBase).isSpectator()) {
         return false;
      }

      if (this.thePet.isSitting()) {
         return false;
      }

      if (this.thePet.getDistanceSqToEntity(entityLivingBase) < this.minDist * this.minDist) {
         return false;
      }

      this.theOwner = entityLivingBase;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > this.maxDist * this.maxDist && !this.thePet.isSitting();
   }

   @Override
   public void startExecuting() {
      this.field_75343_h = 0;
      this.field_75344_i = ((PathNavigateGround)this.thePet.getNavigator()).S();
      ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(false);
   }

   @Override
   public void resetTask() {
      this.theOwner = null;
      this.petPathfinder.clearPathEntity();
      ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(true);
   }

   private boolean func_181065_a(BlockPos blockPos) {
      IBlockState iBlockState = this.theWorld.getBlockState(blockPos);
      Block block = iBlockState.getBlock();
      return block == Blocks.air ? true : !block.isFullCube();
   }

   @Override
   public void updateTask() {
      this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, this.thePet.getVerticalFaceSpeed());
      if (!this.thePet.isSitting() && --this.field_75343_h <= 0) {
         this.field_75343_h = 10;
         if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed)
            && !this.thePet.getLeashed()
            && this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0) {
            int i = MathHelper.floor_double(this.theOwner.posX) - 2;
            int j = MathHelper.floor_double(this.theOwner.posZ) - 2;
            int k = MathHelper.floor_double(this.theOwner.getEntityBoundingBox().minY);

            for (int l = 0; l <= 4; l++) {
               for (int m = 0; m <= 4; m++) {
                  if ((l < 1 || m < 1 || l > 3 || m > 3)
                     && World.doesBlockHaveSolidTopSurface(this.theWorld, new BlockPos(i + l, k - 1, j + m))
                     && this.func_181065_a(new BlockPos(i + l, k, j + m))
                     && this.func_181065_a(new BlockPos(i + l, k + 1, j + m))) {
                     this.thePet.setLocationAndAngles(i + l + 0.5F, k, j + m + 0.5F, this.thePet.rotationYaw, this.thePet.rotationPitch);
                     this.petPathfinder.clearPathEntity();
                     return;
                  }
               }
            }
         }
      }
   }
}
