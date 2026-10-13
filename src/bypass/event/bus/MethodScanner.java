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
   private final Map<Class<?>, Reference<Method[]>> J = new WeakHashMap<>();
   private static final long a = 136677635857808L;

   @Override
   public void collect(Class<?> var1, List<Method> var2) {
      if (var1 != null) {
         label36:
         for (Method var9 : var1.getDeclaredMethods()) {
            for (Method var11 : var2) {
               if (this.hides(var11, var9)) {
                  continue label36;
               }
            }

            var2.add(var9);
         }

         this.collect(var1.getSuperclass(), var2);

         for (Class var15 : var1.getInterfaces()) {
            this.collect(var15, var2);
         }
      }
   }

   public Method[] getMembers(Class<?> var1) {
      Reference var6 = this.J.get(var1);
      if (var6 != null) {
         Method[] var7 = (Method[])var6.get();
         if (var7 != null) {
            return (Method[])var7.clone();
         }
      }

      ArrayList var9 = new ArrayList();
      this.collect(var1, var9);
      Method[] var8 = (java.lang.reflect.Method[])(var9.toArray(new Method[var9.size()]));
      this.J.put(var1, new SoftReference<>(var8));
      return (Method[])var8.clone();
   }

   public boolean hides(Method var1, Method var2) {
      return !Modifier.isPrivate(var1.getModifiers())
         && var1.getName().equals(var2.getName())
         && var1.getReturnType().isAssignableFrom(var2.getReturnType())
         && Arrays.equals(var1.getParameterTypes(), var2.getParameterTypes());
   }
}
