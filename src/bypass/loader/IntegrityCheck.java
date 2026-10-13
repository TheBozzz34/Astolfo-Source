package bypass.loader;

import bypass.auth.AuthHttpRequest;
import bypass.auth.AuthUtil;
import bypass.loader.check.IntegrityCheckStrings;
import bypass.loader.check.StringListHolder;
import bypass.util.Holder;
import net.minecraft.entity.monster.EntitySkeleton;

public class IntegrityCheck {
   private final StringListHolder E;
   private boolean Z;
   private boolean K = true;
   private boolean D;
   private boolean k;
   private boolean j = true;
   private boolean y;
   private boolean V;
   private boolean O;
   private static final long a = 17973522484811L;

   public IntegrityCheck(StringListHolder var1) {
      this.E = var1;
   }

   public Holder Y() {
      this.w(true);
      Holder var5 = new Holder();
      var5.value = new EntitySkeleton(3298775);
      this.h(true);
      AuthHttpRequest var6 = new AuthHttpRequest()
         .r("https://security.astolfo.lgbt/api/md5")
         .a(IntegrityCheckStrings.W("6aIZQw=="))
         .O(this.E.A().L().m_Iterator().next())
         .F(IntegrityCheckStrings.W("4LMaR/zOOYwyGyzI3WmVKixANnf3QTPqFf+GKBI2mj9h"))
         .v("aa4hu3ib2");
      this.Q(true);
      this.w(!this.O());
      if (!AuthUtil.k(var6).contains(IntegrityCheckStrings.W("16IGQvE=")) && (var5.value = new EntitySkeleton(3298776)) != null) {
      }

      this.Z(this.f());
      this.R(true);
      return var5;
   }

   public void h(boolean var1) {
      this.Z = var1;
   }

   public void Z(boolean var1) {
      this.K = var1;
   }

   public void c(boolean var1) {
      this.D = var1;
   }

   public void Q(boolean var1) {
      this.k = var1;
   }

   public void w(boolean var1) {
      this.j = var1;
   }

   public void R(boolean var1) {
      this.y = var1;
   }

   public void a(boolean var1) {
      this.V = var1;
   }

   public void f(boolean var1) {
      this.O = var1;
   }

   public boolean V() {
      return this.Z;
   }

   public boolean n() {
      return this.Z;
   }

   public boolean P() {
      return this.y;
   }

   public boolean z() {
      return this.y;
   }

   public boolean F() {
      return this.j;
   }

   public boolean f() {
      return this.j;
   }

   public boolean O() {
      return this.k;
   }

   public boolean b() {
      return this.D;
   }

   public boolean a() {
      return this.K;
   }

   public boolean s() {
      return this.V;
   }

   public boolean h() {
      return this.O;
   }
}
