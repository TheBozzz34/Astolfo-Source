package bypass.setting;

import java.util.Optional;

public interface Parser<T> {
   Optional<T> parse(String string, Class<? extends T> class2);
}
