package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

public class BlockSponge extends Block {
   public static final PropertyBool WET = PropertyBool.create("wet");
   private static final long e = 89054990352693L;

   public BlockSponge() {
      super(Material.sponge);
      this.setDefaultState(this.blockState.getBaseState().withProperty(WET, false));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + ".dry.name");
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(WET) ? 1 : 0;
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      this.tryAbsorb(var1, var2, var3);
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.tryAbsorb(var1, var2, var3);
      super.onNeighborBlockChange(var1, var2, var3, var4);
   }

   public void tryAbsorb(World var1, BlockPos var2, IBlockState var3) {
      if (!var3.getValue(WET) && this.absorb(var1, var2)) {
         var1.setBlockState(var2, var3.withProperty(WET, true), 2);
         var1.playAuxSFX(2001, var2, Block.getIdFromBlock(Blocks.water));
      }
   }

   private boolean absorb(World var1, BlockPos var2) {
      LinkedList var6 = Lists.newLinkedList();
      ArrayList var7 = Lists.newArrayList();
      var6.add(new Tuple<>(var2, 0));
      int var8 = 0;

      while (!var6.isEmpty()) {
         Tuple var9 = (Tuple)var6.poll();
         BlockPos var10 = (BlockPos)var9.getFirst();
         int var11 = (Integer)var9.getSecond();

         for (EnumFacing var15 : EnumFacing.values()) {
            BlockPos var16 = var10.offset(var15);
            if (var1.getBlockState(var16).getBlock().getMaterial() == Material.water) {
               var1.setBlockState(var16, Blocks.air.getDefaultState(), 2);
               var7.add(var16);
               var8++;
               if (var11 < 6) {
                  var6.add(new Tuple<>(var16, var11 + 1));
               }
            }
         }

         if (var8 > 64) {
            break;
         }
      }

      for (Object var18$raw : var7) {
         BlockPos var18 = (BlockPos)var18$raw;
         var1.notifyNeighborsOfStateChange(var18, Blocks.air);
      }

      return var8 > 0;
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(WET, (var1 & 1) == 1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(WET) ? 1 : 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, WET);
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var3.getValue(WET)) {
         EnumFacing var8 = EnumFacing.random(var4);
         if (var8 != EnumFacing.UP && !World.doesBlockHaveSolidTopSurface(var1, var2.offset(var8))) {
            double var9 = var2.getX();
            double var11 = var2.getY();
            double var13 = var2.getZ();
            if (var8 == EnumFacing.DOWN) {
               var11 -= 0.05;
               var9 += var4.nextDouble();
               var13 += var4.nextDouble();
            } else {
               var11 += var4.nextDouble() * 0.8;
               if (var8.getAxis() == EnumFacing.Axis.X) {
                  var13 += var4.nextDouble();
                  if (var8 == EnumFacing.EAST) {
                     var9++;
                  } else {
                     var9 += 0.05;
                  }
               } else {
                  var9 += var4.nextDouble();
                  if (var8 == EnumFacing.SOUTH) {
                     var13++;
                  } else {
                     var13 += 0.05;
                  }
               }
            }

            var1.spawnParticle(EnumParticleTypes.DRIP_WATER, var9, var11, var13, 0.0, 0.0, 0.0);
         }
      }
   }
}
