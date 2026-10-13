package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonExtension extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing");
   public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = PropertyEnum.create("type", BlockPistonExtension.EnumPistonType.class);
   public static final PropertyBool SHORT = PropertyBool.create("short");
   private static final long i = 121820681820156L;

   public BlockPistonExtension() {
      super(Material.piston);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT)
            .withProperty(SHORT, false)
      );
      this.setStepSound(soundTypePiston);
      this.setHardness(0.5F);
   }

   @Override
   public void onBlockHarvested(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (var4.capabilities.isCreativeMode) {
         EnumFacing var8 = var3.getValue(FACING);
         if (var8 != null) {
            BlockPos var9 = var2.offset(var8.getOpposite());
            Block var10 = var1.getBlockState(var9).getBlock();
            if (var10 == Blocks.piston || var10 == Blocks.sticky_piston) {
               var1.setBlockToAir(var9);
            }
         }
      }

      super.onBlockHarvested(var1, var2, var3, var4);
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      super.breakBlock(var1, var2, var3);
      EnumFacing var7 = var3.getValue(FACING).getOpposite();
      var2 = var2.offset(var7);
      IBlockState var8 = var1.getBlockState(var2);
      if ((var8.getBlock() == Blocks.piston || var8.getBlock() == Blocks.sticky_piston) && var8.getValue(BlockPistonBase.EXTENDED)) {
         var8.getBlock().dropBlockAsItem(var1, var2, var8, 0);
         var1.setBlockToAir(var2);
      }
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
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return false;
   }

   @Override
   public boolean canPlaceBlockOnSide(World var1, BlockPos var2, EnumFacing var3) {
      return false;
   }

   @Override
   public int quantityDropped(Random var1) {
      return 0;
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.applyHeadBounds(var3);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.applyCoreBounds(var3);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   private void applyCoreBounds(IBlockState var1) {
      switch ((EnumFacing)var1.getValue(FACING)) {
         case DOWN:
            this.setBlockBounds(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
            break;
         case UP:
            this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
            break;
         case NORTH:
            this.setBlockBounds(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
            break;
         case SOUTH:
            this.setBlockBounds(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
            break;
         case WEST:
            this.setBlockBounds(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
            break;
         case EAST:
            this.setBlockBounds(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      this.applyHeadBounds(var1.getBlockState(var2));
   }

   public void applyHeadBounds(IBlockState var1) {
      EnumFacing var6 = var1.getValue(FACING);
      if (var6 != null) {
         switch (var6) {
            case DOWN:
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
               break;
            case UP:
               this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
               break;
            case NORTH:
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
               break;
            case SOUTH:
               this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
               break;
            case WEST:
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
               break;
            case EAST:
               this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing var8 = var3.getValue(FACING);
      BlockPos var9 = var2.offset(var8.getOpposite());
      IBlockState var10 = var1.getBlockState(var9);
      if (var10.getBlock() != Blocks.piston && var10.getBlock() != Blocks.sticky_piston) {
         var1.setBlockToAir(var2);
      } else {
         var10.getBlock().onNeighborBlockChange(var1, var9, var10, var4);
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   public static EnumFacing getFacing(int var0) {
      int var4 = var0 & 7;
      return var4 > 5 ? null : EnumFacing.getFront(var4);
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return var1.getBlockState(var2).getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY
         ? Item.getItemFromBlock(Blocks.sticky_piston)
         : Item.getItemFromBlock(Blocks.piston);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState()
         .withProperty(FACING, getFacing(var1))
         .withProperty(TYPE, (var1 & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getIndex();
      if (var1.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, TYPE, SHORT);
   }

   public enum EnumPistonType implements IStringSerializable {
      DEFAULT("normal"),
      STICKY("sticky");

      private final String VARIANT;

      EnumPistonType(String var3) {
         this.VARIANT = var3;
      }

      @Override
      public String toString() {
         return this.VARIANT;
      }

      @Override
      public String getName() {
         return this.VARIANT;
      }
   }
}
