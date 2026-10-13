package bypass.account;

public enum AccountType {
   MOJANG("mojang"),
   THEALTENING("thealtening");

   public String f;
   private static final AccountType[] X = new AccountType[]{MOJANG, THEALTENING};

   AccountType(String var3) {
      this.f = var3;
   }
}
