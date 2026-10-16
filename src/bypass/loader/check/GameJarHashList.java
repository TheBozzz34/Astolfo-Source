package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class GameJarHashList {
   private final ListContainerOwner<String> hashContainer;

   public GameJarHashList() {
      GameJarHash gameJarHash = new GameJarHash();
      this.hashContainer = new ListContainerOwner<>(gameJarHash.getHashes());
   }

   public ListContainerOwner<String> getHashContainer() {
      return this.hashContainer;
   }
}
