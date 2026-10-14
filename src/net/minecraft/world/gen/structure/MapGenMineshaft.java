package net.minecraft.world.gen.structure;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.MathHelper;

public class MapGenMineshaft extends MapGenStructure {
   private double field_82673_e = 0.004;

   public MapGenMineshaft() {
   }

   @Override
   public String getStructureName() {
      return "Mineshaft";
   }

   public MapGenMineshaft(Map<String, String> map) {
      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("chance")) {
            this.field_82673_e = MathHelper.parseDoubleWithDefault((String)entry.getValue(), this.field_82673_e);
         }
      }
   }

   @Override
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      return this.rand.nextDouble() < this.field_82673_e && this.rand.nextInt(80) < Math.max(Math.abs(chunkX), Math.abs(chunkZ));
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new StructureMineshaftStart(this.worldObj, this.rand, chunkX, chunkZ);
   }
}
