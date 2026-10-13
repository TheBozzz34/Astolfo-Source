package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerWolfCollar;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;

public class RenderWolf extends RenderLiving<EntityWolf> {
   private static final ResourceLocation wolfTextures = new ResourceLocation("textures/entity/wolf/wolf.png");
   private static final ResourceLocation tamedWolfTextures = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
   private static final ResourceLocation anrgyWolfTextures = new ResourceLocation("textures/entity/wolf/wolf_angry.png");
   private static final long d = 108982349734651L;

   public RenderWolf(RenderManager var1, ModelBase var2, float var3) {
      super(var1, var2, var3);
      this.addLayer(new LayerWolfCollar(this));
   }

   public float handleRotationFloat(EntityWolf var1, float var2) {
      return var1.getTailRotation();
   }

   public void doRender(EntityWolf var1, double var2, double var4, double var6, float var8, float var9) {
      if (var1.isWolfWet()) {
         float var13 = var1.getBrightness(var9) * var1.getShadingWhileWet(var9);
         GlStateManager.color(var13, var13, var13);
      }

      super.doRender(var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(EntityWolf var1) {
      return var1.isTamed() ? tamedWolfTextures : (var1.isAngry() ? anrgyWolfTextures : wolfTextures);
   }
}
