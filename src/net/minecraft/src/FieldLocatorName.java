package net.minecraft.src;

import java.lang.reflect.Field;

public class FieldLocatorName implements IFieldLocator {
   private ReflectorClass reflectorClass = null;
   private String targetFieldName = null;

   public FieldLocatorName(ReflectorClass reflectorClass, String targetFieldName) {
      this.reflectorClass = reflectorClass;
      this.targetFieldName = targetFieldName;
   }

   @Override
   public Field getField() {
      Class class2 = this.reflectorClass.getTargetClass();
      if (class2 == null) {
         return null;
      }

      try {
         Field field = class2.getDeclaredField(this.targetFieldName);
         field.setAccessible(true);
         return field;
      } catch (NoSuchFieldException noSuchFieldException) {
         Config.log("(Reflector) Field not present: " + class2.getName() + "." + this.targetFieldName);
         return null;
      } catch (SecurityException securityException) {
         securityException.printStackTrace();
         return null;
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         return null;
      }
   }
}
