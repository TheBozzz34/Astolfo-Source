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
   private static final long d = 27286183765204L;

   public RenderPlayer(RenderManager var1) {
      this(var1, false);
   }

   public RenderPlayer(RenderManager var1, boolean var2) {
      super(var1, new ModelPlayer(0.0F, var2), 0.5F);
      this.smallArms = var2;
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

   public void doRender(AbstractClientPlayer var1, double var2, double var4, double var6, float var8, float var9) {
      if (!var1.v() || this.renderManager.livingPlayer == var1) {
         double var13 = var4;
         if (var1.isSneaking() && !(var1 instanceof EntityPlayerSP)) {
            var13 = var4 - 0.125;
         }

         if (var1.getEntityId() != -1337 && var1.getEntityId() != -420) {
            this.setModelVisibilities(var1);
         } else {
            this.setModelVisibilities(Minecraft.getMinecraft().thePlayer);
         }

         this.setModelVisibilities(var1);
         super.doRender(var1, var2, var13, var6, var8, var9);
      }
   }

   private void setModelVisibilities(AbstractClientPlayer var1) {
      ModelPlayer var5 = this.getMainModel();
      if (var1.isSpectator()) {
         var5.setInvisible(false);
         var5.bipedHead.showModel = true;
         var5.bipedHeadwear.showModel = true;
      } else {
         ItemStack var6 = var1.inventory.getCurrentItem();
         var5.setInvisible(true);
         var5.bipedHeadwear.showModel = var1.isWearing(EnumPlayerModelParts.HAT);
         var5.bipedBodyWear.showModel = var1.isWearing(EnumPlayerModelParts.JACKET);
         var5.bipedLeftLegwear.showModel = var1.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
         var5.bipedRightLegwear.showModel = var1.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
         var5.bipedLeftArmwear.showModel = var1.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
         var5.bipedRightArmwear.showModel = var1.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
         var5.heldItemLeft = 0;
         var5.aimedBow = false;
         PlayerModelVisibilityEvent var7 = new PlayerModelVisibilityEvent(var1);
         Astolfo.INSTANCE.getEventBus().post(var7);
         var5.isSneak = var7.isCancelled() || var1.isSneaking();
         if (var6 == null) {
            var5.heldItemRight = 0;
         } else {
            var5.heldItemRight = 1;
            if (var1.p() > 0) {
               EnumAction var8 = var6.getItemUseAction();
               if (var8 == EnumAction.BLOCK) {
                  var5.heldItemRight = 3;
               } else if (var8 == EnumAction.BOW) {
                  var5.aimedBow = true;
               }
            }
         }
      }
   }

   public ResourceLocation getEntityTexture(AbstractClientPlayer var1) {
      return var1.getLocationSkin();
   }

   @Override
   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.0F, 0.1875F, 0.0F);
   }

   public void preRenderCallback(AbstractClientPlayer var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.scale(var3, var3, var3);
   }

   public void renderOffsetLivingLabel(AbstractClientPlayer var1, double var2, double var4, double var6, String var8, float var9, double var10) {
      if (var10 < 100.0) {
         Scoreboard var15 = var1.getWorldScoreboard();
         ScoreObjective var16 = var15.getObjectiveInDisplaySlot(2);
         if (var16 != null) {
            Score var17 = var15.getValueFromObjective(var1.getName(), var16);
            this.renderLivingLabel(var1, var17.getScorePoints() + " " + var16.getDisplayName(), var2, var4, var6, 64);
            var4 += this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * var9;
         }
      }

      super.renderOffsetLivingLabel(var1, var2, var4, var6, var8, var9, var10);
   }

   public void renderRightArm(AbstractClientPlayer var1) {
      float var2 = 1.0F;
      GlStateManager.color(var2, var2, var2);
      ModelPlayer var3 = this.getMainModel();
      this.setModelVisibilities(var1);
      var3.swingProgress = 0.0F;
      var3.isRiding = var3.isSneak = false;
      var3.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, var1);
      var3.renderRightArm();
   }

   public void renderLeftArm(AbstractClientPlayer var1) {
      float var2 = 1.0F;
      GlStateManager.color(var2, var2, var2);
      ModelPlayer var3 = this.getMainModel();
      this.setModelVisibilities(var1);
      var3.isSneak = false;
      var3.swingProgress = 0.0F;
      var3.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, var1);
      var3.renderLeftArm();
   }

   public void renderLivingAt(AbstractClientPlayer var1, double var2, double var4, double var6) {
      if (var1.isEntityAlive() && var1.isPlayerSleeping()) {
         super.renderLivingAt(var1, var2 + var1.renderOffsetX, var4 + var1.renderOffsetY, var6 + var1.renderOffsetZ);
      } else {
         super.renderLivingAt(var1, var2, var4, var6);
      }
   }

   public void rotateCorpse(AbstractClientPlayer var1, float var2, float var3, float var4) {
      if (var1.isEntityAlive() && var1.isPlayerSleeping()) {
         GlStateManager.rotate(var1.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.getDeathMaxRotation(var1), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
      } else {
         super.rotateCorpse(var1, var2, var3, var4);
      }
   }
}
