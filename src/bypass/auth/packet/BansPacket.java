package bypass.auth.packet;

public class BansPacket {
   public BansPacketType type;
   public int mode;
   public String bans;

   public BansPacket(BansPacketType bansPacketType, int i, String string) {
      this.type = bansPacketType;
      this.mode = i;
      this.bans = string;
   }
}
