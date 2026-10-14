package bypass.anticheat;

import bypass.ClientChat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class UnnaturalMovementACheck extends MovementCheck {
   private final List<Double> B = new ArrayList<>(25);
   private double q;
   private double k;
   private double l;

   public UnnaturalMovementACheck() {
      super("Unnatural Movement A");
   }

   @Override
   public int D(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      double e = Math.hypot(d - this.q, d3 - this.k);
      double d4 = Math.abs(e) - Math.abs(this.l);
      if (d4 != 0.0 && d4 > Math.abs(this.l) * 2.0) {
         ClientChat.CHAT.I("lol detect");
         this.T++;
      }

      this.q = d;
      this.k = d3;
      this.l = e;
      return this.T;
   }

   @Override
   public int p() {
      return 3;
   }
}
