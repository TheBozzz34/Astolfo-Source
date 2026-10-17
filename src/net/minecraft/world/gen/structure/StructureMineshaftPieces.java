package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureMineshaftPieces {
   private static final List<WeightedRandomChestContent> CHEST_CONTENT_WEIGHT_LIST = Lists.newArrayList(
      new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
      new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
      new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
      new WeightedRandomChestContent(Items.dye, EnumDyeColor.BLUE.getDyeDamage(), 4, 9, 5),
      new WeightedRandomChestContent(Items.diamond, 0, 1, 2, 3),
      new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10),
      new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
      new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1),
      new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.rail), 0, 4, 8, 1),
      new WeightedRandomChestContent(Items.melon_seeds, 0, 2, 4, 10),
      new WeightedRandomChestContent(Items.pumpkin_seeds, 0, 2, 4, 10),
      new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
      new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1)
   );

   public static void registerStructurePieces() {
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Corridor.class, "MSCorridor");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Cross.class, "MSCrossing");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Room.class, "MSRoom");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Stairs.class, "MSStairs");
   }

   private static StructureComponent func_175892_a(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing, int typeIn) {
      int i = rand.nextInt(100);
      if (i >= 80) {
         StructureBoundingBox structureBoundingBox = StructureMineshaftPieces.Cross.func_175813_a(listIn, rand, x, y, z, facing);
         if (structureBoundingBox != null) {
            return new StructureMineshaftPieces.Cross(typeIn, rand, structureBoundingBox, facing);
         }
      } else if (i >= 70) {
         StructureBoundingBox structureBoundingBox2 = StructureMineshaftPieces.Stairs.func_175812_a(listIn, rand, x, y, z, facing);
         if (structureBoundingBox2 != null) {
            return new StructureMineshaftPieces.Stairs(typeIn, rand, structureBoundingBox2, facing);
         }
      } else {
         StructureBoundingBox structureBoundingBox3 = StructureMineshaftPieces.Corridor.func_175814_a(listIn, rand, x, y, z, facing);
         if (structureBoundingBox3 != null) {
            return new StructureMineshaftPieces.Corridor(typeIn, rand, structureBoundingBox3, facing);
         }
      }

      return null;
   }

   private static StructureComponent func_175890_b(
      StructureComponent componentIn, List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing, int typeIn
   ) {
      if (typeIn > 8) {
         return null;
      }

      if (Math.abs(x - componentIn.getBoundingBox().minX) <= 80 && Math.abs(z - componentIn.getBoundingBox().minZ) <= 80) {
         StructureComponent structureComponent = func_175892_a(listIn, rand, x, y, z, facing, typeIn + 1);
         if (structureComponent != null) {
            listIn.add(structureComponent);
            structureComponent.buildComponent(componentIn, listIn, rand);
         }

         return structureComponent;
      } else {
         return null;
      }
   }

   public static class Corridor extends StructureComponent {
      private boolean hasRails;
      private boolean hasSpiders;
      private boolean spawnerPlaced;
      private int sectionCount;

      public Corridor() {
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setBoolean("hr", this.hasRails);
         tagCompound.setBoolean("sc", this.hasSpiders);
         tagCompound.setBoolean("hps", this.spawnerPlaced);
         tagCompound.setInteger("Num", this.sectionCount);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.hasRails = tagCompound.getBoolean("hr");
         this.hasSpiders = tagCompound.getBoolean("sc");
         this.spawnerPlaced = tagCompound.getBoolean("hps");
         this.sectionCount = tagCompound.getInteger("Num");
      }

      public Corridor(int typeIn, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
         super(typeIn);
         this.coordBaseMode = facing;
         this.boundingBox = structurebb;
         this.hasRails = rand.nextInt(3) == 0;
         this.hasSpiders = !this.hasRails && rand.nextInt(23) == 0;
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH) {
            this.sectionCount = structurebb.getXSize() / 5;
         } else {
            this.sectionCount = structurebb.getZSize() / 5;
         }
      }

      public static StructureBoundingBox func_175814_a(List<StructureComponent> list, Random rand, int x, int y, int z, EnumFacing facing) {
         StructureBoundingBox structureBoundingBox = new StructureBoundingBox(x, y, z, x, y + 2, z);

         int i;
         for (i = rand.nextInt(3) + 2; i > 0; i--) {
            int j = i * 5;
            switch (facing) {
               case NORTH:
                  structureBoundingBox.maxX = x + 2;
                  structureBoundingBox.minZ = z - (j - 1);
                  break;
               case SOUTH:
                  structureBoundingBox.maxX = x + 2;
                  structureBoundingBox.maxZ = z + (j - 1);
                  break;
               case WEST:
                  structureBoundingBox.minX = x - (j - 1);
                  structureBoundingBox.maxZ = z + 2;
                  break;
               case EAST:
                  structureBoundingBox.maxX = x + (j - 1);
                  structureBoundingBox.maxZ = z + 2;
            }

            if (StructureComponent.findIntersecting(list, structureBoundingBox) == null) {
               break;
            }
         }

         return i > 0 ? structureBoundingBox : null;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int i = this.getComponentType();
         int j = rand.nextInt(4);
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  if (j <= 1) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ - 1,
                        this.coordBaseMode,
                        i
                     );
                  } else if (j == 2) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        EnumFacing.WEST,
                        i
                     );
                  } else {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        EnumFacing.EAST,
                        i
                     );
                  }
                  break;
               case SOUTH:
                  if (j <= 1) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        this.coordBaseMode,
                        i
                     );
                  } else if (j == 2) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ - 3,
                        EnumFacing.WEST,
                        i
                     );
                  } else {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ - 3,
                        EnumFacing.EAST,
                        i
                     );
                  }
                  break;
               case WEST:
                  if (j <= 1) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        this.coordBaseMode,
                        i
                     );
                  } else if (j == 2) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ - 1,
                        EnumFacing.NORTH,
                        i
                     );
                  } else {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        EnumFacing.SOUTH,
                        i
                     );
                  }
                  break;
               case EAST:
                  if (j <= 1) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        this.coordBaseMode,
                        i
                     );
                  } else if (j == 2) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX - 3,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ - 1,
                        EnumFacing.NORTH,
                        i
                     );
                  } else {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX - 3,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        EnumFacing.SOUTH,
                        i
                     );
                  }
            }
         }

         if (i < 8) {
            if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH) {
               for (int k = this.boundingBox.minX + 3; k + 3 <= this.boundingBox.maxX; k += 5) {
                  int l = rand.nextInt(5);
                  if (l == 0) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn, listIn, rand, k, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i + 1
                     );
                  } else if (l == 1) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn, listIn, rand, k, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i + 1
                     );
                  }
               }
            } else {
               for (int m = this.boundingBox.minZ + 3; m + 3 <= this.boundingBox.maxZ; m += 5) {
                  int n = rand.nextInt(5);
                  if (n == 0) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, m, EnumFacing.WEST, i + 1
                     );
                  } else if (n == 1) {
                     StructureMineshaftPieces.func_175890_b(
                        componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, m, EnumFacing.EAST, i + 1
                     );
                  }
               }
            }
         }
      }

      @Override
      public boolean generateChestContents(
         World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, List<WeightedRandomChestContent> listIn, int maxIn
      ) {
         BlockPos blockPos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
         if (boundingBoxIn.isVecInside(blockPos) && worldIn.getBlockState(blockPos).getBlock().getMaterial() == Material.air) {
            int i = rand.nextBoolean() ? 1 : 0;
            worldIn.setBlockState(blockPos, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, i)), 2);
            EntityMinecartChest entityMinecartChest = new EntityMinecartChest(worldIn, blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F);
            WeightedRandomChestContent.generateChestContents(rand, listIn, entityMinecartChest, maxIn);
            worldIn.spawnEntityInWorld(entityMinecartChest);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         int i = this.sectionCount * 5 - 1;
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 1, i, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.func_175805_a(
            worldIn, structureBoundingBoxIn, randomIn, 0.8F, 0, 2, 0, 2, 2, i, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false
         );
         if (this.hasSpiders) {
            this.func_175805_a(
               worldIn, structureBoundingBoxIn, randomIn, 0.6F, 0, 0, 0, 2, 1, i, Blocks.web.getDefaultState(), Blocks.air.getDefaultState(), false
            );
         }

         for (int j = 0; j < this.sectionCount; j++) {
            int k = 2 + j * 5;
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 0, 0, k, 0, 1, k, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 2, 0, k, 2, 1, k, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false
            );
            if (randomIn.nextInt(4) == 0) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 0, 2, k, 0, 2, k, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 2, 2, k, 2, 2, k, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false
               );
            } else {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 0, 2, k, 2, 2, k, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false
               );
            }

            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, k - 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, k - 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, k + 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, k + 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, k - 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, k - 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, k + 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, k + 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 1, 2, k - 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 1, 2, k + 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
            if (randomIn.nextInt(100) == 0) {
               this.generateChestContents(
                  worldIn,
                  structureBoundingBoxIn,
                  randomIn,
                  2,
                  0,
                  k - 1,
                  WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, Items.enchanted_book.getRandom(randomIn)),
                  3 + randomIn.nextInt(4)
               );
            }

            if (randomIn.nextInt(100) == 0) {
               this.generateChestContents(
                  worldIn,
                  structureBoundingBoxIn,
                  randomIn,
                  0,
                  0,
                  k + 1,
                  WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, Items.enchanted_book.getRandom(randomIn)),
                  3 + randomIn.nextInt(4)
               );
            }

            if (this.hasSpiders && !this.spawnerPlaced) {
               int l = this.getYWithOffset(0);
               int m = k - 1 + randomIn.nextInt(3);
               int n = this.getXWithOffset(1, m);
               m = this.getZWithOffset(1, m);
               BlockPos blockPos = new BlockPos(n, l, m);
               if (structureBoundingBoxIn.isVecInside(blockPos)) {
                  this.spawnerPlaced = true;
                  worldIn.setBlockState(blockPos, Blocks.mob_spawner.getDefaultState(), 2);
                  TileEntity tileEntity = worldIn.getTileEntity(blockPos);
                  if (tileEntity instanceof TileEntityMobSpawner) {
                     ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic().setEntityName("CaveSpider");
                  }
               }
            }
         }

         for (int i2 = 0; i2 <= 2; i2++) {
            for (int i3 = 0; i3 <= i; i3++) {
               byte b = -1;
               IBlockState iBlockState = this.getBlockStateFromPos(worldIn, i2, b, i3, structureBoundingBoxIn);
               if (iBlockState.getBlock().getMaterial() == Material.air) {
                  byte b2 = -1;
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), i2, b2, i3, structureBoundingBoxIn);
               }
            }
         }

         if (this.hasRails) {
            for (int i4 = 0; i4 <= i; i4++) {
               IBlockState iBlockState2 = this.getBlockStateFromPos(worldIn, 1, -1, i4, structureBoundingBoxIn);
               if (iBlockState2.getBlock().getMaterial() != Material.air && iBlockState2.getBlock().isFullBlock()) {
                  this.randomlyPlaceBlock(
                     worldIn, structureBoundingBoxIn, randomIn, 0.7F, 1, 0, i4, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, 0))
                  );
               }
            }
         }

         return true;
      }
   }

   public static class Cross extends StructureComponent {
      private EnumFacing corridorDirection;
      private boolean isMultipleFloors;

      public Cross() {
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setBoolean("tf", this.isMultipleFloors);
         tagCompound.setInteger("D", this.corridorDirection.getHorizontalIndex());
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.isMultipleFloors = tagCompound.getBoolean("tf");
         this.corridorDirection = EnumFacing.getHorizontal(tagCompound.getInteger("D"));
      }

      public Cross(int typeIn, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
         super(typeIn);
         this.corridorDirection = facing;
         this.boundingBox = structurebb;
         this.isMultipleFloors = structurebb.getYSize() > 3;
      }

      public static StructureBoundingBox func_175813_a(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing) {
         StructureBoundingBox structureBoundingBox = new StructureBoundingBox(x, y, z, x, y + 2, z);
         if (rand.nextInt(4) == 0) {
            structureBoundingBox.maxY += 4;
         }

         switch (facing) {
            case NORTH:
               structureBoundingBox.minX = x - 1;
               structureBoundingBox.maxX = x + 3;
               structureBoundingBox.minZ = z - 4;
               break;
            case SOUTH:
               structureBoundingBox.minX = x - 1;
               structureBoundingBox.maxX = x + 3;
               structureBoundingBox.maxZ = z + 4;
               break;
            case WEST:
               structureBoundingBox.minX = x - 4;
               structureBoundingBox.minZ = z - 1;
               structureBoundingBox.maxZ = z + 3;
               break;
            case EAST:
               structureBoundingBox.maxX = x + 4;
               structureBoundingBox.minZ = z - 1;
               structureBoundingBox.maxZ = z + 3;
         }

         return StructureComponent.findIntersecting(listIn, structureBoundingBox) != null ? null : structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int i = this.getComponentType();
         switch (this.corridorDirection) {
            case NORTH:
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i
               );
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i
               );
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i
               );
               break;
            case SOUTH:
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i
               );
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i
               );
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i
               );
               break;
            case WEST:
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i
               );
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i
               );
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i
               );
               break;
            case EAST:
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i
               );
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i
               );
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i
               );
         }

         if (this.isMultipleFloors) {
            if (rand.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, i
               );
            }

            if (rand.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.WEST, i
               );
            }

            if (rand.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.EAST, i
               );
            }

            if (rand.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i
               );
            }
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         if (this.isMultipleFloors) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.minY,
               this.boundingBox.minZ,
               this.boundingBox.maxX - 1,
               this.boundingBox.minY + 3 - 1,
               this.boundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX,
               this.boundingBox.minY,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX,
               this.boundingBox.minY + 3 - 1,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.maxY - 2,
               this.boundingBox.minZ,
               this.boundingBox.maxX - 1,
               this.boundingBox.maxY,
               this.boundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX,
               this.boundingBox.maxY - 2,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX,
               this.boundingBox.maxY,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.minY + 3,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX - 1,
               this.boundingBox.minY + 3,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         } else {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.minY,
               this.boundingBox.minZ,
               this.boundingBox.maxX - 1,
               this.boundingBox.maxY,
               this.boundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX,
               this.boundingBox.minY,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX,
               this.boundingBox.maxY,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX + 1,
            this.boundingBox.minY,
            this.boundingBox.minZ + 1,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY,
            this.boundingBox.minZ + 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX + 1,
            this.boundingBox.minY,
            this.boundingBox.maxZ - 1,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY,
            this.boundingBox.maxZ - 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.maxX - 1,
            this.boundingBox.minY,
            this.boundingBox.minZ + 1,
            this.boundingBox.maxX - 1,
            this.boundingBox.maxY,
            this.boundingBox.minZ + 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.maxX - 1,
            this.boundingBox.minY,
            this.boundingBox.maxZ - 1,
            this.boundingBox.maxX - 1,
            this.boundingBox.maxY,
            this.boundingBox.maxZ - 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );

         for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; i++) {
            for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; j++) {
               if (this.getBlockStateFromPos(worldIn, i, this.boundingBox.minY - 1, j, structureBoundingBoxIn).getBlock().getMaterial() == Material.air) {
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), i, this.boundingBox.minY - 1, j, structureBoundingBoxIn);
               }
            }
         }

         return true;
      }
   }

   public static class Room extends StructureComponent {
      private List<StructureBoundingBox> roomsLinkedToTheRoom = Lists.newLinkedList();

      public Room() {
      }

      public Room(int typeIn, Random rand, int x, int z) {
         super(typeIn);
         this.boundingBox = new StructureBoundingBox(x, 50, z, x + 7 + rand.nextInt(6), 54 + rand.nextInt(6), z + 7 + rand.nextInt(6));
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int i = this.getComponentType();
         int j = this.boundingBox.getYSize() - 3 - 1;
         if (j <= 0) {
            j = 1;
         }

         int k = 0;

         while (k < this.boundingBox.getXSize()) {
            k += rand.nextInt(this.boundingBox.getXSize());
            if (k + 3 > this.boundingBox.getXSize()) {
               break;
            }

            StructureComponent structureComponent = StructureMineshaftPieces.func_175890_b(
               componentIn,
               listIn,
               rand,
               this.boundingBox.minX + k,
               this.boundingBox.minY + rand.nextInt(j) + 1,
               this.boundingBox.minZ - 1,
               EnumFacing.NORTH,
               i
            );
            if (structureComponent != null) {
               StructureBoundingBox structureBoundingBox = structureComponent.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(structureBoundingBox.minX, structureBoundingBox.minY, this.boundingBox.minZ, structureBoundingBox.maxX, structureBoundingBox.maxY, this.boundingBox.minZ + 1));
            }

            k += 4;
         }

         k = 0;

         while (k < this.boundingBox.getXSize()) {
            k += rand.nextInt(this.boundingBox.getXSize());
            if (k + 3 > this.boundingBox.getXSize()) {
               break;
            }

            StructureComponent structureComponent2 = StructureMineshaftPieces.func_175890_b(
               componentIn,
               listIn,
               rand,
               this.boundingBox.minX + k,
               this.boundingBox.minY + rand.nextInt(j) + 1,
               this.boundingBox.maxZ + 1,
               EnumFacing.SOUTH,
               i
            );
            if (structureComponent2 != null) {
               StructureBoundingBox structureBoundingBox2 = structureComponent2.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(structureBoundingBox2.minX, structureBoundingBox2.minY, this.boundingBox.maxZ - 1, structureBoundingBox2.maxX, structureBoundingBox2.maxY, this.boundingBox.maxZ));
            }

            k += 4;
         }

         k = 0;

         while (k < this.boundingBox.getZSize()) {
            k += rand.nextInt(this.boundingBox.getZSize());
            if (k + 3 > this.boundingBox.getZSize()) {
               break;
            }

            StructureComponent structureComponent3 = StructureMineshaftPieces.func_175890_b(
               componentIn,
               listIn,
               rand,
               this.boundingBox.minX - 1,
               this.boundingBox.minY + rand.nextInt(j) + 1,
               this.boundingBox.minZ + k,
               EnumFacing.WEST,
               i
            );
            if (structureComponent3 != null) {
               StructureBoundingBox structureBoundingBox3 = structureComponent3.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(this.boundingBox.minX, structureBoundingBox3.minY, structureBoundingBox3.minZ, this.boundingBox.minX + 1, structureBoundingBox3.maxY, structureBoundingBox3.maxZ));
            }

            k += 4;
         }

         k = 0;

         while (k < this.boundingBox.getZSize()) {
            k += rand.nextInt(this.boundingBox.getZSize());
            if (k + 3 > this.boundingBox.getZSize()) {
               break;
            }

            StructureComponent structureComponent4 = StructureMineshaftPieces.func_175890_b(
               componentIn,
               listIn,
               rand,
               this.boundingBox.maxX + 1,
               this.boundingBox.minY + rand.nextInt(j) + 1,
               this.boundingBox.minZ + k,
               EnumFacing.EAST,
               i
            );
            if (structureComponent4 != null) {
               StructureBoundingBox structureBoundingBox4 = structureComponent4.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(this.boundingBox.maxX - 1, structureBoundingBox4.minY, structureBoundingBox4.minZ, this.boundingBox.maxX, structureBoundingBox4.maxY, structureBoundingBox4.maxZ));
            }

            k += 4;
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX,
            this.boundingBox.minY,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            this.boundingBox.minY,
            this.boundingBox.maxZ,
            Blocks.dirt.getDefaultState(),
            Blocks.air.getDefaultState(),
            true
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX,
            this.boundingBox.minY + 1,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY),
            this.boundingBox.maxZ,
            Blocks.air.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );

         for (StructureBoundingBox structureBoundingBox : this.roomsLinkedToTheRoom) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               structureBoundingBox.minX,
               structureBoundingBox.maxY - 2,
               structureBoundingBox.minZ,
               structureBoundingBox.maxX,
               structureBoundingBox.maxY,
               structureBoundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         this.randomlyRareFillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX,
            this.boundingBox.minY + 4,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            this.boundingBox.maxY,
            this.boundingBox.maxZ,
            Blocks.air.getDefaultState(),
            false
         );
         return true;
      }

      @Override
      public void func_181138_a(int i, int j, int k) {
         super.func_181138_a(i, j, k);

         for (StructureBoundingBox structureBoundingBox : this.roomsLinkedToTheRoom) {
            structureBoundingBox.offset(i, j, k);
         }
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         NBTTagList nbtTagList = new NBTTagList();

         for (StructureBoundingBox structureBoundingBox : this.roomsLinkedToTheRoom) {
            nbtTagList.appendTag(structureBoundingBox.toNBTTagIntArray());
         }

         tagCompound.setTag("Entrances", nbtTagList);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         NBTTagList nbtTagList = tagCompound.getTagList("Entrances", 11);

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            this.roomsLinkedToTheRoom.add(new StructureBoundingBox(nbtTagList.getIntArrayAt(i)));
         }
      }
   }

   public static class Stairs extends StructureComponent {
      public Stairs() {
      }

      public Stairs(int typeIn, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
         super(typeIn);
         this.coordBaseMode = facing;
         this.boundingBox = structurebb;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
      }

      public static StructureBoundingBox func_175812_a(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing) {
         StructureBoundingBox structureBoundingBox = new StructureBoundingBox(x, y - 5, z, x, y + 2, z);
         switch (facing) {
            case NORTH:
               structureBoundingBox.maxX = x + 2;
               structureBoundingBox.minZ = z - 8;
               break;
            case SOUTH:
               structureBoundingBox.maxX = x + 2;
               structureBoundingBox.maxZ = z + 8;
               break;
            case WEST:
               structureBoundingBox.minX = x - 8;
               structureBoundingBox.maxZ = z + 2;
               break;
            case EAST:
               structureBoundingBox.maxX = x + 8;
               structureBoundingBox.maxZ = z + 2;
         }

         return StructureComponent.findIntersecting(listIn, structureBoundingBox) != null ? null : structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int i = this.getComponentType();
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  StructureMineshaftPieces.func_175890_b(
                     componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i
                  );
                  break;
               case SOUTH:
                  StructureMineshaftPieces.func_175890_b(
                     componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i
                  );
                  break;
               case WEST:
                  StructureMineshaftPieces.func_175890_b(
                     componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, i
                  );
                  break;
               case EAST:
                  StructureMineshaftPieces.func_175890_b(
                     componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, i
                  );
            }
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 2, 7, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 7, 2, 2, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);

         for (int i = 0; i < 5; i++) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               5 - i - (i < 4 ? 1 : 0),
               2 + i,
               2,
               7 - i,
               2 + i,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         return true;
      }
   }
}
