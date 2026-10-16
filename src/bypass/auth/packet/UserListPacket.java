package bypass.auth.packet;

import bypass.Astolfo;

public class UserListPacket {
   public UserListType type;
   public String users;
   public String extra;
   public boolean sense;

   public UserListPacket(UserListType userListType, String string, String string2) {
      this.type = userListType;
      this.users = string;
      this.extra = string2;
      this.sense = Astolfo.INSTANCE.getSenseSetting().getValue();
   }

   public UserListPacket(UserListType userListType, String string, String string2, boolean bl) {
      this.type = userListType;
      this.users = string;
      this.extra = string2;
      this.sense = bl;
   }
}
