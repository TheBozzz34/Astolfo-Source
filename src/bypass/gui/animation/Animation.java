package bypass.gui.animation;

public class Animation {
   private Easing easing = Easing.LINEAR;
   public int duration = 20;
   private boolean forward = true;
   private boolean running = false;
   public double progress = 0.0;

   public Animation start() {
      this.running = true;
      return this;
   }

   public Animation stop() {
      this.running = false;
      return this;
   }

   public Animation rewindAndStop() {
      return this.rewind().stop();
   }

   public Animation rewind() {
      this.progress = this.forward ? 0.0 : 1.0;
      return this;
   }

   public Animation backwards() {
      this.forward = false;
      return this;
   }

   public Animation forwards() {
      this.forward = true;
      return this;
   }

   public Animation setEasing(Easing easing) {
      this.easing = easing;
      return this;
   }

   public Animation setDuration(int i) {
      this.duration = i;
      return this;
   }

   public Animation setProgress(double d) {
      this.progress = d;
      return this;
   }

   public boolean isRunning() {
      return this.running;
   }

   public double getValue() {
      this.step(1);
      return this.ease(this.progress);
   }

   public double getValue(double d, double d2) {
      return (d2 - d) * this.getValue() + d;
   }

   public void step(int i) {
      if (this.running) {
         if (!this.forward) {
            i *= -1;
         }

         this.progress = this.progress + (double)i / this.duration;
         if (this.progress > 1.0) {
            this.progress = 1.0;
            this.running = false;
         } else if (this.progress < 0.0) {
            this.progress = 0.0;
            this.running = false;
         }
      }
   }

   public double ease(double d) {
      this.easing.apply(d);
      return Math.min(Math.max(d, 0.0), 1.0);
   }
}
