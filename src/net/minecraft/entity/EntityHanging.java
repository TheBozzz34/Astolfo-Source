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

   public EntityHanging(World worldIn) {
      super(worldIn);
      this.setSize(0.5F, 0.5F);
   }

   public EntityHanging(World worldIn, BlockPos hangingPositionIn) {
      this(worldIn);
      this.hangingPosition = hangingPositionIn;
   }

   @Override
   public void entityInit() {
   }

   public void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
      Validate.notNull(facingDirectionIn);
      Validate.isTrue(facingDirectionIn.getAxis().isHorizontal());
      this.facingDirection = facingDirectionIn;
      this.prevRotationYaw = this.rotationYaw = this.facingDirection.getHorizontalIndex() * 90;
      this.updateBoundingBox();
   }

   private void updateBoundingBox() {
      if (this.facingDirection != null) {
         double d = this.hangingPosition.getX() + 0.5;
         double e = this.hangingPosition.getY() + 0.5;
         double d2 = this.hangingPosition.getZ() + 0.5;
         double d3 = this.func_174858_a(this.getWidthPixels());
         double d4 = this.func_174858_a(this.getHeightPixels());
         d -= this.facingDirection.getFrontOffsetX() * 0.46875;
         d2 -= this.facingDirection.getFrontOffsetZ() * 0.46875;
         e += d4;
         EnumFacing enumFacing = this.facingDirection.rotateYCCW();
         d += d3 * enumFacing.getFrontOffsetX();
         d2 += d3 * enumFacing.getFrontOffsetZ();
         this.posX = d;
         this.posY = e;
         this.posZ = d2;
         double d5 = this.getWidthPixels();
         double d6 = this.getHeightPixels();
         double d7 = this.getWidthPixels();
         if (this.facingDirection.getAxis() == EnumFacing.Axis.Z) {
            d7 = 1.0;
         } else {
            d5 = 1.0;
         }

         d5 /= 32.0;
         d6 /= 32.0;
         d7 /= 32.0;
         this.setEntityBoundingBox(new AxisAlignedBB(d - d5, e - d6, d2 - d7, d + d5, e + d6, d2 + d7));
      }
   }

   private double func_174858_a(int i) {
      return i % 32 == 0 ? 0.5 : 0.0;
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

      int i = Math.max(1, this.getWidthPixels() / 16);
      int j = Math.max(1, this.getHeightPixels() / 16);
      BlockPos blockPos = this.hangingPosition.offset(this.facingDirection.getOpposite());
      EnumFacing enumFacing = this.facingDirection.rotateYCCW();

      for (int k = 0; k < i; k++) {
         for (int l = 0; l < j; l++) {
            BlockPos blockPos2 = blockPos.offset(enumFacing, k).up(l);
            Block block = this.worldObj.getBlockState(blockPos2).getBlock();
            if (!block.getMaterial().isSolid() && !BlockRedstoneDiode.isRedstoneRepeaterBlockID(block)) {
               return false;
            }
         }
      }

      for (Entity entity : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox())) {
         if (entity instanceof EntityHanging) {
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
   public boolean hitByEntity(Entity entityIn) {
      return entityIn instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityIn), 0.0F) : false;
   }

   @Override
   public EnumFacing getHorizontalFacing() {
      return this.facingDirection;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (!this.isDead && !this.worldObj.isRemote) {
         this.setDead();
         this.setBeenAttacked();
         this.onBroken(source.getEntity());
      }

      return true;
   }

   @Override
   public void moveEntity(double x, double y, double z) {
      if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0) {
         this.setDead();
         this.onBroken((Entity)null);
      }
   }

   @Override
   public void addVelocity(double x, double y, double z) {
      if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0) {
         this.setDead();
         this.onBroken((Entity)null);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setByte("Facing", (byte)this.facingDirection.getHorizontalIndex());
      tagCompound.setInteger("TileX", this.getHangingPosition().getX());
      tagCompound.setInteger("TileY", this.getHangingPosition().getY());
      tagCompound.setInteger("TileZ", this.getHangingPosition().getZ());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.hangingPosition = new BlockPos(tagCompund.getInteger("TileX"), tagCompund.getInteger("TileY"), tagCompund.getInteger("TileZ"));
      EnumFacing enumFacing;
      if (tagCompund.hasKey("Direction", 99)) {
         enumFacing = EnumFacing.getHorizontal(tagCompund.getByte("Direction"));
         this.hangingPosition = this.hangingPosition.offset(enumFacing);
      } else if (tagCompund.hasKey("Facing", 99)) {
         enumFacing = EnumFacing.getHorizontal(tagCompund.getByte("Facing"));
      } else {
         enumFacing = EnumFacing.getHorizontal(tagCompund.getByte("Dir"));
      }

      this.updateFacingWithBoundingBox(enumFacing);
   }

   public abstract int getWidthPixels();

   public abstract int getHeightPixels();

   public abstract void onBroken(Entity brokenEntity);

   @Override
   public boolean shouldSetPosAfterLoading() {
      return false;
   }

   @Override
   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      BlockPos blockPos = this.hangingPosition;
      this.hangingPosition = new BlockPos(x, y, z);
      if (!this.hangingPosition.equals(blockPos)) {
         this.updateBoundingBox();
         this.isAirBorne = true;
      }
   }

   public BlockPos getHangingPosition() {
      return this.hangingPosition;
   }
}
