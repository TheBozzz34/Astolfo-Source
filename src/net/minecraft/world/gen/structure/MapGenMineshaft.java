package net.minecraft.world.gen.structure;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.MathHelper;

public class MapGenMineshaft extends MapGenStructure {
   private double field_82673_e = 0.004;
   private static final long c = 130820885581902L;
   private static final long k = 6766356435203784784L;

   public MapGenMineshaft() {
   }

   @Override
   public String getStructureName() {
      return "Mineshaft";
   }

   public MapGenMineshaft(Map<String, String> var1) {
      for (Entry var6 : var1.entrySet()) {
         if (((String)var6.getKey()).equals("chance")) {
            this.field_82673_e = MathHelper.parseDoubleWithDefault((String)var6.getValue(), this.field_82673_e);
         }
      }
   }

   @Override
   public boolean canSpawnStructureAtCoords(int var1, int var2) {
      return this.rand.nextDouble() < this.field_82673_e && this.rand.nextInt((int)k) < Math.max(Math.abs(var1), Math.abs(var2));
   }

   @Override
   public StructureStart getStructureStart(int var1, int var2) {
      return new StructureMineshaftStart(this.worldObj, this.rand, var1, var2);
   }
}
