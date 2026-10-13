package net.minecraft.block;

import bypass.Astolfo;
import bypass.event.world.LiquidCollisionEvent;
import bypass.module.combat.Velocity;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLiquid extends Block {
   public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);
   private static final long i = 109758505320311L;

   public BlockLiquid(Material var1) {
      super(var1);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.setTickRandomly(true);
   }

   @Override
   public boolean isPassable(IBlockAccess var1, BlockPos var2) {
      return this.blockMaterial != Material.lava;
   }

   @Override
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      return this.blockMaterial == Material.water ? BiomeColorHelper.getWaterColorAtPos(var1, var2) : 16777215;
   }

   public static float getLiquidHeightPercent(int var0) {
      if (var0 >= 8) {
         var0 = 0;
      }

      return (var0 + 1) / 9.0F;
   }

   public int getLevel(IBlockAccess var1, BlockPos var2) {
      return var1.getBlockState(var2).getBlock().getMaterial() == this.blockMaterial ? var1.getBlockState(var2).getValue(LEVEL) : -1;
   }

   public int getEffectiveFlowDecay(IBlockAccess var1, BlockPos var2) {
      int var6 = this.getLevel(var1, var2);
      return var6 >= 8 ? 0 : var6;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean canCollideCheck(IBlockState var1, boolean var2) {
      return var2 && var1.getValue(LEVEL) == 0;
   }

   @Override
   public boolean isBlockSolid(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      Material var6 = var1.getBlockState(var2).getBlock().getMaterial();
      return var6 == this.blockMaterial ? false : (var3 == EnumFacing.UP ? true : (var6 == Material.ice ? false : super.isBlockSolid(var1, var2, var3)));
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var1.getBlockState(var2).getBlock().getMaterial() == this.blockMaterial
         ? false
         : (var3 == EnumFacing.UP ? true : super.shouldSideBeRendered(var1, var2, var3));
   }

   public boolean shouldRenderSides(IBlockAccess var1, BlockPos var2) {
      for (int var6 = -1; var6 <= 1; var6++) {
         for (int var7 = -1; var7 <= 1; var7++) {
            IBlockState var8 = var1.getBlockState(var2.add(var6, 0, var7));
            Block var9 = var8.getBlock();
            Material var10 = var9.getMaterial();
            if (var10 != this.blockMaterial && !var9.isFullBlock()) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      LiquidCollisionEvent var6 = new LiquidCollisionEvent(this, null, var2);
      Astolfo.INSTANCE.getEventBus().post(var6);
      return var6.getBoundingBox();
   }

   @Override
   public int getRenderType() {
      return 1;
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return null;
   }

   @Override
   public int quantityDropped(Random var1) {
      return 0;
   }

   public Vec3 getFlowVector(IBlockAccess var1, BlockPos var2) {
      Vec3 var6 = new Vec3(0.0, 0.0, 0.0);
      int var7 = this.getEffectiveFlowDecay(var1, var2);

      for (Object var9 : EnumFacing.Plane.HORIZONTAL) {
         BlockPos var10 = var2.offset((EnumFacing)var9);
         int var11 = this.getEffectiveFlowDecay(var1, var10);
         if (var11 < 0) {
            if (!var1.getBlockState(var10).getBlock().getMaterial().blocksMovement()) {
               var11 = this.getEffectiveFlowDecay(var1, var10.down());
               if (var11 >= 0) {
                  int var12 = var11 - (var7 - 8);
                  var6 = var6.addVector((var10.getX() - var2.getX()) * var12, (var10.getY() - var2.getY()) * var12, (var10.getZ() - var2.getZ()) * var12);
               }
            }
         } else if (var11 >= 0) {
            int var17 = var11 - var7;
            var6 = var6.addVector((var10.getX() - var2.getX()) * var17, (var10.getY() - var2.getY()) * var17, (var10.getZ() - var2.getZ()) * var17);
         }
      }

      if (var1.getBlockState(var2).getValue(LEVEL) >= 8) {
         for (Object var14 : EnumFacing.Plane.HORIZONTAL) {
            BlockPos var15 = var2.offset((EnumFacing)var14);
            if (this.isBlockSolid(var1, var15, (EnumFacing)var14) || this.isBlockSolid(var1, var15.up(), (EnumFacing)var14)) {
               var6 = var6.normalize().addVector(0.0, -6.0, 0.0);
               break;
            }
         }
      }

      return var6.normalize();
   }

   @Override
   public Vec3 modifyAcceleration(World var1, BlockPos var2, Entity var3, Vec3 var4) {
      long var5 = i ^ 2509541715126L;
      long var7 = var5 ^ 95574484758130L;
      return Astolfo.INSTANCE.getModuleManager().get(Velocity.class).isEnabled() && Velocity.water.getValue() ? var4 : var4.l(this.getFlowVector(var1, var2));
   }

   @Override
   public int tickRate(World var1) {
      return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? (var1.provider.getHasNoSky() ? 10 : 30) : 0);
   }

   @Override
   public int getMixedBrightnessForBlock(IBlockAccess var1, BlockPos var2) {
      int var6 = var1.getCombinedLight(var2, 0);
      int var7 = var1.getCombinedLight(var2.up(), 0);
      int var8 = var6 & 0xFF;
      int var9 = var7 & 0xFF;
      int var10 = var6 >> 16 & 0xFF;
      int var11 = var7 >> 16 & 0xFF;
      return (var8 > var9 ? var8 : var9) | (var10 > var11 ? var10 : var11) << 16;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return this.blockMaterial == Material.water ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.SOLID;
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      double var8 = var2.getX();
      double var10 = var2.getY();
      double var12 = var2.getZ();
      if (this.blockMaterial == Material.water) {
         int var14 = var3.getValue(LEVEL);
         if (var14 > 0 && var14 < 8) {
            if (var4.nextInt(64) == 0) {
               var1.playSound(var8 + 0.5, var10 + 0.5, var12 + 0.5, "liquid.water", var4.nextFloat() * 0.25F + 0.75F, var4.nextFloat() * 1.0F + 0.5F, false);
            }
         } else if (var4.nextInt(10) == 0) {
            var1.spawnParticle(EnumParticleTypes.SUSPENDED, var8 + var4.nextFloat(), var10 + var4.nextFloat(), var12 + var4.nextFloat(), 0.0, 0.0, 0.0);
         }
      }

      if (this.blockMaterial == Material.lava
         && var1.getBlockState(var2.up()).getBlock().getMaterial() == Material.air
         && !var1.getBlockState(var2.up()).getBlock().isOpaqueCube()) {
         if (var4.nextInt(100) == 0) {
            double var21 = var8 + var4.nextFloat();
            double var16 = var10 + this.maxY;
            double var18 = var12 + var4.nextFloat();
            var1.spawnParticle(EnumParticleTypes.LAVA, var21, var16, var18, 0.0, 0.0, 0.0);
            var1.playSound(var21, var16, var18, "liquid.lavapop", 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false);
         }

         if (var4.nextInt(200) == 0) {
            var1.playSound(var8, var10, var12, "liquid.lava", 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false);
         }
      }

      if (var4.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(var1, var2.down())) {
         Material var22 = var1.getBlockState(var2.down(2)).getBlock().getMaterial();
         if (!var22.blocksMovement() && !var22.isLiquid()) {
            double var15 = var8 + var4.nextFloat();
            double var17 = var10 - 1.05;
            double var19 = var12 + var4.nextFloat();
            if (this.blockMaterial == Material.water) {
               var1.spawnParticle(EnumParticleTypes.DRIP_WATER, var15, var17, var19, 0.0, 0.0, 0.0);
            } else {
               var1.spawnParticle(EnumParticleTypes.DRIP_LAVA, var15, var17, var19, 0.0, 0.0, 0.0);
            }
         }
      }
   }

   public static double getFlowDirection(IBlockAccess var0, BlockPos var1, Material var2) {
      Vec3 var6 = getFlowingBlock(var2).getFlowVector(var0, var1);
      return var6.xCoord == 0.0 && var6.zCoord == 0.0 ? -1000.0 : MathHelper.atan2(var6.zCoord, var6.xCoord) - (Math.PI / 2);
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      this.checkForMixing(var1, var2, var3);
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.checkForMixing(var1, var2, var3);
   }

   public boolean checkForMixing(World var1, BlockPos var2, IBlockState var3) {
      if (this.blockMaterial == Material.lava) {
         boolean var7 = false;

         for (EnumFacing var11 : EnumFacing.values()) {
            if (var11 != EnumFacing.DOWN && var1.getBlockState(var2.offset(var11)).getBlock().getMaterial() == Material.water) {
               var7 = true;
               break;
            }
         }

         if (var7) {
            Integer var12 = var3.getValue(LEVEL);
            if (var12 == 0) {
               var1.setBlockState(var2, Blocks.obsidian.getDefaultState());
               this.triggerMixEffects(var1, var2);
               return true;
            }

            if (var12 <= 4) {
               var1.setBlockState(var2, Blocks.cobblestone.getDefaultState());
               this.triggerMixEffects(var1, var2);
               return true;
            }
         }
      }

      return false;
   }

   public void triggerMixEffects(World var1, BlockPos var2) {
      double var6 = var2.getX();
      double var8 = var2.getY();
      double var10 = var2.getZ();
      var1.playSoundEffect(var6 + 0.5, var8 + 0.5, var10 + 0.5, "random.fizz", 0.5F, 2.6F + (var1.rand.nextFloat() - var1.rand.nextFloat()) * 0.8F);

      for (int var12 = 0; var12 < 8; var12++) {
         var1.spawnParticle(EnumParticleTypes.SMOKE_LARGE, var6 + Math.random(), var8 + 1.2, var10 + Math.random(), 0.0, 0.0, 0.0);
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(LEVEL, var1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(LEVEL);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, LEVEL);
   }

   public static BlockDynamicLiquid getFlowingBlock(Material var0) {
      if (var0 == Material.water) {
         return Blocks.flowing_water;
      } else if (var0 == Material.lava) {
         return Blocks.flowing_lava;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }

   public static BlockStaticLiquid getStaticBlock(Material var0) {
      if (var0 == Material.water) {
         return Blocks.water;
      } else if (var0 == Material.lava) {
         return Blocks.lava;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }
}
