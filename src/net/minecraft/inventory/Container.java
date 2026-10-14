package net.minecraft.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

public abstract class Container {
   public List<ItemStack> inventoryItemStacks = Lists.newArrayList();
   public List<Slot> inventorySlots = Lists.newArrayList();
   public int windowId;
   private short transactionID;
   private int dragMode = -1;
   private int dragEvent;
   private final Set<Slot> dragSlots = Sets.newHashSet();
   public List<ICrafting> crafters = Lists.newArrayList();
   private Set<EntityPlayer> playerList = Sets.newHashSet();
   private static final long g = 33550479050602L;

   public Slot addSlotToContainer(Slot slotIn) {
      slotIn.slotNumber = this.inventorySlots.size();
      this.inventorySlots.add(slotIn);
      this.inventoryItemStacks.add((ItemStack)null);
      return slotIn;
   }

   public void onCraftGuiOpened(ICrafting listener) {
      if (this.crafters.contains(listener)) {
         throw new IllegalArgumentException("Listener already listening");
      }

      this.crafters.add(listener);
      listener.updateCraftingInventory(this, this.getInventory());
      this.detectAndSendChanges();
   }

   public void removeCraftingFromCrafters(ICrafting listeners) {
      this.crafters.remove(listeners);
   }

   public List<ItemStack> getInventory() {
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < this.inventorySlots.size(); i++) {
         arrayList.add(this.inventorySlots.get(i).getStack());
      }

