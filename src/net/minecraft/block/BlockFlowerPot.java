package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFlowerPot extends BlockContainer {
   public static final PropertyInteger LEGACY_DATA = PropertyInteger.create("legacy_data", 0, 15);
   public static final PropertyEnum<BlockFlowerPot.EnumFlowerType> CONTENTS = PropertyEnum.create("contents", BlockFlowerPot.EnumFlowerType.class);
   private static final long e = 87044917993348L;

   public BlockFlowerPot() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(CONTENTS, BlockFlowerPot.EnumFlowerType.EMPTY).withProperty(LEGACY_DATA, 0));
      this.setBlockBoundsForItemRender();
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.flowerPot.name");
   }

   @Override
   public void setBlockBoundsForItemRender() {
      float var1 = 0.375F;
      float var2 = var1 / 2.0F;
      this.setBlockBounds(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, var1, 0.5F + var2);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      TileEntity var7 = var1.getTileEntity(var2);
      if (var7 instanceof TileEntityFlowerPot) {
         Item var8 = ((TileEntityFlowerPot)var7).getFlowerPotItem();
         if (var8 instanceof ItemBlock) {
            return Block.getBlockFromItem(var8).colorMultiplier(var1, var2, var3);
         }
      }

      return 16777215;
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      ItemStack var12 = var4.inventory.getCurrentItem();
      if (var12 != null && var12.getItem() instanceof ItemBlock) {
         TileEntityFlowerPot var13 = this.getTileEntity(var1, var2);
         if (var13 == null) {
            return false;
         }

         if (var13.getFlowerPotItem() != null) {
            return false;
         }

         Block var14 = Block.getBlockFromItem(var12.getItem());
         if (!this.canNotContain(var14, var12.getMetadata())) {
            return false;
         }

         var13.setFlowerPotData(var12.getItem(), var12.getMetadata());
         var13.markDirty();
         var1.markBlockForUpdate(var2);
         var4.triggerAchievement(StatList.field_181736_T);
         if (!var4.capabilities.isCreativeMode && --var12.stackSize <= 0) {
            var4.inventory.setInventorySlotContents(var4.inventory.currentItem, (ItemStack)null);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean canNotContain(Block var1, int var2) {
      return var1 != Blocks.yellow_flower
            && var1 != Blocks.red_flower
            && var1 != Blocks.cactus
            && var1 != Blocks.brown_mushroom
            && var1 != Blocks.red_mushroom
            && var1 != Blocks.sapling
            && var1 != Blocks.deadbush
         ? var1 == Blocks.tallgrass && var2 == BlockTallGrass.EnumType.FERN.getMeta()
         : true;
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      TileEntityFlowerPot var6 = this.getTileEntity(var1, var2);
      return var6 != null && var6.getFlowerPotItem() != null ? var6.getFlowerPotItem() : Items.flower_pot;
   }

   @Override
   public int getDamageValue(World var1, BlockPos var2) {
      TileEntityFlowerPot var6 = this.getTileEntity(var1, var2);
      return var6 != null && var6.getFlowerPotItem() != null ? var6.getFlowerPotData() : 0;
   }

   @Override
   public boolean isFlowerPot() {
      return true;
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return super.canPlaceBlockAt(var1, var2) && World.doesBlockHaveSolidTopSurface(var1, var2.down());
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!World.doesBlockHaveSolidTopSurface(var1, var2.down())) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
      }
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      TileEntityFlowerPot var7 = this.getTileEntity(var1, var2);
      if (var7 != null && var7.getFlowerPotItem() != null) {
         spawnAsEntity(var1, var2, new ItemStack(var7.getFlowerPotItem(), 1, var7.getFlowerPotData()));
      }

      super.breakBlock(var1, var2, var3);
   }

   @Override
   public void onBlockHarvested(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      super.onBlockHarvested(var1, var2, var3, var4);
      if (var4.capabilities.isCreativeMode) {
         TileEntityFlowerPot var8 = this.getTileEntity(var1, var2);
         if (var8 != null) {
            var8.setFlowerPotData((Item)null, 0);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.flower_pot;
   }

   private TileEntityFlowerPot getTileEntity(World var1, BlockPos var2) {
      TileEntity var6 = var1.getTileEntity(var2);
      return var6 instanceof TileEntityFlowerPot ? (TileEntityFlowerPot)var6 : null;
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      Block var5 = null;
      int var6 = 0;
      switch (var2) {
         case 1:
            var5 = Blocks.red_flower;
            var6 = BlockFlower.EnumFlowerType.POPPY.getMeta();
            break;
         case 2:
            var5 = Blocks.yellow_flower;
            break;
         case 3:
            var5 = Blocks.sapling;
            var6 = BlockPlanks.EnumType.OAK.getMetadata();
            break;
         case 4:
            var5 = Blocks.sapling;
            var6 = BlockPlanks.EnumType.SPRUCE.getMetadata();
            break;
         case 5:
            var5 = Blocks.sapling;
            var6 = BlockPlanks.EnumType.BIRCH.getMetadata();
            break;
         case 6:
            var5 = Blocks.sapling;
            var6 = BlockPlanks.EnumType.JUNGLE.getMetadata();
            break;
         case 7:
            var5 = Blocks.red_mushroom;
            break;
         case 8:
            var5 = Blocks.brown_mushroom;
            break;
         case 9:
            var5 = Blocks.cactus;
            break;
         case 10:
            var5 = Blocks.deadbush;
            break;
         case 11:
            var5 = Blocks.tallgrass;
            var6 = BlockTallGrass.EnumType.FERN.getMeta();
            break;
         case 12:
            var5 = Blocks.sapling;
            var6 = BlockPlanks.EnumType.ACACIA.getMetadata();
            break;
         case 13:
            var5 = Blocks.sapling;
            var6 = BlockPlanks.EnumType.DARK_OAK.getMetadata();
      }

      return new TileEntityFlowerPot(Item.getItemFromBlock(var5), var6);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, CONTENTS, LEGACY_DATA);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(LEGACY_DATA);
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      BlockFlowerPot.EnumFlowerType var7 = BlockFlowerPot.EnumFlowerType.EMPTY;
      TileEntity var8 = var2.getTileEntity(var3);
      if (var8 instanceof TileEntityFlowerPot) {
         TileEntityFlowerPot var9 = (TileEntityFlowerPot)var8;
         Item var10 = var9.getFlowerPotItem();
         if (var10 instanceof ItemBlock) {
            int var11 = var9.getFlowerPotData();
            Block var12 = Block.getBlockFromItem(var10);
            if (var12 == Blocks.sapling) {
               switch (BlockPlanks.EnumType.byMetadata(var11)) {
                  case OAK:
                     var7 = BlockFlowerPot.EnumFlowerType.OAK_SAPLING;
                     break;
                  case SPRUCE:
                     var7 = BlockFlowerPot.EnumFlowerType.SPRUCE_SAPLING;
                     break;
                  case BIRCH:
                     var7 = BlockFlowerPot.EnumFlowerType.BIRCH_SAPLING;
                     break;
                  case JUNGLE:
                     var7 = BlockFlowerPot.EnumFlowerType.JUNGLE_SAPLING;
                     break;
                  case ACACIA:
                     var7 = BlockFlowerPot.EnumFlowerType.ACACIA_SAPLING;
                     break;
                  case DARK_OAK:
                     var7 = BlockFlowerPot.EnumFlowerType.DARK_OAK_SAPLING;
                     break;
                  default:
                     var7 = BlockFlowerPot.EnumFlowerType.EMPTY;
               }
            } else if (var12 == Blocks.tallgrass) {
               switch (var11) {
                  case 0:
                     var7 = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
                     break;
                  case 2:
                     var7 = BlockFlowerPot.EnumFlowerType.FERN;
                     break;
                  default:
                     var7 = BlockFlowerPot.EnumFlowerType.EMPTY;
               }
            } else if (var12 == Blocks.yellow_flower) {
               var7 = BlockFlowerPot.EnumFlowerType.DANDELION;
            } else if (var12 == Blocks.red_flower) {
               switch (BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, var11)) {
                  case POPPY:
                     var7 = BlockFlowerPot.EnumFlowerType.POPPY;
                     break;
                  case BLUE_ORCHID:
                     var7 = BlockFlowerPot.EnumFlowerType.BLUE_ORCHID;
                     break;
                  case ALLIUM:
                     var7 = BlockFlowerPot.EnumFlowerType.ALLIUM;
                     break;
                  case HOUSTONIA:
                     var7 = BlockFlowerPot.EnumFlowerType.HOUSTONIA;
                     break;
                  case RED_TULIP:
                     var7 = BlockFlowerPot.EnumFlowerType.RED_TULIP;
                     break;
                  case ORANGE_TULIP:
                     var7 = BlockFlowerPot.EnumFlowerType.ORANGE_TULIP;
                     break;
                  case WHITE_TULIP:
                     var7 = BlockFlowerPot.EnumFlowerType.WHITE_TULIP;
                     break;
                  case PINK_TULIP:
                     var7 = BlockFlowerPot.EnumFlowerType.PINK_TULIP;
                     break;
                  case OXEYE_DAISY:
                     var7 = BlockFlowerPot.EnumFlowerType.OXEYE_DAISY;
                     break;
                  default:
                     var7 = BlockFlowerPot.EnumFlowerType.EMPTY;
               }
            } else if (var12 == Blocks.red_mushroom) {
               var7 = BlockFlowerPot.EnumFlowerType.MUSHROOM_RED;
            } else if (var12 == Blocks.brown_mushroom) {
               var7 = BlockFlowerPot.EnumFlowerType.MUSHROOM_BROWN;
            } else if (var12 == Blocks.deadbush) {
               var7 = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
            } else if (var12 == Blocks.cactus) {
               var7 = BlockFlowerPot.EnumFlowerType.CACTUS;
            }
         }
      }

      return var1.withProperty(CONTENTS, var7);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public enum EnumFlowerType implements IStringSerializable {
      EMPTY("empty"),
      POPPY("rose"),
      BLUE_ORCHID("blue_orchid"),
      ALLIUM("allium"),
      HOUSTONIA("houstonia"),
      RED_TULIP("red_tulip"),
      ORANGE_TULIP("orange_tulip"),
      WHITE_TULIP("white_tulip"),
      PINK_TULIP("pink_tulip"),
      OXEYE_DAISY("oxeye_daisy"),
      DANDELION("dandelion"),
      OAK_SAPLING("oak_sapling"),
      SPRUCE_SAPLING("spruce_sapling"),
      BIRCH_SAPLING("birch_sapling"),
      JUNGLE_SAPLING("jungle_sapling"),
      ACACIA_SAPLING("acacia_sapling"),
      DARK_OAK_SAPLING("dark_oak_sapling"),
      MUSHROOM_RED("mushroom_red"),
      MUSHROOM_BROWN("mushroom_brown"),
      DEAD_BUSH("dead_bush"),
      FERN("fern"),
      CACTUS("cactus");

      private final String name;

      EnumFlowerType(String var3) {
         this.name = var3;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
