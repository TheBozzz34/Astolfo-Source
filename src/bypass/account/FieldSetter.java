package bypass.account;

import java.lang.reflect.Field;

public class FieldSetter {
   private String fieldName;
   private Class<?> targetClass;

   public FieldSetter(String string) {
      try {
         this.targetClass = Class.forName(string);
      } catch (ClassNotFoundException classNotFoundException) {
         classNotFoundException.printStackTrace();
      }
   }

   public void setStaticFinalField(String string, Object object) throws NoSuchFieldException, IllegalAccessException {
      Field field = this.targetClass.getDeclaredField(string);
      field.setAccessible(true);
      Field field2 = Field.class.getDeclaredField("modifiers");
      field2.setAccessible(true);
      field2.setInt(field, field.getModifiers() & -17);
      field.set(null, object);
   }
}