      return arrayList;
   }

   public void detectAndSendChanges() {
      for (int i = 0; i < this.inventorySlots.size(); i++) {
         ItemStack itemStack = this.inventorySlots.get(i).getStack();
         ItemStack itemStack2 = this.inventoryItemStacks.get(i);
         if (!ItemStack.areItemStacksEqual(itemStack2, itemStack)) {
            itemStack2 = itemStack == null ? null : itemStack.copy();
            this.inventoryItemStacks.set(i, itemStack2);

            for (int j = 0; j < this.crafters.size(); j++) {
               this.crafters.get(j).sendSlotContents(this, i, itemStack2);
            }
         }
      }
   }

   public boolean enchantItem(EntityPlayer playerIn, int id) {
      return false;
   }

   public Slot getSlotFromInventory(IInventory inv, int slotIn) {
      for (int i = 0; i < this.inventorySlots.size(); i++) {
         Slot slot = this.inventorySlots.get(i);
         if (slot.isHere(inv, slotIn)) {
            return slot;
         }
      }

      return null;
   }

   public Slot getSlot(int slotId) {
      return this.inventorySlots.get(slotId);
   }

   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      Slot slot = this.inventorySlots.get(index);
      return slot != null ? slot.getStack() : null;
   }

   public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
      ItemStack itemStack = null;
      InventoryPlayer inventoryPlayer = playerIn.inventory;
      if (mode == 5) {
         int i = this.dragEvent;
         this.dragEvent = p(clickedButton);
         if ((i != 1 || this.dragEvent != 2) && i != this.dragEvent) {
            this.resetDrag();
         } else if (inventoryPlayer.getItemStack() == null) {
            this.resetDrag();
         } else if (this.dragEvent == 0) {
            this.dragMode = U(clickedButton);
            if (isValidDragMode(this.dragMode, playerIn)) {
               this.dragEvent = 1;
               this.dragSlots.clear();
            } else {
               this.resetDrag();
            }
         } else if (this.dragEvent == 1) {
            Slot slot = this.inventorySlots.get(slotId);
            if (slot != null
               && canAddItemToSlot(slot, inventoryPlayer.getItemStack(), true)
               && slot.isItemValid(inventoryPlayer.getItemStack())
               && inventoryPlayer.getItemStack().stackSize > this.dragSlots.size()
               && this.canDragIntoSlot(slot)) {
               this.dragSlots.add(slot);
            }
         } else if (this.dragEvent == 2) {
            if (!this.dragSlots.isEmpty()) {
               ItemStack itemStack2 = inventoryPlayer.getItemStack().copy();
               int j = inventoryPlayer.getItemStack().stackSize;

               for (Slot slot2 : this.dragSlots) {
                  if (slot2 != null
                     && canAddItemToSlot(slot2, inventoryPlayer.getItemStack(), true)
                     && slot2.isItemValid(inventoryPlayer.getItemStack())
                     && inventoryPlayer.getItemStack().stackSize >= this.dragSlots.size()
                     && this.canDragIntoSlot(slot2)) {
                     ItemStack itemStack3 = itemStack2.copy();
                     int k = slot2.getHasStack() ? slot2.getStack().stackSize : 0;
                     computeStackSize(this.dragSlots, this.dragMode, itemStack3, k);
                     if (itemStack3.stackSize > itemStack3.getMaxStackSize()) {
                        itemStack3.stackSize = itemStack3.getMaxStackSize();
                     }

                     if (itemStack3.stackSize > slot2.getItemStackLimit(itemStack3)) {
                        itemStack3.stackSize = slot2.getItemStackLimit(itemStack3);
                     }

                     j -= itemStack3.stackSize - k;
                     slot2.putStack(itemStack3);
                  }
               }

               itemStack2.stackSize = j;
               if (itemStack2.stackSize <= 0) {
                  itemStack2 = null;
               }

               inventoryPlayer.setItemStack(itemStack2);
            }

            this.resetDrag();
         } else {
            this.resetDrag();
         }
      } else if (this.dragEvent != 0) {
         this.resetDrag();
      } else if ((mode == 0 || mode == 1) && (clickedButton == 0 || clickedButton == 1)) {
         if (slotId == -999) {
            if (inventoryPlayer.getItemStack() != null) {
               if (clickedButton == 0) {
                  playerIn.dropPlayerItemWithRandomChoice(inventoryPlayer.getItemStack(), true);
                  inventoryPlayer.setItemStack((ItemStack)null);
               }

               if (clickedButton == 1) {
                  playerIn.dropPlayerItemWithRandomChoice(inventoryPlayer.getItemStack().splitStack(1), true);
                  if (inventoryPlayer.getItemStack().stackSize == 0) {
                     inventoryPlayer.setItemStack((ItemStack)null);
                  }
               }
            }
         } else if (mode == 1) {
            if (slotId < 0) {
               return null;
            }

            Slot slot3 = this.inventorySlots.get(slotId);
            if (slot3 != null && slot3.canTakeStack(playerIn)) {
               ItemStack itemStack4 = this.transferStackInSlot(playerIn, slotId);
               if (itemStack4 != null) {
                  Item item = itemStack4.getItem();
                  itemStack = itemStack4.copy();
                  if (slot3.getStack() != null && slot3.getStack().getItem() == item) {
                     this.retrySlotClick(slotId, clickedButton, true, playerIn);
                  }
               }
            }
         } else {
            if (slotId < 0) {
               return null;
            }

            Slot slot4 = this.inventorySlots.get(slotId);
            if (slot4 != null) {
               ItemStack itemStack5 = slot4.getStack();
               ItemStack itemStack6 = inventoryPlayer.getItemStack();
               if (itemStack5 != null) {
                  itemStack = itemStack5.copy();
               }

               if (itemStack5 == null) {
                  if (itemStack6 != null && slot4.isItemValid(itemStack6)) {
                     int l = clickedButton == 0 ? itemStack6.stackSize : 1;
                     if (l > slot4.getItemStackLimit(itemStack6)) {
                        l = slot4.getItemStackLimit(itemStack6);
                     }

                     if (itemStack6.stackSize >= l) {
                        slot4.putStack(itemStack6.splitStack(l));
                     }

                     if (itemStack6.stackSize == 0) {
                        inventoryPlayer.setItemStack((ItemStack)null);
                     }
                  }
               } else if (slot4.canTakeStack(playerIn)) {
                  if (itemStack6 == null) {
                     int m = clickedButton == 0 ? itemStack5.stackSize : (itemStack5.stackSize + 1) / 2;
                     ItemStack itemStack7 = slot4.decrStackSize(m);
                     inventoryPlayer.setItemStack(itemStack7);
                     if (itemStack5.stackSize == 0) {
                        slot4.putStack((ItemStack)null);
                     }

                     slot4.onPickupFromSlot(playerIn, inventoryPlayer.getItemStack());
                  } else if (slot4.isItemValid(itemStack6)) {
                     if (itemStack5.getItem() == itemStack6.getItem() && itemStack5.getMetadata() == itemStack6.getMetadata() && ItemStack.areItemStackTagsEqual(itemStack5, itemStack6)) {
                        int n = clickedButton == 0 ? itemStack6.stackSize : 1;
                        if (n > slot4.getItemStackLimit(itemStack6) - itemStack5.stackSize) {
                           n = slot4.getItemStackLimit(itemStack6) - itemStack5.stackSize;
                        }

                        if (n > itemStack6.getMaxStackSize() - itemStack5.stackSize) {
                           n = itemStack6.getMaxStackSize() - itemStack5.stackSize;
                        }

                        itemStack6.splitStack(n);
                        if (itemStack6.stackSize == 0) {
                           inventoryPlayer.setItemStack((ItemStack)null);
                        }

                        itemStack5.stackSize += n;
                     } else if (itemStack6.stackSize <= slot4.getItemStackLimit(itemStack6)) {
                        slot4.putStack(itemStack6);
                        inventoryPlayer.setItemStack(itemStack5);
                     }
                  } else if (itemStack5.getItem() == itemStack6.getItem()
                     && itemStack6.getMaxStackSize() > 1
                     && (!itemStack5.getHasSubtypes() || itemStack5.getMetadata() == itemStack6.getMetadata())
                     && ItemStack.areItemStackTagsEqual(itemStack5, itemStack6)) {
                     int i2 = itemStack5.stackSize;
                     if (i2 > 0 && i2 + itemStack6.stackSize <= itemStack6.getMaxStackSize()) {
                        itemStack6.stackSize += i2;
                        itemStack5 = slot4.decrStackSize(i2);
                        if (itemStack5.stackSize == 0) {
                           slot4.putStack((ItemStack)null);
                        }

                        slot4.onPickupFromSlot(playerIn, inventoryPlayer.getItemStack());
                     }
                  }
               }

               slot4.onSlotChanged();
            }
         }
      } else if (mode == 2 && clickedButton >= 0 && clickedButton < 9) {
         Slot slot5 = this.inventorySlots.get(slotId);
         if (slot5.canTakeStack(playerIn)) {
            ItemStack itemStack8 = inventoryPlayer.getStackInSlot(clickedButton);
            boolean bl = itemStack8 == null || slot5.inventory == inventoryPlayer && slot5.isItemValid(itemStack8);
            int i3 = -1;
            if (!bl) {
               i3 = inventoryPlayer.getFirstEmptyStack();
               bl |= i3 > -1;
            }

            if (slot5.getHasStack() && bl) {
               ItemStack itemStack9 = slot5.getStack();
               inventoryPlayer.setInventorySlotContents(clickedButton, itemStack9.copy());
               if ((slot5.inventory != inventoryPlayer || !slot5.isItemValid(itemStack8)) && itemStack8 != null) {
                  if (i3 > -1) {
                     inventoryPlayer.addItemStackToInventory(itemStack8);
                     slot5.decrStackSize(itemStack9.stackSize);
                     slot5.putStack((ItemStack)null);
                     slot5.onPickupFromSlot(playerIn, itemStack9);
                  }
               } else {
                  slot5.decrStackSize(itemStack9.stackSize);
                  slot5.putStack(itemStack8);
                  slot5.onPickupFromSlot(playerIn, itemStack9);
               }
            } else if (!slot5.getHasStack() && itemStack8 != null && slot5.isItemValid(itemStack8)) {
               inventoryPlayer.setInventorySlotContents(clickedButton, (ItemStack)null);
               slot5.putStack(itemStack8);
            }
         }
      } else if (mode == 3 && playerIn.capabilities.isCreativeMode && inventoryPlayer.getItemStack() == null && slotId >= 0) {
         Slot slot6 = this.inventorySlots.get(slotId);
         if (slot6 != null && slot6.getHasStack()) {
            ItemStack itemStack10 = slot6.getStack().copy();
            itemStack10.stackSize = itemStack10.getMaxStackSize();
            inventoryPlayer.setItemStack(itemStack10);
         }
      } else if (mode == 4 && inventoryPlayer.getItemStack() == null && slotId >= 0) {
         if (slotId >= this.inventorySlots.size()) {
            return itemStack;
         }

         Slot slot7 = this.inventorySlots.get(slotId);
         if (slot7 != null && slot7.getHasStack() && slot7.canTakeStack(playerIn)) {
            ItemStack itemStack11 = slot7.decrStackSize(clickedButton == 0 ? 1 : slot7.getStack().stackSize);
            slot7.onPickupFromSlot(playerIn, itemStack11);
            playerIn.dropPlayerItemWithRandomChoice(itemStack11, true);
         }
      } else if (mode == 6 && slotId >= 0) {
         Slot slot8 = this.inventorySlots.get(slotId);
         ItemStack itemStack12 = inventoryPlayer.getItemStack();
         if (itemStack12 != null && (slot8 == null || !slot8.getHasStack() || !slot8.canTakeStack(playerIn))) {
            int i4 = clickedButton == 0 ? 0 : this.inventorySlots.size() - 1;
            int i5 = clickedButton == 0 ? 1 : -1;

            for (int i6 = 0; i6 < 2; i6++) {
               for (int i7 = i4; i7 >= 0 && i7 < this.inventorySlots.size() && itemStack12.stackSize < itemStack12.getMaxStackSize(); i7 += i5) {
                  Slot slot9 = this.inventorySlots.get(i7);
                  if (slot9.getHasStack()
                     && canAddItemToSlot(slot9, itemStack12, true)
                     && slot9.canTakeStack(playerIn)
                     && this.canMergeSlot(itemStack12, slot9)
                     && (i6 != 0 || slot9.getStack().stackSize != slot9.getStack().getMaxStackSize())) {
                     int i8 = Math.min(itemStack12.getMaxStackSize() - itemStack12.stackSize, slot9.getStack().stackSize);
                     ItemStack itemStack13 = slot9.decrStackSize(i8);
                     itemStack12.stackSize += i8;
                     if (itemStack13.stackSize <= 0) {
                        slot9.putStack((ItemStack)null);
                     }

                     slot9.onPickupFromSlot(playerIn, itemStack13);
                  }
               }
            }
         }

         this.detectAndSendChanges();
      }

      return itemStack;
   }

   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return true;
   }

   public void retrySlotClick(int slotId, int clickedButton, boolean mode, EntityPlayer playerIn) {
      this.slotClick(slotId, clickedButton, 1, playerIn);
   }

   public void onContainerClosed(EntityPlayer playerIn) {
      InventoryPlayer inventoryPlayer = playerIn.inventory;
      if (inventoryPlayer.getItemStack() != null) {
         playerIn.dropPlayerItemWithRandomChoice(inventoryPlayer.getItemStack(), false);
         inventoryPlayer.setItemStack((ItemStack)null);
      }
   }

   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.detectAndSendChanges();
   }

   public void putStackInSlot(int slotID, ItemStack stack) {
      this.getSlot(slotID).putStack(stack);
   }

   public void putStacksInSlots(ItemStack[] itemStacks) {
      for (int i = 0; i < itemStacks.length; i++) {
         this.getSlot(i).putStack(itemStacks[i]);
      }
   }

   public void updateProgressBar(int id, int data) {
   }

   public short getNextTransactionID(InventoryPlayer inventoryPlayer) {
      this.transactionID++;
      return this.transactionID;
   }

   public boolean getCanCraft(EntityPlayer entityPlayer) {
      return !this.playerList.contains(entityPlayer);
   }

   public void setCanCraft(EntityPlayer entityPlayer, boolean bl) {
      if (bl) {
         this.playerList.remove(entityPlayer);
      } else {
         this.playerList.add(entityPlayer);
      }
   }

   public abstract boolean canInteractWith(EntityPlayer playerIn);

   public boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
      boolean bl = false;
      int i = startIndex;
      if (reverseDirection) {
         i = endIndex - 1;
      }

      if (stack.isStackable()) {
         while (stack.stackSize > 0 && (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex)) {
            Slot slot = this.inventorySlots.get(i);
            ItemStack itemStack = slot.getStack();
            if (itemStack != null
               && itemStack.getItem() == stack.getItem()
               && (!stack.getHasSubtypes() || stack.getMetadata() == itemStack.getMetadata())
               && ItemStack.areItemStackTagsEqual(stack, itemStack)) {
               int j = itemStack.stackSize + stack.stackSize;
               if (j <= stack.getMaxStackSize()) {
                  stack.stackSize = 0;
                  itemStack.stackSize = j;
                  slot.onSlotChanged();
                  bl = true;
               } else if (itemStack.stackSize < stack.getMaxStackSize()) {
                  stack.stackSize = stack.stackSize - (stack.getMaxStackSize() - itemStack.stackSize);
                  itemStack.stackSize = stack.getMaxStackSize();
                  slot.onSlotChanged();
                  bl = true;
               }
            }

            if (reverseDirection) {
               i--;
            } else {
               i++;
            }
         }
      }

      if (stack.stackSize > 0) {
         if (reverseDirection) {
            i = endIndex - 1;
         } else {
            i = startIndex;
         }

         while (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex) {
            Slot slot2 = this.inventorySlots.get(i);
            ItemStack itemStack2 = slot2.getStack();
            if (itemStack2 == null) {
               slot2.putStack(stack.copy());
               slot2.onSlotChanged();
               stack.stackSize = 0;
               bl = true;
               break;
            }

            if (reverseDirection) {
               i--;
            } else {
               i++;
            }
         }
      }

      return bl;
   }

   public static int U(int i) {
      return i >> 2 & 3;
   }

   public static int p(int i) {
      return i & 3;
   }

   public static int func_94534_d(int i, int j) {
      return i & 3 | (j & 3) << 2;
   }

   public static boolean isValidDragMode(int dragModeIn, EntityPlayer player) {
      return dragModeIn == 0 ? true : (dragModeIn == 1 ? true : dragModeIn == 2 && player.capabilities.isCreativeMode);
   }

   public void resetDrag() {
      this.dragEvent = 0;
      this.dragSlots.clear();
   }

   public static boolean canAddItemToSlot(Slot slotIn, ItemStack stack, boolean stackSizeMatters) {
      boolean bl = slotIn == null || !slotIn.getHasStack();
      if (slotIn != null
         && slotIn.getHasStack()
         && stack != null
         && stack.isItemEqual(slotIn.getStack())
         && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack)) {
         int i = stackSizeMatters ? 0 : stack.stackSize;
         bl |= slotIn.getStack().stackSize + i <= stack.getMaxStackSize();
      }

      return bl;
   }

   public static void computeStackSize(Set<Slot> set, int i, ItemStack itemStack, int j) {
      switch (i) {
         case 0:
            itemStack.stackSize = MathHelper.floor_float((float)itemStack.stackSize / set.size());
            break;
         case 1:
            itemStack.stackSize = 1;
            break;
         case 2:
            itemStack.stackSize = itemStack.getItem().getItemStackLimit();
      }

      itemStack.stackSize += j;
   }

   public boolean canDragIntoSlot(Slot slot) {
      return true;
   }

   public static int calcRedstone(TileEntity te) {
      return te instanceof IInventory ? calcRedstoneFromInventory((IInventory)te) : 0;
   }

   public static int calcRedstoneFromInventory(IInventory inv) {
      if (inv == null) {
         return 0;
      }

      int i = 0;
      float f = 0.0F;

      for (int j = 0; j < inv.getSizeInventory(); j++) {
         ItemStack itemStack = inv.getStackInSlot(j);
         if (itemStack != null) {
            f += (float)itemStack.stackSize / Math.min(inv.getInventoryStackLimit(), itemStack.getMaxStackSize());
            i++;
         }
      }

      f /= inv.getSizeInventory();
      return MathHelper.floor_float(f * 14.0F) + (i > 0 ? 1 : 0);
   }
}
