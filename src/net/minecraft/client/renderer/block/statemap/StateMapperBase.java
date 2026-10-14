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

   public String getPropertyString(Map<IProperty, Comparable> map) {
      StringBuilder stringBuilder = new StringBuilder();

      for (Entry entry : map.entrySet()) {
         if (stringBuilder.length() != 0) {
            stringBuilder.append(",");
         }

         IProperty iProperty = (IProperty)entry.getKey();
         Comparable comparable = (Comparable)entry.getValue();
         stringBuilder.append(iProperty.getName());
         stringBuilder.append("=");
         stringBuilder.append(iProperty.getName(comparable));
      }

      if (stringBuilder.length() == 0) {
         stringBuilder.append("normal");
      }

      return stringBuilder.toString();
   }

   @Override
   public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
      for (IBlockState iBlockState : blockIn.getBlockState().getValidStates()) {
         this.mapStateModelLocations.put(iBlockState, this.getModelResourceLocation(iBlockState));
      }

      return this.mapStateModelLocations;
   }

   public abstract ModelResourceLocation getModelResourceLocation(IBlockState state);
}
