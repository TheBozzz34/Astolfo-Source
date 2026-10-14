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
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE
            .Z((Block)object, block -> ((Block)block).getMapColor(null) == MapColor.adobeColor ? -63872 : 0, integer -> integer != -63872);
         return new ValueCheckOutcome(abstractValueCheck.L(), abstractValueCheck.o(), abstractValueCheck.C());
      }
   }

   public class e extends ShapedMap<Class<?>, ValueCheckerHolder> {
      private final ValueCheckerHolder m = new ValueCheckerHolder(new ValueCheckers.e.ahr());
      private static final long a = 93872548270841L;

      public ValueCheckerHolder get(Object object) {
         if (object == null) {
            return this.m;
         }

         ValueCheckerHolder valueCheckerHolder = (ValueCheckerHolder)super.get(object);
         if (valueCheckerHolder != null) {
            ValueChecker valueChecker = valueCheckerHolder.O();
            if (valueChecker != null) {
               return valueCheckerHolder;
            }
         }

         return null;
      }

      public class ahr implements ValueChecker {
         @Override
         public ValueCheckOutcome resolveObject(Object object) {
            return null;
         }
      }
   }

   public class kw implements ValueChecker {
      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         return null;
      }
   }

   public class r1_ implements ValueChecker {
      private static final long a = 109160864727063L;

      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE.Z(object, object2 -> object2 != null ? 1 : 0, integer -> integer == 1);
         return new ValueCheckOutcome(abstractValueCheck.L(), abstractValueCheck.o(), abstractValueCheck.C());
      }
   }

   public class r1g implements ValueChecker {
      private static final long a = 46423525043036L;
      private static final long b = 3473136183062560780L;

      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE.Z((Pair)object, pair -> ((String)((Pair)pair).getA()).length(), integer -> integer == (int)b);
         return new ValueCheckOutcome(abstractValueCheck.L(), abstractValueCheck.o(), abstractValueCheck.C());
      }
   }

   public class rbz implements ValueChecker {
      private static final long a = 97265224022355L;
      private static final long b = -1228725826250207122L;

      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE.Z((EntityPig)object, Entity::getEntityId, integer -> integer > (int)b);
         return new ValueCheckOutcome(abstractValueCheck.L(), abstractValueCheck.o(), abstractValueCheck.C());
      }
   }

   public class riu implements ValueChecker {
      private static final long a = 28941834344374L;

      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE
            .Z((ItemFishFood)object, itemFishFood -> ((ItemFishFood)itemFishFood).w() ? -5326515 : -362176356, integer -> integer < -5326515);
         return new ValueCheckOutcome(abstractValueCheck.L(), abstractValueCheck.o(), abstractValueCheck.C());
      }
   }

   public class xb implements ValueChecker {
      private static final long a = 110729714950113L;

      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE.Z((NullPointerException)object, nullPointerException -> nullPointerException.getMessage().length(), integer -> integer == 1);
         return new ValueCheckOutcome(abstractValueCheck.L(), abstractValueCheck.o(), abstractValueCheck.C());
      }
   }

   public class yp implements ValueChecker {
      private static final long a = 101722366626685L;
      private static final long b = -1494641119991802408L;

      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE.Z((EntitySkeleton)object, Entity::getEntityId, integer -> integer < (int)b);
         return new ValueCheckOutcome(abstractValueCheck.L(), abstractValueCheck.o(), abstractValueCheck.C());
      }
   }
}
