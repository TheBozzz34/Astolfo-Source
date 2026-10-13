package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockFalling extends Block {
   public static boolean fallInstantly;
   private static final long l = 126863169432757L;
   private static final long m = 5011311478116974624L;

   public BlockFalling() {
      super(Material.sand);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public BlockFalling(Material var1) {
      super(var1);
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      var1.scheduleUpdate(var2, this, this.tickRate(var1));
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      var1.scheduleUpdate(var2, this, this.tickRate(var1));
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.isRemote) {
         this.checkFallable(var1, var2);
      }
   }

   private void checkFallable(World var1, BlockPos var2) {
      if (canFallInto(var1, var2.down()) && var2.getY() >= 0) {
         int var6 = (int)m;
         if (fallInstantly || !var1.isAreaLoaded(var2.add(-var6, -var6, -var6), var2.add(var6, var6, var6))) {
            var1.setBlockToAir(var2);
            BlockPos var8 = var2.down();

            while (canFallInto(var1, var8) && var8.getY() > 0) {
               var8 = var8.down();
            }

            if (var8.getY() > 0) {
               var1.setBlockState(var8.up(), this.getDefaultState());
            }
         } else if (!var1.isRemote) {
            EntityFallingBlock var7 = new EntityFallingBlock(var1, var2.getX() + 0.5, var2.getY(), var2.getZ() + 0.5, var1.getBlockState(var2));
            this.onStartFalling(var7);
            var1.spawnEntityInWorld(var7);
         }
      }
   }

   public void onStartFalling(EntityFallingBlock var1) {
   }

   @Override
   public int tickRate(World var1) {
      return 2;
   }

   public static boolean canFallInto(World var0, BlockPos var1) {
      Block var5 = var0.getBlockState(var1).getBlock();
      Material var6 = var5.blockMaterial;
      return var5 == Blocks.fire || var6 == Material.air || var6 == Material.water || var6 == Material.lava;
   }

   public void onEndFalling(World var1, BlockPos var2) {
   }
}
