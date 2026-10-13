package bypass.server;

public class CubecraftGamesServer extends Server {
   private static final long c = 42526172279050L;

   public CubecraftGamesServer() {
      super("net", "cubecraftgames", ServerType.CUBECRAFT);
   }

   @Override
   public boolean m() {
      return true;
   }
}
