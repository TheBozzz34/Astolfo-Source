package net.minecraft.entity.ai;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class EntityAIVillagerInteract extends EntityAIWatchClosest2 {
   private int interactionDelay;
   private EntityVillager villager;

   public EntityAIVillagerInteract(EntityVillager villagerIn) {
      super(villagerIn, EntityVillager.class, 3.0F, 0.02F);
      this.villager = villagerIn;
   }

   @Override
   public void startExecuting() {
      super.startExecuting();
      if (this.villager.canAbondonItems() && this.closestEntity instanceof EntityVillager && ((EntityVillager)this.closestEntity).func_175557_cr()) {
         this.interactionDelay = 10;
      } else {
         this.interactionDelay = 0;
      }
   }

   @Override
   public void updateTask() {
      super.updateTask();
      if (this.interactionDelay > 0) {
         this.interactionDelay--;
         if (this.interactionDelay == 0) {
            InventoryBasic inventoryBasic = this.villager.getVillagerInventory();

            for (int i = 0; i < inventoryBasic.getSizeInventory(); i++) {
               ItemStack itemStack = inventoryBasic.getStackInSlot(i);
               ItemStack itemStack2 = null;
               if (itemStack != null) {
                  Item item = itemStack.getItem();
                  if ((item == Items.bread || item == Items.potato || item == Items.carrot) && itemStack.stackSize > 3) {
                     int j = itemStack.stackSize / 2;
                     itemStack.stackSize -= j;
                     itemStack2 = new ItemStack(item, j, itemStack.getMetadata());
                  } else if (item == Items.wheat && itemStack.stackSize > 5) {
                     int k = itemStack.stackSize / 2 / 3 * 3;
                     int l = k / 3;
                     itemStack.stackSize -= k;
                     itemStack2 = new ItemStack(Items.bread, l, 0);
                  }

                  if (itemStack.stackSize <= 0) {
                     inventoryBasic.setInventorySlotContents(i, (ItemStack)null);
                  }
               }

               if (itemStack2 != null) {
                  double d = this.villager.posY - 0.3F + this.villager.getEyeHeight();
                  EntityItem entityItem = new EntityItem(this.villager.worldObj, this.villager.posX, d, this.villager.posZ, itemStack2);
                  float f = 0.3F;
                  float g = this.villager.rotationYawHead;
                  float h = this.villager.rotationPitch;
                  entityItem.motionX = -MathHelper.sin(g / 180.0F * (float) Math.PI) * MathHelper.cos(h / 180.0F * (float) Math.PI) * f;
                  entityItem.motionZ = MathHelper.cos(g / 180.0F * (float) Math.PI) * MathHelper.cos(h / 180.0F * (float) Math.PI) * f;
                  entityItem.motionY = -MathHelper.sin(h / 180.0F * (float) Math.PI) * f + 0.1F;
                  entityItem.setDefaultPickupDelay();
                  this.villager.worldObj.spawnEntityInWorld(entityItem);
                  break;
               }
            }
         }
      }
   }
}
