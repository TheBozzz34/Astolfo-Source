package bypass.module.combat;

public enum ArmorSlot {
   HELMET(5, 0),
   CHEST_PLATE(6, 1),
   LEGGINGS(7, 2),
   BOOTS(8, 3);

   public int slot;
   private int K;
   private static final ArmorSlot[] l = new ArmorSlot[]{HELMET, CHEST_PLATE, LEGGINGS, BOOTS};

   ArmorSlot(int var3, int var4) {
      this.slot = var3;
      this.K = var4;
   }

   public static int z(ArmorSlot var0) {
      return var0.K;
   }
}
