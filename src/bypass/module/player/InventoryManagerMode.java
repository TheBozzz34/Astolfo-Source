package bypass.module.player;

public enum InventoryManagerMode {
   NEVER,
   INVENTORY,
   INVENTORY_LSHIFT,
   NOT_MOVING,
   ALWAYS;

   private static final InventoryManagerMode[] I = new InventoryManagerMode[]{NEVER, INVENTORY, INVENTORY_LSHIFT, NOT_MOVING, ALWAYS};
}
