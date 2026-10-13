package net.minecraft.client.gui;

import bypass.Astolfo;
import bypass.render.RenderUtil;
import bypass.render.font.CustomFontRenderer;
import bypass.util.RandomUtils;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.stream.GuiIngestServers;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
   private int u;
   private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{
      new ResourceLocation("textures/gui/title/background/panorama_0.png"),
      new ResourceLocation("textures/gui/title/background/panorama_1.png"),
      new ResourceLocation("textures/gui/title/background/panorama_2.png"),
      new ResourceLocation("textures/gui/title/background/panorama_3.png"),
      new ResourceLocation("textures/gui/title/background/panorama_4.png"),
      new ResourceLocation("textures/gui/title/background/panorama_5.png")
   };
   private static final ResourceLocation Y;
   private static TreeMap<String, List<String>> n = new TreeMap<>(Collections.reverseOrder());
   private static final float O = 200.0F;
   private static final float y = 180.0F;
   public static CustomFontRenderer P = new CustomFontRenderer();
   public static CustomFontRenderer R = new CustomFontRenderer();
   public static CustomFontRenderer h = new CustomFontRenderer();
   public static CustomFontRenderer c = new CustomFontRenderer();
   public static CustomFontRenderer m = new CustomFontRenderer();
   public static CustomFontRenderer M = new CustomFontRenderer();
   public static final ResourceLocation e = new ResourceLocation("client/icon/close.png");
   private static final ResourceLocation N = new ResourceLocation("client/icon/language.png");
   private static final ResourceLocation x = new ResourceLocation("client/icon/options.png");
   public static final ResourceLocation V = new ResourceLocation("client/icon/sense.png");
   public static final ResourceLocation K = new ResourceLocation("client/icon/no_sense.png");
   private static final ResourceLocation i = new ResourceLocation("client/icon/discord.png");
   private static final float Q = 130.0F;
   private static final float a = 19.0F;
   private final boolean field_175375_v = RandomUtils.nextInt(0, 1000) == 500;
   public static boolean f;
   private static final int l = RandomUtils.nextInt(0, 5);
   private final int B = new Color(121, 255, 80, 120).getRGB();
   private final int q = new Color(255, 47, 40, 120).getRGB();
   private final int b = new Color(65, 176, 254, 120).getRGB();
   private final int d = new Color(239, 236, 22, 122).getRGB();
   private final int w = new Color(255, 255, 255, 120).getRGB();
   private static final long bb = 32299773017083L;

   public GuiMainMenu() {
      n.put("Build #latest", Arrays.asList(":D"));
   }

   @Override
   public void updateScreen() {
      this.u++;
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
   }

   @Override
   public void confirmClicked(boolean var1, int var2) {
   }

   private void drawPanorama(int var1, int var2, float var3) {
      long var4 = bb ^ 98884772780215L;
      Tessellator var7 = Tessellator.getInstance();
      WorldRenderer var8 = var7.getWorldRenderer();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GlStateManager.matrixMode(5888);
      f = true;
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.disableCull();
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      byte var9 = 8;

      for (int var10 = 0; var10 < var9 * var9; var10++) {
         GlStateManager.pushMatrix();
         float var11 = ((float)(var10 % var9) / var9 - 0.5F) / 64.0F;
         float var12 = ((float)(var10 / var9) / var9 - 0.5F) / 64.0F;
         float var13 = 0.0F;
         GlStateManager.translate(var11, var12, var13);
         GlStateManager.rotate(MathHelper.sin((this.u + var3) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-(this.u + var3) * 0.1F, 0.0F, 1.0F, 0.0F);

         for (int var14 = 0; var14 < 6; var14++) {
            GlStateManager.pushMatrix();
            if (var14 == 1) {
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var14 == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var14 == 3) {
               GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var14 == 4) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var14 == 5) {
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            this.mc.getTextureManager().bindTexture(titlePanoramaPaths[var14]);
            var8.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int var15 = 255 / (var10 + 1);
            var8.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, var15).endVertex();
            var8.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, var15).endVertex();
            var8.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, var15).endVertex();
            var8.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, var15).endVertex();
            var7.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.popMatrix();
         GlStateManager.colorMask(true, true, true, false);
      }

      var8.setTranslation(0.0, 0.0, 0.0);
      GlStateManager.colorMask(true, true, true, true);
      long var10000 = 7483274872786576330L ^ var4;
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      var10000 = 9101244035744828431L ^ var4;
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.enableDepth();
   }

   private void rotateAndBlurSkybox(float var1) {
      this.mc.getTextureManager().bindTexture(Y);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.colorMask(true, true, true, false);
      Tessellator var5 = Tessellator.getInstance();
      WorldRenderer var6 = var5.getWorldRenderer();
      var6.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      GlStateManager.disableAlpha();
      byte var7 = 3;

      for (int var8 = 0; var8 < var7; var8++) {
         float var9 = 1.0F / (var8 + 1);
         int var10 = this.width;
         int var11 = this.height;
         float var12 = (var8 - var7 / 2) / 256.0F;
         var6.pos(var10, var11, this.zLevel).tex(0.0F + var12, 1.0).color(1.0F, 1.0F, 1.0F, var9).endVertex();
         var6.pos(var10, 0.0, this.zLevel).tex(1.0F + var12, 1.0).color(1.0F, 1.0F, 1.0F, var9).endVertex();
         var6.pos(0.0, 0.0, this.zLevel).tex(1.0F + var12, 0.0).color(1.0F, 1.0F, 1.0F, var9).endVertex();
         var6.pos(0.0, var11, this.zLevel).tex(0.0F + var12, 0.0).color(1.0F, 1.0F, 1.0F, var9).endVertex();
      }

      var5.draw();
      GlStateManager.enableAlpha();
      GlStateManager.colorMask(true, true, true, true);
   }

   private void renderSkybox(int var1, int var2, float var3) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GlStateManager.viewport(0, 0, 256, 256);
      this.drawPanorama(var1, var2, var3);
      this.rotateAndBlurSkybox(var3);
      this.rotateAndBlurSkybox(var3);
      this.rotateAndBlurSkybox(var3);
      this.rotateAndBlurSkybox(var3);
      this.rotateAndBlurSkybox(var3);
      this.rotateAndBlurSkybox(var3);
      this.rotateAndBlurSkybox(var3);
      this.mc.getFramebuffer().bindFramebuffer(true);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      float var6 = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
      float var7 = this.height * var6 / 256.0F;
      float var8 = this.width * var6 / 256.0F;
      int var9 = this.width;
      int var10 = this.height;
      Tessellator var11 = Tessellator.getInstance();
      WorldRenderer var12 = var11.getWorldRenderer();
      var12.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      var12.pos(0.0, var10, this.zLevel).tex(0.5F - var7, 0.5F + var8).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var12.pos(var9, var10, this.zLevel).tex(0.5F - var7, 0.5F - var8).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var12.pos(var9, 0.0, this.zLevel).tex(0.5F + var7, 0.5F - var8).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var12.pos(0.0, 0.0, this.zLevel).tex(0.5F + var7, 0.5F + var8).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var11.draw();
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      long var4 = bb ^ 35103618917102L;
      long var6 = (var4 ^ 7056163135495L) >>> 16;
      int var8 = (int)((var4 ^ 7056163135495L) << 48 >>> 48);
      ScaledResolution var10 = new ScaledResolution(this.mc);
      GlStateManager.disableAlpha();
      this.renderSkybox(var1, var2, var3);
      GlStateManager.enableAlpha();
      h.v("Astolfo Client (#" + Astolfo.INSTANCE.getVersion() + ")", 8.0F, var10.getScaledHeight() - 12, new Color(255, 255, 255, 150).getRGB(), false, false);
      float var11 = var10.getScaledWidth() - 10.0F;
      float var12 = 5.0F;
      float var13 = 8.0F;

      for (int var14 = 0; var14 < 5; var14++) {
         String var15;
         ResourceLocation var16;
         switch (var14) {
            case 0:
               var15 = "";
               var16 = e;
               break;
            case 1:
               var15 = "Options";
               var16 = x;
               break;
            case 2:
               var15 = "Language";
               var16 = N;
               break;
            case 3:
               var15 = "Sense";
               var16 = Astolfo.INSTANCE.getSenseSetting().getValue() ? V : K;
               break;
            case 4:
               var15 = "Discord";
               var16 = i;
               break;
            default:
               continue;
         }

         float var17 = R.getStringWidth(var15);
         float var18 = var17 + var12 * 2.0F + var13 * 2.0F;
         boolean var19 = var1 > var11 - var18 && var1 < var11 && var2 > 7.5F && var2 < 35.0F;
         this.n(R, var15, (int)(var11 - var18 / 2.0F + var13), 20.0F, var19 ? -1 : -1442840577, false);
         G(var16, var13, var11 - var18 - var12 + var13, 12.0F);
         var11 -= var18 + 10.0F;
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var23 = var10.getScaledWidth() / 2 - 100.0F;
      float var25 = var10.getScaledHeight() / 2 - 90.0F;
      byte var27 = 76;
      byte var28 = 75;
      int var29 = this.width / 2 - 38;
      int var30 = this.height / 2 - this.height / 3;
      GL11.glEnable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/logo.png"));
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      drawModalRectWithCustomSizedTexture(var29, var30, 0.0F, 0.0F, 76, 75, 76.0F, 75.0F);
      var23 = var10.getScaledWidth() / 2 - 65.0F;
      var25 += 110.0F;
      String[] var20 = new String[]{"SINGLEPLAYER", "MULTIPLAYER", "ALT MANAGER"};

      for (int var21 = 0; var21 < 3; var21++) {
         RenderUtil.drawRect(var23, var25, var23 + 130.0F, var25 + 19.0F, new Color(0, 0, 0, 100).getRGB());
         boolean var22 = var1 >= var23 && var1 <= var23 + 130.0F && var2 >= var25 && var2 <= var25 + 19.0F;
         GlStateManager.enableBlend();
         this.n(R, var20[var21], (int)(var23 + 65.0F), (int)(var25 + 9.5F), var22 ? -1 : -1442840577, false);
         var25 += 21.0F;
      }

      this.W(var1, var2, var3);
      this.M(var1, var2);
      String var31 = String.format("Welcome, %s", this.mc.mouseHelper.Y().getAccount().getUsername());
      h.v(var31, var10.getScaledWidth() - h.getStringWidth(var31) - 8, var10.getScaledHeight() - 12, Color.WHITE.getRGB(), false, false);
      super.drawScreen(var1, var2, var3);
   }

   public void H(float var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8) {
      this.e(var1, var2, var3, var4, var5, var7);
      this.e(var1 + var6, var2 + var6, var3 - var6, var4 - var6, var5, var8);
   }

   public void e(double var1, double var3, double var5, double var7, double var9, int var11) {
      float var15 = (var11 >> 24 & 0xFF) / 255.0F;
      float var16 = (var11 >> 16 & 0xFF) / 255.0F;
      float var17 = (var11 >> 8 & 0xFF) / 255.0F;
      float var18 = (var11 & 0xFF) / 255.0F;
      GL11.glPushAttrib(0);
      GL11.glScaled(0.5, 0.5, 0.5);
      var1 *= 2.0;
      var3 *= 2.0;
      var5 *= 2.0;
      var7 *= 2.0;
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glColor4f(var16, var17, var18, var15);
      GL11.glEnable(2848);
      GL11.glBegin(9);

      for (int var19 = 0; var19 <= 90; var19 += 3) {
         GL11.glVertex2d(var1 + var9 + Math.sin(var19 * Math.PI / 180.0) * (var9 * -1.0), var3 + var9 + Math.cos(var19 * Math.PI / 180.0) * (var9 * -1.0));
      }

      for (int var24 = 90; var24 <= 180; var24 += 3) {
         GL11.glVertex2d(var1 + var9 + Math.sin(var24 * Math.PI / 180.0) * (var9 * -1.0), var7 - var9 + Math.cos(var24 * Math.PI / 180.0) * (var9 * -1.0));
      }

      for (int var25 = 0; var25 <= 90; var25 += 3) {
         GL11.glVertex2d(var5 - var9 + Math.sin(var25 * Math.PI / 180.0) * var9, var7 - var9 + Math.cos(var25 * Math.PI / 180.0) * var9);
      }

      for (int var26 = 90; var26 <= 180; var26 += 3) {
         GL11.glVertex2d(var5 - var9 + Math.sin(var26 * Math.PI / 180.0) * var9, var3 + var9 + Math.cos(var26 * Math.PI / 180.0) * var9);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glScaled(2.0, 2.0, 2.0);
      GL11.glPopAttrib();
   }

   public static void G(ResourceLocation var0, float var1, float var2, float var3) {
      float var6 = var1 * 2.0F;
      float var7 = var1 * 2.0F;
      float var8 = 0.0F;
      float var9 = 0.0F;
      GL11.glEnable(3042);
      Minecraft.getMinecraft().getTextureManager().bindTexture(var0);
      GL11.glBegin(7);
      GL11.glTexCoord2d(var8 / var1, var9 / var1);
      GL11.glVertex2d(var2, var3);
      GL11.glTexCoord2d(var8 / var1, (var9 + var1) / var1);
      GL11.glVertex2d(var2, var3 + var6);
      GL11.glTexCoord2d((var8 + var1) / var1, (var9 + var1) / var1);
      GL11.glVertex2d(var2 + var7, var3 + var6);
      GL11.glTexCoord2d((var8 + var1) / var1, var9 / var1);
      GL11.glVertex2d(var2 + var7, var3);
      GL11.glEnd();
   }

   public void n(CustomFontRenderer var1, String var2, float var3, float var4, int var5, boolean var6) {
      int var10 = var1.getStringWidth(var2);
      int var11 = var1.getStringHeight(var2);
      float var12 = var3 - var10 / 2.0F;
      float var13 = var4 - var11 / 2.0F;
      if (var6) {
         var1.drawStringWithShadow(var2, (int)var12, (int)var13, var5);
      } else {
         var1.drawString(var2, (int)var12, (int)var13, var5);
      }
   }

   private void M(int var1, int var2) {
      int var6 = this.fontRendererObj.getStringWidth("Changelog");
      boolean var7 = this.f(0, 0, var6 + 5, this.fontRendererObj.FONT_HEIGHT + 4, var1, var2);
      Astolfo.INSTANCE.getFontRenderer().drawStringWithShadow("Changelog", 3.0F, 3.0F, var7 ? Color.WHITE.getRGB() : this.w);
      if (var7) {
         byte var8 = 15;

         for (Entry var10 : n.entrySet()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            Astolfo.INSTANCE.getFontRenderer().drawStringWithShadow((String)var10.getKey(), 15.0F, var8 * 2, this.w);
            GlStateManager.popMatrix();
            var8 += 5;

            for (Object var12$raw : (List)var10.getValue()) {
               String var12 = (String)var12$raw;
               GlStateManager.pushMatrix();
               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               int var13 = var12.startsWith("[+]")
                  ? this.B
                  : (var12.startsWith("[-]") ? this.q : (var12.startsWith("[0]") ? this.d : (var12.startsWith("[/]") ? this.b : Color.GRAY.getRGB())));
               drawRect(20, var8 * 2 + 2, 25, var8 * 2 + 7, var13);
               GlStateManager.enableBlend();
               String var14 = var12.replace("[+]", "").replace("[/]", "").replace("[-]", "").replace("[0]", "");
               Astolfo.INSTANCE.getFontRenderer().drawStringWithShadow("   " + var14, 15.0F, var8 * 2, this.w);
               var8 += 5;
               GlStateManager.popMatrix();
            }
         }
      } else {
         RenderUtil.drawRect(0.0, 0.0, var6 + 5, this.fontRendererObj.FONT_HEIGHT + 5, new Color(0, 0, 0, 70).getRGB());
         RenderUtil.drawRect(1.0, this.fontRendererObj.FONT_HEIGHT + 4, var6 + 4, this.fontRendererObj.FONT_HEIGHT + 5, new Color(255, 255, 255, 70).getRGB());
      }
   }

   public void W(int var1, int var2, float var3) {
      int var7 = this.field_175375_v ? 75 : (l == 2 ? 50 : (l == 3 ? 100 : (l == 4 ? 75 : 60)));
      int var9 = this.width - var7;
      int var10 = this.height - 75;
      GL11.glEnable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      if (this.field_175375_v) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/astolfos/miguel.png"));
      } else {
         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/astolfos/" + l + ".png"));
      }

      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      drawModalRectWithCustomSizedTexture(var9, var10, 0.0F, 0.0F, var7, 75, var7, 75.0F);
      if (this.f(var9, var10, var7, 75, var1, var2) && Mouse.isButtonDown(0) && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)
         )
       {
         try {
            Desktop.getDesktop().browse(new URI("https://astolfo.lgbt/forums"));
         } catch (IOException | URISyntaxException var12) {
            var12.printStackTrace();
         }
      }
   }

   private boolean f(int var1, int var2, int var3, int var4, int var5, int var6) {
      int var10 = var3;
      int var11 = var4;
      if ((var10 | var11) < 0) {
         return false;
      } else if (var5 >= var1 && var6 >= var2) {
         var10 += var1;
         var11 += var2;
         return (var10 < var1 || var10 > var5) && (var11 < var2 || var11 > var6);
      } else {
         return false;
      }
   }

   public static void g(float var0, float var1, ResourceLocation var2, int var3) {
      GL11.glPushMatrix();
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      Minecraft.getMinecraft().getTextureManager().bindTexture(var2);
      L((int)var0, (int)var1, 0.0F, 0.0F, var3, var3, var3, var3, var3, var3);
      GL11.glDisable(3042);
      GL11.glEnable(2929);
      GL11.glPopMatrix();
   }

   public static void L(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7, float var8, float var9) {
      Gui.drawScaledCustomSizeModalRect(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      long var4 = bb ^ 1029827809290L;
      long var6 = var4 ^ 136146140769035L;
      if (var3 == 0) {
         ScaledResolution var9 = new ScaledResolution(this.mc);
         float var10 = 5.0F;
         float var11 = 9.0F;
         float var12 = var9.getScaledWidth() - 10.0F;
         CustomFontRenderer var13 = R;

         for (int var14 = 0; var14 < 5; var14++) {
            String var15;
            switch (var14) {
               case 0:
                  var15 = "Exit";
                  break;
               case 1:
                  var15 = "Settings";
                  break;
               case 2:
                  var15 = "Language";
                  break;
               case 3:
                  var15 = "Sense";
                  break;
               case 4:
                  var15 = "Discord";
                  break;
               default:
                  continue;
            }

            float var16 = var13.getStringWidth(var15);
            float var17 = var16 + var10 * 2.0F + var11 * 2.0F;
            boolean var18 = var1 > var12 - var17 && var1 < var12 && var2 > 7.5F && var2 < 35.0F;
            if (var18) {
               switch (var14) {
                  case 0:
                     this.mc.shutdown();
                     break;
                  case 1:
                     this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                     break;
                  case 2:
                     this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
                     break;
                  case 3:
                     Astolfo.INSTANCE.getSenseSetting().set(!Astolfo.INSTANCE.getSenseSetting().getValue());
                     Astolfo.INSTANCE.getUserManager().r().clear();
                     break;
                  case 4:
                     if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
                        try {
                           Desktop.getDesktop().browse(new URI("https://www.astolfo.lgbt/forums/discord/invite/general/"));
                        } catch (IOException | URISyntaxException var20) {
                           var20.printStackTrace();
                        }
                     }
               }

               return;
            }

            var12 -= var17 + 10.0F;
         }

         float var21 = var9.getScaledWidth() / 2 - 65.0F;
         float var22 = var9.getScaledHeight() / 2 - 90.0F;
         var22 += 110.0F;

         for (int var24 = 0; var24 < 3; var24++) {
            if (var1 >= var21 && var1 <= var21 + 130.0F && var2 >= var22 && var2 <= var22 + 19.0F) {
               switch (var24) {
                  case 0:
                     this.mc.displayGuiScreen(new GuiSelectWorld(this));
                     break;
                  case 1:
                     this.mc.displayGuiScreen(new GuiMultiplayer(this));
                     break;
                  case 2:
                     this.mc.displayGuiScreen(new GuiIngestServers(this));
               }

               return;
            }

            var22 += 19.0F;
         }

         super.mouseClicked(var1, var2, var3);
      }
   }

   static {
      DynamicTexture var22 = new DynamicTexture(256, 256);
      Y = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("background", var22);

      try {
         Font var23 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/Roboto-Regular.ttf")).deriveFont(28.0F);
         Font var24 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/roboto-light.ttf")).deriveFont(20.0F);
         Font var25 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/Roboto-Regular.ttf")).deriveFont(18.0F);
         Font var26 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/Roboto-Regular.ttf")).deriveFont(10.0F);
         Font var27 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/Lato-Bold.ttf")).deriveFont(15.0F);
         Font var28 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/Comfortaa.ttf")).deriveFont(30.0F);
         P.B(var23, true);
         R.B(var24, true);
         h.B(var25, true);
         c.B(var26, true);
         m.B(var27, true);
         M.B(var28, true);
      } catch (FontFormatException | IOException var29) {
         var29.printStackTrace();
      }
   }
}
