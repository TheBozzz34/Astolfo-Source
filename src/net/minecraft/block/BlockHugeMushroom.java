package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockHugeMushroom extends Block {
   public static final PropertyEnum<BlockHugeMushroom.EnumType> VARIANT = PropertyEnum.create("variant", BlockHugeMushroom.EnumType.class);
   private final Block smallBlock;
   private static final long e = 139798230686258L;

   public BlockHugeMushroom(Material var1, MapColor var2, Block var3) {
      super(var1, var2);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockHugeMushroom.EnumType.ALL_OUTSIDE));
      this.smallBlock = var3;
   }

   @Override
   public int quantityDropped(Random var1) {
      return Math.max(0, var1.nextInt(10) - 7);
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      switch ((BlockHugeMushroom.EnumType)var1.getValue(VARIANT)) {
         case ALL_STEM:
            return MapColor.clothColor;
         case ALL_INSIDE:
            return MapColor.sandColor;
         case STEM:
            return MapColor.sandColor;
         default:
            return super.getMapColor(var1);
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Item.getItemFromBlock(this.smallBlock);
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Item.getItemFromBlock(this.smallBlock);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState();
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(VARIANT, BlockHugeMushroom.EnumType.byMetadata(var1));
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
      NORTH_WEST(1, "north_west"),
      NORTH(2, "north"),
      NORTH_EAST(3, "north_east"),
      WEST(4, "west"),
      CENTER(5, "center"),
      EAST(6, "east"),
      SOUTH_WEST(7, "south_west"),
      SOUTH(8, "south"),
      SOUTH_EAST(9, "south_east"),
      STEM(10, "stem"),
      ALL_INSIDE(0, "all_inside"),
      ALL_OUTSIDE(14, "all_outside"),
      ALL_STEM(15, "all_stem");

      private static final BlockHugeMushroom.EnumType[] META_LOOKUP = new BlockHugeMushroom.EnumType[16];
      private final int meta;
      private final String name;
      private static final long b = 77256946900792L;

      EnumType(int var3, String var4) {
         this.meta = var3;
         this.name = var4;
      }

      public int getMetadata() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockHugeMushroom.EnumType byMetadata(int var0) {
         if (var0 < 0 || var0 >= META_LOOKUP.length) {
            var0 = 0;
         }

         BlockHugeMushroom.EnumType var4 = META_LOOKUP[var0];
         return var4 == null ? META_LOOKUP[0] : var4;
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (BlockHugeMushroom.EnumType var25 : values()) {
            META_LOOKUP[var25.getMetadata()] = var25;
         }
      }
   }
}
