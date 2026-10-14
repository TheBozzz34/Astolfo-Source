package bypass.gui.animation;

public class Animation {
   private Easing i = Easing.LINEAR;
   public int O;
   private boolean z;
   private boolean r;
   public double U;
   private static final long a = 29336265419768L;
   private static final long c = -6639574584083349484L;

   public Animation() {
      this.O = (int)c;
      this.z = true;
      this.r = false;
      this.U = 0.0;
   }

   public Animation start() {
      this.r = true;
      return this;
   }

   public Animation stop() {
      this.r = false;
      return this;
   }

   public Animation rewindAndStop() {
      return this.rewind().stop();
   }

   public Animation rewind() {
      this.U = this.z ? 0.0 : 1.0;
      return this;
   }

   public Animation backwards() {
      this.z = false;
      return this;
   }

   public Animation forwards() {
      this.z = true;
      return this;
   }

   public Animation setEasing(Easing easing) {
      this.i = easing;
      return this;
   }

   public Animation setDuration(int i) {
      this.O = i;
      return this;
   }

   public Animation setProgress(double d) {
      this.U = d;
      return this;
   }

   public boolean isRunning() {
      return this.r;
   }

   public double getValue() {
      this.step(1);
      return this.ease(this.U);
   }

   public double getValue(double d, double d2) {
      return (d2 - d) * this.getValue() + d;
   }

   public void step(int i) {
      if (this.r) {
         if (!this.z) {
            i *= -1;
         }

         this.U = this.U + (double)i / this.O;
         if (this.U > 1.0) {
            this.U = 1.0;
            this.r = false;
         } else if (this.U < 0.0) {
            this.U = 0.0;
            this.r = false;
         }
      }
   }

   public double ease(double d) {
      this.i.r(d);
      return Math.min(Math.max(d, 0.0), 1.0);
   }
}
