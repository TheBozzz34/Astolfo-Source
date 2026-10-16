package bypass.gui;

import bypass.module.visuals.HUD;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

public class CustomChatGui extends GuiNewChat {
   private final Minecraft minecraft;
   private final List<String> sentMessageHistory = Lists.newArrayList();
   private final List<ChatLine> allChatLines = Lists.newArrayList();
   private final List<ChatLine> displayedChatLines = Lists.newArrayList();
   private int scrollOffset;
   private boolean scrolledUp;
   public static float slideProgress = 0.0F;
   public static int newMessageLastLineIndex;
   public static long lastFrameTime = -1L;
   public boolean renderingDisabled;

   public CustomChatGui(Minecraft minecraft) {
      super(minecraft);
      this.minecraft = minecraft;
   }

   public static void advanceSlideAnimation(long l) {
      if (slideProgress < 1.0F) {
         slideProgress += 0.004F * (float)l;
      }

      slideProgress = MathHelper.clamp_float(slideProgress, 0.0F, 1.0F);
   }

   @Override
   public void drawChat(int updateCounter) {
      if (!this.renderingDisabled) {
         if (lastFrameTime == -1L) {
            lastFrameTime = System.currentTimeMillis();
         } else {
            long l = System.currentTimeMillis();
            long m = l - lastFrameTime;
            lastFrameTime = l;
            advanceSlideAnimation(m);
            float f = slideProgress;
            float g = 1.0F - --f * f * f * f;
            g = MathHelper.clamp_float(g, 0.0F, 1.0F);
            if (this.minecraft.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
               int i = this.getLineCount();
               int j = this.displayedChatLines.size();
               float h = this.minecraft.gameSettings.chatOpacity * 0.9F + 0.1F;
               if (j > 0) {
                  boolean bl = false;
                  if (this.getChatOpen()) {
                     bl = true;
                  }

                  float f2 = this.getChatScale();
                  int k = MathHelper.ceiling_float_int(this.getChatWidth() / f2);
                  GlStateManager.pushMatrix();
                  if (HUD.chatSettings.getSetting("Smooth").asBoolean() && !this.scrolledUp) {
                     GlStateManager.translate(2.0F, 8.0F + (9.0F - 9.0F * g) * f2, 0.0F);
                  } else {
                     GlStateManager.translate(2.0F, 8.0F, 0.0F);
                  }

                  GlStateManager.scale(f2, f2, 1.0F);
                  int n = 0;

                  for (int i2 = 0; i2 + this.scrollOffset < this.displayedChatLines.size() && i2 < i; i2++) {
                     ChatLine chatLine = this.displayedChatLines.get(i2 + this.scrollOffset);
                     if (chatLine != null) {
                        int i3 = updateCounter - chatLine.getUpdatedCounter();
                        if (i3 < 200 || bl) {
                           double d = i3 / 200.0;
                           d = 1.0 - d;
                           d *= 10.0;
                           d = MathHelper.clamp_double(d, 0.0, 1.0);
                           d *= d;
                           int i4 = (int)(255.0 * d);
                           if (bl) {
                              i4 = 255;
                           }

                           i4 = (int)(i4 * h);
                           n++;
                           if (i4 > 3) {
                              byte b = 0;
                              int i5 = -i2 * 9;
                              if (!HUD.chatSettings.getSetting("Clear").asBoolean()) {
                                 drawRect(-2, i5 - 9, b + k + 4, i5, i4 / 2 << 24);
                              }

                              String string = chatLine.getChatComponent().getFormattedText();
                              GlStateManager.enableBlend();
                              if (HUD.chatSettings.getSetting("Smooth").asBoolean() && i2 <= newMessageLastLineIndex) {
                                 this.minecraft.fontRendererObj.drawStringWithShadow(string, 0.0F, i5 - 8, 16777215 + ((int)(i4 * g) << 24));
                              } else {
                                 this.minecraft.fontRendererObj.drawStringWithShadow(string, b, i5 - 8, 16777215 + (i4 << 24));
                              }

                              GlStateManager.disableAlpha();
                              GlStateManager.disableBlend();
                           }
                        }
                     }
                  }

                  if (bl) {
                     int i6 = this.minecraft.fontRendererObj.FONT_HEIGHT;
                     GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                     int i7 = j * i6 + j;
                     int i8 = n * i6 + n;
                     int i9 = this.scrollOffset * i8 / j;
                     int i10 = i8 * i8 / i7;
                     if (i7 != i8) {
                        int i11 = i9 > 0 ? 170 : 96;
                        int i12 = this.scrolledUp ? 13382451 : 3355562;
                        drawRect(0, -i9, 2, -i9 - i10, i12 + (i11 << 24));
                        drawRect(2, -i9, 1, -i9 - i10, 13421772 + (i11 << 24));
                     }
                  }

                  GlStateManager.popMatrix();
               }
            }
         }
      }
   }

   @Override
   public void clearChatMessages() {
      this.displayedChatLines.clear();
      this.allChatLines.clear();
      this.sentMessageHistory.clear();
   }

   @Override
   public void printChatMessage(IChatComponent chatComponent) {
      this.printChatMessageWithOptionalDeletion(chatComponent, 0);
   }

