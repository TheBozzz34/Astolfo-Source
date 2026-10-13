package bypass.module.combat;

import net.minecraft.item.ItemStack;

public class HealItem {
   private final ItemStack stack;
   private final int slot;
   private final HealItemType type;

   public HealItem(HealItemType var1, ItemStack var2, int var3) {
      this.type = var1;
      this.stack = var2;
      this.slot = var3;
   }

   public int getSlot() {
      return this.slot;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public HealItemType getType() {
      return this.type;
   }

   public static HealItemType typeOf(HealItem var0) {
      return var0.type;
   }

   public static ItemStack stackOf(HealItem var0) {
      return var0.stack;
   }

   public static int slotOf(HealItem var0) {
      return var0.slot;
   }
}
