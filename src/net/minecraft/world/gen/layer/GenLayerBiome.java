package net.minecraft.world.gen.layer;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings;

public class GenLayerBiome extends GenLayer {
   private BiomeGenBase[] field_151623_c = new BiomeGenBase[]{
      BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.savanna, BiomeGenBase.savanna, BiomeGenBase.plains
   };
   private BiomeGenBase[] field_151621_d = new BiomeGenBase[]{
      BiomeGenBase.forest, BiomeGenBase.roofedForest, BiomeGenBase.extremeHills, BiomeGenBase.plains, BiomeGenBase.birchForest, BiomeGenBase.swampland
   };
   private BiomeGenBase[] field_151622_e = new BiomeGenBase[]{BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.taiga, BiomeGenBase.plains};
   private BiomeGenBase[] field_151620_f = new BiomeGenBase[]{BiomeGenBase.icePlains, BiomeGenBase.icePlains, BiomeGenBase.icePlains, BiomeGenBase.coldTaiga};
   private final ChunkProviderSettings field_175973_g;
   private static final long c = 23987885982097L;

   public GenLayerBiome(long l, GenLayer genLayer, WorldType worldType, String string) {
      super(l);
      this.parent = genLayer;
      if (worldType == WorldType.DEFAULT_1_1) {
         this.field_151623_c = new BiomeGenBase[]{
            BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga
         };
         this.field_175973_g = null;
      } else if (worldType == WorldType.CUSTOMIZED) {
         this.field_175973_g = ChunkProviderSettings.Factory.jsonToFactory(string).func_177864_b();
      } else {
         this.field_175973_g = null;
      }
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] ints = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] ints2 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaHeight; i++) {
         for (int j = 0; j < areaWidth; j++) {
            this.initChunkSeed(j + areaX, i + areaY);
            int k = ints[j + i * areaWidth];
            int l = (k & 3840) >> 8;
            k &= -3841;
            if (this.field_175973_g != null && this.field_175973_g.fixedBiome >= 0) {
               ints2[j + i * areaWidth] = this.field_175973_g.fixedBiome;
            } else if (isBiomeOceanic(k)) {
               ints2[j + i * areaWidth] = k;
            } else if (k == BiomeGenBase.mushroomIsland.biomeID) {
               ints2[j + i * areaWidth] = k;
            } else if (k == 1) {
               if (l > 0) {
                  if (this.nextInt(3) == 0) {
                     ints2[j + i * areaWidth] = BiomeGenBase.mesaPlateau.biomeID;
                  } else {
                     ints2[j + i * areaWidth] = BiomeGenBase.mesaPlateau_F.biomeID;
                  }
               } else {
                  ints2[j + i * areaWidth] = this.field_151623_c[this.nextInt(this.field_151623_c.length)].biomeID;
               }
            } else if (k == 2) {
               if (l > 0) {
                  ints2[j + i * areaWidth] = BiomeGenBase.jungle.biomeID;
               } else {
                  ints2[j + i * areaWidth] = this.field_151621_d[this.nextInt(this.field_151621_d.length)].biomeID;
               }
            } else if (k == 3) {
               if (l > 0) {
                  ints2[j + i * areaWidth] = BiomeGenBase.megaTaiga.biomeID;
               } else {
                  ints2[j + i * areaWidth] = this.field_151622_e[this.nextInt(this.field_151622_e.length)].biomeID;
               }
            } else if (k == 4) {
               ints2[j + i * areaWidth] = this.field_151620_f[this.nextInt(this.field_151620_f.length)].biomeID;
            } else {
               ints2[j + i * areaWidth] = BiomeGenBase.mushroomIsland.biomeID;
            }
         }
      }

      return ints2;
   }
}
