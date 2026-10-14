package net.minecraft.util;

public class RegistryDefaulted<K, V> extends RegistrySimple<K, V> {
   private final V defaultObject;
   private static final long a = 9252780093959L;

   public RegistryDefaulted(V defaultObjectIn) {
      this.defaultObject = (V)defaultObjectIn;
   }

   @Override
   public V getObject(K name) {
      Object object = super.getObject((K)name);
      return (V)(object == null ? this.defaultObject : object);
   }
}
