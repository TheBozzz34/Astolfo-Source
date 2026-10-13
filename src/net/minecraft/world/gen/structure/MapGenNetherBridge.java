package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenNetherBridge extends MapGenStructure {
   private List<BiomeGenBase.SpawnListEntry> spawnList = Lists.newArrayList();
   private static final long c = 113486085321571L;

   public MapGenNetherBridge() {
      this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntityBlaze.class, 10, 2, 3));
      this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
      this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
      this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
   }

   @Override
   public String getStructureName() {
      return "Fortress";
   }

   public List<BiomeGenBase.SpawnListEntry> getSpawnList() {
      return this.spawnList;
   }

   @Override
   public boolean canSpawnStructureAtCoords(int var1, int var2) {
      int var6 = var1 >> 4;
      int var7 = var2 >> 4;
      this.rand.setSeed(var6 ^ var7 << 4 ^ this.worldObj.getSeed());
      this.rand.nextInt();
      return this.rand.nextInt(3) != 0 ? false : (var1 != (var6 << 4) + 4 + this.rand.nextInt(8) ? false : var2 == (var7 << 4) + 4 + this.rand.nextInt(8));
   }

   @Override
   public StructureStart getStructureStart(int var1, int var2) {
      return new MapGenNetherBridge.Start(this.worldObj, this.rand, var1, var2);
   }

   public static class Start extends StructureStart {
      private static final long a = 33196611788234L;

      public Start() {
      }

      public Start(World var1, Random var2, int var3, int var4) {
         super(var3, var4);
         StructureNetherBridgePieces.Start var8 = new StructureNetherBridgePieces.Start(var2, (var3 << 4) + 2, (var4 << 4) + 2);
         this.components.add(var8);
         var8.buildComponent(var8, this.components, var2);
         List var9 = var8.field_74967_d;

         while (!var9.isEmpty()) {
            int var10 = var2.nextInt(var9.size());
            StructureComponent var11 = (StructureComponent)var9.remove(var10);
            var11.buildComponent(var8, this.components, var2);
         }

         this.updateBoundingBox();
         this.setRandomHeight(var1, var2, 48, 70);
      }
   }
}
