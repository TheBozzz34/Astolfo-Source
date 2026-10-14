package net.minecraftforge.client.model;

import javax.vecmath.Matrix4f;
import net.minecraft.util.EnumFacing;

public interface ITransformation {
   Matrix4f m();

   EnumFacing E(EnumFacing enumFacing);

   int x(EnumFacing enumFacing, int i);
}
