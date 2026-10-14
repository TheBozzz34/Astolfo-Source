package bypass.auth;

public class AuthSession {
   private final AuthServerAddress address = new AuthServerAddress();
   private final AuthEndpoint endpoint = new AuthEndpoint(this.address.getHost());
   private AuthAccount account;

   public AuthAccount getAccount() {
      return this.account;
   }

   public AuthEndpoint getEndpoint() {
      return this.endpoint;
   }

   public AuthServerAddress getAddress() {
      return this.address;
   }

   public void setAccount(AuthAccount authAccount) {
      this.account = authAccount;
   }
}
