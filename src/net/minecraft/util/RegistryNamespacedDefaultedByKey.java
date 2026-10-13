package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class RegistryNamespacedDefaultedByKey<K, V> extends RegistryNamespaced<K, V> {
   private final K defaultValueKey;
   private V defaultValue;
   private static final long a = 100272766907113L;

   public RegistryNamespacedDefaultedByKey(K var1) {
      this.defaultValueKey = (K)var1;
   }

   @Override
   public void register(int var1, K var2, V var3) {
      if (this.defaultValueKey.equals(var2)) {
         this.defaultValue = (V)var3;
      }

      super.register(var1, (K)var2, (V)var3);
   }

   public void validateKey() {
      Validate.notNull(this.defaultValueKey);
   }

   @Override
   public V getObject(K var1) {
      Object var4 = super.getObject((K)var1);
      return (V)(var4 == null ? this.defaultValue : var4);
   }

   @Override
   public V getObjectById(int var1) {
      Object var5 = super.getObjectById(var1);
      return (V)(var5 == null ? this.defaultValue : var5);
   }
}
