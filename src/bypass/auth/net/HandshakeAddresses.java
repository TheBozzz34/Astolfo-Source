package bypass.auth.net;

public final class HandshakeAddresses {
   private final String G;
   private final String O;
   private static final long a = 59985426250122L;

   public HandshakeAddresses(String var1, String var2) {
      this.G = var1;
      this.O = var2;
   }

   public String W() {
      return this.G;
   }

   public String F() {
      return this.O;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof HandshakeAddresses)) {
         return false;
      }

      HandshakeAddresses var5 = (HandshakeAddresses)var1;
      return var5.G.equals(this.G) && var5.O.equals(this.O);
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
