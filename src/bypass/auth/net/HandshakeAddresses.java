package bypass.auth.net;

public final class HandshakeAddresses {
   private final String clientAddress;
   private final String remoteAddress;

   public HandshakeAddresses(String string, String string2) {
      this.clientAddress = string;
      this.remoteAddress = string2;
   }

   public String getClientAddress() {
      return this.clientAddress;
   }

   public String getRemoteAddress() {
      return this.remoteAddress;
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
      return handshakeAddresses.clientAddress.equals(this.clientAddress) && handshakeAddresses.remoteAddress.equals(this.remoteAddress);
   }

   @Override
   public int hashCode() {
      return 31 * (31 + this.clientAddress.hashCode()) + this.remoteAddress.hashCode();
   }

   @Override
   public String toString() {
      return this.remoteAddress + '-' + this.clientAddress;
   }
}
