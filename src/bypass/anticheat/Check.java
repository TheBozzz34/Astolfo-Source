package bypass.anticheat;

public interface Check {
   String getName();

   CheckType getCheckType();

   int getMaxViolations();
}
