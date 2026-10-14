package net.minecraft.realms;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class RealmsBufferBuilder {
   private WorldRenderer b;

   public RealmsBufferBuilder(WorldRenderer worldRenderer) {
      this.b = worldRenderer;
   }

   public RealmsBufferBuilder from(WorldRenderer worldRenderer) {
      this.b = worldRenderer;
      return this;
   }

   public void H(float f, float g, float h) {
      this.b.sortVertexData(f, g, h);
   }

   public void fixupQuadColor(int i) {
      this.b.putColor4(i);
   }

   public ByteBuffer getBuffer() {
      return this.b.getByteBuffer();
   }

   public void n(float f, float g, float h) {
      this.b.putNormal(f, g, h);
   }

   public int h() {
      return this.b.getDrawMode();
   }

   public void h(double d, double d2, double d3) {
      this.b.setTranslation(d, d2, d3);
   }

   public void restoreState(WorldRenderer.State state) {
      this.b.setVertexState(state);
   }

   public void B() {
      this.b.endVertex();
   }

   public RealmsBufferBuilder normal(float f, float g, float h) {
      return this.from(this.b.normal(f, g, h));
   }

   public void o() {
      this.b.finishDrawing();
   }

   public void begin(int i, VertexFormat vertexFormat) {
      this.b.begin(i, vertexFormat);
   }

   public RealmsBufferBuilder color(int i, int j, int k, int l) {
      return this.from(this.b.color(i, j, k, l));
   }

   public void faceTex2(int i, int j, int k, int l) {
      this.b.putBrightness4(i, j, k, l);
   }

   public void p(double d, double d2, double d3) {
      this.b.putPosition(d, d2, d3);
   }

   public void J(float f, float g, float h, int i) {
      this.b.putColorRGB_F(f, g, h, i);
   }

   public RealmsBufferBuilder color(float f, float g, float h, float f2) {
      return this.from(this.b.color(f, g, h, f2));
   }

   public RealmsVertexFormat getVertexFormat() {
      return new RealmsVertexFormat(this.b.getVertexFormat());
   }

   public void r(float f, float g, float h, int i) {
      this.b.putColorMultiplier(f, g, h, i);
   }

   public RealmsBufferBuilder tex2(int i, int j) {
      return this.from(this.b.lightmap(i, j));
   }

   public void putBulkData(int[] ints) {
      this.b.addVertexData(ints);
   }

   public RealmsBufferBuilder tex(double d, double d2) {
      return this.from(this.b.tex(d, d2));
   }

   public int c() {
      return this.b.getVertexCount();
   }

   public void X() {
      this.b.reset();
   }

   public RealmsBufferBuilder vertex(double d, double d2, double d3) {
      return this.from(this.b.pos(d, d2, d3));
   }

   public void E(float f, float g, float h) {
      this.b.putColorRGB_F4(f, g, h);
   }

   public void E() {
      this.b.noColor();
   }
}
