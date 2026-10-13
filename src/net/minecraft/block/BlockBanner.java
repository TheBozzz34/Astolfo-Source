package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBanner extends BlockContainer {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 15);
   private static final long i = 13476397745584L;

   public BlockBanner() {
      super(Material.wood);
      float var1 = 0.25F;
      float var2 = 1.0F;
      this.setBlockBounds(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var2, 0.5F + var1);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.banner.white.name");
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World var1, BlockPos var2) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.getSelectedBoundingBox(var1, var2);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isPassable(IBlockAccess var1, BlockPos var2) {
      return true;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean canSpawnInBlock() {
      return true;
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityBanner();
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.banner;
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Items.banner;
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      TileEntity var9 = var1.getTileEntity(var2);
      if (var9 instanceof TileEntityBanner) {
         ItemStack var10 = new ItemStack(Items.banner, 1, ((TileEntityBanner)var9).getBaseColor());
         NBTTagCompound var11 = new NBTTagCompound();
         var9.writeToNBT(var11);
         var11.removeTag("x");
         var11.removeTag("y");
         var11.removeTag("z");
         var11.removeTag("id");
         var10.setTagInfo("BlockEntityTag", var11);
         spawnAsEntity(var1, var2, var10);
      } else {
         super.dropBlockAsItemWithChance(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return !this.hasInvalidNeighbor(var1, var2) && super.canPlaceBlockAt(var1, var2);
   }

   @Override
   public void harvestBlock(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if (var5 instanceof TileEntityBanner) {
         TileEntityBanner var9 = (TileEntityBanner)var5;
         ItemStack var10 = new ItemStack(Items.banner, 1, ((TileEntityBanner)var5).getBaseColor());
         NBTTagCompound var11 = new NBTTagCompound();
         TileEntityBanner.setBaseColorAndPatterns(var11, var9.getBaseColor(), var9.getPatterns());
         var10.setTagInfo("BlockEntityTag", var11);
         spawnAsEntity(var1, var3, var10);
      } else {
         super.harvestBlock(var1, var2, var3, var4, (TileEntity)null);
      }
   }

   public static class BlockBannerHanging extends BlockBanner {
      private static final long l = 59533678655776L;

      public BlockBannerHanging() {
         this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      }

      @Override
      public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
         EnumFacing var6 = var1.getBlockState(var2).getValue(FACING);
         float var7 = 0.0F;
         float var8 = 0.78125F;
         float var9 = 0.0F;
         float var10 = 1.0F;
         float var11 = 0.125F;
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         switch (var6) {
            case NORTH:
            default:
               this.setBlockBounds(var9, var7, 1.0F - var11, var10, var8, 1.0F);
               break;
            case SOUTH:
               this.setBlockBounds(var9, var7, 0.0F, var10, var8, var11);
               break;
            case WEST:
               this.setBlockBounds(1.0F - var11, var7, var9, 1.0F, var8, var10);
               break;
            case EAST:
               this.setBlockBounds(0.0F, var7, var9, var11, var8, var10);
         }
      }

      @Override
      public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
         EnumFacing var8 = var3.getValue(FACING);
         if (!var1.getBlockState(var2.offset(var8.getOpposite())).getBlock().getMaterial().isSolid()) {
            this.dropBlockAsItem(var1, var2, var3, 0);
            var1.setBlockToAir(var2);
         }

         super.onNeighborBlockChange(var1, var2, var3, var4);
      }

      @Override
      public IBlockState getStateFromMeta(int var1) {
         EnumFacing var5 = EnumFacing.getFront(var1);
         if (var5.getAxis() == EnumFacing.Axis.Y) {
            var5 = EnumFacing.NORTH;
         }

         return this.getDefaultState().withProperty(FACING, var5);
      }

      @Override
      public int getMetaFromState(IBlockState var1) {
         return var1.getValue(FACING).getIndex();
      }

      @Override
      public BlockState createBlockState() {
         return new BlockState(this, FACING);
      }
   }

   public static class BlockBannerStanding extends BlockBanner {
      private static final long l = 61343192411017L;

      public BlockBannerStanding() {
         this.setDefaultState(this.blockState.getBaseState().withProperty(ROTATION, 0));
      }

      @Override
      public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
         if (!var1.getBlockState(var2.down()).getBlock().getMaterial().isSolid()) {
            this.dropBlockAsItem(var1, var2, var3, 0);
            var1.setBlockToAir(var2);
         }

         super.onNeighborBlockChange(var1, var2, var3, var4);
      }

      @Override
      public IBlockState getStateFromMeta(int var1) {
         return this.getDefaultState().withProperty(ROTATION, var1);
      }

      @Override
      public int getMetaFromState(IBlockState var1) {
         return var1.getValue(ROTATION);
      }

      @Override
      public BlockState createBlockState() {
         return new BlockState(this, ROTATION);
      }
   }
}
