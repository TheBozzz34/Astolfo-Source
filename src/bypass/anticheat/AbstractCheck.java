package bypass.anticheat;

import bypass.ClientChat;
import bypass.module.other.HackerDetector;

public abstract class AbstractCheck implements Check {
   private final String D;
   private final CheckType P;
   public int T;

   public AbstractCheck(String string, CheckType checkType) {
      this.D = string;
      this.P = checkType;
   }

   public void w(int i) {
      this.T -= i;
      if (this.T <= 0) {
         this.T = 0;
      }
   }

   public void c(String string, int i) {
      this.T += i;
      if (HackerDetector.debug.getValue()) {
         ClientChat.CHAT.I(String.format("%s has flagged %s (vl: %s)", string, this.D, this.T));
      }
   }

   public void x(String string) {
      this.c(string, 1);
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
      return 30;
   }

   public void D_void() {
      this.T = 0;
   }
}
