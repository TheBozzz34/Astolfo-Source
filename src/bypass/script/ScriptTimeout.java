package bypass.script;

public class ScriptTimeout {
   public Object L = null;
   private boolean U = false;
   public final Object W = new Object();
   private static final long a = 2856853134267L;
   private static final long e = 6338341585285022696L;

   public void i(Thread var1, int var2) {
      var1.start();
      synchronized (this.W) {
         if (!this.U) {
            try {
               if (var2 < 1) {
                  this.W.wait();
               } else {
                  this.W.wait((int)e * var2);
               }
            } catch (InterruptedException var9) {
               throw new RuntimeException(var9);
            }
         }
      }

      if (!this.U) {
         var1.interrupt();
         var1.stop();
         throw new RuntimeException("Script was forced to termination: Execution time bigger then " + var2 + " seconds");
      }
   }

   public void J() {
      synchronized (this.W) {
         this.U = true;
         this.W.notifyAll();
      }
   }
}
