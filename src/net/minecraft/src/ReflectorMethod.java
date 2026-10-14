package net.minecraft.src;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ReflectorMethod {
   private ReflectorClass reflectorClass = null;
   private String targetMethodName = null;
   private Class[] targetMethodParameterTypes = null;
   private boolean checked = false;
   private Method targetMethod = null;

   public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName) {
      this(reflectorClass, targetMethodName, (Class[])null, false);
   }

   public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName, Class[] targetMethodParameterTypes) {
      this(reflectorClass, targetMethodName, targetMethodParameterTypes, false);
   }

   public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName, Class[] targetMethodParameterTypes, boolean lazyResolve) {
      this.reflectorClass = reflectorClass;
      this.targetMethodName = targetMethodName;
      this.targetMethodParameterTypes = targetMethodParameterTypes;
      if (!lazyResolve) {
         Method method = this.getTargetMethod();
      }
   }

   public Method getTargetMethod() {
      if (this.checked) {
         return this.targetMethod;
      }

      this.checked = true;
      Class class2 = this.reflectorClass.getTargetClass();
      if (class2 == null) {
         return null;
      }

      try {
         if (this.targetMethodParameterTypes == null) {
            Method[] methods = getMethods(class2, this.targetMethodName);
            if (methods.length <= 0) {
               Config.log("(Reflector) Method not present: " + class2.getName() + "." + this.targetMethodName);
               return null;
            }

            if (methods.length > 1) {
               Config.warn("(Reflector) More than one method found: " + class2.getName() + "." + this.targetMethodName);

               for (int i = 0; i < methods.length; i++) {
                  Method method2 = methods[i];
                  Config.warn("(Reflector)  - " + method2);
               }

               return null;
            }

            this.targetMethod = methods[0];
         } else {
            this.targetMethod = getMethod(class2, this.targetMethodName, this.targetMethodParameterTypes);
         }

         if (this.targetMethod == null) {
            Config.log("(Reflector) Method not present: " + class2.getName() + "." + this.targetMethodName);
            return null;
         } else {
            this.targetMethod.setAccessible(true);
            return this.targetMethod;
         }
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         return null;
      }
   }

   public boolean exists() {
      return this.checked ? this.targetMethod != null : this.getTargetMethod() != null;
   }

   public Class getReturnType() {
      Method method = this.getTargetMethod();
      return method == null ? null : method.getReturnType();
   }

   public void deactivate() {
      this.checked = true;
      this.targetMethod = null;
   }

   public static Method getMethod(Class cls, String methodName, Class[] paramTypes) {
      Method[] methods = cls.getDeclaredMethods();

      for (int i = 0; i < methods.length; i++) {
         Method method = methods[i];
         if (method.getName().equals(methodName)) {
            Class[] classs = method.getParameterTypes();
            if (Reflector.matchesTypes(paramTypes, classs)) {
               return method;
            }
         }
      }

      return null;
   }

   public static Method[] getMethods(Class cls, String methodName) {
      ArrayList arrayList = new ArrayList();
      Method[] methods = cls.getDeclaredMethods();

      for (int i = 0; i < methods.length; i++) {
         Method method = methods[i];
         if (method.getName().equals(methodName)) {
            arrayList.add(method);
         }
      }

      return (java.lang.reflect.Method[])(arrayList.toArray(new Method[arrayList.size()]));
   }
}
