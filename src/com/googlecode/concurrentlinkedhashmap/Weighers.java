package com.googlecode.concurrentlinkedhashmap;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Weighers {
   private Weighers() {
      throw new AssertionError();
   }

   public static <K, V> EntryWeigher<K, V> asEntryWeigher(Weigher<? super V> weigher) {
      return weigher == singleton() ? entrySingleton() : new Weighers.EntryWeigherView<>(weigher);
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

   public static <E> Weigher<? super Iterable<E>> iterable() {
      return Weighers.IterableWeigher.INSTANCE;
   }

   public static <E> Weigher<? super Collection<E>> collection() {
      return Weighers.CollectionWeigher.INSTANCE;
   }

   public static <E> Weigher<? super List<E>> list() {
      return Weighers.ListWeigher.INSTANCE;
   }

   public static <E> Weigher<? super Set<E>> set() {
      return Weighers.SetWeigher.INSTANCE;
   }

   public static <A, B> Weigher<? super Map<A, B>> map() {
      return Weighers.MapWeigher.INSTANCE;
   }

   public enum ByteArrayWeigher implements Weigher<byte[]> {
      INSTANCE;

      public int weightOf(byte[] value) {
         return value.length;
      }
   }

   public enum CollectionWeigher implements Weigher<Collection<?>> {
      INSTANCE;

      public int weightOf(Collection<?> values) {
         return values.size();
      }
   }

   public static final class EntryWeigherView<K, V> implements EntryWeigher<K, V>, Serializable {
      public static final long serialVersionUID = 1L;
      public final Weigher<? super V> weigher;

      public EntryWeigherView(Weigher<? super V> weigher) {
         this.weigher = weigher;
      }

      @Override
      public int weightOf(K key, V value) {
         return this.weigher.weightOf(value);
      }
   }

   public enum IterableWeigher implements Weigher<Iterable<?>> {
      INSTANCE;

      public int weightOf(Iterable<?> values) {
         if (values instanceof Collection) {
            return ((Collection)values).size();
         }

         int i = 0;

         for (Iterator iterator2 = values.iterator(); iterator2.hasNext(); i++) {
            iterator2.next();
         }

         return i;
      }
   }

   public enum ListWeigher implements Weigher<List<?>> {
      INSTANCE;

      public int weightOf(List<?> values) {
         return values.size();
      }
   }

   public enum MapWeigher implements Weigher<Map<?, ?>> {
      INSTANCE;

      public int weightOf(Map<?, ?> values) {
         return values.size();
      }
   }

   public enum SetWeigher implements Weigher<Set<?>> {
      INSTANCE;

      public int weightOf(Set<?> values) {
         return values.size();
      }
   }

   public enum SingletonEntryWeigher implements EntryWeigher<Object, Object> {
      INSTANCE;

      @Override
      public int weightOf(Object key, Object value) {
         return 1;
      }
   }

   public enum SingletonWeigher implements Weigher<Object> {
      INSTANCE;

      @Override
      public int weightOf(Object value) {
         return 1;
      }
   }
}
