package bypass.loader.check;

import bypass.util.collection.ShapedMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.ItemFishFood;
import oshi.util.tuples.Pair;

public enum ValueCheckers implements Initializable {
   INSTANCE;

   private final ShapedMap<Class<?>, ValueCheckerHolder> j = new ValueCheckers.e();
   private static final ValueCheckers[] N = new ValueCheckers[]{INSTANCE};
   private static final long a = 117166835098974L;

   public ShapedMap<Class<?>, ValueCheckerHolder> Q() {
      return this.j;
   }

   @Override
   public void C() {
      this.j.put(NullPointerException.class, new ValueCheckerHolder(new ValueCheckers.xb()));
      this.j.put(Pair.class, new ValueCheckerHolder(new ValueCheckers.r1g()));
      this.j.put(EntityPig.class, new ValueCheckerHolder(new ValueCheckers.rbz()));
      this.j.put(EntitySkeleton.class, new ValueCheckerHolder(new ValueCheckers.yp()));
      this.j.put(Block.class, new ValueCheckerHolder(new ValueCheckers.axp()));
      this.j.put(ItemFishFood.class, new ValueCheckerHolder(new ValueCheckers.riu()));
      this.j.put(EnvironmentKindD.class, new ValueCheckerHolder(new ValueCheckers.r1_()));
      this.j.put(EnvironmentKindC.class, new ValueCheckerHolder(new ValueCheckers.kw()));
   }

   public class axp implements ValueChecker {
      private static final long a = 29266979065118L;

      @Override
      public ValueCheckOutcome resolveObject(Object var1) {
         AbstractValueCheck var2 = ValueCheckFactory.INSTANCE
            .Z((Block)var1, var0 -> ((Block)var0).getMapColor(null) == MapColor.adobeColor ? -63872 : 0, var0 -> var0 != -63872);
         return new ValueCheckOutcome(var2.L(), var2.o(), var2.C());
      }
   }

   public class e extends ShapedMap<Class<?>, ValueCheckerHolder> {
      private final ValueCheckerHolder m = new ValueCheckerHolder(new ValueCheckers.e.ahr());
      private static final long a = 93872548270841L;

      public ValueCheckerHolder get(Object var1) {
         if (var1 == null) {
            return this.m;
         }

         ValueCheckerHolder var5 = (ValueCheckerHolder)super.get(var1);
         if (var5 != null) {
            ValueChecker var6 = var5.O();
            if (var6 != null) {
               return var5;
            }
         }

         return null;
      }

      public class ahr implements ValueChecker {
         @Override
         public ValueCheckOutcome resolveObject(Object var1) {
            return null;
         }
      }
   }

   public class kw implements ValueChecker {
      @Override
      public ValueCheckOutcome resolveObject(Object var1) {
         return null;
      }
   }

   public class r1_ implements ValueChecker {
      private static final long a = 109160864727063L;

      @Override
      public ValueCheckOutcome resolveObject(Object var1) {
         AbstractValueCheck var2 = ValueCheckFactory.INSTANCE.Z(var1, var0 -> var0 != null ? 1 : 0, var0 -> var0 == 1);
         return new ValueCheckOutcome(var2.L(), var2.o(), var2.C());
      }
   }

   public class r1g implements ValueChecker {
      private static final long a = 46423525043036L;
      private static final long b = 3473136183062560780L;

      @Override
      public ValueCheckOutcome resolveObject(Object var1) {
         AbstractValueCheck var2 = ValueCheckFactory.INSTANCE.Z((Pair)var1, var0 -> ((String)((Pair)var0).getA()).length(), var0 -> var0 == (int)b);
         return new ValueCheckOutcome(var2.L(), var2.o(), var2.C());
      }
   }

   public class rbz implements ValueChecker {
      private static final long a = 97265224022355L;
      private static final long b = -1228725826250207122L;

      @Override
      public ValueCheckOutcome resolveObject(Object var1) {
         AbstractValueCheck var5 = ValueCheckFactory.INSTANCE.Z((EntityPig)var1, Entity::getEntityId, var0 -> var0 > (int)b);
         return new ValueCheckOutcome(var5.L(), var5.o(), var5.C());
      }
   }

   public class riu implements ValueChecker {
      private static final long a = 28941834344374L;

      @Override
      public ValueCheckOutcome resolveObject(Object var1) {
         AbstractValueCheck var2 = ValueCheckFactory.INSTANCE
            .Z((ItemFishFood)var1, var0 -> ((ItemFishFood)var0).w() ? -5326515 : -362176356, var0 -> var0 < -5326515);
         return new ValueCheckOutcome(var2.L(), var2.o(), var2.C());
      }
   }

   public class xb implements ValueChecker {
      private static final long a = 110729714950113L;

      @Override
      public ValueCheckOutcome resolveObject(Object var1) {
         AbstractValueCheck var2 = ValueCheckFactory.INSTANCE.Z((NullPointerException)var1, var0 -> var0.getMessage().length(), var0 -> var0 == 1);
         return new ValueCheckOutcome(var2.L(), var2.o(), var2.C());
      }
   }

   public class yp implements ValueChecker {
      private static final long a = 101722366626685L;
      private static final long b = -1494641119991802408L;

      @Override
      public ValueCheckOutcome resolveObject(Object var1) {
         AbstractValueCheck var2 = ValueCheckFactory.INSTANCE.Z((EntitySkeleton)var1, Entity::getEntityId, var0 -> var0 < (int)b);
         return new ValueCheckOutcome(var2.L(), var2.o(), var2.C());
      }
   }
}
