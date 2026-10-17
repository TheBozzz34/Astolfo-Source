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
   private final Animation fadeInAnimation = new TimedAnimation().setDuration(750).setEasing(Easing.LINEAR);
   public static GuiButton loginButton;
   public static GuiTextField usernameField;
   public static PasswordField passwordField;
   public static boolean loggedIn;
   public int unusedSpacing = 12;
   public int unusedInt = 40;
   private int panoramaTimer;
   private ResourceLocation backgroundTexture;
   public static final Stopwatch authTimer = new Stopwatch();
   private static final ResourceLocation[] panoramaTextures = new ResourceLocation[]{
      new ResourceLocation("textures/gui/title/background/panorama_0.png"),
      new ResourceLocation("textures/gui/title/background/panorama_1.png"),
      new ResourceLocation("textures/gui/title/background/panorama_2.png"),
      new ResourceLocation("textures/gui/title/background/panorama_3.png"),
      new ResourceLocation("textures/gui/title/background/panorama_4.png"),
      new ResourceLocation("textures/gui/title/background/panorama_5.png")
   };
   private boolean startFadeIn;
   public static boolean loginFailed;
   public static boolean authenticating;
   public static String loginResponseMessage;
   public static String unusedString1;
   public static String unusedString2;
   private static SecretKeySpec credentialsKey;
   private static byte[] credentialsKeyBytes;

   @Override
   public void initGui() {
      DynamicTexture dynamicTexture = new DynamicTexture(256, 256);
      this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", dynamicTexture);
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(loginButton = new GuiButton(-88888, this.width / 2 - 95, this.height / 2 + 25, 191, 20, "Login"));
      loginButton.useCustomAlpha = true;
      usernameField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 25, this.height / 2 - 40, 120, 20);
      usernameField.useCustomAlpha = true;
      usernameField.setText("");
      passwordField = new PasswordField(0, this.width / 2 - 25, this.height / 2 - 20, 120, 20);
      passwordField.useCustomAlpha = true;
      passwordField.setPasswordMode(true);
      passwordField.setMaxStringLength(128);
      passwordField.setText("");
      this.startFadeIn = true;

      try {
         this.loadSavedCredentials();
      } catch (FileNotFoundException fileNotFoundException) {
         fileNotFoundException.printStackTrace();
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      boolean bl = this.mc.autoLoginToken != null && !this.mc.autoLoginToken.isEmpty();
      if (bl) {
         passwordField.setVisible(false);
         usernameField.setVisible(false);
      } else {
         passwordField.setVisible(true);
         usernameField.setVisible(true);
      }

      GlStateManager.disableAlpha();
      this.renderSkybox(mouseX, mouseY, partialTicks);
      GlStateManager.enableAlpha();
      if (this.startFadeIn) {
         this.fadeInAnimation.rewindAndStop().rewind().forwards().start();
         this.startFadeIn = false;
      }

      int i = this.width / 2;
      int j = this.height / 2;
      int k = (int)this.fadeInAnimation.getValue(40.0, 255.0);
      int l = (int)this.fadeInAnimation.getValue(40.0, 120.0);
      int m = (int)this.fadeInAnimation.getValue(40.0, 170.0);
      RenderUtil.drawBorderedRect(
         i - 105, j - 57, i + 105, j + 55, 1.0F, new Color(17, 17, 17, l).getRGB(), new Color(17, 17, 17, m).getRGB()
      );
      drawRect(i - 105, j - 57, i + 105, j - 55, new Color(164, 54, 145, k).getRGB());
      drawRect(i - 105, j - 56, i + 105, j - 55, new Color(164, 54, 145, k).darker().getRGB());
      if (bl) {
         this.drawLogo(i, j - 17);
      } else {
         this.drawLogo(i - 63, j - 17);
      }

      loginButton.customAlpha = m;
      passwordField.customAlpha = m;
      usernameField.customAlpha = m;
      usernameField.drawTextBox();
      passwordField.drawTextBox();
      if (!bl) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, k / 255.0F);
         GuiMainMenu.drawIcon(new ResourceLocation("client/login/username.png"), 6.0F, i + 83, j - 35);
         GlStateManager.color(1.0F, 1.0F, 1.0F, k / 255.0F);
         GuiMainMenu.drawIcon(new ResourceLocation("client/login/password.png"), 6.0F, i + 83, j - 17);
      }

      String string;
      switch ((int)(Minecraft.getSystemTime() / 300L % 4L)) {
         case 0:
         default:
            string = "...";
            break;
         case 1:
         case 3:
            string = "..";
            break;
         case 2:
            string = ".";
      }

      String string2 = (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) && this.mc.autoLoginToken.isEmpty()
         ? "Please input your username and password"
         : (
            usernameField.getText().contains("@")
               ? "Use the username instead of mail"
               : (
                  authenticating
                     ? "Authenticating" + string
                     : (
                        !loginFailed
                           ? (this.mc.autoLoginToken.isEmpty() ? "Waiting for authentication" : "Auto-Login ready")
                           : (
                              loginResponseMessage.contains("FAILED_INVALID_HWID")
                                 ? "HWID Changed, open a support request in the forums for reset"
                                 : (
                                    loginResponseMessage.contains("FAILED_INVALID_USER")
                                       ? "Username not found"
                                       : (loginResponseMessage.contains("FAILED_INCORRECT_PASS") ? "Incorrect password" : "Unknown")
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
            EnumChatFormatting.GRAY + string2,
            this.width / 2 - this.mc.fontRendererObj.getStringWidth((java.lang.String)(string2)) / 2,
            this.height / 2 - 85,
            new Color(255, 255, 255, k).getRGB()
         );
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      Keyboard.enableRepeatEvents(true);
      usernameField.textboxKeyTyped(typedChar, keyCode);
      passwordField.textboxKeyTyped(typedChar, keyCode);
      if (keyCode == 15) {
         usernameField.setFocused(!usernameField.isFocused());
         passwordField.setFocused(!passwordField.isFocused());
      } else if (keyCode == 28) {
         try {
            this.submitLogin();
         } catch (Exception exception) {
            this.mc.shutdown();
         }
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      usernameField.mouseClicked(mouseX, mouseY, mouseButton);
      passwordField.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void updateScreen() {
      this.mc.displayGuiScreen(new GuiMainMenu());
      if (authTimer.hasElapsed(5000.0F) && authenticating) {
         authenticating = false;
      }

      this.panoramaTimer++;
      usernameField.updateCursorCounter();
      passwordField.updateCursorCounter();
      super.updateScreen();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if ((!usernameField.getText().isEmpty() || !this.mc.autoLoginToken.isEmpty())
         && !usernameField.getText().contains("@")
         && (!passwordField.getText().isEmpty() || !this.mc.autoLoginToken.isEmpty())
         && !loggedIn
         && !authenticating) {
         super.actionPerformed(button);
      }
   }

   private void submitLogin() {
   }

   private void drawLogo(int i, int j) {
      int k = i - 32;
      int l = j - 32;
      GL11.glEnable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/logo.png"));
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      int m = (int)this.fadeInAnimation.getValue(0.0, 255.0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, m / 255.0F);
      drawModalRectWithCustomSizedTexture(k, l, 0.0F, 0.0F, 64, 64, 64.0F, 64.0F);
   }

   private void loadSavedCredentials() throws FileNotFoundException {
      try {
         Path path = Astolfo.INSTANCE.getDirectory().getAbsoluteFile().toPath().resolve("creds");
         BufferedReader bufferedReader = null;

         try {
            bufferedReader = new BufferedReader(new FileReader(path.toFile()));

            String string;
            while ((string = bufferedReader.readLine()) != null) {
               String string2 = this.decryptCredentials(string, AuthUtil.getHwid());
               if (string2 != null) {
                  String[] strings = string2.split(":");
                  if (strings[0] != null) {
                     usernameField.setText(strings[0]);
                  }

                  if (strings[1] != null) {
                     passwordField.setText(strings[1]);
                  }
               }
            }
         } catch (IOException ioException) {
         }
      } catch (Exception exception) {
      }
   }

   private String decryptCredentials(String string, String string2) {
      try {
         initCredentialsKey(string2);
         Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
         cipher.init(2, credentialsKey);
         return new String(cipher.doFinal(Base64.getDecoder().decode(string)));
      } catch (Exception exception) {
         return null;
      }
   }

   public static void initCredentialsKey(String string) {
      try {
         credentialsKeyBytes = string.getBytes(StandardCharsets.UTF_8);
         MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
         credentialsKeyBytes = messageDigest.digest(credentialsKeyBytes);
         credentialsKeyBytes = Arrays.copyOf(credentialsKeyBytes, 16);
         credentialsKey = new SecretKeySpec(credentialsKeyBytes, "AES");
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
         noSuchAlgorithmException.printStackTrace();
      }
   }

   private void drawPanorama(int i, int j, float f) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
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
      byte b = 8;

      for (int k = 0; k < b * b; k++) {
         GlStateManager.pushMatrix();
         float g = ((float)(k % b) / b - 0.5F) / 64.0F;
         float h = ((float)(k / b) / b - 0.5F) / 64.0F;
         float f2 = 0.0F;
         GlStateManager.translate(g, h, f2);
         GlStateManager.rotate(MathHelper.sin((this.panoramaTimer + f) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-(this.panoramaTimer + f) * 0.1F, 0.0F, 1.0F, 0.0F);

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

            this.mc.getTextureManager().bindTexture(panoramaTextures[l]);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int m = 255 / (k + 1);
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

   private void rotateAndBlurSkybox(float f) {
      this.mc.getTextureManager().bindTexture(this.backgroundTexture);
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
         float g = 1.0F / (i + 1);
         int j = this.width;
         int k = this.height;
         float h = (i - b / 2) / 256.0F;
         worldRenderer.pos(j, k, this.zLevel).tex(0.0F + h, 1.0).color(1.0F, 1.0F, 1.0F, g).endVertex();
         worldRenderer.pos(j, 0.0, this.zLevel).tex(1.0F + h, 1.0).color(1.0F, 1.0F, 1.0F, g).endVertex();
         worldRenderer.pos(0.0, 0.0, this.zLevel).tex(1.0F + h, 0.0).color(1.0F, 1.0F, 1.0F, g).endVertex();
         worldRenderer.pos(0.0, k, this.zLevel).tex(0.0F + h, 0.0).color(1.0F, 1.0F, 1.0F, g).endVertex();
      }

      tessellator.draw();
      GlStateManager.enableAlpha();
      GlStateManager.colorMask(true, true, true, true);
   }

   private void renderSkybox(int i, int j, float f) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GlStateManager.viewport(0, 0, 256, 256);
      this.drawPanorama(i, j, f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.mc.getFramebuffer().bindFramebuffer(true);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      float g = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
      float h = this.height * g / 256.0F;
      float f2 = this.width * g / 256.0F;
      int k = this.width;
      int l = this.height;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(0.0, l, this.zLevel).tex(0.5F - h, 0.5F + f2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(k, l, this.zLevel).tex(0.5F - h, 0.5F - f2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(k, 0.0, this.zLevel).tex(0.5F + h, 0.5F - f2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(0.0, 0.0, this.zLevel).tex(0.5F + h, 0.5F + f2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      tessellator.draw();
   }
}
