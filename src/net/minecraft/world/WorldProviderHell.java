package net.minecraft.world;

import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;

public class WorldProviderHell extends WorldProvider {
   private static final long b = 116964462484238L;
   private static final long k = -4576341839195406321L;

   @Override
   public void registerWorldChunkManager() {
      this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F);
      this.isHellWorld = true;
      this.hasNoSky = true;
      this.dimensionId = -1;
   }

   @Override
   public Vec3 getFogColor(float var1, float var2) {
      return new Vec3(0.2F, 0.03F, 0.03F);
   }

   @Override
   public void generateLightBrightnessTable() {
      float var4 = 0.1F;

      for (int var5 = 0; var5 <= (int)k; var5++) {
         float var6 = 1.0F - var5 / 15.0F;
         this.lightBrightnessTable[var5] = (1.0F - var6) / (var6 * 3.0F + 1.0F) * (1.0F - var4) + var4;
      }
   }

   @Override
   public IChunkProvider createChunkGenerator() {
      return new ChunkProviderHell(this.worldObj, this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.worldObj.getSeed());
   }

   @Override
   public boolean isSurfaceWorld() {
      return false;
   }

   @Override
   public boolean canCoordinateBeSpawn(int var1, int var2) {
      return false;
   }

   @Override
   public float calculateCelestialAngle(long var1, float var3) {
      return 0.5F;
   }

   @Override
   public boolean canRespawnHere() {
      return false;
   }

   @Override
   public boolean doesXZShowFog(int var1, int var2) {
      return true;
   }

   @Override
   public String getDimensionName() {
      return "Nether";
   }

   @Override
   public String getInternalNameSuffix() {
      return "_nether";
   }

   @Override
   public WorldBorder getWorldBorder() {
      return new WorldBorder() {
         @Override
         public double getCenterX() {
            return super.getCenterX() / 8.0;
         }

         @Override
         public double getCenterZ() {
            return super.getCenterZ() / 8.0;
         }
      };
   }
}
