package net.minecraft.realms;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class RealmsBufferBuilder {
   private WorldRenderer builder;

   public RealmsBufferBuilder(WorldRenderer worldRenderer) {
      this.builder = worldRenderer;
   }

   public RealmsBufferBuilder from(WorldRenderer worldRenderer) {
      this.builder = worldRenderer;
      return this;
   }

   public void sortQuads(float f, float g, float h) {
      this.builder.sortVertexData(f, g, h);
   }

   public void fixupQuadColor(int i) {
      this.builder.putColor4(i);
   }

   public ByteBuffer getBuffer() {
      return this.builder.getByteBuffer();
   }

   public void postNormal(float f, float g, float h) {
      this.builder.putNormal(f, g, h);
   }

   public int getDrawMode() {
      return this.builder.getDrawMode();
   }

   public void offset(double d, double e, double d2) {
      this.builder.setTranslation(d, e, d2);
   }

   public void restoreState(WorldRenderer.State state) {
      this.builder.setVertexState(state);
   }

   public void endVertex() {
      this.builder.endVertex();
   }

   public RealmsBufferBuilder normal(float f, float g, float h) {
      return this.from(this.builder.normal(f, g, h));
   }

   public void end() {
      this.builder.finishDrawing();
   }

   public void begin(int i, VertexFormat vertexFormat) {
      this.builder.begin(i, vertexFormat);
   }

   public RealmsBufferBuilder color(int i, int j, int k, int l) {
      return this.from(this.builder.color(i, j, k, l));
   }

   public void faceTex2(int i, int j, int k, int l) {
      this.builder.putBrightness4(i, j, k, l);
   }

   public void postProcessFacePosition(double d, double e, double d2) {
      this.builder.putPosition(d, e, d2);
   }

   public void fixupVertexColor(float f, float g, float h, int i) {
      this.builder.putColorRGB_F(f, g, h, i);
   }

   public RealmsBufferBuilder color(float f, float g, float h, float f2) {
      return this.from(this.builder.color(f, g, h, f2));
   }

   public RealmsVertexFormat getVertexFormat() {
      return new RealmsVertexFormat(this.builder.getVertexFormat());
   }

   public void faceTint(float f, float g, float h, int i) {
      this.builder.putColorMultiplier(f, g, h, i);
   }

   public RealmsBufferBuilder tex2(int i, int j) {
      return this.from(this.builder.lightmap(i, j));
   }

   public void putBulkData(int[] ints) {
      this.builder.addVertexData(ints);
   }

   public RealmsBufferBuilder tex(double d, double e) {
      return this.from(this.builder.tex(d, e));
   }

   public int getVertexCount() {
      return this.builder.getVertexCount();
   }

   public void clear() {
      this.builder.reset();
   }

   public RealmsBufferBuilder vertex(double d, double e, double d2) {
      return this.from(this.builder.pos(d, e, d2));
   }

   public void fixupQuadColorRgb(float f, float g, float h) {
      this.builder.putColorRGB_F4(f, g, h);
   }

   public void noColor() {
      this.builder.noColor();
   }
}
