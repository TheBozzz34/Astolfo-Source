package net.minecraft.util;

import java.util.Collection;
import java.util.Random;

public class WeightedRandom {
   private static final long a = 103991711636747L;

   public static int getTotalWeight(Collection<? extends WeightedRandom.Item> collection) {
      int i = 0;

      for (WeightedRandom.Item item : collection) {
         i += item.itemWeight;
      }

      return i;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random randomIn, Collection<T> collection, int totalWeight) {
      if (totalWeight <= 0) {
         throw new IllegalArgumentException();
      }

      int i = randomIn.nextInt(totalWeight);
      return getRandomItem(collection, i);
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Collection<T> collection, int weight) {
      for (WeightedRandom.Item item : collection) {
         weight -= item.itemWeight;
         if (weight < 0) {
            return (T)item;
         }
      }

      return null;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random randomIn, Collection<T> collection) {
      return getRandomItem(randomIn, collection, getTotalWeight(collection));
   }

   public static class Item {
      public int itemWeight;

      public Item(int itemWeightIn) {
         this.itemWeight = itemWeightIn;
      }
   }
}
