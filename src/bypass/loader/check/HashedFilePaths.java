package bypass.loader.check;

import bypass.ClientAccess;
import bypass.util.container.ListContainerOwner;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class HashedFilePaths implements ClientAccess {
   public static final String N = FileHashCheckStrings.s("LVgbym1RpZ2//6iKi245HII=");
   private final ListContainerOwner<Path> b;
   private static final long c = 122997398136430L;

   public HashedFilePaths() {
      ArrayList var5 = new ArrayList();
      var5.add(new File(System.getProperty("user.home")).toPath().resolve(".astolfo").resolve(N));
      this.b = new ListContainerOwner<>(var5);
   }

   public ListContainerOwner<Path> d() {
      return this.b;
   }
}
