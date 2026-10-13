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
   private static final long a = 90236977786692L;
   private static final long c = 1162328002676129802L;

   public EntityAIFollowOwner(EntityTameable var1, double var2, float var4, float var5) {
      this.thePet = var1;
      this.theWorld = var1.worldObj;
      this.followSpeed = var2;
      this.petPathfinder = var1.getNavigator();
      this.minDist = var4;
      this.maxDist = var5;
      this.setMutexBits(3);
      if (!(var1.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase var4 = this.thePet.getOwner();
      if (var4 == null) {
         return false;
      }

      if (var4 instanceof EntityPlayer && ((EntityPlayer)var4).isSpectator()) {
         return false;
      }

      if (this.thePet.isSitting()) {
         return false;
      }

      if (this.thePet.getDistanceSqToEntity(var4) < this.minDist * this.minDist) {
         return false;
      }

      this.theOwner = var4;
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

   private boolean func_181065_a(BlockPos var1) {
      IBlockState var5 = this.theWorld.getBlockState(var1);
      Block var6 = var5.getBlock();
      return var6 == Blocks.air ? true : !var6.isFullCube();
   }

   @Override
   public void updateTask() {
      this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, this.thePet.getVerticalFaceSpeed());
      if (!this.thePet.isSitting() && --this.field_75343_h <= 0) {
         this.field_75343_h = (int)c;
         if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed)
            && !this.thePet.getLeashed()
            && this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0) {
            int var4 = MathHelper.floor_double(this.theOwner.posX) - 2;
            int var5 = MathHelper.floor_double(this.theOwner.posZ) - 2;
            int var6 = MathHelper.floor_double(this.theOwner.getEntityBoundingBox().minY);

            for (int var7 = 0; var7 <= 4; var7++) {
               for (int var8 = 0; var8 <= 4; var8++) {
                  if ((var7 < 1 || var8 < 1 || var7 > 3 || var8 > 3)
                     && World.doesBlockHaveSolidTopSurface(this.theWorld, new BlockPos(var4 + var7, var6 - 1, var5 + var8))
                     && this.func_181065_a(new BlockPos(var4 + var7, var6, var5 + var8))
                     && this.func_181065_a(new BlockPos(var4 + var7, var6 + 1, var5 + var8))) {
                     this.thePet.setLocationAndAngles(var4 + var7 + 0.5F, var6, var5 + var8 + 0.5F, this.thePet.rotationYaw, this.thePet.rotationPitch);
                     this.petPathfinder.clearPathEntity();
                     return;
                  }
               }
            }
         }
      }
   }
}
