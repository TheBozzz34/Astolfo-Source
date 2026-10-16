package bypass.gui.screen;

import bypass.Astolfo;
import bypass.ClientAccess;
import bypass.account.AccountType;
import bypass.account.Alt;
import bypass.account.AltLogin;
import bypass.account.AlteningApi;
import bypass.account.AlteningSsl;
import bypass.account.AuthServiceSwitcher;
import bypass.gui.Checkbox;
import bypass.gui.PasswordField;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class AlteningScreen extends GuiScreen implements ClientAccess {
   private final GuiScreen parentScreen;
   public static String apiKey = "";
   public static boolean addToAltList;
   private AlteningSsl alteningSsl;
   private static AlteningApi alteningApi = new AlteningApi("nono");
   private static final AuthServiceSwitcher authServiceSwitcher = new AuthServiceSwitcher();
   private PasswordField apiKeyField;
   private GuiButton unusedButton;
   private Checkbox addAccountCheckbox;
   private String generatedToken;
   private boolean generationFailed;

   public AlteningScreen(GuiScreen guiScreen) {
      this.parentScreen = guiScreen;
      this.alteningSsl = new AlteningSsl();
      this.alteningSsl.initialize();
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.apiKeyField = new PasswordField(69, this.width / 2 - 85, this.height / 2 - 50, 190, 20);
      this.apiKeyField.setFocused(false);
      this.apiKeyField.setMaxStringLength(512);
      this.apiKeyField.setPasswordMode(true);
      this.apiKeyField.setText(apiKey);
      this.buttonList.add(new GuiButton(0, this.width / 2 - 55, this.height / 2 - 15, 125, 20, "Generate"));
      this.buttonList.add(new GuiButton(2, 2, this.height - 22, 50, 20, "<-"));
      String string = "Add account";
      this.addAccountCheckbox = new Checkbox(string, this.width / 2 - (15 + this.fontRendererObj.getStringWidth(string)) / 2, this.height / 2 + 15, true);
      this.addAccountCheckbox.setChecked(addToAltList);
      this.generatedToken = "";
      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Use coupon \"astolfo\" at check-out for a discount in The Altening", 3.0F, 3.0F, -1);
      GL11.glPopMatrix();
      GL11.glEnable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/altening.png"));
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      Gui.drawModalRectWithCustomSizedTexture(this.width / 2 - 25, this.height / 2 - 115, 0.0F, 0.0F, 62, 64, 62.0F, 64.0F);
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow(
            "API key:", this.width / 2 - 115 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("API key:") / 2 + 5, this.height / 2 - 42, -1
         );
      this.addAccountCheckbox.drawCheckbox(mouseX, mouseY);
      this.apiKeyField.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      this.apiKeyField.textboxKeyTyped(typedChar, keyCode);
      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.apiKeyField.mouseClicked(mouseX, mouseY, mouseButton);
      this.addAccountCheckbox.handleMouseClick(mouseX, mouseY, mouseButton);
      addToAltList = this.addAccountCheckbox.isChecked();
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void updateScreen() {
      if (!this.generatedToken.isEmpty()) {
         try {
            Alt alt = new Alt(this.generatedToken, "");
            AltLogin.AUTH.loginAlt(alt);
            apiKey = this.apiKeyField.getText();
            List list = Astolfo.INSTANCE.getAltManager().getList();
            if (addToAltList) {
               list.add(alt);
            }
         } catch (Exception exception) {
         }

         this.generatedToken = "";
      }

      this.apiKeyField.updateCursorCounter();
      apiKey = this.apiKeyField.getText();
      super.updateScreen();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      getAlteningApi().setApiKey(apiKey);
      switch (button.id) {
         case 0:
            if (!getAuthServiceSwitcher().getAccountType().equals(AccountType.THEALTENING)) {
               try {
                  getAuthServiceSwitcher().setAccountType(AccountType.THEALTENING);
               } catch (NoSuchFieldException | IllegalAccessException value) {
                  this.alteningSsl.setInitialized(false);
                  value.printStackTrace();
               }
            }

            new Thread(() -> {
               try {
                  this.generatedToken = getAlteningApi().generateAccount(getAlteningApi().getLicense()).getToken();
               } catch (IOException ioException) {
                  this.generatedToken = "";
                  this.generationFailed = true;
               }
            }).start();
            break;
         case 1:
            try {
               if (getAuthServiceSwitcher().getAccountType().equals(AccountType.MOJANG)) {
                  getAuthServiceSwitcher().setAccountType(AccountType.THEALTENING);
               } else if (getAuthServiceSwitcher().getAccountType().equals(AccountType.THEALTENING)) {
                  getAuthServiceSwitcher().setAccountType(AccountType.MOJANG);
               }
            } catch (NoSuchFieldException | IllegalAccessException value2) {
               value2.printStackTrace();
            }
            break;
         case 2:
            Minecraft.getMinecraft().displayGuiScreen(this.parentScreen);
      }

      super.actionPerformed(button);
   }

   @Override
   public void onGuiClosed() {
      this.generatedToken = "";
      this.generationFailed = false;
      super.onGuiClosed();
   }

   public static AlteningApi getAlteningApi() {
      return alteningApi;
   }

   public static AuthServiceSwitcher getAuthServiceSwitcher() {
      return authServiceSwitcher;
   }
}
