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

   public GuiDisconnected(GuiScreen var1, String var2, IChatComponent var3) {
      this.parentScreen = var1;
      this.reason = I18n.format(var2);
      this.message = var3;
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
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
      GuiButton var4 = new GuiButton(
         2, this.width / 2 - 100, this.height / 2 + 42 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, 100, 20, "Reconnect with alt"
      );
      AlteningScreen.W().d(AlteningScreen.b);
      if ((AlteningScreen.W().O().equals("nono") || AlteningScreen.W().O().isEmpty()) && b == 0) {
         var4.enabled = false;
      }

      this.buttonList.add(this.R = var4);
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
      Alt var5 = this.F(AltLogin.AUTH.lastLoggedAlt);
      if (var5 != null) {
         for (String var7 : this.multilineMessage) {
            if (var7.contains("You are temporarily banned")) {
               Date var8 = new Date();
               Calendar var9 = Calendar.getInstance();
               var9.setTime(var8);
               String var10 = StringUtils.stripControlCodes(H(var7, "for ", "from"));

               for (String var14 : var10.split(" ")) {
                  String var15 = var14.replaceAll("[^\\d.]", "");
                  if (var14.endsWith("d")) {
                     var9.add(5, Integer.parseInt(var15));
                  }

                  if (var14.endsWith("h")) {
                     var9.add(10, Integer.parseInt(var15));
                  }

                  if (var14.endsWith("m")) {
                     var9.add(12, Integer.parseInt(var15));
                  }

                  if (var14.endsWith("s")) {
                     var9.add(13, Integer.parseInt(var15));
                  }
               }

               var5.m = var9.getTime().getTime();
               if (!var5.N) {
                  Toasts.TOASTS.X("Marked account as banned", AltLogin.AUTH.lastLoggedAlt.j + " is now marked as temporarily banned");
               }

               var5.N = true;
            }

            if (var7.contains("You are permanently banned") && !var5.N) {
               var5.N = true;
               Toasts.TOASTS.X("Marked account as banned", AltLogin.AUTH.lastLoggedAlt.j + " is now marked as banned");
            }
         }
      }
   }

   public static String H(String var0, String var1, String var2) {
      int var6 = var0.indexOf(var1);
      if (var6 == -1) {
         return "";
      }

      int var7 = var0.lastIndexOf(var2);
      if (var7 == -1) {
         return "";
      }

      int var8 = var6 + var1.length();
      return var8 >= var7 ? "" : var0.substring(var8, var7);
   }

   @Override
   public void updateScreen() {
      long var1 = d ^ 71812122919810L;
      long var3 = var1 ^ 62083882695697L;
      if (this.Y != null && !this.Y.isEmpty() && this.V) {
         Alt var6 = new Alt(this.Y, "");
         AltLogin.AUTH.A(var6);
         List var7 = Astolfo.INSTANCE.getAltManager().getList();
         if (AlteningScreen.f) {
            var7.add(var6);
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
   public void actionPerformed(GuiButton var1) throws IOException {
      long var2 = d ^ 106409378218484L;
      long var4 = var2 ^ 28451119922791L;
      if (var1.id == 0) {
         this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
      } else if (var1.id == 1) {
         this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, new ServerData("i said nigga please", ServerData.I, false)));
      } else if (var1.id == 2) {
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
                     } catch (IOException var2x) {
                        this.Y = "";
                        this.N = true;
                     }
                  });
                  this.V = true;
               } catch (NoSuchFieldException | IllegalAccessException var14) {
                  this.V = false;
                  this.Y = "";
                  var14.printStackTrace();
               }
               break;
            case 1:
               this.R.enabled = true;
               Alt var7 = Astolfo.INSTANCE.getAltManager().getList().get(RandomUtils.RANDOM.nextInt(Astolfo.INSTANCE.getAltManager().getList().size()));
               if (var7 != null) {
                  AltLogin.AUTH.A(var7);
                  this.V = true;
               }
               break;
            case 2:
               this.R.enabled = true;
               if (AlteningScreen.r().V().equals(AccountType.THEALTENING)) {
                  try {
                     AlteningScreen.r().g(AccountType.MOJANG);
                  } catch (NoSuchFieldException | IllegalAccessException var13) {
                     var13.printStackTrace();
                  }
               }

               ArrayList var8 = Astolfo.INSTANCE.getAltManager().e();
               if (var8.size() > 0) {
                  Alt var15 = (Alt)var8.get(0);
                  if (var15 != null) {
                     AltLogin.AUTH.A(var15);
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
                  String var9 = GuiScreen.getClipboardString();
                  String[] var10 = var9.split(":");
                  AltLogin.AUTH.A(new Alt(var10[0], var10[1]));
               } catch (ArrayIndexOutOfBoundsException var12) {
               }
         }
      } else if (var1.id == 3) {
         b++;
         if (b > 4) {
            b = 0;
         }

         this.l.displayString = "Alt type: " + (b == 0 ? "Altening" : (b == 1 ? "random" : (b == 2 ? "unbanned" : (b == 3 ? "username" : "clipboard"))));
      } else if (var1.id == 5 && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
         try {
            Desktop.getDesktop().browse(new URI("https://shop.thealtening.com/?r=astolfo"));
         } catch (IOException | URISyntaxException var11) {
            var11.printStackTrace();
         }
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
      int var7 = this.height / 2 - this.field_175353_i / 2;
      if (this.multilineMessage != null) {
         for (String var9 : this.multilineMessage) {
            this.d(this.fontRendererObj, var9, this.width / 2, var7, 16777215);
            var7 += this.fontRendererObj.FONT_HEIGHT;
         }
      }

      if (this.N) {
         this.mc.fontRendererObj.drawStringWithShadow("Altening login has failed, maintenance or plan?", 3.0F, 3.0F, Color.RED.getRGB());
      }

      super.drawScreen(var1, var2, var3);
   }

   private Alt F(Alt var1) {
      long var2 = d ^ 64085456306532L;
      long var4 = var2 ^ 74140145827575L;
      if (var1 == null) {
         return null;
      }

      for (Alt var8 : Astolfo.INSTANCE.getAltManager().getList()) {
         if (var8.equals(var1)) {
            return var8;
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
