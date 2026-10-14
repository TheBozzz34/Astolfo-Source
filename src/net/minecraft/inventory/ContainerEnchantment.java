package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerEnchantment extends Container {
   public IInventory tableInventory = new InventoryBasic("Enchant", true, 2) {
      @Override
      public int getInventoryStackLimit() {
         return 64;
      }

      @Override
      public void markDirty() {
         super.markDirty();
         ContainerEnchantment.this.onCraftMatrixChanged(this);
      }
   };
   private World worldPointer;
   private BlockPos position;
   private Random rand = new Random();
   public int xpSeed;
   public int[] enchantLevels = new int[3];
   public int[] enchantmentIds = new int[]{-1, -1, -1};

   public ContainerEnchantment(InventoryPlayer playerInv, World worldIn) {
      this(playerInv, worldIn, BlockPos.ORIGIN);
   }

   public ContainerEnchantment(InventoryPlayer playerInv, World worldIn, BlockPos pos) {
      this.worldPointer = worldIn;
      this.position = pos;
      this.xpSeed = playerInv.player.getXPSeed();
      this.addSlotToContainer(new Slot(this.tableInventory, 0, 15, 47) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return true;
         }

         @Override
         public int getSlotStackLimit() {
            return 1;
         }
      });
      this.addSlotToContainer(new Slot(this.tableInventory, 1, 35, 47) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return stack.getItem() == Items.dye && EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.BLUE;
         }
      });

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int k = 0; k < 9; k++) {
         this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
      listener.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
      listener.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
      listener.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
      listener.sendProgressBarUpdate(this, 4, this.enchantmentIds[0]);
      listener.sendProgressBarUpdate(this, 5, this.enchantmentIds[1]);
      listener.sendProgressBarUpdate(this, 6, this.enchantmentIds[2]);
   }

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for (int i = 0; i < this.crafters.size(); i++) {
         ICrafting iCrafting = this.crafters.get(i);
         iCrafting.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
         iCrafting.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
         iCrafting.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
         iCrafting.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
         iCrafting.sendProgressBarUpdate(this, 4, this.enchantmentIds[0]);
         iCrafting.sendProgressBarUpdate(this, 5, this.enchantmentIds[1]);
         iCrafting.sendProgressBarUpdate(this, 6, this.enchantmentIds[2]);
      }
   }

   @Override
   public void updateProgressBar(int id, int data) {
      if (id >= 0 && id <= 2) {
         this.enchantLevels[id] = data;
      } else if (id == 3) {
         this.xpSeed = data;
      } else if (id >= 4 && id <= 6) {
         this.enchantmentIds[id - 4] = data;
      } else {
         super.updateProgressBar(id, data);
      }
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      if (inventoryIn == this.tableInventory) {
         ItemStack itemStack = inventoryIn.getStackInSlot(0);
         if (itemStack != null && itemStack.isItemEnchantable()) {
            if (!this.worldPointer.isRemote) {
               int i = 0;

               for (int j = -1; j <= 1; j++) {
                  for (int k = -1; k <= 1; k++) {
                     if ((j != 0 || k != 0)
                        && this.worldPointer.isAirBlock(this.position.add(k, 0, j))
                        && this.worldPointer.isAirBlock(this.position.add(k, 1, j))) {
                        if (this.worldPointer.getBlockState(this.position.add(k * 2, 0, j * 2)).getBlock() == Blocks.bookshelf) {
                           i++;
                        }

                        if (this.worldPointer.getBlockState(this.position.add(k * 2, 1, j * 2)).getBlock() == Blocks.bookshelf) {
                           i++;
                        }

                        if (k != 0 && j != 0) {
                           if (this.worldPointer.getBlockState(this.position.add(k * 2, 0, j)).getBlock() == Blocks.bookshelf) {
                              i++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(k * 2, 1, j)).getBlock() == Blocks.bookshelf) {
                              i++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(k, 0, j * 2)).getBlock() == Blocks.bookshelf) {
                              i++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(k, 1, j * 2)).getBlock() == Blocks.bookshelf) {
                              i++;
                           }
                        }
                     }
                  }
               }

               this.rand.setSeed(this.xpSeed);

               for (int l = 0; l < 3; l++) {
                  this.enchantLevels[l] = EnchantmentHelper.calcItemStackEnchantability(this.rand, l, i, itemStack);
                  this.enchantmentIds[l] = -1;
                  if (this.enchantLevels[l] < l + 1) {
                     this.enchantLevels[l] = 0;
                  }
               }

               for (int m = 0; m < 3; m++) {
                  if (this.enchantLevels[m] > 0) {
                     List list = this.func_178148_a(itemStack, m, this.enchantLevels[m]);
                     if (list != null && !list.isEmpty()) {
                        EnchantmentData enchantmentData = (EnchantmentData)list.get(this.rand.nextInt(list.size()));
                        this.enchantmentIds[m] = enchantmentData.enchantmentobj.effectId | enchantmentData.enchantmentLevel << 8;
                     }
                  }
               }

               this.detectAndSendChanges();
            }
         } else {
            for (int n = 0; n < 3; n++) {
               this.enchantLevels[n] = 0;
               this.enchantmentIds[n] = -1;
            }
         }
      }
   }

   @Override
   public boolean enchantItem(EntityPlayer playerIn, int id) {
      ItemStack itemStack = this.tableInventory.getStackInSlot(0);
      ItemStack itemStack2 = this.tableInventory.getStackInSlot(1);
      int i = id + 1;
      if ((itemStack2 == null || itemStack2.stackSize < i) && !playerIn.capabilities.isCreativeMode) {
         return false;
      }

      if (this.enchantLevels[id] > 0
         && itemStack != null
         && (playerIn.experienceLevel >= i && playerIn.experienceLevel >= this.enchantLevels[id] || playerIn.capabilities.isCreativeMode)) {
         if (!this.worldPointer.isRemote) {
            List list = this.func_178148_a(itemStack, id, this.enchantLevels[id]);
            boolean bl = itemStack.getItem() == Items.book;
            if (list != null) {
               playerIn.removeExperienceLevel(i);
               if (bl) {
                  itemStack.setItem(Items.enchanted_book);
               }

               for (int j = 0; j < list.size(); j++) {
                  EnchantmentData enchantmentData = (EnchantmentData)list.get(j);
                  if (bl) {
                     Items.enchanted_book.addEnchantment(itemStack, enchantmentData);
                  } else {
                     itemStack.addEnchantment(enchantmentData.enchantmentobj, enchantmentData.enchantmentLevel);
                  }
               }

               if (!playerIn.capabilities.isCreativeMode) {
                  itemStack2.stackSize -= i;
                  if (itemStack2.stackSize <= 0) {
                     this.tableInventory.setInventorySlotContents(1, (ItemStack)null);
                  }
               }

               playerIn.triggerAchievement(StatList.field_181739_W);
               this.tableInventory.markDirty();
               this.xpSeed = playerIn.getXPSeed();
               this.onCraftMatrixChanged(this.tableInventory);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private List<EnchantmentData> func_178148_a(ItemStack stack, int i, int j) {
      this.rand.setSeed(this.xpSeed + i);
      List list = EnchantmentHelper.buildEnchantmentList(this.rand, stack, j);
      if (stack.getItem() == Items.book && list != null && list.size() > 1) {
         list.remove(this.rand.nextInt(list.size()));
      }

      return list;
   }

   public int getLapisAmount() {
      ItemStack itemStack = this.tableInventory.getStackInSlot(1);
      return itemStack == null ? 0 : itemStack.stackSize;
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!this.worldPointer.isRemote) {
         for (int i = 0; i < this.tableInventory.getSizeInventory(); i++) {
            ItemStack itemStack = this.tableInventory.removeStackFromSlot(i);
            if (itemStack != null) {
               playerIn.dropPlayerItemWithRandomChoice(itemStack, false);
            }
         }
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.worldPointer.getBlockState(this.position).getBlock() != Blocks.enchanting_table
         ? false
         : playerIn.getDistanceSq(this.position.getX() + 0.5, this.position.getY() + 0.5, this.position.getZ() + 0.5) <= 64.0;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index == 0) {
            if (!this.mergeItemStack(itemStack2, 2, 38, true)) {
               return null;
            }
         } else if (index == 1) {
            if (!this.mergeItemStack(itemStack2, 2, 38, true)) {
               return null;
            }
         } else if (itemStack2.getItem() == Items.dye && EnumDyeColor.byDyeDamage(itemStack2.getMetadata()) == EnumDyeColor.BLUE) {
            if (!this.mergeItemStack(itemStack2, 1, 2, true)) {
               return null;
            }
         } else {
            if (this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(itemStack2)) {
               return null;
            }

            if (itemStack2.hasTagCompound() && itemStack2.stackSize == 1) {
               this.inventorySlots.get(0).putStack(itemStack2.copy());
               itemStack2.stackSize = 0;
            } else if (itemStack2.stackSize >= 1) {
               this.inventorySlots.get(0).putStack(new ItemStack(itemStack2.getItem(), 1, itemStack2.getMetadata()));
               itemStack2.stackSize--;
            }
         }

         if (itemStack2.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }

         if (itemStack2.stackSize == itemStack.stackSize) {
            return null;
         }

         slot.onPickupFromSlot(playerIn, itemStack2);
      }

      return itemStack;
   }
}
