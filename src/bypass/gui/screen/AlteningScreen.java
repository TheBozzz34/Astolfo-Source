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
   private final GuiScreen d;
   public static String b = "";
   public static boolean f;
   private AlteningSsl x;
   private static AlteningApi q = new AlteningApi("nono");
   private static final AuthServiceSwitcher p = new AuthServiceSwitcher();
   private PasswordField w;
   private GuiButton u;
   private Checkbox B;
   private String P;
   private boolean n;

   public AlteningScreen(GuiScreen guiScreen) {
      this.d = guiScreen;
      this.x = new AlteningSsl();
      this.x.H();
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.w = new PasswordField(69, this.width / 2 - 85, this.height / 2 - 50, 190, 20);
      this.w.setFocused(false);
      this.w.setMaxStringLength(512);
      this.w.Z(true);
      this.w.setText(b);
      this.buttonList.add(new GuiButton(0, this.width / 2 - 55, this.height / 2 - 15, 125, 20, "Generate"));
      this.buttonList.add(new GuiButton(2, 2, this.height - 22, 50, 20, "<-"));
      String string = "Add account";
      this.B = new Checkbox(string, this.width / 2 - (15 + this.fontRendererObj.getStringWidth(string)) / 2, this.height / 2 + 15, true);
      this.B.K(f);
      this.P = "";
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
      this.B.E(mouseX, mouseY);
      this.w.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      this.w.textboxKeyTyped(typedChar, keyCode);
      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.w.mouseClicked(mouseX, mouseY, mouseButton);
      this.B.b(mouseX, mouseY, mouseButton);
      f = this.B.a();
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void updateScreen() {
      if (!this.P.isEmpty()) {
         try {
            Alt alt = new Alt(this.P, "");
            AltLogin.AUTH.A(alt);
            b = this.w.getText();
            List list = Astolfo.INSTANCE.getAltManager().getList();
            if (f) {
               list.add(alt);
            }
         } catch (Exception exception) {
         }

         this.P = "";
      }

      this.w.updateCursorCounter();
      b = this.w.getText();
      super.updateScreen();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      W().d(b);
      switch (button.id) {
         case 0:
            if (!r().V().equals(AccountType.THEALTENING)) {
               try {
                  r().g(AccountType.THEALTENING);
               } catch (NoSuchFieldException | IllegalAccessException value) {
                  this.x.m(false);
                  value.printStackTrace();
               }
            }

            new Thread(() -> {
               try {
                  this.P = W().i(W().t()).f();
               } catch (IOException ioException) {
                  this.P = "";
                  this.n = true;
               }
            }).start();
            break;
         case 1:
            try {
               if (r().V().equals(AccountType.MOJANG)) {
                  r().g(AccountType.THEALTENING);
               } else if (r().V().equals(AccountType.THEALTENING)) {
                  r().g(AccountType.MOJANG);
               }
            } catch (NoSuchFieldException | IllegalAccessException value2) {
               value2.printStackTrace();
            }
            break;
         case 2:
            Minecraft.getMinecraft().displayGuiScreen(this.d);
      }

      super.actionPerformed(button);
   }

   @Override
   public void onGuiClosed() {
      this.P = "";
      this.n = false;
      super.onGuiClosed();
   }

   public static AlteningApi W() {
      return q;
   }

   public static AuthServiceSwitcher r() {
      return p;
   }
}
