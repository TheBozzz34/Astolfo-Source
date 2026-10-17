package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

public class AutoArmorCheck extends MovementCheck {
   private double lastX;
   private double lastZ;
   private float lastArmorValue;
   private Item lastHeldItem;

   public AutoArmorCheck() {
      super("AutoArmor");
   }

   @Override
   public int handleMovement(double d, double e, double d2, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      if (entityPlayer.getTotalArmorValue() > this.lastArmorValue && (this.lastHeldItem == null || !(this.lastHeldItem instanceof ItemArmor))) {
         double d3 = Math.hypot(d - this.lastX, d2 - this.lastZ);
         if (d3 >= 0.5 && d3 < 3.0) {
            this.flag(entityPlayer.getName());
         }
      }

      this.lastArmorValue = entityPlayer.getTotalArmorValue();
      if (this.lastArmorValue == 0.0F) {
         this.lastArmorValue = 9999.0F;
      }

      if (entityPlayer.getHeldItem() != null) {
         this.lastHeldItem = entityPlayer.getHeldItem().getItem();
      } else {
         this.lastHeldItem = null;
      }

      this.lastX = d;
      this.lastZ = d2;
      return this.violations;
   }

   @Override
   public int getMaxViolations() {
      return 3;
   }
}
