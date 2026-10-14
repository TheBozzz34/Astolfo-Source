package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer<TileEntityBeacon> {
   private static final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");
   private static final long a = 47042226319443L;

   public void renderTileEntityAt(TileEntityBeacon te, double x, double y, double z, float partialTicks, int destroyStage) {
      long l = a ^ 139945268181177L;
      float f = te.shouldBeamRender();
      GlStateManager.alphaFunc(516, 0.1F);
      if (f > 0.0F) {
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         GlStateManager.disableFog();
         List list = te.getBeamSegments();
         int i = 0;

         for (int j = 0; j < list.size(); j++) {
            TileEntityBeacon.BeamSegment beamSegment = (TileEntityBeacon.BeamSegment)list.get(j);
            int k = i + beamSegment.getHeight();
            long m = 1613919046250444787L ^ l;
            this.bindTexture(beaconBeam);
            GL11.glTexParameterf(3553, 10242, 10497.0F);
            GL11.glTexParameterf(3553, 10243, 10497.0F);
            GlStateManager.disableLighting();
            m = 1330355875583002902L ^ l;
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
            double d = (double)te.getWorld().getTotalWorldTime() + partialTicks;
            double e = MathHelper.func_181162_h(-d * 0.2 - MathHelper.floor_double(-d * 0.1));
            float g = beamSegment.getColors()[0];
            float h = beamSegment.getColors()[1];
            float f2 = beamSegment.getColors()[2];
            double d2 = d * 0.025 * -1.5;
            double d3 = 0.5 + Math.cos(d2 + (Math.PI * 3.0 / 4.0)) * 0.2;
            double d4 = 0.5 + Math.sin(d2 + (Math.PI * 3.0 / 4.0)) * 0.2;
            double d5 = 0.5 + Math.cos(d2 + (Math.PI / 4)) * 0.2;
            double d6 = 0.5 + Math.sin(d2 + (Math.PI / 4)) * 0.2;
            double d7 = 0.5 + Math.cos(d2 + (Math.PI * 5.0 / 4.0)) * 0.2;
            double d8 = 0.5 + Math.sin(d2 + (Math.PI * 5.0 / 4.0)) * 0.2;
            double d9 = 0.5 + Math.cos(d2 + (Math.PI * 7.0 / 4.0)) * 0.2;
            double d10 = 0.5 + Math.sin(d2 + (Math.PI * 7.0 / 4.0)) * 0.2;
            double d11 = 0.0;
            double d12 = 1.0;
            double d13 = -1.0 + e;
            double d14 = beamSegment.getHeight() * f * 2.5 + d13;
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(x + d3, y + k, z + d4).tex(1.0, d14).color(g, h, f2, 1.0F).endVertex();
            m = 1453708193736755526L ^ l;
            worldRenderer.pos(x + d3, y + i, z + d4).tex(1.0, d13).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d5, y + i, z + d6).tex(0.0, d13).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d5, y + k, z + d6).tex(0.0, d14).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d9, y + k, z + d10).tex(1.0, d14).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d9, y + i, z + d10).tex(1.0, d13).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d7, y + i, z + d8).tex(0.0, d13).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d7, y + k, z + d8).tex(0.0, d14).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d5, y + k, z + d6).tex(1.0, d14).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d5, y + i, z + d6).tex(1.0, d13).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d9, y + i, z + d10).tex(0.0, d13).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d9, y + k, z + d10).tex(0.0, d14).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d7, y + k, z + d8).tex(1.0, d14).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d7, y + i, z + d8).tex(1.0, d13).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d3, y + i, z + d4).tex(0.0, d13).color(g, h, f2, 1.0F).endVertex();
            worldRenderer.pos(x + d3, y + k, z + d4).tex(0.0, d14).color(g, h, f2, 1.0F).endVertex();
            tessellator.draw();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.depthMask(false);
            d2 = 0.2;
            d3 = 0.8;
            d4 = 0.2;
            d5 = 0.2;
            d6 = 0.8;
            d7 = 0.8;
            d8 = 0.8;
            d9 = 0.0;
            d10 = 1.0;
            d11 = -1.0 + e;
            d12 = beamSegment.getHeight() * f + d11;
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(x + 0.2, y + k, z + 0.2).tex(1.0, d12).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.2, y + i, z + 0.2).tex(1.0, d11).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.8, y + i, z + 0.2).tex(0.0, d11).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.8, y + k, z + 0.2).tex(0.0, d12).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.8, y + k, z + 0.8).tex(1.0, d12).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.8, y + i, z + 0.8).tex(1.0, d11).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.2, y + i, z + 0.8).tex(0.0, d11).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.2, y + k, z + 0.8).tex(0.0, d12).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.8, y + k, z + 0.2).tex(1.0, d12).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.8, y + i, z + 0.2).tex(1.0, d11).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.8, y + i, z + 0.8).tex(0.0, d11).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.8, y + k, z + 0.8).tex(0.0, d12).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.2, y + k, z + 0.8).tex(1.0, d12).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.2, y + i, z + 0.8).tex(1.0, d11).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.2, y + i, z + 0.2).tex(0.0, d11).color(g, h, f2, 0.125F).endVertex();
            worldRenderer.pos(x + 0.2, y + k, z + 0.2).tex(0.0, d12).color(g, h, f2, 0.125F).endVertex();
            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            i = k;
         }

         GlStateManager.enableFog();
      }
   }

   @Override
   public boolean forceTileEntityRender() {
      return true;
   }
}
