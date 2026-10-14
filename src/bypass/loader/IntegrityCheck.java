package bypass.loader;

import bypass.auth.AuthHttpRequest;
import bypass.auth.AuthUtil;
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

   public IntegrityCheck(StringListHolder stringListHolder) {
      this.E = stringListHolder;
   }

   public Holder Y() {
      this.w(true);
      Holder holder = new Holder();
      holder.value = new EntitySkeleton(3298775);
      this.h(true);
      AuthHttpRequest authHttpRequest = new AuthHttpRequest()
         .r("https://security.astolfo.lgbt/api/md5")
         .a("hash")
         .O(this.E.A().L().m_Iterator().next())
         .F("application/x-www-form-urlencoded")
         .v("aa4hu3ib2");
      this.Q(true);
      this.w(!this.O());
      if (!AuthUtil.k(authHttpRequest).contains("Valid") && (holder.value = new EntitySkeleton(3298776)) != null) {
      }

      this.Z(this.f());
      this.R(true);
      return holder;
   }

   public void h(boolean bl) {
      this.Z = bl;
   }

   public void Z(boolean bl) {
      this.K = bl;
   }

   public void c(boolean bl) {
      this.D = bl;
   }

   public void Q(boolean bl) {
      this.k = bl;
   }

   public void w(boolean bl) {
      this.j = bl;
   }

   public void R(boolean bl) {
      this.y = bl;
   }

   public void a(boolean bl) {
      this.V = bl;
   }

   public void f(boolean bl) {
      this.O = bl;
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
