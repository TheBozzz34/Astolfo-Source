package bypass.account;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

public interface Authenticator {
   String a();

   void Y(Minecraft var1, Consumer<Throwable> var2);

   boolean u();

   boolean d();

   void n();

   int B();

   long v();

   default UUID W() {
      return null;
   }
}
