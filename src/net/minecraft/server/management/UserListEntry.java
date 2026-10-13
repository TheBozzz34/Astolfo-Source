package net.minecraft.server.management;

import com.google.gson.JsonObject;

public class UserListEntry<T> {
   private final T value;

   public UserListEntry(T var1) {
      this.value = (T)var1;
   }

   public UserListEntry(T var1, JsonObject var2) {
      this.value = (T)var1;
   }

   public T getValue() {
      return this.value;
   }

   public boolean hasBanExpired() {
      return false;
   }

   public void onSerialization(JsonObject var1) {
   }
}
