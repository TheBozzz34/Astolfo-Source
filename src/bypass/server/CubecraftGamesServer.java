package bypass.server;

public class CubecraftGamesServer extends Server {
   public CubecraftGamesServer() {
      super("net", "cubecraftgames", ServerType.CUBECRAFT);
   }

   @Override
   public boolean m() {
      return true;
   }
}
