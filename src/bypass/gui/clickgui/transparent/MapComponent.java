package bypass.gui.clickgui.transparent;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.clickgui.SettingContainer;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import bypass.setting.Setting;
import java.awt.Color;

public class MapComponent extends SettingContainer {
   private static final Color backgroundColor = new Color(23, 23, 23);

   public MapComponent(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void render(int i, int j) {
      RenderUtil.drawRect(
         this.getX() + 1.5F,
         this.getYPos() - 1.0F,
         this.getX() + this.getWidth() + 0.5F,
         this.getYPos() + this.getHeight() + 4.0F,
         new Color(1, 1, 1, 120).getRGB()
      );
      client.getCustomFontRenderer()
         .drawStringWithShadow(
            this.getMapSetting().getName() + "...",
            this.getX() + this.getWidth() / 2 - client.getCustomFontRenderer().getStringWidth(this.getSetting().getName() + "...") / 2 + 3.0F,
            this.getYPos(),
            Color.WHITE.getRGB()
         );
      if (this.isExpanded()) {
         int k = 0;

         for (Element element : this.getChildren()) {
            if (element instanceof SettingComponent) {
               ((SettingComponent)element).setPosition(this.getX() + 1.5F, this.getYPos() + k + 15.0F);
               element.render(i, j);
               k += 15;
               k = (int)(k - this.expandAnimation.getValue(0.0, k));
            }
         }
      }
   }

   @Override
   public void onClick(int i) {
      if (i == 0) {
         this.setExpanded(!this.isExpanded());
         if (this.isExpanded()) {
            this.expandAnimation.backwards().rewind().start();
         } else {
            this.expandAnimation.forwards().rewind().start();
         }
      }
   }

   public int getChildrenHeight() {
      byte b = 0;
      if (this.isExpanded()) {
         for (Element element : this.getChildren()) {
            if (element instanceof BooleanComponent || element instanceof StringComponent || element instanceof NumberSlider || element instanceof EnumComponent) {
               b += 15;
            }
         }
      }

      return b;
   }
}
