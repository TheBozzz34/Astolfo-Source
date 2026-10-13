package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityChest extends TileEntityLockable implements ITickable, IInventory {
   private ItemStack[] chestContents = new ItemStack[27];
   public boolean adjacentChestChecked;
   public TileEntityChest adjacentChestZNeg;
   public TileEntityChest adjacentChestXPos;
   public TileEntityChest adjacentChestXNeg;
   public TileEntityChest adjacentChestZPos;
   public float lidAngle;
   public float prevLidAngle;
   public int numPlayersUsing;
   private int ticksSinceSync;
   private int cachedChestType;
   private String customName;
   private static final long a = 133053887856819L;

   public TileEntityChest() {
      this.cachedChestType = -1;
   }

   public TileEntityChest(int var1) {
      this.cachedChestType = var1;
   }

   @Override
   public int getSizeInventory() {
      return 27;
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return this.chestContents[var1];
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      if (this.chestContents[var1] != null) {
         if (this.chestContents[var1].stackSize <= var2) {
            ItemStack var7 = this.chestContents[var1];
            this.chestContents[var1] = null;
            this.markDirty();
            return var7;
         }

         ItemStack var6 = this.chestContents[var1].splitStack(var2);
         if (this.chestContents[var1].stackSize == 0) {
            this.chestContents[var1] = null;
         }

         this.markDirty();
         return var6;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      if (this.chestContents[var1] != null) {
         ItemStack var5 = this.chestContents[var1];
         this.chestContents[var1] = null;
         return var5;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      this.chestContents[var1] = var2;
      if (var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
         var2.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.chest";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String var1) {
      this.customName = var1;
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      NBTTagList var5 = var1.getTagList("Items", 10);
      this.chestContents = new ItemStack[this.getSizeInventory()];
      if (var1.hasKey("CustomName", 8)) {
         this.customName = var1.getString("CustomName");
      }

      for (int var6 = 0; var6 < var5.tagCount(); var6++) {
         NBTTagCompound var7 = var5.getCompoundTagAt(var6);
         int var8 = var7.getByte("Slot") & 255;
         if (var8 >= 0 && var8 < this.chestContents.length) {
            this.chestContents[var8] = ItemStack.loadItemStackFromNBT(var7);
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      NBTTagList var5 = new NBTTagList();

      for (int var6 = 0; var6 < this.chestContents.length; var6++) {
         if (this.chestContents[var6] != null) {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setByte("Slot", (byte)var6);
            this.chestContents[var6].writeToNBT(var7);
            var5.appendTag(var7);
         }
      }

      var1.setTag("Items", var5);
      if (this.hasCustomName()) {
         var1.setString("CustomName", this.customName);
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
   public void updateContainingBlockInfo() {
      super.updateContainingBlockInfo();
      this.adjacentChestChecked = false;
   }

   private void func_174910_a(TileEntityChest var1, EnumFacing var2) {
      if (var1.isInvalid()) {
         this.adjacentChestChecked = false;
      } else if (this.adjacentChestChecked) {
         switch (var2) {
            case NORTH:
               if (this.adjacentChestZNeg != var1) {
                  this.adjacentChestChecked = false;
               }
               break;
            case SOUTH:
               if (this.adjacentChestZPos != var1) {
                  this.adjacentChestChecked = false;
               }
               break;
            case EAST:
               if (this.adjacentChestXPos != var1) {
                  this.adjacentChestChecked = false;
               }
               break;
            case WEST:
               if (this.adjacentChestXNeg != var1) {
                  this.adjacentChestChecked = false;
               }
         }
      }
   }

   public void checkForAdjacentChests() {
      if (!this.adjacentChestChecked) {
         this.adjacentChestChecked = true;
         this.adjacentChestXNeg = this.getAdjacentChest(EnumFacing.WEST);
         this.adjacentChestXPos = this.getAdjacentChest(EnumFacing.EAST);
         this.adjacentChestZNeg = this.getAdjacentChest(EnumFacing.NORTH);
         this.adjacentChestZPos = this.getAdjacentChest(EnumFacing.SOUTH);
      }
   }

   public TileEntityChest getAdjacentChest(EnumFacing var1) {
      BlockPos var5 = this.pos.offset(var1);
      if (this.isChestAt(var5)) {
         TileEntity var6 = this.worldObj.getTileEntity(var5);
         if (var6 instanceof TileEntityChest) {
            TileEntityChest var7 = (TileEntityChest)var6;
            var7.func_174910_a(this, var1.getOpposite());
            return var7;
         }
      }

      return null;
   }

   private boolean isChestAt(BlockPos var1) {
      if (this.worldObj == null) {
         return false;
      }

      Block var5 = this.worldObj.getBlockState(var1).getBlock();
      return var5 instanceof BlockChest && ((BlockChest)var5).chestType == this.getChestType();
   }

   @Override
   public void update() {
      this.checkForAdjacentChests();
      int var4 = this.pos.getX();
      int var5 = this.pos.getY();
      int var6 = this.pos.getZ();
      this.ticksSinceSync++;
      if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + var4 + var5 + var6) % 200 == 0) {
         this.numPlayersUsing = 0;
         float var7 = 5.0F;

         for (EntityPlayer var9 : this.worldObj
            .getEntitiesWithinAABB(
               EntityPlayer.class, new AxisAlignedBB(var4 - var7, var5 - var7, var6 - var7, var4 + 1 + var7, var5 + 1 + var7, var6 + 1 + var7)
            )) {
            if (var9.openContainer instanceof ContainerChest) {
               IInventory var10 = ((ContainerChest)var9.openContainer).getLowerChestInventory();
               if (var10 == this || var10 instanceof InventoryLargeChest && ((InventoryLargeChest)var10).isPartOfLargeChest(this)) {
                  this.numPlayersUsing++;
               }
            }
         }
      }

      this.prevLidAngle = this.lidAngle;
      float var14 = 0.1F;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
         double var15 = var4 + 0.5;
         double var18 = var6 + 0.5;
         if (this.adjacentChestZPos != null) {
            var18 += 0.5;
         }

         if (this.adjacentChestXPos != null) {
            var15 += 0.5;
         }

         this.worldObj.playSoundEffect(var15, var5 + 0.5, var18, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float var16 = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += var14;
         } else {
            this.lidAngle -= var14;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float var17 = 0.5F;
         if (this.lidAngle < var17 && var16 >= var17 && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
            double var19 = var4 + 0.5;
            double var12 = var6 + 0.5;
            if (this.adjacentChestZPos != null) {
               var12 += 0.5;
            }

            if (this.adjacentChestXPos != null) {
               var19 += 0.5;
            }

            this.worldObj.playSoundEffect(var19, var5 + 0.5, var12, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }
   }

   @Override
   public boolean receiveClientEvent(int var1, int var2) {
      if (var1 == 1) {
         this.numPlayersUsing = var2;
         return true;
      } else {
         return super.receiveClientEvent(var1, var2);
      }
   }

   @Override
   public void openInventory(EntityPlayer var1) {
      if (!var1.isSpectator()) {
         if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
         }

         this.numPlayersUsing++;
         this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
         this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
         this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
      }
   }

   @Override
   public void closeInventory(EntityPlayer var1) {
      if (!var1.isSpectator() && this.getBlockType() instanceof BlockChest) {
         this.numPlayersUsing--;
         this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
         this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
         this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
      }
   }

   @Override
   public boolean isItemValidForSlot(int var1, ItemStack var2) {
      return true;
   }

   @Override
   public void invalidate() {
      super.invalidate();
      this.updateContainingBlockInfo();
      this.checkForAdjacentChests();
   }

   public int getChestType() {
      if (this.cachedChestType == -1) {
         if (this.worldObj == null || !(this.getBlockType() instanceof BlockChest)) {
            return 0;
         }

         this.cachedChestType = ((BlockChest)this.getBlockType()).chestType;
      }

      return this.cachedChestType;
   }

   @Override
   public String getGuiID() {
      return "minecraft:chest";
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerChest(var1, this, var2);
   }

   @Override
   public int getField(int var1) {
      return 0;
   }

   @Override
   public void setField(int var1, int var2) {
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void clear() {
      for (int var4 = 0; var4 < this.chestContents.length; var4++) {
         this.chestContents[var4] = null;
      }
   }
}
