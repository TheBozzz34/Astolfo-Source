package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBlaze;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.ResourceLocation;

public class RenderBlaze extends RenderLiving<EntityBlaze> {
   private static final ResourceLocation blazeTextures = new ResourceLocation("textures/entity/blaze.png");

   public RenderBlaze(RenderManager var1) {
      super(var1, new ModelBlaze(), 0.5F);
   }

   public ResourceLocation getEntityTexture(EntityBlaze var1) {
      return blazeTextures;
   }
}
