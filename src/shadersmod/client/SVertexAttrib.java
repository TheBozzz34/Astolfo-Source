package shadersmod.client;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexAttrib {
   public int x;
   public int C;
   public VertexFormatElement.EnumType H;
   public int w;

   public SVertexAttrib(int index, int count, VertexFormatElement.EnumType typeIn) {
      this.x = index;
      this.C = count;
      this.H = typeIn;
   }
}
