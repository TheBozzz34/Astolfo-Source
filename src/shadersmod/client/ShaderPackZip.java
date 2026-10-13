package shadersmod.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.src.StrUtils;

public class ShaderPackZip implements IShaderPack {
   public File packFile;
   public ZipFile packZipFile;
   private static final long a = 137512964160384L;

   public ShaderPackZip(String var1, File var2) {
      this.packFile = var2;
      this.packZipFile = null;
   }

   @Override
   public void close() {
      if (this.packZipFile != null) {
         try {
            this.packZipFile.close();
         } catch (Exception var5) {
         }

         this.packZipFile = null;
      }
   }

   @Override
   public InputStream getResourceAsStream(String var1) {
      try {
         if (this.packZipFile == null) {
            this.packZipFile = new ZipFile(this.packFile);
         }

         String var5 = StrUtils.removePrefix(var1, "/");
         ZipEntry var6 = this.packZipFile.getEntry(var5);
         return var6 == null ? null : this.packZipFile.getInputStream(var6);
      } catch (Exception var7) {
         return null;
      }
   }

   @Override
   public boolean hasDirectory(String var1) {
      try {
         if (this.packZipFile == null) {
            this.packZipFile = new ZipFile(this.packFile);
         }

         String var5 = StrUtils.removePrefix(var1, "/");
         ZipEntry var6 = this.packZipFile.getEntry(var5);
         return var6 != null;
      } catch (IOException var7) {
         return false;
      }
   }

   @Override
   public String getName() {
      return this.packFile.getName();
   }
}
