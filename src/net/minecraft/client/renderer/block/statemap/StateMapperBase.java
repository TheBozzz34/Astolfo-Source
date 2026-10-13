package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public abstract class StateMapperBase implements IStateMapper {
   public Map<IBlockState, ModelResourceLocation> mapStateModelLocations = Maps.newLinkedHashMap();
   private static final long b = 103132157401295L;

   public String getPropertyString(Map<IProperty, Comparable> var1) {
      StringBuilder var5 = new StringBuilder();

      for (Entry var7 : var1.entrySet()) {
         if (var5.length() != 0) {
            var5.append(",");
         }

         IProperty var8 = (IProperty)var7.getKey();
         Comparable var9 = (Comparable)var7.getValue();
         var5.append(var8.getName());
         var5.append("=");
         var5.append(var8.getName(var9));
      }

      if (var5.length() == 0) {
         var5.append("normal");
      }

      return var5.toString();
   }

   @Override
   public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block var1) {
      for (IBlockState var6 : var1.getBlockState().getValidStates()) {
         this.mapStateModelLocations.put(var6, this.getModelResourceLocation(var6));
      }

      return this.mapStateModelLocations;
   }

   public abstract ModelResourceLocation getModelResourceLocation(IBlockState var1);
}
