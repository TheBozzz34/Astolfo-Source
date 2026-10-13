package bypass.gui.tabgui.render;

import bypass.Astolfo;
import bypass.gui.TabGui;
import bypass.gui.tabgui.BooleanSettingItem;
import bypass.gui.tabgui.CategoryItem;
import bypass.gui.tabgui.EnumSettingItem;
import bypass.gui.tabgui.MapSettingItem;
import bypass.gui.tabgui.ModuleItem;
import bypass.gui.tabgui.NumberSettingItem;
import bypass.gui.tabgui.Sized;
import bypass.render.font.CustomFontRenderer;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;

public class DefaultRendererSet implements RendererSet {
   private final Map<Class<? extends Sized>, ElementRenderer<?>> R;
   private static final long a = 58075775627356L;

   @Override
   public CustomFontRenderer O() {
      return Astolfo.INSTANCE.getCustomFontRenderer();
   }

   @Override
   public Map<Class<? extends Sized>, ElementRenderer<?>> getRenderers() {
      return this.R;
   }

   public DefaultRendererSet() {
      ItemRenderer var5 = new ItemRenderer();
      this.R = new Builder<Class<? extends Sized>, ElementRenderer<?>>()
         .put(TabGui.Panel.class, new PanelRenderer())
         .put(CategoryItem.class, var5)
         .put(ModuleItem.class, var5)
         .put(BooleanSettingItem.class, var5)
         .put(NumberSettingItem.class, var5)
         .put(MapSettingItem.class, var5)
         .put(EnumSettingItem.class, var5)
         .build();
   }
}
