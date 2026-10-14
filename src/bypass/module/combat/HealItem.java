package bypass.module.combat;

import net.minecraft.item.ItemStack;

public class HealItem {
   private final ItemStack stack;
   private final int slot;
   private final HealItemType type;

   public HealItem(HealItemType healItemType, ItemStack itemStack, int i) {
      this.type = healItemType;
      this.stack = itemStack;
      this.slot = i;
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

   public static HealItemType typeOf(HealItem healItem) {
      return healItem.type;
   }

   public static ItemStack stackOf(HealItem healItem) {
      return healItem.stack;
   }

   public static int slotOf(HealItem healItem) {
      return healItem.slot;
   }
}
