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

public class BlockSandStone extends Block {
   public static final PropertyEnum<BlockSandStone.EnumType> TYPE = PropertyEnum.create("type", BlockSandStone.EnumType.class);
   private static final long e = 81260112360422L;

   public BlockSandStone() {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockSandStone.EnumType.DEFAULT));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(TYPE).getMetadata();
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (BlockSandStone.EnumType var10 : BlockSandStone.EnumType.values()) {
         var3.add(new ItemStack(var1, 1, var10.getMetadata()));
      }
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      return MapColor.sandColor;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(TYPE, BlockSandStone.EnumType.byMetadata(var1));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(TYPE).getMetadata();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, TYPE);
   }

   public enum EnumType implements IStringSerializable {
      DEFAULT(0, "sandstone", "default"),
      CHISELED(1, "chiseled_sandstone", "chiseled"),
      SMOOTH(2, "smooth_sandstone", "smooth");

      private static final BlockSandStone.EnumType[] META_LOOKUP = new BlockSandStone.EnumType[values().length];
      private final int metadata;
      private final String name;
      private final String unlocalizedName;
      private static final long a = 113962459613029L;

      EnumType(int var3, String var4, String var5) {
         this.metadata = var3;
         this.name = var4;
         this.unlocalizedName = var5;
      }

      public int getMetadata() {
         return this.metadata;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockSandStone.EnumType byMetadata(int var0) {
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
         for (BlockSandStone.EnumType var14 : values()) {
            META_LOOKUP[var14.getMetadata()] = var14;
         }
      }
   }
}
