package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemLead;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFence extends Block {
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   private static final long e = 108758468717065L;

   public BlockFence(Material var1) {
      this(var1, var1.getMaterialMapColor());
   }

   public BlockFence(Material var1, MapColor var2) {
      super(var1, var2);
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false)
      );
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      boolean var10 = this.canConnectTo(var1, var2.north());
      boolean var11 = this.canConnectTo(var1, var2.south());
      boolean var12 = this.canConnectTo(var1, var2.west());
      boolean var13 = this.canConnectTo(var1, var2.east());
      float var14 = 0.375F;
      float var15 = 0.625F;
      float var16 = 0.375F;
      float var17 = 0.625F;
      if (var10) {
         var16 = 0.0F;
      }

      if (var11) {
         var17 = 1.0F;
      }

      if (var10 || var11) {
         this.setBlockBounds(var14, 0.0F, var16, var15, 1.5F, var17);
         super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      }

      var16 = 0.375F;
      var17 = 0.625F;
      if (var12) {
         var14 = 0.0F;
      }

      if (var13) {
         var15 = 1.0F;
      }

      if (var12 || var13 || !var10 && !var11) {
         this.setBlockBounds(var14, 0.0F, var16, var15, 1.5F, var17);
         super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      }

      if (var10) {
         var16 = 0.0F;
      }

      if (var11) {
         var17 = 1.0F;
      }

      this.setBlockBounds(var14, 0.0F, var16, var15, 1.0F, var17);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      boolean var6 = this.canConnectTo(var1, var2.north());
      boolean var7 = this.canConnectTo(var1, var2.south());
      boolean var8 = this.canConnectTo(var1, var2.west());
      boolean var9 = this.canConnectTo(var1, var2.east());
      float var10 = 0.375F;
      float var11 = 0.625F;
      float var12 = 0.375F;
      float var13 = 0.625F;
      if (var6) {
         var12 = 0.0F;
      }

      if (var7) {
         var13 = 1.0F;
      }

      if (var8) {
         var10 = 0.0F;
      }

      if (var9) {
         var11 = 1.0F;
      }

      this.setBlockBounds(var10, 0.0F, var12, var11, 1.0F, var13);
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
   public boolean isPassable(IBlockAccess var1, BlockPos var2) {
      return false;
   }

   public boolean canConnectTo(IBlockAccess var1, BlockPos var2) {
      Block var6 = var1.getBlockState(var2).getBlock();
      return var6 == Blocks.barrier
         ? false
         : (
            (!(var6 instanceof BlockFence) || var6.blockMaterial != this.blockMaterial) && !(var6 instanceof BlockFenceGate)
               ? (var6.blockMaterial.isOpaque() && var6.isFullCube() ? var6.blockMaterial != Material.gourd : false)
               : true
         );
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      return var1.isRemote ? true : ItemLead.attachToFence(var4, var1, var2);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return 0;
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.withProperty(NORTH, this.canConnectTo(var2, var3.north()))
         .withProperty(EAST, this.canConnectTo(var2, var3.east()))
         .withProperty(SOUTH, this.canConnectTo(var2, var3.south()))
         .withProperty(WEST, this.canConnectTo(var2, var3.west()));
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, NORTH, EAST, WEST, SOUTH);
   }
}
