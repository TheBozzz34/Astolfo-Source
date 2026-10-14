package bypass.notification;

import bypass.Astolfo;
import bypass.gui.animation.Animation;
import bypass.gui.animation.TimedAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class Notification {
   private final String i;
   private final String u;
   private final NotificationType t;
   private final int S;
   public final Animation h = new TimedAnimation();
   public final Animation v = new TimedAnimation();

   public Notification(String string, String string2, NotificationType notificationType, int i) {
      this.i = string;
      this.u = string2 == null ? "" : string2;
      this.t = notificationType;
      this.S = i;
   }

   public void K() {
      this.h.setDuration(this.S);
      this.v.setDuration(250);
      Astolfo.INSTANCE.getNotificationManager().add(this);
   }

   public void E() {
      Astolfo.INSTANCE.getNotificationManager().getList().remove(this);
   }

   public void N() {
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.pop"), 1.0F));
      this.h.forwards().rewind().start();
      this.v.forwards().rewind().start();
   }

   public boolean I() {
      return this.h.isRunning();
   }

   public String p() {
      return this.i;
   }

   public String O() {
      return this.u;
   }

   public NotificationType G() {
      return this.t;
   }
}
