package net.minecraft.client.gui;

import bypass.Astolfo;
import bypass.module.visuals.StreamerMode;
import bypass.user.AstolfoUser;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

public class GuiPlayerTabOverlay extends Gui {
   public static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
   private final Minecraft mc;
   private final GuiIngame guiIngame;
   private IChatComponent footer;
   private IChatComponent header;
   private long lastTimeOpened;
   private boolean isBeingRendered;
   private static final long a = 72151825232001L;

   public GuiPlayerTabOverlay(Minecraft var1, GuiIngame var2) {
      this.mc = var1;
      this.guiIngame = var2;
   }

   public String getPlayerName(NetworkPlayerInfo var1) {
      return var1.getDisplayName() != null
         ? var1.getDisplayName().getFormattedText()
         : ScorePlayerTeam.formatPlayerName(var1.getPlayerTeam(), var1.getGameProfile().getName());
   }

   public void updatePlayerList(boolean var1) {
      if (var1 && !this.isBeingRendered) {
         this.lastTimeOpened = Minecraft.getSystemTime();
      }

      this.isBeingRendered = var1;
   }

   public void renderPlayerlist(int var1, Scoreboard var2, ScoreObjective var3) {
      long var4 = a ^ 40483006498369L;
      long var6 = var4 ^ 90406025431923L;
      long var8 = var4 ^ 3383636309562L;

      try {
         NetHandlerPlayClient var11 = this.mc.thePlayer.sendQueue;
         List var12 = field_175252_a.sortedCopy(var11.getPlayerInfoMap());
         int var13 = 0;
         int var14 = 0;

         for (Object var16$raw : var12) {
            NetworkPlayerInfo var16 = (NetworkPlayerInfo)var16$raw;
            String var17 = this.getPlayerName(var16).replace("§k", "");
            if (var16.getGameProfile() != null && Astolfo.INSTANCE.getUserManager().g(var16.getGameProfile().getName()) != null) {
               AstolfoUser var18 = Astolfo.INSTANCE.getUserManager().g(var16.getGameProfile().getName());
               StringBuilder var19 = new StringBuilder(var17);
               var19.insert(0, String.format("%s%s %s- ", EnumChatFormatting.LIGHT_PURPLE, var18.s, EnumChatFormatting.GRAY));
               var17 = var19.toString();
            }

            int var45 = this.mc.fontRendererObj.getStringWidth(var17);
            var13 = Math.max(var13, var45);
            if (var3 != null && var3.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
               var45 = this.mc.fontRendererObj.getStringWidth(" " + var2.getValueFromObjective(var16.getGameProfile().getName(), var3).getScorePoints());
               var14 = Math.max(var14, var45);
            }
         }

         var12 = var12.subList(0, Math.min(var12.size(), 80));
         int var42 = var12.size();
         int var43 = var42;

         int var44;
         for (var44 = 1; var43 > 20; var43 = (var42 + var44 - 1) / var44) {
            var44++;
         }

         boolean var47 = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
         int var48;
         if (var3 != null) {
            if (var3.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
               var48 = 90;
            } else {
               var48 = var14;
            }
         } else {
            var48 = 0;
         }

         int var20 = Math.min(var44 * ((var47 ? 9 : 0) + var13 + var48 + 13), var1 - 50) / var44;
         int var21 = var1 / 2 - (var20 * var44 + (var44 - 1) * 5) / 2;
         int var22 = 10;
         int var23 = var20 * var44 + (var44 - 1) * 5;
         List var24 = null;
         List var25 = null;
         if (this.header != null) {
            var24 = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), var1 - 50);

            for (Object var27$raw : var24) {
               String var27 = (String)var27$raw;
               var23 = Math.max(var23, this.mc.fontRendererObj.getStringWidth(var27));
            }
         }

