package bypass.account;

import java.lang.reflect.Field;

public class FieldSetter {
   private String D;
   private Class<?> P;

   public FieldSetter(String string) {
      try {
         this.P = Class.forName(string);
      } catch (ClassNotFoundException classNotFoundException) {
         classNotFoundException.printStackTrace();
      }
   }

   public void k(String string, Object object) throws NoSuchFieldException, IllegalAccessException {
      Field field = this.P.getDeclaredField(string);
      field.setAccessible(true);
      Field field2 = Field.class.getDeclaredField("modifiers");
      field2.setAccessible(true);
      field2.setInt(field, field.getModifiers() & -17);
      field.set(null, object);
   }
}
