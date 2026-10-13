package net.minecraft.src;

import java.lang.reflect.Constructor;

public class ReflectorConstructor {
   private ReflectorClass reflectorClass = null;
   private Class[] parameterTypes = null;
   private boolean checked = false;
   private Constructor targetConstructor = null;
   private static final long a = 105776566625076L;

   public ReflectorConstructor(ReflectorClass var1, Class[] var2) {
      this.reflectorClass = var1;
      this.parameterTypes = var2;
      Constructor var3 = this.getTargetConstructor();
   }

   public Constructor getTargetConstructor() {
      if (this.checked) {
         return this.targetConstructor;
      }

      this.checked = true;
      Class var3 = this.reflectorClass.getTargetClass();
      if (var3 == null) {
         return null;
      }

      try {
         this.targetConstructor = findConstructor(var3, this.parameterTypes);
         if (this.targetConstructor == null) {
            Config.dbg("(Reflector) Constructor not present: " + var3.getName() + ", params: " + Config.arrayToString(this.parameterTypes));
         }

         if (this.targetConstructor != null) {
            this.targetConstructor.setAccessible(true);
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

      return this.targetConstructor;
   }

   private static Constructor findConstructor(Class var0, Class[] var1) {
      Constructor[] var4 = var0.getDeclaredConstructors();

      for (int var5 = 0; var5 < var4.length; var5++) {
         Constructor var6 = var4[var5];
         Class[] var7 = var6.getParameterTypes();
         if (Reflector.matchesTypes(var1, var7)) {
            return var6;
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
