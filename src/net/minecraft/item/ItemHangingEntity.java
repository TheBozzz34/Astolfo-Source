package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
   private final Class<? extends EntityHanging> hangingEntityClass;
   private static final long a = 74612639432547L;

   public ItemHangingEntity(Class<? extends EntityHanging> var1) {
      this.hangingEntityClass = var1;
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 == EnumFacing.DOWN) {
         return false;
      }

      if (var5 == EnumFacing.UP) {
         return false;
      }

      BlockPos var12 = var4.offset(var5);
      if (!var2.canPlayerEdit(var12, var5, var1)) {
         return false;
      }

      EntityHanging var13 = this.createEntity(var3, var12, var5);
      if (var13 != null && var13.onValidSurface()) {
         if (!var3.isRemote) {
            var3.spawnEntityInWorld(var13);
         }

         var1.stackSize--;
      }

      return true;
   }

   private EntityHanging createEntity(World var1, BlockPos var2, EnumFacing var3) {
      return this.hangingEntityClass == EntityPainting.class
         ? new EntityPainting(var1, var2, var3)
         : (this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(var1, var2, var3) : null);
   }
}
