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
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (state.getValue(HAS_RECORD)) {
         this.dropRecord(worldIn, pos, state);
         state = state.withProperty(HAS_RECORD, false);
         worldIn.setBlockState(pos, state, 2);
         return true;
      } else {
         return false;
      }
   }

   public void insertRecord(World worldIn, BlockPos pos, IBlockState state, ItemStack recordStack) {
      if (!worldIn.isRemote) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof BlockJukebox.TileEntityJukebox) {
            ((BlockJukebox.TileEntityJukebox)tileEntity).setRecord(new ItemStack(recordStack.getItem(), 1, recordStack.getMetadata()));
            worldIn.setBlockState(pos, state.withProperty(HAS_RECORD, true), 2);
         }
      }
   }

   private void dropRecord(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof BlockJukebox.TileEntityJukebox) {
            BlockJukebox.TileEntityJukebox tileEntityJukebox = (BlockJukebox.TileEntityJukebox)tileEntity;
            ItemStack itemStack = tileEntityJukebox.getRecord();
            if (itemStack != null) {
               worldIn.playAuxSFX((int)l, pos, 0);
               worldIn.playRecord(pos, (String)null);
               tileEntityJukebox.setRecord((ItemStack)null);
               float f = 0.7F;
               double d = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5;
               double e = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.2 + 0.6;
               double d2 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5;
               ItemStack itemStack2 = itemStack.copy();
               EntityItem entityItem = new EntityItem(worldIn, pos.getX() + d, pos.getY() + e, pos.getZ() + d2, itemStack2);
               entityItem.setDefaultPickupDelay();
               worldIn.spawnEntityInWorld(entityItem);
            }
         }
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      this.dropRecord(worldIn, pos, state);
      super.breakBlock(worldIn, pos, state);
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!worldIn.isRemote) {
         super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
      }
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new BlockJukebox.TileEntityJukebox();
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof BlockJukebox.TileEntityJukebox) {
         ItemStack itemStack = ((BlockJukebox.TileEntityJukebox)tileEntity).getRecord();
         if (itemStack != null) {
            return Item.getIdFromItem(itemStack.getItem()) + 1 - Item.getIdFromItem(Items.record_13);
         }
      }

      return 0;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(HAS_RECORD, meta > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(HAS_RECORD) ? 1 : 0;
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
      public void readFromNBT(NBTTagCompound compound) {
         super.readFromNBT(compound);
         if (compound.hasKey("RecordItem", (int)g)) {
            this.setRecord(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("RecordItem")));
         } else if (compound.getInteger("Record") > 0) {
            this.setRecord(new ItemStack(Item.getItemById(compound.getInteger("Record")), 1, 0));
         }
      }

      @Override
      public void writeToNBT(NBTTagCompound compound) {
         super.writeToNBT(compound);
         if (this.getRecord() != null) {
            compound.setTag("RecordItem", this.getRecord().writeToNBT(new NBTTagCompound()));
         }
      }

      public ItemStack getRecord() {
         return this.record;
      }

      public void setRecord(ItemStack recordStack) {
         this.record = recordStack;
         this.markDirty();
      }
   }
}
