package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class GameJarHashList {
   private final ListContainerOwner<String> l;

   public GameJarHashList() {
      GameJarHash gameJarHash = new GameJarHash();
      this.l = new ListContainerOwner<>(gameJarHash.H());
   }

   public ListContainerOwner<String> E() {
      return this.l;
   }
}
