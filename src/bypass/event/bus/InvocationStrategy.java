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
      public <O, E extends Event> Listener<E> createDispatcher(Class<E> var1, O var2, AnnotatedElement var3) {
         Method var8 = (Method)var3;
         var8.setAccessible(true);
         return var2x -> {
            try {
               var8.invoke(var2, var2x);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException var4) {
               ReflectionErrors.H(var4);
            }
         };
      }
   },
   METHOD_LAMBDA_INVOCATION(MemberKind.METHOD) {
      private static final long a = 8265636125559L;

      @Override
      public <O, E extends Event> Listener<E> createDispatcher(Class<E> var1, O var2, AnnotatedElement var3) {
         Method var7 = (Method)var3;
         var7.setAccessible(true);

         try {
            Lookup var8 = n.in(var2.getClass());
            MethodHandle var9 = null;
            int var10 = var7.getModifiers();
            if (Modifier.isPrivate(var10)) {
               if (J) {
                  var9 = Modifier.isStatic(var10) ? var8.unreflect(var7) : var8.unreflectSpecial(var7, var2.getClass());
               }
            } else if (Modifier.isProtected(var10)) {
               if (J) {
                  var9 = var8.unreflect(var7);
               }
            } else if (Modifier.isPublic(var10)) {
               var9 = var8.unreflect(var7);
            } else {
               var9 = var8.unreflect(var7);
            }

            if (var9 != null) {
               MethodType var13 = MethodType.methodType(void.class, var1);
               Listener var14;
               if (Modifier.isStatic(var10)) {
                  MethodType var12 = MethodType.methodType(Listener.class);
                  var14 = (Listener)LambdaMetafactory.metafactory(var8, "accept", var12, var13.erase(), var9, var13).dynamicInvoker().invoke();
               } else {
                  MethodType var16 = MethodType.methodType(Listener.class, var2.getClass());
                  var14 = (Listener)LambdaMetafactory.metafactory(var8, "accept", var16, var13.erase(), var9, var13).dynamicInvoker().invoke((Object)var2);
               }

               return var14;
            } else {
               throw new RuntimeException("Unable to do lookup on method \"" + var7.getName() + "\".");
            }
         } catch (Throwable var15) {
            ReflectionErrors.H(var15);
            return null;
         }
      }
   },
   FIELD_REFLECTIVE_INVOCATION(MemberKind.FIELD) {
      @Override
      public <O, E extends Event> Listener<E> createDispatcher(Class<E> var1, O var2, AnnotatedElement var3) {
         Field var4 = (Field)var3;
         var4.setAccessible(true);

         try {
            return (Listener<E>)var4.get(var2);
         } catch (IllegalArgumentException | IllegalAccessException var6) {
            ReflectionErrors.H(var6);
            return null;
         }
      }
   };

   public static final Lookup n;
   public static final boolean J;
   private MemberKind C;
   private static final InvocationStrategy[] X = new InvocationStrategy[]{METHOD_REFLECTIVE_INVOCATION, METHOD_LAMBDA_INVOCATION, FIELD_REFLECTIVE_INVOCATION};

   InvocationStrategy(MemberKind var3) {
      this.C = var3;
   }

   public abstract <O, E extends Event> Listener<E> createDispatcher(Class<E> var1, O var2, AnnotatedElement var3);

   public MemberKind e() {
      return this.C;
   }

   static {
      Lookup var11;
      boolean var12;
      try {
         Field var13 = Lookup.class.getDeclaredField("IMPL_LOOKUP");
         var13.setAccessible(true);
         var11 = (Lookup)var13.get(null);
         var12 = true;
      } catch (ReflectiveOperationException var14) {
         var11 = MethodHandles.lookup();
         var12 = false;
      }

      n = var11;
      J = var12;
   }
}
