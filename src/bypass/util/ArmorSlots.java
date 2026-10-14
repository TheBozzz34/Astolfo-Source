package bypass.util;

public final class ArmorSlots {
   private static final long a = 80938382306195L;

   public static ArmorSlot byArmorType(int i) {
      for (ArmorSlot armorSlot : ArmorSlot.values()) {
         if (armorSlot.armorType == i) {
            return armorSlot;
         }
      }

      return null;
   }
}
