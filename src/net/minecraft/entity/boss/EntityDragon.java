package net.minecraft.entity.boss;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityDragon extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob {
   public double targetX;
   public double targetY;
   public double targetZ;
   public double[][] ringBuffer = new double[64][3];
   public int ringBufferIndex = -1;
   public EntityDragonPart[] dragonPartArray;
   public EntityDragonPart dragonPartHead;
   public EntityDragonPart dragonPartBody;
   public EntityDragonPart dragonPartTail1;
   public EntityDragonPart dragonPartTail2;
   public EntityDragonPart dragonPartTail3;
   public EntityDragonPart dragonPartWing1;
   public EntityDragonPart dragonPartWing2;
   public float prevAnimTime;
   public float animTime;
   public boolean forceNewTarget;
   public boolean slowed;
   private Entity target;
   public int deathTicks;
   public EntityEnderCrystal healingEnderCrystal;
   private static final long ab = 119656833294911L;

   public EntityDragon(World var1) {
      super(var1);
      this.dragonPartArray = new EntityDragonPart[]{
         this.dragonPartHead = new EntityDragonPart(this, "head", 6.0F, 6.0F),
         this.dragonPartBody = new EntityDragonPart(this, "body", 8.0F, 8.0F),
         this.dragonPartTail1 = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.dragonPartTail2 = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.dragonPartTail3 = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.dragonPartWing1 = new EntityDragonPart(this, "wing", 4.0F, 4.0F),
         this.dragonPartWing2 = new EntityDragonPart(this, "wing", 4.0F, 4.0F)
      };
      this.setHealth(this.getMaxHealth());
      this.setSize(16.0F, 8.0F);
      this.noClip = true;
      this.isImmuneToFire = true;
      this.targetY = 100.0;
      this.ignoreFrustumCheck = true;
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0);
   }

   @Override
   public void entityInit() {
      super.entityInit();
   }

   public double[] getMovementOffsets(int var1, float var2) {
      if (this.getHealth() <= 0.0F) {
         var2 = 0.0F;
      }

      var2 = 1.0F - var2;
      int var6 = this.ringBufferIndex - var1 * 1 & 63;
      int var7 = this.ringBufferIndex - var1 * 1 - 1 & 63;
      double[] var8 = new double[3];
      double var9 = this.ringBuffer[var6][0];
      double var11 = MathHelper.wrapAngleTo180_double(this.ringBuffer[var7][0] - var9);
      var8[0] = var9 + var11 * var2;
      var9 = this.ringBuffer[var6][1];
      var11 = this.ringBuffer[var7][1] - var9;
      var8[1] = var9 + var11 * var2;
      var8[2] = this.ringBuffer[var6][2] + (this.ringBuffer[var7][2] - this.ringBuffer[var6][2]) * var2;
      return var8;
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         float var4 = MathHelper.cos(this.animTime * (float) Math.PI * 2.0F);
         float var5 = MathHelper.cos(this.prevAnimTime * (float) Math.PI * 2.0F);
         if (var5 <= -0.3F && var4 >= -0.3F && !this.isSilent()) {
            this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
         }
      }

      this.prevAnimTime = this.animTime;
      if (this.getHealth() <= 0.0F) {
         float var30 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float var33 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float var6 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX + var30, this.posY + 2.0 + var33, this.posZ + var6, 0.0, 0.0, 0.0);
      } else {
         this.updateDragonEnderCrystal();
         float var31 = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
         var31 *= (float)Math.pow(2.0, this.motionY);
         if (this.slowed) {
            this.animTime += var31 * 0.5F;
         } else {
            this.animTime += var31;
         }

         this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
         if (this.isAIDisabled()) {
            this.animTime = 0.5F;
         } else {
            if (this.ringBufferIndex < 0) {
               for (int var34 = 0; var34 < this.ringBuffer.length; var34++) {
                  this.ringBuffer[var34][0] = this.rotationYaw;
                  this.ringBuffer[var34][1] = this.posY;
               }
            }

            if (++this.ringBufferIndex == this.ringBuffer.length) {
               this.ringBufferIndex = 0;
            }

            this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
            this.ringBuffer[this.ringBufferIndex][1] = this.posY;
            if (this.worldObj.isRemote) {
               if (this.newPosRotationIncrements > 0) {
                  double var36 = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
                  double var41 = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
                  double var43 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
                  double var45 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
                  this.rotationYaw = (float)(this.rotationYaw + var45 / this.newPosRotationIncrements);
                  this.rotationPitch = (float)(this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
                  this.newPosRotationIncrements--;
                  this.setPosition(var36, var41, var43);
                  this.setRotation(this.rotationYaw, this.rotationPitch);
               }
            } else {
               double var35 = this.targetX - this.posX;
               double var7 = this.targetY - this.posY;
               double var9 = this.targetZ - this.posZ;
               double var11 = var35 * var35 + var7 * var7 + var9 * var9;
               if (this.target != null) {
                  this.targetX = this.target.posX;
                  this.targetZ = this.target.posZ;
                  double var13 = this.targetX - this.posX;
                  double var15 = this.targetZ - this.posZ;
                  double var17 = Math.sqrt(var13 * var13 + var15 * var15);
                  double var19 = 0.4F + var17 / 80.0 - 1.0;
                  if (var19 > 10.0) {
                     var19 = 10.0;
                  }

                  this.targetY = this.target.getEntityBoundingBox().minY + var19;
               } else {
                  this.targetX = this.targetX + this.rand.nextGaussian() * 2.0;
                  this.targetZ = this.targetZ + this.rand.nextGaussian() * 2.0;
               }

               if (this.forceNewTarget || var11 < 100.0 || var11 > 22500.0 || this.isCollidedHorizontally || this.isCollidedVertically) {
                  this.setNewTarget();
               }

               var7 /= MathHelper.sqrt_double(var35 * var35 + var9 * var9);
               float var47 = 0.6F;
               var7 = MathHelper.clamp_double(var7, -var47, var47);
               this.motionY += var7 * 0.1F;
               this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
               double var14 = 180.0 - MathHelper.atan2(var35, var9) * 180.0 / Math.PI;
               double var16 = MathHelper.wrapAngleTo180_double(var14 - this.rotationYaw);
               if (var16 > 50.0) {
                  var16 = 50.0;
               }

               if (var16 < -50.0) {
                  var16 = -50.0;
               }

               Vec3 var18 = new Vec3(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ).normalize();
               double var54 = -MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
               Vec3 var21 = new Vec3(MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F), this.motionY, var54).normalize();
               float var22 = ((float)var21.dotProduct(var18) + 0.5F) / 1.5F;
               if (var22 < 0.0F) {
                  var22 = 0.0F;
               }

               this.randomYawVelocity *= 0.8F;
               float var23 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
               double var24 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0 + 1.0;
               if (var24 > 40.0) {
                  var24 = 40.0;
               }

               this.randomYawVelocity = (float)(this.randomYawVelocity + var16 * (0.7F / var24 / var23));
               this.rotationYaw = this.rotationYaw + this.randomYawVelocity * 0.1F;
               float var26 = (float)(2.0 / (var24 + 1.0));
               float var27 = 0.06F;
               this.moveFlying(0.0F, -1.0F, var27 * (var22 * var26 + (1.0F - var26)));
               if (this.slowed) {
                  this.moveEntity(this.motionX * 0.8F, this.motionY * 0.8F, this.motionZ * 0.8F);
               } else {
                  this.moveEntity(this.motionX, this.motionY, this.motionZ);
               }

               Vec3 var28 = new Vec3(this.motionX, this.motionY, this.motionZ).normalize();
               float var29 = ((float)var28.dotProduct(var21) + 1.0F) / 2.0F;
               var29 = 0.8F + 0.15F * var29;
               this.motionX *= var29;
               this.motionZ *= var29;
               this.motionY *= 0.91F;
            }

            this.renderYawOffset = this.rotationYaw;
            this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
            this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
            this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
            this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
            this.dragonPartBody.height = 3.0F;
            this.dragonPartBody.width = 5.0F;
            this.dragonPartWing1.height = 2.0F;
            this.dragonPartWing1.width = 4.0F;
            this.dragonPartWing2.height = 3.0F;
            this.dragonPartWing2.width = 4.0F;
            float var37 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
            float var38 = MathHelper.cos(var37);
            float var42 = -MathHelper.sin(var37);
            float var8 = this.rotationYaw * (float) Math.PI / 180.0F;
            float var44 = MathHelper.sin(var8);
            float var10 = MathHelper.cos(var8);
            this.dragonPartBody.onUpdate();
            this.dragonPartBody.setLocationAndAngles(this.posX + var44 * 0.5F, this.posY, this.posZ - var10 * 0.5F, 0.0F, 0.0F);
            this.dragonPartWing1.onUpdate();
            this.dragonPartWing1.setLocationAndAngles(this.posX + var10 * 4.5F, this.posY + 2.0, this.posZ + var44 * 4.5F, 0.0F, 0.0F);
            this.dragonPartWing2.onUpdate();
            this.dragonPartWing2.setLocationAndAngles(this.posX - var10 * 4.5F, this.posY + 2.0, this.posZ - var44 * 4.5F, 0.0F, 0.0F);
            if (!this.worldObj.isRemote && this.hurtTime == 0) {
               this.collideWithEntities(
                  this.worldObj
                     .getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.getEntityBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0))
               );
               this.collideWithEntities(
                  this.worldObj
                     .getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.getEntityBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0))
               );
               this.attackEntitiesInList(
                  this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.getEntityBoundingBox().expand(1.0, 1.0, 1.0))
               );
            }

            double[] var46 = this.getMovementOffsets(5, 1.0F);
            double[] var12 = this.getMovementOffsets(0, 1.0F);
            float var48 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            float var49 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            this.dragonPartHead.onUpdate();
            this.dragonPartHead
               .setLocationAndAngles(
                  this.posX + var48 * 5.5F * var38, this.posY + (var12[1] - var46[1]) * 1.0 + var42 * 5.5F, this.posZ - var49 * 5.5F * var38, 0.0F, 0.0F
               );

            for (int var50 = 0; var50 < 3; var50++) {
               EntityDragonPart var51 = null;
               if (var50 == 0) {
                  var51 = this.dragonPartTail1;
               }

               if (var50 == 1) {
                  var51 = this.dragonPartTail2;
               }

               if (var50 == 2) {
                  var51 = this.dragonPartTail3;
               }

               double[] var52 = this.getMovementOffsets(12 + var50 * 2, 1.0F);
               float var53 = this.rotationYaw * (float) Math.PI / 180.0F + this.simplifyAngle(var52[0] - var46[0]) * (float) Math.PI / 180.0F * 1.0F;
               float var55 = MathHelper.sin(var53);
               float var20 = MathHelper.cos(var53);
               float var56 = 1.5F;
               float var57 = (var50 + 1) * 2.0F;
               var51.onUpdate();
               var51.setLocationAndAngles(
                  this.posX - (var44 * var56 + var55 * var57) * var38,
                  this.posY + (var52[1] - var46[1]) * 1.0 - (var57 + var56) * var42 + 1.5,
                  this.posZ + (var10 * var56 + var20 * var57) * var38,
                  0.0F,
                  0.0F
               );
            }

            if (!this.worldObj.isRemote) {
               this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getEntityBoundingBox())
                  | this.destroyBlocksInAABB(this.dragonPartBody.getEntityBoundingBox());
            }
         }
      }
   }

   private void updateDragonEnderCrystal() {
      if (this.healingEnderCrystal != null) {
         if (this.healingEnderCrystal.isDead) {
            if (!this.worldObj.isRemote) {
               this.attackEntityFromPart(this.dragonPartHead, DamageSource.setExplosionSource((Explosion)null), 10.0F);
            }

            this.healingEnderCrystal = null;
         } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 1.0F);
         }
      }

      if (this.rand.nextInt(10) == 0) {
         float var4 = 32.0F;
         List var5 = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().expand(var4, var4, var4));
         EntityEnderCrystal var6 = null;
         double var7 = Double.MAX_VALUE;

         for (Object var10$raw : var5) {
            EntityEnderCrystal var10 = (EntityEnderCrystal)var10$raw;
            double var11 = var10.getDistanceSqToEntity(this);
            if (var11 < var7) {
               var7 = var11;
               var6 = var10;
            }
         }

         this.healingEnderCrystal = var6;
      }
   }

   private void collideWithEntities(List<Entity> var1) {
      double var5 = (this.dragonPartBody.getEntityBoundingBox().minX + this.dragonPartBody.getEntityBoundingBox().maxX) / 2.0;
      double var7 = (this.dragonPartBody.getEntityBoundingBox().minZ + this.dragonPartBody.getEntityBoundingBox().maxZ) / 2.0;

      for (Entity var10 : var1) {
         if (var10 instanceof EntityLivingBase) {
            double var11 = var10.posX - var5;
            double var13 = var10.posZ - var7;
            double var15 = var11 * var11 + var13 * var13;
            var10.addVelocity(var11 / var15 * 4.0, 0.2F, var13 / var15 * 4.0);
         }
      }
   }

   private void attackEntitiesInList(List<Entity> var1) {
      for (int var5 = 0; var5 < var1.size(); var5++) {
         Entity var6 = (Entity)var1.get(var5);
         if (var6 instanceof EntityLivingBase) {
            var6.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
            this.applyEnchantments(this, var6);
         }
      }
   }

   private void setNewTarget() {
      this.forceNewTarget = false;
      ArrayList var4 = Lists.newArrayList(this.worldObj.playerEntities);
      Iterator var5 = var4.iterator();

      while (var5.hasNext()) {
         if (((EntityPlayer)var5.next()).isSpectator()) {
            var5.remove();
         }
      }

      if (this.rand.nextInt(2) == 0 && !var4.isEmpty()) {
         this.target = (Entity)var4.get(this.rand.nextInt(var4.size()));
      } else {
         boolean var12;
         do {
            this.targetX = 0.0;
            this.targetY = 70.0F + this.rand.nextFloat() * 50.0F;
            this.targetZ = 0.0;
            this.targetX = this.targetX + (this.rand.nextFloat() * 120.0F - 60.0F);
            this.targetZ = this.targetZ + (this.rand.nextFloat() * 120.0F - 60.0F);
            double var6 = this.posX - this.targetX;
            double var8 = this.posY - this.targetY;
            double var10 = this.posZ - this.targetZ;
            var12 = var6 * var6 + var8 * var8 + var10 * var10 > 100.0;
         } while (!var12);

         this.target = null;
      }
   }

   private float simplifyAngle(double var1) {
      return (float)MathHelper.wrapAngleTo180_double(var1);
   }

   private boolean destroyBlocksInAABB(AxisAlignedBB var1) {
      int var5 = MathHelper.floor_double(var1.minX);
      int var6 = MathHelper.floor_double(var1.minY);
      int var7 = MathHelper.floor_double(var1.minZ);
      int var8 = MathHelper.floor_double(var1.maxX);
      int var9 = MathHelper.floor_double(var1.maxY);
      int var10 = MathHelper.floor_double(var1.maxZ);
      boolean var11 = false;
      boolean var12 = false;

      for (int var13 = var5; var13 <= var8; var13++) {
         for (int var14 = var6; var14 <= var9; var14++) {
            for (int var15 = var7; var15 <= var10; var15++) {
               BlockPos var16 = new BlockPos(var13, var14, var15);
               Block var17 = this.worldObj.getBlockState(var16).getBlock();
               if (var17.getMaterial() != Material.air) {
                  if (var17 != Blocks.barrier
                     && var17 != Blocks.obsidian
                     && var17 != Blocks.end_stone
                     && var17 != Blocks.bedrock
                     && var17 != Blocks.command_block
                     && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
                     var12 = this.worldObj.setBlockToAir(var16) || var12;
                  } else {
                     var11 = true;
                  }
               }
            }
         }
      }

      if (var12) {
         double var19 = var1.minX + (var1.maxX - var1.minX) * this.rand.nextFloat();
         double var20 = var1.minY + (var1.maxY - var1.minY) * this.rand.nextFloat();
         double var21 = var1.minZ + (var1.maxZ - var1.minZ) * this.rand.nextFloat();
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, var19, var20, var21, 0.0, 0.0, 0.0);
      }

      return var11;
   }

   @Override
   public boolean attackEntityFromPart(EntityDragonPart var1, DamageSource var2, float var3) {
      if (var1 != this.dragonPartHead) {
         var3 = var3 / 4.0F + 1.0F;
      }

      float var7 = this.rotationYaw * (float) Math.PI / 180.0F;
      float var8 = MathHelper.sin(var7);
      float var9 = MathHelper.cos(var7);
      this.targetX = this.posX + var8 * 5.0F + (this.rand.nextFloat() - 0.5F) * 2.0F;
      this.targetY = this.posY + this.rand.nextFloat() * 3.0F + 1.0;
      this.targetZ = this.posZ - var9 * 5.0F + (this.rand.nextFloat() - 0.5F) * 2.0F;
      this.target = null;
      if (var2.getEntity() instanceof EntityPlayer || var2.isExplosion()) {
         this.attackDragonFrom(var2, var3);
      }

      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (var1 instanceof EntityDamageSource && ((EntityDamageSource)var1).getIsThornsDamage()) {
         this.attackDragonFrom(var1, var2);
      }

      return false;
   }

   public boolean attackDragonFrom(DamageSource var1, float var2) {
      return super.attackEntityFrom(var1, var2);
   }

   @Override
   public void onKillCommand() {
      this.setDead();
   }

   @Override
   public void onDeathUpdate() {
      this.deathTicks++;
      if (this.deathTicks >= 180 && this.deathTicks <= 200) {
         float var4 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float var5 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float var6 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + var4, this.posY + 2.0 + var5, this.posZ + var6, 0.0, 0.0, 0.0);
      }

      boolean var7 = this.worldObj.getGameRules().getBoolean("doMobLoot");
      if (!this.worldObj.isRemote) {
         if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && var7) {
            int var8 = 1000;

            while (var8 > 0) {
               int var10 = EntityXPOrb.getXPSplit(var8);
               var8 -= var10;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var10));
            }
         }

         if (this.deathTicks == 1) {
            this.worldObj.playBroadcastSound(1018, new BlockPos(this), 0);
         }
      }

      this.moveEntity(0.0, 0.1F, 0.0);
      this.renderYawOffset = this.rotationYaw += 20.0F;
      if (this.deathTicks == 200 && !this.worldObj.isRemote) {
         if (var7) {
            int var9 = 2000;

            while (var9 > 0) {
               int var11 = EntityXPOrb.getXPSplit(var9);
               var9 -= var11;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var11));
            }
         }

         this.generatePortal(new BlockPos(this.posX, 64.0, this.posZ));
         this.setDead();
      }
   }

   private void generatePortal(BlockPos var1) {
      for (int var10 = -1; var10 <= 32; var10++) {
         for (int var11 = -4; var11 <= 4; var11++) {
            for (int var12 = -4; var12 <= 4; var12++) {
               double var13 = var11 * var11 + var12 * var12;
               if (var13 <= 12.25) {
                  BlockPos var15 = var1.add(var11, var10, var12);
                  if (var10 < 0) {
                     if (var13 <= 6.25) {
                        this.worldObj.setBlockState(var15, Blocks.bedrock.getDefaultState());
                     }
                  } else if (var10 > 0) {
                     this.worldObj.setBlockState(var15, Blocks.air.getDefaultState());
                  } else if (var13 > 6.25) {
                     this.worldObj.setBlockState(var15, Blocks.bedrock.getDefaultState());
                  } else {
                     this.worldObj.setBlockState(var15, Blocks.end_portal.getDefaultState());
                  }
               }
            }
         }
      }

      this.worldObj.setBlockState(var1, Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(var1.up(), Blocks.bedrock.getDefaultState());
      BlockPos var16 = var1.up(2);
      this.worldObj.setBlockState(var16, Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(var16.west(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
      this.worldObj.setBlockState(var16.east(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
      this.worldObj.setBlockState(var16.north(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
      this.worldObj.setBlockState(var16.south(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
      this.worldObj.setBlockState(var1.up(3), Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(var1.up(4), Blocks.dragon_egg.getDefaultState());
   }

   @Override
   public void despawnEntity() {
   }

   @Override
   public Entity[] getParts() {
      return this.dragonPartArray;
   }

   @Override
   public boolean canBeCollidedWith() {
      return false;
   }

   @Override
   public World getWorld() {
      return this.worldObj;
   }

   @Override
   public String getLivingSound() {
      return "mob.enderdragon.growl";
   }

   @Override
   public String getHurtSound() {
      return "mob.enderdragon.hit";
   }

   @Override
   public float getSoundVolume() {
      return 5.0F;
   }
}
