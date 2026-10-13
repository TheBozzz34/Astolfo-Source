package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch {
   private static Map<World, List<BlockRedstoneTorch.Toggle>> toggles = Maps.newHashMap();
   private final boolean isOn;
   private static final long i = 43754363327437L;

   private boolean isBurnedOut(World var1, BlockPos var2, boolean var3) {
      if (!toggles.containsKey(var1)) {
         toggles.put(var1, Lists.newArrayList());
      }

      List var7 = toggles.get(var1);
      if (var3) {
         var7.add(new BlockRedstoneTorch.Toggle(var2, var1.getTotalWorldTime()));
      }

      int var8 = 0;

      for (int var9 = 0; var9 < var7.size(); var9++) {
         BlockRedstoneTorch.Toggle var10 = (BlockRedstoneTorch.Toggle)var7.get(var9);
         if (var10.pos.equals(var2)) {
            if (++var8 >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   public BlockRedstoneTorch(boolean var1) {
      this.isOn = var1;
      this.setTickRandomly(true);
      this.setCreativeTab((CreativeTabs)null);
   }

   @Override
   public int tickRate(World var1) {
      return 2;
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      if (this.isOn) {
         for (EnumFacing var10 : EnumFacing.values()) {
            var1.notifyNeighborsOfStateChange(var2.offset(var10), this);
         }
      }
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      if (this.isOn) {
         for (EnumFacing var10 : EnumFacing.values()) {
            var1.notifyNeighborsOfStateChange(var2.offset(var10), this);
         }
      }
   }

   @Override
   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return this.isOn && var3.getValue(FACING) != var4 ? 15 : 0;
   }

   private boolean shouldBeOff(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = var3.getValue(FACING).getOpposite();
      return var1.isSidePowered(var2.offset(var4), var4);
   }

   @Override
   public void randomTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      boolean var8 = this.shouldBeOff(var1, var2, var3);
      List var9 = toggles.get(var1);

      while (var9 != null && !var9.isEmpty() && var1.getTotalWorldTime() - ((BlockRedstoneTorch.Toggle)var9.get(0)).time > 60L) {
         var9.remove(0);
      }

      if (this.isOn) {
         if (var8) {
            var1.setBlockState(var2, Blocks.unlit_redstone_torch.getDefaultState().withProperty(FACING, var3.getValue(FACING)), 3);
            if (this.isBurnedOut(var1, var2, true)) {
               var1.playSoundEffect(
                  var2.getX() + 0.5F,
                  var2.getY() + 0.5F,
                  var2.getZ() + 0.5F,
                  "random.fizz",
                  0.5F,
                  2.6F + (var1.rand.nextFloat() - var1.rand.nextFloat()) * 0.8F
               );

               for (int var10 = 0; var10 < 5; var10++) {
                  double var11 = var2.getX() + var4.nextDouble() * 0.6 + 0.2;
                  double var13 = var2.getY() + var4.nextDouble() * 0.6 + 0.2;
                  double var15 = var2.getZ() + var4.nextDouble() * 0.6 + 0.2;
                  var1.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var11, var13, var15, 0.0, 0.0, 0.0);
               }

               var1.scheduleUpdate(var2, var1.getBlockState(var2).getBlock(), 160);
            }
         }
      } else if (!var8 && !this.isBurnedOut(var1, var2, false)) {
         var1.setBlockState(var2, Blocks.redstone_torch.getDefaultState().withProperty(FACING, var3.getValue(FACING)), 3);
      }
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.onNeighborChangeInternal(var1, var2, var3) && this.isOn == this.shouldBeOff(var1, var2, var3)) {
         var1.scheduleUpdate(var2, this, this.tickRate(var1));
      }
   }

   @Override
   public int getStrongPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return var4 == EnumFacing.DOWN ? this.getWeakPower(var1, var2, var3, var4) : 0;
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Item.getItemFromBlock(Blocks.redstone_torch);
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.isOn) {
         double var8 = var2.getX() + 0.5 + (var4.nextDouble() - 0.5) * 0.2;
         double var10 = var2.getY() + 0.7 + (var4.nextDouble() - 0.5) * 0.2;
         double var12 = var2.getZ() + 0.5 + (var4.nextDouble() - 0.5) * 0.2;
         EnumFacing var14 = var3.getValue(FACING);
         if (var14.getAxis().isHorizontal()) {
            EnumFacing var15 = var14.getOpposite();
            var8 += 0.27 * var15.getFrontOffsetX();
            var10 += 0.22;
            var12 += 0.27 * var15.getFrontOffsetZ();
         }

         var1.spawnParticle(EnumParticleTypes.REDSTONE, var8, var10, var12, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Item.getItemFromBlock(Blocks.redstone_torch);
   }

   @Override
   public boolean isAssociatedBlock(Block var1) {
      return var1 == Blocks.unlit_redstone_torch || var1 == Blocks.redstone_torch;
   }

   public static class Toggle {
      public BlockPos pos;
      public long time;

      public Toggle(BlockPos var1, long var2) {
         this.pos = var1;
         this.time = var2;
      }
   }
}
