package net.hypixel.api.pets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PetStats {
   private Map<PetType, Pet> petMap = new HashMap<>();
   private static final long a = 25214044200666L;
   private static final long e = -4318521008320413571L;

   public PetStats(Map<String, Map<String, Object>> var1) {
      for (Entry var6 : var1.entrySet()) {
         try {
            this.petMap.put(PetType.valueOf((String)var6.getKey()), new Pet((Map<String, Object>)var6.getValue()));
         } catch (IllegalArgumentException var8) {
            System.out.println("Invalid pet! " + (String)var6.getKey());
         }
      }
   }

   public Pet getPet(PetType var1) {
      return this.petMap.get(var1);
   }

   public Map<PetType, Pet> getAllPets() {
      return this.petMap;
   }

   @Override
   public String toString() {
      return "PetStats{petMap=" + this.petMap + (int)e;
   }
}
