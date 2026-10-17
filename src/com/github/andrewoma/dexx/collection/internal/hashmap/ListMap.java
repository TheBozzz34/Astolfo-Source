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

   public static <K, V> BuilderFactory<Pair<K, V>, ListMap<K, V>> factory() {
      return new BuilderFactory<Pair<K, V>, ListMap<K, V>>() {
         @Override
         public Builder<Pair<K, V>, ListMap<K, V>> newBuilder() {
            return new AbstractSelfBuilder<Pair<K, V>, ListMap<K, V>>(ListMap.empty()) {
               public Builder<Pair<K, V>, ListMap<K, V>> add(Pair<K, V> element) {
                  this.result = this.result.put(element.component1(), element.component2());
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
   public V get(K key) {
      return null;
   }

   public ListMap<K, V> put(K key, V value) {
      return new ListMap.Node<>((K)this, (V)key, value);
   }

   public ListMap<K, V> remove(K key) {
      return this;
   }

   @Override
   public <U> void forEach(Function<Pair<K, V>, U> f) {
      for (Pair pair : this) {
         f.invoke(pair);
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

   public ListMap.Node<K, V> createNode(K key, V value) {
      return new ListMap.Node<>((K)this, (V)key, value);
   }

   @Override
   public boolean containsKey(K key) {
      return false;
   }

   public static class Node<K, V> extends ListMap<K, V> {
      private final K x;
      private final V value;
      public final ListMap tail;

      public Node(K this$0, V key, Object value) {
         this.tail = (com.github.andrewoma.dexx.collection.internal.hashmap.ListMap)(this$0);
         this.x = (K)(key);
         this.value = (V)(value);
      }

      @Override
      public int size() {
         return this.size0(this, 0);
      }

      private int size0(ListMap<K, V> cur, int acc) {
         return cur.isEmpty() ? acc : this.size0(cur.tail(), acc + 1);
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public V get(K key) {
         ListMap listMap = this.apply0(this, (K)key);
         return (V)(listMap == null ? null : listMap.getValue());
      }

      private ListMap<K, V> apply0(ListMap<K, V> cur, K key) {
         return cur.isEmpty() ? null : (key.equals(cur.getKey()) ? cur : this.apply0(cur.tail(), key));
      }

      @Override
      public ListMap<K, V> tail() {
         return this.tail;
      }

      @Override
      public ListMap<K, V> put(K key, V value) {
         ListMap listMap = this.containsKey((K)key) ? this.remove((K)key) : this;
         return (ListMap<K, V>)listMap.createNode(key, value);
      }

      @Override
      public ListMap<K, V> remove(K key) {
         ListMap listMap = ListMap.empty();

         for (Pair pair : this) {
            if (!key.equals(pair.component1())) {
               listMap = listMap.put(pair.component1(), pair.component2());
            }
         }

         return listMap;
      }

      @Override
      public K getKey() {
         return this.x;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      @Override
      public boolean containsKey(K key) {
         return this.apply0(this, key) != null;
      }
   }
}
