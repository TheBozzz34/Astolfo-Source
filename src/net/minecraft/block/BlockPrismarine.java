package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;

public class BlockPrismarine extends Block {
   public static final PropertyEnum<BlockPrismarine.EnumType> VARIANT = PropertyEnum.create("variant", BlockPrismarine.EnumType.class);
   public static final int ROUGH_META = BlockPrismarine.EnumType.ROUGH.getMetadata();
   public static final int BRICKS_META = BlockPrismarine.EnumType.BRICKS.getMetadata();
   public static final int DARK_META = BlockPrismarine.EnumType.DARK.getMetadata();
   private static final long i = 88979086226148L;

   public BlockPrismarine() {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPrismarine.EnumType.ROUGH));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockPrismarine.EnumType.ROUGH.getUnlocalizedName() + ".name");
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      return var1.getValue(VARIANT) == BlockPrismarine.EnumType.ROUGH ? MapColor.cyanColor : MapColor.diamondColor;
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, VARIANT);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(VARIANT, BlockPrismarine.EnumType.byMetadata(var1));
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, ROUGH_META));
      var3.add(new ItemStack(var1, 1, BRICKS_META));
      var3.add(new ItemStack(var1, 1, DARK_META));
   }

   public enum EnumType implements IStringSerializable {
      ROUGH(0, "prismarine", "rough"),
      BRICKS(1, "prismarine_bricks", "bricks"),
      DARK(2, "dark_prismarine", "dark");

      private static final BlockPrismarine.EnumType[] META_LOOKUP = new BlockPrismarine.EnumType[values().length];
      private final int meta;
      private final String name;
      private final String unlocalizedName;
      private static final long b = 29528893950928L;

      EnumType(int var3, String var4, String var5) {
         this.meta = var3;
         this.name = var4;
         this.unlocalizedName = var5;
      }

      public int getMetadata() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockPrismarine.EnumType byMetadata(int var0) {
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
         return this.unlocalizedName;
      }

      static {
         for (BlockPrismarine.EnumType var14 : values()) {
            META_LOOKUP[var14.getMetadata()] = var14;
         }
      }
   }
}
