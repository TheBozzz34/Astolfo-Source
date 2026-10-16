package bypass.auth.net;

public final class DefaultConnectionHandlerFactory implements ConnectionHandlerFactory {
   private final SelectorRegistrar selectorRegistrar;

   public DefaultConnectionHandlerFactory(SelectorRegistrar selectorRegistrar) {
      this.selectorRegistrar = selectorRegistrar;
   }

   @Override
   public ConnectionHandler createHandler(NioConnection nioConnection) {
      return new DefaultConnectionHandler(this.selectorRegistrar, nioConnection);
   }
}
