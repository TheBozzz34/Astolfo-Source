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
   private static final long a = 43122860187579L;

   public StateMap(IProperty<?> var1, String var2, List<IProperty<?>> var3) {
      this.name = var1;
      this.suffix = var2;
      this.ignored = var3;
   }

   @Override
   public ModelResourceLocation getModelResourceLocation(IBlockState var1) {
      LinkedHashMap var6 = Maps.newLinkedHashMap(var1.getProperties());
      String var7;
      if (this.name == null) {
         var7 = Block.blockRegistry.getNameForObject(var1.getBlock()).toString();
      } else {
         var7 = ((IProperty)this.name).getName((Comparable)var6.remove(this.name));
      }

      if (this.suffix != null) {
         var7 = var7 + this.suffix;
      }

      for (IProperty var9 : this.ignored) {
         var6.remove(var9);
      }

      return new ModelResourceLocation(var7, this.getPropertyString(var6));
   }

   public static class Builder {
      private IProperty<?> name;
      private String suffix;
      private final List<IProperty<?>> ignored = Lists.newArrayList();

      public StateMap.Builder withName(IProperty<?> var1) {
         this.name = var1;
         return this;
      }

      public StateMap.Builder withSuffix(String var1) {
         this.suffix = var1;
         return this;
      }

      public StateMap.Builder ignore(IProperty<?>... var1) {
         Collections.addAll(this.ignored, var1);
         return this;
      }

      public StateMap build() {
         return new StateMap(this.name, this.suffix, this.ignored);
      }
   }
}
