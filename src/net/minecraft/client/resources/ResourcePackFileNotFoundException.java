package net.minecraft.client.resources;

import java.io.File;
import java.io.FileNotFoundException;

public class ResourcePackFileNotFoundException extends FileNotFoundException {
   private static final long a = 134129255218357L;

   public ResourcePackFileNotFoundException(File var1, String var2) {
      super(String.format("'%s' in ResourcePack '%s'", var2, var1));
   }
}
