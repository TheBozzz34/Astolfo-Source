package net.minecraft.block.state.pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

public class BlockStateHelper implements Predicate<IBlockState> {
   private final BlockState blockstate;
   private final Map<IProperty, Predicate> propertyPredicates = Maps.newHashMap();
   private static final long a = 16881479317023L;

   private BlockStateHelper(BlockState var1) {
      this.blockstate = var1;
   }

   public static BlockStateHelper forBlock(Block var0) {
      return new BlockStateHelper(var0.getBlockState());
   }

   public boolean apply(IBlockState var1) {
      if (var1 != null && var1.getBlock().equals(this.blockstate.getBlock())) {
         for (Entry var6 : this.propertyPredicates.entrySet()) {
            Comparable var7 = var1.getValue((IProperty)var6.getKey());
            if (!((Predicate)var6.getValue()).apply(var7)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public <V extends Comparable<V>> BlockStateHelper where(IProperty<V> var1, Predicate<? extends V> var2) {
      if (!this.blockstate.getProperties().contains(var1)) {
         throw new IllegalArgumentException(this.blockstate + " cannot support property " + var1);
      }

      this.propertyPredicates.put(var1, var2);
      return this;
   }
}
