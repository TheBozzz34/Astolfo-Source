package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerSlimeGel;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;

public class RenderSlime extends RenderLiving<EntitySlime> {
   private static final ResourceLocation slimeTextures = new ResourceLocation("textures/entity/slime/slime.png");

   public RenderSlime(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn, modelBaseIn, shadowSizeIn);
      this.addLayer(new LayerSlimeGel(this));
   }

   public void doRender(EntitySlime entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.shadowSize = 0.25F * entity.getSlimeSize();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public void preRenderCallback(EntitySlime entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSlimeSize();
      float g = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime)
         / (f * 0.5F + 1.0F);
      float h = 1.0F / (g + 1.0F);
      GlStateManager.scale(h * f, 1.0F / h * f, h * f);
   }

   public ResourceLocation getEntityTexture(EntitySlime entity) {
      return slimeTextures;
   }
}
