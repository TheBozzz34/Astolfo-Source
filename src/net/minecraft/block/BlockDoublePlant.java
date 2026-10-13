package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public class BlockDoublePlant extends BlockBush implements IGrowable {
   public static final PropertyEnum<BlockDoublePlant.EnumPlantType> VARIANT = PropertyEnum.create("variant", BlockDoublePlant.EnumPlantType.class);
   public static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = PropertyEnum.create("half", BlockDoublePlant.EnumBlockHalf.class);
   public static final PropertyEnum<EnumFacing> FACING = BlockDirectional.FACING;
   private static final long i = 48058388777940L;

   public BlockDoublePlant() {
      super(Material.vine);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(VARIANT, BlockDoublePlant.EnumPlantType.SUNFLOWER)
            .withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER)
            .withProperty(FACING, EnumFacing.NORTH)
      );
      this.setHardness(0.0F);
      this.setStepSound(soundTypeGrass);
      this.setUnlocalizedName("doublePlant");
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public BlockDoublePlant.EnumPlantType getVariant(IBlockAccess var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      if (var6.getBlock() == this) {
         var6 = this.getActualState(var6, var1, var2);
         return var6.getValue(VARIANT);
      } else {
         return BlockDoublePlant.EnumPlantType.FERN;
      }
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return super.canPlaceBlockAt(var1, var2) && var1.isAirBlock(var2.up());
   }

   @Override
   public boolean isReplaceable(World var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      if (var6.getBlock() != this) {
         return true;
      }

      BlockDoublePlant.EnumPlantType var7 = this.getActualState(var6, var1, var2).getValue(VARIANT);
      return var7 == BlockDoublePlant.EnumPlantType.FERN || var7 == BlockDoublePlant.EnumPlantType.GRASS;
   }

   @Override
   public void checkAndDropBlock(World var1, BlockPos var2, IBlockState var3) {
      if (!this.canBlockStay(var1, var2, var3)) {
         boolean var7 = var3.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER;
         BlockPos var8 = var7 ? var2 : var2.up();
         BlockPos var9 = var7 ? var2.down() : var2;
         Block var10 = var7 ? this : var1.getBlockState(var8).getBlock();
         Block var11 = var7 ? var1.getBlockState(var9).getBlock() : this;
         if (var10 == this) {
            var1.setBlockState(var8, Blocks.air.getDefaultState(), 2);
         }

         if (var11 == this) {
            var1.setBlockState(var9, Blocks.air.getDefaultState(), 3);
            if (!var7) {
               this.dropBlockAsItem(var1, var9, var3, 0);
            }
         }
      }
   }

   @Override
   public boolean canBlockStay(World var1, BlockPos var2, IBlockState var3) {
      if (var3.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         return var1.getBlockState(var2.down()).getBlock() == this;
      }

      IBlockState var7 = var1.getBlockState(var2.up());
      return var7.getBlock() == this && super.canBlockStay(var1, var2, var7);
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      if (var1.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         return null;
      }

      BlockDoublePlant.EnumPlantType var7 = var1.getValue(VARIANT);
      return var7 == BlockDoublePlant.EnumPlantType.FERN
         ? null
         : (var7 == BlockDoublePlant.EnumPlantType.GRASS ? (var2.nextInt(8) == 0 ? Items.wheat_seeds : null) : Item.getItemFromBlock(this));
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(HALF) != BlockDoublePlant.EnumBlockHalf.UPPER && var1.getValue(VARIANT) != BlockDoublePlant.EnumPlantType.GRASS
         ? var1.getValue(VARIANT).getMeta()
         : 0;
   }

   @Override
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      BlockDoublePlant.EnumPlantType var7 = this.getVariant(var1, var2);
      return var7 != BlockDoublePlant.EnumPlantType.GRASS && var7 != BlockDoublePlant.EnumPlantType.FERN
         ? 16777215
         : BiomeColorHelper.getGrassColorAtPos(var1, var2);
   }

   public void placeAt(World var1, BlockPos var2, BlockDoublePlant.EnumPlantType var3, int var4) {
      var1.setBlockState(var2, this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER).withProperty(VARIANT, var3), var4);
      var1.setBlockState(var2.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), var4);
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.setBlockState(var2.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
   }

   @Override
   public void harvestBlock(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if (var1.isRemote
         || var2.getCurrentEquippedItem() == null
         || var2.getCurrentEquippedItem().getItem() != Items.shears
         || var4.getValue(HALF) != BlockDoublePlant.EnumBlockHalf.LOWER
         || !this.onHarvest(var1, var3, var4, var2)) {
         super.harvestBlock(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void onBlockHarvested(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (var3.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         if (var1.getBlockState(var2.down()).getBlock() == this) {
            if (!var4.capabilities.isCreativeMode) {
               IBlockState var8 = var1.getBlockState(var2.down());
               BlockDoublePlant.EnumPlantType var9 = var8.getValue(VARIANT);
               if (var9 != BlockDoublePlant.EnumPlantType.FERN && var9 != BlockDoublePlant.EnumPlantType.GRASS) {
                  var1.destroyBlock(var2.down(), true);
               } else if (!var1.isRemote) {
                  if (var4.getCurrentEquippedItem() != null && var4.getCurrentEquippedItem().getItem() == Items.shears) {
                     this.onHarvest(var1, var2, var8, var4);
                     var1.setBlockToAir(var2.down());
                  } else {
                     var1.destroyBlock(var2.down(), true);
                  }
               } else {
                  var1.setBlockToAir(var2.down());
               }
            } else {
               var1.setBlockToAir(var2.down());
            }
         }
      } else if (var4.capabilities.isCreativeMode && var1.getBlockState(var2.up()).getBlock() == this) {
         var1.setBlockState(var2.up(), Blocks.air.getDefaultState(), 2);
      }

      super.onBlockHarvested(var1, var2, var3, var4);
   }

   private boolean onHarvest(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      BlockDoublePlant.EnumPlantType var8 = var3.getValue(VARIANT);
      if (var8 != BlockDoublePlant.EnumPlantType.FERN && var8 != BlockDoublePlant.EnumPlantType.GRASS) {
         return false;
      }

      var4.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
      int var9 = (var8 == BlockDoublePlant.EnumPlantType.GRASS ? BlockTallGrass.EnumType.GRASS : BlockTallGrass.EnumType.FERN).getMeta();
      spawnAsEntity(var1, var2, new ItemStack(Blocks.tallgrass, 2, var9));
      return true;
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (BlockDoublePlant.EnumPlantType var10 : BlockDoublePlant.EnumPlantType.values()) {
         var3.add(new ItemStack(var1, 1, var10.getMeta()));
      }
   }

   @Override
   public int getDamageValue(World var1, BlockPos var2) {
      return this.getVariant(var1, var2).getMeta();
   }

   @Override
   public boolean canGrow(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      BlockDoublePlant.EnumPlantType var8 = this.getVariant(var1, var2);
      return var8 != BlockDoublePlant.EnumPlantType.GRASS && var8 != BlockDoublePlant.EnumPlantType.FERN;
   }

   @Override
   public boolean canUseBonemeal(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   @Override
   public void grow(World var1, Random var2, BlockPos var3, IBlockState var4) {
      spawnAsEntity(var1, var3, new ItemStack(this, 1, this.getVariant(var1, var3).getMeta()));
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return (var1 & 8) > 0
         ? this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER)
         : this.getDefaultState()
            .withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER)
            .withProperty(VARIANT, BlockDoublePlant.EnumPlantType.byMetadata(var1 & 7));
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      if (var1.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         IBlockState var7 = var2.getBlockState(var3.down());
         if (var7.getBlock() == this) {
            var1 = var1.withProperty(VARIANT, var7.getValue(VARIANT));
         }
      }

      return var1;
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER ? 8 | var1.getValue(FACING).getHorizontalIndex() : var1.getValue(VARIANT).getMeta();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, HALF, VARIANT, FACING);
   }

   @Override
   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.XZ;
   }

   public enum EnumBlockHalf implements IStringSerializable {
      UPPER,
      LOWER;

      private static final long a = 21157859906053L;

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this == UPPER ? "upper" : "lower";
      }
   }

   public enum EnumPlantType implements IStringSerializable {
      SUNFLOWER(0, "sunflower"),
      SYRINGA(1, "syringa"),
      GRASS(2, "double_grass", "grass"),
      FERN(3, "double_fern", "fern"),
      ROSE(4, "double_rose", "rose"),
      PAEONIA(5, "paeonia");

      private static final BlockDoublePlant.EnumPlantType[] META_LOOKUP = new BlockDoublePlant.EnumPlantType[values().length];
      private final int meta;
      private final String name;
      private final String unlocalizedName;
      private static final long a = 74538867890403L;

      EnumPlantType(int var3, String var4) {
         this(var3, var4, var4);
      }

      EnumPlantType(int var3, String var4, String var5) {
         this.meta = var3;
         this.name = var4;
         this.unlocalizedName = var5;
      }

      public int getMeta() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockDoublePlant.EnumPlantType byMetadata(int var0) {
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
         for (BlockDoublePlant.EnumPlantType var20 : values()) {
            META_LOOKUP[var20.getMeta()] = var20;
         }
      }
   }
}
