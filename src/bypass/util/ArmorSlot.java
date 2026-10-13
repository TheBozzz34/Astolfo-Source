package bypass.util;

public enum ArmorSlot {
   HELMET(5, 0),
   CHEST_PLATE(6, 1),
   LEGGINGS(7, 2),
   BOOTS(8, 3);

   public int slot;
   public int armorType;
   private static final ArmorSlot[] P = new ArmorSlot[]{HELMET, CHEST_PLATE, LEGGINGS, BOOTS};

   ArmorSlot(int var3, int var4) {
      this.slot = var3;
      this.armorType = var4;
   }
}
