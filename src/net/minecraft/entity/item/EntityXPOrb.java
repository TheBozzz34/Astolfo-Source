package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityXPOrb extends Entity {
   public int xpColor;
   public int xpOrbAge;
   public int delayBeforeCanPickup;
   private int xpOrbHealth = 5;
   private int xpValue;
   private EntityPlayer closestPlayer;
   private int xpTargetColor;
   private static final long ab = 114396881832458L;

   public EntityXPOrb(World var1, double var2, double var4, double var6, int var8) {
      super(var1);
      this.setSize(0.5F, 0.5F);
      this.setPosition(var2, var4, var6);
      this.rotationYaw = (float)(Math.random() * 360.0);
      this.motionX = (float)(Math.random() * 0.2F - 0.1F) * 2.0F;
      this.motionY = (float)(Math.random() * 0.2) * 2.0F;
      this.motionZ = (float)(Math.random() * 0.2F - 0.1F) * 2.0F;
      this.xpValue = var8;
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   public EntityXPOrb(World var1) {
      super(var1);
      this.setSize(0.25F, 0.25F);
   }

   @Override
   public void entityInit() {
   }

   @Override
   public int getBrightnessForRender(float var1) {
      float var5 = 0.5F;
      var5 = MathHelper.clamp_float(var5, 0.0F, 1.0F);
      int var6 = super.getBrightnessForRender(var1);
      int var7 = var6 & 0xFF;
      int var8 = var6 >> 16 & 0xFF;
      var7 += (int)(var5 * 15.0F * 16.0F);
      if (var7 > 240) {
         var7 = 240;
      }

      return var7 | var8 << 16;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.delayBeforeCanPickup > 0) {
         this.delayBeforeCanPickup--;
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= 0.03F;
      if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() == Material.lava) {
         this.motionY = 0.2F;
         this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
         this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
         this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
      }

      this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0, this.posZ);
      double var4 = 8.0;
      if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
         if (this.closestPlayer == null || this.closestPlayer.getDistanceSqToEntity(this) > var4 * var4) {
            this.closestPlayer = this.worldObj.getClosestPlayerToEntity(this, var4);
         }

         this.xpTargetColor = this.xpColor;
      }

      if (this.closestPlayer != null && this.closestPlayer.isSpectator()) {
         this.closestPlayer = null;
      }

      if (this.closestPlayer != null) {
         double var6 = (this.closestPlayer.posX - this.posX) / var4;
         double var8 = (this.closestPlayer.posY + this.closestPlayer.getEyeHeight() - this.posY) / var4;
         double var10 = (this.closestPlayer.posZ - this.posZ) / var4;
         double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
         double var14 = 1.0 - var12;
         if (var14 > 0.0) {
            var14 *= var14;
            this.motionX += var6 / var12 * var14 * 0.1;
            this.motionY += var8 / var12 * var14 * 0.1;
            this.motionZ += var10 / var12 * var14 * 0.1;
         }
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      float var16 = 0.98F;
      if (this.onGround) {
         var16 = this.worldObj
               .getBlockState(
                  new BlockPos(
                     MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ)
                  )
               )
               .getBlock()
               .slipperiness
            * 0.98F;
      }

      this.motionX *= var16;
      this.motionY *= 0.98F;
      this.motionZ *= var16;
      if (this.onGround) {
         this.motionY *= -0.9F;
      }

      this.xpColor++;
      this.xpOrbAge++;
      if (this.xpOrbAge >= 6000) {
         this.setDead();
      }
   }

   @Override
   public boolean handleWaterMovement() {
      return this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.water, this);
   }

   @Override
   public void dealFireDamage(int var1) {
      this.attackEntityFrom(DamageSource.inFire, var1);
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      this.setBeenAttacked();
      this.xpOrbHealth = (int)(this.xpOrbHealth - var2);
      if (this.xpOrbHealth <= 0) {
         this.setDead();
      }

      return false;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      var1.setShort("Health", (byte)this.xpOrbHealth);
      var1.setShort("Age", (short)this.xpOrbAge);
      var1.setShort("Value", (short)this.xpValue);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      this.xpOrbHealth = var1.getShort("Health") & 255;
      this.xpOrbAge = var1.getShort("Age");
      this.xpValue = var1.getShort("Value");
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer var1) {
      if (!this.worldObj.isRemote && this.delayBeforeCanPickup == 0 && var1.xpCooldown == 0) {
         var1.xpCooldown = 2;
         this.worldObj.playSoundAtEntity(var1, "random.orb", 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
         var1.onItemPickup(this, 1);
         var1.addExperience(this.xpValue);
         this.setDead();
      }
   }

   public int getXpValue() {
      return this.xpValue;
   }

   public int getTextureByXP() {
      return this.xpValue >= 2477
         ? 10
         : (
            this.xpValue >= 1237
               ? 9
               : (
                  this.xpValue >= 617
                     ? 8
                     : (
                        this.xpValue >= 307
                           ? 7
                           : (
                              this.xpValue >= 149
                                 ? 6
                                 : (
                                    this.xpValue >= 73
                                       ? 5
                                       : (this.xpValue >= 37 ? 4 : (this.xpValue >= 17 ? 3 : (this.xpValue >= 7 ? 2 : (this.xpValue >= 3 ? 1 : 0))))
                                 )
                           )
                     )
               )
         );
   }

   public static int getXPSplit(int var0) {
      return var0 >= 2477
         ? 2477
         : (
            var0 >= 1237
               ? 1237
               : (
                  var0 >= 617
                     ? 617
                     : (
                        var0 >= 307
                           ? 307
                           : (var0 >= 149 ? 149 : (var0 >= 73 ? 73 : (var0 >= 37 ? 37 : (var0 >= 17 ? 17 : (var0 >= 7 ? 7 : (var0 >= 3 ? 3 : 1))))))
                     )
               )
         );
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }
}