         if (this.footer != null) {
            var25 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), var1 - 50);

            for (Object var54$raw : var25) {
               String var54 = (String)var54$raw;
               var23 = Math.max(var23, this.mc.fontRendererObj.getStringWidth(var54));
            }
         }

         if (var24 != null) {
            drawRect(
               var1 / 2 - var23 / 2 - 1, var22 - 1, var1 / 2 + var23 / 2 + 1, var22 + var24.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE
            );

            for (Object var55$raw : var24) {
               String var55 = (String)var55$raw;
               int var28 = this.mc.fontRendererObj.getStringWidth(var55);
               this.mc.fontRendererObj.drawStringWithShadow(var55, var1 / 2 - var28 / 2, var22, -1);
               var22 += this.mc.fontRendererObj.FONT_HEIGHT;
            }

            var22++;
         }

         drawRect(var1 / 2 - var23 / 2 - 1, var22 - 1, var1 / 2 + var23 / 2 + 1, var22 + var43 * 9, Integer.MIN_VALUE);

         for (int var52 = 0; var52 < var42; var52++) {
            int var56 = var52 / var43;
            int var58 = var52 % var43;
            int var29 = var21 + var56 * var20 + var56 * 5;
            int var30 = var22 + var58 * 9;
            drawRect(var29, var30, var29 + var20, var30 + 8, 553648127);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            long var10000 = 6267255035119345657L ^ var4;
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            if (var52 < var12.size()) {
               NetworkPlayerInfo var31 = (NetworkPlayerInfo)var12.get(var52);
               String var32 = this.getPlayerName(var31).replace("§k", "");
               if (var31.getGameProfile() != null && Astolfo.INSTANCE.getUserManager().g(var31.getGameProfile().getName()) != null) {
                  AstolfoUser var33 = Astolfo.INSTANCE.getUserManager().g(var31.getGameProfile().getName());
                  StringBuilder var34 = new StringBuilder(var32);
                  var34.insert(0, String.format("%s%s %s- ", EnumChatFormatting.LIGHT_PURPLE, var33.s, EnumChatFormatting.GRAY));
                  var32 = var34.toString();
               }

               if (!Astolfo.INSTANCE.getModuleManager().get(StreamerMode.class).isEnabled()
                  || this.mc.thePlayer.getPlayerInfo() == null
                  || !var32.contains(this.mc.thePlayer.getPlayerInfo().getGameProfile().getName())) {
                  GameProfile var61 = var31.getGameProfile();
                  if (var47) {
                     EntityPlayer var62 = this.mc.theWorld.getPlayerEntityByUUID(var61.getId());
                     boolean var35 = var62 != null
                        && var62.isWearing(EnumPlayerModelParts.CAPE)
                        && (var61.getName().equals("Dinnerbone") || var61.getName().equals("Grumm"));
                     this.mc.getTextureManager().bindTexture(var31.c());
                     int var36 = 8 + (var35 ? 8 : 0);
                     int var37 = 8 * (var35 ? -1 : 1);
                     Gui.drawScaledCustomSizeModalRect(var29, var30, 8.0F, var36, 8, var37, 8, 8, 64.0F, 64.0F);
                     if (var62 != null && var62.isWearing(EnumPlayerModelParts.HAT)) {
                        int var38 = 8 + (var35 ? 8 : 0);
                        int var39 = 8 * (var35 ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(var29, var30, 40.0F, var38, 8, var39, 8, 8, 64.0F, 64.0F);
                     }

                     var29 += 9;
                  }

                  if (var31.getGameType() == WorldSettings.GameType.SPECTATOR) {
                     var32 = EnumChatFormatting.ITALIC + var32;
                     this.mc.fontRendererObj.drawStringWithShadow(var32, var29, var30, -1862270977);
                  } else {
                     this.mc.fontRendererObj.drawStringWithShadow(var32, var29, var30, -1);
                  }

                  if (var3 != null && var31.getGameType() != WorldSettings.GameType.SPECTATOR) {
                     int var63 = var29 + var13 + 1;
                     int var64 = var63 + var48;
                     if (var64 - var63 > 5) {
                        this.drawScoreboardValues(var3, var30, var61.getName(), var63, var64, var31);
                     }
                  }

                  this.drawPing(var20, var29 - (var47 ? 9 : 0), var30, var31);
               }
            }
         }

         if (var25 != null) {
            var22 = var22 + var43 * 9 + 1;
            drawRect(
               var1 / 2 - var23 / 2 - 1, var22 - 1, var1 / 2 + var23 / 2 + 1, var22 + var25.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE
            );

            for (Object var57$raw : var25) {
               String var57 = (String)var57$raw;
               int var59 = this.mc.fontRendererObj.getStringWidth(var57);
               this.mc.fontRendererObj.drawStringWithShadow(var57, var1 / 2 - var59 / 2, var22, -1);
               var22 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
         }
      } catch (Exception var40) {
      }
   }

   public void drawPing(int var1, int var2, int var3, NetworkPlayerInfo var4) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
      byte var8 = 0;
      byte var9 = 0;
      if (var4.getResponseTime() < 0) {
         var9 = 5;
      } else if (var4.getResponseTime() < 150) {
         var9 = 0;
      } else if (var4.getResponseTime() < 300) {
         var9 = 1;
      } else if (var4.getResponseTime() < 600) {
         var9 = 2;
      } else if (var4.getResponseTime() < 1000) {
         var9 = 3;
      } else {
         var9 = 4;
      }

      this.zLevel += 100.0F;
      this.drawTexturedModalRect(var2 + var1 - 11, var3, 0 + var8 * 10, 176 + var9 * 8, 10, 8);
      this.zLevel -= 100.0F;
   }

   private void drawScoreboardValues(ScoreObjective var1, int var2, String var3, int var4, int var5, NetworkPlayerInfo var6) {
      int var10 = var1.getScoreboard().getValueFromObjective(var3, var1).getScorePoints();
      if (var1.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
         this.mc.getTextureManager().bindTexture(icons);
         if (this.lastTimeOpened == var6.func_178855_p()) {
            if (var10 < var6.func_178835_l()) {
               var6.func_178846_a(Minecraft.getSystemTime());
               var6.func_178844_b(this.guiIngame.getUpdateCounter() + 20);
            } else if (var10 > var6.func_178835_l()) {
               var6.func_178846_a(Minecraft.getSystemTime());
               var6.func_178844_b(this.guiIngame.getUpdateCounter() + 10);
            }
         }

         if (Minecraft.getSystemTime() - var6.func_178847_n() > 1000L || this.lastTimeOpened != var6.func_178855_p()) {
            var6.func_178836_b(var10);
            var6.func_178857_c(var10);
            var6.func_178846_a(Minecraft.getSystemTime());
         }

         var6.func_178843_c(this.lastTimeOpened);
         var6.func_178836_b(var10);
         int var11 = MathHelper.ceiling_float_int(Math.max(var10, var6.func_178860_m()) / 2.0F);
         int var12 = Math.max(MathHelper.ceiling_float_int(var10 / 2), Math.max(MathHelper.ceiling_float_int(var6.func_178860_m() / 2), 10));
         boolean var13 = var6.func_178858_o() > this.guiIngame.getUpdateCounter() && (var6.func_178858_o() - this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;
         if (var11 > 0) {
            float var14 = Math.min((float)(var5 - var4 - 4) / var12, 9.0F);
            if (var14 > 3.0F) {
               for (int var15 = var11; var15 < var12; var15++) {
                  this.drawTexturedModalRect(var4 + var15 * var14, var2, var13 ? 25 : 16, 0, 9, 9);
               }

               for (int var19 = 0; var19 < var11; var19++) {
                  this.drawTexturedModalRect(var4 + var19 * var14, var2, var13 ? 25 : 16, 0, 9, 9);
                  if (var13) {
                     if (var19 * 2 + 1 < var6.func_178860_m()) {
                        this.drawTexturedModalRect(var4 + var19 * var14, var2, 70, 0, 9, 9);
                     }

                     if (var19 * 2 + 1 == var6.func_178860_m()) {
                        this.drawTexturedModalRect(var4 + var19 * var14, var2, 79, 0, 9, 9);
                     }
                  }

                  if (var19 * 2 + 1 < var10) {
                     this.drawTexturedModalRect(var4 + var19 * var14, var2, var19 >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (var19 * 2 + 1 == var10) {
                     this.drawTexturedModalRect(var4 + var19 * var14, var2, var19 >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float var20 = MathHelper.clamp_float(var10 / 20.0F, 0.0F, 1.0F);
               int var16 = (int)((1.0F - var20) * 255.0F) << 16 | (int)(var20 * 255.0F) << 8;
               String var17 = "" + var10 / 2.0F;
               if (var5 - this.mc.fontRendererObj.getStringWidth(var17 + "hp") >= var4) {
                  var17 = var17 + "hp";
               }

               this.mc.fontRendererObj.drawStringWithShadow(var17, (var5 + var4) / 2 - this.mc.fontRendererObj.getStringWidth(var17) / 2, var2, var16);
            }
         }
      } else {
         String var18 = EnumChatFormatting.YELLOW + "" + var10;
         this.mc.fontRendererObj.drawStringWithShadow(var18, var5 - this.mc.fontRendererObj.getStringWidth(var18), var2, 16777215);
      }
   }

   public void setFooter(IChatComponent var1) {
      this.footer = var1;
   }

   public void setHeader(IChatComponent var1) {
      this.header = var1;
   }

   public void resetFooterHeader() {
      this.header = null;
      this.footer = null;
   }

   public static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
      private static final long a = 46248067067356L;

      public PlayerComparator() {
      }

      public int compare(NetworkPlayerInfo var1, NetworkPlayerInfo var2) {
         ScorePlayerTeam var6 = var1.getPlayerTeam();
         ScorePlayerTeam var7 = var2.getPlayerTeam();
         return ComparisonChain.start()
            .compareTrueFirst(var1.getGameType() != WorldSettings.GameType.SPECTATOR, var2.getGameType() != WorldSettings.GameType.SPECTATOR)
            .compare(var6 != null ? var6.getRegisteredName() : "", var7 != null ? var7.getRegisteredName() : "")
            .compare(var1.getGameProfile().getName(), var2.getGameProfile().getName())
            .result();
      }
   }
}
