package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerWitherAura;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.ResourceLocation;

public class RenderWither extends RenderLiving<EntityWither> {
   private static final ResourceLocation invulnerableWitherTextures = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation witherTextures = new ResourceLocation("textures/entity/wither/wither.png");
   private static final long e = 96085531808302L;
   private static final long g = 8610858392765857872L;

   public RenderWither(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelWither(0.0F), 1.0F);
      this.addLayer(new LayerWitherAura(this));
   }

   public void doRender(EntityWither entity, double x, double y, double z, float entityYaw, float partialTicks) {
      BossStatus.setBossStatus(entity, true);
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityWither entity) {
      int i = entity.getInvulTime();
      return i <= 0 || i <= (int)g && i / 5 % 2 == 1 ? witherTextures : invulnerableWitherTextures;
   }

   public void preRenderCallback(EntityWither entitylivingbaseIn, float partialTickTime) {
      float f = 2.0F;
      int i = entitylivingbaseIn.getInvulTime();
      if (i > 0) {
         f -= (i - partialTickTime) / 220.0F * 0.5F;
      }

      GlStateManager.scale(f, f, f);
   }
}
