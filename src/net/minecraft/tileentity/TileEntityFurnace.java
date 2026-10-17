package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;

public class TileEntityFurnace extends TileEntityLockable implements ITickable, ISidedInventory {
   private static final int[] slotsTop = new int[]{0};
   private static final int[] slotsBottom = new int[]{2, 1};
   private static final int[] slotsSides = new int[]{1};
   private ItemStack[] furnaceItemStacks = new ItemStack[3];
   private int furnaceBurnTime;
   private int currentItemBurnTime;
   private int cookTime;
   private int totalCookTime;
   private String furnaceCustomName;

   @Override
   public int getSizeInventory() {
      return this.furnaceItemStacks.length;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.furnaceItemStacks[index];
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.furnaceItemStacks[index] != null) {
         if (this.furnaceItemStacks[index].stackSize <= count) {
            ItemStack itemStack = this.furnaceItemStacks[index];
            this.furnaceItemStacks[index] = null;
            return itemStack;
         }

         ItemStack itemStack2 = this.furnaceItemStacks[index].splitStack(count);
         if (this.furnaceItemStacks[index].stackSize == 0) {
            this.furnaceItemStacks[index] = null;
         }

         return itemStack2;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.furnaceItemStacks[index] != null) {
         ItemStack itemStack = this.furnaceItemStacks[index];
         this.furnaceItemStacks[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      boolean bl = stack != null && stack.isItemEqual(this.furnaceItemStacks[index]) && ItemStack.areItemStackTagsEqual(stack, this.furnaceItemStacks[index]);
      this.furnaceItemStacks[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }

      if (index == 0 && !bl) {
         this.totalCookTime = this.getCookTime(stack);
         this.cookTime = 0;
         this.markDirty();
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.furnaceCustomName : "container.furnace";
   }

   @Override
   public boolean hasCustomName() {
      return this.furnaceCustomName != null && this.furnaceCustomName.length() > 0;
   }

   public void setCustomInventoryName(String string) {
      this.furnaceCustomName = string;
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagList nbtTagList = compound.getTagList("Items", 10);
      this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

      for (int i = 0; i < nbtTagList.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
         byte b = nbtTagCompound.getByte("Slot");
         if (b >= 0 && b < this.furnaceItemStacks.length) {
            this.furnaceItemStacks[b] = ItemStack.loadItemStackFromNBT(nbtTagCompound);
         }
      }

      this.furnaceBurnTime = compound.getShort("BurnTime");
      this.cookTime = compound.getShort("CookTime");
      this.totalCookTime = compound.getShort("CookTimeTotal");
      this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
      if (compound.hasKey("CustomName", 8)) {
         this.furnaceCustomName = compound.getString("CustomName");
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setShort("BurnTime", (short)this.furnaceBurnTime);
      compound.setShort("CookTime", (short)this.cookTime);
      compound.setShort("CookTimeTotal", (short)this.totalCookTime);
      NBTTagList nbtTagList = new NBTTagList();

      for (int i = 0; i < this.furnaceItemStacks.length; i++) {
         if (this.furnaceItemStacks[i] != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)i);
            this.furnaceItemStacks[i].writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      compound.setTag("Items", nbtTagList);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.furnaceCustomName);
      }
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isBurning() {
      return this.furnaceBurnTime > 0;
   }

   public static boolean isBurning(IInventory iInventory) {
      return iInventory.getField(0) > 0;
   }

   @Override
   public void update() {
      boolean bl = this.isBurning();
      boolean bl2 = false;
      if (this.isBurning()) {
         this.furnaceBurnTime--;
      }

      if (!this.worldObj.isRemote) {
         if (this.isBurning() || this.furnaceItemStacks[1] != null && this.furnaceItemStacks[0] != null) {
            if (!this.isBurning() && this.canSmelt()) {
               this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
               if (this.isBurning()) {
                  bl2 = true;
                  if (this.furnaceItemStacks[1] != null) {
                     this.furnaceItemStacks[1].stackSize--;
                     if (this.furnaceItemStacks[1].stackSize == 0) {
                        Item item = this.furnaceItemStacks[1].getItem().getContainerItem();
                        this.furnaceItemStacks[1] = item != null ? new ItemStack(item) : null;
                     }
                  }
               }
            }

            if (this.isBurning() && this.canSmelt()) {
               this.cookTime++;
               if (this.cookTime == this.totalCookTime) {
                  this.cookTime = 0;
                  this.totalCookTime = this.getCookTime(this.furnaceItemStacks[0]);
                  this.smeltItem();
                  bl2 = true;
               }
            } else {
               this.cookTime = 0;
            }
         } else if (!this.isBurning() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp_int(this.cookTime - 2, 0, this.totalCookTime);
         }

         if (bl != this.isBurning()) {
            bl2 = true;
            BlockFurnace.setState(this.isBurning(), this.worldObj, this.pos);
         }
      }

      if (bl2) {
         this.markDirty();
      }
   }

   public int getCookTime(ItemStack stack) {
      return 200;
   }

   private boolean canSmelt() {
      if (this.furnaceItemStacks[0] == null) {
         return false;
      }

      ItemStack itemStack = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[0]);
      return itemStack == null
         ? false
         : (
            this.furnaceItemStacks[2] == null
               ? true
               : (
                  !this.furnaceItemStacks[2].isItemEqual(itemStack)
                     ? false
                     : (
                        this.furnaceItemStacks[2].stackSize < this.getInventoryStackLimit()
                              && this.furnaceItemStacks[2].stackSize < this.furnaceItemStacks[2].getMaxStackSize()
                           ? true
                           : this.furnaceItemStacks[2].stackSize < itemStack.getMaxStackSize()
                     )
               )
         );
   }

   public void smeltItem() {
      if (this.canSmelt()) {
         ItemStack itemStack = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[0]);
         if (this.furnaceItemStacks[2] == null) {
            this.furnaceItemStacks[2] = itemStack.copy();
         } else if (this.furnaceItemStacks[2].getItem() == itemStack.getItem()) {
            this.furnaceItemStacks[2].stackSize++;
         }

         if (this.furnaceItemStacks[0].getItem() == Item.getItemFromBlock(Blocks.sponge)
            && this.furnaceItemStacks[0].getMetadata() == 1
            && this.furnaceItemStacks[1] != null
            && this.furnaceItemStacks[1].getItem() == Items.bucket) {
            this.furnaceItemStacks[1] = new ItemStack(Items.water_bucket);
         }

         this.furnaceItemStacks[0].stackSize--;
         if (this.furnaceItemStacks[0].stackSize <= 0) {
            this.furnaceItemStacks[0] = null;
         }
      }
   }

