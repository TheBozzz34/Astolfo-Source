package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {
   private boolean isBlocked = true;
   private int transferTicker = -1;
   private BlockPos field_174900_c = BlockPos.ORIGIN;
   private static final long ab = 101229012403116L;

   public EntityMinecartHopper(World var1) {
      super(var1);
   }

   public EntityMinecartHopper(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.HOPPER;
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.hopper.getDefaultState();
   }

   @Override
   public int getDefaultDisplayTileOffset() {
      return 1;
   }

   @Override
   public int getSizeInventory() {
      return 5;
   }

   @Override
   public boolean interactFirst(EntityPlayer var1) {
      if (!this.worldObj.isRemote) {
         var1.displayGUIChest(this);
      }

      return true;
   }

   @Override
   public void onActivatorRailPass(int var1, int var2, int var3, boolean var4) {
      boolean var8 = !var4;
      if (var8 != this.getBlocked()) {
         this.setBlocked(var8);
      }
   }

   public boolean getBlocked() {
      return this.isBlocked;
   }

   public void setBlocked(boolean var1) {
      this.isBlocked = var1;
   }

   @Override
   public World getWorld() {
      return this.worldObj;
   }

   @Override
   public double getXPos() {
      return this.posX;
   }

   @Override
   public double getYPos() {
      return this.posY + 0.5;
   }

   @Override
   public double getZPos() {
      return this.posZ;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.isEntityAlive() && this.getBlocked()) {
         BlockPos var4 = new BlockPos(this);
         if (var4.equals(this.field_174900_c)) {
            this.transferTicker--;
         } else {
            this.setTransferTicker(0);
         }

         if (!this.canTransfer()) {
            this.setTransferTicker(0);
            if (this.func_96112_aD()) {
               this.setTransferTicker(4);
               this.markDirty();
            }
         }
      }
   }

   public boolean func_96112_aD() {
      if (TileEntityHopper.captureDroppedItems(this)) {
         return true;
      }

      List var4 = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(0.25, 0.0, 0.25), EntitySelectors.selectAnything);
      if (var4.size() > 0) {
         TileEntityHopper.putDropInInventoryAllSlots(this, (EntityItem)var4.get(0));
      }

      return false;
   }

   @Override
   public void killMinecart(DamageSource var1) {
      super.killMinecart(var1);
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         this.dropItemWithOffset(Item.getItemFromBlock(Blocks.hopper), 1, 0.0F);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setInteger("TransferCooldown", this.transferTicker);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.transferTicker = var1.getInteger("TransferCooldown");
   }

   public void setTransferTicker(int var1) {
      this.transferTicker = var1;
   }

   public boolean canTransfer() {
      return this.transferTicker > 0;
   }

   @Override
   public String getGuiID() {
      return "minecraft:hopper";
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerHopper(var1, this, var2);
   }
}
