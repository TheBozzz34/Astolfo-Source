package bypass.auth.packet;

public enum PacketId {
   PING((short)623),
   IRC_MESSAGE((short)732),
   SERVER_COMMAND((short)831),
   GYBF((short)144),
   DISABLER_SCRIPT((short)373),
   FEWUBY((short)645),
   USER_LIST((short)723),
   NICK((short)864),
   VFBNHU((short)262),
   BVTER((short)962),
   LOGIN_RESPONSE((short)672),
   BANS((short)892);

   private final short id;

   PacketId(short s) {
      this.id = s;
   }

   public short getId() {
      return this.id;
   }
}
