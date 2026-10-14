package bypass.auth.net;

public final class DefaultConnectionHandlerFactory implements ConnectionHandlerFactory {
   private final SelectorRegistrar E;

   public DefaultConnectionHandlerFactory(SelectorRegistrar selectorRegistrar) {
      this.E = selectorRegistrar;
   }

   @Override
   public ConnectionHandler y(NioConnection nioConnection) {
      return new DefaultConnectionHandler(this.E, nioConnection);
   }
}
