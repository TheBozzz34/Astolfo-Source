package bypass.auth.net;

public class ConnectorFactoryBuilder {
   private static final ConnectionOptions n = ConnectionOptions.M(8);
   public final boolean F;
   public boolean l;

   public ConnectorFactoryBuilder(boolean bl) {
      this.F = bl;
   }

   public ConnectorFactoryBuilder() {
      this(false);
   }

   public ConnectorFactory Z() {
      ConnectionOptions connectionOptions = n;
      if (this.F && this.l) {
         connectionOptions = ConnectionOptions.M(16);
      }

      return new ConnectorFactory();
   }
}
