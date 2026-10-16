package bypass.server;

public final class HypixelServer extends Server {
   public HypixelServer() {
      super("net", "hypixel", ServerType.HYPIXEL);
   }

   @Override
   public boolean isBrandValid() {
      return mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel");
   }
}
