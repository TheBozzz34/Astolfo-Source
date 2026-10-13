package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class StructureOceanMonument extends MapGenStructure {
   private int field_175800_f = 32;
   private int field_175801_g = 5;
   public static final List<BiomeGenBase> field_175802_d = Arrays.asList(
      BiomeGenBase.ocean, BiomeGenBase.deepOcean, BiomeGenBase.river, BiomeGenBase.frozenOcean, BiomeGenBase.frozenRiver
   );
   private static final List<BiomeGenBase.SpawnListEntry> field_175803_h = Lists.newArrayList();
   private static final long c = 49600259522505L;

   public StructureOceanMonument() {
   }

   public StructureOceanMonument(Map<String, String> var1) {
      this();

      for (Entry var6 : var1.entrySet()) {
         if (((String)var6.getKey()).equals("spacing")) {
            this.field_175800_f = MathHelper.parseIntWithDefaultAndMax((String)var6.getValue(), this.field_175800_f, 1);
         } else if (((String)var6.getKey()).equals("separation")) {
            this.field_175801_g = MathHelper.parseIntWithDefaultAndMax((String)var6.getValue(), this.field_175801_g, 1);
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Monument";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int var1, int var2) {
      int var6 = var1;
      int var7 = var2;
      if (var1 < 0) {
         var1 -= this.field_175800_f - 1;
      }

      if (var2 < 0) {
         var2 -= this.field_175800_f - 1;
      }

      int var8 = var1 / this.field_175800_f;
      int var9 = var2 / this.field_175800_f;
      Random var10 = this.worldObj.setRandomSeed(var8, var9, 10387313);
      var8 *= this.field_175800_f;
      var9 *= this.field_175800_f;
      var8 += (var10.nextInt(this.field_175800_f - this.field_175801_g) + var10.nextInt(this.field_175800_f - this.field_175801_g)) / 2;
      var9 += (var10.nextInt(this.field_175800_f - this.field_175801_g) + var10.nextInt(this.field_175800_f - this.field_175801_g)) / 2;
      if (var6 == var8 && var7 == var9) {
         if (this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(var6 * 16 + 8, 64, var7 * 16 + 8), (BiomeGenBase)null)
            != BiomeGenBase.deepOcean) {
            return false;
         }

         boolean var11 = this.worldObj.getWorldChunkManager().areBiomesViable(var6 * 16 + 8, var7 * 16 + 8, 29, field_175802_d);
         if (var11) {
            return true;
         }
      }

      return false;
   }

   @Override
   public StructureStart getStructureStart(int var1, int var2) {
      return new StructureOceanMonument.StartMonument(this.worldObj, this.rand, var1, var2);
   }

   public List<BiomeGenBase.SpawnListEntry> getScatteredFeatureSpawnList() {
      return field_175803_h;
   }

   static {
      field_175803_h.add(new BiomeGenBase.SpawnListEntry(EntityGuardian.class, 1, 2, 4));
   }

   public static class StartMonument extends StructureStart {
      private Set<ChunkCoordIntPair> field_175791_c = Sets.newHashSet();
      private boolean field_175790_d;
      private static final long a = 16372114707539L;

      public StartMonument() {
      }

      public StartMonument(World var1, Random var2, int var3, int var4) {
         super(var3, var4);
         this.func_175789_b(var1, var2, var3, var4);
      }

      private void func_175789_b(World var1, Random var2, int var3, int var4) {
         var2.setSeed(var1.getSeed());
         long var7 = var2.nextLong();
         long var9 = var2.nextLong();
         long var11 = var3 * var7;
         long var13 = var4 * var9;
         var2.setSeed(var11 ^ var13 ^ var1.getSeed());
         int var15 = var3 * 16 + 8 - 29;
         int var16 = var4 * 16 + 8 - 29;
         EnumFacing var17 = EnumFacing.Plane.HORIZONTAL.random(var2);
         this.components.add(new StructureOceanMonumentPieces.MonumentBuilding(var2, var15, var16, var17));
         this.updateBoundingBox();
         this.field_175790_d = true;
      }

      @Override
      public void generateStructure(World var1, Random var2, StructureBoundingBox var3) {
         if (!this.field_175790_d) {
            this.components.clear();
            this.func_175789_b(var1, var2, this.getChunkPosX(), this.getChunkPosZ());
         }

         super.generateStructure(var1, var2, var3);
      }

      @Override
      public boolean func_175788_a(ChunkCoordIntPair var1) {
         return this.field_175791_c.contains(var1) ? false : super.func_175788_a(var1);
      }

      @Override
      public void func_175787_b(ChunkCoordIntPair var1) {
         super.func_175787_b(var1);
         this.field_175791_c.add(var1);
      }

      @Override
      public void writeToNBT(NBTTagCompound var1) {
         super.writeToNBT(var1);
         NBTTagList var5 = new NBTTagList();

         for (ChunkCoordIntPair var7 : this.field_175791_c) {
            NBTTagCompound var8 = new NBTTagCompound();
            var8.setInteger("X", var7.chunkXPos);
            var8.setInteger("Z", var7.chunkZPos);
            var5.appendTag(var8);
         }

         var1.setTag("Processed", var5);
      }

      @Override
      public void readFromNBT(NBTTagCompound var1) {
         super.readFromNBT(var1);
         if (var1.hasKey("Processed", 9)) {
            NBTTagList var5 = var1.getTagList("Processed", 10);

            for (int var6 = 0; var6 < var5.tagCount(); var6++) {
               NBTTagCompound var7 = var5.getCompoundTagAt(var6);
               this.field_175791_c.add(new ChunkCoordIntPair(var7.getInteger("X"), var7.getInteger("Z")));
            }
         }
      }
   }
}
