package net.minecraft.realms;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class RealmsVertexFormat {
   private VertexFormat vertexFormat;

   public RealmsVertexFormat(VertexFormat vIn) {
      this.vertexFormat = vIn;
   }

   public RealmsVertexFormat from(VertexFormat vertexFormat) {
      this.vertexFormat = vertexFormat;
      return this;
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public void clear() {
      this.vertexFormat.clear();
   }

   public int getUvOffset(int i) {
      return this.vertexFormat.getUvOffsetById(i);
   }

   public int getElementCount() {
      return this.vertexFormat.getElementCount();
   }

   public boolean hasColor() {
      return this.vertexFormat.hasColor();
   }

   public boolean hasUv(int i) {
      return this.vertexFormat.hasUvOffset(i);
   }

   public RealmsVertexFormatElement getElement(int i) {
      return new RealmsVertexFormatElement(this.vertexFormat.getElement(i));
   }

   public RealmsVertexFormat addElement(RealmsVertexFormatElement realmsVertexFormatElement) {
      return this.from(this.vertexFormat.addElement(realmsVertexFormatElement.getVertexFormatElement()));
   }

   public int getColorOffset() {
      return this.vertexFormat.getColorOffset();
   }

   public List<RealmsVertexFormatElement> getElements() {
      ArrayList arrayList = new ArrayList();

      for (VertexFormatElement vertexFormatElement : this.vertexFormat.getElements()) {
         arrayList.add(new RealmsVertexFormatElement(vertexFormatElement));
      }

      return arrayList;
   }

   public boolean hasNormal() {
      return this.vertexFormat.hasNormal();
   }

   public int z() {
      return this.vertexFormat.getNextOffset();
   }

   public int getOffset(int i) {
      return this.vertexFormat.getOffset(i);
   }

   public int getNormalOffset() {
      return this.vertexFormat.getNormalOffset();
   }

   public int getIntegerSize() {
      return this.vertexFormat.getIntegerSize();
   }

   @Override
   public boolean equals(Object object) {
      return this.vertexFormat.equals(object);
   }

   @Override
   public int hashCode() {
      return this.vertexFormat.hashCode();
   }

   @Override
   public String toString() {
      return this.vertexFormat.toString();
   }
}
