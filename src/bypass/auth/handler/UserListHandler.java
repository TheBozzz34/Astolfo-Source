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

   public void handle(UserListPacket var1, PacketOutput var2) {
      long var3 = b ^ 127728776374741L;
      long var5 = var3 ^ 102341454961505L;
      if (var1.type == UserListType.A) {
         client.getUserManager().r().clear();
         String[] var8 = var1.users.split(" ");
         int var9 = 0;

         for (int var10 = var8.length; var9 < var10; var9++) {
            String var11 = var8[var9];
            String[] var12 = var11.split("\\+");
            if (var12.length == 2) {
               String var13 = var12[0].substring(0, 1);
               String var14 = var12[0].substring(1);
               String var15 = var12[1];
               if (!var14.isEmpty() && !var15.isEmpty()) {
                  client.getUserManager()
                     .r()
                     .put(
                        var15,
                        new AstolfoUser(var14, var15, var13.equals("*") ? AstolfoUserStatus.C : (var13.equals("º") ? AstolfoUserStatus.B : AstolfoUserStatus.A))
                     );
               }
            }
         }
      }
   }
}
