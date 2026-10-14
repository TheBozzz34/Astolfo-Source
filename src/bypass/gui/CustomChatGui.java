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
   private final Minecraft z;
   private final List<String> J = Lists.newArrayList();
   private final List<ChatLine> X = Lists.newArrayList();
   private final List<ChatLine> n = Lists.newArrayList();
   private int o;
   private boolean m;
   public static float u = 0.0F;
   public static int S;
   public static long l = -1L;
   public boolean f;
   private static final long c = 127710865482847L;

   public CustomChatGui(Minecraft minecraft) {
      super(minecraft);
      this.z = minecraft;
   }

   public static void D(long m) {
      if (u < 1.0F) {
         u += 0.004F * (float)m;
      }

      u = MathHelper.clamp_float(u, 0.0F, 1.0F);
   }

   @Override
   public void drawChat(int updateCounter) {
      if (!this.f) {
         if (l == -1L) {
            l = System.currentTimeMillis();
         } else {
            long l2 = System.currentTimeMillis();
            long l3 = l2 - l;
            l = l2;
            D(l3);
            float g = u;
            float h = 1.0F - --g * g * g * g;
            h = MathHelper.clamp_float(h, 0.0F, 1.0F);
            if (this.z.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
               int i = this.getLineCount();
               int j = this.n.size();
               float f2 = this.z.gameSettings.chatOpacity * 0.9F + 0.1F;
               if (j > 0) {
                  boolean bl = false;
                  if (this.getChatOpen()) {
                     bl = true;
                  }

                  float f3 = this.getChatScale();
                  int k = MathHelper.ceiling_float_int(this.getChatWidth() / f3);
                  GlStateManager.pushMatrix();
                  if (HUD.chatSettings.getSetting("Smooth").asBoolean() && !this.m) {
                     GlStateManager.translate(2.0F, 8.0F + (9.0F - 9.0F * h) * f3, 0.0F);
                  } else {
                     GlStateManager.translate(2.0F, 8.0F, 0.0F);
                  }

                  GlStateManager.scale(f3, f3, 1.0F);
                  int i2 = 0;

                  for (int i3 = 0; i3 + this.o < this.n.size() && i3 < i; i3++) {
                     ChatLine chatLine = this.n.get(i3 + this.o);
                     if (chatLine != null) {
                        int i4 = updateCounter - chatLine.getUpdatedCounter();
                        if (i4 < 200 || bl) {
                           double d = i4 / 200.0;
                           d = 1.0 - d;
                           d *= 10.0;
                           d = MathHelper.clamp_double(d, 0.0, 1.0);
                           d *= d;
                           int i5 = (int)(255.0 * d);
                           if (bl) {
                              i5 = 255;
                           }

                           i5 = (int)(i5 * f2);
                           i2++;
                           if (i5 > 3) {
                              byte b = 0;
                              int i6 = -i3 * 9;
                              if (!HUD.chatSettings.getSetting("Clear").asBoolean()) {
                                 drawRect(-2, i6 - 9, b + k + 4, i6, i5 / 2 << 24);
                              }

                              String string = chatLine.getChatComponent().getFormattedText();
                              GlStateManager.enableBlend();
                              if (HUD.chatSettings.getSetting("Smooth").asBoolean() && i3 <= S) {
                                 this.z.fontRendererObj.drawStringWithShadow(string, 0.0F, i6 - 8, 16777215 + ((int)(i5 * h) << 24));
                              } else {
                                 this.z.fontRendererObj.drawStringWithShadow(string, b, i6 - 8, 16777215 + (i5 << 24));
                              }

                              GlStateManager.disableAlpha();
                              GlStateManager.disableBlend();
                           }
                        }
                     }
                  }

                  if (bl) {
                     int i7 = this.z.fontRendererObj.FONT_HEIGHT;
                     GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                     int i8 = j * i7 + j;
                     int i9 = i2 * i7 + i2;
                     int i10 = this.o * i9 / j;
                     int i11 = i9 * i9 / i8;
                     if (i8 != i9) {
                        int i12 = i10 > 0 ? 170 : 96;
                        int i13 = this.m ? 13382451 : 3355562;
                        drawRect(0, -i10, 2, -i10 - i11, i13 + (i12 << 24));
                        drawRect(2, -i10, 1, -i10 - i11, 13421772 + (i12 << 24));
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
      this.n.clear();
      this.X.clear();
      this.J.clear();
   }

   @Override
   public void printChatMessage(IChatComponent chatComponent) {
      this.printChatMessageWithOptionalDeletion(chatComponent, 0);
   }

   @Override
   public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
      u = 0.0F;
      this.setChatLine(chatComponent, chatLineId, this.z.ingameGUI.getUpdateCounter(), false);
      GuiNewChat.logger.info("[CHAT] " + chatComponent.getUnformattedText());
   }

   @Override
   public void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
      if (chatLineId != 0) {
         this.deleteChatLine(chatLineId);
      }

      int i = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
      List list = GuiUtilRenderComponents.splitText(chatComponent, i, this.z.fontRendererObj, false, false);
      boolean bl = this.getChatOpen();
      S = list.size() - 1;

      for (Object object : list) {
         IChatComponent iChatComponent = (IChatComponent)object;
         if (bl && this.o > 0) {
            this.m = true;
            this.scroll(1);
         }

         this.n.add(0, new ChatLine(updateCounter, iChatComponent, chatLineId));
      }

      while (this.n.size() > 100) {
         this.n.remove(this.n.size() - 1);
      }

      if (!displayOnly) {
         this.X.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

         while (this.X.size() > 100) {
            this.X.remove(this.X.size() - 1);
         }
      }
   }

   @Override
   public void refreshChat() {
      this.n.clear();
      this.resetScroll();

      for (int i = this.X.size() - 1; i >= 0; i--) {
         ChatLine chatLine = this.X.get(i);
         this.setChatLine(chatLine.getChatComponent(), chatLine.getChatLineID(), chatLine.getUpdatedCounter(), true);
      }
   }

   @Override
   public List<String> getSentMessages() {
      return this.J;
   }

   @Override
   public void addToSentMessages(String message) {
      if (this.J.isEmpty() || !this.J.get(this.J.size() - 1).equals(message)) {
         this.J.add(message);
      }
   }

   @Override
   public void resetScroll() {
      this.o = 0;
      this.m = false;
   }

   @Override
   public void scroll(int amount) {
      this.o += amount;
      int i = this.n.size();
      if (this.o > i - this.getLineCount()) {
         this.o = i - this.getLineCount();
      }

      if (this.o <= 0) {
         this.o = 0;
         this.m = false;
      }
   }

   @Override
   public IChatComponent getChatComponent(int mouseX, int mouseY) {
      if (!this.getChatOpen()) {
         return null;
      }

      ScaledResolution scaledResolution = new ScaledResolution(this.z);
      int i = scaledResolution.getScaleFactor();
      float f = this.getChatScale();
      int j = mouseX / i - 2 - 0;
      int k = mouseY / i - 40 + 0;
      j = MathHelper.floor_float(j / f);
      k = MathHelper.floor_float(k / f);
      if (j >= 0 && k >= 0) {
         int l = Math.min(this.getLineCount(), this.n.size());
         if (j <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && k < this.z.fontRendererObj.FONT_HEIGHT * l + l) {
            int m = k / this.z.fontRendererObj.FONT_HEIGHT + this.o;
            if (m >= 0 && m < this.n.size()) {
               ChatLine chatLine = this.n.get(m);
               int i2 = 0;

               for (IChatComponent iChatComponent : chatLine.getChatComponent()) {
                  if (iChatComponent instanceof ChatComponentText) {
                     i2 += this.z.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(iChatComponent.getUnformattedTextForChat(), false));
                     if (i2 > j) {
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
      return this.z.currentScreen instanceof GuiChat;
   }

   @Override
   public void deleteChatLine(int id) {
      Iterator iterator2 = this.n.iterator();

      while (iterator2.hasNext()) {
         ChatLine chatLine = (ChatLine)iterator2.next();
         if (chatLine.getChatLineID() == id) {
            iterator2.remove();
         }
      }

      iterator2 = this.X.iterator();

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
      return S(this.z.gameSettings.l6);
   }

   @Override
   public int getChatHeight() {
      return R(this.getChatOpen() ? this.z.gameSettings.o : this.z.gameSettings.la);
   }

   @Override
   public float getChatScale() {
      return this.z.gameSettings.chatScale;
   }

   public static int S(float f) {
      return MathHelper.floor_float(f * 280.0F + 40.0F);
   }

   public static int R(float f) {
      return MathHelper.floor_float(f * 160.0F + 20.0F);
   }

   @Override
   public int getLineCount() {
      return this.getChatHeight() / 9;
   }
}
