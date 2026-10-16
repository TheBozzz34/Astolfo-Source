package bypass.gui.animation;

public class TimedAnimation extends Animation {
   private long lastUpdateTime;

   public TimedAnimation() {
      this.duration = 400;
   }

   @Override
   public Animation start() {
      this.lastUpdateTime = System.currentTimeMillis();
      return super.start();
   }

   @Override
   public double getValue() {
      long l = System.currentTimeMillis();
      this.step((int)(l - this.lastUpdateTime));
      this.lastUpdateTime = l;
      return this.ease(this.progress);
   }
}