   @Override
   public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
      slideProgress = 0.0F;
      this.setChatLine(chatComponent, chatLineId, this.minecraft.ingameGUI.getUpdateCounter(), false);
      GuiNewChat.logger.info("[CHAT] " + chatComponent.getUnformattedText());
   }

   @Override
   public void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
      if (chatLineId != 0) {
         this.deleteChatLine(chatLineId);
      }

      int i = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
      List list = GuiUtilRenderComponents.splitText(chatComponent, i, this.minecraft.fontRendererObj, false, false);
      boolean bl = this.getChatOpen();
      newMessageLastLineIndex = list.size() - 1;

      for (Object object : list) {
         IChatComponent iChatComponent = (IChatComponent)object;
         if (bl && this.scrollOffset > 0) {
            this.scrolledUp = true;
            this.scroll(1);
         }

         this.displayedChatLines.add(0, new ChatLine(updateCounter, iChatComponent, chatLineId));
      }

      while (this.displayedChatLines.size() > 100) {
         this.displayedChatLines.remove(this.displayedChatLines.size() - 1);
      }

      if (!displayOnly) {
         this.allChatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

         while (this.allChatLines.size() > 100) {
            this.allChatLines.remove(this.allChatLines.size() - 1);
         }
      }
   }

   @Override
   public void refreshChat() {
      this.displayedChatLines.clear();
      this.resetScroll();

      for (int i = this.allChatLines.size() - 1; i >= 0; i--) {
         ChatLine chatLine = this.allChatLines.get(i);
         this.setChatLine(chatLine.getChatComponent(), chatLine.getChatLineID(), chatLine.getUpdatedCounter(), true);
      }
   }

   @Override
   public List<String> getSentMessages() {
      return this.sentMessageHistory;
   }

   @Override
   public void addToSentMessages(String message) {
      if (this.sentMessageHistory.isEmpty() || !this.sentMessageHistory.get(this.sentMessageHistory.size() - 1).equals(message)) {
         this.sentMessageHistory.add(message);
      }
   }

   @Override
   public void resetScroll() {
      this.scrollOffset = 0;
      this.scrolledUp = false;
   }

   @Override
   public void scroll(int amount) {
      this.scrollOffset += amount;
      int i = this.displayedChatLines.size();
      if (this.scrollOffset > i - this.getLineCount()) {
         this.scrollOffset = i - this.getLineCount();
      }

      if (this.scrollOffset <= 0) {
         this.scrollOffset = 0;
         this.scrolledUp = false;
      }
   }

   @Override
   public IChatComponent getChatComponent(int mouseX, int mouseY) {
      if (!this.getChatOpen()) {
         return null;
      }

      ScaledResolution scaledResolution = new ScaledResolution(this.minecraft);
      int i = scaledResolution.getScaleFactor();
      float f = this.getChatScale();
      int j = mouseX / i - 2 - 0;
      int k = mouseY / i - 40 + 0;
      j = MathHelper.floor_float(j / f);
      k = MathHelper.floor_float(k / f);
      if (j >= 0 && k >= 0) {
         int l = Math.min(this.getLineCount(), this.displayedChatLines.size());
         if (j <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && k < this.minecraft.fontRendererObj.FONT_HEIGHT * l + l) {
            int m = k / this.minecraft.fontRendererObj.FONT_HEIGHT + this.scrollOffset;
            if (m >= 0 && m < this.displayedChatLines.size()) {
               ChatLine chatLine = this.displayedChatLines.get(m);
               int n = 0;

               for (IChatComponent iChatComponent : chatLine.getChatComponent()) {
                  if (iChatComponent instanceof ChatComponentText) {
                     n += this.minecraft.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(iChatComponent.getUnformattedTextForChat(), false));
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

   @Override
   public boolean getChatOpen() {
      return this.minecraft.currentScreen instanceof GuiChat;
   }

   @Override
   public void deleteChatLine(int id) {
      Iterator iterator2 = this.displayedChatLines.iterator();

      while (iterator2.hasNext()) {
         ChatLine chatLine = (ChatLine)iterator2.next();
         if (chatLine.getChatLineID() == id) {
            iterator2.remove();
         }
      }

      iterator2 = this.allChatLines.iterator();

      while (iterator2.hasNext()) {
         ChatLine chatLine2 = (ChatLine)iterator2.next();
         if (chatLine2.getChatLineID() == id) {
            iterator2.remove();
            break;
         }
      }
   }

   @Override
   public int getChatWidth() {
      return computeChatWidth(this.minecraft.gameSettings.l6);
   }

   @Override
   public int getChatHeight() {
      return computeChatHeight(this.getChatOpen() ? this.minecraft.gameSettings.o : this.minecraft.gameSettings.la);
   }

   @Override
   public float getChatScale() {
      return this.minecraft.gameSettings.chatScale;
   }

   public static int computeChatWidth(float f) {
      return MathHelper.floor_float(f * 280.0F + 40.0F);
   }

   public static int computeChatHeight(float f) {
      return MathHelper.floor_float(f * 160.0F + 20.0F);
   }

   @Override
   public int getLineCount() {
      return this.getChatHeight() / 9;
   }
}
