package bypass.auth.net;

public class ConnectorFactory {
   private static ConnectionOptions V = ConnectionOptions.M(16);

   public HandshakeConnector d() {
      return new HandshakeConnector();
   }
}
