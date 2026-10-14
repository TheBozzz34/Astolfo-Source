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
      public boolean apply(IBlockState iBlockState) {
         return iBlockState != null && (iBlockState.getBlock() == Blocks.pumpkin || iBlockState.getBlock() == Blocks.lit_pumpkin);
      }
   };

   public BlockPumpkin() {
      super(Material.gourd, MapColor.adobeColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      super.onBlockAdded(worldIn, pos, state);
      this.trySpawnGolem(worldIn, pos);
   }

   public boolean canDispenserPlace(World worldIn, BlockPos pos) {
      return this.getSnowmanBasePattern().match(worldIn, pos) != null || this.getGolemBasePattern().match(worldIn, pos) != null;
   }

   private void trySpawnGolem(World worldIn, BlockPos pos) {
      BlockPattern.PatternHelper patternHelper;
      if ((patternHelper = this.getSnowmanPattern().match(worldIn, pos)) != null) {
         for (int i = 0; i < this.getSnowmanPattern().getThumbLength(); i++) {
            BlockWorldState blockWorldState = patternHelper.translateOffset(0, i, 0);
            worldIn.setBlockState(blockWorldState.getPos(), Blocks.air.getDefaultState(), 2);
         }

         EntitySnowman entitySnowman = new EntitySnowman(worldIn);
         BlockPos blockPos = patternHelper.translateOffset(0, 2, 0).getPos();
         entitySnowman.setLocationAndAngles(blockPos.getX() + 0.5, blockPos.getY() + 0.05, blockPos.getZ() + 0.5, 0.0F, 0.0F);
         worldIn.spawnEntityInWorld(entitySnowman);

         for (int j = 0; j < 120; j++) {
            worldIn.spawnParticle(
               EnumParticleTypes.SNOW_SHOVEL,
               blockPos.getX() + worldIn.rand.nextDouble(),
               blockPos.getY() + worldIn.rand.nextDouble() * 2.5,
               blockPos.getZ() + worldIn.rand.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }

         for (int k = 0; k < this.getSnowmanPattern().getThumbLength(); k++) {
            BlockWorldState blockWorldState2 = patternHelper.translateOffset(0, k, 0);
            worldIn.notifyNeighborsRespectDebug(blockWorldState2.getPos(), Blocks.air);
         }
      } else if ((patternHelper = this.getGolemPattern().match(worldIn, pos)) != null) {
         for (int l = 0; l < this.getGolemPattern().getPalmLength(); l++) {
            for (int m = 0; m < this.getGolemPattern().getThumbLength(); m++) {
               worldIn.setBlockState(patternHelper.translateOffset(l, m, 0).getPos(), Blocks.air.getDefaultState(), 2);
            }
         }

         BlockPos blockPos2 = patternHelper.translateOffset(1, 2, 0).getPos();
         EntityIronGolem entityIronGolem = new EntityIronGolem(worldIn);
         entityIronGolem.setPlayerCreated(true);
         entityIronGolem.setLocationAndAngles(blockPos2.getX() + 0.5, blockPos2.getY() + 0.05, blockPos2.getZ() + 0.5, 0.0F, 0.0F);
         worldIn.spawnEntityInWorld(entityIronGolem);

         for (int n = 0; n < 120; n++) {
            worldIn.spawnParticle(
               EnumParticleTypes.SNOWBALL,
               blockPos2.getX() + worldIn.rand.nextDouble(),
               blockPos2.getY() + worldIn.rand.nextDouble() * 3.9,
               blockPos2.getZ() + worldIn.rand.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }

         for (int i2 = 0; i2 < this.getGolemPattern().getPalmLength(); i2++) {
            for (int i3 = 0; i3 < this.getGolemPattern().getThumbLength(); i3++) {
               BlockWorldState blockWorldState3 = patternHelper.translateOffset(i2, i3, 0);
               worldIn.notifyNeighborsRespectDebug(blockWorldState3.getPos(), Blocks.air);
            }
         }
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getBlock().blockMaterial.isReplaceable() && World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(FACING).getHorizontalIndex();
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
