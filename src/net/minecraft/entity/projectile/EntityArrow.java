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
   private static final long ab = 37588136860315L;

   public EntityArrow(World var1) {
      super(var1);
      this.renderDistanceWeight = 10.0;
      this.setSize(0.5F, 0.5F);
   }

   public EntityArrow(World var1, double var2, double var4, double var6) {
      super(var1);
      this.renderDistanceWeight = 10.0;
      this.setSize(0.5F, 0.5F);
      this.setPosition(var2, var4, var6);
   }

   public EntityArrow(World var1, EntityLivingBase var2, EntityLivingBase var3, float var4, float var5) {
      super(var1);
      this.renderDistanceWeight = 10.0;
      this.shootingEntity = var2;
      if (var2 instanceof EntityPlayer) {
         this.canBePickedUp = 1;
      }

      this.posY = var2.posY + var2.getEyeHeight() - 0.1F;
      double var9 = var3.posX - var2.posX;
      double var11 = var3.getEntityBoundingBox().minY + var3.height / 3.0F - this.posY;
      double var13 = var3.posZ - var2.posZ;
      double var15 = MathHelper.sqrt_double(var9 * var9 + var13 * var13);
      if (var15 >= 1.0E-7) {
         float var17 = (float)(MathHelper.atan2(var13, var9) * 180.0 / Math.PI) - 90.0F;
         float var18 = (float)(-(MathHelper.atan2(var11, var15) * 180.0 / Math.PI));
         double var19 = var9 / var15;
         double var21 = var13 / var15;
         this.setLocationAndAngles(var2.posX + var19, this.posY, var2.posZ + var21, var17, var18);
         float var23 = (float)(var15 * 0.2F);
         this.setThrowableHeading(var9, var11 + var23, var13, var4, var5);
      }
   }

   public EntityArrow(World var1, EntityLivingBase var2, float var3) {
      super(var1);
      this.renderDistanceWeight = 10.0;
      this.shootingEntity = var2;
      if (var2 instanceof EntityPlayer) {
         this.canBePickedUp = 1;
      }

      this.setSize(0.5F, 0.5F);
      this.setLocationAndAngles(var2.posX, var2.posY + var2.getEyeHeight(), var2.posZ, var2.rotationYaw, var2.rotationPitch);
      this.posX = this.posX - MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.posY -= 0.1F;
      this.posZ = this.posZ - MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
      this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
      this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI);
      this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, var3 * 1.5F, 1.0F);
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(16, (byte)0);
   }

   @Override
   public void setThrowableHeading(double var1, double var3, double var5, float var7, float var8) {
      float var12 = MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5);
      var1 /= var12;
      var3 /= var12;
      var5 /= var12;
      var1 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * var8;
      var3 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * var8;
      var5 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * var8;
      var1 *= var7;
      var3 *= var7;
      var5 *= var7;
      this.motionX = var1;
      this.motionY = var3;
      this.motionZ = var5;
      float var13 = MathHelper.sqrt_double(var1 * var1 + var5 * var5);
      this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(var1, var5) * 180.0 / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(var3, var13) * 180.0 / Math.PI);
      this.ticksInGround = 0;
   }

   @Override
   public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.setPosition(var1, var3, var5);
      this.setRotation(var7, var8);
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
         float var4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(this.motionY, var4) * 180.0 / Math.PI);
      }

      BlockPos var21 = new BlockPos(this.xTile, this.yTile, this.zTile);
      IBlockState var5 = this.worldObj.getBlockState(var21);
      Block var6 = var5.getBlock();
      if (var6.getMaterial() != Material.air) {
         var6.setBlockBoundsBasedOnState(this.worldObj, var21);
         AxisAlignedBB var7 = var6.getCollisionBoundingBox(this.worldObj, var21, var5);
         if (var7 != null && var7.isVecInside(new Vec3(this.posX, this.posY, this.posZ))) {
            this.inGround = true;
         }
      }

      if (this.arrowShake > 0) {
         this.arrowShake--;
      }

      if (this.inGround) {
         int var22 = var6.getMetaFromState(var5);
         if (var6 == this.inTile && var22 == this.inData) {
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
         Vec3 var23 = new Vec3(this.posX, this.posY, this.posZ);
         Vec3 var8 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         MovingObjectPosition var9 = this.worldObj.rayTraceBlocks(var23, var8, false, true, false);
         var23 = new Vec3(this.posX, this.posY, this.posZ);
         var8 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         if (var9 != null) {
            var8 = new Vec3(var9.hitVec.xCoord, var9.hitVec.yCoord, var9.hitVec.zCoord);
         }

         Entity var10 = null;
         List var11 = this.worldObj
            .getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
         double var12 = 0.0;

         for (int var14 = 0; var14 < var11.size(); var14++) {
            Entity var15 = (Entity)var11.get(var14);
            if (var15.canBeCollidedWith() && (var15 != this.shootingEntity || this.ticksInAir >= 5)) {
               float var16 = 0.3F;
               AxisAlignedBB var17 = var15.getEntityBoundingBox().expand(var16, var16, var16);
               MovingObjectPosition var18 = var17.calculateIntercept(var23, var8);
               if (var18 != null) {
                  double var19 = var23.squareDistanceTo(var18.hitVec);
                  if (var19 < var12 || var12 == 0.0) {
                     var10 = var15;
                     var12 = var19;
                  }
               }
            }
         }

         if (var10 != null) {
            var9 = new MovingObjectPosition(var10);
         }

         if (var9 != null && var9.entityHit != null && var9.entityHit instanceof EntityPlayer) {
            EntityPlayer var26 = (EntityPlayer)var9.entityHit;
            if (var26.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(var26)) {
               var9 = null;
            }
         }

         if (var9 != null) {
            if (var9.entityHit != null) {
               float var27 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
               int var31 = MathHelper.ceiling_double_int(var27 * this.damage);
               if (this.getIsCritical()) {
                  var31 += this.rand.nextInt(var31 / 2 + 2);
               }

               DamageSource var34;
               if (this.shootingEntity == null) {
                  var34 = DamageSource.causeArrowDamage(this, this);
               } else {
                  var34 = DamageSource.causeArrowDamage(this, this.shootingEntity);
               }

               if (this.isBurning() && !(var9.entityHit instanceof EntityEnderman)) {
                  var9.entityHit.setFire(5);
               }

               if (var9.entityHit.attackEntityFrom(var34, var31)) {
                  if (var9.entityHit instanceof EntityLivingBase) {
                     EntityLivingBase var37 = (EntityLivingBase)var9.entityHit;
                     if (!this.worldObj.isRemote) {
                        var37.setArrowCountInEntity(var37.getArrowCountInEntity() + 1);
                     }

                     if (this.knockbackStrength > 0) {
                        float var39 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                        if (var39 > 0.0F) {
                           var9.entityHit
                              .addVelocity(this.motionX * this.knockbackStrength * 0.6F / var39, 0.1, this.motionZ * this.knockbackStrength * 0.6F / var39);
                        }
                     }

                     if (this.shootingEntity instanceof EntityLivingBase) {
                        EnchantmentHelper.applyThornEnchantments(var37, this.shootingEntity);
                        EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.shootingEntity, var37);
                     }

                     if (this.shootingEntity != null
                        && var9.entityHit != this.shootingEntity
                        && var9.entityHit instanceof EntityPlayer
                        && this.shootingEntity instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                     }
                  }

                  this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                  if (!(var9.entityHit instanceof EntityEnderman)) {
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
               BlockPos var28 = var9.getBlockPos();
               this.xTile = var28.getX();
               this.yTile = var28.getY();
               this.zTile = var28.getZ();
               IBlockState var32 = this.worldObj.getBlockState(var28);
               this.inTile = var32.getBlock();
               this.inData = this.inTile.getMetaFromState(var32);
               this.motionX = (float)(var9.hitVec.xCoord - this.posX);
               this.motionY = (float)(var9.hitVec.yCoord - this.posY);
               this.motionZ = (float)(var9.hitVec.zCoord - this.posZ);
               float var35 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
               this.posX = this.posX - this.motionX / var35 * 0.05F;
               this.posY = this.posY - this.motionY / var35 * 0.05F;
               this.posZ = this.posZ - this.motionZ / var35 * 0.05F;
               this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
               this.inGround = true;
               this.arrowShake = 7;
               this.setIsCritical(false);
               if (this.inTile.getMaterial() != Material.air) {
                  this.inTile.onEntityCollidedWithBlock(this.worldObj, var28, var32, this);
               }
            }
         }

         if (this.getIsCritical()) {
            for (int var29 = 0; var29 < 4; var29++) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.CRIT,
                     this.posX + this.motionX * var29 / 4.0,
                     this.posY + this.motionY * var29 / 4.0,
                     this.posZ + this.motionZ * var29 / 4.0,
                     -this.motionX,
                     -this.motionY + 0.2,
                     -this.motionZ
                  );
            }
         }

         this.posX = this.posX + this.motionX;
         this.posY = this.posY + this.motionY;
         this.posZ = this.posZ + this.motionZ;
         float var30 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
         this.rotationPitch = (float)(MathHelper.atan2(this.motionY, var30) * 180.0 / Math.PI);

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
         float var33 = 0.99F;
         float var36 = 0.05F;
         if (this.isInWater()) {
            for (int var38 = 0; var38 < 4; var38++) {
               float var40 = 0.25F;
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.WATER_BUBBLE,
                     this.posX - this.motionX * var40,
                     this.posY - this.motionY * var40,
                     this.posZ - this.motionZ * var40,
                     this.motionX,
                     this.motionY,
                     this.motionZ
                  );
            }

            var33 = 0.6F;
         }

         if (this.G()) {
            this.extinguish();
         }

         this.motionX *= var33;
         this.motionY *= var33;
         this.motionZ *= var33;
         this.motionY -= var36;
         this.setPosition(this.posX, this.posY, this.posZ);
         this.doBlockCollisions();
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      var1.setShort("xTile", (short)this.xTile);
      var1.setShort("yTile", (short)this.yTile);
      var1.setShort("zTile", (short)this.zTile);
      var1.setShort("life", (short)this.ticksInGround);
      ResourceLocation var5 = Block.blockRegistry.getNameForObject(this.inTile);
      var1.setString("inTile", var5 == null ? "" : var5.toString());
      var1.setByte("inData", (byte)this.inData);
      var1.setByte("shake", (byte)this.arrowShake);
      var1.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      var1.setByte("pickup", (byte)this.canBePickedUp);
      var1.setDouble("damage", this.damage);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      this.xTile = var1.getShort("xTile");
      this.yTile = var1.getShort("yTile");
      this.zTile = var1.getShort("zTile");
      this.ticksInGround = var1.getShort("life");
      if (var1.hasKey("inTile", 8)) {
         this.inTile = Block.getBlockFromName(var1.getString("inTile"));
      } else {
         this.inTile = Block.getBlockById(var1.getByte("inTile") & 255);
      }

      this.inData = var1.getByte("inData") & 255;
      this.arrowShake = var1.getByte("shake") & 255;
      this.inGround = var1.getByte("inGround") == 1;
      if (var1.hasKey("damage", 99)) {
         this.damage = var1.getDouble("damage");
      }

      if (var1.hasKey("pickup", 99)) {
         this.canBePickedUp = var1.getByte("pickup");
      } else if (var1.hasKey("player", 99)) {
         this.canBePickedUp = var1.getBoolean("player") ? 1 : 0;
      }
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer var1) {
      if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
         boolean var5 = this.canBePickedUp == 1 || this.canBePickedUp == 2 && var1.capabilities.isCreativeMode;
         if (this.canBePickedUp == 1 && !var1.inventory.addItemStackToInventory(new ItemStack(Items.arrow, 1))) {
            var5 = false;
         }

         if (var5) {
            this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            var1.onItemPickup(this, 1);
            this.setDead();
         }
      }
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   public void setDamage(double var1) {
      this.damage = var1;
   }

   public double getDamage() {
      return this.damage;
   }

   public void setKnockbackStrength(int var1) {
      this.knockbackStrength = var1;
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }

   @Override
   public float getEyeHeight() {
      return 0.0F;
   }

   public void setIsCritical(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(16);
      if (var1) {
         this.dataWatcher.updateObject(16, (byte)(var5 | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var5 & -2));
      }
   }

   public boolean getIsCritical() {
      byte var4 = this.dataWatcher.getWatchableObjectByte(16);
      return (var4 & 1) != 0;
   }
}
