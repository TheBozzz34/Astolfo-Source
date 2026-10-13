package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimals;

public interface IMob extends IAnimals {
   Predicate<Entity> mobSelector = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1 instanceof IMob;
      }
   };
   Predicate<Entity> VISIBLE_MOB_SELECTOR = new Predicate<Entity>() {
      private static final long a = 102339919495147L;

      public boolean apply(Entity var1) {
         return var1 instanceof IMob && !var1.isInvisible();
      }
   };
}
