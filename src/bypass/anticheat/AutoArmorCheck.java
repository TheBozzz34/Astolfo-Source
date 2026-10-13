package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

public class AutoArmorCheck extends MovementCheck {
   private double X;
   private double G;
   private float w;
   private Item g;
   private static final long b = 30966799658483L;

   public AutoArmorCheck() {
      super("AutoArmor");
   }

   @Override
   public int D(double var1, double var3, double var5, float var7, float var8, boolean var9, EntityPlayer var10) {
      if (var10.getTotalArmorValue() > this.w && (this.g == null || !(this.g instanceof ItemArmor))) {
         double var14 = Math.hypot(var1 - this.X, var5 - this.G);
         if (var14 >= 0.5 && var14 < 3.0) {
            this.x(var10.getName());
         }
      }

      this.w = var10.getTotalArmorValue();
      if (this.w == 0.0F) {
         this.w = 9999.0F;
      }

      if (var10.getHeldItem() != null) {
         this.g = var10.getHeldItem().getItem();
      } else {
         this.g = null;
      }

      this.X = var1;
      this.G = var5;
      return this.T;
   }

   @Override
   public int p() {
      return 3;
   }
}
