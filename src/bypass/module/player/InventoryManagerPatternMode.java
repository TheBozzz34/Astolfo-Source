package bypass.module.player;

public enum InventoryManagerPatternMode {
   NONE,
   SEQUENTIAL,
   RANDOM,
   REVERSE;

   private static final InventoryManagerPatternMode[] VALUES = new InventoryManagerPatternMode[]{NONE, SEQUENTIAL, RANDOM, REVERSE};
}
