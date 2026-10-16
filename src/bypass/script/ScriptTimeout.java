package bypass.script;

public class ScriptTimeout {
   public Object result = null;
   private boolean finished = false;
   public final Object lock = new Object();

   public void runWithTimeout(Thread thread, int i) {
      thread.start();
      synchronized (this.lock) {
         if (!this.finished) {
            try {
               if (i < 1) {
                  this.lock.wait();
               } else {
                  this.lock.wait(1000 * i);
               }
            } catch (InterruptedException interruptedException) {
               throw new RuntimeException(interruptedException);
            }
         }
      }

      if (!this.finished) {
         thread.interrupt();
         thread.stop();
         throw new RuntimeException("Script was forced to termination: Execution time bigger then " + i + " seconds");
      }
   }

   public void markFinished() {
      synchronized (this.lock) {
         this.finished = true;
         this.lock.notifyAll();
      }
   }
}
