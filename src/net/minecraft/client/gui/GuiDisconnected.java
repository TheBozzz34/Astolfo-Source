package net.minecraft.client.gui;

import bypass.Astolfo;
import bypass.Toasts;
import bypass.account.AccountType;
import bypass.account.Alt;
import bypass.account.AltLogin;
import bypass.gui.screen.AlteningScreen;
import bypass.util.FileUtil;
import bypass.util.MathUtil;
import bypass.util.RandomUtils;
import bypass.util.Stopwatch;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import net.minecraft.client.gui.stream.GuiIngestServers;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class GuiDisconnected extends GuiScreen {
   private String reason;
   private IChatComponent message;
   private List<String> multilineMessage;
   private final GuiScreen parentScreen;
   private int field_175353_i;
   private float reconnectSeconds;
   private Stopwatch reconnectTimer;
   private GuiButton reconnectButton;
   private GuiButton altSourceButton;
   private GuiButton reconnectWithAltButton;
   private boolean reconnectPending;
   private String previousUsername;
   private String alteningToken;
   private boolean alteningLoginFailed;
   public static int altSourceMode;

   public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
      this.parentScreen = screen;
      this.reason = I18n.format(reasonLocalizationKey);
      this.message = chatComp;
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
      this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
      this.buttonList
         .add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu")));
      this.reconnectButton = new GuiButton(
         1, this.width / 2 - 100, this.height / 2 + 21 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, String.valueOf(this.reconnectSeconds)
      );
      this.reconnectButton.enabled = false;
      this.buttonList.add(this.reconnectButton);
      GuiButton guiButton = new GuiButton(
         2, this.width / 2 - 100, this.height / 2 + 42 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, 100, 20, "Reconnect with alt"
      );
      AlteningScreen.getAlteningApi().setApiKey(AlteningScreen.apiKey);
      if ((AlteningScreen.getAlteningApi().getApiKey().equals("nono") || AlteningScreen.getAlteningApi().getApiKey().isEmpty()) && altSourceMode == 0) {
         guiButton.enabled = false;
      }

      this.buttonList.add(this.reconnectWithAltButton = guiButton);
      this.buttonList
         .add(
            this.altSourceButton = new GuiButton(
               3,
               this.width / 2 + 1,
               this.height / 2 + 42 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT,
               99,
               20,
               "Alt type: "
                  + (
                     altSourceMode == 0
                        ? "Altening"
                        : (altSourceMode == 1 ? "random" : (altSourceMode == 2 ? "unbanned" : (altSourceMode == 3 ? "username" : "clipboard")))
                  )
            )
         );
      if (GuiIngestServers.showBuyAccounts) {
         this.buttonList
            .add(new GuiButton(5, this.width / 2 - 100, this.height / 2 + 63 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, "Buy accounts"));
      }

      this.reconnectTimer = new Stopwatch();
      Alt alt2 = this.findStoredAlt(AltLogin.AUTH.lastLoggedAlt);
      if (alt2 != null) {
         for (String string : this.multilineMessage) {
            if (string.contains("You are temporarily banned")) {
               Date date = new Date();
               Calendar calendar = Calendar.getInstance();
               calendar.setTime(date);
               String string2 = StringUtils.stripControlCodes(substringBetween(string, "for ", "from"));

               for (String string3 : string2.split(" ")) {
                  String string4 = string3.replaceAll("[^\\d.]", "");
                  if (string3.endsWith("d")) {
                     calendar.add(5, Integer.parseInt(string4));
                  }

                  if (string3.endsWith("h")) {
                     calendar.add(10, Integer.parseInt(string4));
                  }

                  if (string3.endsWith("m")) {
                     calendar.add(12, Integer.parseInt(string4));
                  }

                  if (string3.endsWith("s")) {
                     calendar.add(13, Integer.parseInt(string4));
                  }
               }

               alt2.banExpiry = calendar.getTime().getTime();
               if (!alt2.banned) {
                  Toasts.TOASTS.warningMessage("Marked account as banned", AltLogin.AUTH.lastLoggedAlt.username + " is now marked as temporarily banned");
               }

               alt2.banned = true;
            }

            if (string.contains("You are permanently banned") && !alt2.banned) {
               alt2.banned = true;
               Toasts.TOASTS.warningMessage("Marked account as banned", AltLogin.AUTH.lastLoggedAlt.username + " is now marked as banned");
            }
         }
      }
   }

   public static String substringBetween(String string, String string2, String string3) {
      int i = string.indexOf(string2);
      if (i == -1) {
         return "";
      }

      int j = string.lastIndexOf(string3);
      if (j == -1) {
         return "";
      }

      int k = i + string2.length();
      return k >= j ? "" : string.substring(k, j);
   }

   @Override
   public void updateScreen() {
      if (this.alteningToken != null && !this.alteningToken.isEmpty() && this.reconnectPending) {
         Alt alt = new Alt(this.alteningToken, "");
         AltLogin.AUTH.loginAlt(alt);
         List list = Astolfo.INSTANCE.getAltManager().getList();
         if (AlteningScreen.addToAltList) {
            list.add(alt);
         }

         this.alteningToken = "";
      }

      if (this.reconnectPending && !this.previousUsername.equals(this.mc.session.getUsername())) {
         this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, new ServerData("i said nigga please", ServerData.currentServerIp, false)));
      }

      if (this.reconnectTimer != null) {
         this.reconnectSeconds = (float)(this.reconnectTimer.getElapsed() / 1000.0);
         this.reconnectButton.displayString = String.format("Reconnect (%s)", MathUtil.ONE_DECIMAL.format(this.reconnectSeconds));
         if (this.reconnectTimer.hasElapsed(5000.0F)) {
            this.reconnectButton.displayString = String.format("Reconnect to §7%s", ServerData.currentServerIp);
            this.reconnectButton.enabled = true;
            this.reconnectSeconds = 0.0F;
         }
      }

      super.updateScreen();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
      } else if (button.id == 1) {
         this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, new ServerData("i said nigga please", ServerData.currentServerIp, false)));
      } else if (button.id == 2) {
         this.previousUsername = this.mc.session.getUsername();
         switch (altSourceMode) {
            case 0:
               if (AlteningScreen.getAlteningApi().getApiKey().equals("nono") || AlteningScreen.getAlteningApi().getApiKey().isEmpty()) {
                  this.reconnectWithAltButton.enabled = false;
               }

               try {
                  this.alteningLoginFailed = false;
                  if (AlteningScreen.getAuthServiceSwitcher().getAccountType().equals(AccountType.MOJANG)) {
                     AlteningScreen.getAuthServiceSwitcher().setAccountType(AccountType.THEALTENING);
                  }

                  FileUtil.executor.execute(() -> {
                     try {
                        this.alteningToken = AlteningScreen.getAlteningApi().generateAccount(AlteningScreen.getAlteningApi().getLicense()).getToken();
                     } catch (IOException ioException) {
                        this.alteningToken = "";
                        this.alteningLoginFailed = true;
                     }
                  });
                  this.reconnectPending = true;
               } catch (NoSuchFieldException | IllegalAccessException value) {
                  this.reconnectPending = false;
                  this.alteningToken = "";
                  value.printStackTrace();
               }
               break;
            case 1:
               this.reconnectWithAltButton.enabled = true;
               Alt alt = Astolfo.INSTANCE.getAltManager().getList().get(RandomUtils.RANDOM.nextInt(Astolfo.INSTANCE.getAltManager().getList().size()));
               if (alt != null) {
                  AltLogin.AUTH.loginAlt(alt);
                  this.reconnectPending = true;
               }
               break;
            case 2:
               this.reconnectWithAltButton.enabled = true;
               if (AlteningScreen.getAuthServiceSwitcher().getAccountType().equals(AccountType.THEALTENING)) {
                  try {
                     AlteningScreen.getAuthServiceSwitcher().setAccountType(AccountType.MOJANG);
                  } catch (NoSuchFieldException | IllegalAccessException value2) {
                     value2.printStackTrace();
                  }
               }

               ArrayList arrayList = Astolfo.INSTANCE.getAltManager().getUsableAlts();
               if (arrayList.size() > 0) {
                  Alt alt2 = (Alt)arrayList.get(0);
                  if (alt2 != null) {
                     AltLogin.AUTH.loginAlt(alt2);
                     this.reconnectPending = true;
                  }
               }
               break;
            case 3:
               this.reconnectWithAltButton.enabled = true;
               AltLogin.AUTH.loginAlt(new Alt(RandomStringUtils.random(8, true, false), ""));
               break;
            case 4:
               this.reconnectWithAltButton.enabled = true;

               try {
                  String string = GuiScreen.getClipboardString();
                  String[] strings = string.split(":");
                  AltLogin.AUTH.loginAlt(new Alt(strings[0], strings[1]));
               } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
               }
         }
      } else if (button.id == 3) {
         altSourceMode++;
         if (altSourceMode > 4) {
            altSourceMode = 0;
         }

         this.altSourceButton.displayString = "Alt type: "
            + (
               altSourceMode == 0
                  ? "Altening"
                  : (altSourceMode == 1 ? "random" : (altSourceMode == 2 ? "unbanned" : (altSourceMode == 3 ? "username" : "clipboard")))
            );
      } else if (button.id == 5 && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
         try {
            Desktop.getDesktop().browse(new URI("https://shop.thealtening.com/?r=astolfo"));
         } catch (IOException | URISyntaxException value3) {
            value3.printStackTrace();
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(
         this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810
      );
      int i = this.height / 2 - this.field_175353_i / 2;
      if (this.multilineMessage != null) {
         for (String string : this.multilineMessage) {
            this.drawCenteredString(this.fontRendererObj, string, this.width / 2, i, 16777215);
            i += this.fontRendererObj.FONT_HEIGHT;
         }
      }

      if (this.alteningLoginFailed) {
         this.mc.fontRendererObj.drawStringWithShadow("Altening login has failed, maintenance or plan?", 3.0F, 3.0F, Color.RED.getRGB());
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private Alt findStoredAlt(Alt alt) {
      if (alt == null) {
         return null;
      }

      for (Alt alt2 : Astolfo.INSTANCE.getAltManager().getList()) {
         if (alt2.equals(alt)) {
            return alt2;
         }
      }

      return null;
   }

   @Override
   public void onGuiClosed() {
      this.alteningToken = "";
      this.previousUsername = "";
      this.reconnectPending = false;
      super.onGuiClosed();
   }
}
