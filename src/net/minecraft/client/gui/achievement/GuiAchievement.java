package net.minecraft.client.gui.achievement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;

public class GuiAchievement extends Gui {
   private static final ResourceLocation achievementBg = new ResourceLocation("textures/gui/achievement/achievement_background.png");
   private Minecraft mc;
   private int width;
   private int height;
   private String achievementTitle;
   private String achievementDescription;
   private Achievement theAchievement;
   private long notificationTime;
   private RenderItem renderItem;
   private boolean permanentNotification;
   private static final long a = 45159820649980L;

   public GuiAchievement(Minecraft var1) {
      this.mc = var1;
      this.renderItem = var1.getRenderItem();
   }

   public void displayAchievement(Achievement var1) {
      this.achievementTitle = I18n.format("achievement.get");
      this.achievementDescription = var1.getStatName().getUnformattedText();
      this.notificationTime = Minecraft.getSystemTime();
      this.theAchievement = var1;
      this.permanentNotification = false;
   }

   public void displayUnformattedAchievement(Achievement var1) {
      this.achievementTitle = var1.getStatName().getUnformattedText();
      this.achievementDescription = var1.getDescription();
      this.notificationTime = Minecraft.getSystemTime() + 2500L;
      this.theAchievement = var1;
      this.permanentNotification = true;
   }

   private void updateAchievementWindowScale() {
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      this.width = this.mc.displayWidth;
      this.height = this.mc.displayHeight;
      ScaledResolution var3 = new ScaledResolution(this.mc);
      this.width = var3.getScaledWidth();
      this.height = var3.getScaledHeight();
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, this.width, this.height, 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
   }

   public void updateAchievementWindow() {
      if (this.theAchievement != null && this.notificationTime != 0L && Minecraft.getMinecraft().thePlayer != null) {
         double var4 = (Minecraft.getSystemTime() - this.notificationTime) / 3000.0;
         if (!this.permanentNotification) {
            if (var4 < 0.0 || var4 > 1.0) {
               this.notificationTime = 0L;
               return;
            }
         } else if (var4 > 0.5) {
            var4 = 0.5;
         }

         this.updateAchievementWindowScale();
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         double var6 = var4 * 2.0;
         if (var6 > 1.0) {
            var6 = 2.0 - var6;
         }

         var6 *= 4.0;
         var6 = 1.0 - var6;
         if (var6 < 0.0) {
            var6 = 0.0;
         }

         var6 *= var6;
         var6 *= var6;
         int var8 = this.width - 160;
         int var9 = 0 - (int)(var6 * 36.0);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture2D();
         this.mc.getTextureManager().bindTexture(achievementBg);
         GlStateManager.disableLighting();
         this.drawTexturedModalRect(var8, var9, 96, 202, 160, 32);
         if (this.permanentNotification) {
            this.mc.fontRendererObj.drawSplitString(this.achievementDescription, var8 + 30, var9 + 7, 120, -1);
         } else {
            this.mc.fontRendererObj.D(this.achievementTitle, var8 + 30, var9 + 7, -256);
            this.mc.fontRendererObj.D(this.achievementDescription, var8 + 30, var9 + 18, -1);
         }

         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.renderItem.renderItemAndEffectIntoGUI(this.theAchievement.theItemStack, var8 + 8, var9 + 8);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
      }
   }

   public void clearAchievements() {
      this.theAchievement = null;
      this.notificationTime = 0L;
   }
}
