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
   private static final long a = 9687067109580L;

   @Override
   public int getSizeInventory() {
      return this.furnaceItemStacks.length;
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return this.furnaceItemStacks[var1];
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      if (this.furnaceItemStacks[var1] != null) {
         if (this.furnaceItemStacks[var1].stackSize <= var2) {
            ItemStack var7 = this.furnaceItemStacks[var1];
            this.furnaceItemStacks[var1] = null;
            return var7;
         }

         ItemStack var6 = this.furnaceItemStacks[var1].splitStack(var2);
         if (this.furnaceItemStacks[var1].stackSize == 0) {
            this.furnaceItemStacks[var1] = null;
         }

         return var6;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      if (this.furnaceItemStacks[var1] != null) {
         ItemStack var5 = this.furnaceItemStacks[var1];
         this.furnaceItemStacks[var1] = null;
         return var5;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      boolean var6 = var2 != null && var2.isItemEqual(this.furnaceItemStacks[var1]) && ItemStack.areItemStackTagsEqual(var2, this.furnaceItemStacks[var1]);
      this.furnaceItemStacks[var1] = var2;
      if (var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
         var2.stackSize = this.getInventoryStackLimit();
      }

      if (var1 == 0 && !var6) {
         this.totalCookTime = this.getCookTime(var2);
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

   public void setCustomInventoryName(String var1) {
      this.furnaceCustomName = var1;
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      NBTTagList var5 = var1.getTagList("Items", 10);
      this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

      for (int var6 = 0; var6 < var5.tagCount(); var6++) {
         NBTTagCompound var7 = var5.getCompoundTagAt(var6);
         byte var8 = var7.getByte("Slot");
         if (var8 >= 0 && var8 < this.furnaceItemStacks.length) {
            this.furnaceItemStacks[var8] = ItemStack.loadItemStackFromNBT(var7);
         }
      }

      this.furnaceBurnTime = var1.getShort("BurnTime");
      this.cookTime = var1.getShort("CookTime");
      this.totalCookTime = var1.getShort("CookTimeTotal");
      this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
      if (var1.hasKey("CustomName", 8)) {
         this.furnaceCustomName = var1.getString("CustomName");
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      var1.setShort("BurnTime", (short)this.furnaceBurnTime);
      var1.setShort("CookTime", (short)this.cookTime);
      var1.setShort("CookTimeTotal", (short)this.totalCookTime);
      NBTTagList var5 = new NBTTagList();

      for (int var6 = 0; var6 < this.furnaceItemStacks.length; var6++) {
         if (this.furnaceItemStacks[var6] != null) {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setByte("Slot", (byte)var6);
            this.furnaceItemStacks[var6].writeToNBT(var7);
            var5.appendTag(var7);
         }
      }

      var1.setTag("Items", var5);
      if (this.hasCustomName()) {
         var1.setString("CustomName", this.furnaceCustomName);
      }
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean Y() {
      return this.furnaceBurnTime > 0;
   }

   public static boolean isBurning(IInventory var0) {
      return var0.getField(0) > 0;
   }

   @Override
   public void update() {
      boolean var4 = this.Y();
      boolean var5 = false;
      if (this.Y()) {
         this.furnaceBurnTime--;
      }

      if (!this.worldObj.isRemote) {
         if (this.Y() || this.furnaceItemStacks[1] != null && this.furnaceItemStacks[0] != null) {
            if (!this.Y() && this.P()) {
               this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
               if (this.Y()) {
                  var5 = true;
                  if (this.furnaceItemStacks[1] != null) {
                     this.furnaceItemStacks[1].stackSize--;
                     if (this.furnaceItemStacks[1].stackSize == 0) {
                        Item var6 = this.furnaceItemStacks[1].getItem().getContainerItem();
                        this.furnaceItemStacks[1] = var6 != null ? new ItemStack(var6) : null;
                     }
                  }
               }
            }

            if (this.Y() && this.P()) {
               this.cookTime++;
               if (this.cookTime == this.totalCookTime) {
                  this.cookTime = 0;
                  this.totalCookTime = this.getCookTime(this.furnaceItemStacks[0]);
                  this.y();
                  var5 = true;
               }
            } else {
               this.cookTime = 0;
            }
         } else if (!this.Y() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp_int(this.cookTime - 2, 0, this.totalCookTime);
         }

         if (var4 != this.Y()) {
            var5 = true;
            BlockFurnace.setState(this.Y(), this.worldObj, this.pos);
         }
      }

      if (var5) {
         this.markDirty();
      }
   }

   public int getCookTime(ItemStack var1) {
      return 200;
   }

   private boolean P() {
      if (this.furnaceItemStacks[0] == null) {
         return false;
      }

      ItemStack var4 = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[0]);
      return var4 == null
         ? false
         : (
            this.furnaceItemStacks[2] == null
               ? true
               : (
                  !this.furnaceItemStacks[2].isItemEqual(var4)
                     ? false
                     : (
                        this.furnaceItemStacks[2].stackSize < this.getInventoryStackLimit()
                              && this.furnaceItemStacks[2].stackSize < this.furnaceItemStacks[2].getMaxStackSize()
                           ? true
                           : this.furnaceItemStacks[2].stackSize < var4.getMaxStackSize()
                     )
               )
         );
   }

   public void y() {
      if (this.P()) {
         ItemStack var4 = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[0]);
         if (this.furnaceItemStacks[2] == null) {
            this.furnaceItemStacks[2] = var4.copy();
         } else if (this.furnaceItemStacks[2].getItem() == var4.getItem()) {
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

   public static int getItemBurnTime(ItemStack var0) {
      if (var0 == null) {
         return 0;
      }

      Item var4 = var0.getItem();
      if (var4 instanceof ItemBlock && Block.getBlockFromItem(var4) != Blocks.air) {
         Block var5 = Block.getBlockFromItem(var4);
         if (var5 == Blocks.wooden_slab) {
            return 150;
         }

         if (var5.getMaterial() == Material.wood) {
            return 300;
         }

         if (var5 == Blocks.coal_block) {
            return 16000;
         }
      }

      return var4 instanceof ItemTool && ((ItemTool)var4).getToolMaterialName().equals("WOOD")
         ? 200
         : (
            var4 instanceof ItemSword && ((ItemSword)var4).getToolMaterialName().equals("WOOD")
               ? 200
               : (
                  var4 instanceof ItemHoe && ((ItemHoe)var4).getMaterialName().equals("WOOD")
                     ? 200
                     : (
                        var4 == Items.stick
                           ? 100
                           : (
                              var4 == Items.coal
                                 ? 1600
                                 : (
                                    var4 == Items.lava_bucket
                                       ? 20000
                                       : (var4 == Item.getItemFromBlock(Blocks.sapling) ? 100 : (var4 == Items.blaze_rod ? 2400 : 0))
                                 )
                           )
                     )
               )
         );
   }

   public static boolean isItemFuel(ItemStack var0) {
      return getItemBurnTime(var0) > 0;
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
      return var1 == 2 ? false : (var1 != 1 ? true : isItemFuel(var2) || SlotFurnaceFuel.isBucket(var2));
   }

   @Override
   public int[] getSlotsForFace(EnumFacing var1) {
      return var1 == EnumFacing.DOWN ? slotsBottom : (var1 == EnumFacing.UP ? slotsTop : slotsSides);
   }

   @Override
   public boolean canInsertItem(int var1, ItemStack var2, EnumFacing var3) {
      return this.isItemValidForSlot(var1, var2);
   }

   @Override
   public boolean canExtractItem(int var1, ItemStack var2, EnumFacing var3) {
      if (var3 == EnumFacing.DOWN && var1 == 1) {
         Item var7 = var2.getItem();
         if (var7 != Items.water_bucket && var7 != Items.bucket) {
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
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerFurnace(var1, this);
   }

   @Override
   public int getField(int var1) {
      switch (var1) {
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
   public void setField(int var1, int var2) {
      switch (var1) {
         case 0:
            this.furnaceBurnTime = var2;
            break;
         case 1:
            this.currentItemBurnTime = var2;
            break;
         case 2:
            this.cookTime = var2;
            break;
         case 3:
            this.totalCookTime = var2;
      }
   }

   @Override
   public int getFieldCount() {
      return 4;
   }

   @Override
   public void clear() {
      for (int var4 = 0; var4 < this.furnaceItemStacks.length; var4++) {
         this.furnaceItemStacks[var4] = null;
      }
   }
}
