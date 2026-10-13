package bypass.auth.packet;

public class LoginPacket {
   public String version;
   public String username;
   public String password;
   public String hwid;
   public String token;
   private static final long a = 22694108273668L;

   public LoginPacket(String var1, String var2, String var3, String var4, String var5) {
      this.username = var2;
      this.password = var3;
      this.version = var1;
      this.hwid = var4;
      this.token = var5;
   }
}
