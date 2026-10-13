package shadersmod.client;

import java.io.InputStream;

public class ShaderPackDefault implements IShaderPack {
   @Override
   public void close() {
   }

   @Override
   public InputStream getResourceAsStream(String var1) {
      return ShaderPackDefault.class.getResourceAsStream(var1);
   }

   @Override
   public String getName() {
      return Shaders.packNameDefault;
   }

   @Override
   public boolean hasDirectory(String var1) {
      return false;
   }
}
