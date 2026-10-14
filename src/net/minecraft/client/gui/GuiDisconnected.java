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
   private float B;
   private Stopwatch P;
   private GuiButton p;
   private GuiButton l;
   private GuiButton R;
   private boolean V;
   private String c;
   private String Y;
   private boolean N;
   public static int b;
   private static final long d = 73680682158096L;

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
      this.p = new GuiButton(1, this.width / 2 - 100, this.height / 2 + 21 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, String.valueOf(this.B));
      this.p.enabled = false;
      this.buttonList.add(this.p);
      GuiButton guiButton = new GuiButton(
         2, this.width / 2 - 100, this.height / 2 + 42 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, 100, 20, "Reconnect with alt"
      );
      AlteningScreen.W().d(AlteningScreen.b);
      if ((AlteningScreen.W().O().equals("nono") || AlteningScreen.W().O().isEmpty()) && b == 0) {
         guiButton.enabled = false;
      }

      this.buttonList.add(this.R = guiButton);
      this.buttonList
         .add(
            this.l = new GuiButton(
               3,
               this.width / 2 + 1,
               this.height / 2 + 42 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT,
               99,
               20,
               "Alt type: " + (b == 0 ? "Altening" : (b == 1 ? "random" : (b == 2 ? "unbanned" : (b == 3 ? "username" : "clipboard"))))
            )
         );
      if (GuiIngestServers.f) {
         this.buttonList
            .add(new GuiButton(5, this.width / 2 - 100, this.height / 2 + 63 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, "Buy accounts"));
      }

      this.P = new Stopwatch();
      Alt alt2 = this.F(AltLogin.AUTH.lastLoggedAlt);
      if (alt2 != null) {
         for (String string : this.multilineMessage) {
            if (string.contains("You are temporarily banned")) {
               Date date = new Date();
               Calendar calendar = Calendar.getInstance();
               calendar.setTime(date);
               String string2 = StringUtils.stripControlCodes(H(string, "for ", "from"));

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

               alt2.m = calendar.getTime().getTime();
               if (!alt2.N) {
                  Toasts.TOASTS.X("Marked account as banned", AltLogin.AUTH.lastLoggedAlt.j + " is now marked as temporarily banned");
               }

               alt2.N = true;
            }

            if (string.contains("You are permanently banned") && !alt2.N) {
               alt2.N = true;
               Toasts.TOASTS.X("Marked account as banned", AltLogin.AUTH.lastLoggedAlt.j + " is now marked as banned");
            }
         }
      }
   }

   public static String H(String string, String string2, String string3) {
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
      long l = d ^ 71812122919810L;
      long m = l ^ 62083882695697L;
      if (this.Y != null && !this.Y.isEmpty() && this.V) {
         Alt alt = new Alt(this.Y, "");
         AltLogin.AUTH.A(alt);
         List list = Astolfo.INSTANCE.getAltManager().getList();
         if (AlteningScreen.f) {
            list.add(alt);
         }

         this.Y = "";
      }

      if (this.V && !this.c.equals(this.mc.session.getUsername())) {
         this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, new ServerData("i said nigga please", ServerData.I, false)));
      }

      if (this.P != null) {
         this.B = (float)(this.P.getElapsed() / 1000.0);
         this.p.displayString = String.format("Reconnect (%s)", MathUtil.ONE_DECIMAL.format(this.B));
         if (this.P.hasElapsed(5000.0F)) {
            this.p.displayString = String.format("Reconnect to §7%s", ServerData.I);
            this.p.enabled = true;
            this.B = 0.0F;
         }
      }

      super.updateScreen();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      long m = d ^ 106409378218484L;
      long n = m ^ 28451119922791L;
      if (button.id == 0) {
         this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
      } else if (button.id == 1) {
         this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, new ServerData("i said nigga please", ServerData.I, false)));
      } else if (button.id == 2) {
         this.c = this.mc.session.getUsername();
         switch (b) {
            case 0:
               if (AlteningScreen.W().O().equals("nono") || AlteningScreen.W().O().isEmpty()) {
                  this.R.enabled = false;
               }

               try {
                  this.N = false;
                  if (AlteningScreen.r().V().equals(AccountType.MOJANG)) {
                     AlteningScreen.r().g(AccountType.THEALTENING);
                  }

                  FileUtil.w.execute(() -> {
                     try {
                        this.Y = AlteningScreen.W().i(AlteningScreen.W().t()).f();
                     } catch (IOException ioException) {
                        this.Y = "";
                        this.N = true;
                     }
                  });
                  this.V = true;
               } catch (NoSuchFieldException | IllegalAccessException value) {
                  this.V = false;
                  this.Y = "";
                  value.printStackTrace();
               }
               break;
            case 1:
               this.R.enabled = true;
               Alt alt = Astolfo.INSTANCE.getAltManager().getList().get(RandomUtils.RANDOM.nextInt(Astolfo.INSTANCE.getAltManager().getList().size()));
               if (alt != null) {
                  AltLogin.AUTH.A(alt);
                  this.V = true;
               }
               break;
            case 2:
               this.R.enabled = true;
               if (AlteningScreen.r().V().equals(AccountType.THEALTENING)) {
                  try {
                     AlteningScreen.r().g(AccountType.MOJANG);
                  } catch (NoSuchFieldException | IllegalAccessException value2) {
                     value2.printStackTrace();
                  }
               }

               ArrayList arrayList = Astolfo.INSTANCE.getAltManager().e();
               if (arrayList.size() > 0) {
                  Alt alt2 = (Alt)arrayList.get(0);
                  if (alt2 != null) {
                     AltLogin.AUTH.A(alt2);
                     this.V = true;
                  }
               }
               break;
            case 3:
               this.R.enabled = true;
               AltLogin.AUTH.A(new Alt(RandomStringUtils.random(8, true, false), ""));
               break;
            case 4:
               this.R.enabled = true;

               try {
                  String string = GuiScreen.getClipboardString();
                  String[] strings = string.split(":");
                  AltLogin.AUTH.A(new Alt(strings[0], strings[1]));
               } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
               }
         }
      } else if (button.id == 3) {
         b++;
         if (b > 4) {
            b = 0;
         }

         this.l.displayString = "Alt type: " + (b == 0 ? "Altening" : (b == 1 ? "random" : (b == 2 ? "unbanned" : (b == 3 ? "username" : "clipboard"))));
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
      this.d(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
      int i = this.height / 2 - this.field_175353_i / 2;
      if (this.multilineMessage != null) {
         for (String string : this.multilineMessage) {
            this.d(this.fontRendererObj, string, this.width / 2, i, 16777215);
            i += this.fontRendererObj.FONT_HEIGHT;
         }
      }

      if (this.N) {
         this.mc.fontRendererObj.drawStringWithShadow("Altening login has failed, maintenance or plan?", 3.0F, 3.0F, Color.RED.getRGB());
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private Alt F(Alt alt) {
      long l = d ^ 64085456306532L;
      long m = l ^ 74140145827575L;
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
      this.Y = "";
      this.c = "";
      this.V = false;
      super.onGuiClosed();
   }
}
