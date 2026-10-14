package bypass.auth;

public final class PortChoice {
   private final int index;
   private final int port;

   public PortChoice(int i, int j) {
      this.index = i;
      this.port = j;
   }

   public int getIndex() {
      return this.index;
   }

   public int getPort() {
      return this.port;
   }
}
