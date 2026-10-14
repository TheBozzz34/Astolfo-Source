package bypass.util;

public final class ArmorSlots {
   public static ArmorSlot byArmorType(int i) {
      for (ArmorSlot armorSlot : ArmorSlot.values()) {
         if (armorSlot.armorType == i) {
            return armorSlot;
         }
      }

      return null;
   }
}
