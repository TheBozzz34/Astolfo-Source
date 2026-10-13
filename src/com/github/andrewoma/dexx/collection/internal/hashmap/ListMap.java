package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.Builder;
import com.github.andrewoma.dexx.collection.BuilderFactory;
import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.Pair;
import com.github.andrewoma.dexx.collection.internal.base.AbstractMap;
import com.github.andrewoma.dexx.collection.internal.builder.AbstractSelfBuilder;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListMap<K, V> extends AbstractMap<K, V> {
   private static final ListMap<Object, Object> EMPTY = new ListMap<>();
   private static final long a = 114298190645816L;

   public static <K, V> BuilderFactory<Pair<K, V>, ListMap<K, V>> factory() {
      return new BuilderFactory<Pair<K, V>, ListMap<K, V>>() {
         @Override
         public Builder<Pair<K, V>, ListMap<K, V>> newBuilder() {
            return new AbstractSelfBuilder<Pair<K, V>, ListMap<K, V>>(ListMap.empty()) {
               public Builder<Pair<K, V>, ListMap<K, V>> add(Pair<K, V> var1) {
                  this.result = this.result.put((K)var1.component1(), (V)var1.component2());
                  return this;
               }
            };
         }
      };
   }

   public static <K, V> ListMap<K, V> empty() {
      return (ListMap<K, V>)EMPTY;
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public V get(K var1) {
      return null;
   }

   public ListMap<K, V> put(K var1, V var2) {
      return new ListMap.Node<>((K)this, (V)var1, var2);
   }

   public ListMap<K, V> remove(K var1) {
      return this;
   }

   @Override
   public <U> void forEach(Function<Pair<K, V>, U> var1) {
      for (Pair var6 : this) {
         var1.invoke(var6);
      }
   }

   @Override
   public Iterator<Pair<K, V>> iterator() {
      return new ListMapIterator<>(this);
   }

   public K getKey() {
      throw new NoSuchElementException("empty map");
   }

   public V getValue() {
      throw new NoSuchElementException("empty map");
   }

   public ListMap<K, V> tail() {
      throw new NoSuchElementException("empty map");
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   public ListMap.Node<K, V> createNode(K var1, V var2) {
      return new ListMap.Node<>((K)this, (V)var1, var2);
   }

   @Override
   public boolean containsKey(K var1) {
      return false;
   }

   public static class Node<K, V> extends ListMap<K, V> {
      private final K x;
      private final V D;
      public final ListMap C;
      private static final long d = 33392032105613L;

      public Node(K var1, V var2, Object var3) {
         this.C = (com.github.andrewoma.dexx.collection.internal.hashmap.ListMap)(var1);
         this.x = (K)var2;
         this.D = (V)var3;
      }

      @Override
      public int size() {
         return this.X(this, 0);
      }

      private int X(ListMap<K, V> var1, int var2) {
         return var1.isEmpty() ? var2 : this.X(var1.tail(), var2 + 1);
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public V get(K var1) {
         ListMap var5 = this.E(this, (K)var1);
         return (V)(var5 == null ? null : var5.getValue());
      }

      private ListMap<K, V> E(ListMap<K, V> var1, K var2) {
         return var1.isEmpty() ? null : (var2.equals(var1.getKey()) ? var1 : this.E(var1.tail(), (K)var2));
      }

      @Override
      public ListMap<K, V> tail() {
         return this.C;
      }

      @Override
      public ListMap<K, V> put(K var1, V var2) {
         ListMap var6 = this.containsKey((K)var1) ? this.remove((K)var1) : this;
         return (ListMap<K, V>)var6.createNode(var1, var2);
      }

      @Override
      public ListMap<K, V> remove(K var1) {
         ListMap var5 = ListMap.empty();

         for (Pair var7 : this) {
            if (!var1.equals(var7.component1())) {
               var5 = var5.put(var7.component1(), var7.component2());
            }
         }

         return var5;
      }

      @Override
      public K getKey() {
         return this.x;
      }

      @Override
      public V getValue() {
         return this.D;
      }

      @Override
      public boolean containsKey(K var1) {
         return this.E(this, (K)var1) != null;
      }
   }
}
