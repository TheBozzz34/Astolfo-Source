package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.render.RenderItemEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;

public final class Camera extends Module {
   private final NumberSetting<Float> itemSize = new NumberSetting<>("Item-Size", "Size", 0.6F, 0.0F, 2.0F);
   public static final NumberSetting<Float> itemFOV = new NumberSetting<>("Item-FOV", "Item FOV", 1.0F, 1.0F, 2.0F);
   public static final BooleanSetting viewClip = new BooleanSetting("View-Clip", "clips thru stuff in third person", true);
   public static final MapSetting itemPosition = new MapSettingBuilder("Item-Position")
      .ordered()
      .description("")
      .add("X", new NumberSetting<>("X", "X coordinate", 0.0F, -2.0F, 2.0F))
      .add("Y", new NumberSetting<>("Y", "Y coordinate", 0.0F, -2.0F, 2.0F))
      .add("Z", new NumberSetting<>("Z", "Z coordinate", 0.0F, -2.0F, 2.0F))
      .build();
   public static final MapSetting enchant = new MapSettingBuilder("Enchant")
      .ordered()
      .description("enbchant")
      .add("Color", new ColorSetting("Color", "Attacks per second", Color.BLUE))
      .add("Enabled", new BooleanSetting("Enabled", "", false))
      .build();
   @EventHandler(RenderItemEvent.class)
   private Listener<RenderItemEvent> onRenderItem = var1x -> {
      if (var1x.getEntity() == mc.thePlayer) {
         GlStateManager.scale(this.itemSize.getValue(), this.itemSize.getValue(), this.itemSize.getValue());
      }
   };
   private static final long c = 2182981611249L;

   public Camera(Category var1) {
      super(var1);
      this.addSettings(enchant, viewClip, this.itemSize, itemFOV, itemPosition);
      this.setHidden(false);
   }
}
