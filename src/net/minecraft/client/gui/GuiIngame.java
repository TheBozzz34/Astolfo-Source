package net.minecraft.client.gui;

import bypass.Astolfo;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderHotbarEvent;
import bypass.event.render.RenderOverlaysEvent;
import bypass.event.render.RenderType;
import bypass.gui.CustomChatGui;
import bypass.module.ModuleManager;
import bypass.module.hidden.TitleAnnouncer;
import bypass.module.visuals.Crosshair;
import bypass.module.visuals.StreamerMode;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;

public class GuiIngame extends Gui {
   private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
   private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
   private final Random rand = new Random();
   private final Minecraft mc;
   private final RenderItem itemRenderer;
   private GuiNewChat persistantChatGUI;
   private final GuiStreamIndicator streamIndicator;
   private int updateCounter;
   private String recordPlaying = "";
   private int recordPlayingUpFor;
   private boolean recordIsPlaying;
   public float prevVignetteBrightness = 1.0F;
   private int remainingHighlightTicks;
   private ItemStack highlightingItemStack;
   private final GuiOverlayDebug overlayDebug;
   private final GuiSpectator spectatorGui;
   private final GuiPlayerTabOverlay overlayPlayerList;
   public int titlesTimer;
   public String displayedTitle = "";
   public String displayedSubTitle = "";
   private int titleFadeIn;
   private int titleDisplayTime;
   private int titleFadeOut;
   private int playerHealth = 0;
   private int lastPlayerHealth = 0;
   private long lastSystemTime = 0L;
   private long healthUpdateCounter = 0L;
   public static boolean A;
   private String w = "";
   public static boolean E;
   public static boolean b;
   public static String Y = "by ";
   public static String G = " ";
   private static final long c = 111107255975176L;

   public GuiIngame(Minecraft var1) {
      this.mc = var1;
      this.itemRenderer = var1.getRenderItem();
      this.overlayDebug = new GuiOverlayDebug(var1);
      this.spectatorGui = new GuiSpectator(var1);
      this.persistantChatGUI = new CustomChatGui(var1);
      this.streamIndicator = new GuiStreamIndicator(var1);
      this.overlayPlayerList = new GuiPlayerTabOverlay(var1, this);
      this.setDefaultTitlesTimes();
   }

   public void setDefaultTitlesTimes() {
      this.titleFadeIn = 10;
      this.titleDisplayTime = 70;
      this.titleFadeOut = 20;
   }

   public void renderGameOverlay(float var1) {
      long var2 = c ^ 92627008101409L;
      ScaledResolution var5 = new ScaledResolution(this.mc);
      int var6 = var5.getScaledWidth();
      int var7 = var5.getScaledHeight();
      this.mc.entityRenderer.setupOverlayRendering();
      GlStateManager.enableBlend();
      if (Config.isVignetteEnabled()) {
         this.renderVignette(this.mc.thePlayer.getBrightness(var1), var5);
      } else {
         GlStateManager.enableDepth();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      }

      ItemStack var8 = this.mc.thePlayer.inventory.armorItemInSlot(3);
      if (this.mc.gameSettings.thirdPersonView == 0 && var8 != null && var8.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
         this.renderPumpkinOverlay(var5);
      }

      if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
         float var9 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * var1;
         if (var9 > 0.0F) {
            this.renderPortal(var9, var5);
         }
      }

