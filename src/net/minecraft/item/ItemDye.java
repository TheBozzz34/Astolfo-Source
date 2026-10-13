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
   private static final long a = 55499464260556L;

   public ItemDye() {
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   @Override
   public String getUnlocalizedName(ItemStack var1) {
      int var2 = var1.getMetadata();
      return super.getUnlocalizedName() + "." + EnumDyeColor.byDyeDamage(var2).getUnlocalizedName();
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var2.canPlayerEdit(var4.offset(var5), var5, var1)) {
         return false;
      }

      EnumDyeColor var12 = EnumDyeColor.byDyeDamage(var1.getMetadata());
      if (var12 == EnumDyeColor.WHITE) {
         if (applyBonemeal(var1, var3, var4)) {
            if (!var3.isRemote) {
               var3.playAuxSFX(2005, var4, 0);
            }

            return true;
         }
      } else if (var12 == EnumDyeColor.BROWN) {
         IBlockState var13 = var3.getBlockState(var4);
         Block var14 = var13.getBlock();
         if (var14 == Blocks.log && var13.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE) {
            if (var5 == EnumFacing.DOWN) {
               return false;
            }

            if (var5 == EnumFacing.UP) {
               return false;
            }

            var4 = var4.offset(var5);
            if (var3.isAirBlock(var4)) {
               IBlockState var15 = Blocks.cocoa.onBlockPlaced(var3, var4, var5, var6, var7, var8, 0, var2);
               var3.setBlockState(var4, var15, 2);
               if (!var2.capabilities.isCreativeMode) {
                  var1.stackSize--;
               }
            }

            return true;
         }
      }

      return false;
   }

   public static boolean applyBonemeal(ItemStack var0, World var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      if (var6.getBlock() instanceof IGrowable) {
         IGrowable var7 = (IGrowable)var6.getBlock();
         if (var7.canGrow(var1, var2, var6, var1.isRemote)) {
            if (!var1.isRemote) {
               if (var7.canUseBonemeal(var1, var1.rand, var2, var6)) {
                  var7.grow(var1, var1.rand, var2, var6);
               }

               var0.stackSize--;
            }

            return true;
         }
      }

      return false;
   }

   public static void spawnBonemealParticles(World var0, BlockPos var1, int var2) {
      if (var2 == 0) {
         var2 = 15;
      }

      Block var6 = var0.getBlockState(var1).getBlock();
      if (var6.getMaterial() != Material.air) {
         var6.setBlockBoundsBasedOnState(var0, var1);

         for (int var7 = 0; var7 < var2; var7++) {
            double var8 = itemRand.nextGaussian() * 0.02;
            double var10 = itemRand.nextGaussian() * 0.02;
            double var12 = itemRand.nextGaussian() * 0.02;
            var0.spawnParticle(
               EnumParticleTypes.VILLAGER_HAPPY,
               var1.getX() + itemRand.nextFloat(),
               var1.getY() + itemRand.nextFloat() * var6.getBlockBoundsMaxY(),
               var1.getZ() + itemRand.nextFloat(),
               var8,
               var10,
               var12
            );
         }
      }
   }

   @Override
   public boolean itemInteractionForEntity(ItemStack var1, EntityPlayer var2, EntityLivingBase var3) {
      if (var3 instanceof EntitySheep) {
         EntitySheep var7 = (EntitySheep)var3;
         EnumDyeColor var8 = EnumDyeColor.byDyeDamage(var1.getMetadata());
         if (!var7.getSheared() && var7.getFleeceColor() != var8) {
            var7.setFleeceColor(var8);
            var1.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void getSubItems(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (int var7 = 0; var7 < 16; var7++) {
         var3.add(new ItemStack(var1, 1, var7));
      }
   }
}
