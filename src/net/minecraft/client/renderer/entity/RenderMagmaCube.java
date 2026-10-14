package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;

public class RenderMagmaCube extends RenderLiving<EntityMagmaCube> {
   private static final ResourceLocation magmaCubeTextures = new ResourceLocation("textures/entity/slime/magmacube.png");

   public RenderMagmaCube(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelMagmaCube(), 0.25F);
   }

   public ResourceLocation getEntityTexture(EntityMagmaCube entity) {
      return magmaCubeTextures;
   }

   public void preRenderCallback(EntityMagmaCube entitylivingbaseIn, float partialTickTime) {
      int i = entitylivingbaseIn.getSlimeSize();
      float f = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime)
         / (i * 0.5F + 1.0F);
      float g = 1.0F / (f + 1.0F);
      float h = i;
      GlStateManager.scale(g * h, 1.0F / g * h, g * h);
   }
}
