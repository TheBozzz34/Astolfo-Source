package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public interface IVertexConsumer {
   VertexFormat Z();

   void z(int i);

   void v(EnumFacing enumFacing);

   void U();

   void x(int i, float... floats);
}
