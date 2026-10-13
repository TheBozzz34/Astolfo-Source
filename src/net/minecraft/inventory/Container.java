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

   public Slot addSlotToContainer(Slot var1) {
      var1.slotNumber = this.inventorySlots.size();
      this.inventorySlots.add(var1);
      this.inventoryItemStacks.add((ItemStack)null);
      return var1;
   }

   public void onCraftGuiOpened(ICrafting var1) {
      if (this.crafters.contains(var1)) {
         throw new IllegalArgumentException("Listener already listening");
      }

      this.crafters.add(var1);
      var1.updateCraftingInventory(this, this.getInventory());
      this.detectAndSendChanges();
   }

   public void removeCraftingFromCrafters(ICrafting var1) {
      this.crafters.remove(var1);
   }

   public List<ItemStack> getInventory() {
      ArrayList var4 = Lists.newArrayList();

      for (int var5 = 0; var5 < this.inventorySlots.size(); var5++) {
         var4.add(this.inventorySlots.get(var5).getStack());
      }

      return var4;
   }

   public void detectAndSendChanges() {
      for (int var4 = 0; var4 < this.inventorySlots.size(); var4++) {
         ItemStack var5 = this.inventorySlots.get(var4).getStack();
         ItemStack var6 = this.inventoryItemStacks.get(var4);
         if (!ItemStack.areItemStacksEqual(var6, var5)) {
            var6 = var5 == null ? null : var5.copy();
            this.inventoryItemStacks.set(var4, var6);

            for (int var7 = 0; var7 < this.crafters.size(); var7++) {
               this.crafters.get(var7).sendSlotContents(this, var4, var6);
            }
         }
      }
   }

   public boolean enchantItem(EntityPlayer var1, int var2) {
      return false;
   }

   public Slot getSlotFromInventory(IInventory var1, int var2) {
      for (int var6 = 0; var6 < this.inventorySlots.size(); var6++) {
         Slot var7 = this.inventorySlots.get(var6);
         if (var7.isHere(var1, var2)) {
            return var7;
         }
      }

      return null;
   }

   public Slot getSlot(int var1) {
      return this.inventorySlots.get(var1);
   }

   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      Slot var6 = this.inventorySlots.get(var2);
      return var6 != null ? var6.getStack() : null;
   }

   public ItemStack slotClick(int var1, int var2, int var3, EntityPlayer var4) {
      ItemStack var8 = null;
      InventoryPlayer var9 = var4.inventory;
      if (var3 == 5) {
         int var10 = this.dragEvent;
         this.dragEvent = p(var2);
         if ((var10 != 1 || this.dragEvent != 2) && var10 != this.dragEvent) {
            this.resetDrag();
         } else if (var9.getItemStack() == null) {
            this.resetDrag();
         } else if (this.dragEvent == 0) {
            this.dragMode = U(var2);
            if (isValidDragMode(this.dragMode, var4)) {
               this.dragEvent = 1;
               this.dragSlots.clear();
            } else {
               this.resetDrag();
            }
         } else if (this.dragEvent == 1) {
            Slot var11 = this.inventorySlots.get(var1);
            if (var11 != null
               && canAddItemToSlot(var11, var9.getItemStack(), true)
               && var11.isItemValid(var9.getItemStack())
               && var9.getItemStack().stackSize > this.dragSlots.size()
               && this.canDragIntoSlot(var11)) {
               this.dragSlots.add(var11);
            }
         } else if (this.dragEvent == 2) {
            if (!this.dragSlots.isEmpty()) {
               ItemStack var25 = var9.getItemStack().copy();
               int var12 = var9.getItemStack().stackSize;

               for (Slot var14 : this.dragSlots) {
                  if (var14 != null
                     && canAddItemToSlot(var14, var9.getItemStack(), true)
                     && var14.isItemValid(var9.getItemStack())
                     && var9.getItemStack().stackSize >= this.dragSlots.size()
                     && this.canDragIntoSlot(var14)) {
                     ItemStack var15 = var25.copy();
                     int var16 = var14.getHasStack() ? var14.getStack().stackSize : 0;
                     computeStackSize(this.dragSlots, this.dragMode, var15, var16);
                     if (var15.stackSize > var15.getMaxStackSize()) {
                        var15.stackSize = var15.getMaxStackSize();
                     }

                     if (var15.stackSize > var14.getItemStackLimit(var15)) {
                        var15.stackSize = var14.getItemStackLimit(var15);
                     }

                     var12 -= var15.stackSize - var16;
                     var14.putStack(var15);
                  }
               }

               var25.stackSize = var12;
               if (var25.stackSize <= 0) {
                  var25 = null;
               }

               var9.setItemStack(var25);
            }

            this.resetDrag();
         } else {
            this.resetDrag();
         }
      } else if (this.dragEvent != 0) {
         this.resetDrag();
      } else if ((var3 == 0 || var3 == 1) && (var2 == 0 || var2 == 1)) {
         if (var1 == -999) {
            if (var9.getItemStack() != null) {
               if (var2 == 0) {
                  var4.dropPlayerItemWithRandomChoice(var9.getItemStack(), true);
                  var9.setItemStack((ItemStack)null);
               }

               if (var2 == 1) {
                  var4.dropPlayerItemWithRandomChoice(var9.getItemStack().splitStack(1), true);
                  if (var9.getItemStack().stackSize == 0) {
                     var9.setItemStack((ItemStack)null);
                  }
               }
            }
         } else if (var3 == 1) {
            if (var1 < 0) {
               return null;
            }

            Slot var23 = this.inventorySlots.get(var1);
            if (var23 != null && var23.canTakeStack(var4)) {
               ItemStack var30 = this.transferStackInSlot(var4, var1);
               if (var30 != null) {
                  Item var35 = var30.getItem();
                  var8 = var30.copy();
                  if (var23.getStack() != null && var23.getStack().getItem() == var35) {
                     this.retrySlotClick(var1, var2, true, var4);
                  }
               }
            }
         } else {
            if (var1 < 0) {
               return null;
            }

            Slot var24 = this.inventorySlots.get(var1);
            if (var24 != null) {
               ItemStack var31 = var24.getStack();
               ItemStack var36 = var9.getItemStack();
               if (var31 != null) {
                  var8 = var31.copy();
               }

               if (var31 == null) {
                  if (var36 != null && var24.isItemValid(var36)) {
                     int var42 = var2 == 0 ? var36.stackSize : 1;
                     if (var42 > var24.getItemStackLimit(var36)) {
                        var42 = var24.getItemStackLimit(var36);
                     }

                     if (var36.stackSize >= var42) {
                        var24.putStack(var36.splitStack(var42));
                     }

                     if (var36.stackSize == 0) {
                        var9.setItemStack((ItemStack)null);
                     }
                  }
               } else if (var24.canTakeStack(var4)) {
                  if (var36 == null) {
                     int var41 = var2 == 0 ? var31.stackSize : (var31.stackSize + 1) / 2;
                     ItemStack var45 = var24.decrStackSize(var41);
                     var9.setItemStack(var45);
                     if (var31.stackSize == 0) {
                        var24.putStack((ItemStack)null);
                     }

                     var24.onPickupFromSlot(var4, var9.getItemStack());
                  } else if (var24.isItemValid(var36)) {
                     if (var31.getItem() == var36.getItem() && var31.getMetadata() == var36.getMetadata() && ItemStack.areItemStackTagsEqual(var31, var36)) {
                        int var40 = var2 == 0 ? var36.stackSize : 1;
                        if (var40 > var24.getItemStackLimit(var36) - var31.stackSize) {
                           var40 = var24.getItemStackLimit(var36) - var31.stackSize;
                        }

                        if (var40 > var36.getMaxStackSize() - var31.stackSize) {
                           var40 = var36.getMaxStackSize() - var31.stackSize;
                        }

                        var36.splitStack(var40);
                        if (var36.stackSize == 0) {
                           var9.setItemStack((ItemStack)null);
                        }

                        var31.stackSize += var40;
                     } else if (var36.stackSize <= var24.getItemStackLimit(var36)) {
                        var24.putStack(var36);
                        var9.setItemStack(var31);
                     }
                  } else if (var31.getItem() == var36.getItem()
                     && var36.getMaxStackSize() > 1
                     && (!var31.getHasSubtypes() || var31.getMetadata() == var36.getMetadata())
                     && ItemStack.areItemStackTagsEqual(var31, var36)) {
                     int var39 = var31.stackSize;
                     if (var39 > 0 && var39 + var36.stackSize <= var36.getMaxStackSize()) {
                        var36.stackSize += var39;
                        var31 = var24.decrStackSize(var39);
                        if (var31.stackSize == 0) {
                           var24.putStack((ItemStack)null);
                        }

                        var24.onPickupFromSlot(var4, var9.getItemStack());
                     }
                  }
               }

               var24.onSlotChanged();
            }
         }
      } else if (var3 == 2 && var2 >= 0 && var2 < 9) {
         Slot var22 = this.inventorySlots.get(var1);
         if (var22.canTakeStack(var4)) {
            ItemStack var29 = var9.getStackInSlot(var2);
            boolean var34 = var29 == null || var22.inventory == var9 && var22.isItemValid(var29);
            int var38 = -1;
            if (!var34) {
               var38 = var9.getFirstEmptyStack();
               var34 |= var38 > -1;
            }

            if (var22.getHasStack() && var34) {
               ItemStack var44 = var22.getStack();
               var9.setInventorySlotContents(var2, var44.copy());
               if ((var22.inventory != var9 || !var22.isItemValid(var29)) && var29 != null) {
                  if (var38 > -1) {
                     var9.addItemStackToInventory(var29);
                     var22.decrStackSize(var44.stackSize);
                     var22.putStack((ItemStack)null);
                     var22.onPickupFromSlot(var4, var44);
                  }
               } else {
                  var22.decrStackSize(var44.stackSize);
                  var22.putStack(var29);
                  var22.onPickupFromSlot(var4, var44);
               }
            } else if (!var22.getHasStack() && var29 != null && var22.isItemValid(var29)) {
               var9.setInventorySlotContents(var2, (ItemStack)null);
               var22.putStack(var29);
            }
         }
      } else if (var3 == 3 && var4.capabilities.isCreativeMode && var9.getItemStack() == null && var1 >= 0) {
         Slot var21 = this.inventorySlots.get(var1);
         if (var21 != null && var21.getHasStack()) {
            ItemStack var28 = var21.getStack().copy();
            var28.stackSize = var28.getMaxStackSize();
            var9.setItemStack(var28);
         }
      } else if (var3 == 4 && var9.getItemStack() == null && var1 >= 0) {
         if (var1 >= this.inventorySlots.size()) {
            return var8;
         }

         Slot var20 = this.inventorySlots.get(var1);
         if (var20 != null && var20.getHasStack() && var20.canTakeStack(var4)) {
            ItemStack var27 = var20.decrStackSize(var2 == 0 ? 1 : var20.getStack().stackSize);
            var20.onPickupFromSlot(var4, var27);
            var4.dropPlayerItemWithRandomChoice(var27, true);
         }
      } else if (var3 == 6 && var1 >= 0) {
         Slot var19 = this.inventorySlots.get(var1);
         ItemStack var26 = var9.getItemStack();
         if (var26 != null && (var19 == null || !var19.getHasStack() || !var19.canTakeStack(var4))) {
            int var33 = var2 == 0 ? 0 : this.inventorySlots.size() - 1;
            int var37 = var2 == 0 ? 1 : -1;

            for (int var43 = 0; var43 < 2; var43++) {
               for (int var46 = var33; var46 >= 0 && var46 < this.inventorySlots.size() && var26.stackSize < var26.getMaxStackSize(); var46 += var37) {
                  Slot var47 = this.inventorySlots.get(var46);
                  if (var47.getHasStack()
                     && canAddItemToSlot(var47, var26, true)
                     && var47.canTakeStack(var4)
                     && this.canMergeSlot(var26, var47)
                     && (var43 != 0 || var47.getStack().stackSize != var47.getStack().getMaxStackSize())) {
                     int var17 = Math.min(var26.getMaxStackSize() - var26.stackSize, var47.getStack().stackSize);
                     ItemStack var18 = var47.decrStackSize(var17);
                     var26.stackSize += var17;
                     if (var18.stackSize <= 0) {
                        var47.putStack((ItemStack)null);
                     }

                     var47.onPickupFromSlot(var4, var18);
                  }
               }
            }
         }

         this.detectAndSendChanges();
      }

      return var8;
   }

   public boolean canMergeSlot(ItemStack var1, Slot var2) {
      return true;
   }

   public void retrySlotClick(int var1, int var2, boolean var3, EntityPlayer var4) {
      this.slotClick(var1, var2, 1, var4);
   }

   public void onContainerClosed(EntityPlayer var1) {
      InventoryPlayer var5 = var1.inventory;
      if (var5.getItemStack() != null) {
         var1.dropPlayerItemWithRandomChoice(var5.getItemStack(), false);
         var5.setItemStack((ItemStack)null);
      }
   }

   public void onCraftMatrixChanged(IInventory var1) {
      this.detectAndSendChanges();
   }

   public void putStackInSlot(int var1, ItemStack var2) {
      this.getSlot(var1).putStack(var2);
   }

   public void putStacksInSlots(ItemStack[] var1) {
      for (int var5 = 0; var5 < var1.length; var5++) {
         this.getSlot(var5).putStack(var1[var5]);
      }
   }

   public void updateProgressBar(int var1, int var2) {
   }

   public short getNextTransactionID(InventoryPlayer var1) {
      this.transactionID++;
      return this.transactionID;
   }

   public boolean getCanCraft(EntityPlayer var1) {
      return !this.playerList.contains(var1);
   }

   public void setCanCraft(EntityPlayer var1, boolean var2) {
      if (var2) {
         this.playerList.remove(var1);
      } else {
         this.playerList.add(var1);
      }
   }

   public abstract boolean canInteractWith(EntityPlayer var1);

   public boolean mergeItemStack(ItemStack var1, int var2, int var3, boolean var4) {
      boolean var8 = false;
      int var9 = var2;
      if (var4) {
         var9 = var3 - 1;
      }

      if (var1.isStackable()) {
         while (var1.stackSize > 0 && (!var4 && var9 < var3 || var4 && var9 >= var2)) {
            Slot var10 = this.inventorySlots.get(var9);
            ItemStack var11 = var10.getStack();
            if (var11 != null
               && var11.getItem() == var1.getItem()
               && (!var1.getHasSubtypes() || var1.getMetadata() == var11.getMetadata())
               && ItemStack.areItemStackTagsEqual(var1, var11)) {
               int var12 = var11.stackSize + var1.stackSize;
               if (var12 <= var1.getMaxStackSize()) {
                  var1.stackSize = 0;
                  var11.stackSize = var12;
                  var10.onSlotChanged();
                  var8 = true;
               } else if (var11.stackSize < var1.getMaxStackSize()) {
                  var1.stackSize = var1.stackSize - (var1.getMaxStackSize() - var11.stackSize);
                  var11.stackSize = var1.getMaxStackSize();
                  var10.onSlotChanged();
                  var8 = true;
               }
            }

            if (var4) {
               var9--;
            } else {
               var9++;
            }
         }
      }

      if (var1.stackSize > 0) {
         if (var4) {
            var9 = var3 - 1;
         } else {
            var9 = var2;
         }

         while (!var4 && var9 < var3 || var4 && var9 >= var2) {
            Slot var14 = this.inventorySlots.get(var9);
            ItemStack var15 = var14.getStack();
            if (var15 == null) {
               var14.putStack(var1.copy());
               var14.onSlotChanged();
               var1.stackSize = 0;
               var8 = true;
               break;
            }

            if (var4) {
               var9--;
            } else {
               var9++;
            }
         }
      }

      return var8;
   }

   public static int U(int var0) {
      return var0 >> 2 & 3;
   }

   public static int p(int var0) {
      return var0 & 3;
   }

   public static int func_94534_d(int var0, int var1) {
      return var0 & 3 | (var1 & 3) << 2;
   }

   public static boolean isValidDragMode(int var0, EntityPlayer var1) {
      return var0 == 0 ? true : (var0 == 1 ? true : var0 == 2 && var1.capabilities.isCreativeMode);
   }

   public void resetDrag() {
      this.dragEvent = 0;
      this.dragSlots.clear();
   }

   public static boolean canAddItemToSlot(Slot var0, ItemStack var1, boolean var2) {
      boolean var6 = var0 == null || !var0.getHasStack();
      if (var0 != null && var0.getHasStack() && var1 != null && var1.isItemEqual(var0.getStack()) && ItemStack.areItemStackTagsEqual(var0.getStack(), var1)) {
         int var10002 = var2 ? 0 : var1.stackSize;
         var6 |= var0.getStack().stackSize + var10002 <= var1.getMaxStackSize();
      }

      return var6;
   }

   public static void computeStackSize(Set<Slot> var0, int var1, ItemStack var2, int var3) {
      switch (var1) {
         case 0:
            var2.stackSize = MathHelper.floor_float((float)var2.stackSize / var0.size());
            break;
         case 1:
            var2.stackSize = 1;
            break;
         case 2:
            var2.stackSize = var2.getItem().getItemStackLimit();
      }

      var2.stackSize += var3;
   }

   public boolean canDragIntoSlot(Slot var1) {
      return true;
   }

   public static int calcRedstone(TileEntity var0) {
      return var0 instanceof IInventory ? calcRedstoneFromInventory((IInventory)var0) : 0;
   }

   public static int calcRedstoneFromInventory(IInventory var0) {
      if (var0 == null) {
         return 0;
      }

      int var4 = 0;
      float var5 = 0.0F;

      for (int var6 = 0; var6 < var0.getSizeInventory(); var6++) {
         ItemStack var7 = var0.getStackInSlot(var6);
         if (var7 != null) {
            var5 += (float)var7.stackSize / Math.min(var0.getInventoryStackLimit(), var7.getMaxStackSize());
            var4++;
         }
      }

      var5 /= var0.getSizeInventory();
      return MathHelper.floor_float(var5 * 14.0F) + (var4 > 0 ? 1 : 0);
   }
}
