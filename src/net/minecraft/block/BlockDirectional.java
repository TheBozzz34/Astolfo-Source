package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;

public abstract class BlockDirectional extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

   public BlockDirectional(Material materialIn) {
      super(materialIn);
   }

   public BlockDirectional(Material material, MapColor mapColor) {
      super(material, mapColor);
   }
}
