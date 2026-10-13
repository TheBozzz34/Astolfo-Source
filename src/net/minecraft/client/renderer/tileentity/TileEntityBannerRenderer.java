package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityBannerRenderer extends TileEntitySpecialRenderer<TileEntityBanner> {
   private static final Map<String, TileEntityBannerRenderer.TimedBannerTexture> DESIGNS = Maps.newHashMap();
   private static final ResourceLocation BANNERTEXTURES = new ResourceLocation("textures/entity/banner_base.png");
   private ModelBanner bannerModel = new ModelBanner();
   private static final long a = 36367896534952L;

   public void renderTileEntityAt(TileEntityBanner var1, double var2, double var4, double var6, float var8, int var9) {
      boolean var13 = var1.getWorld() != null;
      boolean var14 = !var13 || var1.getBlockType() == Blocks.standing_banner;
      int var15 = var13 ? var1.getBlockMetadata() : 0;
      long var16 = var13 ? var1.getWorld().getTotalWorldTime() : 0L;
      GlStateManager.pushMatrix();
      float var18 = 0.6666667F;
      if (var14) {
         GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 0.75F * var18, (float)var6 + 0.5F);
         float var19 = var15 * 360 / 16.0F;
         GlStateManager.rotate(-var19, 0.0F, 1.0F, 0.0F);
         this.bannerModel.bannerStand.showModel = true;
      } else {
         float var22 = 0.0F;
         if (var15 == 2) {
            var22 = 180.0F;
         }

         if (var15 == 4) {
            var22 = 90.0F;
         }

         if (var15 == 5) {
            var22 = -90.0F;
         }

         GlStateManager.translate((float)var2 + 0.5F, (float)var4 - 0.25F * var18, (float)var6 + 0.5F);
         GlStateManager.rotate(-var22, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
         this.bannerModel.bannerStand.showModel = false;
      }

      BlockPos var23 = var1.getPos();
      float var20 = var23.getX() * 7 + var23.getY() * 9 + var23.getZ() * 13 + (float)var16 + var8;
      this.bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(var20 * (float) Math.PI * 0.02F)) * (float) Math.PI;
      GlStateManager.enableRescaleNormal();
      ResourceLocation var21 = this.func_178463_a(var1);
      if (var21 != null) {
         this.bindTexture(var21);
         GlStateManager.pushMatrix();
         GlStateManager.scale(var18, -var18, -var18);
         this.bannerModel.renderBanner();
         GlStateManager.popMatrix();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   private ResourceLocation func_178463_a(TileEntityBanner var1) {
      String var5 = var1.getPatternResourceLocation();
      if (var5.isEmpty()) {
         return null;
      }

      TileEntityBannerRenderer.TimedBannerTexture var6 = DESIGNS.get(var5);
      if (var6 == null) {
         if (DESIGNS.size() >= 256) {
            long var7 = System.currentTimeMillis();
            Iterator var9 = DESIGNS.keySet().iterator();

            while (var9.hasNext()) {
               String var10 = (String)var9.next();
               TileEntityBannerRenderer.TimedBannerTexture var11 = DESIGNS.get(var10);
               if (var7 - var11.systemTime > 60000L) {
                  Minecraft.getMinecraft().getTextureManager().deleteTexture(var11.bannerTexture);
                  var9.remove();
               }
            }

            if (DESIGNS.size() >= 256) {
               return null;
            }
         }

         List var12 = var1.getPatternList();
         List var8 = var1.getColorList();
         ArrayList var13 = Lists.newArrayList();

         for (Object var15$raw : var12) {
            TileEntityBanner.EnumBannerPattern var15 = (TileEntityBanner.EnumBannerPattern)var15$raw;
            var13.add("textures/entity/banner/" + var15.getPatternName() + ".png");
         }

         var6 = new TileEntityBannerRenderer.TimedBannerTexture();
         var6.bannerTexture = new ResourceLocation(var5);
         Minecraft.getMinecraft().getTextureManager().loadTexture(var6.bannerTexture, new LayeredColorMaskTexture(BANNERTEXTURES, var13, var8));
         DESIGNS.put(var5, var6);
      }

      var6.systemTime = System.currentTimeMillis();
      return var6.bannerTexture;
   }

   public static class TimedBannerTexture {
      public long systemTime;
      public ResourceLocation bannerTexture;

      public TimedBannerTexture() {
      }
   }
}
