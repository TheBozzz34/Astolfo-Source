package bypass.account;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

public interface Authenticator {
   String getUsername();

   void login(Minecraft minecraft, Consumer<Throwable> consumer);

   boolean canLogin();

   boolean canEdit();

   void markUsed();

   int getUseCount();

   long getLastUsed();

   default UUID getUuid() {
      return null;
   }
}
