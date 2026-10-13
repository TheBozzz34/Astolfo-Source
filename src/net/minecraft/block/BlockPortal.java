package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable {
   public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
   private static final long i = 70570826312331L;

   public BlockPortal() {
      super(Material.portal, false);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X));
      this.setTickRandomly(true);
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      super.updateTick(var1, var2, var3, var4);
      if (var1.provider.isSurfaceWorld() && var1.getGameRules().getBoolean("doMobSpawning") && var4.nextInt(2000) < var1.getDifficulty().getDifficultyId()) {
         int var8 = var2.getY();
         BlockPos var9 = var2;

         while (!World.doesBlockHaveSolidTopSurface(var1, var9) && var9.getY() > 0) {
            var9 = var9.down();
         }

         if (var8 > 0 && !var1.getBlockState(var9.up()).getBlock().isNormalCube()) {
            Entity var10 = ItemMonsterPlacer.spawnCreature(var1, 57, var9.getX() + 0.5, var9.getY() + 1.1, var9.getZ() + 0.5);
            if (var10 != null) {
               var10.timeUntilPortal = var10.getPortalCooldown();
            }
         }
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      EnumFacing.Axis var6 = var1.getBlockState(var2).getValue(AXIS);
      float var7 = 0.125F;
      float var8 = 0.125F;
      if (var6 == EnumFacing.Axis.X) {
         var7 = 0.5F;
      }

      if (var6 == EnumFacing.Axis.Z) {
         var8 = 0.5F;
      }

      this.setBlockBounds(0.5F - var7, 0.0F, 0.5F - var8, 0.5F + var7, 1.0F, 0.5F + var8);
   }

   public static int getMetaForAxis(EnumFacing.Axis var0) {
      return var0 == EnumFacing.Axis.X ? 1 : (var0 == EnumFacing.Axis.Z ? 2 : 0);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public boolean func_176548_d(World var1, BlockPos var2) {
      BlockPortal.Size var6 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.X);
      if (var6.func_150860_b() && var6.field_150864_e == 0) {
         var6.func_150859_c();
         return true;
      } else {
         BlockPortal.Size var7 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.Z);
         if (var7.func_150860_b() && var7.field_150864_e == 0) {
            var7.func_150859_c();
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing.Axis var8 = var3.getValue(AXIS);
      if (var8 == EnumFacing.Axis.X) {
         BlockPortal.Size var9 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.X);
         if (!var9.func_150860_b() || var9.field_150864_e < var9.field_150868_h * var9.field_150862_g) {
            var1.setBlockState(var2, Blocks.air.getDefaultState());
         }
      } else if (var8 == EnumFacing.Axis.Z) {
         BlockPortal.Size var10 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.Z);
         if (!var10.func_150860_b() || var10.field_150864_e < var10.field_150868_h * var10.field_150862_g) {
            var1.setBlockState(var2, Blocks.air.getDefaultState());
         }
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      EnumFacing.Axis var7 = null;
      IBlockState var8 = var1.getBlockState(var2);
      if (var1.getBlockState(var2).getBlock() == this) {
         var7 = var8.getValue(AXIS);
         if (var7 == null) {
            return false;
         }

         if (var7 == EnumFacing.Axis.Z && var3 != EnumFacing.EAST && var3 != EnumFacing.WEST) {
            return false;
         }

         if (var7 == EnumFacing.Axis.X && var3 != EnumFacing.SOUTH && var3 != EnumFacing.NORTH) {
            return false;
         }
      }

      boolean var9 = var1.getBlockState(var2.west()).getBlock() == this && var1.getBlockState(var2.west(2)).getBlock() != this;
      boolean var10 = var1.getBlockState(var2.east()).getBlock() == this && var1.getBlockState(var2.east(2)).getBlock() != this;
      boolean var11 = var1.getBlockState(var2.north()).getBlock() == this && var1.getBlockState(var2.north(2)).getBlock() != this;
      boolean var12 = var1.getBlockState(var2.south()).getBlock() == this && var1.getBlockState(var2.south(2)).getBlock() != this;
      boolean var13 = var9 || var10 || var7 == EnumFacing.Axis.X;
      boolean var14 = var11 || var12 || var7 == EnumFacing.Axis.Z;
      return var13 && var3 == EnumFacing.WEST
         ? true
         : (var13 && var3 == EnumFacing.EAST ? true : (var14 && var3 == EnumFacing.NORTH ? true : var14 && var3 == EnumFacing.SOUTH));
   }

   @Override
   public int quantityDropped(Random var1) {
      return 0;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   @Override
   public void onEntityCollidedWithBlock(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (var4.ridingEntity == null && var4.riddenByEntity == null) {
         var4.setPortal(var2);
      }
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var4.nextInt(100) == 0) {
         var1.playSound(var2.getX() + 0.5, var2.getY() + 0.5, var2.getZ() + 0.5, "portal.portal", 0.5F, var4.nextFloat() * 0.4F + 0.8F, false);
      }

      for (int var8 = 0; var8 < 4; var8++) {
         double var9 = var2.getX() + var4.nextFloat();
         double var11 = var2.getY() + var4.nextFloat();
         double var13 = var2.getZ() + var4.nextFloat();
         double var15 = (var4.nextFloat() - 0.5) * 0.5;
         double var17 = (var4.nextFloat() - 0.5) * 0.5;
         double var19 = (var4.nextFloat() - 0.5) * 0.5;
         int var21 = var4.nextInt(2) * 2 - 1;
         if (var1.getBlockState(var2.west()).getBlock() != this && var1.getBlockState(var2.east()).getBlock() != this) {
            var9 = var2.getX() + 0.5 + 0.25 * var21;
            var15 = var4.nextFloat() * 2.0F * var21;
         } else {
            var13 = var2.getZ() + 0.5 + 0.25 * var21;
            var19 = var4.nextFloat() * 2.0F * var21;
         }

         var1.spawnParticle(EnumParticleTypes.PORTAL, var9, var11, var13, var15, var17, var19);
      }
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return null;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(AXIS, (var1 & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return getMetaForAxis(var1.getValue(AXIS));
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AXIS);
   }

   public BlockPattern.PatternHelper func_181089_f(World var1, BlockPos var2) {
      EnumFacing.Axis var6 = EnumFacing.Axis.Z;
      BlockPortal.Size var7 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.X);
      LoadingCache var8 = BlockPattern.func_181627_a(var1, true);
      if (!var7.func_150860_b()) {
         var6 = EnumFacing.Axis.X;
         var7 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.Z);
      }

      if (!var7.func_150860_b()) {
         return new BlockPattern.PatternHelper(var2, EnumFacing.NORTH, EnumFacing.UP, var8, 1, 1, 1);
      }

      int[] var9 = new int[EnumFacing.AxisDirection.values().length];
      EnumFacing var10 = var7.field_150866_c.rotateYCCW();
      BlockPos var11 = var7.field_150861_f.up(var7.func_181100_a() - 1);

      for (EnumFacing.AxisDirection var15 : EnumFacing.AxisDirection.values()) {
         BlockPattern.PatternHelper var16 = new BlockPattern.PatternHelper(
            var10.getAxisDirection() == var15 ? var11 : var11.offset(var7.field_150866_c, var7.func_181101_b() - 1),
            EnumFacing.getFacingFromAxis(var15, var6),
            EnumFacing.UP,
            var8,
            var7.func_181101_b(),
            var7.func_181100_a(),
            1
         );

         for (int var17 = 0; var17 < var7.func_181101_b(); var17++) {
            for (int var18 = 0; var18 < var7.func_181100_a(); var18++) {
               BlockWorldState var19 = var16.translateOffset(var17, var18, 1);
               if (var19.getBlockState() != null && var19.getBlockState().getBlock().getMaterial() != Material.air) {
                  var9[var15.ordinal()]++;
               }
            }
         }
      }

      EnumFacing.AxisDirection var20 = EnumFacing.AxisDirection.POSITIVE;

      for (EnumFacing.AxisDirection var24 : EnumFacing.AxisDirection.values()) {
         if (var9[var24.ordinal()] < var9[var20.ordinal()]) {
            var20 = var24;
         }
      }

      return new BlockPattern.PatternHelper(
         var10.getAxisDirection() == var20 ? var11 : var11.offset(var7.field_150866_c, var7.func_181101_b() - 1),
         EnumFacing.getFacingFromAxis(var20, var6),
         EnumFacing.UP,
         var8,
         var7.func_181101_b(),
         var7.func_181100_a(),
         1
      );
   }

   public static class Size {
      private final World world;
      private final EnumFacing.Axis axis;
      private final EnumFacing field_150866_c;
      private final EnumFacing field_150863_d;
      private int field_150864_e = 0;
      private BlockPos field_150861_f;
      private int field_150862_g;
      private int field_150868_h;
      private static final long b = 19547903121215L;

      public Size(World var1, BlockPos var2, EnumFacing.Axis var3) {
         this.world = var1;
         this.axis = var3;
         if (var3 == EnumFacing.Axis.X) {
            this.field_150863_d = EnumFacing.EAST;
            this.field_150866_c = EnumFacing.WEST;
         } else {
            this.field_150863_d = EnumFacing.NORTH;
            this.field_150866_c = EnumFacing.SOUTH;
         }

         BlockPos var7 = var2;

         while (var2.getY() > var7.getY() - 21 && var2.getY() > 0 && this.func_150857_a(var1.getBlockState(var2.down()).getBlock())) {
            var2 = var2.down();
         }

         int var8 = this.func_180120_a(var2, this.field_150863_d) - 1;
         if (var8 >= 0) {
            this.field_150861_f = var2.offset(this.field_150863_d, var8);
            this.field_150868_h = this.func_180120_a(this.field_150861_f, this.field_150866_c);
            if (this.field_150868_h < 2 || this.field_150868_h > 21) {
               this.field_150861_f = null;
               this.field_150868_h = 0;
            }
         }

         if (this.field_150861_f != null) {
            this.field_150862_g = this.func_150858_a();
         }
      }

      public int func_180120_a(BlockPos var1, EnumFacing var2) {
         int var6;
         for (var6 = 0; var6 < 22; var6++) {
            BlockPos var7 = var1.offset(var2, var6);
            if (!this.func_150857_a(this.world.getBlockState(var7).getBlock()) || this.world.getBlockState(var7.down()).getBlock() != Blocks.obsidian) {
               break;
            }
         }

         Block var8 = this.world.getBlockState(var1.offset(var2, var6)).getBlock();
         return var8 == Blocks.obsidian ? var6 : 0;
      }

      public int func_181100_a() {
         return this.field_150862_g;
      }

      public int func_181101_b() {
         return this.field_150868_h;
      }

      public int func_150858_a() {
         label56:
         for (this.field_150862_g = 0; this.field_150862_g < 21; this.field_150862_g++) {
            for (int var4 = 0; var4 < this.field_150868_h; var4++) {
               BlockPos var5 = this.field_150861_f.offset(this.field_150866_c, var4).up(this.field_150862_g);
               Block var6 = this.world.getBlockState(var5).getBlock();
               if (!this.func_150857_a(var6)) {
                  break label56;
               }

               if (var6 == Blocks.portal) {
                  this.field_150864_e++;
               }

               if (var4 == 0) {
                  var6 = this.world.getBlockState(var5.offset(this.field_150863_d)).getBlock();
                  if (var6 != Blocks.obsidian) {
                     break label56;
                  }
               } else if (var4 == this.field_150868_h - 1) {
                  var6 = this.world.getBlockState(var5.offset(this.field_150866_c)).getBlock();
                  if (var6 != Blocks.obsidian) {
                     break label56;
                  }
               }
            }
         }

         for (int var7 = 0; var7 < this.field_150868_h; var7++) {
            if (this.world.getBlockState(this.field_150861_f.offset(this.field_150866_c, var7).up(this.field_150862_g)).getBlock() != Blocks.obsidian) {
               this.field_150862_g = 0;
               break;
            }
         }

         if (this.field_150862_g <= 21 && this.field_150862_g >= 3) {
            return this.field_150862_g;
         }

         this.field_150861_f = null;
         this.field_150868_h = 0;
         this.field_150862_g = 0;
         return 0;
      }

      public boolean func_150857_a(Block var1) {
         return var1.blockMaterial == Material.air || var1 == Blocks.fire || var1 == Blocks.portal;
      }

      public boolean func_150860_b() {
         return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
      }

      public void func_150859_c() {
         for (int var4 = 0; var4 < this.field_150868_h; var4++) {
            BlockPos var5 = this.field_150861_f.offset(this.field_150866_c, var4);

            for (int var6 = 0; var6 < this.field_150862_g; var6++) {
               this.world.setBlockState(var5.up(var6), Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, this.axis), 2);
            }
         }
      }
   }
}
