package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockBed extends BlockDirectional {
   public static final PropertyEnum<BlockBed.EnumPartType> PART = PropertyEnum.create("part", BlockBed.EnumPartType.class);
   public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");
   private static final long e = 25966967365841L;

   public BlockBed() {
      super(Material.cloth);
      this.setDefaultState(this.blockState.getBaseState().withProperty(PART, BlockBed.EnumPartType.FOOT).withProperty(OCCUPIED, false));
      this.setBedBounds();
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      if (state.getValue(PART) != BlockBed.EnumPartType.HEAD) {
         pos = pos.offset(state.getValue(FACING));
         state = worldIn.getBlockState(pos);
         if (state.getBlock() != this) {
            return true;
         }
      }

      if (worldIn.provider.canRespawnHere() && worldIn.getBiomeGenForCoords(pos) != BiomeGenBase.hell) {
         if (state.getValue(OCCUPIED)) {
            EntityPlayer entityPlayer = this.getPlayerInBed(worldIn, pos);
            if (entityPlayer != null) {
               playerIn.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied"));
               return true;
            }

            state = state.withProperty(OCCUPIED, false);
            worldIn.setBlockState(pos, state, 4);
         }

         EntityPlayer.EnumStatus enumStatus = playerIn.trySleep(pos);
         if (enumStatus == EntityPlayer.EnumStatus.OK) {
            state = state.withProperty(OCCUPIED, true);
            worldIn.setBlockState(pos, state, 4);
            return true;
         }

         if (enumStatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
            playerIn.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep"));
         } else if (enumStatus == EntityPlayer.EnumStatus.NOT_SAFE) {
            playerIn.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe"));
         }

         return true;
      } else {
         worldIn.setBlockToAir(pos);
         BlockPos blockPos = pos.offset(state.getValue(FACING).getOpposite());
         if (worldIn.getBlockState(blockPos).getBlock() == this) {
            worldIn.setBlockToAir(blockPos);
         }

         worldIn.newExplosion((Entity)null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5.0F, true, true);
         return true;
      }
   }

   private EntityPlayer getPlayerInBed(World worldIn, BlockPos pos) {
      for (EntityPlayer entityPlayer : worldIn.playerEntities) {
         if (entityPlayer.isPlayerSleeping() && entityPlayer.playerLocation.equals(pos)) {
            return entityPlayer;
         }
      }

      return null;
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
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.setBedBounds();
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      EnumFacing enumFacing = state.getValue(FACING);
      if (state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
         if (worldIn.getBlockState(pos.offset(enumFacing.getOpposite())).getBlock() != this) {
            worldIn.setBlockToAir(pos);
         }
      } else if (worldIn.getBlockState(pos.offset(enumFacing)).getBlock() != this) {
         worldIn.setBlockToAir(pos);
         if (!worldIn.isRemote) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(PART) == BlockBed.EnumPartType.HEAD ? null : Items.bed;
   }

   private void setBedBounds() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
   }

   public static BlockPos getSafeExitLocation(World worldIn, BlockPos pos, int tries) {
      EnumFacing enumFacing = worldIn.getBlockState(pos).getValue(FACING);
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();

      for (int l = 0; l <= 1; l++) {
         int m = i - enumFacing.getFrontOffsetX() * l - 1;
         int n = k - enumFacing.getFrontOffsetZ() * l - 1;
         int i2 = m + 2;
         int i3 = n + 2;

         for (int i4 = m; i4 <= i2; i4++) {
            for (int i5 = n; i5 <= i3; i5++) {
               BlockPos blockPos = new BlockPos(i4, j, i5);
               if (hasRoomForPlayer(worldIn, blockPos)) {
                  if (tries <= 0) {
                     return blockPos;
                  }

                  tries--;
               }
            }
         }
      }

      return null;
   }

   public static boolean hasRoomForPlayer(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down())
         && !worldIn.getBlockState(pos).getBlock().getMaterial().isSolid()
         && !worldIn.getBlockState(pos.up()).getBlock().getMaterial().isSolid();
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (state.getValue(PART) == BlockBed.EnumPartType.FOOT) {
         super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
      }
   }

   @Override
   public int getMobilityFlag() {
      return 1;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.bed;
   }

   @Override
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (player.capabilities.isCreativeMode && state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
         BlockPos blockPos = pos.offset(state.getValue(FACING).getOpposite());
         if (worldIn.getBlockState(blockPos).getBlock() == this) {
            worldIn.setBlockToAir(blockPos);
         }
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing enumFacing = EnumFacing.getHorizontal(meta);
      return (meta & 8) > 0
         ? this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.HEAD).withProperty(FACING, enumFacing).withProperty(OCCUPIED, (meta & 4) > 0)
         : this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.FOOT).withProperty(FACING, enumFacing);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (state.getValue(PART) == BlockBed.EnumPartType.FOOT) {
         IBlockState iBlockState = worldIn.getBlockState(pos.offset(state.getValue(FACING)));
         if (iBlockState.getBlock() == this) {
            state = state.withProperty(OCCUPIED, iBlockState.getValue(OCCUPIED));
         }
      }

      return state;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i |= state.getValue(FACING).getHorizontalIndex();
      if (state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
         i |= 8;
         if (state.getValue(OCCUPIED)) {
            i |= 4;
         }
      }

      return i;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, PART, OCCUPIED);
   }

   public enum EnumPartType implements IStringSerializable {
      HEAD("head"),
      FOOT("foot");

      private final String name;

      EnumPartType(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
