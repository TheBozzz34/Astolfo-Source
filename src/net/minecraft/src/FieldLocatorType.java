package net.minecraft.src;

import java.lang.reflect.Field;

public class FieldLocatorType implements IFieldLocator {
   private ReflectorClass reflectorClass = null;
   private Class targetFieldType = null;
   private int targetFieldIndex;
   private static final long a = 84588247639097L;

   public FieldLocatorType(ReflectorClass var1, Class var2) {
      this(var1, var2, 0);
   }

   public FieldLocatorType(ReflectorClass var1, Class var2, int var3) {
      this.reflectorClass = var1;
      this.targetFieldType = var2;
      this.targetFieldIndex = var3;
   }

   @Override
   public Field getField() {
      Class var4 = this.reflectorClass.getTargetClass();
      if (var4 == null) {
         return null;
      }

      try {
         Field[] var5 = var4.getDeclaredFields();
         int var6 = 0;

         for (int var7 = 0; var7 < var5.length; var7++) {
            Field var8 = var5[var7];
            if (var8.getType() == this.targetFieldType) {
               if (var6 == this.targetFieldIndex) {
                  var8.setAccessible(true);
                  return var8;
               }

               var6++;
            }
         }

         Config.log("(Reflector) Field not present: " + var4.getName() + ".(type: " + this.targetFieldType + ", index: " + this.targetFieldIndex + ")");
         return null;
      } catch (SecurityException var9) {
         var9.printStackTrace();
         return null;
      } catch (Throwable var10) {
         var10.printStackTrace();
         return null;
      }
   }
}