      if (this.mc.playerController.W()) {
         this.spectatorGui.renderTooltip(var5, var1);
      } else {
         this.renderTooltip(var5, var1);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
      GlStateManager.enableBlend();
      if (this.showCrosshair() && this.mc.gameSettings.thirdPersonView < 1) {
         GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
         GlStateManager.enableAlpha();
         this.drawTexturedModalRect(var6 / 2 - 7, var7 / 2 - 7, 0, 0, 16, 16);
      }

      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      this.mc.mcProfiler.startSection("bossHealth");
      this.l();
      this.mc.mcProfiler.endSection();
      if (this.mc.playerController.shouldDrawHUD()) {
         this.renderPlayerStats(var5);
      }

      GlStateManager.disableBlend();
      if (this.mc.thePlayer.c() > 0) {
         this.mc.mcProfiler.startSection("sleep");
         GlStateManager.disableDepth();
         GlStateManager.disableAlpha();
         int var14 = this.mc.thePlayer.c();
         float var10 = var14 / 100.0F;
         if (var10 > 1.0F) {
            var10 = 1.0F - (var14 - 100) / 10.0F;
         }

         int var11 = (int)(220.0F * var10) << 24 | 1052704;
         drawRect(0, 0, var6, var7, var11);
         GlStateManager.enableAlpha();
         GlStateManager.enableDepth();
         this.mc.mcProfiler.endSection();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int var15 = var6 / 2 - 91;
      if (this.mc.thePlayer.Y8()) {
         this.renderHorseJumpBar(var5, var15);
      } else if (this.mc.playerController.D()) {
         this.renderExpBar(var5, var15);
      }

      if (this.mc.gameSettings.Z9 && !this.mc.playerController.W()) {
         this.renderSelectedItem(var5);
      } else if (this.mc.thePlayer.isSpectator()) {
         this.spectatorGui.renderSelectedItem(var5);
      }

      if (this.mc.isDemo()) {
         this.renderDemo(var5);
      }

      if (this.mc.gameSettings.n) {
         this.overlayDebug.renderDebugInfo(var5);
      }

      if (this.recordPlayingUpFor > 0) {
         this.mc.mcProfiler.startSection("overlayMessage");
         float var16 = this.recordPlayingUpFor - var1;
         int var19 = (int)(var16 * 255.0F / 20.0F);
         if (var19 > 255) {
            var19 = 255;
         }

         if (var19 > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(var6 / 2, var7 - 68, 0.0F);
            GlStateManager.enableBlend();
            long var10000 = 3199929593377112173L ^ var2;
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            int var12 = 16777215;
            if (this.recordIsPlaying) {
               var12 = MathHelper.hsvToRGB(var16 / 50.0F, 0.7F, 0.6F) & 16777215;
            }

            this.getFontRenderer()
               .D(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4.0F, var12 + (var19 << 24 & 0xFF000000));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }

         this.mc.mcProfiler.endSection();
      }

      if (this.titlesTimer > 0 && (!ModuleManager.S.isEnabled() || !TitleAnnouncer.d.hasElapsed(4500.0F))) {
         String var17 = this.mc.mouseHelper.Y().getAccount().getUsername().toLowerCase();
         if (var17.equals("belle")
            || var17.equals("triangle")
            || var17.equals("god")
            || var17.equals("idonthaveamic")
            || var17.equals("notc")
            || var17.equals("ffxfusion")
            || var17.equals("pure")
            || var17.equals("royalty")
            || var17.equals("idle")) {
            this.displayedSubTitle = this.displayedSubTitle.replace("last man standing", "last §dsissy §7standing");
         }

         this.mc.mcProfiler.startSection("titleAndSubtitle");
         float var20 = this.titlesTimer - var1;
         int var22 = 255;
         if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
            float var13 = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut - var20;
            var22 = (int)(var13 * 255.0F / this.titleFadeIn);
         }

         if (this.titlesTimer <= this.titleFadeOut) {
            var22 = (int)(var20 * 255.0F / this.titleFadeOut);
         }

         var22 = MathHelper.clamp_int(var22, 0, 255);
         if (var22 > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(var6 / 2, var7 / 2, 0.0F);
            long var29 = 3199929593377112173L ^ var2;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 4.0F);
            int var25 = var22 << 24 & 0xFF000000;
            this.getFontRenderer()
               .drawString(this.displayedTitle, -this.getFontRenderer().getStringWidth(this.displayedTitle) / 2, -10.0F, 16777215 | var25, true);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            this.getFontRenderer()
               .drawString(this.displayedSubTitle, -this.getFontRenderer().getStringWidth(this.displayedSubTitle) / 2, 5.0F, 16777215 | var25, true);
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }

         this.mc.mcProfiler.endSection();
      }

      Scoreboard var18 = this.mc.theWorld.getScoreboard();
      ScoreObjective var21 = null;
      ScorePlayerTeam var24 = var18.getPlayersTeam(this.mc.thePlayer.getName());
      if (var24 != null && var24.getChatFormat() != null) {
         int var26 = var24.getChatFormat().getColorIndex();
         if (var26 >= 0) {
            var21 = var18.getObjectiveInDisplaySlot(3 + var26);
         }
      }

      ScoreObjective var27 = var21 != null ? var21 : var18.getObjectiveInDisplaySlot(1);
      if (var27 != null) {
         this.renderScoreboard(var27, var5);
      }

