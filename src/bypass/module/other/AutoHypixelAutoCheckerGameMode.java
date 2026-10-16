package bypass.module.other;

public enum AutoHypixelAutoCheckerGameMode {
   SW("sw", 2),
   BW("bw", 4),
   UHC("uhc", 3),
   MW("mw", 2);

   public int columnAmount = 1;
   public String name = "";
   private static final AutoHypixelAutoCheckerGameMode[] VALUES = new AutoHypixelAutoCheckerGameMode[]{SW, BW, UHC, MW};

   AutoHypixelAutoCheckerGameMode(String string2, int j) {
      this.name = string2;
      this.columnAmount = j;
   }
}
