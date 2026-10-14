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
      public String apply(IProperty iProperty) {
         return iProperty == null ? "<NULL>" : iProperty.getName();
      }
   };
   private final Block block;
   private final ImmutableList<IProperty> properties;
   private final ImmutableList<IBlockState> validStates;

   public BlockState(Block blockIn, IProperty... properties) {
      this.block = blockIn;
      Arrays.sort(properties, new Comparator<IProperty>() {
         public int compare(IProperty iProperty, IProperty iProperty2) {
            return iProperty.getName().compareTo(iProperty2.getName());
         }
      });
      this.properties = ImmutableList.copyOf(properties);
      LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
      ArrayList arrayList = Lists.newArrayList();

      for (List list : Cartesian.cartesianProduct(this.getAllowedValues())) {
         Map map = MapPopulator.createMap(this.properties, list);
         BlockState.StateImplementation stateImplementation = new BlockState.StateImplementation(blockIn, ImmutableMap.copyOf(map));
         linkedHashMap.put(map, stateImplementation);
         arrayList.add(stateImplementation);
      }

      for (Object object : arrayList) {
         BlockState.StateImplementation stateImplementation2 = (BlockState.StateImplementation)object;
         stateImplementation2.buildPropertyValueTable(linkedHashMap);
      }

      this.validStates = ImmutableList.copyOf(arrayList);
   }

   public ImmutableList<IBlockState> getValidStates() {
      return this.validStates;
   }

   private List<Iterable<Comparable>> getAllowedValues() {
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < this.properties.size(); i++) {
         arrayList.add(this.properties.get(i).getAllowedValues());
      }

      return arrayList;
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

      public StateImplementation(Block blockIn, ImmutableMap<IProperty, Comparable> propertiesIn) {
         this.block = blockIn;
         this.properties = propertiesIn;
      }

      @Override
      public Collection<IProperty> getPropertyNames() {
         return Collections.unmodifiableCollection(this.properties.keySet());
      }

      @Override
      public <T extends Comparable<T>> T getValue(IProperty<T> property) {
         if (!this.properties.containsKey(property)) {
            throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getBlockState());
         } else {
            return (T)property.getValueClass().cast(this.properties.get(property));
         }
      }

      @Override
      public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
         if (!this.properties.containsKey(property)) {
            throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getBlockState());
         } else if (!property.getAllowedValues().contains(value)) {
            throw new IllegalArgumentException(
               "Cannot set property "
                  + property
                  + " to "
                  + value
                  + " on block "
                  + Block.blockRegistry.getNameForObject(this.block)
                  + ", it is not an allowed value"
            );
         } else {
            return this.properties.get(property) == value ? this : (IBlockState)this.propertyValueTable.get(property, value);
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
      public boolean equals(Object object) {
         return this == object;
      }

      @Override
      public int hashCode() {
         return this.properties.hashCode();
      }

      public void buildPropertyValueTable(Map<Map<IProperty, Comparable>, BlockState.StateImplementation> map) {
         if (this.propertyValueTable != null) {
            throw new IllegalStateException();
         }

         HashBasedTable hashBasedTable = HashBasedTable.create();

         for (IProperty iProperty : this.properties.keySet()) {
            for (Object object : iProperty.getAllowedValues()) {
               Comparable comparable = (Comparable)object;
               if (comparable != this.properties.get(iProperty)) {
                  hashBasedTable.put(iProperty, comparable, map.get(this.getPropertiesWithValue(iProperty, comparable)));
               }
            }
         }

         this.propertyValueTable = ImmutableTable.copyOf(hashBasedTable);
      }

      private Map<IProperty, Comparable> getPropertiesWithValue(IProperty property, Comparable value) {
         HashMap hashMap = Maps.newHashMap(this.properties);
         hashMap.put(property, value);
         return hashMap;
      }
   }
}
