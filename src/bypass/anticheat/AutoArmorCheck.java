package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

public class AutoArmorCheck extends MovementCheck {
   private double X;
   private double G;
   private float w;
   private Item g;

   public AutoArmorCheck() {
      super("AutoArmor");
   }

   @Override
   public int D(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      if (entityPlayer.getTotalArmorValue() > this.w && (this.g == null || !(this.g instanceof ItemArmor))) {
         double e = Math.hypot(d - this.X, d3 - this.G);
         if (e >= 0.5 && e < 3.0) {
            this.x(entityPlayer.getName());
         }
      }

      this.w = entityPlayer.getTotalArmorValue();
      if (this.w == 0.0F) {
         this.w = 9999.0F;
      }

      if (entityPlayer.getHeldItem() != null) {
         this.g = entityPlayer.getHeldItem().getItem();
      } else {
         this.g = null;
      }

      this.X = d;
      this.G = d3;
      return this.T;
   }

   @Override
   public int p() {
      return 3;
   }
}
