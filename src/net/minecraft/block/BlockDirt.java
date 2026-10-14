package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDirt extends Block {
   public static final PropertyEnum<BlockDirt.DirtType> VARIANT = PropertyEnum.create("variant", BlockDirt.DirtType.class);
   public static final PropertyBool SNOWY = PropertyBool.create("snowy");

   public BlockDirt() {
      super(Material.ground);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockDirt.DirtType.DIRT).withProperty(SNOWY, false));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return state.getValue(VARIANT).func_181066_d();
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (state.getValue(VARIANT) == BlockDirt.DirtType.PODZOL) {
         Block block = worldIn.getBlockState(pos.up()).getBlock();
         state = state.withProperty(SNOWY, block == Blocks.snow || block == Blocks.snow_layer);
      }

      return state;
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      list.add(new ItemStack(this, 1, BlockDirt.DirtType.DIRT.getMetadata()));
      list.add(new ItemStack(this, 1, BlockDirt.DirtType.COARSE_DIRT.getMetadata()));
      list.add(new ItemStack(this, 1, BlockDirt.DirtType.PODZOL.getMetadata()));
   }

   @Override
   public int getDamageValue(World worldIn, BlockPos pos) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      return iBlockState.getBlock() != this ? 0 : iBlockState.getValue(VARIANT).getMetadata();
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockDirt.DirtType.byMetadata(meta));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(VARIANT).getMetadata();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, VARIANT, SNOWY);
   }

   @Override
   public int damageDropped(IBlockState state) {
      BlockDirt.DirtType dirtType = state.getValue(VARIANT);
      if (dirtType == BlockDirt.DirtType.PODZOL) {
         dirtType = BlockDirt.DirtType.DIRT;
      }

      return dirtType.getMetadata();
   }

   public enum DirtType implements IStringSerializable {
      DIRT(0, "dirt", "default", MapColor.dirtColor),
      COARSE_DIRT(1, "coarse_dirt", "coarse", MapColor.dirtColor),
      PODZOL(2, "podzol", MapColor.obsidianColor);

      private static final BlockDirt.DirtType[] METADATA_LOOKUP = new BlockDirt.DirtType[values().length];
      private final int metadata;
      private final String name;
      private final String unlocalizedName;
      private final MapColor field_181067_h;

      DirtType(int j, String string2, MapColor mapColor) {
         this(j, string2, string2, mapColor);
      }

      DirtType(int j, String string2, String string3, MapColor mapColor) {
         this.metadata = j;
         this.name = string2;
         this.unlocalizedName = string3;
         this.field_181067_h = mapColor;
      }

      public int getMetadata() {
         return this.metadata;
      }

      public String getUnlocalizedName() {
         return this.unlocalizedName;
      }

      public MapColor func_181066_d() {
         return this.field_181067_h;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockDirt.DirtType byMetadata(int metadata) {
         if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
            metadata = 0;
         }

         return METADATA_LOOKUP[metadata];
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (BlockDirt.DirtType dirtType : values()) {
            METADATA_LOOKUP[dirtType.getMetadata()] = dirtType;
         }
      }
   }
}
