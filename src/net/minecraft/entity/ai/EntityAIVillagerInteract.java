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
   private static final long a = 107553884856420L;
   private static final long d = -8619613692188688374L;

   public EntityAIVillagerInteract(EntityVillager var1) {
      super(var1, EntityVillager.class, 3.0F, 0.02F);
      this.villager = var1;
   }

   @Override
   public void startExecuting() {
      super.startExecuting();
      if (this.villager.canAbondonItems() && this.closestEntity instanceof EntityVillager && ((EntityVillager)this.closestEntity).func_175557_cr()) {
         this.interactionDelay = (int)d;
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
            InventoryBasic var4 = this.villager.getVillagerInventory();

            for (int var5 = 0; var5 < var4.getSizeInventory(); var5++) {
               ItemStack var6 = var4.getStackInSlot(var5);
               ItemStack var7 = null;
               if (var6 != null) {
                  Item var8 = var6.getItem();
                  if ((var8 == Items.bread || var8 == Items.potato || var8 == Items.carrot) && var6.stackSize > 3) {
                     int var15 = var6.stackSize / 2;
                     var6.stackSize -= var15;
                     var7 = new ItemStack(var8, var15, var6.getMetadata());
                  } else if (var8 == Items.wheat && var6.stackSize > 5) {
                     int var9 = var6.stackSize / 2 / 3 * 3;
                     int var10 = var9 / 3;
                     var6.stackSize -= var9;
                     var7 = new ItemStack(Items.bread, var10, 0);
                  }

                  if (var6.stackSize <= 0) {
                     var4.setInventorySlotContents(var5, (ItemStack)null);
                  }
               }

               if (var7 != null) {
                  double var14 = this.villager.posY - 0.3F + this.villager.getEyeHeight();
                  EntityItem var16 = new EntityItem(this.villager.worldObj, this.villager.posX, var14, this.villager.posZ, var7);
                  float var11 = 0.3F;
                  float var12 = this.villager.rotationYawHead;
                  float var13 = this.villager.rotationPitch;
                  var16.motionX = -MathHelper.sin(var12 / 180.0F * (float) Math.PI) * MathHelper.cos(var13 / 180.0F * (float) Math.PI) * var11;
                  var16.motionZ = MathHelper.cos(var12 / 180.0F * (float) Math.PI) * MathHelper.cos(var13 / 180.0F * (float) Math.PI) * var11;
                  var16.motionY = -MathHelper.sin(var13 / 180.0F * (float) Math.PI) * var11 + 0.1F;
                  var16.setDefaultPickupDelay();
                  this.villager.worldObj.spawnEntityInWorld(var16);
                  break;
               }
            }
         }
      }
   }
}
