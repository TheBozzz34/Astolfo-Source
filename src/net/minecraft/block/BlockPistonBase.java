package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing");
   public static final PropertyBool EXTENDED = PropertyBool.create("extended");
   private final boolean isSticky;
   private static final long e = 6865743439998L;

   public BlockPistonBase(boolean var1) {
      super(Material.piston);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EXTENDED, false));
      this.isSticky = var1;
      this.setStepSound(soundTypePiston);
      this.setHardness(0.5F);
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.setBlockState(var2, var3.withProperty(FACING, getFacingFromEntity(var1, var2, var4)), 2);
      if (!var1.isRemote) {
         this.checkForMove(var1, var2, var3);
      }
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.isRemote) {
         this.checkForMove(var1, var2, var3);
      }
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.isRemote && var1.getTileEntity(var2) == null) {
         this.checkForMove(var1, var2, var3);
      }
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, getFacingFromEntity(var1, var2, var8)).withProperty(EXTENDED, false);
   }

   private void checkForMove(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var7 = var3.getValue(FACING);
      boolean var8 = this.shouldBeExtended(var1, var2, var7);
      if (var8 && !var3.getValue(EXTENDED)) {
         if (new BlockPistonStructureHelper(var1, var2, var7, true).canMove()) {
            var1.addBlockEvent(var2, this, 0, var7.getIndex());
         }
      } else if (!var8 && var3.getValue(EXTENDED)) {
         var1.setBlockState(var2, var3.withProperty(EXTENDED, false), 2);
         var1.addBlockEvent(var2, this, 1, var7.getIndex());
      }
   }

   private boolean shouldBeExtended(World var1, BlockPos var2, EnumFacing var3) {
      for (EnumFacing var10 : EnumFacing.values()) {
         if (var10 != var3 && var1.isSidePowered(var2.offset(var10), var10)) {
            return true;
         }
      }

      if (var1.isSidePowered(var2, EnumFacing.DOWN)) {
         return true;
      }

      BlockPos var12 = var2.up();

      for (EnumFacing var11 : EnumFacing.values()) {
         if (var11 != EnumFacing.DOWN && var1.isSidePowered(var12.offset(var11), var11)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onBlockEventReceived(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      EnumFacing var9 = var3.getValue(FACING);
      if (!var1.isRemote) {
         boolean var10 = this.shouldBeExtended(var1, var2, var9);
         if (var10 && var4 == 1) {
            var1.setBlockState(var2, var3.withProperty(EXTENDED, true), 2);
            return false;
         }

         if (!var10 && var4 == 0) {
            return false;
         }
      }

      if (var4 == 0) {
         if (!this.doMove(var1, var2, var9, true)) {
            return false;
         }

         var1.setBlockState(var2, var3.withProperty(EXTENDED, true), 2);
         var1.playSoundEffect(var2.getX() + 0.5, var2.getY() + 0.5, var2.getZ() + 0.5, "tile.piston.out", 0.5F, var1.rand.nextFloat() * 0.25F + 0.6F);
      } else if (var4 == 1) {
         TileEntity var16 = var1.getTileEntity(var2.offset(var9));
         if (var16 instanceof TileEntityPiston) {
            ((TileEntityPiston)var16).clearPistonTileEntity();
         }

         var1.setBlockState(
            var2,
            Blocks.piston_extension
               .getDefaultState()
               .withProperty(BlockPistonMoving.FACING, var9)
               .withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT),
            3
         );
         var1.setTileEntity(var2, BlockPistonMoving.newTileEntity(this.getStateFromMeta(var5), var9, false, true));
         if (this.isSticky) {
            BlockPos var11 = var2.add(var9.getFrontOffsetX() * 2, var9.getFrontOffsetY() * 2, var9.getFrontOffsetZ() * 2);
            Block var12 = var1.getBlockState(var11).getBlock();
            boolean var13 = false;
            if (var12 == Blocks.piston_extension) {
               TileEntity var14 = var1.getTileEntity(var11);
               if (var14 instanceof TileEntityPiston) {
                  TileEntityPiston var15 = (TileEntityPiston)var14;
                  if (var15.getFacing() == var9 && var15.isExtending()) {
                     var15.clearPistonTileEntity();
                     var13 = true;
                  }
               }
            }

            if (!var13
               && var12.getMaterial() != Material.air
               && canPush(var12, var1, var11, var9.getOpposite(), false)
               && (var12.getMobilityFlag() == 0 || var12 == Blocks.piston || var12 == Blocks.sticky_piston)) {
               this.doMove(var1, var2, var9, false);
            }
         } else {
            var1.setBlockToAir(var2.offset(var9));
         }

         var1.playSoundEffect(var2.getX() + 0.5, var2.getY() + 0.5, var2.getZ() + 0.5, "tile.piston.in", 0.5F, var1.rand.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      if (var6.getBlock() == this && var6.getValue(EXTENDED)) {
         EnumFacing var8 = var6.getValue(FACING);
         if (var8 != null) {
            switch (var8) {
               case DOWN:
                  this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                  break;
               case UP:
                  this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                  break;
               case NORTH:
                  this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                  break;
               case SOUTH:
                  this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                  break;
               case WEST:
                  this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                  break;
               case EAST:
                  this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }
         }
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.getCollisionBoundingBox(var1, var2, var3);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public static EnumFacing getFacing(int var0) {
      int var4 = var0 & 7;
      return var4 > 5 ? null : EnumFacing.getFront(var4);
   }

   public static EnumFacing getFacingFromEntity(World var0, BlockPos var1, EntityLivingBase var2) {
      if (MathHelper.abs((float)var2.posX - var1.getX()) < 2.0F && MathHelper.abs((float)var2.posZ - var1.getZ()) < 2.0F) {
         double var6 = var2.posY + var2.getEyeHeight();
         if (var6 - var1.getY() > 2.0) {
            return EnumFacing.UP;
         }

         if (var1.getY() - var6 > 0.0) {
            return EnumFacing.DOWN;
         }
      }

      return var2.getHorizontalFacing().getOpposite();
   }

   public static boolean canPush(Block var0, World var1, BlockPos var2, EnumFacing var3, boolean var4) {
      if (var0 == Blocks.obsidian) {
         return false;
      }

      if (!var1.getWorldBorder().contains(var2)) {
         return false;
      }

      if (var2.getY() >= 0 && (var3 != EnumFacing.DOWN || var2.getY() != 0)) {
         if (var2.getY() <= var1.getHeight() - 1 && (var3 != EnumFacing.UP || var2.getY() != var1.getHeight() - 1)) {
            if (var0 != Blocks.piston && var0 != Blocks.sticky_piston) {
               if (var0.getBlockHardness(var1, var2) == -1.0F) {
                  return false;
               }

               if (var0.getMobilityFlag() == 2) {
                  return false;
               }

               if (var0.getMobilityFlag() == 1) {
                  if (!var4) {
                     return false;
                  }

                  return true;
               }
            } else if (var1.getBlockState(var2).getValue(EXTENDED)) {
               return false;
            }

            return !(var0 instanceof ITileEntityProvider);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean doMove(World var1, BlockPos var2, EnumFacing var3, boolean var4) {
      if (!var4) {
         var1.setBlockToAir(var2.offset(var3));
      }

      BlockPistonStructureHelper var8 = new BlockPistonStructureHelper(var1, var2, var3, var4);
      List var9 = var8.getBlocksToMove();
      List var10 = var8.getBlocksToDestroy();
      if (!var8.canMove()) {
         return false;
      }

      int var11 = var9.size() + var10.size();
      Block[] var12 = new Block[var11];
      EnumFacing var13 = var4 ? var3 : var3.getOpposite();

      for (int var14 = var10.size() - 1; var14 >= 0; var14--) {
         BlockPos var15 = (BlockPos)var10.get(var14);
         Block var16 = var1.getBlockState(var15).getBlock();
         var16.dropBlockAsItem(var1, var15, var1.getBlockState(var15), 0);
         var1.setBlockToAir(var15);
         var12[--var11] = var16;
      }

      for (int var18 = var9.size() - 1; var18 >= 0; var18--) {
         BlockPos var20 = (BlockPos)var9.get(var18);
         IBlockState var25 = var1.getBlockState(var20);
         Block var17 = var25.getBlock();
         var17.getMetaFromState(var25);
         var1.setBlockToAir(var20);
         var20 = var20.offset(var13);
         var1.setBlockState(var20, Blocks.piston_extension.getDefaultState().withProperty(FACING, var3), 4);
         var1.setTileEntity(var20, BlockPistonMoving.newTileEntity(var25, var3, var4, false));
         var12[--var11] = var17;
      }

      BlockPos var19 = var2.offset(var3);
      if (var4) {
         BlockPistonExtension.EnumPistonType var22 = this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
         IBlockState var26 = Blocks.piston_head
            .getDefaultState()
            .withProperty(BlockPistonExtension.FACING, var3)
            .withProperty(BlockPistonExtension.TYPE, var22);
         IBlockState var27 = Blocks.piston_extension
            .getDefaultState()
            .withProperty(BlockPistonMoving.FACING, var3)
            .withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
         var1.setBlockState(var19, var27, 4);
         var1.setTileEntity(var19, BlockPistonMoving.newTileEntity(var26, var3, true, false));
      }

      for (int var23 = var10.size() - 1; var23 >= 0; var23--) {
         var1.notifyNeighborsOfStateChange((BlockPos)var10.get(var23), var12[var11++]);
      }

      for (int var24 = var9.size() - 1; var24 >= 0; var24--) {
         var1.notifyNeighborsOfStateChange((BlockPos)var9.get(var24), var12[var11++]);
      }

      if (var4) {
         var1.notifyNeighborsOfStateChange(var19, Blocks.piston_head);
         var1.notifyNeighborsOfStateChange(var2, this);
      }

      return true;
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.UP);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, getFacing(var1)).withProperty(EXTENDED, (var1 & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getIndex();
      if (var1.getValue(EXTENDED)) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, EXTENDED);
   }
}
