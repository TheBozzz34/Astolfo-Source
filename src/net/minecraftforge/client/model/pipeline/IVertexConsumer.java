package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public interface IVertexConsumer {
   VertexFormat Z();

   void z(int var1);

   void v(EnumFacing var1);

   void U();

   void x(int var1, float... var2);
}
