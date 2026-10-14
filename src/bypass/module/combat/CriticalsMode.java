package bypass.module.combat;

public enum CriticalsMode {
   PACKET("Packet"),
   EDIT("Edit"),
   INCREMENTAL("Incremental"),
   WATCHDOG("Watchdog");

   private String e;
   private static final CriticalsMode[] y = new CriticalsMode[]{PACKET, EDIT, INCREMENTAL, WATCHDOG};

   CriticalsMode(String string2) {
      this.e = string2;
   }

   public static String p(CriticalsMode criticalsMode) {
      return criticalsMode.e;
   }
}
