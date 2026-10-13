package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileResourcePack extends AbstractResourcePack implements Closeable {
   public static final Splitter entryNameSplitter = Splitter.on('/').omitEmptyStrings().limit(3);
   private ZipFile resourcePackZipFile;
   private static final long b = 86528188425082L;

   public FileResourcePack(File var1) {
      super(var1);
   }

   private ZipFile getResourcePackZipFile() throws IOException {
      if (this.resourcePackZipFile == null) {
         this.resourcePackZipFile = new ZipFile(this.resourcePackFile);
      }

      return this.resourcePackZipFile;
   }

   @Override
   public InputStream getInputStreamByName(String var1) throws IOException {
      ZipFile var4 = this.getResourcePackZipFile();
      ZipEntry var5 = var4.getEntry(var1);
      if (var5 == null) {
         throw new ResourcePackFileNotFoundException(this.resourcePackFile, var1);
      } else {
         return var4.getInputStream(var5);
      }
   }

   @Override
   public boolean hasResourceName(String var1) {
      try {
         return this.getResourcePackZipFile().getEntry(var1) != null;
      } catch (IOException var5) {
         return false;
      }
   }

   @Override
   public Set<String> getResourceDomains() {
      ZipFile var4;
      try {
         var4 = this.getResourcePackZipFile();
      } catch (IOException var11) {
         return Collections.emptySet();
      }

      Enumeration var5 = var4.entries();
      HashSet var6 = Sets.newHashSet();

      while (var5.hasMoreElements()) {
         ZipEntry var7 = (ZipEntry)var5.nextElement();
         String var8 = var7.getName();
         if (var8.startsWith("assets/")) {
            ArrayList var9 = Lists.newArrayList(entryNameSplitter.split(var8));
            if (var9.size() > 1) {
               String var10 = (String)var9.get(1);
               if (!var10.equals(var10.toLowerCase())) {
                  this.logNameNotLowercase(var10);
               } else {
                  var6.add(var10);
               }
            }
         }
      }

      return var6;
   }

   @Override
   public void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   @Override
   public void close() throws IOException {
      if (this.resourcePackZipFile != null) {
         this.resourcePackZipFile.close();
         this.resourcePackZipFile = null;
      }
   }
}
