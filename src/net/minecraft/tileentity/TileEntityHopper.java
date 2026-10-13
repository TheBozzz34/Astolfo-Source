package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockable implements IHopper, ITickable {
   private ItemStack[] inventory = new ItemStack[5];
   private String customName;
   private int transferCooldown = -1;
   private static final long a = 117909285935438L;

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      NBTTagList var5 = var1.getTagList("Items", 10);
      this.inventory = new ItemStack[this.getSizeInventory()];
      if (var1.hasKey("CustomName", 8)) {
         this.customName = var1.getString("CustomName");
      }

      this.transferCooldown = var1.getInteger("TransferCooldown");

      for (int var6 = 0; var6 < var5.tagCount(); var6++) {
         NBTTagCompound var7 = var5.getCompoundTagAt(var6);
         byte var8 = var7.getByte("Slot");
         if (var8 >= 0 && var8 < this.inventory.length) {
            this.inventory[var8] = ItemStack.loadItemStackFromNBT(var7);
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      NBTTagList var5 = new NBTTagList();

      for (int var6 = 0; var6 < this.inventory.length; var6++) {
         if (this.inventory[var6] != null) {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setByte("Slot", (byte)var6);
            this.inventory[var6].writeToNBT(var7);
            var5.appendTag(var7);
         }
      }

      var1.setTag("Items", var5);
      var1.setInteger("TransferCooldown", this.transferCooldown);
      if (this.hasCustomName()) {
         var1.setString("CustomName", this.customName);
      }
   }

   @Override
   public void markDirty() {
      super.markDirty();
   }

   @Override
   public int getSizeInventory() {
      return this.inventory.length;
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return this.inventory[var1];
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      if (this.inventory[var1] != null) {
         if (this.inventory[var1].stackSize <= var2) {
            ItemStack var7 = this.inventory[var1];
            this.inventory[var1] = null;
            return var7;
         }

         ItemStack var6 = this.inventory[var1].splitStack(var2);
         if (this.inventory[var1].stackSize == 0) {
            this.inventory[var1] = null;
         }

         return var6;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      if (this.inventory[var1] != null) {
         ItemStack var5 = this.inventory[var1];
         this.inventory[var1] = null;
         return var5;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      this.inventory[var1] = var2;
      if (var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
         var2.stackSize = this.getInventoryStackLimit();
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.hopper";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String var1) {
      this.customName = var1;
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer var1) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : var1.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void openInventory(EntityPlayer var1) {
   }

   @Override
   public void closeInventory(EntityPlayer var1) {
   }

   @Override
   public boolean isItemValidForSlot(int var1, ItemStack var2) {
      return true;
   }

   @Override
   public void update() {
      if (this.worldObj != null && !this.worldObj.isRemote) {
         this.transferCooldown--;
         if (!this.isOnTransferCooldown()) {
            this.setTransferCooldown(0);
            this.updateHopper();
         }
      }
   }

   public boolean updateHopper() {
      if (this.worldObj != null && !this.worldObj.isRemote) {
         if (!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata())) {
            boolean var4 = false;
            if (!this.isEmpty()) {
               var4 = this.transferItemsOut();
            }

            if (!this.isFull()) {
               var4 = captureDroppedItems(this) || var4;
            }

            if (var4) {
               this.setTransferCooldown(8);
               this.markDirty();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isEmpty() {
      for (ItemStack var7 : this.inventory) {
         if (var7 != null) {
            return false;
         }
      }

      return true;
   }

   private boolean isFull() {
      for (ItemStack var7 : this.inventory) {
         if (var7 == null || var7.stackSize != var7.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   private boolean transferItemsOut() {
      IInventory var4 = this.getInventoryForHopperTransfer();
      if (var4 == null) {
         return false;
      }

      EnumFacing var5 = BlockHopper.getFacing(this.getBlockMetadata()).getOpposite();
      if (this.isInventoryFull(var4, var5)) {
         return false;
      }

      for (int var6 = 0; var6 < this.getSizeInventory(); var6++) {
         if (this.getStackInSlot(var6) != null) {
            ItemStack var7 = this.getStackInSlot(var6).copy();
            ItemStack var8 = putStackInInventoryAllSlots(var4, this.decrStackSize(var6, 1), var5);
            if (var8 == null || var8.stackSize == 0) {
               var4.markDirty();
               return true;
            }

            this.setInventorySlotContents(var6, var7);
         }
      }

      return false;
   }

   private boolean isInventoryFull(IInventory var1, EnumFacing var2) {
      if (var1 instanceof ISidedInventory) {
         ISidedInventory var6 = (ISidedInventory)var1;
         int[] var7 = var6.getSlotsForFace(var2);

         for (int var8 = 0; var8 < var7.length; var8++) {
            ItemStack var9 = var6.getStackInSlot(var7[var8]);
            if (var9 == null || var9.stackSize != var9.getMaxStackSize()) {
               return false;
            }
         }
      } else {
         int var10 = var1.getSizeInventory();

         for (int var11 = 0; var11 < var10; var11++) {
            ItemStack var12 = var1.getStackInSlot(var11);
            if (var12 == null || var12.stackSize != var12.getMaxStackSize()) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean isInventoryEmpty(IInventory var0, EnumFacing var1) {
      if (var0 instanceof ISidedInventory) {
         ISidedInventory var5 = (ISidedInventory)var0;
         int[] var6 = var5.getSlotsForFace(var1);

         for (int var7 = 0; var7 < var6.length; var7++) {
            if (var5.getStackInSlot(var6[var7]) != null) {
               return false;
            }
         }
      } else {
         int var8 = var0.getSizeInventory();

         for (int var9 = 0; var9 < var8; var9++) {
            if (var0.getStackInSlot(var9) != null) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean captureDroppedItems(IHopper var0) {
      IInventory var4 = getHopperInventory(var0);
      if (var4 != null) {
         EnumFacing var5 = EnumFacing.DOWN;
         if (isInventoryEmpty(var4, var5)) {
            return false;
         }

         if (var4 instanceof ISidedInventory) {
            ISidedInventory var6 = (ISidedInventory)var4;
            int[] var7 = var6.getSlotsForFace(var5);

            for (int var8 = 0; var8 < var7.length; var8++) {
               if (pullItemFromSlot(var0, var4, var7[var8], var5)) {
                  return true;
               }
            }
         } else {
            int var10 = var4.getSizeInventory();

            for (int var12 = 0; var12 < var10; var12++) {
               if (pullItemFromSlot(var0, var4, var12, var5)) {
                  return true;
               }
            }
         }
      } else {
         for (EntityItem var11 : func_181556_a(var0.getWorld(), var0.getXPos(), var0.getYPos() + 1.0, var0.getZPos())) {
            if (putDropInInventoryAllSlots(var0, var11)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean pullItemFromSlot(IHopper var0, IInventory var1, int var2, EnumFacing var3) {
      ItemStack var7 = var1.getStackInSlot(var2);
      if (var7 != null && canExtractItemFromSlot(var1, var7, var2, var3)) {
         ItemStack var8 = var7.copy();
         ItemStack var9 = putStackInInventoryAllSlots(var0, var1.decrStackSize(var2, 1), (EnumFacing)null);
         if (var9 == null || var9.stackSize == 0) {
            var1.markDirty();
            return true;
         }

         var1.setInventorySlotContents(var2, var8);
      }

      return false;
   }

   public static boolean putDropInInventoryAllSlots(IInventory var0, EntityItem var1) {
      boolean var5 = false;
      if (var1 == null) {
         return false;
      }

      ItemStack var6 = var1.getEntityItem().copy();
      ItemStack var7 = putStackInInventoryAllSlots(var0, var6, (EnumFacing)null);
      if (var7 != null && var7.stackSize != 0) {
         var1.setEntityItemStack(var7);
      } else {
         var5 = true;
         var1.setDead();
      }

      return var5;
   }

   public static ItemStack putStackInInventoryAllSlots(IInventory var0, ItemStack var1, EnumFacing var2) {
      if (var0 instanceof ISidedInventory && var2 != null) {
         ISidedInventory var9 = (ISidedInventory)var0;
         int[] var10 = var9.getSlotsForFace(var2);

         for (int var8 = 0; var8 < var10.length && var1 != null && var1.stackSize > 0; var8++) {
            var1 = insertStack(var0, var1, var10[var8], var2);
         }
      } else {
         int var6 = var0.getSizeInventory();

         for (int var7 = 0; var7 < var6 && var1 != null && var1.stackSize > 0; var7++) {
            var1 = insertStack(var0, var1, var7, var2);
         }
      }

      if (var1 != null && var1.stackSize == 0) {
         var1 = null;
      }

      return var1;
   }

   private static boolean canInsertItemInSlot(IInventory var0, ItemStack var1, int var2, EnumFacing var3) {
      return !var0.isItemValidForSlot(var2, var1) ? false : !(var0 instanceof ISidedInventory) || ((ISidedInventory)var0).canInsertItem(var2, var1, var3);
   }

   private static boolean canExtractItemFromSlot(IInventory var0, ItemStack var1, int var2, EnumFacing var3) {
      return !(var0 instanceof ISidedInventory) || ((ISidedInventory)var0).canExtractItem(var2, var1, var3);
   }

   private static ItemStack insertStack(IInventory var0, ItemStack var1, int var2, EnumFacing var3) {
      ItemStack var7 = var0.getStackInSlot(var2);
      if (canInsertItemInSlot(var0, var1, var2, var3)) {
         boolean var8 = false;
         if (var7 == null) {
            var0.setInventorySlotContents(var2, var1);
            var1 = null;
            var8 = true;
         } else if (canCombine(var7, var1)) {
            int var9 = var1.getMaxStackSize() - var7.stackSize;
            int var10 = Math.min(var1.stackSize, var9);
            var1.stackSize -= var10;
            var7.stackSize += var10;
            var8 = var10 > 0;
         }

         if (var8) {
            if (var0 instanceof TileEntityHopper) {
               TileEntityHopper var11 = (TileEntityHopper)var0;
               if (var11.mayTransfer()) {
                  var11.setTransferCooldown(8);
               }

               var0.markDirty();
            }

            var0.markDirty();
         }
      }

      return var1;
   }

   private IInventory getInventoryForHopperTransfer() {
      EnumFacing var1 = BlockHopper.getFacing(this.getBlockMetadata());
      return getInventoryAtPosition(
         this.getWorld(), this.pos.getX() + var1.getFrontOffsetX(), this.pos.getY() + var1.getFrontOffsetY(), this.pos.getZ() + var1.getFrontOffsetZ()
      );
   }

   public static IInventory getHopperInventory(IHopper var0) {
      return getInventoryAtPosition(var0.getWorld(), var0.getXPos(), var0.getYPos() + 1.0, var0.getZPos());
   }

   public static List<EntityItem> func_181556_a(World var0, double var1, double var3, double var5) {
      return var0.getEntitiesWithinAABB(
         EntityItem.class, new AxisAlignedBB(var1 - 0.5, var3 - 0.5, var5 - 0.5, var1 + 0.5, var3 + 0.5, var5 + 0.5), EntitySelectors.selectAnything
      );
   }

   public static IInventory getInventoryAtPosition(World var0, double var1, double var3, double var5) {
      IInventory var10 = null;
      int var11 = MathHelper.floor_double(var1);
      int var12 = MathHelper.floor_double(var3);
      int var13 = MathHelper.floor_double(var5);
      BlockPos var14 = new BlockPos(var11, var12, var13);
      Block var15 = var0.getBlockState(var14).getBlock();
      if (var15.hasTileEntity()) {
         TileEntity var16 = var0.getTileEntity(var14);
         if (var16 instanceof IInventory) {
            var10 = (IInventory)var16;
            if (var10 instanceof TileEntityChest && var15 instanceof BlockChest) {
               var10 = ((BlockChest)var15).getLockableContainer(var0, var14);
            }
         }
      }

      if (var10 == null) {
         List var17 = var0.getEntitiesInAABBexcluding(
            (Entity)null, new AxisAlignedBB(var1 - 0.5, var3 - 0.5, var5 - 0.5, var1 + 0.5, var3 + 0.5, var5 + 0.5), EntitySelectors.selectInventories
         );
         if (var17.size() > 0) {
            var10 = (IInventory)var17.get(var0.rand.nextInt(var17.size()));
         }
      }

      return var10;
   }

   private static boolean canCombine(ItemStack var0, ItemStack var1) {
      return var0.getItem() != var1.getItem()
         ? false
         : (var0.getMetadata() != var1.getMetadata() ? false : (var0.stackSize > var0.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(var0, var1)));
   }

   @Override
   public double getXPos() {
      return this.pos.getX() + 0.5;
   }

   @Override
   public double getYPos() {
      return this.pos.getY() + 0.5;
   }

   @Override
   public double getZPos() {
      return this.pos.getZ() + 0.5;
   }

   public void setTransferCooldown(int var1) {
      this.transferCooldown = var1;
   }

   public boolean isOnTransferCooldown() {
      return this.transferCooldown > 0;
   }

   public boolean mayTransfer() {
      return this.transferCooldown <= 1;
   }

   @Override
   public String getGuiID() {
      return "minecraft:hopper";
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerHopper(var1, this, var2);
   }

   @Override
   public int getField(int var1) {
      return 0;
   }

   @Override
   public void setField(int var1, int var2) {
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void clear() {
      for (int var4 = 0; var4 < this.inventory.length; var4++) {
         this.inventory[var4] = null;
      }
   }
}
