package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.Cartesian;
import net.minecraft.util.MapPopulator;

public class BlockState {
   private static final Joiner COMMA_JOINER = Joiner.on(", ");
   private static final Function<IProperty, String> GET_NAME_FUNC = new Function<IProperty, String>() {
      private static final long a = 51129142378123L;

      public String apply(IProperty var1) {
         return var1 == null ? "<NULL>" : var1.getName();
      }
   };
   private final Block block;
   private final ImmutableList<IProperty> properties;
   private final ImmutableList<IBlockState> validStates;
   private static final long a = 119042696040847L;

   public BlockState(Block var1, IProperty... var2) {
      this.block = var1;
      Arrays.sort(var2, new Comparator<IProperty>() {
         public int compare(IProperty var1, IProperty var2x) {
            return var1.getName().compareTo(var2x.getName());
         }
      });
      this.properties = ImmutableList.copyOf(var2);
      LinkedHashMap var7 = Maps.newLinkedHashMap();
      ArrayList var8 = Lists.newArrayList();

      for (List var10 : Cartesian.cartesianProduct(this.getAllowedValues())) {
         Map var11 = MapPopulator.createMap(this.properties, var10);
         BlockState.StateImplementation var12 = new BlockState.StateImplementation(var1, ImmutableMap.copyOf(var11));
         var7.put(var11, var12);
         var8.add(var12);
      }

      for (Object var14$raw : var8) {
         BlockState.StateImplementation var14 = (BlockState.StateImplementation)var14$raw;
         var14.buildPropertyValueTable(var7);
      }

      this.validStates = ImmutableList.copyOf(var8);
   }

   public ImmutableList<IBlockState> getValidStates() {
      return this.validStates;
   }

   private List<Iterable<Comparable>> getAllowedValues() {
      ArrayList var4 = Lists.newArrayList();

      for (int var5 = 0; var5 < this.properties.size(); var5++) {
         var4.add(this.properties.get(var5).getAllowedValues());
      }

      return var4;
   }

   public IBlockState getBaseState() {
      return this.validStates.get(0);
   }

   public Block getBlock() {
      return this.block;
   }

   public Collection<IProperty> getProperties() {
      return this.properties;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
         .add("block", Block.blockRegistry.getNameForObject(this.block))
         .add("properties", Iterables.transform(this.properties, GET_NAME_FUNC))
         .toString();
   }

   public static class StateImplementation extends BlockStateBase {
      private final Block block;
      private final ImmutableMap<IProperty, Comparable> properties;
      private ImmutableTable<IProperty, Comparable, IBlockState> propertyValueTable;
      private static final long a = 45256580002851L;

      public StateImplementation(Block var1, ImmutableMap<IProperty, Comparable> var2) {
         this.block = var1;
         this.properties = var2;
      }

      @Override
      public Collection<IProperty> getPropertyNames() {
         return Collections.unmodifiableCollection(this.properties.keySet());
      }

      @Override
      public <T extends Comparable<T>> T getValue(IProperty<T> var1) {
         if (!this.properties.containsKey(var1)) {
            throw new IllegalArgumentException("Cannot get property " + var1 + " as it does not exist in " + this.block.getBlockState());
         } else {
            return (T)var1.getValueClass().cast(this.properties.get(var1));
         }
      }

      @Override
      public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> var1, V var2) {
         if (!this.properties.containsKey(var1)) {
            throw new IllegalArgumentException("Cannot set property " + var1 + " as it does not exist in " + this.block.getBlockState());
         } else if (!var1.getAllowedValues().contains(var2)) {
            throw new IllegalArgumentException(
               "Cannot set property " + var1 + " to " + var2 + " on block " + Block.blockRegistry.getNameForObject(this.block) + ", it is not an allowed value"
            );
         } else {
            return this.properties.get(var1) == var2 ? this : (IBlockState)this.propertyValueTable.get(var1, var2);
         }
      }

      @Override
      public ImmutableMap<IProperty, Comparable> getProperties() {
         return this.properties;
      }

      @Override
      public Block getBlock() {
         return this.block;
      }

      @Override
      public boolean equals(Object var1) {
         return this == var1;
      }

      @Override
      public int hashCode() {
         return this.properties.hashCode();
      }

      public void buildPropertyValueTable(Map<Map<IProperty, Comparable>, BlockState.StateImplementation> var1) {
         if (this.propertyValueTable != null) {
            throw new IllegalStateException();
         }

         HashBasedTable var5 = HashBasedTable.create();

         for (IProperty var7 : this.properties.keySet()) {
            for (Object var9$raw : var7.getAllowedValues()) {
               Comparable var9 = (Comparable)var9$raw;
               if (var9 != this.properties.get(var7)) {
                  var5.put(var7, var9, var1.get(this.getPropertiesWithValue(var7, var9)));
               }
            }
         }

         this.propertyValueTable = ImmutableTable.copyOf(var5);
      }

      private Map<IProperty, Comparable> getPropertiesWithValue(IProperty var1, Comparable var2) {
         HashMap var3 = Maps.newHashMap(this.properties);
         var3.put(var1, var2);
         return var3;
      }
   }
}
