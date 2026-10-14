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
   public int getClosestServerProtocol(UserConnection userConnection) throws Exception {
      if (!userConnection.isClientSide()) {
         NativeVersionProvider nativeVersionProvider = Via.getManager().getProviders().get(NativeVersionProvider.class);
         return nativeVersionProvider != null ? ProtocolVersion.getProtocol(nativeVersionProvider.getServerProtocolVersion()).getVersion() : super.getClosestServerProtocol(userConnection);
      }

      ProtocolInfo protocolInfo = Objects.requireNonNull(userConnection.getProtocolInfo());
      if (!ViaFabric.enabled) {
         return protocolInfo.getProtocolVersion();
      }

      int i = ViaFabric.INSTANCE.getVersion().getVersion();
      SocketAddress socketAddress = userConnection.getChannel().remoteAddress();
      if (socketAddress instanceof InetSocketAddress) {
         VFAddressParser vfAddressParser = new VFAddressParser();
         Integer integer = vfAddressParser.c(((InetSocketAddress)socketAddress).getHostName()).l;
         if (integer != null) {
            i = integer;
         }
      }

      boolean bl = this.v(socketAddress);
      boolean bl2 = ProtocolUtils.isSupported(i, protocolInfo.getProtocolVersion());
      if (bl || !bl2) {
         i = protocolInfo.getProtocolVersion();
      }

      return i;
   }

   private boolean v(SocketAddress socketAddress) {
      return false;
   }
}
