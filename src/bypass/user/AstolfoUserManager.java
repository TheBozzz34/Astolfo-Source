package bypass.user;

import bypass.Astolfo;
import bypass.util.FnvHash;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class AstolfoUserManager {
   public Map<String, AstolfoUser> users = new ConcurrentHashMap<>();
   public static final FnvHash fnvHash = new FnvHash();

   public Map<String, AstolfoUser> getUsers() {
      return this.users;
   }

   public AstolfoUser getUser(String string) {
      boolean bl = Astolfo.INSTANCE.getSenseSetting().getValue();
      String string2 = fnvHash.fnv1aHash64(string.getBytes()).toString();
      AstolfoUser astolfoUser = this.users.get(string2);
      if (astolfoUser != null && astolfoUser.status == AstolfoUserStatus.C) {
         return astolfoUser;
      }

      boolean bl2 = false;
      EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
      if (entityPlayerSP != null) {
         String string3 = fnvHash.fnv1aHash64(entityPlayerSP.getName().getBytes()).toString();
         AstolfoUser astolfoUser2 = this.users.get(string3);
         if (astolfoUser2 != null && astolfoUser2.status == AstolfoUserStatus.C) {
            bl2 = true;
         }
      }

      return !bl && !bl2 ? null : astolfoUser;
   }
}
