package bypass.setting;

import java.util.Optional;

public interface Parser<T> {
   Optional<T> parse(String var1, Class<? extends T> var2);
}
