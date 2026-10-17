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

   private final ShapedMap<Class<?>, ValueCheckerHolder> checkers = new ValueCheckers.CheckerMap();
   private static final ValueCheckers[] VALUES = new ValueCheckers[]{INSTANCE};

   public ShapedMap<Class<?>, ValueCheckerHolder> getCheckers() {
      return this.checkers;
   }

   @Override
   public void initialize() {
      this.checkers.put(NullPointerException.class, new ValueCheckerHolder(new ValueCheckers.NpeValueChecker()));
      this.checkers.put(Pair.class, new ValueCheckerHolder(new ValueCheckers.PairValueChecker()));
      this.checkers.put(EntityPig.class, new ValueCheckerHolder(new ValueCheckers.PigValueChecker()));
      this.checkers.put(EntitySkeleton.class, new ValueCheckerHolder(new ValueCheckers.SkeletonValueChecker()));
      this.checkers.put(Block.class, new ValueCheckerHolder(new ValueCheckers.BlockValueChecker()));
      this.checkers.put(ItemFishFood.class, new ValueCheckerHolder(new ValueCheckers.FishFoodValueChecker()));
      this.checkers.put(EnvironmentKindD.class, new ValueCheckerHolder(new ValueCheckers.EnvironmentKindDChecker()));
      this.checkers.put(EnvironmentKindC.class, new ValueCheckerHolder(new ValueCheckers.EnvironmentKindCChecker()));
   }

   public class BlockValueChecker implements ValueChecker {
      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE
            .createCheck((Block)object, block -> ((Block)block).getMapColor(null) == MapColor.adobeColor ? -63872 : 0, integer -> integer != -63872);
         return new ValueCheckOutcome(abstractValueCheck.evaluate(), abstractValueCheck.getType(), abstractValueCheck.computeValue());
      }
   }

   public class CheckerMap extends ShapedMap<Class<?>, ValueCheckerHolder> {
      private final ValueCheckerHolder defaultHolder = new ValueCheckerHolder(new ValueCheckers.CheckerMap.NullKeyChecker());

      public ValueCheckerHolder get(Object object) {
         if (object == null) {
            return this.defaultHolder;
         }

         ValueCheckerHolder valueCheckerHolder = (ValueCheckerHolder)super.get(object);
         if (valueCheckerHolder != null) {
            ValueChecker valueChecker = valueCheckerHolder.getChecker();
            if (valueChecker != null) {
               return valueCheckerHolder;
            }
         }

         return null;
      }

      public class NullKeyChecker implements ValueChecker {
         @Override
         public ValueCheckOutcome resolveObject(Object object) {
            return null;
         }
      }
   }

   public class EnvironmentKindCChecker implements ValueChecker {
      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         return null;
      }
   }

   public class EnvironmentKindDChecker implements ValueChecker {
      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE.createCheck(object, object2 -> object2 != null ? 1 : 0, integer -> integer == 1);
         return new ValueCheckOutcome(abstractValueCheck.evaluate(), abstractValueCheck.getType(), abstractValueCheck.computeValue());
      }
   }

   public class FishFoodValueChecker implements ValueChecker {
      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE
            .createCheck((ItemFishFood)object, itemFishFood -> ((ItemFishFood)itemFishFood).w() ? -5326515 : -362176356, integer -> integer < -5326515);
         return new ValueCheckOutcome(abstractValueCheck.evaluate(), abstractValueCheck.getType(), abstractValueCheck.computeValue());
      }
   }

   public class NpeValueChecker implements ValueChecker {
      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE.createCheck((NullPointerException)object, nullPointerException -> nullPointerException.getMessage().length(), integer -> integer == 1);
         return new ValueCheckOutcome(abstractValueCheck.evaluate(), abstractValueCheck.getType(), abstractValueCheck.computeValue());
      }
   }

   public class PairValueChecker implements ValueChecker {
      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE.createCheck((Pair)object, pair -> ((String)((Pair)pair).getA()).length(), integer -> integer == 12);
         return new ValueCheckOutcome(abstractValueCheck.evaluate(), abstractValueCheck.getType(), abstractValueCheck.computeValue());
      }
   }

   public class PigValueChecker implements ValueChecker {
      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE.createCheck((EntityPig)object, Entity::getEntityId, integer -> integer > 135278);
         return new ValueCheckOutcome(abstractValueCheck.evaluate(), abstractValueCheck.getType(), abstractValueCheck.computeValue());
      }
   }

   public class SkeletonValueChecker implements ValueChecker {
      @Override
      public ValueCheckOutcome resolveObject(Object object) {
         AbstractValueCheck abstractValueCheck = ValueCheckFactory.INSTANCE.createCheck((EntitySkeleton)object, Entity::getEntityId, integer -> integer < 3298776);
         return new ValueCheckOutcome(abstractValueCheck.evaluate(), abstractValueCheck.getType(), abstractValueCheck.computeValue());
      }
   }
}
