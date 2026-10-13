package bypass.auth.packet;

public class BansPacket {
   public BansPacketType type;
   public int mode;
   public String bans;

   public BansPacket(BansPacketType var1, int var2, String var3) {
      this.type = var1;
      this.mode = var2;
      this.bans = var3;
   }
}
