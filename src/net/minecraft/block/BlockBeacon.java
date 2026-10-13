package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class BlockBeacon extends BlockContainer {
   private static final long e = 75762643293309L;

   public BlockBeacon() {
      super(Material.glass, MapColor.diamondColor);
      this.setHardness(3.0F);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityBeacon();
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      TileEntity var12 = var1.getTileEntity(var2);
      if (var12 instanceof TileEntityBeacon) {
         var4.displayGUIChest((TileEntityBeacon)var12);
         var4.triggerAchievement(StatList.field_181730_N);
      }

      return true;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      super.onBlockPlacedBy(var1, var2, var3, var4, var5);
      if (var5.hasDisplayName()) {
         TileEntity var9 = var1.getTileEntity(var2);
         if (var9 instanceof TileEntityBeacon) {
            ((TileEntityBeacon)var9).setName(var5.getDisplayName());
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      TileEntity var8 = var1.getTileEntity(var2);
      if (var8 instanceof TileEntityBeacon) {
         ((TileEntityBeacon)var8).updateBeacon();
         var1.addBlockEvent(var2, this, 1, 0);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public static void updateColorAsync(final World var0, final BlockPos var1) {
      HttpUtil.field_180193_a.submit(new Runnable() {
         private static final long a = 108848111984645L;

         @Override
         public void run() {
            Chunk var4 = var0.getChunkFromBlockCoords(var1);

            for (int var5 = var1.getY() - 1; var5 >= 0; var5--) {
               final BlockPos var6 = new BlockPos(var1.getX(), var5, var1.getZ());
               if (!var4.canSeeSky(var6)) {
                  break;
               }

               IBlockState var7 = var0.getBlockState(var6);
               if (var7.getBlock() == Blocks.beacon) {
                  ((WorldServer)var0).addScheduledTask(new Runnable() {
                     private static final long a = 14211617065518L;

                     @Override
                     public void run() {
                        TileEntity var4 = var0.getTileEntity(var6);
                        if (var4 instanceof TileEntityBeacon) {
                           ((TileEntityBeacon)var4).updateBeacon();
                           var0.addBlockEvent(var6, Blocks.beacon, 1, 0);
                        }
                     }
                  });
               }
            }
         }
      });
   }
}
