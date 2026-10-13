package net.minecraft.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.state.BlockWorldState;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class FactoryBlockPattern {
   private static final Joiner COMMA_JOIN = Joiner.on(",");
   private final List<String[]> depth = Lists.newArrayList();
   private final Map<Character, Predicate<BlockWorldState>> symbolMap = Maps.newHashMap();
   private int aisleHeight;
   private int rowWidth;
   private static final long a = 55713340015571L;
   private static final long e = 1765538086176948256L;

   private FactoryBlockPattern() {
      this.symbolMap.put(Character.valueOf((char)((int)e)), Predicates.alwaysTrue());
   }

   public FactoryBlockPattern aisle(String... var1) {
      if (!ArrayUtils.isEmpty(var1) && !StringUtils.isEmpty(var1[0])) {
         if (this.depth.isEmpty()) {
            this.aisleHeight = var1.length;
            this.rowWidth = var1[0].length();
         }

         if (var1.length != this.aisleHeight) {
            throw new IllegalArgumentException(
               "Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + var1.length + ")"
            );
         }

         for (String var8 : var1) {
            if (var8.length() != this.rowWidth) {
               throw new IllegalArgumentException(
                  "Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + var8.length() + ")"
               );
            }

            for (char var12 : var8.toCharArray()) {
               if (!this.symbolMap.containsKey(var12)) {
                  this.symbolMap.put(var12, (Predicate<BlockWorldState>)null);
               }
            }
         }

         this.depth.add(var1);
         return this;
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static FactoryBlockPattern start() {
      return new FactoryBlockPattern();
   }

   public FactoryBlockPattern where(char var1, Predicate<BlockWorldState> var2) {
      this.symbolMap.put(var1, var2);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.makePredicateArray());
   }

   private Predicate<BlockWorldState>[][][] makePredicateArray() {
      this.checkMissingPredicates();
      Predicate[][][] var4 = (Predicate[][][])Array.newInstance(Predicate.class, this.depth.size(), this.aisleHeight, this.rowWidth);

      for (int var5 = 0; var5 < this.depth.size(); var5++) {
         for (int var6 = 0; var6 < this.aisleHeight; var6++) {
            for (int var7 = 0; var7 < this.rowWidth; var7++) {
               var4[var5][var6][var7] = this.symbolMap.get(this.depth.get(var5)[var6].charAt(var7));
            }
         }
      }

      return var4;
   }

   private void checkMissingPredicates() {
      ArrayList var4 = Lists.newArrayList();

      for (Entry var6 : this.symbolMap.entrySet()) {
         if (var6.getValue() == null) {
            var4.add(var6.getKey());
         }
      }

      if (!var4.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + COMMA_JOIN.join(var4) + " are missing");
      }
   }
}
