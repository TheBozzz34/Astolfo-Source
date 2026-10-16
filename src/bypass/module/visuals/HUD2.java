package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.gui.hud.HudElement;
import bypass.gui.hud.WatermarkHud;
import bypass.module.Category;
import bypass.module.Module;
import bypass.render.RenderUtil;
import bypass.setting.NumberSetting;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class HUD2 extends Module {
   private final ArrayList<HudElement> hudElements = new ArrayList<>();
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN && !mc.gameSettings.n) {
         for (HudElement hudElement : this.hudElements) {
            if (hudElement.isVisible()) {
               float f = hudElement.getScale().asFloat();
               double d = renderEvent.getResolution().getScaleFactor() / Math.pow(renderEvent.getResolution().getScaleFactor(), 2.0);
               GlStateManager.pushMatrix();
               GlStateManager.scale(d * f, d * f, d * f);
               NumberSetting numberSetting = hudElement.getPosX();
               NumberSetting numberSetting2 = hudElement.getPosY();
               if (numberSetting.asFloat() > renderEvent.getResolution().getScaledWidth() / 2.0F) {
               }

               int i = renderEvent.getResolution().getScaledHeight();
               int j = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
               int k = i - Mouse.getY() * i / mc.displayHeight - 1;
               if (Mouse.isButtonDown(0)) {
                  numberSetting.setValue(hudElement.getX());
                  numberSetting2.setValue(hudElement.getYPos());
               } else if ((Float)numberSetting.getValue() != hudElement.getX() || (Float)numberSetting2.getValue() != hudElement.getYPos()) {
                  if (hudElement.getX() != (Float)numberSetting.getValue()) {
                     hudElement.setX((Float)numberSetting.getValue());
                  }

                  if (hudElement.getYPos() != (Float)numberSetting2.getValue()) {
                     hudElement.setY((Float)numberSetting2.getValue());
                  }
               }

               hudElement.render(j, k);
               if (mc.currentScreen instanceof GuiChat) {
                  RenderUtil.drawRect(
                     hudElement.getX(), hudElement.getYPos(), hudElement.getX() + hudElement.getWidth(), hudElement.getYPos() + hudElement.getHeight(), new Color(0, 0, 0, 120).getRGB()
                  );
                  if (hudElement.isDragging() && Mouse.isButtonDown(0)) {
                     hudElement.updateDragPosition(j, k);
                  }
               }

               if (Mouse.isButtonDown(0) && hudElement.isHovered(j, k)) {
                  hudElement.setDragging(true, j, k);
               } else {
                  hudElement.setDraggingNoOffset(false);
               }

               GlStateManager.popMatrix();
            }
         }
      }
   };

   public HUD2() {
      super("HUD2", Category.VISUALS);
      this.hudElements.add(new WatermarkHud());
   }
}
