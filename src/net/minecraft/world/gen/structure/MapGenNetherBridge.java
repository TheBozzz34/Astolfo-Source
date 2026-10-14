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
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int i = chunkX >> 4;
      int j = chunkZ >> 4;
      this.rand.setSeed(i ^ j << 4 ^ this.worldObj.getSeed());
      this.rand.nextInt();
      return this.rand.nextInt(3) != 0 ? false : (chunkX != (i << 4) + 4 + this.rand.nextInt(8) ? false : chunkZ == (j << 4) + 4 + this.rand.nextInt(8));
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new MapGenNetherBridge.Start(this.worldObj, this.rand, chunkX, chunkZ);
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(World worldIn, Random random2, int i, int j) {
         super(i, j);
         StructureNetherBridgePieces.Start start = new StructureNetherBridgePieces.Start(random2, (i << 4) + 2, (j << 4) + 2);
         this.components.add(start);
         start.buildComponent(start, this.components, random2);
         List list = start.field_74967_d;

         while (!list.isEmpty()) {
            int k = random2.nextInt(list.size());
            StructureComponent structureComponent = (StructureComponent)list.remove(k);
            structureComponent.buildComponent(start, this.components, random2);
         }

         this.updateBoundingBox();
         this.setRandomHeight(worldIn, random2, 48, 70);
      }
   }
}
