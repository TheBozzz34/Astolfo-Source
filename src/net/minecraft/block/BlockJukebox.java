package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer {
   public static final PropertyBool HAS_RECORD = PropertyBool.create("has_record");
   private static final long e = 3736146251528L;
   private static final long l = 9150181439034098669L;

   public BlockJukebox() {
      super(Material.wood, MapColor.dirtColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(HAS_RECORD, false));
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var3.getValue(HAS_RECORD)) {
         this.dropRecord(var1, var2, var3);
         var3 = var3.withProperty(HAS_RECORD, false);
         var1.setBlockState(var2, var3, 2);
         return true;
      } else {
         return false;
      }
   }

   public void insertRecord(World var1, BlockPos var2, IBlockState var3, ItemStack var4) {
      if (!var1.isRemote) {
         TileEntity var8 = var1.getTileEntity(var2);
         if (var8 instanceof BlockJukebox.TileEntityJukebox) {
            ((BlockJukebox.TileEntityJukebox)var8).setRecord(new ItemStack(var4.getItem(), 1, var4.getMetadata()));
            var1.setBlockState(var2, var3.withProperty(HAS_RECORD, true), 2);
         }
      }
   }

   private void dropRecord(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.isRemote) {
         TileEntity var7 = var1.getTileEntity(var2);
         if (var7 instanceof BlockJukebox.TileEntityJukebox) {
            BlockJukebox.TileEntityJukebox var8 = (BlockJukebox.TileEntityJukebox)var7;
            ItemStack var9 = var8.getRecord();
            if (var9 != null) {
               var1.playAuxSFX((int)l, var2, 0);
               var1.playRecord(var2, (String)null);
               var8.setRecord((ItemStack)null);
               float var10 = 0.7F;
               double var11 = var1.rand.nextFloat() * var10 + (1.0F - var10) * 0.5;
               double var13 = var1.rand.nextFloat() * var10 + (1.0F - var10) * 0.2 + 0.6;
               double var15 = var1.rand.nextFloat() * var10 + (1.0F - var10) * 0.5;
               ItemStack var17 = var9.copy();
               EntityItem var18 = new EntityItem(var1, var2.getX() + var11, var2.getY() + var13, var2.getZ() + var15, var17);
               var18.setDefaultPickupDelay();
               var1.spawnEntityInWorld(var18);
            }
         }
      }
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      this.dropRecord(var1, var2, var3);
      super.breakBlock(var1, var2, var3);
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.isRemote) {
         super.dropBlockAsItemWithChance(var1, var2, var3, var4, 0);
      }
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new BlockJukebox.TileEntityJukebox();
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World var1, BlockPos var2) {
      TileEntity var6 = var1.getTileEntity(var2);
      if (var6 instanceof BlockJukebox.TileEntityJukebox) {
         ItemStack var7 = ((BlockJukebox.TileEntityJukebox)var6).getRecord();
         if (var7 != null) {
            return Item.getIdFromItem(var7.getItem()) + 1 - Item.getIdFromItem(Items.record_13);
         }
      }

      return 0;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(HAS_RECORD, var1 > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(HAS_RECORD) ? 1 : 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, HAS_RECORD);
   }

   public static class TileEntityJukebox extends TileEntity {
      private ItemStack record;
      private static final long a = 26999528100994L;
      private static final long g = 3321001719814422538L;

      @Override
      public void readFromNBT(NBTTagCompound var1) {
         super.readFromNBT(var1);
         if (var1.hasKey("RecordItem", (int)g)) {
            this.setRecord(ItemStack.loadItemStackFromNBT(var1.getCompoundTag("RecordItem")));
         } else if (var1.getInteger("Record") > 0) {
            this.setRecord(new ItemStack(Item.getItemById(var1.getInteger("Record")), 1, 0));
         }
      }

      @Override
      public void writeToNBT(NBTTagCompound var1) {
         super.writeToNBT(var1);
         if (this.getRecord() != null) {
            var1.setTag("RecordItem", this.getRecord().writeToNBT(new NBTTagCompound()));
         }
      }

      public ItemStack getRecord() {
         return this.record;
      }

      public void setRecord(ItemStack var1) {
         this.record = var1;
         this.markDirty();
      }
   }
}
