package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable {
   public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);

   public BlockPortal() {
      super(Material.portal, false);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X));
      this.setTickRandomly(true);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      super.updateTick(worldIn, pos, state, rand);
      if (worldIn.provider.isSurfaceWorld()
         && worldIn.getGameRules().getBoolean("doMobSpawning")
         && rand.nextInt(2000) < worldIn.getDifficulty().getDifficultyId()) {
         int i = pos.getY();
         BlockPos blockPos = pos;

         while (!World.doesBlockHaveSolidTopSurface(worldIn, blockPos) && blockPos.getY() > 0) {
            blockPos = blockPos.down();
         }

         if (i > 0 && !worldIn.getBlockState(blockPos.up()).getBlock().isNormalCube()) {
            Entity entity = ItemMonsterPlacer.spawnCreature(worldIn, 57, blockPos.getX() + 0.5, blockPos.getY() + 1.1, blockPos.getZ() + 0.5);
            if (entity != null) {
               entity.timeUntilPortal = entity.getPortalCooldown();
            }
         }
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      EnumFacing.Axis axis = worldIn.getBlockState(pos).getValue(AXIS);
      float f = 0.125F;
      float g = 0.125F;
      if (axis == EnumFacing.Axis.X) {
         f = 0.5F;
      }

      if (axis == EnumFacing.Axis.Z) {
         g = 0.5F;
      }

      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - g, 0.5F + f, 1.0F, 0.5F + g);
   }

   public static int getMetaForAxis(EnumFacing.Axis axis) {
      return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public boolean func_176548_d(World worldIn, BlockPos blockPos) {
      BlockPortal.Size size = new BlockPortal.Size(worldIn, blockPos, EnumFacing.Axis.X);
      if (size.func_150860_b() && size.field_150864_e == 0) {
         size.func_150859_c();
         return true;
      } else {
         BlockPortal.Size size2 = new BlockPortal.Size(worldIn, blockPos, EnumFacing.Axis.Z);
         if (size2.func_150860_b() && size2.field_150864_e == 0) {
            size2.func_150859_c();
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      EnumFacing.Axis axis = state.getValue(AXIS);
      if (axis == EnumFacing.Axis.X) {
         BlockPortal.Size size = new BlockPortal.Size(worldIn, pos, EnumFacing.Axis.X);
         if (!size.func_150860_b() || size.field_150864_e < size.field_150868_h * size.field_150862_g) {
            worldIn.setBlockState(pos, Blocks.air.getDefaultState());
         }
      } else if (axis == EnumFacing.Axis.Z) {
         BlockPortal.Size size2 = new BlockPortal.Size(worldIn, pos, EnumFacing.Axis.Z);
         if (!size2.func_150860_b() || size2.field_150864_e < size2.field_150868_h * size2.field_150862_g) {
            worldIn.setBlockState(pos, Blocks.air.getDefaultState());
         }
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      EnumFacing.Axis axis = null;
      IBlockState iBlockState = worldIn.getBlockState(pos);
      if (worldIn.getBlockState(pos).getBlock() == this) {
         axis = iBlockState.getValue(AXIS);
         if (axis == null) {
            return false;
         }

         if (axis == EnumFacing.Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST) {
            return false;
         }

         if (axis == EnumFacing.Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH) {
            return false;
         }
      }

      boolean bl = worldIn.getBlockState(pos.west()).getBlock() == this && worldIn.getBlockState(pos.west(2)).getBlock() != this;
      boolean bl2 = worldIn.getBlockState(pos.east()).getBlock() == this && worldIn.getBlockState(pos.east(2)).getBlock() != this;
      boolean bl3 = worldIn.getBlockState(pos.north()).getBlock() == this && worldIn.getBlockState(pos.north(2)).getBlock() != this;
      boolean bl4 = worldIn.getBlockState(pos.south()).getBlock() == this && worldIn.getBlockState(pos.south(2)).getBlock() != this;
      boolean bl5 = bl || bl2 || axis == EnumFacing.Axis.X;
      boolean bl6 = bl3 || bl4 || axis == EnumFacing.Axis.Z;
      return bl5 && side == EnumFacing.WEST
         ? true
         : (bl5 && side == EnumFacing.EAST ? true : (bl6 && side == EnumFacing.NORTH ? true : bl6 && side == EnumFacing.SOUTH));
   }

   @Override
   public int quantityDropped(Random randomIn) {
      return 0;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (entityIn.ridingEntity == null && entityIn.riddenByEntity == null) {
         entityIn.setPortal(pos);
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (rand.nextInt(100) == 0) {
         worldIn.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "portal.portal", 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
      }

      for (int i = 0; i < 4; i++) {
         double d = pos.getX() + rand.nextFloat();
         double e = pos.getY() + rand.nextFloat();
         double d2 = pos.getZ() + rand.nextFloat();
         double d3 = (rand.nextFloat() - 0.5) * 0.5;
         double d4 = (rand.nextFloat() - 0.5) * 0.5;
         double d5 = (rand.nextFloat() - 0.5) * 0.5;
         int j = rand.nextInt(2) * 2 - 1;
         if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
            d = pos.getX() + 0.5 + 0.25 * j;
            d3 = rand.nextFloat() * 2.0F * j;
         } else {
            d2 = pos.getZ() + 0.5 + 0.25 * j;
            d5 = rand.nextFloat() * 2.0F * j;
         }

         worldIn.spawnParticle(EnumParticleTypes.PORTAL, d, e, d2, d3, d4, d5);
      }
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return null;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return getMetaForAxis(state.getValue(AXIS));
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AXIS);
   }

   public BlockPattern.PatternHelper func_181089_f(World world, BlockPos blockPos) {
      EnumFacing.Axis axis = EnumFacing.Axis.Z;
      BlockPortal.Size size = new BlockPortal.Size(world, blockPos, EnumFacing.Axis.X);
      LoadingCache loadingCache = BlockPattern.func_181627_a(world, true);
      if (!size.func_150860_b()) {
         axis = EnumFacing.Axis.X;
         size = new BlockPortal.Size(world, blockPos, EnumFacing.Axis.Z);
      }

      if (!size.func_150860_b()) {
         return new BlockPattern.PatternHelper(blockPos, EnumFacing.NORTH, EnumFacing.UP, loadingCache, 1, 1, 1);
      }

      int[] ints = new int[EnumFacing.AxisDirection.values().length];
      EnumFacing enumFacing = size.field_150866_c.rotateYCCW();
      BlockPos blockPos2 = size.field_150861_f.up(size.func_181100_a() - 1);

      for (EnumFacing.AxisDirection axisDirection : EnumFacing.AxisDirection.values()) {
         BlockPattern.PatternHelper patternHelper = new BlockPattern.PatternHelper(
            enumFacing.getAxisDirection() == axisDirection ? blockPos2 : blockPos2.offset(size.field_150866_c, size.func_181101_b() - 1),
            EnumFacing.getFacingFromAxis(axisDirection, axis),
            EnumFacing.UP,
            loadingCache,
            size.func_181101_b(),
            size.func_181100_a(),
            1
         );

         for (int i = 0; i < size.func_181101_b(); i++) {
            for (int j = 0; j < size.func_181100_a(); j++) {
               BlockWorldState blockWorldState = patternHelper.translateOffset(i, j, 1);
               if (blockWorldState.getBlockState() != null && blockWorldState.getBlockState().getBlock().getMaterial() != Material.air) {
                  ints[axisDirection.ordinal()]++;
               }
            }
         }
      }

      EnumFacing.AxisDirection axisDirection2 = EnumFacing.AxisDirection.POSITIVE;

      for (EnumFacing.AxisDirection axisDirection3 : EnumFacing.AxisDirection.values()) {
         if (ints[axisDirection3.ordinal()] < ints[axisDirection2.ordinal()]) {
            axisDirection2 = axisDirection3;
         }
      }

      return new BlockPattern.PatternHelper(
         enumFacing.getAxisDirection() == axisDirection2 ? blockPos2 : blockPos2.offset(size.field_150866_c, size.func_181101_b() - 1),
         EnumFacing.getFacingFromAxis(axisDirection2, axis),
         EnumFacing.UP,
         loadingCache,
         size.func_181101_b(),
         size.func_181100_a(),
         1
      );
   }

   public static class Size {
      private final World world;
      private final EnumFacing.Axis axis;
      private final EnumFacing field_150866_c;
      private final EnumFacing field_150863_d;
      private int field_150864_e = 0;
      private BlockPos field_150861_f;
      private int field_150862_g;
      private int field_150868_h;

      public Size(World worldIn, BlockPos blockPos, EnumFacing.Axis axis) {
         this.world = worldIn;
         this.axis = axis;
         if (axis == EnumFacing.Axis.X) {
            this.field_150863_d = EnumFacing.EAST;
            this.field_150866_c = EnumFacing.WEST;
         } else {
            this.field_150863_d = EnumFacing.NORTH;
            this.field_150866_c = EnumFacing.SOUTH;
         }

         BlockPos blockPos2 = blockPos;

         while (blockPos.getY() > blockPos2.getY() - 21 && blockPos.getY() > 0 && this.func_150857_a(worldIn.getBlockState(blockPos.down()).getBlock())) {
            blockPos = blockPos.down();
         }

         int i = this.func_180120_a(blockPos, this.field_150863_d) - 1;
         if (i >= 0) {
            this.field_150861_f = blockPos.offset(this.field_150863_d, i);
            this.field_150868_h = this.func_180120_a(this.field_150861_f, this.field_150866_c);
            if (this.field_150868_h < 2 || this.field_150868_h > 21) {
               this.field_150861_f = null;
               this.field_150868_h = 0;
            }
         }

         if (this.field_150861_f != null) {
            this.field_150862_g = this.func_150858_a();
         }
      }

      public int func_180120_a(BlockPos blockPos, EnumFacing enumFacing) {
         int i;
         for (i = 0; i < 22; i++) {
            BlockPos blockPos2 = blockPos.offset(enumFacing, i);
            if (!this.func_150857_a(this.world.getBlockState(blockPos2).getBlock()) || this.world.getBlockState(blockPos2.down()).getBlock() != Blocks.obsidian) {
               break;
            }
         }

         Block block = this.world.getBlockState(blockPos.offset(enumFacing, i)).getBlock();
         return block == Blocks.obsidian ? i : 0;
      }

      public int func_181100_a() {
         return this.field_150862_g;
      }

      public int func_181101_b() {
         return this.field_150868_h;
      }

      public int func_150858_a() {
         label56:
         for (this.field_150862_g = 0; this.field_150862_g < 21; this.field_150862_g++) {
            for (int i = 0; i < this.field_150868_h; i++) {
               BlockPos blockPos = this.field_150861_f.offset(this.field_150866_c, i).up(this.field_150862_g);
               Block block = this.world.getBlockState(blockPos).getBlock();
               if (!this.func_150857_a(block)) {
                  break label56;
               }

               if (block == Blocks.portal) {
                  this.field_150864_e++;
               }

               if (i == 0) {
                  block = this.world.getBlockState(blockPos.offset(this.field_150863_d)).getBlock();
                  if (block != Blocks.obsidian) {
                     break label56;
                  }
               } else if (i == this.field_150868_h - 1) {
                  block = this.world.getBlockState(blockPos.offset(this.field_150866_c)).getBlock();
                  if (block != Blocks.obsidian) {
                     break label56;
                  }
               }
            }
         }

         for (int j = 0; j < this.field_150868_h; j++) {
            if (this.world.getBlockState(this.field_150861_f.offset(this.field_150866_c, j).up(this.field_150862_g)).getBlock() != Blocks.obsidian) {
               this.field_150862_g = 0;
               break;
            }
         }

         if (this.field_150862_g <= 21 && this.field_150862_g >= 3) {
            return this.field_150862_g;
         }

         this.field_150861_f = null;
         this.field_150868_h = 0;
         this.field_150862_g = 0;
         return 0;
      }

      public boolean func_150857_a(Block block) {
         return block.blockMaterial == Material.air || block == Blocks.fire || block == Blocks.portal;
      }

      public boolean func_150860_b() {
         return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
      }

      public void func_150859_c() {
         for (int i = 0; i < this.field_150868_h; i++) {
            BlockPos blockPos = this.field_150861_f.offset(this.field_150866_c, i);

            for (int j = 0; j < this.field_150862_g; j++) {
               this.world.setBlockState(blockPos.up(j), Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, this.axis), 2);
            }
         }
      }
   }
}
