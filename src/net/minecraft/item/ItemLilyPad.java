package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemColored {
   private static final long d = 78110912262362L;

   public ItemLilyPad(Block var1) {
      super(var1, false);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      MovingObjectPosition var7 = this.getMovingObjectPositionFromPlayer(var2, var3, true);
      if (var7 == null) {
         return var1;
      }

      if (var7.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         BlockPos var8 = var7.getBlockPos();
         if (!var2.isBlockModifiable(var3, var8)) {
            return var1;
         }

         if (!var3.canPlayerEdit(var8.offset(var7.sideHit), var7.sideHit, var1)) {
            return var1;
         }

         BlockPos var9 = var8.up();
         IBlockState var10 = var2.getBlockState(var8);
         if (var10.getBlock().getMaterial() == Material.water && var10.getValue(BlockLiquid.LEVEL) == 0 && var2.isAirBlock(var9)) {
            var2.setBlockState(var9, Blocks.waterlily.getDefaultState());
            if (!var3.capabilities.isCreativeMode) {
               var1.stackSize--;
            }

            var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         }
      }

      return var1;
   }

   @Override
   public int getColorFromItemStack(ItemStack var1, int var2) {
      return Blocks.waterlily.getRenderColor(Blocks.waterlily.getStateFromMeta(var1.getMetadata()));
   }
}
