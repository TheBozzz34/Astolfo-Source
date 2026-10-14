package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLeaves extends BlockLeavesBase {
   public static final PropertyBool DECAYABLE = PropertyBool.create("decayable");
   public static final PropertyBool CHECK_DECAY = PropertyBool.create("check_decay");
   public int[] surroundings;
   public int iconIndex;
   public boolean isTransparent;

   public BlockLeaves() {
      super(Material.leaves, false);
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setHardness(0.2F);
      this.setLightOpacity(1);
      this.setStepSound(soundTypeGrass);
   }

   @Override
   public int getBlockColor() {
      return ColorizerFoliage.getFoliageColor(0.5, 1.0);
   }

   @Override
   public int getRenderColor(IBlockState state) {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      byte b = 1;
      int i = b + 1;
      int j = pos.getX();
      int k = pos.getY();
      int l = pos.getZ();
      if (worldIn.isAreaLoaded(new BlockPos(j - i, k - i, l - i), new BlockPos(j + i, k + i, l + i))) {
         for (int m = -b; m <= b; m++) {
            for (int n = -b; n <= b; n++) {
               for (int i2 = -b; i2 <= b; i2++) {
                  BlockPos blockPos = pos.add(m, n, i2);
                  IBlockState iBlockState = worldIn.getBlockState(blockPos);
                  if (iBlockState.getBlock().getMaterial() == Material.leaves && !iBlockState.getValue(CHECK_DECAY)) {
                     worldIn.setBlockState(blockPos, iBlockState.withProperty(CHECK_DECAY, true), 4);
                  }
               }
            }
         }
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && state.getValue(CHECK_DECAY) && state.getValue(DECAYABLE)) {
         byte b = 4;
         int i = b + 1;
         int j = pos.getX();
         int k = pos.getY();
         int l = pos.getZ();
         byte b2 = 32;
         int m = b2 * b2;
         int n = b2 / 2;
         if (this.surroundings == null) {
            this.surroundings = new int[b2 * b2 * b2];
         }

         if (worldIn.isAreaLoaded(new BlockPos(j - i, k - i, l - i), new BlockPos(j + i, k + i, l + i))) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int i2 = -b; i2 <= b; i2++) {
               for (int i3 = -b; i3 <= b; i3++) {
                  for (int i4 = -b; i4 <= b; i4++) {
                     Block block = worldIn.getBlockState(mutableBlockPos.set(j + i2, k + i3, l + i4)).getBlock();
                     if (block != Blocks.log && block != Blocks.log2) {
                        if (block.getMaterial() == Material.leaves) {
                           this.surroundings[(i2 + n) * m + (i3 + n) * b2 + i4 + n] = -2;
                        } else {
                           this.surroundings[(i2 + n) * m + (i3 + n) * b2 + i4 + n] = -1;
                        }
                     } else {
                        this.surroundings[(i2 + n) * m + (i3 + n) * b2 + i4 + n] = 0;
                     }
                  }
               }
            }

            for (int i5 = 1; i5 <= 4; i5++) {
               for (int i6 = -b; i6 <= b; i6++) {
                  for (int i7 = -b; i7 <= b; i7++) {
                     for (int i8 = -b; i8 <= b; i8++) {
                        if (this.surroundings[(i6 + n) * m + (i7 + n) * b2 + i8 + n] == i5 - 1) {
                           if (this.surroundings[(i6 + n - 1) * m + (i7 + n) * b2 + i8 + n] == -2) {
                              this.surroundings[(i6 + n - 1) * m + (i7 + n) * b2 + i8 + n] = i5;
                           }

                           if (this.surroundings[(i6 + n + 1) * m + (i7 + n) * b2 + i8 + n] == -2) {
                              this.surroundings[(i6 + n + 1) * m + (i7 + n) * b2 + i8 + n] = i5;
                           }

                           if (this.surroundings[(i6 + n) * m + (i7 + n - 1) * b2 + i8 + n] == -2) {
                              this.surroundings[(i6 + n) * m + (i7 + n - 1) * b2 + i8 + n] = i5;
                           }

                           if (this.surroundings[(i6 + n) * m + (i7 + n + 1) * b2 + i8 + n] == -2) {
                              this.surroundings[(i6 + n) * m + (i7 + n + 1) * b2 + i8 + n] = i5;
                           }

                           if (this.surroundings[(i6 + n) * m + (i7 + n) * b2 + (i8 + n - 1)] == -2) {
                              this.surroundings[(i6 + n) * m + (i7 + n) * b2 + (i8 + n - 1)] = i5;
                           }

                           if (this.surroundings[(i6 + n) * m + (i7 + n) * b2 + i8 + n + 1] == -2) {
                              this.surroundings[(i6 + n) * m + (i7 + n) * b2 + i8 + n + 1] = i5;
                           }
                        }
                     }
                  }
               }
            }
         }

         int i9 = this.surroundings[n * m + n * b2 + n];
         if (i9 >= 0) {
            worldIn.setBlockState(pos, state.withProperty(CHECK_DECAY, false), 4);
         } else {
            this.destroy(worldIn, pos);
         }
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (worldIn.isRainingAt(pos.up()) && !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && rand.nextInt(15) == 1) {
         double d = pos.getX() + rand.nextFloat();
         double e = pos.getY() - 0.05;
         double d2 = pos.getZ() + rand.nextFloat();
         worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d, e, d2, 0.0, 0.0, 0.0);
      }
   }

   private void destroy(World worldIn, BlockPos pos) {
      this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
      worldIn.setBlockToAir(pos);
   }

   @Override
   public int quantityDropped(Random randomIn) {
      return randomIn.nextInt(20) == 0 ? 1 : 0;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.sapling);
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!worldIn.isRemote) {
         int i = this.getSaplingDropChance(state);
         if (fortune > 0) {
            i -= 2 << fortune;
            if (i < 10) {
               i = 10;
            }
         }

         if (worldIn.rand.nextInt(i) == 0) {
            Item item = this.getItemDropped(state, worldIn.rand, fortune);
            spawnAsEntity(worldIn, pos, new ItemStack(item, 1, this.damageDropped(state)));
         }

         i = 200;
         if (fortune > 0) {
            i -= 10 << fortune;
            if (i < 40) {
               i = 40;
            }
         }

         this.dropApple(worldIn, pos, state, i);
      }
   }

   public void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
   }

   public int getSaplingDropChance(IBlockState state) {
      return 20;
   }

   @Override
   public boolean isOpaqueCube() {
      return !this.fancyGraphics;
   }

   public void setGraphicsLevel(boolean fancy) {
      this.isTransparent = fancy;
      this.fancyGraphics = fancy;
      this.iconIndex = fancy ? 0 : 1;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return this.isTransparent ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
   }

   @Override
   public boolean isVisuallyOpaque() {
      return false;
   }

   public abstract BlockPlanks.EnumType getWoodType(int meta);
}
