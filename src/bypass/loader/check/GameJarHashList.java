package bypass.loader.check;

import bypass.util.container.ListContainerOwner;

public class GameJarHashList {
   private final ListContainerOwner<String> l;

   public GameJarHashList() {
      GameJarHash var1 = new GameJarHash();
      this.l = new ListContainerOwner<>(var1.H());
   }

   public ListContainerOwner<String> E() {
      return this.l;
   }
}
