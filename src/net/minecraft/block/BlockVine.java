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
   private static final long C = 78731357203990L;

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
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.withProperty(UP, var2.getBlockState(var3.up()).getBlock().isBlockNormalCube());
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
   public boolean isReplaceable(World var1, BlockPos var2) {
      return true;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      float var7 = 1.0F;
      float var8 = 1.0F;
      float var9 = 1.0F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      float var12 = 0.0F;
      boolean var13 = false;
      if (var1.getBlockState(var2).getValue(WEST)) {
         var10 = Math.max(var10, 0.0625F);
         var7 = 0.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var9 = 0.0F;
         var12 = 1.0F;
         var13 = true;
      }

      if (var1.getBlockState(var2).getValue(EAST)) {
         var7 = Math.min(var7, 0.9375F);
         var10 = 1.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var9 = 0.0F;
         var12 = 1.0F;
         var13 = true;
      }

      if (var1.getBlockState(var2).getValue(NORTH)) {
         var12 = Math.max(var12, 0.0625F);
         var9 = 0.0F;
         var7 = 0.0F;
         var10 = 1.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var13 = true;
      }

      if (var1.getBlockState(var2).getValue(SOUTH)) {
         var9 = Math.min(var9, 0.9375F);
         var12 = 1.0F;
         var7 = 0.0F;
         var10 = 1.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var13 = true;
      }

      if (!var13 && this.canPlaceOn(var1.getBlockState(var2.up()).getBlock())) {
         var8 = Math.min(var8, 0.9375F);
         var11 = 1.0F;
         var7 = 0.0F;
         var10 = 1.0F;
         var9 = 0.0F;
         var12 = 1.0F;
      }

      this.setBlockBounds(var7, var8, var9, var10, var11, var12);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   @Override
   public boolean canPlaceBlockOnSide(World var1, BlockPos var2, EnumFacing var3) {
      switch (var3) {
         case UP:
            return this.canPlaceOn(var1.getBlockState(var2.up()).getBlock());
         case NORTH:
         case SOUTH:
         case EAST:
         case WEST:
            return this.canPlaceOn(var1.getBlockState(var2.offset(var3.getOpposite())).getBlock());
         default:
            return false;
      }
   }

   private boolean canPlaceOn(Block var1) {
      return var1.isFullCube() && var1.blockMaterial.blocksMovement();
   }

   private boolean recheckGrownSides(World var1, BlockPos var2, IBlockState var3) {
      IBlockState var7 = var3;

      for (Object var9 : EnumFacing.Plane.HORIZONTAL) {
         PropertyBool var10 = getPropertyFor((EnumFacing)var9);
         if (var3.getValue(var10) && !this.canPlaceOn(var1.getBlockState(var2.offset((EnumFacing)var9)).getBlock())) {
            IBlockState var11 = var1.getBlockState(var2.up());
            if (var11.getBlock() != this || !var11.getValue(var10)) {
               var3 = var3.withProperty(var10, false);
            }
         }
      }

      if (getNumGrownFaces(var3) == 0) {
         return false;
      }

      if (var7 != var3) {
         var1.setBlockState(var2, var3, 2);
      }

      return true;
   }

   @Override
   public int getBlockColor() {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   @Override
   public int getRenderColor(IBlockState var1) {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   @Override
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      return var1.getBiomeGenForCoords(var2).getFoliageColorAtPos(var2);
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.isRemote && !this.recheckGrownSides(var1, var2, var3)) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
      }
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.isRemote && var1.rand.nextInt(4) == 0) {
         byte var8 = 4;
         int var9 = 5;
         boolean var10 = false;

         label182:
         for (int var11 = -var8; var11 <= var8; var11++) {
            for (int var12 = -var8; var12 <= var8; var12++) {
               for (int var13 = -1; var13 <= 1; var13++) {
                  if (var1.getBlockState(var2.add(var11, var13, var12)).getBlock() == this) {
                     if (--var9 <= 0) {
                        var10 = true;
                        break label182;
                     }
                  }
               }
            }
         }

         EnumFacing var21 = EnumFacing.random(var4);
         BlockPos var22 = var2.up();
         if (var21 == EnumFacing.UP && var2.getY() < 255 && var1.isAirBlock(var22)) {
            if (!var10) {
               IBlockState var25 = var3;

               for (Object var29 : EnumFacing.Plane.HORIZONTAL) {
                  if (var4.nextBoolean() || !this.canPlaceOn(var1.getBlockState(var22.offset((EnumFacing)var29)).getBlock())) {
                     var25 = var25.withProperty(getPropertyFor((EnumFacing)var29), false);
                  }
               }

               if (var25.getValue(NORTH) || var25.getValue(EAST) || var25.getValue(SOUTH) || var25.getValue(WEST)) {
                  var1.setBlockState(var22, var25, 2);
               }
            }
         } else if (!var21.getAxis().isHorizontal() || var3.getValue(getPropertyFor(var21))) {
            if (var2.getY() > 1) {
               BlockPos var24 = var2.down();
               IBlockState var26 = var1.getBlockState(var24);
               Block var28 = var26.getBlock();
               if (var28.blockMaterial == Material.air) {
                  IBlockState var30 = var3;

                  for (Object var34 : EnumFacing.Plane.HORIZONTAL) {
                     if (var4.nextBoolean()) {
                        var30 = var30.withProperty(getPropertyFor((EnumFacing)var34), false);
                     }
                  }

                  if (var30.getValue(NORTH) || var30.getValue(EAST) || var30.getValue(SOUTH) || var30.getValue(WEST)) {
                     var1.setBlockState(var24, var30, 2);
                  }
               } else if (var28 == this) {
                  IBlockState var31 = var26;

                  for (Object var35 : EnumFacing.Plane.HORIZONTAL) {
                     PropertyBool var36 = getPropertyFor((EnumFacing)var35);
                     if (var4.nextBoolean() && var3.getValue(var36)) {
                        var31 = var31.withProperty(var36, true);
                     }
                  }

                  if (var31.getValue(NORTH) || var31.getValue(EAST) || var31.getValue(SOUTH) || var31.getValue(WEST)) {
                     var1.setBlockState(var24, var31, 2);
                  }
               }
            }
         } else if (!var10) {
            BlockPos var23 = var2.offset(var21);
            Block var14 = var1.getBlockState(var23).getBlock();
            if (var14.blockMaterial == Material.air) {
               EnumFacing var15 = var21.rotateY();
               EnumFacing var16 = var21.rotateYCCW();
               boolean var17 = var3.getValue(getPropertyFor(var15));
               boolean var18 = var3.getValue(getPropertyFor(var16));
               BlockPos var19 = var23.offset(var15);
               BlockPos var20 = var23.offset(var16);
               if (var17 && this.canPlaceOn(var1.getBlockState(var19).getBlock())) {
                  var1.setBlockState(var23, this.getDefaultState().withProperty(getPropertyFor(var15), true), 2);
               } else if (var18 && this.canPlaceOn(var1.getBlockState(var20).getBlock())) {
                  var1.setBlockState(var23, this.getDefaultState().withProperty(getPropertyFor(var16), true), 2);
               } else if (var17 && var1.isAirBlock(var19) && this.canPlaceOn(var1.getBlockState(var2.offset(var15)).getBlock())) {
                  var1.setBlockState(var19, this.getDefaultState().withProperty(getPropertyFor(var21.getOpposite()), true), 2);
               } else if (var18 && var1.isAirBlock(var20) && this.canPlaceOn(var1.getBlockState(var2.offset(var16)).getBlock())) {
                  var1.setBlockState(var20, this.getDefaultState().withProperty(getPropertyFor(var21.getOpposite()), true), 2);
               } else if (this.canPlaceOn(var1.getBlockState(var23.up()).getBlock())) {
                  var1.setBlockState(var23, this.getDefaultState(), 2);
               }
            } else if (var14.blockMaterial.isOpaque() && var14.isFullCube()) {
               var1.setBlockState(var2, var3.withProperty(getPropertyFor(var21), true), 2);
            }
         }
      }
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var11 = this.getDefaultState()
         .withProperty(UP, false)
         .withProperty(NORTH, false)
         .withProperty(EAST, false)
         .withProperty(SOUTH, false)
         .withProperty(WEST, false);
      return var3.getAxis().isHorizontal() ? var11.withProperty(getPropertyFor(var3.getOpposite()), true) : var11;
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return null;
   }

   @Override
   public int quantityDropped(Random var1) {
      return 0;
   }

   @Override
   public void harvestBlock(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if (!var1.isRemote && var2.getCurrentEquippedItem() != null && var2.getCurrentEquippedItem().getItem() == Items.shears) {
         var2.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
         spawnAsEntity(var1, var3, new ItemStack(Blocks.vine, 1, 0));
      } else {
         super.harvestBlock(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState()
         .withProperty(SOUTH, (var1 & 1) > 0)
         .withProperty(WEST, (var1 & 2) > 0)
         .withProperty(NORTH, (var1 & 4) > 0)
         .withProperty(EAST, (var1 & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      byte var5 = 0;
      if (var1.getValue(SOUTH)) {
         var5 |= 1;
      }

      if (var1.getValue(WEST)) {
         var5 |= 2;
      }

      if (var1.getValue(NORTH)) {
         var5 |= 4;
      }

      if (var1.getValue(EAST)) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, UP, NORTH, EAST, SOUTH, WEST);
   }

   public static PropertyBool getPropertyFor(EnumFacing var0) {
      switch (var0) {
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
            throw new IllegalArgumentException(var0 + " is an invalid choice");
      }
   }

   public static int getNumGrownFaces(IBlockState var0) {
      int var4 = 0;

      for (PropertyBool var8 : ALL_FACES) {
         if (var0.getValue(var8)) {
            var4++;
         }
      }

      return var4;
   }
}
