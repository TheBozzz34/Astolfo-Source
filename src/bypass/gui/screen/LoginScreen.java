package bypass.gui.screen;

import bypass.Astolfo;
import bypass.auth.AuthUtil;
import bypass.gui.PasswordField;
import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.render.RenderUtil;
import bypass.util.Stopwatch;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class LoginScreen extends GuiScreen {
   private final Animation P = new TimedAnimation().setDuration(750).setEasing(Easing.LINEAR);
   public static GuiButton e;
   public static GuiTextField usernameField;
   public static PasswordField passwordField;
   public static boolean V;
   public int h = 12;
   public int R = 40;
   private int y;
   private ResourceLocation q;
   public static final Stopwatch N = new Stopwatch();
   private static final ResourceLocation[] a = new ResourceLocation[]{
      new ResourceLocation("textures/gui/title/background/panorama_0.png"),
      new ResourceLocation("textures/gui/title/background/panorama_1.png"),
      new ResourceLocation("textures/gui/title/background/panorama_2.png"),
      new ResourceLocation("textures/gui/title/background/panorama_3.png"),
      new ResourceLocation("textures/gui/title/background/panorama_4.png"),
      new ResourceLocation("textures/gui/title/background/panorama_5.png")
   };
   private boolean Y;
   public static boolean d;
   public static boolean p;
   public static String x;
   public static String Q;
   public static String l;
   private static SecretKeySpec f;
   private static byte[] n;
   private static final long c = 89496277506807L;

   @Override
   public void initGui() {
      DynamicTexture var3 = new DynamicTexture(256, 256);
      this.q = this.mc.getTextureManager().getDynamicTextureLocation(LoginScreenStrings.s("/C1gfMLyXMtfCw=="), var3);
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(e = new GuiButton(-88888, this.width / 2 - 95, this.height / 2 + 25, 191, 20, LoginScreenStrings.s("0iNkfss=")));
      e.n = true;
      usernameField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 25, this.height / 2 - 40, 120, 20);
      usernameField.w = true;
      usernameField.setText("");
      passwordField = new PasswordField(0, this.width / 2 - 25, this.height / 2 - 20, 120, 20);
      passwordField.w = true;
      passwordField.Z(true);
      passwordField.setMaxStringLength(128);
      passwordField.setText("");
      this.Y = true;

      try {
         this.l();
      } catch (FileNotFoundException var5) {
         var5.printStackTrace();
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      boolean var8 = this.mc.autoLoginToken != null && !this.mc.autoLoginToken.isEmpty();
      if (var8) {
         passwordField.setVisible(false);
         usernameField.setVisible(false);
      } else {
         passwordField.setVisible(true);
         usernameField.setVisible(true);
      }

      GlStateManager.disableAlpha();
      this.w(var1, var2, var3);
      GlStateManager.enableAlpha();
      if (this.Y) {
         this.P.rewindAndStop().rewind().forwards().start();
         this.Y = false;
      }

      int var9 = this.width / 2;
      int var10 = this.height / 2;
      int var11 = (int)this.P.getValue(40.0, 255.0);
      int var12 = (int)this.P.getValue(40.0, 120.0);
      int var13 = (int)this.P.getValue(40.0, 170.0);
      RenderUtil.drawBorderedRect(
         var9 - 105, var10 - 57, var9 + 105, var10 + 55, 1.0F, new Color(17, 17, 17, var12).getRGB(), new Color(17, 17, 17, var13).getRGB()
      );
      drawRect(var9 - 105, var10 - 57, var9 + 105, var10 - 55, new Color(164, 54, 145, var11).getRGB());
      drawRect(var9 - 105, var10 - 56, var9 + 105, var10 - 55, new Color(164, 54, 145, var11).darker().getRGB());
      if (var8) {
         this.w(var9, var10 - 17);
      } else {
         this.w(var9 - 63, var10 - 17);
      }

      e.t = var13;
      passwordField.I = var13;
      usernameField.I = var13;
      usernameField.drawTextBox();
      passwordField.drawTextBox();
      if (!var8) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, var11 / 255.0F);
         GuiMainMenu.G(new ResourceLocation("client/login/username.png"), 6.0F, var9 + 83, var10 - 35);
         GlStateManager.color(1.0F, 1.0F, 1.0F, var11 / 255.0F);
         GuiMainMenu.G(new ResourceLocation("client/login/password.png"), 6.0F, var9 + 83, var10 - 17);
      }

      String var14;
      switch ((int)(Minecraft.getSystemTime() / 300L % 4L)) {
         case 0:
         default:
            var14 = "...";
            break;
         case 1:
         case 3:
            var14 = "..";
            break;
         case 2:
            var14 = ".";
      }

      String var15 = (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) && this.mc.autoLoginToken.isEmpty()
         ? "Please input your username and password"
         : (
            usernameField.getText().contains("@")
               ? "Use the username instead of mail"
               : (
                  p
                     ? "Authenticating" + var14
                     : (
                        !d
                           ? (this.mc.autoLoginToken.isEmpty() ? LoginScreenStrings.s("yS1qY8zuVJ5XAIbRQWoOMrYVhYHPu8Twrsg=") : "Auto-Login ready")
                           : (
                              x.contains("FAILED_INVALID_HWID")
                                 ? "HWID Changed, open a support request in the forums for reset"
                                 : (
                                    x.contains("FAILED_INVALID_USER")
                                       ? "Username not found"
                                       : (x.contains("FAILED_INCORRECT_PASS") ? "Incorrect password" : "Unknown")
                                 )
                           )
                     )
               )
         );
      GL11.glEnable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      this.mc
         .fontRendererObj
         .drawStringWithShadow(
            EnumChatFormatting.GRAY + var15,
            this.width / 2 - this.mc.fontRendererObj.getStringWidth((java.lang.String)(var15)) / 2,
            this.height / 2 - 85,
            new Color(255, 255, 255, var11).getRGB()
         );
      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      Keyboard.enableRepeatEvents(true);
      usernameField.textboxKeyTyped(var1, var2);
      passwordField.textboxKeyTyped(var1, var2);
      if (var2 == 15) {
         usernameField.setFocused(!usernameField.isFocused());
         passwordField.setFocused(!passwordField.isFocused());
      } else if (var2 == 28) {
         try {
            this.k();
         } catch (Exception var7) {
            this.mc.shutdown();
         }
      }
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      usernameField.mouseClicked(var1, var2, var3);
      passwordField.mouseClicked(var1, var2, var3);
      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void updateScreen() {
      this.mc.displayGuiScreen(new GuiMainMenu());
      if (N.hasElapsed(5000.0F) && p) {
         p = false;
      }

      this.y++;
      usernameField.updateCursorCounter();
      passwordField.updateCursorCounter();
      super.updateScreen();
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if ((!usernameField.getText().isEmpty() || !this.mc.autoLoginToken.isEmpty())
         && !usernameField.getText().contains("@")
         && (!passwordField.getText().isEmpty() || !this.mc.autoLoginToken.isEmpty())
         && !V
         && !p) {
         super.actionPerformed(var1);
      }
   }

   private void k() {
   }

   private void w(int var1, int var2) {
      int var7 = var1 - 32;
      int var8 = var2 - 32;
      GL11.glEnable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/logo.png"));
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      int var9 = (int)this.P.getValue(0.0, 255.0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, var9 / 255.0F);
      drawModalRectWithCustomSizedTexture(var7, var8, 0.0F, 0.0F, 64, 64, 64.0F, 64.0F);
   }

   private void l() throws FileNotFoundException {
      try {
         Path var4 = Astolfo.INSTANCE.getDirectory().getAbsoluteFile().toPath().resolve(LoginScreenStrings.s("/T5mc9Y="));
         BufferedReader var5 = null;

         try {
            var5 = new BufferedReader(new FileReader(var4.toFile()));

            String var6;
            while ((var6 = var5.readLine()) != null) {
               String var7 = this.b(var6, AuthUtil.getHwid());
               if (var7 != null) {
                  String[] var8 = var7.split(LoginScreenStrings.s("pA=="));
                  if (var8[0] != null) {
                     usernameField.setText(var8[0]);
                  }

                  if (var8[1] != null) {
                     passwordField.setText(var8[1]);
                  }
               }
            }
         } catch (IOException var9) {
         }
      } catch (Exception var10) {
      }
   }

   private String b(String var1, String var2) {
      try {
         x(var2);
         Cipher var5 = Cipher.getInstance(LoginScreenStrings.s("3wlQOODDcZFhJLeiFU87Hpcyv68="));
         var5.init(2, f);
         return new String(var5.doFinal(Base64.getDecoder().decode(var1)));
      } catch (Exception var6) {
         return null;
      }
   }

   public static void x(String var0) {
      try {
         n = var0.getBytes(StandardCharsets.UTF_8);
         MessageDigest var3 = MessageDigest.getInstance(LoginScreenStrings.s("zQRCOpQ="));
         n = var3.digest(n);
         n = Arrays.copyOf(n, 16);
         f = new SecretKeySpec(n, LoginScreenStrings.s("3wlQ"));
      } catch (NoSuchAlgorithmException var5) {
         var5.printStackTrace();
      }
   }

   private void D(int var1, int var2, float var3) {
      long var4 = c ^ 97142252523975L;
      Tessellator var7 = Tessellator.getInstance();
      WorldRenderer var8 = var7.getWorldRenderer();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GlStateManager.matrixMode(5888);
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
         GlStateManager.rotate(MathHelper.sin((this.y + var3) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-(this.y + var3) * 0.1F, 0.0F, 1.0F, 0.0F);

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

            this.mc.getTextureManager().bindTexture(a[var14]);
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
      long var10000 = 7412287223944421437L ^ var4;
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      var10000 = 2333127409049685090L ^ var4;
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.enableDepth();
   }

   private void y(float var1) {
      this.mc.getTextureManager().bindTexture(this.q);
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

   private void w(int var1, int var2, float var3) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GlStateManager.viewport(0, 0, 256, 256);
      this.D(var1, var2, var3);
      this.y(var3);
      this.y(var3);
      this.y(var3);
      this.y(var3);
      this.y(var3);
      this.y(var3);
      this.y(var3);
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
}
