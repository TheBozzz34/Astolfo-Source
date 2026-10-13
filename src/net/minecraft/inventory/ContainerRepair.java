package net.minecraft.inventory;

import java.util.Map;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container {
   private static final Logger logger = LogManager.getLogger();
   private IInventory outputSlot = new InventoryCraftResult();
   private IInventory inputSlots = new InventoryBasic("Repair", true, 2) {
      @Override
      public void markDirty() {
         super.markDirty();
         ContainerRepair.this.onCraftMatrixChanged(this);
      }
   };
   private World theWorld;
   private BlockPos selfPosition;
   public int maximumCost;
   private int materialCost;
   private String repairedItemName;
   private final EntityPlayer thePlayer;
   private static final long b = 117670932480661L;

   public ContainerRepair(InventoryPlayer var1, World var2, EntityPlayer var3) {
      this(var1, var2, BlockPos.ORIGIN, var3);
   }

   public ContainerRepair(InventoryPlayer var1, final World var2, final BlockPos var3, EntityPlayer var4) {
      this.selfPosition = var3;
      this.theWorld = var2;
      this.thePlayer = var4;
      this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
      this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
      this.addSlotToContainer(
         new Slot(this.outputSlot, 2, 134, 47) {
            private static final long a = 59703903831526L;

            @Override
            public boolean isItemValid(ItemStack var1) {
               return false;
            }

            @Override
            public boolean canTakeStack(EntityPlayer var1) {
               return (var1.capabilities.isCreativeMode || var1.experienceLevel >= ContainerRepair.this.maximumCost)
                  && ContainerRepair.this.maximumCost > 0
                  && this.getHasStack();
            }

            @Override
            public void onPickupFromSlot(EntityPlayer var1, ItemStack var2x) {
               if (!var1.capabilities.isCreativeMode) {
                  var1.addExperienceLevel(-ContainerRepair.this.maximumCost);
               }

               ContainerRepair.this.inputSlots.setInventorySlotContents(0, (ItemStack)null);
               if (ContainerRepair.this.materialCost > 0) {
                  ItemStack var6 = ContainerRepair.this.inputSlots.getStackInSlot(1);
                  if (var6 != null && var6.stackSize > ContainerRepair.this.materialCost) {
                     var6.stackSize = var6.stackSize - ContainerRepair.this.materialCost;
                     ContainerRepair.this.inputSlots.setInventorySlotContents(1, var6);
                  } else {
                     ContainerRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
                  }
               } else {
                  ContainerRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
               }

               ContainerRepair.this.maximumCost = 0;
               IBlockState var8 = var2.getBlockState(var3);
               if (!var1.capabilities.isCreativeMode && !var2.isRemote && var8.getBlock() == Blocks.anvil && var1.getRNG().nextFloat() < 0.12F) {
                  int var7 = var8.getValue(BlockAnvil.DAMAGE);
                  if (++var7 > 2) {
                     var2.setBlockToAir(var3);
                     var2.playAuxSFX(1020, var3, 0);
                  } else {
                     var2.setBlockState(var3, var8.withProperty(BlockAnvil.DAMAGE, var7), 2);
                     var2.playAuxSFX(1021, var3, 0);
                  }
               } else if (!var2.isRemote) {
                  var2.playAuxSFX(1021, var3, 0);
               }
            }
         }
      );

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
   public void onCraftMatrixChanged(IInventory var1) {
      super.onCraftMatrixChanged(var1);
      if (var1 == this.inputSlots) {
         this.updateRepairOutput();
      }
   }

   public void updateRepairOutput() {
      ItemStack var11 = this.inputSlots.getStackInSlot(0);
      this.maximumCost = 1;
      int var12 = 0;
      int var13 = 0;
      byte var14 = 0;
      if (var11 == null) {
         this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
         this.maximumCost = 0;
      } else {
         ItemStack var15 = var11.copy();
         ItemStack var16 = this.inputSlots.getStackInSlot(1);
         Map var17 = EnchantmentHelper.getEnchantments(var15);
         boolean var18 = false;
         var13 = var13 + var11.getRepairCost() + (var16 == null ? 0 : var16.getRepairCost());
         this.materialCost = 0;
         if (var16 != null) {
            var18 = var16.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(var16).tagCount() > 0;
            if (var15.isItemStackDamageable() && var15.getItem().getIsRepairable(var11, var16)) {
               int var32 = Math.min(var15.getItemDamage(), var15.getMaxDamage() / 4);
               if (var32 <= 0) {
                  this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                  this.maximumCost = 0;
                  return;
               }

               int var36;
               for (var36 = 0; var32 > 0 && var36 < var16.stackSize; var36++) {
                  int var38 = var15.getItemDamage() - var32;
                  var15.setItemDamage(var38);
                  var12++;
                  var32 = Math.min(var15.getItemDamage(), var15.getMaxDamage() / 4);
               }

               this.materialCost = var36;
            } else {
               if (!var18 && (var15.getItem() != var16.getItem() || !var15.isItemStackDamageable())) {
                  this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                  this.maximumCost = 0;
                  return;
               }

               if (var15.isItemStackDamageable() && !var18) {
                  int var19 = var11.getMaxDamage() - var11.getItemDamage();
                  int var20 = var16.getMaxDamage() - var16.getItemDamage();
                  int var21 = var20 + var15.getMaxDamage() * 12 / 100;
                  int var22 = var19 + var21;
                  int var23 = var15.getMaxDamage() - var22;
                  if (var23 < 0) {
                     var23 = 0;
                  }

                  if (var23 < var15.getMetadata()) {
                     var15.setItemDamage(var23);
                     var12 += 2;
                  }
               }

               Map var31 = EnchantmentHelper.getEnchantments(var16);

               for (Object var37$raw : var31.keySet()) {
                  int var37 = (int)var37$raw;
                  Enchantment var39 = Enchantment.getEnchantmentById(var37);
                  if (var39 != null) {
                     int var40 = var17.containsKey(var37) ? (Integer)var17.get(var37) : 0;
                     int var24 = (Integer)var31.get(var37);
                     int var25;
                     if (var40 == var24) {
                        var25 = ++var24;
                     } else {
                        var25 = Math.max(var24, var40);
                     }

                     var24 = var25;
                     boolean var26 = var39.canApply(var11);
                     if (this.thePlayer.capabilities.isCreativeMode || var11.getItem() == Items.enchanted_book) {
                        var26 = true;
                     }

                     for (Object var28$raw : var17.keySet()) {
                        int var28 = (int)var28$raw;
                        if (var28 != var37 && !var39.canApplyTogether(Enchantment.getEnchantmentById(var28))) {
                           var26 = false;
                           var12++;
                        }
                     }

                     if (var26) {
                        if (var24 > var39.getMaxLevel()) {
                           var24 = var39.getMaxLevel();
                        }

                        var17.put(var37, var24);
                        int var43 = 0;
                        switch (var39.getWeight()) {
                           case 1:
                              var43 = 8;
                              break;
                           case 2:
                              var43 = 4;
                           case 3:
                           case 4:
                           case 6:
                           case 7:
                           case 8:
                           case 9:
                           default:
                              break;
                           case 5:
                              var43 = 2;
                              break;
                           case 10:
                              var43 = 1;
                        }

                        if (var18) {
                           var43 = Math.max(1, var43 / 2);
                        }

                        var12 += var43 * var24;
                     }
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.repairedItemName)) {
            if (var11.hasDisplayName()) {
               var14 = 1;
               var12 += var14;
               var15.clearCustomName();
            }
         } else if (!this.repairedItemName.equals(var11.getDisplayName())) {
            var14 = 1;
            var12 += var14;
            var15.setStackDisplayName(this.repairedItemName);
         }

         this.maximumCost = var13 + var12;
         if (var12 <= 0) {
            var15 = null;
         }

         if (var14 == var12 && var14 > 0 && this.maximumCost >= 40) {
            this.maximumCost = 39;
         }

         if (this.maximumCost >= 40 && !this.thePlayer.capabilities.isCreativeMode) {
            var15 = null;
         }

         if (var15 != null) {
            int var33 = var15.getRepairCost();
            if (var16 != null && var33 < var16.getRepairCost()) {
               var33 = var16.getRepairCost();
            }

            var33 = var33 * 2 + 1;
            var15.setRepairCost(var33);
            EnchantmentHelper.setEnchantments(var17, var15);
         }

         this.outputSlot.setInventorySlotContents(0, var15);
         this.detectAndSendChanges();
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting var1) {
      super.onCraftGuiOpened(var1);
      var1.sendProgressBarUpdate(this, 0, this.maximumCost);
   }

   @Override
   public void updateProgressBar(int var1, int var2) {
      if (var1 == 0) {
         this.maximumCost = var2;
      }
   }

   @Override
   public void onContainerClosed(EntityPlayer var1) {
      super.onContainerClosed(var1);
      if (!this.theWorld.isRemote) {
         for (int var5 = 0; var5 < this.inputSlots.getSizeInventory(); var5++) {
            ItemStack var6 = this.inputSlots.removeStackFromSlot(var5);
            if (var6 != null) {
               var1.dropPlayerItemWithRandomChoice(var6, false);
            }
         }
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer var1) {
      return this.theWorld.getBlockState(this.selfPosition).getBlock() != Blocks.anvil
         ? false
         : var1.getDistanceSq(this.selfPosition.getX() + 0.5, this.selfPosition.getY() + 0.5, this.selfPosition.getZ() + 0.5) <= 64.0;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var6 = null;
      Slot var7 = this.inventorySlots.get(var2);
      if (var7 != null && var7.getHasStack()) {
         ItemStack var8 = var7.getStack();
         var6 = var8.copy();
         if (var2 == 2) {
            if (!this.mergeItemStack(var8, 3, 39, true)) {
               return null;
            }

            var7.onSlotChange(var8, var6);
         } else if (var2 != 0 && var2 != 1) {
            if (var2 >= 3 && var2 < 39 && !this.mergeItemStack(var8, 0, 2, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(var8, 3, 39, false)) {
            return null;
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

   public void updateItemName(String var1) {
      this.repairedItemName = var1;
      if (this.getSlot(2).getHasStack()) {
         ItemStack var5 = this.getSlot(2).getStack();
         if (StringUtils.isBlank(var1)) {
            var5.clearCustomName();
         } else {
            var5.setStackDisplayName(this.repairedItemName);
         }
      }

      this.updateRepairOutput();
   }
}
