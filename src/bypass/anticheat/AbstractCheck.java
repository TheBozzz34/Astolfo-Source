package bypass.anticheat;

import bypass.ClientChat;
import bypass.module.other.HackerDetector;

public abstract class AbstractCheck implements Check {
   private final String name;
   private final CheckType checkType;
   public int violations;

   public AbstractCheck(String string, CheckType checkType) {
      this.name = string;
      this.checkType = checkType;
   }

   public void removeViolations(int i) {
      this.violations -= i;
      if (this.violations <= 0) {
         this.violations = 0;
      }
   }

   public void addViolations(String string, int i) {
      this.violations += i;
      if (HackerDetector.debug.getValue()) {
         ClientChat.CHAT.print(String.format("%s has flagged %s (vl: %s)", string, this.name, this.violations));
      }
   }

   public void flag(String string) {
      this.addViolations(string, 1);
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public CheckType getCheckType() {
      return this.checkType;
   }

   @Override
   public int getMaxViolations() {
      return 30;
   }

   public void D_void() {
      this.violations = 0;
   }
}
