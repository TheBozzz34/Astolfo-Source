package net.minecraft.entity.item;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartFurnace extends EntityMinecart {
   private int fuel;
   public double pushX;
   public double pushZ;
   private static final long ab = 118905500189550L;

   public EntityMinecartFurnace(World var1) {
      super(var1);
   }

   public EntityMinecartFurnace(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.FURNACE;
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.fuel > 0) {
         this.fuel--;
      }

      if (this.fuel <= 0) {
         this.pushX = this.pushZ = 0.0;
      }

      this.setMinecartPowered(this.fuel > 0);
      if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + 0.8, this.posZ, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public double getMaximumSpeed() {
      return 0.2;
   }

   @Override
   public void killMinecart(DamageSource var1) {
      super.killMinecart(var1);
      if (!var1.isExplosion() && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         this.entityDropItem(new ItemStack(Blocks.furnace, 1), 0.0F);
      }
   }

   @Override
   public void func_180460_a(BlockPos var1, IBlockState var2) {
      super.func_180460_a(var1, var2);
      double var6 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (var6 > 1.0E-4 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001) {
         var6 = MathHelper.sqrt_double(var6);
         this.pushX /= var6;
         this.pushZ /= var6;
         if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0) {
            this.pushX = 0.0;
            this.pushZ = 0.0;
         } else {
            double var8 = var6 / this.getMaximumSpeed();
            this.pushX *= var8;
            this.pushZ *= var8;
         }
      }
   }

   @Override
   public void applyDrag() {
      double var4 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (var4 > 1.0E-4) {
         var4 = MathHelper.sqrt_double(var4);
         this.pushX /= var4;
         this.pushZ /= var4;
         double var6 = 1.0;
         this.motionX *= 0.8F;
         this.motionY *= 0.0;
         this.motionZ *= 0.8F;
         this.motionX = this.motionX + this.pushX * var6;
         this.motionZ = this.motionZ + this.pushZ * var6;
      } else {
         this.motionX *= 0.98F;
         this.motionY *= 0.0;
         this.motionZ *= 0.98F;
      }

      super.applyDrag();
   }

   @Override
   public boolean interactFirst(EntityPlayer var1) {
      ItemStack var5 = var1.inventory.getCurrentItem();
      if (var5 != null && var5.getItem() == Items.coal) {
         if (!var1.capabilities.isCreativeMode && --var5.stackSize == 0) {
            var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
         }

         this.fuel += 3600;
      }

      this.pushX = this.posX - var1.posX;
      this.pushZ = this.posZ - var1.posZ;
      return true;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setDouble("PushX", this.pushX);
      var1.setDouble("PushZ", this.pushZ);
      var1.setShort("Fuel", (short)this.fuel);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.pushX = var1.getDouble("PushX");
      this.pushZ = var1.getDouble("PushZ");
      this.fuel = var1.getShort("Fuel");
   }

   public boolean isMinecartPowered() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setMinecartPowered(boolean var1) {
      if (var1) {
         this.dataWatcher.updateObject(16, (byte)(this.dataWatcher.getWatchableObjectByte(16) | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(this.dataWatcher.getWatchableObjectByte(16) & -2));
      }
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return (this.isMinecartPowered() ? Blocks.lit_furnace : Blocks.furnace).getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH);
   }
}
