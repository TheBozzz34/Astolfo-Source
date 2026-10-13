package bypass.event.bus;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class FieldScanner implements MemberScanner<Field> {
   public final Map<Class<?>, Reference<Field[]>> b = new WeakHashMap<>();
   private static final long a = 16534970935776L;

   @Override
   public void collect(Class<?> var1, List<Field> var2) {
      if (var1 != null) {
         label36:
         for (Field var9 : var1.getDeclaredFields()) {
            for (Field var11 : var2) {
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

   public Field[] getMembers(Class<?> var1) {
      Reference var5 = this.b.get(var1);
      if (var5 != null) {
         Field[] var6 = (Field[])var5.get();
         if (var6 != null) {
            return (Field[])var6.clone();
         }
      }

      ArrayList var8 = new ArrayList();
      this.collect(var1, var8);
      Field[] var7 = (java.lang.reflect.Field[])(var8.toArray(new Field[var8.size()]));
      this.b.put(var1, new SoftReference<>(var7));
      return (Field[])var7.clone();
   }

   public boolean hides(Field var1, Field var2) {
      return !Modifier.isPrivate(var1.getModifiers()) && var1.getName().equals(var2.getName());
   }
}
