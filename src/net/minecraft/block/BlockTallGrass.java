package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTallGrass extends BlockBush implements IGrowable {
   public static final PropertyEnum<BlockTallGrass.EnumType> TYPE = PropertyEnum.create("type", BlockTallGrass.EnumType.class);
   private static final long e = 26672734817669L;

   public BlockTallGrass() {
      super(Material.vine);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockTallGrass.EnumType.DEAD_BUSH));
      float var1 = 0.4F;
      this.setBlockBounds(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.8F, 0.5F + var1);
   }

   @Override
   public int getBlockColor() {
      return ColorizerGrass.getGrassColor(0.5, 1.0);
   }

   @Override
   public boolean canBlockStay(World var1, BlockPos var2, IBlockState var3) {
      return this.canPlaceBlockOn(var1.getBlockState(var2.down()).getBlock());
   }

   @Override
   public boolean isReplaceable(World var1, BlockPos var2) {
      return true;
   }

   @Override
   public int getRenderColor(IBlockState var1) {
      if (var1.getBlock() != this) {
         return super.getRenderColor(var1);
      }

      BlockTallGrass.EnumType var5 = var1.getValue(TYPE);
      return var5 == BlockTallGrass.EnumType.DEAD_BUSH ? 16777215 : ColorizerGrass.getGrassColor(0.5, 1.0);
   }

   @Override
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      return var1.getBiomeGenForCoords(var2).getGrassColorAtPos(var2);
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return var2.nextInt(8) == 0 ? Items.wheat_seeds : null;
   }

   @Override
   public int quantityDroppedWithBonus(int var1, Random var2) {
      return 1 + var2.nextInt(var1 * 2 + 1);
   }

   @Override
   public void harvestBlock(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if (!var1.isRemote && var2.getCurrentEquippedItem() != null && var2.getCurrentEquippedItem().getItem() == Items.shears) {
         var2.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
         spawnAsEntity(var1, var3, new ItemStack(Blocks.tallgrass, 1, var4.getValue(TYPE).getMeta()));
      } else {
         super.harvestBlock(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public int getDamageValue(World var1, BlockPos var2) {
      IBlockState var3 = var1.getBlockState(var2);
      return var3.getBlock().getMetaFromState(var3);
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (int var7 = 1; var7 < 3; var7++) {
         var3.add(new ItemStack(var1, 1, var7));
      }
   }

   @Override
   public boolean canGrow(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return var3.getValue(TYPE) != BlockTallGrass.EnumType.DEAD_BUSH;
   }

   @Override
   public boolean canUseBonemeal(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   @Override
   public void grow(World var1, Random var2, BlockPos var3, IBlockState var4) {
      BlockDoublePlant.EnumPlantType var8 = BlockDoublePlant.EnumPlantType.GRASS;
      if (var4.getValue(TYPE) == BlockTallGrass.EnumType.FERN) {
         var8 = BlockDoublePlant.EnumPlantType.FERN;
      }

      if (Blocks.double_plant.canPlaceBlockAt(var1, var3)) {
         Blocks.double_plant.placeAt(var1, var3, var8, 2);
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(TYPE, BlockTallGrass.EnumType.byMetadata(var1));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(TYPE).getMeta();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, TYPE);
   }

   @Override
   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.XYZ;
   }

   public enum EnumType implements IStringSerializable {
      DEAD_BUSH(0, "dead_bush"),
      GRASS(1, "tall_grass"),
      FERN(2, "fern");

      private static final BlockTallGrass.EnumType[] META_LOOKUP = new BlockTallGrass.EnumType[values().length];
      private final int meta;
      private final String name;
      private static final long a = 128318602684703L;

      EnumType(int var3, String var4) {
         this.meta = var3;
         this.name = var4;
      }

      public int getMeta() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockTallGrass.EnumType byMetadata(int var0) {
         if (var0 < 0 || var0 >= META_LOOKUP.length) {
            var0 = 0;
         }

         return META_LOOKUP[var0];
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (BlockTallGrass.EnumType var14 : values()) {
            META_LOOKUP[var14.getMeta()] = var14;
         }
      }
   }
}
