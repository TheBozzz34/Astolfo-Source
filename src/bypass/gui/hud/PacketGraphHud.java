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

   public PacketGraphHud(float f, float g, int i, int j) {
      super("Graph", f, g, i, j);
      this.F(true);
   }

   @Override
   public void render(int i, int j) {
      try {
         int k = this.G ? this.S() : this.t();
         int l = this.G ? this.b() : this.y();
         double d = (this.x() - 2.0) / (this.q.size() - 1);
         double e = (this.Y() - 2.0) / (k - l);
         ArrayList arrayList = new ArrayList();
         int m = 0;
         int n = 0;

         for (PacketSnapshot packetSnapshot : this.q) {
            int i2 = this.G ? packetSnapshot.incoming.size() : packetSnapshot.outgoing.size();
            int i3 = (int)(m * d);
            int i4 = (int)((k - i2) * e);
            arrayList.add(new Vector2f(i3, i4));
            m++;
            n += i2;
         }

         int i5 = n / this.q.size();
         GL11.glPushMatrix();
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         mc.fontRendererObj.drawStringWithShadow(this.G ? "Incoming packets" : "Outgoing packets", this.M() * 2.0F, (this.E_float() - 7.0F) * 2.0F, -1);
         String string = Math.round((float)n / this.q.size() * 20.0F) + " packets/sec";
         mc.fontRendererObj
            .drawStringWithShadow(string, (this.M() + this.x() - mc.fontRendererObj.getStringWidth(string) / 2.0F) * 2.0F, (this.E_float() - 7.0F) * 2.0F, -1);
         GL11.glPopMatrix();
         int i6 = ((ColorSetting)HUD.graphs.getSetting("Background")).getValue().getRGB();
         int i7 = ((ColorSetting)HUD.graphs.getSetting("Line")).getValue().getRGB();
         RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), i6);
         byte b2 = 3;

         for (int i8 = 0; i8 < b2 + 1; i8++) {
            int i9 = (int)this.M();
            int i10 = i9 + this.x();
            int i11 = (int)(this.E_float() + this.Y() - i8 * this.Y() / b2);
            if (this.q.size() > 0) {
               String string2 = Math.round((int)((l + (k - l) * (i8 * 1.0 / b2)) * 100.0) / 100.0) + "";
               int i12 = mc.fontRendererObj.getStringWidth(string2) / 2;
               GL11.glPushMatrix();
               GL11.glScalef(0.5F, 0.5F, 0.5F);
               if (i8 == 3) {
                  i11 = (int)this.E_float() + 2;
               }

               if (i8 == 0) {
                  i11 = (int)this.E_float() + this.Y() - 3;
               }

               mc.fontRendererObj.drawStringWithShadow(string2, (i9 - i12 - 3) * 2, (i11 + mc.fontRendererObj.FONT_HEIGHT / 2.0F - 6.0F) * 2.0F, -1);
               GL11.glPopMatrix();
            }

            RenderUtil.drawLine(1.0F, i9, i11, i10, i11, i6);
         }

         for (int i13 = 0; i13 < this.q.size(); i13 += 2) {
            if (this.q.size() > 1) {
               int i14 = (int)(this.M() + i13 * this.x() / (this.q.size() - 1));
               int i15 = (int)this.E_float();
               int i16 = i15 + this.Y();
               RenderUtil.drawLine(1.0F, i14, i15, i14, i16, i6);
            }
         }

         for (int i17 = 0; i17 < arrayList.size() - 1; i17++) {
            float f = ((Vector2f)arrayList.get(i17)).x;
            float g = ((Vector2f)arrayList.get(i17)).y;
            float f2 = ((Vector2f)arrayList.get(i17 + 1)).x;
            float f3 = ((Vector2f)arrayList.get(i17 + 1)).y;
            RenderUtil.drawLine(1.0F, this.M() + f, this.E_float() + g, this.M() + f2, this.E_float() + f3, i7);
         }

         if (this.h) {
            int i18 = (int)(this.E_float() + (int)((k - i5) * e));
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            mc.fontRendererObj.drawStringWithShadow("avg: " + i5, (this.M() + this.x() + 3.0F) * 2.0F, (i18 - 2) * 2, -1);
            GL11.glPopMatrix();
            RenderUtil.drawLine(1.0F, this.M(), i18, this.M() + this.x(), i18, Color.GREEN.getRGB());
         }
      } catch (ArithmeticException arithmeticException) {
      }
   }

   private int t() {
      int i = 0;

      for (PacketSnapshot packetSnapshot : this.q) {
         if (i < packetSnapshot.outgoing.size()) {
            i = packetSnapshot.outgoing.size();
         }
      }

      return i;
   }

   private int y() {
      int i = 999999;

      for (PacketSnapshot packetSnapshot : this.q) {
         if (i > packetSnapshot.outgoing.size()) {
            i = packetSnapshot.outgoing.size();
         }
      }

      return i;
   }

   private int S() {
      int i = 0;

      for (PacketSnapshot packetSnapshot : this.q) {
         if (i < packetSnapshot.incoming.size()) {
            i = packetSnapshot.incoming.size();
         }
      }

      return i;
   }

   private int b() {
      int i = 999999;

      for (PacketSnapshot packetSnapshot : this.q) {
         if (i > packetSnapshot.incoming.size()) {
            i = packetSnapshot.incoming.size();
         }
      }

      return i;
   }

   @Override
   public void l(int i) {
   }

   public void N(boolean bl) {
      this.h = bl;
   }

   public void r(boolean bl) {
      this.G = bl;
   }

   public void K(ArrayBlockingQueue<PacketSnapshot> arrayBlockingQueue) {
      this.q = arrayBlockingQueue;
   }
}
