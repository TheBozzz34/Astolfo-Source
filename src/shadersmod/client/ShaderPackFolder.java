package shadersmod.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import net.minecraft.src.StrUtils;

public class ShaderPackFolder implements IShaderPack {
   public File packFile;
   private static final long a = 16428691872007L;

   public ShaderPackFolder(String name, File file) {
      this.packFile = file;
   }

   @Override
   public void close() {
   }

   @Override
   public InputStream getResourceAsStream(String resName) {
      try {
         String string = StrUtils.removePrefixSuffix(resName, "/", "/");
         File file = new File(this.packFile, string);
         return !file.exists() ? null : new BufferedInputStream(new FileInputStream(file));
      } catch (Exception exception) {
         return null;
      }
   }

   @Override
   public boolean hasDirectory(String name) {
      File file = new File(this.packFile, name.substring(1));
      return !file.exists() ? false : file.isDirectory();
   }

   @Override
   public String getName() {
      return this.packFile.getName();
   }
}
