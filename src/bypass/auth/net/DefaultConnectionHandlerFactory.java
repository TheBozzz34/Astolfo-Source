package bypass.auth.net;

public final class DefaultConnectionHandlerFactory implements ConnectionHandlerFactory {
   private final SelectorRegistrar E;

   public DefaultConnectionHandlerFactory(SelectorRegistrar var1) {
      this.E = var1;
   }

   @Override
   public ConnectionHandler y(NioConnection var1) {
      return new DefaultConnectionHandler(this.E, var1);
   }
}
