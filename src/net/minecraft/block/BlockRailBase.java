package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   public final boolean isPowered;
   private static final long l = 40655877758408L;

   public static boolean isRailBlock(World var0, BlockPos var1) {
      return isRailBlock(var0.getBlockState(var1));
   }

   public static boolean isRailBlock(IBlockState var0) {
      Block var4 = var0.getBlock();
      return var4 == Blocks.rail || var4 == Blocks.golden_rail || var4 == Blocks.detector_rail || var4 == Blocks.activator_rail;
   }

   public BlockRailBase(boolean var1) {
      super(Material.circuits);
      this.isPowered = var1;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.setCreativeTab(CreativeTabs.tabTransport);
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
   public MovingObjectPosition collisionRayTrace(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.collisionRayTrace(var1, var2, var3, var4);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      BlockRailBase.EnumRailDirection var7 = var6.getBlock() == this ? var6.getValue(this.getShapeProperty()) : null;
      if (var7 != null && var7.isAscending()) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return World.doesBlockHaveSolidTopSurface(var1, var2.down());
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.isRemote) {
         var3 = this.func_176564_a(var1, var2, var3, true);
         if (this.isPowered) {
            this.onNeighborBlockChange(var1, var2, var3, this);
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.isRemote) {
         BlockRailBase.EnumRailDirection var8 = var3.getValue(this.getShapeProperty());
         boolean var9 = false;
         if (!World.doesBlockHaveSolidTopSurface(var1, var2.down())) {
            var9 = true;
         }

         if (var8 == BlockRailBase.EnumRailDirection.ASCENDING_EAST && !World.doesBlockHaveSolidTopSurface(var1, var2.east())) {
            var9 = true;
         } else if (var8 == BlockRailBase.EnumRailDirection.ASCENDING_WEST && !World.doesBlockHaveSolidTopSurface(var1, var2.west())) {
            var9 = true;
         } else if (var8 == BlockRailBase.EnumRailDirection.ASCENDING_NORTH && !World.doesBlockHaveSolidTopSurface(var1, var2.north())) {
            var9 = true;
         } else if (var8 == BlockRailBase.EnumRailDirection.ASCENDING_SOUTH && !World.doesBlockHaveSolidTopSurface(var1, var2.south())) {
            var9 = true;
         }

         if (var9) {
            this.dropBlockAsItem(var1, var2, var3, 0);
            var1.setBlockToAir(var2);
         } else {
            this.onNeighborChangedInternal(var1, var2, var3, var4);
         }
      }
   }

   public void onNeighborChangedInternal(World var1, BlockPos var2, IBlockState var3, Block var4) {
   }

   public IBlockState func_176564_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return var1.isRemote ? var3 : new BlockRailBase.Rail(var1, var2, var3).func_180364_a(var1.isBlockPowered(var2), var4).getBlockState();
   }

   @Override
   public int getMobilityFlag() {
      return 0;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      super.breakBlock(var1, var2, var3);
      if (var3.getValue(this.getShapeProperty()).isAscending()) {
         var1.notifyNeighborsOfStateChange(var2.up(), this);
      }

      if (this.isPowered) {
         var1.notifyNeighborsOfStateChange(var2, this);
         var1.notifyNeighborsOfStateChange(var2.down(), this);
      }
   }

   public abstract IProperty<BlockRailBase.EnumRailDirection> getShapeProperty();

   public enum EnumRailDirection implements IStringSerializable {
      NORTH_SOUTH(0, "north_south"),
      EAST_WEST(1, "east_west"),
      ASCENDING_EAST(2, "ascending_east"),
      ASCENDING_WEST(3, "ascending_west"),
      ASCENDING_NORTH(4, "ascending_north"),
      ASCENDING_SOUTH(5, "ascending_south"),
      SOUTH_EAST(6, "south_east"),
      SOUTH_WEST(7, "south_west"),
      NORTH_WEST(8, "north_west"),
      NORTH_EAST(9, "north_east");

      private static final BlockRailBase.EnumRailDirection[] META_LOOKUP = new BlockRailBase.EnumRailDirection[values().length];
      private final int meta;
      private final String name;
      private static final long a = 101598142959245L;

      EnumRailDirection(int var3, String var4) {
         this.meta = var3;
         this.name = var4;
      }

      public int getMetadata() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public boolean isAscending() {
         return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
      }

      public static BlockRailBase.EnumRailDirection byMetadata(int var0) {
         if (var0 < 0 || var0 >= META_LOOKUP.length) {
            var0 = 0;
         }

         return META_LOOKUP[var0];
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (BlockRailBase.EnumRailDirection var25 : values()) {
            META_LOOKUP[var25.getMetadata()] = var25;
         }
      }
   }

   public class Rail {
      private final World world;
      private final BlockPos pos;
      private final BlockRailBase block;
      private IBlockState state;
      private final boolean isPowered;
      private final List<BlockPos> field_150657_g = Lists.newArrayList();
      private static final long a = 11061687978010L;

      public Rail(World var2, BlockPos var3, IBlockState var4) {
         this.world = var2;
         this.pos = var3;
         this.state = var4;
         this.block = (BlockRailBase)var4.getBlock();
         BlockRailBase.EnumRailDirection var5 = var4.getValue(BlockRailBase.this.getShapeProperty());
         this.isPowered = this.block.isPowered;
         this.func_180360_a(var5);
      }

      private void func_180360_a(BlockRailBase.EnumRailDirection var1) {
         this.field_150657_g.clear();
         switch (var1) {
            case NORTH_SOUTH:
               this.field_150657_g.add(this.pos.north());
               this.field_150657_g.add(this.pos.south());
               break;
            case EAST_WEST:
               this.field_150657_g.add(this.pos.west());
               this.field_150657_g.add(this.pos.east());
               break;
            case ASCENDING_EAST:
               this.field_150657_g.add(this.pos.west());
               this.field_150657_g.add(this.pos.east().up());
               break;
            case ASCENDING_WEST:
               this.field_150657_g.add(this.pos.west().up());
               this.field_150657_g.add(this.pos.east());
               break;
            case ASCENDING_NORTH:
               this.field_150657_g.add(this.pos.north().up());
               this.field_150657_g.add(this.pos.south());
               break;
            case ASCENDING_SOUTH:
               this.field_150657_g.add(this.pos.north());
               this.field_150657_g.add(this.pos.south().up());
               break;
            case SOUTH_EAST:
               this.field_150657_g.add(this.pos.east());
               this.field_150657_g.add(this.pos.south());
               break;
            case SOUTH_WEST:
               this.field_150657_g.add(this.pos.west());
               this.field_150657_g.add(this.pos.south());
               break;
            case NORTH_WEST:
               this.field_150657_g.add(this.pos.west());
               this.field_150657_g.add(this.pos.north());
               break;
            case NORTH_EAST:
               this.field_150657_g.add(this.pos.east());
               this.field_150657_g.add(this.pos.north());
         }
      }

      private void func_150651_b() {
         for (int var4 = 0; var4 < this.field_150657_g.size(); var4++) {
            BlockRailBase.Rail var5 = this.findRailAt(this.field_150657_g.get(var4));
            if (var5 != null && var5.func_150653_a(this)) {
               this.field_150657_g.set(var4, var5.pos);
            } else {
               this.field_150657_g.remove(var4--);
            }
         }
      }

      private boolean hasRailAt(BlockPos var1) {
         return BlockRailBase.isRailBlock(this.world, var1)
            || BlockRailBase.isRailBlock(this.world, var1.up())
            || BlockRailBase.isRailBlock(this.world, var1.down());
      }

      private BlockRailBase.Rail findRailAt(BlockPos var1) {
         IBlockState var5 = this.world.getBlockState(var1);
         if (BlockRailBase.isRailBlock(var5)) {
            return BlockRailBase.this.new Rail(this.world, var1, var5);
         }

         BlockPos var6 = var1.up();
         var5 = this.world.getBlockState(var6);
         if (BlockRailBase.isRailBlock(var5)) {
            return BlockRailBase.this.new Rail(this.world, var6, var5);
         }

         var6 = var1.down();
         var5 = this.world.getBlockState(var6);
         return BlockRailBase.isRailBlock(var5) ? BlockRailBase.this.new Rail(this.world, var6, var5) : null;
      }

      private boolean func_150653_a(BlockRailBase.Rail var1) {
         return this.func_180363_c(var1.pos);
      }

      private boolean func_180363_c(BlockPos var1) {
         for (int var5 = 0; var5 < this.field_150657_g.size(); var5++) {
            BlockPos var6 = this.field_150657_g.get(var5);
            if (var6.getX() == var1.getX() && var6.getZ() == var1.getZ()) {
               return true;
            }
         }

         return false;
      }

      public int countAdjacentRails() {
         int var4 = 0;

         for (Object var6 : EnumFacing.Plane.HORIZONTAL) {
            if (this.hasRailAt(this.pos.offset((EnumFacing)var6))) {
               var4++;
            }
         }

         return var4;
      }

      private boolean func_150649_b(BlockRailBase.Rail var1) {
         return this.func_150653_a(var1) || this.field_150657_g.size() != 2;
      }

      private void func_150645_c(BlockRailBase.Rail var1) {
         this.field_150657_g.add(var1.pos);
         BlockPos var5 = this.pos.north();
         BlockPos var6 = this.pos.south();
         BlockPos var7 = this.pos.west();
         BlockPos var8 = this.pos.east();
         boolean var9 = this.func_180363_c(var5);
         boolean var10 = this.func_180363_c(var6);
         boolean var11 = this.func_180363_c(var7);
         boolean var12 = this.func_180363_c(var8);
         BlockRailBase.EnumRailDirection var13 = null;
         if (var9 || var10) {
            var13 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if (var11 || var12) {
            var13 = BlockRailBase.EnumRailDirection.EAST_WEST;
         }

         if (!this.isPowered) {
            if (var10 && var12 && !var9 && !var11) {
               var13 = BlockRailBase.EnumRailDirection.SOUTH_EAST;
            }

            if (var10 && var11 && !var9 && !var12) {
               var13 = BlockRailBase.EnumRailDirection.SOUTH_WEST;
            }

            if (var9 && var11 && !var10 && !var12) {
               var13 = BlockRailBase.EnumRailDirection.NORTH_WEST;
            }

            if (var9 && var12 && !var10 && !var11) {
               var13 = BlockRailBase.EnumRailDirection.NORTH_EAST;
            }
         }

         if (var13 == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (BlockRailBase.isRailBlock(this.world, var5.up())) {
               var13 = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
            }

            if (BlockRailBase.isRailBlock(this.world, var6.up())) {
               var13 = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
            }
         }

         if (var13 == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (BlockRailBase.isRailBlock(this.world, var8.up())) {
               var13 = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
            }

            if (BlockRailBase.isRailBlock(this.world, var7.up())) {
               var13 = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
            }
         }

         if (var13 == null) {
            var13 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         this.state = this.state.withProperty(this.block.getShapeProperty(), var13);
         this.world.setBlockState(this.pos, this.state, 3);
      }

      private boolean func_180361_d(BlockPos var1) {
         BlockRailBase.Rail var5 = this.findRailAt(var1);
         if (var5 == null) {
            return false;
         }

         var5.func_150651_b();
         return var5.func_150649_b(this);
      }

      public BlockRailBase.Rail func_180364_a(boolean var1, boolean var2) {
         BlockPos var6 = this.pos.north();
         BlockPos var7 = this.pos.south();
         BlockPos var8 = this.pos.west();
         BlockPos var9 = this.pos.east();
         boolean var10 = this.func_180361_d(var6);
         boolean var11 = this.func_180361_d(var7);
         boolean var12 = this.func_180361_d(var8);
         boolean var13 = this.func_180361_d(var9);
         BlockRailBase.EnumRailDirection var14 = null;
         if ((var10 || var11) && !var12 && !var13) {
            var14 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if ((var12 || var13) && !var10 && !var11) {
            var14 = BlockRailBase.EnumRailDirection.EAST_WEST;
         }

         if (!this.isPowered) {
            if (var11 && var13 && !var10 && !var12) {
               var14 = BlockRailBase.EnumRailDirection.SOUTH_EAST;
            }

            if (var11 && var12 && !var10 && !var13) {
               var14 = BlockRailBase.EnumRailDirection.SOUTH_WEST;
            }

            if (var10 && var12 && !var11 && !var13) {
               var14 = BlockRailBase.EnumRailDirection.NORTH_WEST;
            }

            if (var10 && var13 && !var11 && !var12) {
               var14 = BlockRailBase.EnumRailDirection.NORTH_EAST;
            }
         }

         if (var14 == null) {
            if (var10 || var11) {
               var14 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if (var12 || var13) {
               var14 = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.isPowered) {
               if (var1) {
                  if (var11 && var13) {
                     var14 = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                  }

                  if (var12 && var11) {
                     var14 = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                  }

                  if (var13 && var10) {
                     var14 = BlockRailBase.EnumRailDirection.NORTH_EAST;
                  }

                  if (var10 && var12) {
                     var14 = BlockRailBase.EnumRailDirection.NORTH_WEST;
                  }
               } else {
                  if (var10 && var12) {
                     var14 = BlockRailBase.EnumRailDirection.NORTH_WEST;
                  }

                  if (var13 && var10) {
                     var14 = BlockRailBase.EnumRailDirection.NORTH_EAST;
                  }

                  if (var12 && var11) {
                     var14 = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                  }

                  if (var11 && var13) {
                     var14 = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                  }
               }
            }
         }

         if (var14 == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (BlockRailBase.isRailBlock(this.world, var6.up())) {
               var14 = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
            }

            if (BlockRailBase.isRailBlock(this.world, var7.up())) {
               var14 = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
            }
         }

         if (var14 == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (BlockRailBase.isRailBlock(this.world, var9.up())) {
               var14 = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
            }

            if (BlockRailBase.isRailBlock(this.world, var8.up())) {
               var14 = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
            }
         }

         if (var14 == null) {
            var14 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         this.func_180360_a(var14);
         this.state = this.state.withProperty(this.block.getShapeProperty(), var14);
         if (var2 || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);

            for (int var15 = 0; var15 < this.field_150657_g.size(); var15++) {
               BlockRailBase.Rail var16 = this.findRailAt(this.field_150657_g.get(var15));
               if (var16 != null) {
                  var16.func_150651_b();
                  if (var16.func_150649_b(this)) {
                     var16.func_150645_c(this);
                  }
               }
            }
         }

         return this;
      }

      public IBlockState getBlockState() {
         return this.state;
      }
   }
}
