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

   @Override
   public void collect(Class<?> class2, List<Field> list) {
      if (class2 != null) {
         label36:
         for (Field field : class2.getDeclaredFields()) {
            for (Field field2 : list) {
               if (this.hides(field2, field)) {
                  continue label36;
               }
            }

            list.add(field);
         }

         this.collect(class2.getSuperclass(), list);

         for (Class class3 : class2.getInterfaces()) {
            this.collect(class3, list);
         }
      }
   }

   public Field[] getMembers(Class<?> class2) {
      Reference reference = this.b.get(class2);
      if (reference != null) {
         Field[] fields = (Field[])reference.get();
         if (fields != null) {
            return (Field[])fields.clone();
         }
      }

      ArrayList arrayList = new ArrayList();
      this.collect(class2, arrayList);
      Field[] fields2 = (java.lang.reflect.Field[])(arrayList.toArray(new Field[arrayList.size()]));
      this.b.put(class2, new SoftReference<>(fields2));
      return (Field[])fields2.clone();
   }

   public boolean hides(Field field, Field field2) {
      return !Modifier.isPrivate(field.getModifiers()) && field.getName().equals(field2.getName());
   }
}
