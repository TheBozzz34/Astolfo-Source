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
   private static final long x = 67119386082498L;

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
   public int getRenderColor(IBlockState var1) {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   @Override
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      return BiomeColorHelper.getFoliageColorAtPos(var1, var2);
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      byte var7 = 1;
      int var8 = var7 + 1;
      int var9 = var2.getX();
      int var10 = var2.getY();
      int var11 = var2.getZ();
      if (var1.isAreaLoaded(new BlockPos(var9 - var8, var10 - var8, var11 - var8), new BlockPos(var9 + var8, var10 + var8, var11 + var8))) {
         for (int var12 = -var7; var12 <= var7; var12++) {
            for (int var13 = -var7; var13 <= var7; var13++) {
               for (int var14 = -var7; var14 <= var7; var14++) {
                  BlockPos var15 = var2.add(var12, var13, var14);
                  IBlockState var16 = var1.getBlockState(var15);
                  if (var16.getBlock().getMaterial() == Material.leaves && !var16.getValue(CHECK_DECAY)) {
                     var1.setBlockState(var15, var16.withProperty(CHECK_DECAY, true), 4);
                  }
               }
            }
         }
      }
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      long var5 = x ^ 82117306801952L;
      if (!var1.isRemote && var3.getValue(CHECK_DECAY) && var3.getValue(DECAYABLE)) {
         byte var8 = 4;
         int var9 = var8 + 1;
         long var10000 = 6234113995942740239L ^ var5;
         int var10 = var2.getX();
         int var11 = var2.getY();
         int var12 = var2.getZ();
         byte var13 = 32;
         int var14 = var13 * var13;
         int var15 = var13 / 2;
         if (this.surroundings == null) {
            this.surroundings = new int[var13 * var13 * var13];
         }

         if (var1.isAreaLoaded(new BlockPos(var10 - var9, var11 - var9, var12 - var9), new BlockPos(var10 + var9, var11 + var9, var12 + var9))) {
            BlockPos.MutableBlockPos var16 = new BlockPos.MutableBlockPos();

            for (int var17 = -var8; var17 <= var8; var17++) {
               for (int var18 = -var8; var18 <= var8; var18++) {
                  for (int var19 = -var8; var19 <= var8; var19++) {
                     Block var20 = var1.getBlockState(var16.set(var10 + var17, var11 + var18, var12 + var19)).getBlock();
                     if (var20 != Blocks.log && var20 != Blocks.log2) {
                        if (var20.getMaterial() == Material.leaves) {
                           this.surroundings[(var17 + var15) * var14 + (var18 + var15) * var13 + var19 + var15] = -2;
                        } else {
                           this.surroundings[(var17 + var15) * var14 + (var18 + var15) * var13 + var19 + var15] = -1;
                        }
                     } else {
                        this.surroundings[(var17 + var15) * var14 + (var18 + var15) * var13 + var19 + var15] = 0;
                     }
                  }
               }
            }

            for (int var22 = 1; var22 <= 4; var22++) {
               for (int var23 = -var8; var23 <= var8; var23++) {
                  for (int var24 = -var8; var24 <= var8; var24++) {
                     for (int var25 = -var8; var25 <= var8; var25++) {
                        if (this.surroundings[(var23 + var15) * var14 + (var24 + var15) * var13 + var25 + var15] == var22 - 1) {
                           if (this.surroundings[(var23 + var15 - 1) * var14 + (var24 + var15) * var13 + var25 + var15] == -2) {
                              this.surroundings[(var23 + var15 - 1) * var14 + (var24 + var15) * var13 + var25 + var15] = var22;
                           }

                           if (this.surroundings[(var23 + var15 + 1) * var14 + (var24 + var15) * var13 + var25 + var15] == -2) {
                              this.surroundings[(var23 + var15 + 1) * var14 + (var24 + var15) * var13 + var25 + var15] = var22;
                           }

                           if (this.surroundings[(var23 + var15) * var14 + (var24 + var15 - 1) * var13 + var25 + var15] == -2) {
                              this.surroundings[(var23 + var15) * var14 + (var24 + var15 - 1) * var13 + var25 + var15] = var22;
                           }

                           if (this.surroundings[(var23 + var15) * var14 + (var24 + var15 + 1) * var13 + var25 + var15] == -2) {
                              this.surroundings[(var23 + var15) * var14 + (var24 + var15 + 1) * var13 + var25 + var15] = var22;
                           }

                           if (this.surroundings[(var23 + var15) * var14 + (var24 + var15) * var13 + (var25 + var15 - 1)] == -2) {
                              this.surroundings[(var23 + var15) * var14 + (var24 + var15) * var13 + (var25 + var15 - 1)] = var22;
                           }

                           if (this.surroundings[(var23 + var15) * var14 + (var24 + var15) * var13 + var25 + var15 + 1] == -2) {
                              this.surroundings[(var23 + var15) * var14 + (var24 + var15) * var13 + var25 + var15 + 1] = var22;
                           }
                        }
                     }
                  }
               }
            }
         }

         int var21 = this.surroundings[var15 * var14 + var15 * var13 + var15];
         if (var21 >= 0) {
            var1.setBlockState(var2, var3.withProperty(CHECK_DECAY, false), 4);
         } else {
            this.destroy(var1, var2);
         }
      }
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var1.isRainingAt(var2.up()) && !World.doesBlockHaveSolidTopSurface(var1, var2.down()) && var4.nextInt(15) == 1) {
         double var8 = var2.getX() + var4.nextFloat();
         double var10 = var2.getY() - 0.05;
         double var12 = var2.getZ() + var4.nextFloat();
         var1.spawnParticle(EnumParticleTypes.DRIP_WATER, var8, var10, var12, 0.0, 0.0, 0.0);
      }
   }

   private void destroy(World var1, BlockPos var2) {
      this.dropBlockAsItem(var1, var2, var1.getBlockState(var2), 0);
      var1.setBlockToAir(var2);
   }

   @Override
   public int quantityDropped(Random var1) {
      return var1.nextInt(20) == 0 ? 1 : 0;
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Item.getItemFromBlock(Blocks.sapling);
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.isRemote) {
         int var9 = this.getSaplingDropChance(var3);
         if (var5 > 0) {
            var9 -= 2 << var5;
            if (var9 < 10) {
               var9 = 10;
            }
         }

         if (var1.rand.nextInt(var9) == 0) {
            Item var10 = this.getItemDropped(var3, var1.rand, var5);
            spawnAsEntity(var1, var2, new ItemStack(var10, 1, this.damageDropped(var3)));
         }

         var9 = 200;
         if (var5 > 0) {
            var9 -= 10 << var5;
            if (var9 < 40) {
               var9 = 40;
            }
         }

         this.dropApple(var1, var2, var3, var9);
      }
   }

   public void dropApple(World var1, BlockPos var2, IBlockState var3, int var4) {
   }

   public int getSaplingDropChance(IBlockState var1) {
      return 20;
   }

   @Override
   public boolean isOpaqueCube() {
      return !this.fancyGraphics;
   }

   public void setGraphicsLevel(boolean var1) {
      this.isTransparent = var1;
      this.fancyGraphics = var1;
      this.iconIndex = var1 ? 0 : 1;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return this.isTransparent ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
   }

   @Override
   public boolean isVisuallyOpaque() {
      return false;
   }

   public abstract BlockPlanks.EnumType getWoodType(int var1);
}
