package bypass.module.other;

public enum InfiniteReachMode {
   LINEAR("Linear"),
   PATH_FIND("Path Find");

   private String m;
   private static final InfiniteReachMode[] s = new InfiniteReachMode[]{LINEAR, PATH_FIND};

   InfiniteReachMode(String var3) {
      this.m = var3;
   }
}
