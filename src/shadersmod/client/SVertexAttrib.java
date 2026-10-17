package shadersmod.client;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexAttrib {
   public int x;
   public int count;
   public VertexFormatElement.EnumType type;
   public int offset;

   public SVertexAttrib(int index, int count, VertexFormatElement.EnumType typeIn) {
      this.x = index;
      this.count = count;
      this.type = typeIn;
   }
}
