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

   public GuiIngame(Minecraft mcIn) {
      this.mc = mcIn;
      this.itemRenderer = mcIn.getRenderItem();
      this.overlayDebug = new GuiOverlayDebug(mcIn);
      this.spectatorGui = new GuiSpectator(mcIn);
      this.persistantChatGUI = new CustomChatGui(mcIn);
      this.streamIndicator = new GuiStreamIndicator(mcIn);
      this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
      this.setDefaultTitlesTimes();
   }

   public void setDefaultTitlesTimes() {
      this.titleFadeIn = 10;
      this.titleDisplayTime = 70;
      this.titleFadeOut = 20;
   }

   public void renderGameOverlay(float partialTicks) {
      long m = c ^ 92627008101409L;
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      int i = scaledResolution.getScaledWidth();
      int j = scaledResolution.getScaledHeight();
      this.mc.entityRenderer.setupOverlayRendering();
      GlStateManager.enableBlend();
      if (Config.isVignetteEnabled()) {
         this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledResolution);
      } else {
         GlStateManager.enableDepth();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      }

      ItemStack itemStack = this.mc.thePlayer.inventory.armorItemInSlot(3);
      if (this.mc.gameSettings.thirdPersonView == 0 && itemStack != null && itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
         this.renderPumpkinOverlay(scaledResolution);
      }

      if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
         float f = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
         if (f > 0.0F) {
            this.renderPortal(f, scaledResolution);
         }
      }

      if (this.mc.playerController.W()) {
         this.spectatorGui.renderTooltip(scaledResolution, partialTicks);
      } else {
         this.renderTooltip(scaledResolution, partialTicks);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
      GlStateManager.enableBlend();
      if (this.showCrosshair() && this.mc.gameSettings.thirdPersonView < 1) {
         GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
         GlStateManager.enableAlpha();
         this.drawTexturedModalRect(i / 2 - 7, j / 2 - 7, 0, 0, 16, 16);
      }

      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      this.mc.mcProfiler.startSection("bossHealth");
      this.l();
      this.mc.mcProfiler.endSection();
      if (this.mc.playerController.shouldDrawHUD()) {
         this.renderPlayerStats(scaledResolution);
      }

      GlStateManager.disableBlend();
      if (this.mc.thePlayer.c() > 0) {
         this.mc.mcProfiler.startSection("sleep");
         GlStateManager.disableDepth();
         GlStateManager.disableAlpha();
         int k = this.mc.thePlayer.c();
         float g = k / 100.0F;
         if (g > 1.0F) {
            g = 1.0F - (k - 100) / 10.0F;
         }

         int i2 = (int)(220.0F * g) << 24 | 1052704;
         drawRect(0, 0, i, j, i2);
         GlStateManager.enableAlpha();
         GlStateManager.enableDepth();
         this.mc.mcProfiler.endSection();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int i3 = i / 2 - 91;
      if (this.mc.thePlayer.Y8()) {
         this.renderHorseJumpBar(scaledResolution, i3);
      } else if (this.mc.playerController.D()) {
         this.renderExpBar(scaledResolution, i3);
      }

      if (this.mc.gameSettings.Z9 && !this.mc.playerController.W()) {
         this.renderSelectedItem(scaledResolution);
      } else if (this.mc.thePlayer.isSpectator()) {
         this.spectatorGui.renderSelectedItem(scaledResolution);
      }

      if (this.mc.isDemo()) {
         this.renderDemo(scaledResolution);
      }

      if (this.mc.gameSettings.n) {
         this.overlayDebug.renderDebugInfo(scaledResolution);
      }

      if (this.recordPlayingUpFor > 0) {
         this.mc.mcProfiler.startSection("overlayMessage");
         float h = this.recordPlayingUpFor - partialTicks;
         int i4 = (int)(h * 255.0F / 20.0F);
         if (i4 > 255) {
            i4 = 255;
         }

         if (i4 > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(i / 2, j - 68, 0.0F);
            GlStateManager.enableBlend();
            long l2 = 3199929593377112173L ^ m;
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            int i5 = 16777215;
            if (this.recordIsPlaying) {
               i5 = MathHelper.hsvToRGB(h / 50.0F, 0.7F, 0.6F) & 16777215;
            }

            this.getFontRenderer()
               .D(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4.0F, i5 + (i4 << 24 & 0xFF000000));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }

         this.mc.mcProfiler.endSection();
      }

      if (this.titlesTimer > 0 && (!ModuleManager.S.isEnabled() || !TitleAnnouncer.d.hasElapsed(4500.0F))) {
         String string = this.mc.mouseHelper.Y().getAccount().getUsername().toLowerCase();
         if (string.equals("belle")
            || string.equals("triangle")
            || string.equals("god")
            || string.equals("idonthaveamic")
            || string.equals("notc")
            || string.equals("ffxfusion")
            || string.equals("pure")
            || string.equals("royalty")
            || string.equals("idle")) {
            this.displayedSubTitle = this.displayedSubTitle.replace("last man standing", "last §dsissy §7standing");
         }

         this.mc.mcProfiler.startSection("titleAndSubtitle");
         float f2 = this.titlesTimer - partialTicks;
         int i6 = 255;
         if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
            float f3 = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut - f2;
            i6 = (int)(f3 * 255.0F / this.titleFadeIn);
         }

         if (this.titlesTimer <= this.titleFadeOut) {
            i6 = (int)(f2 * 255.0F / this.titleFadeOut);
         }

         i6 = MathHelper.clamp_int(i6, 0, 255);
         if (i6 > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(i / 2, j / 2, 0.0F);
            long l3 = 3199929593377112173L ^ m;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 4.0F);
            int i7 = i6 << 24 & 0xFF000000;
            this.getFontRenderer()
               .drawString(this.displayedTitle, -this.getFontRenderer().getStringWidth(this.displayedTitle) / 2, -10.0F, 16777215 | i7, true);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            this.getFontRenderer()
               .drawString(this.displayedSubTitle, -this.getFontRenderer().getStringWidth(this.displayedSubTitle) / 2, 5.0F, 16777215 | i7, true);
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }

         this.mc.mcProfiler.endSection();
      }

      Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
      ScoreObjective scoreObjective = null;
      ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());
      if (scorePlayerTeam != null && scorePlayerTeam.getChatFormat() != null) {
         int i8 = scorePlayerTeam.getChatFormat().getColorIndex();
         if (i8 >= 0) {
            scoreObjective = scoreboard.getObjectiveInDisplaySlot(3 + i8);
         }
      }

      ScoreObjective scoreObjective2 = scoreObjective != null ? scoreObjective : scoreboard.getObjectiveInDisplaySlot(1);
      if (scoreObjective2 != null) {
         this.renderScoreboard(scoreObjective2, scaledResolution);
      }

      Astolfo.INSTANCE.getEventBus().post(new RenderEvent(RenderType.SCREEN, partialTicks, scaledResolution, this));
      GlStateManager.enableBlend();
      long l4 = 3199929593377112173L ^ m;
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.disableAlpha();
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, j - 48, 0.0F);
      this.mc.mcProfiler.startSection("chat");
      this.persistantChatGUI.drawChat(this.updateCounter);
      this.mc.mcProfiler.endSection();
      GlStateManager.popMatrix();
      scoreObjective2 = scoreboard.getObjectiveInDisplaySlot(0);
      if (this.mc.gameSettings.ZW.isKeyDown()
         && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() > 1 || scoreObjective2 != null)) {
         this.overlayPlayerList.updatePlayerList(true);
         this.overlayPlayerList.renderPlayerlist(i, scoreboard, scoreObjective2);
      } else {
         this.overlayPlayerList.updatePlayerList(false);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      GlStateManager.enableAlpha();
   }

   public void renderTooltip(ScaledResolution sr, float partialTicks) {
      long l = c ^ 126356357983019L;
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(widgetsTexPath);
         EntityPlayer entityPlayer = (EntityPlayer)this.mc.getRenderViewEntity();
         int i = sr.getScaledWidth() / 2;
         float f = this.zLevel;
         this.zLevel = -90.0F;
         this.drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
         RenderHotbarEvent renderHotbarEvent = new RenderHotbarEvent(entityPlayer.inventory.currentItem);
         Astolfo.INSTANCE.getEventBus().post(renderHotbarEvent);
         this.drawTexturedModalRect(i - 91 - 1 + renderHotbarEvent.getSlot() * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
         this.zLevel = f;
         long m = 3199888723445544807L ^ l;
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         RenderHelper.enableGUIStandardItemLighting();

         for (int j = 0; j < 9; j++) {
            int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
            int n = sr.getScaledHeight() - 16 - 3;
            this.renderHotbarItem(j, k, n, partialTicks, entityPlayer);
         }

         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
      }
   }

   public void renderHorseJumpBar(ScaledResolution scaledRes, int x) {
      this.mc.mcProfiler.startSection("jumpBar");
      this.mc.getTextureManager().bindTexture(Gui.icons);
      float f = this.mc.thePlayer.getHorseJumpPower();
      short s = 182;
      int i = (int)(f * (s + 1));
      int j = scaledRes.getScaledHeight() - 32 + 3;
      this.drawTexturedModalRect(x, j, 0, 84, s, 5);
      if (i > 0) {
         this.drawTexturedModalRect(x, j, 0, 89, i, 5);
      }

      this.mc.mcProfiler.endSection();
   }

   public void renderExpBar(ScaledResolution scaledRes, int x) {
      this.mc.mcProfiler.startSection("expBar");
      this.mc.getTextureManager().bindTexture(Gui.icons);
      int i = this.mc.thePlayer.xpBarCap();
      if (i > 0) {
         short s = 182;
         int j = (int)(this.mc.thePlayer.experience * (s + 1));
         int l = scaledRes.getScaledHeight() - 32 + 3;
         this.drawTexturedModalRect(x, l, 0, 64, s, 5);
         if (j > 0) {
            this.drawTexturedModalRect(x, l, 0, 69, j, 5);
         }
      }

      this.mc.mcProfiler.endSection();
      if (this.mc.thePlayer.experienceLevel > 0) {
         this.mc.mcProfiler.startSection("expLevel");
         int m = 8453920;
         if (Config.isCustomColors()) {
            m = CustomColors.k(m);
         }

         String string = "" + this.mc.thePlayer.experienceLevel;
         int n = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(string)) / 2;
         int i2 = scaledRes.getScaledHeight() - 31 - 4;
         this.getFontRenderer().D(string, n + 1, i2, 0);
         this.getFontRenderer().D(string, n - 1, i2, 0);
         this.getFontRenderer().D(string, n, i2 + 1, 0);
         this.getFontRenderer().D(string, n, i2 - 1, 0);
         this.getFontRenderer().D(string, n, i2, m);
         this.mc.mcProfiler.endSection();
      }
   }

   public void renderSelectedItem(ScaledResolution scaledRes) {
      long l = c ^ 77693665576219L;
      this.mc.mcProfiler.startSection("selectedItemName");
      if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
         String string = this.highlightingItemStack.getDisplayName();
         if (this.highlightingItemStack.hasDisplayName()) {
            string = EnumChatFormatting.ITALIC + string;
         }

         int i = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth((java.lang.String)(string))) / 2;
         int j = scaledRes.getScaledHeight() - 59;
         if (!this.mc.playerController.shouldDrawHUD()) {
            j += 14;
         }

         int k = (int)(this.remainingHighlightTicks * 256.0F / 10.0F);
         if (k > 255) {
            k = 255;
         }

         if (k > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            long m = 3199945617627175255L ^ l;
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            this.getFontRenderer().drawStringWithShadow((java.lang.String)(string), i, j, 16777215 + (k << 24));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }

      this.mc.mcProfiler.endSection();
   }

   public void renderDemo(ScaledResolution scaledRes) {
      this.mc.mcProfiler.startSection("demo");
      String string = "";
      if (this.mc.theWorld.getTotalWorldTime() >= 120500L) {
         string = I18n.format("demo.demoExpired");
      } else {
         string = I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime())));
      }

      int i = this.getFontRenderer().getStringWidth(string);
      this.getFontRenderer().drawStringWithShadow(string, scaledRes.getScaledWidth() - i - 10, 5.0F, 16777215);
      this.mc.mcProfiler.endSection();
   }

   public boolean showCrosshair() {
      long l = c ^ 17572052907717L;
      long m = l ^ 102811327526526L;
      if ((!this.mc.gameSettings.n || this.mc.thePlayer.w() || this.mc.gameSettings.reducedDebugInfo)
         && !Astolfo.INSTANCE.getModuleManager().get(Crosshair.class).isEnabled()) {
         if (!this.mc.playerController.W()) {
            return true;
         }

         if (this.mc.pointedEntity != null) {
            return true;
         }

         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
            if (this.mc.theWorld.getTileEntity(blockPos) instanceof IInventory) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void renderStreamIndicator(ScaledResolution scaledRes) {
      this.streamIndicator.render(scaledRes.getScaledWidth() - 10, 10);
   }

   private void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes) {
      long l = c ^ 68540950107578L;
      long m = l ^ 118938409407745L;
      Scoreboard scoreboard = objective.getScoreboard();
      Collection collection = scoreboard.getSortedScores(objective);
      ArrayList arrayList = Lists.newArrayList(Iterables.filter(collection, new Predicate<Score>() {
         private static final long a = 130210547240308L;

         public boolean apply(Score score) {
            return score.getPlayerName() != null && !score.getPlayerName().startsWith("#");
         }
      }));
      ArrayList arrayList2;
      if (arrayList.size() > 15) {
         arrayList2 = Lists.newArrayList(Iterables.skip(arrayList, collection.size() - 15));
      } else {
         arrayList2 = arrayList;
      }

      int i = this.getFontRenderer().getStringWidth(objective.getDisplayName());

      for (Object object : arrayList2) {
         ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(((Score)object).getPlayerName());
         String string = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, ((Score)object).getPlayerName())
            + ": "
            + EnumChatFormatting.RED
            + ((Score)object).getScorePoints();
         i = Math.max(i, this.getFontRenderer().getStringWidth(string));
      }

      bypass.module.visuals.Scoreboard scoreboard2 = Astolfo.INSTANCE.getModuleManager().get(bypass.module.visuals.Scoreboard.class);
      StreamerMode streamerMode = (StreamerMode)ModuleManager.G;
      int j = arrayList2.size() * this.getFontRenderer().FONT_HEIGHT;
      int n = scaledRes.getScaledHeight() / 2 + j / 3;
      if (scoreboard2.isEnabled()) {
         n = (int)(n + bypass.module.visuals.Scoreboard.position.getValue());
      }

      boolean bl = !scoreboard2.isEnabled() || !bypass.module.visuals.Scoreboard.remove.getValue();
      byte b2 = 3;
      int i2 = scaledRes.getScaledWidth() - i - b2;
      int i3 = 0;
      b = false;
      E = false;

      for (Object object2 : arrayList2) {
         i3++;
         ScorePlayerTeam scorePlayerTeam2 = scoreboard.getPlayersTeam(((Score)object2).getPlayerName());
         String string2 = ScorePlayerTeam.formatPlayerName(scorePlayerTeam2, ((Score)object2).getPlayerName());
         if (i3 == arrayList2.size() && string2.contains("  §8")) {
            String string3 = V(string2);
            if (!V(this.w).equals(string3)) {
               A = true;
            }

            this.w = string3;
            if (streamerMode.isEnabled()) {
               string2 = string2.replaceAll("§8", "§8§k");
            }
         }

         int i4 = n - i3 * this.getFontRenderer().FONT_HEIGHT;
         int i5 = scaledRes.getScaledWidth() - b2 + 2;
         if (bl) {
            drawRect(i2 - 2, i4, i5, i4 + this.getFontRenderer().FONT_HEIGHT, 1342177280);
         }

         String string4 = StringUtils.stripControlCodes(string2);
         if (string4.contains("SPIRE")) {
            E = true;
         }

         if (string4.contains("Mode: Ranked")) {
            b = true;
         }

         if (bl) {
            this.getFontRenderer().D(string2, i2, i4, 553648127);
         }

         if (i3 == arrayList2.size()) {
            String string5 = objective.getDisplayName();
            if (bl) {
               drawRect(i2 - 2, i4 - this.getFontRenderer().FONT_HEIGHT - 1, i5, i4 - 1, 1610612736);
               drawRect(i2 - 2, i4 - 1, i5, i4, 1342177280);
               this.getFontRenderer()
                  .D(string5, i2 + i / 2 - this.getFontRenderer().getStringWidth(string5) / 2, i4 - this.getFontRenderer().FONT_HEIGHT, 553648127);
            }
         }
      }
   }

   private void renderPlayerStats(ScaledResolution scaledRes) {
      long l = c ^ 34352600964681L;
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer entityPlayer = (EntityPlayer)this.mc.getRenderViewEntity();
         int i = MathHelper.ceiling_float_int(entityPlayer.getHealth());
         boolean bl = this.healthUpdateCounter > this.updateCounter && (this.healthUpdateCounter - this.updateCounter) / 3L % 2L == 1L;
         if (i < this.playerHealth && entityPlayer.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = this.updateCounter + 20;
         } else if (i > this.playerHealth && entityPlayer.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = this.updateCounter + 10;
         }

         if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
            this.playerHealth = i;
            this.lastPlayerHealth = i;
            this.lastSystemTime = Minecraft.getSystemTime();
         }

         this.playerHealth = i;
         int j = this.lastPlayerHealth;
         this.rand.setSeed(this.updateCounter * 312871);
         boolean bl2 = false;
         FoodStats foodStats = entityPlayer.getFoodStats();
         int k = foodStats.getFoodLevel();
         int m = foodStats.getPrevFoodLevel();
         IAttributeInstance iAttributeInstance = entityPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
         int n = scaledRes.getScaledWidth() / 2 - 91;
         int i2 = scaledRes.getScaledWidth() / 2 + 91;
         int i3 = scaledRes.getScaledHeight() - 39;
         float f = (float)iAttributeInstance.getAttributeValue();
         long l2 = 8396277908648840727L ^ l;
         float g = entityPlayer.getAbsorptionAmount();
         int i4 = MathHelper.ceiling_float_int((f + g) / 2.0F / 10.0F);
         int i5 = Math.max(10 - (i4 - 2), 3);
         int i6 = i3 - (i4 - 1) * i5 - 10;
         float h = g;
         int i7 = entityPlayer.getTotalArmorValue();
         int i8 = -1;
         if (entityPlayer.isPotionActive(Potion.regeneration)) {
            i8 = this.updateCounter % MathHelper.ceiling_float_int(f + 5.0F);
         }

         this.mc.mcProfiler.startSection("armor");

         for (int i9 = 0; i9 < 10; i9++) {
            if (i7 > 0) {
               int i10 = n + i9 * 8;
               if (i9 * 2 + 1 < i7) {
                  this.drawTexturedModalRect(i10, i6, 34, 9, 9, 9);
               }

               if (i9 * 2 + 1 == i7) {
                  this.drawTexturedModalRect(i10, i6, 25, 9, 9, 9);
               }

               if (i9 * 2 + 1 > i7) {
                  this.drawTexturedModalRect(i10, i6, 16, 9, 9, 9);
               }
            }
         }

         this.mc.mcProfiler.endStartSection("health");

         for (int i11 = MathHelper.ceiling_float_int((f + g) / 2.0F) - 1; i11 >= 0; i11--) {
            byte b = 16;
            if (entityPlayer.isPotionActive(Potion.poison)) {
               b += 36;
            } else if (entityPlayer.isPotionActive(Potion.wither)) {
               b += 72;
            }

            byte b2 = 0;
            if (bl) {
               b2 = 1;
            }

            int i12 = MathHelper.ceiling_float_int((i11 + 1) / 10.0F) - 1;
            int i13 = n + i11 % 10 * 8;
            int i14 = i3 - i12 * i5;
            if (i <= 4) {
               i14 += this.rand.nextInt(2);
            }

            if (i11 == i8) {
               i14 -= 2;
            }

            byte b3 = 0;
            if (entityPlayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
               b3 = 5;
            }

            this.drawTexturedModalRect(i13, i14, 16 + b2 * 9, 9 * b3, 9, 9);
            if (bl) {
               if (i11 * 2 + 1 < j) {
                  this.drawTexturedModalRect(i13, i14, b + 54, 9 * b3, 9, 9);
               }

               if (i11 * 2 + 1 == j) {
                  this.drawTexturedModalRect(i13, i14, b + 63, 9 * b3, 9, 9);
               }
            }

            if (h <= 0.0F) {
               if (i11 * 2 + 1 < i) {
                  this.drawTexturedModalRect(i13, i14, b + 36, 9 * b3, 9, 9);
               }

               if (i11 * 2 + 1 == i) {
                  this.drawTexturedModalRect(i13, i14, b + 45, 9 * b3, 9, 9);
               }
            } else {
               if (h == g && g % 2.0F == 1.0F) {
                  this.drawTexturedModalRect(i13, i14, b + 153, 9 * b3, 9, 9);
               } else {
                  this.drawTexturedModalRect(i13, i14, b + 144, 9 * b3, 9, 9);
               }

               h -= 2.0F;
            }
         }

         Entity entity = entityPlayer.ridingEntity;
         if (entity == null) {
            this.mc.mcProfiler.endStartSection("food");

            for (int i15 = 0; i15 < 10; i15++) {
               int i16 = i3;
               byte b4 = 16;
               byte b5 = 0;
               if (entityPlayer.isPotionActive(Potion.hunger)) {
                  b4 += 36;
                  b5 = 13;
               }

               if (entityPlayer.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (k * 3 + 1) == 0) {
                  i16 = i3 + (this.rand.nextInt(3) - 1);
               }

               if (bl2) {
                  b5 = 1;
               }

               int i17 = i2 - i15 * 8 - 9;
               this.drawTexturedModalRect(i17, i16, 16 + b5 * 9, 27, 9, 9);
               if (bl2) {
                  if (i15 * 2 + 1 < m) {
                     this.drawTexturedModalRect(i17, i16, b4 + 54, 27, 9, 9);
                  }

                  if (i15 * 2 + 1 == m) {
                     this.drawTexturedModalRect(i17, i16, b4 + 63, 27, 9, 9);
                  }
               }

               if (i15 * 2 + 1 < k) {
                  this.drawTexturedModalRect(i17, i16, b4 + 36, 27, 9, 9);
               }

               if (i15 * 2 + 1 == k) {
                  this.drawTexturedModalRect(i17, i16, b4 + 45, 27, 9, 9);
               }
            }
         } else if (entity instanceof EntityLivingBase) {
            this.mc.mcProfiler.endStartSection("mountHealth");
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            int i18 = (int)Math.ceil(entityLivingBase.getHealth());
            float f2 = entityLivingBase.getMaxHealth();
            int i19 = (int)(f2 + 0.5F) / 2;
            if (i19 > 30) {
               i19 = 30;
            }

            int i20 = i3;

            for (int i21 = 0; i19 > 0; i21 += 20) {
               int i22 = Math.min(i19, 10);
               i19 -= i22;

               for (int i23 = 0; i23 < i22; i23++) {
                  byte b6 = 52;
                  byte b7 = 0;
                  if (bl2) {
                     b7 = 1;
                  }

                  int i24 = i2 - i23 * 8 - 9;
                  this.drawTexturedModalRect(i24, i20, b6 + b7 * 9, 9, 9, 9);
                  if (i23 * 2 + 1 + i21 < i18) {
                     this.drawTexturedModalRect(i24, i20, b6 + 36, 9, 9, 9);
                  }

                  if (i23 * 2 + 1 + i21 == i18) {
                     this.drawTexturedModalRect(i24, i20, b6 + 45, 9, 9, 9);
                  }
               }

               i20 -= 10;
            }
         }

         this.mc.mcProfiler.endStartSection("air");
         if (entityPlayer.isInsideOfMaterial(Material.water)) {
            int i25 = this.mc.thePlayer.getAir();
            int i26 = MathHelper.ceiling_double_int((i25 - 2) * 10.0 / 300.0);
            int i27 = MathHelper.ceiling_double_int(i25 * 10.0 / 300.0) - i26;

            for (int i28 = 0; i28 < i26 + i27; i28++) {
               if (i28 < i26) {
                  this.drawTexturedModalRect(i2 - i28 * 8 - 9, i6, 16, 18, 9, 9);
               } else {
                  this.drawTexturedModalRect(i2 - i28 * 8 - 9, i6, 25, 18, 9, 9);
               }
            }
         }

         this.mc.mcProfiler.endSection();
      }
   }

   public static String V(String string) {
      return StringUtils.stripControlCodes(string).replaceAll("[^a-zA-Z0-9]", "");
   }

   private void l() {
      long m = c ^ 119610574004867L;
      if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
         BossStatus.statusBarTime--;
         FontRenderer fontRenderer = this.mc.fontRendererObj;
         long n = 5366603126143516077L ^ m;
         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         int i = scaledResolution.getScaledWidth();
         short s = 182;
         int j = i / 2 - s / 2;
         n = 7187445831345286316L ^ m;
         int k = (int)(BossStatus.healthScale * (s + 1));
         byte b = 12;
         this.drawTexturedModalRect(j, b, 0, 74, s, 5);
         this.drawTexturedModalRect(j, b, 0, 74, s, 5);
         if (k > 0) {
            this.drawTexturedModalRect(j, b, 0, 79, k, 5);
         }

         String string = BossStatus.bossName;
         int i2 = 16777215;
         if (Config.isCustomColors()) {
            i2 = CustomColors.P(i2);
         }

         this.getFontRenderer().drawStringWithShadow(string, i / 2 - this.getFontRenderer().getStringWidth(string) / 2, b - 10, i2);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(icons);
      }
   }

   private void renderPumpkinOverlay(ScaledResolution scaledRes) {
      RenderOverlaysEvent renderOverlaysEvent = new RenderOverlaysEvent();
      Astolfo.INSTANCE.getEventBus().post(renderOverlaysEvent);
      if (!renderOverlaysEvent.isCancelled()) {
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableAlpha();
         this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
         worldRenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
         worldRenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
         worldRenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
         tessellator.draw();
         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
         GlStateManager.enableAlpha();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   private void renderVignette(float lightLevel, ScaledResolution scaledRes) {
      long l = c ^ 46409062513015L;
      if (!Config.isVignetteEnabled()) {
         GlStateManager.enableDepth();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      } else {
         lightLevel = 1.0F - lightLevel;
         lightLevel = MathHelper.clamp_float(lightLevel, 0.0F, 1.0F);
         WorldBorder worldBorder = this.mc.theWorld.getWorldBorder();
         float f = (float)worldBorder.getClosestDistance(this.mc.thePlayer);
         double d = Math.min(worldBorder.getResizeSpeed() * worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getDiameter()));
         double e = Math.max(worldBorder.getWarningDistance(), d);
         if (f < e) {
            f = 1.0F - (float)(f / e);
         } else {
            f = 0.0F;
         }

         this.prevVignetteBrightness = (float)(this.prevVignetteBrightness + (lightLevel - this.prevVignetteBrightness) * 0.01);
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
         if (f > 0.0F) {
            GlStateManager.color(0.0F, f, f, 1.0F);
         } else {
            GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
         }

         this.mc.getTextureManager().bindTexture(vignetteTexPath);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
         long m = 3199826269017843003L ^ l;
         worldRenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
         worldRenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
         worldRenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
         tessellator.draw();
         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      }
   }

   private void renderPortal(float timeInPortal, ScaledResolution scaledRes) {
      long l = c ^ 116404718523973L;
      if (timeInPortal < 1.0F) {
         timeInPortal *= timeInPortal;
         timeInPortal *= timeInPortal;
         timeInPortal = timeInPortal * 0.8F + 0.2F;
      }

      GlStateManager.disableAlpha();
      GlStateManager.disableDepth();
      long m = 3199898666627172873L ^ l;
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, timeInPortal);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      TextureAtlasSprite textureAtlasSprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
      float f = textureAtlasSprite.getMinU();
      float g = textureAtlasSprite.getMinV();
      float h = textureAtlasSprite.getMaxU();
      float f2 = textureAtlasSprite.getMaxV();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(f, f2).endVertex();
      worldRenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(h, f2).endVertex();
      worldRenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(h, g).endVertex();
      worldRenderer.pos(0.0, 0.0, -90.0).tex(f, g).endVertex();
      tessellator.draw();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player) {
      ItemStack itemStack = player.inventory.mainInventory[index];
      if (itemStack != null) {
         float f = itemStack.animationsToGo - partialTicks;
         if (f > 0.0F) {
            GlStateManager.pushMatrix();
            float g = 1.0F + f / 5.0F;
            GlStateManager.translate(xPos + 8, yPos + 12, 0.0F);
            GlStateManager.scale(1.0F / g, (g + 1.0F) / 2.0F, 1.0F);
            GlStateManager.translate(-(xPos + 8), -(yPos + 12), 0.0F);
         }

         this.itemRenderer.renderItemAndEffectIntoGUI(itemStack, xPos, yPos);
         if (f > 0.0F) {
            GlStateManager.popMatrix();
         }

         this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, itemStack, xPos, yPos);
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
         ItemStack itemStack = this.mc.thePlayer.inventory.getCurrentItem();
         if (itemStack == null) {
            this.remainingHighlightTicks = 0;
         } else if (this.highlightingItemStack != null
            && itemStack.getItem() == this.highlightingItemStack.getItem()
            && ItemStack.areItemStackTagsEqual(itemStack, this.highlightingItemStack)
            && (itemStack.isItemStackDamageable() || itemStack.getMetadata() == this.highlightingItemStack.getMetadata())) {
            if (this.remainingHighlightTicks > 0) {
               this.remainingHighlightTicks--;
            }
         } else {
            this.remainingHighlightTicks = 40;
         }

         this.highlightingItemStack = itemStack;
      }
   }

   public void setRecordPlayingMessage(String recordName) {
      this.setRecordPlaying(I18n.format("record.nowPlaying", recordName), true);
   }

   public void setRecordPlaying(String message, boolean isPlaying) {
      this.recordPlaying = message;
      this.recordPlayingUpFor = 60;
      this.recordIsPlaying = isPlaying;
   }

   public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut) {
      if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0) {
         this.displayedTitle = "";
         this.displayedSubTitle = "";
         this.titlesTimer = 0;
      } else if (title != null) {
         this.displayedTitle = title;
         this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
      } else if (subTitle != null) {
         this.displayedSubTitle = subTitle;
      } else {
         if (timeFadeIn >= 0) {
            this.titleFadeIn = timeFadeIn;
         }

         if (displayTime >= 0) {
            this.titleDisplayTime = displayTime;
         }

         if (timeFadeOut >= 0) {
            this.titleFadeOut = timeFadeOut;
         }

         if (this.titlesTimer > 0) {
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
         }
      }
   }

   public void setRecordPlaying(IChatComponent component, boolean isPlaying) {
      this.setRecordPlaying(component.getUnformattedText(), isPlaying);
   }

   public GuiNewChat getChatGUI() {
      return this.persistantChatGUI;
   }

   public void x(GuiNewChat guiNewChat) {
      this.persistantChatGUI = guiNewChat;
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
