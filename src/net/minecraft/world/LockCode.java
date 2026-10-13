package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

public class LockCode {
   public static final LockCode EMPTY_CODE = new LockCode("");
   private final String lock;
   private static final long a = 16861613365010L;
   private static final long e = -3675970501856985080L;

   public LockCode(String var1) {
      this.lock = var1;
   }

   public boolean isEmpty() {
      return this.lock == null || this.lock.isEmpty();
   }

   public String getLock() {
      return this.lock;
   }

   public void toNBT(NBTTagCompound var1) {
      var1.setString("Lock", this.lock);
   }

   public static LockCode fromNBT(NBTTagCompound var0) {
      if (var0.hasKey("Lock", (int)e)) {
         String var4 = var0.getString("Lock");
         return new LockCode(var4);
      } else {
         return EMPTY_CODE;
      }
   }
}
