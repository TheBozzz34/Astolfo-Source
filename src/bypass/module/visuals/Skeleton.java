package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.util.GameUtil;
import bypass.util.MathUtil;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public final class Skeleton extends Module {
   private final BooleanSetting smooth = new BooleanSetting("Smooth", "Smooth lines", true);
   private final ColorSetting color = new ColorSetting("Color", "color", Color.WHITE);
   private final MapSetting checks = new MapSettingBuilder("Checks")
      .ordered()
      .description("entity checks")
      .add("Invisible", new BooleanSetting("Invisible", "Renders invisible players", true))
      .build();
   private static Map<EntityPlayer, float[][]> modelRotations = new HashMap<>();
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD) {
         try {
            this.setupRender(true);
            GL11.glEnable(2903);
            if (!this.smooth.getValue()) {
               GL11.glDisable(2848);
            }

            modelRotations.keySet().removeIf(entityPlayer -> GameUtil.isRemoved(entityPlayer) || GameUtil.isDead(entityPlayer));
            List list = mc.theWorld.playerEntities;
            int i = 0;

            for (int j = list.size(); i < j; i++) {
               EntityPlayer entityPlayer2 = (EntityPlayer)list.get(i);
               double d = MathUtil.interpolate(entityPlayer2.lastTickPosX, entityPlayer2.posX, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosX);
               double e = MathUtil.interpolate(entityPlayer2.lastTickPosY, entityPlayer2.posY, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosY);
               double d2 = MathUtil.interpolate(entityPlayer2.lastTickPosZ, entityPlayer2.posZ, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosZ);
               if (this.shouldRender(entityPlayer2)) {
                  this.drawSkeleton(entityPlayer2, d, e, d2);
               }
            }

            RenderUtil.setColor(-1);
            this.setupRender(false);
            modelRotations.clear();
         } catch (Exception exception) {
            this.setupRender(false);
            modelRotations.clear();
         }
      }
   };

   public Skeleton(Category category) {
      super(category);
      this.addSettings(this.checks, this.color, this.smooth);
      this.setHidden(false);
   }

   @Override
   public void onDisable() {
      modelRotations.clear();
      super.onDisable();
   }

   private boolean shouldRender(EntityPlayer entityPlayer) {
      return entityPlayer != mc.thePlayer
         && !entityPlayer.isPlayerSleeping()
         && (!GameUtil.isFullyInvisible(entityPlayer) || this.checks.getSetting("Invisible").asBoolean());
   }

   private void drawSkeleton(EntityPlayer entityPlayer, double d, double e, double d2) {
      float[][] floatss = modelRotations.get(entityPlayer);
      if (floatss != null) {
         int i = this.color.getValue().getRGB();
         if (client.getRelationManager().isFriend(entityPlayer)) {
            i = new Color(15, 176, 245, 255).getRGB();
         }

         GL11.glPushMatrix();
         GL11.glLineWidth(1.0F);
         RenderUtil.setColor(i);
         GL11.glTranslated(d, e, d2);
         float f = entityPlayer.prevRenderYawOffset + (entityPlayer.renderYawOffset - entityPlayer.prevRenderYawOffset) * mc.timer.timerSpeed;
         GL11.glRotatef(-f, 0.0F, 1.0F, 0.0F);
         GL11.glTranslated(0.0, 0.0, entityPlayer.isSneaking() ? -0.235 : 0.0);
         float g = entityPlayer.isSneaking() ? 0.6F : 0.75F;
         GL11.glPushMatrix();
         RenderUtil.setColor(i);
         GL11.glTranslated(-0.125, g, 0.0);
         if (floatss[3][0] != 0.0F) {
            GL11.glRotatef(floatss[3][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (floatss[3][1] != 0.0F) {
            GL11.glRotatef(floatss[3][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (floatss[3][2] != 0.0F) {
            GL11.glRotatef(floatss[3][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, -g, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         RenderUtil.setColor(i);
         GL11.glTranslated(0.125, g, 0.0);
         if (floatss[4][0] != 0.0F) {
            GL11.glRotatef(floatss[4][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (floatss[4][1] != 0.0F) {
            GL11.glRotatef(floatss[4][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (floatss[4][2] != 0.0F) {
            GL11.glRotatef(floatss[4][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, -g, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glTranslated(0.0, 0.0, entityPlayer.isSneaking() ? 0.25 : 0.0);
         GL11.glPushMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glTranslated(0.0, entityPlayer.isSneaking() ? -0.05 : 0.0, entityPlayer.isSneaking() ? -0.01725 : 0.0);
         GL11.glPushMatrix();
         RenderUtil.setColor(i);
         GL11.glTranslated(-0.375, g + 0.55, 0.0);
         if (floatss[1][0] != 0.0F) {
            GL11.glRotatef(floatss[1][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (floatss[1][1] != 0.0F) {
            GL11.glRotatef(floatss[1][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (floatss[1][2] != 0.0F) {
            GL11.glRotatef(-floatss[1][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, -0.5, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         GL11.glTranslated(0.375, g + 0.55, 0.0);
         if (floatss[2][0] != 0.0F) {
            GL11.glRotatef(floatss[2][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (floatss[2][1] != 0.0F) {
            GL11.glRotatef(floatss[2][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (floatss[2][2] != 0.0F) {
            GL11.glRotatef(-floatss[2][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, -0.5, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glRotatef(f - entityPlayer.rotationYawHead, 0.0F, 1.0F, 0.0F);
         GL11.glPushMatrix();
         RenderUtil.setColor(i);
         GL11.glTranslated(0.0, g + 0.55, 0.0);
         if (floatss[0][0] != 0.0F) {
            GL11.glRotatef(floatss[0][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, 0.3, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPopMatrix();
         GL11.glRotatef(entityPlayer.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
         GL11.glTranslated(0.0, entityPlayer.isSneaking() ? -0.16175 : 0.0, entityPlayer.isSneaking() ? -0.48025 : 0.0);
         GL11.glPushMatrix();
         GL11.glTranslated(0.0, g, 0.0);
         GL11.glBegin(3);
         GL11.glVertex3d(-0.125, 0.0, 0.0);
         GL11.glVertex3d(0.125, 0.0, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         RenderUtil.setColor(i);
         GL11.glTranslated(0.0, g, 0.0);
         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, 0.55, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         GL11.glTranslated(0.0, g + 0.55, 0.0);
         GL11.glBegin(3);
         GL11.glVertex3d(-0.375, 0.0, 0.0);
         GL11.glVertex3d(0.375, 0.0, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPopMatrix();
      }
   }

   public static void saveRotations(EntityPlayer entityPlayer, ModelPlayer modelPlayer) {
      modelRotations.put(
         entityPlayer,
         new float[][]{
            {modelPlayer.bipedHead.rotateAngleX, modelPlayer.bipedHead.rotateAngleY, modelPlayer.bipedHead.rotateAngleZ},
            {modelPlayer.bipedRightArm.rotateAngleX, modelPlayer.bipedRightArm.rotateAngleY, modelPlayer.bipedRightArm.rotateAngleZ},
            {modelPlayer.bipedLeftArm.rotateAngleX, modelPlayer.bipedLeftArm.rotateAngleY, modelPlayer.bipedLeftArm.rotateAngleZ},
            {modelPlayer.bipedRightLeg.rotateAngleX, modelPlayer.bipedRightLeg.rotateAngleY, modelPlayer.bipedRightLeg.rotateAngleZ},
            {modelPlayer.bipedLeftLeg.rotateAngleX, modelPlayer.bipedLeftLeg.rotateAngleY, modelPlayer.bipedLeftLeg.rotateAngleZ}
         }
      );
   }

   private void setupRender(boolean bl) {
      if (bl) {
         GlStateManager.pushMatrix();
         GlStateManager.enableBlend();
         GL11.glEnable(2848);
         GlStateManager.disableDepth();
         GlStateManager.disableTexture2D();
         GlStateManager.blendFunc(770, 771);
         GL11.glHint(3154, 4354);
      } else {
         GlStateManager.disableBlend();
         GlStateManager.enableTexture2D();
         GL11.glDisable(2848);
         GlStateManager.enableDepth();
         GlStateManager.popMatrix();
      }

      GlStateManager.depthMask(!bl);
   }
}
