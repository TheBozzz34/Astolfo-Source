package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderPainting extends Render<EntityPainting> {
   private static final ResourceLocation KRISTOFFER_PAINTING_TEXTURE = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");
   private static final long a = 51539349536466L;

   public RenderPainting(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void doRender(EntityPainting entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.enableRescaleNormal();
      this.bindEntityTexture(entity);
      EntityPainting.EnumArt enumArt = entity.art;
      float f = 0.0625F;
      GlStateManager.scale(f, f, f);
      this.renderPainting(entity, enumArt.sizeX, enumArt.sizeY, enumArt.offsetX, enumArt.offsetY);
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityPainting entity) {
      return KRISTOFFER_PAINTING_TEXTURE;
   }

   private void renderPainting(EntityPainting painting, int width, int height, int textureU, int textureV) {
      float f = -width / 2.0F;
      float g = -height / 2.0F;
      float h = 0.5F;
      float f2 = 0.75F;
      float f3 = 0.8125F;
      float f4 = 0.0F;
      float f5 = 0.0625F;
      float f6 = 0.75F;
      float f7 = 0.8125F;
      float f8 = 0.001953125F;
      float f9 = 0.001953125F;
      float f10 = 0.7519531F;
      float f11 = 0.7519531F;
      float f12 = 0.0F;
      float f13 = 0.0625F;

      for (int i = 0; i < width / 16; i++) {
         for (int j = 0; j < height / 16; j++) {
            float f14 = f + (i + 1) * 16;
            float f15 = f + i * 16;
            float f16 = g + (j + 1) * 16;
            float f17 = g + j * 16;
            this.setLightmap(painting, (f14 + f15) / 2.0F, (f16 + f17) / 2.0F);
            float f18 = (textureU + width - i * 16) / 256.0F;
            float f19 = (textureU + width - (i + 1) * 16) / 256.0F;
            float f20 = (textureV + height - j * 16) / 256.0F;
            float f21 = (textureV + height - (j + 1) * 16) / 256.0F;
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
            worldRenderer.pos(f14, f17, -h).tex(f19, f20).normal(0.0F, 0.0F, -1.0F).endVertex();
            worldRenderer.pos(f15, f17, -h).tex(f18, f20).normal(0.0F, 0.0F, -1.0F).endVertex();
            worldRenderer.pos(f15, f16, -h).tex(f18, f21).normal(0.0F, 0.0F, -1.0F).endVertex();
            worldRenderer.pos(f14, f16, -h).tex(f19, f21).normal(0.0F, 0.0F, -1.0F).endVertex();
            worldRenderer.pos(f14, f16, h).tex(f2, f4).normal(0.0F, 0.0F, 1.0F).endVertex();
            worldRenderer.pos(f15, f16, h).tex(f3, f4).normal(0.0F, 0.0F, 1.0F).endVertex();
            worldRenderer.pos(f15, f17, h).tex(f3, f5).normal(0.0F, 0.0F, 1.0F).endVertex();
            worldRenderer.pos(f14, f17, h).tex(f2, f5).normal(0.0F, 0.0F, 1.0F).endVertex();
            worldRenderer.pos(f14, f16, -h).tex(f6, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRenderer.pos(f15, f16, -h).tex(f7, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRenderer.pos(f15, f16, h).tex(f7, f9).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRenderer.pos(f14, f16, h).tex(f6, f9).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRenderer.pos(f14, f17, h).tex(f6, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldRenderer.pos(f15, f17, h).tex(f7, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldRenderer.pos(f15, f17, -h).tex(f7, f9).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldRenderer.pos(f14, f17, -h).tex(f6, f9).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldRenderer.pos(f14, f16, h).tex(f11, f12).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f14, f17, h).tex(f11, f13).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f14, f17, -h).tex(f10, f13).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f14, f16, -h).tex(f10, f12).normal(-1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f15, f16, -h).tex(f11, f12).normal(1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f15, f17, -h).tex(f11, f13).normal(1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f15, f17, h).tex(f10, f13).normal(1.0F, 0.0F, 0.0F).endVertex();
            worldRenderer.pos(f15, f16, h).tex(f10, f12).normal(1.0F, 0.0F, 0.0F).endVertex();
            tessellator.draw();
         }
      }
   }

   private void setLightmap(EntityPainting painting, float f, float g) {
      int i = MathHelper.floor_double(painting.posX);
      int j = MathHelper.floor_double(painting.posY + g / 16.0F);
      int k = MathHelper.floor_double(painting.posZ);
      EnumFacing enumFacing = painting.facingDirection;
      if (enumFacing == EnumFacing.NORTH) {
         i = MathHelper.floor_double(painting.posX + f / 16.0F);
      }

      if (enumFacing == EnumFacing.WEST) {
         k = MathHelper.floor_double(painting.posZ - f / 16.0F);
      }

      if (enumFacing == EnumFacing.SOUTH) {
         i = MathHelper.floor_double(painting.posX - f / 16.0F);
      }

      if (enumFacing == EnumFacing.EAST) {
         k = MathHelper.floor_double(painting.posZ + f / 16.0F);
      }

      int l = this.renderManager.worldObj.getCombinedLight(new BlockPos(i, j, k), 0);
      int m = l % 65536;
      int n = l / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, m, n);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
   }
}
