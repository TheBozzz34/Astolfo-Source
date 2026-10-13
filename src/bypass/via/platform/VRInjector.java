package bypass.via.platform;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntLinkedOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.gson.JsonObject;

public class VRInjector implements ViaInjector {
   private static final long a = 80067678242604L;
   private static final long e = 463030739030507567L;

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
      IntLinkedOpenHashSet var4 = new IntLinkedOpenHashSet();
      var4.add(ProtocolVersion.v1_8.getOriginalVersion());
      var4.add(ProtocolVersion.getProtocols().stream().mapToInt(ProtocolVersion::getOriginalVersion).max().getAsInt());
      return var4;
   }

   @Override
   public int getServerProtocolVersion() throws Exception {
      return (int)e;
   }
}
