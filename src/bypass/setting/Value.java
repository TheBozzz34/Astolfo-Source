package bypass.setting;

public interface Value<T> extends Named {
   String getDescription();

   T getValue();

   void setValue(T var1);

   void parse(String var1);
}
