package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;

public abstract class BlockStateBase implements IBlockState {
   private static final Joiner COMMA_JOINER = Joiner.on(',');
   private static final Function<Entry<IProperty, Comparable>, String> MAP_ENTRY_TO_STRING = new Function<Entry<IProperty, Comparable>, String>() {
      public String apply(Entry<IProperty, Comparable> entry) {
         if (entry == null) {
            return "<NULL>";
         }

         IProperty iProperty = (IProperty)entry.getKey();
         return iProperty.getName() + "=" + iProperty.getName((Comparable)entry.getValue());
      }
   };
   private int blockId = -1;
   private int blockStateId = -1;
   private int metadata = -1;
   private ResourceLocation blockLocation = null;

   public int getBlockId() {
      if (this.blockId < 0) {
         this.blockId = Block.getIdFromBlock(this.getBlock());
      }

      return this.blockId;
   }

   public int getBlockStateId() {
      if (this.blockStateId < 0) {
         this.blockStateId = Block.getStateId(this);
      }

      return this.blockStateId;
   }

   public int getMetadata() {
      if (this.metadata < 0) {
         this.metadata = this.getBlock().getMetaFromState(this);
      }

      return this.metadata;
   }

   public ResourceLocation getBlockLocation() {
      if (this.blockLocation == null) {
         this.blockLocation = Block.blockRegistry.getNameForObject(this.getBlock());
      }

      return this.blockLocation;
   }

   @Override
   public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
      return this.withProperty(property, cyclePropertyValue(property.getAllowedValues(), this.getValue(property)));
   }

   public static <T> T cyclePropertyValue(Collection<T> values, T currentValue) {
      Iterator iterator2 = values.iterator();

      while (iterator2.hasNext()) {
         if (iterator2.next().equals(currentValue)) {
            if (iterator2.hasNext()) {
               return (T)iterator2.next();
            }

            return (T)values.iterator().next();
         }
      }

      return (T)iterator2.next();
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(Block.blockRegistry.getNameForObject(this.getBlock()));
      if (!this.getProperties().isEmpty()) {
         stringBuilder.append("[");
         COMMA_JOINER.appendTo(stringBuilder, Iterables.transform(this.getProperties().entrySet(), MAP_ENTRY_TO_STRING));
         stringBuilder.append("]");
      }

      return stringBuilder.toString();
   }

   public ImmutableTable<IProperty, Comparable, IBlockState> getPropertyValueTable() {
      return null;
   }
}
