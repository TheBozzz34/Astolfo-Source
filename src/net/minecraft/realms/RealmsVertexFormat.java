package net.minecraft.realms;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class RealmsVertexFormat {
   private VertexFormat v;
   private static final long a = 64455630052650L;

   public RealmsVertexFormat(VertexFormat var1) {
      this.v = var1;
   }

   public RealmsVertexFormat from(VertexFormat var1) {
      this.v = var1;
      return this;
   }

   public VertexFormat getVertexFormat() {
      return this.v;
   }

   public void clear() {
      this.v.clear();
   }

   public int N(int var1) {
      return this.v.getUvOffsetById(var1);
   }

   public int W() {
      return this.v.getElementCount();
   }

   public boolean C() {
      return this.v.H();
   }

   public boolean hasUv(int var1) {
      return this.v.hasUvOffset(var1);
   }

   public RealmsVertexFormatElement getElement(int var1) {
      return new RealmsVertexFormatElement(this.v.getElement(var1));
   }

   public RealmsVertexFormat addElement(RealmsVertexFormatElement var1) {
      return this.from(this.v.addElement(var1.getVertexFormatElement()));
   }

   public int w() {
      return this.v.getColorOffset();
   }

   public List<RealmsVertexFormatElement> getElements() {
      ArrayList var4 = new ArrayList();

      for (VertexFormatElement var6 : this.v.getElements()) {
         var4.add(new RealmsVertexFormatElement(var6));
      }

      return var4;
   }

   public boolean c() {
      return this.v.hasNormal();
   }

   public int z() {
      return this.v.getNextOffset();
   }

   public int q(int var1) {
      return this.v.getOffset(var1);
   }

   public int F() {
      return this.v.getNormalOffset();
   }

   public int b() {
      return this.v.getIntegerSize();
   }

   @Override
   public boolean equals(Object var1) {
      return this.v.equals(var1);
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
