package bypass.auth;

public class AuthAccount {
   private final String username;

   public AuthAccount(String string) {
      this.username = string;
   }

   public String getUsername() {
      return this.username;
   }
}
