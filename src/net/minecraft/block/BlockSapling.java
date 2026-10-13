package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;

public class BlockSapling extends BlockBush implements IGrowable {
   public static final PropertyEnum<BlockPlanks.EnumType> TYPE = PropertyEnum.create("type", BlockPlanks.EnumType.class);
   public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
   private static final long e = 51086624602128L;

   public BlockSapling() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockPlanks.EnumType.OAK).withProperty(STAGE, 0));
      float var1 = 0.4F;
      this.setBlockBounds(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var1 * 2.0F, 0.5F + var1);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockPlanks.EnumType.OAK.getUnlocalizedName() + ".name");
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.isRemote) {
         super.updateTick(var1, var2, var3, var4);
         if (var1.getLightFromNeighbors(var2.up()) >= 9 && var4.nextInt(7) == 0) {
            this.grow(var1, var2, var3, var4);
         }
      }
   }

   public void grow(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var3.getValue(STAGE) == 0) {
         var1.setBlockState(var2, var3.cycleProperty(STAGE), 4);
      } else {
         this.generateTree(var1, var2, var3, var4);
      }
   }

   public void generateTree(World var1, BlockPos var2, IBlockState var3, Random var4) {
      WorldGenAbstractTree var8 = var4.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
      int var9 = 0;
      int var10 = 0;
      boolean var11 = false;
      switch ((BlockPlanks.EnumType)var3.getValue(TYPE)) {
         case SPRUCE:
            label68:
            for (var9 = 0; var9 >= -1; var9--) {
               for (var10 = 0; var10 >= -1; var10--) {
                  if (this.func_181624_a(var1, var2, var9, var10, BlockPlanks.EnumType.SPRUCE)) {
                     var8 = new WorldGenMegaPineTree(false, var4.nextBoolean());
                     var11 = true;
                     break label68;
                  }
               }
            }

            if (!var11) {
               var10 = 0;
               var9 = 0;
               var8 = new WorldGenTaiga2(true);
            }
            break;
         case BIRCH:
            var8 = new WorldGenForest(true, false);
            break;
         case JUNGLE:
            IBlockState var12 = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
            IBlockState var13 = Blocks.leaves
               .getDefaultState()
               .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
               .withProperty(BlockLeaves.CHECK_DECAY, false);

            label82:
            for (var9 = 0; var9 >= -1; var9--) {
               for (var10 = 0; var10 >= -1; var10--) {
                  if (this.func_181624_a(var1, var2, var9, var10, BlockPlanks.EnumType.JUNGLE)) {
                     var8 = new WorldGenMegaJungle(true, 10, 20, var12, var13);
                     var11 = true;
                     break label82;
                  }
               }
            }

            if (!var11) {
               var10 = 0;
               var9 = 0;
               var8 = new WorldGenTrees(true, 4 + var4.nextInt(7), var12, var13, false);
            }
            break;
         case ACACIA:
            var8 = new WorldGenSavannaTree(true);
            break;
         case DARK_OAK:
            label96:
            for (var9 = 0; var9 >= -1; var9--) {
               for (var10 = 0; var10 >= -1; var10--) {
                  if (this.func_181624_a(var1, var2, var9, var10, BlockPlanks.EnumType.DARK_OAK)) {
                     var8 = new WorldGenCanopyTree(true);
                     var11 = true;
                     break label96;
                  }
               }
            }

            if (!var11) {
               return;
            }
         case OAK:
      }

      IBlockState var14 = Blocks.air.getDefaultState();
      if (var11) {
         var1.setBlockState(var2.add(var9, 0, var10), var14, 4);
         var1.setBlockState(var2.add(var9 + 1, 0, var10), var14, 4);
         var1.setBlockState(var2.add(var9, 0, var10 + 1), var14, 4);
         var1.setBlockState(var2.add(var9 + 1, 0, var10 + 1), var14, 4);
      } else {
         var1.setBlockState(var2, var14, 4);
      }

      if (!var8.generate(var1, var4, var2.add(var9, 0, var10))) {
         if (var11) {
            var1.setBlockState(var2.add(var9, 0, var10), var3, 4);
            var1.setBlockState(var2.add(var9 + 1, 0, var10), var3, 4);
            var1.setBlockState(var2.add(var9, 0, var10 + 1), var3, 4);
            var1.setBlockState(var2.add(var9 + 1, 0, var10 + 1), var3, 4);
         } else {
            var1.setBlockState(var2, var3, 4);
         }
      }
   }

   private boolean func_181624_a(World var1, BlockPos var2, int var3, int var4, BlockPlanks.EnumType var5) {
      return this.isTypeAt(var1, var2.add(var3, 0, var4), var5)
         && this.isTypeAt(var1, var2.add(var3 + 1, 0, var4), var5)
         && this.isTypeAt(var1, var2.add(var3, 0, var4 + 1), var5)
         && this.isTypeAt(var1, var2.add(var3 + 1, 0, var4 + 1), var5);
   }

   public boolean isTypeAt(World var1, BlockPos var2, BlockPlanks.EnumType var3) {
      IBlockState var7 = var1.getBlockState(var2);
      return var7.getBlock() == this && var7.getValue(TYPE) == var3;
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(TYPE).getMetadata();
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (BlockPlanks.EnumType var10 : BlockPlanks.EnumType.values()) {
         var3.add(new ItemStack(var1, 1, var10.getMetadata()));
      }
   }

   @Override
   public boolean canGrow(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return true;
   }

   @Override
   public boolean canUseBonemeal(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return var1.rand.nextFloat() < 0.45;
   }

   @Override
   public void grow(World var1, Random var2, BlockPos var3, IBlockState var4) {
      this.grow(var1, var3, var4, var2);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(TYPE, BlockPlanks.EnumType.byMetadata(var1 & 7)).withProperty(STAGE, (var1 & 8) >> 3);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var2 = 0;
      var2 |= var1.getValue(TYPE).getMetadata();
      return var2 | var1.getValue(STAGE) << 3;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, TYPE, STAGE);
   }
}
