package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.andrewoma.dexx.collection.Pair;
import java.util.Collections;
import java.util.Iterator;

public class HashMap1<K, V> extends CompactHashMap<K, V> {
   public final K key;
   public final int hash;
   public final V value;
   public Pair<K, V> kv;
   private static final long b = 24127742855281L;

   public HashMap1(K var1, int var2, V var3, Pair<K, V> var4) {
      this.key = (K)var1;
      this.hash = var2;
      this.value = (V)var3;
      this.kv = var4;
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   public V get0(K var1, int var2, int var3, KeyFunction<K, V> var4) {
      return var2 == this.hash && var1.equals(this.key) ? this.value : null;
   }

   @Override
   public CompactHashMap<K, V> updated0(K var1, int var2, int var3, V var4, Pair<K, V> var5, KeyFunction<K, V> var6) {
      if (var2 == this.hash && var1.equals(this.key)) {
         return new HashMap1<>((K)var1, var2, (V)var4, var5);
      } else if (var2 != this.hash) {
         HashMap1 var10 = new HashMap1<>(var1, var2, var4, var5);
         return makeHashTrieMap(this.hash, this, var2, var10, var3, 2);
      } else {
         return new HashMapCollision1<>(var2, ListMap.<K, V>empty().put(this.key, this.value).put((K)var1, (V)var4));
      }
   }

   @Override
   public CompactHashMap<K, V> removed0(K var1, int var2, int var3, KeyFunction<K, V> var4) {
      return var2 == this.hash && var1.equals(this.key) ? CompactHashMap.empty() : this;
   }

   @Override
   public <U> void forEach(Function<Pair<K, V>, U> var1, KeyFunction<K, V> var2) {
      var1.invoke(new Pair<>(this.key, this.value));
   }

   private Pair<K, V> ensureKv() {
      if (this.kv == null) {
         this.kv = new Pair<>(this.key, this.value);
      }

      return this.kv;
   }

   @Override
   public Iterator<Pair<K, V>> iterator(KeyFunction<K, V> var1) {
      return Collections.singleton(this.ensureKv()).iterator();
   }
}
