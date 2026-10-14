package bypass.auth.packet;

public class LoginResponse {
   public boolean success;
   public String message;
   private static final long a = 79762784218006L;

   public LoginResponse(boolean bl, String string) {
      this.success = bl;
      this.message = string;
   }
}
