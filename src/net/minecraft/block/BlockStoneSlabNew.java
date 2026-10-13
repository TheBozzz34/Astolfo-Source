package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public abstract class BlockStoneSlabNew extends BlockSlab {
   public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");
   public static final PropertyEnum<BlockStoneSlabNew.EnumType> VARIANT = PropertyEnum.create("variant", BlockStoneSlabNew.EnumType.class);
   private static final long i = 121352704930711L;

   public BlockStoneSlabNew() {
      super(Material.rock);
      IBlockState var4 = this.blockState.getBaseState();
      if (this.isDouble()) {
         var4 = var4.withProperty(SEAMLESS, false);
      } else {
         var4 = var4.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
      }

      this.setDefaultState(var4.withProperty(VARIANT, BlockStoneSlabNew.EnumType.RED_SANDSTONE));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + ".red_sandstone.name");
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Item.getItemFromBlock(Blocks.stone_slab2);
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Item.getItemFromBlock(Blocks.stone_slab2);
   }

   @Override
   public String getUnlocalizedName(int var1) {
      return super.getUnlocalizedName() + "." + BlockStoneSlabNew.EnumType.byMetadata(var1).getUnlocalizedName();
   }

   @Override
   public IProperty<?> getVariantProperty() {
      return VARIANT;
   }

   @Override
   public Object getVariant(ItemStack var1) {
      return BlockStoneSlabNew.EnumType.byMetadata(var1.getMetadata() & 7);
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      if (var1 != Item.getItemFromBlock(Blocks.double_stone_slab2)) {
         for (BlockStoneSlabNew.EnumType var10 : BlockStoneSlabNew.EnumType.values()) {
            var3.add(new ItemStack(var1, 1, var10.getMetadata()));
         }
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      IBlockState var5 = this.getDefaultState().withProperty(VARIANT, BlockStoneSlabNew.EnumType.byMetadata(var1 & 7));
      if (this.isDouble()) {
         var5 = var5.withProperty(SEAMLESS, (var1 & 8) != 0);
      } else {
         var5 = var5.withProperty(HALF, (var1 & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
      }

      return var5;
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(VARIANT).getMetadata();
      if (this.isDouble()) {
         if (var1.getValue(SEAMLESS)) {
            var5 |= 8;
         }
      } else if (var1.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return this.isDouble() ? new BlockState(this, SEAMLESS, VARIANT) : new BlockState(this, HALF, VARIANT);
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      return var1.getValue(VARIANT).func_181068_c();
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   public enum EnumType implements IStringSerializable {
      RED_SANDSTONE(0, "red_sandstone", BlockSand.EnumType.RED_SAND.getMapColor());

      private static final BlockStoneSlabNew.EnumType[] META_LOOKUP = new BlockStoneSlabNew.EnumType[values().length];
      private final int meta;
      private final String name;
      private final MapColor field_181069_e;
      private static final long a = 102381625708489L;

      EnumType(int var3, String var4, MapColor var5) {
         this.meta = var3;
         this.name = var4;
         this.field_181069_e = var5;
      }

      public int getMetadata() {
         return this.meta;
      }

      public MapColor func_181068_c() {
         return this.field_181069_e;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockStoneSlabNew.EnumType byMetadata(int var0) {
         if (var0 < 0 || var0 >= META_LOOKUP.length) {
            var0 = 0;
         }

         return META_LOOKUP[var0];
      }

      @Override
      public String getName() {
         return this.name;
      }

      public String getUnlocalizedName() {
         return this.name;
      }

      static {
         for (BlockStoneSlabNew.EnumType var14 : values()) {
            META_LOOKUP[var14.getMetadata()] = var14;
         }
      }
   }
}
