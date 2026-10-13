package bypass.loader.check;

import bypass.util.collection.ShapedMap;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.ItemFishFood;
import net.minecraft.src.BetterGrass;
import oshi.util.tuples.Pair;

public enum ValueType {
   A(EntitySkeleton.class),
   B(Block.class),
   C(Integer.class),
   D(NullPointerException.class),
   E(System.class),
   F(Pair.class),
   G(ItemFishFood.class),
   H(Package.class),
   I(BetterGrass.class),
   J(EntityPig.class),
   K(EntitySkeleton.class);

   private final Class<?> s;
   public static final ShapedMap<Class<?>, ValueType> compactHashMap = new ShapedMap<>();
   private static final ValueType[] f = new ValueType[]{A, B, C, D, E, F, G, H, I, J, K};

   ValueType(Class<?> var3) {
      this.s = var3;
   }

   public Class<?> B() {
      return this.s;
   }

   static {
      for (ValueType var16 : values()) {
         compactHashMap.put(var16.s, var16);
      }
   }
}
