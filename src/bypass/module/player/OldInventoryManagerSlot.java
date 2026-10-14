package bypass.module.player;

public enum OldInventoryManagerSlot {
   PICKAXE("Pickaxe-Slot"),
   SHOVEL("Shovel-Slot"),
   AXE("Axe-Slot"),
   SWORD("Sword-Slot"),
   GAPPLE("Gapple-Slot"),
   HEAD("Head-Slot"),
   BOW("Bow-Slot"),
   FISHING_ROD("Fishing-Rod-Slot"),
   PEARL("Pearl-Slot");

   private final String settingName;
   private static final OldInventoryManagerSlot[] B = new OldInventoryManagerSlot[]{PICKAXE, SHOVEL, AXE, SWORD, GAPPLE, HEAD, BOW, FISHING_ROD, PEARL};

   OldInventoryManagerSlot(String string2) {
      this.settingName = string2;
   }

   public String getSettingName() {
      return this.settingName;
   }
}
