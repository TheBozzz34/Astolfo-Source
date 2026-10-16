package bypass.event;

public enum EventPriority {
   HIGHEST(4),
   HIGH(3),
   NORMAL(2),
   LOW(1),
   LOWEST(0);

   public int level;

   EventPriority(int j) {
      this.level = j;
   }

   public int getLevel() {
      return this.level;
   }
}
