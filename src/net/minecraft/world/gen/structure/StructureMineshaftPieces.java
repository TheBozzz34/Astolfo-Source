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
   private static final long a = 89220285574454L;

   public static void registerStructurePieces() {
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Corridor.class, "MSCorridor");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Cross.class, "MSCrossing");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Room.class, "MSRoom");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Stairs.class, "MSStairs");
   }

   private static StructureComponent func_175892_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
      int var10 = var1.nextInt(100);
      if (var10 >= 80) {
         StructureBoundingBox var11 = StructureMineshaftPieces.Cross.func_175813_a(var0, var1, var2, var3, var4, var5);
         if (var11 != null) {
            return new StructureMineshaftPieces.Cross(var6, var1, var11, var5);
         }
      } else if (var10 >= 70) {
         StructureBoundingBox var12 = StructureMineshaftPieces.Stairs.func_175812_a(var0, var1, var2, var3, var4, var5);
         if (var12 != null) {
            return new StructureMineshaftPieces.Stairs(var6, var1, var12, var5);
         }
      } else {
         StructureBoundingBox var13 = StructureMineshaftPieces.Corridor.func_175814_a(var0, var1, var2, var3, var4, var5);
         if (var13 != null) {
            return new StructureMineshaftPieces.Corridor(var6, var1, var13, var5);
         }
      }

      return null;
   }

   private static StructureComponent func_175890_b(
      StructureComponent var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7
   ) {
      if (var7 > 8) {
         return null;
      }

      if (Math.abs(var3 - var0.getBoundingBox().minX) <= 80 && Math.abs(var5 - var0.getBoundingBox().minZ) <= 80) {
         StructureComponent var11 = func_175892_a(var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var11 != null) {
            var1.add(var11);
            var11.buildComponent(var0, var1, var2);
         }

         return var11;
      } else {
         return null;
      }
   }

   public static class Corridor extends StructureComponent {
      private boolean hasRails;
      private boolean hasSpiders;
      private boolean spawnerPlaced;
      private int sectionCount;
      private static final long b = 13343761484318L;

      public Corridor() {
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         var1.setBoolean("hr", this.hasRails);
         var1.setBoolean("sc", this.hasSpiders);
         var1.setBoolean("hps", this.spawnerPlaced);
         var1.setInteger("Num", this.sectionCount);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         this.hasRails = var1.getBoolean("hr");
         this.hasSpiders = var1.getBoolean("sc");
         this.spawnerPlaced = var1.getBoolean("hps");
         this.sectionCount = var1.getInteger("Num");
      }

      public Corridor(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.boundingBox = var3;
         this.hasRails = var2.nextInt(3) == 0;
         this.hasSpiders = !this.hasRails && var2.nextInt(23) == 0;
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH) {
            this.sectionCount = var3.G() / 5;
         } else {
            this.sectionCount = var3.S() / 5;
         }
      }

      public static StructureBoundingBox func_175814_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         StructureBoundingBox var9 = new StructureBoundingBox(var2, var3, var4, var2, var3 + 2, var4);

         int var10;
         for (var10 = var1.nextInt(3) + 2; var10 > 0; var10--) {
            int var11 = var10 * 5;
            switch (var5) {
               case NORTH:
                  var9.maxX = var2 + 2;
                  var9.minZ = var4 - (var11 - 1);
                  break;
               case SOUTH:
                  var9.maxX = var2 + 2;
                  var9.maxZ = var4 + (var11 - 1);
                  break;
               case WEST:
                  var9.minX = var2 - (var11 - 1);
                  var9.maxZ = var4 + 2;
                  break;
               case EAST:
                  var9.maxX = var2 + (var11 - 1);
                  var9.maxZ = var4 + 2;
            }

            if (StructureComponent.findIntersecting(var0, var9) == null) {
               break;
            }
         }

         return var10 > 0 ? var9 : null;
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         int var7 = this.getComponentType();
         int var8 = var3.nextInt(4);
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  if (var8 <= 1) {
                     StructureMineshaftPieces.func_175890_b(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + var3.nextInt(3),
                        this.boundingBox.minZ - 1,
                        this.coordBaseMode,
                        var7
                     );
                  } else if (var8 == 2) {
                     StructureMineshaftPieces.func_175890_b(
                        var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + var3.nextInt(3), this.boundingBox.minZ, EnumFacing.WEST, var7
                     );
                  } else {
                     StructureMineshaftPieces.func_175890_b(
                        var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + var3.nextInt(3), this.boundingBox.minZ, EnumFacing.EAST, var7
                     );
                  }
                  break;
               case SOUTH:
                  if (var8 <= 1) {
                     StructureMineshaftPieces.func_175890_b(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + var3.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        this.coordBaseMode,
                        var7
                     );
                  } else if (var8 == 2) {
                     StructureMineshaftPieces.func_175890_b(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + var3.nextInt(3),
                        this.boundingBox.maxZ - 3,
                        EnumFacing.WEST,
                        var7
                     );
                  } else {
                     StructureMineshaftPieces.func_175890_b(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + var3.nextInt(3),
                        this.boundingBox.maxZ - 3,
                        EnumFacing.EAST,
                        var7
                     );
                  }
                  break;
               case WEST:
                  if (var8 <= 1) {
                     StructureMineshaftPieces.func_175890_b(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + var3.nextInt(3),
                        this.boundingBox.minZ,
                        this.coordBaseMode,
                        var7
                     );
                  } else if (var8 == 2) {
                     StructureMineshaftPieces.func_175890_b(
                        var1, var2, var3, this.boundingBox.minX, this.boundingBox.minY - 1 + var3.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, var7
                     );
                  } else {
                     StructureMineshaftPieces.func_175890_b(
                        var1, var2, var3, this.boundingBox.minX, this.boundingBox.minY - 1 + var3.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var7
                     );
                  }
                  break;
               case EAST:
                  if (var8 <= 1) {
                     StructureMineshaftPieces.func_175890_b(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + var3.nextInt(3),
                        this.boundingBox.minZ,
                        this.coordBaseMode,
                        var7
                     );
                  } else if (var8 == 2) {
                     StructureMineshaftPieces.func_175890_b(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.maxX - 3,
                        this.boundingBox.minY - 1 + var3.nextInt(3),
                        this.boundingBox.minZ - 1,
                        EnumFacing.NORTH,
                        var7
                     );
                  } else {
                     StructureMineshaftPieces.func_175890_b(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.maxX - 3,
                        this.boundingBox.minY - 1 + var3.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        EnumFacing.SOUTH,
                        var7
                     );
                  }
            }
         }

         if (var7 < 8) {
            if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH) {
               for (int var11 = this.boundingBox.minX + 3; var11 + 3 <= this.boundingBox.maxX; var11 += 5) {
                  int var12 = var3.nextInt(5);
                  if (var12 == 0) {
                     StructureMineshaftPieces.func_175890_b(
                        var1, var2, var3, var11, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, var7 + 1
                     );
                  } else if (var12 == 1) {
                     StructureMineshaftPieces.func_175890_b(
                        var1, var2, var3, var11, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var7 + 1
                     );
                  }
               }
            } else {
               for (int var9 = this.boundingBox.minZ + 3; var9 + 3 <= this.boundingBox.maxZ; var9 += 5) {
                  int var10 = var3.nextInt(5);
                  if (var10 == 0) {
                     StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY, var9, EnumFacing.WEST, var7 + 1);
                  } else if (var10 == 1) {
                     StructureMineshaftPieces.func_175890_b(var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY, var9, EnumFacing.EAST, var7 + 1);
                  }
               }
            }
         }
      }

      @Override
      public boolean generateChestContents(
         World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, List<WeightedRandomChestContent> var7, int var8
      ) {
         BlockPos var12 = new BlockPos(this.getXWithOffset(var4, var6), this.getYWithOffset(var5), this.getZWithOffset(var4, var6));
         if (var2.isVecInside(var12) && var1.getBlockState(var12).getBlock().getMaterial() == Material.air) {
            int var13 = var3.nextBoolean() ? 1 : 0;
            var1.setBlockState(var12, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, var13)), 2);
            EntityMinecartChest var14 = new EntityMinecartChest(var1, var12.getX() + 0.5F, var12.getY() + 0.5F, var12.getZ() + 0.5F);
            WeightedRandomChestContent.generateChestContents(var3, var7, var14, var8);
            var1.spawnEntityInWorld(var14);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         int var11 = this.sectionCount * 5 - 1;
         this.fillWithBlocks(var1, var3, 0, 0, 0, 2, 1, var11, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.func_175805_a(var1, var3, var2, 0.8F, 0, 2, 0, 2, 2, var11, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         if (this.hasSpiders) {
            this.func_175805_a(var1, var3, var2, 0.6F, 0, 0, 0, 2, 1, var11, Blocks.web.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         for (int var12 = 0; var12 < this.sectionCount; var12++) {
            int var13 = 2 + var12 * 5;
            this.fillWithBlocks(var1, var3, 0, 0, var13, 0, 1, var13, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false);
            this.fillWithBlocks(var1, var3, 2, 0, var13, 2, 1, var13, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false);
            if (var2.nextInt(4) == 0) {
               this.fillWithBlocks(var1, var3, 0, 2, var13, 0, 2, var13, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
               this.fillWithBlocks(var1, var3, 2, 2, var13, 2, 2, var13, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
            } else {
               this.fillWithBlocks(var1, var3, 0, 2, var13, 2, 2, var13, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
            }

            this.randomlyPlaceBlock(var1, var3, var2, 0.1F, 0, 2, var13 - 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(var1, var3, var2, 0.1F, 2, 2, var13 - 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(var1, var3, var2, 0.1F, 0, 2, var13 + 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(var1, var3, var2, 0.1F, 2, 2, var13 + 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(var1, var3, var2, 0.05F, 0, 2, var13 - 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(var1, var3, var2, 0.05F, 2, 2, var13 - 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(var1, var3, var2, 0.05F, 0, 2, var13 + 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(var1, var3, var2, 0.05F, 2, 2, var13 + 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(var1, var3, var2, 0.05F, 1, 2, var13 - 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
            this.randomlyPlaceBlock(var1, var3, var2, 0.05F, 1, 2, var13 + 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
            if (var2.nextInt(100) == 0) {
               this.generateChestContents(
                  var1,
                  var3,
                  var2,
                  2,
                  0,
                  var13 - 1,
                  WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, Items.enchanted_book.getRandom(var2)),
                  3 + var2.nextInt(4)
               );
            }

            if (var2.nextInt(100) == 0) {
               this.generateChestContents(
                  var1,
                  var3,
                  var2,
                  0,
                  0,
                  var13 + 1,
                  WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, Items.enchanted_book.getRandom(var2)),
                  3 + var2.nextInt(4)
               );
            }

            if (this.hasSpiders && !this.spawnerPlaced) {
               int var14 = this.getYWithOffset(0);
               int var15 = var13 - 1 + var2.nextInt(3);
               int var16 = this.getXWithOffset(1, var15);
               var15 = this.getZWithOffset(1, var15);
               BlockPos var17 = new BlockPos(var16, var14, var15);
               if (var3.isVecInside(var17)) {
                  this.spawnerPlaced = true;
                  var1.setBlockState(var17, Blocks.mob_spawner.getDefaultState(), 2);
                  TileEntity var18 = var1.getTileEntity(var17);
                  if (var18 instanceof TileEntityMobSpawner) {
                     ((TileEntityMobSpawner)var18).getSpawnerBaseLogic().setEntityName("CaveSpider");
                  }
               }
            }
         }

         for (int var19 = 0; var19 <= 2; var19++) {
            for (int var21 = 0; var21 <= var11; var21++) {
               byte var23 = -1;
               IBlockState var25 = this.getBlockStateFromPos(var1, var19, var23, var21, var3);
               if (var25.getBlock().getMaterial() == Material.air) {
                  byte var26 = -1;
                  this.setBlockState(var1, Blocks.planks.getDefaultState(), var19, var26, var21, var3);
               }
            }
         }

         if (this.hasRails) {
            for (int var20 = 0; var20 <= var11; var20++) {
               IBlockState var22 = this.getBlockStateFromPos(var1, 1, -1, var20, var3);
               if (var22.getBlock().getMaterial() != Material.air && var22.getBlock().isFullBlock()) {
                  this.randomlyPlaceBlock(var1, var3, var2, 0.7F, 1, 0, var20, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, 0)));
               }
            }
         }

         return true;
      }
   }

   public static class Cross extends StructureComponent {
      private EnumFacing corridorDirection;
      private boolean isMultipleFloors;
      private static final long b = 56487734882121L;

      public Cross() {
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         var1.setBoolean("tf", this.isMultipleFloors);
         var1.setInteger("D", this.corridorDirection.getHorizontalIndex());
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         this.isMultipleFloors = var1.getBoolean("tf");
         this.corridorDirection = EnumFacing.getHorizontal(var1.getInteger("D"));
      }

      public Cross(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.corridorDirection = var4;
         this.boundingBox = var3;
         this.isMultipleFloors = var3.getYSize() > 3;
      }

      public static StructureBoundingBox func_175813_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         StructureBoundingBox var9 = new StructureBoundingBox(var2, var3, var4, var2, var3 + 2, var4);
         if (var1.nextInt(4) == 0) {
            var9.maxY += 4;
         }

         switch (var5) {
            case NORTH:
               var9.minX = var2 - 1;
               var9.maxX = var2 + 3;
               var9.minZ = var4 - 4;
               break;
            case SOUTH:
               var9.minX = var2 - 1;
               var9.maxX = var2 + 3;
               var9.maxZ = var4 + 4;
               break;
            case WEST:
               var9.minX = var2 - 4;
               var9.minZ = var4 - 1;
               var9.maxZ = var4 + 3;
               break;
            case EAST:
               var9.maxX = var2 + 4;
               var9.minZ = var4 - 1;
               var9.maxZ = var4 + 3;
         }

         return StructureComponent.findIntersecting(var0, var9) != null ? null : var9;
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         int var7 = this.getComponentType();
         switch (this.corridorDirection) {
            case NORTH:
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, var7
               );
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, var7
               );
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, var7
               );
               break;
            case SOUTH:
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var7
               );
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, var7
               );
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, var7
               );
               break;
            case WEST:
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, var7
               );
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var7
               );
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, var7
               );
               break;
            case EAST:
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, var7
               );
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var7
               );
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, var7
               );
         }

         if (this.isMultipleFloors) {
            if (var3.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, var7
               );
            }

            if (var3.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.WEST, var7
               );
            }

            if (var3.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.EAST, var7
               );
            }

            if (var3.nextBoolean()) {
               StructureMineshaftPieces.func_175890_b(
                  var1, var2, var3, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var7
               );
            }
         }
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         if (this.isMultipleFloors) {
            this.fillWithBlocks(
               var1,
               var3,
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
               var1,
               var3,
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
               var1,
               var3,
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
               var1,
               var3,
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
               var1,
               var3,
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
               var1,
               var3,
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
               var1,
               var3,
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
            var1,
            var3,
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
            var1,
            var3,
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
            var1,
            var3,
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
            var1,
            var3,
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

         for (int var7 = this.boundingBox.minX; var7 <= this.boundingBox.maxX; var7++) {
            for (int var8 = this.boundingBox.minZ; var8 <= this.boundingBox.maxZ; var8++) {
               if (this.getBlockStateFromPos(var1, var7, this.boundingBox.minY - 1, var8, var3).getBlock().getMaterial() == Material.air) {
                  this.setBlockState(var1, Blocks.planks.getDefaultState(), var7, this.boundingBox.minY - 1, var8, var3);
               }
            }
         }

         return true;
      }
   }

   public static class Room extends StructureComponent {
      private List<StructureBoundingBox> roomsLinkedToTheRoom = Lists.newLinkedList();
      private static final long b = 124954989997125L;

      public Room() {
      }

      public Room(int var1, Random var2, int var3, int var4) {
         super(var1);
         this.boundingBox = new StructureBoundingBox(var3, 50, var4, var3 + 7 + var2.nextInt(6), 54 + var2.nextInt(6), var4 + 7 + var2.nextInt(6));
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         int var7 = this.getComponentType();
         int var8 = this.boundingBox.getYSize() - 3 - 1;
         if (var8 <= 0) {
            var8 = 1;
         }

         int var9 = 0;

         while (var9 < this.boundingBox.G()) {
            var9 += var3.nextInt(this.boundingBox.G());
            if (var9 + 3 > this.boundingBox.G()) {
               break;
            }

            StructureComponent var10 = StructureMineshaftPieces.func_175890_b(
               var1,
               var2,
               var3,
               this.boundingBox.minX + var9,
               this.boundingBox.minY + var3.nextInt(var8) + 1,
               this.boundingBox.minZ - 1,
               EnumFacing.NORTH,
               var7
            );
            if (var10 != null) {
               StructureBoundingBox var11 = var10.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(var11.minX, var11.minY, this.boundingBox.minZ, var11.maxX, var11.maxY, this.boundingBox.minZ + 1));
            }

            var9 += 4;
         }

         var9 = 0;

         while (var9 < this.boundingBox.G()) {
            var9 += var3.nextInt(this.boundingBox.G());
            if (var9 + 3 > this.boundingBox.G()) {
               break;
            }

            StructureComponent var19 = StructureMineshaftPieces.func_175890_b(
               var1,
               var2,
               var3,
               this.boundingBox.minX + var9,
               this.boundingBox.minY + var3.nextInt(var8) + 1,
               this.boundingBox.maxZ + 1,
               EnumFacing.SOUTH,
               var7
            );
            if (var19 != null) {
               StructureBoundingBox var22 = var19.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(var22.minX, var22.minY, this.boundingBox.maxZ - 1, var22.maxX, var22.maxY, this.boundingBox.maxZ));
            }

            var9 += 4;
         }

         var9 = 0;

         while (var9 < this.boundingBox.S()) {
            var9 += var3.nextInt(this.boundingBox.S());
            if (var9 + 3 > this.boundingBox.S()) {
               break;
            }

            StructureComponent var20 = StructureMineshaftPieces.func_175890_b(
               var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY + var3.nextInt(var8) + 1, this.boundingBox.minZ + var9, EnumFacing.WEST, var7
            );
            if (var20 != null) {
               StructureBoundingBox var23 = var20.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(this.boundingBox.minX, var23.minY, var23.minZ, this.boundingBox.minX + 1, var23.maxY, var23.maxZ));
            }

            var9 += 4;
         }

         var9 = 0;

         while (var9 < this.boundingBox.S()) {
            var9 += var3.nextInt(this.boundingBox.S());
            if (var9 + 3 > this.boundingBox.S()) {
               break;
            }

            StructureComponent var21 = StructureMineshaftPieces.func_175890_b(
               var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY + var3.nextInt(var8) + 1, this.boundingBox.minZ + var9, EnumFacing.EAST, var7
            );
            if (var21 != null) {
               StructureBoundingBox var24 = var21.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(this.boundingBox.maxX - 1, var24.minY, var24.minZ, this.boundingBox.maxX, var24.maxY, var24.maxZ));
            }

            var9 += 4;
         }
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithBlocks(
            var1,
            var3,
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
            var1,
            var3,
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

         for (StructureBoundingBox var8 : this.roomsLinkedToTheRoom) {
            this.fillWithBlocks(
               var1,
               var3,
               var8.minX,
               var8.maxY - 2,
               var8.minZ,
               var8.maxX,
               var8.maxY,
               var8.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         this.randomlyRareFillWithBlocks(
            var1,
            var3,
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
      public void func_181138_a(int var1, int var2, int var3) {
         super.func_181138_a(var1, var2, var3);

         for (StructureBoundingBox var8 : this.roomsLinkedToTheRoom) {
            var8.offset(var1, var2, var3);
         }
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         NBTTagList var5 = new NBTTagList();

         for (StructureBoundingBox var7 : this.roomsLinkedToTheRoom) {
            var5.appendTag(var7.toNBTTagIntArray());
         }

         var1.setTag("Entrances", var5);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         NBTTagList var5 = var1.getTagList("Entrances", 11);

         for (int var6 = 0; var6 < var5.tagCount(); var6++) {
            this.roomsLinkedToTheRoom.add(new StructureBoundingBox(var5.getIntArrayAt(var6)));
         }
      }
   }

   public static class Stairs extends StructureComponent {
      private static final long b = 68995988797099L;

      public Stairs() {
      }

      public Stairs(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.boundingBox = var3;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
      }

      public static StructureBoundingBox func_175812_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         StructureBoundingBox var9 = new StructureBoundingBox(var2, var3 - 5, var4, var2, var3 + 2, var4);
         switch (var5) {
            case NORTH:
               var9.maxX = var2 + 2;
               var9.minZ = var4 - 8;
               break;
            case SOUTH:
               var9.maxX = var2 + 2;
               var9.maxZ = var4 + 8;
               break;
            case WEST:
               var9.minX = var2 - 8;
               var9.maxZ = var4 + 2;
               break;
            case EAST:
               var9.maxX = var2 + 8;
               var9.maxZ = var4 + 2;
         }

         return StructureComponent.findIntersecting(var0, var9) != null ? null : var9;
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         int var7 = this.getComponentType();
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  StructureMineshaftPieces.func_175890_b(
                     var1, var2, var3, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, var7
                  );
                  break;
               case SOUTH:
                  StructureMineshaftPieces.func_175890_b(
                     var1, var2, var3, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var7
                  );
                  break;
               case WEST:
                  StructureMineshaftPieces.func_175890_b(
                     var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, var7
                  );
                  break;
               case EAST:
                  StructureMineshaftPieces.func_175890_b(
                     var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, var7
                  );
            }
         }
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithBlocks(var1, var3, 0, 5, 0, 2, 7, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 0, 0, 7, 2, 2, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);

         for (int var7 = 0; var7 < 5; var7++) {
            this.fillWithBlocks(
               var1, var3, 0, 5 - var7 - (var7 < 4 ? 1 : 0), 2 + var7, 2, 7 - var7, 2 + var7, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false
            );
         }

         return true;
      }
   }
}
