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

   public Animation setEasing(Easing var1) {
      this.i = var1;
      return this;
   }

   public Animation setDuration(int var1) {
      this.O = var1;
      return this;
   }

   public Animation setProgress(double var1) {
      this.U = var1;
      return this;
   }

   public boolean isRunning() {
      return this.r;
   }

   public double getValue() {
      this.step(1);
      return this.ease(this.U);
   }

   public double getValue(double var1, double var3) {
      return (var3 - var1) * this.getValue() + var1;
   }

   public void step(int var1) {
      if (this.r) {
         if (!this.z) {
            var1 *= -1;
         }

         this.U = this.U + (double)var1 / this.O;
         if (this.U > 1.0) {
            this.U = 1.0;
            this.r = false;
         } else if (this.U < 0.0) {
            this.U = 0.0;
            this.r = false;
         }
      }
   }

   public double ease(double var1) {
      this.i.r(var1);
      return Math.min(Math.max(var1, 0.0), 1.0);
   }
}
