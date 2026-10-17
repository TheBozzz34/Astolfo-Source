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

   public LayerCape(RenderPlayer playerRendererIn) {
      this.playerRenderer = playerRendererIn;
   }

   public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      if (entitylivingbaseIn.hasPlayerInfo()
         && !entitylivingbaseIn.isInvisible()
         && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE)
         && entitylivingbaseIn.getLocationCape() != null) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (!J) {
            InputStream inputStream = LayerCape.class.getResourceAsStream("/assets/minecraft/client/cape.png");
            CapeImageBuffer capeImageBuffer = new CapeImageBuffer(entitylivingbaseIn, K);

            try {
               BufferedImage bufferedImage = TextureUtil.readBufferedImage(inputStream);
               BufferedImage bufferedImage2 = capeImageBuffer.parseUserSkin(bufferedImage);
               DynamicTexture dynamicTexture = new DynamicTexture(bufferedImage2);
               Minecraft.getMinecraft().getTextureManager().loadTexture(K, dynamicTexture);
            } catch (IOException ioException) {
               ioException.printStackTrace();
            }

            J = true;
         }

         this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, 0.125F);
         double d = entitylivingbaseIn.prevChasingPosX
            + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * partialTicks
            - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * partialTicks);
         double e = entitylivingbaseIn.prevChasingPosY
            + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * partialTicks
            - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * partialTicks);
         double d2 = entitylivingbaseIn.prevChasingPosZ
            + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * partialTicks
            - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * partialTicks);
         float f4 = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
         double d3 = MathHelper.sin(f4 * (float) Math.PI / 180.0F);
         double d4 = -MathHelper.cos(f4 * (float) Math.PI / 180.0F);
         float f5 = (float)e * 10.0F;
         f5 = MathHelper.clamp_float(f5, -6.0F, 32.0F);
         float f6 = (float)(d * d3 + d2 * d4) * 100.0F;
         float f7 = (float)(d * d4 - d2 * d3) * 100.0F;
         if (f6 < 0.0F) {
            f6 = 0.0F;
         }

         if (f6 > 165.0F) {
            f6 = 165.0F;
         }

         float f8 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
         f5 += MathHelper.sin(
               (
                     entitylivingbaseIn.prevDistanceWalkedModified
                        + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks
                  )
                  * 6.0F
            )
            * 32.0F
            * f8;
         if (entitylivingbaseIn.isSneaking()) {
            f5 += 25.0F;
            GlStateManager.translate(0.0F, 0.142F, -0.0178F);
         }

         GlStateManager.rotate(6.0F + f6 / 2.0F + f5, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(f7 / 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(-f7 / 2.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         this.playerRenderer.getMainModel().renderCape(0.0625F);
         GlStateManager.popMatrix();
      }
   }

   public static ResourceLocation h(AbstractClientPlayer abstractClientPlayer, String string) {
      ResourceLocation resourceLocation = new ResourceLocation("capes/" + string);
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      net.minecraft.client.renderer.texture.ITextureObject iTextureObject = textureManager.getTexture(resourceLocation);
      if (iTextureObject == null) {
         CapeImageBuffer capeImageBuffer = new CapeImageBuffer(abstractClientPlayer, resourceLocation);
         iTextureObject = new ThreadDownloadImageData(null, String.format("https://capes.astolfo.lgbt/v1/cape/%s", string), K, capeImageBuffer);
         textureManager.loadTexture(resourceLocation, iTextureObject);
         BufferedImage bufferedImage = capeImageBuffer.parseUserSkin(((net.minecraft.client.renderer.ThreadDownloadImageData)iTextureObject).l());
         DynamicTexture dynamicTexture = new DynamicTexture(bufferedImage);
         textureManager.loadTexture(resourceLocation, dynamicTexture);
      }

      return resourceLocation;
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
