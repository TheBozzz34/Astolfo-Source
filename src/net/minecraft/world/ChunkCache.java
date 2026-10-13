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
   private static final long c = 90925683008963L;

   public ChunkCache(World var1, BlockPos var2, BlockPos var3, int var4) {
      this.worldObj = var1;
      this.chunkX = var2.getX() - var4 >> 4;
      this.chunkZ = var2.getZ() - var4 >> 4;
      int var8 = var3.getX() + var4 >> 4;
      int var9 = var3.getZ() + var4 >> 4;
      this.chunkArray = new Chunk[var8 - this.chunkX + 1][var9 - this.chunkZ + 1];
      this.hasExtendedLevels = true;

      for (int var10 = this.chunkX; var10 <= var8; var10++) {
         for (int var11 = this.chunkZ; var11 <= var9; var11++) {
            this.chunkArray[var10 - this.chunkX][var11 - this.chunkZ] = var1.getChunkFromChunkCoords(var10, var11);
         }
      }

      for (int var13 = var2.getX() >> 4; var13 <= var3.getX() >> 4; var13++) {
         for (int var14 = var2.getZ() >> 4; var14 <= var3.getZ() >> 4; var14++) {
            Chunk var12 = this.chunkArray[var13 - this.chunkX][var14 - this.chunkZ];
            if (var12 != null && !var12.getAreLevelsEmpty(var2.getY(), var3.getY())) {
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
   public TileEntity getTileEntity(BlockPos var1) {
      int var2 = (var1.getX() >> 4) - this.chunkX;
      int var3 = (var1.getZ() >> 4) - this.chunkZ;
      return this.chunkArray[var2][var3].getTileEntity(var1, Chunk.EnumCreateEntityType.IMMEDIATE);
   }

   @Override
   public int getCombinedLight(BlockPos var1, int var2) {
      int var6 = this.getLightForExt(EnumSkyBlock.SKY, var1);
      int var7 = this.getLightForExt(EnumSkyBlock.BLOCK, var1);
      if (var7 < var2) {
         var7 = var2;
      }

      return var6 << 20 | var7 << 4;
   }

   @Override
   public IBlockState getBlockState(BlockPos var1) {
      if (var1.getY() >= 0 && var1.getY() < 256) {
         int var5 = (var1.getX() >> 4) - this.chunkX;
         int var6 = (var1.getZ() >> 4) - this.chunkZ;
         if (var5 >= 0 && var5 < this.chunkArray.length && var6 >= 0 && var6 < this.chunkArray[var5].length) {
            Chunk var7 = this.chunkArray[var5][var6];
            if (var7 != null) {
               return var7.getBlockState(var1);
            }
         }
      }

      return Blocks.air.getDefaultState();
   }

   @Override
   public BiomeGenBase getBiomeGenForCoords(BlockPos var1) {
      return this.worldObj.getBiomeGenForCoords(var1);
   }

   private int getLightForExt(EnumSkyBlock var1, BlockPos var2) {
      if (var1 == EnumSkyBlock.SKY && this.worldObj.provider.getHasNoSky()) {
         return 0;
      }

      if (var2.getY() >= 0 && var2.getY() < 256) {
         if (this.getBlockState(var2).getBlock().getUseNeighborBrightness()) {
            int var12 = 0;

            for (EnumFacing var10 : EnumFacing.values()) {
               int var11 = this.getLightFor(var1, var2.offset(var10));
               if (var11 > var12) {
                  var12 = var11;
               }

               if (var12 >= 15) {
                  return var12;
               }
            }

            return var12;
         } else {
            int var6 = (var2.getX() >> 4) - this.chunkX;
            int var7 = (var2.getZ() >> 4) - this.chunkZ;
            return this.chunkArray[var6][var7].getLightFor(var1, var2);
         }
      } else {
         return var1.defaultLightValue;
      }
   }

   @Override
   public boolean isAirBlock(BlockPos var1) {
      return this.getBlockState(var1).getBlock().getMaterial() == Material.air;
   }

   public int getLightFor(EnumSkyBlock var1, BlockPos var2) {
      if (var2.getY() >= 0 && var2.getY() < 256) {
         int var6 = (var2.getX() >> 4) - this.chunkX;
         int var7 = (var2.getZ() >> 4) - this.chunkZ;
         return this.chunkArray[var6][var7].getLightFor(var1, var2);
      } else {
         return var1.defaultLightValue;
      }
   }

   @Override
   public int getStrongPower(BlockPos var1, EnumFacing var2) {
      IBlockState var3 = this.getBlockState(var1);
      return var3.getBlock().getStrongPower(this, var1, var3, var2);
   }

   @Override
   public WorldType getWorldType() {
      return this.worldObj.getWorldType();
   }
}
