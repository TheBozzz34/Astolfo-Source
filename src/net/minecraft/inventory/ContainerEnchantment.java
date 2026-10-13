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
      private static final long a = 80269045990230L;
      private static final long c = 8885746788019994688L;

      @Override
      public int getInventoryStackLimit() {
         return (int)c;
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
   private static final long a = 132109630087024L;

   public ContainerEnchantment(InventoryPlayer var1, World var2) {
      this(var1, var2, BlockPos.ORIGIN);
   }

   public ContainerEnchantment(InventoryPlayer var1, World var2, BlockPos var3) {
      this.worldPointer = var2;
      this.position = var3;
      this.xpSeed = var1.player.getXPSeed();
      this.addSlotToContainer(new Slot(this.tableInventory, 0, 15, 47) {
         @Override
         public boolean isItemValid(ItemStack var1) {
            return true;
         }

         @Override
         public int getSlotStackLimit() {
            return 1;
         }
      });
      this.addSlotToContainer(new Slot(this.tableInventory, 1, 35, 47) {
         private static final long a = 135605819168633L;

         @Override
         public boolean isItemValid(ItemStack var1) {
            return var1.getItem() == Items.dye && EnumDyeColor.byDyeDamage(var1.getMetadata()) == EnumDyeColor.BLUE;
         }
      });

      for (int var8 = 0; var8 < 3; var8++) {
         for (int var9 = 0; var9 < 9; var9++) {
            this.addSlotToContainer(new Slot(var1, var9 + var8 * 9 + 9, 8 + var9 * 18, 84 + var8 * 18));
         }
      }

      for (int var10 = 0; var10 < 9; var10++) {
         this.addSlotToContainer(new Slot(var1, var10, 8 + var10 * 18, 142));
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting var1) {
      super.onCraftGuiOpened(var1);
      var1.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
      var1.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
      var1.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
      var1.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
      var1.sendProgressBarUpdate(this, 4, this.enchantmentIds[0]);
      var1.sendProgressBarUpdate(this, 5, this.enchantmentIds[1]);
      var1.sendProgressBarUpdate(this, 6, this.enchantmentIds[2]);
   }

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for (int var4 = 0; var4 < this.crafters.size(); var4++) {
         ICrafting var5 = this.crafters.get(var4);
         var5.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
         var5.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
         var5.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
         var5.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
         var5.sendProgressBarUpdate(this, 4, this.enchantmentIds[0]);
         var5.sendProgressBarUpdate(this, 5, this.enchantmentIds[1]);
         var5.sendProgressBarUpdate(this, 6, this.enchantmentIds[2]);
      }
   }

   @Override
   public void updateProgressBar(int var1, int var2) {
      if (var1 >= 0 && var1 <= 2) {
         this.enchantLevels[var1] = var2;
      } else if (var1 == 3) {
         this.xpSeed = var2;
      } else if (var1 >= 4 && var1 <= 6) {
         this.enchantmentIds[var1 - 4] = var2;
      } else {
         super.updateProgressBar(var1, var2);
      }
   }

   @Override
   public void onCraftMatrixChanged(IInventory var1) {
      if (var1 == this.tableInventory) {
         ItemStack var5 = var1.getStackInSlot(0);
         if (var5 != null && var5.isItemEnchantable()) {
            if (!this.worldPointer.isRemote) {
               int var10 = 0;

               for (int var7 = -1; var7 <= 1; var7++) {
                  for (int var8 = -1; var8 <= 1; var8++) {
                     if ((var7 != 0 || var8 != 0)
                        && this.worldPointer.isAirBlock(this.position.add(var8, 0, var7))
                        && this.worldPointer.isAirBlock(this.position.add(var8, 1, var7))) {
                        if (this.worldPointer.getBlockState(this.position.add(var8 * 2, 0, var7 * 2)).getBlock() == Blocks.bookshelf) {
                           var10++;
                        }

                        if (this.worldPointer.getBlockState(this.position.add(var8 * 2, 1, var7 * 2)).getBlock() == Blocks.bookshelf) {
                           var10++;
                        }

                        if (var8 != 0 && var7 != 0) {
                           if (this.worldPointer.getBlockState(this.position.add(var8 * 2, 0, var7)).getBlock() == Blocks.bookshelf) {
                              var10++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(var8 * 2, 1, var7)).getBlock() == Blocks.bookshelf) {
                              var10++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(var8, 0, var7 * 2)).getBlock() == Blocks.bookshelf) {
                              var10++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(var8, 1, var7 * 2)).getBlock() == Blocks.bookshelf) {
                              var10++;
                           }
                        }
                     }
                  }
               }

               this.rand.setSeed(this.xpSeed);

               for (int var11 = 0; var11 < 3; var11++) {
                  this.enchantLevels[var11] = EnchantmentHelper.calcItemStackEnchantability(this.rand, var11, var10, var5);
                  this.enchantmentIds[var11] = -1;
                  if (this.enchantLevels[var11] < var11 + 1) {
                     this.enchantLevels[var11] = 0;
                  }
               }

               for (int var12 = 0; var12 < 3; var12++) {
                  if (this.enchantLevels[var12] > 0) {
                     List var13 = this.func_178148_a(var5, var12, this.enchantLevels[var12]);
                     if (var13 != null && !var13.isEmpty()) {
                        EnchantmentData var9 = (EnchantmentData)var13.get(this.rand.nextInt(var13.size()));
                        this.enchantmentIds[var12] = var9.enchantmentobj.effectId | var9.enchantmentLevel << 8;
                     }
                  }
               }

               this.detectAndSendChanges();
            }
         } else {
            for (int var6 = 0; var6 < 3; var6++) {
               this.enchantLevels[var6] = 0;
               this.enchantmentIds[var6] = -1;
            }
         }
      }
   }

   @Override
   public boolean enchantItem(EntityPlayer var1, int var2) {
      ItemStack var6 = this.tableInventory.getStackInSlot(0);
      ItemStack var7 = this.tableInventory.getStackInSlot(1);
      int var8 = var2 + 1;
      if ((var7 == null || var7.stackSize < var8) && !var1.capabilities.isCreativeMode) {
         return false;
      }

      if (this.enchantLevels[var2] > 0
         && var6 != null
         && (var1.experienceLevel >= var8 && var1.experienceLevel >= this.enchantLevels[var2] || var1.capabilities.isCreativeMode)) {
         if (!this.worldPointer.isRemote) {
            List var9 = this.func_178148_a(var6, var2, this.enchantLevels[var2]);
            boolean var10 = var6.getItem() == Items.book;
            if (var9 != null) {
               var1.removeExperienceLevel(var8);
               if (var10) {
                  var6.setItem(Items.enchanted_book);
               }

               for (int var11 = 0; var11 < var9.size(); var11++) {
                  EnchantmentData var12 = (EnchantmentData)var9.get(var11);
                  if (var10) {
                     Items.enchanted_book.addEnchantment(var6, var12);
                  } else {
                     var6.addEnchantment(var12.enchantmentobj, var12.enchantmentLevel);
                  }
               }

               if (!var1.capabilities.isCreativeMode) {
                  var7.stackSize -= var8;
                  if (var7.stackSize <= 0) {
                     this.tableInventory.setInventorySlotContents(1, (ItemStack)null);
                  }
               }

               var1.triggerAchievement(StatList.field_181739_W);
               this.tableInventory.markDirty();
               this.xpSeed = var1.getXPSeed();
               this.onCraftMatrixChanged(this.tableInventory);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private List<EnchantmentData> func_178148_a(ItemStack var1, int var2, int var3) {
      this.rand.setSeed(this.xpSeed + var2);
      List var7 = EnchantmentHelper.buildEnchantmentList(this.rand, var1, var3);
      if (var1.getItem() == Items.book && var7 != null && var7.size() > 1) {
         var7.remove(this.rand.nextInt(var7.size()));
      }

      return var7;
   }

   public int getLapisAmount() {
      ItemStack var4 = this.tableInventory.getStackInSlot(1);
      return var4 == null ? 0 : var4.stackSize;
   }

   @Override
   public void onContainerClosed(EntityPlayer var1) {
      super.onContainerClosed(var1);
      if (!this.worldPointer.isRemote) {
         for (int var5 = 0; var5 < this.tableInventory.getSizeInventory(); var5++) {
            ItemStack var6 = this.tableInventory.removeStackFromSlot(var5);
            if (var6 != null) {
               var1.dropPlayerItemWithRandomChoice(var6, false);
            }
         }
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer var1) {
      return this.worldPointer.getBlockState(this.position).getBlock() != Blocks.enchanting_table
         ? false
         : var1.getDistanceSq(this.position.getX() + 0.5, this.position.getY() + 0.5, this.position.getZ() + 0.5) <= 64.0;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var6 = null;
      Slot var7 = this.inventorySlots.get(var2);
      if (var7 != null && var7.getHasStack()) {
         ItemStack var8 = var7.getStack();
         var6 = var8.copy();
         if (var2 == 0) {
            if (!this.mergeItemStack(var8, 2, 38, true)) {
               return null;
            }
         } else if (var2 == 1) {
            if (!this.mergeItemStack(var8, 2, 38, true)) {
               return null;
            }
         } else if (var8.getItem() == Items.dye && EnumDyeColor.byDyeDamage(var8.getMetadata()) == EnumDyeColor.BLUE) {
            if (!this.mergeItemStack(var8, 1, 2, true)) {
               return null;
            }
         } else {
            if (this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(var8)) {
               return null;
            }

            if (var8.hasTagCompound() && var8.stackSize == 1) {
               this.inventorySlots.get(0).putStack(var8.copy());
               var8.stackSize = 0;
            } else if (var8.stackSize >= 1) {
               this.inventorySlots.get(0).putStack(new ItemStack(var8.getItem(), 1, var8.getMetadata()));
               var8.stackSize--;
            }
         }

         if (var8.stackSize == 0) {
            var7.putStack((ItemStack)null);
         } else {
            var7.onSlotChanged();
         }

         if (var8.stackSize == var6.stackSize) {
            return null;
         }

         var7.onPickupFromSlot(var1, var8);
      }

      return var6;
   }
}
