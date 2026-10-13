package net.minecraft.src;

import java.lang.reflect.Field;

public class FieldLocatorName implements IFieldLocator {
   private ReflectorClass reflectorClass = null;
   private String targetFieldName = null;
   private static final long a = 8239406473918L;

   public FieldLocatorName(ReflectorClass var1, String var2) {
      this.reflectorClass = var1;
      this.targetFieldName = var2;
   }

   @Override
   public Field getField() {
      Class var4 = this.reflectorClass.getTargetClass();
      if (var4 == null) {
         return null;
      }

      try {
         Field var5 = var4.getDeclaredField(this.targetFieldName);
         var5.setAccessible(true);
         return var5;
      } catch (NoSuchFieldException var6) {
         Config.log("(Reflector) Field not present: " + var4.getName() + "." + this.targetFieldName);
         return null;
      } catch (SecurityException var7) {
         var7.printStackTrace();
         return null;
      } catch (Throwable var8) {
         var8.printStackTrace();
         return null;
      }
   }
}
