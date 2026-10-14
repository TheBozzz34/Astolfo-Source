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

   public ContainerRepair(InventoryPlayer playerInventory, World worldIn, EntityPlayer player) {
      this(playerInventory, worldIn, BlockPos.ORIGIN, player);
   }

   public ContainerRepair(InventoryPlayer playerInventory, final World worldIn, final BlockPos blockPosIn, EntityPlayer player) {
      this.selfPosition = blockPosIn;
      this.theWorld = worldIn;
      this.thePlayer = player;
      this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
      this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
      this.addSlotToContainer(
         new Slot(this.outputSlot, 2, 134, 47) {
            private static final long a = 59703903831526L;

            @Override
            public boolean isItemValid(ItemStack stack) {
               return false;
            }

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
               return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= ContainerRepair.this.maximumCost)
                  && ContainerRepair.this.maximumCost > 0
                  && this.getHasStack();
            }

            @Override
            public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
               if (!playerIn.capabilities.isCreativeMode) {
                  playerIn.addExperienceLevel(-ContainerRepair.this.maximumCost);
               }

               ContainerRepair.this.inputSlots.setInventorySlotContents(0, (ItemStack)null);
               if (ContainerRepair.this.materialCost > 0) {
                  ItemStack itemStack = ContainerRepair.this.inputSlots.getStackInSlot(1);
                  if (itemStack != null && itemStack.stackSize > ContainerRepair.this.materialCost) {
                     itemStack.stackSize = itemStack.stackSize - ContainerRepair.this.materialCost;
                     ContainerRepair.this.inputSlots.setInventorySlotContents(1, itemStack);
                  } else {
                     ContainerRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
                  }
               } else {
                  ContainerRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
               }

               ContainerRepair.this.maximumCost = 0;
               IBlockState iBlockState = worldIn.getBlockState(blockPosIn);
               if (!playerIn.capabilities.isCreativeMode && !worldIn.isRemote && iBlockState.getBlock() == Blocks.anvil && playerIn.getRNG().nextFloat() < 0.12F) {
                  int i = iBlockState.getValue(BlockAnvil.DAMAGE);
                  if (++i > 2) {
                     worldIn.setBlockToAir(blockPosIn);
                     worldIn.playAuxSFX(1020, blockPosIn, 0);
                  } else {
                     worldIn.setBlockState(blockPosIn, iBlockState.withProperty(BlockAnvil.DAMAGE, i), 2);
                     worldIn.playAuxSFX(1021, blockPosIn, 0);
                  }
               } else if (!worldIn.isRemote) {
                  worldIn.playAuxSFX(1021, blockPosIn, 0);
               }
            }
         }
      );

      for (int j = 0; j < 3; j++) {
         for (int k = 0; k < 9; k++) {
            this.addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
         }
      }

      for (int l = 0; l < 9; l++) {
         this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
      }
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      super.onCraftMatrixChanged(inventoryIn);
      if (inventoryIn == this.inputSlots) {
         this.updateRepairOutput();
      }
   }

   public void updateRepairOutput() {
      ItemStack itemStack = this.inputSlots.getStackInSlot(0);
      this.maximumCost = 1;
      int i = 0;
      int j = 0;
      byte b = 0;
      if (itemStack == null) {
         this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
         this.maximumCost = 0;
      } else {
         ItemStack itemStack2 = itemStack.copy();
         ItemStack itemStack3 = this.inputSlots.getStackInSlot(1);
         Map map = EnchantmentHelper.getEnchantments(itemStack2);
         boolean bl = false;
         j = j + itemStack.getRepairCost() + (itemStack3 == null ? 0 : itemStack3.getRepairCost());
         this.materialCost = 0;
         if (itemStack3 != null) {
            bl = itemStack3.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(itemStack3).tagCount() > 0;
            if (itemStack2.isItemStackDamageable() && itemStack2.getItem().getIsRepairable(itemStack, itemStack3)) {
               int k = Math.min(itemStack2.getItemDamage(), itemStack2.getMaxDamage() / 4);
               if (k <= 0) {
                  this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                  this.maximumCost = 0;
                  return;
               }

               int l;
               for (l = 0; k > 0 && l < itemStack3.stackSize; l++) {
                  int m = itemStack2.getItemDamage() - k;
                  itemStack2.setItemDamage(m);
                  i++;
                  k = Math.min(itemStack2.getItemDamage(), itemStack2.getMaxDamage() / 4);
               }

               this.materialCost = l;
            } else {
               if (!bl && (itemStack2.getItem() != itemStack3.getItem() || !itemStack2.isItemStackDamageable())) {
                  this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                  this.maximumCost = 0;
                  return;
               }

               if (itemStack2.isItemStackDamageable() && !bl) {
                  int n = itemStack.getMaxDamage() - itemStack.getItemDamage();
                  int i2 = itemStack3.getMaxDamage() - itemStack3.getItemDamage();
                  int i3 = i2 + itemStack2.getMaxDamage() * 12 / 100;
                  int i4 = n + i3;
                  int i5 = itemStack2.getMaxDamage() - i4;
                  if (i5 < 0) {
                     i5 = 0;
                  }

                  if (i5 < itemStack2.getMetadata()) {
                     itemStack2.setItemDamage(i5);
                     i += 2;
                  }
               }

               Map map2 = EnchantmentHelper.getEnchantments(itemStack3);

               for (Object object : map2.keySet()) {
                  int i6 = (int)object;
                  Enchantment enchantment = Enchantment.getEnchantmentById(i6);
                  if (enchantment != null) {
                     int i7 = map.containsKey(i6) ? (Integer)map.get(i6) : 0;
                     int i8 = (Integer)map2.get(i6);
                     int i9;
                     if (i7 == i8) {
                        i9 = ++i8;
                     } else {
                        i9 = Math.max(i8, i7);
                     }

                     i8 = i9;
                     boolean bl2 = enchantment.canApply(itemStack);
                     if (this.thePlayer.capabilities.isCreativeMode || itemStack.getItem() == Items.enchanted_book) {
                        bl2 = true;
                     }

                     for (Object object2 : map.keySet()) {
                        int i10 = (int)object2;
                        if (i10 != i6 && !enchantment.canApplyTogether(Enchantment.getEnchantmentById(i10))) {
                           bl2 = false;
                           i++;
                        }
                     }

                     if (bl2) {
                        if (i8 > enchantment.getMaxLevel()) {
                           i8 = enchantment.getMaxLevel();
                        }

                        map.put(i6, i8);
                        int i11 = 0;
                        switch (enchantment.getWeight()) {
                           case 1:
                              i11 = 8;
                              break;
                           case 2:
                              i11 = 4;
                           case 3:
                           case 4:
                           case 6:
                           case 7:
                           case 8:
                           case 9:
                           default:
                              break;
                           case 5:
                              i11 = 2;
                              break;
                           case 10:
                              i11 = 1;
                        }

                        if (bl) {
                           i11 = Math.max(1, i11 / 2);
                        }

                        i += i11 * i8;
                     }
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.repairedItemName)) {
            if (itemStack.hasDisplayName()) {
               b = 1;
               i += b;
               itemStack2.clearCustomName();
            }
         } else if (!this.repairedItemName.equals(itemStack.getDisplayName())) {
            b = 1;
            i += b;
            itemStack2.setStackDisplayName(this.repairedItemName);
         }

         this.maximumCost = j + i;
         if (i <= 0) {
            itemStack2 = null;
         }

         if (b == i && b > 0 && this.maximumCost >= 40) {
            this.maximumCost = 39;
         }

         if (this.maximumCost >= 40 && !this.thePlayer.capabilities.isCreativeMode) {
            itemStack2 = null;
         }

         if (itemStack2 != null) {
            int i12 = itemStack2.getRepairCost();
            if (itemStack3 != null && i12 < itemStack3.getRepairCost()) {
               i12 = itemStack3.getRepairCost();
            }

            i12 = i12 * 2 + 1;
            itemStack2.setRepairCost(i12);
            EnchantmentHelper.setEnchantments(map, itemStack2);
         }

         this.outputSlot.setInventorySlotContents(0, itemStack2);
         this.detectAndSendChanges();
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendProgressBarUpdate(this, 0, this.maximumCost);
   }

   @Override
   public void updateProgressBar(int id, int data) {
      if (id == 0) {
         this.maximumCost = data;
      }
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!this.theWorld.isRemote) {
         for (int i = 0; i < this.inputSlots.getSizeInventory(); i++) {
            ItemStack itemStack = this.inputSlots.removeStackFromSlot(i);
            if (itemStack != null) {
               playerIn.dropPlayerItemWithRandomChoice(itemStack, false);
            }
         }
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.theWorld.getBlockState(this.selfPosition).getBlock() != Blocks.anvil
         ? false
         : playerIn.getDistanceSq(this.selfPosition.getX() + 0.5, this.selfPosition.getY() + 0.5, this.selfPosition.getZ() + 0.5) <= 64.0;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index == 2) {
            if (!this.mergeItemStack(itemStack2, 3, 39, true)) {
               return null;
            }

            slot.onSlotChange(itemStack2, itemStack);
         } else if (index != 0 && index != 1) {
            if (index >= 3 && index < 39 && !this.mergeItemStack(itemStack2, 0, 2, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 3, 39, false)) {
            return null;
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

   public void updateItemName(String newName) {
      this.repairedItemName = newName;
      if (this.getSlot(2).getHasStack()) {
         ItemStack itemStack = this.getSlot(2).getStack();
         if (StringUtils.isBlank(newName)) {
            itemStack.clearCustomName();
         } else {
            itemStack.setStackDisplayName(this.repairedItemName);
         }
      }

      this.updateRepairOutput();
   }
}
