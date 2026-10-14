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

   public GuiPlayerTabOverlay(Minecraft mcIn, GuiIngame guiIngameIn) {
      this.mc = mcIn;
      this.guiIngame = guiIngameIn;
   }

   public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
      return networkPlayerInfoIn.getDisplayName() != null
         ? networkPlayerInfoIn.getDisplayName().getFormattedText()
         : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
   }

   public void updatePlayerList(boolean willBeRendered) {
      if (willBeRendered && !this.isBeingRendered) {
         this.lastTimeOpened = Minecraft.getSystemTime();
      }

      this.isBeingRendered = willBeRendered;
   }

   public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
      long l = a ^ 40483006498369L;
      long m = l ^ 90406025431923L;
      long n = l ^ 3383636309562L;

      try {
         NetHandlerPlayClient netHandlerPlayClient = this.mc.thePlayer.sendQueue;
         List list = field_175252_a.sortedCopy(netHandlerPlayClient.getPlayerInfoMap());
         int i = 0;
         int j = 0;

         for (Object object : list) {
            NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)object;
            String string = this.getPlayerName(networkPlayerInfo).replace("§k", "");
            if (networkPlayerInfo.getGameProfile() != null && Astolfo.INSTANCE.getUserManager().g(networkPlayerInfo.getGameProfile().getName()) != null) {
               AstolfoUser astolfoUser = Astolfo.INSTANCE.getUserManager().g(networkPlayerInfo.getGameProfile().getName());
               StringBuilder stringBuilder = new StringBuilder(string);
               stringBuilder.insert(0, String.format("%s%s %s- ", EnumChatFormatting.LIGHT_PURPLE, astolfoUser.s, EnumChatFormatting.GRAY));
               string = stringBuilder.toString();
            }

            int i2 = this.mc.fontRendererObj.getStringWidth(string);
            i = Math.max(i, i2);
            if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
               i2 = this.mc
                  .fontRendererObj
                  .getStringWidth(" " + scoreboardIn.getValueFromObjective(networkPlayerInfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
               j = Math.max(j, i2);
            }
         }

         list = list.subList(0, Math.min(list.size(), 80));
         int i3 = list.size();
         int i4 = i3;

         int i5;
         for (i5 = 1; i4 > 20; i4 = (i3 + i5 - 1) / i5) {
            i5++;
         }

         boolean bl = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
         int i6;
         if (scoreObjectiveIn != null) {
            if (scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
               i6 = 90;
            } else {
               i6 = j;
            }
         } else {
            i6 = 0;
         }

         int i7 = Math.min(i5 * ((bl ? 9 : 0) + i + i6 + 13), width - 50) / i5;
         int i8 = width / 2 - (i7 * i5 + (i5 - 1) * 5) / 2;
         int i9 = 10;
         int i10 = i7 * i5 + (i5 - 1) * 5;
         List list2 = null;
         List list3 = null;
         if (this.header != null) {
            list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);

            for (Object object2 : list2) {
               String string2 = (String)object2;
               i10 = Math.max(i10, this.mc.fontRendererObj.getStringWidth(string2));
            }
         }

         if (this.footer != null) {
            list3 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);

            for (Object object3 : list3) {
               String string3 = (String)object3;
               i10 = Math.max(i10, this.mc.fontRendererObj.getStringWidth(string3));
            }
         }

         if (list2 != null) {
            drawRect(
               width / 2 - i10 / 2 - 1, i9 - 1, width / 2 + i10 / 2 + 1, i9 + list2.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE
            );

            for (Object object4 : list2) {
               String string4 = (String)object4;
               int i11 = this.mc.fontRendererObj.getStringWidth(string4);
               this.mc.fontRendererObj.drawStringWithShadow(string4, width / 2 - i11 / 2, i9, -1);
               i9 += this.mc.fontRendererObj.FONT_HEIGHT;
            }

            i9++;
         }

         drawRect(width / 2 - i10 / 2 - 1, i9 - 1, width / 2 + i10 / 2 + 1, i9 + i4 * 9, Integer.MIN_VALUE);

         for (int i12 = 0; i12 < i3; i12++) {
            int i13 = i12 / i4;
            int i14 = i12 % i4;
            int i15 = i8 + i13 * i7 + i13 * 5;
            int i16 = i9 + i14 * 9;
            drawRect(i15, i16, i15 + i7, i16 + 8, 553648127);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            long l2 = 6267255035119345657L ^ l;
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            if (i12 < list.size()) {
               NetworkPlayerInfo networkPlayerInfo2 = (NetworkPlayerInfo)list.get(i12);
               String string5 = this.getPlayerName(networkPlayerInfo2).replace("§k", "");
               if (networkPlayerInfo2.getGameProfile() != null && Astolfo.INSTANCE.getUserManager().g(networkPlayerInfo2.getGameProfile().getName()) != null) {
                  AstolfoUser astolfoUser2 = Astolfo.INSTANCE.getUserManager().g(networkPlayerInfo2.getGameProfile().getName());
                  StringBuilder stringBuilder2 = new StringBuilder(string5);
                  stringBuilder2.insert(0, String.format("%s%s %s- ", EnumChatFormatting.LIGHT_PURPLE, astolfoUser2.s, EnumChatFormatting.GRAY));
                  string5 = stringBuilder2.toString();
               }

               if (!Astolfo.INSTANCE.getModuleManager().get(StreamerMode.class).isEnabled()
                  || this.mc.thePlayer.getPlayerInfo() == null
                  || !string5.contains(this.mc.thePlayer.getPlayerInfo().getGameProfile().getName())) {
                  GameProfile gameProfile = networkPlayerInfo2.getGameProfile();
                  if (bl) {
                     EntityPlayer entityPlayer = this.mc.theWorld.getPlayerEntityByUUID(gameProfile.getId());
                     boolean bl2 = entityPlayer != null
                        && entityPlayer.isWearing(EnumPlayerModelParts.CAPE)
                        && (gameProfile.getName().equals("Dinnerbone") || gameProfile.getName().equals("Grumm"));
                     this.mc.getTextureManager().bindTexture(networkPlayerInfo2.c());
                     int i17 = 8 + (bl2 ? 8 : 0);
                     int i18 = 8 * (bl2 ? -1 : 1);
                     Gui.drawScaledCustomSizeModalRect(i15, i16, 8.0F, i17, 8, i18, 8, 8, 64.0F, 64.0F);
                     if (entityPlayer != null && entityPlayer.isWearing(EnumPlayerModelParts.HAT)) {
                        int i19 = 8 + (bl2 ? 8 : 0);
                        int i20 = 8 * (bl2 ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(i15, i16, 40.0F, i19, 8, i20, 8, 8, 64.0F, 64.0F);
                     }

                     i15 += 9;
                  }

                  if (networkPlayerInfo2.getGameType() == WorldSettings.GameType.SPECTATOR) {
                     string5 = EnumChatFormatting.ITALIC + string5;
                     this.mc.fontRendererObj.drawStringWithShadow(string5, i15, i16, -1862270977);
                  } else {
                     this.mc.fontRendererObj.drawStringWithShadow(string5, i15, i16, -1);
                  }

                  if (scoreObjectiveIn != null && networkPlayerInfo2.getGameType() != WorldSettings.GameType.SPECTATOR) {
                     int i21 = i15 + i + 1;
                     int i22 = i21 + i6;
                     if (i22 - i21 > 5) {
                        this.drawScoreboardValues(scoreObjectiveIn, i16, gameProfile.getName(), i21, i22, networkPlayerInfo2);
                     }
                  }

                  this.drawPing(i7, i15 - (bl ? 9 : 0), i16, networkPlayerInfo2);
               }
            }
         }

         if (list3 != null) {
            i9 = i9 + i4 * 9 + 1;
            drawRect(
               width / 2 - i10 / 2 - 1, i9 - 1, width / 2 + i10 / 2 + 1, i9 + list3.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE
            );

            for (Object object5 : list3) {
               String string6 = (String)object5;
               int i23 = this.mc.fontRendererObj.getStringWidth(string6);
               this.mc.fontRendererObj.drawStringWithShadow(string6, width / 2 - i23 / 2, i9, -1);
               i9 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
         }
      } catch (Exception exception) {
      }
   }

   public void drawPing(int i, int j, int k, NetworkPlayerInfo networkPlayerInfoIn) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
      byte b = 0;
      byte b2 = 0;
      if (networkPlayerInfoIn.getResponseTime() < 0) {
         b2 = 5;
      } else if (networkPlayerInfoIn.getResponseTime() < 150) {
         b2 = 0;
      } else if (networkPlayerInfoIn.getResponseTime() < 300) {
         b2 = 1;
      } else if (networkPlayerInfoIn.getResponseTime() < 600) {
         b2 = 2;
      } else if (networkPlayerInfoIn.getResponseTime() < 1000) {
         b2 = 3;
      } else {
         b2 = 4;
      }

      this.zLevel += 100.0F;
      this.drawTexturedModalRect(j + i - 11, k, 0 + b * 10, 176 + b2 * 8, 10, 8);
      this.zLevel -= 100.0F;
   }

   private void drawScoreboardValues(ScoreObjective scoreObjective, int i, String string, int j, int k, NetworkPlayerInfo networkPlayerInfo) {
      int l = scoreObjective.getScoreboard().getValueFromObjective(string, scoreObjective).getScorePoints();
      if (scoreObjective.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
         this.mc.getTextureManager().bindTexture(icons);
         if (this.lastTimeOpened == networkPlayerInfo.func_178855_p()) {
            if (l < networkPlayerInfo.func_178835_l()) {
               networkPlayerInfo.func_178846_a(Minecraft.getSystemTime());
               networkPlayerInfo.func_178844_b(this.guiIngame.getUpdateCounter() + 20);
            } else if (l > networkPlayerInfo.func_178835_l()) {
               networkPlayerInfo.func_178846_a(Minecraft.getSystemTime());
               networkPlayerInfo.func_178844_b(this.guiIngame.getUpdateCounter() + 10);
            }
         }

         if (Minecraft.getSystemTime() - networkPlayerInfo.func_178847_n() > 1000L || this.lastTimeOpened != networkPlayerInfo.func_178855_p()) {
            networkPlayerInfo.func_178836_b(l);
            networkPlayerInfo.func_178857_c(l);
            networkPlayerInfo.func_178846_a(Minecraft.getSystemTime());
         }

         networkPlayerInfo.func_178843_c(this.lastTimeOpened);
         networkPlayerInfo.func_178836_b(l);
         int m = MathHelper.ceiling_float_int(Math.max(l, networkPlayerInfo.func_178860_m()) / 2.0F);
         int n = Math.max(MathHelper.ceiling_float_int(l / 2), Math.max(MathHelper.ceiling_float_int(networkPlayerInfo.func_178860_m() / 2), 10));
         boolean bl = networkPlayerInfo.func_178858_o() > this.guiIngame.getUpdateCounter()
            && (networkPlayerInfo.func_178858_o() - this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;
         if (m > 0) {
            float f = Math.min((float)(k - j - 4) / n, 9.0F);
            if (f > 3.0F) {
               for (int i2 = m; i2 < n; i2++) {
                  this.drawTexturedModalRect(j + i2 * f, i, bl ? 25 : 16, 0, 9, 9);
               }

               for (int i3 = 0; i3 < m; i3++) {
                  this.drawTexturedModalRect(j + i3 * f, i, bl ? 25 : 16, 0, 9, 9);
                  if (bl) {
                     if (i3 * 2 + 1 < networkPlayerInfo.func_178860_m()) {
                        this.drawTexturedModalRect(j + i3 * f, i, 70, 0, 9, 9);
                     }

                     if (i3 * 2 + 1 == networkPlayerInfo.func_178860_m()) {
                        this.drawTexturedModalRect(j + i3 * f, i, 79, 0, 9, 9);
                     }
                  }

                  if (i3 * 2 + 1 < l) {
                     this.drawTexturedModalRect(j + i3 * f, i, i3 >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (i3 * 2 + 1 == l) {
                     this.drawTexturedModalRect(j + i3 * f, i, i3 >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float g = MathHelper.clamp_float(l / 20.0F, 0.0F, 1.0F);
               int i4 = (int)((1.0F - g) * 255.0F) << 16 | (int)(g * 255.0F) << 8;
               String string2 = "" + l / 2.0F;
               if (k - this.mc.fontRendererObj.getStringWidth(string2 + "hp") >= j) {
                  string2 = string2 + "hp";
               }

               this.mc.fontRendererObj.drawStringWithShadow(string2, (k + j) / 2 - this.mc.fontRendererObj.getStringWidth(string2) / 2, i, i4);
            }
         }
      } else {
         String string3 = EnumChatFormatting.YELLOW + "" + l;
         this.mc.fontRendererObj.drawStringWithShadow(string3, k - this.mc.fontRendererObj.getStringWidth(string3), i, 16777215);
      }
   }

   public void setFooter(IChatComponent footerIn) {
      this.footer = footerIn;
   }

   public void setHeader(IChatComponent headerIn) {
      this.header = headerIn;
   }

   public void resetFooterHeader() {
      this.header = null;
      this.footer = null;
   }

   public static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
      private static final long a = 46248067067356L;

      public PlayerComparator() {
      }

      public int compare(NetworkPlayerInfo networkPlayerInfo, NetworkPlayerInfo networkPlayerInfo2) {
         ScorePlayerTeam scorePlayerTeam = networkPlayerInfo.getPlayerTeam();
         ScorePlayerTeam scorePlayerTeam2 = networkPlayerInfo2.getPlayerTeam();
         return ComparisonChain.start()
            .compareTrueFirst(
               networkPlayerInfo.getGameType() != WorldSettings.GameType.SPECTATOR, networkPlayerInfo2.getGameType() != WorldSettings.GameType.SPECTATOR
            )
            .compare(scorePlayerTeam != null ? scorePlayerTeam.getRegisteredName() : "", scorePlayerTeam2 != null ? scorePlayerTeam2.getRegisteredName() : "")
            .compare(networkPlayerInfo.getGameProfile().getName(), networkPlayerInfo2.getGameProfile().getName())
            .result();
      }
   }
}
