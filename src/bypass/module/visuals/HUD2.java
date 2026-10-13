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
   private final ArrayList<HudElement> z = new ArrayList<>();
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = var1 -> {
      if (var1.getType() == RenderType.SCREEN && !mc.gameSettings.n) {
         for (HudElement var6 : this.z) {
            if (var6.N()) {
               float var7 = var6.getScale().asFloat();
               double var8 = var1.getResolution().getScaleFactor() / Math.pow(var1.getResolution().getScaleFactor(), 2.0);
               GlStateManager.pushMatrix();
               GlStateManager.scale(var8 * var7, var8 * var7, var8 * var7);
               NumberSetting var10 = var6.getPosX();
               NumberSetting var11 = var6.getPosY();
               if (var10.asFloat() > var1.getResolution().getScaledWidth() / 2.0F) {
               }

               int var12 = var1.getResolution().getScaledHeight();
               int var13 = Mouse.getX() * var1.getResolution().getScaledWidth() / mc.displayWidth;
               int var14 = var12 - Mouse.getY() * var12 / mc.displayHeight - 1;
               if (Mouse.isButtonDown(0)) {
                  var10.setValue(var6.M());
                  var11.setValue(var6.E_float());
               } else if ((Float)var10.getValue() != var6.M() || (Float)var11.getValue() != var6.E_float()) {
                  if (var6.M() != (Float)var10.getValue()) {
                     var6.o((Float)var10.getValue());
                  }

                  if (var6.E_float() != (Float)var11.getValue()) {
                     var6.q((Float)var11.getValue());
                  }
               }

               var6.render(var13, var14);
               if (mc.currentScreen instanceof GuiChat) {
                  RenderUtil.drawRect(var6.M(), var6.E_float(), var6.M() + var6.x(), var6.E_float() + var6.Y(), new Color(0, 0, 0, (int)e).getRGB());
                  if (var6.u_boolean() && Mouse.isButtonDown(0)) {
                     var6.T(var13, var14);
                  }
               }

               if (Mouse.isButtonDown(0) && var6.G(var13, var14)) {
                  var6.F(true, var13, var14);
               } else {
                  var6.x(false);
               }

               GlStateManager.popMatrix();
            }
         }
      }
   };
   private static final long c = 133059744355620L;
   private static final long e = -6422960953641402248L;

   public HUD2() {
      super("HUD2", Category.VISUALS);
      this.z.add(new WatermarkHud());
   }
}
