package bypass.auth;

public final class PortChoice {
   private final int index;
   private final int port;

   public PortChoice(int var1, int var2) {
      this.index = var1;
      this.port = var2;
   }

   public int getIndex() {
      return this.index;
   }

   public int getPort() {
      return this.port;
   }
}
