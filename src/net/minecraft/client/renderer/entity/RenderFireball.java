package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

public class RenderFireball extends Render<EntityFireball> {
   private float scale;
   private static final long a = 117428612093944L;
   private static final long c = 7684220190791303175L;

   public RenderFireball(RenderManager var1, float var2) {
      super(var1);
      this.scale = var2;
   }

   public void doRender(EntityFireball var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      this.bindEntityTexture(var1);
      GlStateManager.translate((float)var2, (float)var4, (float)var6);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(this.scale, this.scale, this.scale);
      TextureAtlasSprite var12 = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Items.fire_charge);
      Tessellator var13 = Tessellator.getInstance();
      WorldRenderer var14 = var13.getWorldRenderer();
      float var15 = var12.getMinU();
      float var16 = var12.getMaxU();
      float var17 = var12.getMinV();
      float var18 = var12.getMaxV();
      GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      var14.begin((int)c, DefaultVertexFormats.POSITION_TEX_NORMAL);
      var14.pos(-0.5, -0.25, 0.0).tex(var15, var18).normal(0.0F, 1.0F, 0.0F).endVertex();
      var14.pos(0.5, -0.25, 0.0).tex(var16, var18).normal(0.0F, 1.0F, 0.0F).endVertex();
      var14.pos(0.5, 0.75, 0.0).tex(var16, var17).normal(0.0F, 1.0F, 0.0F).endVertex();
      var14.pos(-0.5, 0.75, 0.0).tex(var15, var17).normal(0.0F, 1.0F, 0.0F).endVertex();
      var13.draw();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(EntityFireball var1) {
      return TextureMap.locationBlocksTexture;
   }
}
