package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class EntityHanging extends Entity {
   private int tickCounter1;
   public BlockPos hangingPosition;
   public EnumFacing facingDirection;
   private static final long ab = 127655886648514L;

   public EntityHanging(World var1) {
      super(var1);
      this.setSize(0.5F, 0.5F);
   }

   public EntityHanging(World var1, BlockPos var2) {
      this(var1);
      this.hangingPosition = var2;
   }

   @Override
   public void entityInit() {
   }

   public void updateFacingWithBoundingBox(EnumFacing var1) {
      Validate.notNull(var1);
      Validate.isTrue(var1.getAxis().isHorizontal());
      this.facingDirection = var1;
      this.prevRotationYaw = this.rotationYaw = this.facingDirection.getHorizontalIndex() * 90;
      this.updateBoundingBox();
   }

   private void updateBoundingBox() {
      if (this.facingDirection != null) {
         double var4 = this.hangingPosition.getX() + 0.5;
         double var6 = this.hangingPosition.getY() + 0.5;
         double var8 = this.hangingPosition.getZ() + 0.5;
         double var12 = this.func_174858_a(this.getWidthPixels());
         double var14 = this.func_174858_a(this.getHeightPixels());
         var4 -= this.facingDirection.getFrontOffsetX() * 0.46875;
         var8 -= this.facingDirection.getFrontOffsetZ() * 0.46875;
         var6 += var14;
         EnumFacing var16 = this.facingDirection.rotateYCCW();
         var4 += var12 * var16.getFrontOffsetX();
         var8 += var12 * var16.getFrontOffsetZ();
         this.posX = var4;
         this.posY = var6;
         this.posZ = var8;
         double var17 = this.getWidthPixels();
         double var19 = this.getHeightPixels();
         double var21 = this.getWidthPixels();
         if (this.facingDirection.getAxis() == EnumFacing.Axis.Z) {
            var21 = 1.0;
         } else {
            var17 = 1.0;
         }

         var17 /= 32.0;
         var19 /= 32.0;
         var21 /= 32.0;
         this.setEntityBoundingBox(new AxisAlignedBB(var4 - var17, var6 - var19, var8 - var21, var4 + var17, var6 + var19, var8 + var21));
      }
   }

   private double func_174858_a(int var1) {
      return var1 % 32 == 0 ? 0.5 : 0.0;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.tickCounter1++ == 100 && !this.worldObj.isRemote) {
         this.tickCounter1 = 0;
         if (!this.isDead && !this.onValidSurface()) {
            this.setDead();
            this.onBroken((Entity)null);
         }
      }
   }

   public boolean onValidSurface() {
      if (!this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
         return false;
      }

      int var4 = Math.max(1, this.getWidthPixels() / 16);
      int var5 = Math.max(1, this.getHeightPixels() / 16);
      BlockPos var6 = this.hangingPosition.offset(this.facingDirection.getOpposite());
      EnumFacing var7 = this.facingDirection.rotateYCCW();

      for (int var8 = 0; var8 < var4; var8++) {
         for (int var9 = 0; var9 < var5; var9++) {
            BlockPos var10 = var6.offset(var7, var8).up(var9);
            Block var11 = this.worldObj.getBlockState(var10).getBlock();
            if (!var11.getMaterial().isSolid() && !BlockRedstoneDiode.isRedstoneRepeaterBlockID(var11)) {
               return false;
            }
         }
      }

      for (Entity var13 : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox())) {
         if (var13 instanceof EntityHanging) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   public boolean hitByEntity(Entity var1) {
      return var1 instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)var1), 0.0F) : false;
   }

   @Override
   public EnumFacing getHorizontalFacing() {
      return this.facingDirection;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      if (!this.isDead && !this.worldObj.isRemote) {
         this.setDead();
         this.setBeenAttacked();
         this.onBroken(var1.getEntity());
      }

      return true;
   }

   @Override
   public void moveEntity(double var1, double var3, double var5) {
      if (!this.worldObj.isRemote && !this.isDead && var1 * var1 + var3 * var3 + var5 * var5 > 0.0) {
         this.setDead();
         this.onBroken((Entity)null);
      }
   }

   @Override
   public void addVelocity(double var1, double var3, double var5) {
      if (!this.worldObj.isRemote && !this.isDead && var1 * var1 + var3 * var3 + var5 * var5 > 0.0) {
         this.setDead();
         this.onBroken((Entity)null);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      var1.setByte("Facing", (byte)this.facingDirection.getHorizontalIndex());
      var1.setInteger("TileX", this.getHangingPosition().getX());
      var1.setInteger("TileY", this.getHangingPosition().getY());
      var1.setInteger("TileZ", this.getHangingPosition().getZ());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      this.hangingPosition = new BlockPos(var1.getInteger("TileX"), var1.getInteger("TileY"), var1.getInteger("TileZ"));
      EnumFacing var5;
      if (var1.hasKey("Direction", 99)) {
         var5 = EnumFacing.getHorizontal(var1.getByte("Direction"));
         this.hangingPosition = this.hangingPosition.offset(var5);
      } else if (var1.hasKey("Facing", 99)) {
         var5 = EnumFacing.getHorizontal(var1.getByte("Facing"));
      } else {
         var5 = EnumFacing.getHorizontal(var1.getByte("Dir"));
      }

      this.updateFacingWithBoundingBox(var5);
   }

   public abstract int getWidthPixels();

   public abstract int getHeightPixels();

   public abstract void onBroken(Entity var1);

   @Override
   public boolean shouldSetPosAfterLoading() {
      return false;
   }

   @Override
   public void setPosition(double var1, double var3, double var5) {
      this.posX = var1;
      this.posY = var3;
      this.posZ = var5;
      BlockPos var10 = this.hangingPosition;
      this.hangingPosition = new BlockPos(var1, var3, var5);
      if (!this.hangingPosition.equals(var10)) {
         this.updateBoundingBox();
         this.isAirBorne = true;
      }
   }

   public BlockPos getHangingPosition() {
      return this.hangingPosition;
   }
}
