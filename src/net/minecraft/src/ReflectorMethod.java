package net.minecraft.src;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ReflectorMethod {
   private ReflectorClass reflectorClass = null;
   private String targetMethodName = null;
   private Class[] targetMethodParameterTypes = null;
   private boolean checked = false;
   private Method targetMethod = null;
   private static final long a = 120749656986586L;

   public ReflectorMethod(ReflectorClass var1, String var2) {
      this(var1, var2, (Class[])null, false);
   }

   public ReflectorMethod(ReflectorClass var1, String var2, Class[] var3) {
      this(var1, var2, var3, false);
   }

   public ReflectorMethod(ReflectorClass var1, String var2, Class[] var3, boolean var4) {
      this.reflectorClass = var1;
      this.targetMethodName = var2;
      this.targetMethodParameterTypes = var3;
      if (!var4) {
         Method var7 = this.getTargetMethod();
      }
   }

   public Method getTargetMethod() {
      if (this.checked) {
         return this.targetMethod;
      }

      this.checked = true;
      Class var3 = this.reflectorClass.getTargetClass();
      if (var3 == null) {
         return null;
      }

      try {
         if (this.targetMethodParameterTypes == null) {
            Method[] var4 = getMethods(var3, this.targetMethodName);
            if (var4.length <= 0) {
               Config.log("(Reflector) Method not present: " + var3.getName() + "." + this.targetMethodName);
               return null;
            }

            if (var4.length > 1) {
               Config.warn("(Reflector) More than one method found: " + var3.getName() + "." + this.targetMethodName);

               for (int var5 = 0; var5 < var4.length; var5++) {
                  Method var6 = var4[var5];
                  Config.warn("(Reflector)  - " + var6);
               }

               return null;
            }

            this.targetMethod = var4[0];
         } else {
            this.targetMethod = getMethod(var3, this.targetMethodName, this.targetMethodParameterTypes);
         }

         if (this.targetMethod == null) {
            Config.log("(Reflector) Method not present: " + var3.getName() + "." + this.targetMethodName);
            return null;
         } else {
            this.targetMethod.setAccessible(true);
            return this.targetMethod;
         }
      } catch (Throwable var7) {
         var7.printStackTrace();
         return null;
      }
   }

   public boolean exists() {
      return this.checked ? this.targetMethod != null : this.getTargetMethod() != null;
   }

   public Class getReturnType() {
      Method var4 = this.getTargetMethod();
      return var4 == null ? null : var4.getReturnType();
   }

   public void deactivate() {
      this.checked = true;
      this.targetMethod = null;
   }

   public static Method getMethod(Class var0, String var1, Class[] var2) {
      Method[] var5 = var0.getDeclaredMethods();

      for (int var6 = 0; var6 < var5.length; var6++) {
         Method var7 = var5[var6];
         if (var7.getName().equals(var1)) {
            Class[] var8 = var7.getParameterTypes();
            if (Reflector.matchesTypes(var2, var8)) {
               return var7;
            }
         }
      }

      return null;
   }

   public static Method[] getMethods(Class var0, String var1) {
      ArrayList var4 = new ArrayList();
      Method[] var5 = var0.getDeclaredMethods();

      for (int var6 = 0; var6 < var5.length; var6++) {
         Method var7 = var5[var6];
         if (var7.getName().equals(var1)) {
            var4.add(var7);
         }
      }

      return (java.lang.reflect.Method[])(var4.toArray(new Method[var4.size()]));
   }
}
