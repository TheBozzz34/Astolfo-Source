package bypass.via.platform;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntLinkedOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.gson.JsonObject;

public class VRInjector implements ViaInjector {
   @Override
   public void inject() {
   }

   @Override
   public void uninject() {
   }

   @Override
   public String getEncoderName() {
      return "via-encoder";
   }

   @Override
   public String getDecoderName() {
      return "via-decoder";
   }

   @Override
   public JsonObject getDump() {
      return new JsonObject();
   }

   @Override
   public IntSortedSet getServerProtocolVersions() {
      IntLinkedOpenHashSet intLinkedOpenHashSet = new IntLinkedOpenHashSet();
      intLinkedOpenHashSet.add(ProtocolVersion.v1_8.getOriginalVersion());
      intLinkedOpenHashSet.add(ProtocolVersion.getProtocols().stream().mapToInt(ProtocolVersion::getOriginalVersion).max().getAsInt());
      return intLinkedOpenHashSet;
   }

   @Override
   public int getServerProtocolVersion() throws Exception {
      return 47;
   }
}
