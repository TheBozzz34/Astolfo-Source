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
   private static final long a = 51304178136456L;
   private static final long c = 2383881169117643002L;

   public Notification(String var1, String var2, NotificationType var3, int var4) {
      this.i = var1;
      this.u = var2 == null ? "" : var2;
      this.t = var3;
      this.S = var4;
   }

   public void K() {
      long var1 = a ^ 140338932476934L;
      long var3 = var1 ^ 25451262682325L;
      this.h.setDuration(this.S);
      this.v.setDuration((int)c);
      Astolfo.INSTANCE.getNotificationManager().add(this);
   }

   public void E() {
      long var1 = a ^ 108225880169274L;
      long var3 = var1 ^ 11994441438185L;
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
