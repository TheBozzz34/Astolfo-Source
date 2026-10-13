package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block {
   private static final long e = 132189705297883L;

   public BlockDragonEgg() {
      super(Material.dragonEgg, MapColor.blackColor);
      this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
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
      this.checkFall(var1, var2);
   }

   private void checkFall(World var1, BlockPos var2) {
      if (BlockFalling.canFallInto(var1, var2.down()) && var2.getY() >= 0) {
         byte var6 = 32;
         if (!BlockFalling.fallInstantly && var1.isAreaLoaded(var2.add(-var6, -var6, -var6), var2.add(var6, var6, var6))) {
            var1.spawnEntityInWorld(new EntityFallingBlock(var1, var2.getX() + 0.5F, var2.getY(), var2.getZ() + 0.5F, this.getDefaultState()));
         } else {
            var1.setBlockToAir(var2);
            BlockPos var7 = var2;

            while (BlockFalling.canFallInto(var1, var7) && var7.getY() > 0) {
               var7 = var7.down();
            }

            if (var7.getY() > 0) {
               var1.setBlockState(var7, this.getDefaultState(), 2);
            }
         }
      }
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      this.teleport(var1, var2);
      return true;
   }

   @Override
   public void onBlockClicked(World var1, BlockPos var2, EntityPlayer var3) {
      this.teleport(var1, var2);
   }

   private void teleport(World var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      if (var6.getBlock() == this) {
         for (int var7 = 0; var7 < 1000; var7++) {
            BlockPos var8 = var2.add(
               var1.rand.nextInt(16) - var1.rand.nextInt(16), var1.rand.nextInt(8) - var1.rand.nextInt(8), var1.rand.nextInt(16) - var1.rand.nextInt(16)
            );
            if (var1.getBlockState(var8).getBlock().blockMaterial == Material.air) {
               if (var1.isRemote) {
                  for (int var9 = 0; var9 < 128; var9++) {
                     double var10 = var1.rand.nextDouble();
                     float var12 = (var1.rand.nextFloat() - 0.5F) * 0.2F;
                     float var13 = (var1.rand.nextFloat() - 0.5F) * 0.2F;
                     float var14 = (var1.rand.nextFloat() - 0.5F) * 0.2F;
                     double var15 = var8.getX() + (var2.getX() - var8.getX()) * var10 + (var1.rand.nextDouble() - 0.5) * 1.0 + 0.5;
                     double var17 = var8.getY() + (var2.getY() - var8.getY()) * var10 + var1.rand.nextDouble() * 1.0 - 0.5;
                     double var19 = var8.getZ() + (var2.getZ() - var8.getZ()) * var10 + (var1.rand.nextDouble() - 0.5) * 1.0 + 0.5;
                     var1.spawnParticle(EnumParticleTypes.PORTAL, var15, var17, var19, var12, var13, var14);
                  }
               } else {
                  var1.setBlockState(var8, var6, 2);
                  var1.setBlockToAir(var2);
               }

               return;
            }
         }
      }
   }

   @Override
   public int tickRate(World var1) {
      return 5;
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
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return null;
   }
}
