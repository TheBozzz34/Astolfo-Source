package bypass.loader.check;

public interface ValueCheckView {
   int computeValue();

   ValueType getType();

   CheckResult evaluate();
}
