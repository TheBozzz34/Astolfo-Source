package net.minecraft.realms;

import net.minecraft.client.renderer.Tessellator;

public class Tezzelator {
   public static Tessellator tessellator = Tessellator.getInstance();
   public static final Tezzelator instance = new Tezzelator();

   public void end() {
      tessellator.draw();
   }

   public Tezzelator vertex(double d, double e, double d2) {
      tessellator.getWorldRenderer().pos(d, e, d2);
      return this;
   }

   public void color(float f, float g, float h, float f2) {
      tessellator.getWorldRenderer().color(f, g, h, f2);
   }

   public void tex2(short s, short s2) {
      tessellator.getWorldRenderer().lightmap(s, s2);
   }

   public void normal(float f, float g, float h) {
      tessellator.getWorldRenderer().normal(f, g, h);
   }

   public void begin(int i, RealmsVertexFormat realmsVertexFormat) {
      tessellator.getWorldRenderer().begin(i, realmsVertexFormat.getVertexFormat());
   }

   public void endVertex() {
      tessellator.getWorldRenderer().endVertex();
   }

   public void offset(double d, double e, double d2) {
      tessellator.getWorldRenderer().setTranslation(d, e, d2);
   }

   public RealmsBufferBuilder color(int i, int j, int k, int l) {
      return new RealmsBufferBuilder(tessellator.getWorldRenderer().color(i, j, k, l));
   }

   public Tezzelator tex(double d, double e) {
      tessellator.getWorldRenderer().tex(d, e);
      return this;
   }
}
