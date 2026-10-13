package net.minecraft.entity.passive;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityMooshroom extends EntityCow {
   private static final long hb = 111690236893589L;

   public EntityMooshroom(World var1) {
      super(var1);
      this.setSize(0.9F, 1.3F);
      this.spawnableBlock = Blocks.mycelium;
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      ItemStack var5 = var1.inventory.getCurrentItem();
      if (var5 != null && var5.getItem() == Items.bowl && this.getGrowingAge() >= 0) {
         if (var5.stackSize == 1) {
            var1.inventory.setInventorySlotContents(var1.inventory.currentItem, new ItemStack(Items.mushroom_stew));
            return true;
         }

         if (var1.inventory.addItemStackToInventory(new ItemStack(Items.mushroom_stew)) && !var1.capabilities.isCreativeMode) {
            var1.inventory.decrStackSize(var1.inventory.currentItem, 1);
            return true;
         }
      }

      if (var5 != null && var5.getItem() == Items.shears && this.getGrowingAge() >= 0) {
         this.setDead();
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY + this.height / 2.0F, this.posZ, 0.0, 0.0, 0.0);
         if (!this.worldObj.isRemote) {
            EntityCow var6 = new EntityCow(this.worldObj);
            var6.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            var6.setHealth(this.getHealth());
            var6.renderYawOffset = this.renderYawOffset;
            if (this.hasCustomName()) {
               var6.setCustomNameTag(this.getCustomNameTag());
            }

            this.worldObj.spawnEntityInWorld(var6);

            for (int var7 = 0; var7 < 5; var7++) {
               this.worldObj
                  .spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY + this.height, this.posZ, new ItemStack(Blocks.red_mushroom)));
            }

            var5.damageItem(1, var1);
            this.playSound("mob.sheep.shear", 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.interact(var1);
      }
   }

   public EntityMooshroom createChild(EntityAgeable var1) {
      return new EntityMooshroom(this.worldObj);
   }
}
