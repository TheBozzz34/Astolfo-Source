package bypass.account;

public class AuthException extends Exception {
   private String h;

   public AuthException(String var1) {
      super(var1);
      this.h = var1;
   }

   public AuthException(String var1, String var2) {
      super(var1 + ":" + var2);
      this.h = var1;
   }

   public String U() {
      return this.h;
   }
}
