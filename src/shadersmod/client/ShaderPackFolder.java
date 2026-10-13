package shadersmod.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import net.minecraft.src.StrUtils;

public class ShaderPackFolder implements IShaderPack {
   public File packFile;
   private static final long a = 16428691872007L;

   public ShaderPackFolder(String var1, File var2) {
      this.packFile = var2;
   }

   @Override
   public void close() {
   }

   @Override
   public InputStream getResourceAsStream(String var1) {
      try {
         String var4 = StrUtils.removePrefixSuffix(var1, "/", "/");
         File var5 = new File(this.packFile, var4);
         return !var5.exists() ? null : new BufferedInputStream(new FileInputStream(var5));
      } catch (Exception var6) {
         return null;
      }
   }

   @Override
   public boolean hasDirectory(String var1) {
      File var5 = new File(this.packFile, var1.substring(1));
      return !var5.exists() ? false : var5.isDirectory();
   }

   @Override
   public String getName() {
      return this.packFile.getName();
   }
}
