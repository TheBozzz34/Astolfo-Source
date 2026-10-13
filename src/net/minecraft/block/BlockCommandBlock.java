package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer {
   public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");
   private static final long e = 75590778114536L;

   public BlockCommandBlock() {
      super(Material.iron, MapColor.adobeColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TRIGGERED, false));
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityCommandBlock();
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.isRemote) {
         boolean var8 = var1.isBlockPowered(var2);
         boolean var9 = var3.getValue(TRIGGERED);
         if (var8 && !var9) {
            var1.setBlockState(var2, var3.withProperty(TRIGGERED, true), 4);
            var1.scheduleUpdate(var2, this, this.tickRate(var1));
         } else if (!var8 && var9) {
            var1.setBlockState(var2, var3.withProperty(TRIGGERED, false), 4);
         }
      }
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      TileEntity var8 = var1.getTileEntity(var2);
      if (var8 instanceof TileEntityCommandBlock) {
         ((TileEntityCommandBlock)var8).getCommandBlockLogic().trigger(var1);
         var1.updateComparatorOutputLevel(var2, this);
      }
   }

   @Override
   public int tickRate(World var1) {
      return 1;
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      TileEntity var12 = var1.getTileEntity(var2);
      return var12 instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)var12).getCommandBlockLogic().tryOpenEditCommandBlock(var4) : false;
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World var1, BlockPos var2) {
      TileEntity var6 = var1.getTileEntity(var2);
      return var6 instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)var6).getCommandBlockLogic().getSuccessCount() : 0;
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      TileEntity var9 = var1.getTileEntity(var2);
      if (var9 instanceof TileEntityCommandBlock) {
         CommandBlockLogic var10 = ((TileEntityCommandBlock)var9).getCommandBlockLogic();
         if (var5.hasDisplayName()) {
            var10.setName(var5.getDisplayName());
         }

         if (!var1.isRemote) {
            var10.setTrackOutput(var1.getGameRules().getBoolean("sendCommandFeedback"));
         }
      }
   }

   @Override
   public int quantityDropped(Random var1) {
      return 0;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(TRIGGERED, (var1 & 1) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      byte var5 = 0;
      if (var1.getValue(TRIGGERED)) {
         var5 |= 1;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, TRIGGERED);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(TRIGGERED, false);
   }
}
