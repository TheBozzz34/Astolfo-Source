package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockSand extends BlockFalling {
   public static final PropertyEnum<BlockSand.EnumType> VARIANT = PropertyEnum.create("variant", BlockSand.EnumType.class);
   private static final long e = 104844071973062L;

   public BlockSand() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockSand.EnumType.SAND));
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (BlockSand.EnumType var10 : BlockSand.EnumType.values()) {
         var3.add(new ItemStack(var1, 1, var10.getMetadata()));
      }
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      return var1.getValue(VARIANT).getMapColor();
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(VARIANT, BlockSand.EnumType.byMetadata(var1));
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
      SAND(0, "sand", "default", MapColor.sandColor),
      RED_SAND(1, "red_sand", "red", MapColor.adobeColor);

      private static final BlockSand.EnumType[] META_LOOKUP = new BlockSand.EnumType[values().length];
      private final int meta;
      private final String name;
      private final MapColor mapColor;
      private final String unlocalizedName;
      private static final long a = 119763049334423L;

      EnumType(int var3, String var4, String var5, MapColor var6) {
         this.meta = var3;
         this.name = var4;
         this.mapColor = var6;
         this.unlocalizedName = var5;
      }

      public int getMetadata() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public MapColor getMapColor() {
         return this.mapColor;
      }

      public static BlockSand.EnumType byMetadata(int var0) {
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
         for (BlockSand.EnumType var14 : values()) {
            META_LOOKUP[var14.getMetadata()] = var14;
         }
      }
   }
}
