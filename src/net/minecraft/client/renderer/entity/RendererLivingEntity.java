package net.minecraft.client.renderer.entity;

import bypass.Astolfo;
import bypass.event.render.RenderLivingEvent;
import bypass.event.render.RenderModelEvent;
import bypass.event.render.RenderNameTagEvent;
import com.google.common.collect.Lists;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public abstract class RendererLivingEntity<T extends EntityLivingBase> extends Render<T> {
   private static final Logger logger = LogManager.getLogger();
   private static final DynamicTexture textureBrightness = new DynamicTexture(16, 16);
   public ModelBase mainModel;
   public FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
   public List<LayerRenderer<T>> layerRenderers = Lists.newArrayList();
   public boolean renderOutlines = false;
   public static float p = 64.0F;
   public static float E = 32.0F;
   private static float f;
   private static final long O = 90147843737786L;

   public RendererLivingEntity(RenderManager var1, ModelBase var2, float var3) {
      super(var1);
      this.mainModel = var2;
      this.shadowSize = var3;
   }

   public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U var1) {
      return this.layerRenderers.add((net.minecraft.client.renderer.entity.layers.LayerRenderer)(var1));
   }

   public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean removeLayer(U var1) {
      return this.layerRenderers.remove(var1);
   }

   public ModelBase getMainModel() {
      return this.mainModel;
   }

   public float interpolateRotation(float var1, float var2, float var3) {
      float var7 = var2 - var1;

      while (var7 < -180.0F) {
         var7 += 360.0F;
      }

      while (var7 >= 180.0F) {
         var7 -= 360.0F;
      }

      return var1 + var3 * var7;
   }

   public void transformHeldFull3DItemLayer() {
   }

   public void doRender(T var1, double var2, double var4, double var6, float var8, float var9) {
      if (!Reflector.RenderLivingEvent_Pre_Constructor.exists()
         || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Pre_Constructor, var1, this, var2, var4, var6)) {
         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         this.mainModel.swingProgress = this.o((T)var1, var9);
         this.mainModel.isRiding = var1.E();
         if (Reflector.ForgeEntity_shouldRiderSit.exists()) {
            this.mainModel.isRiding = var1.E() && var1.ridingEntity != null && Reflector.callBoolean(var1.ridingEntity, Reflector.ForgeEntity_shouldRiderSit);
         }

         this.mainModel.isChild = var1.isChild();

         try {
            RenderLivingEvent var13 = new RenderLivingEvent(var1);
            Astolfo.INSTANCE.getEventBus().post(var13);
            float var14 = this.interpolateRotation(var1.prevRenderYawOffset, var1.renderYawOffset, var9);
            float var15 = this.interpolateRotation(var1.prevRotationYawHead, var1.rotationYawHead, var9);
            float var16 = var15 - var14;
            if (this.mainModel.isRiding && var1.ridingEntity instanceof EntityLivingBase) {
               EntityLivingBase var17 = (EntityLivingBase)var1.ridingEntity;
               var14 = this.interpolateRotation(var1.prevRenderYawOffset, var1.renderYawOffset, var9);
               var16 = var15 - var14;
               float var18 = MathHelper.wrapAngleTo180_float(var16);
               if (var18 < -85.0F) {
                  var18 = -85.0F;
               }

               if (var18 >= 85.0F) {
                  var18 = 85.0F;
               }

               var14 = var15 - var18;
               if (var18 * var18 > 2500.0F) {
                  var14 += var18 * 0.2F;
               }
            }

            float var27 = var13.getPrevPitch() + (var13.getPitch() - var13.getPrevPitch()) * var9;
            this.renderLivingAt((T)var1, var2, var4, var6);
            float var28 = this.handleRotationFloat((T)var1, var9);
            this.rotateCorpse((T)var1, var28, var14, var9);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback((T)var1, var9);
            float var19 = 0.0625F;
            GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
            float var20 = var1.prevLimbSwingAmount + (var1.limbSwingAmount - var1.prevLimbSwingAmount) * var9;
            float var21 = var1.limbSwing - var1.limbSwingAmount * (1.0F - var9);
            if (var1.isChild()) {
               var21 *= 3.0F;
            }

            if (var20 > 1.0F) {
               var20 = 1.0F;
            }

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(var1, var21, var20, var9);
            this.mainModel.setRotationAngles(var21, var20, var28, var16, var27, 0.0625F, var1);
            RenderModelEvent var22 = new RenderModelEvent(this, var1, var21, var20, var28, var16, var27, var14, var19);
            Astolfo.INSTANCE.getEventBus().post(var22);
            if (this.renderOutlines) {
               boolean var29 = this.setScoreTeamColor((T)var1);
               if (!var22.isCancelled()) {
                  this.renderModel((T)var1, var21, var20, var28, var16, var27, 0.0625F);
               }

               if (var29) {
                  this.z();
               }
            } else {
               boolean var23 = this.setDoRenderBrightness((T)var1, var9);
               if (!var22.isCancelled()) {
                  this.renderModel((T)var1, var21, var20, var28, var16, var27, 0.0625F);
               }

               if (var23) {
                  this.unsetBrightness();
               }

               GlStateManager.depthMask(true);
               if (!(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).isSpectator()) {
                  this.renderLayers((T)var1, var21, var20, var9, var28, var16, var27, 0.0625F);
               }
            }

            GlStateManager.disableRescaleNormal();
         } catch (Exception var24) {
            logger.error("Couldn't render entity", var24);
         }

         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.enableCull();
         GlStateManager.popMatrix();
         if (!this.renderOutlines) {
            super.doRender((T)var1, var2, var4, var6, var8, var9);
         }

         if (Reflector.RenderLivingEvent_Post_Constructor.exists()
            && !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Post_Constructor, var1, this, var2, var4, var6)) {
         }
      }

      RenderModelEvent var25 = new RenderModelEvent();
      Astolfo.INSTANCE.getEventBus().post(var25);
   }

   public boolean setScoreTeamColor(T var1) {
      int var5 = 16777215;
      if (var1 instanceof EntityPlayer) {
         ScorePlayerTeam var6 = (ScorePlayerTeam)var1.getTeam();
         if (var6 != null) {
            String var7 = FontRenderer.getFormatFromString(var6.getColorPrefix());
            if (var7.length() >= 2) {
               var5 = this.getFontRendererFromRenderManager().getColorCode(var7.charAt(1));
            }
         }
      }

      float var9 = (var5 >> 16 & 0xFF) / 255.0F;
      float var10 = (var5 >> 8 & 0xFF) / 255.0F;
      float var8 = (var5 & 0xFF) / 255.0F;
      GlStateManager.disableLighting();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.color(var9, var10, var8, 1.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      return true;
   }

   public void z() {
      GlStateManager.enableLighting();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   public void renderModel(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      long var8 = O ^ 77041210463917L;
      boolean var11 = !var1.isInvisible();
      boolean var12 = !var11 && !var1.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
      if (var11 || var12) {
         if (!this.bindEntityTexture((T)var1)) {
            return;
         }

         if (var12) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
            long var10000 = 8349117774095511730L ^ var8;
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         this.mainModel.render(var1, var2, var3, var4, var5, var6, var7);
         if (var12) {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
         }
      }
   }

   public boolean setDoRenderBrightness(T var1, float var2) {
      return this.setBrightness((T)var1, var2, true);
   }

   public boolean setBrightness(T var1, float var2, boolean var3) {
      long var4 = O ^ 60107803437872L;
      float var7 = var1.getBrightness(var2);
      int var8 = this.getColorMultiplier((T)var1, var7, var2);
      boolean var9 = (var8 >> 24 & 0xFF) > 0;
      boolean var10 = var1.hurtTime > 0 || var1.deathTime > 0;
      if (!var9 && !var10) {
         return false;
      }

      if (!var9 && !var3) {
         return false;
      }

      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      long var10000 = 1979229814613904758L ^ var4;
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      var10000 = 1979229814613904758L ^ var4;
      GlStateManager.enableTexture2D();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      ((Buffer)this.brightnessBuffer).position(0);
      if (var10) {
         this.brightnessBuffer.put(1.0F);
         this.brightnessBuffer.put(0.0F);
         this.brightnessBuffer.put(0.0F);
         this.brightnessBuffer.put(0.3F);
         if (Config.isShaders()) {
            Shaders.setEntityColor(1.0F, 0.0F, 0.0F, 0.3F);
         }
      } else {
         float var11 = (var8 >> 24 & 0xFF) / 255.0F;
         float var12 = (var8 >> 16 & 0xFF) / 255.0F;
         float var13 = (var8 >> 8 & 0xFF) / 255.0F;
         float var14 = (var8 & 0xFF) / 255.0F;
         this.brightnessBuffer.put(var12);
         this.brightnessBuffer.put(var13);
         this.brightnessBuffer.put(var14);
         this.brightnessBuffer.put(1.0F - var11);
         if (Config.isShaders()) {
            Shaders.setEntityColor(var12, var13, var14, 1.0F - var11);
         }
      }

      ((Buffer)this.brightnessBuffer).flip();
      GL11.glTexEnv(8960, 8705, this.brightnessBuffer);
      GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
      var10000 = 1979229814613904758L ^ var4;
      GlStateManager.enableTexture2D();
      GlStateManager.bindTexture(textureBrightness.getGlTextureId());
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      return true;
   }

   public void unsetBrightness() {
      long var1 = O ^ 67727928361702L;
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
      long var10000 = 1979235504686998688L ^ var1;
      GlStateManager.disableTexture2D();
      GlStateManager.bindTexture(0);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      if (Config.isShaders()) {
         Shaders.setEntityColor(0.0F, 0.0F, 0.0F, 0.0F);
      }
   }

   public void renderLivingAt(T var1, double var2, double var4, double var6) {
      GlStateManager.translate((float)var2, (float)var4, (float)var6);
   }

   public void rotateCorpse(T var1, float var2, float var3, float var4) {
      GlStateManager.rotate(180.0F - var3, 0.0F, 1.0F, 0.0F);
      if (var1.deathTime > 0) {
         float var8 = (var1.deathTime + var4 - 1.0F) / 20.0F * 1.6F;
         var8 = MathHelper.sqrt_float(var8);
         if (var8 > 1.0F) {
            var8 = 1.0F;
         }

         GlStateManager.rotate(var8 * this.getDeathMaxRotation((T)var1), 0.0F, 0.0F, 1.0F);
      } else {
         String var10 = EnumChatFormatting.getTextWithoutFormattingCodes(var1.getName());
         if (var10 != null
            && (var10.equals("Dinnerbone") || var10.equals("Grumm"))
            && (!(var1 instanceof EntityPlayer) || ((EntityPlayer)var1).isWearing(EnumPlayerModelParts.CAPE))) {
            GlStateManager.translate(0.0F, var1.height + 0.1F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }
   }

   public float o(T var1, float var2) {
      return var1.getSwingProgress(var2);
   }

   public float handleRotationFloat(T var1, float var2) {
      return var1.ticksExisted + var2;
   }

   public void renderLayers(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      for (LayerRenderer var13 : this.layerRenderers) {
         boolean var14 = this.setBrightness((T)var1, var4, var13.shouldCombineTextures());
         var13.doRenderLayer(var1, var2, var3, var4, var5, var6, var7, var8);
         if (var14) {
            this.unsetBrightness();
         }
      }
   }

   public float getDeathMaxRotation(T var1) {
      return 90.0F;
   }

   public int getColorMultiplier(T var1, float var2, float var3) {
      return 0;
   }

   public void preRenderCallback(T var1, float var2) {
   }

   public void renderName(T var1, double var2, double var4, double var6) {
      long var8 = O ^ 80966086978154L;
      if (!Reflector.RenderLivingEvent_Specials_Pre_Constructor.exists()
         || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Specials_Pre_Constructor, var1, this, var2, var4, var6)) {
         if (this.canRenderName((T)var1)) {
            double var11 = var1.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float var13 = var1.isSneaking() ? E : p;
            if (var11 < var13 * var13) {
               String var14 = var1.getDisplayName().getFormattedText();
               long var10000 = 8406457471310327947L ^ var8;
               GlStateManager.alphaFunc(516, 0.1F);
               RenderNameTagEvent var16 = new RenderNameTagEvent();
               Astolfo.INSTANCE.getEventBus().post(var16);
               if (var16.isCancelled() && !(var1 instanceof EntityArmorStand)) {
                  return;
               }

               if (var1.isSneaking()) {
                  FontRenderer var17 = this.getFontRendererFromRenderManager();
                  GlStateManager.pushMatrix();
                  GlStateManager.translate((float)var2, (float)var4 + var1.height + 0.5F - (var1.isChild() ? var1.height / 2.0F : 0.0F), (float)var6);
                  GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                  var10000 = 641125262909184929L ^ var8;
                  GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                  GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                  GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                  GlStateManager.translate(0.0F, 9.374999F, 0.0F);
                  GlStateManager.disableLighting();
                  GlStateManager.depthMask(false);
                  GlStateManager.enableBlend();
                  GlStateManager.disableTexture2D();
                  GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                  int var18 = var17.getStringWidth(var14) / 2;
                  Tessellator var19 = Tessellator.getInstance();
                  WorldRenderer var20 = var19.getWorldRenderer();
                  var20.begin(7, DefaultVertexFormats.POSITION_COLOR);
                  var20.pos(-var18 - 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  var20.pos(-var18 - 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  var20.pos(var18 + 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  var20.pos(var18 + 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  var19.draw();
                  GlStateManager.enableTexture2D();
                  GlStateManager.depthMask(true);
                  var17.D(var14, -var17.getStringWidth(var14) / 2, 0.0F, 553648127);
                  GlStateManager.enableLighting();
                  GlStateManager.disableBlend();
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  GlStateManager.popMatrix();
               } else {
                  this.renderOffsetLivingLabel((T)var1, var2, var4 - (var1.isChild() ? var1.height / 2.0F : 0.0), var6, var14, 0.02666667F, var11);
               }
            }
         }

         if (Reflector.RenderLivingEvent_Specials_Post_Constructor.exists()
            && !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Specials_Post_Constructor, var1, this, var2, var4, var6)) {
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean canRenderName(T var1) {
      EntityPlayerSP var5 = Minecraft.getMinecraft().thePlayer;
      if (var1 instanceof EntityPlayer && var1 != var5) {
         Team var6 = var1.getTeam();
         Team var7 = var5.getTeam();
         if (var6 != null) {
            Team.EnumVisible var8 = var6.getNameTagVisibility();
            switch (var8) {
               case ALWAYS:
                  return true;
               case NEVER:
                  return false;
               case HIDE_FOR_OTHER_TEAMS:
                  return var7 == null || var6.isSameTeam(var7);
               case HIDE_FOR_OWN_TEAM:
                  return var7 == null || !var6.isSameTeam(var7);
               default:
                  return true;
            }
         }
      }

      return Minecraft.isGuiEnabled() && var1 != this.renderManager.livingPlayer && !var1.isInvisibleToPlayer(var5) && var1.riddenByEntity == null;
   }

   public void setRenderOutlines(boolean var1) {
      this.renderOutlines = var1;
   }

   static {
      int[] var22 = textureBrightness.getTextureData();

      for (int var23 = 0; var23 < 256; var23++) {
         var22[var23] = -1;
      }

      textureBrightness.updateDynamicTexture();
   }
}
