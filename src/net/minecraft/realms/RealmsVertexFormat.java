package net.minecraft.realms;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class RealmsVertexFormat {
   private VertexFormat v;
   private static final long a = 64455630052650L;

   public RealmsVertexFormat(VertexFormat vIn) {
      this.v = vIn;
   }

   public RealmsVertexFormat from(VertexFormat vertexFormat) {
      this.v = vertexFormat;
      return this;
   }

   public VertexFormat getVertexFormat() {
      return this.v;
   }

   public void clear() {
      this.v.clear();
   }

   public int N(int i) {
      return this.v.getUvOffsetById(i);
   }

   public int W() {
      return this.v.getElementCount();
   }

   public boolean C() {
      return this.v.H();
   }

   public boolean hasUv(int i) {
      return this.v.hasUvOffset(i);
   }

   public RealmsVertexFormatElement getElement(int i) {
      return new RealmsVertexFormatElement(this.v.getElement(i));
   }

   public RealmsVertexFormat addElement(RealmsVertexFormatElement realmsVertexFormatElement) {
      return this.from(this.v.addElement(realmsVertexFormatElement.getVertexFormatElement()));
   }

   public int w() {
      return this.v.getColorOffset();
   }

   public List<RealmsVertexFormatElement> getElements() {
      ArrayList arrayList = new ArrayList();

      for (VertexFormatElement vertexFormatElement : this.v.getElements()) {
         arrayList.add(new RealmsVertexFormatElement(vertexFormatElement));
      }

      return arrayList;
   }

   public boolean c() {
      return this.v.hasNormal();
   }

   public int z() {
      return this.v.getNextOffset();
   }

   public int q(int i) {
      return this.v.getOffset(i);
   }

   public int F() {
      return this.v.getNormalOffset();
   }

   public int b() {
      return this.v.getIntegerSize();
   }

   @Override
   public boolean equals(Object object) {
      return this.v.equals(object);
   }

   @Override
   public int hashCode() {
      return this.v.hashCode();
   }

   @Override
   public String toString() {
      return this.v.toString();
   }
}
