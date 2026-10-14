package bypass.event.bus;

import bypass.event.Event;
import bypass.event.Listener;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public enum InvocationStrategy {
   METHOD_REFLECTIVE_INVOCATION(MemberKind.METHOD) {
      private static final long a = 28013588115475L;

      @Override
      public <O, E extends Event> Listener<E> createDispatcher(Class<E> class2, O object, AnnotatedElement annotatedElement) {
         Method method = (Method)annotatedElement;
         method.setAccessible(true);
         return value -> {
            try {
               method.invoke(object, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException value2) {
               ReflectionErrors.H(value2);
            }
         };
      }
   },
   METHOD_LAMBDA_INVOCATION(MemberKind.METHOD) {
      private static final long a = 8265636125559L;

      @Override
      public <O, E extends Event> Listener<E> createDispatcher(Class<E> class2, O object, AnnotatedElement annotatedElement) {
         Method method2 = (Method)annotatedElement;
         method2.setAccessible(true);

         try {
            Lookup lookup2 = n.in(object.getClass());
            MethodHandle methodHandle = null;
            int i = method2.getModifiers();
            if (Modifier.isPrivate(i)) {
               if (J) {
                  methodHandle = Modifier.isStatic(i) ? lookup2.unreflect(method2) : lookup2.unreflectSpecial(method2, object.getClass());
               }
            } else if (Modifier.isProtected(i)) {
               if (J) {
                  methodHandle = lookup2.unreflect(method2);
               }
            } else if (Modifier.isPublic(i)) {
               methodHandle = lookup2.unreflect(method2);
            } else {
               methodHandle = lookup2.unreflect(method2);
            }

            if (methodHandle != null) {
               MethodType methodType2 = MethodType.methodType(void.class, class2);
               Listener listener;
               if (Modifier.isStatic(i)) {
                  MethodType methodType3 = MethodType.methodType(Listener.class);
                  listener = (Listener)LambdaMetafactory.metafactory(lookup2, "accept", methodType3, methodType2.erase(), methodHandle, methodType2).dynamicInvoker().invoke();
               } else {
                  MethodType methodType4 = MethodType.methodType(Listener.class, object.getClass());
                  listener = (Listener)LambdaMetafactory.metafactory(lookup2, "accept", methodType4, methodType2.erase(), methodHandle, methodType2).dynamicInvoker().invoke((Object)object);
               }

               return listener;
            } else {
               throw new RuntimeException("Unable to do lookup on method \"" + method2.getName() + "\".");
            }
         } catch (Throwable throwable) {
            ReflectionErrors.H(throwable);
            return null;
         }
      }
   },
   FIELD_REFLECTIVE_INVOCATION(MemberKind.FIELD) {
      @Override
      public <O, E extends Event> Listener<E> createDispatcher(Class<E> class2, O object, AnnotatedElement annotatedElement) {
         Field field = (Field)annotatedElement;
         field.setAccessible(true);

         try {
            return (Listener<E>)field.get(object);
         } catch (IllegalArgumentException | IllegalAccessException value) {
            ReflectionErrors.H(value);
            return null;
         }
      }
   };

   public static final Lookup n;
   public static final boolean J;
   private MemberKind C;
   private static final InvocationStrategy[] X = new InvocationStrategy[]{METHOD_REFLECTIVE_INVOCATION, METHOD_LAMBDA_INVOCATION, FIELD_REFLECTIVE_INVOCATION};

   InvocationStrategy(MemberKind memberKind) {
      this.C = memberKind;
   }

   public abstract <O, E extends Event> Listener<E> createDispatcher(Class<E> class2, O object, AnnotatedElement annotatedElement);

   public MemberKind e() {
      return this.C;
   }

   static {
      Lookup lookup2;
      boolean bl;
      try {
         Field field = Lookup.class.getDeclaredField("IMPL_LOOKUP");
         field.setAccessible(true);
         lookup2 = (Lookup)field.get(null);
         bl = true;
      } catch (ReflectiveOperationException reflectiveOperationException) {
         lookup2 = MethodHandles.lookup();
         bl = false;
      }

      n = lookup2;
      J = bl;
   }
}
