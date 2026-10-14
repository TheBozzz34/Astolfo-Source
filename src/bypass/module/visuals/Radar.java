package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.gui.hud.RadarHud;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Mouse;

public final class Radar extends Module {
   private final MapSetting entities = new MapSettingBuilder("Entities")
      .ordered()
      .description("esp blocky")
      .add("Players", new BooleanSetting("Players", "Renders players", true))
      .add("Animals", new BooleanSetting("Animals", "Renders animals", true))
      .add("Monsters", new BooleanSetting("Monsters", "Renders monsters", false))
      .add("Passives", new BooleanSetting("Passives", "Renders passives", false))
      .add("Invisibles", new BooleanSetting("Invisibles", "Renders invisibles", false))
      .add("Local", new BooleanSetting("Local", "Renders local player", false))
      .add("Items", new BooleanSetting("Items", "Renders items", false))
      .build();
   private final NumberSetting<Float> posX = new NumberSetting<>("Pos-X", "Pos", 150.0F, 1.0F, 50000.0F);
   private final NumberSetting<Float> posY = new NumberSetting<>("Pos-Y", "Pos", 150.0F, 1.0F, 50000.0F);
   private final NumberSetting<Integer> spacing = new NumberSetting<>("Spacing", "Space", 1, 1, 5);
   private final NumberSetting<Integer> size = new NumberSetting<>("Size", "Width", 100, 50, 250);
   private RadarHud W;
   private int P;
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN) {
         if (this.P != this.size.asInt()) {
            this.L();
         }

         this.P = this.size.getValue();
         if (this.W != null) {
            if (Mouse.isButtonDown(0)) {
               this.posX.setValue(this.W.M());
               this.posY.setValue(this.W.E_float());
            } else if (this.posX.getValue() != this.W.M() || this.posY.getValue() != this.W.E_float()) {
               if (this.W.M() != this.posX.getValue()) {
                  this.W.o(this.posX.getValue());
               }

               if (this.W.E_float() != this.posY.getValue()) {
                  this.W.q(this.posY.getValue());
               }
            }

            int i = renderEvent.getResolution().getScaledHeight();
            int j = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
            int k = i - Mouse.getY() * i / mc.displayHeight - 1;
            this.W.E(this.entities);
            this.W.Z(this.spacing.getValue());
            this.W.render(j, k);
            if (mc.currentScreen instanceof GuiChat && this.W.u_boolean() && Mouse.isButtonDown(0)) {
               this.W.T(j, k);
            }

            if (Mouse.isButtonDown(0) && this.W.G(j, k)) {
               this.W.F(true, j, k);
            } else {
               this.W.x(false);
            }
         }
      }
   };

   public Radar(Category category) {
      super(category);
      this.addSettings(this.entities, this.posX, this.posY, this.spacing, this.size);
      this.setHidden(false);
   }

   private void L() {
      this.W = new RadarHud(this.posX.getValue(), this.posY.getValue(), this.size.asInt(), this.size.asInt());
   }

   @Override
   public void onEnable() {
      this.L();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.W = null;
      super.onDisable();
   }
}
