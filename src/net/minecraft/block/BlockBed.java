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
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      if (var3.getValue(PART) != BlockBed.EnumPartType.HEAD) {
         var2 = var2.offset(var3.getValue(FACING));
         var3 = var1.getBlockState(var2);
         if (var3.getBlock() != this) {
            return true;
         }
      }

      if (var1.provider.canRespawnHere() && var1.getBiomeGenForCoords(var2) != BiomeGenBase.hell) {
         if (var3.getValue(OCCUPIED)) {
            EntityPlayer var14 = this.getPlayerInBed(var1, var2);
            if (var14 != null) {
               var4.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied"));
               return true;
            }

            var3 = var3.withProperty(OCCUPIED, false);
            var1.setBlockState(var2, var3, 4);
         }

         EntityPlayer.EnumStatus var15 = var4.trySleep(var2);
         if (var15 == EntityPlayer.EnumStatus.OK) {
            var3 = var3.withProperty(OCCUPIED, true);
            var1.setBlockState(var2, var3, 4);
            return true;
         }

         if (var15 == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
            var4.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep"));
         } else if (var15 == EntityPlayer.EnumStatus.NOT_SAFE) {
            var4.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe"));
         }

         return true;
      } else {
         var1.setBlockToAir(var2);
         BlockPos var12 = var2.offset(var3.getValue(FACING).getOpposite());
         if (var1.getBlockState(var12).getBlock() == this) {
            var1.setBlockToAir(var12);
         }

         var1.newExplosion((Entity)null, var2.getX() + 0.5, var2.getY() + 0.5, var2.getZ() + 0.5, 5.0F, true, true);
         return true;
      }
   }

   private EntityPlayer getPlayerInBed(World var1, BlockPos var2) {
      for (EntityPlayer var7 : var1.playerEntities) {
         if (var7.isPlayerSleeping() && var7.playerLocation.equals(var2)) {
            return var7;
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
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      this.setBedBounds();
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing var8 = var3.getValue(FACING);
      if (var3.getValue(PART) == BlockBed.EnumPartType.HEAD) {
         if (var1.getBlockState(var2.offset(var8.getOpposite())).getBlock() != this) {
            var1.setBlockToAir(var2);
         }
      } else if (var1.getBlockState(var2.offset(var8)).getBlock() != this) {
         var1.setBlockToAir(var2);
         if (!var1.isRemote) {
            this.dropBlockAsItem(var1, var2, var3, 0);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return var1.getValue(PART) == BlockBed.EnumPartType.HEAD ? null : Items.bed;
   }

   private void setBedBounds() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
   }

   public static BlockPos getSafeExitLocation(World var0, BlockPos var1, int var2) {
      EnumFacing var6 = var0.getBlockState(var1).getValue(FACING);
      int var7 = var1.getX();
      int var8 = var1.getY();
      int var9 = var1.getZ();

      for (int var10 = 0; var10 <= 1; var10++) {
         int var11 = var7 - var6.getFrontOffsetX() * var10 - 1;
         int var12 = var9 - var6.getFrontOffsetZ() * var10 - 1;
         int var13 = var11 + 2;
         int var14 = var12 + 2;

         for (int var15 = var11; var15 <= var13; var15++) {
            for (int var16 = var12; var16 <= var14; var16++) {
               BlockPos var17 = new BlockPos(var15, var8, var16);
               if (hasRoomForPlayer(var0, var17)) {
                  if (var2 <= 0) {
                     return var17;
                  }

                  var2--;
               }
            }
         }
      }

      return null;
   }

   public static boolean hasRoomForPlayer(World var0, BlockPos var1) {
      return World.doesBlockHaveSolidTopSurface(var0, var1.down())
         && !var0.getBlockState(var1).getBlock().getMaterial().isSolid()
         && !var0.getBlockState(var1.up()).getBlock().getMaterial().isSolid();
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (var3.getValue(PART) == BlockBed.EnumPartType.FOOT) {
         super.dropBlockAsItemWithChance(var1, var2, var3, var4, 0);
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
   public Item getItem(World var1, BlockPos var2) {
      return Items.bed;
   }

   @Override
   public void onBlockHarvested(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (var4.capabilities.isCreativeMode && var3.getValue(PART) == BlockBed.EnumPartType.HEAD) {
         BlockPos var8 = var2.offset(var3.getValue(FACING).getOpposite());
         if (var1.getBlockState(var8).getBlock() == this) {
            var1.setBlockToAir(var8);
         }
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      EnumFacing var5 = EnumFacing.getHorizontal(var1);
      return (var1 & 8) > 0
         ? this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.HEAD).withProperty(FACING, var5).withProperty(OCCUPIED, (var1 & 4) > 0)
         : this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.FOOT).withProperty(FACING, var5);
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      if (var1.getValue(PART) == BlockBed.EnumPartType.FOOT) {
         IBlockState var7 = var2.getBlockState(var3.offset(var1.getValue(FACING)));
         if (var7.getBlock() == this) {
            var1 = var1.withProperty(OCCUPIED, var7.getValue(OCCUPIED));
         }
      }

      return var1;
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getHorizontalIndex();
      if (var1.getValue(PART) == BlockBed.EnumPartType.HEAD) {
         var5 |= 8;
         if (var1.getValue(OCCUPIED)) {
            var5 |= 4;
         }
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, PART, OCCUPIED);
   }

   public enum EnumPartType implements IStringSerializable {
      HEAD("head"),
      FOOT("foot");

      private final String name;

      EnumPartType(String var3) {
         this.name = var3;
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
