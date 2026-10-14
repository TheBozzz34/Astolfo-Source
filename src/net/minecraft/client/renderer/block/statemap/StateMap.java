package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class StateMap extends StateMapperBase {
   private final IProperty<?> name;
   private final String suffix;
   private final List<IProperty<?>> ignored;

   public StateMap(IProperty<?> name, String suffix, List<IProperty<?>> ignored) {
      this.name = name;
      this.suffix = suffix;
      this.ignored = ignored;
   }

   @Override
   public ModelResourceLocation getModelResourceLocation(IBlockState state) {
      LinkedHashMap linkedHashMap = Maps.newLinkedHashMap(state.getProperties());
      String string;
      if (this.name == null) {
         string = Block.blockRegistry.getNameForObject(state.getBlock()).toString();
      } else {
         string = ((IProperty)this.name).getName((Comparable)linkedHashMap.remove(this.name));
      }

      if (this.suffix != null) {
         string = string + this.suffix;
      }

      for (IProperty iProperty : this.ignored) {
         linkedHashMap.remove(iProperty);
      }

      return new ModelResourceLocation(string, this.getPropertyString(linkedHashMap));
   }

   public static class Builder {
      private IProperty<?> name;
      private String suffix;
      private final List<IProperty<?>> ignored = Lists.newArrayList();

      public StateMap.Builder withName(IProperty<?> builderPropertyIn) {
         this.name = builderPropertyIn;
         return this;
      }

      public StateMap.Builder withSuffix(String builderSuffixIn) {
         this.suffix = builderSuffixIn;
         return this;
      }

      public StateMap.Builder ignore(IProperty<?>... iPropertys) {
         Collections.addAll(this.ignored, iPropertys);
         return this;
      }

      public StateMap build() {
         return new StateMap(this.name, this.suffix, this.ignored);
      }
   }
}
