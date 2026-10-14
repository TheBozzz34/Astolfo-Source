package bypass.user;

import bypass.Astolfo;
import bypass.util.FnvHash;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class AstolfoUserManager {
   public Map<String, AstolfoUser> e = new ConcurrentHashMap<>();
   public static final FnvHash b = new FnvHash();
   private static final long a = 138647984534202L;

   public Map<String, AstolfoUser> r() {
      return this.e;
   }

   public AstolfoUser g(String string) {
      boolean bl = Astolfo.INSTANCE.getSenseSetting().getValue();
      String string2 = b.s(string.getBytes()).toString();
      AstolfoUser astolfoUser = this.e.get(string2);
      if (astolfoUser != null && astolfoUser.K == AstolfoUserStatus.C) {
         return astolfoUser;
      }

      boolean bl2 = false;
      EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
      if (entityPlayerSP != null) {
         String string3 = b.s(entityPlayerSP.getName().getBytes()).toString();
         AstolfoUser astolfoUser2 = this.e.get(string3);
         if (astolfoUser2 != null && astolfoUser2.K == AstolfoUserStatus.C) {
            bl2 = true;
         }
      }

      return !bl && !bl2 ? null : astolfoUser;
   }
}
