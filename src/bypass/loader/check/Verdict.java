package bypass.loader.check;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum Verdict {
   K,
   H,
   N;

   private static final Verdict[] D = new Verdict[]{K, H, N};
   private static final long a = 138916839629188L;

   public static Optional<Verdict> from(String var0) throws NullPointerException {
      Objects.requireNonNull(var0, "input");
      return Arrays.stream(values()).filter(var1 -> var1.name().equalsIgnoreCase(var0)).findAny();
   }
}
