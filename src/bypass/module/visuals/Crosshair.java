package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.render.ClientColor;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.NumberSetting;
import java.awt.Color;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public final class Crosshair extends Module {
   private final NumberSetting<Float> gap = new NumberSetting<>("Gap", "Gap of crosshair", 5.0F, 0.0F, 15.0F);
   private final NumberSetting<Float> width = new NumberSetting<>("Width", "Width of crosshair", 0.5F, 0.1F, 3.0F);
   private final NumberSetting<Float> size = new NumberSetting<>("Size", "Size of crosshair", 7.0F, 1.0F, 15.0F);
   private final ColorSetting color = new ColorSetting("Color", "Color", ClientColor.PURPLE.getColor());
   private final ColorSetting outlineColor = new ColorSetting("Outline-Color", "Color", Color.BLACK);
   private final BooleanSetting dynamic = new BooleanSetting("Dynamic", "with bows n shit u know !!", true);
   private final BooleanSetting dot = new BooleanSetting("Dot", "puts da DOT in da MIDDLE", true);
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN) {
         int i = this.outlineColor.getValue().getRGB();
         int j = renderEvent.getResolution().getScaledWidth() / 2;
         int k = renderEvent.getResolution().getScaledHeight() / 2;
         float f = this.gap.getValue();
         if (!mc.thePlayer.isSpectator() && this.dynamic.getValue() && mc.thePlayer.getHeldItem() != null) {
            ItemStack itemStack = mc.thePlayer.getHeldItem();
            int l = mc.thePlayer.p();
            if (itemStack.getItem() == Items.bow) {
               float g = (itemStack.getMaxItemUseDuration() - l) / 20.0F;
               if (l == 0 || g > 1.0F) {
                  g = 1.0F;
               }

               f = this.gap.getValue() + (int)((1.0F - g) * (this.gap.getValue() + 5.0F) * 2.0F);
            }
         }

         RenderUtil.drawThinBorderedRect(
            j - this.width.getValue(),
            k - f - this.size.getValue(),
            j + 1.0F + this.width.getValue(),
            k - f + 0.2F,
            this.color.getValue().getRGB(),
            i
         );
         RenderUtil.drawThinBorderedRect(
            j - this.width.getValue(),
            k + f + 1.0F - 0.2F,
            j + 1.0F + this.width.getValue(),
            k + 1 + f + this.size.getValue() - 0.15F,
            this.color.getValue().getRGB(),
            i
         );
         RenderUtil.drawThinBorderedRect(
            j - f - this.size.getValue() + 0.15F,
            k - this.width.getValue(),
            j - f + 0.2F,
            k + 1.0F + this.width.getValue(),
            this.color.getValue().getRGB(),
            i
         );
         RenderUtil.drawThinBorderedRect(
            j + 1 + f - 0.2F,
            k - this.width.getValue(),
            j + this.size.getValue() + f + 1.0F,
            k + 1.0F + this.width.getValue(),
            this.color.getValue().getRGB(),
            i
         );
         if (this.dot.getValue()) {
            RenderUtil.drawThinBorderedRect(j - 0.7F, k - 0.7F, j + 1.7F, k + 1.7F, this.color.getValue().getRGB(), i);
         }
      }
   };

   public Crosshair(Category category) {
      super(category);
      this.setHidden(false);
      this.addSettings(this.color, this.outlineColor, this.size, this.gap, this.width, this.dynamic, this.dot);
   }
}
