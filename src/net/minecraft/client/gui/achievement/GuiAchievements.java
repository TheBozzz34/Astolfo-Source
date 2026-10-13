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
   private static final long a = 44911525915446L;

   public GuiAchievements(GuiScreen var1, StatFileWriter var2) {
      this.parentScreen = var1;
      this.statFileWriter = var2;
      short var5 = 141;
      short var6 = 141;
      this.field_146569_s = this.field_146567_u = this.field_146565_w = AchievementList.openInventory.displayColumn * 24 - var5 / 2 - 12;
      this.field_146568_t = this.field_146566_v = this.field_146573_x = AchievementList.openInventory.displayRow * 24 - var6 / 2;
   }

   @Override
   public void initGui() {
      this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
      this.buttonList.clear();
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (!this.loadingAchievements && var1.id == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (var2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      } else {
         super.keyTyped(var1, var2);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      if (this.loadingAchievements) {
         this.drawDefaultBackground();
         this.d(this.fontRendererObj, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
         this.d(
            this.fontRendererObj,
            lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % lanSearchStates.length)],
            this.width / 2,
            this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2,
            16777215
         );
      } else {
         if (Mouse.isButtonDown(0)) {
            int var7 = (this.width - this.field_146555_f) / 2;
            int var8 = (this.height - this.field_146557_g) / 2;
            int var9 = var7 + 8;
            int var10 = var8 + 17;
            if ((this.field_146554_D == 0 || this.field_146554_D == 1) && var1 >= var9 && var1 < var9 + 224 && var2 >= var10 && var2 < var10 + 155) {
               if (this.field_146554_D == 0) {
                  this.field_146554_D = 1;
               } else {
                  this.field_146567_u = this.field_146567_u - (var1 - this.field_146563_h) * this.field_146570_r;
                  this.field_146566_v = this.field_146566_v - (var2 - this.field_146564_i) * this.field_146570_r;
                  this.field_146565_w = this.field_146569_s = this.field_146567_u;
                  this.field_146573_x = this.field_146568_t = this.field_146566_v;
               }

               this.field_146563_h = var1;
               this.field_146564_i = var2;
            }
         } else {
            this.field_146554_D = 0;
         }

         int var14 = Mouse.getDWheel();
         float var15 = this.field_146570_r;
         if (var14 < 0) {
            this.field_146570_r += 0.25F;
         } else if (var14 > 0) {
            this.field_146570_r -= 0.25F;
         }

         this.field_146570_r = MathHelper.clamp_float(this.field_146570_r, 1.0F, 2.0F);
         if (this.field_146570_r != var15) {
            float var16 = var15 - this.field_146570_r;
            float var17 = var15 * this.field_146555_f;
            float var11 = var15 * this.field_146557_g;
            float var12 = this.field_146570_r * this.field_146555_f;
            float var13 = this.field_146570_r * this.field_146557_g;
            this.field_146567_u -= (var12 - var17) * 0.5F;
            this.field_146566_v -= (var13 - var11) * 0.5F;
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
         this.drawAchievementScreen(var1, var2, var3);
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
         double var4 = this.field_146565_w - this.field_146567_u;
         double var6 = this.field_146573_x - this.field_146566_v;
         if (var4 * var4 + var6 * var6 < 4.0) {
            this.field_146567_u += var4;
            this.field_146566_v += var6;
         } else {
            this.field_146567_u += var4 * 0.85;
            this.field_146566_v += var6 * 0.85;
         }
      }
   }

   public void drawTitle() {
      int var3 = (this.width - this.field_146555_f) / 2;
      int var4 = (this.height - this.field_146557_g) / 2;
      this.fontRendererObj.D(I18n.format("gui.achievements"), var3 + 15, var4 + 5, 4210752);
   }

   public void drawAchievementScreen(int var1, int var2, float var3) {
      long var4 = a ^ 3445759962589L;
      int var8 = MathHelper.floor_double(this.field_146569_s + (this.field_146567_u - this.field_146569_s) * var3);
      int var9 = MathHelper.floor_double(this.field_146568_t + (this.field_146566_v - this.field_146568_t) * var3);
      if (var8 < field_146572_y) {
         var8 = field_146572_y;
      }

      if (var9 < field_146571_z) {
         var9 = field_146571_z;
      }

      if (var8 >= field_146559_A) {
         var8 = field_146559_A - 1;
      }

      if (var9 >= field_146560_B) {
         var9 = field_146560_B - 1;
      }

      int var10 = (this.width - this.field_146555_f) / 2;
      int var11 = (this.height - this.field_146557_g) / 2;
      int var12 = var10 + 16;
      int var13 = var11 + 17;
      this.zLevel = 0.0F;
      long var10000 = 1984634036510663296L ^ var4;
      GlStateManager.depthFunc(518);
      GlStateManager.pushMatrix();
      GlStateManager.translate(var12, var13, -200.0F);
      GlStateManager.scale(1.0F / this.field_146570_r, 1.0F / this.field_146570_r, 0.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      int var14 = var8 + 288 >> 4;
      int var15 = var9 + 288 >> 4;
      int var16 = (var8 + 288) % 16;
      int var17 = (var9 + 288) % 16;
      var10000 = 8988041302816562449L ^ var4;
      Random var23 = new Random();
      float var24 = 16.0F / this.field_146570_r;
      float var25 = 16.0F / this.field_146570_r;

      for (int var26 = 0; var26 * var24 - var17 < 155.0F; var26++) {
         float var27 = 0.6F - (var15 + var26) / 25.0F * 0.3F;
         GlStateManager.color(var27, var27, var27, 1.0F);

         for (int var28 = 0; var28 * var25 - var16 < 224.0F; var28++) {
            var23.setSeed(this.mc.getSession().getPlayerID().hashCode() + var14 + var28 + (var15 + var26) * 16);
            int var29 = var23.nextInt(1 + var15 + var26) + (var15 + var26) / 2;
            TextureAtlasSprite var30 = this.func_175371_a(Blocks.sand);
            if (var29 > 37 || var15 + var26 == 35) {
               Block var31 = Blocks.bedrock;
               var30 = this.func_175371_a(var31);
            } else if (var29 == 22) {
               if (var23.nextInt(2) == 0) {
                  var30 = this.func_175371_a(Blocks.diamond_ore);
               } else {
                  var30 = this.func_175371_a(Blocks.redstone_ore);
               }
            } else if (var29 == 10) {
               var30 = this.func_175371_a(Blocks.iron_ore);
            } else if (var29 == 8) {
               var30 = this.func_175371_a(Blocks.coal_ore);
            } else if (var29 > 4) {
               var30 = this.func_175371_a(Blocks.stone);
            } else if (var29 > 0) {
               var30 = this.func_175371_a(Blocks.dirt);
            }

            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.drawTexturedModalRect(var28 * 16 - var16, var26 * 16 - var17, var30, 16, 16);
         }
      }

      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);

      for (int var37 = 0; var37 < AchievementList.achievementList.size(); var37++) {
         Achievement var39 = AchievementList.achievementList.get(var37);
         if (var39.parentAchievement != null) {
            int var41 = var39.displayColumn * 24 - var8 + 11;
            int var43 = var39.displayRow * 24 - var9 + 11;
            int var46 = var39.parentAchievement.displayColumn * 24 - var8 + 11;
            int var49 = var39.parentAchievement.displayRow * 24 - var9 + 11;
            boolean var32 = this.statFileWriter.hasAchievementUnlocked(var39);
            boolean var33 = this.statFileWriter.canUnlockAchievement(var39);
            int var34 = this.statFileWriter.func_150874_c(var39);
            if (var34 <= 4) {
               int var35 = -16777216;
               if (var32) {
                  var35 = -6250336;
               } else if (var33) {
                  var35 = -16711936;
               }

               S(var41, var46, var43, var35);
               J(var46, var43, var49, var35);
               if (var41 > var46) {
                  this.drawTexturedModalRect(var41 - 11 - 7, var43 - 5, 114, 234, 7, 11);
               } else if (var41 < var46) {
                  this.drawTexturedModalRect(var41 + 11, var43 - 5, 107, 234, 7, 11);
               } else if (var43 > var49) {
                  this.drawTexturedModalRect(var41 - 5, var43 - 11 - 7, 96, 234, 11, 7);
               } else if (var43 < var49) {
                  this.drawTexturedModalRect(var41 - 5, var43 + 11, 96, 241, 11, 7);
               }
            }
         }
      }

      Achievement var38 = null;
      float var40 = (var1 - var12) * this.field_146570_r;
      float var42 = (var2 - var13) * this.field_146570_r;
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();

      for (int var44 = 0; var44 < AchievementList.achievementList.size(); var44++) {
         Achievement var47 = AchievementList.achievementList.get(var44);
         int var50 = var47.displayColumn * 24 - var8;
         int var52 = var47.displayRow * 24 - var9;
         if (var50 >= -24 && var52 >= -24 && var50 <= 224.0F * this.field_146570_r && var52 <= 155.0F * this.field_146570_r) {
            int var54 = this.statFileWriter.func_150874_c(var47);
            if (this.statFileWriter.hasAchievementUnlocked(var47)) {
               float var56 = 0.75F;
               GlStateManager.color(var56, var56, var56, 1.0F);
            } else if (this.statFileWriter.canUnlockAchievement(var47)) {
               float var57 = 1.0F;
               GlStateManager.color(var57, var57, var57, 1.0F);
            } else if (var54 < 3) {
               float var58 = 0.3F;
               GlStateManager.color(var58, var58, var58, 1.0F);
            } else if (var54 == 3) {
               float var59 = 0.2F;
               GlStateManager.color(var59, var59, var59, 1.0F);
            } else {
               if (var54 != 4) {
                  continue;
               }

               float var60 = 0.1F;
               GlStateManager.color(var60, var60, var60, 1.0F);
            }

            this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
            if (var47.getSpecial()) {
               this.drawTexturedModalRect(var50 - 2, var52 - 2, 26, 202, 26, 26);
            } else {
               this.drawTexturedModalRect(var50 - 2, var52 - 2, 0, 202, 26, 26);
            }

            if (!this.statFileWriter.canUnlockAchievement(var47)) {
               float var61 = 0.1F;
               GlStateManager.color(var61, var61, var61, 1.0F);
               this.itemRender.l(false);
            }

            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            var10000 = 5965809750228969771L ^ var4;
            this.itemRender.renderItemAndEffectIntoGUI(var47.theItemStack, var50 + 3, var52 + 3);
            GlStateManager.blendFunc(770, 771);
            GlStateManager.disableLighting();
            if (!this.statFileWriter.canUnlockAchievement(var47)) {
               this.itemRender.l(true);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (var40 >= var50 && var40 <= var50 + 22 && var42 >= var52 && var42 <= var52 + 22) {
               var38 = var47;
            }
         }
      }

      GlStateManager.disableDepth();
      GlStateManager.enableBlend();
      var10000 = 1015726158486668956L ^ var4;
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
      this.drawTexturedModalRect(var10, var11, 0, 0, this.field_146555_f, this.field_146557_g);
      this.zLevel = 0.0F;
      GlStateManager.depthFunc(515);
      GlStateManager.disableDepth();
      GlStateManager.enableTexture2D();
      super.drawScreen(var1, var2, var3);
      if (var38 != null) {
         String var45 = var38.getStatName().getUnformattedText();
         String var48 = var38.getDescription();
         int var51 = var1 + 12;
         int var53 = var2 - 4;
         int var55 = this.statFileWriter.func_150874_c(var38);
         if (this.statFileWriter.canUnlockAchievement(var38)) {
            int var62 = Math.max(this.fontRendererObj.getStringWidth(var45), 120);
            int var65 = this.fontRendererObj.splitStringWidth(var48, var62);
            if (this.statFileWriter.hasAchievementUnlocked(var38)) {
               var65 += 12;
            }

            this.drawGradientRect(var51 - 3, var53 - 3, var51 + var62 + 3, var53 + var65 + 3 + 12, -1073741824, -1073741824);
            this.fontRendererObj.drawSplitString(var48, var51, var53 + 12, var62, -6250336);
            if (this.statFileWriter.hasAchievementUnlocked(var38)) {
               this.fontRendererObj.drawStringWithShadow(I18n.format("achievement.taken"), var51, var53 + var65 + 4, -7302913);
            }
         } else if (var55 == 3) {
            var45 = I18n.format("achievement.unknown");
            int var63 = Math.max(this.fontRendererObj.getStringWidth(var45), 120);
            String var66 = new ChatComponentTranslation("achievement.requires", var38.parentAchievement.getStatName()).getUnformattedText();
            int var36 = this.fontRendererObj.splitStringWidth(var66, var63);
            this.drawGradientRect(var51 - 3, var53 - 3, var51 + var63 + 3, var53 + var36 + 12 + 3, -1073741824, -1073741824);
            this.fontRendererObj.drawSplitString(var66, var51, var53 + 12, var63, -9416624);
         } else if (var55 < 3) {
            int var64 = Math.max(this.fontRendererObj.getStringWidth(var45), 120);
            String var67 = new ChatComponentTranslation("achievement.requires", var38.parentAchievement.getStatName()).getUnformattedText();
            int var68 = this.fontRendererObj.splitStringWidth(var67, var64);
            this.drawGradientRect(var51 - 3, var53 - 3, var51 + var64 + 3, var53 + var68 + 12 + 3, -1073741824, -1073741824);
            this.fontRendererObj.drawSplitString(var67, var51, var53 + 12, var64, -9416624);
         } else {
            var45 = null;
         }

         if (var45 != null) {
            this.fontRendererObj
               .drawStringWithShadow(
                  var45,
                  var51,
                  var53,
                  this.statFileWriter.canUnlockAchievement(var38) ? (var38.getSpecial() ? -128 : -1) : (var38.getSpecial() ? -8355776 : -8355712)
               );
         }
      }

      GlStateManager.enableDepth();
      GlStateManager.enableLighting();
      RenderHelper.disableStandardItemLighting();
   }

   private TextureAtlasSprite func_175371_a(Block var1) {
      return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(var1.getDefaultState());
   }

   @Override
   public boolean doesGuiPauseGame() {
      return !this.loadingAchievements;
   }
}
