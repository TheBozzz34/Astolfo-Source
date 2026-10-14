package net.minecraft.realms;

import net.minecraft.client.renderer.Tessellator;

public class Tezzelator {
   public static Tessellator t = Tessellator.getInstance();
   public static final Tezzelator instance = new Tezzelator();

   public void U() {
      t.draw();
   }

   public Tezzelator vertex(double d, double d2, double d3) {
      t.getWorldRenderer().pos(d, d2, d3);
      return this;
   }

   public void color(float f, float g, float h, float f2) {
      t.getWorldRenderer().color(f, g, h, f2);
   }

   public void tex2(short s, short s2) {
      t.getWorldRenderer().lightmap(s, s2);
   }

   public void normal(float f, float g, float h) {
      t.getWorldRenderer().normal(f, g, h);
   }

   public void begin(int i, RealmsVertexFormat realmsVertexFormat) {
      t.getWorldRenderer().begin(i, realmsVertexFormat.getVertexFormat());
   }

   public void r() {
      t.getWorldRenderer().endVertex();
   }

   public void offset(double d, double d2, double d3) {
      t.getWorldRenderer().setTranslation(d, d2, d3);
   }

   public RealmsBufferBuilder color(int i, int j, int k, int l) {
      return new RealmsBufferBuilder(t.getWorldRenderer().color(i, j, k, l));
   }

   public Tezzelator tex(double d, double d2) {
      t.getWorldRenderer().tex(d, d2);
      return this;
   }
}
