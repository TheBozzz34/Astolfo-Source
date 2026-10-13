package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItemWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;

public class RenderWitch extends RenderLiving<EntityWitch> {
   private static final ResourceLocation witchTextures = new ResourceLocation("textures/entity/witch.png");
   private static final long d = 6004993694874L;

   public RenderWitch(RenderManager var1) {
      super(var1, new ModelWitch(0.0F), 0.5F);
      this.addLayer(new LayerHeldItemWitch(this));
   }

   public void doRender(EntityWitch var1, double var2, double var4, double var6, float var8, float var9) {
      ((ModelWitch)this.mainModel).field_82900_g = var1.getHeldItem() != null;
      super.doRender(var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(EntityWitch var1) {
      return witchTextures;
   }

   @Override
   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.0F, 0.1875F, 0.0F);
   }

   public void preRenderCallback(EntityWitch var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.scale(var3, var3, var3);
   }
}
