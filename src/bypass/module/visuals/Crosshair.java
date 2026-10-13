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
   private Listener<RenderEvent> onRender = var1x -> {
      if (var1x.getType() == RenderType.SCREEN) {
         int var5 = this.outlineColor.getValue().getRGB();
         int var6 = var1x.getResolution().getScaledWidth() / 2;
         int var7 = var1x.getResolution().getScaledHeight() / 2;
         float var8 = this.gap.getValue();
         if (!mc.thePlayer.isSpectator() && this.dynamic.getValue() && mc.thePlayer.getHeldItem() != null) {
            ItemStack var9 = mc.thePlayer.getHeldItem();
            int var10 = mc.thePlayer.p();
            if (var9.getItem() == Items.bow) {
               float var11 = (var9.getMaxItemUseDuration() - var10) / 20.0F;
               if (var10 == 0 || var11 > 1.0F) {
                  var11 = 1.0F;
               }

               var8 = this.gap.getValue() + (int)((1.0F - var11) * (this.gap.getValue() + 5.0F) * 2.0F);
            }
         }

         RenderUtil.drawThinBorderedRect(
            var6 - this.width.getValue(),
            var7 - var8 - this.size.getValue(),
            var6 + 1.0F + this.width.getValue(),
            var7 - var8 + 0.2F,
            this.color.getValue().getRGB(),
            var5
         );
         RenderUtil.drawThinBorderedRect(
            var6 - this.width.getValue(),
            var7 + var8 + 1.0F - 0.2F,
            var6 + 1.0F + this.width.getValue(),
            var7 + 1 + var8 + this.size.getValue() - 0.15F,
            this.color.getValue().getRGB(),
            var5
         );
         RenderUtil.drawThinBorderedRect(
            var6 - var8 - this.size.getValue() + 0.15F,
            var7 - this.width.getValue(),
            var6 - var8 + 0.2F,
            var7 + 1.0F + this.width.getValue(),
            this.color.getValue().getRGB(),
            var5
         );
         RenderUtil.drawThinBorderedRect(
            var6 + 1 + var8 - 0.2F,
            var7 - this.width.getValue(),
            var6 + this.size.getValue() + var8 + 1.0F,
            var7 + 1.0F + this.width.getValue(),
            this.color.getValue().getRGB(),
            var5
         );
         if (this.dot.getValue()) {
            RenderUtil.drawThinBorderedRect(var6 - 0.7F, var7 - 0.7F, var6 + 1.7F, var7 + 1.7F, this.color.getValue().getRGB(), var5);
         }
      }
   };
   private static final long c = 127268056824835L;

   public Crosshair(Category var1) {
      super(var1);
      this.setHidden(false);
      this.addSettings(this.color, this.outlineColor, this.size, this.gap, this.width, this.dynamic, this.dot);
   }
}
