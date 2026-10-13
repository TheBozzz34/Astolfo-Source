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
      private static final long a = 47686318496203L;

      public String apply(Entry<IProperty, Comparable> var1) {
         if (var1 == null) {
            return "<NULL>";
         }

         IProperty var5 = (IProperty)var1.getKey();
         return var5.getName() + "=" + var5.getName((Comparable)var1.getValue());
      }
   };
   private int blockId = -1;
   private int blockStateId = -1;
   private int metadata = -1;
   private ResourceLocation blockLocation = null;
   private static final long b = 94713360854204L;

   public int getBlockId() {
      if (this.blockId < 0) {
         this.blockId = Block.getIdFromBlock(this.getBlock());
      }

      return this.blockId;
   }

   public int q() {
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
   public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> var1) {
      return this.withProperty(var1, cyclePropertyValue(var1.getAllowedValues(), this.getValue(var1)));
   }

   public static <T> T cyclePropertyValue(Collection<T> var0, T var1) {
      Iterator var5 = var0.iterator();

      while (var5.hasNext()) {
         if (var5.next().equals(var1)) {
            if (var5.hasNext()) {
               return (T)var5.next();
            }

            return (T)var0.iterator().next();
         }
      }

      return (T)var5.next();
   }

   @Override
   public String toString() {
      StringBuilder var4 = new StringBuilder();
      var4.append(Block.blockRegistry.getNameForObject(this.getBlock()));
      if (!this.getProperties().isEmpty()) {
         var4.append("[");
         COMMA_JOINER.appendTo(var4, Iterables.transform(this.getProperties().entrySet(), MAP_ENTRY_TO_STRING));
         var4.append("]");
      }

      return var4.toString();
   }

   public ImmutableTable<IProperty, Comparable, IBlockState> getPropertyValueTable() {
      return null;
   }
}
