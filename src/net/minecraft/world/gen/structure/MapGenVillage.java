package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenVillage extends MapGenStructure {
   public static final List<BiomeGenBase> villageSpawnBiomes = Arrays.asList(BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.savanna);
   private int terrainType;
   private int field_82665_g = 32;
   private int field_82666_h = 8;
   private static final long c = 92214098246919L;

   public MapGenVillage() {
   }

   public MapGenVillage(Map<String, String> var1) {
      this();

      for (Entry var6 : var1.entrySet()) {
         if (((String)var6.getKey()).equals("size")) {
            this.terrainType = MathHelper.parseIntWithDefaultAndMax((String)var6.getValue(), this.terrainType, 0);
         } else if (((String)var6.getKey()).equals("distance")) {
            this.field_82665_g = MathHelper.parseIntWithDefaultAndMax((String)var6.getValue(), this.field_82665_g, this.field_82666_h + 1);
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Village";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int var1, int var2) {
      int var6 = var1;
      int var7 = var2;
      if (var1 < 0) {
         var1 -= this.field_82665_g - 1;
      }

      if (var2 < 0) {
         var2 -= this.field_82665_g - 1;
      }

      int var8 = var1 / this.field_82665_g;
      int var9 = var2 / this.field_82665_g;
      Random var10 = this.worldObj.setRandomSeed(var8, var9, 10387312);
      var8 *= this.field_82665_g;
      var9 *= this.field_82665_g;
      var8 += var10.nextInt(this.field_82665_g - this.field_82666_h);
      var9 += var10.nextInt(this.field_82665_g - this.field_82666_h);
      if (var6 == var8 && var7 == var9) {
         boolean var11 = this.worldObj.getWorldChunkManager().areBiomesViable(var6 * 16 + 8, var7 * 16 + 8, 0, villageSpawnBiomes);
         if (var11) {
            return true;
         }
      }

      return false;
   }

   @Override
   public StructureStart getStructureStart(int var1, int var2) {
      return new MapGenVillage.Start(this.worldObj, this.rand, var1, var2, this.terrainType);
   }

   public static class Start extends StructureStart {
      private boolean hasMoreThanTwoComponents;
      private static final long a = 133109328593550L;

      public Start() {
      }

      public Start(World var1, Random var2, int var3, int var4, int var5) {
         super(var3, var4);
         List var9 = StructureVillagePieces.getStructureVillageWeightedPieceList(var2, var5);
         StructureVillagePieces.Start var10 = new StructureVillagePieces.Start(
            var1.getWorldChunkManager(), 0, var2, (var3 << 4) + 2, (var4 << 4) + 2, var9, var5
         );
         this.components.add(var10);
         var10.buildComponent(var10, this.components, var2);
         List var11 = var10.field_74930_j;
         List var12 = var10.field_74932_i;

         while (!var11.isEmpty() || !var12.isEmpty()) {
            if (var11.isEmpty()) {
               int var13 = var2.nextInt(var12.size());
               StructureComponent var14 = (StructureComponent)var12.remove(var13);
               var14.buildComponent(var10, this.components, var2);
            } else {
               int var16 = var2.nextInt(var11.size());
               StructureComponent var18 = (StructureComponent)var11.remove(var16);
               var18.buildComponent(var10, this.components, var2);
            }
         }

         this.updateBoundingBox();
         int var17 = 0;

         for (StructureComponent var15 : this.components) {
            if (!(var15 instanceof StructureVillagePieces.Road)) {
               var17++;
            }
         }

         this.hasMoreThanTwoComponents = var17 > 2;
      }

      @Override
      public boolean isSizeableStructure() {
         return this.hasMoreThanTwoComponents;
      }

      @Override
      public void writeToNBT(NBTTagCompound var1) {
         super.writeToNBT(var1);
         var1.setBoolean("Valid", this.hasMoreThanTwoComponents);
      }

      @Override
      public void readFromNBT(NBTTagCompound var1) {
         super.readFromNBT(var1);
         this.hasMoreThanTwoComponents = var1.getBoolean("Valid");
      }
   }
}
