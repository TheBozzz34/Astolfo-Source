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

   public FileResourcePack(File resourcePackFileIn) {
      super(resourcePackFileIn);
   }

   private ZipFile getResourcePackZipFile() throws IOException {
      if (this.resourcePackZipFile == null) {
         this.resourcePackZipFile = new ZipFile(this.resourcePackFile);
      }

      return this.resourcePackZipFile;
   }

   @Override
   public InputStream getInputStreamByName(String name) throws IOException {
      ZipFile zipFile = this.getResourcePackZipFile();
      ZipEntry zipEntry = zipFile.getEntry(name);
      if (zipEntry == null) {
         throw new ResourcePackFileNotFoundException(this.resourcePackFile, name);
      } else {
         return zipFile.getInputStream(zipEntry);
      }
   }

   @Override
   public boolean hasResourceName(String name) {
      try {
         return this.getResourcePackZipFile().getEntry(name) != null;
      } catch (IOException ioException) {
         return false;
      }
   }

   @Override
   public Set<String> getResourceDomains() {
      ZipFile zipFile;
      try {
         zipFile = this.getResourcePackZipFile();
      } catch (IOException ioException) {
         return Collections.emptySet();
      }

      Enumeration enumeration = zipFile.entries();
      HashSet hashSet = Sets.newHashSet();

      while (enumeration.hasMoreElements()) {
         ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
         String string = zipEntry.getName();
         if (string.startsWith("assets/")) {
            ArrayList arrayList = Lists.newArrayList(entryNameSplitter.split(string));
            if (arrayList.size() > 1) {
               String string2 = (String)arrayList.get(1);
               if (!string2.equals(string2.toLowerCase())) {
                  this.logNameNotLowercase(string2);
               } else {
                  hashSet.add(string2);
               }
            }
         }
      }

      return hashSet;
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
