package bypass.auth.fingerprint;

import java.util.UUID;

public final class FingerprintUuid {
   public static final UUID W = UUID.fromString("00000000-0000-0000-0000-000000000000");
   private final FingerprintRegistry R;
   private UUID j;
   private static final long a = 59518042135852L;

   public FingerprintUuid(FingerprintRegistry var1) {
      this.R = var1;
      int var6 = this.R.B();
      if (var6 != this.R.l()) {
         this.j = W;
         this.R.invalidateAll();
      }
   }

   public UUID h() {
      if (this.j != null) {
         return this.j;
      }

      long var4 = this.R.C();
      long var6 = Long.MIN_VALUE | var4 << 31 << 11;
      return UUID.nameUUIDFromBytes(Long.toBinaryString(var6).getBytes());
   }
}
