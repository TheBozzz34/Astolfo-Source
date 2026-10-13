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

   public MapItemRenderer(TextureManager var1) {
      this.textureManager = var1;
   }

   public void updateMapTexture(MapData var1) {
      this.getMapRendererInstance(var1).updateMapTexture();
   }

   public void renderMap(MapData var1, boolean var2) {
      this.getMapRendererInstance(var1).render(var2);
   }

   private MapItemRenderer.Instance getMapRendererInstance(MapData var1) {
      MapItemRenderer.Instance var5 = this.loadedMaps.get(var1.mapName);
      if (var5 == null) {
         var5 = new MapItemRenderer.Instance(var1);
         this.loadedMaps.put(var1.mapName, var5);
      }

      return var5;
   }

   public void clearLoadedMaps() {
      for (MapItemRenderer.Instance var5 : this.loadedMaps.values()) {
         this.textureManager.deleteTexture(var5.location);
      }

      this.loadedMaps.clear();
   }

   public class Instance {
      private final MapData mapData;
      private final DynamicTexture mapTexture;
      private final ResourceLocation location;
      private final int[] mapTextureData;
      private static final long a = 62848190280753L;

      public Instance(MapData var2) {
         this.mapData = var2;
         this.mapTexture = new DynamicTexture(128, 128);
         this.mapTextureData = this.mapTexture.getTextureData();
         this.location = MapItemRenderer.this.textureManager.getDynamicTextureLocation("map/" + var2.mapName, this.mapTexture);

         for (int var6 = 0; var6 < this.mapTextureData.length; var6++) {
            this.mapTextureData[var6] = 0;
         }
      }

      private void updateMapTexture() {
         for (int var4 = 0; var4 < 16384; var4++) {
            int var5 = this.mapData.colors[var4] & 255;
            if (var5 / 4 == 0) {
               this.mapTextureData[var4] = (var4 + var4 / 128 & 1) * 8 + 16 << 24;
            } else {
               this.mapTextureData[var4] = MapColor.mapColorArray[var5 / 4].getMapColor(var5 & 3);
            }
         }

         this.mapTexture.updateDynamicTexture();
      }

      private void render(boolean var1) {
         byte var5 = 0;
         byte var6 = 0;
         Tessellator var7 = Tessellator.getInstance();
         WorldRenderer var8 = var7.getWorldRenderer();
         float var9 = 0.0F;
         MapItemRenderer.this.textureManager.bindTexture(this.location);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(1, 771, 0, 1);
         GlStateManager.disableAlpha();
         var8.begin(7, DefaultVertexFormats.POSITION_TEX);
         var8.pos(var5 + 0 + var9, var6 + 128 - var9, -0.01F).tex(0.0, 1.0).endVertex();
         var8.pos(var5 + 128 - var9, var6 + 128 - var9, -0.01F).tex(1.0, 1.0).endVertex();
         var8.pos(var5 + 128 - var9, var6 + 0 + var9, -0.01F).tex(1.0, 0.0).endVertex();
         var8.pos(var5 + 0 + var9, var6 + 0 + var9, -0.01F).tex(0.0, 0.0).endVertex();
         var7.draw();
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
         MapItemRenderer.this.textureManager.bindTexture(MapItemRenderer.mapIcons);
         int var10 = 0;

         for (Vec4b var12 : this.mapData.mapDecorations.values()) {
            if (!var1 || var12.func_176110_a() == 1) {
               GlStateManager.pushMatrix();
               GlStateManager.translate(var5 + var12.func_176112_b() / 2.0F + 64.0F, var6 + var12.func_176113_c() / 2.0F + 64.0F, -0.02F);
               GlStateManager.rotate(var12.func_176111_d() * 360 / 16.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(4.0F, 4.0F, 3.0F);
               GlStateManager.translate(-0.125F, 0.125F, 0.0F);
               byte var13 = var12.func_176110_a();
               float var14 = (var13 % 4 + 0) / 4.0F;
               float var15 = (var13 / 4 + 0) / 4.0F;
               float var16 = (var13 % 4 + 1) / 4.0F;
               float var17 = (var13 / 4 + 1) / 4.0F;
               var8.begin(7, DefaultVertexFormats.POSITION_TEX);
               var8.pos(-1.0, 1.0, var10 * -0.001F).tex(var14, var15).endVertex();
               var8.pos(1.0, 1.0, var10 * -0.001F).tex(var16, var15).endVertex();
               var8.pos(1.0, -1.0, var10 * -0.001F).tex(var16, var17).endVertex();
               var8.pos(-1.0, -1.0, var10 * -0.001F).tex(var14, var17).endVertex();
               var7.draw();
               GlStateManager.popMatrix();
               var10++;
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, -0.04F);
         GlStateManager.scale(1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }
}
