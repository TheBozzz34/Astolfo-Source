package net.minecraft.realms;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class RealmsVertexFormatElement {
   private VertexFormatElement vertexFormatElement;

   public RealmsVertexFormatElement(VertexFormatElement vIn) {
      this.vertexFormatElement = vIn;
   }

   public VertexFormatElement getVertexFormatElement() {
      return this.vertexFormatElement;
   }

   public boolean isPosition() {
      return this.vertexFormatElement.isPositionElement();
   }

   public int getIndex() {
      return this.vertexFormatElement.getIndex();
   }

   public int getByteSize() {
      return this.vertexFormatElement.getSize();
   }

   public int getCount() {
      return this.vertexFormatElement.getElementCount();
   }

   @Override
   public int hashCode() {
      return this.vertexFormatElement.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      return this.vertexFormatElement.equals(object);
   }

   @Override
   public String toString() {
      return this.vertexFormatElement.toString();
   }
}
