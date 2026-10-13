package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityFireball extends Entity {
   private int xTile = -1;
   private int yTile = -1;
   private int zTile = -1;
   private Block inTile;
   private boolean inGround;
   public EntityLivingBase shootingEntity;
   private int ticksAlive;
   private int ticksInAir;
   public double accelerationX;
   public double accelerationY;
   public double accelerationZ;
   private static final long ab = 91722360336515L;

   public EntityFireball(World var1) {
      super(var1);
      this.setSize(1.0F, 1.0F);
   }

   @Override
   public void entityInit() {
   }

   @Override
   public boolean isInRangeToRenderDist(double var1) {
      double var6 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
      if (Double.isNaN(var6)) {
         var6 = 4.0;
      }

      var6 *= 64.0;
      return var1 < var6 * var6;
   }

   public EntityFireball(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1);
      this.setSize(1.0F, 1.0F);
      this.setLocationAndAngles(var2, var4, var6, this.rotationYaw, this.rotationPitch);
      this.setPosition(var2, var4, var6);
      double var14 = MathHelper.sqrt_double(var8 * var8 + var10 * var10 + var12 * var12);
      this.accelerationX = var8 / var14 * 0.1;
      this.accelerationY = var10 / var14 * 0.1;
      this.accelerationZ = var12 / var14 * 0.1;
   }

   public EntityFireball(World var1, EntityLivingBase var2, double var3, double var5, double var7) {
      super(var1);
      this.shootingEntity = var2;
      this.setSize(1.0F, 1.0F);
      this.setLocationAndAngles(var2.posX, var2.posY, var2.posZ, var2.rotationYaw, var2.rotationPitch);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionX = this.motionY = this.motionZ = 0.0;
      var3 += this.rand.nextGaussian() * 0.4;
      var5 += this.rand.nextGaussian() * 0.4;
      var7 += this.rand.nextGaussian() * 0.4;
      double var9 = MathHelper.sqrt_double(var3 * var3 + var5 * var5 + var7 * var7);
      this.accelerationX = var3 / var9 * 0.1;
      this.accelerationY = var5 / var9 * 0.1;
      this.accelerationZ = var7 / var9 * 0.1;
   }

   @Override
   public void onUpdate() {
      if (this.worldObj.isRemote || (this.shootingEntity == null || !this.shootingEntity.isDead) && this.worldObj.isBlockLoaded(new BlockPos(this))) {
         super.onUpdate();
         this.setFire(1);
         if (this.inGround) {
            if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
               this.ticksAlive++;
               if (this.ticksAlive == 600) {
                  this.setDead();
               }

               return;
            }

            this.inGround = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.ticksAlive = 0;
            this.ticksInAir = 0;
         } else {
            this.ticksInAir++;
         }

         Vec3 var4 = new Vec3(this.posX, this.posY, this.posZ);
         Vec3 var5 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         MovingObjectPosition var6 = this.worldObj.rayTraceBlocks(var4, var5);
         var4 = new Vec3(this.posX, this.posY, this.posZ);
         var5 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         if (var6 != null) {
            var5 = new Vec3(var6.hitVec.xCoord, var6.hitVec.yCoord, var6.hitVec.zCoord);
         }

         Entity var7 = null;
         List var8 = this.worldObj
            .getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
         double var9 = 0.0;

         for (int var11 = 0; var11 < var8.size(); var11++) {
            Entity var12 = (Entity)var8.get(var11);
            if (var12.canBeCollidedWith() && (!var12.isEntityEqual(this.shootingEntity) || this.ticksInAir >= 25)) {
               float var13 = 0.3F;
               AxisAlignedBB var14 = var12.getEntityBoundingBox().expand(var13, var13, var13);
               MovingObjectPosition var15 = var14.calculateIntercept(var4, var5);
               if (var15 != null) {
                  double var16 = var4.squareDistanceTo(var15.hitVec);
                  if (var16 < var9 || var9 == 0.0) {
                     var7 = var12;
                     var9 = var16;
                  }
               }
            }
         }

         if (var7 != null) {
            var6 = new MovingObjectPosition(var7);
         }

         if (var6 != null) {
            this.onImpact(var6);
         }

         this.posX = this.posX + this.motionX;
         this.posY = this.posY + this.motionY;
         this.posZ = this.posZ + this.motionZ;
         float var20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(MathHelper.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) + 90.0F;
         this.rotationPitch = (float)(MathHelper.atan2(var20, this.motionY) * 180.0 / Math.PI) - 90.0F;

         while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
            this.prevRotationPitch -= 360.0F;
         }

         while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
         }

         while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
         }

         while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
         }

         this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
         this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
         float var21 = this.getMotionFactor();
         if (this.isInWater()) {
            for (int var22 = 0; var22 < 4; var22++) {
               float var23 = 0.25F;
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.WATER_BUBBLE,
                     this.posX - this.motionX * var23,
                     this.posY - this.motionY * var23,
                     this.posZ - this.motionZ * var23,
                     this.motionX,
                     this.motionY,
                     this.motionZ
                  );
            }

            var21 = 0.8F;
         }

         this.motionX = this.motionX + this.accelerationX;
         this.motionY = this.motionY + this.accelerationY;
         this.motionZ = this.motionZ + this.accelerationZ;
         this.motionX *= var21;
         this.motionY *= var21;
         this.motionZ *= var21;
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
         this.setPosition(this.posX, this.posY, this.posZ);
      } else {
         this.setDead();
      }
   }

   public float getMotionFactor() {
      return 0.95F;
   }

   public abstract void onImpact(MovingObjectPosition var1);

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      var1.setShort("xTile", (short)this.xTile);
      var1.setShort("yTile", (short)this.yTile);
      var1.setShort("zTile", (short)this.zTile);
      ResourceLocation var5 = Block.blockRegistry.getNameForObject(this.inTile);
      var1.setString("inTile", var5 == null ? "" : var5.toString());
      var1.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      var1.setTag("direction", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      this.xTile = var1.getShort("xTile");
      this.yTile = var1.getShort("yTile");
      this.zTile = var1.getShort("zTile");
      if (var1.hasKey("inTile", 8)) {
         this.inTile = Block.getBlockFromName(var1.getString("inTile"));
      } else {
         this.inTile = Block.getBlockById(var1.getByte("inTile") & 255);
      }

      this.inGround = var1.getByte("inGround") == 1;
      if (var1.hasKey("direction", 9)) {
         NBTTagList var5 = var1.getTagList("direction", 6);
         this.motionX = var5.getDoubleAt(0);
         this.motionY = var5.getDoubleAt(1);
         this.motionZ = var5.getDoubleAt(2);
      } else {
         this.setDead();
      }
   }

   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   public float getCollisionBorderSize() {
      return 1.0F;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      this.setBeenAttacked();
      if (var1.getEntity() != null) {
         Vec3 var6 = var1.getEntity().getLookVec();
         if (var6 != null) {
            this.motionX = var6.xCoord;
            this.motionY = var6.yCoord;
            this.motionZ = var6.zCoord;
            this.accelerationX = this.motionX * 0.1;
            this.accelerationY = this.motionY * 0.1;
            this.accelerationZ = this.motionZ * 0.1;
         }

         if (var1.getEntity() instanceof EntityLivingBase) {
            this.shootingEntity = (EntityLivingBase)var1.getEntity();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public float getBrightness(float var1) {
      return 1.0F;
   }

   @Override
   public int getBrightnessForRender(float var1) {
      return 15728880;
   }
}
