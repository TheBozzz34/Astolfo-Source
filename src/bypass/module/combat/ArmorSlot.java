package bypass.module.combat;

public enum ArmorSlot {
   HELMET(5, 0),
   CHEST_PLATE(6, 1),
   LEGGINGS(7, 2),
   BOOTS(8, 3);

   public int slot;
   private int K;
   private static final ArmorSlot[] l = new ArmorSlot[]{HELMET, CHEST_PLATE, LEGGINGS, BOOTS};

   ArmorSlot(int j, int k) {
      this.slot = j;
      this.K = k;
   }

   public static int z(ArmorSlot armorSlot) {
      return armorSlot.K;
   }
}
