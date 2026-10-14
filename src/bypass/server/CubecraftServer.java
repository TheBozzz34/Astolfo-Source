package bypass.server;

public final class CubecraftServer extends Server {
   public CubecraftServer() {
      super("net", "cubecraft", ServerType.CUBECRAFT);
   }

   @Override
   public boolean m() {
      return true;
   }
}
