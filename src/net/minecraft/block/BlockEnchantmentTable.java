package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockEnchantmentTable extends BlockContainer {
   private static final long e = 47213108222634L;

   public BlockEnchantmentTable() {
      super(Material.rock, MapColor.redColor);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
      this.setLightOpacity(0);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      super.randomDisplayTick(var1, var2, var3, var4);

      for (int var8 = -2; var8 <= 2; var8++) {
         for (int var9 = -2; var9 <= 2; var9++) {
            if (var8 > -2 && var8 < 2 && var9 == -1) {
               var9 = 2;
            }

            if (var4.nextInt(16) == 0) {
               for (int var10 = 0; var10 <= 1; var10++) {
                  BlockPos var11 = var2.add(var8, var10, var9);
                  if (var1.getBlockState(var11).getBlock() == Blocks.bookshelf) {
                     if (!var1.isAirBlock(var2.add(var8 / 2, 0, var9 / 2))) {
                        break;
                     }

                     var1.spawnParticle(
                        EnumParticleTypes.ENCHANTMENT_TABLE,
                        var2.getX() + 0.5,
                        var2.getY() + 2.0,
                        var2.getZ() + 0.5,
                        var8 + var4.nextFloat() - 0.5,
                        var10 - var4.nextFloat() - 1.0F,
                        var9 + var4.nextFloat() - 0.5
                     );
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityEnchantmentTable();
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      TileEntity var12 = var1.getTileEntity(var2);
      if (var12 instanceof TileEntityEnchantmentTable) {
         var4.displayGui((TileEntityEnchantmentTable)var12);
      }

      return true;
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      super.onBlockPlacedBy(var1, var2, var3, var4, var5);
      if (var5.hasDisplayName()) {
         TileEntity var9 = var1.getTileEntity(var2);
         if (var9 instanceof TileEntityEnchantmentTable) {
            ((TileEntityEnchantmentTable)var9).setCustomName(var5.getDisplayName());
         }
      }
   }
}
