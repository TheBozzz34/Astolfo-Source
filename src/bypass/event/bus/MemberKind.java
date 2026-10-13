package bypass.event.bus;

public enum MemberKind {
   METHOD,
   FIELD;

   private static final MemberKind[] k = new MemberKind[]{METHOD, FIELD};
}
