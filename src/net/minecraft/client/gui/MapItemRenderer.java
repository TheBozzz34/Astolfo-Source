package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class MapItemRenderer {
   private static final ResourceLocation mapIcons = new ResourceLocation("textures/map/map_icons.png");
   private final TextureManager textureManager;
   private final Map<String, MapItemRenderer.Instance> loadedMaps = Maps.newHashMap();
   private static final long a = 102715040440845L;

   public MapItemRenderer(TextureManager textureManagerIn) {
      this.textureManager = textureManagerIn;
   }

   public void updateMapTexture(MapData mapdataIn) {
      this.getMapRendererInstance(mapdataIn).updateMapTexture();
   }

   public void renderMap(MapData mapdataIn, boolean bl) {
      this.getMapRendererInstance(mapdataIn).render(bl);
   }

   private MapItemRenderer.Instance getMapRendererInstance(MapData mapdataIn) {
      MapItemRenderer.Instance instance = this.loadedMaps.get(mapdataIn.mapName);
      if (instance == null) {
         instance = new MapItemRenderer.Instance(mapdataIn);
         this.loadedMaps.put(mapdataIn.mapName, instance);
      }

      return instance;
   }

   public void clearLoadedMaps() {
      for (MapItemRenderer.Instance instance : this.loadedMaps.values()) {
         this.textureManager.deleteTexture(instance.location);
      }

      this.loadedMaps.clear();
   }

   public class Instance {
      private final MapData mapData;
      private final DynamicTexture mapTexture;
      private final ResourceLocation location;
      private final int[] mapTextureData;
      private static final long a = 62848190280753L;

      public Instance(MapData mapdataIn) {
         this.mapData = mapdataIn;
         this.mapTexture = new DynamicTexture(128, 128);
         this.mapTextureData = this.mapTexture.getTextureData();
         this.location = MapItemRenderer.this.textureManager.getDynamicTextureLocation("map/" + mapdataIn.mapName, this.mapTexture);

         for (int i = 0; i < this.mapTextureData.length; i++) {
            this.mapTextureData[i] = 0;
         }
      }

      private void updateMapTexture() {
         for (int i = 0; i < 16384; i++) {
            int j = this.mapData.colors[i] & 255;
            if (j / 4 == 0) {
               this.mapTextureData[i] = (i + i / 128 & 1) * 8 + 16 << 24;
            } else {
               this.mapTextureData[i] = MapColor.mapColorArray[j / 4].getMapColor(j & 3);
            }
         }

         this.mapTexture.updateDynamicTexture();
      }

      private void render(boolean noOverlayRendering) {
         byte b = 0;
         byte b2 = 0;
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         float f = 0.0F;
         MapItemRenderer.this.textureManager.bindTexture(this.location);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(1, 771, 0, 1);
         GlStateManager.disableAlpha();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(b + 0 + f, b2 + 128 - f, -0.01F).tex(0.0, 1.0).endVertex();
         worldRenderer.pos(b + 128 - f, b2 + 128 - f, -0.01F).tex(1.0, 1.0).endVertex();
         worldRenderer.pos(b + 128 - f, b2 + 0 + f, -0.01F).tex(1.0, 0.0).endVertex();
         worldRenderer.pos(b + 0 + f, b2 + 0 + f, -0.01F).tex(0.0, 0.0).endVertex();
         tessellator.draw();
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
         MapItemRenderer.this.textureManager.bindTexture(MapItemRenderer.mapIcons);
         int i = 0;

         for (Vec4b vec4b : this.mapData.mapDecorations.values()) {
            if (!noOverlayRendering || vec4b.func_176110_a() == 1) {
               GlStateManager.pushMatrix();
               GlStateManager.translate(b + vec4b.func_176112_b() / 2.0F + 64.0F, b2 + vec4b.func_176113_c() / 2.0F + 64.0F, -0.02F);
               GlStateManager.rotate(vec4b.func_176111_d() * 360 / 16.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(4.0F, 4.0F, 3.0F);
               GlStateManager.translate(-0.125F, 0.125F, 0.0F);
               byte b3 = vec4b.func_176110_a();
               float g = (b3 % 4 + 0) / 4.0F;
               float h = (b3 / 4 + 0) / 4.0F;
               float f2 = (b3 % 4 + 1) / 4.0F;
               float f3 = (b3 / 4 + 1) / 4.0F;
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
               worldRenderer.pos(-1.0, 1.0, i * -0.001F).tex(g, h).endVertex();
               worldRenderer.pos(1.0, 1.0, i * -0.001F).tex(f2, h).endVertex();
               worldRenderer.pos(1.0, -1.0, i * -0.001F).tex(f2, f3).endVertex();
               worldRenderer.pos(-1.0, -1.0, i * -0.001F).tex(g, f3).endVertex();
               tessellator.draw();
               GlStateManager.popMatrix();
               i++;
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, -0.04F);
         GlStateManager.scale(1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }
}
