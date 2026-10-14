package bypass.loader.check;

import bypass.ClientAccess;

public class FileHashCheckSetup implements ClientAccess {
   private final PathListHolder w;
   private final FileHashCheck d;
   private static final long b = 70945743115021L;

   public FileHashCheckSetup() {
      HashedFilePaths hashedFilePaths = new HashedFilePaths();
      this.w = new PathListHolder(hashedFilePaths.d());
      this.d = new FileHashCheck(this.w);
   }

   public FileHashCheck c() {
      return this.d;
   }
}
