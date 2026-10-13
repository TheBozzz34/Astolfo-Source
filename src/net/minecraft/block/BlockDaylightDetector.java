package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer {
   public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
   private final boolean inverted;
   private static final long e = 93111996815840L;

   public BlockDaylightDetector(boolean var1) {
      super(Material.wood);
      this.inverted = var1;
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0));
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setHardness(0.2F);
      this.setStepSound(soundTypeWood);
      this.setUnlocalizedName("daylightDetector");
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
   }

   @Override
   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return var3.getValue(POWER);
   }

   public void updatePower(World var1, BlockPos var2) {
      if (!var1.provider.getHasNoSky()) {
         IBlockState var6 = var1.getBlockState(var2);
         int var7 = var1.getLightFor(EnumSkyBlock.SKY, var2) - var1.getSkylightSubtracted();
         float var8 = var1.getCelestialAngleRadians(1.0F);
         float var9 = var8 < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
         var8 += (var9 - var8) * 0.2F;
         var7 = Math.round(var7 * MathHelper.cos(var8));
         var7 = MathHelper.clamp_int(var7, 0, 15);
         if (this.inverted) {
            var7 = 15 - var7;
         }

         if (var6.getValue(POWER) != var7) {
            var1.setBlockState(var2, var6.withProperty(POWER, var7), 3);
         }
      }
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var4.isAllowEdit()) {
         if (var1.isRemote) {
            return true;
         }

         if (this.inverted) {
            var1.setBlockState(var2, Blocks.daylight_detector.getDefaultState().withProperty(POWER, var3.getValue(POWER)), 4);
            Blocks.daylight_detector.updatePower(var1, var2);
         } else {
            var1.setBlockState(var2, Blocks.daylight_detector_inverted.getDefaultState().withProperty(POWER, var3.getValue(POWER)), 4);
            Blocks.daylight_detector_inverted.updatePower(var1, var2);
         }

         return true;
      } else {
         return super.onBlockActivated(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Item.getItemFromBlock(Blocks.daylight_detector);
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Item.getItemFromBlock(Blocks.daylight_detector);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityDaylightDetector();
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(POWER, var1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(POWER);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, POWER);
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      if (!this.inverted) {
         super.getSubBlocks(var1, var2, var3);
      }
   }
}
