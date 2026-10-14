package bypass.auth.packet;

public class LoginResponse {
   public boolean success;
   public String message;

   public LoginResponse(boolean bl, String string) {
      this.success = bl;
      this.message = string;
   }
}
