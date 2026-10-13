package bypass.setting.attribute;

public enum AttributePriority {
   HIGHEST(0),
   HIGH(1),
   NORMAL(2),
   LOW(3),
   LOWEST(4);

   public int level;

   AttributePriority(int var3) {
      this.level = var3;
   }

   public int h() {
      return this.level;
   }
}
