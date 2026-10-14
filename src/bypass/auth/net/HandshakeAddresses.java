package bypass.auth.net;

public final class HandshakeAddresses {
   private final String G;
   private final String O;
   private static final long a = 59985426250122L;

   public HandshakeAddresses(String string, String string2) {
      this.G = string;
      this.O = string2;
   }

   public String W() {
      return this.G;
   }

   public String F() {
      return this.O;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      if (!(object instanceof HandshakeAddresses)) {
         return false;
      }

      HandshakeAddresses handshakeAddresses = (HandshakeAddresses)object;
      return handshakeAddresses.G.equals(this.G) && handshakeAddresses.O.equals(this.O);
   }

   @Override
   public int hashCode() {
      return 31 * (31 + this.G.hashCode()) + this.O.hashCode();
   }

   @Override
   public String toString() {
      return this.O + '-' + this.G;
   }
}
