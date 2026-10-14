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
   public int damageDropped(IBlockState state) {
      return state.getValue(WET) ? 1 : 0;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.tryAbsorb(worldIn, pos, state);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      this.tryAbsorb(worldIn, pos, state);
      super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
   }

   public void tryAbsorb(World worldIn, BlockPos pos, IBlockState state) {
      if (!state.getValue(WET) && this.absorb(worldIn, pos)) {
         worldIn.setBlockState(pos, state.withProperty(WET, true), 2);
         worldIn.playAuxSFX(2001, pos, Block.getIdFromBlock(Blocks.water));
      }
   }

   private boolean absorb(World worldIn, BlockPos pos) {
      LinkedList linkedList = Lists.newLinkedList();
      ArrayList arrayList = Lists.newArrayList();
      linkedList.add(new Tuple<>(pos, 0));
      int i = 0;

      while (!linkedList.isEmpty()) {
         Tuple tuple = (Tuple)linkedList.poll();
         BlockPos blockPos = (BlockPos)tuple.getFirst();
         int j = (Integer)tuple.getSecond();

         for (EnumFacing enumFacing : EnumFacing.values()) {
            BlockPos blockPos2 = blockPos.offset(enumFacing);
            if (worldIn.getBlockState(blockPos2).getBlock().getMaterial() == Material.water) {
               worldIn.setBlockState(blockPos2, Blocks.air.getDefaultState(), 2);
               arrayList.add(blockPos2);
               i++;
               if (j < 6) {
                  linkedList.add(new Tuple<>(blockPos2, j + 1));
               }
            }
         }

         if (i > 64) {
            break;
         }
      }

      for (Object object : arrayList) {
         BlockPos blockPos3 = (BlockPos)object;
         worldIn.notifyNeighborsOfStateChange(blockPos3, Blocks.air);
      }

      return i > 0;
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      list.add(new ItemStack(itemIn, 1, 0));
      list.add(new ItemStack(itemIn, 1, 1));
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(WET, (meta & 1) == 1);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(WET) ? 1 : 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, WET);
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (state.getValue(WET)) {
         EnumFacing enumFacing = EnumFacing.random(rand);
         if (enumFacing != EnumFacing.UP && !World.doesBlockHaveSolidTopSurface(worldIn, pos.offset(enumFacing))) {
            double d = pos.getX();
            double e = pos.getY();
            double d2 = pos.getZ();
            if (enumFacing == EnumFacing.DOWN) {
               e -= 0.05;
               d += rand.nextDouble();
               d2 += rand.nextDouble();
            } else {
               e += rand.nextDouble() * 0.8;
               if (enumFacing.getAxis() == EnumFacing.Axis.X) {
                  d2 += rand.nextDouble();
                  if (enumFacing == EnumFacing.EAST) {
                     d++;
                  } else {
                     d += 0.05;
                  }
               } else {
                  d += rand.nextDouble();
                  if (enumFacing == EnumFacing.SOUTH) {
                     d2++;
                  } else {
                     d2 += 0.05;
                  }
               }
            }

            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d, e, d2, 0.0, 0.0, 0.0);
         }
      }
   }
}
