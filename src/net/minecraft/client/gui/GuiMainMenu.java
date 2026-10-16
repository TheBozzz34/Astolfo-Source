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
   public void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
   }

   @Override
   public void confirmClicked(boolean result, int id) {
   }

   private void drawPanorama(int j, int k, float g) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
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
      byte b = 8;

      for (int i = 0; i < b * b; i++) {
         GlStateManager.pushMatrix();
         float h = ((float)(i % b) / b - 0.5F) / 64.0F;
         float f2 = ((float)(i / b) / b - 0.5F) / 64.0F;
         float f3 = 0.0F;
         GlStateManager.translate(h, f2, f3);
         GlStateManager.rotate(MathHelper.sin((this.u + g) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-(this.u + g) * 0.1F, 0.0F, 1.0F, 0.0F);

         for (int l = 0; l < 6; l++) {
            GlStateManager.pushMatrix();
            if (l == 1) {
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (l == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (l == 3) {
               GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (l == 4) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (l == 5) {
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            this.mc.getTextureManager().bindTexture(titlePanoramaPaths[l]);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int m = 255 / (i + 1);
            worldRenderer.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, m).endVertex();
            worldRenderer.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, m).endVertex();
            worldRenderer.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, m).endVertex();
            worldRenderer.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, m).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.popMatrix();
         GlStateManager.colorMask(true, true, true, false);
      }

      worldRenderer.setTranslation(0.0, 0.0, 0.0);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.enableDepth();
   }

   private void rotateAndBlurSkybox(float g) {
      this.mc.getTextureManager().bindTexture(Y);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.colorMask(true, true, true, false);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      GlStateManager.disableAlpha();
      byte b = 3;

      for (int i = 0; i < b; i++) {
         float f = 1.0F / (i + 1);
         int j = this.width;
         int k = this.height;
         float h = (i - b / 2) / 256.0F;
         worldRenderer.pos(j, k, this.zLevel).tex(0.0F + h, 1.0).color(1.0F, 1.0F, 1.0F, f).endVertex();
         worldRenderer.pos(j, 0.0, this.zLevel).tex(1.0F + h, 1.0).color(1.0F, 1.0F, 1.0F, f).endVertex();
         worldRenderer.pos(0.0, 0.0, this.zLevel).tex(1.0F + h, 0.0).color(1.0F, 1.0F, 1.0F, f).endVertex();
         worldRenderer.pos(0.0, k, this.zLevel).tex(0.0F + h, 0.0).color(1.0F, 1.0F, 1.0F, f).endVertex();
      }

      tessellator.draw();
      GlStateManager.enableAlpha();
      GlStateManager.colorMask(true, true, true, true);
   }

   private void renderSkybox(int j, int k, float g) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GlStateManager.viewport(0, 0, 256, 256);
      this.drawPanorama(j, k, g);
      this.rotateAndBlurSkybox(g);
      this.rotateAndBlurSkybox(g);
      this.rotateAndBlurSkybox(g);
      this.rotateAndBlurSkybox(g);
      this.rotateAndBlurSkybox(g);
      this.rotateAndBlurSkybox(g);
      this.rotateAndBlurSkybox(g);
      this.mc.getFramebuffer().bindFramebuffer(true);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      float f = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
      float h = this.height * f / 256.0F;
      float f2 = this.width * f / 256.0F;
      int i = this.width;
      int l = this.height;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(0.0, l, this.zLevel).tex(0.5F - h, 0.5F + f2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(i, l, this.zLevel).tex(0.5F - h, 0.5F - f2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(i, 0.0, this.zLevel).tex(0.5F + h, 0.5F - f2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(0.0, 0.0, this.zLevel).tex(0.5F + h, 0.5F + f2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      tessellator.draw();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      GlStateManager.disableAlpha();
      this.renderSkybox(mouseX, mouseY, partialTicks);
      GlStateManager.enableAlpha();
      h.renderString(
         "Astolfo Client (#" + Astolfo.INSTANCE.getVersion() + ")", 8.0F, scaledResolution.getScaledHeight() - 12, new Color(255, 255, 255, 150).getRGB(), false, false
      );
      float f = scaledResolution.getScaledWidth() - 10.0F;
      float g = 5.0F;
      float f2 = 8.0F;

      for (int j = 0; j < 5; j++) {
         String string;
         ResourceLocation resourceLocation;
         switch (j) {
            case 0:
               string = "";
               resourceLocation = e;
               break;
            case 1:
               string = "Options";
               resourceLocation = x;
               break;
            case 2:
               string = "Language";
               resourceLocation = N;
               break;
            case 3:
               string = "Sense";
               resourceLocation = Astolfo.INSTANCE.getSenseSetting().getValue() ? V : K;
               break;
            case 4:
               string = "Discord";
               resourceLocation = i;
               break;
            default:
               continue;
         }

         float f3 = R.getStringWidth(string);
         float f4 = f3 + g * 2.0F + f2 * 2.0F;
         boolean bl = mouseX > f - f4 && mouseX < f && mouseY > 7.5F && mouseY < 35.0F;
         this.n(R, string, (int)(f - f4 / 2.0F + f2), 20.0F, bl ? -1 : -1442840577, false);
         G(resourceLocation, f2, f - f4 - g + f2, 12.0F);
         f -= f4 + 10.0F;
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f5 = scaledResolution.getScaledWidth() / 2 - 100.0F;
      float f6 = scaledResolution.getScaledHeight() / 2 - 90.0F;
      byte b = 76;
      byte b2 = 75;
      int k = this.width / 2 - 38;
      int l = this.height / 2 - this.height / 3;
      GL11.glEnable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/logo.png"));
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      drawModalRectWithCustomSizedTexture(k, l, 0.0F, 0.0F, 76, 75, 76.0F, 75.0F);
      f5 = scaledResolution.getScaledWidth() / 2 - 65.0F;
      f6 += 110.0F;
      String[] strings = new String[]{"SINGLEPLAYER", "MULTIPLAYER", "ALT MANAGER"};

      for (int m = 0; m < 3; m++) {
         RenderUtil.drawRect(f5, f6, f5 + 130.0F, f6 + 19.0F, new Color(0, 0, 0, 100).getRGB());
         boolean bl2 = mouseX >= f5 && mouseX <= f5 + 130.0F && mouseY >= f6 && mouseY <= f6 + 19.0F;
         GlStateManager.enableBlend();
         this.n(R, strings[m], (int)(f5 + 65.0F), (int)(f6 + 9.5F), bl2 ? -1 : -1442840577, false);
         f6 += 21.0F;
      }

      this.W(mouseX, mouseY, partialTicks);
      this.M(mouseX, mouseY);
      String string2 = String.format("Welcome, %s", this.mc.mouseHelper.Y().getAccount().getUsername());
      h.renderString(string2, scaledResolution.getScaledWidth() - h.getStringWidth(string2) - 8, scaledResolution.getScaledHeight() - 12, Color.WHITE.getRGB(), false, false);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void H(float g, float f2, float f3, float f4, float f5, float f6, int j, int k) {
      this.e(g, f2, f3, f4, f5, j);
      this.e(g + f6, f2 + f6, f3 - f6, f4 - f6, f5, k);
   }

   public void e(double d, double d2, double d3, double d4, double d5, int j) {
      float f = (j >> 24 & 0xFF) / 255.0F;
      float g = (j >> 16 & 0xFF) / 255.0F;
      float h = (j >> 8 & 0xFF) / 255.0F;
      float f2 = (j & 0xFF) / 255.0F;
      GL11.glPushAttrib(0);
      GL11.glScaled(0.5, 0.5, 0.5);
      d *= 2.0;
      d2 *= 2.0;
      d3 *= 2.0;
      d4 *= 2.0;
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glColor4f(g, h, f2, f);
      GL11.glEnable(2848);
      GL11.glBegin(9);

      for (int i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(d + d5 + Math.sin(i * Math.PI / 180.0) * (d5 * -1.0), d2 + d5 + Math.cos(i * Math.PI / 180.0) * (d5 * -1.0));
      }

      for (int k = 90; k <= 180; k += 3) {
         GL11.glVertex2d(d + d5 + Math.sin(k * Math.PI / 180.0) * (d5 * -1.0), d4 - d5 + Math.cos(k * Math.PI / 180.0) * (d5 * -1.0));
      }

      for (int l = 0; l <= 90; l += 3) {
         GL11.glVertex2d(d3 - d5 + Math.sin(l * Math.PI / 180.0) * d5, d4 - d5 + Math.cos(l * Math.PI / 180.0) * d5);
      }

      for (int m = 90; m <= 180; m += 3) {
         GL11.glVertex2d(d3 - d5 + Math.sin(m * Math.PI / 180.0) * d5, d2 + d5 + Math.cos(m * Math.PI / 180.0) * d5);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glScaled(2.0, 2.0, 2.0);
      GL11.glPopAttrib();
   }

   public static void G(ResourceLocation resourceLocation, float g, float f2, float f3) {
      float f = g * 2.0F;
      float h = g * 2.0F;
      float f4 = 0.0F;
      float f5 = 0.0F;
      GL11.glEnable(3042);
      Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
      GL11.glBegin(7);
      GL11.glTexCoord2d(f4 / g, f5 / g);
      GL11.glVertex2d(f2, f3);
      GL11.glTexCoord2d(f4 / g, (f5 + g) / g);
      GL11.glVertex2d(f2, f3 + f);
      GL11.glTexCoord2d((f4 + g) / g, (f5 + g) / g);
      GL11.glVertex2d(f2 + h, f3 + f);
      GL11.glTexCoord2d((f4 + g) / g, f5 / g);
      GL11.glVertex2d(f2 + h, f3);
      GL11.glEnd();
   }

   public void n(CustomFontRenderer customFontRenderer, String string, float g, float f2, int j, boolean bl) {
      int i = customFontRenderer.getStringWidth(string);
      int k = customFontRenderer.getStringHeight(string);
      float f = g - i / 2.0F;
      float h = f2 - k / 2.0F;
      if (bl) {
         customFontRenderer.drawStringWithShadow(string, (int)f, (int)h, j);
      } else {
         customFontRenderer.drawString(string, (int)f, (int)h, j);
      }
   }

   private void M(int j, int k) {
      int i = this.fontRendererObj.getStringWidth("Changelog");
      boolean bl = this.f(0, 0, i + 5, this.fontRendererObj.FONT_HEIGHT + 4, j, k);
      Astolfo.INSTANCE.getFontRenderer().drawStringWithShadow("Changelog", 3.0F, 3.0F, bl ? Color.WHITE.getRGB() : this.w);
      if (bl) {
         byte b2 = 15;

         for (Entry entry : n.entrySet()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            Astolfo.INSTANCE.getFontRenderer().drawStringWithShadow((String)entry.getKey(), 15.0F, b2 * 2, this.w);
            GlStateManager.popMatrix();
            b2 += 5;

            for (Object object : (List)entry.getValue()) {
               String string = (String)object;
               GlStateManager.pushMatrix();
               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               int l = string.startsWith("[+]")
                  ? this.B
                  : (string.startsWith("[-]") ? this.q : (string.startsWith("[0]") ? this.d : (string.startsWith("[/]") ? this.b : Color.GRAY.getRGB())));
               drawRect(20, b2 * 2 + 2, 25, b2 * 2 + 7, l);
               GlStateManager.enableBlend();
               String string2 = string.replace("[+]", "").replace("[/]", "").replace("[-]", "").replace("[0]", "");
               Astolfo.INSTANCE.getFontRenderer().drawStringWithShadow("   " + string2, 15.0F, b2 * 2, this.w);
               b2 += 5;
               GlStateManager.popMatrix();
            }
         }
      } else {
         RenderUtil.drawRect(0.0, 0.0, i + 5, this.fontRendererObj.FONT_HEIGHT + 5, new Color(0, 0, 0, 70).getRGB());
         RenderUtil.drawRect(1.0, this.fontRendererObj.FONT_HEIGHT + 4, i + 4, this.fontRendererObj.FONT_HEIGHT + 5, new Color(255, 255, 255, 70).getRGB());
      }
   }

   public void W(int j, int k, float g) {
      int i = this.field_175375_v ? 75 : (l == 2 ? 50 : (l == 3 ? 100 : (l == 4 ? 75 : 60)));
      int m = this.width - i;
      int n = this.height - 75;
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
      drawModalRectWithCustomSizedTexture(m, n, 0.0F, 0.0F, i, 75, i, 75.0F);
      if (this.f(m, n, i, 75, j, k) && Mouse.isButtonDown(0) && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
         try {
            Desktop.getDesktop().browse(new URI("https://astolfo.lgbt/forums"));
         } catch (IOException | URISyntaxException value) {
            value.printStackTrace();
         }
      }
   }

   private boolean f(int j, int k, int i2, int i3, int i4, int i5) {
      int i = i2;
      int l = i3;
      if ((i | l) < 0) {
         return false;
      } else if (i4 >= j && i5 >= k) {
         i += j;
         l += k;
         return (i < j || i > i4) && (l < k || l > i5);
      } else {
         return false;
      }
   }

   public static void g(float g, float f2, ResourceLocation resourceLocation, int j) {
      GL11.glPushMatrix();
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
      L((int)g, (int)f2, 0.0F, 0.0F, j, j, j, j, j, j);
      GL11.glDisable(3042);
      GL11.glEnable(2929);
      GL11.glPopMatrix();
   }

   public static void L(int j, int k, float g, float f2, int i2, int i3, int i4, int i5, float f3, float f4) {
      Gui.drawScaledCustomSizeModalRect(j, k, g, f2, i2, i3, i4, i5, f3, f4);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         float f = 5.0F;
         float g = 9.0F;
         float h = scaledResolution.getScaledWidth() - 10.0F;
         CustomFontRenderer customFontRenderer = R;

         for (int i = 0; i < 5; i++) {
            String string;
            switch (i) {
               case 0:
                  string = "Exit";
                  break;
               case 1:
                  string = "Settings";
                  break;
               case 2:
                  string = "Language";
                  break;
               case 3:
                  string = "Sense";
                  break;
               case 4:
                  string = "Discord";
                  break;
               default:
                  continue;
            }

            float f2 = customFontRenderer.getStringWidth(string);
            float f3 = f2 + f * 2.0F + g * 2.0F;
            boolean bl = mouseX > h - f3 && mouseX < h && mouseY > 7.5F && mouseY < 35.0F;
            if (bl) {
               switch (i) {
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
                     Astolfo.INSTANCE.getUserManager().getUsers().clear();
                     break;
                  case 4:
                     if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
                        try {
                           Desktop.getDesktop().browse(new URI("https://www.astolfo.lgbt/forums/discord/invite/general/"));
                        } catch (IOException | URISyntaxException value) {
                           value.printStackTrace();
                        }
                     }
               }

               return;
            }

            h -= f3 + 10.0F;
         }

         float f4 = scaledResolution.getScaledWidth() / 2 - 65.0F;
         float f5 = scaledResolution.getScaledHeight() / 2 - 90.0F;
         f5 += 110.0F;

         for (int j = 0; j < 3; j++) {
            if (mouseX >= f4 && mouseX <= f4 + 130.0F && mouseY >= f5 && mouseY <= f5 + 19.0F) {
               switch (j) {
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

            f5 += 19.0F;
         }

         super.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   static {
      DynamicTexture dynamicTexture = new DynamicTexture(256, 256);
      Y = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("background", dynamicTexture);

      try {
         Font font2 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/Roboto-Regular.ttf")).deriveFont(28.0F);
         Font font3 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/roboto-light.ttf")).deriveFont(20.0F);
         Font font4 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/Roboto-Regular.ttf")).deriveFont(18.0F);
         Font font5 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/Roboto-Regular.ttf")).deriveFont(10.0F);
         Font font6 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/Lato-Bold.ttf")).deriveFont(15.0F);
         Font font7 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/Comfortaa.ttf")).deriveFont(30.0F);
         P.loadFont(font2, true);
         R.loadFont(font3, true);
         h.loadFont(font4, true);
         c.loadFont(font5, true);
         m.loadFont(font6, true);
         M.loadFont(font7, true);
      } catch (FontFormatException | IOException value) {
         value.printStackTrace();
      }
   }
}
