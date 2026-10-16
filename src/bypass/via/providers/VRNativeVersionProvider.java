package bypass.via.providers;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.realms.RealmsSharedConstants;

public class VRNativeVersionProvider implements NativeVersionProvider {
   @Override
   public int getServerProtocolVersion() {
      return this.getRealmsProtocolVersion();
   }

   private int getRealmsProtocolVersion() {
      try {
         return RealmsSharedConstants.class.getField("e").getInt(null);
      } catch (IllegalAccessException | NoSuchFieldException value) {
         value.printStackTrace();
         return ProtocolVersion.v1_8.getVersion();
      }
   }
}
