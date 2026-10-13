package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockDirectional {
   private BlockPattern snowmanBasePattern;
   private BlockPattern snowmanPattern;
   private BlockPattern golemBasePattern;
   private BlockPattern golemPattern;
   private static final Predicate<IBlockState> field_181085_Q = new Predicate<IBlockState>() {
      private static final long a = 136028497038362L;

      public boolean apply(IBlockState var1) {
         return var1 != null && (var1.getBlock() == Blocks.pumpkin || var1.getBlock() == Blocks.lit_pumpkin);
      }
   };
   private static final long l = 105740951920552L;

   public BlockPumpkin() {
      super(Material.gourd, MapColor.adobeColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      super.onBlockAdded(var1, var2, var3);
      this.trySpawnGolem(var1, var2);
   }

   public boolean canDispenserPlace(World var1, BlockPos var2) {
      return this.getSnowmanBasePattern().match(var1, var2) != null || this.getGolemBasePattern().match(var1, var2) != null;
   }

   private void trySpawnGolem(World var1, BlockPos var2) {
      BlockPattern.PatternHelper var6;
      if ((var6 = this.getSnowmanPattern().match(var1, var2)) != null) {
         for (int var7 = 0; var7 < this.getSnowmanPattern().getThumbLength(); var7++) {
            BlockWorldState var8 = var6.translateOffset(0, var7, 0);
            var1.setBlockState(var8.getPos(), Blocks.air.getDefaultState(), 2);
         }

         EntitySnowman var13 = new EntitySnowman(var1);
         BlockPos var16 = var6.translateOffset(0, 2, 0).getPos();
         var13.setLocationAndAngles(var16.getX() + 0.5, var16.getY() + 0.05, var16.getZ() + 0.5, 0.0F, 0.0F);
         var1.spawnEntityInWorld(var13);

         for (int var9 = 0; var9 < 120; var9++) {
            var1.spawnParticle(
               EnumParticleTypes.SNOW_SHOVEL,
               var16.getX() + var1.rand.nextDouble(),
               var16.getY() + var1.rand.nextDouble() * 2.5,
               var16.getZ() + var1.rand.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }

         for (int var19 = 0; var19 < this.getSnowmanPattern().getThumbLength(); var19++) {
            BlockWorldState var10 = var6.translateOffset(0, var19, 0);
            var1.notifyNeighborsRespectDebug(var10.getPos(), Blocks.air);
         }
      } else if ((var6 = this.getGolemPattern().match(var1, var2)) != null) {
         for (int var14 = 0; var14 < this.getGolemPattern().getPalmLength(); var14++) {
            for (int var17 = 0; var17 < this.getGolemPattern().getThumbLength(); var17++) {
               var1.setBlockState(var6.translateOffset(var14, var17, 0).getPos(), Blocks.air.getDefaultState(), 2);
            }
         }

         BlockPos var15 = var6.translateOffset(1, 2, 0).getPos();
         EntityIronGolem var18 = new EntityIronGolem(var1);
         var18.setPlayerCreated(true);
         var18.setLocationAndAngles(var15.getX() + 0.5, var15.getY() + 0.05, var15.getZ() + 0.5, 0.0F, 0.0F);
         var1.spawnEntityInWorld(var18);

         for (int var20 = 0; var20 < 120; var20++) {
            var1.spawnParticle(
               EnumParticleTypes.SNOWBALL,
               var15.getX() + var1.rand.nextDouble(),
               var15.getY() + var1.rand.nextDouble() * 3.9,
               var15.getZ() + var1.rand.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }

         for (int var21 = 0; var21 < this.getGolemPattern().getPalmLength(); var21++) {
            for (int var22 = 0; var22 < this.getGolemPattern().getThumbLength(); var22++) {
               BlockWorldState var11 = var6.translateOffset(var21, var22, 0);
               var1.notifyNeighborsRespectDebug(var11.getPos(), Blocks.air);
            }
         }
      }
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return var1.getBlockState(var2).getBlock().blockMaterial.isReplaceable() && World.doesBlockHaveSolidTopSurface(var1, var2.down());
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, var8.getHorizontalFacing().getOpposite());
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(var1));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(FACING).getHorizontalIndex();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING);
   }

   public BlockPattern getSnowmanBasePattern() {
      if (this.snowmanBasePattern == null) {
         this.snowmanBasePattern = FactoryBlockPattern.start()
            .aisle(" ", "#", "#")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow)))
            .build();
      }

      return this.snowmanBasePattern;
   }

   public BlockPattern getSnowmanPattern() {
      if (this.snowmanPattern == null) {
         this.snowmanPattern = FactoryBlockPattern.start()
            .aisle("^", "#", "#")
            .where('^', BlockWorldState.hasState(field_181085_Q))
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow)))
            .build();
      }

      return this.snowmanPattern;
   }

   public BlockPattern getGolemBasePattern() {
      if (this.golemBasePattern == null) {
         this.golemBasePattern = FactoryBlockPattern.start()
            .aisle("~ ~", "###", "~#~")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block)))
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.golemBasePattern;
   }

   public BlockPattern getGolemPattern() {
      if (this.golemPattern == null) {
         this.golemPattern = FactoryBlockPattern.start()
            .aisle("~^~", "###", "~#~")
            .where('^', BlockWorldState.hasState(field_181085_Q))
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block)))
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.golemPattern;
   }
}
