package bypass.gui.hud;

import bypass.gui.component.Component;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import bypass.setting.ColorSetting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import javax.vecmath.Vector2f;
import org.lwjgl.opengl.GL11;

public class PacketGraphHud extends Component {
   private ArrayBlockingQueue<PacketSnapshot> q = new ArrayBlockingQueue<>(50);
   private boolean h;
   private boolean G;
   private static final long c = 37151489776439L;

   public PacketGraphHud(float var1, float var2, int var3, int var4) {
      super("Graph", var1, var2, var3, var4);
      this.F(true);
   }

   @Override
   public void render(int var1, int var2) {
      try {
         int var6 = this.G ? this.S() : this.t();
         int var7 = this.G ? this.b() : this.y();
         double var8 = (this.x() - 2.0) / (this.q.size() - 1);
         double var10 = (this.Y() - 2.0) / (var6 - var7);
         ArrayList var12 = new ArrayList();
         int var13 = 0;
         int var14 = 0;

         for (PacketSnapshot var16 : this.q) {
            int var17 = this.G ? var16.incoming.size() : var16.outgoing.size();
            int var18 = (int)(var13 * var8);
            int var19 = (int)((var6 - var17) * var10);
            var12.add(new Vector2f(var18, var19));
            var13++;
            var14 += var17;
         }

         int var27 = var14 / this.q.size();
         GL11.glPushMatrix();
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         mc.fontRendererObj.drawStringWithShadow(this.G ? "Incoming packets" : "Outgoing packets", this.M() * 2.0F, (this.E_float() - 7.0F) * 2.0F, -1);
         String var28 = Math.round((float)var14 / this.q.size() * 20.0F) + " packets/sec";
         mc.fontRendererObj
            .drawStringWithShadow(var28, (this.M() + this.x() - mc.fontRendererObj.getStringWidth(var28) / 2.0F) * 2.0F, (this.E_float() - 7.0F) * 2.0F, -1);
         GL11.glPopMatrix();
         int var29 = ((ColorSetting)HUD.graphs.getSetting("Background")).getValue().getRGB();
         int var30 = ((ColorSetting)HUD.graphs.getSetting("Line")).getValue().getRGB();
         RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), var29);
         byte var31 = 3;

         for (int var20 = 0; var20 < var31 + 1; var20++) {
            int var21 = (int)this.M();
            int var22 = var21 + this.x();
            int var23 = (int)(this.E_float() + this.Y() - var20 * this.Y() / var31);
            if (this.q.size() > 0) {
               String var24 = Math.round((int)((var7 + (var6 - var7) * (var20 * 1.0 / var31)) * 100.0) / 100.0) + "";
               int var25 = mc.fontRendererObj.getStringWidth(var24) / 2;
               GL11.glPushMatrix();
               GL11.glScalef(0.5F, 0.5F, 0.5F);
               if (var20 == 3) {
                  var23 = (int)this.E_float() + 2;
               }

               if (var20 == 0) {
                  var23 = (int)this.E_float() + this.Y() - 3;
               }

               mc.fontRendererObj.drawStringWithShadow(var24, (var21 - var25 - 3) * 2, (var23 + mc.fontRendererObj.FONT_HEIGHT / 2.0F - 6.0F) * 2.0F, -1);
               GL11.glPopMatrix();
            }

            RenderUtil.drawLine(1.0F, var21, var23, var22, var23, var29);
         }

         for (int var32 = 0; var32 < this.q.size(); var32 += 2) {
            if (this.q.size() > 1) {
               int var35 = (int)(this.M() + var32 * this.x() / (this.q.size() - 1));
               int var37 = (int)this.E_float();
               int var39 = var37 + this.Y();
               RenderUtil.drawLine(1.0F, var35, var37, var35, var39, var29);
            }
         }

         for (int var33 = 0; var33 < var12.size() - 1; var33++) {
            float var36 = ((Vector2f)var12.get(var33)).x;
            float var38 = ((Vector2f)var12.get(var33)).y;
            float var40 = ((Vector2f)var12.get(var33 + 1)).x;
            float var41 = ((Vector2f)var12.get(var33 + 1)).y;
            RenderUtil.drawLine(1.0F, this.M() + var36, this.E_float() + var38, this.M() + var40, this.E_float() + var41, var30);
         }

         if (this.h) {
            int var34 = (int)(this.E_float() + (int)((var6 - var27) * var10));
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            mc.fontRendererObj.drawStringWithShadow("avg: " + var27, (this.M() + this.x() + 3.0F) * 2.0F, (var34 - 2) * 2, -1);
            GL11.glPopMatrix();
            RenderUtil.drawLine(1.0F, this.M(), var34, this.M() + this.x(), var34, Color.GREEN.getRGB());
         }
      } catch (ArithmeticException var26) {
      }
   }

   private int t() {
      int var4 = 0;

      for (PacketSnapshot var6 : this.q) {
         if (var4 < var6.outgoing.size()) {
            var4 = var6.outgoing.size();
         }
      }

      return var4;
   }

   private int y() {
      int var4 = 999999;

      for (PacketSnapshot var6 : this.q) {
         if (var4 > var6.outgoing.size()) {
            var4 = var6.outgoing.size();
         }
      }

      return var4;
   }

   private int S() {
      int var4 = 0;

      for (PacketSnapshot var6 : this.q) {
         if (var4 < var6.incoming.size()) {
            var4 = var6.incoming.size();
         }
      }

      return var4;
   }

   private int b() {
      int var4 = 999999;

      for (PacketSnapshot var6 : this.q) {
         if (var4 > var6.incoming.size()) {
            var4 = var6.incoming.size();
         }
      }

      return var4;
   }

   @Override
   public void l(int var1) {
   }

   public void N(boolean var1) {
      this.h = var1;
   }

   public void r(boolean var1) {
      this.G = var1;
   }

   public void K(ArrayBlockingQueue<PacketSnapshot> var1) {
      this.q = var1;
   }
}
