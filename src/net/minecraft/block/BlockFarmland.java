package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFarmland extends Block {
   public static final PropertyInteger MOISTURE = PropertyInteger.create("moisture", 0, 7);
   private static final long e = 115194344734114L;

   public BlockFarmland() {
      super(Material.ground);
      this.setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, 0));
      this.setTickRandomly(true);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
      this.setLightOpacity(255);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return new AxisAlignedBB(var2.getX(), var2.getY(), var2.getZ(), var2.getX() + 1, var2.getY() + 1, var2.getZ() + 1);
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
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      int var8 = var3.getValue(MOISTURE);
      if (!this.hasWater(var1, var2) && !var1.isRainingAt(var2.up())) {
         if (var8 > 0) {
            var1.setBlockState(var2, var3.withProperty(MOISTURE, var8 - 1), 2);
         } else if (!this.hasCrops(var1, var2)) {
            var1.setBlockState(var2, Blocks.dirt.getDefaultState());
         }
      } else if (var8 < 7) {
         var1.setBlockState(var2, var3.withProperty(MOISTURE, 7), 2);
      }
   }

   @Override
   public void onFallenUpon(World var1, BlockPos var2, Entity var3, float var4) {
      if (var3 instanceof EntityLivingBase) {
         if (!var1.isRemote && var1.rand.nextFloat() < var4 - 0.5F) {
            if (!(var3 instanceof EntityPlayer) && !var1.getGameRules().getBoolean("mobGriefing")) {
               return;
            }

            var1.setBlockState(var2, Blocks.dirt.getDefaultState());
         }

         super.onFallenUpon(var1, var2, var3, var4);
      }
   }

   private boolean hasCrops(World var1, BlockPos var2) {
      Block var6 = var1.getBlockState(var2.up()).getBlock();
      return var6 instanceof BlockCrops || var6 instanceof BlockStem;
   }

   private boolean hasWater(World var1, BlockPos var2) {
      for (BlockPos.MutableBlockPos var7 : BlockPos.getAllInBoxMutable(var2.add(-4, 0, -4), var2.add(4, 1, 4))) {
         if (var1.getBlockState(var7).getBlock().getMaterial() == Material.water) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      super.onNeighborBlockChange(var1, var2, var3, var4);
      if (var1.getBlockState(var2.up()).getBlock().getMaterial().isSolid()) {
         var1.setBlockState(var2, Blocks.dirt.getDefaultState());
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      switch (var3) {
         case UP:
            return true;
         case NORTH:
         case SOUTH:
         case WEST:
         case EAST:
            Block var7 = var1.getBlockState(var2).getBlock();
            return !var7.isOpaqueCube() && var7 != Blocks.farmland;
         default:
            return super.shouldSideBeRendered(var1, var2, var3);
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Blocks.dirt.getItemDropped(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), var2, var3);
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Item.getItemFromBlock(Blocks.dirt);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(MOISTURE, var1 & 7);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(MOISTURE);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, MOISTURE);
   }
}
