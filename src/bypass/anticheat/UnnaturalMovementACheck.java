package bypass.anticheat;

import bypass.ClientChat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class UnnaturalMovementACheck extends MovementCheck {
   private final List<Double> distanceHistory = new ArrayList<>(25);
   private double lastX;
   private double lastZ;
   private double lastDistance;

   public UnnaturalMovementACheck() {
      super("Unnatural Movement A");
   }

   @Override
   public int handleMovement(double d, double e, double d2, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      double d3 = Math.hypot(d - this.lastX, d2 - this.lastZ);
      double d4 = Math.abs(d3) - Math.abs(this.lastDistance);
      if (d4 != 0.0 && d4 > Math.abs(this.lastDistance) * 2.0) {
         ClientChat.CHAT.print("lol detect");
         this.violations++;
      }

      this.lastX = d;
      this.lastZ = d2;
      this.lastDistance = d3;
      return this.violations;
   }

   @Override
   public int getMaxViolations() {
      return 3;
   }
}
