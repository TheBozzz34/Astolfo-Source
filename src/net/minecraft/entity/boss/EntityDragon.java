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

   public EntityDragon(World worldIn) {
      super(worldIn);
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

   public double[] getMovementOffsets(int i, float f) {
      if (this.getHealth() <= 0.0F) {
         f = 0.0F;
      }

      f = 1.0F - f;
      int j = this.ringBufferIndex - i * 1 & 63;
      int k = this.ringBufferIndex - i * 1 - 1 & 63;
      double[] doubles = new double[3];
      double d = this.ringBuffer[j][0];
      double e = MathHelper.wrapAngleTo180_double(this.ringBuffer[k][0] - d);
      doubles[0] = d + e * f;
      d = this.ringBuffer[j][1];
      e = this.ringBuffer[k][1] - d;
      doubles[1] = d + e * f;
      doubles[2] = this.ringBuffer[j][2] + (this.ringBuffer[k][2] - this.ringBuffer[j][2]) * f;
      return doubles;
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         float f = MathHelper.cos(this.animTime * (float) Math.PI * 2.0F);
         float g = MathHelper.cos(this.prevAnimTime * (float) Math.PI * 2.0F);
         if (g <= -0.3F && f >= -0.3F && !this.isSilent()) {
            this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
         }
      }

      this.prevAnimTime = this.animTime;
      if (this.getHealth() <= 0.0F) {
         float h = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float f2 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float f3 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX + h, this.posY + 2.0 + f2, this.posZ + f3, 0.0, 0.0, 0.0);
      } else {
         this.updateDragonEnderCrystal();
         float f4 = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
         f4 *= (float)Math.pow(2.0, this.motionY);
         if (this.slowed) {
            this.animTime += f4 * 0.5F;
         } else {
            this.animTime += f4;
         }

         this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
         if (this.isAIDisabled()) {
            this.animTime = 0.5F;
         } else {
            if (this.ringBufferIndex < 0) {
               for (int i = 0; i < this.ringBuffer.length; i++) {
                  this.ringBuffer[i][0] = this.rotationYaw;
                  this.ringBuffer[i][1] = this.posY;
               }
            }

            if (++this.ringBufferIndex == this.ringBuffer.length) {
               this.ringBufferIndex = 0;
            }

            this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
            this.ringBuffer[this.ringBufferIndex][1] = this.posY;
            if (this.worldObj.isRemote) {
               if (this.newPosRotationIncrements > 0) {
                  double d = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
                  double e = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
                  double d2 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
                  double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
                  this.rotationYaw = (float)(this.rotationYaw + d3 / this.newPosRotationIncrements);
                  this.rotationPitch = (float)(this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
                  this.newPosRotationIncrements--;
                  this.setPosition(d, e, d2);
                  this.setRotation(this.rotationYaw, this.rotationPitch);
               }
            } else {
               double d4 = this.targetX - this.posX;
               double d5 = this.targetY - this.posY;
               double d6 = this.targetZ - this.posZ;
               double d7 = d4 * d4 + d5 * d5 + d6 * d6;
               if (this.target != null) {
                  this.targetX = this.target.posX;
                  this.targetZ = this.target.posZ;
                  double d8 = this.targetX - this.posX;
                  double d9 = this.targetZ - this.posZ;
                  double d10 = Math.sqrt(d8 * d8 + d9 * d9);
                  double d11 = 0.4F + d10 / 80.0 - 1.0;
                  if (d11 > 10.0) {
                     d11 = 10.0;
                  }

                  this.targetY = this.target.getEntityBoundingBox().minY + d11;
               } else {
                  this.targetX = this.targetX + this.rand.nextGaussian() * 2.0;
                  this.targetZ = this.targetZ + this.rand.nextGaussian() * 2.0;
               }

               if (this.forceNewTarget || d7 < 100.0 || d7 > 22500.0 || this.isCollidedHorizontally || this.isCollidedVertically) {
                  this.setNewTarget();
               }

               d5 /= MathHelper.sqrt_double(d4 * d4 + d6 * d6);
               float f5 = 0.6F;
               d5 = MathHelper.clamp_double(d5, -f5, f5);
               this.motionY += d5 * 0.1F;
               this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
               double d12 = 180.0 - MathHelper.atan2(d4, d6) * 180.0 / Math.PI;
               double d13 = MathHelper.wrapAngleTo180_double(d12 - this.rotationYaw);
               if (d13 > 50.0) {
                  d13 = 50.0;
               }

               if (d13 < -50.0) {
                  d13 = -50.0;
               }

               Vec3 vec3 = new Vec3(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ).normalize();
               double d14 = -MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
               Vec3 vec32 = new Vec3(MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F), this.motionY, d14).normalize();
               float f6 = ((float)vec32.dotProduct(vec3) + 0.5F) / 1.5F;
               if (f6 < 0.0F) {
                  f6 = 0.0F;
               }

               this.randomYawVelocity *= 0.8F;
               float f7 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
               double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0 + 1.0;
               if (d15 > 40.0) {
                  d15 = 40.0;
               }

               this.randomYawVelocity = (float)(this.randomYawVelocity + d13 * (0.7F / d15 / f7));
               this.rotationYaw = this.rotationYaw + this.randomYawVelocity * 0.1F;
               float f8 = (float)(2.0 / (d15 + 1.0));
               float f9 = 0.06F;
               this.moveFlying(0.0F, -1.0F, f9 * (f6 * f8 + (1.0F - f8)));
               if (this.slowed) {
                  this.moveEntity(this.motionX * 0.8F, this.motionY * 0.8F, this.motionZ * 0.8F);
               } else {
                  this.moveEntity(this.motionX, this.motionY, this.motionZ);
               }

               Vec3 vec33 = new Vec3(this.motionX, this.motionY, this.motionZ).normalize();
               float f10 = ((float)vec33.dotProduct(vec32) + 1.0F) / 2.0F;
               f10 = 0.8F + 0.15F * f10;
               this.motionX *= f10;
               this.motionZ *= f10;
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
            float f11 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
            float f12 = MathHelper.cos(f11);
            float f13 = -MathHelper.sin(f11);
            float f14 = this.rotationYaw * (float) Math.PI / 180.0F;
            float f15 = MathHelper.sin(f14);
            float f16 = MathHelper.cos(f14);
            this.dragonPartBody.onUpdate();
            this.dragonPartBody.setLocationAndAngles(this.posX + f15 * 0.5F, this.posY, this.posZ - f16 * 0.5F, 0.0F, 0.0F);
            this.dragonPartWing1.onUpdate();
            this.dragonPartWing1.setLocationAndAngles(this.posX + f16 * 4.5F, this.posY + 2.0, this.posZ + f15 * 4.5F, 0.0F, 0.0F);
            this.dragonPartWing2.onUpdate();
            this.dragonPartWing2.setLocationAndAngles(this.posX - f16 * 4.5F, this.posY + 2.0, this.posZ - f15 * 4.5F, 0.0F, 0.0F);
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

            double[] doubles = this.getMovementOffsets(5, 1.0F);
            double[] doubles2 = this.getMovementOffsets(0, 1.0F);
            float f17 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            float f18 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            this.dragonPartHead.onUpdate();
            this.dragonPartHead
               .setLocationAndAngles(
                  this.posX + f17 * 5.5F * f12, this.posY + (doubles2[1] - doubles[1]) * 1.0 + f13 * 5.5F, this.posZ - f18 * 5.5F * f12, 0.0F, 0.0F
               );

            for (int j = 0; j < 3; j++) {
               EntityDragonPart entityDragonPart = null;
               if (j == 0) {
                  entityDragonPart = this.dragonPartTail1;
               }

               if (j == 1) {
                  entityDragonPart = this.dragonPartTail2;
               }

               if (j == 2) {
                  entityDragonPart = this.dragonPartTail3;
               }

               double[] doubles3 = this.getMovementOffsets(12 + j * 2, 1.0F);
               float f19 = this.rotationYaw * (float) Math.PI / 180.0F + this.simplifyAngle(doubles3[0] - doubles[0]) * (float) Math.PI / 180.0F * 1.0F;
               float f20 = MathHelper.sin(f19);
               float f21 = MathHelper.cos(f19);
               float f22 = 1.5F;
               float f23 = (j + 1) * 2.0F;
               entityDragonPart.onUpdate();
               entityDragonPart.setLocationAndAngles(
                  this.posX - (f15 * f22 + f20 * f23) * f12,
                  this.posY + (doubles3[1] - doubles[1]) * 1.0 - (f23 + f22) * f13 + 1.5,
                  this.posZ + (f16 * f22 + f21 * f23) * f12,
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
         float f = 32.0F;
         List list = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().expand(f, f, f));
         EntityEnderCrystal entityEnderCrystal = null;
         double d = Double.MAX_VALUE;

         for (Object object : list) {
            EntityEnderCrystal entityEnderCrystal2 = (EntityEnderCrystal)object;
            double e = entityEnderCrystal2.getDistanceSqToEntity(this);
            if (e < d) {
               d = e;
               entityEnderCrystal = entityEnderCrystal2;
            }
         }

         this.healingEnderCrystal = entityEnderCrystal;
      }
   }

   private void collideWithEntities(List<Entity> list) {
      double d = (this.dragonPartBody.getEntityBoundingBox().minX + this.dragonPartBody.getEntityBoundingBox().maxX) / 2.0;
      double e = (this.dragonPartBody.getEntityBoundingBox().minZ + this.dragonPartBody.getEntityBoundingBox().maxZ) / 2.0;

      for (Entity entity : list) {
         if (entity instanceof EntityLivingBase) {
            double d2 = entity.posX - d;
            double d3 = entity.posZ - e;
            double d4 = d2 * d2 + d3 * d3;
            entity.addVelocity(d2 / d4 * 4.0, 0.2F, d3 / d4 * 4.0);
         }
      }
   }

   private void attackEntitiesInList(List<Entity> list) {
      for (int i = 0; i < list.size(); i++) {
         Entity entity = (Entity)list.get(i);
         if (entity instanceof EntityLivingBase) {
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
            this.applyEnchantments(this, entity);
         }
      }
   }

   private void setNewTarget() {
      this.forceNewTarget = false;
      ArrayList arrayList = Lists.newArrayList(this.worldObj.playerEntities);
      Iterator iterator2 = arrayList.iterator();

      while (iterator2.hasNext()) {
         if (((EntityPlayer)iterator2.next()).isSpectator()) {
            iterator2.remove();
         }
      }

      if (this.rand.nextInt(2) == 0 && !arrayList.isEmpty()) {
         this.target = (Entity)arrayList.get(this.rand.nextInt(arrayList.size()));
      } else {
         boolean bl;
         do {
            this.targetX = 0.0;
            this.targetY = 70.0F + this.rand.nextFloat() * 50.0F;
            this.targetZ = 0.0;
            this.targetX = this.targetX + (this.rand.nextFloat() * 120.0F - 60.0F);
            this.targetZ = this.targetZ + (this.rand.nextFloat() * 120.0F - 60.0F);
            double d = this.posX - this.targetX;
            double e = this.posY - this.targetY;
            double d2 = this.posZ - this.targetZ;
            bl = d * d + e * e + d2 * d2 > 100.0;
         } while (!bl);

         this.target = null;
      }
   }

   private float simplifyAngle(double d) {
      return (float)MathHelper.wrapAngleTo180_double(d);
   }

   private boolean destroyBlocksInAABB(AxisAlignedBB axisAlignedBB) {
      int i = MathHelper.floor_double(axisAlignedBB.minX);
      int j = MathHelper.floor_double(axisAlignedBB.minY);
      int k = MathHelper.floor_double(axisAlignedBB.minZ);
      int l = MathHelper.floor_double(axisAlignedBB.maxX);
      int m = MathHelper.floor_double(axisAlignedBB.maxY);
      int n = MathHelper.floor_double(axisAlignedBB.maxZ);
      boolean bl = false;
      boolean bl2 = false;

      for (int i2 = i; i2 <= l; i2++) {
         for (int i3 = j; i3 <= m; i3++) {
            for (int i4 = k; i4 <= n; i4++) {
               BlockPos blockPos = new BlockPos(i2, i3, i4);
               Block block = this.worldObj.getBlockState(blockPos).getBlock();
               if (block.getMaterial() != Material.air) {
                  if (block != Blocks.barrier
                     && block != Blocks.obsidian
                     && block != Blocks.end_stone
                     && block != Blocks.bedrock
                     && block != Blocks.command_block
                     && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
                     bl2 = this.worldObj.setBlockToAir(blockPos) || bl2;
                  } else {
                     bl = true;
                  }
               }
            }
         }
      }

      if (bl2) {
         double d = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * this.rand.nextFloat();
         double e = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * this.rand.nextFloat();
         double d2 = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * this.rand.nextFloat();
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d, e, d2, 0.0, 0.0, 0.0);
      }

      return bl;
   }

   @Override
   public boolean attackEntityFromPart(EntityDragonPart dragonPart, DamageSource source, float f) {
      if (dragonPart != this.dragonPartHead) {
         f = f / 4.0F + 1.0F;
      }

      float g = this.rotationYaw * (float) Math.PI / 180.0F;
      float h = MathHelper.sin(g);
      float f2 = MathHelper.cos(g);
      this.targetX = this.posX + h * 5.0F + (this.rand.nextFloat() - 0.5F) * 2.0F;
      this.targetY = this.posY + this.rand.nextFloat() * 3.0F + 1.0;
      this.targetZ = this.posZ - f2 * 5.0F + (this.rand.nextFloat() - 0.5F) * 2.0F;
      this.target = null;
      if (source.getEntity() instanceof EntityPlayer || source.isExplosion()) {
         this.attackDragonFrom(source, f);
      }

      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage()) {
         this.attackDragonFrom(source, amount);
      }

      return false;
   }

   public boolean attackDragonFrom(DamageSource source, float amount) {
      return super.attackEntityFrom(source, amount);
   }

   @Override
   public void onKillCommand() {
      this.setDead();
   }

   @Override
   public void onDeathUpdate() {
      this.deathTicks++;
      if (this.deathTicks >= 180 && this.deathTicks <= 200) {
         float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float g = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float h = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + f, this.posY + 2.0 + g, this.posZ + h, 0.0, 0.0, 0.0);
      }

      boolean bl = this.worldObj.getGameRules().getBoolean("doMobLoot");
      if (!this.worldObj.isRemote) {
         if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && bl) {
            int i = 1000;

            while (i > 0) {
               int j = EntityXPOrb.getXPSplit(i);
               i -= j;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }
         }

         if (this.deathTicks == 1) {
            this.worldObj.playBroadcastSound(1018, new BlockPos(this), 0);
         }
      }

      this.moveEntity(0.0, 0.1F, 0.0);
      this.renderYawOffset = this.rotationYaw += 20.0F;
      if (this.deathTicks == 200 && !this.worldObj.isRemote) {
         if (bl) {
            int k = 2000;

            while (k > 0) {
               int l = EntityXPOrb.getXPSplit(k);
               k -= l;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, l));
            }
         }

         this.generatePortal(new BlockPos(this.posX, 64.0, this.posZ));
         this.setDead();
      }
   }

   private void generatePortal(BlockPos pos) {
      for (int i = -1; i <= 32; i++) {
         for (int j = -4; j <= 4; j++) {
            for (int k = -4; k <= 4; k++) {
               double d = j * j + k * k;
               if (d <= 12.25) {
                  BlockPos blockPos = pos.add(j, i, k);
                  if (i < 0) {
                     if (d <= 6.25) {
                        this.worldObj.setBlockState(blockPos, Blocks.bedrock.getDefaultState());
                     }
                  } else if (i > 0) {
                     this.worldObj.setBlockState(blockPos, Blocks.air.getDefaultState());
                  } else if (d > 6.25) {
                     this.worldObj.setBlockState(blockPos, Blocks.bedrock.getDefaultState());
                  } else {
                     this.worldObj.setBlockState(blockPos, Blocks.end_portal.getDefaultState());
                  }
               }
            }
         }
      }

      this.worldObj.setBlockState(pos, Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(pos.up(), Blocks.bedrock.getDefaultState());
      BlockPos blockPos2 = pos.up(2);
      this.worldObj.setBlockState(blockPos2, Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(blockPos2.west(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
      this.worldObj.setBlockState(blockPos2.east(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
      this.worldObj.setBlockState(blockPos2.north(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
      this.worldObj.setBlockState(blockPos2.south(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
      this.worldObj.setBlockState(pos.up(3), Blocks.bedrock.getDefaultState());
      this.worldObj.setBlockState(pos.up(4), Blocks.dragon_egg.getDefaultState());
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
