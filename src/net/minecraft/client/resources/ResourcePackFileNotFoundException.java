package net.minecraft.client.resources;

import java.io.File;
import java.io.FileNotFoundException;

public class ResourcePackFileNotFoundException extends FileNotFoundException {
   private static final long a = 134129255218357L;

   public ResourcePackFileNotFoundException(File resourcePack, String string) {
      super(String.format("'%s' in ResourcePack '%s'", string, resourcePack));
   }
}
