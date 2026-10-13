package bypass.auth;

public class HwidOptions {
   private boolean Y;
   private boolean h;
   private boolean c;
   private boolean f;
   private boolean A;
   private boolean z;
   private boolean m;

   public HwidOptions J(boolean var1) {
      this.Y = var1;
      return this;
   }

   public HwidOptions i(boolean var1) {
      this.h = var1;
      return this;
   }

   public HwidOptions n(boolean var1) {
      this.c = var1;
      return this;
   }

   public HwidOptions O(boolean var1) {
      this.A = var1;
      return this;
   }

   public HwidOptions R(boolean var1) {
      this.z = var1;
      return this;
   }

   public HwidOptions d(boolean var1) {
      this.m = var1;
      return this;
   }

   public HwidOptions P(boolean var1) {
      this.f = var1;
      return this;
   }

   public HwidBuilder H() {
      return new HwidBuilder(this);
   }

   public static boolean E(HwidOptions var0) {
      return var0.f;
   }

   public static boolean o(HwidOptions var0) {
      return var0.Y;
   }

   public static boolean k(HwidOptions var0) {
      return var0.h;
   }

   public static boolean s(HwidOptions var0) {
      return var0.A;
   }

   public static boolean z(HwidOptions var0) {
      return var0.z;
   }

   public static boolean D(HwidOptions var0) {
      return var0.m;
   }

   public static boolean X(HwidOptions var0) {
      return var0.c;
   }
}
