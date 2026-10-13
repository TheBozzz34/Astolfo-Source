package bypass.server;

public final class CubecraftServer extends Server {
   private static final long c = 27120598476481L;

   public CubecraftServer() {
      super("net", "cubecraft", ServerType.CUBECRAFT);
   }

   @Override
   public boolean m() {
      return true;
   }
}
