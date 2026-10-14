package bypass.account;

public class AuthException extends Exception {
   private String h;

   public AuthException(String string) {
      super(string);
      this.h = string;
   }

   public AuthException(String string, String string2) {
      super(string + ":" + string2);
      this.h = string;
   }

   public String U() {
      return this.h;
   }
}
