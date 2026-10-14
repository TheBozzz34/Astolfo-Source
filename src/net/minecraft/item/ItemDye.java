package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ItemDye extends Item {
   public static final int[] dyeColors = new int[]{
      1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320
   };

   public ItemDye() {
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      int i = stack.getMetadata();
      return super.getUnlocalizedName() + "." + EnumDyeColor.byDyeDamage(i).getUnlocalizedName();
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
         return false;
      }

      EnumDyeColor enumDyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
      if (enumDyeColor == EnumDyeColor.WHITE) {
         if (applyBonemeal(stack, worldIn, pos)) {
            if (!worldIn.isRemote) {
               worldIn.playAuxSFX(2005, pos, 0);
            }

            return true;
         }
      } else if (enumDyeColor == EnumDyeColor.BROWN) {
         IBlockState iBlockState = worldIn.getBlockState(pos);
         Block block = iBlockState.getBlock();
         if (block == Blocks.log && iBlockState.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE) {
            if (side == EnumFacing.DOWN) {
               return false;
            }

            if (side == EnumFacing.UP) {
               return false;
            }

            pos = pos.offset(side);
            if (worldIn.isAirBlock(pos)) {
               IBlockState iBlockState2 = Blocks.cocoa.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
               worldIn.setBlockState(pos, iBlockState2, 2);
               if (!playerIn.capabilities.isCreativeMode) {
                  stack.stackSize--;
               }
            }

            return true;
         }
      }

      return false;
   }

   public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos target) {
      IBlockState iBlockState = worldIn.getBlockState(target);
      if (iBlockState.getBlock() instanceof IGrowable) {
         IGrowable iGrowable = (IGrowable)iBlockState.getBlock();
         if (iGrowable.canGrow(worldIn, target, iBlockState, worldIn.isRemote)) {
            if (!worldIn.isRemote) {
               if (iGrowable.canUseBonemeal(worldIn, worldIn.rand, target, iBlockState)) {
                  iGrowable.grow(worldIn, worldIn.rand, target, iBlockState);
               }

               stack.stackSize--;
            }

            return true;
         }
      }

      return false;
   }

   public static void spawnBonemealParticles(World worldIn, BlockPos pos, int amount) {
      if (amount == 0) {
         amount = 15;
      }

      Block block = worldIn.getBlockState(pos).getBlock();
      if (block.getMaterial() != Material.air) {
         block.setBlockBoundsBasedOnState(worldIn, pos);

         for (int i = 0; i < amount; i++) {
            double d = itemRand.nextGaussian() * 0.02;
            double e = itemRand.nextGaussian() * 0.02;
            double d2 = itemRand.nextGaussian() * 0.02;
            worldIn.spawnParticle(
               EnumParticleTypes.VILLAGER_HAPPY,
               pos.getX() + itemRand.nextFloat(),
               pos.getY() + itemRand.nextFloat() * block.getBlockBoundsMaxY(),
               pos.getZ() + itemRand.nextFloat(),
               d,
               e,
               d2
            );
         }
      }
   }

   @Override
   public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target) {
      if (target instanceof EntitySheep) {
         EntitySheep entitySheep = (EntitySheep)target;
         EnumDyeColor enumDyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
         if (!entitySheep.getSheared() && entitySheep.getFleeceColor() != enumDyeColor) {
            entitySheep.setFleeceColor(enumDyeColor);
            stack.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      for (int i = 0; i < 16; i++) {
         subItems.add(new ItemStack(itemIn, 1, i));
      }
   }
}
