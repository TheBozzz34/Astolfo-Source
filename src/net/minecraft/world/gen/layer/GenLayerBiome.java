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

   public GenLayerBiome(long var1, GenLayer var3, WorldType var4, String var5) {
      super(var1);
      this.parent = var3;
      if (var4 == WorldType.DEFAULT_1_1) {
         this.field_151623_c = new BiomeGenBase[]{
            BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga
         };
         this.field_175973_g = null;
      } else if (var4 == WorldType.CUSTOMIZED) {
         this.field_175973_g = ChunkProviderSettings.Factory.jsonToFactory(var5).func_177864_b();
      } else {
         this.field_175973_g = null;
      }
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      int[] var8 = this.parent.getInts(var1, var2, var3, var4);
      int[] var9 = IntCache.getIntCache(var3 * var4);

      for (int var10 = 0; var10 < var4; var10++) {
         for (int var11 = 0; var11 < var3; var11++) {
            this.initChunkSeed(var11 + var1, var10 + var2);
            int var12 = var8[var11 + var10 * var3];
            int var13 = (var12 & 3840) >> 8;
            var12 &= -3841;
            if (this.field_175973_g != null && this.field_175973_g.fixedBiome >= 0) {
               var9[var11 + var10 * var3] = this.field_175973_g.fixedBiome;
            } else if (isBiomeOceanic(var12)) {
               var9[var11 + var10 * var3] = var12;
            } else if (var12 == BiomeGenBase.mushroomIsland.biomeID) {
               var9[var11 + var10 * var3] = var12;
            } else if (var12 == 1) {
               if (var13 > 0) {
                  if (this.nextInt(3) == 0) {
                     var9[var11 + var10 * var3] = BiomeGenBase.mesaPlateau.biomeID;
                  } else {
                     var9[var11 + var10 * var3] = BiomeGenBase.mesaPlateau_F.biomeID;
                  }
               } else {
                  var9[var11 + var10 * var3] = this.field_151623_c[this.nextInt(this.field_151623_c.length)].biomeID;
               }
            } else if (var12 == 2) {
               if (var13 > 0) {
                  var9[var11 + var10 * var3] = BiomeGenBase.jungle.biomeID;
               } else {
                  var9[var11 + var10 * var3] = this.field_151621_d[this.nextInt(this.field_151621_d.length)].biomeID;
               }
            } else if (var12 == 3) {
               if (var13 > 0) {
                  var9[var11 + var10 * var3] = BiomeGenBase.megaTaiga.biomeID;
               } else {
                  var9[var11 + var10 * var3] = this.field_151622_e[this.nextInt(this.field_151622_e.length)].biomeID;
               }
            } else if (var12 == 4) {
               var9[var11 + var10 * var3] = this.field_151620_f[this.nextInt(this.field_151620_f.length)].biomeID;
            } else {
               var9[var11 + var10 * var3] = BiomeGenBase.mushroomIsland.biomeID;
            }
         }
      }

      return var9;
   }
}
