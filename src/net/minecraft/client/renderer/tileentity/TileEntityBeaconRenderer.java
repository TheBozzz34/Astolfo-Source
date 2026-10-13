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

   public void renderTileEntityAt(TileEntityBeacon var1, double var2, double var4, double var6, float var8, int var9) {
      long var10 = a ^ 139945268181177L;
      float var13 = var1.shouldBeamRender();
      GlStateManager.alphaFunc(516, 0.1F);
      if (var13 > 0.0F) {
         Tessellator var14 = Tessellator.getInstance();
         WorldRenderer var15 = var14.getWorldRenderer();
         GlStateManager.disableFog();
         List var16 = var1.getBeamSegments();
         int var17 = 0;

         for (int var18 = 0; var18 < var16.size(); var18++) {
            TileEntityBeacon.BeamSegment var19 = (TileEntityBeacon.BeamSegment)var16.get(var18);
            int var20 = var17 + var19.getHeight();
            long var10000 = 1613919046250444787L ^ var10;
            this.bindTexture(beaconBeam);
            GL11.glTexParameterf(3553, 10242, 10497.0F);
            GL11.glTexParameterf(3553, 10243, 10497.0F);
            GlStateManager.disableLighting();
            var10000 = 1330355875583002902L ^ var10;
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
            double var21 = (double)var1.getWorld().getTotalWorldTime() + var8;
            double var23 = MathHelper.func_181162_h(-var21 * 0.2 - MathHelper.floor_double(-var21 * 0.1));
            float var25 = var19.getColors()[0];
            float var26 = var19.getColors()[1];
            float var27 = var19.getColors()[2];
            double var28 = var21 * 0.025 * -1.5;
            double var32 = 0.5 + Math.cos(var28 + (Math.PI * 3.0 / 4.0)) * 0.2;
            double var34 = 0.5 + Math.sin(var28 + (Math.PI * 3.0 / 4.0)) * 0.2;
            double var36 = 0.5 + Math.cos(var28 + (Math.PI / 4)) * 0.2;
            double var38 = 0.5 + Math.sin(var28 + (Math.PI / 4)) * 0.2;
            double var40 = 0.5 + Math.cos(var28 + (Math.PI * 5.0 / 4.0)) * 0.2;
            double var42 = 0.5 + Math.sin(var28 + (Math.PI * 5.0 / 4.0)) * 0.2;
            double var44 = 0.5 + Math.cos(var28 + (Math.PI * 7.0 / 4.0)) * 0.2;
            double var46 = 0.5 + Math.sin(var28 + (Math.PI * 7.0 / 4.0)) * 0.2;
            double var48 = 0.0;
            double var50 = 1.0;
            double var52 = -1.0 + var23;
            double var54 = var19.getHeight() * var13 * 2.5 + var52;
            var15.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            var15.pos(var2 + var32, var4 + var20, var6 + var34).tex(1.0, var54).color(var25, var26, var27, 1.0F).endVertex();
            var10000 = 1453708193736755526L ^ var10;
            var15.pos(var2 + var32, var4 + var17, var6 + var34).tex(1.0, var52).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var36, var4 + var17, var6 + var38).tex(0.0, var52).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var36, var4 + var20, var6 + var38).tex(0.0, var54).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var44, var4 + var20, var6 + var46).tex(1.0, var54).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var44, var4 + var17, var6 + var46).tex(1.0, var52).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var40, var4 + var17, var6 + var42).tex(0.0, var52).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var40, var4 + var20, var6 + var42).tex(0.0, var54).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var36, var4 + var20, var6 + var38).tex(1.0, var54).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var36, var4 + var17, var6 + var38).tex(1.0, var52).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var44, var4 + var17, var6 + var46).tex(0.0, var52).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var44, var4 + var20, var6 + var46).tex(0.0, var54).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var40, var4 + var20, var6 + var42).tex(1.0, var54).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var40, var4 + var17, var6 + var42).tex(1.0, var52).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var32, var4 + var17, var6 + var34).tex(0.0, var52).color(var25, var26, var27, 1.0F).endVertex();
            var15.pos(var2 + var32, var4 + var20, var6 + var34).tex(0.0, var54).color(var25, var26, var27, 1.0F).endVertex();
            var14.draw();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.depthMask(false);
            var28 = 0.2;
            var32 = 0.8;
            var34 = 0.2;
            var36 = 0.2;
            var38 = 0.8;
            var40 = 0.8;
            var42 = 0.8;
            var44 = 0.0;
            var46 = 1.0;
            var48 = -1.0 + var23;
            var50 = var19.getHeight() * var13 + var48;
            var15.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            var15.pos(var2 + 0.2, var4 + var20, var6 + 0.2).tex(1.0, var50).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.2, var4 + var17, var6 + 0.2).tex(1.0, var48).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.8, var4 + var17, var6 + 0.2).tex(0.0, var48).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.8, var4 + var20, var6 + 0.2).tex(0.0, var50).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.8, var4 + var20, var6 + 0.8).tex(1.0, var50).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.8, var4 + var17, var6 + 0.8).tex(1.0, var48).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.2, var4 + var17, var6 + 0.8).tex(0.0, var48).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.2, var4 + var20, var6 + 0.8).tex(0.0, var50).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.8, var4 + var20, var6 + 0.2).tex(1.0, var50).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.8, var4 + var17, var6 + 0.2).tex(1.0, var48).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.8, var4 + var17, var6 + 0.8).tex(0.0, var48).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.8, var4 + var20, var6 + 0.8).tex(0.0, var50).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.2, var4 + var20, var6 + 0.8).tex(1.0, var50).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.2, var4 + var17, var6 + 0.8).tex(1.0, var48).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.2, var4 + var17, var6 + 0.2).tex(0.0, var48).color(var25, var26, var27, 0.125F).endVertex();
            var15.pos(var2 + 0.2, var4 + var20, var6 + 0.2).tex(0.0, var50).color(var25, var26, var27, 0.125F).endVertex();
            var14.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            var17 = var20;
         }

         GlStateManager.enableFog();
      }
   }

   @Override
   public boolean forceTileEntityRender() {
      return true;
   }
}
