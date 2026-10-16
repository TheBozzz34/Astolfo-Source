package bypass.event.bus;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class MethodScanner implements MemberScanner<Method> {
   private final Map<Class<?>, Reference<Method[]>> cache = new WeakHashMap<>();

   @Override
   public void collect(Class<?> class2, List<Method> list) {
      if (class2 != null) {
         label36:
         for (Method method : class2.getDeclaredMethods()) {
            for (Method method2 : list) {
               if (this.hides(method2, method)) {
                  continue label36;
               }
            }

            list.add(method);
         }

         this.collect(class2.getSuperclass(), list);

         for (Class class3 : class2.getInterfaces()) {
            this.collect(class3, list);
         }
      }
   }

   public Method[] getMembers(Class<?> class2) {
      Reference reference = this.cache.get(class2);
      if (reference != null) {
         Method[] methods = (Method[])reference.get();
         if (methods != null) {
            return (Method[])methods.clone();
         }
      }

      ArrayList arrayList = new ArrayList();
      this.collect(class2, arrayList);
      Method[] methods2 = (java.lang.reflect.Method[])(arrayList.toArray(new Method[arrayList.size()]));
      this.cache.put(class2, new SoftReference<>(methods2));
      return (Method[])methods2.clone();
   }

   public boolean hides(Method method, Method method2) {
      return !Modifier.isPrivate(method.getModifiers())
         && method.getName().equals(method2.getName())
         && method.getReturnType().isAssignableFrom(method2.getReturnType())
         && Arrays.equals(method.getParameterTypes(), method2.getParameterTypes());
   }
}
