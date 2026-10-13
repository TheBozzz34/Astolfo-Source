package net.minecraft.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderDebug;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

public abstract class WorldProvider {
   public static final float[] moonPhaseFactors = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   public World worldObj;
   private WorldType terrainType;
   private String generatorSettings;
   public WorldChunkManager worldChunkMgr;
   public boolean isHellWorld;
   public boolean hasNoSky;
   public final float[] lightBrightnessTable = new float[16];
   public int dimensionId;
   private final float[] colorsSunriseSunset = new float[4];
   private static final long a = 15963653166596L;

   public final void registerWorld(World var1) {
      this.worldObj = var1;
      this.terrainType = var1.getWorldInfo().getTerrainType();
      this.generatorSettings = var1.getWorldInfo().getGeneratorOptions();
      this.registerWorldChunkManager();
      this.generateLightBrightnessTable();
   }

   public void generateLightBrightnessTable() {
      float var4 = 0.0F;

      for (int var5 = 0; var5 <= 15; var5++) {
         float var6 = 1.0F - var5 / 15.0F;
         this.lightBrightnessTable[var5] = (1.0F - var6) / (var6 * 3.0F + 1.0F) * (1.0F - var4) + var4;
      }
   }

   public void registerWorldChunkManager() {
      WorldType var4 = this.worldObj.getWorldInfo().getTerrainType();
      if (var4 == WorldType.FLAT) {
         FlatGeneratorInfo var5 = FlatGeneratorInfo.createFlatGeneratorFromString(this.worldObj.getWorldInfo().getGeneratorOptions());
         this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.getBiomeFromBiomeList(var5.getBiome(), BiomeGenBase.field_180279_ad), 0.5F);
      } else if (var4 == WorldType.DEBUG_WORLD) {
         this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.plains, 0.0F);
      } else {
         this.worldChunkMgr = new WorldChunkManager(this.worldObj);
      }
   }

   public IChunkProvider createChunkGenerator() {
      return this.terrainType == WorldType.FLAT
         ? new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings)
         : (
            this.terrainType == WorldType.DEBUG_WORLD
               ? new ChunkProviderDebug(this.worldObj)
               : (
                  this.terrainType == WorldType.CUSTOMIZED
                     ? new ChunkProviderGenerate(
                        this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings
                     )
                     : new ChunkProviderGenerate(
                        this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings
                     )
               )
         );
   }

   public boolean canCoordinateBeSpawn(int var1, int var2) {
      return this.worldObj.getGroundAboveSeaLevel(new BlockPos(var1, 0, var2)) == Blocks.grass;
   }

   public float calculateCelestialAngle(long var1, float var3) {
      int var7 = (int)(var1 % 24000L);
      float var8 = (var7 + var3) / 24000.0F - 0.25F;
      if (var8 < 0.0F) {
         var8++;
      }

      if (var8 > 1.0F) {
         var8--;
      }

      var8 = 1.0F - (float)((Math.cos(var8 * Math.PI) + 1.0) / 2.0);
      return var8 + (var8 - var8) / 3.0F;
   }

   public int getMoonPhase(long var1) {
      return (int)(var1 / 24000L % 8L + 8L) % 8;
   }

   public boolean isSurfaceWorld() {
      return true;
   }

   public float[] calcSunriseSunsetColors(float var1, float var2) {
      float var6 = 0.4F;
      float var7 = MathHelper.cos(var1 * (float) Math.PI * 2.0F) - 0.0F;
      float var8 = -0.0F;
      if (var7 >= var8 - var6 && var7 <= var8 + var6) {
         float var9 = (var7 - var8) / var6 * 0.5F + 0.5F;
         float var10 = 1.0F - (1.0F - MathHelper.sin(var9 * (float) Math.PI)) * 0.99F;
         var10 *= var10;
         this.colorsSunriseSunset[0] = var9 * 0.3F + 0.7F;
         this.colorsSunriseSunset[1] = var9 * var9 * 0.7F + 0.2F;
         this.colorsSunriseSunset[2] = var9 * var9 * 0.0F + 0.2F;
         this.colorsSunriseSunset[3] = var10;
         return this.colorsSunriseSunset;
      } else {
         return null;
      }
   }

   public Vec3 getFogColor(float var1, float var2) {
      float var3 = MathHelper.cos(var1 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      var3 = MathHelper.clamp_float(var3, 0.0F, 1.0F);
      float var4 = 0.7529412F;
      float var5 = 0.84705883F;
      float var6 = 1.0F;
      var4 *= var3 * 0.94F + 0.06F;
      var5 *= var3 * 0.94F + 0.06F;
      var6 *= var3 * 0.91F + 0.09F;
      return new Vec3(var4, var5, var6);
   }

   public boolean canRespawnHere() {
      return true;
   }

   public static WorldProvider getProviderForDimension(int var0) {
      return var0 == -1 ? new WorldProviderHell() : (var0 == 0 ? new WorldProviderSurface() : (var0 == 1 ? new WorldProviderEnd() : null));
   }

   public float getCloudHeight() {
      return 128.0F;
   }

   public boolean isSkyColored() {
      return true;
   }

   public BlockPos getSpawnCoordinate() {
      return null;
   }

   public int getAverageGroundLevel() {
      return this.terrainType == WorldType.FLAT ? 4 : this.worldObj.getSeaLevel() + 1;
   }

   public double getVoidFogYFactor() {
      return this.terrainType == WorldType.FLAT ? 1.0 : 0.03125;
   }

   public boolean doesXZShowFog(int var1, int var2) {
      return false;
   }

   public abstract String getDimensionName();

   public abstract String getInternalNameSuffix();

   public WorldChunkManager getWorldChunkManager() {
      return this.worldChunkMgr;
   }

   public boolean doesWaterVaporize() {
      return this.isHellWorld;
   }

   public boolean getHasNoSky() {
      return this.hasNoSky;
   }

   public float[] getLightBrightnessTable() {
      return this.lightBrightnessTable;
   }

   public int getDimensionId() {
      return this.dimensionId;
   }

   public WorldBorder getWorldBorder() {
      return new WorldBorder();
   }
}
