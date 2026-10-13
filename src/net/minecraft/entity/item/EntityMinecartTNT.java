package net.minecraft.entity.item;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityMinecartTNT extends EntityMinecart {
   private int minecartTNTFuse = -1;
   private static final long ab = 31679656379482L;

   public EntityMinecartTNT(World var1) {
      super(var1);
   }

   public EntityMinecartTNT(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.TNT;
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.tnt.getDefaultState();
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.minecartTNTFuse > 0) {
         this.minecartTNTFuse--;
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
      } else if (this.minecartTNTFuse == 0) {
         this.explodeCart(this.motionX * this.motionX + this.motionZ * this.motionZ);
      }

      if (this.isCollidedHorizontally) {
         double var4 = this.motionX * this.motionX + this.motionZ * this.motionZ;
         if (var4 >= 0.01F) {
            this.explodeCart(var4);
         }
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      Entity var6 = var1.getSourceOfDamage();
      if (var6 instanceof EntityArrow) {
         EntityArrow var7 = (EntityArrow)var6;
         if (var7.isBurning()) {
            this.explodeCart(var7.motionX * var7.motionX + var7.motionY * var7.motionY + var7.motionZ * var7.motionZ);
         }
      }

      return super.attackEntityFrom(var1, var2);
   }

   @Override
   public void killMinecart(DamageSource var1) {
      super.killMinecart(var1);
      double var5 = this.motionX * this.motionX + this.motionZ * this.motionZ;
      if (!var1.isExplosion() && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         this.entityDropItem(new ItemStack(Blocks.tnt, 1), 0.0F);
      }

      if (var1.isFireDamage() || var1.isExplosion() || var5 >= 0.01F) {
         this.explodeCart(var5);
      }
   }

   public void explodeCart(double var1) {
      if (!this.worldObj.isRemote) {
         double var6 = Math.sqrt(var1);
         if (var6 > 5.0) {
            var6 = 5.0;
         }

         this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)(4.0 + this.rand.nextDouble() * 1.5 * var6), true);
         this.setDead();
      }
   }

   @Override
   public void fall(float var1, float var2) {
      if (var1 >= 3.0F) {
         float var6 = var1 / 10.0F;
         this.explodeCart(var6 * var6);
      }

      super.fall(var1, var2);
   }

   @Override
   public void onActivatorRailPass(int var1, int var2, int var3, boolean var4) {
      if (var4 && this.minecartTNTFuse < 0) {
         this.ignite();
      }
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 10) {
         this.ignite();
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   public void ignite() {
      this.minecartTNTFuse = 80;
      if (!this.worldObj.isRemote) {
         this.worldObj.setEntityState(this, (byte)10);
         if (!this.isSilent()) {
            this.worldObj.playSoundAtEntity(this, "game.tnt.primed", 1.0F, 1.0F);
         }
      }
   }

   public int getFuseTicks() {
      return this.minecartTNTFuse;
   }

   public boolean isIgnited() {
      return this.minecartTNTFuse > -1;
   }

   @Override
   public float getExplosionResistance(Explosion var1, World var2, BlockPos var3, IBlockState var4) {
      return this.isIgnited() && (BlockRailBase.isRailBlock(var4) || BlockRailBase.isRailBlock(var2, var3.up()))
         ? 0.0F
         : super.getExplosionResistance(var1, var2, var3, var4);
   }

   @Override
   public boolean verifyExplosion(Explosion var1, World var2, BlockPos var3, IBlockState var4, float var5) {
      return this.isIgnited() && (BlockRailBase.isRailBlock(var4) || BlockRailBase.isRailBlock(var2, var3.up()))
         ? false
         : super.verifyExplosion(var1, var2, var3, var4, var5);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      if (var1.hasKey("TNTFuse", 99)) {
         this.minecartTNTFuse = var1.getInteger("TNTFuse");
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setInteger("TNTFuse", this.minecartTNTFuse);
   }
}
