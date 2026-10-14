package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.auth.packet.PacketId;
import bypass.auth.packet.UserListPacket;
import bypass.auth.packet.UserListType;
import bypass.user.AstolfoUser;
import bypass.user.AstolfoUserStatus;

public class UserListHandler extends PacketHandler<UserListPacket> {
   private static final long b = 87283649196622L;

   public UserListHandler() {
      super(PacketId.USER_LIST);
   }

   public void handle(UserListPacket userListPacket, PacketOutput packetOutput) {
      long l = b ^ 127728776374741L;
      long m = l ^ 102341454961505L;
      if (userListPacket.type == UserListType.A) {
         client.getUserManager().r().clear();
         String[] strings = userListPacket.users.split(" ");
         int i = 0;

         for (int j = strings.length; i < j; i++) {
            String string = strings[i];
            String[] strings2 = string.split("\\+");
            if (strings2.length == 2) {
               String string2 = strings2[0].substring(0, 1);
               String string3 = strings2[0].substring(1);
               String string4 = strings2[1];
               if (!string3.isEmpty() && !string4.isEmpty()) {
                  client.getUserManager()
                     .r()
                     .put(
                        string4,
                        new AstolfoUser(string3, string4, string2.equals("*") ? AstolfoUserStatus.C : (string2.equals("º") ? AstolfoUserStatus.B : AstolfoUserStatus.A))
                     );
               }
            }
         }
      }
   }
}
