package net.minecraft.realms;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class RealmsVertexFormatElement {
   private VertexFormatElement v;

   public RealmsVertexFormatElement(VertexFormatElement vIn) {
      this.v = vIn;
   }

   public VertexFormatElement getVertexFormatElement() {
      return this.v;
   }

   public boolean isPosition() {
      return this.v.isPositionElement();
   }

   public int L() {
      return this.v.getIndex();
   }

   public int Q() {
      return this.v.getSize();
   }

   public int B() {
      return this.v.getElementCount();
   }

   @Override
   public int hashCode() {
      return this.v.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      return this.v.equals(object);
   }

   @Override
   public String toString() {
      return this.v.toString();
   }
}
