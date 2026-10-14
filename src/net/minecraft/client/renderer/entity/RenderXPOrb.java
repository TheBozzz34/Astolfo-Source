package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderXPOrb extends Render<EntityXPOrb> {
   private static final ResourceLocation experienceOrbTextures = new ResourceLocation("textures/entity/experience_orb.png");

   public RenderXPOrb(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   public void doRender(EntityXPOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      this.bindEntityTexture(entity);
      int i = entity.getTextureByXP();
      float f = (i % 4 * 16 + 0) / 64.0F;
      float g = (i % 4 * 16 + 16) / 64.0F;
      float h = (i / 4 * 16 + 0) / 64.0F;
      float f2 = (i / 4 * 16 + 16) / 64.0F;
      float f3 = 1.0F;
      float f4 = 0.5F;
      float f5 = 0.25F;
      int j = entity.getBrightnessForRender(partialTicks);
      int k = j % 65536;
      int l = j / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, l / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f6 = (entity.xpColor + partialTicks) / 2.0F;
      l = (int)((MathHelper.sin(f6 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      int m = (int)((MathHelper.sin(f6 + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
      GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.3F, 0.3F, 0.3F);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      int n = l;
      int i2 = 255;
      int i3 = m;
      if (Config.isCustomColors()) {
         int i4 = CustomColors.getXpOrbColor(f6);
         if (i4 >= 0) {
            n = i4 >> 16 & 0xFF;
            i2 = i4 >> 8 & 0xFF;
            i3 = i4 >> 0 & 0xFF;
         }
      }

      worldRenderer.pos(0.0F - f4, 0.0F - f5, 0.0).tex(f, f2).color(n, i2, i3, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(f3 - f4, 0.0F - f5, 0.0).tex(g, f2).color(n, i2, i3, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(f3 - f4, 1.0F - f5, 0.0).tex(g, h).color(n, i2, i3, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(0.0F - f4, 1.0F - f5, 0.0).tex(f, h).color(n, i2, i3, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      tessellator.draw();
      GlStateManager.disableBlend();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityXPOrb entity) {
      return experienceOrbTextures;
   }
}
