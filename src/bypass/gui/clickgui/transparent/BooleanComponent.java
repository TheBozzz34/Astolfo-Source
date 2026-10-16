package bypass.gui.clickgui.transparent;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.Setting;
import java.awt.Color;
import net.minecraft.util.EnumChatFormatting;

public class BooleanComponent extends SettingComponent {
   private static final Color grayColor = new Color(57, 57, 57);
   public static final Color darkColor = new Color(22, 22, 22);
   private int hoverAlpha;

   public BooleanComponent(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void render(int i, int j) {
      if (this.isHovered(i, j)) {
         if (this.hoverAlpha < 120) {
            this.hoverAlpha += 5;
         }
      } else if (this.hoverAlpha > 0) {
         this.hoverAlpha -= 5;
      }

      if (this.hoverAlpha > 0) {
         RenderUtil.drawRect(
            this.getX() + 1.5F,
            this.getYPos() - 1.0F,
            this.getX() + this.getWidth() + 0.5F,
            this.getYPos() + this.getHeight() + 4.0F,
            new Color(255, 255, 255, this.hoverAlpha).getRGB()
         );
      }

      RenderUtil.drawRect(
         this.getX() + 1.5F,
         this.getYPos() - 1.0F,
         this.getX() + this.getWidth() + 0.5F,
         this.getYPos() + this.getHeight() + 4.0F,
         new Color(1, 1, 1, 120).getRGB()
      );
      RenderUtil.drawRectOutline(this.getX() + 3.0F, this.getYPos() + 2.0F, this.getX() + 12.0F, this.getYPos() + 11.0F, new Color(137, 137, 137).getRGB());
      if (this.getBooleanSetting().getValue()) {
         RenderUtil.drawLine(2.0F, this.getX() + 6.0F, this.getYPos() + 7.0F, this.getX() + 8.0F, this.getYPos() + 10.0F, client.getColor().getRGB());
         RenderUtil.drawLine(2.0F, this.getX() + 11.0F, this.getYPos() + 3.0F, this.getX() + 8.0F, this.getYPos() + 10.0F, client.getColor().getRGB());
      }

      client.getCustomFontRenderer()
         .drawStringWithShadow(
            EnumChatFormatting.GRAY + this.getBooleanSetting().getName().replace("-", " "), this.getX() + 15.0F, this.getYPos() + 0.5F, Color.WHITE.getRGB()
         );
      if (this.isHovered(i, j) && HUD.toolTips.getValue()) {
         client.getCustomFontRenderer().drawStringWithShadow(this.getBooleanSetting().getDescription(), i + 6, j - 4, -1);
      }
   }

   @Override
   public void onClick(int i) {
      if (this.getParentContainer().isExpanded() && i == 0) {
         this.getBooleanSetting().set(!this.getBooleanSetting().getValue());
      }
   }

   private BooleanSetting getBooleanSetting() {
      return (BooleanSetting)this.getSetting();
   }
}
