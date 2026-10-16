package bypass.auth.net;

public class ConnectorFactoryBuilder {
   private static final ConnectionOptions defaultOptions = ConnectionOptions.ofSize(8);
   public final boolean extended;
   public boolean extendedEnabled;

   public ConnectorFactoryBuilder(boolean bl) {
      this.extended = bl;
   }

   public ConnectorFactoryBuilder() {
      this(false);
   }

   public ConnectorFactory build() {
      ConnectionOptions connectionOptions = defaultOptions;
      if (this.extended && this.extendedEnabled) {
         connectionOptions = ConnectionOptions.ofSize(16);
      }

      return new ConnectorFactory();
   }
}
