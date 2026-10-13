package net.minecraft.client.renderer.entity.layers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.src.CapeImageBuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerCape implements LayerRenderer<AbstractClientPlayer> {
   private final RenderPlayer playerRenderer;
   private static boolean J;
   private static final ResourceLocation K = new ResourceLocation("client/cape.png");
   private static final long a = 137989076499973L;

   public LayerCape(RenderPlayer var1) {
      this.playerRenderer = var1;
   }

   public void doRenderLayer(AbstractClientPlayer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.hasPlayerInfo() && !var1.isInvisible() && var1.isWearing(EnumPlayerModelParts.CAPE) && var1.u() != null) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (!J) {
            InputStream var12 = LayerCape.class.getResourceAsStream("/assets/minecraft/client/cape.png");
            CapeImageBuffer var13 = new CapeImageBuffer(var1, K);

            try {
               BufferedImage var14 = TextureUtil.readBufferedImage(var12);
               BufferedImage var15 = var13.parseUserSkin(var14);
               DynamicTexture var16 = new DynamicTexture(var15);
               Minecraft.getMinecraft().getTextureManager().loadTexture(K, var16);
            } catch (IOException var27) {
               var27.printStackTrace();
            }

            J = true;
         }

         this.playerRenderer.bindTexture(var1.u());
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, 0.125F);
         double var28 = var1.prevChasingPosX + (var1.chasingPosX - var1.prevChasingPosX) * var4 - (var1.prevPosX + (var1.posX - var1.prevPosX) * var4);
         double var29 = var1.prevChasingPosY + (var1.chasingPosY - var1.prevChasingPosY) * var4 - (var1.prevPosY + (var1.posY - var1.prevPosY) * var4);
         double var30 = var1.prevChasingPosZ + (var1.chasingPosZ - var1.prevChasingPosZ) * var4 - (var1.prevPosZ + (var1.posZ - var1.prevPosZ) * var4);
         float var18 = var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * var4;
         double var19 = MathHelper.sin(var18 * (float) Math.PI / 180.0F);
         double var21 = -MathHelper.cos(var18 * (float) Math.PI / 180.0F);
         float var23 = (float)var29 * 10.0F;
         var23 = MathHelper.clamp_float(var23, -6.0F, 32.0F);
         float var24 = (float)(var28 * var19 + var30 * var21) * 100.0F;
         float var25 = (float)(var28 * var21 - var30 * var19) * 100.0F;
         if (var24 < 0.0F) {
            var24 = 0.0F;
         }

         if (var24 > 165.0F) {
            var24 = 165.0F;
         }

         float var26 = var1.prevCameraYaw + (var1.cameraYaw - var1.prevCameraYaw) * var4;
         var23 += MathHelper.sin((var1.prevDistanceWalkedModified + (var1.distanceWalkedModified - var1.prevDistanceWalkedModified) * var4) * 6.0F)
            * 32.0F
            * var26;
         if (var1.isSneaking()) {
            var23 += 25.0F;
            GlStateManager.translate(0.0F, 0.142F, -0.0178F);
         }

         GlStateManager.rotate(6.0F + var24 / 2.0F + var23, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(var25 / 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(-var25 / 2.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         this.playerRenderer.getMainModel().X(0.0625F);
         GlStateManager.popMatrix();
      }
   }

   public static ResourceLocation h(AbstractClientPlayer var0, String var1) {
      ResourceLocation var4 = new ResourceLocation("capes/" + var1);
      TextureManager var5 = Minecraft.getMinecraft().getTextureManager();
      net.minecraft.client.renderer.texture.ITextureObject var6 = var5.getTexture(var4);
      if (var6 == null) {
         CapeImageBuffer var7 = new CapeImageBuffer(var0, var4);
         var6 = new ThreadDownloadImageData(null, String.format("https://capes.astolfo.lgbt/v1/cape/%s", var1), K, var7);
         var5.loadTexture(var4, var6);
         BufferedImage var8 = var7.parseUserSkin(((net.minecraft.client.renderer.ThreadDownloadImageData)var6).l());
         DynamicTexture var9 = new DynamicTexture(var8);
         var5.loadTexture(var4, var9);
      }

      return var4;
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
