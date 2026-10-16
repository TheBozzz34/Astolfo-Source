package bypass.via.protocol;

import bypass.via.util.VFAddressParser;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;

public class ViaFabricHostnameProtocol extends AbstractSimpleProtocol {
   public static final ViaFabricHostnameProtocol instance = new ViaFabricHostnameProtocol();

   @Override
   public void registerPackets() {
      this.registerServerbound(State.HANDSHAKE, 0, 0, new ViaFabricHostnameProtocol.alf());
   }

   @Override
   public boolean isBaseProtocol() {
      return true;
   }

   public class alf extends PacketRemapper {
      @Override
      public void registerMap() {
         this.map(Type.VAR_INT);
         this.map(Type.STRING, new ViaFabricHostnameProtocol.alf.a1w(Type.STRING));
      }

      public class a1w extends ValueTransformer<String, String> {
         public a1w(Type type2) {
            super(type2);
         }

         public String transform(PacketWrapper packetWrapper, String string) {
            return new VFAddressParser().parse(string).serverAddress;
         }
      }
   }
}
