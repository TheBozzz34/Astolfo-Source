package bypass.module.player;

public enum InventoryManagerPatternMode {
   NONE,
   SEQUENTIAL,
   RANDOM,
   REVERSE;

   private static final InventoryManagerPatternMode[] O = new InventoryManagerPatternMode[]{NONE, SEQUENTIAL, RANDOM, REVERSE};
}
