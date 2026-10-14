package net.minecraft.client.renderer.entity;

import bypass.Astolfo;
import bypass.event.render.PlayerModelVisibilityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

public class RenderPlayer extends RendererLivingEntity<AbstractClientPlayer> {
   private boolean smallArms;

   public RenderPlayer(RenderManager renderManager) {
      this(renderManager, false);
   }

   public RenderPlayer(RenderManager renderManager, boolean useSmallArms) {
      super(renderManager, new ModelPlayer(0.0F, useSmallArms), 0.5F);
      this.smallArms = useSmallArms;
      this.addLayer(new LayerBipedArmor(this));
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerArrow(this));
      this.addLayer(new LayerDeadmau5Head(this));
      this.addLayer(new LayerCape(this));
      this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
   }

   public ModelPlayer getMainModel() {
      return (ModelPlayer)super.getMainModel();
   }

   public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!entity.v() || this.renderManager.livingPlayer == entity) {
         double d = y;
         if (entity.isSneaking() && !(entity instanceof EntityPlayerSP)) {
            d = y - 0.125;
         }

         if (entity.getEntityId() != -1337 && entity.getEntityId() != -420) {
            this.setModelVisibilities(entity);
         } else {
            this.setModelVisibilities(Minecraft.getMinecraft().thePlayer);
         }

         this.setModelVisibilities(entity);
         super.doRender(entity, x, d, z, entityYaw, partialTicks);
      }
   }

   private void setModelVisibilities(AbstractClientPlayer clientPlayer) {
      ModelPlayer modelPlayer = this.getMainModel();
      if (clientPlayer.isSpectator()) {
         modelPlayer.setInvisible(false);
         modelPlayer.bipedHead.showModel = true;
         modelPlayer.bipedHeadwear.showModel = true;
      } else {
         ItemStack itemStack = clientPlayer.inventory.getCurrentItem();
         modelPlayer.setInvisible(true);
         modelPlayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
         modelPlayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
         modelPlayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
         modelPlayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
         modelPlayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
         modelPlayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
         modelPlayer.heldItemLeft = 0;
         modelPlayer.aimedBow = false;
         PlayerModelVisibilityEvent playerModelVisibilityEvent = new PlayerModelVisibilityEvent(clientPlayer);
         Astolfo.INSTANCE.getEventBus().post(playerModelVisibilityEvent);
         modelPlayer.isSneak = playerModelVisibilityEvent.isCancelled() || clientPlayer.isSneaking();
         if (itemStack == null) {
            modelPlayer.heldItemRight = 0;
         } else {
            modelPlayer.heldItemRight = 1;
            if (clientPlayer.p() > 0) {
               EnumAction enumAction = itemStack.getItemUseAction();
               if (enumAction == EnumAction.BLOCK) {
                  modelPlayer.heldItemRight = 3;
               } else if (enumAction == EnumAction.BOW) {
                  modelPlayer.aimedBow = true;
               }
            }
         }
      }
   }

   public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
      return entity.getLocationSkin();
   }

   @Override
   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.0F, 0.1875F, 0.0F);
   }

   public void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime) {
      float f = 0.9375F;
      GlStateManager.scale(f, f, f);
   }

   public void renderOffsetLivingLabel(AbstractClientPlayer entityIn, double x, double y, double z, String str, float g, double d) {
      if (d < 100.0) {
         Scoreboard scoreboard = entityIn.getWorldScoreboard();
         ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(2);
         if (scoreObjective != null) {
            Score score = scoreboard.getValueFromObjective(entityIn.getName(), scoreObjective);
            this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreObjective.getDisplayName(), x, y, z, 64);
            y += this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * g;
         }
      }

      super.renderOffsetLivingLabel(entityIn, x, y, z, str, g, d);
   }

   public void renderRightArm(AbstractClientPlayer clientPlayer) {
      float f = 1.0F;
      GlStateManager.color(f, f, f);
      ModelPlayer modelPlayer = this.getMainModel();
      this.setModelVisibilities(clientPlayer);
      modelPlayer.swingProgress = 0.0F;
      modelPlayer.isRiding = modelPlayer.isSneak = false;
      modelPlayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
      modelPlayer.renderRightArm();
   }

   public void renderLeftArm(AbstractClientPlayer clientPlayer) {
      float f = 1.0F;
      GlStateManager.color(f, f, f);
      ModelPlayer modelPlayer = this.getMainModel();
      this.setModelVisibilities(clientPlayer);
      modelPlayer.isSneak = false;
      modelPlayer.swingProgress = 0.0F;
      modelPlayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
      modelPlayer.renderLeftArm();
   }

   public void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z) {
      if (entityLivingBaseIn.isEntityAlive() && entityLivingBaseIn.isPlayerSleeping()) {
         super.renderLivingAt(
            entityLivingBaseIn, x + entityLivingBaseIn.renderOffsetX, y + entityLivingBaseIn.renderOffsetY, z + entityLivingBaseIn.renderOffsetZ
         );
      } else {
         super.renderLivingAt(entityLivingBaseIn, x, y, z);
      }
   }

   public void rotateCorpse(AbstractClientPlayer bat, float g, float h, float partialTicks) {
      if (bat.isEntityAlive() && bat.isPlayerSleeping()) {
         GlStateManager.rotate(bat.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
      } else {
         super.rotateCorpse(bat, g, h, partialTicks);
      }
   }
}
