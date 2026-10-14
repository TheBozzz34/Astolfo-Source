package net.minecraft.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess {
   public int chunkX;
   public int chunkZ;
   public Chunk[][] chunkArray;
   public boolean hasExtendedLevels;
   public World worldObj;

   public ChunkCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
      this.worldObj = worldIn;
      this.chunkX = posFromIn.getX() - subIn >> 4;
      this.chunkZ = posFromIn.getZ() - subIn >> 4;
      int i = posToIn.getX() + subIn >> 4;
      int j = posToIn.getZ() + subIn >> 4;
      this.chunkArray = new Chunk[i - this.chunkX + 1][j - this.chunkZ + 1];
      this.hasExtendedLevels = true;

      for (int k = this.chunkX; k <= i; k++) {
         for (int l = this.chunkZ; l <= j; l++) {
            this.chunkArray[k - this.chunkX][l - this.chunkZ] = worldIn.getChunkFromChunkCoords(k, l);
         }
      }

      for (int m = posFromIn.getX() >> 4; m <= posToIn.getX() >> 4; m++) {
         for (int n = posFromIn.getZ() >> 4; n <= posToIn.getZ() >> 4; n++) {
            Chunk chunk = this.chunkArray[m - this.chunkX][n - this.chunkZ];
            if (chunk != null && !chunk.getAreLevelsEmpty(posFromIn.getY(), posToIn.getY())) {
               this.hasExtendedLevels = false;
            }
         }
      }
   }

   @Override
   public boolean extendedLevelsInChunkCache() {
      return this.hasExtendedLevels;
   }

   @Override
   public TileEntity getTileEntity(BlockPos pos) {
      int i = (pos.getX() >> 4) - this.chunkX;
      int j = (pos.getZ() >> 4) - this.chunkZ;
      return this.chunkArray[i][j].getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
   }

   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int i = this.getLightForExt(EnumSkyBlock.SKY, pos);
      int j = this.getLightForExt(EnumSkyBlock.BLOCK, pos);
      if (j < lightValue) {
         j = lightValue;
      }

      return i << 20 | j << 4;
   }

   @Override
   public IBlockState getBlockState(BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int i = (pos.getX() >> 4) - this.chunkX;
         int j = (pos.getZ() >> 4) - this.chunkZ;
         if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length) {
            Chunk chunk = this.chunkArray[i][j];
            if (chunk != null) {
               return chunk.getBlockState(pos);
            }
         }
      }

      return Blocks.air.getDefaultState();
   }

   @Override
   public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
      return this.worldObj.getBiomeGenForCoords(pos);
   }

   private int getLightForExt(EnumSkyBlock enumSkyBlock, BlockPos pos) {
      if (enumSkyBlock == EnumSkyBlock.SKY && this.worldObj.provider.getHasNoSky()) {
         return 0;
      }

      if (pos.getY() >= 0 && pos.getY() < 256) {
         if (this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
            int i = 0;

            for (EnumFacing enumFacing : EnumFacing.values()) {
               int j = this.getLightFor(enumSkyBlock, pos.offset(enumFacing));
               if (j > i) {
                  i = j;
               }

               if (i >= 15) {
                  return i;
               }
            }

            return i;
         } else {
            int k = (pos.getX() >> 4) - this.chunkX;
            int l = (pos.getZ() >> 4) - this.chunkZ;
            return this.chunkArray[k][l].getLightFor(enumSkyBlock, pos);
         }
      } else {
         return enumSkyBlock.defaultLightValue;
      }
   }

   @Override
   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
   }

   public int getLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int i = (pos.getX() >> 4) - this.chunkX;
         int j = (pos.getZ() >> 4) - this.chunkZ;
         return this.chunkArray[i][j].getLightFor(enumSkyBlock, pos);
      } else {
         return enumSkyBlock.defaultLightValue;
      }
   }

   @Override
   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      IBlockState iBlockState = this.getBlockState(pos);
      return iBlockState.getBlock().getStrongPower(this, pos, iBlockState, direction);
   }

   @Override
   public WorldType getWorldType() {
      return this.worldObj.getWorldType();
   }
}
