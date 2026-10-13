package shadersmod.client;

import java.io.InputStream;

public class ShaderPackNone implements IShaderPack {
   @Override
   public void close() {
   }

   @Override
   public InputStream getResourceAsStream(String var1) {
      return null;
   }

   @Override
   public boolean hasDirectory(String var1) {
      return false;
   }

   @Override
   public String getName() {
      return Shaders.packNameNone;
   }
}
