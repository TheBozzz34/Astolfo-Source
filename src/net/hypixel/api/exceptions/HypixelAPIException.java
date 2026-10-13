package net.hypixel.api.exceptions;

public class HypixelAPIException extends RuntimeException {
   private static final long b = 7110322635741L;

   public HypixelAPIException() {
   }

   public HypixelAPIException(String var1) {
      super(var1);
   }

   public HypixelAPIException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public HypixelAPIException(Throwable var1) {
      super(var1);
   }

   public HypixelAPIException(String var1, Throwable var2, boolean var3, boolean var4) {
      super(var1, var2, var3, var4);
   }
}
