package bypass.auth.fingerprint;

import java.util.UUID;

public final class FingerprintUuid {
   public static final UUID W = UUID.fromString("00000000-0000-0000-0000-000000000000");
   private final FingerprintRegistry R;
   private UUID j;

   public FingerprintUuid(FingerprintRegistry fingerprintRegistry) {
      this.R = fingerprintRegistry;
      int i = this.R.B();
      if (i != this.R.l()) {
         this.j = W;
         this.R.invalidateAll();
      }
   }

   public UUID h() {
      if (this.j != null) {
         return this.j;
      }

      long l = this.R.C();
      long m = Long.MIN_VALUE | l << 31 << 11;
      return UUID.nameUUIDFromBytes(Long.toBinaryString(m).getBytes());
   }
}
