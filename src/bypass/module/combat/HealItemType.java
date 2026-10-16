package bypass.module.combat;

public enum HealItemType {
   POTION,
   USABLE,
   HOLD,
   FAKE;

   private static final HealItemType[] VALUES = new HealItemType[]{POTION, USABLE, HOLD, FAKE};
}
