package bypass.anticheat;

import bypass.ClientChat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class UnnaturalMovementACheck extends MovementCheck {
   private final List<Double> B;
   private double q;
   private double k;
   private double l;
   private static final long b = 63936516203597L;
   private static final long g = 647984447546720281L;

   public UnnaturalMovementACheck() {
      super("Unnatural Movement A");
      this.B = new ArrayList<>((int)g);
   }

   @Override
   public int D(double var1, double var3, double var5, float var7, float var8, boolean var9, EntityPlayer var10) {
      double var14 = Math.hypot(var1 - this.q, var5 - this.k);
      double var16 = Math.abs(var14) - Math.abs(this.l);
      if (var16 != 0.0 && var16 > Math.abs(this.l) * 2.0) {
         ClientChat.CHAT.I("lol detect");
         this.T++;
      }

      this.q = var1;
      this.k = var5;
      this.l = var14;
      return this.T;
   }

   @Override
   public int p() {
      return 3;
   }
}
