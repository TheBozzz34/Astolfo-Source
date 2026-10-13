package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;

public class RenderSpider<T extends EntitySpider> extends RenderLiving<T> {
   private static final ResourceLocation spiderTextures = new ResourceLocation("textures/entity/spider/spider.png");

   public RenderSpider(RenderManager var1) {
      super(var1, new ModelSpider(), 1.0F);
      this.addLayer(new LayerSpiderEyes(this));
   }

   public float getDeathMaxRotation(T var1) {
      return 180.0F;
   }

   public ResourceLocation getEntityTexture(T var1) {
      return spiderTextures;
   }
}
