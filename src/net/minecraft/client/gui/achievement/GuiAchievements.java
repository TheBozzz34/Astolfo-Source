package net.minecraft.client.gui.achievement;

import java.io.IOException;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiAchievements extends GuiScreen implements IProgressMeter {
   private static final int field_146572_y = AchievementList.minDisplayColumn * 24 - 112;
   private static final int field_146571_z = AchievementList.minDisplayRow * 24 - 112;
   private static final int field_146559_A = AchievementList.maxDisplayColumn * 24 - 77;
   private static final int field_146560_B = AchievementList.maxDisplayRow * 24 - 77;
   private static final ResourceLocation ACHIEVEMENT_BACKGROUND = new ResourceLocation("textures/gui/achievement/achievement_background.png");
   public GuiScreen parentScreen;
   public int field_146555_f = 256;
   public int field_146557_g = 202;
   public int field_146563_h;
   public int field_146564_i;
   public float field_146570_r = 1.0F;
   public double field_146569_s;
   public double field_146568_t;
   public double field_146567_u;
   public double field_146566_v;
   public double field_146565_w;
   public double field_146573_x;
   private int field_146554_D;
   private StatFileWriter statFileWriter;
   private boolean loadingAchievements = true;

   public GuiAchievements(GuiScreen parentScreenIn, StatFileWriter statFileWriterIn) {
      this.parentScreen = parentScreenIn;
      this.statFileWriter = statFileWriterIn;
      short s = 141;
      short s2 = 141;
      this.field_146569_s = this.field_146567_u = this.field_146565_w = AchievementList.openInventory.displayColumn * 24 - s / 2 - 12;
      this.field_146568_t = this.field_146566_v = this.field_146573_x = AchievementList.openInventory.displayRow * 24 - s2 / 2;
   }

   @Override
   public void initGui() {
      this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
      this.buttonList.clear();
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (!this.loadingAchievements && button.id == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      } else {
         super.keyTyped(typedChar, keyCode);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.loadingAchievements) {
         this.drawDefaultBackground();
         this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
         this.drawCenteredString(
            this.fontRendererObj,
            lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % lanSearchStates.length)],
            this.width / 2,
            this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2,
            16777215
         );
      } else {
         if (Mouse.isButtonDown(0)) {
            int i = (this.width - this.field_146555_f) / 2;
            int j = (this.height - this.field_146557_g) / 2;
            int k = i + 8;
            int l = j + 17;
            if ((this.field_146554_D == 0 || this.field_146554_D == 1) && mouseX >= k && mouseX < k + 224 && mouseY >= l && mouseY < l + 155) {
               if (this.field_146554_D == 0) {
                  this.field_146554_D = 1;
               } else {
                  this.field_146567_u = this.field_146567_u - (mouseX - this.field_146563_h) * this.field_146570_r;
                  this.field_146566_v = this.field_146566_v - (mouseY - this.field_146564_i) * this.field_146570_r;
                  this.field_146565_w = this.field_146569_s = this.field_146567_u;
                  this.field_146573_x = this.field_146568_t = this.field_146566_v;
               }

               this.field_146563_h = mouseX;
               this.field_146564_i = mouseY;
            }
         } else {
            this.field_146554_D = 0;
         }

         int m = Mouse.getDWheel();
         float f = this.field_146570_r;
         if (m < 0) {
            this.field_146570_r += 0.25F;
         } else if (m > 0) {
            this.field_146570_r -= 0.25F;
         }

         this.field_146570_r = MathHelper.clamp_float(this.field_146570_r, 1.0F, 2.0F);
         if (this.field_146570_r != f) {
            float g = f - this.field_146570_r;
            float h = f * this.field_146555_f;
            float f2 = f * this.field_146557_g;
            float f3 = this.field_146570_r * this.field_146555_f;
            float f4 = this.field_146570_r * this.field_146557_g;
            this.field_146567_u -= (f3 - h) * 0.5F;
            this.field_146566_v -= (f4 - f2) * 0.5F;
            this.field_146565_w = this.field_146569_s = this.field_146567_u;
            this.field_146573_x = this.field_146568_t = this.field_146566_v;
         }

         if (this.field_146565_w < field_146572_y) {
            this.field_146565_w = field_146572_y;
         }

         if (this.field_146573_x < field_146571_z) {
            this.field_146573_x = field_146571_z;
         }

         if (this.field_146565_w >= field_146559_A) {
            this.field_146565_w = field_146559_A - 1;
         }

         if (this.field_146573_x >= field_146560_B) {
            this.field_146573_x = field_146560_B - 1;
         }

         this.drawDefaultBackground();
         this.drawAchievementScreen(mouseX, mouseY, partialTicks);
         GlStateManager.disableLighting();
         GlStateManager.disableDepth();
         this.drawTitle();
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
      }
   }

   @Override
   public void doneLoading() {
      if (this.loadingAchievements) {
         this.loadingAchievements = false;
      }
   }

   @Override
   public void updateScreen() {
      if (!this.loadingAchievements) {
         this.field_146569_s = this.field_146567_u;
         this.field_146568_t = this.field_146566_v;
         double d = this.field_146565_w - this.field_146567_u;
         double e = this.field_146573_x - this.field_146566_v;
         if (d * d + e * e < 4.0) {
            this.field_146567_u += d;
            this.field_146566_v += e;
         } else {
            this.field_146567_u += d * 0.85;
            this.field_146566_v += e * 0.85;
         }
      }
   }

   public void drawTitle() {
      int i = (this.width - this.field_146555_f) / 2;
      int j = (this.height - this.field_146557_g) / 2;
      this.fontRendererObj.drawStringNoShadow(I18n.format("gui.achievements"), i + 15, j + 5, 4210752);
   }

   public void drawAchievementScreen(int i, int j, float f) {
      int k = MathHelper.floor_double(this.field_146569_s + (this.field_146567_u - this.field_146569_s) * f);
      int l = MathHelper.floor_double(this.field_146568_t + (this.field_146566_v - this.field_146568_t) * f);
      if (k < field_146572_y) {
         k = field_146572_y;
      }

      if (l < field_146571_z) {
         l = field_146571_z;
      }

      if (k >= field_146559_A) {
         k = field_146559_A - 1;
      }

      if (l >= field_146560_B) {
         l = field_146560_B - 1;
      }

      int m = (this.width - this.field_146555_f) / 2;
      int n = (this.height - this.field_146557_g) / 2;
      int i2 = m + 16;
      int i3 = n + 17;
      this.zLevel = 0.0F;
      GlStateManager.depthFunc(518);
      GlStateManager.pushMatrix();
      GlStateManager.translate(i2, i3, -200.0F);
      GlStateManager.scale(1.0F / this.field_146570_r, 1.0F / this.field_146570_r, 0.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      int i4 = k + 288 >> 4;
      int i5 = l + 288 >> 4;
      int i6 = (k + 288) % 16;
      int i7 = (l + 288) % 16;
      Random random = new Random();
      float g = 16.0F / this.field_146570_r;
      float h = 16.0F / this.field_146570_r;

      for (int i8 = 0; i8 * g - i7 < 155.0F; i8++) {
         float f2 = 0.6F - (i5 + i8) / 25.0F * 0.3F;
         GlStateManager.color(f2, f2, f2, 1.0F);

         for (int i9 = 0; i9 * h - i6 < 224.0F; i9++) {
            random.setSeed(this.mc.getSession().getPlayerID().hashCode() + i4 + i9 + (i5 + i8) * 16);
            int i10 = random.nextInt(1 + i5 + i8) + (i5 + i8) / 2;
            TextureAtlasSprite textureAtlasSprite = this.func_175371_a(Blocks.sand);
            if (i10 > 37 || i5 + i8 == 35) {
               Block block = Blocks.bedrock;
               textureAtlasSprite = this.func_175371_a(block);
            } else if (i10 == 22) {
               if (random.nextInt(2) == 0) {
                  textureAtlasSprite = this.func_175371_a(Blocks.diamond_ore);
               } else {
                  textureAtlasSprite = this.func_175371_a(Blocks.redstone_ore);
               }
            } else if (i10 == 10) {
               textureAtlasSprite = this.func_175371_a(Blocks.iron_ore);
            } else if (i10 == 8) {
               textureAtlasSprite = this.func_175371_a(Blocks.coal_ore);
            } else if (i10 > 4) {
               textureAtlasSprite = this.func_175371_a(Blocks.stone);
            } else if (i10 > 0) {
               textureAtlasSprite = this.func_175371_a(Blocks.dirt);
            }

            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.drawTexturedModalRect(i9 * 16 - i6, i8 * 16 - i7, textureAtlasSprite, 16, 16);
         }
      }

      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);

      for (int i11 = 0; i11 < AchievementList.achievementList.size(); i11++) {
         Achievement achievement2 = AchievementList.achievementList.get(i11);
         if (achievement2.parentAchievement != null) {
            int i12 = achievement2.displayColumn * 24 - k + 11;
            int i13 = achievement2.displayRow * 24 - l + 11;
            int i14 = achievement2.parentAchievement.displayColumn * 24 - k + 11;
            int i15 = achievement2.parentAchievement.displayRow * 24 - l + 11;
            boolean bl = this.statFileWriter.hasAchievementUnlocked(achievement2);
            boolean bl2 = this.statFileWriter.canUnlockAchievement(achievement2);
            int i16 = this.statFileWriter.func_150874_c(achievement2);
            if (i16 <= 4) {
               int i17 = -16777216;
               if (bl) {
                  i17 = -6250336;
               } else if (bl2) {
                  i17 = -16711936;
               }

               drawHorizontalLine(i12, i14, i13, i17);
               drawVerticalLine(i14, i13, i15, i17);
               if (i12 > i14) {
                  this.drawTexturedModalRect(i12 - 11 - 7, i13 - 5, 114, 234, 7, 11);
               } else if (i12 < i14) {
                  this.drawTexturedModalRect(i12 + 11, i13 - 5, 107, 234, 7, 11);
               } else if (i13 > i15) {
                  this.drawTexturedModalRect(i12 - 5, i13 - 11 - 7, 96, 234, 11, 7);
               } else if (i13 < i15) {
                  this.drawTexturedModalRect(i12 - 5, i13 + 11, 96, 241, 11, 7);
               }
            }
         }
      }

      Achievement achievement3 = null;
      float f3 = (i - i2) * this.field_146570_r;
      float f4 = (j - i3) * this.field_146570_r;
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();

      for (int i18 = 0; i18 < AchievementList.achievementList.size(); i18++) {
         Achievement achievement4 = AchievementList.achievementList.get(i18);
         int i19 = achievement4.displayColumn * 24 - k;
         int i20 = achievement4.displayRow * 24 - l;
         if (i19 >= -24 && i20 >= -24 && i19 <= 224.0F * this.field_146570_r && i20 <= 155.0F * this.field_146570_r) {
            int i21 = this.statFileWriter.func_150874_c(achievement4);
            if (this.statFileWriter.hasAchievementUnlocked(achievement4)) {
               float f5 = 0.75F;
               GlStateManager.color(f5, f5, f5, 1.0F);
            } else if (this.statFileWriter.canUnlockAchievement(achievement4)) {
               float f6 = 1.0F;
               GlStateManager.color(f6, f6, f6, 1.0F);
            } else if (i21 < 3) {
               float f7 = 0.3F;
               GlStateManager.color(f7, f7, f7, 1.0F);
            } else if (i21 == 3) {
               float f8 = 0.2F;
               GlStateManager.color(f8, f8, f8, 1.0F);
            } else {
               if (i21 != 4) {
                  continue;
               }

               float f9 = 0.1F;
               GlStateManager.color(f9, f9, f9, 1.0F);
            }

            this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
            if (achievement4.getSpecial()) {
               this.drawTexturedModalRect(i19 - 2, i20 - 2, 26, 202, 26, 26);
            } else {
               this.drawTexturedModalRect(i19 - 2, i20 - 2, 0, 202, 26, 26);
            }

            if (!this.statFileWriter.canUnlockAchievement(achievement4)) {
               float f10 = 0.1F;
               GlStateManager.color(f10, f10, f10, 1.0F);
               this.itemRender.isNotRenderingEffectsInGUI(false);
            }

            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            this.itemRender.renderItemAndEffectIntoGUI(achievement4.theItemStack, i19 + 3, i20 + 3);
            GlStateManager.blendFunc(770, 771);
            GlStateManager.disableLighting();
            if (!this.statFileWriter.canUnlockAchievement(achievement4)) {
               this.itemRender.isNotRenderingEffectsInGUI(true);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (f3 >= i19 && f3 <= i19 + 22 && f4 >= i20 && f4 <= i20 + 22) {
               achievement3 = achievement4;
            }
         }
      }

      GlStateManager.disableDepth();
      GlStateManager.enableBlend();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
      this.drawTexturedModalRect(m, n, 0, 0, this.field_146555_f, this.field_146557_g);
      this.zLevel = 0.0F;
      GlStateManager.depthFunc(515);
      GlStateManager.disableDepth();
      GlStateManager.enableTexture2D();
      super.drawScreen(i, j, f);
      if (achievement3 != null) {
         String string = achievement3.getStatName().getUnformattedText();
         String string2 = achievement3.getDescription();
         int i22 = i + 12;
         int i23 = j - 4;
         int i24 = this.statFileWriter.func_150874_c(achievement3);
         if (this.statFileWriter.canUnlockAchievement(achievement3)) {
            int i25 = Math.max(this.fontRendererObj.getStringWidth(string), 120);
            int i26 = this.fontRendererObj.splitStringWidth(string2, i25);
            if (this.statFileWriter.hasAchievementUnlocked(achievement3)) {
               i26 += 12;
            }

            this.drawGradientRect(i22 - 3, i23 - 3, i22 + i25 + 3, i23 + i26 + 3 + 12, -1073741824, -1073741824);
            this.fontRendererObj.drawSplitString(string2, i22, i23 + 12, i25, -6250336);
            if (this.statFileWriter.hasAchievementUnlocked(achievement3)) {
               this.fontRendererObj.drawStringWithShadow(I18n.format("achievement.taken"), i22, i23 + i26 + 4, -7302913);
            }
         } else if (i24 == 3) {
            string = I18n.format("achievement.unknown");
            int i27 = Math.max(this.fontRendererObj.getStringWidth(string), 120);
            String string3 = new ChatComponentTranslation("achievement.requires", achievement3.parentAchievement.getStatName()).getUnformattedText();
            int i28 = this.fontRendererObj.splitStringWidth(string3, i27);
            this.drawGradientRect(i22 - 3, i23 - 3, i22 + i27 + 3, i23 + i28 + 12 + 3, -1073741824, -1073741824);
            this.fontRendererObj.drawSplitString(string3, i22, i23 + 12, i27, -9416624);
         } else if (i24 < 3) {
            int i29 = Math.max(this.fontRendererObj.getStringWidth(string), 120);
            String string4 = new ChatComponentTranslation("achievement.requires", achievement3.parentAchievement.getStatName()).getUnformattedText();
            int i30 = this.fontRendererObj.splitStringWidth(string4, i29);
            this.drawGradientRect(i22 - 3, i23 - 3, i22 + i29 + 3, i23 + i30 + 12 + 3, -1073741824, -1073741824);
            this.fontRendererObj.drawSplitString(string4, i22, i23 + 12, i29, -9416624);
         } else {
            string = null;
         }

         if (string != null) {
            this.fontRendererObj
               .drawStringWithShadow(
                  string,
                  i22,
                  i23,
                  this.statFileWriter.canUnlockAchievement(achievement3) ? (achievement3.getSpecial() ? -128 : -1) : (achievement3.getSpecial() ? -8355776 : -8355712)
               );
         }
      }

      GlStateManager.enableDepth();
      GlStateManager.enableLighting();
      RenderHelper.disableStandardItemLighting();
   }

   private TextureAtlasSprite func_175371_a(Block block) {
      return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(block.getDefaultState());
   }

   @Override
   public boolean doesGuiPauseGame() {
      return !this.loadingAchievements;
   }
}
