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

   public ShaderPackZip(String name, File file) {
      this.packFile = file;
      this.packZipFile = null;
   }

   @Override
   public void close() {
      if (this.packZipFile != null) {
         try {
            this.packZipFile.close();
         } catch (Exception exception) {
         }

         this.packZipFile = null;
      }
   }

   @Override
   public InputStream getResourceAsStream(String resName) {
      try {
         if (this.packZipFile == null) {
            this.packZipFile = new ZipFile(this.packFile);
         }

         String string = StrUtils.removePrefix(resName, "/");
         ZipEntry zipEntry = this.packZipFile.getEntry(string);
         return zipEntry == null ? null : this.packZipFile.getInputStream(zipEntry);
      } catch (Exception exception) {
         return null;
      }
   }

   @Override
   public boolean hasDirectory(String resName) {
      try {
         if (this.packZipFile == null) {
            this.packZipFile = new ZipFile(this.packFile);
         }

         String string = StrUtils.removePrefix(resName, "/");
         ZipEntry zipEntry = this.packZipFile.getEntry(string);
         return zipEntry != null;
      } catch (IOException ioException) {
         return false;
      }
   }

   @Override
   public String getName() {
      return this.packFile.getName();
   }
}
