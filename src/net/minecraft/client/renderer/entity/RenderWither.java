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

   public RenderWither(RenderManager var1) {
      super(var1, new ModelWither(0.0F), 1.0F);
      this.addLayer(new LayerWitherAura(this));
   }

   public void doRender(EntityWither var1, double var2, double var4, double var6, float var8, float var9) {
      BossStatus.setBossStatus(var1, true);
      super.doRender(var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(EntityWither var1) {
      int var5 = var1.getInvulTime();
      return var5 <= 0 || var5 <= (int)g && var5 / 5 % 2 == 1 ? witherTextures : invulnerableWitherTextures;
   }

   public void preRenderCallback(EntityWither var1, float var2) {
      float var6 = 2.0F;
      int var7 = var1.getInvulTime();
      if (var7 > 0) {
         var6 -= (var7 - var2) / 220.0F * 0.5F;
      }

      GlStateManager.scale(var6, var6, var6);
   }
}
