package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockStoneBrick extends Block {
   public static final PropertyEnum<BlockStoneBrick.EnumType> VARIANT = PropertyEnum.create("variant", BlockStoneBrick.EnumType.class);
   public static final int DEFAULT_META = BlockStoneBrick.EnumType.DEFAULT.getMetadata();
   public static final int MOSSY_META = BlockStoneBrick.EnumType.MOSSY.getMetadata();
   public static final int CRACKED_META = BlockStoneBrick.EnumType.CRACKED.getMetadata();
   public static final int CHISELED_META = BlockStoneBrick.EnumType.CHISELED.getMetadata();
   private static final long i = 14451029844325L;

   public BlockStoneBrick() {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockStoneBrick.EnumType.DEFAULT));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (BlockStoneBrick.EnumType var10 : BlockStoneBrick.EnumType.values()) {
         var3.add(new ItemStack(var1, 1, var10.getMetadata()));
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(VARIANT, BlockStoneBrick.EnumType.byMetadata(var1));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, VARIANT);
   }

   public enum EnumType implements IStringSerializable {
      DEFAULT(0, "stonebrick", "default"),
      MOSSY(1, "mossy_stonebrick", "mossy"),
      CRACKED(2, "cracked_stonebrick", "cracked"),
      CHISELED(3, "chiseled_stonebrick", "chiseled");

      private static final BlockStoneBrick.EnumType[] META_LOOKUP = new BlockStoneBrick.EnumType[values().length];
      private final int meta;
      private final String name;
      private final String unlocalizedName;
      private static final long a = 97475102719642L;

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

      public static BlockStoneBrick.EnumType byMetadata(int var0) {
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
         for (BlockStoneBrick.EnumType var14 : values()) {
            META_LOOKUP[var14.getMetadata()] = var14;
         }
      }
   }
}
