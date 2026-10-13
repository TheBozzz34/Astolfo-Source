package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderTNTPrimed extends Render<EntityTNTPrimed> {
   private static final long a = 69224644639933L;

   public RenderTNTPrimed(RenderManager var1) {
      super(var1);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityTNTPrimed var1, double var2, double var4, double var6, float var8, float var9) {
      long var10 = a ^ 91156797491890L;
      BlockRendererDispatcher var13 = Minecraft.getMinecraft().getBlockRendererDispatcher();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2, (float)var4 + 0.5F, (float)var6);
      if (var1.fuse - var9 + 1.0F < 10.0F) {
         float var14 = 1.0F - (var1.fuse - var9 + 1.0F) / 10.0F;
         var14 = MathHelper.clamp_float(var14, 0.0F, 1.0F);
         var14 *= var14;
         var14 *= var14;
         float var15 = 1.0F + var14 * 0.3F;
         GlStateManager.scale(var15, var15, var15);
      }

      float var19 = (1.0F - (var1.fuse - var9 + 1.0F) / 100.0F) * 0.8F;
      this.bindEntityTexture(var1);
      GlStateManager.translate(-0.5F, -0.5F, 0.5F);
      var13.renderBlockBrightness(Blocks.tnt.getDefaultState(), var1.getBrightness(var9));
      GlStateManager.translate(0.0F, 0.0F, 1.0F);
      if (var1.fuse / 5 % 2 == 0) {
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         long var10000 = 4443019072265457914L ^ var10;
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 772);
         GlStateManager.color(1.0F, 1.0F, 1.0F, var19);
         GlStateManager.doPolygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         var13.renderBlockBrightness(Blocks.tnt.getDefaultState(), 1.0F);
         GlStateManager.doPolygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
      }

      GlStateManager.popMatrix();
      super.doRender(var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(EntityTNTPrimed var1) {
      return TextureMap.locationBlocksTexture;
   }
}
