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
   private ArrayBlockingQueue<PacketSnapshot> snapshots = new ArrayBlockingQueue<>(50);
   private boolean showAverage;
   private boolean showIncoming;

   public PacketGraphHud(float f, float g, int i, int j) {
      super("Graph", f, g, i, j);
      this.setDraggable(true);
   }

   @Override
   public void render(int i, int j) {
      try {
         int k = this.showIncoming ? this.getMaxIncoming() : this.getMaxOutgoing();
         int l = this.showIncoming ? this.getMinIncoming() : this.getMinOutgoing();
         double d = (this.getWidth() - 2.0) / (this.snapshots.size() - 1);
         double e = (this.getHeight() - 2.0) / (k - l);
         ArrayList arrayList = new ArrayList();
         int m = 0;
         int n = 0;

         for (PacketSnapshot packetSnapshot : this.snapshots) {
            int i2 = this.showIncoming ? packetSnapshot.incoming.size() : packetSnapshot.outgoing.size();
            int i3 = (int)(m * d);
            int i4 = (int)((k - i2) * e);
            arrayList.add(new Vector2f(i3, i4));
            m++;
            n += i2;
         }

         int i5 = n / this.snapshots.size();
         GL11.glPushMatrix();
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         mc.fontRendererObj
            .drawStringWithShadow(this.showIncoming ? "Incoming packets" : "Outgoing packets", this.getX() * 2.0F, (this.getYPos() - 7.0F) * 2.0F, -1);
         String string = Math.round((float)n / this.snapshots.size() * 20.0F) + " packets/sec";
         mc.fontRendererObj
            .drawStringWithShadow(
               string, (this.getX() + this.getWidth() - mc.fontRendererObj.getStringWidth(string) / 2.0F) * 2.0F, (this.getYPos() - 7.0F) * 2.0F, -1
            );
         GL11.glPopMatrix();
         int i6 = ((ColorSetting)HUD.graphs.getSetting("Background")).getValue().getRGB();
         int i7 = ((ColorSetting)HUD.graphs.getSetting("Line")).getValue().getRGB();
         RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), i6);
         byte b = 3;

         for (int i8 = 0; i8 < b + 1; i8++) {
            int i9 = (int)this.getX();
            int i10 = i9 + this.getWidth();
            int i11 = (int)(this.getYPos() + this.getHeight() - i8 * this.getHeight() / b);
            if (this.snapshots.size() > 0) {
               String string2 = Math.round((int)((l + (k - l) * (i8 * 1.0 / b)) * 100.0) / 100.0) + "";
               int i12 = mc.fontRendererObj.getStringWidth(string2) / 2;
               GL11.glPushMatrix();
               GL11.glScalef(0.5F, 0.5F, 0.5F);
               if (i8 == 3) {
                  i11 = (int)this.getYPos() + 2;
               }

               if (i8 == 0) {
                  i11 = (int)this.getYPos() + this.getHeight() - 3;
               }

               mc.fontRendererObj.drawStringWithShadow(string2, (i9 - i12 - 3) * 2, (i11 + mc.fontRendererObj.FONT_HEIGHT / 2.0F - 6.0F) * 2.0F, -1);
               GL11.glPopMatrix();
            }

            RenderUtil.drawLine(1.0F, i9, i11, i10, i11, i6);
         }

         for (int i13 = 0; i13 < this.snapshots.size(); i13 += 2) {
            if (this.snapshots.size() > 1) {
               int i14 = (int)(this.getX() + i13 * this.getWidth() / (this.snapshots.size() - 1));
               int i15 = (int)this.getYPos();
               int i16 = i15 + this.getHeight();
               RenderUtil.drawLine(1.0F, i14, i15, i14, i16, i6);
            }
         }

         for (int i17 = 0; i17 < arrayList.size() - 1; i17++) {
            float f = ((Vector2f)arrayList.get(i17)).x;
            float g = ((Vector2f)arrayList.get(i17)).y;
            float h = ((Vector2f)arrayList.get(i17 + 1)).x;
            float f2 = ((Vector2f)arrayList.get(i17 + 1)).y;
            RenderUtil.drawLine(1.0F, this.getX() + f, this.getYPos() + g, this.getX() + h, this.getYPos() + f2, i7);
         }

         if (this.showAverage) {
            int i18 = (int)(this.getYPos() + (int)((k - i5) * e));
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            mc.fontRendererObj.drawStringWithShadow("avg: " + i5, (this.getX() + this.getWidth() + 3.0F) * 2.0F, (i18 - 2) * 2, -1);
            GL11.glPopMatrix();
            RenderUtil.drawLine(1.0F, this.getX(), i18, this.getX() + this.getWidth(), i18, Color.GREEN.getRGB());
         }
      } catch (ArithmeticException arithmeticException) {
      }
   }

   private int getMaxOutgoing() {
      int i = 0;

      for (PacketSnapshot packetSnapshot : this.snapshots) {
         if (i < packetSnapshot.outgoing.size()) {
            i = packetSnapshot.outgoing.size();
         }
      }

      return i;
   }

   private int getMinOutgoing() {
      int i = 999999;

      for (PacketSnapshot packetSnapshot : this.snapshots) {
         if (i > packetSnapshot.outgoing.size()) {
            i = packetSnapshot.outgoing.size();
         }
      }

      return i;
   }

   private int getMaxIncoming() {
      int i = 0;

      for (PacketSnapshot packetSnapshot : this.snapshots) {
         if (i < packetSnapshot.incoming.size()) {
            i = packetSnapshot.incoming.size();
         }
      }

      return i;
   }

   private int getMinIncoming() {
      int i = 999999;

      for (PacketSnapshot packetSnapshot : this.snapshots) {
         if (i > packetSnapshot.incoming.size()) {
            i = packetSnapshot.incoming.size();
         }
      }

      return i;
   }

   @Override
   public void onClick(int i) {
   }

   public void setShowAverage(boolean bl) {
      this.showAverage = bl;
   }

   public void setShowIncoming(boolean bl) {
      this.showIncoming = bl;
   }

   public void setSnapshots(ArrayBlockingQueue<PacketSnapshot> arrayBlockingQueue) {
      this.snapshots = arrayBlockingQueue;
   }
}
