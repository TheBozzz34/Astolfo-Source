package bypass.account;

public class AuthException extends Exception {
   private String errorKey;

   public AuthException(String string) {
      super(string);
      this.errorKey = string;
   }

   public AuthException(String string, String string2) {
      super(string + ":" + string2);
      this.errorKey = string;
   }

   public String getErrorKey() {
      return this.errorKey;
   }
}
