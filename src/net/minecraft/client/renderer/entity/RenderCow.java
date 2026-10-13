package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderCow extends RenderLiving<EntityCow> {
   private static final ResourceLocation cowTextures = new ResourceLocation("textures/entity/cow/cow.png");

   public RenderCow(RenderManager var1, ModelBase var2, float var3) {
      super(var1, var2, var3);
   }

   public ResourceLocation getEntityTexture(EntityCow var1) {
      return cowTextures;
   }
}
