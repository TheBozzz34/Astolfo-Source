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

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagList nbtTagList = compound.getTagList("Items", 10);
      this.inventory = new ItemStack[this.getSizeInventory()];
      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }

      this.transferCooldown = compound.getInteger("TransferCooldown");

      for (int i = 0; i < nbtTagList.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
         byte b = nbtTagCompound.getByte("Slot");
         if (b >= 0 && b < this.inventory.length) {
            this.inventory[b] = ItemStack.loadItemStackFromNBT(nbtTagCompound);
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      NBTTagList nbtTagList = new NBTTagList();

      for (int i = 0; i < this.inventory.length; i++) {
         if (this.inventory[i] != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)i);
            this.inventory[i].writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      compound.setTag("Items", nbtTagList);
      compound.setInteger("TransferCooldown", this.transferCooldown);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
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
   public ItemStack getStackInSlot(int index) {
      return this.inventory[index];
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.inventory[index] != null) {
         if (this.inventory[index].stackSize <= count) {
            ItemStack itemStack = this.inventory[index];
            this.inventory[index] = null;
            return itemStack;
         }

         ItemStack itemStack2 = this.inventory[index].splitStack(count);
         if (this.inventory[index].stackSize == 0) {
            this.inventory[index] = null;
         }

         return itemStack2;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.inventory[index] != null) {
         ItemStack itemStack = this.inventory[index];
         this.inventory[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.inventory[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
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

   public void setCustomName(String customNameIn) {
      this.customName = customNameIn;
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void openInventory(EntityPlayer player) {
   }

   @Override
   public void closeInventory(EntityPlayer player) {
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
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
            boolean bl = false;
            if (!this.isEmpty()) {
               bl = this.transferItemsOut();
            }

            if (!this.isFull()) {
               bl = captureDroppedItems(this) || bl;
            }

            if (bl) {
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
      for (ItemStack itemStack : this.inventory) {
         if (itemStack != null) {
            return false;
         }
      }

      return true;
   }

   private boolean isFull() {
      for (ItemStack itemStack : this.inventory) {
         if (itemStack == null || itemStack.stackSize != itemStack.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   private boolean transferItemsOut() {
      IInventory iInventory = this.getInventoryForHopperTransfer();
      if (iInventory == null) {
         return false;
      }

      EnumFacing enumFacing = BlockHopper.getFacing(this.getBlockMetadata()).getOpposite();
      if (this.isInventoryFull(iInventory, enumFacing)) {
         return false;
      }

      for (int i = 0; i < this.getSizeInventory(); i++) {
         if (this.getStackInSlot(i) != null) {
            ItemStack itemStack = this.getStackInSlot(i).copy();
            ItemStack itemStack2 = putStackInInventoryAllSlots(iInventory, this.decrStackSize(i, 1), enumFacing);
            if (itemStack2 == null || itemStack2.stackSize == 0) {
               iInventory.markDirty();
               return true;
            }

            this.setInventorySlotContents(i, itemStack);
         }
      }

      return false;
   }

   private boolean isInventoryFull(IInventory inventoryIn, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory) {
         ISidedInventory iSidedInventory = (ISidedInventory)inventoryIn;
         int[] ints = iSidedInventory.getSlotsForFace(side);

         for (int i = 0; i < ints.length; i++) {
            ItemStack itemStack = iSidedInventory.getStackInSlot(ints[i]);
            if (itemStack == null || itemStack.stackSize != itemStack.getMaxStackSize()) {
               return false;
            }
         }
      } else {
         int j = inventoryIn.getSizeInventory();

         for (int k = 0; k < j; k++) {
            ItemStack itemStack2 = inventoryIn.getStackInSlot(k);
            if (itemStack2 == null || itemStack2.stackSize != itemStack2.getMaxStackSize()) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory) {
         ISidedInventory iSidedInventory = (ISidedInventory)inventoryIn;
         int[] ints = iSidedInventory.getSlotsForFace(side);

         for (int i = 0; i < ints.length; i++) {
            if (iSidedInventory.getStackInSlot(ints[i]) != null) {
               return false;
            }
         }
      } else {
         int j = inventoryIn.getSizeInventory();

         for (int k = 0; k < j; k++) {
            if (inventoryIn.getStackInSlot(k) != null) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean captureDroppedItems(IHopper iHopper) {
      IInventory iInventory = getHopperInventory(iHopper);
      if (iInventory != null) {
         EnumFacing enumFacing = EnumFacing.DOWN;
         if (isInventoryEmpty(iInventory, enumFacing)) {
            return false;
         }

         if (iInventory instanceof ISidedInventory) {
            ISidedInventory iSidedInventory = (ISidedInventory)iInventory;
            int[] ints = iSidedInventory.getSlotsForFace(enumFacing);

            for (int i = 0; i < ints.length; i++) {
               if (pullItemFromSlot(iHopper, iInventory, ints[i], enumFacing)) {
                  return true;
               }
            }
         } else {
            int j = iInventory.getSizeInventory();

            for (int k = 0; k < j; k++) {
               if (pullItemFromSlot(iHopper, iInventory, k, enumFacing)) {
                  return true;
               }
            }
         }
      } else {
         for (EntityItem entityItem : func_181556_a(iHopper.getWorld(), iHopper.getXPos(), iHopper.getYPos() + 1.0, iHopper.getZPos())) {
            if (putDropInInventoryAllSlots(iHopper, entityItem)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, EnumFacing direction) {
      ItemStack itemStack = inventoryIn.getStackInSlot(index);
      if (itemStack != null && canExtractItemFromSlot(inventoryIn, itemStack, index, direction)) {
         ItemStack itemStack2 = itemStack.copy();
         ItemStack itemStack3 = putStackInInventoryAllSlots(hopper, inventoryIn.decrStackSize(index, 1), (EnumFacing)null);
         if (itemStack3 == null || itemStack3.stackSize == 0) {
            inventoryIn.markDirty();
            return true;
         }

         inventoryIn.setInventorySlotContents(index, itemStack2);
      }

      return false;
   }

   public static boolean putDropInInventoryAllSlots(IInventory iInventory, EntityItem itemIn) {
      boolean bl = false;
      if (itemIn == null) {
         return false;
      }

      ItemStack itemStack = itemIn.getEntityItem().copy();
      ItemStack itemStack2 = putStackInInventoryAllSlots(iInventory, itemStack, (EnumFacing)null);
      if (itemStack2 != null && itemStack2.stackSize != 0) {
         itemIn.setEntityItemStack(itemStack2);
      } else {
         bl = true;
         itemIn.setDead();
      }

      return bl;
   }

   public static ItemStack putStackInInventoryAllSlots(IInventory inventoryIn, ItemStack stack, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory && side != null) {
         ISidedInventory iSidedInventory = (ISidedInventory)inventoryIn;
         int[] ints = iSidedInventory.getSlotsForFace(side);

         for (int i = 0; i < ints.length && stack != null && stack.stackSize > 0; i++) {
            stack = insertStack(inventoryIn, stack, ints[i], side);
         }
      } else {
         int j = inventoryIn.getSizeInventory();

         for (int k = 0; k < j && stack != null && stack.stackSize > 0; k++) {
            stack = insertStack(inventoryIn, stack, k, side);
         }
      }

      if (stack != null && stack.stackSize == 0) {
         stack = null;
      }

      return stack;
   }

   private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
      return !inventoryIn.isItemValidForSlot(index, stack)
         ? false
         : !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canInsertItem(index, stack, side);
   }

   private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
      return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
   }

   private static ItemStack insertStack(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
      ItemStack itemStack = inventoryIn.getStackInSlot(index);
      if (canInsertItemInSlot(inventoryIn, stack, index, side)) {
         boolean bl = false;
         if (itemStack == null) {
            inventoryIn.setInventorySlotContents(index, stack);
            stack = null;
            bl = true;
         } else if (canCombine(itemStack, stack)) {
            int i = stack.getMaxStackSize() - itemStack.stackSize;
            int j = Math.min(stack.stackSize, i);
            stack.stackSize -= j;
            itemStack.stackSize += j;
            bl = j > 0;
         }

         if (bl) {
            if (inventoryIn instanceof TileEntityHopper) {
               TileEntityHopper tileEntityHopper = (TileEntityHopper)inventoryIn;
               if (tileEntityHopper.mayTransfer()) {
                  tileEntityHopper.setTransferCooldown(8);
               }

               inventoryIn.markDirty();
            }

            inventoryIn.markDirty();
         }
      }

      return stack;
   }

   private IInventory getInventoryForHopperTransfer() {
      EnumFacing enumFacing = BlockHopper.getFacing(this.getBlockMetadata());
      return getInventoryAtPosition(
         this.getWorld(), this.pos.getX() + enumFacing.getFrontOffsetX(), this.pos.getY() + enumFacing.getFrontOffsetY(), this.pos.getZ() + enumFacing.getFrontOffsetZ()
      );
   }

   public static IInventory getHopperInventory(IHopper hopper) {
      return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0, hopper.getZPos());
   }

   public static List<EntityItem> func_181556_a(World world, double d, double e, double d2) {
      return world.getEntitiesWithinAABB(
         EntityItem.class, new AxisAlignedBB(d - 0.5, e - 0.5, d2 - 0.5, d + 0.5, e + 0.5, d2 + 0.5), EntitySelectors.selectAnything
      );
   }

   public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z) {
      IInventory iInventory = null;
      int i = MathHelper.floor_double(x);
      int j = MathHelper.floor_double(y);
      int k = MathHelper.floor_double(z);
      BlockPos blockPos = new BlockPos(i, j, k);
      Block block = worldIn.getBlockState(blockPos).getBlock();
      if (block.hasTileEntity()) {
         TileEntity tileEntity = worldIn.getTileEntity(blockPos);
         if (tileEntity instanceof IInventory) {
            iInventory = (IInventory)tileEntity;
            if (iInventory instanceof TileEntityChest && block instanceof BlockChest) {
               iInventory = ((BlockChest)block).getLockableContainer(worldIn, blockPos);
            }
         }
      }

      if (iInventory == null) {
         List list = worldIn.getEntitiesInAABBexcluding(
            (Entity)null, new AxisAlignedBB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelectors.selectInventories
         );
         if (list.size() > 0) {
            iInventory = (IInventory)list.get(worldIn.rand.nextInt(list.size()));
         }
      }

      return iInventory;
   }

   private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
      return stack1.getItem() != stack2.getItem()
         ? false
         : (
            stack1.getMetadata() != stack2.getMetadata()
               ? false
               : (stack1.stackSize > stack1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2))
         );
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

   public void setTransferCooldown(int ticks) {
      this.transferCooldown = ticks;
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
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerHopper(playerInventory, this, playerIn);
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.inventory.length; i++) {
         this.inventory[i] = null;
      }
   }
}
