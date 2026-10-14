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

   public RendererLivingEntity(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn);
      this.mainModel = modelBaseIn;
      this.shadowSize = shadowSizeIn;
   }

   public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U layer) {
      return this.layerRenderers.add((net.minecraft.client.renderer.entity.layers.LayerRenderer)(layer));
   }

   public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean removeLayer(U layer) {
      return this.layerRenderers.remove(layer);
   }

   public ModelBase getMainModel() {
      return this.mainModel;
   }

   public float interpolateRotation(float par1, float par2, float par3) {
      float f = par2 - par1;

      while (f < -180.0F) {
         f += 360.0F;
      }

      while (f >= 180.0F) {
         f -= 360.0F;
      }

      return par1 + par3 * f;
   }

   public void transformHeldFull3DItemLayer() {
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!Reflector.RenderLivingEvent_Pre_Constructor.exists()
         || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Pre_Constructor, entity, this, x, y, z)) {
         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         this.mainModel.swingProgress = this.o((T)entity, partialTicks);
         this.mainModel.isRiding = entity.E();
         if (Reflector.ForgeEntity_shouldRiderSit.exists()) {
            this.mainModel.isRiding = entity.E()
               && entity.ridingEntity != null
               && Reflector.callBoolean(entity.ridingEntity, Reflector.ForgeEntity_shouldRiderSit);
         }

         this.mainModel.isChild = entity.isChild();

         try {
            RenderLivingEvent renderLivingEvent = new RenderLivingEvent(entity);
            Astolfo.INSTANCE.getEventBus().post(renderLivingEvent);
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float g = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float h = g - f;
            if (this.mainModel.isRiding && entity.ridingEntity instanceof EntityLivingBase) {
               EntityLivingBase entityLivingBase = (EntityLivingBase)entity.ridingEntity;
               f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
               h = g - f;
               float f2 = MathHelper.wrapAngleTo180_float(h);
               if (f2 < -85.0F) {
                  f2 = -85.0F;
               }

               if (f2 >= 85.0F) {
                  f2 = 85.0F;
               }

               f = g - f2;
               if (f2 * f2 > 2500.0F) {
                  f += f2 * 0.2F;
               }
            }

            float f3 = renderLivingEvent.getPrevPitch() + (renderLivingEvent.getPitch() - renderLivingEvent.getPrevPitch()) * partialTicks;
            this.renderLivingAt((T)entity, x, y, z);
            float f4 = this.handleRotationFloat((T)entity, partialTicks);
            this.rotateCorpse((T)entity, f4, f, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback((T)entity, partialTicks);
            float f5 = 0.0625F;
            GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
            float f6 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float f7 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
            if (entity.isChild()) {
               f7 *= 3.0F;
            }

            if (f6 > 1.0F) {
               f6 = 1.0F;
            }

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f7, f6, partialTicks);
            this.mainModel.setRotationAngles(f7, f6, f4, h, f3, 0.0625F, entity);
            RenderModelEvent renderModelEvent = new RenderModelEvent(this, entity, f7, f6, f4, h, f3, f, f5);
            Astolfo.INSTANCE.getEventBus().post(renderModelEvent);
            if (this.renderOutlines) {
               boolean bl = this.setScoreTeamColor((T)entity);
               if (!renderModelEvent.isCancelled()) {
                  this.renderModel((T)entity, f7, f6, f4, h, f3, 0.0625F);
               }

               if (bl) {
                  this.z();
               }
            } else {
               boolean bl2 = this.setDoRenderBrightness((T)entity, partialTicks);
               if (!renderModelEvent.isCancelled()) {
                  this.renderModel((T)entity, f7, f6, f4, h, f3, 0.0625F);
               }

               if (bl2) {
                  this.unsetBrightness();
               }

               GlStateManager.depthMask(true);
               if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
                  this.renderLayers((T)entity, f7, f6, partialTicks, f4, h, f3, 0.0625F);
               }
            }

            GlStateManager.disableRescaleNormal();
         } catch (Exception exception) {
            logger.error("Couldn't render entity", exception);
         }

         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.enableCull();
         GlStateManager.popMatrix();
         if (!this.renderOutlines) {
            super.doRender((T)entity, x, y, z, entityYaw, partialTicks);
         }

         if (Reflector.RenderLivingEvent_Post_Constructor.exists()
            && !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Post_Constructor, entity, this, x, y, z)) {
         }
      }

      RenderModelEvent renderModelEvent2 = new RenderModelEvent();
      Astolfo.INSTANCE.getEventBus().post(renderModelEvent2);
   }

   public boolean setScoreTeamColor(T entityLivingBaseIn) {
      int i = 16777215;
      if (entityLivingBaseIn instanceof EntityPlayer) {
         ScorePlayerTeam scorePlayerTeam = (ScorePlayerTeam)entityLivingBaseIn.getTeam();
         if (scorePlayerTeam != null) {
            String string = FontRenderer.getFormatFromString(scorePlayerTeam.getColorPrefix());
            if (string.length() >= 2) {
               i = this.getFontRendererFromRenderManager().getColorCode(string.charAt(1));
            }
         }
      }

      float f = (i >> 16 & 0xFF) / 255.0F;
      float g = (i >> 8 & 0xFF) / 255.0F;
      float h = (i & 0xFF) / 255.0F;
      GlStateManager.disableLighting();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.color(f, g, h, 1.0F);
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

   public void renderModel(T entitylivingbaseIn, float g, float h, float f2, float f3, float f4, float scaleFactor) {
      long l = O ^ 77041210463917L;
      boolean bl = !entitylivingbaseIn.isInvisible();
      boolean bl2 = !bl && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
      if (bl || bl2) {
         if (!this.bindEntityTexture((T)entitylivingbaseIn)) {
            return;
         }

         if (bl2) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
            long m = 8349117774095511730L ^ l;
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         this.mainModel.render(entitylivingbaseIn, g, h, f2, f3, f4, scaleFactor);
         if (bl2) {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
         }
      }
   }

   public boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks) {
      return this.setBrightness((T)entityLivingBaseIn, partialTicks, true);
   }

   public boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures) {
      long l = O ^ 60107803437872L;
      float f = entitylivingbaseIn.getBrightness(partialTicks);
      int i = this.getColorMultiplier((T)entitylivingbaseIn, f, partialTicks);
      boolean bl = (i >> 24 & 0xFF) > 0;
      boolean bl2 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
      if (!bl && !bl2) {
         return false;
      }

      if (!bl && !combineTextures) {
         return false;
      }

      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      long m = 1979229814613904758L ^ l;
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
      m = 1979229814613904758L ^ l;
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
      if (bl2) {
         this.brightnessBuffer.put(1.0F);
         this.brightnessBuffer.put(0.0F);
         this.brightnessBuffer.put(0.0F);
         this.brightnessBuffer.put(0.3F);
         if (Config.isShaders()) {
            Shaders.setEntityColor(1.0F, 0.0F, 0.0F, 0.3F);
         }
      } else {
         float g = (i >> 24 & 0xFF) / 255.0F;
         float h = (i >> 16 & 0xFF) / 255.0F;
         float f2 = (i >> 8 & 0xFF) / 255.0F;
         float f3 = (i & 0xFF) / 255.0F;
         this.brightnessBuffer.put(h);
         this.brightnessBuffer.put(f2);
         this.brightnessBuffer.put(f3);
         this.brightnessBuffer.put(1.0F - g);
         if (Config.isShaders()) {
            Shaders.setEntityColor(h, f2, f3, 1.0F - g);
         }
      }

      ((Buffer)this.brightnessBuffer).flip();
      GL11.glTexEnv(8960, 8705, this.brightnessBuffer);
      GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
      m = 1979229814613904758L ^ l;
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
      long l = O ^ 67727928361702L;
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
      long m = 1979235504686998688L ^ l;
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

   public void renderLivingAt(T entityLivingBaseIn, double x, double y, double z) {
      GlStateManager.translate((float)x, (float)y, (float)z);
   }

   public void rotateCorpse(T bat, float g, float h, float partialTicks) {
      GlStateManager.rotate(180.0F - h, 0.0F, 1.0F, 0.0F);
      if (bat.deathTime > 0) {
         float f = (bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt_float(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         GlStateManager.rotate(f * this.getDeathMaxRotation((T)bat), 0.0F, 0.0F, 1.0F);
      } else {
         String string = EnumChatFormatting.getTextWithoutFormattingCodes(bat.getName());
         if (string != null
            && (string.equals("Dinnerbone") || string.equals("Grumm"))
            && (!(bat instanceof EntityPlayer) || ((EntityPlayer)bat).isWearing(EnumPlayerModelParts.CAPE))) {
            GlStateManager.translate(0.0F, bat.height + 0.1F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }
   }

   public float o(T entityLivingBase, float g) {
      return entityLivingBase.getSwingProgress(g);
   }

   public float handleRotationFloat(T livingBase, float partialTicks) {
      return livingBase.ticksExisted + partialTicks;
   }

   public void renderLayers(T entitylivingbaseIn, float g, float h, float partialTicks, float f2, float f3, float f4, float f5) {
      for (LayerRenderer layerRenderer : this.layerRenderers) {
         boolean bl = this.setBrightness((T)entitylivingbaseIn, partialTicks, layerRenderer.shouldCombineTextures());
         layerRenderer.doRenderLayer(entitylivingbaseIn, g, h, partialTicks, f2, f3, f4, f5);
         if (bl) {
            this.unsetBrightness();
         }
      }
   }

   public float getDeathMaxRotation(T entityLivingBaseIn) {
      return 90.0F;
   }

   public int getColorMultiplier(T entitylivingbaseIn, float lightBrightness, float partialTickTime) {
      return 0;
   }

   public void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
   }

   public void renderName(T entity, double x, double y, double z) {
      long l = O ^ 80966086978154L;
      if (!Reflector.RenderLivingEvent_Specials_Pre_Constructor.exists()
         || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Specials_Pre_Constructor, entity, this, x, y, z)) {
         if (this.canRenderName((T)entity)) {
            double d = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float f = entity.isSneaking() ? E : p;
            if (d < f * f) {
               String string = entity.getDisplayName().getFormattedText();
               long m = 8406457471310327947L ^ l;
               GlStateManager.alphaFunc(516, 0.1F);
               RenderNameTagEvent renderNameTagEvent = new RenderNameTagEvent();
               Astolfo.INSTANCE.getEventBus().post(renderNameTagEvent);
               if (renderNameTagEvent.isCancelled() && !(entity instanceof EntityArmorStand)) {
                  return;
               }

               if (entity.isSneaking()) {
                  FontRenderer fontRenderer = this.getFontRendererFromRenderManager();
                  GlStateManager.pushMatrix();
                  GlStateManager.translate((float)x, (float)y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float)z);
                  GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                  m = 641125262909184929L ^ l;
                  GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                  GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                  GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                  GlStateManager.translate(0.0F, 9.374999F, 0.0F);
                  GlStateManager.disableLighting();
                  GlStateManager.depthMask(false);
                  GlStateManager.enableBlend();
                  GlStateManager.disableTexture2D();
                  GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                  int i = fontRenderer.getStringWidth(string) / 2;
                  Tessellator tessellator = Tessellator.getInstance();
                  WorldRenderer worldRenderer = tessellator.getWorldRenderer();
                  worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                  worldRenderer.pos(-i - 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  worldRenderer.pos(-i - 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  worldRenderer.pos(i + 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  worldRenderer.pos(i + 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  tessellator.draw();
                  GlStateManager.enableTexture2D();
                  GlStateManager.depthMask(true);
                  fontRenderer.D(string, -fontRenderer.getStringWidth(string) / 2, 0.0F, 553648127);
                  GlStateManager.enableLighting();
                  GlStateManager.disableBlend();
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  GlStateManager.popMatrix();
               } else {
                  this.renderOffsetLivingLabel((T)entity, x, y - (entity.isChild() ? entity.height / 2.0F : 0.0), z, string, 0.02666667F, d);
               }
            }
         }

         if (Reflector.RenderLivingEvent_Specials_Post_Constructor.exists()
            && !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Specials_Post_Constructor, entity, this, x, y, z)) {
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean canRenderName(T entity) {
      EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
      if (entity instanceof EntityPlayer && entity != entityPlayerSP) {
         Team team = entity.getTeam();
         Team team2 = entityPlayerSP.getTeam();
         if (team != null) {
            Team.EnumVisible enumVisible = team.getNameTagVisibility();
            switch (enumVisible) {
               case ALWAYS:
                  return true;
               case NEVER:
                  return false;
               case HIDE_FOR_OTHER_TEAMS:
                  return team2 == null || team.isSameTeam(team2);
               case HIDE_FOR_OWN_TEAM:
                  return team2 == null || !team.isSameTeam(team2);
               default:
                  return true;
            }
         }
      }

      return Minecraft.isGuiEnabled() && entity != this.renderManager.livingPlayer && !entity.isInvisibleToPlayer(entityPlayerSP) && entity.riddenByEntity == null;
   }

   public void setRenderOutlines(boolean renderOutlinesIn) {
      this.renderOutlines = renderOutlinesIn;
   }

   static {
      int[] ints = textureBrightness.getTextureData();

      for (int i = 0; i < 256; i++) {
         ints[i] = -1;
      }

      textureBrightness.updateDynamicTexture();
   }
}
