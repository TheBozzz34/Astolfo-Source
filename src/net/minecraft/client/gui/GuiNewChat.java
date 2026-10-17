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

   public GuiNewChat(Minecraft mcIn) {
      this.mc = mcIn;
   }

   public void drawChat(int updateCounter) {
      if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
         int i = this.getLineCount();
         boolean bl = false;
         int j = 0;
         int k = this.drawnChatLines.size();
         float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
         if (k > 0) {
            if (this.getChatOpen()) {
               bl = true;
            }

            float g = this.getChatScale();
            int l = MathHelper.ceiling_float_int(this.getChatWidth() / g);
            GlStateManager.pushMatrix();
            GlStateManager.translate(2.0F, 20.0F, 0.0F);
            GlStateManager.scale(g, g, 1.0F);

            for (int m = 0; m + this.scrollPos < this.drawnChatLines.size() && m < i; m++) {
               ChatLine chatLine = this.drawnChatLines.get(m + this.scrollPos);
               if (chatLine != null) {
                  int n = updateCounter - chatLine.getUpdatedCounter();
                  if (n < 200 || bl) {
                     double d = n / 200.0;
                     d = 1.0 - d;
                     d *= 10.0;
                     d = MathHelper.clamp_double(d, 0.0, 1.0);
                     d *= d;
                     int i2 = (int)(255.0 * d);
                     if (bl) {
                        i2 = 255;
                     }

                     i2 = (int)(i2 * f);
                     j++;
                     if (i2 > 3) {
                        byte b = 0;
                        int i3 = -m * 9;
                        drawRect(b, i3 - 9, b + l + 4, i3, i2 / 2 << 24);
                        String string = chatLine.getChatComponent().getFormattedText();
                        GlStateManager.enableBlend();
                        this.mc.fontRendererObj.drawStringWithShadow(string, b, i3 - 8, 16777215 + (i2 << 24));
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                     }
                  }
               }
            }

            if (bl) {
               int i4 = this.mc.fontRendererObj.FONT_HEIGHT;
               GlStateManager.translate(-3.0F, 0.0F, 0.0F);
               int i5 = k * i4 + k;
               int i6 = j * i4 + j;
               int i7 = this.scrollPos * i6 / k;
               int i8 = i6 * i6 / i5;
               if (i5 != i6) {
                  int i9 = i7 > 0 ? 170 : 96;
                  int i10 = this.isScrolled ? 13382451 : 3355562;
                  drawRect(0, -i7, 2, -i7 - i8, i10 + (i9 << 24));
                  drawRect(2, -i7, 1, -i7 - i8, 13421772 + (i9 << 24));
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

   public void printChatMessage(IChatComponent chatComponent) {
      this.printChatMessageWithOptionalDeletion(chatComponent, 0);
   }

   public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
      this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
      logger.info("[CHAT] " + chatComponent.getUnformattedText());
   }

   public void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
      if (chatLineId != 0) {
         this.deleteChatLine(chatLineId);
      }

      int i = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
      List list = GuiUtilRenderComponents.splitText(chatComponent, i, this.mc.fontRendererObj, false, false);
      boolean bl = this.getChatOpen();

      for (Object object : list) {
         IChatComponent iChatComponent = (IChatComponent)object;
         if (bl && this.scrollPos > 0) {
            this.isScrolled = true;
            this.scroll(1);
         }

         this.drawnChatLines.add(0, new ChatLine(updateCounter, iChatComponent, chatLineId));
      }

      while (this.drawnChatLines.size() > 100) {
         this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
      }

      if (!displayOnly) {
         this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

         while (this.chatLines.size() > 100) {
            this.chatLines.remove(this.chatLines.size() - 1);
         }
      }
   }

   public void refreshChat() {
      this.drawnChatLines.clear();
      this.resetScroll();

      for (int i = this.chatLines.size() - 1; i >= 0; i--) {
         ChatLine chatLine = this.chatLines.get(i);
         this.setChatLine(chatLine.getChatComponent(), chatLine.getChatLineID(), chatLine.getUpdatedCounter(), true);
      }
   }

   public List<String> getSentMessages() {
      return this.sentMessages;
   }

   public void addToSentMessages(String message) {
      if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
         this.sentMessages.add(message);
      }
   }

   public void resetScroll() {
      this.scrollPos = 0;
      this.isScrolled = false;
   }

   public void scroll(int amount) {
      this.scrollPos += amount;
      int i = this.drawnChatLines.size();
      if (this.scrollPos > i - this.getLineCount()) {
         this.scrollPos = i - this.getLineCount();
      }

      if (this.scrollPos <= 0) {
         this.scrollPos = 0;
         this.isScrolled = false;
      }
   }

   public IChatComponent getChatComponent(int mouseX, int mouseY) {
      if (!this.getChatOpen()) {
         return null;
      }

      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      int i = scaledResolution.getScaleFactor();
      float f = this.getChatScale();
      int j = mouseX / i - 3;
      int k = mouseY / i - 27;
      j = MathHelper.floor_float(j / f);
      k = MathHelper.floor_float(k / f);
      if (j >= 0 && k >= 0) {
         int l = Math.min(this.getLineCount(), this.drawnChatLines.size());
         if (j <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
            int m = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
            if (m >= 0 && m < this.drawnChatLines.size()) {
               ChatLine chatLine = this.drawnChatLines.get(m);
               int n = 0;

               for (IChatComponent iChatComponent : chatLine.getChatComponent()) {
                  if (iChatComponent instanceof ChatComponentText) {
                     n += this.mc
                        .fontRendererObj
                        .getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)iChatComponent).getChatComponentText_TextValue(), false));
                     if (n > j) {
                        return iChatComponent;
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

   public void deleteChatLine(int id) {
      Iterator iterator2 = this.drawnChatLines.iterator();

      while (iterator2.hasNext()) {
         ChatLine chatLine = (ChatLine)iterator2.next();
         if (chatLine.getChatLineID() == id) {
            iterator2.remove();
         }
      }

      iterator2 = this.chatLines.iterator();

      while (iterator2.hasNext()) {
         ChatLine chatLine2 = (ChatLine)iterator2.next();
         if (chatLine2.getChatLineID() == id) {
            iterator2.remove();
            break;
         }
      }
   }

   public int getChatWidth() {
      return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
   }

   public int getChatHeight() {
      return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
   }

   public float getChatScale() {
      return this.mc.gameSettings.chatScale;
   }

   public static int calculateChatboxWidth(float scale) {
      short s = 320;
      byte b = 40;
      return MathHelper.floor_float(scale * (s - b) + b);
   }

   public static int calculateChatboxHeight(float scale) {
      short s = 180;
      byte b = 20;
      return MathHelper.floor_float(scale * (s - b) + b);
   }

   public int getLineCount() {
      return this.getChatHeight() / 9;
   }

   public List<ChatLine> getChatLines() {
      return this.chatLines;
   }
}
