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

   public CustomChatGui(Minecraft var1) {
      super(var1);
      this.z = var1;
   }

   public static void D(long var0) {
      if (u < 1.0F) {
         u += 0.004F * (float)var0;
      }

      u = MathHelper.clamp_float(u, 0.0F, 1.0F);
   }

   @Override
   public void drawChat(int var1) {
      if (!this.f) {
         if (l == -1L) {
            l = System.currentTimeMillis();
         } else {
            long var5 = System.currentTimeMillis();
            long var7 = var5 - l;
            l = var5;
            D(var7);
            float var9 = u;
            float var10 = 1.0F - --var9 * var9 * var9 * var9;
            var10 = MathHelper.clamp_float(var10, 0.0F, 1.0F);
            if (this.z.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
               int var11 = this.getLineCount();
               int var12 = this.n.size();
               float var13 = this.z.gameSettings.chatOpacity * 0.9F + 0.1F;
               if (var12 > 0) {
                  boolean var14 = false;
                  if (this.getChatOpen()) {
                     var14 = true;
                  }

                  float var15 = this.getChatScale();
                  int var16 = MathHelper.ceiling_float_int(this.getChatWidth() / var15);
                  GlStateManager.pushMatrix();
                  if (HUD.chatSettings.getSetting("Smooth").asBoolean() && !this.m) {
                     GlStateManager.translate(2.0F, 8.0F + (9.0F - 9.0F * var10) * var15, 0.0F);
                  } else {
                     GlStateManager.translate(2.0F, 8.0F, 0.0F);
                  }

                  GlStateManager.scale(var15, var15, 1.0F);
                  int var17 = 0;

                  for (int var18 = 0; var18 + this.o < this.n.size() && var18 < var11; var18++) {
                     ChatLine var19 = this.n.get(var18 + this.o);
                     if (var19 != null) {
                        int var20 = var1 - var19.getUpdatedCounter();
                        if (var20 < 200 || var14) {
                           double var21 = var20 / 200.0;
                           var21 = 1.0 - var21;
                           var21 *= 10.0;
                           var21 = MathHelper.clamp_double(var21, 0.0, 1.0);
                           var21 *= var21;
                           int var23 = (int)(255.0 * var21);
                           if (var14) {
                              var23 = 255;
                           }

                           var23 = (int)(var23 * var13);
                           var17++;
                           if (var23 > 3) {
                              byte var24 = 0;
                              int var25 = -var18 * 9;
                              if (!HUD.chatSettings.getSetting("Clear").asBoolean()) {
                                 drawRect(-2, var25 - 9, var24 + var16 + 4, var25, var23 / 2 << 24);
                              }

                              String var26 = var19.getChatComponent().getFormattedText();
                              GlStateManager.enableBlend();
                              if (HUD.chatSettings.getSetting("Smooth").asBoolean() && var18 <= S) {
                                 this.z.fontRendererObj.drawStringWithShadow(var26, 0.0F, var25 - 8, 16777215 + ((int)(var23 * var10) << 24));
                              } else {
                                 this.z.fontRendererObj.drawStringWithShadow(var26, var24, var25 - 8, 16777215 + (var23 << 24));
                              }

                              GlStateManager.disableAlpha();
                              GlStateManager.disableBlend();
                           }
                        }
                     }
                  }

                  if (var14) {
                     int var29 = this.z.fontRendererObj.FONT_HEIGHT;
                     GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                     int var30 = var12 * var29 + var12;
                     int var31 = var17 * var29 + var17;
                     int var36 = this.o * var31 / var12;
                     int var22 = var31 * var31 / var30;
                     if (var30 != var31) {
                        int var38 = var36 > 0 ? 170 : 96;
                        int var39 = this.m ? 13382451 : 3355562;
                        drawRect(0, -var36, 2, -var36 - var22, var39 + (var38 << 24));
                        drawRect(2, -var36, 1, -var36 - var22, 13421772 + (var38 << 24));
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
   public void printChatMessage(IChatComponent var1) {
      this.printChatMessageWithOptionalDeletion(var1, 0);
   }

   @Override
   public void printChatMessageWithOptionalDeletion(IChatComponent var1, int var2) {
      u = 0.0F;
      this.setChatLine(var1, var2, this.z.ingameGUI.getUpdateCounter(), false);
      GuiNewChat.logger.info("[CHAT] " + var1.getUnformattedText());
   }

   @Override
   public void setChatLine(IChatComponent var1, int var2, int var3, boolean var4) {
      if (var2 != 0) {
         this.deleteChatLine(var2);
      }

      int var8 = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
      List var9 = GuiUtilRenderComponents.splitText(var1, var8, this.z.fontRendererObj, false, false);
      boolean var10 = this.getChatOpen();
      S = var9.size() - 1;

      for (Object var12$raw : var9) {
         IChatComponent var12 = (IChatComponent)var12$raw;
         if (var10 && this.o > 0) {
            this.m = true;
            this.scroll(1);
         }

         this.n.add(0, new ChatLine(var3, var12, var2));
      }

      while (this.n.size() > 100) {
         this.n.remove(this.n.size() - 1);
      }

      if (!var4) {
         this.X.add(0, new ChatLine(var3, var1, var2));

         while (this.X.size() > 100) {
            this.X.remove(this.X.size() - 1);
         }
      }
   }

   @Override
   public void refreshChat() {
      this.n.clear();
      this.resetScroll();

      for (int var4 = this.X.size() - 1; var4 >= 0; var4--) {
         ChatLine var5 = this.X.get(var4);
         this.setChatLine(var5.getChatComponent(), var5.getChatLineID(), var5.getUpdatedCounter(), true);
      }
   }

   @Override
   public List<String> getSentMessages() {
      return this.J;
   }

   @Override
   public void addToSentMessages(String var1) {
      if (this.J.isEmpty() || !this.J.get(this.J.size() - 1).equals(var1)) {
         this.J.add(var1);
      }
   }

   @Override
   public void resetScroll() {
      this.o = 0;
      this.m = false;
   }

   @Override
   public void scroll(int var1) {
      this.o += var1;
      int var5 = this.n.size();
      if (this.o > var5 - this.getLineCount()) {
         this.o = var5 - this.getLineCount();
      }

      if (this.o <= 0) {
         this.o = 0;
         this.m = false;
      }
   }

   @Override
   public IChatComponent getChatComponent(int var1, int var2) {
      if (!this.getChatOpen()) {
         return null;
      }

      ScaledResolution var6 = new ScaledResolution(this.z);
      int var7 = var6.getScaleFactor();
      float var8 = this.getChatScale();
      int var9 = var1 / var7 - 2 - 0;
      int var10 = var2 / var7 - 40 + 0;
      var9 = MathHelper.floor_float(var9 / var8);
      var10 = MathHelper.floor_float(var10 / var8);
      if (var9 >= 0 && var10 >= 0) {
         int var11 = Math.min(this.getLineCount(), this.n.size());
         if (var9 <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && var10 < this.z.fontRendererObj.FONT_HEIGHT * var11 + var11) {
            int var12 = var10 / this.z.fontRendererObj.FONT_HEIGHT + this.o;
            if (var12 >= 0 && var12 < this.n.size()) {
               ChatLine var13 = this.n.get(var12);
               int var14 = 0;

               for (IChatComponent var16 : var13.getChatComponent()) {
                  if (var16 instanceof ChatComponentText) {
                     var14 += this.z.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(var16.getUnformattedTextForChat(), false));
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

   @Override
   public boolean getChatOpen() {
      return this.z.currentScreen instanceof GuiChat;
   }

   @Override
   public void deleteChatLine(int var1) {
      Iterator var5 = this.n.iterator();

      while (var5.hasNext()) {
         ChatLine var6 = (ChatLine)var5.next();
         if (var6.getChatLineID() == var1) {
            var5.remove();
         }
      }

      var5 = this.X.iterator();

      while (var5.hasNext()) {
         ChatLine var8 = (ChatLine)var5.next();
         if (var8.getChatLineID() == var1) {
            var5.remove();
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

   public static int S(float var0) {
      return MathHelper.floor_float(var0 * 280.0F + 40.0F);
   }

   public static int R(float var0) {
      return MathHelper.floor_float(var0 * 160.0F + 20.0F);
   }

   @Override
   public int getLineCount() {
      return this.getChatHeight() / 9;
   }
}
