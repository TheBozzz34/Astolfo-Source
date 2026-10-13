package bypass.server;

public final class HypixelServer extends Server {
   private static final long c = 16364574108760L;

   public HypixelServer() {
      super("net", "hypixel", ServerType.HYPIXEL);
   }

   @Override
   public boolean m() {
      return mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel");
   }
}
