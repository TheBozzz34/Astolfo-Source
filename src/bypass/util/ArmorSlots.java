package bypass.util;

public final class ArmorSlots {
   private static final long a = 80938382306195L;

   public static ArmorSlot byArmorType(int var0) {
      for (ArmorSlot var7 : ArmorSlot.values()) {
         if (var7.armorType == var0) {
            return var7;
         }
      }

      return null;
   }
}
