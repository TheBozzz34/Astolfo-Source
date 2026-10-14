package shadersmod.client;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexFormatElement extends VertexFormatElement {
   public int sUsage;

   public SVertexFormatElement(int sUsage, VertexFormatElement.EnumType typeIn, int count) {
      super(0, typeIn, VertexFormatElement.EnumUsage.PADDING, count);
      this.sUsage = sUsage;
   }
}
