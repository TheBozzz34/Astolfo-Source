package net.minecraft.tileentity;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityBrewingStand extends TileEntityLockable implements ITickable, ISidedInventory {
   private static final int[] inputSlots = new int[]{3};
   private static final int[] outputSlots = new int[]{0, 1, 2};
   private ItemStack[] brewingItemStacks = new ItemStack[4];
   private int brewTime;
   private boolean[] filledSlots;
   private Item ingredientID;
   private String customName;
   private static final long a = 134992188778027L;

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.brewing";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setName(String var1) {
      this.customName = var1;
   }

   @Override
   public int getSizeInventory() {
      return this.brewingItemStacks.length;
   }

   @Override
   public void update() {
      if (this.brewTime > 0) {
         this.brewTime--;
         if (this.brewTime == 0) {
            this.brewPotions();
            this.markDirty();
         } else if (!this.canBrew()) {
            this.brewTime = 0;
            this.markDirty();
         } else if (this.ingredientID != this.brewingItemStacks[3].getItem()) {
            this.brewTime = 0;
            this.markDirty();
         }
      } else if (this.canBrew()) {
         this.brewTime = 400;
         this.ingredientID = this.brewingItemStacks[3].getItem();
      }

      if (!this.worldObj.isRemote) {
         boolean[] var4 = this.func_174902_m();
         if (!Arrays.equals(var4, this.filledSlots)) {
            this.filledSlots = var4;
            IBlockState var5 = this.worldObj.getBlockState(this.getPos());
            if (!(var5.getBlock() instanceof BlockBrewingStand)) {
               return;
            }

            for (int var6 = 0; var6 < BlockBrewingStand.HAS_BOTTLE.length; var6++) {
               var5 = var5.withProperty(BlockBrewingStand.HAS_BOTTLE[var6], var4[var6]);
            }

            this.worldObj.setBlockState(this.pos, var5, 2);
         }
      }
   }

   private boolean canBrew() {
      if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0) {
         ItemStack var4 = this.brewingItemStacks[3];
         if (!var4.getItem().isPotionIngredient(var4)) {
            return false;
         }

         boolean var5 = false;

         for (int var6 = 0; var6 < 3; var6++) {
            if (this.brewingItemStacks[var6] != null && this.brewingItemStacks[var6].getItem() == Items.potionitem) {
               int var7 = this.brewingItemStacks[var6].getMetadata();
               int var8 = this.getPotionResult(var7, var4);
               if (!ItemPotion.isSplash(var7) && ItemPotion.isSplash(var8)) {
                  var5 = true;
                  break;
               }

               List var9 = Items.potionitem.getEffects(var7);
               List var10 = Items.potionitem.getEffects(var8);
               if ((var7 <= 0 || var9 != var10) && (var9 == null || !var9.equals(var10) && var10 != null) && var7 != var8) {
                  var5 = true;
                  break;
               }
            }
         }

         return var5;
      } else {
         return false;
      }
   }

   private void brewPotions() {
      if (this.canBrew()) {
         ItemStack var4 = this.brewingItemStacks[3];

         for (int var5 = 0; var5 < 3; var5++) {
            if (this.brewingItemStacks[var5] != null && this.brewingItemStacks[var5].getItem() == Items.potionitem) {
               int var6 = this.brewingItemStacks[var5].getMetadata();
               int var7 = this.getPotionResult(var6, var4);
               List var8 = Items.potionitem.getEffects(var6);
               List var9 = Items.potionitem.getEffects(var7);
               if (var6 > 0 && var8 == var9 || var8 != null && (var8.equals(var9) || var9 == null)) {
                  if (!ItemPotion.isSplash(var6) && ItemPotion.isSplash(var7)) {
                     this.brewingItemStacks[var5].setItemDamage(var7);
                  }
               } else if (var6 != var7) {
                  this.brewingItemStacks[var5].setItemDamage(var7);
               }
            }
         }

         if (var4.getItem().hasContainerItem()) {
            this.brewingItemStacks[3] = new ItemStack(var4.getItem().getContainerItem());
         } else {
            this.brewingItemStacks[3].stackSize--;
            if (this.brewingItemStacks[3].stackSize <= 0) {
               this.brewingItemStacks[3] = null;
            }
         }
      }
   }

   private int getPotionResult(int var1, ItemStack var2) {
      return var2 == null ? var1 : (var2.getItem().isPotionIngredient(var2) ? PotionHelper.applyIngredient(var1, var2.getItem().getPotionEffect(var2)) : var1);
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      NBTTagList var5 = var1.getTagList("Items", 10);
      this.brewingItemStacks = new ItemStack[this.getSizeInventory()];

      for (int var6 = 0; var6 < var5.tagCount(); var6++) {
         NBTTagCompound var7 = var5.getCompoundTagAt(var6);
         byte var8 = var7.getByte("Slot");
         if (var8 >= 0 && var8 < this.brewingItemStacks.length) {
            this.brewingItemStacks[var8] = ItemStack.loadItemStackFromNBT(var7);
         }
      }

      this.brewTime = var1.getShort("BrewTime");
      if (var1.hasKey("CustomName", 8)) {
         this.customName = var1.getString("CustomName");
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      var1.setShort("BrewTime", (short)this.brewTime);
      NBTTagList var5 = new NBTTagList();

      for (int var6 = 0; var6 < this.brewingItemStacks.length; var6++) {
         if (this.brewingItemStacks[var6] != null) {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setByte("Slot", (byte)var6);
            this.brewingItemStacks[var6].writeToNBT(var7);
            var5.appendTag(var7);
         }
      }

      var1.setTag("Items", var5);
      if (this.hasCustomName()) {
         var1.setString("CustomName", this.customName);
      }
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return var1 >= 0 && var1 < this.brewingItemStacks.length ? this.brewingItemStacks[var1] : null;
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      if (var1 >= 0 && var1 < this.brewingItemStacks.length) {
         ItemStack var6 = this.brewingItemStacks[var1];
         this.brewingItemStacks[var1] = null;
         return var6;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      if (var1 >= 0 && var1 < this.brewingItemStacks.length) {
         ItemStack var5 = this.brewingItemStacks[var1];
         this.brewingItemStacks[var1] = null;
         return var5;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      if (var1 >= 0 && var1 < this.brewingItemStacks.length) {
         this.brewingItemStacks[var1] = var2;
      }
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
      return var1 == 3 ? var2.getItem().isPotionIngredient(var2) : var2.getItem() == Items.potionitem || var2.getItem() == Items.glass_bottle;
   }

   public boolean[] func_174902_m() {
      boolean[] var4 = new boolean[3];

      for (int var5 = 0; var5 < 3; var5++) {
         if (this.brewingItemStacks[var5] != null) {
            var4[var5] = true;
         }
      }

      return var4;
   }

   @Override
   public int[] getSlotsForFace(EnumFacing var1) {
      return var1 == EnumFacing.UP ? inputSlots : outputSlots;
   }

   @Override
   public boolean canInsertItem(int var1, ItemStack var2, EnumFacing var3) {
      return this.isItemValidForSlot(var1, var2);
   }

   @Override
   public boolean canExtractItem(int var1, ItemStack var2, EnumFacing var3) {
      return true;
   }

   @Override
   public String getGuiID() {
      return "minecraft:brewing_stand";
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerBrewingStand(var1, this);
   }

   @Override
   public int getField(int var1) {
      switch (var1) {
         case 0:
            return this.brewTime;
         default:
            return 0;
      }
   }

   @Override
   public void setField(int var1, int var2) {
      switch (var1) {
         case 0:
            this.brewTime = var2;
      }
   }

   @Override
   public int getFieldCount() {
      return 1;
   }

   @Override
   public void clear() {
      for (int var4 = 0; var4 < this.brewingItemStacks.length; var4++) {
         this.brewingItemStacks[var4] = null;
      }
   }
}
