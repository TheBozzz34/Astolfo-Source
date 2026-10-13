package bypass.module.hidden;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.render.RenderAfterScreenEvent;
import bypass.module.HiddenModule;
import bypass.util.Stopwatch;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public final class TitleAnnouncer extends HiddenModule {
   public static final Stopwatch d = new Stopwatch();
   public static String D;
   @EventHandler(RenderAfterScreenEvent.class)
   private Listener<RenderAfterScreenEvent> onRenderAfterScreen = var1 -> {
      if (!d.hasElapsed(5000.0F) && !StringUtils.isEmpty(D) && mc.ingameGUI != null) {
         mc.ingameGUI.displayedSubTitle = "";
         mc.ingameGUI.setDefaultTitlesTimes();
         mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), 1.2F));
         mc.ingameGUI.displayTitle(D, "", 500, 5000, 500);
         D = null;
      }
   };
   private static final long c = 121254683101945L;

   public TitleAnnouncer() {
      client.getEventBus().subscribe(this);
   }
}
