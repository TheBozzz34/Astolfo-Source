package bypass.module.combat;

public enum CriticalsMode {
   PACKET("Packet"),
   EDIT("Edit"),
   INCREMENTAL("Incremental"),
   WATCHDOG("Watchdog");

   private String displayName;
   private static final CriticalsMode[] VALUES = new CriticalsMode[]{PACKET, EDIT, INCREMENTAL, WATCHDOG};

   CriticalsMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(CriticalsMode criticalsMode) {
      return criticalsMode.displayName;
   }
}
