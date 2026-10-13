package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemShears extends Item {
   private static final long a = 64653354698640L;
   private static final long b = -8908532401274093330L;

   public ItemShears() {
      this.setMaxStackSize(1);
      this.setMaxDamage((int)b);
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   @Override
   public boolean onBlockDestroyed(ItemStack var1, World var2, Block var3, BlockPos var4, EntityLivingBase var5) {
      if (var3.getMaterial() != Material.leaves
         && var3 != Blocks.web
         && var3 != Blocks.tallgrass
         && var3 != Blocks.vine
         && var3 != Blocks.tripwire
         && var3 != Blocks.wool) {
         return super.onBlockDestroyed(var1, var2, var3, var4, var5);
      }

      var1.damageItem(1, var5);
      return true;
   }

   @Override
   public boolean canHarvestBlock(Block var1) {
      return var1 == Blocks.web || var1 == Blocks.redstone_wire || var1 == Blocks.tripwire;
   }

   @Override
   public float getStrVsBlock(ItemStack var1, Block var2) {
      return var2 != Blocks.web && var2.getMaterial() != Material.leaves ? (var2 == Blocks.wool ? 5.0F : super.getStrVsBlock(var1, var2)) : 15.0F;
   }
}
