package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer {
   public static final PropertyDirection FACING = PropertyDirection.create("facing");
   public static final PropertyBool NODROP = PropertyBool.create("nodrop");
   private static final Predicate<BlockWorldState> IS_WITHER_SKELETON = new Predicate<BlockWorldState>() {
      private static final long a = 7672341126908L;

      public boolean apply(BlockWorldState var1) {
         return var1.getBlockState() != null
            && var1.getBlockState().getBlock() == Blocks.skull
            && var1.getTileEntity() instanceof TileEntitySkull
            && ((TileEntitySkull)var1.getTileEntity()).getSkullType() == 1;
      }
   };
   private BlockPattern witherBasePattern;
   private BlockPattern witherPattern;
   private static final long i = 61880740559330L;

   public BlockSkull() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(NODROP, false));
      this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("tile.skull.skeleton.name");
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
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      switch ((EnumFacing)var1.getBlockState(var2).getValue(FACING)) {
         case UP:
         default:
            this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
            break;
         case NORTH:
            this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
            break;
         case SOUTH:
            this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
            break;
         case WEST:
            this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
            break;
         case EAST:
            this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.getCollisionBoundingBox(var1, var2, var3);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, var8.getHorizontalFacing()).withProperty(NODROP, false);
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntitySkull();
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Items.skull;
   }

   @Override
   public int getDamageValue(World var1, BlockPos var2) {
      TileEntity var6 = var1.getTileEntity(var2);
      return var6 instanceof TileEntitySkull ? ((TileEntitySkull)var6).getSkullType() : super.getDamageValue(var1, var2);
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
   }

   @Override
   public void onBlockHarvested(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (var4.capabilities.isCreativeMode) {
         var3 = var3.withProperty(NODROP, true);
         var1.setBlockState(var2, var3, 4);
      }

      super.onBlockHarvested(var1, var2, var3, var4);
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.isRemote) {
         if (!var3.getValue(NODROP)) {
            TileEntity var7 = var1.getTileEntity(var2);
            if (var7 instanceof TileEntitySkull) {
               TileEntitySkull var8 = (TileEntitySkull)var7;
               ItemStack var9 = new ItemStack(Items.skull, 1, this.getDamageValue(var1, var2));
               if (var8.getSkullType() == 3 && var8.getPlayerProfile() != null) {
                  var9.setTagCompound(new NBTTagCompound());
                  NBTTagCompound var10 = new NBTTagCompound();
                  NBTUtil.writeGameProfile(var10, var8.getPlayerProfile());
                  var9.getTagCompound().setTag("SkullOwner", var10);
               }

               spawnAsEntity(var1, var2, var9);
            }
         }

         super.breakBlock(var1, var2, var3);
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.skull;
   }

   public boolean canDispenserPlace(World var1, BlockPos var2, ItemStack var3) {
      return var3.getMetadata() == 1 && var2.getY() >= 2 && var1.getDifficulty() != EnumDifficulty.PEACEFUL && !var1.isRemote
         ? this.getWitherBasePattern().match(var1, var2) != null
         : false;
   }

   public void checkWitherSpawn(World var1, BlockPos var2, TileEntitySkull var3) {
      if (var3.getSkullType() == 1 && var2.getY() >= 2 && var1.getDifficulty() != EnumDifficulty.PEACEFUL && !var1.isRemote) {
         BlockPattern var7 = this.getWitherPattern();
         BlockPattern.PatternHelper var8 = var7.match(var1, var2);
         if (var8 != null) {
            for (int var9 = 0; var9 < 3; var9++) {
               BlockWorldState var10 = var8.translateOffset(var9, 0, 0);
               var1.setBlockState(var10.getPos(), var10.getBlockState().withProperty(NODROP, true), 2);
            }

            for (int var15 = 0; var15 < var7.getPalmLength(); var15++) {
               for (int var17 = 0; var17 < var7.getThumbLength(); var17++) {
                  BlockWorldState var11 = var8.translateOffset(var15, var17, 0);
                  var1.setBlockState(var11.getPos(), Blocks.air.getDefaultState(), 2);
               }
            }

            BlockPos var16 = var8.translateOffset(1, 0, 0).getPos();
            EntityWither var18 = new EntityWither(var1);
            BlockPos var19 = var8.translateOffset(1, 2, 0).getPos();
            var18.setLocationAndAngles(
               var19.getX() + 0.5, var19.getY() + 0.55, var19.getZ() + 0.5, var8.getFinger().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F, 0.0F
            );
            var18.renderYawOffset = var8.getFinger().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F;
            var18.func_82206_m();

            for (EntityPlayer var13 : var1.getEntitiesWithinAABB(EntityPlayer.class, var18.getEntityBoundingBox().expand(50.0, 50.0, 50.0))) {
               var13.triggerAchievement(AchievementList.spawnWither);
            }

            var1.spawnEntityInWorld(var18);

            for (int var20 = 0; var20 < 120; var20++) {
               var1.spawnParticle(
                  EnumParticleTypes.SNOWBALL,
                  var16.getX() + var1.rand.nextDouble(),
                  var16.getY() - 2 + var1.rand.nextDouble() * 3.9,
                  var16.getZ() + var1.rand.nextDouble(),
                  0.0,
                  0.0,
                  0.0
               );
            }

            for (int var21 = 0; var21 < var7.getPalmLength(); var21++) {
               for (int var22 = 0; var22 < var7.getThumbLength(); var22++) {
                  BlockWorldState var14 = var8.translateOffset(var21, var22, 0);
                  var1.notifyNeighborsRespectDebug(var14.getPos(), Blocks.air);
               }
            }
         }
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(var1 & 7)).withProperty(NODROP, (var1 & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getIndex();
      if (var1.getValue(NODROP)) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, NODROP);
   }

   public BlockPattern getWitherBasePattern() {
      if (this.witherBasePattern == null) {
         this.witherBasePattern = FactoryBlockPattern.start()
            .aisle("   ", "###", "~#~")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand)))
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.witherBasePattern;
   }

   public BlockPattern getWitherPattern() {
      if (this.witherPattern == null) {
         this.witherPattern = FactoryBlockPattern.start()
            .aisle("^^^", "###", "~#~")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand)))
            .where('^', IS_WITHER_SKELETON)
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.witherPattern;
   }
}
