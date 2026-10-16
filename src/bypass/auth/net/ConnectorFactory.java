package bypass.auth.net;

public class ConnectorFactory {
   private static ConnectionOptions options = ConnectionOptions.ofSize(16);

   public HandshakeConnector createConnector() {
      return new HandshakeConnector();
   }
}
