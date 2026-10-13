package shadersmod.client;

import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.src.Reflector;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ShadersRender {
   private static final long a = 29902270934658L;

   public static void setFrustrumPosition(Frustum var0, double var1, double var3, double var5) {
      var0.setPosition(var1, var3, var5);
   }

   public static void setupTerrain(RenderGlobal var0, Entity var1, double var2, ICamera var4, int var5, boolean var6) {
      var0.setupTerrain(var1, var2, var4, var5, var6);
   }

   public static void o() {
      if (Shaders.isRenderingWorld) {
         Shaders.fogEnabled = true;
         Shaders.useProgram(7);
      }
   }

   public static void z() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(7);
      }
   }

   public static void I() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(7);
      }
   }

   public static void P() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(3);
      }
   }

   public static void beginTranslucent() {
      long var0 = a ^ 38347340608345L;
      if (Shaders.isRenderingWorld) {
         if (Shaders.ew >= 2) {
            GlStateManager.setActiveTexture(33995);
            Shaders.checkGLError("pre copy depth");
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Shaders.renderWidth, Shaders.renderHeight);
            long var10000 = 6245932639845834108L ^ var0;
            Shaders.checkGLError("copy depth");
            GlStateManager.setActiveTexture(33984);
         }

         Shaders.useProgram(12);
      }
   }

   public static void r() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(3);
      }
   }

   public static void B(EntityRenderer var0, float var1, int var2) {
      if (!Shaders.isShadowPass) {
         boolean var6 = Shaders.B();
         if (!var6) {
            Shaders.readCenterDepth();
            Shaders.t();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            var0.renderHand(var1, var2, true, false, false);
            Shaders.endHand();
            Shaders.i(true);
         }
      }
   }

   public static void l(EntityRenderer var0, float var1, int var2) {
      if (!Shaders.isShadowPass && !Shaders.e_boolean()) {
         Shaders.readCenterDepth();
         GlStateManager.enableBlend();
         Shaders.t();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         var0.renderHand(var1, var2, true, false, true);
         Shaders.endHand();
         Shaders.i(true);
      }
   }

   public static void renderItemFP(ItemRenderer var0, float var1, boolean var2) {
      GlStateManager.depthMask(true);
      if (var2) {
         GlStateManager.depthFunc(519);
         GL11.glPushMatrix();
         IntBuffer var6 = Shaders.activeDrawBuffers;
         Shaders.setDrawBuffers(Shaders.drawBuffersNone);
         Shaders.renderItemKeepDepthMask = true;
         var0.renderItemInFirstPerson(var1);
         Shaders.renderItemKeepDepthMask = false;
         Shaders.setDrawBuffers(var6);
         GL11.glPopMatrix();
      }

      GlStateManager.depthFunc(515);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      var0.renderItemInFirstPerson(var1);
   }

   public static void t(EntityRenderer var0, float var1, int var2) {
      if (!Shaders.isShadowPass) {
         Shaders.b();
         var0.renderHand(var1, var2, false, true, false);
         Shaders.D();
      }
   }

   public static void beginBlockDamage() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(11);
         if (Shaders.programsID[11] == Shaders.programsID[7]) {
            Shaders.setDrawBuffers(Shaders.drawBuffersColorAtt0);
            GlStateManager.depthMask(false);
         }
      }
   }

   public static void G() {
      if (Shaders.isRenderingWorld) {
         GlStateManager.depthMask(true);
         Shaders.useProgram(3);
      }
   }

   public static void renderShadowMap(EntityRenderer var0, int var1, float var2, long var3) {
      long var5 = a ^ 60762455785721L;
      if (Shaders.OF > 0 && --Shaders.uU <= 0) {
         Minecraft var8 = Minecraft.getMinecraft();
         var8.mcProfiler.endStartSection("shadow pass");
         RenderGlobal var9 = var8.renderGlobal;
         Shaders.isShadowPass = true;
         Shaders.uU = Shaders.OO;
         long var10000 = 4957220021731333637L ^ var5;
         Shaders.er = var8.gameSettings.thirdPersonView;
         var8.gameSettings.thirdPersonView = 1;
         Shaders.checkGLError("pre shadow");
         var10000 = 713584400688289600L ^ var5;
         GL11.glMatrixMode(5889);
         GL11.glPushMatrix();
         var10000 = 2609992289330517189L ^ var5;
         GL11.glMatrixMode(5888);
         GL11.glPushMatrix();
         var8.mcProfiler.endStartSection("shadow clear");
         EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.n);
         var10000 = 3032225369751746146L ^ var5;
         Shaders.checkGLError("shadow bind sfb");
         Shaders.useProgram(30);
         var8.mcProfiler.endStartSection("shadow camera");
         var0.setupCameraTransform(var2, 2);
         Shaders.setCameraShadow(var2);
         ActiveRenderInfo.updateRenderInfo(var8.thePlayer, var8.gameSettings.thirdPersonView == 2);
         Shaders.checkGLError("shadow camera");
         var10000 = 3593416244862018056L ^ var5;
         GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
         long var10001 = 5142470906953957167L ^ var5;
         Shaders.checkGLError("shadow drawbuffers");
         GL11.glReadBuffer(0);
         var10001 = 3874332234316794071L ^ var5;
         Shaders.checkGLError("shadow readbuffer");
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.sfbDepthTextures.get(0), 0);
         if (Shaders.uQ != 0) {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, Shaders.sfbColorTextures.get(0), 0);
         }

         Shaders.checkFramebufferStatus("shadow fb");
         GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glClear(Shaders.uQ != 0 ? 16640 : 256);
         Shaders.checkGLError("shadow clear");
         var10000 = 149632112055759080L ^ var5;
         var8.mcProfiler.endStartSection("shadow frustum");
         ClippingHelper var10 = ClippingHelperShadow.getInstance();
         var8.mcProfiler.endStartSection("shadow culling");
         Frustum var11 = new Frustum(var10);
         Entity var12 = var8.getRenderViewEntity();
         double var13 = var12.lastTickPosX + (var12.posX - var12.lastTickPosX) * var2;
         double var15 = var12.lastTickPosY + (var12.posY - var12.lastTickPosY) * var2;
         double var17 = var12.lastTickPosZ + (var12.posZ - var12.lastTickPosZ) * var2;
         var11.setPosition(var13, var15, var17);
         GlStateManager.shadeModel(7425);
         GlStateManager.enableDepth();
         var10000 = 1432206434286879248L ^ var5;
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         GlStateManager.colorMask(true, true, true, true);
         GlStateManager.disableCull();
         var8.mcProfiler.endStartSection("shadow prepareterrain");
         var8.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         var8.mcProfiler.endStartSection("shadow setupterrain");
         int var19 = 0;
         var19 = var0.R++;
         var9.setupTerrain(var12, var2, var11, var19, var8.thePlayer.isSpectator());
         var8.mcProfiler.endStartSection("shadow updatechunks");
         var8.mcProfiler.endStartSection("shadow terrain");
         GlStateManager.matrixMode(5888);
         var10000 = 183324432622635883L ^ var5;
         GlStateManager.pushMatrix();
         GlStateManager.disableAlpha();
         var9.renderBlockLayer(EnumWorldBlockLayer.SOLID, var2, 2, var12);
         Shaders.checkGLError("shadow terrain solid");
         GlStateManager.enableAlpha();
         var10000 = 8500757531783953346L ^ var5;
         var9.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, var2, 2, var12);
         Shaders.checkGLError("shadow terrain cutoutmipped");
         var10000 = 8811481516282464480L ^ var5;
         var8.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
         var10001 = 7899367782786995254L ^ var5;
         var9.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, var2, 2, var12);
         Shaders.checkGLError("shadow terrain cutout");
         var8.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
         GlStateManager.shadeModel(7424);
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         var8.mcProfiler.endStartSection("shadow entities");
         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 0);
         }

         var9.renderEntities(var12, var11, var2);
         Shaders.checkGLError("shadow entities");
         var10000 = 6505990617124593230L ^ var5;
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         var10000 = 1194866909457584513L ^ var5;
         GlStateManager.depthMask(true);
         GlStateManager.disableBlend();
         GlStateManager.enableCull();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.alphaFunc(516, 0.1F);
         if (Shaders.OF >= 2) {
            GlStateManager.setActiveTexture(33989);
            Shaders.checkGLError("pre copy shadow depth");
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Shaders.Ow, Shaders.e5);
            var10000 = 3922292682356405331L ^ var5;
            Shaders.checkGLError("copy shadow depth");
            GlStateManager.setActiveTexture(33984);
         }

         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         var10000 = 1135697088738864897L ^ var5;
         var8.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         GlStateManager.shadeModel(7425);
         Shaders.checkGLError("shadow pre-translucent");
         GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
         var10000 = 5977225982888854849L ^ var5;
         Shaders.checkGLError("shadow drawbuffers pre-translucent");
         Shaders.checkFramebufferStatus("shadow pre-translucent");
         if (Shaders.isRenderShadowTranslucent()) {
            var8.mcProfiler.endStartSection("shadow translucent");
            var9.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, var2, 2, var12);
            var10000 = 7735530390833142956L ^ var5;
            Shaders.checkGLError("shadow translucent");
         }

         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            RenderHelper.enableStandardItemLighting();
            Reflector.call(Reflector.ForgeHooksClient_setRenderPass, 1);
            var10000 = 5426547092510258830L ^ var5;
            var9.renderEntities(var12, var11, var2);
            Reflector.call(Reflector.ForgeHooksClient_setRenderPass, -1);
            RenderHelper.disableStandardItemLighting();
            Shaders.checkGLError("shadow entities 1");
         }

         GlStateManager.shadeModel(7424);
         GlStateManager.depthMask(true);
         var10000 = 5597102099851875792L ^ var5;
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GL11.glFlush();
         Shaders.checkGLError("shadow flush");
         Shaders.isShadowPass = false;
         var8.gameSettings.thirdPersonView = Shaders.er;
         var8.mcProfiler.endStartSection("shadow postprocess");
         if (Shaders.hasGlGenMipmap) {
            if (Shaders.OF >= 1) {
               if (Shaders.shadowMipmapEnabled[0]) {
                  GlStateManager.setActiveTexture(33988);
                  GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(0));
                  var10000 = 8673419638326628698L ^ var5;
                  GL30.glGenerateMipmap(3553);
                  GL11.glTexParameteri(3553, 10241, Shaders.shadowFilterNearest[0] ? 9984 : 9987);
               }

               if (Shaders.OF >= 2 && Shaders.shadowMipmapEnabled[1]) {
                  GlStateManager.setActiveTexture(33989);
                  GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(1));
                  var10000 = 8673419638326628698L ^ var5;
                  GL30.glGenerateMipmap(3553);
                  GL11.glTexParameteri(3553, 10241, Shaders.shadowFilterNearest[1] ? 9984 : 9987);
               }

               GlStateManager.setActiveTexture(33984);
            }

            if (Shaders.uQ >= 1) {
               if (Shaders.shadowColorMipmapEnabled[0]) {
                  GlStateManager.setActiveTexture(33997);
                  GlStateManager.bindTexture(Shaders.sfbColorTextures.get(0));
                  var10000 = 8673419638326628698L ^ var5;
                  GL30.glGenerateMipmap(3553);
                  GL11.glTexParameteri(3553, 10241, Shaders.shadowColorFilterNearest[0] ? 9984 : 9987);
               }

               if (Shaders.uQ >= 2 && Shaders.shadowColorMipmapEnabled[1]) {
                  GlStateManager.setActiveTexture(33998);
                  GlStateManager.bindTexture(Shaders.sfbColorTextures.get(1));
                  var10000 = 8673419638326628698L ^ var5;
                  GL30.glGenerateMipmap(3553);
                  GL11.glTexParameteri(3553, 10241, Shaders.shadowColorFilterNearest[1] ? 9984 : 9987);
               }

               GlStateManager.setActiveTexture(33984);
            }
         }

         Shaders.checkGLError("shadow postprocess");
         EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.OU);
         GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
         Shaders.activeDrawBuffers = null;
         var10000 = 2427063737592814041L ^ var5;
         var8.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         Shaders.useProgram(7);
         GL11.glMatrixMode(5888);
         GL11.glPopMatrix();
         GL11.glMatrixMode(5889);
         GL11.glPopMatrix();
         GL11.glMatrixMode(5888);
         Shaders.checkGLError("shadow end");
      }
   }

   public static void preRenderChunkLayer(EnumWorldBlockLayer var0) {
      if (Shaders.isRenderBackFace(var0)) {
         GlStateManager.disableCull();
      }

      if (OpenGlHelper.useVbo()) {
         GL11.glEnableClientState(32885);
         GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
         GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
         GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
      }
   }

   public static void postRenderChunkLayer(EnumWorldBlockLayer var0) {
      if (OpenGlHelper.useVbo()) {
         GL11.glDisableClientState(32885);
         GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
         GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
         GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
      }

      if (Shaders.isRenderBackFace(var0)) {
         GlStateManager.enableCull();
      }
   }

   public static void setupArrayPointersVbo() {
      GL11.glVertexPointer(3, 5126, 56, 0L);
      GL11.glColorPointer(4, 5121, 56, 12L);
      GL11.glTexCoordPointer(2, 5126, 56, 16L);
      OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glTexCoordPointer(2, 5122, 56, 24L);
      OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
      GL11.glNormalPointer(5120, 56, 28L);
      GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, 5126, false, 56, 32L);
      GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, 5122, false, 56, 40L);
      GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, 5122, false, 56, 48L);
   }

   public static void beaconBeamBegin() {
      Shaders.useProgram(14);
   }

   public static void t() {
   }

   public static void b() {
   }

   public static void j() {
   }

   public static void S() {
      GlStateManager.disableBlend();
   }

   public static void renderEnchantedGlintBegin() {
      Shaders.useProgram(17);
   }

   public static void renderEnchantedGlintEnd() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(16);
      } else {
         Shaders.useProgram(0);
      }
   }
}
