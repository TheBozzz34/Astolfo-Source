package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiNewChat extends Gui {
   public static final Logger logger = LogManager.getLogger();
   private final Minecraft mc;
   private final List<String> sentMessages = Lists.newArrayList();
   private final List<ChatLine> chatLines = Lists.newArrayList();
   private final List<ChatLine> drawnChatLines = Lists.newArrayList();
   private int scrollPos;
   private boolean isScrolled;
   private static final long d = 34027939022577L;

   public GuiNewChat(Minecraft var1) {
      this.mc = var1;
   }

   public void drawChat(int var1) {
      if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
         int var5 = this.getLineCount();
         boolean var6 = false;
         int var7 = 0;
         int var8 = this.drawnChatLines.size();
         float var9 = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
         if (var8 > 0) {
            if (this.getChatOpen()) {
               var6 = true;
            }

            float var10 = this.getChatScale();
            int var11 = MathHelper.ceiling_float_int(this.getChatWidth() / var10);
            GlStateManager.pushMatrix();
            GlStateManager.translate(2.0F, 20.0F, 0.0F);
            GlStateManager.scale(var10, var10, 1.0F);

            for (int var12 = 0; var12 + this.scrollPos < this.drawnChatLines.size() && var12 < var5; var12++) {
               ChatLine var13 = this.drawnChatLines.get(var12 + this.scrollPos);
               if (var13 != null) {
                  int var14 = var1 - var13.getUpdatedCounter();
                  if (var14 < 200 || var6) {
                     double var15 = var14 / 200.0;
                     var15 = 1.0 - var15;
                     var15 *= 10.0;
                     var15 = MathHelper.clamp_double(var15, 0.0, 1.0);
                     var15 *= var15;
                     int var17 = (int)(255.0 * var15);
                     if (var6) {
                        var17 = 255;
                     }

                     var17 = (int)(var17 * var9);
                     var7++;
                     if (var17 > 3) {
                        byte var18 = 0;
                        int var19 = -var12 * 9;
                        drawRect(var18, var19 - 9, var18 + var11 + 4, var19, var17 / 2 << 24);
                        String var20 = var13.getChatComponent().getFormattedText();
                        GlStateManager.enableBlend();
                        this.mc.fontRendererObj.drawStringWithShadow(var20, var18, var19 - 8, 16777215 + (var17 << 24));
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                     }
                  }
               }
            }

            if (var6) {
               int var21 = this.mc.fontRendererObj.FONT_HEIGHT;
               GlStateManager.translate(-3.0F, 0.0F, 0.0F);
               int var22 = var8 * var21 + var8;
               int var23 = var7 * var21 + var7;
               int var28 = this.scrollPos * var23 / var8;
               int var16 = var23 * var23 / var22;
               if (var22 != var23) {
                  int var30 = var28 > 0 ? 170 : 96;
                  int var31 = this.isScrolled ? 13382451 : 3355562;
                  drawRect(0, -var28, 2, -var28 - var16, var31 + (var30 << 24));
                  drawRect(2, -var28, 1, -var28 - var16, 13421772 + (var30 << 24));
               }
            }

            GlStateManager.popMatrix();
         }
      }
   }

   public void clearChatMessages() {
      this.drawnChatLines.clear();
      this.chatLines.clear();
      this.sentMessages.clear();
   }

   public void printChatMessage(IChatComponent var1) {
      this.printChatMessageWithOptionalDeletion(var1, 0);
   }

   public void printChatMessageWithOptionalDeletion(IChatComponent var1, int var2) {
      this.setChatLine(var1, var2, this.mc.ingameGUI.getUpdateCounter(), false);
      logger.info("[CHAT] " + var1.getUnformattedText());
   }

   public void setChatLine(IChatComponent var1, int var2, int var3, boolean var4) {
      if (var2 != 0) {
         this.deleteChatLine(var2);
      }

      int var8 = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
      List var9 = GuiUtilRenderComponents.splitText(var1, var8, this.mc.fontRendererObj, false, false);
      boolean var10 = this.getChatOpen();

      for (Object var12$raw : var9) {
         IChatComponent var12 = (IChatComponent)var12$raw;
         if (var10 && this.scrollPos > 0) {
            this.isScrolled = true;
            this.scroll(1);
         }

         this.drawnChatLines.add(0, new ChatLine(var3, var12, var2));
      }

      while (this.drawnChatLines.size() > 100) {
         this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
      }

      if (!var4) {
         this.chatLines.add(0, new ChatLine(var3, var1, var2));

         while (this.chatLines.size() > 100) {
            this.chatLines.remove(this.chatLines.size() - 1);
         }
      }
   }

   public void refreshChat() {
      this.drawnChatLines.clear();
      this.resetScroll();

      for (int var4 = this.chatLines.size() - 1; var4 >= 0; var4--) {
         ChatLine var5 = this.chatLines.get(var4);
         this.setChatLine(var5.getChatComponent(), var5.getChatLineID(), var5.getUpdatedCounter(), true);
      }
   }

   public List<String> getSentMessages() {
      return this.sentMessages;
   }

   public void addToSentMessages(String var1) {
      if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(var1)) {
         this.sentMessages.add(var1);
      }
   }

   public void resetScroll() {
      this.scrollPos = 0;
      this.isScrolled = false;
   }

   public void scroll(int var1) {
      this.scrollPos += var1;
      int var5 = this.drawnChatLines.size();
      if (this.scrollPos > var5 - this.getLineCount()) {
         this.scrollPos = var5 - this.getLineCount();
      }

      if (this.scrollPos <= 0) {
         this.scrollPos = 0;
         this.isScrolled = false;
      }
   }

   public IChatComponent getChatComponent(int var1, int var2) {
      if (!this.getChatOpen()) {
         return null;
      }

      ScaledResolution var6 = new ScaledResolution(this.mc);
      int var7 = var6.getScaleFactor();
      float var8 = this.getChatScale();
      int var9 = var1 / var7 - 3;
      int var10 = var2 / var7 - 27;
      var9 = MathHelper.floor_float(var9 / var8);
      var10 = MathHelper.floor_float(var10 / var8);
      if (var9 >= 0 && var10 >= 0) {
         int var11 = Math.min(this.getLineCount(), this.drawnChatLines.size());
         if (var9 <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && var10 < this.mc.fontRendererObj.FONT_HEIGHT * var11 + var11) {
            int var12 = var10 / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
            if (var12 >= 0 && var12 < this.drawnChatLines.size()) {
               ChatLine var13 = this.drawnChatLines.get(var12);
               int var14 = 0;

               for (IChatComponent var16 : var13.getChatComponent()) {
                  if (var16 instanceof ChatComponentText) {
                     var14 += this.mc
                        .fontRendererObj
                        .getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)var16).getChatComponentText_TextValue(), false));
                     if (var14 > var9) {
                        return var16;
                     }
                  }
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean getChatOpen() {
      return this.mc.currentScreen instanceof GuiChat;
   }

   public void deleteChatLine(int var1) {
      Iterator var5 = this.drawnChatLines.iterator();

      while (var5.hasNext()) {
         ChatLine var6 = (ChatLine)var5.next();
         if (var6.getChatLineID() == var1) {
            var5.remove();
         }
      }

      var5 = this.chatLines.iterator();

      while (var5.hasNext()) {
         ChatLine var8 = (ChatLine)var5.next();
         if (var8.getChatLineID() == var1) {
            var5.remove();
            break;
         }
      }
   }

   public int getChatWidth() {
      return calculateChatboxWidth(this.mc.gameSettings.l6);
   }

   public int getChatHeight() {
      return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.o : this.mc.gameSettings.la);
   }

   public float getChatScale() {
      return this.mc.gameSettings.chatScale;
   }

   public static int calculateChatboxWidth(float var0) {
      short var3 = 320;
      byte var4 = 40;
      return MathHelper.floor_float(var0 * (var3 - var4) + var4);
   }

   public static int calculateChatboxHeight(float var0) {
      short var3 = 180;
      byte var4 = 20;
      return MathHelper.floor_float(var0 * (var3 - var4) + var4);
   }

   public int getLineCount() {
      return this.getChatHeight() / 9;
   }

   public List<ChatLine> B() {
      return this.chatLines;
   }
}
