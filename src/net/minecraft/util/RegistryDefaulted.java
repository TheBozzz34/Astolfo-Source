package net.minecraft.util;

public class RegistryDefaulted<K, V> extends RegistrySimple<K, V> {
   private final V defaultObject;
   private static final long a = 9252780093959L;

   public RegistryDefaulted(V var1) {
      this.defaultObject = (V)var1;
   }

   @Override
   public V getObject(K var1) {
      Object var5 = super.getObject((K)var1);
      return (V)(var5 == null ? this.defaultObject : var5);
   }
}
