package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced<K, V> extends RegistrySimple<K, V> implements IObjectIntIterable<V> {
   public final ObjectIntIdentityMap<V> underlyingIntegerMap = new ObjectIntIdentityMap<>();
   public final Map<V, K> inverseObjectRegistry = ((BiMap)this.registryObjects).inverse();

   public void register(int var1, K var2, V var3) {
      this.underlyingIntegerMap.put((V)var3, var1);
      this.putObject((K)var2, (V)var3);
   }

   @Override
   public Map<K, V> createUnderlyingMap() {
      return HashBiMap.create();
   }

   @Override
   public V getObject(K var1) {
      return super.getObject((K)var1);
   }

   public K getNameForObject(V var1) {
      return this.inverseObjectRegistry.get(var1);
   }

   @Override
   public boolean containsKey(K var1) {
      return super.containsKey((K)var1);
   }

   public int getIDForObject(V var1) {
      return this.underlyingIntegerMap.get((V)var1);
   }

   public V getObjectById(int var1) {
      return this.underlyingIntegerMap.getByValue(var1);
   }

   @Override
   public Iterator<V> iterator() {
      return this.underlyingIntegerMap.iterator();
   }
}
