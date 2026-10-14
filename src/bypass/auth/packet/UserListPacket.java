package bypass.auth.packet;

import bypass.Astolfo;

public class UserListPacket {
   public UserListType type;
   public String users;
   public String s;
   public boolean H;
   private static final long a = 47742426424070L;

   public UserListPacket(UserListType userListType, String string, String string2) {
      this.type = userListType;
      this.users = string;
      this.s = string2;
      this.H = Astolfo.INSTANCE.getSenseSetting().getValue();
   }

   public UserListPacket(UserListType userListType, String string, String string2, boolean bl) {
      this.type = userListType;
      this.users = string;
      this.s = string2;
      this.H = bl;
   }
}
