package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenDungeons extends WorldGenerator {
   private static final Logger field_175918_a = LogManager.getLogger();
   private static final String[] SPAWNERTYPES = new String[]{"Skeleton", "Zombie", "Zombie", "Spider"};
   private static final List<WeightedRandomChestContent> CHESTCONTENT = Lists.newArrayList(
      new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 4, 10),
      new WeightedRandomChestContent(Items.bread, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.wheat, 0, 1, 4, 10),
      new WeightedRandomChestContent(Items.gunpowder, 0, 1, 4, 10),
      new WeightedRandomChestContent(Items.string, 0, 1, 4, 10),
      new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1),
      new WeightedRandomChestContent(Items.redstone, 0, 1, 4, 10),
      new WeightedRandomChestContent(Items.record_13, 0, 1, 1, 4),
      new WeightedRandomChestContent(Items.record_cat, 0, 1, 1, 4),
      new WeightedRandomChestContent(Items.name_tag, 0, 1, 1, 10),
      new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 2),
      new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
   );
   private static final long a = 133072748709056L;

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      int var8 = var2.nextInt(2) + 2;
      int var9 = -var8 - 1;
      int var10 = var8 + 1;
      int var13 = var2.nextInt(2) + 2;
      int var14 = -var13 - 1;
      int var15 = var13 + 1;
      int var16 = 0;

      for (int var17 = var9; var17 <= var10; var17++) {
         for (int var18 = -1; var18 <= 4; var18++) {
            for (int var19 = var14; var19 <= var15; var19++) {
               BlockPos var20 = var3.add(var17, var18, var19);
               Material var21 = var1.getBlockState(var20).getBlock().getMaterial();
               boolean var22 = var21.isSolid();
               if (var18 == -1 && !var22) {
                  return false;
               }

               if (var18 == 4 && !var22) {
                  return false;
               }

               if ((var17 == var9 || var17 == var10 || var19 == var14 || var19 == var15) && var18 == 0 && var1.isAirBlock(var20) && var1.isAirBlock(var20.up())
                  )
                {
                  var16++;
               }
            }
         }
      }

      if (var16 >= 1 && var16 <= 5) {
         for (int var26 = var9; var26 <= var10; var26++) {
            for (int var29 = 3; var29 >= -1; var29--) {
               for (int var31 = var14; var31 <= var15; var31++) {
                  BlockPos var33 = var3.add(var26, var29, var31);
                  if (var26 != var9 && var29 != -1 && var31 != var14 && var26 != var10 && var29 != 4 && var31 != var15) {
                     if (var1.getBlockState(var33).getBlock() != Blocks.chest) {
                        var1.setBlockToAir(var33);
                     }
                  } else if (var33.getY() >= 0 && !var1.getBlockState(var33.down()).getBlock().getMaterial().isSolid()) {
                     var1.setBlockToAir(var33);
                  } else if (var1.getBlockState(var33).getBlock().getMaterial().isSolid() && var1.getBlockState(var33).getBlock() != Blocks.chest) {
                     if (var29 == -1 && var2.nextInt(4) != 0) {
                        var1.setBlockState(var33, Blocks.mossy_cobblestone.getDefaultState(), 2);
                     } else {
                        var1.setBlockState(var33, Blocks.cobblestone.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

         for (int var27 = 0; var27 < 2; var27++) {
            for (int var30 = 0; var30 < 3; var30++) {
               int var32 = var3.getX() + var2.nextInt(var8 * 2 + 1) - var8;
               int var34 = var3.getY();
               int var35 = var3.getZ() + var2.nextInt(var13 * 2 + 1) - var13;
               BlockPos var36 = new BlockPos(var32, var34, var35);
               if (var1.isAirBlock(var36)) {
                  int var23 = 0;

                  for (Object var25 : EnumFacing.Plane.HORIZONTAL) {
                     if (var1.getBlockState(var36.offset((EnumFacing)var25)).getBlock().getMaterial().isSolid()) {
                        var23++;
                     }
                  }

                  if (var23 == 1) {
                     var1.setBlockState(var36, Blocks.chest.correctFacing(var1, var36, Blocks.chest.getDefaultState()), 2);
                     List var37 = WeightedRandomChestContent.func_177629_a(CHESTCONTENT, Items.enchanted_book.getRandom(var2));
                     TileEntity var38 = var1.getTileEntity(var36);
                     if (var38 instanceof TileEntityChest) {
                        WeightedRandomChestContent.generateChestContents(var2, var37, (TileEntityChest)var38, 8);
                     }
                     break;
                  }
               }
            }
         }

         var1.setBlockState(var3, Blocks.mob_spawner.getDefaultState(), 2);
         TileEntity var28 = var1.getTileEntity(var3);
         if (var28 instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner)var28).getSpawnerBaseLogic().setEntityName(this.pickMobSpawner(var2));
         } else {
            field_175918_a.error("Failed to fetch mob spawner entity at (" + var3.getX() + ", " + var3.getY() + ", " + var3.getZ() + ")");
         }

         return true;
      } else {
         return false;
      }
   }

   private String pickMobSpawner(Random var1) {
      return SPAWNERTYPES[var1.nextInt(SPAWNERTYPES.length)];
   }

   static {
      long var20 = a ^ 54601469313003L;
      long var10000 = 872157486398752095L ^ var20;
   }
}
