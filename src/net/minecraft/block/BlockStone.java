package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;

public class BlockStone extends Block {
   public static final PropertyEnum<BlockStone.EnumType> VARIANT = PropertyEnum.create("variant", BlockStone.EnumType.class);
   private static final long e = 101488317713423L;

   public BlockStone() {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockStone.EnumType.STONE));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockStone.EnumType.STONE.getUnlocalizedName() + ".name");
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      return var1.getValue(VARIANT).func_181072_c();
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return var1.getValue(VARIANT) == BlockStone.EnumType.STONE ? Item.getItemFromBlock(Blocks.cobblestone) : Item.getItemFromBlock(Blocks.stone);
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (BlockStone.EnumType var10 : BlockStone.EnumType.values()) {
         var3.add(new ItemStack(var1, 1, var10.getMetadata()));
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(VARIANT, BlockStone.EnumType.byMetadata(var1));
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
      STONE(0, MapColor.stoneColor, "stone"),
      GRANITE(1, MapColor.dirtColor, "granite"),
      GRANITE_SMOOTH(2, MapColor.dirtColor, "smooth_granite", "graniteSmooth"),
      DIORITE(3, MapColor.quartzColor, "diorite"),
      DIORITE_SMOOTH(4, MapColor.quartzColor, "smooth_diorite", "dioriteSmooth"),
      ANDESITE(5, MapColor.stoneColor, "andesite"),
      ANDESITE_SMOOTH(6, MapColor.stoneColor, "smooth_andesite", "andesiteSmooth");

      private static final BlockStone.EnumType[] META_LOOKUP = new BlockStone.EnumType[values().length];
      private final int meta;
      private final String name;
      private final String unlocalizedName;
      private final MapColor field_181073_l;
      private static final long a = 35295044297280L;

      EnumType(int var3, MapColor var4, String var5) {
         this(var3, var4, var5, var5);
      }

      EnumType(int var3, MapColor var4, String var5, String var6) {
         this.meta = var3;
         this.name = var5;
         this.unlocalizedName = var6;
         this.field_181073_l = var4;
      }

      public int getMetadata() {
         return this.meta;
      }

      public MapColor func_181072_c() {
         return this.field_181073_l;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockStone.EnumType byMetadata(int var0) {
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
         for (BlockStone.EnumType var25 : values()) {
            META_LOOKUP[var25.getMetadata()] = var25;
         }
      }
   }
}
