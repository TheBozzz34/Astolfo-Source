package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class EntityItemFrame extends EntityHanging {
   private float itemDropChance = 1.0F;
   private static final long bb = 102780455373603L;

   public EntityItemFrame(World worldIn) {
      super(worldIn);
   }

   public EntityItemFrame(World worldIn, BlockPos blockPos, EnumFacing enumFacing) {
      super(worldIn, blockPos);
      this.updateFacingWithBoundingBox(enumFacing);
   }

   @Override
   public void entityInit() {
      this.getDataWatcher().addObjectByDataType(8, 5);
      this.getDataWatcher().addObject(9, (byte)0);
   }

   @Override
   public float getCollisionBorderSize() {
      return 0.0F;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (!source.isExplosion() && this.getDisplayedItem() != null) {
         if (!this.worldObj.isRemote) {
            this.dropItemOrSelf(source.getEntity(), false);
            this.setDisplayedItem((ItemStack)null);
         }

         return true;
      } else {
         return super.attackEntityFrom(source, amount);
      }
   }

   @Override
   public int getWidthPixels() {
      return 12;
   }

   @Override
   public int getHeightPixels() {
      return 12;
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      double d = 16.0;
      d = d * 64.0 * this.renderDistanceWeight;
      return distance < d * d;
   }

   @Override
   public void onBroken(Entity brokenEntity) {
      this.dropItemOrSelf(brokenEntity, true);
   }

   public void dropItemOrSelf(Entity entity, boolean bl) {
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack itemStack = this.getDisplayedItem();
         if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            if (entityPlayer.capabilities.isCreativeMode) {
               this.removeFrameFromMap(itemStack);
               return;
            }
         }

         if (bl) {
            this.entityDropItem(new ItemStack(Items.item_frame), 0.0F);
         }

         if (itemStack != null && this.rand.nextFloat() < this.itemDropChance) {
            itemStack = itemStack.copy();
            this.removeFrameFromMap(itemStack);
            this.entityDropItem(itemStack, 0.0F);
         }
      }
   }

   private void removeFrameFromMap(ItemStack itemStack) {
      if (itemStack != null) {
         if (itemStack.getItem() == Items.filled_map) {
            MapData mapData = ((ItemMap)itemStack.getItem()).getMapData(itemStack, this.worldObj);
            mapData.mapDecorations.remove("frame-" + this.getEntityId());
         }

         itemStack.setItemFrame((EntityItemFrame)null);
      }
   }

   public ItemStack getDisplayedItem() {
      return this.getDataWatcher().getWatchableObjectItemStack(8);
   }

   public void setDisplayedItem(ItemStack itemStack) {
      this.setDisplayedItemWithUpdate(itemStack, true);
   }

   private void setDisplayedItemWithUpdate(ItemStack itemStack, boolean bl) {
      if (itemStack != null) {
         itemStack = itemStack.copy();
         itemStack.stackSize = 1;
         itemStack.setItemFrame(this);
      }

      this.getDataWatcher().updateObject(8, itemStack);
      this.getDataWatcher().setObjectWatched(8);
      if (bl && this.hangingPosition != null) {
         this.worldObj.updateComparatorOutputLevel(this.hangingPosition, Blocks.air);
      }
   }

   public int getRotation() {
      return this.getDataWatcher().getWatchableObjectByte(9);
   }

   public void setItemRotation(int i) {
      this.func_174865_a(i, true);
   }

   private void func_174865_a(int i, boolean bl) {
      this.getDataWatcher().updateObject(9, (byte)(i % 8));
      if (bl && this.hangingPosition != null) {
         this.worldObj.updateComparatorOutputLevel(this.hangingPosition, Blocks.air);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      if (this.getDisplayedItem() != null) {
         tagCompound.setTag("Item", this.getDisplayedItem().writeToNBT(new NBTTagCompound()));
         tagCompound.setByte("ItemRotation", (byte)this.getRotation());
         tagCompound.setFloat("ItemDropChance", this.itemDropChance);
      }

      super.writeEntityToNBT(tagCompound);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      NBTTagCompound nbtTagCompound = tagCompund.getCompoundTag("Item");
      if (nbtTagCompound != null && !nbtTagCompound.hasNoTags()) {
         this.setDisplayedItemWithUpdate(ItemStack.loadItemStackFromNBT(nbtTagCompound), false);
         this.func_174865_a(tagCompund.getByte("ItemRotation"), false);
         if (tagCompund.hasKey("ItemDropChance", 99)) {
            this.itemDropChance = tagCompund.getFloat("ItemDropChance");
         }

         if (tagCompund.hasKey("Direction")) {
            this.func_174865_a(this.getRotation() * 2, false);
         }
      }

      super.readEntityFromNBT(tagCompund);
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if (this.getDisplayedItem() == null) {
         ItemStack itemStack = playerIn.getHeldItem();
         if (itemStack != null && !this.worldObj.isRemote) {
            this.setDisplayedItem(itemStack);
            if (!playerIn.capabilities.isCreativeMode && --itemStack.stackSize <= 0) {
               playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack)null);
            }
         }
      } else if (!this.worldObj.isRemote) {
         this.setItemRotation(this.getRotation() + 1);
      }

      return true;
   }

   public int func_174866_q() {
      return this.getDisplayedItem() == null ? 0 : this.getRotation() % 8 + 1;
   }
}
