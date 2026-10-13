package bypass.auth.packet;

public class LoginResponse {
   public boolean success;
   public String message;
   private static final long a = 79762784218006L;

   public LoginResponse(boolean var1, String var2) {
      this.success = var1;
      this.message = var2;
   }
}
