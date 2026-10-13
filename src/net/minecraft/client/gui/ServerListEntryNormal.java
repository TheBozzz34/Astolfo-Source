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
   private static final long a = 122445519407707L;

   public ServerListEntryNormal(GuiMultiplayer var1, ServerData var2) {
      this.owner = var1;
      this.server = var2;
      this.mc = Minecraft.getMinecraft();
      this.serverIcon = new ResourceLocation("servers/" + var2.serverIP + "/icon");
      this.field_148305_h = (DynamicTexture)this.mc.getTextureManager().getTexture(this.serverIcon);
   }

   @Override
   public void drawEntry(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      if (!this.server.field_78841_f) {
         this.server.field_78841_f = true;
         this.server.pingToServer = -2L;
         this.server.serverMOTD = "";
         this.server.populationInfo = "";
         field_148302_b.submit(new Runnable() {
            private static final long a = 20585806632810L;

            @Override
            public void run() {
               try {
                  ServerListEntryNormal.this.owner.getOldServerPinger().ping(ServerListEntryNormal.this.server);
               } catch (UnknownHostException var4x) {
                  ServerListEntryNormal.this.server.pingToServer = -1L;
                  ServerListEntryNormal.this.server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
               } catch (Exception var5x) {
                  ServerListEntryNormal.this.server.pingToServer = -1L;
                  ServerListEntryNormal.this.server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
               }
            }
         });
      }

      boolean var12 = this.server.version > 47;
      boolean var13 = this.server.version < 47;
      boolean var14 = var12 || var13;
      this.mc.fontRendererObj.D(this.server.serverName, var2 + 32 + 3, var3 + 1, 16777215);
      List var15 = this.mc.fontRendererObj.listFormattedStringToWidth(this.server.serverMOTD, var4 - 32 - 2);

      for (int var16 = 0; var16 < Math.min(var15.size(), 2); var16++) {
         this.mc.fontRendererObj.D((String)var15.get(var16), var2 + 32 + 3, var3 + 12 + this.mc.fontRendererObj.FONT_HEIGHT * var16, 8421504);
      }

      String var26 = var14 ? EnumChatFormatting.DARK_RED + this.server.gameVersion : this.server.populationInfo;
      int var17 = this.mc.fontRendererObj.getStringWidth(var26);
      this.mc.fontRendererObj.D(var26, var2 + var4 - var17 - 15 - 2, var3 + 1, 8421504);
      byte var18 = 0;
      String var19 = null;
      int var20;
      String var21;
      if (var14) {
         var20 = 5;
         var21 = var12 ? "Client out of date!" : "Server out of date!";
         var19 = this.server.playerList;
      } else if (this.server.field_78841_f && this.server.pingToServer != -2L) {
         if (this.server.pingToServer < 0L) {
            var20 = 5;
         } else if (this.server.pingToServer < 150L) {
            var20 = 0;
         } else if (this.server.pingToServer < 300L) {
            var20 = 1;
         } else if (this.server.pingToServer < 600L) {
            var20 = 2;
         } else if (this.server.pingToServer < 1000L) {
            var20 = 3;
         } else {
            var20 = 4;
         }

         if (this.server.pingToServer < 0L) {
            var21 = "(no connection)";
         } else {
            var21 = this.server.pingToServer + "ms";
            var19 = this.server.playerList;
         }
      } else {
         var18 = 1;
         var20 = (int)(Minecraft.getSystemTime() / 100L + var1 * 2 & 7L);
         if (var20 > 4) {
            var20 = 8 - var20;
         }

         var21 = "Pinging...";
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(Gui.icons);
      Gui.drawModalRectWithCustomSizedTexture(var2 + var4 - 15, var3, var18 * 10, 176 + var20 * 8, 10, 8, 256.0F, 256.0F);
      if (this.server.getBase64EncodedIconData() != null && !this.server.getBase64EncodedIconData().equals(this.field_148299_g)) {
         this.field_148299_g = this.server.getBase64EncodedIconData();
         this.prepareServerIcon();
         this.owner.getServerList().saveServerList();
      }

      if (this.field_148305_h != null) {
         this.drawTextureAt(var2, var3, this.serverIcon);
      } else {
         this.drawTextureAt(var2, var3, UNKNOWN_SERVER);
      }

      int var22 = var6 - var2;
      int var23 = var7 - var3;
      if (var22 >= var4 - 15 && var22 <= var4 - 5 && var23 >= 0 && var23 <= 8) {
         this.owner.setHoveringText(var21);
      } else if (var22 >= var4 - var17 - 15 - 2 && var22 <= var4 - 15 - 2 && var23 >= 0 && var23 <= 8) {
         this.owner.setHoveringText(var19);
      }

      if (this.mc.gameSettings.touchscreen || var8) {
         this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
         Gui.drawRect(var2, var3, var2 + 32, var3 + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int var24 = var6 - var2;
         int var25 = var7 - var3;
         if (this.func_178013_b()) {
            if (var24 < 32 && var24 > 16) {
               Gui.drawModalRectWithCustomSizedTexture(var2, var3, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(var2, var3, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.owner.func_175392_a(this, var1)) {
            if (var24 < 16 && var25 < 16) {
               Gui.drawModalRectWithCustomSizedTexture(var2, var3, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(var2, var3, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.owner.func_175394_b(this, var1)) {
            if (var24 < 16 && var25 > 16) {
               Gui.drawModalRectWithCustomSizedTexture(var2, var3, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(var2, var3, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }
      }
   }

   public void drawTextureAt(int var1, int var2, ResourceLocation var3) {
      this.mc.getTextureManager().bindTexture(var3);
      GlStateManager.enableBlend();
      Gui.drawModalRectWithCustomSizedTexture(var1, var2, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
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
         ByteBuf var4 = Unpooled.copiedBuffer(this.server.getBase64EncodedIconData(), Charsets.UTF_8);
         ByteBuf var5 = Base64.decode(var4);

         BufferedImage var6;
         label63: {
            try {
               var6 = TextureUtil.readBufferedImage(new ByteBufInputStream(var5));
               Validate.validState(var6.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(var6.getHeight() == 64, "Must be 64 pixels high");
               break label63;
            } catch (Throwable var11) {
               logger.error("Invalid icon for server " + this.server.serverName + " (" + this.server.serverIP + ")", var11);
               this.server.setBase64EncodedIconData((String)null);
            } finally {
               var4.release();
               var5.release();
            }

            return;
         }

         if (this.field_148305_h == null) {
            this.field_148305_h = new DynamicTexture(var6.getWidth(), var6.getHeight());
            this.mc.getTextureManager().loadTexture(this.serverIcon, this.field_148305_h);
         }

         var6.getRGB(0, 0, var6.getWidth(), var6.getHeight(), this.field_148305_h.getTextureData(), 0, var6.getWidth());
         this.field_148305_h.updateDynamicTexture();
      }
   }

   @Override
   public boolean mousePressed(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var5 <= 32) {
         if (var5 < 32 && var5 > 16 && this.func_178013_b()) {
            this.owner.selectServer(var1);
            this.owner.connectToSelected();
            return true;
         }

         if (var5 < 16 && var6 < 16 && this.owner.func_175392_a(this, var1)) {
            this.owner.func_175391_a(this, var1, GuiScreen.isShiftKeyDown());
            return true;
         }

         if (var5 < 16 && var6 > 16 && this.owner.func_175394_b(this, var1)) {
            this.owner.func_175393_b(this, var1, GuiScreen.isShiftKeyDown());
            return true;
         }
      }

      this.owner.selectServer(var1);
      if (Minecraft.getSystemTime() - this.field_148298_f < 250L) {
         this.owner.connectToSelected();
      }

      this.field_148298_f = Minecraft.getSystemTime();
      return false;
   }

   @Override
   public void setSelected(int var1, int var2, int var3) {
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   public ServerData getServerData() {
      return this.server;
   }
}
