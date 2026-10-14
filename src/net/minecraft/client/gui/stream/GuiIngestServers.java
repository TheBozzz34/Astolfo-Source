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

   public GuiIngestServers(GuiScreen guiScreen) {
      this.field_152309_a = guiScreen;
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
      long l = b ^ 140412576883912L;
      long n = l ^ 20833144898373L;
      boolean bl = this.m != null;
      this.P.enabled = bl;
      this.G.enabled = bl;
      this.h.enabled = bl;
      this.O.enabled = !Astolfo.INSTANCE.getAltManager().getList().isEmpty();
   }

   private void u() {
      long l = b ^ 113330334409251L;
      long m = l ^ 11356021769646L;
      this.R.clear();

      for (Alt alt : Astolfo.INSTANCE.getAltManager().getList()) {
         if (alt.o.toLowerCase().contains(this.q != null ? this.q.getText().toLowerCase() : "")) {
            this.R.add(alt);
         }
      }

      switch (d) {
         case 0:
            this.a.displayString = "Sorting";
            break;
         case 1:
            this.R.sort((alt2, alt3) -> {
               int i = !alt2.N ? (!alt2.I ? 2 : 1) : 0;
               int j = !alt3.N ? (!alt3.I ? 2 : 1) : 0;
               return j - i;
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
   public void actionPerformed(GuiButton button) {
      long l = b ^ 128258646645226L;
      long n = l ^ 28451119922791L;
      switch (button.id) {
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
            File file = FileUtil.chooseFile(Astolfo.INSTANCE.getDirectory().getParentFile());
            if (file != null) {
               Astolfo.INSTANCE.getAltManager().r(file);
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
            this.field_152311_g.actionPerformed(button);
            break;
         case 10:
            switch (y) {
               case 0:
                  int i = Astolfo.INSTANCE.getAltManager().N();
                  if (i > 0) {
                     Toasts.TOASTS.D(i + " duplicates removed");
                  } else {
                     Toasts.TOASTS.m("No duplicates found");
                  }
                  break;
               case 1:
               case 2:
                  int j = Astolfo.INSTANCE.getAltManager().removeBanned(y == 2);
                  if (j > 0) {
                     Toasts.TOASTS.D(j + " banned accounts removed");
                  } else {
                     Toasts.TOASTS.m("No banned accounts found");
                  }
                  break;
               case 3:
                  int k = Astolfo.INSTANCE.getAltManager().removeFailedLogins();
                  if (k > 0) {
                     Toasts.TOASTS.D(k + " failed login accounts removed");
                  } else {
                     Toasts.TOASTS.m("No failed login accounts found");
                  }
                  break;
               case 4:
                  int i2 = Astolfo.INSTANCE.getAltManager().getList().size();
                  Astolfo.INSTANCE.getAltManager().getList().clear();
                  if (i2 > 0) {
                     Toasts.TOASTS.D(i2 + " accounts have been removed");
                  } else {
                     Toasts.TOASTS.m("No accounts have been removed");
                  }
                  break;
               case 5:
                  int i3 = Astolfo.INSTANCE.getAltManager().removeMojang();
                  if (i3 > 0) {
                     Toasts.TOASTS.D(i3 + " Mojang accounts removed");
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
               } catch (IOException | URISyntaxException value) {
                  value.printStackTrace();
               }
            }
      }
   }

   @Override
   public void updateScreen() {
      this.q.updateCursorCounter();
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 1 && this.u(mouseX, mouseY)) {
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

      if (mouseButton == 2 && this.m != null) {
         this.m.Y = !this.m.Y;
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.q.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      long l = b ^ 17763936838883L;
      long n = l ^ 137856585745262L;
      if (this.q.isFocused()) {
         this.q.textboxKeyTyped(typedChar, keyCode);
      } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
         Astolfo.INSTANCE.getAltManager().O();
      } else if (GuiScreen.isKeyComboCtrlC(keyCode) && this.m != null) {
         GuiScreen.setClipboardString(this.m.j + ":" + this.m.z);
      }

      this.u();
      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.field_152311_g.drawScreen(mouseX, mouseY, partialTicks);
      this.I(this.fontRendererObj, "§7Username: §3" + this.mc.getSession().getUsername(), 3, 3, -1);
      int i = this.width / 2 + 193;
      byte b = 5;
      GL11.glEnable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      this.mc.getTextureManager().bindTexture(new ResourceLocation("client/altening.png"));
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      Gui.drawModalRectWithCustomSizedTexture(this.width / 2 + 193, 5, 0.0F, 0.0F, 15, 16, 15.0F, 16.0F);
      if (this.P(i, b, i + 16, b + 16, mouseX, mouseY)) {
         drawRect(i, b, i + 16, b + 16, new Color(0, 0, 0, 120).getRGB());
         if (Mouse.isButtonDown(0)) {
            this.mc.displayGuiScreen(new AlteningScreen(this));
         }
      }

      if (this.u(mouseX, mouseY)) {
         this.mc
            .fontRendererObj
            .D(
               "Right click to change erase modes",
               mouseX - this.mc.fontRendererObj.getStringWidth("Right click to change erase modes") / 2.0F,
               mouseY - 20,
               -1
            );
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

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private boolean u(int i, int j) {
      return this.P(this.width / 2 + 140, this.height - 55, 60, 20, i, j);
   }

   private boolean P(int i, int j, int k, int l, int m, int n) {
      int i2 = k;
      int i3 = l;
      if ((i2 | i3) < 0) {
         return false;
      } else if (m >= i && n >= j) {
         i2 += i;
         i3 += j;
         return (i2 < i || i2 > m) && (i3 < j || i3 > n);
      } else {
         return false;
      }
   }

   public static ResourceLocation W(String string) {
      string = StringUtils.stripControlCodes(string);
      ResourceLocation resourceLocation = new ResourceLocation("skins/" + string);
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      ITextureObject iTextureObject = textureManager.getTexture(resourceLocation);
      if (iTextureObject == null) {
         iTextureObject = new ThreadDownloadImageData(
            null,
            String.format("https://minotar.net/skin/%s", string),
            DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getOfflineUUID(string)),
            new ImageBufferDownload()
         );
         textureManager.loadTexture(resourceLocation, iTextureObject);
      }

      return resourceLocation;
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
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         GuiIngestServers.this.m = GuiIngestServers.this.R.get(slotIndex);
         GuiIngestServers.this.t();
         if (isDoubleClick) {
            AltLogin.AUTH.A(GuiIngestServers.this.m);
         }
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return GuiIngestServers.this.R.get(slotIndex) == GuiIngestServers.this.m;
      }

      @Override
      public void drawBackground() {
         GuiIngestServers.this.drawDefaultBackground();
      }

      @Override
      public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
         if (j <= GuiIngestServers.this.height && j >= 0) {
            Alt alt = GuiIngestServers.this.R.get(entryID);
            GuiIngestServers.this.I(this.mc.fontRendererObj, alt.o, i + 40, j + 5, -1);
            GuiIngestServers.this.I(this.mc.fontRendererObj, this.Y(alt.z), i + 41, j + 15, -6513508);
            RenderUtil.setColor(alt.V_boolean() ? (alt.I ? Color.YELLOW.getRGB() : Color.GREEN.getRGB()) : Color.GRAY.getRGB());
            if (alt.F) {
               RenderUtil.setColor(Color.WHITE.getRGB());
            }

            this.mc
               .getTextureManager()
               .bindTexture(
                  new ResourceLocation(alt.V_boolean() ? (alt.F ? "client/login/microsoft.png" : "client/login/premium.png") : "client/login/cracked.png")
               );
            Gui.drawModalRectWithCustomSizedTexture(i + 205, j + 3, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
            if (alt.N) {
               boolean bl = alt.m != -1L;
               RenderUtil.setColor(bl ? Color.GRAY.getRGB() : Color.RED.getRGB());
               this.mc.getTextureManager().bindTexture(new ResourceLocation(alt.m != -1L ? "client/login/date.png" : "client/login/ban.png"));
               Gui.drawModalRectWithCustomSizedTexture(i + 205, j + 14, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
            }

            if (alt.Y) {
               RenderUtil.setColor(Color.YELLOW.getRGB());
               this.mc.getTextureManager().bindTexture(new ResourceLocation("client/login/favorite.png"));
               Gui.drawModalRectWithCustomSizedTexture(i + 205, j + 25, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
            }

            if (alt.N && alt.m != -1L) {
               long l = alt.m - System.currentTimeMillis();
               if (l > 0L) {
                  long n = l / 1000L % 60L;
                  long l2 = l / 60000L % 60L;
                  long l3 = l / 3600000L % 24L;
                  long l4 = l / 86400000L;
                  String string = "";
                  if (l4 > 0L) {
                     string = string + l4 + "d ";
                  }

                  if (l3 > 0L) {
                     string = string + l3 + "h ";
                  }

                  if (l2 > 0L) {
                     string = string + l2 + "m ";
                  }

                  if (n > 0L) {
                     string = string + n + "s";
                  }

                  GuiIngestServers.this.I(this.mc.fontRendererObj, string, i + 40, j + 23, 16777215);
               } else {
                  alt.N = false;
                  alt.m = -1L;
               }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(GuiIngestServers.W(alt.o));
            GuiIngestServers.this.drawTexturedModalRect(i + 3, j + 2, 32, 32, 32, 32);
         }
      }

      private String Y(String string) {
         return org.apache.commons.lang3.StringUtils.repeat('*', string.length());
      }
   }
}
