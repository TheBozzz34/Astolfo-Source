package bypass.event;

import java.util.function.Consumer;

public interface Listener<E extends Event> extends Consumer<E> {
}
