package bypass.gui.tabgui.render;

import bypass.Astolfo;
import bypass.gui.tabgui.Sized;
import bypass.render.font.CustomFontRenderer;
import java.util.Map;

public interface RendererSet {
   long b = 130721837572061L;

   Map<Class<? extends Sized>, ElementRenderer<?>> getRenderers();

   default void register(Class<? extends Sized> class2, ElementRenderer<?> elementRenderer) {
      this.getRenderers().put(class2, elementRenderer);
   }

   default ElementRenderer<?> get(Class<? extends Sized> class2) {
      return this.getRenderers().get(class2);
   }

   default CustomFontRenderer O() {
      return Astolfo.INSTANCE.getCustomFontRenderer();
   }
}
