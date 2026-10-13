package bypass.anticheat;

import bypass.ClientChat;
import bypass.module.other.HackerDetector;

public abstract class AbstractCheck implements Check {
   private final String D;
   private final CheckType P;
   public int T;
   private static final long a = 45984530929003L;
   private static final long h = -4183889473106345954L;

   public AbstractCheck(String var1, CheckType var2) {
      this.D = var1;
      this.P = var2;
   }

   public void w(int var1) {
      this.T -= var1;
      if (this.T <= 0) {
         this.T = 0;
      }
   }

   public void c(String var1, int var2) {
      this.T += var2;
      if (HackerDetector.debug.getValue()) {
         ClientChat.CHAT.I(String.format("%s has flagged %s (vl: %s)", var1, this.D, this.T));
      }
   }

   public void x(String var1) {
      this.c(var1, 1);
   }

   @Override
   public String A() {
      return this.D;
   }

   @Override
   public CheckType D() {
      return this.P;
   }

   @Override
   public int p() {
      return (int)h;
   }

   public void D_void() {
      this.T = 0;
   }
}
