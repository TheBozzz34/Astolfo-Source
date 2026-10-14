package net.hypixel.api.pets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PetStats {
   private Map<PetType, Pet> petMap = new HashMap<>();

   public PetStats(Map<String, Map<String, Object>> petStats) {
      for (Entry entry : petStats.entrySet()) {
         try {
            this.petMap.put(PetType.valueOf((String)entry.getKey()), new Pet((Map<String, Object>)entry.getValue()));
         } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Invalid pet! " + (String)entry.getKey());
         }
      }
   }

   public Pet getPet(PetType typeIn) {
      return this.petMap.get(typeIn);
   }

   public Map<PetType, Pet> getAllPets() {
      return this.petMap;
   }

   @Override
   public String toString() {
      return "PetStats{petMap=" + this.petMap + '}';
   }
}