   public static int getItemBurnTime(ItemStack itemStack) {
      if (itemStack == null) {
         return 0;
      }

      Item item = itemStack.getItem();
      if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
         Block block = Block.getBlockFromItem(item);
         if (block == Blocks.wooden_slab) {
            return 150;
         }

         if (block.getMaterial() == Material.wood) {
            return 300;
         }

         if (block == Blocks.coal_block) {
            return 16000;
         }
      }

      return item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")
         ? 200
         : (
            item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")
               ? 200
               : (
                  item instanceof ItemHoe && ((ItemHoe)item).getMaterialName().equals("WOOD")
                     ? 200
                     : (
                        item == Items.stick
                           ? 100
                           : (
                              item == Items.coal
                                 ? 1600
                                 : (
                                    item == Items.lava_bucket
                                       ? 20000
                                       : (item == Item.getItemFromBlock(Blocks.sapling) ? 100 : (item == Items.blaze_rod ? 2400 : 0))
                                 )
                           )
                     )
               )
         );
   }

   public static boolean isItemFuel(ItemStack itemStack) {
      return getItemBurnTime(itemStack) > 0;
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
      return index == 2 ? false : (index != 1 ? true : isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack));
   }

   @Override
   public int[] getSlotsForFace(EnumFacing side) {
      return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
   }

   @Override
   public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
      return this.isItemValidForSlot(index, itemStackIn);
   }

   @Override
   public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
      if (direction == EnumFacing.DOWN && index == 1) {
         Item item = stack.getItem();
         if (item != Items.water_bucket && item != Items.bucket) {
            return false;
         }
      }

      return true;
   }

   @Override
   public String getGuiID() {
      return "minecraft:furnace";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerFurnace(playerInventory, this);
   }

   @Override
   public int getField(int id) {
      switch (id) {
         case 0:
            return this.furnaceBurnTime;
         case 1:
            return this.currentItemBurnTime;
         case 2:
            return this.cookTime;
         case 3:
            return this.totalCookTime;
         default:
            return 0;
      }
   }

   @Override
   public void setField(int id, int value) {
      switch (id) {
         case 0:
            this.furnaceBurnTime = value;
            break;
         case 1:
            this.currentItemBurnTime = value;
            break;
         case 2:
            this.cookTime = value;
            break;
         case 3:
            this.totalCookTime = value;
      }
   }

   @Override
   public int getFieldCount() {
      return 4;
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.furnaceItemStacks.length; i++) {
         this.furnaceItemStacks[i] = null;
      }
   }
}
