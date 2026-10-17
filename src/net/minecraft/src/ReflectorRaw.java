package net.minecraft.src;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectorRaw {
   public static Field getField(Class cls, Class fieldType) {
      try {
         Field[] fields = cls.getDeclaredFields();

         for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getType() == fieldType) {
               field.setAccessible(true);
               return field;
            }
         }

         return null;
      } catch (Exception exception) {
         return null;
      }
   }

   public static Field[] getFields(Class cls, Class fieldType) {
      try {
         Field[] fields = cls.getDeclaredFields();
         return getFields(fields, fieldType);
      } catch (Exception e) {
         return null;
      }
   }

   public static Field[] getFields(Field[] fields, Class fieldType) {
      try {
         ArrayList arrayList = new ArrayList();

         for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getType() == fieldType) {
               field.setAccessible(true);
               arrayList.add(field);
            }
         }

         return (java.lang.reflect.Field[])(arrayList.toArray(new Field[arrayList.size()]));
      } catch (Exception exception) {
         return null;
      }
   }

   public static Field[] getFieldsAfter(Class cls, Field field, Class fieldType) {
      try {
         Field[] fields = cls.getDeclaredFields();
         List list = Arrays.asList(fields);
         int i = list.indexOf(field);
         if (i < 0) {
            return new Field[0];
         }

         List list2 = list.subList(i + 1, list.size());
         Field[] fields2 = (java.lang.reflect.Field[])(list2.toArray(new Field[list2.size()]));
         return getFields(fields2, fieldType);
      } catch (Exception exception) {
         return null;
      }
   }

   public static Field[] getFields(Object obj, Field[] fields, Class fieldType, Object value) {
      try {
         ArrayList arrayList = new ArrayList();

         for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getType() == fieldType) {
               boolean bl = Modifier.isStatic(field.getModifiers());
               if ((obj != null || bl) && (obj == null || !bl)) {
                  field.setAccessible(true);
                  Object object = field.get(obj);
                  if (object == value) {
                     arrayList.add(field);
                  } else if (object != null && value != null && object.equals(value)) {
                     arrayList.add(field);
                  }
               }
            }
         }

         return (java.lang.reflect.Field[])(arrayList.toArray(new Field[arrayList.size()]));
      } catch (Exception exception) {
         return null;
      }
   }

   public static Field getField(Class cls, Class fieldType, int index) {
      Field[] fields = getFields(cls, fieldType);
      return index >= 0 && index < fields.length ? fields[index] : null;
   }

   public static Field getFieldAfter(Class cls, Field field, Class fieldType, int index) {
      Field[] fields = getFieldsAfter(cls, field, fieldType);
      return index >= 0 && index < fields.length ? fields[index] : null;
   }

   public static Object getFieldValue(Object obj, Class cls, Class fieldType) {
      ReflectorField reflectorField = getReflectorField(cls, fieldType);
      return reflectorField == null ? null : (!reflectorField.exists() ? null : Reflector.getFieldValue(obj, reflectorField));
   }

   public static Object getFieldValue(Object obj, Class cls, Class fieldType, int index) {
      ReflectorField reflectorField = getReflectorField(cls, fieldType, index);
      return reflectorField == null ? null : (!reflectorField.exists() ? null : Reflector.getFieldValue(obj, reflectorField));
   }

   public static boolean setFieldValue(Object obj, Class cls, Class fieldType, Object value) {
      ReflectorField reflectorField = getReflectorField(cls, fieldType);
      return reflectorField == null ? false : (!reflectorField.exists() ? false : Reflector.setFieldValue(obj, reflectorField, value));
   }

   public static boolean setFieldValue(Object obj, Class cls, Class fieldType, int index, Object value) {
      ReflectorField reflectorField = getReflectorField(cls, fieldType, index);
      return reflectorField == null ? false : (!reflectorField.exists() ? false : Reflector.setFieldValue(obj, reflectorField, value));
   }

   public static ReflectorField getReflectorField(Class cls, Class fieldType) {
      Field field = getField(cls, fieldType);
      if (field == null) {
         return null;
      }

      ReflectorClass reflectorClass = new ReflectorClass(cls);
      return new ReflectorField(reflectorClass, field.getName());
   }

   public static ReflectorField getReflectorField(Class cls, Class fieldType, int index) {
      Field field = getField(cls, fieldType, index);
      if (field == null) {
         return null;
      }

      ReflectorClass reflectorClass = new ReflectorClass(cls);
      return new ReflectorField(reflectorClass, field.getName());
   }
}
