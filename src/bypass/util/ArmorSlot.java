package bypass.util;

public enum ArmorSlot {
   HELMET(5, 0),
   CHEST_PLATE(6, 1),
   LEGGINGS(7, 2),
   BOOTS(8, 3);

   public int slot;
   public int armorType;
   private static final ArmorSlot[] VALUES = new ArmorSlot[]{HELMET, CHEST_PLATE, LEGGINGS, BOOTS};

   ArmorSlot(int j, int k) {
      this.slot = j;
      this.armorType = k;
   }
}
