package bypass.auth.packet;

import bypass.Astolfo;

public class UserListPacket {
   public UserListType type;
   public String users;
   public String s;
   public boolean H;
   private static final long a = 47742426424070L;

   public UserListPacket(UserListType var1, String var2, String var3) {
      this.type = var1;
      this.users = var2;
      this.s = var3;
      this.H = Astolfo.INSTANCE.getSenseSetting().getValue();
   }

   public UserListPacket(UserListType var1, String var2, String var3, boolean var4) {
      this.type = var1;
      this.users = var2;
      this.s = var3;
      this.H = var4;
   }
}
