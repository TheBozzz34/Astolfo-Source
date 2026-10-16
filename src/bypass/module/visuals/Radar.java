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
   private RadarHud hud;
   private int lastSize;
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN) {
         if (this.lastSize != this.size.asInt()) {
            this.createHud();
         }

         this.lastSize = this.size.getValue();
         if (this.hud != null) {
            if (Mouse.isButtonDown(0)) {
               this.posX.setValue(this.hud.getX());
               this.posY.setValue(this.hud.getYPos());
            } else if (this.posX.getValue() != this.hud.getX() || this.posY.getValue() != this.hud.getYPos()) {
               if (this.hud.getX() != this.posX.getValue()) {
                  this.hud.setX(this.posX.getValue());
               }

               if (this.hud.getYPos() != this.posY.getValue()) {
                  this.hud.setY(this.posY.getValue());
               }
            }

            int i = renderEvent.getResolution().getScaledHeight();
            int j = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
            int k = i - Mouse.getY() * i / mc.displayHeight - 1;
            this.hud.setEntities(this.entities);
            this.hud.setSpacing(this.spacing.getValue());
            this.hud.render(j, k);
            if (mc.currentScreen instanceof GuiChat && this.hud.isDragging() && Mouse.isButtonDown(0)) {
               this.hud.updateDragPosition(j, k);
            }

            if (Mouse.isButtonDown(0) && this.hud.isHovered(j, k)) {
               this.hud.setDragging(true, j, k);
            } else {
               this.hud.setDraggingNoOffset(false);
            }
         }
      }
   };

   public Radar(Category category) {
      super(category);
      this.addSettings(this.entities, this.posX, this.posY, this.spacing, this.size);
      this.setHidden(false);
   }

   private void createHud() {
      this.hud = new RadarHud(this.posX.getValue(), this.posY.getValue(), this.size.asInt(), this.size.asInt());
   }

   @Override
   public void onEnable() {
      this.createHud();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.hud = null;
      super.onDisable();
   }
}
