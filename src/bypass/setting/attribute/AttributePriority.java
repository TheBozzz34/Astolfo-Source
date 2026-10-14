package bypass.setting.attribute;

public enum AttributePriority {
   HIGHEST(0),
   HIGH(1),
   NORMAL(2),
   LOW(3),
   LOWEST(4);

   public int level;

   AttributePriority(int k) {
      this.level = k;
   }

   public int h() {
      return this.level;
   }
}
