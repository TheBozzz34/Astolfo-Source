package bypass.auth;

public class HwidOptions {
   private boolean Y;
   private boolean h;
   private boolean c;
   private boolean f;
   private boolean A;
   private boolean z;
   private boolean m;

   public HwidOptions J(boolean bl) {
      this.Y = bl;
      return this;
   }

   public HwidOptions i(boolean bl) {
      this.h = bl;
      return this;
   }

   public HwidOptions n(boolean bl) {
      this.c = bl;
      return this;
   }

   public HwidOptions O(boolean bl) {
      this.A = bl;
      return this;
   }

   public HwidOptions R(boolean bl) {
      this.z = bl;
      return this;
   }

   public HwidOptions d(boolean bl) {
      this.m = bl;
      return this;
   }

   public HwidOptions P(boolean bl) {
      this.f = bl;
      return this;
   }

   public HwidBuilder H() {
      return new HwidBuilder(this);
   }

   public static boolean E(HwidOptions hwidOptions) {
      return hwidOptions.f;
   }

   public static boolean o(HwidOptions hwidOptions) {
      return hwidOptions.Y;
   }

   public static boolean k(HwidOptions hwidOptions) {
      return hwidOptions.h;
   }

   public static boolean s(HwidOptions hwidOptions) {
      return hwidOptions.A;
   }

   public static boolean z(HwidOptions hwidOptions) {
      return hwidOptions.z;
   }

   public static boolean D(HwidOptions hwidOptions) {
      return hwidOptions.m;
   }

   public static boolean X(HwidOptions hwidOptions) {
      return hwidOptions.c;
   }
}
