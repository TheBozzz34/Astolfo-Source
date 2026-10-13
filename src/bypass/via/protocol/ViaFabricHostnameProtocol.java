package bypass.via.protocol;

import bypass.via.util.VFAddressParser;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;

public class ViaFabricHostnameProtocol extends AbstractSimpleProtocol {
   public static final ViaFabricHostnameProtocol i = new ViaFabricHostnameProtocol();
   private static final long a = 40667888257833L;

   @Override
   public void registerPackets() {
      this.registerServerbound(State.HANDSHAKE, 0, 0, new ViaFabricHostnameProtocol.alf());
   }

   @Override
   public boolean isBaseProtocol() {
      return true;
   }

   public class alf extends PacketRemapper {
      private static final long a = 91192389229130L;

      @Override
      public void registerMap() {
         this.map(Type.VAR_INT);
         this.map(Type.STRING, new ViaFabricHostnameProtocol.alf.a1w(Type.STRING));
      }

      public class a1w extends ValueTransformer<String, String> {
         public a1w(Type var2) {
            super(var2);
         }

         public String transform(PacketWrapper var1, String var2) {
            return new VFAddressParser().c(var2).p;
         }
      }
   }
}
