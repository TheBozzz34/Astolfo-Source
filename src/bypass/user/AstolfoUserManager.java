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

   public AstolfoUser g(String var1) {
      boolean var6 = Astolfo.INSTANCE.getSenseSetting().getValue();
      String var7 = b.s(var1.getBytes()).toString();
      AstolfoUser var8 = this.e.get(var7);
      if (var8 != null && var8.K == AstolfoUserStatus.C) {
         return var8;
      }

      boolean var9 = false;
      EntityPlayerSP var10 = Minecraft.getMinecraft().thePlayer;
      if (var10 != null) {
         String var11 = b.s(var10.getName().getBytes()).toString();
         AstolfoUser var12 = this.e.get(var11);
         if (var12 != null && var12.K == AstolfoUserStatus.C) {
            var9 = true;
         }
      }

      return !var6 && !var9 ? null : var8;
   }
}
