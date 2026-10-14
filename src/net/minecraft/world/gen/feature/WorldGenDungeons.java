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

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(2) + 2;
      int j = -i - 1;
      int k = i + 1;
      int l = rand.nextInt(2) + 2;
      int m = -l - 1;
      int n = l + 1;
      int i2 = 0;

      for (int i3 = j; i3 <= k; i3++) {
         for (int i4 = -1; i4 <= 4; i4++) {
            for (int i5 = m; i5 <= n; i5++) {
               BlockPos blockPos = position.add(i3, i4, i5);
               Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
               boolean bl = material.isSolid();
               if (i4 == -1 && !bl) {
                  return false;
               }

               if (i4 == 4 && !bl) {
                  return false;
               }

               if ((i3 == j || i3 == k || i5 == m || i5 == n)
                  && i4 == 0
                  && worldIn.isAirBlock(blockPos)
                  && worldIn.isAirBlock(blockPos.up())) {
                  i2++;
               }
            }
         }
      }

      if (i2 >= 1 && i2 <= 5) {
         for (int i6 = j; i6 <= k; i6++) {
            for (int i7 = 3; i7 >= -1; i7--) {
               for (int i8 = m; i8 <= n; i8++) {
                  BlockPos blockPos2 = position.add(i6, i7, i8);
                  if (i6 != j && i7 != -1 && i8 != m && i6 != k && i7 != 4 && i8 != n) {
                     if (worldIn.getBlockState(blockPos2).getBlock() != Blocks.chest) {
                        worldIn.setBlockToAir(blockPos2);
                     }
                  } else if (blockPos2.getY() >= 0 && !worldIn.getBlockState(blockPos2.down()).getBlock().getMaterial().isSolid()) {
                     worldIn.setBlockToAir(blockPos2);
                  } else if (worldIn.getBlockState(blockPos2).getBlock().getMaterial().isSolid() && worldIn.getBlockState(blockPos2).getBlock() != Blocks.chest) {
                     if (i7 == -1 && rand.nextInt(4) != 0) {
                        worldIn.setBlockState(blockPos2, Blocks.mossy_cobblestone.getDefaultState(), 2);
                     } else {
                        worldIn.setBlockState(blockPos2, Blocks.cobblestone.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

         for (int i9 = 0; i9 < 2; i9++) {
            for (int i10 = 0; i10 < 3; i10++) {
               int i11 = position.getX() + rand.nextInt(i * 2 + 1) - i;
               int i12 = position.getY();
               int i13 = position.getZ() + rand.nextInt(l * 2 + 1) - l;
               BlockPos blockPos3 = new BlockPos(i11, i12, i13);
               if (worldIn.isAirBlock(blockPos3)) {
                  int i14 = 0;

                  for (Object object : EnumFacing.Plane.HORIZONTAL) {
                     if (worldIn.getBlockState(blockPos3.offset((EnumFacing)object)).getBlock().getMaterial().isSolid()) {
                        i14++;
                     }
                  }

                  if (i14 == 1) {
                     worldIn.setBlockState(blockPos3, Blocks.chest.correctFacing(worldIn, blockPos3, Blocks.chest.getDefaultState()), 2);
                     List list = WeightedRandomChestContent.func_177629_a(CHESTCONTENT, Items.enchanted_book.getRandom(rand));
                     TileEntity tileEntity = worldIn.getTileEntity(blockPos3);
                     if (tileEntity instanceof TileEntityChest) {
                        WeightedRandomChestContent.generateChestContents(rand, list, (TileEntityChest)tileEntity, 8);
                     }
                     break;
                  }
               }
            }
         }

         worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState(), 2);
         TileEntity tileEntity2 = worldIn.getTileEntity(position);
         if (tileEntity2 instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner)tileEntity2).getSpawnerBaseLogic().setEntityName(this.pickMobSpawner(rand));
         } else {
            field_175918_a.error("Failed to fetch mob spawner entity at (" + position.getX() + ", " + position.getY() + ", " + position.getZ() + ")");
         }

         return true;
      } else {
         return false;
      }
   }

   private String pickMobSpawner(Random random2) {
      return SPAWNERTYPES[random2.nextInt(SPAWNERTYPES.length)];
   }
}
