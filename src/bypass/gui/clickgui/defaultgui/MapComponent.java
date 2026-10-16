package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.clickgui.SettingContainer;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import bypass.setting.Setting;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;

public class MapComponent extends SettingContainer {
   public MapComponent(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void onClick(int i) {
      if (i == 0) {
         this.setExpanded(!this.isExpanded());
      }
   }

   @Override
   public void render(int i, int j) {
      int k = this.getChildrenHeight();
      if (this.isExpanded()) {
         RenderUtil.drawRect(
            this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight() + k, new Color(18, 18, 18).getRGB()
         );
      }

      GuiMainMenu.m
         .drawString(
            this.getMapSetting().getName() + "...",
            this.getX() + this.getWidth() / 2 - GuiMainMenu.m.getStringWidth(this.getSetting().getName() + "...", true) / 2 + 3.0F,
            this.getYPos(),
            Color.WHITE.getRGB()
         );
      if (this.isExpanded()) {
         int l = 0;

         for (Element element : this.getChildren()) {
            if (element instanceof SettingContainer) {
               ((SettingContainer)element).setPosition(this.getX() + 3.0F, this.getYPos() + l + 10.0F);
               element.render(i, j);
               l += ((SettingContainer)element).getHeight() + (((SettingContainer)element).isExpanded() ? 3 : 0);
            }

            if (element instanceof SettingComponent) {
               ((SettingComponent)element).setPosition(this.getX() + 3.0F, this.getYPos() + l + 10.0F);
               element.render(i, j);
               l += 10;
            }
         }
      }
   }

   public int getChildrenHeight() {
      int i = 0;
      if (this.isExpanded()) {
         for (Element element : this.getChildren()) {
            if (element instanceof BooleanComponent
               || element instanceof ColorPicker
               || element instanceof StringComponent
               || element instanceof NumberSlider
               || element instanceof RangeSlider
               || element instanceof EnumComponent) {
               i += ((Component)element).getHeight();
            }
         }
      }

      return i + (this.isExpanded() ? 3 : 0);
   }
}
