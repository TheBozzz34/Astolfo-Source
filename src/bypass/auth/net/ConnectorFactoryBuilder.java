package bypass.auth.net;

public class ConnectorFactoryBuilder {
   private static final ConnectionOptions n = ConnectionOptions.M(8);
   public final boolean F;
   public boolean l;
   private static final long a = 42385277103549L;

   public ConnectorFactoryBuilder(boolean var1) {
      this.F = var1;
   }

   public ConnectorFactoryBuilder() {
      this(false);
   }

   public ConnectorFactory Z() {
      ConnectionOptions var4 = n;
      if (this.F && this.l) {
         var4 = ConnectionOptions.M(16);
      }

      return new ConnectorFactory();
   }
}
