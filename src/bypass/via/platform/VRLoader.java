package bypass.via.platform;

import bypass.via.providers.NativeVersionProvider;
import bypass.via.providers.VRHandItemProvider;
import bypass.via.providers.VRNativeVersionProvider;
import bypass.via.providers.VRVersionProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.bungee.providers.BungeeMovementTransmitter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;

public class VRLoader implements ViaPlatformLoader {
   @Override
   public void load() {
      Via.getManager().getProviders().use(NativeVersionProvider.class, new VRNativeVersionProvider());
      Via.getManager().getProviders().use(MovementTransmitterProvider.class, new BungeeMovementTransmitter());
      Via.getManager().getProviders().use(VersionProvider.class, new VRVersionProvider());
      if (Via.getPlatform().getConf().isItemCache()) {
         VRHandItemProvider vrHandItemProvider = new VRHandItemProvider();
         Via.getManager().getProviders().use(HandItemProvider.class, vrHandItemProvider);
      }
   }

   @Override
   public void unload() {
   }
}
