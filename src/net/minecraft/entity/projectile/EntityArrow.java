package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityArrow extends Entity implements IProjectile {
   private int xTile = -1;
   private int yTile = -1;
   private int zTile = -1;
   private Block inTile;
   private int inData;
   private boolean inGround;
   public int canBePickedUp;
   public int arrowShake;
   public Entity shootingEntity;
   private int ticksInGround;
   private int ticksInAir;
   private double damage = 2.0;
   private int knockbackStrength;

   public EntityArrow(World worldIn) {
      super(worldIn);
      this.renderDistanceWeight = 10.0;
      this.setSize(0.5F, 0.5F);
   }

   public EntityArrow(World worldIn, double x, double y, double z) {
      super(worldIn);
      this.renderDistanceWeight = 10.0;
      this.setSize(0.5F, 0.5F);
      this.setPosition(x, y, z);
   }

   public EntityArrow(World worldIn, EntityLivingBase shooter, EntityLivingBase target, float velocity, float innacuracy) {
      super(worldIn);
      this.renderDistanceWeight = 10.0;
      this.shootingEntity = shooter;
      if (shooter instanceof EntityPlayer) {
         this.canBePickedUp = 1;
      }

      this.posY = shooter.posY + shooter.getEyeHeight() - 0.1F;
      double d = target.posX - shooter.posX;
      double e = target.getEntityBoundingBox().minY + target.height / 3.0F - this.posY;
      double d2 = target.posZ - shooter.posZ;
      double d3 = MathHelper.sqrt_double(d * d + d2 * d2);
      if (d3 >= 1.0E-7) {
         float f = (float)(MathHelper.atan2(d2, d) * 180.0 / Math.PI) - 90.0F;
         float g = (float)(-(MathHelper.atan2(e, d3) * 180.0 / Math.PI));
         double d4 = d / d3;
         double d5 = d2 / d3;
         this.setLocationAndAngles(shooter.posX + d4, this.posY, shooter.posZ + d5, f, g);
         float h = (float)(d3 * 0.2F);
         this.setThrowableHeading(d, e + h, d2, velocity, innacuracy);
      }
   }

   public EntityArrow(World worldIn, EntityLivingBase shooter, float velocity) {
      super(worldIn);
      this.renderDistanceWeight = 10.0;
      this.shootingEntity = shooter;
      if (shooter instanceof EntityPlayer) {
         this.canBePickedUp = 1;
      }

      this.setSize(0.5F, 0.5F);
      this.setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
      this.posX = this.posX - MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.posY -= 0.1F;
      this.posZ = this.posZ - MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
      this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
      this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI);
      this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity * 1.5F, 1.0F);
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(16, (byte)0);
   }

   @Override
   public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
      float f = MathHelper.sqrt_double(x * x + y * y + z * z);
      x /= f;
      y /= f;
      z /= f;
      x += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * inaccuracy;
      y += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * inaccuracy;
      z += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * inaccuracy;
      x *= velocity;
      y *= velocity;
      z *= velocity;
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      float g = MathHelper.sqrt_double(x * x + z * z);
      this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(x, z) * 180.0 / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(y, g) * 180.0 / Math.PI);
      this.ticksInGround = 0;
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean bl) {
      this.setPosition(x, y, z);
      this.setRotation(yaw, pitch);
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt_double(x * x + z * z);
         this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(x, z) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(y, f) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch;
         this.prevRotationYaw = this.rotationYaw;
         this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.ticksInGround = 0;
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f) * 180.0 / Math.PI);
      }

      BlockPos blockPos = new BlockPos(this.xTile, this.yTile, this.zTile);
      IBlockState iBlockState = this.worldObj.getBlockState(blockPos);
      Block block = iBlockState.getBlock();
      if (block.getMaterial() != Material.air) {
         block.setBlockBoundsBasedOnState(this.worldObj, blockPos);
         AxisAlignedBB axisAlignedBB = block.getCollisionBoundingBox(this.worldObj, blockPos, iBlockState);
         if (axisAlignedBB != null && axisAlignedBB.isVecInside(new Vec3(this.posX, this.posY, this.posZ))) {
            this.inGround = true;
         }
      }

      if (this.arrowShake > 0) {
         this.arrowShake--;
      }

      if (this.inGround) {
         int i = block.getMetaFromState(iBlockState);
         if (block == this.inTile && i == this.inData) {
            this.ticksInGround++;
            if (this.ticksInGround >= 1200) {
               this.setDead();
            }
         } else {
            this.inGround = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         }
      } else {
         this.ticksInAir++;
         Vec3 vec3 = new Vec3(this.posX, this.posY, this.posZ);
         Vec3 vec32 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         MovingObjectPosition movingObjectPosition = this.worldObj.rayTraceBlocks(vec3, vec32, false, true, false);
         vec3 = new Vec3(this.posX, this.posY, this.posZ);
         vec32 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         if (movingObjectPosition != null) {
            vec32 = new Vec3(movingObjectPosition.hitVec.xCoord, movingObjectPosition.hitVec.yCoord, movingObjectPosition.hitVec.zCoord);
         }

         Entity entity = null;
         List list = this.worldObj
            .getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
         double d = 0.0;

         for (int j = 0; j < list.size(); j++) {
            Entity entity2 = (Entity)list.get(j);
            if (entity2.canBeCollidedWith() && (entity2 != this.shootingEntity || this.ticksInAir >= 5)) {
               float g = 0.3F;
               AxisAlignedBB axisAlignedBB2 = entity2.getEntityBoundingBox().expand(g, g, g);
               MovingObjectPosition movingObjectPosition2 = axisAlignedBB2.calculateIntercept(vec3, vec32);
               if (movingObjectPosition2 != null) {
                  double e = vec3.squareDistanceTo(movingObjectPosition2.hitVec);
                  if (e < d || d == 0.0) {
                     entity = entity2;
                     d = e;
                  }
               }
            }
         }

         if (entity != null) {
            movingObjectPosition = new MovingObjectPosition(entity);
         }

         if (movingObjectPosition != null && movingObjectPosition.entityHit != null && movingObjectPosition.entityHit instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)movingObjectPosition.entityHit;
            if (entityPlayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityPlayer)) {
               movingObjectPosition = null;
            }
         }

         if (movingObjectPosition != null) {
            if (movingObjectPosition.entityHit != null) {
               float h = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
               int k = MathHelper.ceiling_double_int(h * this.damage);
               if (this.getIsCritical()) {
                  k += this.rand.nextInt(k / 2 + 2);
               }

               DamageSource damageSource;
               if (this.shootingEntity == null) {
                  damageSource = DamageSource.causeArrowDamage(this, this);
               } else {
                  damageSource = DamageSource.causeArrowDamage(this, this.shootingEntity);
               }

               if (this.isBurning() && !(movingObjectPosition.entityHit instanceof EntityEnderman)) {
                  movingObjectPosition.entityHit.setFire(5);
               }

               if (movingObjectPosition.entityHit.attackEntityFrom(damageSource, k)) {
                  if (movingObjectPosition.entityHit instanceof EntityLivingBase) {
                     EntityLivingBase entityLivingBase = (EntityLivingBase)movingObjectPosition.entityHit;
                     if (!this.worldObj.isRemote) {
                        entityLivingBase.setArrowCountInEntity(entityLivingBase.getArrowCountInEntity() + 1);
                     }

                     if (this.knockbackStrength > 0) {
                        float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                        if (f2 > 0.0F) {
                           movingObjectPosition.entityHit
                              .addVelocity(this.motionX * this.knockbackStrength * 0.6F / f2, 0.1, this.motionZ * this.knockbackStrength * 0.6F / f2);
                        }
                     }

                     if (this.shootingEntity instanceof EntityLivingBase) {
                        EnchantmentHelper.applyThornEnchantments(entityLivingBase, this.shootingEntity);
                        EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.shootingEntity, entityLivingBase);
                     }

                     if (this.shootingEntity != null
                        && movingObjectPosition.entityHit != this.shootingEntity
                        && movingObjectPosition.entityHit instanceof EntityPlayer
                        && this.shootingEntity instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                     }
                  }

                  this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                  if (!(movingObjectPosition.entityHit instanceof EntityEnderman)) {
                     this.setDead();
                  }
               } else {
                  this.motionX *= -0.1F;
                  this.motionY *= -0.1F;
                  this.motionZ *= -0.1F;
                  this.rotationYaw += 180.0F;
                  this.prevRotationYaw += 180.0F;
                  this.ticksInAir = 0;
               }
            } else {
               BlockPos blockPos2 = movingObjectPosition.getBlockPos();
               this.xTile = blockPos2.getX();
               this.yTile = blockPos2.getY();
               this.zTile = blockPos2.getZ();
               IBlockState iBlockState2 = this.worldObj.getBlockState(blockPos2);
               this.inTile = iBlockState2.getBlock();
               this.inData = this.inTile.getMetaFromState(iBlockState2);
               this.motionX = (float)(movingObjectPosition.hitVec.xCoord - this.posX);
               this.motionY = (float)(movingObjectPosition.hitVec.yCoord - this.posY);
               this.motionZ = (float)(movingObjectPosition.hitVec.zCoord - this.posZ);
               float f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
               this.posX = this.posX - this.motionX / f3 * 0.05F;
               this.posY = this.posY - this.motionY / f3 * 0.05F;
               this.posZ = this.posZ - this.motionZ / f3 * 0.05F;
               this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
               this.inGround = true;
               this.arrowShake = 7;
               this.setIsCritical(false);
               if (this.inTile.getMaterial() != Material.air) {
                  this.inTile.onEntityCollidedWithBlock(this.worldObj, blockPos2, iBlockState2, this);
               }
            }
         }

         if (this.getIsCritical()) {
            for (int l = 0; l < 4; l++) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.CRIT,
                     this.posX + this.motionX * l / 4.0,
                     this.posY + this.motionY * l / 4.0,
                     this.posZ + this.motionZ * l / 4.0,
                     -this.motionX,
                     -this.motionY + 0.2,
                     -this.motionZ
                  );
            }
         }

         this.posX = this.posX + this.motionX;
         this.posY = this.posY + this.motionY;
         this.posZ = this.posZ + this.motionZ;
         float f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
         this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f4) * 180.0 / Math.PI);

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
         float f5 = 0.99F;
         float f6 = 0.05F;
         if (this.isInWater()) {
            for (int m = 0; m < 4; m++) {
               float f7 = 0.25F;
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.WATER_BUBBLE,
                     this.posX - this.motionX * f7,
                     this.posY - this.motionY * f7,
                     this.posZ - this.motionZ * f7,
                     this.motionX,
                     this.motionY,
                     this.motionZ
                  );
            }

            f5 = 0.6F;
         }

         if (this.isWet()) {
            this.extinguish();
         }

         this.motionX *= f5;
         this.motionY *= f5;
         this.motionZ *= f5;
         this.motionY -= f6;
         this.setPosition(this.posX, this.posY, this.posZ);
         this.doBlockCollisions();
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setShort("xTile", (short)this.xTile);
      tagCompound.setShort("yTile", (short)this.yTile);
      tagCompound.setShort("zTile", (short)this.zTile);
      tagCompound.setShort("life", (short)this.ticksInGround);
      ResourceLocation resourceLocation = Block.blockRegistry.getNameForObject(this.inTile);
      tagCompound.setString("inTile", resourceLocation == null ? "" : resourceLocation.toString());
      tagCompound.setByte("inData", (byte)this.inData);
      tagCompound.setByte("shake", (byte)this.arrowShake);
      tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      tagCompound.setByte("pickup", (byte)this.canBePickedUp);
      tagCompound.setDouble("damage", this.damage);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.xTile = tagCompund.getShort("xTile");
      this.yTile = tagCompund.getShort("yTile");
      this.zTile = tagCompund.getShort("zTile");
      this.ticksInGround = tagCompund.getShort("life");
      if (tagCompund.hasKey("inTile", 8)) {
         this.inTile = Block.getBlockFromName(tagCompund.getString("inTile"));
      } else {
         this.inTile = Block.getBlockById(tagCompund.getByte("inTile") & 255);
      }

      this.inData = tagCompund.getByte("inData") & 255;
      this.arrowShake = tagCompund.getByte("shake") & 255;
      this.inGround = tagCompund.getByte("inGround") == 1;
      if (tagCompund.hasKey("damage", 99)) {
         this.damage = tagCompund.getDouble("damage");
      }

      if (tagCompund.hasKey("pickup", 99)) {
         this.canBePickedUp = tagCompund.getByte("pickup");
      } else if (tagCompund.hasKey("player", 99)) {
         this.canBePickedUp = tagCompund.getBoolean("player") ? 1 : 0;
      }
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
         boolean bl = this.canBePickedUp == 1 || this.canBePickedUp == 2 && entityIn.capabilities.isCreativeMode;
         if (this.canBePickedUp == 1 && !entityIn.inventory.addItemStackToInventory(new ItemStack(Items.arrow, 1))) {
            bl = false;
         }

         if (bl) {
            this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            entityIn.onItemPickup(this, 1);
            this.setDead();
         }
      }
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   public void setDamage(double damageIn) {
      this.damage = damageIn;
   }

   public double getDamage() {
      return this.damage;
   }

   public void setKnockbackStrength(int knockbackStrengthIn) {
      this.knockbackStrength = knockbackStrengthIn;
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }

   @Override
   public float getEyeHeight() {
      return 0.0F;
   }

   public void setIsCritical(boolean critical) {
      byte b = this.dataWatcher.getWatchableObjectByte(16);
      if (critical) {
         this.dataWatcher.updateObject(16, (byte)(b | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(b & -2));
      }
   }

   public boolean getIsCritical() {
      byte b = this.dataWatcher.getWatchableObjectByte(16);
      return (b & 1) != 0;
   }
}
