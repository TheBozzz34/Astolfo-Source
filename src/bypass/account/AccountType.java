package bypass.account;

public enum AccountType {
   MOJANG("mojang"),
   THEALTENING("thealtening");

   public String f;
   private static final AccountType[] X = new AccountType[]{MOJANG, THEALTENING};

   AccountType(String string2) {
      this.f = string2;
   }
}
