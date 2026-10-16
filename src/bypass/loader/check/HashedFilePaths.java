package bypass.loader.check;

import bypass.ClientAccess;
import bypass.util.container.ListContainerOwner;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class HashedFilePaths implements ClientAccess {
   public static final String AGENT_JAR_NAME = "astolfo-agent.jar";
   private final ListContainerOwner<Path> pathContainer;

   public HashedFilePaths() {
      ArrayList arrayList = new ArrayList();
      arrayList.add(new File(System.getProperty("user.home")).toPath().resolve(".astolfo").resolve(AGENT_JAR_NAME));
      this.pathContainer = new ListContainerOwner<>(arrayList);
   }

   public ListContainerOwner<Path> getPathContainer() {
      return this.pathContainer;
   }
}
