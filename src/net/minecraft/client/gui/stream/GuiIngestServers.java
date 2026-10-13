package net.minecraft.client.gui.stream;

import bypass.Astolfo;
import bypass.Toasts;
import bypass.account.Alt;
import bypass.account.AltLogin;
import bypass.account.AltManager;
import bypass.gui.screen.AddAltScreen;
import bypass.gui.screen.AlteningScreen;
import bypass.gui.screen.DirectLoginScreen;
import bypass.render.RenderUtil;
import bypass.util.FileUtil;
import bypass.util.RandomUtils;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiIngestServers extends GuiScreen {
   private final GuiScreen field_152309_a;
   private GuiIngestServers.ServerList field_152311_g;
   public Alt m;
   public ArrayList<Alt> R = new ArrayList<>();
   private GuiButton G;
   private GuiButton h;
   private GuiButton P;
   private GuiButton O;
   private GuiButton N;
   private GuiButton a;
   private GuiTextField q;
   private static int y;
   private static int d;
   public static boolean f = false;
   private static final long b = 96081786507278L;

   public GuiIngestServers(GuiScreen var1) {
      this.field_152309_a = var1;
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      this.m = null;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(this.P = new GuiButton(3, this.width / 2 - 37, this.height - 30, 75, 20, "Delete"));
      this.buttonList.add(this.G = new GuiButton(0, this.width / 2 - 125, this.height - 55, 120, 20, "Login"));
      this.buttonList.add(this.h = new GuiButton(2, this.width / 2 - 125, this.height - 30, 75, 20, "Edit"));
      this.buttonList.add(this.O = new GuiButton(6, this.width / 2 - 200, this.height - 30, 60, 20, "Random Alt"));
      this.t();
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 55, 120, 20, "Add"));
      this.buttonList.add(new GuiButton(4, this.width / 2 + 51, this.height - 30, 75, 20, "Done"));
      this.buttonList.add(new GuiButton(5, this.width / 2 + 140, this.height - 30, 60, 20, "Import"));
      this.buttonList.add(this.N = new GuiButton(10, this.width / 2 + 140, this.height - 55, 60, 20, "Erase dupe"));
      this.buttonList.add(new GuiButton(11, this.width / 2 - 200, this.height - 55, 60, 20, "Direct Login"));
      this.buttonList.add(this.a = new GuiButton(12, this.width / 2 + 210, this.height - 55, 80, 20, "Sorting"));
      if (f) {
         this.buttonList.add(new GuiButton(13, this.width / 2 + 210, this.height - 30, 80, 20, "Buy accounts"));
      }

      Keyboard.enableRepeatEvents(true);
      this.q = new GuiTextField(9, this.fontRendererObj, this.width / 2 + 90, 8, 100, 15);
      this.q.setMaxStringLength(128);
      this.field_152311_g = new GuiIngestServers.ServerList();
      this.field_152311_g.U(7, 8);
      this.u();
      if (AltManager.o > 0) {
         Toasts.TOASTS.G("Accounts unbanned", AltManager.o + " accounts have been unbanned", 10000);
         AltManager.o = 0;
      }
   }

   public void t() {
      long var1 = b ^ 140412576883912L;
      long var3 = var1 ^ 20833144898373L;
      boolean var6 = this.m != null;
      this.P.enabled = var6;
      this.G.enabled = var6;
      this.h.enabled = var6;
      this.O.enabled = !Astolfo.INSTANCE.getAltManager().getList().isEmpty();
   }

   private void u() {
      long var1 = b ^ 113330334409251L;
      long var3 = var1 ^ 11356021769646L;
      this.R.clear();

      for (Alt var7 : Astolfo.INSTANCE.getAltManager().getList()) {
         if (var7.o.toLowerCase().contains(this.q != null ? this.q.getText().toLowerCase() : "")) {
            this.R.add(var7);
         }
      }

      switch (d) {
         case 0:
            this.a.displayString = "Sorting";
            break;
         case 1:
            this.R.sort((var0, var1x) -> {
               int var5 = !var0.N ? (!var0.I ? 2 : 1) : 0;
               int var6 = !var1x.N ? (!var1x.I ? 2 : 1) : 0;
               return var6 - var5;
            });
            this.a.displayString = "Sorting unbanned";
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.field_152311_g.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton var1) {
      long var2 = b ^ 128258646645226L;
      long var4 = var2 ^ 28451119922791L;
      switch (var1.id) {
         case 0:
            AltLogin.AUTH.A(this.m);
            break;
         case 1:
            this.mc.displayGuiScreen(new AddAltScreen(this));
            break;
         case 2:
            this.mc.displayGuiScreen(new AddAltScreen(this, this.m));
            break;
         case 3:
            Astolfo.INSTANCE.getAltManager().getList().remove(this.m);
            this.m = null;
            this.t();
            this.u();
            break;
         case 4:
            this.mc.displayGuiScreen(this.field_152309_a);
            break;
         case 5:
            File var7 = FileUtil.chooseFile(Astolfo.INSTANCE.getDirectory().getParentFile());
            if (var7 != null) {
               Astolfo.INSTANCE.getAltManager().r(var7);
            }

            this.u();
            break;
         case 6:
            if (this.R.size() > 0) {
               AltLogin.AUTH.A(this.m = this.R.get(RandomUtils.RANDOM.nextInt(this.R.size())));
               this.t();
            }
            break;
         case 7:
         case 8:
         case 9:
         default:
            this.field_152311_g.actionPerformed(var1);
            break;
         case 10:
            switch (y) {
               case 0:
                  int var14 = Astolfo.INSTANCE.getAltManager().N();
                  if (var14 > 0) {
                     Toasts.TOASTS.D(var14 + " duplicates removed");
                  } else {
                     Toasts.TOASTS.m("No duplicates found");
                  }
                  break;
               case 1:
               case 2:
                  int var13 = Astolfo.INSTANCE.getAltManager().removeBanned(y == 2);
                  if (var13 > 0) {
                     Toasts.TOASTS.D(var13 + " banned accounts removed");
                  } else {
                     Toasts.TOASTS.m("No banned accounts found");
                  }
                  break;
               case 3:
                  int var12 = Astolfo.INSTANCE.getAltManager().removeFailedLogins();
                  if (var12 > 0) {
                     Toasts.TOASTS.D(var12 + " failed login accounts removed");
                  } else {
                     Toasts.TOASTS.m("No failed login accounts found");
                  }
                  break;
               case 4:
                  int var11 = Astolfo.INSTANCE.getAltManager().getList().size();
                  Astolfo.INSTANCE.getAltManager().getList().clear();
                  if (var11 > 0) {
                     Toasts.TOASTS.D(var11 + " accounts have been removed");
                  } else {
                     Toasts.TOASTS.m("No accounts have been removed");
                  }
                  break;
               case 5:
                  int var8 = Astolfo.INSTANCE.getAltManager().removeMojang();
                  if (var8 > 0) {
                     Toasts.TOASTS.D(var8 + " Mojang accounts removed");
                  } else {
                     Toasts.TOASTS.m("No Mojang accounts found");
                  }
            }

            this.u();
            break;
         case 11:
            this.mc.displayGuiScreen(new DirectLoginScreen(this));
            break;
         case 12:
            d++;
            if (d >= 2) {
               d = 0;
            }

            this.u();
            break;
         case 13:
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
               try {
                  Desktop.getDesktop().browse(new URI("https://shop.thealtening.com/?r=astolfo"));
               } catch (IOException | URISyntaxException var10) {
                  var10.printStackTrace();
               }
            }
      }
   }

   @Override
   public void updateScreen() {
      this.q.updateCursorCounter();
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      if (var3 == 1 && this.u(var1, var2)) {
         y++;
         if (y >= 6) {
            y = 0;
         }

         switch (y) {
            case 0:
               this.N.displayString = "Erase dupe";
               break;
            case 1:
               this.N.displayString = "Erase perm ban";
               break;
            case 2:
               this.N.displayString = "Erase temp ban";
               break;
            case 3:
               this.N.displayString = "Erase failed";
               break;
            case 4:
               this.N.displayString = "Erase all";
               break;
            case 5:
               this.N.displayString = "Erase Mojang";
         }
      }

      if (var3 == 2 && this.m != null) {
         this.m.Y = !this.m.Y;
      }

      super.mouseClicked(var1, var2, var3);
      this.q.mouseClicked(var1, var2, var3);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      long var3 = b ^ 17763936838883L;
      long var5 = var3 ^ 137856585745262L;
      if (this.q.isFocused()) {
         this.q.textboxKeyTyped(var1, var2);
      } else if (GuiScreen.isKeyComboCtrlV(var2)) {
         Astolfo.INSTANCE.getAltManager().O();
      } else if (GuiScreen.isKeyComboCtrlC(var2) && this.m != null) {
         GuiScreen.setClipboardString(this.m.j + ":" + this.m.z);
      }

      this.u();
      super.keyTyped(var1, var2);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.field_152311_g.drawScreen(var1, var2, var3);
      this.I(this.fontRendererObj, "§7Username: §3" + this.mc.getSession().getUsername(), 3, 3, -1);
      int var7 = this.width / 2 + 193;
      byte var8 = 5;
      GL11.glEnable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      this.mc.getTextureManager().bindTexture(new ResourceLocation("client/altening.png"));
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      Gui.drawModalRectWithCustomSizedTexture(this.width / 2 + 193, 5, 0.0F, 0.0F, 15, 16, 15.0F, 16.0F);
      if (this.P(var7, var8, var7 + 16, var8 + 16, var1, var2)) {
         drawRect(var7, var8, var7 + 16, var8 + 16, new Color(0, 0, 0, 120).getRGB());
         if (Mouse.isButtonDown(0)) {
            this.mc.displayGuiScreen(new AlteningScreen(this));
         }
      }

      if (this.u(var1, var2)) {
         this.mc
            .fontRendererObj
            .D("Right click to change erase modes", var1 - this.mc.fontRendererObj.getStringWidth("Right click to change erase modes") / 2.0F, var2 - 20, -1);
      }

      this.d(this.fontRendererObj, "Alt Manager", this.width / 2, 9, 16777215);
      this.d(
         this.fontRendererObj,
         EnumChatFormatting.DARK_GRAY + "(" + EnumChatFormatting.GRAY + this.R.size() + EnumChatFormatting.DARK_GRAY + ")",
         this.width / 2,
         19,
         16777215
      );
      this.q.drawTextBox();
      if (this.q.getText().isEmpty() && !this.q.isFocused()) {
         this.mc.fontRendererObj.D("Search...", this.width / 2 + 93, 11.0F, new Color(255, 255, 255, 120).getRGB());
      }

      super.drawScreen(var1, var2, var3);
   }

   private boolean u(int var1, int var2) {
      return this.P(this.width / 2 + 140, this.height - 55, 60, 20, var1, var2);
   }

   private boolean P(int var1, int var2, int var3, int var4, int var5, int var6) {
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

   public static ResourceLocation W(String var0) {
      var0 = StringUtils.stripControlCodes(var0);
      ResourceLocation var3 = new ResourceLocation("skins/" + var0);
      TextureManager var4 = Minecraft.getMinecraft().getTextureManager();
      ITextureObject var5 = var4.getTexture(var3);
      if (var5 == null) {
         var5 = new ThreadDownloadImageData(
            null,
            String.format("https://minotar.net/skin/%s", var0),
            DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getOfflineUUID(var0)),
            new ImageBufferDownload()
         );
         var4.loadTexture(var3, var5);
      }

      return var3;
   }

   public class ServerList extends GuiSlot {
      private static final long c = 25492104763680L;

      public ServerList() {
         super(Minecraft.getMinecraft(), GuiIngestServers.this.width, GuiIngestServers.this.height, 32, GuiIngestServers.this.height - 60, 40);
      }

      @Override
      public int getSize() {
         return GuiIngestServers.this.R.size();
      }

      @Override
      public void elementClicked(int var1, boolean var2, int var3, int var4) {
         GuiIngestServers.this.m = GuiIngestServers.this.R.get(var1);
         GuiIngestServers.this.t();
         if (var2) {
            AltLogin.AUTH.A(GuiIngestServers.this.m);
         }
      }

      @Override
      public boolean isSelected(int var1) {
         return GuiIngestServers.this.R.get(var1) == GuiIngestServers.this.m;
      }

      @Override
      public void drawBackground() {
         GuiIngestServers.this.drawDefaultBackground();
      }

      @Override
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         if (var3 <= GuiIngestServers.this.height && var3 >= 0) {
            Alt var10 = GuiIngestServers.this.R.get(var1);
            GuiIngestServers.this.I(this.mc.fontRendererObj, var10.o, var2 + 40, var3 + 5, -1);
            GuiIngestServers.this.I(this.mc.fontRendererObj, this.Y(var10.z), var2 + 41, var3 + 15, -6513508);
            RenderUtil.setColor(var10.V_boolean() ? (var10.I ? Color.YELLOW.getRGB() : Color.GREEN.getRGB()) : Color.GRAY.getRGB());
            if (var10.F) {
               RenderUtil.setColor(Color.WHITE.getRGB());
            }

            this.mc
               .getTextureManager()
               .bindTexture(
                  new ResourceLocation(var10.V_boolean() ? (var10.F ? "client/login/microsoft.png" : "client/login/premium.png") : "client/login/cracked.png")
               );
            Gui.drawModalRectWithCustomSizedTexture(var2 + 205, var3 + 3, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
            if (var10.N) {
               boolean var11 = var10.m != -1L;
               RenderUtil.setColor(var11 ? Color.GRAY.getRGB() : Color.RED.getRGB());
               this.mc.getTextureManager().bindTexture(new ResourceLocation(var10.m != -1L ? "client/login/date.png" : "client/login/ban.png"));
               Gui.drawModalRectWithCustomSizedTexture(var2 + 205, var3 + 14, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
            }

            if (var10.Y) {
               RenderUtil.setColor(Color.YELLOW.getRGB());
               this.mc.getTextureManager().bindTexture(new ResourceLocation("client/login/favorite.png"));
               Gui.drawModalRectWithCustomSizedTexture(var2 + 205, var3 + 25, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
            }

            if (var10.N && var10.m != -1L) {
               long var22 = var10.m - System.currentTimeMillis();
               if (var22 > 0L) {
                  long var13 = var22 / 1000L % 60L;
                  long var15 = var22 / 60000L % 60L;
                  long var17 = var22 / 3600000L % 24L;
                  long var19 = var22 / 86400000L;
                  String var21 = "";
                  if (var19 > 0L) {
                     var21 = var21 + var19 + "d ";
                  }

                  if (var17 > 0L) {
                     var21 = var21 + var17 + "h ";
                  }

                  if (var15 > 0L) {
                     var21 = var21 + var15 + "m ";
                  }

                  if (var13 > 0L) {
                     var21 = var21 + var13 + "s";
                  }

                  GuiIngestServers.this.I(this.mc.fontRendererObj, var21, var2 + 40, var3 + 23, 16777215);
               } else {
                  var10.N = false;
                  var10.m = -1L;
               }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(GuiIngestServers.W(var10.o));
            GuiIngestServers.this.drawTexturedModalRect(var2 + 3, var3 + 2, 32, 32, 32, 32);
         }
      }

      private String Y(String var1) {
         return org.apache.commons.lang3.StringUtils.repeat('*', var1.length());
      }
   }
}
