package bypass.module.visuals;

import bypass.event.CancellableEvent;
import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.player.PushOutOfBlocksEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.event.render.SetupTerrainEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;

public final class NoRender extends Module {
   public static final BooleanSetting smart = new BooleanSetting("Smart", "smart entity rendering hack", true);
   private final BooleanSetting tnt = new BooleanSetting("TNT", "Removes Tnt", true);
   public static final BooleanSetting visGraph = new BooleanSetting("Vis-Graph", "", true);
   public static final BooleanSetting culling = new BooleanSetting("Culling", "disables culling", true);
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private Listener<RenderEvent> onRender = renderEvent -> {
      if (this.tnt.getValue() && renderEvent.getType() == RenderType.WORLD) {
         for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityTNTPrimed) {
               mc.theWorld.removeEntity(entity);
            }
         }
      }
   };
   @EventHandler(SetupTerrainEvent.class)
   private Listener<SetupTerrainEvent> onSetupTerrain = setupTerrainEvent -> {
      if (culling.getValue()) {
         setupTerrainEvent.cancel();
      }
   };
   @EventHandler(PushOutOfBlocksEvent.class)
   private Listener<PushOutOfBlocksEvent> onPushOutOfBlocks = CancellableEvent::cancel;
   private static final long c = 50777526781569L;

   public NoRender(Category category) {
      super(category);
      this.addSettings(smart, visGraph, this.tnt, culling);
      this.setHidden(false);
   }

   @Override
   public void onEnable() {
      mc.renderGlobal.loadRenderers();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      mc.renderGlobal.loadRenderers();
      super.onDisable();
   }
}
