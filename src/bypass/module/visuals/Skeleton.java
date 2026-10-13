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
   private Listener<RenderEvent> onRender = var1x -> {
      if (var1x.getType() == RenderType.WORLD) {
         try {
            this.setupRender(true);
            GL11.glEnable(2903);
            if (!this.smooth.getValue()) {
               GL11.glDisable(2848);
            }

            modelRotations.keySet().removeIf(var0 -> GameUtil.isRemoved(var0) || GameUtil.isDead(var0));
            List var5 = mc.theWorld.playerEntities;
            int var6 = 0;

            for (int var7 = var5.size(); var6 < var7; var6++) {
               EntityPlayer var8 = (EntityPlayer)var5.get(var6);
               double var9 = MathUtil.interpolate(var8.lastTickPosX, var8.posX, var1x.getPartialTicks(), mc.getRenderManager().viewerPosX);
               double var11 = MathUtil.interpolate(var8.lastTickPosY, var8.posY, var1x.getPartialTicks(), mc.getRenderManager().viewerPosY);
               double var13 = MathUtil.interpolate(var8.lastTickPosZ, var8.posZ, var1x.getPartialTicks(), mc.getRenderManager().viewerPosZ);
               if (this.shouldRender(var8)) {
                  this.drawSkeleton(var8, var9, var11, var13);
               }
            }

            RenderUtil.setColor(-1);
            this.setupRender(false);
            modelRotations.clear();
         } catch (Exception var15) {
            this.setupRender(false);
            modelRotations.clear();
         }
      }
   };
   private static final long c = 45837517856036L;

   public Skeleton(Category var1) {
      super(var1);
      this.addSettings(this.checks, this.color, this.smooth);
      this.setHidden(false);
   }

   @Override
   public void onDisable() {
      modelRotations.clear();
      super.onDisable();
   }

   private boolean shouldRender(EntityPlayer var1) {
      return var1 != mc.thePlayer && !var1.isPlayerSleeping() && (!GameUtil.S(var1) || this.checks.getSetting("Invisible").asBoolean());
   }

   private void drawSkeleton(EntityPlayer var1, double var2, double var4, double var6) {
      long var8 = c ^ 34037393528557L;
      long var10001 = var8 ^ 77017285537428L;
      int var10 = (int)((var8 ^ 77017285537428L) >>> 56);
      int var11 = (int)((var8 ^ 77017285537428L) << 8 >>> 32);
      int var12 = (int)(var10001 << 40 >>> 40);
      float[][] var14 = modelRotations.get(var1);
      if (var14 != null) {
         int var15 = this.color.getValue().getRGB();
         if (client.getRelationManager().z(var1)) {
            var15 = new Color(15, 176, 245, 255).getRGB();
         }

         GL11.glPushMatrix();
         GL11.glLineWidth(1.0F);
         RenderUtil.setColor(var15);
         GL11.glTranslated(var2, var4, var6);
         float var16 = var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * mc.timer.timerSpeed;
         GL11.glRotatef(-var16, 0.0F, 1.0F, 0.0F);
         GL11.glTranslated(0.0, 0.0, var1.isSneaking() ? -0.235 : 0.0);
         float var17 = var1.isSneaking() ? 0.6F : 0.75F;
         GL11.glPushMatrix();
         RenderUtil.setColor(var15);
         GL11.glTranslated(-0.125, var17, 0.0);
         if (var14[3][0] != 0.0F) {
            GL11.glRotatef(var14[3][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (var14[3][1] != 0.0F) {
            GL11.glRotatef(var14[3][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (var14[3][2] != 0.0F) {
            GL11.glRotatef(var14[3][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, -var17, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         RenderUtil.setColor(var15);
         GL11.glTranslated(0.125, var17, 0.0);
         if (var14[4][0] != 0.0F) {
            GL11.glRotatef(var14[4][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (var14[4][1] != 0.0F) {
            GL11.glRotatef(var14[4][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (var14[4][2] != 0.0F) {
            GL11.glRotatef(var14[4][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, -var17, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glTranslated(0.0, 0.0, var1.isSneaking() ? 0.25 : 0.0);
         GL11.glPushMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glTranslated(0.0, var1.isSneaking() ? -0.05 : 0.0, var1.isSneaking() ? -0.01725 : 0.0);
         GL11.glPushMatrix();
         RenderUtil.setColor(var15);
         GL11.glTranslated(-0.375, var17 + 0.55, 0.0);
         if (var14[1][0] != 0.0F) {
            GL11.glRotatef(var14[1][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (var14[1][1] != 0.0F) {
            GL11.glRotatef(var14[1][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (var14[1][2] != 0.0F) {
            GL11.glRotatef(-var14[1][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, -0.5, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         GL11.glTranslated(0.375, var17 + 0.55, 0.0);
         if (var14[2][0] != 0.0F) {
            GL11.glRotatef(var14[2][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (var14[2][1] != 0.0F) {
            GL11.glRotatef(var14[2][1] * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (var14[2][2] != 0.0F) {
            GL11.glRotatef(-var14[2][2] * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, -0.5, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glRotatef(var16 - var1.rotationYawHead, 0.0F, 1.0F, 0.0F);
         GL11.glPushMatrix();
         RenderUtil.setColor(var15);
         GL11.glTranslated(0.0, var17 + 0.55, 0.0);
         if (var14[0][0] != 0.0F) {
            GL11.glRotatef(var14[0][0] * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, 0.3, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPopMatrix();
         GL11.glRotatef(var1.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
         GL11.glTranslated(0.0, var1.isSneaking() ? -0.16175 : 0.0, var1.isSneaking() ? -0.48025 : 0.0);
         GL11.glPushMatrix();
         GL11.glTranslated(0.0, var17, 0.0);
         GL11.glBegin(3);
         GL11.glVertex3d(-0.125, 0.0, 0.0);
         GL11.glVertex3d(0.125, 0.0, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         RenderUtil.setColor(var15);
         GL11.glTranslated(0.0, var17, 0.0);
         GL11.glBegin(3);
         GL11.glVertex3d(0.0, 0.0, 0.0);
         GL11.glVertex3d(0.0, 0.55, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         GL11.glTranslated(0.0, var17 + 0.55, 0.0);
         GL11.glBegin(3);
         GL11.glVertex3d(-0.375, 0.0, 0.0);
         GL11.glVertex3d(0.375, 0.0, 0.0);
         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glPopMatrix();
      }
   }

   public static void saveRotations(EntityPlayer var0, ModelPlayer var1) {
      modelRotations.put(
         var0,
         new float[][]{
            {var1.bipedHead.rotateAngleX, var1.bipedHead.rotateAngleY, var1.bipedHead.rotateAngleZ},
            {var1.bipedRightArm.rotateAngleX, var1.bipedRightArm.rotateAngleY, var1.bipedRightArm.rotateAngleZ},
            {var1.bipedLeftArm.rotateAngleX, var1.bipedLeftArm.rotateAngleY, var1.bipedLeftArm.rotateAngleZ},
            {var1.bipedRightLeg.rotateAngleX, var1.bipedRightLeg.rotateAngleY, var1.bipedRightLeg.rotateAngleZ},
            {var1.bipedLeftLeg.rotateAngleX, var1.bipedLeftLeg.rotateAngleY, var1.bipedLeftLeg.rotateAngleZ}
         }
      );
   }

   private void setupRender(boolean var1) {
      long var2 = c ^ 137173192615224L;
      if (var1) {
         GlStateManager.pushMatrix();
         GlStateManager.enableBlend();
         GL11.glEnable(2848);
         GlStateManager.disableDepth();
         long var10000 = 5633909756340823739L ^ var2;
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

      GlStateManager.depthMask(!var1);
   }
}
