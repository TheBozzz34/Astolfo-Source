package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVine extends Block {
   public static final PropertyBool UP = PropertyBool.create("up");
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   public static final PropertyBool[] ALL_FACES = new PropertyBool[]{UP, NORTH, SOUTH, WEST, EAST};

   public BlockVine() {
      super(Material.vine);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(UP, false)
            .withProperty(NORTH, false)
            .withProperty(EAST, false)
            .withProperty(SOUTH, false)
            .withProperty(WEST, false)
      );
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(UP, worldIn.getBlockState(pos.up()).getBlock().isBlockNormalCube());
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
   public boolean isReplaceable(World worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      float f = 1.0F;
      float g = 1.0F;
      float h = 1.0F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      boolean bl = false;
      if (worldIn.getBlockState(pos).getValue(WEST)) {
         f2 = Math.max(f2, 0.0625F);
         f = 0.0F;
         g = 0.0F;
         f3 = 1.0F;
         h = 0.0F;
         f4 = 1.0F;
         bl = true;
      }

      if (worldIn.getBlockState(pos).getValue(EAST)) {
         f = Math.min(f, 0.9375F);
         f2 = 1.0F;
         g = 0.0F;
         f3 = 1.0F;
         h = 0.0F;
         f4 = 1.0F;
         bl = true;
      }

      if (worldIn.getBlockState(pos).getValue(NORTH)) {
         f4 = Math.max(f4, 0.0625F);
         h = 0.0F;
         f = 0.0F;
         f2 = 1.0F;
         g = 0.0F;
         f3 = 1.0F;
         bl = true;
      }

      if (worldIn.getBlockState(pos).getValue(SOUTH)) {
         h = Math.min(h, 0.9375F);
         f4 = 1.0F;
         f = 0.0F;
         f2 = 1.0F;
         g = 0.0F;
         f3 = 1.0F;
         bl = true;
      }

      if (!bl && this.canPlaceOn(worldIn.getBlockState(pos.up()).getBlock())) {
         g = Math.min(g, 0.9375F);
         f3 = 1.0F;
         f = 0.0F;
         f2 = 1.0F;
         h = 0.0F;
         f4 = 1.0F;
      }

      this.setBlockBounds(f, g, h, f2, f3, f4);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      switch (side) {
         case UP:
            return this.canPlaceOn(worldIn.getBlockState(pos.up()).getBlock());
         case NORTH:
         case SOUTH:
         case EAST:
         case WEST:
            return this.canPlaceOn(worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock());
         default:
            return false;
      }
   }

   private boolean canPlaceOn(Block blockIn) {
      return blockIn.isFullCube() && blockIn.blockMaterial.blocksMovement();
   }

   private boolean recheckGrownSides(World worldIn, BlockPos pos, IBlockState state) {
      IBlockState iBlockState = state;

      for (Object object : EnumFacing.Plane.HORIZONTAL) {
         PropertyBool propertyBool = getPropertyFor((EnumFacing)object);
         if (state.getValue(propertyBool) && !this.canPlaceOn(worldIn.getBlockState(pos.offset((EnumFacing)object)).getBlock())) {
            IBlockState iBlockState2 = worldIn.getBlockState(pos.up());
            if (iBlockState2.getBlock() != this || !iBlockState2.getValue(propertyBool)) {
               state = state.withProperty(propertyBool, false);
            }
         }
      }

      if (getNumGrownFaces(state) == 0) {
         return false;
      }

      if (iBlockState != state) {
         worldIn.setBlockState(pos, state, 2);
      }

      return true;
   }

   @Override
   public int getBlockColor() {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   @Override
   public int getRenderColor(IBlockState state) {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return worldIn.getBiomeGenForCoords(pos).getFoliageColorAtPos(pos);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote && !this.recheckGrownSides(worldIn, pos, state)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && worldIn.rand.nextInt(4) == 0) {
         byte b = 4;
         int i = 5;
         boolean bl = false;

         label182:
         for (int j = -b; j <= b; j++) {
            for (int k = -b; k <= b; k++) {
               for (int l = -1; l <= 1; l++) {
                  if (worldIn.getBlockState(pos.add(j, l, k)).getBlock() == this) {
                     if (--i <= 0) {
                        bl = true;
                        break label182;
                     }
                  }
               }
            }
         }

         EnumFacing enumFacing = EnumFacing.random(rand);
         BlockPos blockPos = pos.up();
         if (enumFacing == EnumFacing.UP && pos.getY() < 255 && worldIn.isAirBlock(blockPos)) {
            if (!bl) {
               IBlockState iBlockState = state;

               for (Object object : EnumFacing.Plane.HORIZONTAL) {
                  if (rand.nextBoolean() || !this.canPlaceOn(worldIn.getBlockState(blockPos.offset((EnumFacing)object)).getBlock())) {
                     iBlockState = iBlockState.withProperty(getPropertyFor((EnumFacing)object), false);
                  }
               }

               if (iBlockState.getValue(NORTH) || iBlockState.getValue(EAST) || iBlockState.getValue(SOUTH) || iBlockState.getValue(WEST)) {
                  worldIn.setBlockState(blockPos, iBlockState, 2);
               }
            }
         } else if (!enumFacing.getAxis().isHorizontal() || state.getValue(getPropertyFor(enumFacing))) {
            if (pos.getY() > 1) {
               BlockPos blockPos2 = pos.down();
               IBlockState iBlockState2 = worldIn.getBlockState(blockPos2);
               Block block = iBlockState2.getBlock();
               if (block.blockMaterial == Material.air) {
                  IBlockState iBlockState3 = state;

                  for (Object object2 : EnumFacing.Plane.HORIZONTAL) {
                     if (rand.nextBoolean()) {
                        iBlockState3 = iBlockState3.withProperty(getPropertyFor((EnumFacing)object2), false);
                     }
                  }

                  if (iBlockState3.getValue(NORTH) || iBlockState3.getValue(EAST) || iBlockState3.getValue(SOUTH) || iBlockState3.getValue(WEST)) {
                     worldIn.setBlockState(blockPos2, iBlockState3, 2);
                  }
               } else if (block == this) {
                  IBlockState iBlockState4 = iBlockState2;

                  for (Object object3 : EnumFacing.Plane.HORIZONTAL) {
                     PropertyBool propertyBool = getPropertyFor((EnumFacing)object3);
                     if (rand.nextBoolean() && state.getValue(propertyBool)) {
                        iBlockState4 = iBlockState4.withProperty(propertyBool, true);
                     }
                  }

                  if (iBlockState4.getValue(NORTH) || iBlockState4.getValue(EAST) || iBlockState4.getValue(SOUTH) || iBlockState4.getValue(WEST)) {
                     worldIn.setBlockState(blockPos2, iBlockState4, 2);
                  }
               }
            }
         } else if (!bl) {
            BlockPos blockPos3 = pos.offset(enumFacing);
            Block block2 = worldIn.getBlockState(blockPos3).getBlock();
            if (block2.blockMaterial == Material.air) {
               EnumFacing enumFacing2 = enumFacing.rotateY();
               EnumFacing enumFacing3 = enumFacing.rotateYCCW();
               boolean bl2 = state.getValue(getPropertyFor(enumFacing2));
               boolean bl3 = state.getValue(getPropertyFor(enumFacing3));
               BlockPos blockPos4 = blockPos3.offset(enumFacing2);
               BlockPos blockPos5 = blockPos3.offset(enumFacing3);
               if (bl2 && this.canPlaceOn(worldIn.getBlockState(blockPos4).getBlock())) {
                  worldIn.setBlockState(blockPos3, this.getDefaultState().withProperty(getPropertyFor(enumFacing2), true), 2);
               } else if (bl3 && this.canPlaceOn(worldIn.getBlockState(blockPos5).getBlock())) {
                  worldIn.setBlockState(blockPos3, this.getDefaultState().withProperty(getPropertyFor(enumFacing3), true), 2);
               } else if (bl2 && worldIn.isAirBlock(blockPos4) && this.canPlaceOn(worldIn.getBlockState(pos.offset(enumFacing2)).getBlock())) {
                  worldIn.setBlockState(blockPos4, this.getDefaultState().withProperty(getPropertyFor(enumFacing.getOpposite()), true), 2);
               } else if (bl3 && worldIn.isAirBlock(blockPos5) && this.canPlaceOn(worldIn.getBlockState(pos.offset(enumFacing3)).getBlock())) {
                  worldIn.setBlockState(blockPos5, this.getDefaultState().withProperty(getPropertyFor(enumFacing.getOpposite()), true), 2);
               } else if (this.canPlaceOn(worldIn.getBlockState(blockPos3.up()).getBlock())) {
                  worldIn.setBlockState(blockPos3, this.getDefaultState(), 2);
               }
            } else if (block2.blockMaterial.isOpaque() && block2.isFullCube()) {
               worldIn.setBlockState(pos, state.withProperty(getPropertyFor(enumFacing), true), 2);
            }
         }
      }
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState iBlockState = this.getDefaultState()
         .withProperty(UP, false)
         .withProperty(NORTH, false)
         .withProperty(EAST, false)
         .withProperty(SOUTH, false)
         .withProperty(WEST, false);
      return facing.getAxis().isHorizontal() ? iBlockState.withProperty(getPropertyFor(facing.getOpposite()), true) : iBlockState;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public int quantityDropped(Random randomIn) {
      return 0;
   }

   @Override
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
      if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
         player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
         spawnAsEntity(worldIn, pos, new ItemStack(Blocks.vine, 1, 0));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(SOUTH, (meta & 1) > 0)
         .withProperty(WEST, (meta & 2) > 0)
         .withProperty(NORTH, (meta & 4) > 0)
         .withProperty(EAST, (meta & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      byte b = 0;
      if (state.getValue(SOUTH)) {
         b |= 1;
      }

      if (state.getValue(WEST)) {
         b |= 2;
      }

      if (state.getValue(NORTH)) {
         b |= 4;
      }

      if (state.getValue(EAST)) {
         b |= 8;
      }

      return b;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, UP, NORTH, EAST, SOUTH, WEST);
   }

   public static PropertyBool getPropertyFor(EnumFacing side) {
      switch (side) {
         case UP:
            return UP;
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case EAST:
            return EAST;
         case WEST:
            return WEST;
         default:
            throw new IllegalArgumentException(side + " is an invalid choice");
      }
   }

   public static int getNumGrownFaces(IBlockState state) {
      int i = 0;

      for (PropertyBool propertyBool : ALL_FACES) {
         if (state.getValue(propertyBool)) {
            i++;
         }
      }

      return i;
   }
}
