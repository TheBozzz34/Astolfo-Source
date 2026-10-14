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
      IBlockState iBlockState = this.blockState.getBaseState();
      if (this.isDouble()) {
         iBlockState = iBlockState.withProperty(SEAMLESS, false);
      } else {
         iBlockState = iBlockState.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
      }

      this.setDefaultState(iBlockState.withProperty(VARIANT, BlockStoneSlab.EnumType.STONE));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.stone_slab);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(Blocks.stone_slab);
   }

   @Override
   public String getUnlocalizedName(int meta) {
      return super.getUnlocalizedName() + "." + BlockStoneSlab.EnumType.byMetadata(meta).getUnlocalizedName();
   }

   @Override
   public IProperty<?> getVariantProperty() {
      return VARIANT;
   }

   @Override
   public Object getVariant(ItemStack stack) {
      return BlockStoneSlab.EnumType.byMetadata(stack.getMetadata() & 7);
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      if (itemIn != Item.getItemFromBlock(Blocks.double_stone_slab)) {
         for (BlockStoneSlab.EnumType enumType : BlockStoneSlab.EnumType.values()) {
            if (enumType != BlockStoneSlab.EnumType.WOOD) {
               list.add(new ItemStack(itemIn, 1, enumType.getMetadata()));
            }
         }
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState iBlockState = this.getDefaultState().withProperty(VARIANT, BlockStoneSlab.EnumType.byMetadata(meta & 7));
      if (this.isDouble()) {
         iBlockState = iBlockState.withProperty(SEAMLESS, (meta & 8) != 0);
      } else {
         iBlockState = iBlockState.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
      }

      return iBlockState;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i |= state.getValue(VARIANT).getMetadata();
      if (this.isDouble()) {
         if (state.getValue(SEAMLESS)) {
            i |= 8;
         }
      } else if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockState createBlockState() {
      return this.isDouble() ? new BlockState(this, SEAMLESS, VARIANT) : new BlockState(this, HALF, VARIANT);
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(VARIANT).getMetadata();
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return state.getValue(VARIANT).func_181074_c();
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

      EnumType(int k, MapColor mapColor, String string2) {
         this(k, mapColor, string2, string2);
      }

      EnumType(int k, MapColor mapColor, String string2, String string3) {
         this.meta = k;
         this.field_181075_k = mapColor;
         this.name = string2;
         this.unlocalizedName = string3;
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

      public static BlockStoneSlab.EnumType byMetadata(int meta) {
         if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         return META_LOOKUP[meta];
      }

      @Override
      public String getName() {
         return this.name;
      }

      public String getUnlocalizedName() {
         return this.unlocalizedName;
      }

      static {
         for (BlockStoneSlab.EnumType enumType : values()) {
            META_LOOKUP[enumType.getMetadata()] = enumType;
         }
      }
   }
}
