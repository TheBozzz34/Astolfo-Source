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

   public void renderTileEntityAt(TileEntityBanner te, double x, double y, double z, float partialTicks, int destroyStage) {
      boolean bl = te.getWorld() != null;
      boolean bl2 = !bl || te.getBlockType() == Blocks.standing_banner;
      int i = bl ? te.getBlockMetadata() : 0;
      long l = bl ? te.getWorld().getTotalWorldTime() : 0L;
      GlStateManager.pushMatrix();
      float f = 0.6666667F;
      if (bl2) {
         GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * f, (float)z + 0.5F);
         float g = i * 360 / 16.0F;
         GlStateManager.rotate(-g, 0.0F, 1.0F, 0.0F);
         this.bannerModel.bannerStand.showModel = true;
      } else {
         float h = 0.0F;
         if (i == 2) {
            h = 180.0F;
         }

         if (i == 4) {
            h = 90.0F;
         }

         if (i == 5) {
            h = -90.0F;
         }

         GlStateManager.translate((float)x + 0.5F, (float)y - 0.25F * f, (float)z + 0.5F);
         GlStateManager.rotate(-h, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
         this.bannerModel.bannerStand.showModel = false;
      }

      BlockPos blockPos = te.getPos();
      float f2 = blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13 + (float)l + partialTicks;
      this.bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f2 * (float) Math.PI * 0.02F)) * (float) Math.PI;
      GlStateManager.enableRescaleNormal();
      ResourceLocation resourceLocation = this.func_178463_a(te);
      if (resourceLocation != null) {
         this.bindTexture(resourceLocation);
         GlStateManager.pushMatrix();
         GlStateManager.scale(f, -f, -f);
         this.bannerModel.renderBanner();
         GlStateManager.popMatrix();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   private ResourceLocation func_178463_a(TileEntityBanner bannerObj) {
      String string = bannerObj.getPatternResourceLocation();
      if (string.isEmpty()) {
         return null;
      }

      TileEntityBannerRenderer.TimedBannerTexture timedBannerTexture = DESIGNS.get(string);
      if (timedBannerTexture == null) {
         if (DESIGNS.size() >= 256) {
            long l = System.currentTimeMillis();
            Iterator iterator2 = DESIGNS.keySet().iterator();

            while (iterator2.hasNext()) {
               String string2 = (String)iterator2.next();
               TileEntityBannerRenderer.TimedBannerTexture timedBannerTexture2 = DESIGNS.get(string2);
               if (l - timedBannerTexture2.systemTime > 60000L) {
                  Minecraft.getMinecraft().getTextureManager().deleteTexture(timedBannerTexture2.bannerTexture);
                  iterator2.remove();
               }
            }

            if (DESIGNS.size() >= 256) {
               return null;
            }
         }

         List list = bannerObj.getPatternList();
         List list2 = bannerObj.getColorList();
         ArrayList arrayList = Lists.newArrayList();

         for (Object object : list) {
            TileEntityBanner.EnumBannerPattern enumBannerPattern = (TileEntityBanner.EnumBannerPattern)object;
            arrayList.add("textures/entity/banner/" + enumBannerPattern.getPatternName() + ".png");
         }

         timedBannerTexture = new TileEntityBannerRenderer.TimedBannerTexture();
         timedBannerTexture.bannerTexture = new ResourceLocation(string);
         Minecraft.getMinecraft().getTextureManager().loadTexture(timedBannerTexture.bannerTexture, new LayeredColorMaskTexture(BANNERTEXTURES, arrayList, list2));
         DESIGNS.put(string, timedBannerTexture);
      }

      timedBannerTexture.systemTime = System.currentTimeMillis();
      return timedBannerTexture.bannerTexture;
   }

   public static class TimedBannerTexture {
      public long systemTime;
      public ResourceLocation bannerTexture;

      public TimedBannerTexture() {
      }
   }
}
