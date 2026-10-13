package com.googlecode.concurrentlinkedhashmap;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Weighers {
   private static final long a = 15047677922800L;

   private Weighers() {
      throw new AssertionError();
   }

   public static <K, V> EntryWeigher<K, V> asEntryWeigher(Weigher<? super V> var0) {
      return var0 == singleton() ? entrySingleton() : new Weighers.EntryWeigherView<>(var0);
   }

   public static <K, V> EntryWeigher<K, V> entrySingleton() {
      return (com.googlecode.concurrentlinkedhashmap.EntryWeigher)(Weighers.SingletonEntryWeigher.INSTANCE);
   }

   public static <V> Weigher<V> singleton() {
      return (com.googlecode.concurrentlinkedhashmap.Weigher)(Weighers.SingletonWeigher.INSTANCE);
   }

   public static Weigher<byte[]> x() {
      return Weighers.ByteArrayWeigher.INSTANCE;
   }

   public static <E> Weigher<? super Iterable<E>> O() {
      return Weighers.IterableWeigher.INSTANCE;
   }

   public static <E> Weigher<? super Collection<E>> D() {
      return Weighers.CollectionWeigher.INSTANCE;
   }

   public static <E> Weigher<? super List<E>> g() {
      return Weighers.ListWeigher.INSTANCE;
   }

   public static <E> Weigher<? super Set<E>> k() {
      return Weighers.SetWeigher.INSTANCE;
   }

   public static <A, B> Weigher<? super Map<A, B>> I() {
      return Weighers.MapWeigher.INSTANCE;
   }

   public enum ByteArrayWeigher implements Weigher<byte[]> {
      INSTANCE;

      public int weightOf(byte[] var1) {
         return var1.length;
      }
   }

   public enum CollectionWeigher implements Weigher<Collection<?>> {
      INSTANCE;

      public int weightOf(Collection<?> var1) {
         return var1.size();
      }
   }

   public static final class EntryWeigherView<K, V> implements EntryWeigher<K, V>, Serializable {
      public static final long serialVersionUID = 1L;
      public final Weigher<? super V> weigher;
      private static final long a = 119771151034914L;

      public EntryWeigherView(Weigher<? super V> var1) {
         this.weigher = var1;
      }

      @Override
      public int weightOf(K var1, V var2) {
         return this.weigher.weightOf((V)var2);
      }
   }

   public enum IterableWeigher implements Weigher<Iterable<?>> {
      INSTANCE;

      private static final long a = 25803324189206L;

      public int weightOf(Iterable<?> var1) {
         if (var1 instanceof Collection) {
            return ((Collection)var1).size();
         }

         int var5 = 0;

         for (Iterator var6 = var1.iterator(); var6.hasNext(); var5++) {
            var6.next();
         }

         return var5;
      }
   }

   public enum ListWeigher implements Weigher<List<?>> {
      INSTANCE;

      public int weightOf(List<?> var1) {
         return var1.size();
      }
   }

   public enum MapWeigher implements Weigher<Map<?, ?>> {
      INSTANCE;

      public int weightOf(Map<?, ?> var1) {
         return var1.size();
      }
   }

   public enum SetWeigher implements Weigher<Set<?>> {
      INSTANCE;

      public int weightOf(Set<?> var1) {
         return var1.size();
      }
   }

   public enum SingletonEntryWeigher implements EntryWeigher<Object, Object> {
      INSTANCE;

      @Override
      public int weightOf(Object var1, Object var2) {
         return 1;
      }
   }

   public enum SingletonWeigher implements Weigher<Object> {
      INSTANCE;

      @Override
      public int weightOf(Object var1) {
         return 1;
      }
   }
}
