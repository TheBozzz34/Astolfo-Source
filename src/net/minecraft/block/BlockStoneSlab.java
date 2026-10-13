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
import net.minecraft.world.World;

public abstract class BlockStoneSlab extends BlockSlab {
   public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");
   public static final PropertyEnum<BlockStoneSlab.EnumType> VARIANT = PropertyEnum.create("variant", BlockStoneSlab.EnumType.class);
   private static final long i = 117444540907427L;

   public BlockStoneSlab() {
      super(Material.rock);
      IBlockState var4 = this.blockState.getBaseState();
      if (this.isDouble()) {
         var4 = var4.withProperty(SEAMLESS, false);
      } else {
         var4 = var4.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
      }

      this.setDefaultState(var4.withProperty(VARIANT, BlockStoneSlab.EnumType.STONE));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Item.getItemFromBlock(Blocks.stone_slab);
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Item.getItemFromBlock(Blocks.stone_slab);
   }

   @Override
   public String getUnlocalizedName(int var1) {
      return super.getUnlocalizedName() + "." + BlockStoneSlab.EnumType.byMetadata(var1).getUnlocalizedName();
   }

   @Override
   public IProperty<?> getVariantProperty() {
      return VARIANT;
   }

   @Override
   public Object getVariant(ItemStack var1) {
      return BlockStoneSlab.EnumType.byMetadata(var1.getMetadata() & 7);
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      if (var1 != Item.getItemFromBlock(Blocks.double_stone_slab)) {
         for (BlockStoneSlab.EnumType var10 : BlockStoneSlab.EnumType.values()) {
            if (var10 != BlockStoneSlab.EnumType.WOOD) {
               var3.add(new ItemStack(var1, 1, var10.getMetadata()));
            }
         }
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      IBlockState var5 = this.getDefaultState().withProperty(VARIANT, BlockStoneSlab.EnumType.byMetadata(var1 & 7));
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
   public int damageDropped(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      return var1.getValue(VARIANT).func_181074_c();
   }

   public enum EnumType implements IStringSerializable {
      STONE(0, MapColor.stoneColor, "stone"),
      SAND(1, MapColor.sandColor, "sandstone", "sand"),
      WOOD(2, MapColor.woodColor, "wood_old", "wood"),
      COBBLESTONE(3, MapColor.stoneColor, "cobblestone", "cobble"),
      BRICK(4, MapColor.redColor, "brick"),
      SMOOTHBRICK(5, MapColor.stoneColor, "stone_brick", "smoothStoneBrick"),
      NETHERBRICK(6, MapColor.netherrackColor, "nether_brick", "netherBrick"),
      QUARTZ(7, MapColor.quartzColor, "quartz");

      private static final BlockStoneSlab.EnumType[] META_LOOKUP = new BlockStoneSlab.EnumType[values().length];
      private final int meta;
      private final MapColor field_181075_k;
      private final String name;
      private final String unlocalizedName;
      private static final long a = 7670391174655L;

      EnumType(int var3, MapColor var4, String var5) {
         this(var3, var4, var5, var5);
      }

      EnumType(int var3, MapColor var4, String var5, String var6) {
         this.meta = var3;
         this.field_181075_k = var4;
         this.name = var5;
         this.unlocalizedName = var6;
      }

      public int getMetadata() {
         return this.meta;
      }

      public MapColor func_181074_c() {
         return this.field_181075_k;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockStoneSlab.EnumType byMetadata(int var0) {
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
         for (BlockStoneSlab.EnumType var25 : values()) {
            META_LOOKUP[var25.getMetadata()] = var25;
         }
      }
   }
}
