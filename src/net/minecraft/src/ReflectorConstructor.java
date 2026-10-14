package net.minecraft.src;

import java.lang.reflect.Constructor;

public class ReflectorConstructor {
   private ReflectorClass reflectorClass = null;
   private Class[] parameterTypes = null;
   private boolean checked = false;
   private Constructor targetConstructor = null;
   private static final long a = 105776566625076L;

   public ReflectorConstructor(ReflectorClass reflectorClass, Class[] parameterTypes) {
      this.reflectorClass = reflectorClass;
      this.parameterTypes = parameterTypes;
      Constructor c = this.getTargetConstructor();
   }

   public Constructor getTargetConstructor() {
      if (this.checked) {
         return this.targetConstructor;
      }

      this.checked = true;
      Class class2 = this.reflectorClass.getTargetClass();
      if (class2 == null) {
         return null;
      }

      try {
         this.targetConstructor = findConstructor(class2, this.parameterTypes);
         if (this.targetConstructor == null) {
            Config.dbg("(Reflector) Constructor not present: " + class2.getName() + ", params: " + Config.arrayToString(this.parameterTypes));
         }

         if (this.targetConstructor != null) {
            this.targetConstructor.setAccessible(true);
         }
      } catch (Throwable throwable) {
         throwable.printStackTrace();
      }

      return this.targetConstructor;
   }

   private static Constructor findConstructor(Class cls, Class[] paramTypes) {
      Constructor[] constructors = cls.getDeclaredConstructors();

      for (int i = 0; i < constructors.length; i++) {
         Constructor constructor = constructors[i];
         Class[] classs = constructor.getParameterTypes();
         if (Reflector.matchesTypes(paramTypes, classs)) {
            return constructor;
         }
      }

      return null;
   }

   public boolean exists() {
      return this.checked ? this.targetConstructor != null : this.getTargetConstructor() != null;
   }

   public void deactivate() {
      this.checked = true;
      this.targetConstructor = null;
   }
}
