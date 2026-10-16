package bypass.notification;

import bypass.Astolfo;
import bypass.gui.animation.Animation;
import bypass.gui.animation.TimedAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class Notification {
   private final String title;
   private final String message;
   private final NotificationType type;
   private final int duration;
   public final Animation timerAnimation = new TimedAnimation();
   public final Animation slideAnimation = new TimedAnimation();

   public Notification(String string, String string2, NotificationType notificationType, int i) {
      this.title = string;
      this.message = string2 == null ? "" : string2;
      this.type = notificationType;
      this.duration = i;
   }

   public void show() {
      this.timerAnimation.setDuration(this.duration);
      this.slideAnimation.setDuration(250);
      Astolfo.INSTANCE.getNotificationManager().add(this);
   }

   public void dismiss() {
      Astolfo.INSTANCE.getNotificationManager().getList().remove(this);
   }

   public void start() {
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.pop"), 1.0F));
      this.timerAnimation.forwards().rewind().start();
      this.slideAnimation.forwards().rewind().start();
   }

   public boolean isTimerRunning() {
      return this.timerAnimation.isRunning();
   }

   public String getTitle() {
      return this.title;
   }

   public String getMessage() {
      return this.message;
   }

   public NotificationType getType() {
      return this.type;
   }
}