      Astolfo.INSTANCE.getEventBus().post(new RenderEvent(RenderType.SCREEN, var1, var5, this));
      GlStateManager.enableBlend();
      long var30 = 3199929593377112173L ^ var2;
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.disableAlpha();
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, var7 - 48, 0.0F);
      this.mc.mcProfiler.startSection("chat");
      this.persistantChatGUI.drawChat(this.updateCounter);
      this.mc.mcProfiler.endSection();
      GlStateManager.popMatrix();
      var27 = var18.getObjectiveInDisplaySlot(0);
      if (this.mc.gameSettings.ZW.isKeyDown()
         && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() > 1 || var27 != null)) {
         this.overlayPlayerList.updatePlayerList(true);
         this.overlayPlayerList.renderPlayerlist(var6, var18, var27);
      } else {
         this.overlayPlayerList.updatePlayerList(false);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      GlStateManager.enableAlpha();
   }

   public void renderTooltip(ScaledResolution var1, float var2) {
      long var3 = c ^ 126356357983019L;
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(widgetsTexPath);
         EntityPlayer var6 = (EntityPlayer)this.mc.getRenderViewEntity();
         int var7 = var1.getScaledWidth() / 2;
         float var8 = this.zLevel;
         this.zLevel = -90.0F;
         this.drawTexturedModalRect(var7 - 91, var1.getScaledHeight() - 22, 0, 0, 182, 22);
         RenderHotbarEvent var9 = new RenderHotbarEvent(var6.inventory.currentItem);
         Astolfo.INSTANCE.getEventBus().post(var9);
         this.drawTexturedModalRect(var7 - 91 - 1 + var9.getSlot() * 20, var1.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
         this.zLevel = var8;
         long var10000 = 3199888723445544807L ^ var3;
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         RenderHelper.enableGUIStandardItemLighting();

         for (int var10 = 0; var10 < 9; var10++) {
            int var11 = var1.getScaledWidth() / 2 - 90 + var10 * 20 + 2;
            int var12 = var1.getScaledHeight() - 16 - 3;
            this.renderHotbarItem(var10, var11, var12, var2, var6);
         }

         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
      }
   }

   public void renderHorseJumpBar(ScaledResolution var1, int var2) {
      this.mc.mcProfiler.startSection("jumpBar");
      this.mc.getTextureManager().bindTexture(Gui.icons);
      float var6 = this.mc.thePlayer.getHorseJumpPower();
      short var7 = 182;
      int var8 = (int)(var6 * (var7 + 1));
      int var9 = var1.getScaledHeight() - 32 + 3;
      this.drawTexturedModalRect(var2, var9, 0, 84, var7, 5);
      if (var8 > 0) {
         this.drawTexturedModalRect(var2, var9, 0, 89, var8, 5);
      }

      this.mc.mcProfiler.endSection();
   }

   public void renderExpBar(ScaledResolution var1, int var2) {
      this.mc.mcProfiler.startSection("expBar");
      this.mc.getTextureManager().bindTexture(Gui.icons);
      int var6 = this.mc.thePlayer.xpBarCap();
      if (var6 > 0) {
         short var7 = 182;
         int var8 = (int)(this.mc.thePlayer.experience * (var7 + 1));
         int var9 = var1.getScaledHeight() - 32 + 3;
         this.drawTexturedModalRect(var2, var9, 0, 64, var7, 5);
         if (var8 > 0) {
            this.drawTexturedModalRect(var2, var9, 0, 69, var8, 5);
         }
      }

      this.mc.mcProfiler.endSection();
      if (this.mc.thePlayer.experienceLevel > 0) {
         this.mc.mcProfiler.startSection("expLevel");
         int var11 = 8453920;
         if (Config.isCustomColors()) {
            var11 = CustomColors.k(var11);
         }

         String var12 = "" + this.mc.thePlayer.experienceLevel;
         int var13 = (var1.getScaledWidth() - this.getFontRenderer().getStringWidth(var12)) / 2;
         int var10 = var1.getScaledHeight() - 31 - 4;
         this.getFontRenderer().D(var12, var13 + 1, var10, 0);
         this.getFontRenderer().D(var12, var13 - 1, var10, 0);
         this.getFontRenderer().D(var12, var13, var10 + 1, 0);
         this.getFontRenderer().D(var12, var13, var10 - 1, 0);
         this.getFontRenderer().D(var12, var13, var10, var11);
         this.mc.mcProfiler.endSection();
      }
   }

   public void renderSelectedItem(ScaledResolution var1) {
      long var2 = c ^ 77693665576219L;
      this.mc.mcProfiler.startSection("selectedItemName");
      if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
         String var5 = this.highlightingItemStack.getDisplayName();
         if (this.highlightingItemStack.hasDisplayName()) {
            var5 = EnumChatFormatting.ITALIC + var5;
         }

         int var6 = (var1.getScaledWidth() - this.getFontRenderer().getStringWidth((java.lang.String)(var5))) / 2;
         int var7 = var1.getScaledHeight() - 59;
         if (!this.mc.playerController.shouldDrawHUD()) {
            var7 += 14;
         }

         int var8 = (int)(this.remainingHighlightTicks * 256.0F / 10.0F);
         if (var8 > 255) {
            var8 = 255;
         }

         if (var8 > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            long var10000 = 3199945617627175255L ^ var2;
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            this.getFontRenderer().drawStringWithShadow((java.lang.String)(var5), var6, var7, 16777215 + (var8 << 24));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }

      this.mc.mcProfiler.endSection();
   }

   public void renderDemo(ScaledResolution var1) {
      this.mc.mcProfiler.startSection("demo");
      String var5 = "";
      if (this.mc.theWorld.getTotalWorldTime() >= 120500L) {
         var5 = I18n.format("demo.demoExpired");
      } else {
         var5 = I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime())));
      }

      int var6 = this.getFontRenderer().getStringWidth(var5);
      this.getFontRenderer().drawStringWithShadow(var5, var1.getScaledWidth() - var6 - 10, 5.0F, 16777215);
      this.mc.mcProfiler.endSection();
   }

   public boolean showCrosshair() {
      long var1 = c ^ 17572052907717L;
      long var3 = var1 ^ 102811327526526L;
      if ((!this.mc.gameSettings.n || this.mc.thePlayer.w() || this.mc.gameSettings.reducedDebugInfo)
         && !Astolfo.INSTANCE.getModuleManager().get(Crosshair.class).isEnabled()) {
         if (!this.mc.playerController.W()) {
            return true;
         }

         if (this.mc.pointedEntity != null) {
            return true;
         }

         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos var6 = this.mc.objectMouseOver.getBlockPos();
            if (this.mc.theWorld.getTileEntity(var6) instanceof IInventory) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void renderStreamIndicator(ScaledResolution var1) {
      this.streamIndicator.render(var1.getScaledWidth() - 10, 10);
   }

   private void renderScoreboard(ScoreObjective var1, ScaledResolution var2) {
      long var3 = c ^ 68540950107578L;
      long var5 = var3 ^ 118938409407745L;
      Scoreboard var8 = var1.getScoreboard();
      Collection var9 = var8.getSortedScores(var1);
      ArrayList var10 = Lists.newArrayList(Iterables.filter(var9, new Predicate<Score>() {
         private static final long a = 130210547240308L;

         public boolean apply(Score var1) {
            return var1.getPlayerName() != null && !var1.getPlayerName().startsWith("#");
         }
      }));
      ArrayList var11;
      if (var10.size() > 15) {
         var11 = Lists.newArrayList(Iterables.skip(var10, var9.size() - 15));
      } else {
         var11 = var10;
      }

      int var12 = this.getFontRenderer().getStringWidth(var1.getDisplayName());

      for (Object var14 : var11) {
         ScorePlayerTeam var15 = var8.getPlayersTeam(((Score)var14).getPlayerName());
         String var16 = ScorePlayerTeam.formatPlayerName(var15, ((Score)var14).getPlayerName())
            + ": "
            + EnumChatFormatting.RED
            + ((Score)var14).getScorePoints();
         var12 = Math.max(var12, this.getFontRenderer().getStringWidth(var16));
      }

      bypass.module.visuals.Scoreboard var29 = Astolfo.INSTANCE.getModuleManager().get(bypass.module.visuals.Scoreboard.class);
      StreamerMode var30 = (StreamerMode)ModuleManager.G;
      int var31 = var11.size() * this.getFontRenderer().FONT_HEIGHT;
      int var32 = var2.getScaledHeight() / 2 + var31 / 3;
      if (var29.isEnabled()) {
         var32 = (int)(var32 + bypass.module.visuals.Scoreboard.position.getValue());
      }

      boolean var17 = !var29.isEnabled() || !bypass.module.visuals.Scoreboard.remove.getValue();
      byte var18 = 3;
      int var19 = var2.getScaledWidth() - var12 - var18;
      int var20 = 0;
      b = false;
      E = false;

      for (Object var22 : var11) {
         var20++;
         ScorePlayerTeam var23 = var8.getPlayersTeam(((Score)var22).getPlayerName());
         String var24 = ScorePlayerTeam.formatPlayerName(var23, ((Score)var22).getPlayerName());
         if (var20 == var11.size() && var24.contains("  §8")) {
            String var25 = V(var24);
            if (!V(this.w).equals(var25)) {
               A = true;
            }

            this.w = var25;
            if (var30.isEnabled()) {
               var24 = var24.replaceAll("§8", "§8§k");
            }
         }

         int var33 = var32 - var20 * this.getFontRenderer().FONT_HEIGHT;
         int var26 = var2.getScaledWidth() - var18 + 2;
         if (var17) {
            drawRect(var19 - 2, var33, var26, var33 + this.getFontRenderer().FONT_HEIGHT, 1342177280);
         }

         String var27 = StringUtils.stripControlCodes(var24);
         if (var27.contains("SPIRE")) {
            E = true;
         }

         if (var27.contains("Mode: Ranked")) {
            b = true;
         }

         if (var17) {
            this.getFontRenderer().D(var24, var19, var33, 553648127);
         }

         if (var20 == var11.size()) {
            String var28 = var1.getDisplayName();
            if (var17) {
               drawRect(var19 - 2, var33 - this.getFontRenderer().FONT_HEIGHT - 1, var26, var33 - 1, 1610612736);
               drawRect(var19 - 2, var33 - 1, var26, var33, 1342177280);
               this.getFontRenderer()
                  .D(var28, var19 + var12 / 2 - this.getFontRenderer().getStringWidth(var28) / 2, var33 - this.getFontRenderer().FONT_HEIGHT, 553648127);
            }
         }
      }
   }

   private void renderPlayerStats(ScaledResolution var1) {
      long var2 = c ^ 34352600964681L;
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer var5 = (EntityPlayer)this.mc.getRenderViewEntity();
         int var6 = MathHelper.ceiling_float_int(var5.getHealth());
         boolean var7 = this.healthUpdateCounter > this.updateCounter && (this.healthUpdateCounter - this.updateCounter) / 3L % 2L == 1L;
         if (var6 < this.playerHealth && var5.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = this.updateCounter + 20;
         } else if (var6 > this.playerHealth && var5.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = this.updateCounter + 10;
         }

         if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
            this.playerHealth = var6;
            this.lastPlayerHealth = var6;
            this.lastSystemTime = Minecraft.getSystemTime();
         }

         this.playerHealth = var6;
         int var8 = this.lastPlayerHealth;
         this.rand.setSeed(this.updateCounter * 312871);
         boolean var9 = false;
         FoodStats var10 = var5.getFoodStats();
         int var11 = var10.getFoodLevel();
         int var12 = var10.getPrevFoodLevel();
         IAttributeInstance var13 = var5.getEntityAttribute(SharedMonsterAttributes.maxHealth);
         int var14 = var1.getScaledWidth() / 2 - 91;
         int var15 = var1.getScaledWidth() / 2 + 91;
         int var16 = var1.getScaledHeight() - 39;
         float var17 = (float)var13.getAttributeValue();
         long var10000 = 8396277908648840727L ^ var2;
         float var18 = var5.getAbsorptionAmount();
         int var19 = MathHelper.ceiling_float_int((var17 + var18) / 2.0F / 10.0F);
         int var20 = Math.max(10 - (var19 - 2), 3);
         int var21 = var16 - (var19 - 1) * var20 - 10;
         float var22 = var18;
         int var23 = var5.getTotalArmorValue();
         int var24 = -1;
         if (var5.isPotionActive(Potion.regeneration)) {
            var24 = this.updateCounter % MathHelper.ceiling_float_int(var17 + 5.0F);
         }

         this.mc.mcProfiler.startSection("armor");

         for (int var25 = 0; var25 < 10; var25++) {
            if (var23 > 0) {
               int var26 = var14 + var25 * 8;
               if (var25 * 2 + 1 < var23) {
                  this.drawTexturedModalRect(var26, var21, 34, 9, 9, 9);
               }

               if (var25 * 2 + 1 == var23) {
                  this.drawTexturedModalRect(var26, var21, 25, 9, 9, 9);
               }

               if (var25 * 2 + 1 > var23) {
                  this.drawTexturedModalRect(var26, var21, 16, 9, 9, 9);
               }
            }
         }

         this.mc.mcProfiler.endStartSection("health");

         for (int var37 = MathHelper.ceiling_float_int((var17 + var18) / 2.0F) - 1; var37 >= 0; var37--) {
            byte var39 = 16;
            if (var5.isPotionActive(Potion.poison)) {
               var39 += 36;
            } else if (var5.isPotionActive(Potion.wither)) {
               var39 += 72;
            }

            byte var27 = 0;
            if (var7) {
               var27 = 1;
            }

            int var28 = MathHelper.ceiling_float_int((var37 + 1) / 10.0F) - 1;
            int var29 = var14 + var37 % 10 * 8;
            int var30 = var16 - var28 * var20;
            if (var6 <= 4) {
               var30 += this.rand.nextInt(2);
            }

            if (var37 == var24) {
               var30 -= 2;
            }

            byte var31 = 0;
            if (var5.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
               var31 = 5;
            }

            this.drawTexturedModalRect(var29, var30, 16 + var27 * 9, 9 * var31, 9, 9);
            if (var7) {
               if (var37 * 2 + 1 < var8) {
                  this.drawTexturedModalRect(var29, var30, var39 + 54, 9 * var31, 9, 9);
               }

               if (var37 * 2 + 1 == var8) {
                  this.drawTexturedModalRect(var29, var30, var39 + 63, 9 * var31, 9, 9);
               }
            }

            if (var22 <= 0.0F) {
               if (var37 * 2 + 1 < var6) {
                  this.drawTexturedModalRect(var29, var30, var39 + 36, 9 * var31, 9, 9);
               }

               if (var37 * 2 + 1 == var6) {
                  this.drawTexturedModalRect(var29, var30, var39 + 45, 9 * var31, 9, 9);
               }
            } else {
               if (var22 == var18 && var18 % 2.0F == 1.0F) {
                  this.drawTexturedModalRect(var29, var30, var39 + 153, 9 * var31, 9, 9);
               } else {
                  this.drawTexturedModalRect(var29, var30, var39 + 144, 9 * var31, 9, 9);
               }

               var22 -= 2.0F;
            }
         }

         Entity var38 = var5.ridingEntity;
         if (var38 == null) {
            this.mc.mcProfiler.endStartSection("food");

            for (int var41 = 0; var41 < 10; var41++) {
               int var44 = var16;
               byte var47 = 16;
               byte var50 = 0;
               if (var5.isPotionActive(Potion.hunger)) {
                  var47 += 36;
                  var50 = 13;
               }

               if (var5.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (var11 * 3 + 1) == 0) {
                  var44 = var16 + (this.rand.nextInt(3) - 1);
               }

               if (var9) {
                  var50 = 1;
               }

               int var53 = var15 - var41 * 8 - 9;
               this.drawTexturedModalRect(var53, var44, 16 + var50 * 9, 27, 9, 9);
               if (var9) {
                  if (var41 * 2 + 1 < var12) {
                     this.drawTexturedModalRect(var53, var44, var47 + 54, 27, 9, 9);
                  }

                  if (var41 * 2 + 1 == var12) {
                     this.drawTexturedModalRect(var53, var44, var47 + 63, 27, 9, 9);
                  }
               }

               if (var41 * 2 + 1 < var11) {
                  this.drawTexturedModalRect(var53, var44, var47 + 36, 27, 9, 9);
               }

               if (var41 * 2 + 1 == var11) {
                  this.drawTexturedModalRect(var53, var44, var47 + 45, 27, 9, 9);
               }
            }
         } else if (var38 instanceof EntityLivingBase) {
            this.mc.mcProfiler.endStartSection("mountHealth");
            EntityLivingBase var40 = (EntityLivingBase)var38;
            int var43 = (int)Math.ceil(var40.getHealth());
            float var46 = var40.getMaxHealth();
            int var49 = (int)(var46 + 0.5F) / 2;
            if (var49 > 30) {
               var49 = 30;
            }

            int var52 = var16;

            for (int var54 = 0; var49 > 0; var54 += 20) {
               int var32 = Math.min(var49, 10);
               var49 -= var32;

               for (int var33 = 0; var33 < var32; var33++) {
                  byte var34 = 52;
                  byte var35 = 0;
                  if (var9) {
                     var35 = 1;
                  }

                  int var36 = var15 - var33 * 8 - 9;
                  this.drawTexturedModalRect(var36, var52, var34 + var35 * 9, 9, 9, 9);
                  if (var33 * 2 + 1 + var54 < var43) {
                     this.drawTexturedModalRect(var36, var52, var34 + 36, 9, 9, 9);
                  }

                  if (var33 * 2 + 1 + var54 == var43) {
                     this.drawTexturedModalRect(var36, var52, var34 + 45, 9, 9, 9);
                  }
               }

               var52 -= 10;
            }
         }

         this.mc.mcProfiler.endStartSection("air");
         if (var5.isInsideOfMaterial(Material.water)) {
            int var42 = this.mc.thePlayer.getAir();
            int var45 = MathHelper.ceiling_double_int((var42 - 2) * 10.0 / 300.0);
            int var48 = MathHelper.ceiling_double_int(var42 * 10.0 / 300.0) - var45;

            for (int var51 = 0; var51 < var45 + var48; var51++) {
               if (var51 < var45) {
                  this.drawTexturedModalRect(var15 - var51 * 8 - 9, var21, 16, 18, 9, 9);
               } else {
                  this.drawTexturedModalRect(var15 - var51 * 8 - 9, var21, 25, 18, 9, 9);
               }
            }
         }

         this.mc.mcProfiler.endSection();
      }
   }

   public static String V(String var0) {
      return StringUtils.stripControlCodes(var0).replaceAll("[^a-zA-Z0-9]", "");
   }

   private void l() {
      long var1 = c ^ 119610574004867L;
      if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
         BossStatus.statusBarTime--;
         FontRenderer var4 = this.mc.fontRendererObj;
         long var10000 = 5366603126143516077L ^ var1;
         ScaledResolution var5 = new ScaledResolution(this.mc);
         int var6 = var5.getScaledWidth();
         short var7 = 182;
         int var8 = var6 / 2 - var7 / 2;
         var10000 = 7187445831345286316L ^ var1;
         int var9 = (int)(BossStatus.healthScale * (var7 + 1));
         byte var10 = 12;
         this.drawTexturedModalRect(var8, var10, 0, 74, var7, 5);
         this.drawTexturedModalRect(var8, var10, 0, 74, var7, 5);
         if (var9 > 0) {
            this.drawTexturedModalRect(var8, var10, 0, 79, var9, 5);
         }

         String var11 = BossStatus.bossName;
         int var12 = 16777215;
         if (Config.isCustomColors()) {
            var12 = CustomColors.P(var12);
         }

         this.getFontRenderer().drawStringWithShadow(var11, var6 / 2 - this.getFontRenderer().getStringWidth(var11) / 2, var10 - 10, var12);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(icons);
      }
   }

   private void renderPumpkinOverlay(ScaledResolution var1) {
      RenderOverlaysEvent var5 = new RenderOverlaysEvent();
      Astolfo.INSTANCE.getEventBus().post(var5);
      if (!var5.isCancelled()) {
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableAlpha();
         this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
         Tessellator var6 = Tessellator.getInstance();
         WorldRenderer var7 = var6.getWorldRenderer();
         var7.begin(7, DefaultVertexFormats.POSITION_TEX);
         var7.pos(0.0, var1.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
         var7.pos(var1.getScaledWidth(), var1.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
         var7.pos(var1.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
         var7.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
         var6.draw();
         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
         GlStateManager.enableAlpha();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   private void renderVignette(float var1, ScaledResolution var2) {
      long var3 = c ^ 46409062513015L;
      if (!Config.isVignetteEnabled()) {
         GlStateManager.enableDepth();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      } else {
         var1 = 1.0F - var1;
         var1 = MathHelper.clamp_float(var1, 0.0F, 1.0F);
         WorldBorder var6 = this.mc.theWorld.getWorldBorder();
         float var7 = (float)var6.getClosestDistance(this.mc.thePlayer);
         double var8 = Math.min(var6.getResizeSpeed() * var6.getWarningTime() * 1000.0, Math.abs(var6.getTargetSize() - var6.getDiameter()));
         double var10 = Math.max(var6.getWarningDistance(), var8);
         if (var7 < var10) {
            var7 = 1.0F - (float)(var7 / var10);
         } else {
            var7 = 0.0F;
         }

         this.prevVignetteBrightness = (float)(this.prevVignetteBrightness + (var1 - this.prevVignetteBrightness) * 0.01);
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
         if (var7 > 0.0F) {
            GlStateManager.color(0.0F, var7, var7, 1.0F);
         } else {
            GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
         }

         this.mc.getTextureManager().bindTexture(vignetteTexPath);
         Tessellator var12 = Tessellator.getInstance();
         WorldRenderer var13 = var12.getWorldRenderer();
         var13.begin(7, DefaultVertexFormats.POSITION_TEX);
         var13.pos(0.0, var2.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
         long var10000 = 3199826269017843003L ^ var3;
         var13.pos(var2.getScaledWidth(), var2.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
         var13.pos(var2.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
         var13.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
         var12.draw();
         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      }
   }

   private void renderPortal(float var1, ScaledResolution var2) {
      long var3 = c ^ 116404718523973L;
      if (var1 < 1.0F) {
         var1 *= var1;
         var1 *= var1;
         var1 = var1 * 0.8F + 0.2F;
      }

      GlStateManager.disableAlpha();
      GlStateManager.disableDepth();
      long var10000 = 3199898666627172873L ^ var3;
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, var1);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      TextureAtlasSprite var6 = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
      float var7 = var6.getMinU();
      float var8 = var6.getMinV();
      float var9 = var6.getMaxU();
      float var10 = var6.getMaxV();
      Tessellator var11 = Tessellator.getInstance();
      WorldRenderer var12 = var11.getWorldRenderer();
      var12.begin(7, DefaultVertexFormats.POSITION_TEX);
      var12.pos(0.0, var2.getScaledHeight(), -90.0).tex(var7, var10).endVertex();
      var12.pos(var2.getScaledWidth(), var2.getScaledHeight(), -90.0).tex(var9, var10).endVertex();
      var12.pos(var2.getScaledWidth(), 0.0, -90.0).tex(var9, var8).endVertex();
      var12.pos(0.0, 0.0, -90.0).tex(var7, var8).endVertex();
      var11.draw();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderHotbarItem(int var1, int var2, int var3, float var4, EntityPlayer var5) {
      ItemStack var9 = var5.inventory.mainInventory[var1];
      if (var9 != null) {
         float var10 = var9.animationsToGo - var4;
         if (var10 > 0.0F) {
            GlStateManager.pushMatrix();
            float var11 = 1.0F + var10 / 5.0F;
            GlStateManager.translate(var2 + 8, var3 + 12, 0.0F);
            GlStateManager.scale(1.0F / var11, (var11 + 1.0F) / 2.0F, 1.0F);
            GlStateManager.translate(-(var2 + 8), -(var3 + 12), 0.0F);
         }

         this.itemRenderer.renderItemAndEffectIntoGUI(var9, var2, var3);
         if (var10 > 0.0F) {
            GlStateManager.popMatrix();
         }

         this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, var9, var2, var3);
      }
   }

   public void updateTick() {
      if (this.recordPlayingUpFor > 0) {
         this.recordPlayingUpFor--;
      }

      if (this.titlesTimer > 0) {
         this.titlesTimer--;
         if (this.titlesTimer <= 0) {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
         }
      }

      this.updateCounter++;
      this.streamIndicator.updateStreamAlpha();
      if (this.mc.thePlayer != null) {
         ItemStack var4 = this.mc.thePlayer.inventory.getCurrentItem();
         if (var4 == null) {
            this.remainingHighlightTicks = 0;
         } else if (this.highlightingItemStack != null
            && var4.getItem() == this.highlightingItemStack.getItem()
            && ItemStack.areItemStackTagsEqual(var4, this.highlightingItemStack)
            && (var4.isItemStackDamageable() || var4.getMetadata() == this.highlightingItemStack.getMetadata())) {
            if (this.remainingHighlightTicks > 0) {
               this.remainingHighlightTicks--;
            }
         } else {
            this.remainingHighlightTicks = 40;
         }

         this.highlightingItemStack = var4;
      }
   }

   public void setRecordPlayingMessage(String var1) {
      this.setRecordPlaying(I18n.format("record.nowPlaying", var1), true);
   }

   public void setRecordPlaying(String var1, boolean var2) {
      this.recordPlaying = var1;
      this.recordPlayingUpFor = 60;
      this.recordIsPlaying = var2;
   }

   public void displayTitle(String var1, String var2, int var3, int var4, int var5) {
      if (var1 == null && var2 == null && var3 < 0 && var4 < 0 && var5 < 0) {
         this.displayedTitle = "";
         this.displayedSubTitle = "";
         this.titlesTimer = 0;
      } else if (var1 != null) {
         this.displayedTitle = var1;
         this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
      } else if (var2 != null) {
         this.displayedSubTitle = var2;
      } else {
         if (var3 >= 0) {
            this.titleFadeIn = var3;
         }

         if (var4 >= 0) {
            this.titleDisplayTime = var4;
         }

         if (var5 >= 0) {
            this.titleFadeOut = var5;
         }

         if (this.titlesTimer > 0) {
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
         }
      }
   }

   public void setRecordPlaying(IChatComponent var1, boolean var2) {
      this.setRecordPlaying(var1.getUnformattedText(), var2);
   }

   public GuiNewChat getChatGUI() {
      return this.persistantChatGUI;
   }

   public void x(GuiNewChat var1) {
      this.persistantChatGUI = var1;
   }

   public int getUpdateCounter() {
      return this.updateCounter;
   }

   public FontRenderer getFontRenderer() {
      return this.mc.fontRendererObj;
   }

   public GuiSpectator getSpectatorGui() {
      return this.spectatorGui;
   }

   public GuiPlayerTabOverlay getTabList() {
      return this.overlayPlayerList;
   }

   public void o() {
      this.overlayPlayerList.resetFooterHeader();
   }
}
