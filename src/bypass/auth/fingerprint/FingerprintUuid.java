package bypass.auth.fingerprint;

import java.util.UUID;

public final class FingerprintUuid {
   public static final UUID NIL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
   private final FingerprintRegistry registry;
   private UUID forcedUuid;

   public FingerprintUuid(FingerprintRegistry fingerprintRegistry) {
      this.registry = fingerprintRegistry;
      int i = this.registry.validateAll();
      if (i != this.registry.countValidatedEntries()) {
         this.forcedUuid = NIL_UUID;
         this.registry.invalidateAll();
      }
   }

   public UUID getUuid() {
      if (this.forcedUuid != null) {
         return this.forcedUuid;
      }

      long l = this.registry.getCombinedHash();
      long m = Long.MIN_VALUE | l << 31 << 11;
      return UUID.nameUUIDFromBytes(Long.toBinaryString(m).getBytes());
   }
}
