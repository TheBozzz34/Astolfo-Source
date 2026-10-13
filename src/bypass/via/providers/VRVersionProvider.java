package bypass.via.providers;

import bypass.via.ViaFabric;
import bypass.via.util.ProtocolUtils;
import bypass.via.util.VFAddressParser;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;

public class VRVersionProvider extends BaseVersionProvider {
   private static final long a = 43674454758878L;

   @Override
   public int getClosestServerProtocol(UserConnection var1) throws Exception {
      if (!var1.isClientSide()) {
         NativeVersionProvider var10 = Via.getManager().getProviders().get(NativeVersionProvider.class);
         return var10 != null ? ProtocolVersion.getProtocol(var10.getServerProtocolVersion()).getVersion() : super.getClosestServerProtocol(var1);
      }

      ProtocolInfo var5 = Objects.requireNonNull(var1.getProtocolInfo());
      if (!ViaFabric.enabled) {
         return var5.getProtocolVersion();
      }

      int var6 = ViaFabric.INSTANCE.getVersion().getVersion();
      SocketAddress var7 = var1.getChannel().remoteAddress();
      if (var7 instanceof InetSocketAddress) {
         VFAddressParser var8 = new VFAddressParser();
         Integer var9 = var8.c(((InetSocketAddress)var7).getHostName()).l;
         if (var9 != null) {
            var6 = var9;
         }
      }

      boolean var11 = this.v(var7);
      boolean var12 = ProtocolUtils.isSupported(var6, var5.getProtocolVersion());
      if (var11 || !var12) {
         var6 = var5.getProtocolVersion();
      }

      return var6;
   }

   private boolean v(SocketAddress var1) {
      return false;
   }
}
