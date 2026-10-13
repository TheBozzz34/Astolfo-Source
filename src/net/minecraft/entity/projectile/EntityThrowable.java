package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityThrowable extends Entity implements IProjectile {
   private int xTile = -1;
   private int yTile = -1;
   private int zTile = -1;
   private Block inTile;
   public boolean inGround;
   public int throwableShake;
   private EntityLivingBase thrower;
   private String throwerName;
   private int ticksInGround;
   private int ticksInAir;
   private static final long bb = 5560911139055L;

   public EntityThrowable(World var1) {
      super(var1);
      this.setSize(0.25F, 0.25F);
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

   public EntityThrowable(World var1, EntityLivingBase var2) {
      super(var1);
      this.thrower = var2;
      this.setSize(0.25F, 0.25F);
      this.setLocationAndAngles(var2.posX, var2.posY + var2.getEyeHeight(), var2.posZ, var2.rotationYaw, var2.rotationPitch);
      this.posX = this.posX - MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.posY -= 0.1F;
      this.posZ = this.posZ - MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.setPosition(this.posX, this.posY, this.posZ);
      float var3 = 0.4F;
      this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * var3;
      this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * var3;
      this.motionY = -MathHelper.sin((this.rotationPitch + this.getInaccuracy()) / 180.0F * (float) Math.PI) * var3;
      this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, this.getVelocity(), 1.0F);
   }

   public EntityThrowable(World var1, double var2, double var4, double var6) {
      super(var1);
      this.ticksInGround = 0;
      this.setSize(0.25F, 0.25F);
      this.setPosition(var2, var4, var6);
   }

   public float getVelocity() {
      return 1.5F;
   }

   public float getInaccuracy() {
      return 0.0F;
   }

   @Override
   public void setThrowableHeading(double var1, double var3, double var5, float var7, float var8) {
      float var9 = MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5);
      var1 /= var9;
      var3 /= var9;
      var5 /= var9;
      var1 += this.rand.nextGaussian() * 0.0075F * var8;
      var3 += this.rand.nextGaussian() * 0.0075F * var8;
      var5 += this.rand.nextGaussian() * 0.0075F * var8;
      var1 *= var7;
      var3 *= var7;
      var5 *= var7;
      this.motionX = var1;
      this.motionY = var3;
      this.motionZ = var5;
      float var10 = MathHelper.sqrt_double(var1 * var1 + var5 * var5);
      this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(var1, var5) * 180.0 / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(var3, var10) * 180.0 / Math.PI);
      this.ticksInGround = 0;
   }

   @Override
   public void setVelocity(double var1, double var3, double var5) {
      this.motionX = var1;
      this.motionY = var3;
      this.motionZ = var5;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float var10 = MathHelper.sqrt_double(var1 * var1 + var5 * var5);
         this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(var1, var5) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(var3, var10) * 180.0 / Math.PI);
      }
   }

   @Override
   public void onUpdate() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      super.onUpdate();
      if (this.throwableShake > 0) {
         this.throwableShake--;
      }

      if (this.inGround) {
         if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
            this.ticksInGround++;
            if (this.ticksInGround == 1200) {
               this.setDead();
            }

            return;
         }

         this.inGround = false;
         this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
         this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
         this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
         this.ticksInGround = 0;
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

      if (!this.worldObj.isRemote) {
         Entity var7 = null;
         List var8 = this.worldObj
            .getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
         double var9 = 0.0;
         EntityLivingBase var11 = this.getThrower();

         for (int var12 = 0; var12 < var8.size(); var12++) {
            Entity var13 = (Entity)var8.get(var12);
            if (var13.canBeCollidedWith() && (var13 != var11 || this.ticksInAir >= 5)) {
               float var14 = 0.3F;
               AxisAlignedBB var15 = var13.getEntityBoundingBox().expand(var14, var14, var14);
               MovingObjectPosition var16 = var15.calculateIntercept(var4, var5);
               if (var16 != null) {
                  double var17 = var4.squareDistanceTo(var16.hitVec);
                  if (var17 < var9 || var9 == 0.0) {
                     var7 = var13;
                     var9 = var17;
                  }
               }
            }
         }

         if (var7 != null) {
            var6 = new MovingObjectPosition(var7);
         }
      }

      if (var6 != null) {
         if (var6.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.worldObj.getBlockState(var6.getBlockPos()).getBlock() == Blocks.portal) {
            this.setPortal(var6.getBlockPos());
         } else {
            this.onImpact(var6);
         }
      }

      this.posX = this.posX + this.motionX;
      this.posY = this.posY + this.motionY;
      this.posZ = this.posZ + this.motionZ;
      float var21 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
      this.rotationPitch = (float)(MathHelper.atan2(this.motionY, var21) * 180.0 / Math.PI);

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
      float var22 = 0.99F;
      float var23 = this.getGravityVelocity();
      if (this.isInWater()) {
         for (int var10 = 0; var10 < 4; var10++) {
            float var24 = 0.25F;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.WATER_BUBBLE,
                  this.posX - this.motionX * var24,
                  this.posY - this.motionY * var24,
                  this.posZ - this.motionZ * var24,
                  this.motionX,
                  this.motionY,
                  this.motionZ
               );
         }

         var22 = 0.8F;
      }

      this.motionX *= var22;
      this.motionY *= var22;
      this.motionZ *= var22;
      this.motionY -= var23;
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   public float getGravityVelocity() {
      return 0.03F;
   }

   public abstract void onImpact(MovingObjectPosition var1);

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      var1.setShort("xTile", (short)this.xTile);
      var1.setShort("yTile", (short)this.yTile);
      var1.setShort("zTile", (short)this.zTile);
      ResourceLocation var5 = Block.blockRegistry.getNameForObject(this.inTile);
      var1.setString("inTile", var5 == null ? "" : var5.toString());
      var1.setByte("shake", (byte)this.throwableShake);
      var1.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      if ((this.throwerName == null || this.throwerName.length() == 0) && this.thrower instanceof EntityPlayer) {
         this.throwerName = this.thrower.getName();
      }

      var1.setString("ownerName", this.throwerName == null ? "" : this.throwerName);
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

      this.throwableShake = var1.getByte("shake") & 255;
      this.inGround = var1.getByte("inGround") == 1;
      this.thrower = null;
      this.throwerName = var1.getString("ownerName");
      if (this.throwerName != null && this.throwerName.length() == 0) {
         this.throwerName = null;
      }

      this.thrower = this.getThrower();
   }

   public EntityLivingBase getThrower() {
      if (this.thrower == null && this.throwerName != null && this.throwerName.length() > 0) {
         this.thrower = this.worldObj.getPlayerEntityByName(this.throwerName);
         if (this.thrower == null && this.worldObj instanceof WorldServer) {
            try {
               Entity var4 = ((WorldServer)this.worldObj).getEntityFromUuid(UUID.fromString(this.throwerName));
               if (var4 instanceof EntityLivingBase) {
                  this.thrower = (EntityLivingBase)var4;
               }
            } catch (Throwable var5) {
               this.thrower = null;
            }
         }
      }

      return this.thrower;
   }
}
