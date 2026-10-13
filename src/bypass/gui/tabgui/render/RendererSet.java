package bypass.gui.tabgui.render;

import bypass.Astolfo;
import bypass.gui.tabgui.Sized;
import bypass.render.font.CustomFontRenderer;
import java.util.Map;

public interface RendererSet {
   long b = 130721837572061L;

   Map<Class<? extends Sized>, ElementRenderer<?>> getRenderers();

   default void register(Class<? extends Sized> var1, ElementRenderer<?> var2) {
      this.getRenderers().put(var1, var2);
   }

   default ElementRenderer<?> get(Class<? extends Sized> var1) {
      return this.getRenderers().get(var1);
   }

   default CustomFontRenderer O() {
      return Astolfo.INSTANCE.getCustomFontRenderer();
   }
}
