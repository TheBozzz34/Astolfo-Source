package net.minecraft.util;

import java.util.Collection;
import java.util.Random;

public class WeightedRandom {
   private static final long a = 103991711636747L;

   public static int getTotalWeight(Collection<? extends WeightedRandom.Item> var0) {
      int var4 = 0;

      for (WeightedRandom.Item var6 : var0) {
         var4 += var6.itemWeight;
      }

      return var4;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random var0, Collection<T> var1, int var2) {
      if (var2 <= 0) {
         throw new IllegalArgumentException();
      }

      int var6 = var0.nextInt(var2);
      return getRandomItem(var1, var6);
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Collection<T> var0, int var1) {
      for (WeightedRandom.Item var6 : var0) {
         var1 -= var6.itemWeight;
         if (var1 < 0) {
            return (T)var6;
         }
      }

      return null;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random var0, Collection<T> var1) {
      return getRandomItem(var0, var1, getTotalWeight(var1));
   }

   public static class Item {
      public int itemWeight;

      public Item(int var1) {
         this.itemWeight = var1;
      }
   }
}
