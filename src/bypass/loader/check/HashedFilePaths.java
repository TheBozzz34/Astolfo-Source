package bypass.loader.check;

import bypass.ClientAccess;
import bypass.util.container.ListContainerOwner;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class HashedFilePaths implements ClientAccess {
   public static final String N = "astolfo-agent.jar";
   private final ListContainerOwner<Path> b;

   public HashedFilePaths() {
      ArrayList arrayList = new ArrayList();
      arrayList.add(new File(System.getProperty("user.home")).toPath().resolve(".astolfo").resolve(N));
      this.b = new ListContainerOwner<>(arrayList);
   }

   public ListContainerOwner<Path> d() {
      return this.b;
   }
}
