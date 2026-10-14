package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerListEntryNormal implements GuiListExtended.IGuiListEntry {
   private static final Logger logger = LogManager.getLogger();
   private static final ThreadPoolExecutor field_148302_b = new ScheduledThreadPoolExecutor(
      5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
   );
   private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
   private final GuiMultiplayer owner;
   private final Minecraft mc;
   private final ServerData server;
   private final ResourceLocation serverIcon;
   private String field_148299_g;
   private DynamicTexture field_148305_h;
   private long field_148298_f;

   public ServerListEntryNormal(GuiMultiplayer guiMultiplayer, ServerData serverIn) {
      this.owner = guiMultiplayer;
      this.server = serverIn;
      this.mc = Minecraft.getMinecraft();
      this.serverIcon = new ResourceLocation("servers/" + serverIn.serverIP + "/icon");
      this.field_148305_h = (DynamicTexture)this.mc.getTextureManager().getTexture(this.serverIcon);
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      if (!this.server.field_78841_f) {
         this.server.field_78841_f = true;
         this.server.pingToServer = -2L;
         this.server.serverMOTD = "";
         this.server.populationInfo = "";
         field_148302_b.submit(new Runnable() {
            @Override
            public void run() {
               try {
                  ServerListEntryNormal.this.owner.getOldServerPinger().ping(ServerListEntryNormal.this.server);
               } catch (UnknownHostException unknownHostException) {
                  ServerListEntryNormal.this.server.pingToServer = -1L;
                  ServerListEntryNormal.this.server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
               } catch (Exception exception) {
                  ServerListEntryNormal.this.server.pingToServer = -1L;
                  ServerListEntryNormal.this.server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
               }
            }
         });
      }

      boolean bl = this.server.version > 47;
      boolean bl2 = this.server.version < 47;
      boolean bl3 = bl || bl2;
      this.mc.fontRendererObj.D(this.server.serverName, x + 32 + 3, y + 1, 16777215);
      List list = this.mc.fontRendererObj.listFormattedStringToWidth(this.server.serverMOTD, listWidth - 32 - 2);

      for (int i = 0; i < Math.min(list.size(), 2); i++) {
         this.mc.fontRendererObj.D((String)list.get(i), x + 32 + 3, y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i, 8421504);
      }

      String string = bl3 ? EnumChatFormatting.DARK_RED + this.server.gameVersion : this.server.populationInfo;
      int j = this.mc.fontRendererObj.getStringWidth(string);
      this.mc.fontRendererObj.D(string, x + listWidth - j - 15 - 2, y + 1, 8421504);
      byte b = 0;
      String string2 = null;
      int k;
      String string3;
      if (bl3) {
         k = 5;
         string3 = bl ? "Client out of date!" : "Server out of date!";
         string2 = this.server.playerList;
      } else if (this.server.field_78841_f && this.server.pingToServer != -2L) {
         if (this.server.pingToServer < 0L) {
            k = 5;
         } else if (this.server.pingToServer < 150L) {
            k = 0;
         } else if (this.server.pingToServer < 300L) {
            k = 1;
         } else if (this.server.pingToServer < 600L) {
            k = 2;
         } else if (this.server.pingToServer < 1000L) {
            k = 3;
         } else {
            k = 4;
         }

         if (this.server.pingToServer < 0L) {
            string3 = "(no connection)";
         } else {
            string3 = this.server.pingToServer + "ms";
            string2 = this.server.playerList;
         }
      } else {
         b = 1;
         k = (int)(Minecraft.getSystemTime() / 100L + slotIndex * 2 & 7L);
         if (k > 4) {
            k = 8 - k;
         }

         string3 = "Pinging...";
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(Gui.icons);
      Gui.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, b * 10, 176 + k * 8, 10, 8, 256.0F, 256.0F);
      if (this.server.getBase64EncodedIconData() != null && !this.server.getBase64EncodedIconData().equals(this.field_148299_g)) {
         this.field_148299_g = this.server.getBase64EncodedIconData();
         this.prepareServerIcon();
         this.owner.getServerList().saveServerList();
      }

      if (this.field_148305_h != null) {
         this.drawTextureAt(x, y, this.serverIcon);
      } else {
         this.drawTextureAt(x, y, UNKNOWN_SERVER);
      }

      int l = mouseX - x;
      int m = mouseY - y;
      if (l >= listWidth - 15 && l <= listWidth - 5 && m >= 0 && m <= 8) {
         this.owner.setHoveringText(string3);
      } else if (l >= listWidth - j - 15 - 2 && l <= listWidth - 15 - 2 && m >= 0 && m <= 8) {
         this.owner.setHoveringText(string2);
      }

      if (this.mc.gameSettings.touchscreen || isSelected) {
         this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
         Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int n = mouseX - x;
         int i2 = mouseY - y;
         if (this.func_178013_b()) {
            if (n < 32 && n > 16) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.owner.func_175392_a(this, slotIndex)) {
            if (n < 16 && i2 < 16) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.owner.func_175394_b(this, slotIndex)) {
            if (n < 16 && i2 > 16) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }
      }
   }

   public void drawTextureAt(int i, int j, ResourceLocation resourceLocation) {
      this.mc.getTextureManager().bindTexture(resourceLocation);
      GlStateManager.enableBlend();
      Gui.drawModalRectWithCustomSizedTexture(i, j, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.disableBlend();
   }

   private boolean func_178013_b() {
      return true;
   }

   private void prepareServerIcon() {
      if (this.server.getBase64EncodedIconData() == null) {
         this.mc.getTextureManager().deleteTexture(this.serverIcon);
         this.field_148305_h = null;
      } else {
         ByteBuf byteBuf = Unpooled.copiedBuffer(this.server.getBase64EncodedIconData(), Charsets.UTF_8);
         ByteBuf byteBuf2 = Base64.decode(byteBuf);

         BufferedImage bufferedImage;
         label63: {
            try {
               bufferedImage = TextureUtil.readBufferedImage(new ByteBufInputStream(byteBuf2));
               Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
               break label63;
            } catch (Throwable throwable) {
               logger.error("Invalid icon for server " + this.server.serverName + " (" + this.server.serverIP + ")", throwable);
               this.server.setBase64EncodedIconData((String)null);
            } finally {
               byteBuf.release();
               byteBuf2.release();
            }

            return;
         }

         if (this.field_148305_h == null) {
            this.field_148305_h = new DynamicTexture(bufferedImage.getWidth(), bufferedImage.getHeight());
            this.mc.getTextureManager().loadTexture(this.serverIcon, this.field_148305_h);
         }

         bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.field_148305_h.getTextureData(), 0, bufferedImage.getWidth());
         this.field_148305_h.updateDynamicTexture();
      }
   }

   @Override
   public boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m) {
      if (l <= 32) {
         if (l < 32 && l > 16 && this.func_178013_b()) {
            this.owner.selectServer(slotIndex);
            this.owner.connectToSelected();
            return true;
         }

         if (l < 16 && m < 16 && this.owner.func_175392_a(this, slotIndex)) {
            this.owner.func_175391_a(this, slotIndex, GuiScreen.isShiftKeyDown());
            return true;
         }

         if (l < 16 && m > 16 && this.owner.func_175394_b(this, slotIndex)) {
            this.owner.func_175393_b(this, slotIndex, GuiScreen.isShiftKeyDown());
            return true;
         }
      }

      this.owner.selectServer(slotIndex);
      if (Minecraft.getSystemTime() - this.field_148298_f < 250L) {
         this.owner.connectToSelected();
      }

      this.field_148298_f = Minecraft.getSystemTime();
      return false;
   }

   @Override
   public void setSelected(int i, int j, int k) {
   }

   @Override
   public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
   }

   public ServerData getServerData() {
      return this.server;
   }
}
