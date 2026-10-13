package net.minecraft.client.resources;

import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

public class FolderResourcePack extends AbstractResourcePack {
   private static final long b = 44560436986742L;

   public FolderResourcePack(File var1) {
      super(var1);
   }

   @Override
   public InputStream getInputStreamByName(String var1) throws IOException {
      return new BufferedInputStream(new FileInputStream(new File(this.resourcePackFile, var1)));
   }

   @Override
   public boolean hasResourceName(String var1) {
      return new File(this.resourcePackFile, var1).isFile();
   }

   @Override
   public Set<String> getResourceDomains() {
      HashSet var4 = Sets.newHashSet();
      File var5 = new File(this.resourcePackFile, "assets/");
      if (var5.isDirectory()) {
         for (File var9 : var5.listFiles((java.io.FileFilter)(DirectoryFileFilter.DIRECTORY))) {
            String var10 = getRelativeName(var5, var9);
            if (!var10.equals(var10.toLowerCase())) {
               this.logNameNotLowercase(var10);
            } else {
               var4.add(var10.substring(0, var10.length() - 1));
            }
         }
      }

      return var4;
   }
}
