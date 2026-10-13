package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLever extends Block {
   public static final PropertyEnum<BlockLever.EnumOrientation> FACING = PropertyEnum.create("facing", BlockLever.EnumOrientation.class);
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   private static final long e = 35052500795515L;

   public BlockLever() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, BlockLever.EnumOrientation.NORTH).withProperty(POWERED, false));
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return null;
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
   public boolean canPlaceBlockOnSide(World var1, BlockPos var2, EnumFacing var3) {
      return func_181090_a(var1, var2, var3.getOpposite());
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      for (EnumFacing var9 : EnumFacing.values()) {
         if (func_181090_a(var1, var2, var9)) {
            return true;
         }
      }

      return false;
   }

   public static boolean func_181090_a(World var0, BlockPos var1, EnumFacing var2) {
      return BlockButton.func_181088_a(var0, var1, var2);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var12 = this.getDefaultState().withProperty(POWERED, false);
      if (func_181090_a(var1, var2, var3.getOpposite())) {
         return var12.withProperty(FACING, BlockLever.EnumOrientation.forFacings(var3, var8.getHorizontalFacing()));
      }

      for (Object var14 : EnumFacing.Plane.HORIZONTAL) {
         if (var14 != var3 && func_181090_a(var1, var2, ((EnumFacing)var14).getOpposite())) {
            return var12.withProperty(FACING, BlockLever.EnumOrientation.forFacings((EnumFacing)var14, var8.getHorizontalFacing()));
         }
      }

      return World.doesBlockHaveSolidTopSurface(var1, var2.down())
         ? var12.withProperty(FACING, BlockLever.EnumOrientation.forFacings(EnumFacing.UP, var8.getHorizontalFacing()))
         : var12;
   }

   public static int getMetadataForFacing(EnumFacing var0) {
      switch (var0) {
         case DOWN:
            return 0;
         case UP:
            return 5;
         case NORTH:
            return 4;
         case SOUTH:
            return 3;
         case WEST:
            return 2;
         case EAST:
            return 1;
         default:
            return -1;
      }
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (this.func_181091_e(var1, var2, var3) && !func_181090_a(var1, var2, var3.getValue(FACING).getFacing().getOpposite())) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
      }
   }

   private boolean func_181091_e(World var1, BlockPos var2, IBlockState var3) {
      if (this.canPlaceBlockAt(var1, var2)) {
         return true;
      }

      this.dropBlockAsItem(var1, var2, var3, 0);
      var1.setBlockToAir(var2);
      return false;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      float var6 = 0.1875F;
      switch ((BlockLever.EnumOrientation)var1.getBlockState(var2).getValue(FACING)) {
         case EAST:
            this.setBlockBounds(0.0F, 0.2F, 0.5F - var6, var6 * 2.0F, 0.8F, 0.5F + var6);
            break;
         case WEST:
            this.setBlockBounds(1.0F - var6 * 2.0F, 0.2F, 0.5F - var6, 1.0F, 0.8F, 0.5F + var6);
            break;
         case SOUTH:
            this.setBlockBounds(0.5F - var6, 0.2F, 0.0F, 0.5F + var6, 0.8F, var6 * 2.0F);
            break;
         case NORTH:
            this.setBlockBounds(0.5F - var6, 0.2F, 1.0F - var6 * 2.0F, 0.5F + var6, 0.8F, 1.0F);
            break;
         case UP_Z:
         case UP_X:
            var6 = 0.25F;
            this.setBlockBounds(0.5F - var6, 0.0F, 0.5F - var6, 0.5F + var6, 0.6F, 0.5F + var6);
            break;
         case DOWN_X:
         case DOWN_Z:
            var6 = 0.25F;
            this.setBlockBounds(0.5F - var6, 0.4F, 0.5F - var6, 0.5F + var6, 1.0F, 0.5F + var6);
      }
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      var3 = var3.cycleProperty(POWERED);
      var1.setBlockState(var2, var3, 3);
      var1.playSoundEffect(var2.getX() + 0.5, var2.getY() + 0.5, var2.getZ() + 0.5, "random.click", 0.3F, var3.getValue(POWERED) ? 0.6F : 0.5F);
      var1.notifyNeighborsOfStateChange(var2, this);
      EnumFacing var12 = var3.getValue(FACING).getFacing();
      var1.notifyNeighborsOfStateChange(var2.offset(var12.getOpposite()), this);
      return true;
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      if (var3.getValue(POWERED)) {
         var1.notifyNeighborsOfStateChange(var2, this);
         EnumFacing var7 = var3.getValue(FACING).getFacing();
         var1.notifyNeighborsOfStateChange(var2.offset(var7.getOpposite()), this);
      }

      super.breakBlock(var1, var2, var3);
   }

   @Override
   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return var3.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public int getStrongPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return !var3.getValue(POWERED) ? 0 : (var3.getValue(FACING).getFacing() == var4 ? 15 : 0);
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, BlockLever.EnumOrientation.byMetadata(var1 & 7)).withProperty(POWERED, (var1 & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getMetadata();
      if (var1.getValue(POWERED)) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, POWERED);
   }

   public enum EnumOrientation implements IStringSerializable {
      DOWN_X(0, "down_x", EnumFacing.DOWN),
      EAST(1, "east", EnumFacing.EAST),
      WEST(2, "west", EnumFacing.WEST),
      SOUTH(3, "south", EnumFacing.SOUTH),
      NORTH(4, "north", EnumFacing.NORTH),
      UP_Z(5, "up_z", EnumFacing.UP),
      UP_X(6, "up_x", EnumFacing.UP),
      DOWN_Z(7, "down_z", EnumFacing.DOWN);

      private static final BlockLever.EnumOrientation[] META_LOOKUP = new BlockLever.EnumOrientation[values().length];
      private final int meta;
      private final String name;
      private final EnumFacing facing;
      private static final long a = 136308620155399L;

      EnumOrientation(int var3, String var4, EnumFacing var5) {
         this.meta = var3;
         this.name = var4;
         this.facing = var5;
      }

      public int getMetadata() {
         return this.meta;
      }

      public EnumFacing getFacing() {
         return this.facing;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockLever.EnumOrientation byMetadata(int var0) {
         if (var0 < 0 || var0 >= META_LOOKUP.length) {
            var0 = 0;
         }

         return META_LOOKUP[var0];
      }

      public static BlockLever.EnumOrientation forFacings(EnumFacing var0, EnumFacing var1) {
         switch (var0) {
            case DOWN:
               switch (var1.getAxis()) {
                  case X:
                     return DOWN_X;
                  case Z:
                     return DOWN_Z;
                  default:
                     throw new IllegalArgumentException("Invalid entityFacing " + var1 + " for facing " + var0);
               }
            case UP:
               switch (var1.getAxis()) {
                  case X:
                     return UP_X;
                  case Z:
                     return UP_Z;
                  default:
                     throw new IllegalArgumentException("Invalid entityFacing " + var1 + " for facing " + var0);
               }
            case NORTH:
               return NORTH;
            case SOUTH:
               return SOUTH;
            case WEST:
               return WEST;
            case EAST:
               return EAST;
            default:
               throw new IllegalArgumentException("Invalid facing: " + var0);
         }
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (BlockLever.EnumOrientation var25 : values()) {
            META_LOOKUP[var25.getMetadata()] = var25;
         }
      }
   }
}
