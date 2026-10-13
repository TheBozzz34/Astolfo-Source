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

   public StatsHud(float var1, float var2, int var3, int var4) {
      super("Stats", var1, var2, var3, var4);
      this.F(true);
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = c ^ 37543297409481L;
      if (this.R != null) {
         float var6 = this.M();
         float var7 = this.E_float();
         long var10000 = 1295372147817266408L ^ var3;
         float var8 = this.x();
         int var9 = (int)(var8 / 12.0F);
         int var10 = 1;
         byte var11 = 25;

         for (PlayerStatsEntry var13 : PlayerListTracker.playerStats) {
            if (var10 > this.Q) {
               break;
            }

            if (PlayerListTracker.tabListNames.contains(var13.Q)) {
               var11 += 12;
               var10++;
            }
         }

         int var21 = var11 + 5;
         float var22 = mc.fontRendererObj.getStringWidth("PLAYERS");
         float var14 = mc.fontRendererObj.getStringWidth("30000");
         float var15 = this.c();
         float var16 = var15 + var22 + var14 * this.R.columnAmount + 15.0F;
         RenderUtil.drawRect(var6, var7, var6 + var16, var7 + 15.0F, new Color(1, 1, 1, 180).getRGB());
         RenderUtil.drawRect(var6, var7 + 15.0F, var6 + var16, var7 + var21, new Color(1, 1, 1, 120).getRGB());
         switch (this.R) {
            case BW:
               PlayerListTracker.playerStats.sort((var0, var1x) -> Float.compare(var1x.w, var0.w));
               break;
            case SW:
               PlayerListTracker.playerStats.sort((var0, var1x) -> Float.compare(var1x.sF, var0.sF));
               break;
            case UHC:
               PlayerListTracker.playerStats.sort((var0, var1x) -> Float.compare(var1x.O, var0.O));
               break;
            case MW:
               PlayerListTracker.playerStats.sort((var0, var1x) -> Float.compare(var1x.F, var0.F));
         }

         mc.fontRendererObj.drawStringWithShadow("PLAYERS", this.M() + 11.0F, this.E_float() + 4.0F, -1);
         byte var17 = 25;
         int var18 = 1;

         for (PlayerStatsEntry var20 : PlayerListTracker.playerStats) {
            if (var18 > this.Q) {
               break;
            }

            if (PlayerListTracker.tabListNames.contains(var20.Q)) {
               if (var20.m != null) {
                  mc.getTextureManager().bindTexture(var20.m.c());
                  Gui.drawScaledCustomSizeModalRect((int)(this.M() + var9 - 12.0F), (int)(this.E_float() + var17), 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
               }

               switch (this.R) {
                  case BW:
                     mc.fontRendererObj.drawStringWithShadow("WS", this.M() + var15 + var22, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(String.valueOf(var20.w), this.M() + var15 + var22, this.E_float() + var17, -1);
                     RenderUtil.drawArrow(this.M() + var15 + var22 + 14.0F, this.E_float() + 10.0F, 6.0, false, Color.WHITE.getRGB());
                     mc.fontRendererObj.drawStringWithShadow("KD", this.M() + var15 + var22 + var14, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(var20.Z), this.M() + var15 + var14 + var22, this.E_float() + var17, -1);
                     mc.fontRendererObj.drawStringWithShadow("FKD", this.M() + var15 + var22 + var14 * 2.0F, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(var20.sO), this.M() + var15 + var14 * 2.0F + var22, this.E_float() + var17, -1);
                     mc.fontRendererObj.drawStringWithShadow("STARS", this.M() + var15 + var22 + var14 * 3.0F, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(String.valueOf(var20.sV), this.M() + var15 + var14 * 3.0F + var22, this.E_float() + var17, -1);
                     break;
                  case SW:
                     mc.fontRendererObj.drawStringWithShadow("KD", this.M() + var15 + var22, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(MathUtil.ONE_DECIMAL.format(var20.sF), this.M() + var15 + var22, this.E_float() + var17, -1);
                     RenderUtil.drawArrow(this.M() + var15 + var22 + 14.0F, this.E_float() + 10.0F, 6.0, false, Color.WHITE.getRGB());
                     mc.fontRendererObj.drawStringWithShadow("WL", this.M() + var15 + var22 + var14, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(var20.M), this.M() + var15 + var14 + var22, this.E_float() + var17, -1);
                     break;
                  case UHC:
                     mc.fontRendererObj.drawStringWithShadow("SKD", this.M() + var15 + var22, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(MathUtil.ONE_DECIMAL.format(var20.n), this.M() + var15 + var22, this.E_float() + var17, -1);
                     mc.fontRendererObj.drawStringWithShadow("TKD", this.M() + var15 + var22 + var14, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(var20.e), this.M() + var15 + var14 + var22, this.E_float() + var17, -1);
                     mc.fontRendererObj.drawStringWithShadow("STARS", this.M() + var15 + var22 + var14 * 2.0F, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(String.valueOf(var20.V), this.M() + var15 + var14 * 2.0F + var22, this.E_float() + var17, -1);
                     break;
                  case MW:
                     mc.fontRendererObj.drawStringWithShadow("KD", this.M() + var15 + var22, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(MathUtil.ONE_DECIMAL.format(var20.F), this.M() + var15 + var22, this.E_float() + var17, -1);
                     RenderUtil.drawArrow(this.M() + var15 + var22 + 14.0F, this.E_float() + 10.0F, 6.0, false, Color.WHITE.getRGB());
                     mc.fontRendererObj.drawStringWithShadow("FKD", this.M() + var15 + var22 + var14, this.E_float() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(var20.J), this.M() + var15 + var14 + var22, this.E_float() + var17, -1);
               }

               mc.fontRendererObj
                  .drawStringWithShadow(var20.E != null ? EnumChatFormatting.GOLD + var20.E : var20.Q, this.M() + var9, this.E_float() + var17, -1);
               var17 += 12;
               var18++;
            }
         }

         this.B(var21);
      }
   }

   @Override
   public void l(int var1) {
   }

   private float c() {
      float var4 = 30.0F;

      for (PlayerStatsEntry var6 : PlayerListTracker.playerStats) {
         String var7 = var6.Q;
         float var8 = mc.fontRendererObj.getStringWidth(var7);
         if (var8 > var4) {
            var4 = var8;
         }
      }

      return var4;
   }

   public void I(int var1) {
      this.Q = var1;
   }

   public void J(AutoHypixelAutoCheckerGameMode var1) {
      this.R = var1;
   }
}
