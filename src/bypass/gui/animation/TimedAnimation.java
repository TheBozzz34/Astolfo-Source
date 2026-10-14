package bypass.gui.animation;

public class TimedAnimation extends Animation {
   private long f;

   public TimedAnimation() {
      this.O = 400;
   }

   @Override
   public Animation start() {
      this.f = System.currentTimeMillis();
      return super.start();
   }

   @Override
   public double getValue() {
      long l = System.currentTimeMillis();
      this.step((int)(l - this.f));
      this.f = l;
      return this.ease(this.U);
   }
}
