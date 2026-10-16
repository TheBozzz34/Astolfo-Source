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
   public static final Stopwatch titleTimer = new Stopwatch();
   public static String pendingTitle;
   @EventHandler(RenderAfterScreenEvent.class)
   private Listener<RenderAfterScreenEvent> onRenderAfterScreen = renderAfterScreenEvent -> {
      if (!titleTimer.hasElapsed(5000.0F) && !StringUtils.isEmpty(pendingTitle) && mc.ingameGUI != null) {
         mc.ingameGUI.displayedSubTitle = "";
         mc.ingameGUI.setDefaultTitlesTimes();
         mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), 1.2F));
         mc.ingameGUI.displayTitle(pendingTitle, "", 500, 5000, 500);
         pendingTitle = null;
      }
   };

   public TitleAnnouncer() {
      client.getEventBus().subscribe(this);
   }
}
