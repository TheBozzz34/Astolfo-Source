package bypass.setting;

public interface Value<T> extends Named {
   String getDescription();

   T getValue();

   void setValue(T object);

   void parse(String string);
}
