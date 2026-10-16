package bypass.loader.check;

import bypass.ClientAccess;

public class FileHashCheckSetup implements ClientAccess {
   private final PathListHolder pathListHolder;
   private final FileHashCheck fileHashCheck;

   public FileHashCheckSetup() {
      HashedFilePaths hashedFilePaths = new HashedFilePaths();
      this.pathListHolder = new PathListHolder(hashedFilePaths.getPathContainer());
      this.fileHashCheck = new FileHashCheck(this.pathListHolder);
   }

   public FileHashCheck getFileHashCheck() {
      return this.fileHashCheck;
   }
}
