package bypass.user;

public class AstolfoUser {
   public String clientName;
   public String nameHash;
   public AstolfoUserStatus status;

   public AstolfoUser(String string, String string2, AstolfoUserStatus astolfoUserStatus) {
      this.clientName = string;
      this.nameHash = string2;
      this.status = astolfoUserStatus;
   }
}
