package bypass.script;

public class ScriptTimeout {
   public Object L = null;
   private boolean U = false;
   public final Object W = new Object();
   private static final long a = 2856853134267L;
   private static final long e = 6338341585285022696L;

   public void i(Thread thread, int i) {
      thread.start();
      synchronized (this.W) {
         if (!this.U) {
            try {
               if (i < 1) {
                  this.W.wait();
               } else {
                  this.W.wait((int)e * i);
               }
            } catch (InterruptedException interruptedException) {
               throw new RuntimeException(interruptedException);
            }
         }
      }

      if (!this.U) {
         thread.interrupt();
         thread.stop();
         throw new RuntimeException("Script was forced to termination: Execution time bigger then " + i + " seconds");
      }
   }

   public void J() {
      synchronized (this.W) {
         this.U = true;
         this.W.notifyAll();
      }
   }
}
