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
      ItemRenderer itemRenderer = new ItemRenderer();
      this.R = new Builder<Class<? extends Sized>, ElementRenderer<?>>()
         .put(TabGui.Panel.class, new PanelRenderer())
         .put(CategoryItem.class, itemRenderer)
         .put(ModuleItem.class, itemRenderer)
         .put(BooleanSettingItem.class, itemRenderer)
         .put(NumberSettingItem.class, itemRenderer)
         .put(MapSettingItem.class, itemRenderer)
         .put(EnumSettingItem.class, itemRenderer)
         .build();
   }
}
