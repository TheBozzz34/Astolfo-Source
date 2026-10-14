package bypass.module.visuals;

import bypass.event.CancellableEvent;
import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.render.SetupTerrainEvent;
import bypass.module.Category;
import bypass.module.Module;
import org.lwjgl.opengl.GL11;

public final class CaveFinder extends Module {
   @EventHandler(value = SetupTerrainEvent.class, priority = EventPriority.LOWEST)
   private final Listener<SetupTerrainEvent> onSetupTerrain = CancellableEvent::cancel;
   private static final long c = 9304086497074L;

   public CaveFinder(Category category) {
      super(category);
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

   public static void j() {
      GL11.glCullFace(1028);
   }

   public static void B() {
      GL11.glCullFace(1029);
   }
}
