package bypass.gui.hud;

import bypass.gui.component.Component;
import bypass.hypixel.PlayerStatsEntry;
import bypass.module.hidden.PlayerListTracker;
import bypass.module.other.AutoHypixelAutoCheckerGameMode;
import bypass.render.RenderUtil;
import bypass.util.MathUtil;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;

public class StatsHud extends Component {
   private AutoHypixelAutoCheckerGameMode R;
   private int Q = 10;
   private static final long c = 58458748682171L;

   public StatsHud(float f, float g, int i, int j) {
      super("Stats", f, g, i, j);
      this.F(true);
   }

   @Override
   public void render(int i, int j) {
      long l = c ^ 37543297409481L;
      if (this.R != null) {
         float f = this.M();
         float g = this.E_float();
         long l2 = 1295372147817266408L ^ l;
         float h = this.x();
         int k = (int)(h / 12.0F);
         int i2 = 1;
         byte b = 25;

         for (PlayerStatsEntry playerStatsEntry : PlayerListTracker.playerStats) {
            if (i2 > this.Q) {
               break;
            }

            if (PlayerListTracker.tabListNames.contains(playerStatsEntry.Q)) {
               b += 12;
               i2++;
            }
         }

         int i3 = b + 5;
         float f2 = mc.fontRendererObj.getStringWidth("PLAYERS");
         float f3 = mc.fontRendererObj.getStringWidth("30000");
         float f4 = this.c();
         float f5 = f4 + f2 + f3 * this.R.columnAmount + 15.0F;
         RenderUtil.drawRect(f, g, f + f5, g + 15.0F, new Color(1, 1, 1, 180).getRGB());
         RenderUtil.drawRect(f, g + 15.0F, f + f5, g + i3, new Color(1, 1, 1, 120).getRGB());
         switch (this.R) {
            case BW:
               PlayerListTracker.playerStats.sort((playerStatsEntry2, playerStatsEntry3) -> Float.compare(playerStatsEntry3.w, playerStatsEntry2.w));
               break;
            case SW:
               PlayerListTracker.playerStats.sort((playerStatsEntry4, playerStatsEntry5) -> Float.compare(playerStatsEntry5.sF, playerStatsEntry4.sF));
               break;
            case UHC:
               PlayerListTracker.playerStats.sort((playerStatsEntry6, playerStatsEntry7) -> Float.compare(playerStatsEntry7.O, playerStatsEntry6.O));
               break;
            case MW:
               PlayerListTracker.playerStats.sort((playerStatsEntry8, playerStatsEntry9) -> Float.compare(playerStatsEntry9.F, playerStatsEntry8.F));
         }

         mc.fontRendererObj.drawStringWithShadow("PLAYERS", this.M() + 11.0F, this.E_float() + 4.0F, -1);
         byte b2 = 25;
         int i4 = 1;

         for (PlayerStatsEntry playerStatsEntry10 : PlayerListTracker.playerStats) {
            if (i4 > this.Q) {
               break;
            }

            if (PlayerListTracker.tabListNames.contains(playerStatsEntry10.Q)) {
               if (playerStatsEntry10.m != null) {
                  mc.getTextureManager().bindTexture(playerStatsEntry10.m.c());
                  Gui.drawScaledCustomSizeModalRect((int)(this.M() + k - 12.0F), (int)(this.E_float() + b2), 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
               }

               switch (this.R) {
                  case BW:
                     mc.fontRendererObj.drawStringWithShadow("WS", this.M() + f4 + f2, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(String.valueOf(playerStatsEntry10.w), this.M() + f4 + f2, this.E_float() + b2, -1);
                     RenderUtil.drawArrow(this.M() + f4 + f2 + 14.0F, this.E_float() + 10.0F, 6.0, false, Color.WHITE.getRGB());
                     mc.fontRendererObj.drawStringWithShadow("KD", this.M() + f4 + f2 + f3, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.Z), this.M() + f4 + f3 + f2, this.E_float() + b2, -1);
                     mc.fontRendererObj.drawStringWithShadow("FKD", this.M() + f4 + f2 + f3 * 2.0F, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.sO), this.M() + f4 + f3 * 2.0F + f2, this.E_float() + b2, -1);
                     mc.fontRendererObj.drawStringWithShadow("STARS", this.M() + f4 + f2 + f3 * 3.0F, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(String.valueOf(playerStatsEntry10.sV), this.M() + f4 + f3 * 3.0F + f2, this.E_float() + b2, -1);
                     break;
                  case SW:
                     mc.fontRendererObj.drawStringWithShadow("KD", this.M() + f4 + f2, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.sF), this.M() + f4 + f2, this.E_float() + b2, -1);
                     RenderUtil.drawArrow(this.M() + f4 + f2 + 14.0F, this.E_float() + 10.0F, 6.0, false, Color.WHITE.getRGB());
                     mc.fontRendererObj.drawStringWithShadow("WL", this.M() + f4 + f2 + f3, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.M), this.M() + f4 + f3 + f2, this.E_float() + b2, -1);
                     break;
                  case UHC:
                     mc.fontRendererObj.drawStringWithShadow("SKD", this.M() + f4 + f2, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.n), this.M() + f4 + f2, this.E_float() + b2, -1);
                     mc.fontRendererObj.drawStringWithShadow("TKD", this.M() + f4 + f2 + f3, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.e), this.M() + f4 + f3 + f2, this.E_float() + b2, -1);
                     mc.fontRendererObj.drawStringWithShadow("STARS", this.M() + f4 + f2 + f3 * 2.0F, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(String.valueOf(playerStatsEntry10.V), this.M() + f4 + f3 * 2.0F + f2, this.E_float() + b2, -1);
                     break;
                  case MW:
                     mc.fontRendererObj.drawStringWithShadow("KD", this.M() + f4 + f2, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.F), this.M() + f4 + f2, this.E_float() + b2, -1);
                     RenderUtil.drawArrow(this.M() + f4 + f2 + 14.0F, this.E_float() + 10.0F, 6.0, false, Color.WHITE.getRGB());
                     mc.fontRendererObj.drawStringWithShadow("FKD", this.M() + f4 + f2 + f3, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.J), this.M() + f4 + f3 + f2, this.E_float() + b2, -1);
               }

               mc.fontRendererObj
                  .drawStringWithShadow(playerStatsEntry10.E != null ? EnumChatFormatting.GOLD + playerStatsEntry10.E : playerStatsEntry10.Q, this.M() + k, this.E_float() + b2, -1);
               b2 += 12;
               i4++;
            }
         }

         this.B(i3);
      }
   }

   @Override
   public void l(int i) {
   }

   private float c() {
      float f = 30.0F;

      for (PlayerStatsEntry playerStatsEntry : PlayerListTracker.playerStats) {
         String string = playerStatsEntry.Q;
         float g = mc.fontRendererObj.getStringWidth(string);
         if (g > f) {
            f = g;
         }
      }

      return f;
   }

   public void I(int i) {
      this.Q = i;
   }

   public void J(AutoHypixelAutoCheckerGameMode autoHypixelAutoCheckerGameMode) {
      this.R = autoHypixelAutoCheckerGameMode;
   }
}
