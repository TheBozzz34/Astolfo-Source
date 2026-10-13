package bypass.module.combat;

public enum CriticalsMode {
   PACKET("Packet"),
   EDIT("Edit"),
   INCREMENTAL("Incremental"),
   WATCHDOG("Watchdog");

   private String e;
   private static final CriticalsMode[] y = new CriticalsMode[]{PACKET, EDIT, INCREMENTAL, WATCHDOG};

   CriticalsMode(String var3) {
      this.e = var3;
   }

   public static String p(CriticalsMode var0) {
      return var0.e;
   }
}
