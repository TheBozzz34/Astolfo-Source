package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public abstract class StructureComponent {
   public StructureBoundingBox boundingBox;
   public EnumFacing coordBaseMode;
   public int componentType;
   private static final long f = 114162865585155L;
   private static final long K = 2461909613805568255L;

   public StructureComponent() {
   }

   public StructureComponent(int var1) {
      this.componentType = var1;
   }

   public NBTTagCompound createStructureBaseNBT() {
      NBTTagCompound var4 = new NBTTagCompound();
      var4.setString("id", MapGenStructureIO.getStructureComponentName(this));
      var4.setTag("BB", this.boundingBox.toNBTTagIntArray());
      var4.setInteger("O", this.coordBaseMode == null ? -1 : this.coordBaseMode.getHorizontalIndex());
      var4.setInteger("GD", this.componentType);
      this.writeStructureToNBT(var4);
      return var4;
   }

   public abstract void writeStructureToNBT(NBTTagCompound var1);

   public void readStructureBaseNBT(World var1, NBTTagCompound var2) {
      if (var2.hasKey("BB")) {
         this.boundingBox = new StructureBoundingBox(var2.getIntArray("BB"));
      }

      int var6 = var2.getInteger("O");
      this.coordBaseMode = var6 == -1 ? null : EnumFacing.getHorizontal(var6);
      this.componentType = var2.getInteger("GD");
      this.readStructureFromNBT(var2);
   }

   public abstract void readStructureFromNBT(NBTTagCompound var1);

   public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
   }

   public abstract boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3);

   public StructureBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public int getComponentType() {
      return this.componentType;
   }

   public static StructureComponent findIntersecting(List<StructureComponent> var0, StructureBoundingBox var1) {
      for (StructureComponent var6 : var0) {
         if (var6.getBoundingBox() != null && var6.getBoundingBox().intersectsWith(var1)) {
            return var6;
         }
      }

      return null;
   }

   public BlockPos getBoundingBoxCenter() {
      return new BlockPos(this.boundingBox.getCenter());
   }

   public boolean isLiquidInStructureBoundingBox(World var1, StructureBoundingBox var2) {
      int var6 = Math.max(this.boundingBox.minX - 1, var2.minX);
      int var7 = Math.max(this.boundingBox.minY - 1, var2.minY);
      int var8 = Math.max(this.boundingBox.minZ - 1, var2.minZ);
      int var9 = Math.min(this.boundingBox.maxX + 1, var2.maxX);
      int var10 = Math.min(this.boundingBox.maxY + 1, var2.maxY);
      int var11 = Math.min(this.boundingBox.maxZ + 1, var2.maxZ);
      BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

      for (int var13 = var6; var13 <= var9; var13++) {
         for (int var14 = var8; var14 <= var11; var14++) {
            if (var1.getBlockState(var12.set(var13, var7, var14)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (var1.getBlockState(var12.set(var13, var10, var14)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for (int var15 = var6; var15 <= var9; var15++) {
         for (int var17 = var7; var17 <= var10; var17++) {
            if (var1.getBlockState(var12.set(var15, var17, var8)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (var1.getBlockState(var12.set(var15, var17, var11)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for (int var16 = var8; var16 <= var11; var16++) {
         for (int var18 = var7; var18 <= var10; var18++) {
            if (var1.getBlockState(var12.set(var6, var18, var16)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (var1.getBlockState(var12.set(var9, var18, var16)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   public int getXWithOffset(int var1, int var2) {
      if (this.coordBaseMode == null) {
         return var1;
      }

      switch (this.coordBaseMode) {
         case NORTH:
         case SOUTH:
            return this.boundingBox.minX + var1;
         case WEST:
            return this.boundingBox.maxX - var2;
         case EAST:
            return this.boundingBox.minX + var2;
         default:
            return var1;
      }
   }

   public int getYWithOffset(int var1) {
      return this.coordBaseMode == null ? var1 : var1 + this.boundingBox.minY;
   }

   public int getZWithOffset(int var1, int var2) {
      if (this.coordBaseMode == null) {
         return var2;
      }

      switch (this.coordBaseMode) {
         case NORTH:
            return this.boundingBox.maxZ - var2;
         case SOUTH:
            return this.boundingBox.minZ + var2;
         case WEST:
         case EAST:
            return this.boundingBox.minZ + var1;
         default:
            return var2;
      }
   }

   public int getMetadataWithOffset(Block var1, int var2) {
      if (var1 == Blocks.rail) {
         if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.EAST) {
            if (var2 == 1) {
               return 0;
            }

            return 1;
         }
      } else if (var1 instanceof BlockDoor) {
         if (this.coordBaseMode == EnumFacing.SOUTH) {
            if (var2 == 0) {
               return 2;
            }

            if (var2 == 2) {
               return 0;
            }
         } else {
            if (this.coordBaseMode == EnumFacing.WEST) {
               return var2 + 1 & 3;
            }

            if (this.coordBaseMode == EnumFacing.EAST) {
               return var2 + 3 & 3;
            }
         }
      } else if (var1 != Blocks.stone_stairs
         && var1 != Blocks.oak_stairs
         && var1 != Blocks.nether_brick_stairs
         && var1 != Blocks.stone_brick_stairs
         && var1 != Blocks.sandstone_stairs) {
         if (var1 == Blocks.ladder) {
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (var2 == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }

               if (var2 == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (var2 == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (var2 == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (var2 == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (var2 == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (var2 == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (var2 == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (var2 == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (var2 == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            }
         } else if (var1 == Blocks.stone_button) {
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (var2 == 3) {
                  return 4;
               }

               if (var2 == 4) {
                  return 3;
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (var2 == 3) {
                  return 1;
               }

               if (var2 == 4) {
                  return 2;
               }

               if (var2 == 2) {
                  return 3;
               }

               if (var2 == 1) {
                  return 4;
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (var2 == 3) {
                  return 2;
               }

               if (var2 == 4) {
                  return 1;
               }

               if (var2 == 2) {
                  return 3;
               }

               if (var2 == 1) {
                  return 4;
               }
            }
         } else if (var1 == Blocks.tripwire_hook || var1 instanceof BlockDirectional) {
            EnumFacing var6 = EnumFacing.getHorizontal(var2);
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (var6 == EnumFacing.SOUTH || var6 == EnumFacing.NORTH) {
                  return var6.getOpposite().getHorizontalIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (var6 == EnumFacing.NORTH) {
                  return EnumFacing.WEST.getHorizontalIndex();
               }

               if (var6 == EnumFacing.SOUTH) {
                  return EnumFacing.EAST.getHorizontalIndex();
               }

               if (var6 == EnumFacing.WEST) {
                  return EnumFacing.NORTH.getHorizontalIndex();
               }

               if (var6 == EnumFacing.EAST) {
                  return EnumFacing.SOUTH.getHorizontalIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (var6 == EnumFacing.NORTH) {
                  return EnumFacing.EAST.getHorizontalIndex();
               }

               if (var6 == EnumFacing.SOUTH) {
                  return EnumFacing.WEST.getHorizontalIndex();
               }

               if (var6 == EnumFacing.WEST) {
                  return EnumFacing.NORTH.getHorizontalIndex();
               }

               if (var6 == EnumFacing.EAST) {
                  return EnumFacing.SOUTH.getHorizontalIndex();
               }
            }
         } else if (var1 == Blocks.piston || var1 == Blocks.sticky_piston || var1 == Blocks.lever || var1 == Blocks.dispenser) {
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (var2 == EnumFacing.NORTH.getIndex() || var2 == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.getFront(var2).getOpposite().getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (var2 == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (var2 == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (var2 == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (var2 == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (var2 == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (var2 == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (var2 == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (var2 == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            }
         }
      } else if (this.coordBaseMode == EnumFacing.SOUTH) {
         if (var2 == 2) {
            return 3;
         }

         if (var2 == 3) {
            return 2;
         }
      } else if (this.coordBaseMode == EnumFacing.WEST) {
         if (var2 == 0) {
            return 2;
         }

         if (var2 == 1) {
            return 3;
         }

         if (var2 == 2) {
            return 0;
         }

         if (var2 == 3) {
            return 1;
         }
      } else if (this.coordBaseMode == EnumFacing.EAST) {
         if (var2 == 0) {
            return 2;
         }

         if (var2 == 1) {
            return 3;
         }

         if (var2 == 2) {
            return 1;
         }

         if (var2 == 3) {
            return 0;
         }
      }

      return var2;
   }

   public void setBlockState(World var1, IBlockState var2, int var3, int var4, int var5, StructureBoundingBox var6) {
      BlockPos var10 = new BlockPos(this.getXWithOffset(var3, var5), this.getYWithOffset(var4), this.getZWithOffset(var3, var5));
      if (var6.isVecInside(var10)) {
         var1.setBlockState(var10, var2, 2);
      }
   }

   public IBlockState getBlockStateFromPos(World var1, int var2, int var3, int var4, StructureBoundingBox var5) {
      int var8 = this.getXWithOffset(var2, var4);
      int var9 = this.getYWithOffset(var3);
      int var10 = this.getZWithOffset(var2, var4);
      BlockPos var11 = new BlockPos(var8, var9, var10);
      return !var5.isVecInside(var11) ? Blocks.air.getDefaultState() : var1.getBlockState(var11);
   }

   public void fillWithAir(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      for (int var12 = var4; var12 <= var7; var12++) {
         for (int var13 = var3; var13 <= var6; var13++) {
            for (int var14 = var5; var14 <= var8; var14++) {
               this.setBlockState(var1, Blocks.air.getDefaultState(), var13, var12, var14, var2);
            }
         }
      }
   }

   public void fillWithBlocks(
      World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, IBlockState var9, IBlockState var10, boolean var11
   ) {
      for (int var15 = var4; var15 <= var7; var15++) {
         for (int var16 = var3; var16 <= var6; var16++) {
            for (int var17 = var5; var17 <= var8; var17++) {
               if (!var11 || this.getBlockStateFromPos(var1, var16, var15, var17, var2).getBlock().getMaterial() != Material.air) {
                  if (var15 != var4 && var15 != var7 && var16 != var3 && var16 != var6 && var17 != var5 && var17 != var8) {
                     this.setBlockState(var1, var10, var16, var15, var17, var2);
                  } else {
                     this.setBlockState(var1, var9, var16, var15, var17, var2);
                  }
               }
            }
         }
      }
   }

   public void fillWithRandomizedBlocks(
      World var1,
      StructureBoundingBox var2,
      int var3,
      int var4,
      int var5,
      int var6,
      int var7,
      int var8,
      boolean var9,
      Random var10,
      StructureComponent.BlockSelector var11
   ) {
      for (int var15 = var4; var15 <= var7; var15++) {
         for (int var16 = var3; var16 <= var6; var16++) {
            for (int var17 = var5; var17 <= var8; var17++) {
               if (!var9 || this.getBlockStateFromPos(var1, var16, var15, var17, var2).getBlock().getMaterial() != Material.air) {
                  var11.selectBlocks(
                     var10, var16, var15, var17, var15 == var4 || var15 == var7 || var16 == var3 || var16 == var6 || var17 == var5 || var17 == var8
                  );
                  this.setBlockState(var1, var11.getBlockState(), var16, var15, var17, var2);
               }
            }
         }
      }
   }

   public void func_175805_a(
      World var1,
      StructureBoundingBox var2,
      Random var3,
      float var4,
      int var5,
      int var6,
      int var7,
      int var8,
      int var9,
      int var10,
      IBlockState var11,
      IBlockState var12,
      boolean var13
   ) {
      for (int var17 = var6; var17 <= var9; var17++) {
         for (int var18 = var5; var18 <= var8; var18++) {
            for (int var19 = var7; var19 <= var10; var19++) {
               if (var3.nextFloat() <= var4 && (!var13 || this.getBlockStateFromPos(var1, var18, var17, var19, var2).getBlock().getMaterial() != Material.air)) {
                  if (var17 != var6 && var17 != var9 && var18 != var5 && var18 != var8 && var19 != var7 && var19 != var10) {
                     this.setBlockState(var1, var12, var18, var17, var19, var2);
                  } else {
                     this.setBlockState(var1, var11, var18, var17, var19, var2);
                  }
               }
            }
         }
      }
   }

   public void randomlyPlaceBlock(World var1, StructureBoundingBox var2, Random var3, float var4, int var5, int var6, int var7, IBlockState var8) {
      if (var3.nextFloat() < var4) {
         this.setBlockState(var1, var8, var5, var6, var7, var2);
      }
   }

   public void randomlyRareFillWithBlocks(
      World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, IBlockState var9, boolean var10
   ) {
      float var14 = var6 - var3 + 1;
      float var15 = var7 - var4 + 1;
      float var16 = var8 - var5 + 1;
      float var17 = var3 + var14 / 2.0F;
      float var18 = var5 + var16 / 2.0F;

      for (int var19 = var4; var19 <= var7; var19++) {
         float var20 = (var19 - var4) / var15;

         for (int var21 = var3; var21 <= var6; var21++) {
            float var22 = (var21 - var17) / (var14 * 0.5F);

            for (int var23 = var5; var23 <= var8; var23++) {
               float var24 = (var23 - var18) / (var16 * 0.5F);
               if (!var10 || this.getBlockStateFromPos(var1, var21, var19, var23, var2).getBlock().getMaterial() != Material.air) {
                  float var25 = var22 * var22 + var20 * var20 + var24 * var24;
                  if (var25 <= 1.05F) {
                     this.setBlockState(var1, var9, var21, var19, var23, var2);
                  }
               }
            }
         }
      }
   }

   public void clearCurrentPositionBlocksUpwards(World var1, int var2, int var3, int var4, StructureBoundingBox var5) {
      BlockPos var9 = new BlockPos(this.getXWithOffset(var2, var4), this.getYWithOffset(var3), this.getZWithOffset(var2, var4));
      if (var5.isVecInside(var9)) {
         while (!var1.isAirBlock(var9) && var9.getY() < (int)K) {
            var1.setBlockState(var9, Blocks.air.getDefaultState(), 2);
            var9 = var9.up();
         }
      }
   }

   public void replaceAirAndLiquidDownwards(World var1, IBlockState var2, int var3, int var4, int var5, StructureBoundingBox var6) {
      int var10 = this.getXWithOffset(var3, var5);
      int var11 = this.getYWithOffset(var4);
      int var12 = this.getZWithOffset(var3, var5);
      if (var6.isVecInside(new BlockPos(var10, var11, var12))) {
         while (
            (var1.isAirBlock(new BlockPos(var10, var11, var12)) || var1.getBlockState(new BlockPos(var10, var11, var12)).getBlock().getMaterial().isLiquid())
               && var11 > 1
         ) {
            var1.setBlockState(new BlockPos(var10, var11, var12), var2, 2);
            var11--;
         }
      }
   }

   public boolean generateChestContents(
      World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, List<WeightedRandomChestContent> var7, int var8
   ) {
      BlockPos var12 = new BlockPos(this.getXWithOffset(var4, var6), this.getYWithOffset(var5), this.getZWithOffset(var4, var6));
      if (var2.isVecInside(var12) && var1.getBlockState(var12).getBlock() != Blocks.chest) {
         IBlockState var13 = Blocks.chest.getDefaultState();
         var1.setBlockState(var12, Blocks.chest.correctFacing(var1, var12, var13), 2);
         TileEntity var14 = var1.getTileEntity(var12);
         if (var14 instanceof TileEntityChest) {
            WeightedRandomChestContent.generateChestContents(var3, var7, (TileEntityChest)var14, var8);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean generateDispenserContents(
      World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, int var7, List<WeightedRandomChestContent> var8, int var9
   ) {
      BlockPos var13 = new BlockPos(this.getXWithOffset(var4, var6), this.getYWithOffset(var5), this.getZWithOffset(var4, var6));
      if (var2.isVecInside(var13) && var1.getBlockState(var13).getBlock() != Blocks.dispenser) {
         var1.setBlockState(var13, Blocks.dispenser.getStateFromMeta(this.getMetadataWithOffset(Blocks.dispenser, var7)), 2);
         TileEntity var14 = var1.getTileEntity(var13);
         if (var14 instanceof TileEntityDispenser) {
            WeightedRandomChestContent.generateDispenserContents(var3, var8, (TileEntityDispenser)var14, var9);
         }

         return true;
      } else {
         return false;
      }
   }

   public void placeDoorCurrentPosition(World var1, StructureBoundingBox var2, Random var3, int var4, int var5, int var6, EnumFacing var7) {
      BlockPos var10 = new BlockPos(this.getXWithOffset(var4, var6), this.getYWithOffset(var5), this.getZWithOffset(var4, var6));
      if (var2.isVecInside(var10)) {
         ItemDoor.placeDoor(var1, var10, var7.rotateYCCW(), Blocks.oak_door);
      }
   }

   public void func_181138_a(int var1, int var2, int var3) {
      this.boundingBox.offset(var1, var2, var3);
   }

   public abstract static class BlockSelector {
      public IBlockState blockstate = Blocks.air.getDefaultState();

      public abstract void selectBlocks(Random var1, int var2, int var3, int var4, boolean var5);

      public IBlockState getBlockState() {
         return this.blockstate;
      }
   }
}
