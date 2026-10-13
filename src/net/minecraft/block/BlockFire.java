package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class BlockFire extends Block {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
   public static final PropertyBool FLIP = PropertyBool.create("flip");
   public static final PropertyBool ALT = PropertyBool.create("alt");
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   public static final PropertyInteger UPPER = PropertyInteger.create("upper", 0, 2);
   private final Map<Block, Integer> encouragements = Maps.newIdentityHashMap();
   private final Map<Block, Integer> flammabilities = Maps.newIdentityHashMap();
   private static final long bb = 80381298342352L;

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      int var7 = var3.getX();
      int var8 = var3.getY();
      int var9 = var3.getZ();
      if (!World.doesBlockHaveSolidTopSurface(var2, var3.down()) && !Blocks.fire.canCatchFire(var2, var3.down())) {
         boolean var10 = (var7 + var8 + var9 & 1) == 1;
         boolean var11 = (var7 / 2 + var8 / 2 + var9 / 2 & 1) == 1;
         byte var12 = 0;
         if (this.canCatchFire(var2, var3.up())) {
            var12 = (byte)(var10 ? 1 : 2);
         }

         return var1.withProperty(NORTH, this.canCatchFire(var2, var3.north()))
            .withProperty(EAST, this.canCatchFire(var2, var3.east()))
            .withProperty(SOUTH, this.canCatchFire(var2, var3.south()))
            .withProperty(WEST, this.canCatchFire(var2, var3.west()))
            .withProperty(UPPER, Integer.valueOf(var12))
            .withProperty(FLIP, var11)
            .withProperty(ALT, var10);
      } else {
         return this.getDefaultState();
      }
   }

   public BlockFire() {
      super(Material.fire);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(AGE, 0)
            .withProperty(FLIP, false)
            .withProperty(ALT, false)
            .withProperty(NORTH, false)
            .withProperty(EAST, false)
            .withProperty(SOUTH, false)
            .withProperty(WEST, false)
            .withProperty(UPPER, 0)
      );
      this.setTickRandomly(true);
   }

   public static void init() {
      Blocks.fire.setFireInfo(Blocks.planks, 5, 20);
      Blocks.fire.setFireInfo(Blocks.double_wooden_slab, 5, 20);
      Blocks.fire.setFireInfo(Blocks.wooden_slab, 5, 20);
      Blocks.fire.setFireInfo(Blocks.oak_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.spruce_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.birch_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.jungle_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.dark_oak_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.acacia_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.oak_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.spruce_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.birch_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.jungle_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.dark_oak_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.acacia_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.oak_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.birch_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.spruce_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.jungle_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.log, 5, 5);
      Blocks.fire.setFireInfo(Blocks.log2, 5, 5);
      Blocks.fire.setFireInfo(Blocks.leaves, 30, 60);
      Blocks.fire.setFireInfo(Blocks.leaves2, 30, 60);
      Blocks.fire.setFireInfo(Blocks.bookshelf, 30, 20);
      Blocks.fire.setFireInfo(Blocks.tnt, 15, 100);
      Blocks.fire.setFireInfo(Blocks.tallgrass, 60, 100);
      Blocks.fire.setFireInfo(Blocks.double_plant, 60, 100);
      Blocks.fire.setFireInfo(Blocks.yellow_flower, 60, 100);
      Blocks.fire.setFireInfo(Blocks.red_flower, 60, 100);
      Blocks.fire.setFireInfo(Blocks.deadbush, 60, 100);
      Blocks.fire.setFireInfo(Blocks.wool, 30, 60);
      Blocks.fire.setFireInfo(Blocks.vine, 15, 100);
      Blocks.fire.setFireInfo(Blocks.coal_block, 5, 5);
      Blocks.fire.setFireInfo(Blocks.hay_block, 60, 20);
      Blocks.fire.setFireInfo(Blocks.carpet, 60, 20);
   }

   public void setFireInfo(Block var1, int var2, int var3) {
      this.encouragements.put(var1, var2);
      this.flammabilities.put(var1, var3);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int quantityDropped(Random var1) {
      return 0;
   }

   @Override
   public int tickRate(World var1) {
      return 30;
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var1.getGameRules().getBoolean("doFireTick")) {
         if (!this.canPlaceBlockAt(var1, var2)) {
            var1.setBlockToAir(var2);
         }

         Block var8 = var1.getBlockState(var2.down()).getBlock();
         boolean var9 = var8 == Blocks.netherrack;
         if (var1.provider instanceof WorldProviderEnd && var8 == Blocks.bedrock) {
            var9 = true;
         }

         if (!var9 && var1.isRaining() && this.canDie(var1, var2)) {
            var1.setBlockToAir(var2);
         } else {
            int var10 = var3.getValue(AGE);
            if (var10 < 15) {
               var3 = var3.withProperty(AGE, var10 + var4.nextInt(3) / 2);
               var1.setBlockState(var2, var3, 4);
            }

            var1.scheduleUpdate(var2, this, this.tickRate(var1) + var4.nextInt(10));
            if (!var9) {
               if (!this.canNeighborCatchFire(var1, var2)) {
                  if (!World.doesBlockHaveSolidTopSurface(var1, var2.down()) || var10 > 3) {
                     var1.setBlockToAir(var2);
                  }

                  return;
               }

               if (!this.canCatchFire(var1, var2.down()) && var10 == 15 && var4.nextInt(4) == 0) {
                  var1.setBlockToAir(var2);
                  return;
               }
            }

            boolean var11 = var1.isBlockinHighHumidity(var2);
            byte var12 = 0;
            if (var11) {
               var12 = -50;
            }

            this.catchOnFire(var1, var2.east(), 300 + var12, var4, var10);
            this.catchOnFire(var1, var2.west(), 300 + var12, var4, var10);
            this.catchOnFire(var1, var2.down(), 250 + var12, var4, var10);
            this.catchOnFire(var1, var2.up(), 250 + var12, var4, var10);
            this.catchOnFire(var1, var2.north(), 300 + var12, var4, var10);
            this.catchOnFire(var1, var2.south(), 300 + var12, var4, var10);

            for (int var13 = -1; var13 <= 1; var13++) {
               for (int var14 = -1; var14 <= 1; var14++) {
                  for (int var15 = -1; var15 <= 4; var15++) {
                     if (var13 != 0 || var15 != 0 || var14 != 0) {
                        int var16 = 100;
                        if (var15 > 1) {
                           var16 += (var15 - 1) * 100;
                        }

                        BlockPos var17 = var2.add(var13, var15, var14);
                        int var18 = this.getNeighborEncouragement(var1, var17);
                        if (var18 > 0) {
                           int var19 = (var18 + 40 + var1.getDifficulty().getDifficultyId() * 7) / (var10 + 30);
                           if (var11) {
                              var19 /= 2;
                           }

                           if (var19 > 0 && var4.nextInt(var16) <= var19 && (!var1.isRaining() || !this.canDie(var1, var17))) {
                              int var20 = var10 + var4.nextInt(5) / 4;
                              if (var20 > 15) {
                                 var20 = 15;
                              }

                              var1.setBlockState(var17, var3.withProperty(AGE, var20), 3);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean canDie(World var1, BlockPos var2) {
      return var1.isRainingAt(var2)
         || var1.isRainingAt(var2.west())
         || var1.isRainingAt(var2.east())
         || var1.isRainingAt(var2.north())
         || var1.isRainingAt(var2.south());
   }

   @Override
   public boolean requiresUpdates() {
      return false;
   }

   private int getFlammability(Block var1) {
      Integer var5 = this.flammabilities.get(var1);
      return var5 == null ? 0 : var5;
   }

   private int getEncouragement(Block var1) {
      Integer var5 = this.encouragements.get(var1);
      return var5 == null ? 0 : var5;
   }

   private void catchOnFire(World var1, BlockPos var2, int var3, Random var4, int var5) {
      int var9 = this.getFlammability(var1.getBlockState(var2).getBlock());
      if (var4.nextInt(var3) < var9) {
         IBlockState var10 = var1.getBlockState(var2);
         if (var4.nextInt(var5 + 10) < 5 && !var1.isRainingAt(var2)) {
            int var11 = var5 + var4.nextInt(5) / 4;
            if (var11 > 15) {
               var11 = 15;
            }

            var1.setBlockState(var2, this.getDefaultState().withProperty(AGE, var11), 3);
         } else {
            var1.setBlockToAir(var2);
         }

         if (var10.getBlock() == Blocks.tnt) {
            Blocks.tnt.onBlockDestroyedByPlayer(var1, var2, var10.withProperty(BlockTNT.EXPLODE, true));
         }
      }
   }

   private boolean canNeighborCatchFire(World var1, BlockPos var2) {
      for (EnumFacing var9 : EnumFacing.values()) {
         if (this.canCatchFire(var1, var2.offset(var9))) {
            return true;
         }
      }

      return false;
   }

   private int getNeighborEncouragement(World var1, BlockPos var2) {
      if (!var1.isAirBlock(var2)) {
         return 0;
      }

      int var6 = 0;

      for (EnumFacing var10 : EnumFacing.values()) {
         var6 = Math.max(this.getEncouragement(var1.getBlockState(var2.offset(var10)).getBlock()), var6);
      }

      return var6;
   }

   @Override
   public boolean isCollidable() {
      return false;
   }

   public boolean canCatchFire(IBlockAccess var1, BlockPos var2) {
      return this.getEncouragement(var1.getBlockState(var2).getBlock()) > 0;
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return World.doesBlockHaveSolidTopSurface(var1, var2.down()) || this.canNeighborCatchFire(var1, var2);
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!World.doesBlockHaveSolidTopSurface(var1, var2.down()) && !this.canNeighborCatchFire(var1, var2)) {
         var1.setBlockToAir(var2);
      }
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      if (var1.provider.getDimensionId() > 0 || !Blocks.portal.func_176548_d(var1, var2)) {
         if (!World.doesBlockHaveSolidTopSurface(var1, var2.down()) && !this.canNeighborCatchFire(var1, var2)) {
            var1.setBlockToAir(var2);
         } else {
            var1.scheduleUpdate(var2, this, this.tickRate(var1) + var1.rand.nextInt(10));
         }
      }
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var4.nextInt(24) == 0) {
         var1.playSound(var2.getX() + 0.5F, var2.getY() + 0.5F, var2.getZ() + 0.5F, "fire.fire", 1.0F + var4.nextFloat(), var4.nextFloat() * 0.7F + 0.3F, false);
      }

      if (!World.doesBlockHaveSolidTopSurface(var1, var2.down()) && !Blocks.fire.canCatchFire(var1, var2.down())) {
         if (Blocks.fire.canCatchFire(var1, var2.west())) {
            for (int var15 = 0; var15 < 2; var15++) {
               double var20 = var2.getX() + var4.nextDouble() * 0.1F;
               double var25 = var2.getY() + var4.nextDouble();
               double var30 = var2.getZ() + var4.nextDouble();
               var1.spawnParticle(EnumParticleTypes.SMOKE_LARGE, var20, var25, var30, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(var1, var2.east())) {
            for (int var16 = 0; var16 < 2; var16++) {
               double var21 = var2.getX() + 1 - var4.nextDouble() * 0.1F;
               double var26 = var2.getY() + var4.nextDouble();
               double var31 = var2.getZ() + var4.nextDouble();
               var1.spawnParticle(EnumParticleTypes.SMOKE_LARGE, var21, var26, var31, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(var1, var2.north())) {
            for (int var17 = 0; var17 < 2; var17++) {
               double var22 = var2.getX() + var4.nextDouble();
               double var27 = var2.getY() + var4.nextDouble();
               double var32 = var2.getZ() + var4.nextDouble() * 0.1F;
               var1.spawnParticle(EnumParticleTypes.SMOKE_LARGE, var22, var27, var32, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(var1, var2.south())) {
            for (int var18 = 0; var18 < 2; var18++) {
               double var23 = var2.getX() + var4.nextDouble();
               double var28 = var2.getY() + var4.nextDouble();
               double var33 = var2.getZ() + 1 - var4.nextDouble() * 0.1F;
               var1.spawnParticle(EnumParticleTypes.SMOKE_LARGE, var23, var28, var33, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(var1, var2.up())) {
            for (int var19 = 0; var19 < 2; var19++) {
               double var24 = var2.getX() + var4.nextDouble();
               double var29 = var2.getY() + 1 - var4.nextDouble() * 0.1F;
               double var34 = var2.getZ() + var4.nextDouble();
               var1.spawnParticle(EnumParticleTypes.SMOKE_LARGE, var24, var29, var34, 0.0, 0.0, 0.0);
            }
         }
      } else {
         for (int var8 = 0; var8 < 3; var8++) {
            double var9 = var2.getX() + var4.nextDouble();
            double var11 = var2.getY() + var4.nextDouble() * 0.5 + 0.5;
            double var13 = var2.getZ() + var4.nextDouble();
            var1.spawnParticle(EnumParticleTypes.SMOKE_LARGE, var9, var11, var13, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      return MapColor.tntColor;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(AGE, var1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(AGE);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AGE, NORTH, EAST, SOUTH, WEST, UPPER, FLIP, ALT);
   }
}
