package net.minecraft.src;

import java.lang.reflect.Field;

public class FieldLocatorType implements IFieldLocator {
   private ReflectorClass reflectorClass = null;
   private Class targetFieldType = null;
   private int targetFieldIndex;
   private static final long a = 84588247639097L;

   public FieldLocatorType(ReflectorClass reflectorClass, Class targetFieldType) {
      this(reflectorClass, targetFieldType, 0);
   }

   public FieldLocatorType(ReflectorClass reflectorClass, Class targetFieldType, int targetFieldIndex) {
      this.reflectorClass = reflectorClass;
      this.targetFieldType = targetFieldType;
      this.targetFieldIndex = targetFieldIndex;
   }

   @Override
   public Field getField() {
      Class class2 = this.reflectorClass.getTargetClass();
      if (class2 == null) {
         return null;
      }

      try {
         Field[] fields = class2.getDeclaredFields();
         int i = 0;

         for (int j = 0; j < fields.length; j++) {
            Field field = fields[j];
            if (field.getType() == this.targetFieldType) {
               if (i == this.targetFieldIndex) {
                  field.setAccessible(true);
                  return field;
               }

               i++;
            }
         }

         Config.log("(Reflector) Field not present: " + class2.getName() + ".(type: " + this.targetFieldType + ", index: " + this.targetFieldIndex + ")");
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
