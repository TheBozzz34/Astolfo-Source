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

   public static Optional<Verdict> from(String string) throws NullPointerException {
      Objects.requireNonNull(string, "input");
      return Arrays.stream(values()).filter(verdict -> verdict.name().equalsIgnoreCase(string)).findAny();
   }
}
