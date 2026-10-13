package net.minecraft.world;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;

public class WorldProviderEnd extends WorldProvider {
   private static final long b = 4099583918379L;

   @Override
   public void registerWorldChunkManager() {
      this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F);
      this.dimensionId = 1;
      this.hasNoSky = true;
   }

   @Override
   public IChunkProvider createChunkGenerator() {
      return new ChunkProviderEnd(this.worldObj, this.worldObj.getSeed());
   }

   @Override
   public float calculateCelestialAngle(long var1, float var3) {
      return 0.0F;
   }

   @Override
   public float[] calcSunriseSunsetColors(float var1, float var2) {
      return null;
   }

   @Override
   public Vec3 getFogColor(float var1, float var2) {
      int var5 = 10518688;
      float var6 = MathHelper.cos(var1 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      var6 = MathHelper.clamp_float(var6, 0.0F, 1.0F);
      float var7 = (var5 >> 16 & 0xFF) / 255.0F;
      float var8 = (var5 >> 8 & 0xFF) / 255.0F;
      float var9 = (var5 & 0xFF) / 255.0F;
      var7 *= var6 * 0.0F + 0.15F;
      var8 *= var6 * 0.0F + 0.15F;
      var9 *= var6 * 0.0F + 0.15F;
      return new Vec3(var7, var8, var9);
   }

   @Override
   public boolean isSkyColored() {
      return false;
   }

   @Override
   public boolean canRespawnHere() {
      return false;
   }

   @Override
   public boolean isSurfaceWorld() {
      return false;
   }

   @Override
   public float getCloudHeight() {
      return 8.0F;
   }

   @Override
   public boolean canCoordinateBeSpawn(int var1, int var2) {
      return this.worldObj.getGroundAboveSeaLevel(new BlockPos(var1, 0, var2)).getMaterial().blocksMovement();
   }

   @Override
   public BlockPos getSpawnCoordinate() {
      return new BlockPos(100, 50, 0);
   }

   @Override
   public int getAverageGroundLevel() {
      return 50;
   }

   @Override
   public boolean doesXZShowFog(int var1, int var2) {
      return true;
   }

   @Override
   public String getDimensionName() {
      return "The End";
   }

   @Override
   public String getInternalNameSuffix() {
      return "_end";
   }
}
