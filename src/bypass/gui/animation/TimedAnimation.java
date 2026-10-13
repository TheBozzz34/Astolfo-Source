package bypass.gui.animation;

public class TimedAnimation extends Animation {
   private long f;
   private static final long b = 118455887835517L;
   private static final long d = 8020059136957874576L;

   public TimedAnimation() {
      this.O = (int)d;
   }

   @Override
   public Animation start() {
      this.f = System.currentTimeMillis();
      return super.start();
   }

   @Override
   public double getValue() {
      long var5 = System.currentTimeMillis();
      this.step((int)(var5 - this.f));
      this.f = var5;
      return this.ease(this.U);
   }
}
