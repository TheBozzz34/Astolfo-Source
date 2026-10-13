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
   private static final long a = 83909860454863L;

   public RenderXPOrb(RenderManager var1) {
      super(var1);
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   public void doRender(EntityXPOrb var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2, (float)var4, (float)var6);
      this.bindEntityTexture(var1);
      int var13 = var1.getTextureByXP();
      float var14 = (var13 % 4 * 16 + 0) / 64.0F;
      float var15 = (var13 % 4 * 16 + 16) / 64.0F;
      float var16 = (var13 / 4 * 16 + 0) / 64.0F;
      float var17 = (var13 / 4 * 16 + 16) / 64.0F;
      float var18 = 1.0F;
      float var19 = 0.5F;
      float var20 = 0.25F;
      int var21 = var1.getBrightnessForRender(var9);
      int var22 = var21 % 65536;
      int var23 = var21 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var22 / 1.0F, var23 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float var25 = (var1.xpColor + var9) / 2.0F;
      var23 = (int)((MathHelper.sin(var25 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      int var27 = (int)((MathHelper.sin(var25 + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
      GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.3F, 0.3F, 0.3F);
      Tessellator var29 = Tessellator.getInstance();
      WorldRenderer var30 = var29.getWorldRenderer();
      var30.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      int var31 = var23;
      int var32 = 255;
      int var33 = var27;
      if (Config.isCustomColors()) {
         int var34 = CustomColors.getXpOrbColor(var25);
         if (var34 >= 0) {
            var31 = var34 >> 16 & 0xFF;
            var32 = var34 >> 8 & 0xFF;
            var33 = var34 >> 0 & 0xFF;
         }
      }

      var30.pos(0.0F - var19, 0.0F - var20, 0.0).tex(var14, var17).color(var31, var32, var33, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      var30.pos(var18 - var19, 0.0F - var20, 0.0).tex(var15, var17).color(var31, var32, var33, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      var30.pos(var18 - var19, 1.0F - var20, 0.0).tex(var15, var16).color(var31, var32, var33, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      var30.pos(0.0F - var19, 1.0F - var20, 0.0).tex(var14, var16).color(var31, var32, var33, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      var29.draw();
      GlStateManager.disableBlend();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(EntityXPOrb var1) {
      return experienceOrbTextures;
   }
}
